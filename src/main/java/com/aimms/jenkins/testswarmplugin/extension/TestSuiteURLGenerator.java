package com.aimms.jenkins.testswarmplugin.extension;

import java.util.ArrayList;
import java.util.List;

public class TestSuiteURLGenerator {

	public static List<String> getURLs(String baseURL, List<String> testPaths){
		List<String> digestibleURLs = new ArrayList<String>();
		for(String path : testPaths){
			digestibleURLs.add(baseURL+path);
		}
		return digestibleURLs;
		
	}
}
