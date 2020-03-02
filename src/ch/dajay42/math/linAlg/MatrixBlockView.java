package ch.dajay42.math.linAlg;

public class MatrixBlockView extends MatrixView{
	
	private final int rowOff, colOff;
	
	public MatrixBlockView(Matrix base, int rowOff, int colOff, int rows, int cols){
		super(rows, cols, base);
		this.rowOff = rowOff;
		this.colOff = colOff;
	}
	
	@Override
	protected int transformElemIndex(int elem){
		int row = asRowIndex(elem), col = asColIndex(elem);
		return base.asElemIndex(transformRowIndex(row, col), transformColIndex(row, col));
	}
	
	@Override
	protected int transformRowIndex(int row, int col){
		return (row + rowOff) % base.rows;
	}
	
	@Override
	protected int transformColIndex(int row, int col){
		return (col + colOff) % base.cols;
	}
	
	@Override
	protected double internalGetValueAt(int elem){
		return internalGetValueAt(asRowIndex(elem), asColIndex(elem));
	}
	
	@Override
	protected void internalSetValueAt(int elem, double val){
		internalSetValueAt(asRowIndex(elem), asColIndex(elem), val);
	}
	
	@Override
	public boolean isLazy(){
		return false;
	}
	
	@Override
	public boolean isSparse(){
		return false;
	}
	
}
