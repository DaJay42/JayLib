package ch.dajay42.application.settings;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;

public interface AutoConfig extends SettingsManager {
	
	@Override
	public default Serializable getValueOf(String name) {
		try {
			return (Serializable) getClass().getField(name).get(this);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public default <T extends Serializable> void setValueOf(String name, T value) {
		try {
			getClass().getField(name).set(this, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public default Class<? extends Serializable> getTypeOf(String name) {
		try {
			return (Class<? extends Serializable>) getClass().getField(name).getType();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public default void registerSetting(Setting<?> setting) {
		throw new UnsupportedOperationException();
	}

	@Override
	public default void registerSettings(Setting<?>... settings) {
		throw new UnsupportedOperationException();
	}

	@Override
	public default boolean hasSetting(String name) {
		try {
			getClass().getField(name);
			return true;
		} catch (NoSuchFieldException | SecurityException e) {
			return false;
		}
	}


	public default String asString() {
		StringBuilder builder = new StringBuilder();
		Field[] set = getClass().getFields();
		Arrays.sort(set);

		builder.append("[Settings]\r\n");
		
		for(Field setting : set){
			builder.append(setting.getName());
			builder.append('=');
			try {
				builder.append(setting.get(this));
			} catch (Exception e) {
				e.printStackTrace();
				builder.append((String)null);
			}
			builder.append("\r\n");
		}
		
		return builder.toString();
	}
}
