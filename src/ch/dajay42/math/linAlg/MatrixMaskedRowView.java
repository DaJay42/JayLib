package ch.dajay42.math.linAlg;

public class MatrixMaskedRowView extends Matrix{
	
	final Matrix base;
	
	final int maskedRow;
	
	public MatrixMaskedRowView(Matrix base, int maskedRow){
		super(base.rows - 1, base.cols);
		this.base = base;
		this.maskedRow = maskedRow;
	}
	
	@Override
	protected double internalGetValueAt(int row, int col){
		return base.internalGetValueAt(row >= maskedRow ? row + 1 : row, col);
	}
	
	@Override
	protected double internalGetValueAt(int elem){
		return base.internalGetValueAt(asRowIndex(elem) >= maskedRow ? elem + cols : elem);
	}
	
	@Override
	protected void internalSetValueAt(int row, int col, double val){
		base.internalSetValueAt(row >= maskedRow ? row + 1 : row, col, val);
	}
	
	@Override
	protected void internalSetValueAt(int elem, double val){
		base.internalSetValueAt(asRowIndex(elem) >= maskedRow ? elem + cols : elem, val);
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
