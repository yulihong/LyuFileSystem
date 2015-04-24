package com.lyu.filesystem.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.lyu.filesystem.ILyuFileSystem;
import com.lyu.filesystem.entity.LyuFile;
import com.lyu.filesystem.entity.LyuFile.FILE_TYPE;

/**
 * @author Lihong Yu
 * @version 0.1
 * @since April 10, 2015
 *
 * This is the implementation class - core of this file system.
 * It implements the file system API
 */
public class LyuFileSystemImpl implements ILyuFileSystem{
	private static final String ZIP_FILE_EX = ".zip";
	private  String userFileSeparator;
	private  String FILE_SEPARATOR = "/"; //if not / set to \\\\ 
	
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
		LyuFile rootData = new LyuFile(DEFAULT_ROOT_NAME, LyuFile.FILE_TYPE.DRIVE);
		rootData.setName(DEFAULT_ROOT_NAME);
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

		System.out.println("Name: " + startNode.getNodeData().getName());
		System.out.println("Type: " + startNode.getNodeData().getFileType());
		System.out.println("Size: " + startNode.getNodeData().getSize());
		if(startNode.getNodeData() != null && 
				startNode.getNodeData().getFileType() == LyuFile.FILE_TYPE.FILE){
			System.out.println("File content is:");
			System.out.println(startNode.getNodeData().getContent());
		}
		
		startNode.getChildrenMap().forEach((k, v) -> {
			printOutTree(v);
		});	
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.lyu.filesystem.ILyuFileSystem#create(com.lyu.filesystem.entity.LyuFile.FILE_TYPE, java.lang.String, java.lang.String)
	 * @param path cannot contain name here
	 */
	@Override
	public LyuFile create(FILE_TYPE fileType, String name, String path)throws FileNotFoundException, FileAlreadyExistsException {
		LyuFile newFile = new LyuFile(name,  path, fileType);
		LyuDirectoryNode root = findRootNode(path);
		
		LinkedList<String> pathList = getPathList(path);
			
		LyuDirectoryNode insertLocation = root.findPathEndNode(root, pathList);
		if(insertLocation.getChildrenMap() != null && insertLocation.getChildrenMap().containsKey(name)){
			throw new FileAlreadyExistsException(name + " already exist");
		}
		if(insertLocation.getNodeData() == null){//new created root and not set data yet
			insertLocation.setNodeData(newFile);
			return newFile;
		}
			
		LyuDirectoryNode newNode = new LyuDirectoryNode();
		newNode.setNodeData(newFile);
		newNode.setParent(insertLocation);
		newNode.setCurrent(newNode);
		insertLocation.getChildrenMap().put(newFile.getName(), newNode);
	
		return newFile;
	}

