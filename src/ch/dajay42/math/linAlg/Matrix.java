package ch.dajay42.math.linAlg;

import java.io.Serializable;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.*;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import ch.dajay42.math.function.*;

//TODO: parallelize wherever useful


/**
 * <p/>Unless otherwise stated, matrices are row-major
 * <p/>NAMING CONVENTIONS:
 * <p/>n - number of rows
 *<br/>m - number of columns
 *<br/>i - row index
 *<br/>j - column index
 *<br/>s - number of elements
 *<br/>e - element index
 * @author DaJay42
 *
 */
@SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
public abstract class Matrix implements Serializable, IntToDoubleFunction, IntIntToDoubleBiFunction{
	
	private static final long serialVersionUID = 1L;
	
	//GLOBAL CONFIGURATION

	/**Default limit of filledness to decide whether a Matrix should be created sparse or not.*/
	public static double DEFAULT_SPARSE_LIMIT = 0.1d;
	
	/**Minimal number of operations required for parallelization to be efficient*/
	public static int PARALLEL_LIMIT = 1024;//65536;
	
	// FIELDS

	
	/**Number of Rows*/
	public final int n;
	/**Number of Columns*/
	public final int m;
	/**Number of Elements*/
	public final int s;
	
	
	
	
	
	
	// CONSTRUCTORS
	
	/**Default constructor is hidden to force Subclasses to call the other one.*/
	@SuppressWarnings("unused")
	private Matrix() throws UnsupportedOperationException{
		throw new UnsupportedOperationException();
	}
	
	/**Create a Matrix of size n*m.*/
	protected Matrix(int n, int m){
		if(n > 0 && m > 0){
			this.n = n;
			this.m = m;
			this.s = n*m;
		}else{
			throw new MatrixCreationException();
		}
	}
	
	
	
	
	
	
	// STATIC METHODS
	
	/**Creates a new zero Matrix of chosen sparsity and dimension.*/
	public static Matrix zeroes(int n, int m, boolean sparse){
		if(sparse)
			if(m == 1)
				return new ColumnVectorSparse(n);
			else if(n == 1)
				return new RowVectorSparse(m);
			else
				return new MatrixSparse(n,m);
		else
			if(m == 1)
				return new ColumnVectorDense(n);
			else if(n == 1)
				return new RowVectorDense(m);
			else
				return new MatrixDense(n,m);
	}
	
	/**Creates a new non-sparse zero matrix of chosen dimensions*/
	public static Matrix zeroes(int n, int m){
		return zeroes(n, m, false);
	}
	
	/**Creates a new zero matrix of the chosen sparsity and same dimensions as other*/
	public static Matrix zeroesLike(Matrix other, boolean sparse){
		return zeroes(other.n, other.m, sparse);
	}
	
	/**Creates a new zero matrix equal in size and sparsity to other
	 * @param other Matrix to copy properties from
	 * @return zero-matrix
	 */
	public static Matrix zeroesLike(Matrix other){
		return zeroesLike(other, other.isSparse());
	}

	/**Creates a new matrix filled with 1s of size n*m
	 * <p/>Runs in O(n*m).
	 * @param n rows
	 * @param m columns
	 * @return ones(n, m)
	 */
	public static Matrix ones(int n, int m){
		return zeroes(n,m).fill(1.0d);
	}

	/**Creates a new matrix filled with 1s equal in size to other
	 * <p/>Runs in O(n*m).
	 * @param other Matrix
	 * @return ones(other.n, other.m)
	 */
	public static Matrix onesLike(Matrix other){
		return ones(other.n, other.m);
	}
	
	/**Creates a new matrix filled with values, equal in size to values.
	 * <p/>Runs in O(n*m).
	 */
	public static Matrix filledWith(double[][] values){
		if(values.length < 1 || values[0].length < 1)
			throw new MatrixCreationException();
		Matrix m = zeroes(values.length, values[0].length);
		m.fill(values);
		return smartSparsify(m);
	}
	
