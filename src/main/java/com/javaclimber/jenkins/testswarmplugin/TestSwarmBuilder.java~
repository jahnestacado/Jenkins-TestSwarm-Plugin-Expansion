package com.javaclimber.jenkins.testswarmplugin;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import hudson.util.VariableResolver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.codehaus.jackson.map.ObjectMapper;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.tap4j.model.Comment;
import org.tap4j.model.Directive;
import org.tap4j.model.Plan;
import org.tap4j.model.TestResult;
import org.tap4j.model.TestSet;
import org.tap4j.producer.TapProducer;
import org.tap4j.util.DirectiveValues;
import org.tap4j.util.StatusValues;

import com.aimms.jenkins.testswarmplugin.extension.RemoteData;
import com.aimms.jenkins.testswarmplugin.extension.RemoteWorkspaceSubDirs;
import com.aimms.jenkins.testswarmplugin.extension.TestDirPathFiltering;
import com.aimms.jenkins.testswarmplugin.extension.TestSuiteDataExpansion;
import com.aimms.jenkins.testswarmplugin.extension.TestSuiteURLGenerator;
import com.aimms.jenkins.testswarmplugin.extension.WriteToRemoteWorkspace;

/**
 * This is plugin is responsible for integrating TestSwarm into jenkins. It will
 * take all test case urls and post it to TestSwarm server
 * 
 * @author kevinnilson
 * 
 */
public class TestSwarmBuilder extends Builder implements Serializable {
	private ObjectMapper mapper = new ObjectMapper();

	protected final String CHAR_ENCODING = "iso-8859-1";

	// client id
	private final String CLIENT_ID = "fromJenkins";

	// state
	private final String STATE = "addjob";

	// browsers type
	private String chooseBrowsers;

	// job name
	private String jobName;
	private String jobNameCopy;

	// user name
	private String userName;

	// password
	private String authToken;

	// max run
	private String maxRuns;

	// minimum passing
	private String minimumPassing;

	// test swarm server url
	private String testswarmServerUrl;

	/*
	 * How frequent this plugin will hit the testswarm job url to know about
	 * test suite results
	 */
	private String pollingIntervalInSecs;

	/*
	 * How long this plugin will wait to know about test suite results
	 */
	private String timeOutPeriodInMins;

	private TestSuiteData[] testSuiteList = new TestSuiteData[0];

	private TestSuiteData[] testSuiteListCopy;

	private String testswarmServerUrlCopy;

	// private TestTypeConfig testTypeConfig;

	private TestSwarmDecisionMaker resultsAnalyzer;

	
	private String testContainerDirs;
	private String baseURL;
	private String logFilePath;
	private String testFolderName;
	private boolean enableCacheCracker;

	public static final int UNKNOWN = 0;
	public static final int ALL_PASSING = 1;
	public static final int IN_PROGRESS_ENOUGH_PASSING_NO_ERRORS = 2;
	public static final int IN_PROGRESS_NOT_ENOUGH_PASSING_NO_ERRORS = 3;
	public static final int TIMEOUT_NOT_ENOUGH_PASSING_NO_ERRORS = 4;
	public static final int FAILURE_IN_PROGRESS = 5;
	public static final int FAILURE_DONE = 6;

	// Fields in config.jelly must match the parameter names in the
	// "DataBoundConstructor"
	@DataBoundConstructor
	public TestSwarmBuilder(String testswarmServerUrl, String jobName,
			String userName, String authToken, String maxRuns,
			String chooseBrowsers, String pollingIntervalInSecs,
			String timeOutPeriodInMins, String minimumPassing,
		//	List<TestSuiteData> testSuiteList,
			String testContainerDirs, String testFolderName, String baseURL,
			String logFilePath, boolean enableCacheCracker) {

		this.testswarmServerUrl = testswarmServerUrl;
		this.jobName = jobName;
		this.userName = userName;
		this.authToken = authToken;
		this.maxRuns = maxRuns;
		this.chooseBrowsers = chooseBrowsers;
		this.pollingIntervalInSecs = pollingIntervalInSecs;
		this.timeOutPeriodInMins = timeOutPeriodInMins;
		this.minimumPassing = minimumPassing;
	//	this.testSuiteList = testSuiteList
	//			.toArray(new TestSuiteData[testSuiteList.size()]);
		this.resultsAnalyzer = new TestSwarmDecisionMaker();

	
		this.testContainerDirs = testContainerDirs;
		this.baseURL = baseURL;
		this.logFilePath = logFilePath;
		this.testFolderName = testFolderName;
		this.enableCacheCracker = enableCacheCracker;

	}

//	@Exported
//	public TestSuiteData[] getTestSuiteList() {
//		return testSuiteList;
//	}