	public LyuDirectoryNode findRootNode(String path) throws FileNotFoundException {
		LyuDirectoryNode root = null;
		if(!path.startsWith(DEFAULT_ROOT_NAME) && path.contains(":")) //in a drive
		{
			String driveName = findRootNameInPath(path);		
			if(driveName == null){//no name in path
				throw new FileNotFoundException("No such drive name " + path);
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
		if(userFileSeparator == null)
			userFileSeparator = getPathSeparator(path);
		
		if(path.contains(":")){
			String[] pathAndName = path.split(userFileSeparator);
			if (pathAndName.length < 2) {
				return path;
			} else {
				if(!userFileSeparator.equals("/"))
					return pathAndName[0] + WINDOWS_SEPARATOR;
				else
					return pathAndName[0] + userFileSeparator;
			}
		}
	
		return null; //do not know which root for this case		
	}

	/*
	 * (non-Javadoc)
	 * @see com.lyu.filesystem.ILyuFileSystem#delete(java.lang.String)
	 */
	@Override
	public LyuDirectoryNode delete(String path) throws FileNotFoundException, IllegalArgumentException {
		LyuDirectoryNode root = findRootNode(path);
		LinkedList<String> pathList = getPathList(path);
		LyuDirectoryNode needDeleted = null;
		needDeleted = root.findPathEndNode(root, pathList);
		LyuDirectoryNode parentNode = needDeleted.getParent();
		if(parentNode == null && needDeleted.isRoot()){
			throw new IllegalArgumentException("Cannot delete root " + path);
		}
			
		HashMap<String, LyuDirectoryNode> childrenMap = parentNode.getChildrenMap();
		childrenMap.remove(needDeleted.getNodeData().getName());
		int deletedSize = needDeleted.getNodeData().getSize();
		int newSize = parentNode.calculateTreeNodeSize();
		parentNode.getNodeData().setSize(newSize);
		parentNode.updateSizeAfterChange(deletedSize*(-1));

		return needDeleted;
	}
	
	private boolean addNode(String path, LyuDirectoryNode addedNode) throws FileNotFoundException, FileAlreadyExistsException {
		if(addedNode == null || addedNode.getNodeData() == null){
			return false;
		}
		
		LyuDirectoryNode root = findRootNode(path);
		LinkedList<String> pathList = getPathList(path);
		LyuDirectoryNode insertLocation = root.findLocation(pathList, root);
		if(insertLocation.getChildrenMap() != null && insertLocation.getChildrenMap().containsKey(addedNode.getNodeData().getName())){
			throw new FileAlreadyExistsException(addedNode.getNodeData().getName() + " already exist");
		}
		insertLocation.getChildrenMap().put(addedNode.getNodeData().getName(), addedNode);
		int addedSize = addedNode.getNodeData().getSize();
		insertLocation.getNodeData().setSize(insertLocation.getNodeData().getSize()+addedSize);
		insertLocation.updateSizeAfterChange(addedSize);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.lyu.filesystem.ILyuFileSystem#move(java.lang.String, java.lang.String)
	 * if deleting failed stop process, if adding fail, we need add back the node to src folder
	 */
	@Override
	public boolean move(String src, String target) throws FileNotFoundException, FileAlreadyExistsException, IllegalArgumentException{
		LyuDirectoryNode movedNode;
		try {
			movedNode = delete(src);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Cannot delete from source folder " + src, e);
		}
	
		boolean success;
		try {
			success = addNode(target, movedNode);
		} catch (FileNotFoundException | FileAlreadyExistsException e) {
			success = addNode(src, movedNode);//add back to source
			throw new RuntimeException("Cannot add folder or file to destination folder  " + target, e);
		}
		return success;
	}

	/*
	 * (non-Javadoc)
	 * @see com.lyu.filesystem.ILyuFileSystem#write(java.lang.String, java.lang.String)
	 */
	@Override
	public LyuFile write(String path, String content) throws FileNotFoundException, IllegalArgumentException {
		StringBuffer strBuffer = null;
		LyuDirectoryNode root = findRootNode(path);
		LinkedList<String> pathList = getPathList(path);
		LyuDirectoryNode insertLocation = root.findLocation(pathList, root);
		if(insertLocation.getNodeData().getFileType() != LyuFile.FILE_TYPE.FILE){
			throw new IllegalArgumentException("Only file cannbe writen.");
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

	private void updateSizeOnPath(int length, LyuDirectoryNode node) {
		while(node.getParent() != null){
			node = node.getParent();
			if(node.getNodeData() != null){
				node.getNodeData().setSize(node.getNodeData().getSize() + length);
			}
		}	
	}
	
	private LyuDirectoryNode createNewTree(String rootName){
		LyuDirectoryNode newRoot = new LyuDirectoryNode();
		newRoot.setRoot(true);
		newRoot.setCurrent(newRoot);
		return newRoot;
	}

	/*
	 * (non-Javadoc)
	 * @see com.lyu.filesystem.ILyuFileSystem#read(java.lang.String)
	 */
	@Override
	public String read(String path) throws FileNotFoundException,IllegalArgumentException{
		LyuDirectoryNode root = findRootNode(path);
		LinkedList<String> pathList = getPathList(path);
		LyuDirectoryNode readNode = root.findLocation(pathList, root);
		if(readNode == null || readNode.getNodeData() == null){
			throw new FileNotFoundException("No such file " + path + " can be read.");
		}
			
		if(readNode.getNodeData().getFileType() != LyuFile.FILE_TYPE.FILE){
			throw new IllegalArgumentException("Only file can be read.");
		}
		
		return readNode.getNodeData().getContent();	
	}
	/*
	 * @see com.lyu.filesystem.ILyuFileSystem#zip(java.lang.String, java.lang.String)
	 * @param filename - if zip a folder, the path need include the folder name
	 * if zip a file, then path stop at the last folder name and fileName is the zip file name
	 */
	public void zip(String path, String fileName) throws FileNotFoundException{
		LyuDirectoryNode root = findRootNode(path);
		LinkedList<String> pathList = getPathList(path);
		LyuDirectoryNode startNode = root.findLocation(pathList, root);
		
		Map<String, LyuDirectoryNode> childrenMap = startNode.getChildrenMap();
		if(childrenMap == null || !childrenMap.containsKey(fileName)){
			throw new FileNotFoundException("cannot find " + fileName + " under " + path);
		}
		startNode = childrenMap.get(fileName);
		startNode.getNodeData().setFileType(LyuFile.FILE_TYPE.ZIPFILE);
		int dotPos = fileName.indexOf('.');
		if(dotPos == -1)
			dotPos = fileName.length();
		startNode.getNodeData().setName(fileName.substring(0, dotPos)+ZIP_FILE_EX);
		startNode.zipFile();
		startNode.updateSizeAfterChange(startNode.getNodeData().getSize()* (-1));
	}
	
	private LinkedList<String> getPathList(String path) {
		if(userFileSeparator == null)
			userFileSeparator = getPathSeparator(path);
		String[] pathAndName = path.split(userFileSeparator);
		LinkedList<String> pathList = new LinkedList<String>(Arrays.asList(pathAndName));
		return pathList;
	}
	
	private String getPathSeparator(String path){
		if(!StringUtils.isBlank(path)){
			if(!path.contains(FILE_SEPARATOR)){
				FILE_SEPARATOR = "\\\\";
				return FILE_SEPARATOR;
			}
		}
		return FILE_SEPARATOR;	
	}

}
