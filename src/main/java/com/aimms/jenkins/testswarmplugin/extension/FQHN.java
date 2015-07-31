package com.aimms.jenkins.testswarmplugin.extension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;

public class FQHN implements Serializable {
	private String fqhn;
	private static final long serialVersionUID = 1L;

	public FQHN() throws IOException, InterruptedException {
		retrieveFQHN();
	}

	private void retrieveFQHN() throws IOException, InterruptedException {
		Runtime run = Runtime.getRuntime();
		Process pr = run.exec("hostname -f");
		pr.waitFor();
		BufferedReader buf = new BufferedReader(new InputStreamReader(
				pr.getInputStream()));
		String name = buf.readLine();
		if (!name.isEmpty()) {
			fqhn = name;

		}
	}

	public String getFQHN() {
		return fqhn;
	}

}
