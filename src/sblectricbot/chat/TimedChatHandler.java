package sblectricbot.chat;

import java.util.Iterator;

import sblectricbot.SBlectricBot;
import sblectricbot.command.Command;
import sblectricbot.command.CommandTimer;
import sblectricbot.io.BotData;
import sblectricbot.io.BotData.TimerData;
import sblectricbot.util.Utils;

/** Timed chat commands get processed here */
public class TimedChatHandler {
	
	public TimerData timerData;
	private boolean canAccessList = true;
	
	public TimedChatHandler() {
		initTimers();
	}
	
	public void initTimers() {
		System.out.println("Loading timers from disk...");
		timerData = BotData.loadTimers();
		System.out.println("All timers loaded.");
	}
	
	/** Wait loop here */
	public void launch() {
		new Thread(new Runnable() {
			public void run() {
				while(true) { // forever waiting
					if(SBlectricBot.botCore.isConnected() && timerData.active) {
						
						// iterate through the timers
						canAccessList = false;
						for(CommandTimer t : timerData.timers) {
							Utils.whileTimeout(()->!SBlectricBot.listener.canAccess()); // avoid thread contention
							t.tryCallCommand(); // call command if possible
						}
						canAccessList = true; // avoid thread contention
					}
					
					// sleep for 1s
					try {Thread.sleep(1000);} catch(Exception e){e.printStackTrace();}
				}
		    }
		}).start();
	}
	
	/** Can the object list be safely accessed? */
	public boolean canAccess() {
		return canAccessList;
	}
	
	/** Toggles the timer functionality, and returns true if the timers are now active and false otherwise */
	public boolean toggleTimers() {
		timerData.active = !timerData.active;
		return timerData.active;
	}
	
	/** Add a timer to the timers */
	public TimedChatHandler addTimer(CommandTimer t) {
		Utils.whileTimeout(()->!canAccess()); // wait for unsafe conditions to pass
		timerData.timers.add(t);
		return this;
	}
	
	/** Remove all timers of the specified command */
	public boolean removeTimers(Command c) {
		boolean flag = false;
		Utils.whileTimeout(()->!canAccess()); // wait for the resources to be free
		Iterator<CommandTimer> i = timerData.timers.iterator();
		while(i.hasNext()) {
			Utils.whileTimeout(()->!canAccess()); // wait for the resources to be free again
			CommandTimer t = i.next();
			if(t.getCommand() == c) {
				i.remove();
				flag = true;
			}
		}
		return flag;
	}

}
