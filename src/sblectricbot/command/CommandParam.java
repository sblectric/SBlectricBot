package sblectricbot.command;

import org.pircbotx.User;

import sblectricbot.chat.Chat;
import sblectricbot.util.PermissionLevel;

/** A command with parameter support */
public class CommandParam extends Command {
	
	protected boolean useParams = true;
	
	public static interface RunnableParam extends Runnable {
		public void run(String param);
	}

	public CommandParam(String name, Runnable task, PermissionLevel perms) {
		super(name, task, perms);
	}
	
	public CommandParam(String name, Runnable task) {
		super(name, task);
	}
	
	/** Disable parameter use */
	public CommandParam setNoParams() {
		useParams = false;
		return this;
	}
	
	/** Enable parameter use if it had been disabled previously */
	public CommandParam setUseParams() {
		useParams = true;
		return this;
	}
	
	/** Run the task with parameters, if not disabled */
	public void runTask(User user, String param) {
		if(useParams && getTask() instanceof RunnableParam) {
			if(Chat.getUserPermissionLevel(user).isAtLeast(getPermissions())) {
				((RunnableParam)getTask()).run(param);
			}
		} else {
			runTask(user);
		}
	}

}
