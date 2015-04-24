package com.lyu.filesystem;

import java.io.FileNotFoundException;
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
	public static final String DEFAULT_ROOT_NAME = "C:\\";
	public static final String WINDOWS_SEPARATOR = "\\";
	
	LyuFile create(LyuFile.FILE_TYPE fileType, String name, String path) throws FileNotFoundException, FileAlreadyExistsException;
	LyuDirectoryNode delete(String path) throws FileNotFoundException,IllegalArgumentException;
	boolean move(String src, String target) throws FileNotFoundException, FileAlreadyExistsException, IllegalArgumentException;
	LyuFile write(String path, String content) throws FileNotFoundException;
	String read(String path) throws FileNotFoundException, IllegalArgumentException;
	void zip(String path, String fileName) throws FileNotFoundException;
	//The following method is used in LyuFileSystemImpl for 
	//finding root in a tree. This need to be defined here because
	//there are multiple trees in the file system
	public LyuDirectoryNode findRootNode(String path) throws FileNotFoundException;
}
