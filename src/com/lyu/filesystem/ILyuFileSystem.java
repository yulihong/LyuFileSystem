package com.lyu.filesystem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

import com.lyu.filesystem.entity.LyuFile;
import com.lyu.filesystem.impl.LyuDirectoryNode;
/**
 * 
 * @author Lihong Yu
 * @version 0.1
 * @since April 10, 2015
 *
 *This interface define APIs for this file system
 */
public interface ILyuFileSystem {
	LyuFile create(LyuFile.FILE_TYPE fileType, String name, String path) throws FileNotFoundException, FileAlreadyExistsException;
	LyuDirectoryNode delete(String path) throws IOException, FileNotFoundException;
	boolean move(String src, String target) throws FileNotFoundException, FileAlreadyExistsException, IOException;
	LyuFile write(String path, String content) throws FileNotFoundException, IOException;
	String read(String path) throws FileNotFoundException,IOException;
	//The following are heavily used in LyuFileSystemImpl for 
	//manipulating the information saved in the trees.
	//It would be better to hide them ( move out of this API)
	//This would be a 
	//TODO -Improvement: task for improvement
	LyuDirectoryNode getRootNode(String rootName);
	void printOutTree(LyuDirectoryNode startNode);
	LyuDirectoryNode findLocation(String path);
	void clearTree(LyuDirectoryNode startNode);
	int calculateTreeNodeSize(LyuDirectoryNode startNode);
}
