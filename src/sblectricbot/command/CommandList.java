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
	private Map<Command, Long> cmdCooldownMap = new HashMap<Command, Long>();
	private Map<Command, Long> cmdCooldownTrackerMap = new HashMap<Command, Long>();
	
	private static final long defaultCooldown = 5000; // 5s default timeout
	
	/** Set a command cooldown to a non-default value */
	public void setCommandCooldown(Command cmd, long cooldown) {
		cmdCooldownMap.put(cmd, cooldown);
	}
	
	/** Gets the cooldown for the specified command */
	public long getCommandCooldown(Command cmd) {
		return cmdCooldownMap.containsKey(cmd) ? cmdCooldownMap.get(cmd) : defaultCooldown;
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
			long time = System.currentTimeMillis();
			long cooldown = this.getCommandCooldown(c);
			
			// check cooldown conditions
			boolean cooldownPassed = (!cmdCooldownTrackerMap.containsKey(c)) || (time - cmdCooldownTrackerMap.get(c) >= cooldown);
			if(user == null || Chat.isBroadcaster(user) || cooldownPassed) {
				runCommand(user, c, param);
				if(user != null) cmdCooldownTrackerMap.put(c, time);
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
