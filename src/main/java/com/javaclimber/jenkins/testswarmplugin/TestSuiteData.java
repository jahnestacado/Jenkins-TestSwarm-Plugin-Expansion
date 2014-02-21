package com.javaclimber.jenkins.testswarmplugin;

import java.io.Serializable;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

@ExportedBean
public class TestSuiteData implements Serializable {

	private static final long serialVersionUID = 1L;

	@Exported
	public String testName;

	@Exported
	public String testUrl;

	@Exported
	public boolean testCacheCracker;

	@Exported
	public boolean disableTest;

	@DataBoundConstructor
	public TestSuiteData(String testName, String testUrl,
			boolean testCacheCracker, boolean disableTest) {
		this.testName = testName;
		this.testUrl = testUrl;
		this.testCacheCracker = testCacheCracker;
		this.disableTest = disableTest;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public void setTestUrl(String testUrl) {
		this.testUrl = testUrl;
	}

	public String getTestName() {
		return testName;
	}

	public String getTestUrl() {
		return testUrl;
	}

	public String toString() {
		return "==> " + testName + ", " + testUrl + ", " + testCacheCracker
				+ "sss<==";
	}

	public boolean isTestCacheCracker() {
		return testCacheCracker;
	}

	public boolean isDisableTest() {
		return disableTest;
	}

}
