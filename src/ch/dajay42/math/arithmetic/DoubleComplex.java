package ch.dajay42.math.arithmetic;

import java.util.function.*;

public class DoubleComplex implements DoubleArithmetic<DoubleComplex>{
	
	public final double real;
	
	public final double imaginary;
	
	public DoubleComplex(double real, double imaginary){
		this.real = real;
		this.imaginary = imaginary;
	}
	
	@Override
	public DoubleComplex negate(){
		return new DoubleComplex(-real,-imaginary);
	}
	
	@Override
	public DoubleComplex reciprocal(){
		double denominator = real * real + imaginary * imaginary;
		return new DoubleComplex(real/denominator, imaginary/denominator);
	}
	
	public DoubleComplex conjugate(){
		return new DoubleComplex(real, -imaginary);
	}
	
	@Override
	public DoubleComplex add(DoubleComplex other){
		return new DoubleComplex(this.real + other.real, this.imaginary + other.imaginary);
	}
	
	@Override
	public DoubleComplex subtract(DoubleComplex other){
		return new DoubleComplex(this.real - other.real, this.imaginary - other.imaginary);
	}
	
	@Override
	public DoubleComplex multiply(DoubleComplex other){
		return new DoubleComplex(this.real * other.real - this.imaginary * other.imaginary,
				this.real * other.imaginary + this.imaginary * other.real);
	}
	
	@Override
	public DoubleComplex exp(){
		return new DoubleComplex(Math.exp(real) * Math.cos(imaginary),
				Math.exp(real) * Math.sin(imaginary));
	}
	
	@Override
	public double norm(){
		return Math.sqrt(real * real + imaginary * imaginary);
	}
	
	@Override
	public DoubleComplex cwise(DoubleUnaryOperator o){
		return new DoubleComplex(o.applyAsDouble(real), o.applyAsDouble(imaginary));
	}
	
	@Override
	public DoubleComplex cwise(DoubleBinaryOperator o, DoubleComplex other){
		return new DoubleComplex(o.applyAsDouble(this.real,other.real),
				o.applyAsDouble(this.imaginary, other.imaginary));
	}
	
	@Override
	public DoubleComplex cwise(DoubleBinaryOperator o, double other){
		return new DoubleComplex(o.applyAsDouble(this.real, other), o.applyAsDouble(this.imaginary, other));
	}
}
