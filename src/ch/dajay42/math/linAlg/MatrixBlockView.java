package ch.dajay42.math.linAlg;

public class MatrixBlockView extends Matrix{
	
	private final Matrix base;
	
	private final int rowOff, colOff;
	
	public MatrixBlockView(Matrix base, int rowOff, int colOff, int rows, int cols){
		super(rows, cols);
		this.base = base;
		this.rowOff = rowOff;
		this.colOff = colOff;
	}
	
	@Override
	protected double internalGetValueAt(int row, int col){
		return base.internalGetValueAt((row + rowOff) % base.rows, (col + colOff) % base.cols);
	}
	
	@Override
	protected double internalGetValueAt(int elem){
		return internalGetValueAt(asRowIndex(elem), asColIndex(elem));
	}
	
	@Override
	protected void internalSetValueAt(int row, int col, double val){
		base.internalSetValueAt((row + rowOff) % base.rows, (col + colOff) % base.cols, val);
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
	
	@Override
	public boolean isView(){
		return true;
	}
}
