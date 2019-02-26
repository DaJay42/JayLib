package ch.dajay42.physics2D;

import ch.dajay42.math.Point2D;

public interface PointMass {

	default double getMass(){
		return 1d/getInverseMass();
	}
	
	default void setMass(double mass){
		setInverseMass(1d/mass);
	}
	
	double getInverseMass();
	
	void setInverseMass(double inverseMass);
	
	Point2D getPosition();
	
	void setPosition(Point2D position);
	
	Point2D getVelocity();
	
	void setVelocity(Point2D velocity);
	
	default Point2D getMomentum(){
		return getVelocity().getStretched(getMass());
	}
}
