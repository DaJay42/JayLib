package ch.dajay42.math.linAlg;

public class MatrixMaskedColView extends MatrixView{
	
	final int maskedCol;
	
	public MatrixMaskedColView(Matrix base, int maskedCol){
		super(base.rows, base.cols - 1, base);
		this.maskedCol = maskedCol;
	}
	
	@Override
	protected int transformElemIndex(int elem){
		return asColIndex(elem) >= maskedCol ? elem + 1 : elem;
	}
	
	@Override
	protected int transformRowIndex(int row, int col){
		return row;
	}
	
	@Override
	protected int transformColIndex(int row, int col){
		return col >= maskedCol ? col + 1 : col;
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
