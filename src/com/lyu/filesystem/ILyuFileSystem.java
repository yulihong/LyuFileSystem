package com.lyu.filesystem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

import com.lyu.filesystem.entity.LyuFile;
import com.lyu.filesystem.impl.LyuDirectoryNode;

public interface ILyuFileSystem {
	LyuFile create(LyuFile.FILE_TYPE fileType, String name, String path) throws FileNotFoundException, FileAlreadyExistsException;
	LyuDirectoryNode delete(String path) throws IOException, FileNotFoundException;
	boolean move(String src, String target) throws FileNotFoundException, FileAlreadyExistsException, IOException;
	LyuFile write(String path, String content) throws FileNotFoundException, IOException;
	String read(String path) throws FileNotFoundException,IOException;
	
	LyuDirectoryNode getRootNode(String rootName);
	void printOutTree(LyuDirectoryNode startNode);
	LyuDirectoryNode findLocation(String path);
	void clearTree(LyuDirectoryNode startNode);
	int calculateTreeNodeSize(LyuDirectoryNode startNode);
}