	/**Creates a new identity matrix of size d*d
	 * <p/>Runs in O(d).
	 * @param d dimension
	 * @return identity(d)
	 */
	public static Matrix iden(int d){
		Matrix Id = zeroes(d,d, true);
		for(int k = 0; k < d; k++)
			Id.internalSetValueAt(k, k, 1.0d);
		return Id;
	}

	/**Creates a new Matrix of size n*m, with values between min (inclusive) and max (exclusive)
	 * <p/>Runs in O(n*m).
	 * @param n rows
	 * @param m columns
	 * @param min smallest value
	 * @param max highest value
	 * @return random matrix
	 */
	public static Matrix random(int n, int m, double min, double max){
		Matrix r = zeroes(n,m);
		r.fill(() -> ThreadLocalRandom.current().nextDouble(min, max));
		return r;
	}

	/**Creates a new Matrix of size n*m, with values in {0,1} drawn from a Bernoulli-distribution with mean p
	 * <p/>Result is sparse iff p < DEFAULT_SPARSE_LIMIT
	 * <p/>Runs in O(n*m).
	 * @param n rows
	 * @param m columns
	 * @param p probability of each value to be 1
	 * @return random matrix
	 */
	public static Matrix bernoulli(int n, int m, double p){
		Matrix r = zeroes(n,m, p < DEFAULT_SPARSE_LIMIT);
		r.fill(() -> (ThreadLocalRandom.current().nextDouble() < p) ? 1.0d : 0.0d);
		return r;
	}


	/**Creates a new Matrix of the same size as other, with values in {0,1} drawn from a Bernoulli-distribution with mean p
	 * <p/>Result is sparse iff p < DEFAULT_SPARSE_LIMIT
	 * <p/>Runs in O(n*m).
	 * @param other matrix to copy size of
	 * @param p probability of each value to be 1
	 * @return random matrix
	 */
	public static Matrix bernoulliLike(Matrix other, double p){
		return bernoulli(other.n, other.m, p);
	}

	
	public static Matrix reshape(Matrix other, int n, int m){
		if (n*m != other.s)
			throw new MatrixDimensionMismatchException();
		Matrix matrix = zeroes(n, m, other.isSparse());
		matrix.fill(other::internalGetValueAt);
		return matrix;
	}
	
	/**Method that intelligently copies a Matrix into its sparse or nonsparse equivalent.
	 * <p> if in.isSparse() && in.getFilledness() > DEFAULT_SPARSE_LIMIT, returns a non-sparse copy of in.
	 * <p> if !in.isSparse() && in.getFilledness() < DEFAULT_SPARSE_LIMIT, returns a sparse copy of in.
	 * <p> otherwise, returns in
	 * <p>
	 * <p>Be aware that this means that previous references may or may not point to the result and should therefore be discarded.
	 * <p/>Runs in O(n*m).
	 */
	public static Matrix smartSparsify(Matrix in){
		return smartSparsify(in, DEFAULT_SPARSE_LIMIT);
	}
	
	/**Method that intelligently copies a Matrix into its sparse or nonsparse equivalent.
	 * <p> if in.isSparse() && in.getFilledness() > limit, returns a non-sparse copy of in.
	 * <p> if !in.isSparse() && in.getFilledness() < limit, returns a sparse copy of in.
	 * <p> otherwise, returns in 
	 * <p>
	 * <p>Be aware that this means that previous references may or may not point to the result and should therefore be discarded.
	 * <p/>Runs in O(n*m).
	 */
	public static Matrix smartSparsify(Matrix in, double limit){
		Matrix out = null;
		if(limit < 0 || limit > 1)
			throw new IllegalArgumentException("limit must be between 0.0 and 1.0, inclusively.");
		
		if(in.isSparse() && in.getFilledness() > limit)
			out = zeroes(in.n, in.m, false);
		else if(!in.isSparse() && in.getFilledness() < limit)
			out = zeroes(in.n, in.m, true);
		
		if(out != null)
			out.fill(in);
		else
			out = in;
		
		return out;
	}
	
	
	
	
	
	
	
	
	// ABSTRACT FUNCTIONS -- TO BE IMPLEMENTED
	
	
	/** Gets value at position (i,j).
	 * <p>
	 * Implementation should NOT check for validity of indices.
	 * <p/>Performance Critical. Assumed to run in O(1).
	 * @param i row
	 * @param j column
	 * @return value
	 */
	protected abstract double internalGetValueAt(int i, int j);
	