	/**
	 * We'll use this from the <tt>config.jelly</tt>.
	 */
	public String getTestswarmServerUrl() {
		return testswarmServerUrl;
	}

	public String getChooseBrowsers() {
		return chooseBrowsers;
	}

	public String getJobName() {
		return jobName;
	}

	public String getUserName() {
		return userName;
	}

	public String getAuthToken() {
		return authToken;
	}

	public String getMaxRuns() {
		return maxRuns;
	}

	public String getPollingIntervalInSecs() {
		return pollingIntervalInSecs;
	}

	public String getTimeOutPeriodInMins() {
		return timeOutPeriodInMins;
	}
	
	public String getTestFolderName(){
		return testFolderName;
	}
	
	public String getTestContainerDirs(){
		return testContainerDirs;
	}
	
	public String getBaseURL(){
		return baseURL;
	}
	
	
	public String getLogFilePath(){
		return logFilePath;
	}
	
	public boolean getEnableCacheCracker(){
		return enableCacheCracker;
	}

	/**
	 * Check if config file loc is a url
	 * 
	 * @return true if the configFileLoc is a valid url else return false
	 */
	public boolean isValidUrl(String urlStr) {

		try {
			URL url = new URL(urlStr);
			return url != null;
		} catch (Exception ex) {
			return false;
		}
	}

	@Override
	public DescriptorImpl getDescriptor() {
		return (DescriptorImpl) super.getDescriptor();
	}

	



