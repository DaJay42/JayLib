package ch.dajay42.math.linAlg;

public class MatrixMaskedColView extends Matrix{
	
	final Matrix base;
	
	final int maskedCol;
	
	public MatrixMaskedColView(Matrix base, int maskedCol){
		super(base.rows, base.cols - 1);
		this.base = base;
		this.maskedCol = maskedCol;
	}
	
	@Override
	protected double internalGetValueAt(int row, int col){
		return base.internalGetValueAt(row, col >= maskedCol ? col + 1 : col);
	}
	
	@Override
	protected double internalGetValueAt(int elem){
		return base.internalGetValueAt(asColIndex(elem) >= maskedCol ? elem + 1 : elem);
	}
	
	@Override
	protected void internalSetValueAt(int row, int col, double val){
		base.internalSetValueAt(row, col >= maskedCol ? col + 1 : col, val);
	}
	
	@Override
	protected void internalSetValueAt(int elem, double val){
		base.internalSetValueAt(asColIndex(elem) >= maskedCol ? elem + 1 : elem, val);
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
