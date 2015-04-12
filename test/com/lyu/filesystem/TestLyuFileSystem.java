package com.lyu.filesystem;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.lyu.filesystem.entity.LyuFile;
import com.lyu.filesystem.impl.LyuDirectoryNode;
import com.lyu.filesystem.impl.LyuFileSystemImpl;

public class TestLyuFileSystem {
	private static ILyuFileSystem lyuFileSystem;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		lyuFileSystem = LyuFileSystemImpl.SingleInstance.INSTANCE.getSingleton();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		lyuFileSystem = null;
	}
	
	@After
	public void tearDown() throws Exception {
	}

	//@Test
	public void testCreate() {
		try {
			LyuFile createdFolder = lyuFileSystem.create(LyuFile.FILE_TYPE.FOLDER, "testfolder", "/Users");
			
			LyuDirectoryNode rootNode = lyuFileSystem.getRootNode("/Users");
			
			System.out.println("This is the tree:");
			lyuFileSystem.printOutTree(rootNode);
		    Assert.assertNotNull(createdFolder);
		    
		    LyuFile createdFile = lyuFileSystem.create(LyuFile.FILE_TYPE.FILE, "testfile.txt", "/Users/testfolder");
		    System.out.println("This is the tree:");
			lyuFileSystem.printOutTree(rootNode);
		    Assert.assertNotNull(createdFile);
		    
		    LyuFile createdDrive = lyuFileSystem.create(LyuFile.FILE_TYPE.FOLDER, "C:/", "C:/");		
		    lyuFileSystem.create(LyuFile.FILE_TYPE.FOLDER, "tmp", "C:/tmp");		
		    rootNode = lyuFileSystem.getRootNode("C:/");			
			System.out.println("This is the tree:");
			lyuFileSystem.printOutTree(rootNode);
		    Assert.assertNotNull(createdDrive);
		    
		    createdFile = lyuFileSystem.create(LyuFile.FILE_TYPE.FILE, "testfile2.txt", "C:/tmp");
		    System.out.println("This is the tree:");
			lyuFileSystem.printOutTree(rootNode);
		    Assert.assertNotNull(createdFile);
		    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//@Test
	public void testMove() {
		try {
			LyuFile createdFolder = lyuFileSystem.create(LyuFile.FILE_TYPE.FOLDER, "testfolder", "/Users");
			Assert.assertNotNull(createdFolder);
			LyuFile createdFile = lyuFileSystem.create(LyuFile.FILE_TYPE.FILE, "testfile.txt", "/Users/testfolder");
			Assert.assertNotNull(createdFile);
			
			LyuFile createdDrive = lyuFileSystem.create(LyuFile.FILE_TYPE.FOLDER, "C:/", "C:/");	
			Assert.assertNotNull(createdDrive);
			LyuFile createdFolderInDrive = lyuFileSystem.create(LyuFile.FILE_TYPE.FOLDER, "tmp", "C:/tmp");	
			Assert.assertNotNull(createdFolderInDrive);
			createdFile = lyuFileSystem.create(LyuFile.FILE_TYPE.FILE, "testfile2.txt", "C:/tmp");
			Assert.assertNotNull(createdFile);
			
			LyuDirectoryNode driveRootNode = lyuFileSystem.getRootNode("C:/");
			LyuDirectoryNode rootNode = lyuFileSystem.getRootNode("/Users");
			System.out.println("This is the tree before moving:");
			lyuFileSystem.printOutTree(rootNode);
			lyuFileSystem.printOutTree(driveRootNode);
			boolean success = lyuFileSystem.move("C:/tmp/testfile2.txt", "/Users/testfolder");
			Assert.assertTrue(success);
			System.out.println("This is the tree after moving:");
			lyuFileSystem.printOutTree(rootNode);
			lyuFileSystem.printOutTree(driveRootNode);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//@Test
	public void testDelete() {
		try {			
			LyuFile createdDrive = lyuFileSystem.create(LyuFile.FILE_TYPE.FOLDER, "C:/", "C:/");		
		    lyuFileSystem.create(LyuFile.FILE_TYPE.FOLDER, "tmp", "C:/tmp");		
		    LyuDirectoryNode rootNode = lyuFileSystem.getRootNode("C:/");			
			System.out.println("This is the tree:");
			lyuFileSystem.printOutTree(rootNode);
		    Assert.assertNotNull(createdDrive);
		    
		    LyuFile createdFile = lyuFileSystem.create(LyuFile.FILE_TYPE.FILE, "testfile2.txt", "C:/tmp");
		    System.out.println("This is the tree:");
			lyuFileSystem.printOutTree(rootNode);
		    Assert.assertNotNull(createdFile);
			
			rootNode = lyuFileSystem.getRootNode("C:/");	
			System.out.println("This is the tree before deleteing:");
			lyuFileSystem.printOutTree(rootNode);
			LyuDirectoryNode deletedNode = lyuFileSystem.delete("C:/tmp/testfile2.txt");
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
			LyuFile createdDrive = lyuFileSystem.create(LyuFile.FILE_TYPE.FOLDER, "C:/", "C:/");		
		    lyuFileSystem.create(LyuFile.FILE_TYPE.FOLDER, "tmp", "C:/tmp");		
		    LyuDirectoryNode rootNode = lyuFileSystem.getRootNode("C:/");			
			System.out.println("This is the tree:");
			lyuFileSystem.printOutTree(rootNode);
		    Assert.assertNotNull(createdDrive);
		    
		    LyuFile createdFile = lyuFileSystem.create(LyuFile.FILE_TYPE.FILE, "testfile2.txt", "C:/tmp");
		    System.out.println("This is the tree:");
			lyuFileSystem.printOutTree(rootNode);
		    Assert.assertNotNull(createdFile);
			
			rootNode = lyuFileSystem.getRootNode("C:/");	
			System.out.println("This is the tree before writing:");
			lyuFileSystem.printOutTree(rootNode);
			LyuFile updatedFile = lyuFileSystem.write("C:/tmp/testfile2.txt", "Hello world!");
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
			LyuFile createdDrive = lyuFileSystem.create(LyuFile.FILE_TYPE.FOLDER, "C:/", "C:/");	
			Assert.assertNotNull(createdDrive);
		    lyuFileSystem.create(LyuFile.FILE_TYPE.FOLDER, "tmp", "C:/tmp");	
		    
		    LyuFile createdFile = lyuFileSystem.create(LyuFile.FILE_TYPE.FILE, "testfile1.txt", "C:/tmp");
		    Assert.assertNotNull(createdFile);
		    LyuFile updatedFile = lyuFileSystem.write("C:/tmp/testfile1.txt", "Hello world!");
			Assert.assertNotNull(updatedFile);
			updatedFile = lyuFileSystem.write("C:/tmp/testfile1.txt", "012345");
			Assert.assertNotNull(updatedFile);
		    
		    createdFile = lyuFileSystem.create(LyuFile.FILE_TYPE.FILE, "testfile2.txt", "C:/tmp");
		    Assert.assertNotNull(createdFile);
		   
		    updatedFile = lyuFileSystem.write("C:/tmp/testfile2.txt", "Hello world!");
			Assert.assertNotNull(updatedFile);
			updatedFile = lyuFileSystem.write("C:/tmp/testfile2.txt", "0123456789");
			Assert.assertNotNull(updatedFile);
			LyuDirectoryNode rootNode = lyuFileSystem.getRootNode("C:/");
			System.out.println("This is the tree after writing:");
			lyuFileSystem.printOutTree(rootNode);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testZip() {
		try {		
			LyuFile createdDrive = lyuFileSystem.create(LyuFile.FILE_TYPE.ZIPFILE, "C:/", "C:/");	
			Assert.assertNotNull(createdDrive);
		    lyuFileSystem.create(LyuFile.FILE_TYPE.FOLDER, "tmp", "C:/tmp");	
		    
		    LyuFile createdFile = lyuFileSystem.create(LyuFile.FILE_TYPE.FILE, "testfile1.txt", "C:/tmp");
		    Assert.assertNotNull(createdFile);
		    LyuFile updatedFile = lyuFileSystem.write("C:/tmp/testfile1.txt", "Hello world!");
			Assert.assertNotNull(updatedFile);
			updatedFile = lyuFileSystem.write("C:/tmp/testfile1.txt", "012345");
			Assert.assertNotNull(updatedFile);
		    
		    createdFile = lyuFileSystem.create(LyuFile.FILE_TYPE.FILE, "testfile2.txt", "C:/tmp");
		    Assert.assertNotNull(createdFile);
		   
		    updatedFile = lyuFileSystem.write("C:/tmp/testfile2.txt", "Hello world!");
			Assert.assertNotNull(updatedFile);
			updatedFile = lyuFileSystem.write("C:/tmp/testfile2.txt", "0123456789");
			Assert.assertNotNull(updatedFile);
			LyuDirectoryNode rootNode = lyuFileSystem.getRootNode("C:/");
			LyuDirectoryNode startNode = lyuFileSystem.findLocation("C:/tmp/testfile2.txt");
			lyuFileSystem.zipFile(startNode);
			System.out.println("This is the tree after writing:");
			lyuFileSystem.printOutTree(rootNode);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