	/** Gets value at position e.
	 * <p>
	 * Implementation should NOT check for validity of indices.
	 * <p/>Performance Critical. Assumed to run in O(1).
	 * @param e element index
	 * @return value
	 */
	protected abstract double internalGetValueAt(int e);

	/**Sets value at position (i,j) to val.
	 * <p>
	 * Implementation should NOT check for validity of index.
	 * <p/>Performance Critical. Assumed to run in O(1).
	 * @param i row
	 * @param j column
	 * @param val new value
	 */
	protected abstract void internalSetValueAt(int i, int j, double val);

	/**Sets value at position e to val.
	 * <p>
	 * Implementation should NOT check for validity of index.
	 * <p/>Performance Critical. Assumed to run in O(1).
	 * @param e element index
	 * @param val new value
	 */
	protected abstract void internalSetValueAt(int e, double val);
	

	/**Returns true iff this Matrix is uses a lazy implementation,
	 * i.e. if values are computed on-demand.
	 * @return iff this is lazy*/
	public abstract boolean isLazy();
	
	/**Returns true iff this Matrix is uses a sparse implementation,
	 * i.e. if values are stored in a sparse manner.
	 * @return iff this is Sparse*/
	public abstract boolean isSparse();
	
	/**Returns true iff this Matrix is a View,
	 * i.e. if values are stored in a underlying Matrix instance.
	 * @return iff this is a View*/
	public abstract boolean isView();
	
	
	
	
	// DEFAULT IMPLEMENTATIONS -- OVERRIDE AS NECESSARY
	
	
	/** Gets value at position (i,j), checks for validity of indices.
	 * <p/>Runs in O(1).
	 * @param i row
	 * @param j column
	 * @return value
	 */
	public final double getValueAt(int i, int j){
		assertBounds(i, j);
		return internalGetValueAt(i, j);
	}
	
	/** Gets value at linear index e, checks for validity of index.
	 * <p/>Runs in O(1).
	 * @param e linear index
	 * @return value
	 */
	public final double getValueAt(int e){
		assertBounds(e);
		return internalGetValueAt(e);
	}

	/**@see #getValueAt(int) */
	@Override
	public double applyAsDouble(int e) {
		return getValueAt(e);
	}
	
	/**@see #getValueAt(int, int) */
	@Override
	public double applyAsDouble(int i, int j) {
		return getValueAt(i, j);
	}

	/**Sets value at position (i,j) to val, checks for validity of indices.
	 * <p/>Runs in O(1).
	 * @param i row
	 * @param j column
	 * @param val new value
	 */
	public final void setValueAt(int i, int j, double val){
		assertBounds(i, j);
		internalSetValueAt(i, j, val);
	}
	
	/**Sets value at linear index e to val, checks for validity of index.
	 * <p/>Runs in O(1).
	 * @param e linear index
	 * @param val new value
	 */
	public final void setValueAt(int e, double val){
		assertBounds(e);
		internalSetValueAt(e, val);
	}
	
	/**Adds off to value at position (i,j) to val;
	 * performs checks;
	 * returns new value.
	 * <p/>Runs in O(1).
	 * @param i row
	 * @param j column
	 * @param off offset
	 * @return new value
	 */
	public final double modValueAt(int i, int j, double off){
		assertBounds(i, j);
		return internalModValueAt(i, j, off);
	}
	
	/**Adds off to value at position (i,j);
	 * does not perform checks;
	 * returns new value.
	 * <p/>Runs in O(1).
	 * <p/>Implementations are encouraged to override this for increased performance.
	 * @param i row
	 * @param j column
	 * @param off offset
	 * @return new value
	 */
	protected double internalModValueAt(int i, int j, double off){
		double v = internalGetValueAt(i, j) + off;
		internalSetValueAt(i,j,v);
		return v;
	}
	
