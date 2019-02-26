package ch.dajay42.physics2D;

public class ForceSymTension extends ForceSymGenericSquare<PointMass>{

	public ForceSymTension(double magnitude) {
		super(PointMass::getMass, magnitude, 16);
	}
	
	@Override
	public double getParamOf(PointMass entity) {
		return entity.getMass();
	}
}
