package ch.dajay42.math.arithmetic;

import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;
import ch.dajay42.math.Util;

public class IntRational implements IntArithmetic<IntRational>{
	
	public final int numerator;
	
	public final int denominator;
	
	public IntRational(int numerator, int denominator){
		if(denominator == 0){
			throw new ArithmeticException("divide by zero");
		}
		else if(denominator < 0){
			denominator = -denominator;
			numerator = -numerator;
		}
		int gcd = Util.gcd(numerator, denominator);
		if(gcd != 1){
			numerator /= gcd;
			denominator /= gcd;
		}
		this.numerator = numerator;
		this.denominator = denominator;
	}
	
	@Override
	public IntRational negate(){
		return new IntRational(-numerator, denominator);
	}
	
	@Override
	public IntRational reciprocal(){
		return new IntRational(denominator, numerator);
	}
	
	@Override
	public IntRational add(IntRational other){
		return new IntRational(this.numerator * other.denominator + other.numerator * this.denominator,
				this.denominator * other.denominator);
	}
	
	@Override
	public IntRational subtract(IntRational other){
		return new IntRational(this.numerator * other.denominator - other.numerator * this.denominator,
				this.denominator * other.denominator);
	}
	
	@Override
	public IntRational multiply(IntRational other){
		return new IntRational(this.numerator * other.numerator,
				this.denominator * other.denominator);
	}
	
	@Override
	public IntRational divide(IntRational other){
		return new IntRational(this.numerator * other.denominator,
				this.denominator * other.numerator);
	}
	
	public double toDouble(){
		return (double) numerator / (double) denominator;
	}
	
	@Override
	public double norm(){
		return Math.abs((double) numerator / (double) denominator);
	}
	
	@Override
	public IntRational cwise(IntUnaryOperator o){
		return new IntRational(o.applyAsInt(numerator), o.applyAsInt(denominator));
	}
	
	@Override
	public IntRational cwise(IntBinaryOperator o, IntRational other){
		return new IntRational(o.applyAsInt(this.numerator, other.numerator),
				o.applyAsInt(this.denominator, other.denominator));
	}
	
	@Override
	public IntRational cwise(IntBinaryOperator o, int other){
		return new IntRational(o.applyAsInt(this.numerator, other),
				o.applyAsInt(this.denominator, other));
	}
}
