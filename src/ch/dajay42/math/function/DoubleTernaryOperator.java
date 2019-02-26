package ch.dajay42.math.function;

import java.io.Serializable;

@FunctionalInterface
public interface DoubleTernaryOperator extends Serializable{
	double applyAsDouble(double a, double b, double c);
}
