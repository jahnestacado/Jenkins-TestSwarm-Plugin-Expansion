package com.javaclimber.jenkins.testswarmplugin;

import hudson.model.BuildListener;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestSwarmDecisionMaker {

	@SuppressWarnings("deprecation")
	public String grabPage(String url) throws IOException {

		URL u;
		InputStream is = null;
		DataInputStream dis = null;
		String s;
		StringBuffer result = new StringBuffer();

		try {

			u = new URL(url);
			is = u.openStream();
			dis = new DataInputStream(new BufferedInputStream(is));

			while ((s = dis.readLine()) != null) {
				result.append(s).append("\n");
			}
		} finally {
			if (dis != null)
				dis.close();
			if (is != null)
				is.close();
		}
		return result.toString();
	}

	@SuppressWarnings("unchecked")
	public int jobStatus(Map<String, Object> resultMap, int minimumPassing,
			BuildListener listener) {

		Map<String, Object> job = (Map<String, Object>) resultMap.get("job");
		List<Map<String, Object>> runs = (ArrayList<Map<String, Object>>) job
				.get("runs");

		int allRunStatus = 0;
		for (Map<String, Object> run : runs) {

			listener.getLogger().println(
					((Map<String, Object>) run.get("info")).get("name"));

			Map<String, Integer> resultCount = new HashMap<String, Integer>();
			Map<String, Object> uaRuns = (Map<String, Object>) run
					.get("uaRuns");
			for (String ua : uaRuns.keySet()) {
				Map<String, Object> uaRun = (Map<String, Object>) uaRuns
						.get(ua);
				String runStatus = (String) uaRun.get("runStatus");
				Integer resultTypeCount = resultCount.get(runStatus);
				if (resultTypeCount == null) {
					resultTypeCount = new Integer(0);
				}
				resultTypeCount++;
				resultCount.put(runStatus, resultTypeCount);
			}
			resultCount.remove("new");
			listener.getLogger().println(resultCount);

			int runStatus = checkRunStatus(resultCount, minimumPassing,
					listener);
			if (runStatus > allRunStatus
					|| runStatus == TestSwarmBuilder.FAILURE_IN_PROGRESS)
				allRunStatus = runStatus;

		}

		return allRunStatus;

	}

	private int checkRunStatus(Map<String, Integer> runResult,
			int minimumPassing, BuildListener listener) {

		if (runResult.keySet().size() == 0) {
			listener.getLogger().println("NO RESULTS FOUND");
			// buildSuccessful = false;
			return TestSwarmBuilder.IN_PROGRESS_NOT_ENOUGH_PASSING_NO_ERRORS;
		}

		//Integer notstarted = runResult.get("new");
		Integer pass = runResult.get("passed");
		Integer progress = runResult.get("progress");
		Integer error = runResult.get("error");
		Integer fail = runResult.get("failed");
		Integer timeout = runResult.get("timeout");

		if (error != null && error.intValue() > 0) {
			listener.getLogger().println(
					error.intValue() + " test suites ends with ERROR");

			int passCount = 0;
			if (pass != null)
				passCount = pass.intValue();

			// TODO should consider fail
			if ((error.intValue() + passCount) < minimumPassing)
				return TestSwarmBuilder.FAILURE_IN_PROGRESS;
			else
				// TODO need to check all tests to determine if done
				// return TestSwarmBuilder.FAILURE_DONE;
				return TestSwarmBuilder.FAILURE_IN_PROGRESS;

		}

		if (fail != null && fail.intValue() > 0) {
			listener.getLogger().println(
					fail.intValue() + " test suites ends with FAILURE");

			int passCount = 0;
			if (pass != null)
				passCount = pass.intValue();

			// TODO should consider errors
			if ((fail.intValue() + passCount) < minimumPassing)
				return TestSwarmBuilder.FAILURE_IN_PROGRESS;
			else
				// TODO need to check all tests to determine if done
				// return TestSwarmBuilder.FAILURE_DONE;
				return TestSwarmBuilder.FAILURE_IN_PROGRESS;
		}

		if (timeout != null && timeout.intValue() > 0) {
			listener.getLogger().println(
					timeout.intValue() + " test suites ends with TIMED OUT");
			// I think we can ignore this, but in some cases this could mean
			// failure
		}

		if (pass == null || pass.intValue() < minimumPassing) {
			listener.getLogger().println(
					"not enough passing: " + pass + " < " + minimumPassing);
			return TestSwarmBuilder.IN_PROGRESS_NOT_ENOUGH_PASSING_NO_ERRORS;
		}

		if (progress != null && progress.intValue() > 0) {
			listener.getLogger().println("progress " + progress);
			return TestSwarmBuilder.IN_PROGRESS_ENOUGH_PASSING_NO_ERRORS;
		}

		return TestSwarmBuilder.ALL_PASSING;

	}

}
