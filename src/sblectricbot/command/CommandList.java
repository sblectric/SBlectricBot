package sblectricbot.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.pircbotx.User;

import sblectricbot.chat.Chat;

/** A command list */
public class CommandList {
	
	/** The list of commands */
	private List<Command> commands = new LinkedList<Command>();
	private Map<Command, Long> cmdTimeoutMap = new HashMap<Command, Long>();
	private Map<String, Long> userTimeoutTracker = new HashMap<String, Long>();
	
	private static final long defaultTimeout = 5000; // 5s default timeout
	
	/** Set a command timeout to a non-default value */
	public void setCommandTimeout(Command cmd, long timeout) {
		cmdTimeoutMap.put(cmd, timeout);
	}
	
	/** Add a command to the command list */
	public CommandList addCommand(Command cmd) {
		commands.add(cmd);
		return this;
	}
	
	/** Add commands to the command list */
	public CommandList addCommands(Command... cmds) {
		for(Command c : cmds) addCommand(c);
		return this;
	}
	
	/** Remove all commands from this list */
	public void clearCommands() {
		commands.clear();
	}
	
	/** Get the command list */
	public final List<Command> getList() {
		return commands;
	}
	
	/** Gets the command names */
	public List<String> getCommandNames() {
		List<String> names = new ArrayList<String>();
		for(Command c : commands) names.add(c.getName());
		return names;
	}
	
	/** Get a command by name */
	public Command getCommandByName(String command) {
		for(Command c : commands) {
			if(c.getName().equals(command)) {
				return c;
			}
		}
		return null;
	}
	
	/** Run a command by name, if it exists */
	public boolean runCommandByName(User user, String command, String param) {
		Command c = getCommandByName(command);
		if(c != null) {
			String key = user + ":" + command;
			long time = System.currentTimeMillis();
			long timeout = cmdTimeoutMap.containsKey(command) ? cmdTimeoutMap.get(command) : defaultTimeout;
			if(Chat.isBroadcaster(user) || 
					!userTimeoutTracker.containsKey(key) || 
					time - userTimeoutTracker.get(key) >= timeout) { // check timeout conditions
				if(c instanceof CommandParam) {
					((CommandParam)c).runTask(user, param);
				} else {
					c.runTask(user);
				}
				if(!Chat.isBroadcaster(user)) userTimeoutTracker.put(key, time);
			}
			return true;
		} else {
			return false;
		}
	}
	
	/** Run an existing command */
	public void runCommand(User user, Command command, String param) {
		if(command instanceof CommandParam) {
			((CommandParam)command).runTask(user, param);
		} else {
			command.runTask(user);
		}
	}

}
