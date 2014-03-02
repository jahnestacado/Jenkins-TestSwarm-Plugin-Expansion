package com.aimms.jenkins.testswarmplugin.extension;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RemoteData implements Serializable {

	private static final long serialVersionUID = 1L;
	private List<String> allSubDirPaths = new ArrayList<String>();
	private final String rootDir;
	private final String fqhn;

	public RemoteData(String rootDir, String fqhn) {
		this.rootDir = rootDir + "/";
		this.fqhn = fqhn;
	}

	public void addPath(String path) {
		allSubDirPaths.add(path);
	}

	public List<String> getAllSubDirPaths() {
		return allSubDirPaths;
	}

	public String getRootDirPath() {
		return rootDir;
	}

	public String getFQHN() {
		return fqhn;
	}

}