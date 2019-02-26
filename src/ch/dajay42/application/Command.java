package ch.dajay42.application;

public abstract class Command{
	
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
	
}
