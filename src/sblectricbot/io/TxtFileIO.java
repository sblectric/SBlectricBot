package sblectricbot.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Text file reader/writer */
public class TxtFileIO {
	
	public static final String ERROR = "/NULL/";
	
	/** Create the directory if it doesn't exist */
	public boolean createDirIfNeeded(String dir) {
		return new File(dir).mkdirs();
	}
	
	/** Get the string (first line) from a file on disk */
	public String readFromFile(String filename) {
		try {
			FileReader f = new FileReader(filename);
			BufferedReader br = new BufferedReader(f);
			String s = br.readLine();
			br.close();
			f.close();
			return s;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ERROR;
	}
	
	/** Get the string (first line) from a file on disk, converted to lowercase */
	public String readFromFileLowerCase(String filename) {
		String result = readFromFile(filename);
		if(!result.equals(ERROR)) result = result.toLowerCase(Locale.ROOT);
		return result;
	}
	
	/** Read all lines in the file specified */
	public List<String> readAllLines(String filename) {
		List<String> strs = new ArrayList<String>();
		try {
			FileReader f = new FileReader(filename);
			BufferedReader br = new BufferedReader(f);
			String s;
			while((s = br.readLine()) != null) strs.add(s);
			br.close();
			f.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strs;
	}
	
	/** Read all lines in the file specified, converted to lowercase */
	public List<String> readAllLinesLowerCase(String filename) {
		List<String> strs = new ArrayList<String>();
		for(String s : readAllLines(filename)) {
			strs.add(s.toLowerCase(Locale.ROOT));
		}
		return strs;
	}
	
	/** Writer functionality */
	public boolean writeToFile(String filename, List<String> lines) {
		boolean success = false;
		try {
			FileWriter f = new FileWriter(filename);
			BufferedWriter bw = new BufferedWriter(f);
			for(String s : lines) bw.write(s + "\n");
			bw.close();
			f.close();
			success = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return success;
	}

}
