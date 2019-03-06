package ch.dajay42.math.linAlg;

/**Thrown to indicate that a method has been passed an argument Matrix of unsuitable dimensions.
 */
public final class MatrixNotAVectorException extends IllegalMatrixException{
	
	private static final long serialVersionUID = 1L;
	
	public MatrixNotAVectorException(){
		super("Matrix error: Matrix must be a Vector.");
	}
	
	public MatrixNotAVectorException(int rows, int cols){
		super("Matrix error: Matrix must be a Vector, was "+rows+"*"+cols+".");
	}
}
