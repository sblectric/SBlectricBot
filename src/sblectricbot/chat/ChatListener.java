package sblectricbot.chat;

import java.util.List;
import java.util.Random;

import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;

import sblectricbot.SBlectricBot;
import sblectricbot.command.Command;
import sblectricbot.command.CommandList;
import sblectricbot.command.CommandMutable;
import sblectricbot.command.CommandParam;
import sblectricbot.command.CommandParam.RunnableParam;
import sblectricbot.command.CommandTimer;
import sblectricbot.io.BotData;
import sblectricbot.io.TxtFileIO;
import sblectricbot.util.PermissionLevel;
import sblectricbot.util.Utils;

/** Bot protocols */
public class ChatListener extends ListenerAdapter {

	public CommandList chatCommands;
	public User currentUser;
	public int chatLines;
	
	private boolean canAccessList = true;
	private Random rng = new Random();
	
	private static final String mentionsFile = "./db/mentions.txt";
	private static List<String> mentions = new TxtFileIO().readAllLines(mentionsFile);
	
	private static final String memesFile = "./db/memes.txt";
	private static List<String> memes = new TxtFileIO().readAllLines(memesFile);
	
	public ChatListener() {
		initCommands();
	}
	
	public void initCommands() {
		canAccessList = false;
		
		chatCommands = new CommandList();
		
		// add default broadcaster commands
		chatCommands.addCommand(new Command("!commandlist", ()->listCommands()).setDefault());
		chatCommands.addCommand(new CommandParam("!newcommandb", new CommandAdder(PermissionLevel.BROADCASTER), PermissionLevel.BROADCASTER).setDefault());
		chatCommands.addCommand(new CommandParam("!newcommandm", new CommandAdder(PermissionLevel.MODERATOR), PermissionLevel.BROADCASTER).setDefault());
		chatCommands.addCommand(new CommandParam("!newcommand", new CommandAdder(PermissionLevel.VIEWER), PermissionLevel.BROADCASTER).setDefault());
		chatCommands.addCommand(new Command("!toggletimers", new TimerToggle(), PermissionLevel.BROADCASTER).setDefault());
		chatCommands.addCommand(new CommandParam("!newtimer30m", new TimerAdder(1800, 25), PermissionLevel.BROADCASTER).setDefault());
		chatCommands.addCommand(new CommandParam("!newtimer90m", new TimerAdder(5400, 40), PermissionLevel.BROADCASTER).setDefault());
		chatCommands.addCommand(new CommandParam("!remcommand", new CommandRemover(), PermissionLevel.BROADCASTER).setDefault());
		chatCommands.addCommand(new CommandParam("!remtimer", new TimerRemover(), PermissionLevel.BROADCASTER).setDefault());
		chatCommands.addCommand(new Command("!reloadmentions", new MentionReloader(), PermissionLevel.BROADCASTER).setDefault());
		
		// add default moderator commands
		chatCommands.addCommand(new CommandParam("!newmeme", new MemeAdder(), PermissionLevel.MODERATOR).setDefault());
		chatCommands.addCommand(new CommandParam("!remmeme", new MemeRemover(), PermissionLevel.MODERATOR).setDefault());
		chatCommands.addCommand(new CommandParam("!memecount", ()->new Meme().outputCount(), PermissionLevel.MODERATOR).setDefault());
		chatCommands.addCommand(new CommandParam("!shoutout", new Shoutout(), PermissionLevel.MODERATOR).setDefault());
		
		// add default viewer commands
		Command meme = new CommandParam("!meme", new Meme(), PermissionLevel.VIEWER).setDefault();
		chatCommands.addCommand(meme); chatCommands.setCommandCooldown(meme, 20000); // !meme has 20s cooldown by default
		
		// load custom commands from disk
		System.out.println("Loading commands from disk...");
		List<Command> readList = BotData.loadCommands();
		System.out.println("All commands loaded.");
		for(Command c : readList) chatCommands.addCommand(c);
		
		canAccessList = true;
	}
	
	/** Called when the bot is saved, meme saving and such done here */
	public void onSave() {
		new TxtFileIO().writeToFile(memesFile, memes);
	}
	
