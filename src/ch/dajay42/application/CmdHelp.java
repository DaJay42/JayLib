package ch.dajay42.application;

import java.util.Map;

public class CmdHelp extends Command {

	private static final String rebreak = "\r\n\t";
	
	private Map<String, Command> commands;
	
	CmdHelp(Map<String, Command> commands) {
		super("help", "[<command>]", "Prints a list of available commands, or details about a single command, if one is provided.");
		this.commands = commands;
	}

	@Override
	public void execute(String... args) {
		if(args.length > 0){
			Command cmd = commands.get(args[0]);
			if(cmd != null){
				System.out.println("Command '"+cmd.name+"':");
				System.out.println(cmd.argPattern+rebreak+cmd.descText);
			}else{
				System.out.println("Command '"+args[0]+"' does not exist.");
			}
		}else{
			System.out.println("Available commands:");
			for(Command cmd : commands.values()){
				System.out.print(rebreak+cmd.argPattern);
			}
			System.out.println();
			System.out.println();
			System.out.println("Type 'help <command>' to get more information about a specific command.");
		}
	}

}
