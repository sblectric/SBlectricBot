package sblectricbot.chat.cmd;

import sblectricbot.chat.Chat;
import sblectricbot.command.CommandMutable;
import sblectricbot.command.CommandParam.RunnableParam;
import sblectricbot.util.PermissionLevel;

/** Set the command response */
public class CommandSetter implements RunnableParam {
	
	private CommandMutable cmd;
	private PermissionLevel perms;
	
	public CommandSetter(CommandMutable cmd, PermissionLevel perms) {
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
