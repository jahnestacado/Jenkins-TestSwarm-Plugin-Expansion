package com.aimms.jenkins.testswarmplugin.extension;

import com.javaclimber.jenkins.testswarmplugin.TestSwarmBuilder.TestSuiteData;

public class TestSuiteDataExpansion extends TestSuiteData {
	
	
	private static final long serialVersionUID = 1L;
	private final static boolean testCacheCracker = true;
	private final static boolean disableTest = false;

	public TestSuiteDataExpansion(String testName, String testUrl) {
		super(testName, testUrl, testCacheCracker, disableTest);
	}

}
