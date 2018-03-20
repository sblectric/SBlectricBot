package sblectricbot.console;

import java.util.Scanner;

import sblectricbot.SBlectricBot;
import sblectricbot.command.Command;
import sblectricbot.command.CommandList;
import sblectricbot.command.CommandTimer;
import sblectricbot.io.BotData;
import sblectricbot.ref.RefStrings;
import sblectricbot.util.Utils;

/** Handle console input */
public class ConsoleInputHandler {
	
	public CommandList commands;
	
	public ConsoleInputHandler() {
		// add the console commands
		commands = new CommandList().addCommand(new Command("stop", ()->System.exit(0)).setDefault());
		commands.addCommand(new Command("version", ()->System.out.println(RefStrings.NAMEV)));
		commands.addCommand(new Command("reload", ()->{
							SBlectricBot.listener.initCommands();
							SBlectricBot.timers.initTimers();
							}).setDefault());
		commands.addCommand(new Command("commands", ()->{
							for(Command c : SBlectricBot.getChatCommands().getList()) {
								System.out.println(c);
							}}).setDefault());
		commands.addCommand(new Command("timers", ()->{
							for(CommandTimer t: SBlectricBot.getTimerData().timers) {
								System.out.println(t);
							}}).setDefault());
		commands.addCommand(new Command("save", ()->{
								BotData.save(SBlectricBot.getChatCommands().getList(), SBlectricBot.getTimerData());
								SBlectricBot.listener.onSave();
							}).setDefault());
	}
	
	/** Scanner method */
	@SuppressWarnings("resource")
	public void launch() {
		new Thread(new Runnable() {
			public void run() {
				// now scan the console for commands
				Scanner reader = new Scanner(System.in);
				System.out.println("Console ready.");
				while(true) {
					String s = "";
					try {
						s = reader.nextLine(); // wait for the next input
					} catch(Exception e) {}
					Utils.whileTimeout(()->!(SBlectricBot.timers.canAccess() && SBlectricBot.listener.canAccess())); // avoid thread contention
					if(s != "" && !commands.runCommandByName(null, s, "")) System.out.println("'" + s + "' is not a valid command!");
				}
		    }
		}).start();
	}

}
