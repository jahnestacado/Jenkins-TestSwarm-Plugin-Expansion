package com.aimms.jenkins.testswarmplugin.extension;

import hudson.FilePath.FileCallable;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class WriteToRemoteWorkspace implements FileCallable<Void> {
	private static final long serialVersionUID = 1L;
	private final static String ENCODING = "UTF-8";
	private final String logFilePath;
	private final List<String> reportPaths;

	public WriteToRemoteWorkspace(String logFilePath, List<String> reportPaths) {
		this.logFilePath = logFilePath;
		this.reportPaths = reportPaths;
	}

	@Override
	public Void invoke(File file, VirtualChannel channel) throws IOException,
			InterruptedException {

		PrintWriter writer = new PrintWriter(logFilePath, ENCODING);
		for (String reportPath : reportPaths) {
			writer.println(reportPath);
		}
		writer.close();

		return null;

	}

}
