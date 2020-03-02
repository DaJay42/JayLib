package ch.dajay42.application;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class SimpleCLI implements Runnable{

	final Map<String, Command> commands = new HashMap<>();
	
	public String prompt = "?: ";
	
	public String greeting = "Starting SimpleCLI 1.0";
	
	public String cmdNotFound = "Unknown command '%s'. Type 'help' for a list of commands.";

	public SimpleCLI() {
		registerCommmand(new CmdHelp(commands));
		registerCommmand(Command.EXIT);
	}

	public SimpleCLI(Command... cmds) {
		this();
		for(Command cmd : cmds) registerCommmand(cmd);
	}

	public SimpleCLI(Collection<Command> cmds) {
		this();
		for(Command cmd : cmds) registerCommmand(cmd);
	}
	
	public void registerCommmand(Command cmd){
		commands.put(cmd.name, cmd);
	}
	
	public void registerCommmands(Collection<Command> cmds){
		for(Command cmd : cmds) registerCommmand(cmd);
	}
	
	public void registerCommmands(Command... cmds){
		for(Command cmd : cmds) registerCommmand(cmd);
	}
	
	
	public void parseProgramArgs(String[] args) {
		if(args.length > 0){
			String[] cmds = String.join(" ", args).split("--");
			for(String string : cmds){
				parseCommand(string.split(" "));
			}
		}
	}
	
	public void readContinuously(){
		try(Scanner in = new Scanner(System.in)){
			while(true){
				System.err.flush();
				System.out.flush();
				System.out.println();
				System.out.print(prompt);
				String s = in.nextLine();
				parseCommand(s.split(" "));
			}
		}catch(ExitException e){
			System.out.println("Session Terminated.");
		}
	}
	
	public boolean readSingleCommand(){
		try(Scanner in = new Scanner(System.in)){
			System.err.flush();
			System.out.flush();
			System.out.print(prompt);
			String s = in.nextLine();
			parseCommand(s.split(" "));
			
		}catch(ExitException e){
			return false;
		}
		return true;
	}
	
	public void parseCommand(String...strings){
		if(strings != null && strings.length > 0){
			String[] args = Arrays.copyOfRange(strings, 1, strings.length);
			Command cmd = commands.get(strings[0]);
			if(cmd != null)
				cmd.execute(args);
			else
				System.out.println(String.format(cmdNotFound, strings[0]));
		}
	}


	@Override
	public void run() {
		greet();
		readContinuously();
	}
	
	public void greet(){
		System.out.println(greeting);
	}
}
