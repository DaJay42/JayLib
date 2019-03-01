package ch.dajay42.math.linAlg;

import java.util.function.*;
import java.util.stream.IntStream;

import ch.dajay42.collections.*;
import ch.dajay42.math.function.*;

public class MatrixLazy extends Matrix {
	private static final long serialVersionUID = 1L;
	
	
	private IntToDoubleFunction f;
	
	
	@SuppressWarnings("WeakerAccess")
	public MatrixLazy(int n, int m, IntToDoubleFunction f) {
		super(n,m);
		if(f == null)
			throw new NullPointerException();
		
		this.f = f;
	}
	
	MatrixLazy(Matrix source){
		super(source.n,source.m);
		this.f = source::internalGetValueAt;
	}
	
	@Override
	public MatrixLazy lazy() {
		return this;
	}

	public Matrix eval(){
		return Matrix.zeroes(n,m).fill((IntToDoubleFunction) this);
	}

	@Override
	public double applyAsDouble(int e) {
		return f.applyAsDouble(e);
	}

	@Override
	protected double internalGetValueAt(int i, int j) {
		return internalGetValueAt(i*m+j);
	}

	@Override
	protected double internalGetValueAt(int e) {
		return f.applyAsDouble(e);
	}

	@Override
	protected void internalSetValueAt(int i, int j, double val) {
		internalSetValueAt(i*m+j, val);
	}

	@Override
	protected void internalSetValueAt(int e, double val) {
		final IntToDoubleFunction g = f;
		f = (ee) -> ee == e ? val : g.applyAsDouble(ee); 
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
	protected double internalModValueAt(int i, int j, double off) {
		return internalModValueAt(i*m+j, off);
	}
	
	@Override
	protected double internalModValueAt(int e, double off) {
		final IntToDoubleFunction g = f;
		final double v = g.applyAsDouble(e) + off;
		f = (ee) -> ee == e ? v : g.applyAsDouble(ee);
		return v;
	}

	@Override
	public MatrixLazy clone() {
		return new MatrixLazy(n, m, f);
	}

	@Override
	public MatrixLazy getRow(int i) {
		assertBounds(i, 0);
		return new MatrixLazy(1, m, (e) -> f.applyAsDouble(e + i*m));
	}

	@Override
	public MatrixLazy getColumn(int j) {
		assertBounds(0, j);
		return new MatrixLazy(n, 1, (e) -> f.applyAsDouble(e*m + j));
	}

	@Override
	public MatrixLazy fill(double d) {
		f = (e) -> d;
		return this;
	}

	@Override
	public MatrixLazy fill(DoubleSupplier f) {
		this.f = (e) -> f.getAsDouble();
		return this;
	}

	@Override
	public MatrixLazy fill(IntToDoubleFunction f) {
		this.f = f;
		return this;
	}

	@Override
	public MatrixLazy fill(double[][] values) {
		if(n != values.length || m != values[0].length)
			throw new MatrixDimensionMismatchException();
		
		f = (e) -> values[e%m][e/m];
		return this;
	}

	@Override
	public MatrixLazy fill(Matrix other) {
		if(n != other.n || m != other.m)
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
		return new MatrixLazy(n, b.m, (e) -> this.getRow(e%b.m).dot(b.getColumn(e/b.m))).cacheIfLazy();
	}
	
	
	@Override
	public double dot(Matrix b){
		return dot(b.lazy());
	}
	
	@SuppressWarnings("WeakerAccess")
	public double dot(MatrixLazy b){
		if(m != b.n || n != 1 || b.m != 1)
			throw new MatrixDimensionMismatchException();
		return IntStream.range(0, m).mapToDouble((e) -> this.f.applyAsDouble(e) * b.f.applyAsDouble(e)).sum();
	}

	@Override
	public MatrixLazy transpose() {
		return new MatrixLazy(m, n, (e) -> f.applyAsDouble(e/m*n+e%m));
	}
	
	@SuppressWarnings("OptionalGetWithoutIsPresent")
	public double aggregate(DoubleBinaryOperator f){
		return IntStream.range(0, n*m).mapToDouble(this.f).reduce(f).getAsDouble();
	}

	@Override
	public MatrixLazy aggregateOpRowWise(ToDoubleFunction<double[]> f) {
		return super.aggregateOpRowWise(f).lazy();
	}

	@SuppressWarnings("OptionalGetWithoutIsPresent")
	@Override
	public MatrixLazy aggregateRowWise(DoubleBinaryOperator f){
		return new MatrixLazy(n, 1, (i) -> IntStream.range(0, m).mapToDouble((j) -> internalGetValueAt(i,j)).reduce(f).getAsDouble());
	}

	@Override
	public MatrixLazy aggregateOpColumnWise(ToDoubleFunction<double[]> f) {
		return super.aggregateOpColumnWise(f).lazy();
	}

	@SuppressWarnings("OptionalGetWithoutIsPresent")
	@Override
	public MatrixLazy aggregateColumnWise(DoubleBinaryOperator f){
		return new MatrixLazy(1, m, (j) -> IntStream.range(0, n).mapToDouble((i) -> internalGetValueAt(i,j)).reduce(f).getAsDouble());
	}

	public MatrixLazy inplaceSum(MatrixLazy b) {
		if(this.n != b.n || this.m != b.m)
			throw new MatrixDimensionMismatchException();

		final IntToDoubleFunction g = f;
		f = (e) -> g.applyAsDouble(e) + b.f.applyAsDouble(e);
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
		this.f = (e) -> f.applyAsDouble(g.applyAsDouble(e));
		return this;
	}

	@Override
	public MatrixLazy elementWise(DoubleUnaryOperator f){
		return clone().inplaceElementWise(f);
	}

	public MatrixLazy inplaceElementWise(DoubleBinaryOperator f, MatrixLazy b) {
		if(this.n != b.n || this.m != b.m)
			throw new MatrixDimensionMismatchException();
		final IntToDoubleFunction g = this.f;
		this.f = (e) -> f.applyAsDouble(g.applyAsDouble(e), b.f.applyAsDouble(e));
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
		if(this.n != b.n || this.m != b.m || this.n != c.n || this.m != c.m)
			throw new MatrixDimensionMismatchException();
		final IntToDoubleFunction g = this.f;
		this.f = (e) -> f.applyAsDouble(g.applyAsDouble(e), b.f.applyAsDouble(e), c.f.applyAsDouble(e));
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
		this.f = (e) -> f.applyAsDouble(g.applyAsDouble(e), b);
		return this;
	}

	@Override
	public MatrixLazy scalarOp(DoubleBinaryOperator f, double b){
		return clone().inplaceScalarOp(f, b);
	}
	
	@Override
	public MatrixLazy cacheIfLazy(){
		final FixedIntToDoubleCache cache = new FixedIntToDoubleCache(s);
		return new MatrixLazy(n, m, (e) -> {
			if(!cache.containsKey(e))
				cache.put(e, f.applyAsDouble(e));
			return cache.get(e);
		});
	}
}
