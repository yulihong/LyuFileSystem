package com.lyu.filesystem.entity;

import java.io.File;

import org.apache.commons.lang.StringUtils;

public class LyuFile {
	public enum FILE_TYPE {
		DRIVE, FOLDER, FILE, ZIPFILE
	};

	private String name;
	private String path;
	private int size;
	private String content;
	private FILE_TYPE fileType;

	private static String DEFAULT_NAME = "NewFile";

	public LyuFile(String name, String path, FILE_TYPE fileType) {
		this.fileType = fileType;

		if (StringUtils.isBlank(name)) {
			this.name = DEFAULT_NAME;
		} else {
			this.name = name;
		}

		if (StringUtils.isBlank(path)) {
			this.path = System.getProperty("user.dir");
		} else {
			this.path = path;
		}
	}

	public LyuFile(String path, FILE_TYPE fileType) {
		this.fileType = fileType;

		if (StringUtils.isBlank(path)) {
			setToDefaultName();
		} else {
			String[] pathAndName = path.split(File.separator);
			if (pathAndName.length > 1) {			
				this.name = pathAndName[pathAndName.length - 1];
			}
			else if(pathAndName.length == 1){
				this.name = pathAndName[0];
			}
		}
	}

	public String findNameInPath() {
		String[] pathAndName = path.split(File.separator);
		if (pathAndName.length < 2) {
			return path;
		} else {
			return pathAndName[pathAndName.length-1];
		}

	}

	private void setToDefaultName() {
		this.path = System.getProperty("user.dir");
		this.name = DEFAULT_NAME;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public FILE_TYPE getFileType() {
		return fileType;
	}

	public void setFileType(FILE_TYPE fileType) {
		this.fileType = fileType;
	}

}
