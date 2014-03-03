package com.aimms.jenkins.testswarmplugin.extension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class TestDirPathFiltering {
	private Set<String> paths = new LinkedHashSet<String>(); // Don't want duplicate path-elements
	private final String testDirSuffix;
	private final String sourceDir;
	private List<String> includeDirList;
	private final static String MODULE_DIVIDER_SYMBOL = ",";
	private final boolean INCLUDE_ALL_DIRS;

	public TestDirPathFiltering(List<String> subDirPaths, String sourceDir,
			String testFolderName, String includedDirsInStrformat) {
		this.sourceDir = sourceDir;
		INCLUDE_ALL_DIRS = includeAllRootDir(includedDirsInStrformat);
        if(!INCLUDE_ALL_DIRS){
		includeDirList = Arrays.asList(includedDirsInStrformat
				.split(MODULE_DIVIDER_SYMBOL));
        }
		testDirSuffix = "/" + testFolderName;
		for (String subDirPath : subDirPaths) {
			filter(subDirPath);
		}
	}

	private void filter(String path) {
		path = path.replace(sourceDir, "");
		if (INCLUDE_ALL_DIRS || mustBeIncluded(path)) {
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
		List<String> filteredPaths = new ArrayList<String>();
		filteredPaths.addAll(paths);
		return filteredPaths;
	}

	private boolean mustBeIncluded(String path) {
		String topDir = path.split("/")[0];
		if (includeDirList.contains(topDir)
				|| includeDirList.contains(path.replace(testDirSuffix, ""))
				|| shouldIncludeNonTestParentFolder(path))
			return true;
		return false;
	}

	private boolean includeAllRootDir(String includedDirsInStrformat) {
		if (includedDirsInStrformat.equals("*"))
			return true;
		return false;
	}
	
	
	private boolean shouldIncludeNonTestParentFolder(String path){
		for(String dirPath :includeDirList){
			if(path.contains(dirPath) && dirPath.split("/")[0].equals(path.split("/")[0])){
				return true;
			}
		}
		return false;
	}

}