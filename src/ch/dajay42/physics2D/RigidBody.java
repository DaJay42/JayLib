package ch.dajay42.physics2D;

public interface RigidBody extends PointMass, Rotatable, TangibleVolume{
	
	double getScale();
	
	void setScale(double scale);
	
	default Collider getGlobalFrameCollider(int colliderIndex){
		return getColliders()[colliderIndex].getTransformed(getOrientation(), getScale(), getPosition());
	}
	
}
