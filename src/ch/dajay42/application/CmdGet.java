package ch.dajay42.application;

import java.io.Serializable;

import ch.dajay42.application.settings.SettingsManager;

public class CmdGet extends Command {

	SettingsManager settingsManager;
	
	public CmdGet(SettingsManager settingsManager) {
		super("get", "[<name>]", "Prints the value of the Setting <name>, or of all Settings if none provided.");
		this.settingsManager = settingsManager;
	}

	@Override
	public void execute(String... args) {
		if(args.length > 0){
			String name = args[0];
			try{
				Serializable value = settingsManager.getValueOf(name);
				System.out.println(name+"="+value);
			}catch(Exception e){
				System.out.println(e.getMessage());
			}
		}else{
			System.out.println("Current Settings:");
			System.out.println(settingsManager.toString());
		}
	}

}
