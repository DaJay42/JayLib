package ch.dajay42.math.linAlg;

public class MatrixMaskedRowView extends MatrixView{
	
	final int maskedRow;
	
	public MatrixMaskedRowView(Matrix base, int maskedRow){
		super(base.rows - 1, base.cols, base);
		this.maskedRow = maskedRow;
	}
	
	@Override
	protected int transformElemIndex(int elem){
		return asRowIndex(elem) >= maskedRow ? elem + cols : elem;
	}
	
	@Override
	protected int transformRowIndex(int row, int col){
		return row >= maskedRow ? row + 1 : row;
	}
	
	@Override
	protected int transformColIndex(int row, int col){
		return col;
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
