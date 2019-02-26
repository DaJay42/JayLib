package ch.dajay42.math.arithmetic;

import java.util.function.*;

public interface DoubleArithmetic<A extends DoubleArithmetic<A>> {
	
	//unary functions
	
	A negate();
	
	A reciprocal();
	
	//binary functions
	
	A add(A other);
	
	default A subtract(A other) {
		return add(other.negate());
	}
	
	A multiply(A other);
	
	default A divide(A other) {
		return multiply(other.reciprocal());
	}
	
	
	//
	
	A exp();
	
	double norm();
	
	//Element operations
	
	A cwise(DoubleUnaryOperator o);
	
	A cwise(DoubleBinaryOperator o, A other);
	
	A cwise(DoubleBinaryOperator o, double other);
}
