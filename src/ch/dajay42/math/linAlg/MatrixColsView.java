package ch.dajay42.math.linAlg;

public class MatrixColsView extends MatrixView{
	
	protected final int startCol;
	
	public MatrixColsView(int startCol, int cols, Matrix base){
		super(base.rows, cols, base);
		this.startCol = startCol;
	}
	
	@Override
	protected int transformElemIndex(int elem){
		return elem + startCol;
	}
	
	@Override
	protected int transformRowIndex(int row, int col){
		return row;
	}
	
	@Override
	protected int transformColIndex(int row, int col){
		return col + startCol;
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
