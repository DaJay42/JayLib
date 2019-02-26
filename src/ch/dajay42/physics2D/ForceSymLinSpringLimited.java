package ch.dajay42.physics2D;

import java.util.function.ToDoubleFunction;

public class ForceSymLinSpringLimited<T extends PointMass> extends ForceSymmetric<T> {

	final double limit;
	
	public ForceSymLinSpringLimited(ToDoubleFunction<T> toDoubleFunction, double magnitude, double radius, double limit) {
		super(toDoubleFunction, magnitude, radius);
		this.limit = limit;
	}

	@Override
	public double calculateDistanceFactor(double r){
		return (r > limit) ? 0d : (r - radius);
	}
}
