package sblectricbot.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONWriter;

import sblectricbot.SBlectricBot;
import sblectricbot.chat.cmd.RunnableChat;
import sblectricbot.command.Command;
import sblectricbot.command.CommandMutable;
import sblectricbot.command.CommandTimer;
import sblectricbot.util.PermissionLevel;
import sblectricbot.util.Utils;

/** Data to be saved to disk */
public class BotData {
	
	public static final String cmdFile = "./db/commands.json";
	public static final String timerFile = "./db/timers.json";
	
	/** Timer data object */
	public static class TimerData {
		public boolean active;
		public List<CommandTimer> timers;
		
		public TimerData(boolean active, List<CommandTimer> timers) {
			this.active = active;
			this.timers = timers;
		}
	}
	
	/** Load the data */
	public static void load(List<Command> commandList, TimerData timerData) {
    	System.out.println("Loading data...");
		commandList.clear();
		timerData.timers.clear();
		for(Command c : loadCommands()) commandList.add(c);
		TimerData dataLocal = loadTimers();
		timerData.active = dataLocal.active;
		for(CommandTimer t : dataLocal.timers) timerData.timers.add(t);
    	System.out.println("Data loaded.");
	}
	
	/** Save the data */
	public static void save(List<Command> commandList, TimerData timerData) {
		Utils.whileTimeout(()->!SBlectricBot.timers.canAccess()); // avoid thread issues
    	System.out.println("Saving data...");
		saveCommands(commandList);
		saveTimers(timerData);
    	System.out.println("Data saved.");
	}
	
	/** Save commands to file */
	public static void saveCommands(List<Command> commandList) {
		try {
			FileWriter f = new FileWriter(cmdFile);
			JSONWriter writer = new JSONWriter(f);
			writer.object();
			writer.key("commandList");
			writer.array();
			for(Command command : commandList) {
				if(!command.isDefault() && command.isTaskSerializable()) {
					writer.object();
					writer.key("command");
					writer.value(command.toString());
					writer.endObject();
				}
			}
			writer.endArray();
			writer.endObject();
			f.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/** Load commands from file */
	public static List<Command> loadCommands() {
		List<Command> commandList = new LinkedList<Command>();
		try {
			FileInputStream input = new FileInputStream(cmdFile);
			JSONTokener tokener = new JSONTokener(input);
			JSONObject json = new JSONObject(tokener);
			JSONArray cmds = json.getJSONArray("commandList");
			for(int i = 0; i < cmds.length(); i++) {
				try {
					JSONObject e = (JSONObject)cmds.get(i);
					String[] elements = e.getString("command").split(Command.SEPARATOR);
					String name = elements[0];
					String task = elements[1];
					PermissionLevel perms = PermissionLevel.fromString(elements[2]);
					Command toAdd = new CommandMutable(name, new RunnableChat(task), perms);
					commandList.add(toAdd);
					System.out.println("Loaded command [" + toAdd + "]");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			input.close();
		} catch(FileNotFoundException e) {
			System.out.println("File does not exist.");
		} catch(Exception e) {
			e.printStackTrace();
		}
		return commandList;
	}
	
	/** Save timers to file */
	public static void saveTimers(TimerData timerData) {
		List<CommandTimer> commandTimers = timerData.timers;
		try {
			FileWriter f = new FileWriter(timerFile);
			JSONWriter writer = new JSONWriter(f);
			
			// save the active state
			writer.object();
			writer.key("isActive");
			writer.value(timerData.active);
			
			// save the timer list
			writer.key("timerList");
			writer.array();
			for(CommandTimer timer : commandTimers) {
				writer.object();
				writer.key("timer");
				writer.value(timer.toString());
				writer.endObject();
			}
			writer.endArray();
			writer.endObject();
			
			f.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/** Load timers from file */
	public static TimerData loadTimers() {
		boolean active = true;
		List<CommandTimer> timerList = new LinkedList<CommandTimer>();
		try {
			FileInputStream input = new FileInputStream(timerFile);
			JSONTokener tokener = new JSONTokener(input);
			JSONObject json = new JSONObject(tokener);
			
			// get the active state
			try {
				active = json.getBoolean("isActive");
			} catch(JSONException e) {
				e.printStackTrace();
			}
			
			// load the timer list
			JSONArray cmds = json.getJSONArray("timerList");
			for(int i = 0; i < cmds.length(); i++) {
				try {
					JSONObject e = (JSONObject)cmds.get(i);
					String[] elements = e.getString("timer").split(Command.SEPARATOR);
					String name = elements[0];
					int minSeconds = Integer.valueOf(elements[1]);
					int minChatLines = Integer.valueOf(elements[2]);
					Command c = SBlectricBot.getChatCommands().getCommandByName(name);
					if(c != null) {
						CommandTimer toAdd = new CommandTimer(c, minSeconds, minChatLines);
						timerList.add(toAdd);
						System.out.println("Loaded timer [" + toAdd + "]");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			input.close();
		} catch(FileNotFoundException e) {
			System.out.println("File does not exist.");
		} catch(Exception e) {
			e.printStackTrace();
		}
		return new TimerData(active, timerList);
	}

}
