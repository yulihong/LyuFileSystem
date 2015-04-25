package com.lyu.filesystem;

import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.lyu.filesystem.entity.LyuFile;
import com.lyu.filesystem.impl.LyuDirectoryNode;
import com.lyu.filesystem.impl.LyuFileSystemImpl;

/**
 * 
 * @version 0.1
 * @since April 10, 2015 This is the test class which use Windows path separator
 *        '\' The test cases do not depend on each other. They are designed to
 *        be able to run in a arbitrary order.
 */
public class TestLyuFileSystem {
	private static ILyuFileSystem lyuFileSystem;

	/**
	 * Directory tree is created in setUpBeforeClass() and is used for all test
	 * cases
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		lyuFileSystem = LyuFileSystemImpl.SingleInstance.INSTANCE
				.getSingleton();
		LyuFile createdFolder = lyuFileSystem.create(LyuFile.FILE_TYPE.FOLDER,
				"testfolder", "C:\\");
		LyuFile createdFile = lyuFileSystem.create(LyuFile.FILE_TYPE.FILE,
				"testfile.txt", "C:\\testfolder");
		createdFile = lyuFileSystem.create(LyuFile.FILE_TYPE.FILE,
				"testfile3.txt", "C:\\testfolder");
		Assert.assertNotNull(createdFolder);
		Assert.assertNotNull(createdFile);

		LyuFile createdDrive = lyuFileSystem.create(LyuFile.FILE_TYPE.DRIVE,
				"E:\\", "E:\\");
		lyuFileSystem.create(LyuFile.FILE_TYPE.FOLDER, "tmp", "E:\\");
		Assert.assertNotNull(createdDrive);
		LyuFile createdFile1 = lyuFileSystem.create(LyuFile.FILE_TYPE.FILE,
				"testfile1.txt", "E:\\tmp");
		createdFile1 = lyuFileSystem.create(LyuFile.FILE_TYPE.FILE,
				"testfileMove.txt", "E:\\tmp");
		createdFile1 = lyuFileSystem.create(LyuFile.FILE_TYPE.FILE,
				"testfile3.txt", "E:\\tmp");
		createdFile1 = lyuFileSystem.create(LyuFile.FILE_TYPE.FILE,
				"testfileDel.txt", "E:\\tmp");
		Assert.assertNotNull(createdFile1);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		lyuFileSystem = null;
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testMoveHandleExceptionCase() {
		try {
			System.out
					.println("==========testMoveHandleExceptionCase() logs START===============");
			boolean success = lyuFileSystem.move("E:\\tmp\\testfile3.txt", "C:\\testfolder");
			// ASSERT FAIL!!!! because testfile3.txt has existed in C:\\testfolder
			Assert.assertFalse(success);
			System.out.println("This is the tree after moving:");
			LyuDirectoryNode rootNode = lyuFileSystem.findRootNode("C:\\");
			rootNode.printOutTree("");
			System.out
					.println("==========testMoveHandleExceptionCase() logs END===================");
			System.out.println(" ");

		} catch (FileNotFoundException | FileAlreadyExistsException
				| RuntimeException e) {
			System.out
					.println("!!!!!!!!!!!Here we expect the testfile3.txt is added back to src folder E:\\tmp !!!!!!!!!!");
			LyuDirectoryNode rootNode = lyuFileSystem.findRootNode("E:\\");
			rootNode.printOutTree("");
		}
	}

	@Test
	public void testMove() {
		try {
			System.out
					.println("==========testMove() logs START===============");
			System.out.println("This is the tree before moving:");
			LyuDirectoryNode eRootNode = lyuFileSystem.findRootNode("C:\\");
			eRootNode.printOutTree("");
			boolean success = lyuFileSystem.move("E:\\tmp\\testfileMove.txt",
					"C:\\testfolder");
			Assert.assertTrue(success);
			System.out.println("This is the tree after moving:");
			LyuDirectoryNode rootNode = lyuFileSystem.findRootNode("C:\\");
			rootNode.printOutTree("");
			System.out
					.println("==========testMove() logs END===================");
			System.out.println(" ");

		} catch (FileNotFoundException | FileAlreadyExistsException | IllegalArgumentException e) {
			System.out
					.println("!!!!!!!!!!!Here we expect the folder or file is added back to src folder !!!!!!!!!!");
			LyuDirectoryNode rootNode = lyuFileSystem.findRootNode("E:\\");
			rootNode.printOutTree("");
			e.printStackTrace();
		}
	}

	@Test
	public void testDelete() {
		try {
			System.out
					.println("==========testDelete() logs START===============");
			LyuDirectoryNode rootNode = lyuFileSystem.findRootNode("E:\\");
			System.out.println("This is the tree before deleteing:");
			rootNode.printOutTree("");
			LyuDirectoryNode deletedNode = lyuFileSystem
					.delete("E:\\tmp\\testfileDel.txt");
			Assert.assertNotNull(deletedNode);
			System.out.println("This is the tree after deleteing:");
			rootNode = lyuFileSystem.findRootNode("C:\\");
			rootNode.printOutTree("");
			System.out
					.println("==========testDelete() logs END===================");
			System.out.println(" ");
		} catch (FileNotFoundException | RuntimeException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testCreate() {
		try {
			System.out
					.println("==========testCreate() logs START===============");
			System.out.println("This is the tree before adding a file to tmp:");
			LyuDirectoryNode rootNode1 = lyuFileSystem.findRootNode("E:\\");
			rootNode1.printOutTree("");
			LyuFile createdFile1 = lyuFileSystem.create(LyuFile.FILE_TYPE.FILE,
					"testfile5.txt", "E:\\tmp");
			System.out.println("This is the tree after adding a file to tmp:");
			rootNode1 = lyuFileSystem.findRootNode("E:\\");
			rootNode1.printOutTree("");
			Assert.assertNotNull(createdFile1);
			System.out
					.println("==========testCreate() logs END===================");
			System.out.println(" ");
		} catch (FileAlreadyExistsException | FileNotFoundException
				| RuntimeException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testWrite() {
		try {
			System.out
					.println("==========testWrite() logs START===============");
			LyuDirectoryNode rootNode = lyuFileSystem.findRootNode("E:\\");
			System.out.println("This is the tree before writing:");
			rootNode.printOutTree("");
			LyuFile updatedFile = lyuFileSystem.write("E:\\tmp\\testfile1.txt",
					"Hello world!");
			Assert.assertNotNull(updatedFile);
			System.out.println("This is the tree after writing:");
			rootNode.printOutTree("");
			System.out
					.println("==========testWrite() logs END===================");
			System.out.println(" ");
		} catch (FileNotFoundException | RuntimeException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testZip() {
		try {
			System.out.println("==========testZip() logs START===============");
			LyuFile updatedFile = lyuFileSystem.write(
					"C:\\testfolder\\testfile.txt", "Hello world!");
			Assert.assertNotNull(updatedFile);
			updatedFile = lyuFileSystem.write("C:\\testfolder\\testfile.txt",
					"012345");
			Assert.assertNotNull(updatedFile);

			LyuDirectoryNode rootNode = lyuFileSystem.findRootNode("C:\\");
			System.out.println("This is the tree before zip:");
			rootNode.printOutTree("");

			lyuFileSystem.zip("C:\\", "testfolder");

			System.out.println("This is the tree after zip:");
			rootNode.printOutTree("");
			System.out
					.println("==========testZip() logs END===================");
			System.out.println(" ");
		} catch (FileNotFoundException | RuntimeException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testRead() {

		try {
			System.out
					.println("==========testRead() logs START===============");

			LyuFile updatedFile = lyuFileSystem.write("E:\\tmp\\testfile1.txt",
					"Hello world!");
			Assert.assertNotNull(updatedFile);
			updatedFile = lyuFileSystem.write("E:\\tmp\\testfile1.txt",
					"012345");
			Assert.assertNotNull(updatedFile);

			LyuDirectoryNode rootNode = lyuFileSystem.findRootNode("E:\\");
			String str = lyuFileSystem.read("E:\\tmp\\testfile1.txt");
			System.out.println("Content in E:\\tmp\\testfile1.txt is: " + str);
			rootNode.printOutTree("");
			System.out
					.println("==========testRead() logs END===================");
			System.out.println(" ");
		} catch (FileNotFoundException | RuntimeException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testCalcualteSize() {

		try {
			System.out
					.println("==========testCalcualteSize() logs START===============");
			LyuFile updatedFile = lyuFileSystem.write("E:\\tmp\\testfile1.txt",
					"Hello world!");
			Assert.assertNotNull(updatedFile);
			updatedFile = lyuFileSystem.write("E:\\tmp\\testfile1.txt",
					"0123456789");
			Assert.assertNotNull(updatedFile);
			LyuDirectoryNode rootNode = lyuFileSystem.findRootNode("E:\\");
			System.out.println("This is the tree after writing:");
			int sizeOfRoot = rootNode.calculateTreeNodeSize();
			System.out.println("The tree size is: " + sizeOfRoot);
			rootNode.printOutTree("");
			System.out
					.println("==========testCalcualteSize() logs END===================");
			System.out.println(" ");
		} catch (FileNotFoundException | RuntimeException e) {
			e.printStackTrace();
		}
	}
}
