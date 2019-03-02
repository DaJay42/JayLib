package ch.dajay42.math.linAlg;

/**Thrown to indicate that a Matrix has been accessed with an illegal index.
 * The index is either negative or greater than or equal to the size of the Matrix.
*/
public final class MatrixIndexOutOfBoundsException extends ArrayIndexOutOfBoundsException{

	private static final long serialVersionUID = 1L;
	
	public MatrixIndexOutOfBoundsException(){
		super("Matrix error: Index is out of bounds.");
	}
	
	public MatrixIndexOutOfBoundsException(int row, int col, int rows, int cols){
		super("Matrix error: Index is out of bounds. Row must be between 0 and "+rows+", was "+row+". Column must be between 0 and "+cols+", was "+col+".");
	}

	public MatrixIndexOutOfBoundsException(int elem, int elems) {
		super("Matrix error: Index is out of bounds. Element index must be between 0 and "+elems+", was "+elem+".");
	}
}
