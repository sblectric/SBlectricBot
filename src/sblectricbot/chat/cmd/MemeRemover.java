package sblectricbot.chat.cmd;

import java.util.List;

import sblectricbot.chat.Chat;
import sblectricbot.command.CommandParam.RunnableParam;

/** Remove a maymay */
public class MemeRemover implements RunnableParam {
	
	private List<String> memes;
	
	public MemeRemover(List<String> memes) {
		this.memes = memes;
	}
	
	@Override
	public void run() {}

	@Override
	public void run(String param) {
		int index = -1;
		try {
			index = Integer.parseInt(param);
		} catch(Exception e) {}
		
		if(index >= 1 && index < memes.size() + 1) {
			Chat.sendMessage("Meme #" + index + " (" + memes.get(index - 1) + ") removed successfully.");
			memes.remove(index - 1);
		} else {
			Chat.sendMessage("Must specify a meme to remove by its valid numerical index!");
		}
	}
}