package edu.gatech.webdiff;
// Created by DevDaily.com

import java.io.*;

public class RunCommand {

    public static String runCmd(String x) {
    	//System.out.println(x);
    	Runtime run = Runtime.getRuntime();
		try {
			Process pp=run.exec(WDConstants.EXEC_DIR+x);
//			System.out.println("D:/workspace/webdiff/"+x);
//			System.out.println(pp.waitFor());
			BufferedReader in =new BufferedReader(new InputStreamReader(pp.getInputStream()));
			
			String line=null;
			while ((line = in.readLine()) != null) {
				
				return line;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return null;
		
	}
  }

