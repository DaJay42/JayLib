package ch.dajay42.math.linAlg;

/**Thrown to indicate that a Matrix has been created with an illegal argument.
 */
public final class MatrixCreationException extends IllegalArgumentException{

	private static final long serialVersionUID = 1L;
	
	public MatrixCreationException(){
		super("Matrix error: Matrix dimensions must be positive.");
	}
}