	/**Adds off to value at position e;
	 * performs checks;
	 * returns new value.
	 * <p/>Runs in O(1).
	 * @param e element index
	 * @param off offset
	 * @return new value
	 */
	public final double modValueAt(int e, double off){
		assertBounds(e);
		return internalModValueAt(e, off);
	}
	
	/**Adds off to value at position e;
	 * does not perform checks;
	 * returns new value.
	 * <p/>Runs in O(1).
	 * <p/>Implementations are encouraged to override this for increased performance.
	 * @param e element index
	 * @param off offset
	 * @return new value
	 */
	protected double internalModValueAt(int e, double off){
		double v = internalGetValueAt(e) + off;
		internalSetValueAt(e,v);
		return v;
	}
	
	/**Asserts that given indices (i,j) are valid for this Matrix.
	 * Throws a MatrixIndexOutOfBoundsException otherwise.
	 */
	public void assertBounds(int i, int j){
		if(i < 0 || j < 0 || i >= n || j >= m)
			throw new MatrixIndexOutOfBoundsException(i, j, n, m);
	}
	
	/**Asserts that given index e is valid for this Matrix.
	 * Throws a MatrixIndexOutOfBoundsException otherwise.
	 */
	public void assertBounds(int e){
		if(e < 0 || e >= s)
			throw new MatrixIndexOutOfBoundsException(e, s);
	}
	
	/**Returns the fraction of entries of this Matrix that contain non-zero values.
	 * Not to be confused with isSparse()
	 * <br/>Subclasses are encouraged to override this for performance improvements.
	 * <p/>Runs in O(n*m).
	 * @see #isSparse() */
	@SuppressWarnings("OptionalGetWithoutIsPresent")
	public double getFilledness(){
		return stream().mapToInt(d -> d != 0.0d ? 1 : 0).average().getAsDouble();
	}
	
	/** Get a DoubleStream of all elements, in element index order.
	 * Parallel if s > PARALLEL_LIMIT, sequential otherwise.
	 * @return DoubleStream containing all elements
	 */
	DoubleStream stream(){
		return (s > PARALLEL_LIMIT ? IntStream.range(0, s).parallel() : IntStream.range(0, s).sequential())
				.mapToDouble(this::internalGetValueAt);
	}
	
	
	/**Creates a new matrix equal to this
	 * <p/>Runs in O(n*m).
	 * @return deep copy of this
	 */
	@SuppressWarnings("MethodDoesntCallSuperMethod")
	@Override
	public Matrix clone(){
		return zeroesLike(this).fill(this);
	}
	

	/**Gets a copy of the ith row as Vector
	 * <p/>Runs in O(m).
	 * @param i row
	 * @return copy of row
	 */
	public Matrix getRow(int i){
		assertBounds(i, 0);
		Matrix r = zeroes(1, m, isSparse());
		r.fill((j) -> internalGetValueAt(i, j));
		return r;
	}
	
	/**Gets a View of the ith row as Vector
	 * <p/>Runs in O(1).
	 * @param i row
	 * @return View of row
	 */
	public MatrixBlockView getRowView(int i){
		assertBounds(i, 0);
		return new MatrixBlockView(this, i, 0, 1, m);
	}
	
	/**Replace the ith row with the given Vector
	 * <p/>Runs in O(m).
	 * @param i row
	 * @return this
	 */
	public Matrix setRow(int i, Matrix rowVector){
		if(rowVector.n != 1 || m != rowVector.m)
			throw new MatrixDimensionMismatchException();
		assertBounds(i, 0);
		
		IntStream.range(0, m).forEach(j -> internalSetValueAt(i, j, rowVector.internalGetValueAt(j)));
		
		return this;
	}
	
	/**Gets a copy of the jth column as Vector
	 * <p/>Runs in O(n).
	 * @param j column
	 * @return copy of column
	 */
	public Matrix getColumn(int j){
		assertBounds(0, j);
		Matrix r = zeroes(n, 1, isSparse());
		r.fill((i) -> internalGetValueAt(i, j));
		return r;
	}
	
	/**Gets a View of the jth column as Vector
	 * <p/>Runs in O(1).
	 * @param j column
	 * @return View of column
	 */
	public MatrixBlockView getColumnView(int j){
		assertBounds(0, j);
		return new MatrixBlockView(this, 0, j, n, 1);
	}
	
