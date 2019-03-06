package ch.dajay42.math.linAlg;

import java.util.HashMap;
import java.util.Map;

public class MatrixSparse extends Matrix{

	private static final long serialVersionUID = 1L;

	private final Map<Integer, Double> values;
	
	
	@SuppressWarnings("WeakerAccess")
	public MatrixSparse(int rows, int cols) {
		super(rows, cols);
		values = new HashMap<>(); // TODO: is there any usable specialized int-double map implementation?
	}
	
	@Override
	protected double internalGetValueAt(int row, int col) {
		Double d = values.get(asElemIndex(row, col));
		return (d == null ? 0.0d : d);
	}

	@Override
	protected void internalSetValueAt(int row, int col, double val) {
		if(val == 0.0d){
			values.remove(asElemIndex(row, col));
		}else{
			values.put(asElemIndex(row, col), val);
		}
	}

	@Override
	protected double internalGetValueAt(int elem) {
		Double d = values.get(elem);
		return (d == null ? 0.0d : d);
	}

	@Override
	protected void internalSetValueAt(int elem, double val) {
		if(val == 0.0d){
			values.remove(elem);
		}else{
			values.put(elem, val);
		}
	}
	
	@Override
	public boolean isLazy(){
		return false;
	}
	
	@Override
	public boolean isSparse(){
		return true;
	}
	
	@Override
	public boolean isView(){
		return false;
	}
}
