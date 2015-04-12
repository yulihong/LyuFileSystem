package com.lyu.filesystem.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lyu.filesystem.entity.LyuFile;

public class LyuDirectoryNode {
	private LyuDirectoryNode parent;
	private LyuDirectoryNode current;
	private LyuFile nodeData;
	private HashMap<String, LyuDirectoryNode> childrenMap;
	boolean isRoot;
	
	public LyuDirectoryNode(){
		childrenMap = new HashMap<String, LyuDirectoryNode>();
	}
		
	public LyuDirectoryNode getParent() {
		return parent;
	}
	public void setParent(LyuDirectoryNode parent) {
		this.parent = parent;
	}
	public LyuDirectoryNode getCurrent() {
		return current;
	}
	public void setCurrent(LyuDirectoryNode current) {
		this.current = current;
	}
	public HashMap<String, LyuDirectoryNode> getChildrenMap() {
		return childrenMap;
	}
	public void setChildrenMap(HashMap<String, LyuDirectoryNode> childrenMap) {
		this.childrenMap = childrenMap;
	}
	public boolean isRoot() {
		return isRoot;
	}
	public void setRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}
	public LyuFile getNodeData() {
		return nodeData;
	}
	public void setNodeData(LyuFile nodeData) {
		this.nodeData = nodeData;
		if(nodeData.getFileType() == LyuFile.FILE_TYPE.ZIPFILE){
			
		}
	}
	
	public LyuDirectoryNode findPathEndNode(LyuDirectoryNode startNode, List<String> pathList) throws Exception{
		LyuDirectoryNode pathEndNode = startNode;
		Map<String, LyuDirectoryNode> cildrenMap = startNode.getChildrenMap();
		while(!cildrenMap.isEmpty()){
			String curListHead = pathList.get(0);
			pathList.remove(curListHead);//move one forward to get next level path name
			if(pathList.size() == 0){
				return pathEndNode; 
			}
			pathEndNode = cildrenMap.get(pathList.get(0));
			if(pathEndNode == null)
			{
				throw new Exception("Wrong path!!!");
			}
			cildrenMap = pathEndNode.getChildrenMap();
		}
		
		return pathEndNode; 
	}
	
	

}
