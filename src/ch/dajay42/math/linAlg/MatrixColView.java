package ch.dajay42.math.linAlg;

public class MatrixColView extends MatrixView{
	
	protected final int col;
	
	public MatrixColView(int col, Matrix base){
		super(base.rows, 1, base);
		this.col = col;
	}
	
	@Override
	protected int transformElemIndex(int elem){
		return elem + col;
	}
	
	@Override
	protected int transformRowIndex(int row, int col){
		return row;
	}
	
	@Override
	protected int transformColIndex(int row, int col){
		return col + this.col;
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
