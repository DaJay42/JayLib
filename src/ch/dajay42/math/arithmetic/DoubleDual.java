package ch.dajay42.math.arithmetic;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

public class DoubleDual implements DoubleArithmetic<DoubleDual>{
	
	public final double primal;
	
	public final double dual;
	
	public DoubleDual(double primal, double dual){
		this.primal = primal;
		this.dual = dual;
	}
	
	@Override
	public DoubleDual negate(){
		return new DoubleDual(-primal,-dual);
	}
	
	@Override
	public DoubleDual reciprocal(){
		return new DoubleDual(1 / primal, dual / (primal * primal));
	}
	
	public DoubleDual conjugate(){
		return new DoubleDual(primal, -dual);
	}
	
	@Override
	public DoubleDual add(DoubleDual other){
		return new DoubleDual(this.primal + other.primal, this.dual + other.dual);
	}
	
	@Override
	public DoubleDual subtract(DoubleDual other){
		return new DoubleDual(this.primal - other.primal, this.dual - other.dual);
	}
	
	@Override
	public DoubleDual multiply(DoubleDual other){
		return new DoubleDual(this.primal * other.primal,
				this.primal * other.dual + this.dual * other.primal);
	}
	
	@Override
	public DoubleDual exp(){
		return new DoubleDual(Math.exp(primal) + 1, dual); //uncertain. TODO: look up
	}
	
	@Override
	public double norm(){
		return primal;
	}
	
	@Override
	public DoubleDual cwise(DoubleUnaryOperator o){
		return new DoubleDual(o.applyAsDouble(primal), o.applyAsDouble(dual));
	}
	
	@Override
	public DoubleDual cwise(DoubleBinaryOperator o, DoubleDual other){
		return new DoubleDual(o.applyAsDouble(this.primal,other.primal),
				o.applyAsDouble(this.dual, other.dual));
	}
	
	@Override
	public DoubleDual cwise(DoubleBinaryOperator o, double other){
		return new DoubleDual(o.applyAsDouble(this.primal, other), o.applyAsDouble(this.dual, other));
	}
}
