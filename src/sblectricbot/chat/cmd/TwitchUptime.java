package sblectricbot.chat.cmd;

import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import sblectricbot.api.Twitch_API;
import sblectricbot.chat.Chat;
import sblectricbot.util.Utils;

/** Get the uptime of the stream */
public class TwitchUptime implements Runnable {
	@Override
	public void run() {
		try {
			JSONObject channelData = Twitch_API.getStreamData();
			
			String utc = channelData.getString("started_at");
			long startTime = Utils.UTCtoMillis(utc);
			if(startTime < 0) throw new Exception();
			
			long time = System.currentTimeMillis() - startTime;
			
			long hours = TimeUnit.MILLISECONDS.toHours(time);
			long minutes = TimeUnit.MILLISECONDS.toMinutes(time) - hours * 60;
			
			Chat.sendMessage("Stream has been up for " + 
						(hours > 0 ? hours + " hours, " : "") + minutes + " minutes.");
		
		} catch (Exception e) {
			Chat.sendMessage("Stream is offline.");
		}
	}

}
