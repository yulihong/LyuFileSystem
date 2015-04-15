package com.lyu.filesystem;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import org.apache.commons.lang.StringUtils;

import com.lyu.filesystem.entity.LyuFile;
import com.lyu.filesystem.impl.LyuDirectoryNode;
import com.lyu.filesystem.impl.LyuFileSystemImpl;

/**
 * 
 * @author Lihong Yu
 * @version 0.1
 * @since April 10, 2015
 * 
 * This is an application which implements an in-memory file system. This file-system consists of 4 types of entities: 
 * Drives, Folders, Text files, and Zip files.
 * 
 * These entities, very much like their â€œrealâ€� file-system counterparts, obey the following relations.

 * 1. A folder, a drive or a zip file, may contain zero to many other folders, or files (text or zip).
 * 2. A text file does not contain any other entity.
 * 3. A drive is not contained in any entity.
 * 4. Any non-drive entity must be contained in another entity.

 * If entity A contains entity B then we say that A is the parent of B.
 
 * Every entity has the following properties:

 *   + Type â€“ The type of the entity (one of the 4 type above).
 *   + Name - An alphanumeric string. Two entities with the same parent cannot have the same name. Similarly, two drives cannot have the same name.
 *   + Path â€“ The concatenation of the names of the containing entities, from the drive down to and including the entity. The names are separated by â€˜\â€™.
 *   + A text file has a property called Content which is a string.
 *   + Size â€“ an integer defined as follows:

 * Â§  For a text file â€“ it is the length of its content.
 * Â§  For a drive or a folder, it is the sum of all sizes of the entities it contains.
 * Â§  For a zip file, it is one half of the sum of all sizes of the entities it contains.
 
 * The system should be capable of supporting file-system like operations
 
 * 1)	Create â€“ Creates a new entity.
 * Arguments: Type, Name, Path of parent.
 * Exceptions: Path not found; Path already exists; Illegal File System Operation (if any of the rules a-d above is violated).

 * 2)	Delete â€“ Deletes an existing entity (and all the entities it contains).
 * Arguments: Path
 * Exceptions: Path not found.

 * 3)	Move â€“ Changing the parent of an entity.
 * Arguments: Source Path, Destination Path.
 * Exceptions: Path not found; Path already exists, Illegal File System Operation.

 * 4)	WriteToFile â€“ Changes the content of a text file.
 * Arguments: Path, Content
 * Exceptions: Path not found; Not a text file.
 
 * Tasks:
 * 1. Come up with the design for this system. Full implementation is not required, but only to the level which you feel is a â€œproof of conceptâ€�.
 * 2. Show a sketch of implementation of the Move operation.
 * 3. Explicitly implement the property Size.
 *
 */

/**
 * 
 * @version 0.1
 * @since April 10, 2015
 * This is the test class (run class) which use Linux/Unix path separator '/' and output 
 * a test report.
 * It also define a more user friendly API and hide some implementation complexity.
 * For example addDriver(String driverName) is more readable than
 * create(LyuFile.FILE_TYPE.DRIVE, driverName, driverName);
 */
public class LyuFileSystemApp {
	
	private static ILyuFileSystem fileSystem;
	private static final String ZIP_FILE_EX = ".zip";
	private  String userFileSeparator;
	private  String FILE_SEPARATOR = "/"; //if not / set to \\\\ 
	
	public LyuFileSystemApp() throws Exception {
		fileSystem = LyuFileSystemImpl.SingleInstance.INSTANCE.getSingleton();
	}

	public void addDriver(String driverName) throws IOException{
		fileSystem.create(LyuFile.FILE_TYPE.DRIVE, driverName, driverName);	
	}

	public void createFolder(String folderName, String path)  throws Exception {
		    fileSystem.create(LyuFile.FILE_TYPE.FOLDER, folderName, path);
	}
	
	public void createFile(String folderName, String path) throws Exception {
		    fileSystem.create(LyuFile.FILE_TYPE.FILE, folderName, path);
	}

	public void wrtieTextFile(String path, String filename, String textContent) throws Exception {
		    fileSystem.write(path+filename, textContent);
	}
	
	public void delete(String filename) throws Exception {
			fileSystem.delete(filename);
	}
	
