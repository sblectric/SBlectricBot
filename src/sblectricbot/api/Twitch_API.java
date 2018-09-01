package sblectricbot.api;

import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import sblectricbot.SBlectricBot;

/** Twitch API calls and such for uptime and more */
public class Twitch_API {
	
	/** Get the client ID for the bot from the OAuth token */
	public static String getClientID(String auth) {
		
		String id = null;
		
		try {
			URI uri = new URI("https://id.twitch.tv/oauth2/validate");
			
			URLConnection conn = uri.toURL().openConnection();
			conn.setRequestProperty("Authorization", "OAuth " + auth.substring(6));
			InputStream input = conn.getInputStream();
			
			JSONTokener tokener = new JSONTokener(input);
			JSONObject json = new JSONObject(tokener);
			
			id = json.getString("client_id");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return id;
	}
	
	/** Get the stream data */
	public static JSONObject getStreamData() {
		return getStreamData(SBlectricBot.channelName.substring(1));
	}
	
	/** Get the stream data */
	public static JSONObject getStreamData(String channel) {
		try {
			URI uri = new URI("https://api.twitch.tv/helix/streams?user_login=" + channel);
			
			URLConnection conn = uri.toURL().openConnection();
			conn.setRequestProperty("Client-ID", SBlectricBot.clientID);
			InputStream input = conn.getInputStream();
			
			JSONTokener tokener = new JSONTokener(input);
			JSONObject json = new JSONObject(tokener);
			
			JSONArray array = json.getJSONArray("data");
			JSONObject subData = array.getJSONObject(0);
			
			return subData;
		} catch(Exception e) {
			e.printStackTrace();
			return new JSONObject();
		}
	}

}
