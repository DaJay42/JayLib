package ch.dajay42.math.linAlg;

import java.util.Arrays;
import java.util.function.DoubleSupplier;
import java.util.function.IntToDoubleFunction;

public class ColumnVectorDense extends Matrix{

	private static final long serialVersionUID = 1L;

	private final double[] values;
	
	private final boolean parallelize;
	
	@SuppressWarnings("WeakerAccess")
	public ColumnVectorDense(int rows) {
		super(rows, 1);
		values = new double[rows];
		this.parallelize = elems > PARALLEL_LIMIT;
	}

	@Override
	protected double internalGetValueAt(int row, int col) {
		return values[row];
	}

	@Override
	protected void internalSetValueAt(int row, int col, double val) {
		values[row] = val;
	}

	
	@Override
	protected double internalModValueAt(int row, int col, double off) {
		values[row] += off;
		return values[row];
	}

	@Override
	protected double internalGetValueAt(int elem) {
		return values[elem];
	}

	@Override
	protected void internalSetValueAt(int elem, double val) {
		values[elem] = val;
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
			Arrays.parallelSetAll(values, (elem) -> d);
		else
			Arrays.fill(values, d);
		return this;
	}
	
	@Override
	public Matrix fill(DoubleSupplier f) {
		if(parallelize)
			Arrays.parallelSetAll(values, (elem) -> f.getAsDouble());
		else
			Arrays.setAll(values, (elem) -> f.getAsDouble());
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
		if(rows != other.rows || cols != other.cols){
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
		if(cols != b.cols || rows != b.rows){
			throw new MatrixDimensionMismatchException();
		}
		if(parallelize)
			Arrays.parallelSetAll(values, (elem) -> values[elem] + b.internalGetValueAt(elem));
		else
			Arrays.setAll(values, (elem) -> values[elem] + b.internalGetValueAt(elem));
		return this;
	}
}
