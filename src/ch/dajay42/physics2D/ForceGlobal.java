package ch.dajay42.physics2D;

import ch.dajay42.math.Point2D;

import java.util.function.ToDoubleFunction;

public class ForceGlobal<T extends PointMass> extends Force<T> {

	public static final Point2D DOWN = new Point2D(0,1);
	public static final Point2D UP = new Point2D(0,-1);
	public static final Point2D LEFT = new Point2D(-1,0);
	public static final Point2D RIGHT = new Point2D(1,0);
	
	Point2D direction;
	Point2D value;
	
	protected ForceGlobal(ToDoubleFunction<T> toDoubleFunction, double magnitude, Point2D direction) {
		super(toDoubleFunction, magnitude);
		this.direction = direction;
		this.value = direction.getNormalized().stretch(magnitude);
	}
	
	public Point2D get(T entity){
		double parameter = getParamOf(entity);
		return value.getStretched(parameter);
	}
	
}