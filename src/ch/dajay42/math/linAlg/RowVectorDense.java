package ch.dajay42.math.linAlg;

import java.util.Arrays;
import java.util.function.DoubleSupplier;
import java.util.function.IntToDoubleFunction;

public class RowVectorDense extends Matrix{

	private static final long serialVersionUID = 1L;

	private final double[] values;
	
	private final boolean parallelize;
	
	@SuppressWarnings("WeakerAccess")
	public RowVectorDense(int m) {
		super(1, m);
		values = new double[m];
		this.parallelize = s > PARALLEL_LIMIT;
	}

	@Override
	protected double internalGetValueAt(int i, int j) {
		return values[j];
	}

	@Override
	protected void internalSetValueAt(int i, int j, double val) {
		values[j] = val;
	}

	
	@Override
	protected double internalModValueAt(int i, int j, double off) {
		values[j] += off;
		return values[j];
	}

	@Override
	protected double internalGetValueAt(int e) {
		return values[e];
	}

	@Override
	protected void internalSetValueAt(int e, double val) {
		values[e] = val;
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
		return false;
	}
	
	@Override
	public Matrix fill(double d) {
		if(parallelize)
			Arrays.parallelSetAll(values, (e) -> d);
		else
			Arrays.fill(values, d);
		return this;
	}
	
	@Override
	public Matrix fill(DoubleSupplier f) {
		if(parallelize)
			Arrays.parallelSetAll(values, (e) -> f.getAsDouble());
		else
			Arrays.setAll(values, (e) -> f.getAsDouble());
		return this;
	}
	
	@Override
	public Matrix fill(IntToDoubleFunction f) {
		if(parallelize)
			Arrays.parallelSetAll(values, f);
		else
			Arrays.setAll(values, f);
		return this;
	}
	
	@Override
	public Matrix fill(Matrix other) {
		if(n != other.n || m != other.m){
			throw new MatrixDimensionMismatchException();
		}
		if(parallelize)
			Arrays.parallelSetAll(values, other::internalGetValueAt);
		else
			Arrays.setAll(values, other::internalGetValueAt);
		return this;
	}
	
	@Override
	public Matrix inplaceSum(Matrix b) {
		if(m != b.m || n != b.n){
			throw new MatrixDimensionMismatchException();
		}
		if(parallelize)
			Arrays.parallelSetAll(values, (e) -> values[e] + b.internalGetValueAt(e));
		else
			Arrays.setAll(values, (e) -> values[e] + b.internalGetValueAt(e));
		return this;
	}
}
