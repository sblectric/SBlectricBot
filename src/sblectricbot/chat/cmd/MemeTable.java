package sblectricbot.chat.cmd;

import java.util.LinkedList;
import java.util.List;

import sblectricbot.chat.Chat;

/** A command that lists the memes for a user (weblink) */
public class MemeTable extends WeblinkTable {

	private List<String> memes;
	private String filename = LOCAL_PATH + "memes.html";

	public MemeTable(List<String> memes) {
		this.memes = memes;
	}

	@Override
	public void run() {

		List<String> tableLeft = new LinkedList<String>();
		List<String> tableRight = new LinkedList<String>();

		tableLeft.add("<b>Index</b>");
		tableRight.add("<b>Meme</b>");
		
		int i = 0;

		for(String m : memes) {
			tableLeft.add("#" + (++i));
			tableRight.add(m);
		}

		boolean success = this.writeTable(filename, tableLeft, tableRight);

		if (success) {
			Chat.sendMessage("Meme list: " + REMOTE_PATH + "memes.html");
		} else {
			Chat.sendMessage("HTML writing of file '" + filename + "' failed. Check your filepath and permissions!");
		}

	}

}
