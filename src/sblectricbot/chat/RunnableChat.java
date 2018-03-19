package sblectricbot.chat;

import sblectricbot.util.IMessageSerializable;

/** Display a chat message as Runnable */
public class RunnableChat implements Runnable, IMessageSerializable {
	
	private String channel;
	private String message;
	
	public RunnableChat(String message) {
		this.message = message;
	}
	
	public RunnableChat(String channel, String message) {
		this.channel = channel;
		this.message = message;
	}

	@Override
	public void run() {
		if(channel == null) {
			Chat.sendMessage(message);
		} else {
			Chat.sendMessage(channel, message);
		}
	}

	@Override
	public String getMessage() {
		return message;
	}

}
