package sblectricbot.chat.cmd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

import sblectricbot.SBlectricBot;

/** Weblink command parent class */
public abstract class WeblinkTable implements Runnable {
	
	public static String LOCAL_PATH, REMOTE_PATH;
	public static boolean localPathExists;
	static {
		if(!SBlectricBot.webLink.isEmpty()) {
			LOCAL_PATH = SBlectricBot.webLink.get(0);
			REMOTE_PATH = SBlectricBot.webLink.get(1);
			
			if(!LOCAL_PATH.substring(LOCAL_PATH.length() - 1).equals("/")) {
				LOCAL_PATH = LOCAL_PATH + "/";
			}
			if(!REMOTE_PATH.substring(REMOTE_PATH.length() - 1).equals("/")) {
				REMOTE_PATH = REMOTE_PATH + "/";
			}
			
			localPathExists = new File(LOCAL_PATH).isDirectory();
		} else {
			LOCAL_PATH = "";
			REMOTE_PATH = "";
			localPathExists = false;
		}
	}
	
	/** Write the table to file */
	protected boolean writeTable(String htmlfile, List<String> tableLeft, List<String> tableRight) {
		boolean success = false;
		
		if(localPathExists && tableLeft.size() == tableRight.size()) {
			
			int size = tableLeft.size();
			
			try {
				FileWriter f = new FileWriter(htmlfile);
				BufferedWriter bw = new BufferedWriter(f);
				
				bw.write("<html>\n");
				bw.write("<head><link rel='stylesheet' href='table.css'></head>\n");
				bw.write("<table>\n");
				
				for(int i = 0; i < size; i++) {
					bw.write("<tr>\n");
					
					bw.write("<td>\n");
					bw.write("<p>" + tableLeft.get(i) + "</p>");
					bw.write("</td>\n");
					
					bw.write("<td>\n");
					bw.write("<p>" + tableRight.get(i) + "</p>");
					bw.write("</td>\n");
					
					bw.write("</tr>\n");
				}
				
				bw.write("</table>\n");
				bw.write("</html>\n");
				
				bw.close();
				f.close();
				success = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return success;
	}
	
}
