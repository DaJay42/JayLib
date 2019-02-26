package ch.dajay42.collections;

import java.util.function.IntToDoubleFunction;

public final class FixedIntToDoubleCache implements IntToDoubleFunction {
	
	private final boolean[] valid;
	private final double[] values;
	
	public FixedIntToDoubleCache(int size){
		valid = new boolean[size];
		values = new double[size];
	}
	
	public final void put(int key, double value){
		values[key] = value;
		valid[key] = true;
	}
	
	public final double get(int key){
		return values[key];
	}
	
	public final void remove(int key){
		valid[key] = false;
	}
	
	public final boolean containsKey(int key){
		return valid[key];
	}

	@Override
	public final double applyAsDouble(int key) {
		return values[key];
	}
}
