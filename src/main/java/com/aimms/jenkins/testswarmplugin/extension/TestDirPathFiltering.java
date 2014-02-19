package com.aimms.jenkins.testswarmplugin.extension;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class TestDirPathFiltering {
	private List<String> paths = new ArrayList<String>();
	private final String testDirSuffix;
	private final String sourceDir;

	public TestDirPathFiltering(List<String> subDirPaths, String sourceDir, String testFolderName) {
		this.sourceDir = sourceDir;
		testDirSuffix = "/"+testFolderName;
		for (String subDirPath : subDirPaths) {
			filter(subDirPath);
		}
	}

	private void filter(String path) {

			if (path.endsWith(testDirSuffix)) {
			path = path.replace(sourceDir, "");
			paths.add(path);
		} else if (path.contains(testDirSuffix + "/")) {
			paths.add(getProperPath(path));
		}
	}

	private String getProperPath(String path) {
		// EOP = End Of Path
		int EOP = path.indexOf(testDirSuffix + "/") + testDirSuffix.length();
		path = path.substring(0, EOP).replace(sourceDir, "");
		return path;
	}

	public List<String> getFilteredPaths() {
		//Remove possible duplicate path-elements
		Set<String> pathSet = new LinkedHashSet<String>(paths);
		List<String> filteredPaths = new ArrayList<String>();
		filteredPaths.addAll(pathSet);
		return filteredPaths;
	}

}
