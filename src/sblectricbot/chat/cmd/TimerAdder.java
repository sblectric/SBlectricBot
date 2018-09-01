package sblectricbot.chat.cmd;

import sblectricbot.SBlectricBot;
import sblectricbot.chat.Chat;
import sblectricbot.command.Command;
import sblectricbot.command.CommandList;
import sblectricbot.command.CommandParam.RunnableParam;
import sblectricbot.command.CommandTimer;
import sblectricbot.util.Utils;

/** Add a command to a timer */
public class TimerAdder implements RunnableParam {
	
	private int minSeconds;
	private int minChatLines;
	private CommandList list;
	
	public TimerAdder(int minSeconds, int minChatLines, CommandList list) {
		this.minSeconds = minSeconds;
		this.minChatLines = minChatLines;
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