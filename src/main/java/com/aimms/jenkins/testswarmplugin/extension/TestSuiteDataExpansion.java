package com.aimms.jenkins.testswarmplugin.extension;

import com.javaclimber.jenkins.testswarmplugin.TestSwarmBuilder.TestSuiteData;

public class TestSuiteDataExpansion extends TestSuiteData {
	
	
	private static final long serialVersionUID = 1L;
	private final static boolean DISABLE_TESTS = false;

	public TestSuiteDataExpansion(String testName, String testUrl, boolean testCacheCracker) {
		super(testName, testUrl, testCacheCracker, DISABLE_TESTS);
	}

}
