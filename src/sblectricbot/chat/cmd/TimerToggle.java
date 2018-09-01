package sblectricbot.chat.cmd;

import sblectricbot.SBlectricBot;
import sblectricbot.chat.Chat;

/** Toggle timed commands */
public class TimerToggle implements Runnable {
	@Override
	public void run() {
		String status = SBlectricBot.timers.toggleTimers() ? "enabled" : "disabled";
		String output = "Timed commands are now " + status + ".";
		Chat.sendMessage(output);
	}
}