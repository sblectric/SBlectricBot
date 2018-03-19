package sblectricbot.command;

import org.pircbotx.User;

import sblectricbot.chat.Chat;
import sblectricbot.util.IMessageSerializable;
import sblectricbot.util.PermissionLevel;

/** Singleton command */
public class Command {
	
	public static final String SEPARATOR = "`.`";

	protected String name;
	protected Runnable task;
	protected PermissionLevel perms;
	protected boolean isDefault = false;

	public Command(String name, Runnable task, PermissionLevel perms) {
		this.name = name;
		this.task = task;
		this.perms = perms;
	}
	
	public Command(String name, Runnable task) {
		this.name = name;
		this.task = task;
		this.perms = PermissionLevel.DEFAULT; // default permissions
	}
	
	/** Set the command to be a default command (not user-added) */
	public Command setDefault() {
		this.isDefault = true;
		return this;
	}
	
	/** Is this command a default command (not user-added)? */
	public boolean isDefault() {
		return this.isDefault;
	}

	/** Get the name of this command */
	public String getName() {
		return name;
	}

	/** Get the task this command runs */
	public Runnable getTask() {
		return task;
	}
	
	/** Get the command permission level */
	public PermissionLevel getPermissions() {
		return perms;
	}
	
	/** Run the task */
	public void runTask(User user) {
		if(Chat.getUserPermissionLevel(user).isAtLeast(perms)) {
			task.run();
		}
	}
	
	/** Is the task serializable to a message string? */
	public boolean isTaskSerializable() {
		return task instanceof IMessageSerializable;
	}
	
	@Override
	public String toString() {
		if(isTaskSerializable()) {
			return name + SEPARATOR + ((IMessageSerializable)task).getMessage() + SEPARATOR + perms;
		} else {
			return name + SEPARATOR + "NULL" + SEPARATOR + perms;
		}
	}

}
