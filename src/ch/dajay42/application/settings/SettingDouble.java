package ch.dajay42.application.settings;

public class SettingDouble extends Setting<Double> {

	public SettingDouble(String name, Double value) {
		super(name, value);
	}

	@Override
	public Double parse(String value) {
		try{
			return Double.valueOf(value);
		}catch(Exception e){
			return null;
		}
	}

	@Override
	public Class<Double> valueClass() {
		return Double.class;
	}

}
