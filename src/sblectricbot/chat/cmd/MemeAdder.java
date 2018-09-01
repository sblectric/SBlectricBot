package sblectricbot.chat.cmd;

import java.util.List;

import sblectricbot.chat.Chat;
import sblectricbot.command.CommandParam.RunnableParam;

/** Add a maymay */
public class MemeAdder implements RunnableParam {
	
	private List<String> memes;
	
	public MemeAdder(List<String> memes) {
		this.memes = memes;
	}
	
	@Override
	public void run() {}

	@Override
	public void run(String param) {
		if(!param.equals("")) {
			memes.add(param);
			Chat.sendMessage("Added meme #" + memes.size() +". Type '!meme " + memes.size() + "' to check it out!");
		} else {
			Chat.sendMessage("Must specify a meme to add!");
		}
	}
}
