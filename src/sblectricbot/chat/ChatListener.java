package sblectricbot.chat;

import java.util.List;

import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;

import sblectricbot.SBlectricBot;
import sblectricbot.api.SRC_API.SRCType;
import sblectricbot.chat.cmd.CommandAdder;
import sblectricbot.chat.cmd.CommandRemover;
import sblectricbot.chat.cmd.Meme;
import sblectricbot.chat.cmd.MemeAdder;
import sblectricbot.chat.cmd.MemeRemover;
import sblectricbot.chat.cmd.MentionReloader;
import sblectricbot.chat.cmd.RunnableChat;
import sblectricbot.chat.cmd.SRC;
import sblectricbot.chat.cmd.Shoutout;
import sblectricbot.chat.cmd.TimerAdder;
import sblectricbot.chat.cmd.TimerRemover;
import sblectricbot.chat.cmd.TimerToggle;
import sblectricbot.chat.cmd.TwitchUptime;
import sblectricbot.command.Command;
import sblectricbot.command.CommandList;
import sblectricbot.command.CommandParam;
import sblectricbot.io.BotData;
import sblectricbot.io.TxtFileIO;
import sblectricbot.ref.RefStrings;
import sblectricbot.util.PermissionLevel;

/** Bot protocols */
public class ChatListener extends ListenerAdapter {

	public CommandList cmds;
	public User currentUser;
	public int chatLines;
	
	private boolean canAccessList = true;
	
	private static final String mentionsFile = "./db/mentions.txt";
	private static List<String> mentions = new TxtFileIO().readAllLines(mentionsFile);
	
	private static final String memesFile = "./db/memes.txt";
	private static List<String> memes = new TxtFileIO().readAllLines(memesFile);
	
	public ChatListener() {
		initCommands();
	}
	
	public void initCommands() {
		canAccessList = false;
		
		cmds = new CommandList();
		
		// command list command
		cmds.addCommand(new Command("!commandlist", ()->listCommands()).setDefault());
		
		// add default broadcaster commands
		cmds.addCommand(new CommandParam("!newcommandb", new CommandAdder(PermissionLevel.BROADCASTER, cmds), PermissionLevel.BROADCASTER).setDefault());
		cmds.addCommand(new CommandParam("!newcommandm", new CommandAdder(PermissionLevel.MODERATOR, cmds), PermissionLevel.BROADCASTER).setDefault());
		cmds.addCommand(new CommandParam("!newcommand", new CommandAdder(PermissionLevel.VIEWER, cmds), PermissionLevel.BROADCASTER).setDefault());
		cmds.addCommand(new Command("!toggletimers", new TimerToggle(), PermissionLevel.BROADCASTER).setDefault());
		cmds.addCommand(new CommandParam("!newtimer30m", new TimerAdder(1800, 25, cmds), PermissionLevel.BROADCASTER).setDefault());
		cmds.addCommand(new CommandParam("!newtimer90m", new TimerAdder(5400, 40, cmds), PermissionLevel.BROADCASTER).setDefault());
		cmds.addCommand(new CommandParam("!remcommand", new CommandRemover(cmds), PermissionLevel.BROADCASTER).setDefault());
		cmds.addCommand(new CommandParam("!remtimer", new TimerRemover(cmds), PermissionLevel.BROADCASTER).setDefault());
		cmds.addCommand(new Command("!reloadmentions", new MentionReloader(mentions, mentionsFile), PermissionLevel.BROADCASTER).setDefault());
		
		// add default moderator commands
		cmds.addCommand(new CommandParam("!newmeme", new MemeAdder(memes), PermissionLevel.MODERATOR).setDefault());
		cmds.addCommand(new CommandParam("!remmeme", new MemeRemover(memes), PermissionLevel.MODERATOR).setDefault());
		cmds.addCommand(new CommandParam("!memecount", ()->new Meme(memes).outputCount(), PermissionLevel.MODERATOR).setDefault());
		cmds.addCommand(new CommandParam("!shoutout", new Shoutout(), PermissionLevel.MODERATOR).setDefault());
		
		// add default viewer commands
		cmds.addCommand(new Command("!github", new RunnableChat(RefStrings.NAME + " GitHub page: " + RefStrings.GITHUB)).setDefault());
		Command meme = new CommandParam("!meme", new Meme(memes), PermissionLevel.VIEWER).setDefault();
		cmds.addCommand(meme); cmds.setCommandCooldown(meme, 20000); // !meme has 20s cooldown by default
		
		// add default twitch API commands
		cmds.addCommand(new Command("!uptime", new TwitchUptime(), PermissionLevel.VIEWER).setDefault());
		
		// add default speedrun.com API commands
		cmds.addCommand(new CommandParam("!wr", new SRC(SRCType.WR), PermissionLevel.VIEWER).setDefault());
		cmds.addCommand(new CommandParam("!pb", new SRC(SRCType.PB), PermissionLevel.VIEWER).setDefault());
		
		// load custom commands from disk
		System.out.println("Loading commands from disk...");
		List<Command> readList = BotData.loadCommands();
		System.out.println("All commands loaded.");
		for(Command c : readList) cmds.addCommand(c);
		
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
		cmds.runCommandByName(currentUser, cmd, param);
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
			int randomIndex = SBlectricBot.rng.nextInt(mentions.size());
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
		for(int i = 0; i < cmds.getList().size(); i++) {
			Command c = cmds.getList().get(i);
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
    
}
