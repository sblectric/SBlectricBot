package sblectricbot.chat.cmd;

import sblectricbot.chat.Chat;
import sblectricbot.command.CommandList;
import sblectricbot.command.CommandMutable;
import sblectricbot.command.CommandParam.RunnableParam;
import sblectricbot.util.PermissionLevel;

/** Add a new command */
public class CommandAdder implements RunnableParam {
	
	private PermissionLevel perms;
	private PermissionLevel modifyPerms;
	private CommandList list;
	
	public CommandAdder(PermissionLevel perms, CommandList list) {
		this.perms = perms;
		this.modifyPerms = (perms == PermissionLevel.BROADCASTER ? perms : PermissionLevel.MODERATOR);
		this.list = list;
	}

	@Override
	public void run() {}

	@Override
	public void run(String param) {
		CommandMutable c = new CommandMutable(param).setPermissions(modifyPerms);
		list.addCommand(c.setTask(new CommandSetter(c, perms)));
		String output = "Command '" + param + "' added with permission level " + perms + ".";
		System.out.println(output);
		Chat.sendMessage(output + " Type '" + param + " <message>' to set its output.");
	}
}