	/** Respond to commands and mentions and the like */
	@Override
	public void onGenericMessage(GenericMessageEvent event) {
		currentUser = event.getUser();
		String cmd = "";
		String param = "";
		String message = event.getMessage();
		try {
			cmd = message.split(" ")[0];
			param = message.split(" ", 2)[1];
		} catch (Exception e) {
			cmd = message;
		}
		
		// first check for commands
		canAccessList = false;
		chatCommands.runCommandByName(currentUser, cmd, param);
		canAccessList = true;
		
		// then check for mentions
		if(message.toLowerCase().contains(SBlectricBot.username)) {
			sendRandomReplyToMention(currentUser.getNick());
		}
		
		// track the number of lines in chat that have passed
		chatLines++;
	}
	
	/** Reply to mentions randomly */
	private void sendRandomReplyToMention(String user) {
		if(!mentions.isEmpty()) {
			int randomIndex = rng.nextInt(mentions.size());
			Chat.sendMessage(String.format(mentions.get(randomIndex), user));
		} else {
			System.out.println("File '" + mentionsFile + "' must exist before you can use the mentions feature! "
					+ "This file should have lines in the format of '<first part of message> %s <rest of message>', "
					+ "where %s is the user who mentioned the bot.");
		}
	}
	
	/** List the commands */
	private void listCommands() {
		String output = "Yo, " + currentUser.getNick() + ", your commands are: ";
		canAccessList = false;
		for(int i = 0; i < chatCommands.getList().size(); i++) {
			Command c = chatCommands.getList().get(i);
			if(Chat.getUserPermissionLevel(currentUser).isAtLeast(c.getPermissions())) {
				output += c.getName() + ", ";
			}
		}
		canAccessList = true;
		output = output.substring(0, output.length() - 2);
		new RunnableChat(output).run();
	}
	
	/** Can the list be safely accessed? */
	public boolean canAccess() {
		return this.canAccessList;
	}
	
	/** Add a new command */
	private class CommandAdder implements RunnableParam {
		
		private PermissionLevel perms;
		private PermissionLevel modifyPerms;
		
		private CommandAdder(PermissionLevel perms) {
			this.perms = perms;
			this.modifyPerms = (perms == PermissionLevel.BROADCASTER ? perms : PermissionLevel.MODERATOR);
		}

		@Override
		public void run() {}

		@Override
		public void run(String param) {
			CommandMutable c = new CommandMutable(param).setPermissions(modifyPerms);
			chatCommands.addCommand(c.setTask(new CommandSetter(c, perms)));
			String output = "Command '" + param + "' added with permission level " + perms + ".";
			System.out.println(output);
			Chat.sendMessage(output + " Type '" + param + " <message>' to set its output.");
		}
	}
	
	/** Set the command response */
	private class CommandSetter implements RunnableParam {
		
		private CommandMutable cmd;
		private PermissionLevel perms;
		
		private CommandSetter(CommandMutable cmd, PermissionLevel perms) {
			this.cmd = cmd;
			this.perms = perms;
		}

		@Override
		public void run() {}

		@Override
		public void run(String param) {
			cmd.setTask(new RunnableChat(param)).setPermissions(perms);
			String output = "Command '" + cmd.getName() + "' set to output '" + param + "'.";
			System.out.println(output);
			Chat.sendMessage(output + " Try it out!");
		}
	}
	
	/** Toggle timed commands */
	private class TimerToggle implements Runnable {
		@Override
		public void run() {
			String status = SBlectricBot.timers.toggleTimers() ? "enabled" : "disabled";
			String output = "Timed commands are now " + status + ".";
			Chat.sendMessage(output);
		}
	}
	
	/** Add a command to a timer */
	private class TimerAdder implements RunnableParam {
		
		private int minSeconds;
		private int minChatLines;
		
		private TimerAdder(int minSeconds, int minChatLines) {
			this.minSeconds = minSeconds;
			this.minChatLines = minChatLines;
		}

		@Override
		public void run() {}
		
		@Override
		public void run(String param) {
			Utils.whileTimeout(()->!SBlectricBot.timers.canAccess()); // wait for unsafe conditions to pass
			Command c = chatCommands.getCommandByName(param);
			String output;
			if(c != null) {
				SBlectricBot.timers.addTimer(new CommandTimer(c, minSeconds, minChatLines));
				output = "Timer added for command '" + param + "', with minimum second count " + 
						minSeconds + " and minimum chat line count " + minChatLines + ".";
			} else {
				output = "Timer addition failed, command '" + param + "' does not exist!";
			}
			System.out.println(output);
			Chat.sendMessage(output);
		}
		
	}
	
