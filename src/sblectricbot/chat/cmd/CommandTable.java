package sblectricbot.chat.cmd;

import java.util.LinkedList;
import java.util.List;

import sblectricbot.chat.Chat;
import sblectricbot.command.Command;
import sblectricbot.command.CommandList;
import sblectricbot.util.PermissionLevel;

/** A command that lists the commands for a user (weblink) */
public class CommandTable extends WeblinkTable {

	private CommandList cmds;
	private String filename = LOCAL_PATH + "commands.html";

	public CommandTable(CommandList commands) {
		cmds = commands;
	}

	@Override
	public void run() {

		List<String> tableLeft = new LinkedList<String>();
		List<String> tableRight = new LinkedList<String>();

		tableLeft.add("<b>Command</b>");
		tableRight.add("<b>Needed Permissions</b>");

		for(Command c : cmds.getList()) {
			if(c.getPermissions() != PermissionLevel.BROADCASTER) {
				tableLeft.add(c.getName());
				tableRight.add(c.getPermissions().toString());
			}
		}

		boolean success = this.writeTable(filename, tableLeft, tableRight);

		if (success) {
			Chat.sendMessage("Command list: " + REMOTE_PATH + "commands.html");
		} else {
			Chat.sendMessage("HTML writing failed. Check your filepath and permissions!");
		}

	}

}
