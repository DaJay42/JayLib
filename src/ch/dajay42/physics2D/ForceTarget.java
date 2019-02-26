package ch.dajay42.physics2D;

import ch.dajay42.math.Point2D;

import java.util.function.ToDoubleFunction;

public class ForceTarget extends ForceGlobal<PointMass> {

	double desiredSpeed = 4;
	double relaxationTime = 3;
	
	
	public ForceTarget(ToDoubleFunction<PointMass> toDoubleFunction, double magnitude, Point2D targetLoc) {
		super(toDoubleFunction, magnitude, targetLoc);
		this.direction = targetLoc;
		this.value = null;
	}

	@Override
	public double getParamOf(PointMass pointMass) {
		return 0;
	}

	@Override
	public Point2D get(PointMass pointMass){
		//1/t *(target*direction - actual)
		Point2D force = new Point2D(pointMass.getPosition(),direction);
		force.setLength(desiredSpeed);
		force.add(pointMass.getVelocity().getInverse());
		force.stretch(1/relaxationTime);
		//mass?
		return force;
	}

}
