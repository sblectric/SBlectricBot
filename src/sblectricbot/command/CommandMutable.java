package sblectricbot.command;

import sblectricbot.util.PermissionLevel;

/** A command that can have its properties changed after creation */
public class CommandMutable extends CommandParam {

	public CommandMutable(String name, Runnable task, PermissionLevel perms) {
		super(name, task, perms);
	}
	
	public CommandMutable(String name, Runnable task) {
		super(name, task);
	}
	
	public CommandMutable(String name) {
		this(name, new BlankRunnable());
	}
	
	/** Set the name */
	public CommandMutable setName(String name) {
		this.name = name;
		return this;
	}
	
	/** Set the task */
	public CommandMutable setTask(Runnable task) {
		this.task = task;
		return this;
	}
	
	/** Set the task */
	public CommandMutable setPermissions(PermissionLevel perms) {
		this.perms = perms;
		return this;
	}
	
	private static class BlankRunnable implements RunnableParam {
		@Override public void run() {}
		@Override public void run(String s) {}
	}

}
