package ch.dajay42.math.linAlg;

import java.util.function.*;
import java.util.stream.IntStream;

import ch.dajay42.collections.*;
import ch.dajay42.math.function.*;

public class MatrixLazy extends Matrix {
	private static final long serialVersionUID = 1L;
	
	
	private IntToDoubleFunction f;
	
	
	@SuppressWarnings("WeakerAccess")
	public MatrixLazy(int rows, int cols, IntToDoubleFunction f) {
		super(rows,cols);
		if(f == null)
			throw new NullPointerException();
		
		this.f = f;
	}
	
	MatrixLazy(Matrix source){
		super(source.rows,source.cols);
		this.f = source::internalGetValueAt;
	}
	
	@Override
	public MatrixLazy lazy() {
		return this;
	}

	public Matrix eval(){
		return Matrix.zeroes(rows, cols).fill((IntToDoubleFunction) this);
	}

	@Override
	public double applyAsDouble(int elem) {
		return f.applyAsDouble(elem);
	}

	@Override
	protected double internalGetValueAt(int row, int col) {
		return internalGetValueAt(asElemIndex(row, col));
	}

	@Override
	protected double internalGetValueAt(int elem) {
		return f.applyAsDouble(elem);
	}

	@Override
	protected void internalSetValueAt(int row, int col, double val) {
		internalSetValueAt(asElemIndex(row, col), val);
	}

	@Override
	protected void internalSetValueAt(int elem, double val) {
		final IntToDoubleFunction g = f;
		f = (ee) -> ee == elem ? val : g.applyAsDouble(ee);
	}
	
