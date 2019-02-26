package ch.dajay42.application.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class Setting<T extends Serializable> implements Comparable<Setting<?>>{
	
	@SuppressWarnings("serial")
	static class BadNameException extends IllegalArgumentException{
		public BadNameException(String name) {
			super(String.format("Not a valid name: '%s'", name));
		}
		public BadNameException(String name, char c) {
			super(String.format("Not a valid name: '%s' - contains invalid character '%s'.", name, c));
		}
	}
	
	@SuppressWarnings("serial")
	static class TypeMismatchException extends IllegalArgumentException{
		public TypeMismatchException(Serializable from, Class<?> to) {
			super(String.format("Cannot cast '%s' to %s.", from, to.toString().toLowerCase()));
		}
	}
	
	public abstract T parse(String value);
	
	public abstract Class<T> valueClass();
	
	public final String name;
	
	T value;
	
	List<Consumer<T>> listeners;

	public Setting(String name, T value){
		checkName(name);
		this.name = name;
		this.value = value;
		this.listeners = new ArrayList<>();
	}

	public T getValue() {
		return value;
	}
	
	public void setValue(T value){
		this.value = value;
		for(Consumer<T> consumer : listeners)
			consumer.accept(value);
	}

	public void parseAndSetValue(String value) {
		T val = parse(value);
		if(val == null) throw new TypeMismatchException(value, valueClass());
		setValue(val);
	}
	
	public void registerChangeListener(Consumer<T> listener){
		listeners.add(listener);
	}
	

	@Override
	public int compareTo(Setting<?> other) {
		return this.name.compareToIgnoreCase(other.name);
	}
	

	public static void checkName(String name){
		if(name == null || name.isEmpty())
			throw new BadNameException(name);
		
		for(Character c : name.toCharArray())
			if(!Character.isJavaIdentifierPart(c))
				throw new BadNameException(name, c);
		
	}
	
	
}
