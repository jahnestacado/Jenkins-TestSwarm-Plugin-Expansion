package com.aimms.jenkins.testswarmplugin.extension;

import hudson.FilePath.FileCallable;
import hudson.remoting.VirtualChannel;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RetrieveRemoteWorkspaceSubDirs implements FileCallable<RemoteData> {
	private static final long serialVersionUID = 1L;
	private RemoteData rData;

	@Override
	public RemoteData invoke(File file, VirtualChannel channel)
			throws IOException, InterruptedException {

		String rootDir = file.getAbsolutePath();
		rData = new RemoteData(rootDir);
		List<String> subDirs = getSubDirNames(rootDir);

		List<String> topLevelDirPaths = new ArrayList<String>();
		for (int i = 0; i <= subDirs.size() - 1; i++) {
			topLevelDirPaths.add(rootDir + "/" + subDirs.get(i));
		}

		retrieveAllSubDirPaths(topLevelDirPaths);
		return rData;

	}

	private void retrieveAllSubDirPaths(List<String> topLevelDirPaths) {
		for (String topDirPath : topLevelDirPaths) {
			retrieveSubDirPaths(topDirPath);
		}
	}

	private void retrieveSubDirPaths(String parentDir) {
		List<String> sDirs = getSubDirNames(parentDir);
		if (!sDirs.isEmpty()) {
			for (String sDir : sDirs) {
				retrieveSubDirPaths(parentDir + "/" + sDir);
			}

		} else {
			rData.addPath(parentDir);
		}
	}

	public List<String> getSubDirNames(String parentDir) {
		File file = new File(parentDir);
		String[] directories = file.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});

		return stringArrayToList(directories);
	}

	private List<String> stringArrayToList(String[] array) {
		List<String> list = new ArrayList<String>();
		for (String s : array) {
			if (checkIfDirMustBeIncluded(s)) {
				list.add(s);
			}
		}
		return list;

	}

	private boolean checkIfDirMustBeIncluded(String dirName) {
		if (dirName.startsWith("."))
			return false;
		return true;

	}
}
