package sblectricbot.util;

import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.Callable;

import org.json.JSONObject;
import org.json.JSONTokener;

/** Various utility methods */
public class Utils {

	/** Get the system time in seconds */
	public static int systemTimeSeconds() {
		return (int)(System.currentTimeMillis() / 1000L);
	}

	/** A while loop with the condition specified, but with a max execution time */
	public static void whileTimeout(Callable<Boolean> condition, long timeoutMillis) {
		boolean check;
		long startTime = System.currentTimeMillis();
		do {
			try {
				check = condition.call();
			} catch(Exception e) {
				e.printStackTrace();
				check = false; // abort the loop
			}
			if(System.currentTimeMillis() > startTime + timeoutMillis) check = false; // timeout check
		} while(check);
	}

	/** A while loop with the condition specified, but with a max execution time of 1s */
	public static void whileTimeout(Callable<Boolean> condition) {
		whileTimeout(condition, 1000);
	}

	/** Get the ordinal of a number */
	public static String getOrdinal(int number) {
		number = number % 100;
		if(number > 20) number = number % 10;
		if(number == 1) return "st";
		if(number == 2) return "nd";
		if(number == 3) return "rd";
		return "th";
	}
	
	/** Get a JSON from a basic web path */
	public static JSONObject getJSONFromWeb(String webPath) {
		try {
			URI uri = new URI(webPath);
			URLConnection conn = uri.toURL().openConnection();
			InputStream input = conn.getInputStream();
			JSONTokener tokener = new JSONTokener(input);
			JSONObject json = new JSONObject(tokener);
			
			return json;
		} catch(Exception e) {
			e.printStackTrace();
			return new JSONObject();
		}
	}
	
	private static String lastJSONName = "";

	/** Get a json object by any case */
	public static JSONObject getJSONObjectAnyCase(JSONObject parent, String name) {
		name = name.toLowerCase();    
		int combinations = 1 << name.length();
		
		for (int i = 0; i < combinations; i++) {
			char[] result = name.toCharArray();
			
			// build the current combination
			for (int j = 0; j < name.length(); j++) {
				if (((i >> j) & 1) == 1) {
					result[j] = Character.toUpperCase(name.charAt(j));
				}
			}
			
			// try it
			String currentName = new String(result);
			
			// return the value if successful
			if(parent.has(currentName) && parent.get(currentName) instanceof JSONObject) {
				lastJSONName = currentName;
				return parent.getJSONObject(currentName);
			}
		}
		
		return new JSONObject(); // empty if failed
	}
	
	/** Get the exact case of the last JSON object name that was successfully obtained */
	public static String getLastJSONObjectName() {
		return lastJSONName;
	}
	
	/** Converts a UTC string to milliseconds since epoch */
	public static long UTCtoMillis(String utc) {
		DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			Date date = utcFormat.parse(utc);
			return date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return -1;
		}
	}

}
