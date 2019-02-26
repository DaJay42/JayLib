package ch.dajay42.math.linAlg;

import java.util.HashMap;
import java.util.Map;

public class MatrixSparse extends Matrix{

	private static final long serialVersionUID = 1L;

	private final Map<Integer, Double> values;
	
	
	@SuppressWarnings("WeakerAccess")
	public MatrixSparse(int n, int m) {
		super(n, m);
		values = new HashMap<>(); // TODO: is there any usable specialized int-double map implementation?
	}
	
	@Override
	protected double internalGetValueAt(int i, int j) {
		Integer e = i*m+j;
		Double d = values.get(e);
		return (d == null ? 0.0d : d);
	}

	@Override
	protected void internalSetValueAt(int i, int j, double val) {
		Integer e = i*m+j;
		if(val == 0.0d){
			values.remove(e);
		}else{
			values.put(e, val);
		}
	}

	@Override
	protected double internalGetValueAt(int e) {
		Double d = values.get(e);
		return (d == null ? 0.0d : d);
	}

	@Override
	protected void internalSetValueAt(int e, double val) {
		if(val == 0.0d){
			values.remove(e);
		}else{
			values.put(e, val);
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
}
