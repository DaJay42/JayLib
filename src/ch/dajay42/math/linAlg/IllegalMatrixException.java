package ch.dajay42.math.linAlg;

/**Thrown to indicate that a method has been passed an argument Matrix of unsuitable dimensions.
 */
public class IllegalMatrixException extends IllegalArgumentException{
	
	private static final long serialVersionUID = 1L;
	
	public IllegalMatrixException(){
		super("Matrix error: Given Matrix is not valid for this function.");
	}
	public IllegalMatrixException(String arg){
		super(arg);
	}
}
