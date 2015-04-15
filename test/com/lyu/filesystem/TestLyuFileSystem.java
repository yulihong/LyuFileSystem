package com.lyu.filesystem;

import java.io.IOException;

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
		lyuFileSystem = LyuFileSystemImpl.SingleInstance.INSTANCE.getSingleton();
		LyuFile createdFolder = lyuFileSystem.create(LyuFile.FILE_TYPE.FOLDER, "testfolder", "C:\\");	    
	    LyuFile createdFile = lyuFileSystem.create(LyuFile.FILE_TYPE.FILE, "testfile.txt", "C:\\testfolder");
	    Assert.assertNotNull(createdFolder);
	    Assert.assertNotNull(createdFile);
	    
	    LyuFile createdDrive = lyuFileSystem.create(LyuFile.FILE_TYPE.DRIVE, "E:\\", "E:\\");		
	    lyuFileSystem.create(LyuFile.FILE_TYPE.FOLDER, "tmp", "E:\\");		
	    Assert.assertNotNull(createdDrive);
	    
	    LyuFile createdFile1 = lyuFileSystem.create(LyuFile.FILE_TYPE.FILE, "testfile2.txt", "E:\\tmp");
	    Assert.assertNotNull(createdFile1);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		lyuFileSystem = null;
	}
	
	@Before
	public void setUp() throws Exception {
		//lyuFileSystem = LyuFileSystemImpl.SingleInstance.INSTANCE.getSingleton();
	}

	@After
	public void tearDown() throws Exception {
		//TestDeleteTree();
		//lyuFileSystem = null;
	}

	//@Test
	public void testCreate() {
		try {
			LyuFile createdFolder = lyuFileSystem.create(LyuFile.FILE_TYPE.FOLDER, "testfolder", "C:\\");	    
		    LyuFile createdFile = lyuFileSystem.create(LyuFile.FILE_TYPE.FILE, "testfile.txt", "C:\\testfolder");
		    Assert.assertNotNull(createdFolder);
		    Assert.assertNotNull(createdFile);
		    System.out.println("This is the tree:");
		    LyuDirectoryNode rootNode = lyuFileSystem.findRootNode("C:\\");
		    rootNode.printOutTree("");	    
		    
		    LyuFile createdDrive = lyuFileSystem.create(LyuFile.FILE_TYPE.DRIVE, "E:\\", "E:\\");		
		    lyuFileSystem.create(LyuFile.FILE_TYPE.FOLDER, "tmp", "E:\\");		
		    LyuDirectoryNode rootNode1 = lyuFileSystem.findRootNode("E:\\");			
			System.out.println("This is the tree after adding a folder tmp:");
			rootNode1.printOutTree("");
		    Assert.assertNotNull(createdDrive);
		    
		    LyuFile createdFile1 = lyuFileSystem.create(LyuFile.FILE_TYPE.FILE, "testfile2.txt", "E:\\tmp");
		    System.out.println("This is the tree after adding a file to tmp:");
		    rootNode1.printOutTree("");
		    Assert.assertNotNull(createdFile1);
		    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//@Test
	public void testMove() {
		try {
			System.out.println("This is the tree before moving:");
			LyuDirectoryNode eRootNode = lyuFileSystem.findRootNode("C:\\");
			eRootNode.printOutTree("");
			boolean success = lyuFileSystem.move("E:\\tmp\\testfile2.txt", "C:\\testfolder");
			Assert.assertTrue(success);
			System.out.println("This is the tree after moving:");
			LyuDirectoryNode rootNode = lyuFileSystem.findRootNode("C:\\");
			rootNode.printOutTree("");
			//driveRootNode.printOutTree("");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testDelete() {
		try {						
			LyuDirectoryNode rootNode = lyuFileSystem.findRootNode("E:\\");	
			System.out.println("This is the tree before deleteing:");
			rootNode.printOutTree("");
			LyuDirectoryNode deletedNode = lyuFileSystem.delete("E:\\tmp\\testfile2.txt");
			Assert.assertNotNull(deletedNode);
			System.out.println("This is the tree after deleteing:");
			rootNode.printOutTree("");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testWrite() {
		try {			
			LyuDirectoryNode rootNode = lyuFileSystem.findRootNode("E:\\");	
			System.out.println("This is the tree before writing:");
			rootNode.printOutTree("");
			LyuFile updatedFile = lyuFileSystem.write("E:\\tmp\\testfile2.txt", "Hello world!");
			Assert.assertNotNull(updatedFile);
			System.out.println("This is the tree after writing:");
			rootNode.printOutTree("");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testSize() {
		try {			    	    		   
		    LyuFile updatedFile = lyuFileSystem.write("E:\\tmp\\testfile2.txt", "Hello world!");
			Assert.assertNotNull(updatedFile);
			updatedFile = lyuFileSystem.write("E:\\tmp\\testfile2.txt", "0123456789");
			Assert.assertNotNull(updatedFile);
			LyuDirectoryNode rootNode = lyuFileSystem.findRootNode("E:\\");
			System.out.println("This is the tree after writing:");
			rootNode.printOutTree("");
			
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
			LyuDirectoryNode rootNode = lyuFileSystem.findRootNode("E:\\");
			//LyuDirectoryNode startNode = lyuFileSystem.findLocation("E:\\tmp\\testfile2.txt");
			//startNode.getNodeData().setFileType(LyuFile.FILE_TYPE.ZIPFILE);
			//startNode.zipFile();
			//startNode.updateSizeAfterChange(startNode.getNodeData().getSize()*(-1));
			System.out.println("This is the tree after writing:");
			rootNode.printOutTree("");
			
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
			LyuDirectoryNode rootNode = lyuFileSystem.findRootNode("E:\\");
			//LyuDirectoryNode startNode = lyuFileSystem.findLocation("E:\\tmp\\testfile2.txt");
			String str = lyuFileSystem.read("E:\\tmp\\testfile2.txt");
			System.out.println("Content in E:\\tmp\\testfile2.txt is: " + str);
			rootNode.printOutTree("");
			
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
		    
		    LyuDirectoryNode rootNode = lyuFileSystem.findRootNode("C:\\");
		    LyuDirectoryNode rootNode1 = lyuFileSystem.findRootNode("E:\\");
		    rootNode.printOutTree("");	 
		    
		    rootNode.clearTree();
		    rootNode.clearTree();
			System.out.println("After delete Tree:");
			rootNode.printOutTree("");
			rootNode1.clearTree();
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
			LyuDirectoryNode rootNode = lyuFileSystem.findRootNode("E:\\");
			System.out.println("This is the tree after writing:");
			int sizeOfRoot = rootNode.calculateTreeNodeSize();
			System.out.println("The tree size is: " + sizeOfRoot);
			rootNode.printOutTree("");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
