package ch.dajay42.util;

import java.util.function.Supplier;

public class Lazy<T> implements Supplier<T>{
	
	private T value = null;
	
	private final Supplier<T> supplier;
	
	public Lazy(Supplier<T> supplier){
		this.supplier = supplier;
	}
	
	public T get(){
		if(value == null){
			value = supplier.get();
		}
		return value;
	}
}
