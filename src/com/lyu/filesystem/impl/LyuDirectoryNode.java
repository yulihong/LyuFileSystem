package com.lyu.filesystem.impl;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

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
		if(nodeData.getFileType() == LyuFile.FILE_TYPE.ZIPFILE)
		{
			zipFile();
		}
	}
	
	public void zipFile(){	
		if(this.getNodeData() != null){
			this.getNodeData().setSize(this.getNodeData().getSize()/2);
		}
		
		this.getChildrenMap().forEach((k, v) -> {
			v.zipFile();
		});	
		
	}
	/**
	 * 
	 * @param changedSize can be negitive
	 */
	public void updateSizeAfterChange(int changedSize){
		if(this.getNodeData() == null)
			return;
		
		LyuDirectoryNode parentNode = this.getParent();
		while(parentNode != null){	
			if(parentNode.getNodeData() != null){
				int oldSize =parentNode.getNodeData().getSize();
				parentNode.getNodeData().setSize(oldSize + changedSize);
			}
			parentNode = parentNode.getParent();
		}
		
	}
	
	public LyuDirectoryNode findPathEndNode(LyuDirectoryNode startNode, List<String> pathList) throws FileNotFoundException{
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
				throw new FileNotFoundException("Wrong path: " +  StringUtils.join(pathList.toArray()));
			}
			cildrenMap = pathEndNode.getChildrenMap();
		}
		
		return pathEndNode; 
	}
	
	public LyuDirectoryNode findLeaf(LyuDirectoryNode rootNode){
		LyuDirectoryNode pathEndNode = rootNode;
		Map<String, LyuDirectoryNode> cildrenMap = rootNode.getChildrenMap();
		while(!cildrenMap.isEmpty()){
			Set<String> keys = cildrenMap.keySet();
			pathEndNode = cildrenMap.get(keys.iterator().next());
			cildrenMap = pathEndNode.getChildrenMap();
		}
		
		return pathEndNode; 
		
	}
	
	public void clearNodeData(){
		if(this.getNodeData() != null){
			this.getNodeData().setContent(null);
			this.getNodeData().setSize(0);
		}
	}

}
