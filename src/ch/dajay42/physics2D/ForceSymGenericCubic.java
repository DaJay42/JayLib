package ch.dajay42.physics2D;

import java.util.function.ToDoubleFunction;

public abstract class ForceSymGenericCubic<T extends PointMass> extends ForceSymmetric<T> {

	public ForceSymGenericCubic(ToDoubleFunction<T> toDoubleFunction, double magnitude, double radius) {
		super(toDoubleFunction, magnitude, radius);
	}
	
	@Override
	public double calculateDistanceFactor(double r) {
		return 1/(r*r*r);
	}

}
