package com.lyu.filesystem;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class Test2 {
	private String userFileSeparator = "\\\\";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		String path = "C:\\testfolder";
		String[] pathAndName = path.split(userFileSeparator);
		
		//path = "C:/testfolder";
		//pathAndName = path.split(userFileSeparator);
		
		if(!path.contains(userFileSeparator)){
			assertTrue(true);
		}
	}

}
