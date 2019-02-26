package ch.dajay42.physics2D;

import ch.dajay42.math.Point2D;

import java.util.Arrays;

public class Collider{
	
	private final Point2D[] boundary;
	
	private final int type;
	
	public Collider(Point2D[] boundary, int type){
		this.boundary = boundary;
		this.type = type;
	}
	
	Point2D[] getBoundary(){
		return boundary;
	}
	
	int getType(){
		return type;
	}
	
	Collider getTransformed(double orientation, double scale, Point2D translation){
		return new Collider(Arrays.stream(getBoundary()).map(point2D ->
				point2D.getRotated(orientation).getStretched(scale).add(translation)
		).toArray(Point2D[]::new), getType());
	}
}
