package sblectricbot.chat;

import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.pircbotx.User;

import sblectricbot.SBlectricBot;
import sblectricbot.util.PermissionLevel;

/** Useful functions for chat */
public class Chat {
	
	private static final String defChannel = SBlectricBot.channelName;
	
	/** Send a chat message to the specified channel */
	public static void sendMessage(String channel, String message) {
        SBlectricBot.botCore.sendIRC().message(channel, message);
	}
	
	/** Send a chat message to the default channel */
	public static void sendMessage(String message) {
		sendMessage(defChannel, message);
	}
	
	private static Map<String, PermissionLevel> userMap = new HashMap<String, PermissionLevel>();
	private static Map<String, Long> resetTimerMap = new HashMap<String, Long>();
	private static final long periodMillis = 120000; // every 120s minimum
	
	/** Get the specified user's permissions, with caching for speed, as web access is slow */
	public static PermissionLevel getUserPermissionLevel(String channel, User user) {
		if(user != null) {
			String key = channel + ":" + user.getNick();
			
			if(!userMap.containsKey(key)) {
				
				// see if the user is the channel owner
				if(isBroadcaster(channel, user)) return addPermissionToCache(key, PermissionLevel.BROADCASTER);
				
				// see if the user is a mod
				try {
					URI uri = new URI("http://tmi.twitch.tv/group/user/" + channel.substring(1) + "/chatters");
					InputStream input = uri.toURL().openStream();
					JSONTokener tokener = new JSONTokener(input);
					JSONObject json = new JSONObject(tokener);
					JSONObject chatters = json.getJSONObject("chatters");
					JSONArray mods = chatters.getJSONArray("moderators");
					for(int i = 0; i < mods.length(); i++) {
						if(user.getNick().equals((String)mods.get(i))) {
							return addPermissionToCache(key, PermissionLevel.MODERATOR);
						}
					}
					input.close();
				} catch(Exception e) {
					e.printStackTrace();
				}
				
			} else {
				return updatePermissionCache(key, userMap.get(key));
			}
			
			// user is just a viewer
			return addPermissionToCache(key, PermissionLevel.VIEWER);
		}
		
		// null user default
		return PermissionLevel.DEFAULT;
	}
	
	/** Get the specified user's permissions */
	public static PermissionLevel getUserPermissionLevel(User user) {
		return getUserPermissionLevel(defChannel, user);
	}
	
	/** Quick way to query for the broadcaster */
	public static boolean isBroadcaster(String channel, User user) {
		if(user == null) return false;
		return channel.substring(1).equals(user.getNick());
	}
	
	/** Quick way to query for the broadcaster */
	public static boolean isBroadcaster(User user) {
		return isBroadcaster(defChannel, user);
	}
	
	/** Adds the specified permission to the cache and returns that permission */
	private static PermissionLevel addPermissionToCache(String key, PermissionLevel level) {
		System.out.println("User " + key + " added to the cache.");
		resetTimerMap.put(key, System.currentTimeMillis());
		userMap.put(key, level);
		return level;
	}
	
	/** Remove users from the cache every so often */
	private static PermissionLevel updatePermissionCache(String key, PermissionLevel level) {
		if(System.currentTimeMillis() > resetTimerMap.get(key) + periodMillis) {
			System.out.println("User " + key + " removed from the cache.");
			userMap.remove(key);
		}
		return level;
	}

}
