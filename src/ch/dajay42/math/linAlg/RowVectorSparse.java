package ch.dajay42.math.linAlg;

import java.util.HashMap;
import java.util.Map;

public class RowVectorSparse extends Matrix{

	private static final long serialVersionUID = 1L;

	private final Map<Integer, Double> values;
	
	
	@SuppressWarnings("WeakerAccess")
	public RowVectorSparse(int cols) {
		super(1, cols);
		values = new HashMap<>();
	}

	@Override
	protected double internalGetValueAt(int row, int col) {
		Double d = values.get(col);
		return (d == null ? 0.0d : d);
	}

	@Override
	protected void internalSetValueAt(int row, int col, double val) {
		if(val == 0.0d){
			values.remove(col);
		}else{
			values.put(col, val);
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
