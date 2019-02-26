package ch.dajay42.physics2D;

import java.util.function.ToDoubleFunction;

public abstract class ForceSymGenericSquare<T extends PointMass> extends ForceSymmetric<T> {

	public ForceSymGenericSquare(ToDoubleFunction<T> toDoubleFunction, double magnitude, double radius) {
		super(toDoubleFunction, magnitude, radius);
	}

	@Override
	public double calculateDistanceFactor(double r) {
		return 1/(r*r);
	}

}