	@Override
	public boolean isLazy(){
		return true;
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
	protected double internalModValueAt(int row, int col, double off) {
		return internalModValueAt(asElemIndex(row, col), off);
	}
	
	@Override
	protected double internalModValueAt(int elem, double off) {
		final IntToDoubleFunction g = f;
		final double v = g.applyAsDouble(elem) + off;
		f = (ee) -> ee == elem ? v : g.applyAsDouble(ee);
		return v;
	}

	@Override
	public MatrixLazy clone() {
		return new MatrixLazy(rows, cols, f);
	}

	@Override
	public MatrixLazy getRow(int row) {
		assertBounds(row, 0);
		return new MatrixLazy(1, cols, (col) -> f.applyAsDouble(asElemIndex(row, col)));
	}

	@Override
	public MatrixLazy getColumn(int col) {
		assertBounds(0, col);
		return new MatrixLazy(rows, 1, (row) -> f.applyAsDouble(asElemIndex(row, col)));
	}

	@Override
	public MatrixLazy fill(double d) {
		f = (elem) -> d;
		return this;
	}

	@Override
	public MatrixLazy fill(DoubleSupplier f) {
		this.f = (elem) -> f.getAsDouble();
		return this;
	}

	@Override
	public MatrixLazy fill(IntToDoubleFunction f) {
		this.f = f;
		return this;
	}

	@Override
	public MatrixLazy fill(double[][] values) {
		if(rows != values.length || cols != values[0].length)
			throw new MatrixDimensionMismatchException();
		
		f = (elem) -> values[asRowIndex(elem)][asColIndex(elem)];
		return this;
	}

	@Override
	public MatrixLazy fill(Matrix other) {
		if(rows != other.rows || cols != other.cols)
			throw new MatrixDimensionMismatchException();
		
		f = other::internalGetValueAt;
		return this;
	}

	@Override
	public Matrix multiplySimple(Matrix b) {
		return this.cacheIfLazy().internalMultiplySimple(b.lazy());
	}
	
	public MatrixLazy multiplySimple(MatrixLazy b) {
		return this.cacheIfLazy().internalMultiplySimple(b.cacheIfLazy());
	}

	private MatrixLazy internalMultiplySimple(MatrixLazy b){
		return new MatrixLazy(rows, b.cols, (elem) -> this.getRow(b.asColIndex(elem)).dot(b.getColumn(b.asRowIndex(elem)))).cacheIfLazy();
	}
	
	
	@Override
	public double dot(Matrix b){
		return dot(b.lazy());
	}
	
	@SuppressWarnings("WeakerAccess")
	public double dot(MatrixLazy b){
		if(cols != b.rows || rows != 1 || b.cols != 1)
			throw new MatrixDimensionMismatchException();
		return IntStream.range(0, cols).mapToDouble((elem) -> this.f.applyAsDouble(elem) * b.f.applyAsDouble(elem)).sum();
	}

	@Override
	public MatrixLazy transpose() {
		return new MatrixLazy(cols, rows, (elem) -> f.applyAsDouble(asRowIndex(elem) * rows + asColIndex(elem)));
	}
	
	@SuppressWarnings("OptionalGetWithoutIsPresent")
	public double aggregate(DoubleBinaryOperator f){
		return IntStream.range(0, elems).mapToDouble(this.f).reduce(f).getAsDouble();
	}

	@Override
	public MatrixLazy aggregateOpRowWise(ToDoubleFunction<double[]> f) {
		return super.aggregateOpRowWise(f).lazy();
	}

	@SuppressWarnings("OptionalGetWithoutIsPresent")
	@Override
	public MatrixLazy aggregateRowWise(DoubleBinaryOperator f){
		return new MatrixLazy(rows, 1, (row) -> IntStream.range(0, cols).mapToDouble((col) -> internalGetValueAt(row,col)).reduce(f).getAsDouble());
	}

	@Override
	public MatrixLazy aggregateOpColumnWise(ToDoubleFunction<double[]> f) {
		return super.aggregateOpColumnWise(f).lazy();
	}

	@SuppressWarnings("OptionalGetWithoutIsPresent")
	@Override
	public MatrixLazy aggregateColumnWise(DoubleBinaryOperator f){
		return new MatrixLazy(1, cols, (col) -> IntStream.range(0, rows).mapToDouble((row) -> internalGetValueAt(row,col)).reduce(f).getAsDouble());
	}

	public MatrixLazy inplaceSum(MatrixLazy b) {
		if(this.rows != b.rows || this.cols != b.cols)
			throw new MatrixDimensionMismatchException();

		final IntToDoubleFunction g = f;
		f = (elem) -> g.applyAsDouble(elem) + b.f.applyAsDouble(elem);
		return this;
	}

	@Override
	public MatrixLazy inplaceSum(Matrix b){
		return this.inplaceSum(b.lazy());
	}

	@Override
	public MatrixLazy sum(Matrix b){
		return clone().inplaceSum(b);
	}

	@Override
	public MatrixLazy inplaceElementWise(DoubleUnaryOperator f) {
		final IntToDoubleFunction g = this.f;
		this.f = (elem) -> f.applyAsDouble(g.applyAsDouble(elem));
		return this;
	}

	@Override
	public MatrixLazy elementWise(DoubleUnaryOperator f){
		return clone().inplaceElementWise(f);
	}

	public MatrixLazy inplaceElementWise(DoubleBinaryOperator f, MatrixLazy b) {
		if(this.rows != b.rows || this.cols != b.cols)
			throw new MatrixDimensionMismatchException();
		final IntToDoubleFunction g = this.f;
		this.f = (elem) -> f.applyAsDouble(g.applyAsDouble(elem), b.f.applyAsDouble(elem));
		return this;
	}

	@Override
	public MatrixLazy inplaceElementWise(DoubleBinaryOperator f, Matrix b){
		return inplaceElementWise(f, b.lazy());
	}
	
	@Override
	public MatrixLazy elementWise(DoubleBinaryOperator f, Matrix b){
		return clone().inplaceElementWise(f, b);
	}
	
	@SuppressWarnings("WeakerAccess")
	public MatrixLazy inplaceElementWise(DoubleTernaryOperator f, MatrixLazy b, MatrixLazy c) {
		if(this.rows != b.rows || this.cols != b.cols || this.rows != c.rows || this.cols != c.cols)
			throw new MatrixDimensionMismatchException();
		final IntToDoubleFunction g = this.f;
		this.f = (elem) -> f.applyAsDouble(g.applyAsDouble(elem), b.f.applyAsDouble(elem), c.f.applyAsDouble(elem));
		return this;
	}

	@Override
	public MatrixLazy inplaceElementWise(DoubleTernaryOperator f, Matrix b, Matrix c){
		return inplaceElementWise(f, b.lazy(), c.lazy());
	}

	@Override
	public MatrixLazy elementWise(DoubleTernaryOperator f, Matrix b, Matrix c){
		return clone().inplaceElementWise(f, b, c);
	}

	@Override
	public MatrixLazy inplaceScalarOp(DoubleBinaryOperator f, double b) {
		final IntToDoubleFunction g = this.f;
		this.f = (elem) -> f.applyAsDouble(g.applyAsDouble(elem), b);
		return this;
	}

	@Override
	public MatrixLazy scalarOp(DoubleBinaryOperator f, double b){
		return clone().inplaceScalarOp(f, b);
	}
	
	@Override
	public MatrixLazy cacheIfLazy(){
		final FixedIntToDoubleCache cache = new FixedIntToDoubleCache(elems);
		return new MatrixLazy(rows, cols, (elem) -> {
			if(!cache.containsKey(elem))
				cache.put(elem, f.applyAsDouble(elem));
			return cache.get(elem);
		});
	}
}
