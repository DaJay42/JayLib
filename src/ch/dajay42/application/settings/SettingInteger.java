package ch.dajay42.application.settings;

public class SettingInteger extends Setting<Integer> {

	public SettingInteger(String name, Integer value) {
		super(name, value);
	}

	@Override
	public Integer parse(String value) {
		try{
			return Integer.valueOf(value);
		}catch(Exception e){
			return null;
		}
	}

	@Override
	public Class<Integer> valueClass() {
		return Integer.class;
	}
	
}
