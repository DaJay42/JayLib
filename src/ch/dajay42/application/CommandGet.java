package ch.dajay42.application;

import ch.dajay42.application.config.ConfigUtil;
import ch.dajay42.application.config.Setting;

import java.util.Map;

public class CommandGet extends Command {
	
	private Map<String, Setting> settingMap;
	
	public CommandGet(Map<String, Setting> settingMap) {
		super("get", "[<name>]", "Prints the value of the Setting <name>, or of all Settings if none provided.");
		this.settingMap = settingMap;
	}

	@Override
	public void execute(String... args) {
		if(args.length > 0){
			String name = args[0];
			try{
				if(settingMap.containsKey(name)){
					String value = settingMap.get(name).toString();
					System.out.println(name + "=" + value);
				}else{
					System.out.println("Error: get: no such setting.");
				}
			}catch(Exception e){
				System.out.println(e.getMessage());
			}
		}else{
			System.out.println("Current Settings:");
			System.out.println(ConfigUtil.prettyPrint(settingMap));
		}
	}

}
