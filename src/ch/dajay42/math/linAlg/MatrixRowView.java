package ch.dajay42.math.linAlg;

public class MatrixRowView extends MatrixView{
	
	protected final int row;
	
	public MatrixRowView(int row, Matrix base){
		super(1, base.cols, base);
		this.row = row;
	}
	
	@Override
	protected int transformElemIndex(int elem){
		return elem + row * cols;
	}
	
	@Override
	protected int transformRowIndex(int row, int col){
		return row + this.row;
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
