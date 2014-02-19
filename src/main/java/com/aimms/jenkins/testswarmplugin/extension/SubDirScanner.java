package com.aimms.jenkins.testswarmplugin.extension;
import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SubDirScanner  implements Serializable{
	private  List<String> allSubDirPaths = new ArrayList<String>();

	public SubDirScanner(String sourceDir) {
		

	//	listener.getLogger().println(subDirs.size());
		List<String> subDirs = getSubDirNames(sourceDir);
		
		List<String> topLevelDirPaths = new ArrayList<String>();
		for (int i = 0; i <= subDirs.size() - 1; i++) {
			topLevelDirPaths.add(sourceDir + subDirs.get(i));
		}

		retrieveAllSubDirPaths(topLevelDirPaths);
	}
	

	public List<String> getAllSubDirPaths() {
		return allSubDirPaths;
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
			allSubDirPaths.add(parentDir);
		}
	}
	

	public List<String> getSubDirNames(String parentDir){
		File file = new File(parentDir);
		String[] directories = file.list(new FilenameFilter() {
		  @Override
		  public boolean accept(File current, String name) {
		    return new File(current, name).isDirectory();
		  }
		});
		
		return stringArrayToList(directories);
	}
	
	private List<String> stringArrayToList(String[] array){
		List<String> list = new ArrayList<String>();
		for(String s: array){
			if(checkIfDirMustBeIncluded(s)){
			list.add(s);
			}
		}
		return list;
		
	}
	
	private boolean checkIfDirMustBeIncluded(String dirName){
		if(dirName.startsWith(".")) return false;
		
		return true;
		
	}
	
	

}
