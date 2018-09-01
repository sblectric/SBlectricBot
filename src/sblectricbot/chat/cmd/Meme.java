package sblectricbot.chat.cmd;

import java.util.List;

import sblectricbot.SBlectricBot;
import sblectricbot.chat.Chat;
import sblectricbot.command.CommandParam.RunnableParam;

/** Gotta have a meme command */
public class Meme implements RunnableParam {
	
	private List<String> memes;
	
	public Meme(List<String> memes) {
		this.memes = memes;
	}

	@Override
	public void run() {}

	/** Output the size of the meme list */
	public void outputCount() {
		Chat.sendMessage("There are " + memes.size() + " memes available for your shitposting pleasure.");
	}
	
	/** Gets a specified meme by its index, including the index at the start */
	private String getMeme(int index) {
		return "#" + index + ". " + memes.get(index - 1);
	}
	
	/** Output the specified meme to the chat */
	private void sendMeme(int index) {
		Chat.sendMessage(getMeme(index));
	}

	@Override
	public void run(String param) {
		final int NaN = Integer.MIN_VALUE;
		int index = NaN;
		try {
			index = Integer.parseInt(param);
		} catch(Exception e) {}
		
		if(index >= 1 && index <= memes.size()) {
			sendMeme(index);
		} else {
			if(index == NaN) { // not a number, use meme search feature and fall back to random meme
				String toSend = getMeme(1 + SBlectricBot.rng.nextInt(memes.size()));
				if(!param.equals("")) {
					for(int i = 1; i <= memes.size(); i++) {
						String meme = memes.get(i - 1);
						if(meme.toLowerCase().contains(param.toLowerCase())) {
							toSend = getMeme(i);
							break;
						}
					}
				}
				Chat.sendMessage(toSend);
			} else { // normal OoB condition
				Chat.sendMessage("Specified index is out of bounds, valid range is 1 to " + memes.size() + ".");
			}
		}
	}
	
}