	/**Replace the jth column with the given Vector
	 * <p/>Runs in O(m).
	 * @param j col
	 * @return this
	 */
	public Matrix setColumn(int j, Matrix colVector){
		if(colVector.m != m || 1 != colVector.n)
			throw new MatrixDimensionMismatchException();
		assertBounds(0, j);
		
		IntStream.range(0, m).forEach(i -> internalSetValueAt(i, j, colVector.internalGetValueAt(i)));
		
		return this;
	}
	
	/**Gets a View of the selected block as MatrixView
	 * <p/>Runs in O(1).
	 * @param startRow first row of the block
	 * @param startCol first column of the block
	 * @param rows number of rows of the block
	 * @param cols number of columns of the block
	 * @return View of block
	 */
	public MatrixBlockView getBlockView(int startRow, int startCol, int rows, int cols){
		return new MatrixBlockView(this, startRow, startCol, rows, cols);
	}
	

	/**Gets a copy of the ith row as double[]
	 * <p/>Runs in O(m).
	 * @param i row
	 * @return copy of row
	 */
	public double[] getValuesInRow(int i){
		assertBounds(i, 0);
		double[] ds = new double[m];
		Arrays.setAll(ds, (j) -> internalGetValueAt(i, j));
		return ds;
	}
	
	/**Gets a copy of the jth column as double[]
	 * <p/>Runs in O(n).
	 * @param j column
	 * @return copy of column
	 */
	public double[] getValuesInColumn(int j){
		assertBounds(0, j);
		double[] ds = new double[n];
		Arrays.setAll(ds, (i) -> internalGetValueAt(i, j));
		return ds;
	}
	
	
	/**Returns a double[][] that is equal in values and dimensions
	 * to the internal values of the given block of this Matrix.
	 * This creates a copy; future modifications to either
	 * will not be reflected in the other.
	 * <p>
	 * Whenever possible, use getValueAt, getRow, or getColumn instead.
	 * <p/>Runs in O(n*m).
	 * @return copy of the values
	 */
	public double[][] getValuesInBlock(int startRow, int startCol, int rows, int cols){
		return getBlockView(startRow,startCol,rows,cols).getValues();
	}
	
	/**Returns a double[][] that is equal in values and dimensions
	 * to the internal values of this Matrix.
	 * This creates a copy; future modifications to either
	 * will not be reflected in the other.
	 * <p>
	 * Whenever possible, use getValueAt, getRow, or getColumn instead.
	 * <p/>Runs in O(n*m).
	 * @return copy of all values
	 */
	public double[][] getValues(){
		double[][] ds = new double[n][m];
		if(s > PARALLEL_LIMIT)
			Arrays.parallelSetAll(ds, (i) -> {
				Arrays.setAll(ds[i], (j) -> internalGetValueAt(i, j));
				return ds[i];
			});
		else
			Arrays.setAll(ds, (i) -> {
				Arrays.setAll(ds[i], (j) -> internalGetValueAt(i, j));
				return ds[i];
			});
		return ds;
	}

	
	/**Replaces every value of this with d.
	 * <br/>Implementations are encouraged override this in a more efficient way.
	 * <p/>Runs in O(n*m).
	 * @param d value to fill with
	 * @return this
	 */
	public Matrix fill(double d){
		for(int e = 0; e < s; e++)
			internalSetValueAt(e, d);
		return this;
	}


	/**Replaces every value of this by an invocation of f.
	 * The order of invocations is not guaranteed.
	 * <br/>Implementations are encouraged override this in a more efficient way.
	 * <p/>Runs in O(n*m).
	 * @param f function to fill with
	 * @return this
	 */
	public Matrix fill(DoubleSupplier f){
		for(int e = 0; e < s; e++)
			internalSetValueAt(e, f.getAsDouble());
		return this;
	}


	/**Replaces every value of this by an invocation of f on the element index.
	 * <br/>Implementations are encouraged override this in a more efficient way.
	 * <p/>Runs in O(n*m).
	 * @param f function to fill with
	 * @return this
	 */
	public Matrix fill(IntToDoubleFunction f){
		for(int e = 0; e < s; e++)
			internalSetValueAt(e, f.applyAsDouble(e));
		return this;
	}
	
