package ch.dajay42.math.linAlg;

public class MatrixBlockView extends Matrix{
	
	private final Matrix base;
	
	final int rowOff, colOff;
	
	public MatrixBlockView(Matrix base, int i, int j, int n, int m){
		super(n, m);
		base.assertBounds(i, j);
		base.assertBounds(i + n, j + m);
		this.base = base;
		this.rowOff = i;
		this.colOff = j;
	}
	
	@Override
	protected double internalGetValueAt(int i, int j){
		return base.internalGetValueAt(i + rowOff, j + colOff);
	}
	
	@Override
	protected double internalGetValueAt(int e){
		return internalGetValueAt(e%m,e/m);
	}
	
	@Override
	protected void internalSetValueAt(int i, int j, double val){
		base.internalSetValueAt(i + rowOff, j + colOff, val);
	}
	
	@Override
	protected void internalSetValueAt(int e, double val){
		internalSetValueAt(e%m, e/m, val);
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
