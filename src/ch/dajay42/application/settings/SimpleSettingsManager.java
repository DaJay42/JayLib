package ch.dajay42.application.settings;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;

public class SimpleSettingsManager implements SettingsManager {
	
	public String filename;
	
	final HashMap<String, Setting<? extends Serializable>> settings = new HashMap<>();

	public SimpleSettingsManager(String filename){
		this.filename = filename;
	}

	@Override
	public boolean hasSetting(String name) {
		return settings.containsKey(name);
	}

	@Override
	public void registerSetting(Setting<?> setting) {
		settings.put(setting.name, setting);
	}
	
	@Override
	public void registerSettings(Setting<?>... settings) {
		for(Setting<?> setting : settings)
			this.settings.put(setting.name, setting);
	}
	
	@Override
	public Serializable getValueOf(String name) {
		if(!hasSetting(name)) throw new IllegalArgumentException("No setting '"+name+"' exists.");
		return settings.get(name).getValue();
	}
	
	@Override
	public <T extends Serializable> void setValueOf(String name, T value){
		if(!hasSetting(name)) throw new IllegalArgumentException("No setting '"+name+"' exists.");
		if(settings.get(name).valueClass().isInstance(value)){
			@SuppressWarnings("unchecked")
			Setting<T> setting = (Setting<T>) settings.get(name);
			setting.setValue(value);
		}
	}


	@Override
	public Class<? extends Serializable> getTypeOf(String name) {
		if(!hasSetting(name)) throw new IllegalArgumentException("No setting '"+name+"' exists.");
		return settings.get(name).valueClass();
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		Setting<?>[] set = settings.values().toArray(new Setting[settings.values().size()]);
		Arrays.sort(set);

		builder.append("[Settings]\r\n");
		
		for(Setting<?> setting : set){
			builder.append(setting.name);
			builder.append('=');
			builder.append(setting.value);
			builder.append("\r\n");
		}
		
		return builder.toString();
	}
	@Override
	public String fileName() {
		return filename;
	}
	
}
