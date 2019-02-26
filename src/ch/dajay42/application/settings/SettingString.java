package ch.dajay42.application.settings;

public class SettingString extends Setting<String> {

	
	public SettingString(String name, String value) {
		super(name, value);
	}

	@Override
	public String parse(String value) {
		return value;
	}

	@Override
	public Class<String> valueClass() {
		return String.class;
	}


}
