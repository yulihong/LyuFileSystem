package com.lyu.filesystem;

import java.io.IOException;
import java.util.LinkedList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.lyu.filesystem.entity.LyuFile;
import com.lyu.filesystem.impl.LyuDirectoryNode;
import com.lyu.filesystem.impl.LyuFileSystemImpl;

public class TestLyuFileSystem {
	private static ILyuFileSystem lyuFileSystem;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//lyuFileSystem = LyuFileSystemImpl.SingleInstance.INSTANCE.getSingleton();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		//lyuFileSystem = null;
	}
	
	@Before
	public void setUp() throws Exception {
		lyuFileSystem = LyuFileSystemImpl.SingleInstance.INSTANCE.getSingleton();
	}

	@After
	public void tearDown() throws Exception {
		TestDeleteTree();
		lyuFileSystem = null;
	}

	//@Test
	public void testCreate() {
		try {
			LyuFile createdFolder = lyuFileSystem.create(LyuFile.FILE_TYPE.FOLDER, "testfolder", "C:\\");	    
		    LyuFile createdFile = lyuFileSystem.create(LyuFile.FILE_TYPE.FILE, "testfile.txt", "C:\\testfolder");
		    Assert.assertNotNull(createdFolder);
		    Assert.assertNotNull(createdFile);
		    System.out.println("This is the tree:");
		    LyuDirectoryNode rootNode = lyuFileSystem.getRootNode("C:\\");
			lyuFileSystem.printOutTree(rootNode);	    
		    
		    LyuFile createdDrive = lyuFileSystem.create(LyuFile.FILE_TYPE.DRIVE, "E:\\", "E:\\");		
		    lyuFileSystem.create(LyuFile.FILE_TYPE.FOLDER, "tmp", "E:\\");		
		    LyuDirectoryNode rootNode1 = lyuFileSystem.getRootNode("E:\\");			
			System.out.println("This is the tree after adding a folder tmp:");
			lyuFileSystem.printOutTree(rootNode1);
		    Assert.assertNotNull(createdDrive);
		    
		    LyuFile createdFile1 = lyuFileSystem.create(LyuFile.FILE_TYPE.FILE, "testfile2.txt", "E:\\tmp");
		    System.out.println("This is the tree after adding a file to tmp:");
			lyuFileSystem.printOutTree(rootNode1);
		    Assert.assertNotNull(createdFile1);
		    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//@Test
	public void testMove() {
		try {
			LyuFile createdFolder = lyuFileSystem.create(LyuFile.FILE_TYPE.FOLDER, "testfolder", "C:\\");
			Assert.assertNotNull(createdFolder);
			LyuFile createdFile = lyuFileSystem.create(LyuFile.FILE_TYPE.FILE, "testfile.txt", "C:\\testfolder");
			Assert.assertNotNull(createdFile);
			
			LyuFile createdDrive = lyuFileSystem.create(LyuFile.FILE_TYPE.DRIVE, "E:\\", "E:\\");	
			Assert.assertNotNull(createdDrive);
			LyuFile createdFolderInDrive = lyuFileSystem.create(LyuFile.FILE_TYPE.FOLDER, "tmp", "E:\\tmp");	
			Assert.assertNotNull(createdFolderInDrive);
			createdFile = lyuFileSystem.create(LyuFile.FILE_TYPE.FILE, "testfile2.txt", "E:\\tmp");
			Assert.assertNotNull(createdFile);
			
			LyuDirectoryNode driveRootNode = lyuFileSystem.getRootNode("E:\\");
			LyuDirectoryNode rootNode = lyuFileSystem.getRootNode("C:\\");
			System.out.println("This is the tree before moving:");
			lyuFileSystem.printOutTree(rootNode);
			lyuFileSystem.printOutTree(driveRootNode);
			boolean success = lyuFileSystem.move("E:\\tmp\\testfile2.txt", "C:\\testfolder");
			Assert.assertTrue(success);
			System.out.println("This is the tree after moving:");
			lyuFileSystem.printOutTree(rootNode);
			lyuFileSystem.printOutTree(driveRootNode);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testDelete() {
		try {			
			LyuFile createdDrive = lyuFileSystem.create(LyuFile.FILE_TYPE.FOLDER, "E:\\", "E:\\");		
		    lyuFileSystem.create(LyuFile.FILE_TYPE.FOLDER, "tmp", "E:\\");		
		    LyuDirectoryNode rootNode = lyuFileSystem.getRootNode("E:\\");			
			System.out.println("This is the tree:");
			lyuFileSystem.printOutTree(rootNode);
		    Assert.assertNotNull(createdDrive);
		    
		    LyuFile createdFile = lyuFileSystem.create(LyuFile.FILE_TYPE.FILE, "testfile2.txt", "E:\\tmp");
		    System.out.println("This is the tree:");
			lyuFileSystem.printOutTree(rootNode);
		    Assert.assertNotNull(createdFile);
			
			rootNode = lyuFileSystem.getRootNode("E:\\");	
			System.out.println("This is the tree before deleteing:");
			lyuFileSystem.printOutTree(rootNode);
			LyuDirectoryNode deletedNode = lyuFileSystem.delete("E:\\tmp\\testfile2.txt");
			Assert.assertNotNull(deletedNode);
			System.out.println("This is the tree after deleteing:");
			lyuFileSystem.printOutTree(rootNode);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//@Test
	public void testWrite() {
		try {			
			LyuFile createdDrive = lyuFileSystem.create(LyuFile.FILE_TYPE.FOLDER, "E:\\", "E:\\");		
		    lyuFileSystem.create(LyuFile.FILE_TYPE.FOLDER, "tmp", "E:\\");		
		    LyuDirectoryNode rootNode = lyuFileSystem.getRootNode("E:\\");			
			System.out.println("This is the tree:");
			lyuFileSystem.printOutTree(rootNode);
		    Assert.assertNotNull(createdDrive);
		    
		    LyuFile createdFile = lyuFileSystem.create(LyuFile.FILE_TYPE.FILE, "testfile2.txt", "E:\\tmp");
		    System.out.println("This is the tree:");
			lyuFileSystem.printOutTree(rootNode);
		    Assert.assertNotNull(createdFile);
			
			rootNode = lyuFileSystem.getRootNode("E:\\");	
			System.out.println("This is the tree before writing:");
			lyuFileSystem.printOutTree(rootNode);
			LyuFile updatedFile = lyuFileSystem.write("E:\\tmp\\testfile2.txt", "Hello world!");
			Assert.assertNotNull(updatedFile);
			System.out.println("This is the tree after writing:");
			lyuFileSystem.printOutTree(rootNode);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//@Test
	public void testSize() {
		try {		
			LyuFile createdDrive = lyuFileSystem.create(LyuFile.FILE_TYPE.FOLDER, "E:\\", "E:\\");	
			Assert.assertNotNull(createdDrive);
		    lyuFileSystem.create(LyuFile.FILE_TYPE.FOLDER, "tmp", "E:\\");	
		    
		    LyuFile createdFile = lyuFileSystem.create(LyuFile.FILE_TYPE.FILE, "testfile1.txt", "E:\\tmp");
		    Assert.assertNotNull(createdFile);
		    LyuFile updatedFile = lyuFileSystem.write("E:\\tmp\\testfile1.txt", "Hello world!");
			Assert.assertNotNull(updatedFile);
			updatedFile = lyuFileSystem.write("E:\\tmp\\testfile1.txt", "012345");
			Assert.assertNotNull(updatedFile);
		    
		    createdFile = lyuFileSystem.create(LyuFile.FILE_TYPE.FILE, "testfile2.txt", "E:\\tmp");
		    Assert.assertNotNull(createdFile);
		   
		    updatedFile = lyuFileSystem.write("E:\\tmp\\testfile2.txt", "Hello world!");
			Assert.assertNotNull(updatedFile);
			updatedFile = lyuFileSystem.write("E:\\tmp\\testfile2.txt", "0123456789");
			Assert.assertNotNull(updatedFile);
			LyuDirectoryNode rootNode = lyuFileSystem.getRootNode("E:\\");
			System.out.println("This is the tree after writing:");
			lyuFileSystem.printOutTree(rootNode);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//@Test
	public void testZip() {
		try {		
			LyuFile createdDrive = lyuFileSystem.create(LyuFile.FILE_TYPE.DRIVE, "E:\\", "E:\\");	
			Assert.assertNotNull(createdDrive);
		    lyuFileSystem.create(LyuFile.FILE_TYPE.FOLDER, "tmp", "E:\\");	
		    
		    LyuFile createdFile = lyuFileSystem.create(LyuFile.FILE_TYPE.FILE, "testfile1.txt", "E:\\tmp");
		    Assert.assertNotNull(createdFile);
		    LyuFile updatedFile = lyuFileSystem.write("E:\\tmp\\testfile1.txt", "Hello world!");
			Assert.assertNotNull(updatedFile);
			updatedFile = lyuFileSystem.write("E:\\tmp\\testfile1.txt", "012345");
			Assert.assertNotNull(updatedFile);
		    
		    createdFile = lyuFileSystem.create(LyuFile.FILE_TYPE.FILE, "testfile2.txt", "E:\\tmp");
		    Assert.assertNotNull(createdFile);
		   
		    updatedFile = lyuFileSystem.write("E:\\tmp\\testfile2.txt", "Hello world!");
			Assert.assertNotNull(updatedFile);
			updatedFile = lyuFileSystem.write("E:\\tmp\\testfile2.txt", "0123456789");
			Assert.assertNotNull(updatedFile);
			LyuDirectoryNode rootNode = lyuFileSystem.getRootNode("E:\\");
			LyuDirectoryNode startNode = lyuFileSystem.findLocation("E:\\tmp\\testfile2.txt");
			startNode.getNodeData().setFileType(LyuFile.FILE_TYPE.ZIPFILE);
			startNode.zipFile();
			startNode.updateSizeAfterChange(startNode.getNodeData().getSize()*(-1));
			System.out.println("This is the tree after writing:");
			lyuFileSystem.printOutTree(rootNode);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//@Test
	public void testRead() {
		try {		
			LyuFile createdDrive = lyuFileSystem.create(LyuFile.FILE_TYPE.ZIPFILE, "E:\\", "E:\\");	
			Assert.assertNotNull(createdDrive);
		    lyuFileSystem.create(LyuFile.FILE_TYPE.FOLDER, "tmp", "E:\\");	
		    
		    LyuFile createdFile = lyuFileSystem.create(LyuFile.FILE_TYPE.FILE, "testfile1.txt", "E:\\tmp");
		    Assert.assertNotNull(createdFile);
		    LyuFile updatedFile = lyuFileSystem.write("E:\\tmp\\testfile1.txt", "Hello world!");
			Assert.assertNotNull(updatedFile);
			updatedFile = lyuFileSystem.write("E:\\tmp\\testfile1.txt", "012345");
			Assert.assertNotNull(updatedFile);
		    
		    createdFile = lyuFileSystem.create(LyuFile.FILE_TYPE.FILE, "testfile2.txt", "E:\\tmp");
		    Assert.assertNotNull(createdFile);
		   
		    updatedFile = lyuFileSystem.write("E:\\tmp\\testfile2.txt", "Hello world!");
			Assert.assertNotNull(updatedFile);
			updatedFile = lyuFileSystem.write("E:\\tmp\\testfile2.txt", "0123456789");
			Assert.assertNotNull(updatedFile);
			LyuDirectoryNode rootNode = lyuFileSystem.getRootNode("E:\\");
			LyuDirectoryNode startNode = lyuFileSystem.findLocation("E:\\tmp\\testfile2.txt");
			String str = lyuFileSystem.read("E:\\tmp\\testfile2.txt");
			System.out.println("Content in E:\\tmp\\testfile2.txt is: " + str);
			lyuFileSystem.printOutTree(rootNode);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//@Test
	public void TestDeleteTree(){
		try {
			//createdFolder = lyuFileSystem.create(LyuFile.FILE_TYPE.FOLDER, "testfolder", "C:\\");
		  
		    //LyuFile createdFile = lyuFileSystem.create(LyuFile.FILE_TYPE.FILE, "testfile.txt", "C:\\testfolder");
		    //Assert.assertNotNull(createdFolder);
		   // Assert.assertNotNull(createdFile);
		    
		    LyuDirectoryNode rootNode = lyuFileSystem.getRootNode("C:\\");
		    LyuDirectoryNode rootNode1 = lyuFileSystem.getRootNode("E:\\");
			lyuFileSystem.printOutTree(rootNode);	 
		    
			lyuFileSystem.clearTree(rootNode);
			lyuFileSystem.clearTree(rootNode1);
			System.out.println("After delete Tree:");
			lyuFileSystem.printOutTree(rootNode);
			lyuFileSystem.clearTree(rootNode1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	
	//@Test
	public void testCalcualteSize(){
		try {		
			LyuFile createdDrive = lyuFileSystem.create(LyuFile.FILE_TYPE.DRIVE, "E:\\", "E:\\");	
			Assert.assertNotNull(createdDrive);
		    lyuFileSystem.create(LyuFile.FILE_TYPE.FOLDER, "tmp", "E:\\");	
		    LyuFile createdFile = lyuFileSystem.create(LyuFile.FILE_TYPE.FILE, "testfile3.txt", "E:\\");
		    LyuFile updatedFile = lyuFileSystem.write("E:\\testfile3.txt", "0123456789");
			Assert.assertNotNull(updatedFile);
		    
		    createdFile = lyuFileSystem.create(LyuFile.FILE_TYPE.FILE, "testfile1.txt", "E:\\tmp");
		    Assert.assertNotNull(createdFile);
		    updatedFile = lyuFileSystem.write("E:\\tmp\\testfile1.txt", "Hello world!");
			Assert.assertNotNull(updatedFile);
			updatedFile = lyuFileSystem.write("E:\\tmp\\testfile1.txt", "012345");
			Assert.assertNotNull(updatedFile);
		    
		    createdFile = lyuFileSystem.create(LyuFile.FILE_TYPE.FILE, "testfile2.txt", "E:\\tmp");
		    Assert.assertNotNull(createdFile);
		   
		    updatedFile = lyuFileSystem.write("E:\\tmp\\testfile2.txt", "Hello world!");
			Assert.assertNotNull(updatedFile);
			updatedFile = lyuFileSystem.write("E:\\tmp\\testfile2.txt", "0123456789");
			Assert.assertNotNull(updatedFile);
			LyuDirectoryNode rootNode = lyuFileSystem.getRootNode("E:\\");
			System.out.println("This is the tree after writing:");
			int sizeOfRoot = lyuFileSystem.calculateTreeNodeSize(rootNode);
			System.out.println("The tree size is: " + sizeOfRoot);
			lyuFileSystem.printOutTree(rootNode);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
