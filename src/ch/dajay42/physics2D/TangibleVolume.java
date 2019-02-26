package ch.dajay42.physics2D;

public interface TangibleVolume{

	void setColliders(Collider... colliders);
	
	Collider[] getColliders();

}
