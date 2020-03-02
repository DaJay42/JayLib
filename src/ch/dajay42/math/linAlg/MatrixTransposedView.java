package ch.dajay42.math.linAlg;

public class MatrixTransposedView extends MatrixView{
	
	public MatrixTransposedView(Matrix base){
		super(base.cols, base.rows, base);
	}
	
	@Override
	protected int transformElemIndex(int elem){
		return base.asElemIndex(asColIndex(elem), asRowIndex(elem));
	}
	
	@Override
	protected int transformRowIndex(int row, int col){
		return col;
	}
	
	@Override
	protected int transformColIndex(int row, int col){
		return row;
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