	/**Fills Matrix with the given values.
	 * The given double[][] will be copied;
	 * future changes to it will not be reflected in the Matrix,
	 * and vice-versa.
	 * The dimension of this and the array must agree.
	 * <p/>Runs in O(n*m).
	 * @param values values to fill the matrix with
	 * @return this
	 */
	public Matrix fill(double[][] values){
		if(n != values.length || m != values[0].length){
			throw new MatrixDimensionMismatchException();
		}
		
		for(int i = 0; i < n; i++)
			for(int j = 0; j < m; j++)
				internalSetValueAt(i,j, values[i][j]);
		return this;
	}
	
	/**Fills Matrix with the values of the given Matrix.
	 * The given Matrix will be copied;
	 * future changes to it will no be reflected in the Matrix,
	 * and vice-versa.
	 * The dimension of this and other must agree.
	 * <br/>Implementations are encouraged override this in a more efficient way.
	 * <p/>Runs in O(n*m).
	 * @param other values to fill the matrix with
	 */
	public Matrix fill(Matrix other) {
		if(n != other.n || m != other.m){
			throw new MatrixDimensionMismatchException();
		}
		
		for(int e = 0; e < s; e++)
			internalSetValueAt(e, other.internalGetValueAt(e));
		return this;
	}
	
	//TODO: Smarter Multiplication
	/**Naive Matrix Multiplication.
	 * <p/>Runs in O(n*m*b.m).
	 * @param b Matrix to be multiplied with
	 * @return product
	 */
	public Matrix multiplySimple(Matrix b){
		if(m != b.n){
			throw new MatrixDimensionMismatchException();
		}
		int p = b.m;
		
		Matrix c = zeroes(n, p);
		for(int i = 0; i < n; i++){
			for(int j = 0; j < p; j++){
				for(int k = 0; k < m; k++){
					c.internalModValueAt(i,j, internalGetValueAt(i,k)*b.internalGetValueAt(k,j));
				}
			}
		}
		
		return smartSparsify(c);
	}
	


	/**Quick inner vector product.
	 * This must be a row vector, b must be a column vector, and both must have the same length.
	 * @param b Vector to be multiplied with
	 * @return product
	 */
	public double dot(Matrix b){
		if(m != b.n || n != 1 || b.m != 1)
			throw new MatrixDimensionMismatchException();
		return IntStream.range(0, m).mapToDouble((e) -> this.internalGetValueAt(e) * b.internalGetValueAt(e)).sum();
	}

	
	/**Gets a new matrix equal to the transposed of this
	 * <p/>Runs in O(n*m).
	 * @return transposed copy of this
	 */
	public Matrix transpose(){
		Matrix t = zeroes(m, n, isSparse());

		for(int i = 0; i < n; i++){
			for(int j = 0; j < m; j++){
				t.internalSetValueAt(j,i, internalGetValueAt(i,j));
			}
		}
		return t;
	}
	

	
	/**Returns a new matrix C, such that for each index (i,j), C(i,j) = A(i,j) + B(i,j)
	 * <br/>As the most used elementWise function, this gets its own dedicated implementation.
	 * <p/>Runs in O(n*m).
	 * @param b second argument Matrix B
	 * @return Result matrix C
	 */
	public Matrix sum(Matrix b){
		return zeroesLike(this).fill((e) -> this.internalGetValueAt(e) + b.internalGetValueAt(e));
	}
	

	/**Returns a new matrix B, such that for each index (i,j), B(i,j) = f(A(i,j))
	 * <p/>Runs in O(n*m*f).
	 * @param f unary operator to apply
	 * @return result matrix B
	 */
	public Matrix elementWise(DoubleUnaryOperator f){
		return zeroesLike(this).fill((e) -> f.applyAsDouble(this.internalGetValueAt(e)));
	}
	
