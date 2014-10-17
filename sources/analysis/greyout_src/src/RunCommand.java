// Created by DevDaily.com

import java.io.*;

public class RunCommand {

    public static String runCmd(String x) {
    	Runtime run = Runtime.getRuntime();
		try {
			Process pp=run.exec("D:/workspace/webdiff/"+x);
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

