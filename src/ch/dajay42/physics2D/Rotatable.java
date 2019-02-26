package ch.dajay42.physics2D;

public interface Rotatable {

	double getOrientation();
	
	void setOrientation(double orientation);
	
	double getAngularVelocity();
	
	void setAngularVelocity(double angularVelocity);
	
	default double getMomentOfInertia(){
		return 1d/getInverseMomentOfInertia();
	}
	
	default void setMomentOfInertia(double momentOfInertia){
		setInverseMomentOfInertia(1d/momentOfInertia);
	}
	
	double getInverseMomentOfInertia();
	
	void setInverseMomentOfInertia(double inverseMomentOfInertia);

}
