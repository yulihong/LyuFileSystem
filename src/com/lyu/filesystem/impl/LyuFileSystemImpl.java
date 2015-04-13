package com.lyu.filesystem.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

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

	private static final String DEFAULT_ROOT_NAME = "C:\\";
	private static HashMap<String, LyuDirectoryNode> trees;
	public String FILE_SEPARATOR = "/"; //if not / set to \\\\ 
	private String userFileSeparator;
	private String WINDOWS_SEPARATOR = "\\";
	
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
	
	public void clearTree(LyuDirectoryNode startNode){	
		if(startNode == null){
			System.out.println("This is an empty tree already.");
			return;
		}
		
		HashMap<String, LyuDirectoryNode> cildrenMap = startNode.getChildrenMap();
		LyuDirectoryNode pathEndNode = startNode;
		while(!cildrenMap.isEmpty()){
			Set<String> keys = cildrenMap.keySet();
			pathEndNode = cildrenMap.get(keys.iterator().next());
			cildrenMap = pathEndNode.getChildrenMap();
		}
		
		LyuDirectoryNode parent = pathEndNode.getParent();
		if(parent != null){
			parent.getChildrenMap().clear();
			clearTree(parent);
		}
	}
	
	/**
	 * path cannot contain name here
	 */
	@Override
	public LyuFile create(FILE_TYPE fileType, String name, String path)throws FileNotFoundException, FileAlreadyExistsException {
		LyuFile newFile = new LyuFile(name,  path, fileType);
		LyuDirectoryNode root = findRootNode(path);
		
		LinkedList<String> pathList = getPathList(path);
			
		try {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return newFile;
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


	public LinkedList<String> getPathList(String path) {
		if(userFileSeparator == null)
			userFileSeparator = getPathSeparator(path);
		String[] pathAndName = path.split(userFileSeparator);
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

	@Override
	public LyuDirectoryNode delete(String path) throws IOException, FileNotFoundException {
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
			int deletedSize = needDeleted.getNodeData().getSize();
			int newSize = calculateTreeNodeSize(parentNode);
			parentNode.getNodeData().setSize(newSize);
			parentNode.updateSizeAfterChange(deletedSize*(-1));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return needDeleted;
	}
	
	private boolean addNode(String path, LyuDirectoryNode addedNode) throws FileAlreadyExistsException {
		if(addedNode == null || addedNode.getNodeData() == null){
			return false;
		}
		LyuDirectoryNode insertLocation = findLocation(path);
		if(insertLocation.getChildrenMap() != null && insertLocation.getChildrenMap().containsKey(addedNode.getNodeData().getName())){
			throw new FileAlreadyExistsException(addedNode.getNodeData().getName() + " already exist");
		}
		insertLocation.getChildrenMap().put(addedNode.getNodeData().getName(), addedNode);
		int addedSize = addedNode.getNodeData().getSize();
		insertLocation.getNodeData().setSize(insertLocation.getNodeData().getSize()+addedSize);
		insertLocation.updateSizeAfterChange(addedSize);
		return true;
	}

	@Override
	public boolean move(String src, String target) throws FileNotFoundException, FileAlreadyExistsException, IOException {
		LyuDirectoryNode movedNode = delete(src);
		boolean success = addNode(target, movedNode);
		return success;
	}

	@Override
	public LyuFile write(String path, String content) throws FileNotFoundException, IOException{
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

	private void updateSizeOnPath(int length, LyuDirectoryNode node) {
		while(node.getParent() != null){
			node = node.getParent();
			if(node.getNodeData() != null){
				node.getNodeData().setSize(node.getNodeData().getSize() + length);
			}
		}	
	}
	
	public int calculateTreeNodeSize(LyuDirectoryNode startNode){
		int sum = 0;
		if(startNode == null){
			return sum;
		}
		
		if(startNode.getNodeData() != null && startNode.getNodeData().getFileType() == LyuFile.FILE_TYPE.FILE){
			sum += startNode.getNodeData().getSize();
		}	
		
		Map<String, LyuDirectoryNode> cildrenMap = startNode.getChildrenMap();
		if(cildrenMap == null ){
			return sum;
		}
		
		if(cildrenMap != null){
			Iterator<String> keys = cildrenMap.keySet().iterator();
			while(keys.hasNext()){			
				LyuDirectoryNode tmp = cildrenMap.get(keys.next());
				sum += calculateTreeNodeSize(tmp);	
			}
		}
		
		return sum;
	}

	public LyuDirectoryNode findLocation(String path) {
		LyuDirectoryNode root = findRootNode(path);
		if(path.startsWith(userFileSeparator))
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

	@Override
	public String read(String path) throws FileNotFoundException,IOException{
		LyuDirectoryNode readNode = findLocation(path);
		if(readNode == null || readNode.getNodeData() == null){
			throw new FileNotFoundException("No such file " + path + " can be read.");
		}
			
		if(readNode.getNodeData().getFileType() != LyuFile.FILE_TYPE.FILE){
			throw new IOException("Only file can be read.");
		}
		
		return readNode.getNodeData().getContent();	
	}

}
