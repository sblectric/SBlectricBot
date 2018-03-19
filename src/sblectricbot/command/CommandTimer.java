package sblectricbot.command;

import sblectricbot.SBlectricBot;
import sblectricbot.util.Utils;

/** A timed command */
public class CommandTimer {
	
	private Command command;
	private int minSeconds;
	private int minChatLines;
	private int lastSecondCount;
	private int lastChatLineCount;
	
	public CommandTimer(Command command, int minSeconds, int minChatLines) {
		this.command = command;
		this.minSeconds = minSeconds;
		this.minChatLines = minChatLines;
		reset();
	}
	
	/** Set the last second and line counters to the current time and line count */
	private void reset() {
		lastChatLineCount = SBlectricBot.listener.chatLines;
		lastSecondCount = Utils.systemTimeSeconds();
	}
	
	/** Get the command to be called */
	public Command getCommand() {
		return command;
	}
	
	/** Get the minimum seconds between calls */
	public int getMinSecondsElapsed() {
		return minSeconds;
	}
	
	/** Get the minimum chat lines between calls */
	public int getMinChatLinesPassed() {
		return minChatLines;
	}
	
	/** Is the command valid to call? */
	public boolean canCallCommand() {
		return (SBlectricBot.listener.chatLines >= lastChatLineCount + minChatLines) && 
				(Utils.systemTimeSeconds() >= lastSecondCount + minSeconds);
	}
	
	/** Call the command */
	public boolean tryCallCommand() {
		if(canCallCommand()) {
			reset();
			command.getTask().run(); // bypass command security
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return command.getName() + Command.SEPARATOR + minSeconds + Command.SEPARATOR + minChatLines;
	}

}
