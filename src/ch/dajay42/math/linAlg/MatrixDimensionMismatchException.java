package ch.dajay42.math.linAlg;

/**Thrown to indicate that a method has been passed an argument Matrix of unsuitable dimensions.
 */
public final class MatrixDimensionMismatchException extends IllegalMatrixException{
	
	private static final long serialVersionUID = 1L;
	
	public MatrixDimensionMismatchException(){
		super("Matrix error: Matrix dimensions must agree.");
	}
}
