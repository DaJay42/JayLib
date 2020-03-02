package ch.dajay42.math.arithmetic;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

public class DoubleInterval implements DoubleArithmetic<DoubleInterval>{
	
	public final double min, max;
	
	public DoubleInterval(double min, double max){
		this.min = min;
		this.max = max;
	}
	
	@Override
	public DoubleInterval negate(){
		return new DoubleInterval(-max, -min);
	}
	
	@Override
	public DoubleInterval reciprocal(){
		if(min >= 0.0 || max < 0.0)
			return new DoubleInterval(1.0/max, 1.0/min);
		else
			return new DoubleInterval(1.0/min, 1.0/max);
	}
	
	@Override
	public DoubleInterval add(DoubleInterval other){
		return new DoubleInterval(this.min + other.min, this.max + other.max);
	}
	
	@Override
	public DoubleInterval subtract(DoubleInterval other){
		return new DoubleInterval(this.min - other.max, this.max - other.min);
	}
	
	@Override
	public DoubleInterval multiply(DoubleInterval other){
		return new DoubleInterval(Math.min(Math.min(this.min * other.min, this.min * other.max), Math.min(this.max * other.min, this.max * other.max)),
				Math.max(Math.max(this.min * other.min, this.min * other.max), Math.max(this.max * other.min, this.max * other.max)));
	}
	
	@Override
	public DoubleInterval divide(DoubleInterval other){
		return new DoubleInterval(Math.min(Math.min(this.min / other.min, this.min / other.max), Math.min(this.max / other.min, this.max / other.max)),
				Math.max(Math.max(this.min / other.min, this.min / other.max), Math.max(this.max * other.min, this.max * other.max)));
	}
	
	@Override
	public DoubleInterval exp(){
		return new DoubleInterval(Math.exp(min), Math.exp(max));
	}
	
	@Override
	public double norm(){
		return max - min;
	}
	
	@Override
	public DoubleInterval cwise(DoubleUnaryOperator o){
		double a = o.applyAsDouble(min), b = o.applyAsDouble(max);
		return new DoubleInterval(Math.min(a,b),Math.max(a,b));
	}
	
	@Override
	public DoubleInterval cwise(DoubleBinaryOperator o, DoubleInterval other){
		double a = o.applyAsDouble(this.min, other.min), b = o.applyAsDouble(this.max, other.max);
		return new DoubleInterval(Math.min(a,b),Math.max(a,b));
	}
	
	@Override
	public DoubleInterval cwise(DoubleBinaryOperator o, double other){
		double a = o.applyAsDouble(min, other), b = o.applyAsDouble(max, other);
		return new DoubleInterval(Math.min(a,b),Math.max(a,b));
	}
}
