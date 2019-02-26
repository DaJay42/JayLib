package ch.dajay42.application;

import ch.dajay42.application.settings.SettingsManager;

public class CmdSet extends Command{
	
	SettingsManager settingsManager;

	public CmdSet(SettingsManager settingsManager) {
		super("set", "<name> [to] <value>", "Sets the value of the Setting <name> to <value>.");
		if(settingsManager == null)
			throw new IllegalArgumentException();
		this.settingsManager = settingsManager;
	}

	@Override
	public void execute(String... args) {
		if(args.length > 1){
			String name = (String) args[0];
			try{
				String value;
				if(args.length > 2 && "to".equals(args[1]))
					value = args[2];
				else
					value = args[1];
				settingsManager.setValueOf(name, value);
				System.out.println("Set '"+name+"' to '"+value+"'.");
			}catch(Exception e){
				System.out.println(e.getMessage());
			}
		}else
			System.out.println("Error: set: not enough arguments.");
		
	}

}
