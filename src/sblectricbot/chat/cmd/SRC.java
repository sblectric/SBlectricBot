package sblectricbot.chat.cmd;

import sblectricbot.SBlectricBot;
import sblectricbot.api.SRC_API;
import sblectricbot.api.SRC_API.SRCType;
import sblectricbot.chat.Chat;
import sblectricbot.command.CommandParam.RunnableParam;

/** Adds a speedrun.com command */
public class SRC implements RunnableParam {
	
	private SRCType srcType;
	
	public SRC(SRCType srcType) {
		this.srcType = srcType;
	}

	@Override
	public void run() {}
	
	@Override
	public void run(String param) {
		
		// all lowercase
		param = param.toLowerCase();
		
		// parameter order: user, game, category
		String params[] = param.split(" ");
		
		String paramUser = null;
		String paramGame = null;
		String paramCategory = null;
		
		try {
			if(srcType.isWR()) { // wr
				if(params.length < 2) throw new SRCException(2);
				paramUser = null;
				paramGame = params[0];
				paramCategory = "";
				for(int i = 1; i < params.length; i++) paramCategory += params[i] + " ";
				paramCategory = paramCategory.substring(0, paramCategory.length() - 1);
			} else { // pb
				if(params.length < 2) throw new SRCException(2);
				int gameParamNumber;
				
				if(params.length < 3) {
					paramUser = SBlectricBot.channelName.substring(1);
					gameParamNumber = 0;
				} else {
					paramUser = params[0];
					gameParamNumber = 1;
				}
				paramGame = params[gameParamNumber];
				paramCategory = "";
				for(int i = gameParamNumber + 1; i < params.length; i++) paramCategory += params[i] + " ";
				paramCategory = paramCategory.substring(0, paramCategory.length() - 1);
			}
		} catch(SRCException e) {
			Chat.sendMessage(e.getMessage());
			return;
		}
		
		// send the message
		Chat.sendMessage(SRC_API.getTime(paramUser, paramGame, paramCategory));
	}
	
	/** SRC parameter exception */
	private static class SRCException extends Exception {
		
		private static final long serialVersionUID = 69;

		private SRCException(int minParameters) {
			super("The minimum number of parameters for this command is " + minParameters + "!");
		}
		
	}

}
