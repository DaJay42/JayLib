package ch.dajay42.math.linAlg;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.*;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.dajay42.math.function.*;

//TODO: parallelize wherever useful


/**
 * <p/>Unless otherwise stated, matrices are row-major
 * <p/>NAMING CONVENTIONS:
 * <p/>rows - number of rows
 *<br/>cols - number of columns
 *<br/>row - row index
 *<br/>col - column index
 *<br/>elems - number of elements
 *<br/>elem - element index
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
	public static int PARALLEL_LIMIT = 4096;//65536;
	
	// FIELDS

	
	/**Number of Rows*/
	public final int rows;
	/**Number of Columns*/
	public final int cols;
	/**Number of Elements*/
	public final int elems;
	
	
	
	
	
	
	// CONSTRUCTORS
	
	/**Default constructor is hidden to force Subclasses to call the other one.*/
	@SuppressWarnings("unused")
	private Matrix() throws UnsupportedOperationException{
		throw new UnsupportedOperationException();
	}
	
	/**Create a Matrix of size rows*cols.*/
	protected Matrix(int rows, int cols){
		if(rows > 0 && cols > 0){
			this.rows = rows;
			this.cols = cols;
			this.elems = rows * cols;
		}else{
			throw new MatrixCreationException();
		}
	}
	
	
	
	
	
	
	// STATIC METHODS
	
	/**Creates a new zero Matrix of chosen sparsity and dimension.*/
	public static Matrix zeroes(int rows, int cols, boolean sparse){
		if(sparse)
			if(cols == 1)
				return new ColumnVectorSparse(rows);
			else if(rows == 1)
				return new RowVectorSparse(cols);
			else
				return new MatrixSparse(rows,cols);
		else
			if(cols == 1)
				return new ColumnVectorDense(rows);
			else if(rows == 1)
				return new RowVectorDense(cols);
			else
				return new MatrixDense(rows,cols);
	}
	
	/**Creates a new non-sparse zero matrix of chosen dimensions*/
	public static Matrix zeroes(int rows, int cols){
		return zeroes(rows, cols, false);
	}
	
	/**Creates a new zero matrix of the chosen sparsity and same dimensions as other*/
	public static Matrix zeroesLike(Matrix other, boolean sparse){
		return zeroes(other.rows, other.cols, sparse);
	}
	
	/**Creates a new zero matrix equal in size and sparsity to other
	 * @param other Matrix to copy properties from
	 * @return zero-matrix
	 */
	public static Matrix zeroesLike(Matrix other){
		return zeroesLike(other, other.isSparse());
	}

	/**Creates a new matrix filled with 1s of size rows*cols
	 * <p/>Runs in O(rows*cols).
	 * @param rows rows
	 * @param cols columns
	 * @return ones(rows, cols)
	 */
	public static Matrix ones(int rows, int cols){
		return zeroes(rows,cols).fill(1.0d);
	}

	/**Creates a new matrix filled with 1s equal in size to other
	 * <p/>Runs in O(rows*cols).
	 * @param other Matrix
	 * @return ones(other.rows, other.cols)
	 */
	public static Matrix onesLike(Matrix other){
		return ones(other.rows, other.cols);
	}
	
	/**Creates a new matrix filled with values, equal in size to values.
	 * <p/>Runs in O(rows*cols).
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

	/**Creates a new Matrix of size rows*cols, with values between min (inclusive) and max (exclusive)
	 * <p/>Runs in O(rows*cols).
	 * @param rows rows
	 * @param cols columns
	 * @param min smallest value
	 * @param max highest value
	 * @return random matrix
	 */
	public static Matrix random(int rows, int cols, double min, double max){
		Matrix r = zeroes(rows,cols);
		r.fill(() -> ThreadLocalRandom.current().nextDouble(min, max));
		return r;
	}

	/**Creates a new Matrix of size rows*cols, with values in {0,1} drawn from a Bernoulli-distribution with mean p
	 * <p/>Result is sparse iff p < DEFAULT_SPARSE_LIMIT
	 * <p/>Runs in O(rows*cols).
	 * @param rows rows
	 * @param cols columns
	 * @param p probability of each value to be 1
	 * @return random matrix
	 */
	public static Matrix bernoulli(int rows, int cols, double p){
		Matrix r = zeroes(rows,cols, p < DEFAULT_SPARSE_LIMIT);
		r.fill(() -> (ThreadLocalRandom.current().nextDouble() < p) ? 1.0d : 0.0d);
		return r;
	}


	/**Creates a new Matrix of the same size as other, with values in {0,1} drawn from a Bernoulli-distribution with mean p
	 * <p/>Result is sparse iff p < DEFAULT_SPARSE_LIMIT
	 * <p/>Runs in O(rows*cols).
	 * @param other matrix to copy size of
	 * @param p probability of each value to be 1
	 * @return random matrix
	 */
	public static Matrix bernoulliLike(Matrix other, double p){
		return bernoulli(other.rows, other.cols, p);
	}

	
	public static Matrix reshaped(Matrix other, int rows, int cols){
		if (rows*cols != other.elems)
			throw new MatrixDimensionMismatchException();
		Matrix matrix = zeroes(rows, cols, other.isSparse());
		matrix.fill(other::internalGetValueAt);
		return matrix;
	}
	
	
	public static Matrix horzCat(Matrix left, Matrix right){
		if(left.rows != right.rows){
			throw new MatrixDimensionMismatchException();
		}
		Matrix out = zeroes(left.rows, left.cols + right.cols, left.isSparse() && right.isSparse());
		out.getColsView(0,left.cols).fill(left);
		out.getColsView(left.cols,right.cols).fill(right);
		return out;
	}
	
	public static Matrix vertCat(Matrix top, Matrix bottom){
		if(top.cols != bottom.cols){
			throw new MatrixDimensionMismatchException();
		}
		Matrix out = zeroes(top.rows + bottom.rows, top.cols, top.isSparse() && bottom.isSparse());
		out.getRowsView(0, top.rows).fill(top);
		out.getRowsView(top.rows, bottom.rows).fill(bottom);
		return out;
	}
	
	public static Matrix horzStack(Collection<Matrix> matrices){
		int rows = matrices.stream().findFirst().orElseThrow().rows;
		if(matrices.stream().anyMatch((matrix) -> matrix.rows != rows)){
			throw new MatrixDimensionMismatchException();
		}
		int cols = matrices.stream().mapToInt(value -> value.cols).sum();
		boolean sparse = matrices.stream().allMatch(Matrix::isSparse);
		Matrix out = zeroes(rows, cols, sparse);
		int col = 0;
		for(Matrix matrix : matrices){
			out.getColsView(col, matrix.cols).fill(matrix);
			col += matrix.cols;
		}
		return out;
	}
	public static Matrix horzStack(Matrix... matrices){
		return horzStack(Arrays.asList(matrices));
	}
	
	public static Matrix vertStack(Collection<Matrix> matrices){
		int cols = matrices.stream().findFirst().orElseThrow().cols;
		if(matrices.stream().anyMatch((matrix) -> matrix.cols != cols)){
			throw new MatrixDimensionMismatchException();
		}
		int rows = matrices.stream().mapToInt(value -> value.rows).sum();
		boolean sparse = matrices.stream().allMatch(Matrix::isSparse);
		Matrix out = zeroes(rows, cols, sparse);
		int row = 0;
		for(Matrix matrix : matrices){
			out.getRowsView(row, matrix.rows).fill(matrix);
			row += matrix.rows;
		}
		return out;
	}
	public static Matrix vertStack(Matrix... matrices){
		return vertStack(Arrays.asList(matrices));
	}
	
	public static Matrix diagStack(Collection<Matrix> matrices){
		int rows = matrices.stream().mapToInt(value -> value.rows).sum();
		int cols = matrices.stream().mapToInt(value -> value.cols).sum();
		double elems = matrices.stream().mapToDouble(value -> value.elems * value.getFilledness()).sum();
		boolean sparse = elems < rows * cols * DEFAULT_SPARSE_LIMIT;
		
		Matrix out = zeroes(rows, cols, sparse);
		int row = 0, col = 0;
		for(Matrix matrix : matrices){
			out.getBlockView(row, col, matrix.rows, matrix.cols).fill(matrix);
			row += matrix.rows;
			col += matrix.cols;
		}
		return out;
	}
	public static Matrix diagStack(Matrix... matrices){
		return diagStack(Arrays.asList(matrices));
	}
	
	public static Matrix repMat(Matrix matrix, int horizontal, int vertical){
		if(horizontal <= 0 || vertical <= 0){
			throw new IllegalArgumentException("Arguments must be positive.");
		}
		Matrix out = zeroes(matrix.rows * horizontal, matrix.cols * vertical, matrix.isSparse());
		for(int i = 0; i < horizontal; i++){
			for(int j = 0; j < vertical; j++){
				out.getBlockView(i * matrix.rows, j * matrix.cols, matrix.rows, matrix.cols).fill(matrix);
			}
		}
		return out;
	}
	
	
	////////
	
	/**Method that intelligently copies a Matrix into its sparse or nonsparse equivalent.
	 * <p> if in.isSparse() && in.getFilledness() > DEFAULT_SPARSE_LIMIT, returns a non-sparse copy of in.
	 * <p> if !in.isSparse() && in.getFilledness() < DEFAULT_SPARSE_LIMIT, returns a sparse copy of in.
	 * <p> otherwise, returns in
	 * <p>
	 * <p>Be aware that this means that previous references may or may not point to the result and should therefore be discarded.
	 * <p/>Runs in O(rows*cols).
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
	 * <p/>Runs in O(rows*cols).
	 */
	public static Matrix smartSparsify(Matrix in, double limit){
		Matrix out = null;
		if(limit < 0 || limit > 1)
			throw new IllegalArgumentException("limit must be between 0.0 and 1.0, inclusively.");
		
		if(in.isSparse() && in.getFilledness() > limit)
			out = zeroes(in.rows, in.cols, false);
		else if(!in.isSparse() && in.getFilledness() < limit)
			out = zeroes(in.rows, in.cols, true);
		
		if(out != null)
			out.fill(in);
		else
			out = in;
		
		return out;
	}
	
	
	
	
	
	
	
	
	// ABSTRACT FUNCTIONS -- TO BE IMPLEMENTED
	
	
	/** Gets value at position (row,col).
	 * <p>
	 * Implementation should NOT check for validity of indices.
	 * <p/>Performance Critical. Assumed to run in O(1).
	 * @param row row
	 * @param col column
	 * @return value
	 */
	protected abstract double internalGetValueAt(int row, int col);
	
	/** Gets value at position elem.
	 * <p>
	 * Implementation should NOT check for validity of indices.
	 * <p/>Performance Critical. Assumed to run in O(1).
	 * @param elem element index
	 * @return value
	 */
	protected abstract double internalGetValueAt(int elem);

	/**Sets value at position (row,col) to val.
	 * <p>
	 * Implementation should NOT check for validity of index.
	 * <p/>Performance Critical. Assumed to run in O(1).
	 * @param row row
	 * @param col column
	 * @param val new value
	 */
	protected abstract void internalSetValueAt(int row, int col, double val);

	/**Sets value at position elem to val.
	 * <p>
	 * Implementation should NOT check for validity of index.
	 * <p/>Performance Critical. Assumed to run in O(1).
	 * @param elem element index
	 * @param val new value
	 */
	protected abstract void internalSetValueAt(int elem, double val);
	

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
	
	public int asElemIndex(int row, int col){
		return row * cols + col;
	}
	
	public int asRowIndex(int elem){
		return elem / cols;
	}
	
	public int asColIndex(int elem){
		return elem % cols;
	}
	
	/** Gets value at position (row,col), checks for validity of indices.
	 * <p/>Runs in O(1).
	 * @param row row
	 * @param col column
	 * @return value
	 */
	public final double getValueAt(int row, int col){
		assertBounds(row, col);
		return internalGetValueAt(row, col);
	}
	
	/** Gets value at linear index elem, checks for validity of index.
	 * <p/>Runs in O(1).
	 * @param elem linear index
	 * @return value
	 */
	public final double getValueAt(int elem){
		assertBounds(elem);
		return internalGetValueAt(elem);
	}

	/**@see #getValueAt(int) */
	@Override
	public double applyAsDouble(int elem) {
		return getValueAt(elem);
	}
	
	/**@see #getValueAt(int, int) */
	@Override
	public double applyAsDouble(int row, int col) {
		return getValueAt(row, col);
	}

	/**Sets value at position (row,col) to val, checks for validity of indices.
	 * <p/>Runs in O(1).
	 * @param row row
	 * @param col column
	 * @param val new value
	 */
	public final void setValueAt(int row, int col, double val){
		assertBounds(row, col);
		internalSetValueAt(row, col, val);
	}
	
	/**Sets value at linear index elem to val, checks for validity of index.
	 * <p/>Runs in O(1).
	 * @param elem linear index
	 * @param val new value
	 */
	public final void setValueAt(int elem, double val){
		assertBounds(elem);
		internalSetValueAt(elem, val);
	}
	
	/**Adds off to value at position (row,col) to val;
	 * performs checks;
	 * returns new value.
	 * <p/>Runs in O(1).
	 * @param row row
	 * @param col column
	 * @param off offset
	 * @return new value
	 */
	public final double modValueAt(int row, int col, double off){
		assertBounds(row, col);
		return internalModValueAt(row, col, off);
	}
	
	/**Adds off to value at position (row,col);
	 * does not perform checks;
	 * returns new value.
	 * <p/>Runs in O(1).
	 * <p/>Implementations are encouraged to override this for increased performance.
	 * @param row row
	 * @param col column
	 * @param off offset
	 * @return new value
	 */
	protected double internalModValueAt(int row, int col, double off){
		double v = internalGetValueAt(row, col) + off;
		internalSetValueAt(row,col,v);
		return v;
	}
	
	/**Adds off to value at position elem;
	 * performs checks;
	 * returns new value.
	 * <p/>Runs in O(1).
	 * @param elem element index
	 * @param off offset
	 * @return new value
	 */
	public final double modValueAt(int elem, double off){
		assertBounds(elem);
		return internalModValueAt(elem, off);
	}
	
	/**Adds off to value at position elem;
	 * does not perform checks;
	 * returns new value.
	 * <p/>Runs in O(1).
	 * <p/>Implementations are encouraged to override this for increased performance.
	 * @param elem element index
	 * @param off offset
	 * @return new value
	 */
	protected double internalModValueAt(int elem, double off){
		double v = internalGetValueAt(elem) + off;
		internalSetValueAt(elem,v);
		return v;
	}
	
	/**Asserts that given indices (row,col) are valid for this Matrix.
	 * Throws a MatrixIndexOutOfBoundsException otherwise.
	 */
	public void assertBounds(int row, int col){
		if(row < 0 || col < 0 || row >= rows || col >= cols)
			throw new MatrixIndexOutOfBoundsException(row, col, rows, cols);
	}
	
	/**Asserts that given index elem is valid for this Matrix.
	 * Throws a MatrixIndexOutOfBoundsException otherwise.
	 */
	public void assertBounds(int elem){
		if(elem < 0 || elem >= elems)
			throw new MatrixIndexOutOfBoundsException(elem, elems);
	}
	
	/**Returns the fraction of entries of this Matrix that contain non-zero values.
	 * Not to be confused with isSparse()
	 * <br/>Subclasses are encouraged to override this for performance improvements.
	 * <p/>Runs in O(rows*cols).
	 * @see #isSparse() */
	@SuppressWarnings("OptionalGetWithoutIsPresent")
	public double getFilledness(){
		return stream().mapToInt(d -> d != 0.0d ? 1 : 0).average().getAsDouble();
	}
	
	/** Get a DoubleStream of all elements, in element index order.
	 * Parallel if elems > PARALLEL_LIMIT, sequential otherwise.
	 * @return DoubleStream containing all elements
	 */
	DoubleStream stream(){
		return (elems > PARALLEL_LIMIT ? IntStream.range(0, elems).parallel() : IntStream.range(0, elems).sequential())
				.mapToDouble(this::internalGetValueAt);
	}
	
	
	/**Creates a new matrix equal to this
	 * <p/>Runs in O(rows*cols).
	 * @return deep copy of this
	 */
	@SuppressWarnings("MethodDoesntCallSuperMethod")
	@Override
	public Matrix clone(){
		return zeroesLike(this).fill(this);
	}
	

	/**Gets a copy of the ith row as Vector
	 * <p/>Runs in O(cols).
	 * @param row row
	 * @return copy of row
	 */
	public Matrix getRow(int row){
		assertBounds(row, 0);
		Matrix r = zeroes(1, cols, isSparse());
		r.fill((col) -> internalGetValueAt(row, col));
		return r;
	}
	
	/**Gets a View of the ith row as Vector
	 * <p/>Runs in O(1).
	 * @param row row
	 * @return View of row
	 */
	public MatrixRowView getRowView(int row){
		assertBounds(row, 0);
		return new MatrixRowView(row,this);
	}
	
	/**Gets a View of the startRow-th through (startRow+rows)-th rows as Matrix
	 * <p/>Runs in O(1).
	 * @param startRow first row
	 * @param rows number of rows
	 * @return View of rows
	 */
	public MatrixRowsView getRowsView(int startRow, int rows){
		assertBounds(startRow, 0);
		assertBounds(startRow+rows, 0);
		return new MatrixRowsView(startRow, rows, this);
	}
	
	/**Replace the ith row with the given Vector
	 * <p/>Runs in O(cols).
	 * @param row row
	 * @return this
	 */
	public Matrix setRow(int row, Matrix rowVector){
		if(rowVector.rows != 1 || cols != rowVector.cols)
			throw new MatrixDimensionMismatchException();
		assertBounds(row, 0);
		
		IntStream.range(0, cols).forEach((col) -> internalSetValueAt(row, col, rowVector.internalGetValueAt(col)));
		
		return this;
	}
	
	/**Gets a copy of the jth column as Vector
	 * <p/>Runs in O(rows).
	 * @param col column
	 * @return copy of column
	 */
	public Matrix getColumn(int col){
		assertBounds(0, col);
		Matrix r = zeroes(rows, 1, isSparse());
		r.fill((row) -> internalGetValueAt(row, col));
		return r;
	}
	
	/**Gets a View of the jth column as Vector
	 * <p/>Runs in O(1).
	 * @param col column
	 * @return View of column
	 */
	public MatrixColView getColumnView(int col){
		assertBounds(0, col);
		return new MatrixColView(col,this);
	}
	
	/**Gets a View of the startCol-th through (startCol+cols)-th cols as Matrix
	 * <p/>Runs in O(1).
	 * @param startCol first col
	 * @param cols number of lols
	 * @return View of cols
	 */
	public MatrixRowsView getColsView(int startCol, int cols){
		assertBounds(startCol, 0);
		assertBounds(startCol+cols, 0);
		return new MatrixRowsView(startCol, cols, this);
	}
	
	/**Replace the jth column with the given Vector
	 * <p/>Runs in O(cols).
	 * @param col col
	 * @return this
	 */
	public Matrix setColumn(int col, Matrix colVector){
		if(colVector.cols != cols || 1 != colVector.rows)
			throw new MatrixDimensionMismatchException();
		assertBounds(0, col);
		
		IntStream.range(0, rows).forEach((row) -> internalSetValueAt(row, col, colVector.internalGetValueAt(row)));
		
		return this;
	}
	
	/**Gets a View of the selected block as MatrixView.
	 *<br/>Note that the number of rows asn columns in the View may
	 *<br/>exceed those in the underlying matrix; the resultant indices will wrap around.
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
	
	public MatrixMaskedRowView exceptRowView(int row){
		return new MatrixMaskedRowView(this, row);
	}
	
	public MatrixMaskedColView exceptColView(int col){
		return new MatrixMaskedColView(this, col);
	}

	/**Gets a copy of the ith row as double[]
	 * <p/>Runs in O(cols).
	 * @param row row
	 * @return copy of row
	 */
	public double[] getValuesInRow(int row){
		assertBounds(row, 0);
		double[] ds = new double[cols];
		Arrays.setAll(ds, (col) -> internalGetValueAt(row, col));
		return ds;
	}
	
	/**Gets a copy of the jth column as double[]
	 * <p/>Runs in O(rows).
	 * @param col column
	 * @return copy of column
	 */
	public double[] getValuesInColumn(int col){
		assertBounds(0, col);
		double[] ds = new double[rows];
		Arrays.setAll(ds, (row) -> internalGetValueAt(row, col));
		return ds;
	}
	
	
	/**Returns a double[][] that is equal in values and dimensions
	 * to the internal values of the given block of this Matrix.
	 * This creates a copy; future modifications to either
	 * will not be reflected in the other.
	 * <p>
	 * Whenever possible, use getValueAt, getRow, or getColumn instead.
	 * <p/>Runs in O(rows*cols).
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
	 * <p/>Runs in O(rows*cols).
	 * @return copy of all values
	 */
	public double[][] getValues(){
		double[][] ds = new double[rows][cols];
		if(elems > PARALLEL_LIMIT)
			Arrays.parallelSetAll(ds, (row) -> {
				Arrays.setAll(ds[row], (col) -> internalGetValueAt(row, col));
				return ds[row];
			});
		else
			Arrays.setAll(ds, (row) -> {
				Arrays.setAll(ds[row], (col) -> internalGetValueAt(row, col));
				return ds[row];
			});
		return ds;
	}

	
	/**Replaces every value of this with d.
	 * <br/>Implementations are encouraged override this in a more efficient way.
	 * <p/>Runs in O(rows*cols).
	 * @param d value to fill with
	 * @return this
	 */
	public Matrix fill(double d){
		for(int elem = 0; elem < elems; elem++)
			internalSetValueAt(elem, d);
		return this;
	}


	/**Replaces every value of this by an invocation of f.
	 * The order of invocations is not guaranteed.
	 * <br/>Implementations are encouraged override this in a more efficient way.
	 * <p/>Runs in O(rows*cols).
	 * @param f function to fill with
	 * @return this
	 */
	public Matrix fill(DoubleSupplier f){
		for(int elem = 0; elem < elems; elem++)
			internalSetValueAt(elem, f.getAsDouble());
		return this;
	}


	/**Replaces every value of this by an invocation of f on the element index.
	 * <br/>Implementations are encouraged override this in a more efficient way.
	 * <p/>Runs in O(rows*cols).
	 * @param f function to fill with
	 * @return this
	 */
	public Matrix fill(IntToDoubleFunction f){
		for(int elem = 0; elem < elems; elem++)
			internalSetValueAt(elem, f.applyAsDouble(elem));
		return this;
	}
	
	/**Fills Matrix with the given values.
	 * The given double[] will be copied;
	 * future changes to it will not be reflected in the Matrix,
	 * and vice-versa.
	 * The dimension of this and the array must agree.
	 * <p/>Runs in O(rows*cols).
	 * @param values values to fill the matrix with
	 * @return this
	 */
	public Matrix fill(double[] values){
		if(elems != values.length){
			throw new MatrixDimensionMismatchException();
		}
		
		for(int elem = 0; elem < elems; elem++)
			internalSetValueAt(elem, values[elem]);
		return this;
	}
	
	/**Fills Matrix with the given values.
	 * The given double[][] will be copied;
	 * future changes to it will not be reflected in the Matrix,
	 * and vice-versa.
	 * The dimension of this and the array must agree.
	 * <p/>Runs in O(rows*cols).
	 * @param values values to fill the matrix with
	 * @return this
	 */
	public Matrix fill(double[][] values){
		if(rows != values.length || cols != values[0].length){
			throw new MatrixDimensionMismatchException();
		}
		
		for(int row = 0; row < rows; row++)
			for(int col = 0; col < cols; col++)
				internalSetValueAt(row,col, values[row][col]);
		return this;
	}
	
	/**Fills Matrix with the values of the given Matrix.
	 * The given Matrix will be copied;
	 * future changes to it will no be reflected in the Matrix,
	 * and vice-versa.
	 * The dimension of this and other must agree.
	 * <br/>Implementations are encouraged override this in a more efficient way.
	 * <p/>Runs in O(rows*cols).
	 * @param other values to fill the matrix with
	 */
	public Matrix fill(Matrix other) {
		if(rows != other.rows || cols != other.cols){
			throw new MatrixDimensionMismatchException();
		}
		
		for(int elem = 0; elem < elems; elem++)
			internalSetValueAt(elem, other.internalGetValueAt(elem));
		return this;
	}
	
	//TODO: Smarter Multiplication
	/**Naive Matrix Multiplication.
	 * <p/>Runs in O(rows*cols*b.cols).
	 * @param b Matrix to be multiplied with
	 * @return product
	 */
	public Matrix multiplySimple(Matrix b){
		if(cols != b.rows){
			throw new MatrixDimensionMismatchException();
		}
		int bcols = b.cols;
		
		Matrix c = zeroes(rows, bcols);
		for(int row = 0; row < rows; row++){
			for(int bcol = 0; bcol < bcols; bcol++){
				for(int col = 0; col < cols; col++){
					c.internalModValueAt(row,bcol, internalGetValueAt(row,col)*b.internalGetValueAt(col,bcol));
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
		if(cols != b.rows)
			throw new MatrixDimensionMismatchException();
		if(rows != 1 || b.cols != 1)
			throw new MatrixNotAVectorException();
		return IntStream.range(0, cols).mapToDouble((elem) -> this.internalGetValueAt(elem) * b.internalGetValueAt(elem)).sum();
	}

	
	/**Gets a new matrix equal to the transposed of this
	 * <p/>Runs in O(rows*cols).
	 * @return transposed copy of this
	 */
	public Matrix transpose(){
		Matrix t = zeroes(cols, rows, isSparse());

		for(int row = 0; row < rows; row++){
			for(int col = 0; col < cols; col++){
				t.internalSetValueAt(col,row, internalGetValueAt(row,col));
			}
		}
		return t;
	}
	

	
	/**Returns a new matrix C, such that for each index (i,j), C(i,j) = A(i,j) + B(i,j)
	 * <br/>As the most used elementWise function, this gets its own dedicated implementation.
	 * <p/>Runs in O(rows*cols).
	 * @param b second argument Matrix B
	 * @return Result matrix C
	 */
	public Matrix sum(Matrix b){
		return zeroesLike(this).fill((elem) -> this.internalGetValueAt(elem) + b.internalGetValueAt(elem));
	}
	

	/**Returns a new matrix B, such that for each index (i,j), B(i,j) = f(A(i,j))
	 * <p/>Runs in O(rows*cols*f).
	 * @param f unary operator to apply
	 * @return result matrix B
	 */
	public Matrix elementWise(DoubleUnaryOperator f){
		return zeroesLike(this).fill((elem) -> f.applyAsDouble(this.internalGetValueAt(elem)));
	}
	
	/**Returns a new matrix C, such that for each index (i,j), C(i,j) = f(A(i,j), B(i,j))
	 * <p/>Runs in O(rows*cols*f).
	 * @param f binary operator to apply
	 * @param b second argument Matrix B
	 * @return Result matrix C
	 */
	public Matrix elementWise(DoubleBinaryOperator f, Matrix b){
		return zeroesLike(this).fill((elem) -> f.applyAsDouble(this.internalGetValueAt(elem), b.internalGetValueAt(elem)));
	}
	
	/**Returns a new matrix D, such that for each index (i,j), D(i,j) = f(A(i,j), B(i,j), C(i,j))
	 * <p/>Runs in O(rows*cols*f).
	 * @param f ternary operator to apply
	 * @param b second argument matrix B
	 * @param c third argument matrix C
	 * @return result matrix D
	 */
	public Matrix elementWise(DoubleTernaryOperator f, Matrix b, Matrix c){
		return zeroesLike(this).fill((elem) ->
				f.applyAsDouble(this.internalGetValueAt(elem), b.internalGetValueAt(elem), c.internalGetValueAt(elem)));
	}
	
	/**Returns a new matrix C, such that for each index (i,j), C(i,j) = f(A(i,j), b)
	 * <p/>Runs in O(rows*cols*f).
	 * @param f binary operator to apply
	 * @param b second argument scalar
	 * @return result matrix C
	 */
	public Matrix scalarOp(DoubleBinaryOperator f, double b){
		return zeroesLike(this).fill((elem) -> f.applyAsDouble(this.internalGetValueAt(elem), b));
	}
	
	/**Returns value equal to f applied to the results of f applied to each row of this
	 * <p/>Runs in O(rows*f).
	 * @param f aggregate operator to apply
	 * @return result
	 */
	public double aggregateOp(ToDoubleFunction<double[]> f){
		double[] r = new double[rows];
		Arrays.setAll(r, (row) -> f.applyAsDouble(getValuesInRow(row)));
		return f.applyAsDouble(r);
	}
	
	/**Returns value equal to the associative binary operator f applied to all elements of this
	 * <p/>Runs in O(rows*cols*f).
	 * @param f associative binary operator to apply
	 * @return result
	 */
	@SuppressWarnings("OptionalGetWithoutIsPresent")
	public double aggregate(DoubleBinaryOperator f){
		return IntStream.range(0, elems).mapToDouble(this::internalGetValueAt).reduce(f).getAsDouble();
	}
	
	/**Returns column Vector equal to f applied to each row of this
	 * <p/>Runs in O(rows*f).
	 * @param f aggregate operator to apply
	 * @return result
	 */
	public Matrix aggregateOpRowWise(ToDoubleFunction<double[]> f){
		ColumnVectorDense r = new ColumnVectorDense(rows);
		r.fill((row) -> f.applyAsDouble(getValuesInRow(row)));
		return r;
	}
	
	/**Returns column Vector equal to the associative binary operator f applied to all elements of each row of this
	 * <p/>Runs in O(rows*cols*f).
	 * @param f associative binary operator to apply
	 * @return result
	 */
	@SuppressWarnings("OptionalGetWithoutIsPresent")
	public Matrix aggregateRowWise(DoubleBinaryOperator f){
		ColumnVectorDense r = new ColumnVectorDense(rows);
		IntStream.range(0, rows).mapToDouble(this::internalGetValueAt).reduce(f).getAsDouble();
		r.fill((row) -> IntStream.range(0, cols).mapToDouble((col) -> internalGetValueAt(row,col)).reduce(f).getAsDouble());
		return r;
	}
	
	/**Returns row Vector equal to f applied to each column of this.
	 * <p/>Runs in O(cols*f).
	 * @param f aggregate operator to apply
	 * @return result vector
	 */
	public Matrix aggregateOpColumnWise(ToDoubleFunction<double[]> f){
		RowVectorDense c = new RowVectorDense(cols);
		c.fill((col) -> f.applyAsDouble(getValuesInColumn(col)));
		return c;
	}
	
	/**Returns column Vector equal to the associative binary operator f applied to all elements of each column of this
	 * <p/>Runs row O(rows*cols*f).
	 * @param f associative binary operator to apply
	 * @return result vector
	 */
	@SuppressWarnings("OptionalGetWithoutIsPresent")
	public Matrix aggregateColumnWise(DoubleBinaryOperator f){
		RowVectorDense c = new RowVectorDense(cols);
		IntStream.range(0, cols).mapToDouble(this::internalGetValueAt).reduce(f).getAsDouble();
		c.fill((col) -> IntStream.range(0, rows).mapToDouble((row) -> internalGetValueAt(row,col)).reduce(f).getAsDouble());
		return c;
	}
	

	
	/**Modifies and returns this Matrix A, such that for each index (i,j), A(i,j) <- A(i,j) + B(i,j)
	 * <br/>As the most used elementWise function, this gets its own dedicated implementation.
	 * <p/>Runs in O(rows*cols).
	 * @param b second argument Matrix B
	 * @return modified Matrix this
	 */
	public Matrix inplaceSum(Matrix b){
		if(cols != b.cols || rows != b.rows){
			throw new MatrixDimensionMismatchException();
		}
		for(int elem = 0; elem < elems; elem++){
			internalModValueAt(elem, b.internalGetValueAt(elem));
		}
		return this;
	}
	

	/**Modifies and returns this Matrix A, such that for each index (i,j), A(i,j) <- f(A(i,j))
	 * <p/>Runs in O(rows*cols*f).
	 * @param f unary operator to apply
	 * @return modified Matrix this
	 */
	public Matrix inplaceElementWise(DoubleUnaryOperator f){
		return fill((elem) -> f.applyAsDouble(internalGetValueAt(elem)));
	}
	
	/**Modifies and returns this Matrix A, such that for each index (i,j), A(i,j) <- f(A(i,j), B(i,j))
	 * <p/>Runs in O(rows*cols*f).
	 * @param f binary operator to apply
	 * @param b second argument Matrix B
	 * @return modified Matrix this
	 */
	public Matrix inplaceElementWise(DoubleBinaryOperator f, Matrix b){
		if(cols != b.cols || rows != b.rows){
			throw new MatrixDimensionMismatchException();
		}
		return fill((elem) -> f.applyAsDouble(internalGetValueAt(elem), b.internalGetValueAt(elem)));
	}
	
	/**Modifies and returns this Matrix A, such that for each index (i,j), A(i,j) <- f(A(i,j), B(i,j), C(i,j))
	 * <p/>Runs in O(rows*cols*f).
	 * @param f ternary operator to apply
	 * @param b second argument matrix B
	 * @param c third argument matrix C
	 * @return modified Matrix this
	 */
	public Matrix inplaceElementWise(DoubleTernaryOperator f, Matrix b, Matrix c){
		if(cols != b.cols || rows != b.rows || cols != c.cols || rows != c.rows){
			throw new MatrixDimensionMismatchException();
		}
		return fill((elem) -> f.applyAsDouble(internalGetValueAt(elem), b.internalGetValueAt(elem), c.internalGetValueAt(elem)));
	}
	
	/**Modifies and returns this Matrix A, such that for each index (i,j), A(i,j) <- f(A(i,j), b)
	 * <p/>Runs in O(rows*cols*f).
	 * @param f binary operator to apply
	 * @param b second argument scalar
	 * @return modified Matrix this
	 */
	public Matrix inplaceScalarOp(DoubleBinaryOperator f, double b){
		return fill((elem) -> f.applyAsDouble(internalGetValueAt(elem), b));
	}
	
	public long countWhere(DoublePredicate doublePredicate){
		return stream().filter(doublePredicate).count();
	}
	
	public MatrixLazy lazy(){
		return new MatrixLazy(this);
	}
	
	public Matrix cacheIfLazy(){
		return this;
	}
	
	public double det(){
		if(cols != rows)
			throw new MatrixNotSquareException(rows, cols);
		//TODO: factorization-based efficient impl.
		return recursiveDet(true);
	}
	
	protected double recursiveDet(boolean parallel){
		if(cols == 1){
			return getValueAt(0);
		}else if(cols == 2){
			return internalGetValueAt(0,0) * internalGetValueAt(1,1)
					- internalGetValueAt(0,1) * internalGetValueAt(1,0);
		}else {
			final MatrixMaskedRowView bottom = exceptRowView(0);
			IntStream intStream = parallel ? IntStream.range(0, cols).parallel() : IntStream.range(0, cols).sequential();
			return intStream.mapToDouble((col) ->
					Math.pow(-1, col) * internalGetValueAt(0, col) * bottom.exceptColView(col).recursiveDet(false))
					.sum();
		}
	}
	
	
	public Stream<MatrixRowView> rowViewStream(){
		return IntStream.range(0, rows).mapToObj(this::getRowView);
	}
	
	public Stream<MatrixColView> colViewStream(){
		return IntStream.range(0, rows).mapToObj(this::getColumnView);
	}
	
	
	public boolean equals(Matrix other) {
		if(rows != other.rows || cols != other.cols)
			return false;
		return IntStream.range(0, elems).parallel().allMatch((elem) ->
				Double.compare(internalGetValueAt(elem), other.internalGetValueAt(elem)) == 0);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append('[');
		for(int row = 0; row < rows; row++){
			if(row > 0) builder.append(',');
			builder.append(System.lineSeparator());
			builder.append('[');
			for(int col = 0; col < cols; col++){
				if(col > 0) builder.append(',');
				builder.append(internalGetValueAt(row,col));
			}
			builder.append(']');
		}
		builder.append(System.lineSeparator());
		builder.append(']');
		
		return builder.toString();
	}
	
	public static Matrix valueOf(String s){
		ArrayList<ArrayList<Double>> values = new ArrayList<>();
		ArrayList<Double> currentRow = new ArrayList<>();
		StringBuilder currentValue = new StringBuilder();
		boolean betweenRows = false;
		for(char c : s.toCharArray()){
			switch(c){
				case '[':
					if(betweenRows){
						currentRow = new ArrayList<>();
						currentValue = new StringBuilder();
						betweenRows = false;
					}else{
						betweenRows = true;
					}
					break;
				case ']':
					if(betweenRows){
						betweenRows = false;
					}else {
						if(currentValue.length() > 0){
							currentRow.add(Double.valueOf(currentValue.toString()));
						}
						values.add(currentRow);
						betweenRows = true;
					}
					break;
				case ',':
					if(!betweenRows){
						if(currentValue.length() > 0){
							currentRow.add(Double.valueOf(currentValue.toString()));
							currentValue = new StringBuilder();
						}
					}
					break;
				default:
					if(Character.isDigit(c)){
						currentValue.append(c);
					}
			}
		}
		
		int rows = values.size();
		int cols = values.stream().mapToInt(ArrayList::size).max().orElseThrow();
		double[][] doubles = new double[rows][cols];
		for(int row = 0; row < rows; row++){
			for(int col = 0; col < values.get(row).size(); col++){
				doubles[row][col] = values.get(row).get(col);
			}
		}
		
		return filledWith(doubles);
	}
	
	Object writeReplace() throws ObjectStreamException{
		return new SerializedMatrix(this.toString());
	}
}
