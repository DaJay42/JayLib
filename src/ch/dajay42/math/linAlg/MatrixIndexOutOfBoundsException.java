package ch.dajay42.math.linAlg;

/**Thrown to indicate that a Matrix has been accessed with an illegal index.
 * The index is either negative or greater than or equal to the size of the Matrix.
*/
public final class MatrixIndexOutOfBoundsException extends ArrayIndexOutOfBoundsException{

	private static final long serialVersionUID = 1L;
	
	public MatrixIndexOutOfBoundsException(){
		super("Matrix error: Index is out of bounds.");
	}
	
	public MatrixIndexOutOfBoundsException(int i, int j, int n, int m){
		super("Matrix error: Index is out of bounds. Row must be between 0 and "+n+", was "+i+". Column must be between 0 and "+m+", was "+j+".");
	}

	public MatrixIndexOutOfBoundsException(int e, int s) {
		super("Matrix error: Index is out of bounds. Element index must be between 0 and "+s+", was "+e+".");
	}
}
