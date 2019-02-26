package ch.dajay42.math.linAlg;

import java.util.HashMap;
import java.util.Map;

public class RowVectorSparse extends Matrix{

	private static final long serialVersionUID = 1L;

	private final Map<Integer, Double> values;
	
	
	@SuppressWarnings("WeakerAccess")
	public RowVectorSparse(int m) {
		super(1, m);
		values = new HashMap<>();
	}

	@Override
	protected double internalGetValueAt(int i, int j) {
		Double d = values.get(j);
		return (d == null ? 0.0d : d);
	}

	@Override
	protected void internalSetValueAt(int i, int j, double val) {
		if(val == 0.0d){
			values.remove(j);
		}else{
			values.put(j, val);
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
