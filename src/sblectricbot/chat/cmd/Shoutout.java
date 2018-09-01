package sblectricbot.chat.cmd;

import sblectricbot.chat.Chat;
import sblectricbot.command.CommandParam.RunnableParam;

/** Shoutout command, ala YateBot */
public class Shoutout implements RunnableParam {

	@Override
	public void run() {}
	
	@Override
	public void run(String param) {
		if(!param.isEmpty()) {		
			Chat.sendMessage("Yo, check out " + param + 
					", another awesome streamer here: https://www.twitch.tv/" + param + " PartyTime");
		} else {
			Chat.sendMessage("No streamer specified!");
		}
	}

}