	@Override
	public boolean perform(AbstractBuild build, Launcher launcher,
			BuildListener listener) throws InterruptedException, IOException {

		listener.getLogger().println("");
		listener.getLogger()
				.println("Launching TestSwarm Integration Suite...");

		FilePath remoteWorkspace = new FilePath(build.getWorkspace(), "");
		RemoteData workspaceData = remoteWorkspace.act(new RemoteWorkspaceSubDirs());
		
		List<String> allSubDirs = workspaceData.getAllSubDirPaths();
        String rootDir = workspaceData.getRootDirPath();
		TestDirPathFiltering testDirPaths = new TestDirPathFiltering(
				allSubDirs, rootDir, testFolderName, testContainerDirs);
		
		List<String> testDirLocalPaths = testDirPaths.getFilteredPaths();
		
		// Bind baseURL with test suites List<String> digestibleURLs =
		List<String> testSuitesURLs = TestSuiteURLGenerator.getURLs(baseURL,
				testDirLocalPaths);
		
		listener.getLogger().println("");
		

		List<TestSuiteData> testSuiteDynamicList = new ArrayList<TestSuiteData>();

		for (String url : testSuitesURLs) {
			String name = url.replace(baseURL, "").replace(
					"/" + testFolderName, ""); //
			testSuiteDynamicList.add(new TestSuiteDataExpansion(name,url,enableCacheCracker));		
		}
		
		
		//Save included test suites local paths in specified logFilePath
		remoteWorkspace.act(new WriteToRemoteWorkspace(logFilePath,testDirLocalPaths));
		listener.getLogger().println("***************   Created file that includes local test suite paths that will run at "+logFilePath+"   ***************");
		listener.getLogger().println("");

		
		for(TestSuiteData u : testSuiteDynamicList){
			
			listener.getLogger().println(workspaceData.getFQHN());

			listener.getLogger().println(u.getTestName());
			listener.getLogger().println(u.getTestUrl());
			listener.getLogger().println("");

		}

	
		testSuiteList = testSuiteDynamicList.toArray(new TestSuiteData[testSuiteDynamicList.size()]);
		testswarmServerUrlCopy = new String(testswarmServerUrl);

		testSuiteListCopy = new TestSuiteData[testSuiteList.length];
		TestSuiteData copyData;
		TestSuiteData origData;
		for (int i = 0; i < testSuiteList.length; i++) {
			origData = (TestSuiteData) testSuiteList[i];
			copyData = new TestSuiteData(origData.testName, origData.testUrl,
					origData.testCacheCracker, origData.disableTest);

			if (origData.disableTest)
				listener.getLogger().println(
						"Test is disabled for : " + origData.testName);

			testSuiteListCopy[i] = copyData;

		}

		// resolve environmental variables
		expandRuntimeVariables(listener, build);

		// check all required parameters are entered
		CheckInputFields.check(this, build, listener);
	

		try {

			String data = "authUsername="
					+ URLEncoder.encode(userName, "UTF-8") + "&authToken="
					+ URLEncoder.encode(authToken, "UTF-8") + "&jobName="
					+ jobName + "&runMax=" + maxRuns + "&browserSets[]="
					+ chooseBrowsers + buildTestSuitesQueryString();

			System.out.println(data);

			URL url = new URL(testswarmServerUrl + "/api.php?action=addjob");
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(
					conn.getOutputStream());
			wr.write(data);
			wr.flush();

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line;
			String result = "";
			while ((line = rd.readLine()) != null) {
				listener.getLogger().println(line);
				result += line;
			}
			wr.close();
			rd.close();

			
			if (result == null || "".equals(result)) {
				listener.error("no result from job submission");
				build.setResult(Result.FAILURE);
				return false;
			}

			int jobId = -1;
			ObjectMapper mapper = new ObjectMapper(); // can reuse, share
														// globally
			Map<String, Object> resultMap = mapper.readValue(result, Map.class);

			Map<String, Object> addJobResult = (Map) resultMap.get("addjob");
			jobId = (Integer) addJobResult.get("id");

			String jobUrl = this.testswarmServerUrlCopy
					+ "/api.php?format=json&action=job&item=" + jobId;
			listener.getLogger()
					.println(
							"**************************************************************");
			listener.getLogger().println(
					"Your request is successfully posted to TestSwarm Server and "
							+ " you can view the result in the following URL");
			listener.getLogger().println(
					testswarmServerUrlCopy + "/job/" + jobId);

			listener.getLogger().println();

			listener.getLogger().println(jobUrl);
			listener.getLogger()
					.println(
							"**************************************************************");
			listener.getLogger().println("");
			listener.getLogger().println("Analyzing Test Suite Result....");
			int jobStatus = analyzeTestSuiteResults(jobUrl, build, listener);

			boolean jobResult = (jobStatus == ALL_PASSING || jobStatus == IN_PROGRESS_ENOUGH_PASSING_NO_ERRORS);

			if (jobResult) {
				build.setResult(Result.SUCCESS);
			} else {
				build.setResult(Result.FAILURE);
			}

			listener.getLogger().println(
					"Analyzing Test Suite Result COMPLETED...");

			if (jobStatus == ALL_PASSING)
				listener.getLogger().println("ALL PASSING");
			else if (jobStatus == IN_PROGRESS_ENOUGH_PASSING_NO_ERRORS)
				listener.getLogger()
						.println("ALL PASSING - SOME STILL RUNNING");
			else if (jobStatus == IN_PROGRESS_NOT_ENOUGH_PASSING_NO_ERRORS)
				listener.getLogger().println("FAILURE - NOT ENOUGH FINISHED");
			else if (jobStatus == FAILURE_DONE
					|| jobStatus == FAILURE_IN_PROGRESS)
				listener.getLogger().println("FAILURE");

			produceTAPReport(jobUrl, build, Integer.parseInt(minimumPassing),
					testswarmServerUrlCopy + "/job/" + jobId);
			return jobResult;

		} catch (Exception ex) {
			ex.printStackTrace();
			listener.error(ex.toString());
			build.setResult(Result.FAILURE);
			return false;

		}

	}

