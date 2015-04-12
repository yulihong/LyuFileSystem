package com.lyu.filesystem;

import java.io.IOException;

import com.lyu.filesystem.entity.LyuFile;
import com.lyu.filesystem.impl.LyuDirectoryNode;

public interface ILyuFileSystem {
	LyuFile create(LyuFile.FILE_TYPE fileType, String name, String path) throws IOException;
	LyuDirectoryNode delete(String path) throws IOException;
	boolean move(String src, String target) throws IOException;
	LyuFile write(String path, String content) throws IOException;
	public void zipFile( LyuDirectoryNode startNode);
	LyuDirectoryNode getRootNode(String rootName);
	void printOutTree(LyuDirectoryNode startNode);
	public LyuDirectoryNode findLocation(String path);
	//TODO add read
}
