package ch.dajay42.physics2D;

public class ForceSymNormal extends ForceSymGenericCubic<PointMass> {

	public ForceSymNormal(double magnitude) {
		super(PointMass::getMass, magnitude, 1);
	}

	@Override
	public double getParamOf(PointMass entity){
		return entity.getMass();
	}
}
