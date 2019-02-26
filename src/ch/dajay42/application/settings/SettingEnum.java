package ch.dajay42.application.settings;

public class SettingEnum<T extends Enum<T>> extends Setting<T> {

	Class<T> myClass;
	
	public SettingEnum(String name, T value) {
		super(name, value);
		myClass = value.getDeclaringClass();
	}

	@Override
	public T parse(String value) {
		return Enum.valueOf(myClass, name);
	}

	@Override
	public Class<T> valueClass() {
		return myClass;
	}

}
