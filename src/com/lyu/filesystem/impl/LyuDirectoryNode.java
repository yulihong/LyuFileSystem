package com.lyu.filesystem.impl;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.lyu.filesystem.entity.LyuFile;

/**
 * @author Lihong Yu
 * @version 0.1
 * @since April 10, 2015
 *
 *        This is the most important data structure for this file system. It is
 *        called a node, but it can be a whole tree because each node has a
 *        parent and as many as you need children. If the node is the root, then
 *        you can get the whole tree with it.
 */
public class LyuDirectoryNode {

	private LyuDirectoryNode parent;
	private LyuDirectoryNode current;
	private LyuFile nodeData;
	private HashMap<String, LyuDirectoryNode> childrenMap;
	boolean isRoot;

	public LyuDirectoryNode() {
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
		if (nodeData.getFileType() == LyuFile.FILE_TYPE.ZIPFILE) {
			zipFile();
		}
	}

	public void zipFile() {
		if (this.getNodeData() != null) {
			this.getNodeData().setSize(this.getNodeData().getSize() / 2);
		}

		this.getChildrenMap().forEach((k, v) -> {
			v.zipFile();
		});

	}

	/**
	 * 
	 * @param changedSize
	 *            can be negitive
	 */
	public void updateSizeAfterChange(int changedSize) {
		if (this.getNodeData() == null)
			return;

		LyuDirectoryNode parentNode = this.getParent();
		while (parentNode != null) {
			if (parentNode.getNodeData() != null) {
				int oldSize = parentNode.getNodeData().getSize();
				parentNode.getNodeData().setSize(oldSize + changedSize);
			}
			parentNode = parentNode.getParent();
		}

	}

	public LyuDirectoryNode findPathEndNode(LyuDirectoryNode startNode,
			List<String> pathList) throws FileNotFoundException {
		LyuDirectoryNode pathEndNode = startNode;
		Map<String, LyuDirectoryNode> cildrenMap = startNode.getChildrenMap();
		while (!cildrenMap.isEmpty()) {
			String curListHead = pathList.get(0);
			pathList.remove(curListHead);// move one forward to get next level
											// path name
			if (pathList.size() == 0) {
				return pathEndNode;
			}
			pathEndNode = cildrenMap.get(pathList.get(0));
			if (pathEndNode == null) {
				throw new FileNotFoundException("Wrong path: "
						+ StringUtils.join(pathList.toArray()));
			}
			cildrenMap = pathEndNode.getChildrenMap();
		}

		return pathEndNode;
	}

	public LyuDirectoryNode findLeaf(LyuDirectoryNode rootNode) {
		LyuDirectoryNode pathEndNode = rootNode;
		Map<String, LyuDirectoryNode> cildrenMap = rootNode.getChildrenMap();
		while (!cildrenMap.isEmpty()) {
			Set<String> keys = cildrenMap.keySet();
			pathEndNode = cildrenMap.get(keys.iterator().next());
			cildrenMap = pathEndNode.getChildrenMap();
		}

		return pathEndNode;

	}

	public void clearTree() {
		HashMap<String, LyuDirectoryNode> cildrenMap = this.getChildrenMap();
		LyuDirectoryNode pathEndNode = this;
		while (!cildrenMap.isEmpty()) {
			Set<String> keys = cildrenMap.keySet();
			pathEndNode = cildrenMap.get(keys.iterator().next());
			cildrenMap = pathEndNode.getChildrenMap();
		}

		LyuDirectoryNode parent = pathEndNode.getParent();
		if (parent != null) {
			parent.getChildrenMap().clear();
			parent.clearTree();
		}
	}

	public void clearNodeData() {
		if (this.getNodeData() != null) {
			this.getNodeData().setContent(null);
			this.getNodeData().setSize(0);
		}
	}

	public int calculateTreeNodeSize() {
		int sum = 0;

		if (this.getNodeData() != null
				&& this.getNodeData().getFileType() == LyuFile.FILE_TYPE.FILE) {
			sum += this.getNodeData().getSize();
		}

		Map<String, LyuDirectoryNode> cildrenMap = this.getChildrenMap();
		if (cildrenMap == null) {
			return sum;
		}

		if (cildrenMap != null) {
			Iterator<String> keys = cildrenMap.keySet().iterator();
			while (keys.hasNext()) {
				LyuDirectoryNode tmp = cildrenMap.get(keys.next());
				sum += tmp.calculateTreeNodeSize();
			}
		}

		return sum;
	}

	public void printOutTree(String treeIndentA) {

		String treeIndentB = treeIndentA + "   ";

		System.out.print(treeIndentB + " |-- " + this.getNodeData().getName()
				+ " ---------- ");
		System.out.print("Type: " + this.getNodeData().getFileType() + ",   ");
		System.out.println("Size: " + this.getNodeData().getSize());
		if(this.getNodeData().getFileType() == LyuFile.FILE_TYPE.FILE){
			System.out.println("Content: " + this.getNodeData().getContent());
		}

		this.getChildrenMap().forEach((k, v) -> {
			v.printOutTree(treeIndentB);
		});
	}

	public LyuDirectoryNode findLocation(LinkedList<String> pathList,
			LyuDirectoryNode root) {
		LyuDirectoryNode insertLocation = null;
		try {
			insertLocation = root.findPathEndNode(root, pathList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return insertLocation;
	}

}
