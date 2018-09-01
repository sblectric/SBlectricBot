package sblectricbot.chat.cmd;

import java.util.List;

import sblectricbot.chat.Chat;
import sblectricbot.io.TxtFileIO;

/** Reload the mentions file */
public class MentionReloader implements Runnable {
	
	private List<String> mentions;
	private String mentionsFile;
	
	public MentionReloader(List<String> mentions, String mentionsFile) {
		this.mentions = mentions;
		this.mentionsFile = mentionsFile;
	}
	
	@Override
	public void run() {
		List<String> mentionsTemp = new TxtFileIO().readAllLines(mentionsFile);
		mentions.clear();
		for(String s : mentionsTemp) mentions.add(s);
		String output = "Reloaded mentions successfully.";
		System.out.println(output);
		Chat.sendMessage(output);
	}
}