	// TODO add skipped
	@SuppressWarnings("unchecked")
	private void produceTAPReport(String jobUrl, AbstractBuild build,
			int minimumPassing, String jobFriendlyUrl) {
		try {

			List<TestSuiteData> disabledTests = new ArrayList<TestSuiteData>();
			for (int i = 0; i < testSuiteListCopy.length; i++) {
				if (testSuiteListCopy[i].isDisableTest()) {
					disabledTests.add(testSuiteListCopy[i]);
				}
			}

			TapProducer tapProducer = new TapProducer();// TapProducerFactory.makeTap13YamlProducer();

			TestSet testSet = new TestSet();
			testSet.addComment(new Comment(jobFriendlyUrl));

			String json;

			json = this.resultsAnalyzer.grabPage(jobUrl);

			ObjectMapper mapper = new ObjectMapper(); // can reuse, share
			// globally

			Map<String, Object> resultMap = mapper.readValue(json, Map.class);

			Map<String, Object> job = (Map<String, Object>) resultMap
					.get("job");
			List<Map<String, Object>> runs = (ArrayList<Map<String, Object>>) job
					.get("runs");
			testSet.setPlan(new Plan(runs.size() + disabledTests.size()));
			int i = 1;
			for (Map<String, Object> run : runs) {
				Map<String, Integer> resultCount = new HashMap<String, Integer>();
				Map<String, List<String>> resultBrowsers = new HashMap<String, List<String>>();
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

					List<String> browserList = resultBrowsers.get(runStatus);
					if (browserList == null)
						browserList = new ArrayList<String>();
					browserList.add(ua);
					resultBrowsers.put(runStatus, browserList);
				}
				resultCount.remove("new");
				resultBrowsers.remove("new");

				TestResult testResult = null;
				if (resultCount.get("failed") == null)
					if (resultCount.get("passed") != null
							&& resultCount.get("passed") >= minimumPassing)
						testResult = new TestResult(StatusValues.OK, i);
					else {
						testResult = new TestResult(StatusValues.NOT_OK, i);
						testResult.addComment(new Comment("passing: "
								+ resultCount.get("passed") + " < "
								+ minimumPassing));
					}
				else {
					// failure
					testResult = new TestResult(StatusValues.NOT_OK, i);
					testResult.addComment(new Comment("failing - "
							+ resultBrowsers.get("failed")));
					testResult.addComment(new Comment("passed - "
							+ resultBrowsers.get("passed")));
					
				}

				testResult.setDescription((String) ((Map) run.get("info"))
						.get("name"));
				testResult.addComment(new Comment((String) ((Map) run
						.get("info")).get("url")));
				testSet.addTestResult(testResult);
				i++;
			}

			for (TestSuiteData diabledTest : disabledTests) {
				TestResult testResult = new TestResult(StatusValues.OK, i);

				testResult.setDescription(diabledTest.testName);

				testResult.setDirective(new Directive(DirectiveValues.SKIP,
						"test disabled"));
				testSet.addTestResult(testResult);
				i++;
			}

			String tapStream = tapProducer.dump(testSet);
			System.out.println(tapStream);

		
			File f = new File(build.getProject().getRootDir().getAbsolutePath()
					+ File.separatorChar + "workspace", "testswarm.tap");

			System.out.println("Writing TAP results to " + f.getAbsolutePath());
			PrintWriter out = new PrintWriter(f);
			out.write(tapStream);
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void expandRuntimeVariables(BuildListener listener,
			AbstractBuild build) throws IOException, InterruptedException {
		VariableResolver<String> varResolver = build.getBuildVariableResolver();
		EnvVars env = build.getEnvironment(listener);
		this.jobNameCopy = Util.replaceMacro(this.getJobName(), varResolver);
		this.jobNameCopy = Util.replaceMacro(this.jobNameCopy, env);
		this.testswarmServerUrlCopy = Util.replaceMacro(
				this.getTestswarmServerUrl(), varResolver);
		this.testswarmServerUrlCopy = Util.replaceMacro(
				this.testswarmServerUrlCopy, env);

		for (int i = testSuiteListCopy.length - 1; i >= 0; i--) {
			// Ignore testcase if disabled
			if (!testSuiteListCopy[i].isDisableTest()) {
				testSuiteListCopy[i].setTestName(Util.replaceMacro(
						testSuiteListCopy[i].getTestName(), varResolver));
				testSuiteListCopy[i].setTestName(Util.replaceMacro(
						testSuiteListCopy[i].getTestName(), env));
				testSuiteListCopy[i].setTestUrl(Util.replaceMacro(
						testSuiteListCopy[i].getTestUrl(), varResolver));
				testSuiteListCopy[i].setTestUrl(Util.replaceMacro(
						testSuiteListCopy[i].getTestUrl(), env));
			}
		}
	}


	private String buildTestSuitesQueryString() throws Exception {
		StringBuffer requestStr = new StringBuffer();

		for (int i = 0; i < testSuiteListCopy.length; i++) {
			// Ignore testcase if disbled
			if (!testSuiteListCopy[i].isDisableTest()) {
				encodeAndAppendTestSuiteUrl(requestStr,
						testSuiteListCopy[i].getTestName(),
						testSuiteListCopy[i].getTestUrl(),
						testSuiteListCopy[i].isTestCacheCracker());
			}
		}

		return requestStr.toString();
	}

	private void encodeAndAppendTestSuiteUrl(StringBuffer requestStr,
			String testName, String testSuiteUrl, boolean cacheCrackerEnabled)
			throws Exception {

		requestStr.append("&")
				.append(URLEncoder.encode("runNames[]", CHAR_ENCODING))
				.append("=").append(URLEncoder.encode(testName, CHAR_ENCODING))
				.append("&")
				.append(URLEncoder.encode("runUrls[]", CHAR_ENCODING))
				.append("=");
		requestStr.append(URLEncoder.encode(testSuiteUrl, CHAR_ENCODING));
		if (cacheCrackerEnabled) {
			if (testSuiteUrl.contains("?"))
				requestStr.append(URLEncoder.encode("&", CHAR_ENCODING));
			else
				requestStr.append(URLEncoder.encode("?", CHAR_ENCODING));

			requestStr
					.append(URLEncoder.encode(
							"cache_killer=" + System.currentTimeMillis(),
							CHAR_ENCODING));
		}
	}

	@SuppressWarnings("unchecked")
	private int analyzeTestSuiteResults(String jobUrl, AbstractBuild build,
			BuildListener listener) throws Exception {

		long secondsBetweenResultPolls = Long
				.parseLong(getPollingIntervalInSecs());
		long minutesTimeOut = Long.parseLong(getTimeOutPeriodInMins());

		long start = System.currentTimeMillis();
		// give testswarm 15 seconds to finish earlier activities
		try {
			Thread.sleep(15 * 1000);
		} catch (InterruptedException ex) {
			// ignore
		}
		String json;
		int jobStatus = UNKNOWN;
		while (start + (minutesTimeOut * 60000) > System.currentTimeMillis()
				&& jobStatus != ALL_PASSING && jobStatus != FAILURE_DONE) {
			json = this.resultsAnalyzer.grabPage(jobUrl);

			System.out.println(json);

			ObjectMapper mapper = new ObjectMapper(); // can reuse, share
			// globally

			Map<String, Object> resultMap = mapper.readValue(json, Map.class);

			jobStatus = resultsAnalyzer.jobStatus(resultMap,
					Integer.parseInt(minimumPassing), listener);
			if (jobStatus != ALL_PASSING && jobStatus != FAILURE_DONE) {

				listener.getLogger().println(
						"Sleeping for " + secondsBetweenResultPolls
								+ " seconds...");
				listener.getLogger().println();
				Thread.sleep(secondsBetweenResultPolls * 1000);
				listener.getLogger().println();
			}
		}

		return jobStatus;

	}

	/**
	 * Descriptor for {@link HelloWorldBuilder}. Used as a singleton. The class
	 * is marked as public so that it can be accessed from views.
	 * 
	 * <p>
	 * See <tt>views/hudson/plugins/hello_world/HelloWorldBuilder/*.jelly</tt>
	 * for the actual HTML fragment for the configuration screen.
	 */
	@Extension
	// this marker indicates Hudson that this is an implementation of an
	// extension point.
	public static final class DescriptorImpl extends
			BuildStepDescriptor<Builder> {
		/**
		 * To persist global configuration information, simply store it in a
		 * field and call save().
		 * 
		 * <p>
		 * If you don't want fields to be persisted, use <tt>transient</tt>.
		 */
		private boolean useFrench;

		/**
		 * Performs on-the-fly validation of the form field 'name'.
		 * 
		 * @param value
		 *            This parameter receives the value that the user has typed.
		 * @return Indicates the outcome of the validation. This is sent to the
		 *         browser.
		 */
		public FormValidation doCheckName(@QueryParameter String value)
				throws IOException, ServletException {
			if (value.length() == 0)
				return FormValidation.error("Please set a name");
			if (value.length() < 4)
				return FormValidation.warning("Isn't the name too short?");
			return FormValidation.ok();
		}

		public boolean isApplicable(Class<? extends AbstractProject> aClass) {
			// indicates that this builder can be used with all kinds of project
			// types
			return true;
		}

		/**
		 * This human readable name is used in the configuration screen.
		 */
		public String getDisplayName() {
			return "TestSwarm Integration Test - AIMMS Version";
		}

		@Override
		public boolean configure(StaplerRequest req, JSONObject formData)
				throws FormException {
			// To persist global configuration information,
			// set that to properties and call save().
			useFrench = formData.getBoolean("useFrench");

			// ^Can also use req.bindJSON(this, formData);
			// (easier when there are many fields; need set* methods for this,
			// like setUseFrench)
			save();
			return super.configure(req, formData);
		}

		@Override
		public Builder newInstance(StaplerRequest staplerRequest,
				JSONObject jsonObject) throws FormException {
			return super.newInstance(staplerRequest, jsonObject);
		}

		public DescriptorImpl() {
			super(TestSwarmBuilder.class);
			load();
		}

		/**
		 * This method returns true if the global configuration says we should
		 * speak French.
		 */
		public boolean useFrench() {
			return useFrench;
		}

	}



}