	/**Returns a new matrix C, such that for each index (i,j), C(i,j) = f(A(i,j), B(i,j))
	 * <p/>Runs in O(n*m*f).
	 * @param f binary operator to apply
	 * @param b second argument Matrix B
	 * @return Result matrix C
	 */
	public Matrix elementWise(DoubleBinaryOperator f, Matrix b){
		return zeroesLike(this).fill((e) -> f.applyAsDouble(this.internalGetValueAt(e), b.internalGetValueAt(e)));
	}
	
	/**Returns a new matrix D, such that for each index (i,j), D(i,j) = f(A(i,j), B(i,j), C(i,j))
	 * <p/>Runs in O(n*m*f).
	 * @param f ternary operator to apply
	 * @param b second argument matrix B
	 * @param c third argument matrix C
	 * @return result matrix D
	 */
	public Matrix elementWise(DoubleTernaryOperator f, Matrix b, Matrix c){
		return zeroesLike(this).fill((e) ->
				f.applyAsDouble(this.internalGetValueAt(e), b.internalGetValueAt(e), c.internalGetValueAt(e)));
	}
	
	/**Returns a new matrix C, such that for each index (i,j), C(i,j) = f(A(i,j), b)
	 * <p/>Runs in O(n*m*f).
	 * @param f binary operator to apply
	 * @param b second argument scalar
	 * @return result matrix C
	 */
	public Matrix scalarOp(DoubleBinaryOperator f, double b){
		return zeroesLike(this).fill((e) -> f.applyAsDouble(this.internalGetValueAt(e), b));
	}
	
	/**Returns value equal to f applied to the results of f applied to each row of this
	 * <p/>Runs in O(n*f).
	 * @param f aggregate operator to apply
	 * @return result
	 */
	public double aggregateOp(ToDoubleFunction<double[]> f){
		double[] r = new double[n];
		Arrays.setAll(r, (i) -> f.applyAsDouble(getValuesInRow(i)));
		return f.applyAsDouble(r);
	}
	
	/**Returns value equal to the associative binary operator f applied to all elements of this
	 * <p/>Runs in O(n*m*f).
	 * @param f associative binary operator to apply
	 * @return result
	 */
	@SuppressWarnings("OptionalGetWithoutIsPresent")
	public double aggregate(DoubleBinaryOperator f){
		return IntStream.range(0, n*m).mapToDouble(this::internalGetValueAt).reduce(f).getAsDouble();
	}
	
	/**Returns column Vector equal to f applied to each row of this
	 * <p/>Runs in O(n*f).
	 * @param f aggregate operator to apply
	 * @return result
	 */
	public Matrix aggregateOpRowWise(ToDoubleFunction<double[]> f){
		ColumnVectorDense r = new ColumnVectorDense(n);
		r.fill((i) -> f.applyAsDouble(getValuesInRow(i)));
		return r;
	}
	
	/**Returns column Vector equal to the associative binary operator f applied to all elements of each row of this
	 * <p/>Runs in O(n*m*f).
	 * @param f associative binary operator to apply
	 * @return result
	 */
	@SuppressWarnings("OptionalGetWithoutIsPresent")
	public Matrix aggregateRowWise(DoubleBinaryOperator f){
		ColumnVectorDense r = new ColumnVectorDense(n);
		IntStream.range(0, n).mapToDouble(this::internalGetValueAt).reduce(f).getAsDouble();
		r.fill((i) -> IntStream.range(0, m).mapToDouble((j) -> internalGetValueAt(i,j)).reduce(f).getAsDouble());
		return r;
	}
	
	/**Returns row Vector equal to f applied to each column of this.
	 * <p/>Runs in O(m*f).
	 * @param f aggregate operator to apply
	 * @return result vector
	 */
	public Matrix aggregateOpColumnWise(ToDoubleFunction<double[]> f){
		RowVectorDense c = new RowVectorDense(m);
		c.fill((j) -> f.applyAsDouble(getValuesInColumn(j)));
		return c;
	}
	
