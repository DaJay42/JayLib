package ch.dajay42.physics2D;

import java.util.function.ToDoubleFunction;

public abstract class Force<T extends PointMass> {

	final double magnitude;
	protected final ToDoubleFunction<T> toDoubleFunction;
	
	protected Force(ToDoubleFunction<T> toDoubleFunction, double magnitude){
		this.toDoubleFunction = toDoubleFunction;
		this.magnitude = magnitude;
	}
	

	public double getParamOf(T pointMass){
		return toDoubleFunction.applyAsDouble(pointMass);
	}
}
