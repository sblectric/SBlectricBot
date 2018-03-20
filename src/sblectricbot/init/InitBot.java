package sblectricbot.init;

import sblectricbot.SBlectricBot;
import sblectricbot.io.BotData;
import sblectricbot.ref.RefStrings;

/** Bot initialization class */
public class InitBot {
	
	/** Called at startup */
	public static void startup() {
		System.out.println(RefStrings.NAMEV);
		System.out.println(RefStrings.WELCOME);
		System.out.println("..............................");
		System.out.println("Initializing...");
	}
	
	/** Called after loading */
	public static void initDone() {
		System.out.println("Initialization complete.");
		System.out.println("[Using account '" + SBlectricBot.username + "']");
		System.out.println("[Using channel '" + SBlectricBot.channelName + "']");
		
		// add a shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
		    public void run() {
		    	try {Thread.sleep(250);} catch (Exception e) {e.printStackTrace();}
		    	BotData.save(SBlectricBot.getChatCommands().getList(), SBlectricBot.getTimerData());
		    	SBlectricBot.listener.onSave();
		    	try {Thread.sleep(250);} catch (Exception e) {e.printStackTrace();}
		        System.out.println("Bot stopped.");
		    }
		}));
	}

}