	public void move(String originalFile, String destinationFile) throws Exception {
		    fileSystem.move(originalFile, destinationFile);
	}
	
	public void zip(String path, String filename) {
		LyuDirectoryNode root = fileSystem.findRootNode(path);
		LinkedList<String> pathList = getPathList(path);
		LyuDirectoryNode startNode = root.findLocation(pathList, root);
		startNode.getNodeData().setFileType(LyuFile.FILE_TYPE.ZIPFILE);
		int dotPos = filename.indexOf('.');
		if(dotPos == 0)
			dotPos = filename.length();
		startNode.getNodeData().setName(filename.substring(0, dotPos)+ZIP_FILE_EX);
		startNode.zipFile();
		startNode.updateSizeAfterChange(startNode.getNodeData().getSize()* (-1));
	}
	
	public String readTextFile(String path, String filename) {
		String textContent = null;
		try {
			textContent = fileSystem.read(path+filename);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return textContent;
	}
	
	public static void printOutTree(String driver) {
		LyuDirectoryNode rootNode = fileSystem.findRootNode(driver);
		rootNode.printOutTree("");
	}
	
	public void getSize(String driver) throws Exception {
		LyuDirectoryNode rootNode = fileSystem.findRootNode(driver);
		System.out.println("Size: " + rootNode.calculateTreeNodeSize());
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
	
	public static void main(String[] agrgs) {

		try {
			
			LyuFileSystemApp lyuFileSystem = new LyuFileSystemApp();
	
			System.out.println("============== Build Drivers C and E ==============");
			System.out.println("    |---------It also show creating folders and files ---------------------------|");
			System.out.println("    |---------File contents is writen into files and show size-------------------|");
			
		    lyuFileSystem.addDriver("C:/");	
		    lyuFileSystem.createFolder("dir1", "C:/");
		    lyuFileSystem.createFolder("dir2", "C:/");

		    lyuFileSystem.createFolder("dir3", "C:/dir1/");
		    lyuFileSystem.createFolder("dir4", "C:/dir1/");
		    lyuFileSystem.createFile("textFile_11.txt", "C:/dir1/");
		    lyuFileSystem.wrtieTextFile("C:/dir1/", "textFile_11.txt", "C:/dir1/textFile_11.txt --- Hello world!");
		    lyuFileSystem.createFile("textFile_12.txt", "C:/dir1/");
		    lyuFileSystem.wrtieTextFile("C:/dir1/", "textFile_12.txt", "C:/dir1/textFile_12.txt --- abcdefghijklmn");
		    lyuFileSystem.createFile("textFile_13.txt", "C:/dir1/");
		    lyuFileSystem.wrtieTextFile("C:/dir1/", "textFile_13.txt", "C:/dir1/textFile_13.txt --- 0123456789");


		    lyuFileSystem.createFile("textFile_31.txt", "C:/dir1/dir3/");
		    lyuFileSystem.wrtieTextFile("C:/dir1/dir3/", "textFile_31.txt", "C:/dir1/dir3/textFile_31.txt --- Hello world!");
		    
		    lyuFileSystem.addDriver("E:/");	
		    lyuFileSystem.createFolder("dirE1", "E:/");
		    lyuFileSystem.createFile("textFile_E11.txt", "E:/dirE1/");
		    lyuFileSystem.wrtieTextFile("E:/dirE1/", "textFile_E11.txt", "E:/dirE1/textFile_E11.txt --- Hello world!");
		    
		    System.out.println("\n=========== Bellow are the current file lists of C:/ and E:/ ===========");
		    
		    System.out.println("\n    +------------------------- File List of C:/ ---------------------------+");
		    printOutTree("C:/");
		    System.out.println("    +----------------------------------------------------------------------+");
		    
		    System.out.println("\n    +------------------------- List List of E:/ ---------------------------+");		    
		    printOutTree("E:/");
		    System.out.println("    +----------------------------------------------------------------------+");
		    
		    // ---------- Read file ---------- //
		    
		    System.out.println("\n=========== Read the file C:/dir1/textFile_11.txt ===========\n");
		    
		    String fileContentString = lyuFileSystem.readTextFile("C:/dir1/", "textFile_11.txt");
		    System.out.println("----- Bellow is the content of file C:/dir1/textFile_11.txt ----- ");
		    System.out.println(fileContentString);
		    
		    // ---------- Move file ---------- //
		    
		    System.out.println("\n=========== Move file C:/dir1/textFile_12.txt to C:/dir2/textFile_12.txt ===========");	    
		    lyuFileSystem.move("C:/dir1/textFile_12.txt", "C:/dir2/textFile_12.txt");
		    
		    System.out.println("\n    - Bellow is the file list of C:/ after moving the file C:/dir1/textFile_12.txt to C:/dir2/textFile_12.txt");
		    System.out.println("    - C:/dir1/textFile_12.txt has been removed.");
		    System.out.println("    - C:/dir2/textFile_12.txt was added.");
		    System.out.println("\n    +------------------------- List of C Driver ---------------------------+");
		    printOutTree("C:/");	
		    System.out.println("    +----------------------------------------------------------------------+");
		    
		    System.out.println("\n=========== Move file C:/dir1/dir3/textFile_31.txt to E:/dirE1/ ===========");
		    lyuFileSystem.move("C:/dir1/dir3/textFile_31.txt", "E:/dirE1/");
		    
		    System.out.println("\n    - Bellow are the file lists of C:/ and E:/ after moving the file C:/dir1/dir3/textFile_31.txt to E:/dirE1/");
		    System.out.println("    - C:/dir1/dir3/textFile_31.txt has been removed.");
		    System.out.println("    - E:/dirE1/textFile_31.txt was added.");
		    System.out.println("\n    +------------------------- List of C Driver ---------------------------+");
		    printOutTree("C:/");
		    System.out.println("    +----------------------------------------------------------------------+");
		    System.out.println("\n    +------------------------- List of E Driver ---------------------------+");
		    printOutTree("E:/");
		    System.out.println("    +----------------------------------------------------------------------+");

		    System.out.println("\n=========== Move folder E:/dirE1 to C:/dir1/ ===========");	    
		    lyuFileSystem.move("E:/dirE1/", "C:/dir1/");
		    
		    System.out.println("\n    - Bellow are the file lists of C:/ and E:/ after moving folder E:/dirE1 to C:/dir1/");
		    System.out.println("    - E:/ is now empty.");
		    System.out.println("    - Folder dirE1 was added to C:/dir1/.");
		    System.out.println("\n    +------------------------- List of C Driver ---------------------------+");
		    printOutTree("C:/");
		    System.out.println("    +----------------------------------------------------------------------+");
		    System.out.println("\n    +------------------------- List of E Driver ---------------------------+");
		    printOutTree("E:/");
		    System.out.println("    +----------------------------------------------------------------------+");		    

		    
		    // ---------- Delete file / folder ---------- //
		    
		    System.out.println("\n=========== Delete file C:/dir1/textFile_11.txt ===========");	    
		    lyuFileSystem.delete("C:/dir1/textFile_11.txt");
		    
		    System.out.println("\n    - Bellow are the file lists of C:/ after deleting file C:/dir1/textFile_11.txt");
		    System.out.println("    - File C:/dir1/textFile_11.txt was removed.");
		    System.out.println("\n    +------------------------- List of C Driver ---------------------------+");
		    printOutTree("C:/");
		    System.out.println("    +----------------------------------------------------------------------+");
		    
		    
		    // ---------- Zip file / folder ---------- //
		    
		    System.out.println("\n=========== Zip file C:/dir1/textFile_13.txt ===========");
		    lyuFileSystem.zip("C:/dir1/", "textFile_13.txt");
		    
		    System.out.println("\n    - Bellow are the file lists of C:/ after zipping file C:/dir1/textFile_13.txt");
		    System.out.println("    - The name of file C:/dir1/textFile_13.txt has been changed to textFile_13.zip.");
		    System.out.println("    - The size of file C:/dir1/textFile_13.zip has been changed from 38 to 19. ");
		    System.out.println("    - It's parent folders (C:/, C:/dir1) are both reduce size correctly (167 -> 148, 125 -> 106).");
		    System.out.println("\n    +------------------------- List of C Driver ---------------------------+");
		    printOutTree("C:/");
		    System.out.println("    +----------------------------------------------------------------------+");
			
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
