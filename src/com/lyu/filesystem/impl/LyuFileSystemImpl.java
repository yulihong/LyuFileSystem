package com.lyu.filesystem.impl;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import com.lyu.filesystem.ILyuFileSystem;
import com.lyu.filesystem.entity.LyuFile;
import com.lyu.filesystem.entity.LyuFile.FILE_TYPE;

public class LyuFileSystemImpl implements ILyuFileSystem{

	private static final String DEFAULT_ROOT_NAME = File.separator + "Users";
	private static HashMap<String, LyuDirectoryNode> trees;
	
	public static enum SingleInstance {
        INSTANCE;

        private static final LyuFileSystemImpl singleton = new LyuFileSystemImpl();

        public LyuFileSystemImpl getSingleton() {
            return singleton;
        }
    }
	
	private LyuFileSystemImpl(){
		trees = new HashMap<String, LyuDirectoryNode>();
		LyuDirectoryNode ROOTNODE = new LyuDirectoryNode();
		ROOTNODE.setRoot(true);
		LyuFile rootData = new LyuFile(DEFAULT_ROOT_NAME, LyuFile.FILE_TYPE.FOLDER);
		ROOTNODE.setNodeData(rootData);
		ROOTNODE.setCurrent(ROOTNODE);
		trees.put(DEFAULT_ROOT_NAME, ROOTNODE);
	}
	
	public LyuDirectoryNode getRootNode(String rootName){
		return trees.get(rootName);
	}
	
	public void printOutTree(LyuDirectoryNode startNode){
		if(startNode == null){
			System.out.println("This is an empty tree.");
			return;
		}

		System.out.println(startNode.getNodeData().getName());
		System.out.println(startNode.getNodeData().getSize());
		if(startNode.getNodeData() != null && 
				startNode.getNodeData().getFileType() == LyuFile.FILE_TYPE.FILE){
			System.out.println("File content is:");
			System.out.println(startNode.getNodeData().getContent());
		}
		
		startNode.getChildrenMap().forEach((k, v) -> {
			printOutTree(v);
		});	
	}
	
	/**
	 * path cannot contain name here
	 */
	@Override
	public LyuFile create(FILE_TYPE fileType, String name, String path)throws IOException {
		LyuFile newFile = new LyuFile(name,  path, fileType);
		LyuDirectoryNode root = findRootNode(path);
		if(path.startsWith(File.separator))
			path = path.substring(1);
		
		LinkedList<String> pathList = getPathList(path);
			
		try {
			LyuDirectoryNode insertLocation = root.findPathEndNode(root, pathList);
			if(insertLocation.getNodeData() == null){//new created root and not set data yet
				insertLocation.setNodeData(newFile);
				return newFile;
			}
				
			LyuDirectoryNode newNode = new LyuDirectoryNode();
			newNode.setNodeData(newFile);
			newNode.setParent(insertLocation);
			newNode.setCurrent(newNode);
			insertLocation.getChildrenMap().put(newFile.getName(), newNode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return newFile;
	}

	private LinkedList<String> getPathList(String path) {
		String[] pathAndName = path.split(File.separator);
		LinkedList<String> pathList = new LinkedList<String>(Arrays.asList(pathAndName));
		return pathList;
	}

	private LyuDirectoryNode findRootNode(String path) throws RuntimeException {
		LyuDirectoryNode root = null;
		if(!path.startsWith(DEFAULT_ROOT_NAME) && path.contains(":")) //in a drive
		{
			String driveName = findRootNameInPath(path);		
			if(driveName == null){//no name in path
				throw new RuntimeException("No Drive name.");
			}
				
			if(trees.containsKey(driveName)){
				root = trees.get(driveName);
			}
			else{ //need create a new tree from this drive
				root = createNewTree(driveName);
				trees.put(driveName, root);
				return root;
			}
		}
		else if(path.startsWith(DEFAULT_ROOT_NAME)){
			root = trees.get(DEFAULT_ROOT_NAME);
		}
		return root;
	}
		
	public String findRootNameInPath(String path){
		
		if(path.contains(":")){
			String[] pathAndName = path.split(File.separator);
			if (pathAndName.length < 2) {
				return path;
			} else {
				return pathAndName[0] + File.separator;
			}
		}
	
		return null; //the default tree not a drive tree			
	}

	@Override
	public LyuDirectoryNode delete(String path) throws IOException {
		LyuDirectoryNode root = findRootNode(path);
		LinkedList<String> pathList = getPathList(path);
		LyuDirectoryNode needDeleted = null;
		try {
			needDeleted = root.findPathEndNode(root, pathList);
			LyuDirectoryNode parentNode = needDeleted.getParent();
			if(parentNode == null && needDeleted.isRoot()){
				throw new IOException("Cannot delete root");
			}
				
			HashMap<String, LyuDirectoryNode> childrenMap = parentNode.getChildrenMap();
			childrenMap.remove(needDeleted.getNodeData().getName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return needDeleted;
	}
	
	private boolean addNode(String path, LyuDirectoryNode addedNode) throws IOException {
		LyuDirectoryNode insertLocation = findLocation(path);
		insertLocation.getChildrenMap().put(addedNode.getNodeData().getName(), addedNode);
		
		return true;
	}

	@Override
	public boolean move(String src, String target) throws IOException {
		LyuDirectoryNode movedNode = delete(src);
		boolean success = addNode(target, movedNode);
		return success;
	}

	@Override
	public LyuFile write(String path, String content) throws IOException{
		StringBuffer strBuffer = null;
		LyuDirectoryNode insertLocation = findLocation(path);
		if(insertLocation.getNodeData().getFileType() != LyuFile.FILE_TYPE.FILE){
			throw new IOException("Only file cannbe writen.");
		}
		String oldContent = insertLocation.getNodeData().getContent();
		if(oldContent == null){
			insertLocation.getNodeData().setContent(content);
		}
		else{
			strBuffer = new StringBuffer(oldContent);//Use StringBuffer for thread safe
			strBuffer.append(content);
			insertLocation.getNodeData().setContent(strBuffer.toString());
		}
		insertLocation.getNodeData().setSize(insertLocation.getNodeData().getContent().length());
		updateSizeOnPath(content.length(), insertLocation);
				
		return insertLocation.getNodeData();
	}
	
	public void zipFile( LyuDirectoryNode startNode){
		if(startNode == null){
			System.out.println("This is an empty tree.");
			return;
		}
		
		if(startNode.getNodeData() != null){
			startNode.getNodeData().setSize(startNode.getNodeData().getSize()/2);
		}
		
		startNode.getChildrenMap().forEach((k, v) -> {
			zipFile(v);
		});	
		
	}

	private void updateSizeOnPath(int length, LyuDirectoryNode node) {
		while(node.getParent() != null){
			node = node.getParent();
			if(node.getNodeData() != null){
				node.getNodeData().setSize(node.getNodeData().getSize() + length);
			}
		}
		
	}

	public LyuDirectoryNode findLocation(String path) {
		LyuDirectoryNode root = findRootNode(path);
		if(path.startsWith(File.separator))
			path = path.substring(1);
		
		LinkedList<String> pathList = getPathList(path);

		LyuDirectoryNode insertLocation = null;
		try {
			insertLocation = root.findPathEndNode(root, pathList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return insertLocation;
	}
	
	private LyuDirectoryNode createNewTree(String rootName){
		LyuDirectoryNode newRoot = new LyuDirectoryNode();
		newRoot.setRoot(true);
		newRoot.setCurrent(newRoot);
		return newRoot;
	}

}
