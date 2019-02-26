package ch.dajay42.physics2D;

import ch.dajay42.math.Point2D;

public class ForceGlobalGravity extends ForceGlobal<PointMass> {

	public ForceGlobalGravity(double magnitude, Point2D direction) {
		super(PointMass::getMass, magnitude, direction);
	}
	@Override
	public double getParamOf(PointMass pointMass) {
		return pointMass.getMass();
	}
}
