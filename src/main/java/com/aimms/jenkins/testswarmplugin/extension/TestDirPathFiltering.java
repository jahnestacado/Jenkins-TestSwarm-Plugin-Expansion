package com.aimms.jenkins.testswarmplugin.extension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class TestDirPathFiltering {
	private List<String> paths = new ArrayList<String>();
	private final String testDirSuffix;
	private final String sourceDir;
	 private final List<String> includeDirList ;
	 private final static String MODULE_DIVIDER_SYMBOL=",";

	public TestDirPathFiltering(List<String> subDirPaths, String sourceDir, String testFolderName,String  includedDirsInStrformat) {
		this.sourceDir = sourceDir;
		includeDirList = Arrays.asList(includedDirsInStrformat.split(MODULE_DIVIDER_SYMBOL));
		testDirSuffix = "/"+testFolderName;
		for (String subDirPath : subDirPaths) {
			filter(subDirPath);
		}
	}

	private void filter(String path) {
		path = path.replace(sourceDir, "");
		if (mustBeIncluded(path)) {
			if (path.endsWith(testDirSuffix)) {
				paths.add(path);

			} else if (path.contains(testDirSuffix + "/")) {
				paths.add(getProperPath(path));
			}
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
	
	private boolean mustBeIncluded(String path){
		String topDir = path.split("/")[0];
		if(includeDirList.contains(topDir)) return true;
		return false;
	}

}