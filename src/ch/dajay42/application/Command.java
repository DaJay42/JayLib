package ch.dajay42.application;

import java.util.function.Consumer;

public abstract class Command implements Consumer<String[]>{
	
	public static final Command EXIT = new Command("exit", "", "Ends the session.") {
		@Override
		public void execute(String... args) {
			System.out.println("Ending session.");
			throw new ExitException();
		}
	};
	
	public final String name;
	
	public final String argPattern;
	
	public final String descText;
	
	public Command(String name, String argPattern, String descText) {
		this.name = name;
		this.argPattern = name + " " + argPattern;
		this.descText = descText;
	}
	
	public abstract void execute(String... args);
	
	@Override
	public void accept(String[] strings){
		execute(strings);
	}
	
	public static Command create(String name, String argPattern, String descText, Consumer<String[]> consumer){
		return new Command(name, argPattern, descText){
			@Override
			public void execute(String... args){
				consumer.accept(args);
			}
		};
	}
}
