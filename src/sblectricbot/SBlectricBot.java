package sblectricbot;

import java.util.List;
import java.util.Random;

import me.tyler.twitchbot.TwitchBot;
import sblectricbot.api.Twitch_API;
import sblectricbot.chat.ChatListener;
import sblectricbot.chat.TimedChatHandler;
import sblectricbot.command.CommandList;
import sblectricbot.console.ConsoleInputHandler;
import sblectricbot.init.InitBot;
import sblectricbot.io.BotData.TimerData;
import sblectricbot.io.TxtFileIO;
import sblectricbot.ref.RefStrings;
import sblectricbot.util.Utils;

/** The main bot class */
public class SBlectricBot {
	
	// create the directory (if it doesn't exist) and load some data
	static { new TxtFileIO().createDirIfNeeded("./db"); }
	
	// core fileset
	private static final String usernameFile, authFile, channelNameFile, webLinkFile;
	public static final String username = new TxtFileIO().readFromFileLowerCase(usernameFile = "./db/username.txt");
	public static final String auth = new TxtFileIO().readFromFileLowerCase(authFile = "./db/oauth.txt");
	public static final String channelName = new TxtFileIO().readFromFileLowerCase(channelNameFile = "./db/channel.txt");
	
	// weblink feature
	public static final List<String> webLink = new TxtFileIO().readAllLines(webLinkFile = "./db/weblink.txt");
	
	// client ID
	public static final String clientID = Twitch_API.getClientID(auth);
	
	// the bot core
	public static TwitchBot botCore;
	
	// global random number generator
	public static Random rng = new Random();
	
	// output bot details before startup
	static { InitBot.startup(); }
	
	// prepare the threads
	public static final ConsoleInputHandler console = new ConsoleInputHandler();
	public static final ChatListener listener = new ChatListener();
	public static final TimedChatHandler timers = new TimedChatHandler();
	
	/** Main method */
	public static void main(String[] args) {
		
		// check if the files exist and are not empty
		boolean canUse = true;
		if(username.equals(TxtFileIO.ERROR)) {
			System.out.println("Before you can use " + RefStrings.NAME + ", you need to create the file '" + usernameFile + 
					"', with the only contents being the user account name the bot should use.");
			canUse = false;
		}
		if(auth.equals(TxtFileIO.ERROR)) {
			System.out.println("Before you can use " + RefStrings.NAME + ", you need to create the file '" + authFile + 
					"', with the only contents being the OAuth key of the user account (oauth:<blah blah blah>).");
			canUse = false;
		}
		if(channelName.equals(TxtFileIO.ERROR)) {
			System.out.println("Before you can use " + RefStrings.NAME + ", you need to create the file '" + channelNameFile + 
					"', with the only contents being the channel name the bot should connect to (#<your name> usually).");
			canUse = false;
		}
		if(webLinkFile.equals(TxtFileIO.ERROR)) {
			System.out.println("Before you can use weblink features, you need to create the file '" + webLinkFile + 
					"', with the first line being " + 
					"<path to your web server directory to save HTML files> and the second line being " + 
					"<the URL corresponding to that path>");
		}
		if(!canUse) {
			System.out.println("Bot aborted.");
			System.exit(1);
		}
		
		// create the bot instance
		botCore = new TwitchBot(username, auth, channelName);
		InitBot.initDone();
		
		// start the extra threads
		console.launch();
		timers.launch();
		
		// start the bot threads
		try {
			botCore.addListener(listener);
			botCore.startBot(); // start it up boys
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/** Get the chat command list */
	public static CommandList getConsoleCommands() {
		return console.commands;
	}
	
	/** Get the chat command list */
	public static CommandList getChatCommands() {
		return listener.cmds;
	}
	
	/** Get the chat command timer data */
	public static TimerData getTimerData() {
		Utils.whileTimeout(()->!timers.canAccess()); // wait for unsafe conditions to pass
		return timers.timerData;
	}

}
