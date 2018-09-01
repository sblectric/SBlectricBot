package sblectricbot.chat.cmd;

import sblectricbot.chat.Chat;
import sblectricbot.command.Command;
import sblectricbot.command.CommandList;
import sblectricbot.command.CommandParam.RunnableParam;

/** Remove a command */
public class CommandRemover implements RunnableParam {
	
	private CommandList list;
	
	public CommandRemover(CommandList list) {
		this.list = list;
	}
	
	@Override
	public void run() {}
	
	@Override
	public void run(String param) {
		Command c = list.getCommandByName(param);
		String output;
		if(c != null) {
			list.getList().remove(c);
			output = "Command '" + param + "' successfully removed.";
		} else {
			output = "Removal failed, command '" + param + "' does not exist!";
		}
		System.out.println(output);
		Chat.sendMessage(output);
	}
	
}