package ch.dajay42.math.arithmetic;

import java.util.function.*;

public interface IntArithmetic<A extends IntArithmetic<A>> {
	
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
	
	double norm();
	
	//Element operations
	
	A cwise(IntUnaryOperator o);
	
	A cwise(IntBinaryOperator o, A other);
	
	A cwise(IntBinaryOperator o, int other);
}
