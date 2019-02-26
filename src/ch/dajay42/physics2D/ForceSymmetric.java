package ch.dajay42.physics2D;

import ch.dajay42.math.Point2D;

import java.util.function.ToDoubleFunction;

public abstract class ForceSymmetric<T extends PointMass> extends Force<T> {
	
	final double radius;
	
	protected ForceSymmetric(ToDoubleFunction<T> toDoubleFunction, double magnitude, double radius) {
		super(toDoubleFunction, magnitude);
		this.radius = radius;
	}


	public Point2D get(T from, T to){
		Point2D pull = new Point2D(from.getPosition(), to.getPosition());
		double r = calculateDistanceFactor(pull.length()*ModelConfig.scaleFactor);
		
		double parameterFrom = getParamOf(from);
		double parameterTo = getParamOf(to);
		
		pull.setLength(magnitude*r*parameterFrom*parameterTo);
		
		return pull;
	}

	public abstract double calculateDistanceFactor(double r);
}