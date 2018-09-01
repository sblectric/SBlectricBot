package sblectricbot.chat.cmd;

import sblectricbot.SBlectricBot;
import sblectricbot.chat.Chat;
import sblectricbot.command.Command;
import sblectricbot.command.CommandList;
import sblectricbot.command.CommandParam.RunnableParam;
import sblectricbot.util.Utils;

/** Remove command timers matching a command */
public class TimerRemover implements RunnableParam {
	
	private CommandList list;
	
	public TimerRemover(CommandList list) {
		this.list = list;
	}
	
	@Override
	public void run() {}
	
	@Override
	public void run(String param) {
		Utils.whileTimeout(()->!SBlectricBot.timers.canAccess()); // wait for unsafe conditions to pass
		Command c = list.getCommandByName(param);
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