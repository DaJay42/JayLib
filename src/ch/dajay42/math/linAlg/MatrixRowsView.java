package ch.dajay42.math.linAlg;

public class MatrixRowsView extends MatrixView{
	
	protected final int startRow;
	
	public MatrixRowsView(int startRow, int rows, Matrix base){
		super(rows, base.cols, base);
		this.startRow = startRow;
	}
	
	@Override
	protected int transformElemIndex(int elem){
		return elem + startRow * cols;
	}
	
	@Override
	protected int transformRowIndex(int row, int col){
		return row + startRow;
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
