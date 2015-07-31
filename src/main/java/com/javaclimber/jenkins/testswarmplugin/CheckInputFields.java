package com.javaclimber.jenkins.testswarmplugin;

import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;

public class CheckInputFields {

	public static boolean check(TestSwarmBuilder testBuilder,
			@SuppressWarnings("rawtypes") AbstractBuild build, BuildListener listener) {

		if (testBuilder.getTestswarmServerUrl() == null
				|| testBuilder.getTestswarmServerUrl().length() == 0) {
			listener.error("TestSwarm Server Url is mandatory");
			build.setResult(Result.FAILURE);
			return false;
		}

		if (testBuilder.getJobName() == null
				|| testBuilder.getJobName().length() == 0) {
			listener.error("Jobname is mandatory");
			build.setResult(Result.FAILURE);
			return false;
		}

		if (testBuilder.getUserName() == null
				|| testBuilder.getUserName().length() == 0) {
			listener.error("Username is mandatory");
			build.setResult(Result.FAILURE);
			return false;
		}

		if (testBuilder.getAuthToken() == null
				|| testBuilder.getAuthToken().length() == 0) {
			listener.error("Auth Token is mandatory");
			build.setResult(Result.FAILURE);
			return false;
		}

		if (testBuilder.getMaxRuns() == null
				|| testBuilder.getMaxRuns().length() == 0) {
			listener.error("Maximum number of runs is mandatory");
			build.setResult(Result.FAILURE);
			return false;
		} else {
			// Check for integer value
			try {
				Integer.parseInt(testBuilder.getMaxRuns());
			} catch (Exception parseEx) {
				listener.error("Maximum number of runs is not an integer");
				build.setResult(Result.FAILURE);
				return false;
			}
		}

		if (testBuilder.getPollingIntervalInSecs() == null
				|| testBuilder.getPollingIntervalInSecs().length() == 0) {
			listener.error("Polling interval is mandatory");
			build.setResult(Result.FAILURE);
			return false;
		} else {
			// Check for integer value
			try {
				Integer.parseInt(testBuilder.getPollingIntervalInSecs());
			} catch (Exception parseEx) {
				listener.error("Polling interval is not an integer");
				build.setResult(Result.FAILURE);
				return false;
			}
		}

		if (testBuilder.getTimeOutPeriodInMins() == null
				|| testBuilder.getTimeOutPeriodInMins().length() == 0) {
			listener.error("Timeout Period is mandatory");
			build.setResult(Result.FAILURE);
			return false;
		} else {
			// Check for integer value
			try {
				Integer.parseInt(testBuilder.getTimeOutPeriodInMins());
			} catch (Exception parseEx) {
				listener.error("Timeout period is not an integer");
				build.setResult(Result.FAILURE);
				return false;
			}
		}

		if (!testBuilder.isValidUrl(testBuilder.getTestswarmServerUrl())) {
			listener.error("Testswarm Server Url is not a valid url ! check your TestSwarm Integration Plugin configuration");
			build.setResult(Result.FAILURE);
			return false;
		}

		return true;
	}
}