	/**Returns column Vector equal to the associative binary operator f applied to all elements of each column of this
	 * <p/>Runs row O(n*m*f).
	 * @param f associative binary operator to apply
	 * @return result vector
	 */
	@SuppressWarnings("OptionalGetWithoutIsPresent")
	public Matrix aggregateColumnWise(DoubleBinaryOperator f){
		RowVectorDense c = new RowVectorDense(m);
		IntStream.range(0, m).mapToDouble(this::internalGetValueAt).reduce(f).getAsDouble();
		c.fill((j) -> IntStream.range(0, n).mapToDouble((i) -> internalGetValueAt(i,j)).reduce(f).getAsDouble());
		return c;
	}
	

	
	/**Modifies and returns this Matrix A, such that for each index (i,j), A(i,j) <- A(i,j) + B(i,j)
	 * <br/>As the most used elementWise function, this gets its own dedicated implementation.
	 * <p/>Runs in O(n*m).
	 * @param b second argument Matrix B
	 * @return modified Matrix this
	 */
	public Matrix inplaceSum(Matrix b){
		if(m != b.m || n != b.n){
			throw new MatrixDimensionMismatchException();
		}
		for(int e = 0; e < s; e++){
			internalModValueAt(e, b.internalGetValueAt(e));
		}
		return this;
	}
	

	/**Modifies and returns this Matrix A, such that for each index (i,j), A(i,j) <- f(A(i,j))
	 * <p/>Runs in O(n*m*f).
	 * @param f unary operator to apply
	 * @return modified Matrix this
	 */
	public Matrix inplaceElementWise(DoubleUnaryOperator f){
		return fill((e) -> f.applyAsDouble(internalGetValueAt(e)));
	}
	
	/**Modifies and returns this Matrix A, such that for each index (i,j), A(i,j) <- f(A(i,j), B(i,j))
	 * <p/>Runs in O(n*m*f).
	 * @param f binary operator to apply
	 * @param b second argument Matrix B
	 * @return modified Matrix this
	 */
	public Matrix inplaceElementWise(DoubleBinaryOperator f, Matrix b){
		if(m != b.m || n != b.n){
			throw new MatrixDimensionMismatchException();
		}
		return fill((e) -> f.applyAsDouble(internalGetValueAt(e), b.internalGetValueAt(e)));
	}
	
	/**Modifies and returns this Matrix A, such that for each index (i,j), A(i,j) <- f(A(i,j), B(i,j), C(i,j))
	 * <p/>Runs in O(n*m*f).
	 * @param f ternary operator to apply
	 * @param b second argument matrix B
	 * @param c third argument matrix C
	 * @return modified Matrix this
	 */
	public Matrix inplaceElementWise(DoubleTernaryOperator f, Matrix b, Matrix c){
		if(m != b.m || n != b.n || m != c.m || n != c.n){
			throw new MatrixDimensionMismatchException();
		}
		return fill((e) -> f.applyAsDouble(internalGetValueAt(e), b.internalGetValueAt(e), c.internalGetValueAt(e)));
	}
	
	/**Modifies and returns this Matrix A, such that for each index (i,j), A(i,j) <- f(A(i,j), b)
	 * <p/>Runs in O(n*m*f).
	 * @param f binary operator to apply
	 * @param b second argument scalar
	 * @return modified Matrix this
	 */
	public Matrix inplaceScalarOp(DoubleBinaryOperator f, double b){
		return fill((e) -> f.applyAsDouble(internalGetValueAt(e), b));
	}
	
	public MatrixLazy lazy(){
		return new MatrixLazy(this);
	}
	
	public Matrix cacheIfLazy(){
		return this;
	}
	
	public boolean equals(Matrix other) {
		if(n != other.n || m != other.m)
			return false;
		for(int e=0; e<s; e++){
			if(Double.compare(internalGetValueAt(e), other.internalGetValueAt(e)) != 0)
				return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("\"value\":");
		builder.append('[');
		for(int i = 0; i < n; i++){
			if(i > 0) builder.append(',');
			builder.append(System.lineSeparator());
			builder.append('[');
			for(int j = 0; j < m; j++){
				if(j > 0) builder.append(',');
				builder.append('\t');
				builder.append(internalGetValueAt(i,j));
			}
			builder.append(']');
		}
		builder.append(System.lineSeparator());
		builder.append(']');
		
		return builder.toString();
	}
}
