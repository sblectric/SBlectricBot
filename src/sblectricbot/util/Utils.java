package sblectricbot.util;

import java.util.concurrent.Callable;

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

}