	/** Remove a command */
	private class CommandRemover implements RunnableParam {
		
		@Override
		public void run() {}
		
		@Override
		public void run(String param) {
			Command c = chatCommands.getCommandByName(param);
			String output;
			if(c != null) {
				chatCommands.getList().remove(c);
				output = "Command '" + param + "' successfully removed.";
			} else {
				output = "Removal failed, command '" + param + "' does not exist!";
			}
			System.out.println(output);
			Chat.sendMessage(output);
		}
		
	}
	
	/** Remove command timers matching a command */
	private class TimerRemover implements RunnableParam {
		
		@Override
		public void run() {}
		
		@Override
		public void run(String param) {
			Utils.whileTimeout(()->!SBlectricBot.timers.canAccess()); // wait for unsafe conditions to pass
			Command c = chatCommands.getCommandByName(param);
			String output;
			if(c != null) {
				if(SBlectricBot.timers.removeTimers(c)) {
					output = "All timers calling command '" + param + "' successfully removed.";
				} else {
					output = "Removal failed, command '" + param + "' does not have any timers associated with it!";
				}
			} else {
				output = "Removal failed, command '" + param + "' does not exist!";
			}
			System.out.println(output);
			Chat.sendMessage(output);
		}
		
	}
	
	/** Reload the mentions file */
	private class MentionReloader implements Runnable {
		@Override
		public void run() {
			mentions = new TxtFileIO().readAllLines(mentionsFile);
			String output = "Reloaded mentions successfully.";
			System.out.println(output);
			Chat.sendMessage(output);
		}
	}
	
	/** Gotta have a meme command */
	private class Meme implements RunnableParam {

		@Override
		public void run() {}

		/** Output the size of the meme list */
		public void outputCount() {
			Chat.sendMessage("There are " + memes.size() + " memes available for your shitposting pleasure.");
		}

		@Override
		public void run(String param) {
			final int NaN = Integer.MIN_VALUE;
			int index = NaN;
			try {
				index = Integer.parseInt(param);
			} catch(Exception e) {}
			
			if(index >= 1 && index < memes.size() + 1) {
				Chat.sendMessage(memes.get(index - 1));
			} else {
				if(index == NaN) { // not a number, use meme search feature and fall back to random meme
					String toSend = memes.get(rng.nextInt(memes.size()));
					if(!param.equals("")) {
						for(String meme : memes) {
							if(meme.toLowerCase().contains(param.toLowerCase())) {
								toSend = meme;
								break;
							}
						}
					}
					Chat.sendMessage(toSend);
				} else { // normal OoB condition
					Chat.sendMessage("Specified index is out of bounds, valid range is 1 to " + memes.size() + ".");
				}
			}
		}
		
	}
	
	/** Add a maymay */
	private class MemeAdder implements RunnableParam {
		
		@Override
		public void run() {}

		@Override
		public void run(String param) {
			if(!param.equals("")) {
				memes.add(param);
				Chat.sendMessage("Added meme #" + memes.size() +". Type '!meme " + memes.size() + "' to check it out!");
			} else {
				Chat.sendMessage("Must specify a meme to add!");
			}
		}
	}
	
	/** Remove a maymay */
	private class MemeRemover implements RunnableParam {
		
		@Override
		public void run() {}

		@Override
		public void run(String param) {
			int index = -1;
			try {
				index = Integer.parseInt(param);
			} catch(Exception e) {}
			
			if(index >= 1 && index < memes.size() + 1) {
				Chat.sendMessage("Meme #" + index + " (" + memes.get(index - 1) + ") removed successfully.");
				memes.remove(index - 1);
			} else {
				Chat.sendMessage("Must specify a meme to remove by its valid numerical index!");
			}
		}
	}
	
	/** Shoutout command, ala YateBot */
	private class Shoutout implements RunnableParam {

		@Override
		public void run() {}
		
		@Override
		public void run(String param) {
			Chat.sendMessage("Yo, check out " + param + ", another awesome streamer here: https://www.twitch.tv/" + param + " PartyTime");
		}

	}
    
}
