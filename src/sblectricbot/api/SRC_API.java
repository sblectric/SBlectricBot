package sblectricbot.api;

import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import sblectricbot.util.Utils;

/** speedrun.com API integration class */
public class SRC_API {
	
	/** Two types of valid calls, WR and PB */
	public static enum SRCType {
		WR, PB;
		
		public boolean isWR() {return this == WR;}
	}
	
	/** Get SRC data from web request */
	private static JSONObject getSRCData(String user, String gameID, String category) {
		SRCType type = user == null ? SRCType.WR : SRCType.PB;
		String path;
		if(type.isWR()) {
			path = "https://www.speedrun.com/api_records.php?game=" + gameID;
		} else {
			path ="https://www.speedrun.com/api_records.php?game=" + gameID + "&user=" + user;
		}
		
		return Utils.getJSONFromWeb(path);
	}
	
	/** Get the specified time / WR of a runner / game on speedrun.com */
	public static String getTime(String user, String gameID, String category) {
		JSONObject json = getSRCData(user, gameID, category);
		String gameName = getGameName(gameID, json);
		String text = "An error occurred.";
		SRCType type = user == null ? SRCType.WR : SRCType.PB;
		
		try {
			JSONObject game = json.getJSONObject(gameName);
			JSONObject cat = Utils.getJSONObjectAnyCase(game, category);
			String catName = Utils.getLastJSONObjectName();
			
			String id = cat.getString("id");
			String player = cat.getString("player");
			double time = cat.getDouble("time");
			int place = -1; if(!type.isWR()) place = Integer.parseInt(cat.getString("place"));
			
			long hours = TimeUnit.SECONDS.toHours((long)time);
			long minutes = TimeUnit.SECONDS.toMinutes((long)time) - hours * 60;
			long seconds = (long)time - hours * 3600 - minutes * 60;
			long millis = (long) ((time - (long)time) * 1000D);
			
			String timeFormatted = "";
			timeFormatted += (hours == 0) ? "" : hours + "h ";
			timeFormatted += (hours == 0 && minutes == 0) ? "" : ((minutes < 10) ? "0" : "") + minutes + "m ";
			timeFormatted += ((seconds < 10) ? "0" : "") + seconds + "s";
			timeFormatted += (millis == 0) ? "" : " " + millis + "ms";
			
			if(type.isWR()) {
				text = "The world record for ";
			} else {
				text = player + "'s personal best for ";
			}
			text += gameName + " - " + catName + " is " + timeFormatted + 
					(type.isWR() ? (" by " + player) : " (" + place + Utils.getOrdinal(place) + " place)") + 
					". Run page: https://www.speedrun.com/run/" + id;
			
		} catch(JSONException e) {
			e.printStackTrace();
		}
		
		return text;
	}
	
	/** Get the long name of a game */
	private static String getGameName(String gameID, JSONObject SRCData) {
		String json = SRCData.toString();
		
		String jsonFirstQuoteless = json.substring(2);
		int secondQuoteIndex = jsonFirstQuoteless.indexOf("\"");		
		String gameName = jsonFirstQuoteless.substring(0, secondQuoteIndex);
		
		return gameName;
	}

}
