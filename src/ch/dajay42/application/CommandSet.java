package ch.dajay42.application;

import ch.dajay42.application.config.Setting;

import java.util.Map;

public class CommandSet extends Command{
	
	private Map<String, Setting> settingMap;

	public CommandSet(Map<String, Setting> settingMap) {
		super("set", "<name> [to] <value>", "Sets the value of the Setting <name> to <value>.");
		if(settingMap == null)
			throw new IllegalArgumentException();
		this.settingMap = settingMap;
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
				if(settingMap.containsKey(name)){
					settingMap.get(name).parse(value);
					System.out.println("Set '" + name + "' to '" + settingMap.get(name).toString() + "'.");
				}else {
					System.out.println("Error: set: no such setting.");
				}
			}catch(Exception e){
				System.out.println(e.getMessage());
			}
		}else
			System.out.println("Error: set: not enough arguments.");
		
	}

}
