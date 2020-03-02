package ch.dajay42.math.linAlg;

public abstract class MatrixView extends Matrix{
	
	protected final Matrix base;
	
	/**
	 * Create a MatrixView of size rows*cols,
	 * with underlying Matrix base.
	 * @param rows number of rows in the view
	 * @param cols number of cols in the view
	 * @param base underlying matrix
	 */
	protected MatrixView(int rows, int cols, Matrix base){
		super(rows, cols);
		this.base = base;
	}
	
	/**Returns the element index of base corresponding to index elem of this.
	 * May throw {@link MatrixIndexOutOfBoundsException} if appropriate.
	 * @param elem element index in this
	 * @return element index in base
	 */
	protected abstract int transformElemIndex(int elem);
	
	/**Returns the row index of base corresponding to indices row,col of this.
	 * May throw {@link MatrixIndexOutOfBoundsException} if appropriate.
	 * @param row row index in this
	 * @param col column index in this
	 * @return row index in base
	 */
	protected abstract int transformRowIndex(int row, int col);
	
	/**Returns the column index of base corresponding to indices row,col of this.
	 * May throw {@link MatrixIndexOutOfBoundsException} if appropriate.
	 * @param row row index in this
	 * @param col column index in this
	 * @return column index in base
	 */
	protected abstract int transformColIndex(int row, int col);
	
	@Override
	protected double internalGetValueAt(int row, int col){
		return base.internalGetValueAt(transformRowIndex(row, col), transformColIndex(row, col));
	}
	
	@Override
	protected double internalGetValueAt(int elem){
		return base.internalGetValueAt(transformElemIndex(elem));
	}
	
	@Override
	protected void internalSetValueAt(int row, int col, double val){
		base.internalSetValueAt(transformRowIndex(row,col),transformColIndex(row,col),val);
	}
	
	@Override
	protected void internalSetValueAt(int elem, double val){
		base.internalSetValueAt(transformElemIndex(elem),val);
	}
	
	@Override
	public final boolean isView(){
		return true;
	}
}
