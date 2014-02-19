package com.aimms.jenkins.testswarmplugin.extension;

public class Auxiliaries {
	
	public static String transformedRootDirPath(String rootPath){
		if(!rootPath.endsWith("/")) return rootPath+"/";
		return rootPath;
	}

}
