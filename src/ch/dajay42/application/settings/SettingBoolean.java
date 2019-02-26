package ch.dajay42.application.settings;

public class SettingBoolean extends Setting<Boolean> {

	public SettingBoolean(String name, Boolean value) {
		super(name, value);
	}

	@Override
	public Boolean parse(String value) {
		if("true".equalsIgnoreCase(value))
			return true;
		else if("false".equalsIgnoreCase(value))
			return false;
		else
			return null;
	}

	@Override
	public Class<Boolean> valueClass() {
		return Boolean.class;
	}

}
