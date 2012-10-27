package Jama;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StreamTokenizer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

/**
 * Matrix which stores values only with double precision. This saves ~50% memory
 * space in comparison to double precision.
 * 
 * @author Nepomuk Seiler
 * @since 1.1.0
 * 
 */
public class FloatMatrix implements Cloneable, Serializable {

	private static final long serialVersionUID = 5318241614427787535L;

	/**
	 * Array for internal storage of elements.
	 * 
	 * @serial internal array storage.
	 */
	private float[][] A;

	/**
	 * Row and column dimensions.
	 * 
	 * @serial row dimension.
	 * @serial column dimension.
	 */
	private int m, n;

	/*
	 * ------------------------ Constructors ------------------------
	 */

	/**
	 * Construct an m-by-n matrix of zeros.
	 * 
	 * @param m
	 *            Number of rows.
	 * @param n
	 *            Number of colums.
	 */

	public FloatMatrix(int m, int n) {
		this.m = m;
		this.n = n;
		A = new float[m][n];
	}

	/**
	 * Construct an m-by-n constant matrix.
	 * 
	 * @param m
	 *            Number of rows.
	 * @param n
	 *            Number of colums.
	 * @param s
	 *            Fill the matrix with this scalar value.
	 */

	public FloatMatrix(int m, int n, float s) {
		this.m = m;
		this.n = n;
		A = new float[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				A[i][j] = s;
			}
		}
	}

	/**
	 * Construct a matrix from a 2-D array.
	 * 
	 * @param A
	 *            Two-dimensional array of floats.
	 * @exception IllegalArgumentException
	 *                All rows must have the same length
	 * @see #constructWithCopy
	 */

	public FloatMatrix(float[][] A) {
		m = A.length;
		n = A[0].length;
		for (int i = 0; i < m; i++) {
			if (A[i].length != n) {
				throw new IllegalArgumentException("All rows must have the same length.");
			}
		}
		this.A = A;
	}

	/**
	 * Construct a matrix quickly without checking arguments.
	 * 
	 * @param A
	 *            Two-dimensional array of floats.
	 * @param m
	 *            Number of rows.
	 * @param n
	 *            Number of colums.
	 */

	public FloatMatrix(float[][] A, int m, int n) {
		this.A = A;
		this.m = m;
		this.n = n;
	}

	/**
	 * Construct a matrix from a one-dimensional packed array
	 * 
	 * @param vals
	 *            One-dimensional array of floats, packed by columns (ala
	 *            Fortran).
	 * @param m
	 *            Number of rows.
	 * @exception IllegalArgumentException
	 *                Array length must be a multiple of m.
	 */

	public FloatMatrix(float vals[], int m) {
		this.m = m;
		n = (m != 0 ? vals.length / m : 0);
		if (m * n != vals.length) {
			throw new IllegalArgumentException("Array length must be a multiple of m.");
		}
		A = new float[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				A[i][j] = vals[i + j * m];
			}
		}
	}

	/*
	 * ------------------------ Public Methods ------------------------
	 */

	/**
	 * Construct a matrix from a copy of a 2-D array.
	 * 
	 * @param A
	 *            Two-dimensional array of floats.
	 * @exception IllegalArgumentException
	 *                All rows must have the same length
	 */

	public static FloatMatrix constructWithCopy(float[][] A) {
		int m = A.length;
		int n = A[0].length;
		FloatMatrix X = new FloatMatrix(m, n);
		float[][] C = X.getArray();
		for (int i = 0; i < m; i++) {
			if (A[i].length != n) {
				throw new IllegalArgumentException("All rows must have the same length.");
			}
			for (int j = 0; j < n; j++) {
				C[i][j] = A[i][j];
			}
		}
		return X;
	}

	/**
	 * Make a deep copy of a matrix
	 */

	public FloatMatrix copy() {
		FloatMatrix X = new FloatMatrix(m, n);
		float[][] C = X.getArray();
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				C[i][j] = A[i][j];
			}
		}
		return X;
	}

	/**
	 * Clone the FloatMatrix object.
	 */

	public Object clone() {
		return this.copy();
	}

	/**
	 * Access the internal two-dimensional array.
	 * 
	 * @return Pointer to the two-dimensional array of matrix elements.
	 */

	public float[][] getArray() {
		return A;
	}

	/**
	 * Copy the internal two-dimensional array.
	 * 
	 * @return Two-dimensional array copy of matrix elements.
	 */

	public float[][] getArrayCopy() {
		float[][] C = new float[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				C[i][j] = A[i][j];
			}
		}
		return C;
	}

	/**
	 * Make a one-dimensional column packed copy of the internal array.
	 * 
	 * @return FloatMatrix elements packed in a one-dimensional array by
	 *         columns.
	 */

	public float[] getColumnPackedCopy() {
		float[] vals = new float[m * n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				vals[i + j * m] = A[i][j];
			}
		}
		return vals;
	}

	/**
	 * Make a one-dimensional row packed copy of the internal array.
	 * 
	 * @return FloatMatrix elements packed in a one-dimensional array by rows.
	 */

	public float[] getRowPackedCopy() {
		float[] vals = new float[m * n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				vals[i * n + j] = A[i][j];
			}
		}
		return vals;
	}

	/**
	 * Get row dimension.
	 * 
	 * @return m, the number of rows.
	 */

	public int getRowDimension() {
		return m;
	}

	/**
	 * Get column dimension.
	 * 
	 * @return n, the number of columns.
	 */

	public int getColumnDimension() {
		return n;
	}

	/**
	 * Get a single element.
	 * 
	 * @param i
	 *            Row index.
	 * @param j
	 *            Column index.
	 * @return A(i,j)
	 * @exception ArrayIndexOutOfBoundsException
	 */

	public float get(int i, int j) {
		return A[i][j];
	}

	/**
	 * Get a submatrix.
	 * 
	 * @param i0
	 *            Initial row index
	 * @param i1
	 *            Final row index
	 * @param j0
	 *            Initial column index
	 * @param j1
	 *            Final column index
	 * @return A(i0:i1,j0:j1)
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */

	public FloatMatrix getFloatMatrix(int i0, int i1, int j0, int j1) {
		FloatMatrix X = new FloatMatrix(i1 - i0 + 1, j1 - j0 + 1);
		float[][] B = X.getArray();
		try {
			for (int i = i0; i <= i1; i++) {
				for (int j = j0; j <= j1; j++) {
					B[i - i0][j - j0] = A[i][j];
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
		return X;
	}

	/**
	 * Get a submatrix.
	 * 
	 * @param r
	 *            Array of row indices.
	 * @param c
	 *            Array of column indices.
	 * @return A(r(:),c(:))
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */

	public FloatMatrix getFloatMatrix(int[] r, int[] c) {
		FloatMatrix X = new FloatMatrix(r.length, c.length);
		float[][] B = X.getArray();
		try {
			for (int i = 0; i < r.length; i++) {
				for (int j = 0; j < c.length; j++) {
					B[i][j] = A[r[i]][c[j]];
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
		return X;
	}

	/**
	 * Get a submatrix.
	 * 
	 * @param i0
	 *            Initial row index
	 * @param i1
	 *            Final row index
	 * @param c
	 *            Array of column indices.
	 * @return A(i0:i1,c(:))
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */

	public FloatMatrix getFloatMatrix(int i0, int i1, int[] c) {
		FloatMatrix X = new FloatMatrix(i1 - i0 + 1, c.length);
		float[][] B = X.getArray();
		try {
			for (int i = i0; i <= i1; i++) {
				for (int j = 0; j < c.length; j++) {
					B[i - i0][j] = A[i][c[j]];
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
		return X;
	}

	/**
	 * Get a submatrix.
	 * 
	 * @param r
	 *            Array of row indices.
	 * @param i0
	 *            Initial column index
	 * @param i1
	 *            Final column index
	 * @return A(r(:),j0:j1)
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */

	public FloatMatrix getFloatMatrix(int[] r, int j0, int j1) {
		FloatMatrix X = new FloatMatrix(r.length, j1 - j0 + 1);
		float[][] B = X.getArray();
		try {
			for (int i = 0; i < r.length; i++) {
				for (int j = j0; j <= j1; j++) {
					B[i][j - j0] = A[r[i]][j];
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
		return X;
	}

	/**
	 * Set a single element.
	 * 
	 * @param i
	 *            Row index.
	 * @param j
	 *            Column index.
	 * @param s
	 *            A(i,j).
	 * @exception ArrayIndexOutOfBoundsException
	 */

	public void set(int i, int j, float s) {
		A[i][j] = s;
	}

	/**
	 * Set a submatrix.
	 * 
	 * @param i0
	 *            Initial row index
	 * @param i1
	 *            Final row index
	 * @param j0
	 *            Initial column index
	 * @param j1
	 *            Final column index
	 * @param X
	 *            A(i0:i1,j0:j1)
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */

	public void setFloatMatrix(int i0, int i1, int j0, int j1, FloatMatrix X) {
		try {
			for (int i = i0; i <= i1; i++) {
				for (int j = j0; j <= j1; j++) {
					A[i][j] = X.get(i - i0, j - j0);
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
	}

	/**
	 * Set a submatrix.
	 * 
	 * @param r
	 *            Array of row indices.
	 * @param c
	 *            Array of column indices.
	 * @param X
	 *            A(r(:),c(:))
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */

	public void setFloatMatrix(int[] r, int[] c, FloatMatrix X) {
		try {
			for (int i = 0; i < r.length; i++) {
				for (int j = 0; j < c.length; j++) {
					A[r[i]][c[j]] = X.get(i, j);
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
	}

	/**
	 * Set a submatrix.
	 * 
	 * @param r
	 *            Array of row indices.
	 * @param j0
	 *            Initial column index
	 * @param j1
	 *            Final column index
	 * @param X
	 *            A(r(:),j0:j1)
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */

	public void setFloatMatrix(int[] r, int j0, int j1, FloatMatrix X) {
		try {
			for (int i = 0; i < r.length; i++) {
				for (int j = j0; j <= j1; j++) {
					A[r[i]][j] = X.get(i, j - j0);
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
	}

	/**
	 * Set a submatrix.
	 * 
	 * @param i0
	 *            Initial row index
	 * @param i1
	 *            Final row index
	 * @param c
	 *            Array of column indices.
	 * @param X
	 *            A(i0:i1,c(:))
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */

	public void setFloatMatrix(int i0, int i1, int[] c, FloatMatrix X) {
		try {
			for (int i = i0; i <= i1; i++) {
				for (int j = 0; j < c.length; j++) {
					A[i][c[j]] = X.get(i - i0, j);
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
	}

	/**
	 * FloatMatrix transpose.
	 * 
	 * @return A'
	 */

	public FloatMatrix transpose() {
		FloatMatrix X = new FloatMatrix(n, m);
		float[][] C = X.getArray();
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				C[j][i] = A[i][j];
			}
		}
		return X;
	}

	/**
	 * One norm
	 * 
	 * @return maximum column sum.
	 */

	public float norm1() {
		float f = 0;
		for (int j = 0; j < n; j++) {
			float s = 0;
			for (int i = 0; i < m; i++) {
				s += Math.abs(A[i][j]);
			}
			f = Math.max(f, s);
		}
		return f;
	}

	/**
	 * Two norm
	 * 
	 * @return maximum singular value.
	 */

	public float norm2() {
		// FIXME
		// return (new SingularValueDecomposition(this).norm2());
		// return 0f;
		throw new UnsupportedOperationException("Not implemented with float yet");
	}

	/**
	 * Infinity norm
	 * 
	 * @return maximum row sum.
	 */

	public float normInf() {
		float f = 0;
		for (int i = 0; i < m; i++) {
			float s = 0;
			for (int j = 0; j < n; j++) {
				s += Math.abs(A[i][j]);
			}
			f = Math.max(f, s);
		}
		return f;
	}

	/**
	 * Frobenius norm
	 * 
	 * @return sqrt of sum of squares of all elements.
	 */

	public float normF() {
		// FIXME
		float f = 0f;
		/*
		 * for (int i = 0; i < m; i++) { for (int j = 0; j < n; j++) { f =
		 * Maths.hypot(f, A[i][j]); } }
		 */
		// return f;
		throw new UnsupportedOperationException("Not implemented with float yet");
	}

	/**
	 * Unary minus
	 * 
	 * @return -A
	 */

	public FloatMatrix uminus() {
		FloatMatrix X = new FloatMatrix(m, n);
		float[][] C = X.getArray();
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				C[i][j] = -A[i][j];
			}
		}
		return X;
	}

	/**
	 * C = A + B
	 * 
	 * @param B
	 *            another matrix
	 * @return A + B
	 */

	public FloatMatrix plus(FloatMatrix B) {
		checkFloatMatrixDimensions(B);
		FloatMatrix X = new FloatMatrix(m, n);
		float[][] C = X.getArray();
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				C[i][j] = A[i][j] + B.A[i][j];
			}
		}
		return X;
	}

	/**
	 * A = A + B
	 * 
	 * @param B
	 *            another matrix
	 * @return A + B
	 */

	public FloatMatrix plusEquals(FloatMatrix B) {
		checkFloatMatrixDimensions(B);
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				A[i][j] = A[i][j] + B.A[i][j];
			}
		}
		return this;
	}

	/**
	 * C = A - B
	 * 
	 * @param B
	 *            another matrix
	 * @return A - B
	 */

	public FloatMatrix minus(FloatMatrix B) {
		checkFloatMatrixDimensions(B);
		FloatMatrix X = new FloatMatrix(m, n);
		float[][] C = X.getArray();
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				C[i][j] = A[i][j] - B.A[i][j];
			}
		}
		return X;
	}

	/**
	 * A = A - B
	 * 
	 * @param B
	 *            another matrix
	 * @return A - B
	 */

	public FloatMatrix minusEquals(FloatMatrix B) {
		checkFloatMatrixDimensions(B);
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				A[i][j] = A[i][j] - B.A[i][j];
			}
		}
		return this;
	}

	/**
	 * Element-by-element multiplication, C = A.*B
	 * 
	 * @param B
	 *            another matrix
	 * @return A.*B
	 */

	public FloatMatrix arrayTimes(FloatMatrix B) {
		checkFloatMatrixDimensions(B);
		FloatMatrix X = new FloatMatrix(m, n);
		float[][] C = X.getArray();
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				C[i][j] = A[i][j] * B.A[i][j];
			}
		}
		return X;
	}

	/**
	 * Element-by-element multiplication in place, A = A.*B
	 * 
	 * @param B
	 *            another matrix
	 * @return A.*B
	 */

	public FloatMatrix arrayTimesEquals(FloatMatrix B) {
		checkFloatMatrixDimensions(B);
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				A[i][j] = A[i][j] * B.A[i][j];
			}
		}
		return this;
	}

	/**
	 * Element-by-element right division, C = A./B
	 * 
	 * @param B
	 *            another matrix
	 * @return A./B
	 */

	public FloatMatrix arrayRightDivide(FloatMatrix B) {
		checkFloatMatrixDimensions(B);
		FloatMatrix X = new FloatMatrix(m, n);
		float[][] C = X.getArray();
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				C[i][j] = A[i][j] / B.A[i][j];
			}
		}
		return X;
	}

	/**
	 * Element-by-element right division in place, A = A./B
	 * 
	 * @param B
	 *            another matrix
	 * @return A./B
	 */

	public FloatMatrix arrayRightDivideEquals(FloatMatrix B) {
		checkFloatMatrixDimensions(B);
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				A[i][j] = A[i][j] / B.A[i][j];
			}
		}
		return this;
	}

	/**
	 * Element-by-element left division, C = A.\B
	 * 
	 * @param B
	 *            another matrix
	 * @return A.\B
	 */

	public FloatMatrix arrayLeftDivide(FloatMatrix B) {
		checkFloatMatrixDimensions(B);
		FloatMatrix X = new FloatMatrix(m, n);
		float[][] C = X.getArray();
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				C[i][j] = B.A[i][j] / A[i][j];
			}
		}
		return X;
	}

	/**
	 * Element-by-element left division in place, A = A.\B
	 * 
	 * @param B
	 *            another matrix
	 * @return A.\B
	 */

	public FloatMatrix arrayLeftDivideEquals(FloatMatrix B) {
		checkFloatMatrixDimensions(B);
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				A[i][j] = B.A[i][j] / A[i][j];
			}
		}
		return this;
	}

	/**
	 * Multiply a matrix by a scalar, C = s*A
	 * 
	 * @param s
	 *            scalar
	 * @return s*A
	 */

	public FloatMatrix times(float s) {
		FloatMatrix X = new FloatMatrix(m, n);
		float[][] C = X.getArray();
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				C[i][j] = s * A[i][j];
			}
		}
		return X;
	}

	/**
	 * Multiply a matrix by a scalar in place, A = s*A
	 * 
	 * @param s
	 *            scalar
	 * @return replace A by s*A
	 */

	public FloatMatrix timesEquals(float s) {
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				A[i][j] = s * A[i][j];
			}
		}
		return this;
	}

	/**
	 * Linear algebraic matrix multiplication, A * B
	 * 
	 * @param B
	 *            another matrix
	 * @return FloatMatrix product, A * B
	 * @exception IllegalArgumentException
	 *                FloatMatrix inner dimensions must agree.
	 */

	public FloatMatrix times(FloatMatrix B) {
		if (B.m != n) {
			throw new IllegalArgumentException("FloatMatrix inner dimensions must agree.");
		}
		FloatMatrix X = new FloatMatrix(m, B.n);
		float[][] C = X.getArray();
		float[] Bcolj = new float[n];
		for (int j = 0; j < B.n; j++) {
			for (int k = 0; k < n; k++) {
				Bcolj[k] = B.A[k][j];
			}
			for (int i = 0; i < m; i++) {
				float[] Arowi = A[i];
				float s = 0;
				for (int k = 0; k < n; k++) {
					s += Arowi[k] * Bcolj[k];
				}
				C[i][j] = s;
			}
		}
		return X;
	}

	// FIXME all decompositions

	/**
	 * LU Decomposition
	 * 
	 * @return LUDecomposition
	 * @see LUDecomposition
	 */

	public LUDecomposition lu() {
		// return new LUDecomposition(this)
		// FIXME
		throw new UnsupportedOperationException("Not implemented with float yet");
	}

	/**
	 * QR Decomposition
	 * 
	 * @return QRDecomposition
	 * @see QRDecomposition
	 */

	public QRDecomposition qr() {
		// return new QRDecomposition(this);
		// FIXME
		throw new UnsupportedOperationException("Not implemented with float yet");
	}

	/**
	 * Cholesky Decomposition
	 * 
	 * @return CholeskyDecomposition
	 * @see CholeskyDecomposition
	 */

	public CholeskyDecomposition chol() {
		// return new CholeskyDecomposition(this);
		// FIXME
		throw new UnsupportedOperationException("Not implemented with float yet");
	}

	/**
	 * Singular Value Decomposition
	 * 
	 * @return SingularValueDecomposition
	 * @see SingularValueDecomposition
	 */

	public SingularValueDecomposition svd() {
		// return new SingularValueDecomposition(this);
		// FIXME
		throw new UnsupportedOperationException("Not implemented with float yet");
	}

	/**
	 * Eigenvalue Decomposition
	 * 
	 * @return EigenvalueDecomposition
	 * @see EigenvalueDecomposition
	 */

	public EigenvalueDecomposition eig() {
		// return new EigenvalueDecomposition(this);
		// FIXME
		throw new UnsupportedOperationException("Not implemented with float yet");
	}

	/**
	 * Solve A*X = B
	 * 
	 * @param B
	 *            right hand side
	 * @return solution if A is square, least squares solution otherwise
	 */

	public FloatMatrix solve(FloatMatrix B) {
		// return (m == n ? (new LUDecomposition(this)).solve(B) : (new
		// QRDecomposition(this)).solve(B));
		// FIXME
		throw new UnsupportedOperationException("Not implemented with float yet");
	}

	/**
	 * Solve X*A = B, which is also A'*X' = B'
	 * 
	 * @param B
	 *            right hand side
	 * @return solution if A is square, least squares solution otherwise.
	 */

	public FloatMatrix solveTranspose(FloatMatrix B) {
		return transpose().solve(B.transpose());
	}

	/**
	 * FloatMatrix inverse or pseudoinverse
	 * 
	 * @return inverse(A) if A is square, pseudoinverse otherwise.
	 */

	public FloatMatrix inverse() {
		// return solve(identity(m, m));
		// FIXME
		throw new UnsupportedOperationException("Not implemented with float yet");
	}

	/**
	 * FloatMatrix determinant
	 * 
	 * @return determinant
	 */

	public float det() {
		// return new LUDecomposition(this).det();
		// FIXME
		// return 0f;
		throw new UnsupportedOperationException("Not implemented with float yet");
	}

	/**
	 * FloatMatrix rank
	 * 
	 * @return effective numerical rank, obtained from SVD.
	 */

	public int rank() {
		// return new SingularValueDecomposition(this).rank();
		// FIXME
		// throw new
		// UnsupportedOperationException("Not implemented with float yet");
		// return 0;
		throw new UnsupportedOperationException("Not implemented with float yet");
	}

	/**
	 * FloatMatrix condition (2 norm)
	 * 
	 * @return ratio of largest to smallest singular value.
	 */

	public float cond() {
		// return new SingularValueDecomposition(this).cond();
		// FIXME
		throw new UnsupportedOperationException("Not implemented with float yet");
		// return 0f;
	}

	/**
	 * FloatMatrix trace.
	 * 
	 * @return sum of the diagonal elements.
	 */

	public float trace() {
		float t = 0;
		for (int i = 0; i < Math.min(m, n); i++) {
			t += A[i][i];
		}
		return t;
	}

	/**
	 * Generate matrix with random elements
	 * 
	 * @param m
	 *            Number of rows.
	 * @param n
	 *            Number of colums.
	 * @return An m-by-n matrix with uniformly distributed random elements.
	 */

	public static FloatMatrix random(int m, int n) {
		FloatMatrix A = new FloatMatrix(m, n);
		float[][] X = A.getArray();
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				X[i][j] = (float) Math.random();
			}
		}
		return A;
	}

	/**
	 * Generate identity matrix
	 * 
	 * @param m
	 *            Number of rows.
	 * @param n
	 *            Number of colums.
	 * @return An m-by-n matrix with ones on the diagonal and zeros elsewhere.
	 */

	public static FloatMatrix identity(int m, int n) {
		FloatMatrix A = new FloatMatrix(m, n);
		float[][] X = A.getArray();
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				X[i][j] = (i == j ? 1.0f : 0.0f);
			}
		}
		return A;
	}

	/**
	 * Print the matrix to stdout. Line the elements up in columns with a
	 * Fortran-like 'Fw.d' style format.
	 * 
	 * @param w
	 *            Column width.
	 * @param d
	 *            Number of digits after the decimal.
	 */

	public void print(int w, int d) {
		print(new PrintWriter(System.out, true), w, d);
	}

	/**
	 * Print the matrix to the output stream. Line the elements up in columns
	 * with a Fortran-like 'Fw.d' style format.
	 * 
	 * @param output
	 *            Output stream.
	 * @param w
	 *            Column width.
	 * @param d
	 *            Number of digits after the decimal.
	 */

	public void print(PrintWriter output, int w, int d) {
		DecimalFormat format = new DecimalFormat();
		format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
		format.setMinimumIntegerDigits(1);
		format.setMaximumFractionDigits(d);
		format.setMinimumFractionDigits(d);
		format.setGroupingUsed(false);
		print(output, format, w + 2);
	}

	/**
	 * Print the matrix to stdout. Line the elements up in columns. Use the
	 * format object, and right justify within columns of width characters. Note
	 * that is the matrix is to be read back in, you probably will want to use a
	 * NumberFormat that is set to US Locale.
	 * 
	 * @param format
	 *            A Formatting object for individual elements.
	 * @param width
	 *            Field width for each column.
	 * @see java.text.DecimalFormat#setDecimalFormatSymbols
	 */

	public void print(NumberFormat format, int width) {
		print(new PrintWriter(System.out, true), format, width);
	}

	// DecimalFormat is a little disappointing coming from Fortran or C's
	// printf.
	// Since it doesn't pad on the left, the elements will come out different
	// widths. Consequently, we'll pass the desired column width in as an
	// argument and do the extra padding ourselves.

	/**
	 * Print the matrix to the output stream. Line the elements up in columns.
	 * Use the format object, and right justify within columns of width
	 * characters. Note that is the matrix is to be read back in, you probably
	 * will want to use a NumberFormat that is set to US Locale.
	 * 
	 * @param output
	 *            the output stream.
	 * @param format
	 *            A formatting object to format the matrix elements
	 * @param width
	 *            Column width.
	 * @see java.text.DecimalFormat#setDecimalFormatSymbols
	 */

	public void print(PrintWriter output, NumberFormat format, int width) {
		output.println(); // start on new line.
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				String s = format.format(A[i][j]); // format the number
				int padding = Math.max(1, width - s.length()); // At _least_ 1
																// space
				for (int k = 0; k < padding; k++)
					output.print(' ');
				output.print(s);
			}
			output.println();
		}
		output.println(); // end with blank line.
	}

	/**
	 * Read a matrix from a stream. The format is the same the print method, so
	 * printed matrices can be read back in (provided they were printed using US
	 * Locale). Elements are separated by whitespace, all the elements for each
	 * row appear on a single line, the last row is followed by a blank line.
	 * 
	 * @param input
	 *            the input stream.
	 */

	public static FloatMatrix read(BufferedReader input) throws java.io.IOException {
		StreamTokenizer tokenizer = new StreamTokenizer(input);

		// Although StreamTokenizer will parse numbers, it doesn't recognize
		// scientific notation (E or D); however, Double.valueOf does.
		// The strategy here is to disable StreamTokenizer's number parsing.
		// We'll only get whitespace delimited words, EOL's and EOF's.
		// These words should all be numbers, for Double.valueOf to parse.

		tokenizer.resetSyntax();
		tokenizer.wordChars(0, 255);
		tokenizer.whitespaceChars(0, ' ');
		tokenizer.eolIsSignificant(true);
		java.util.Vector v = new java.util.Vector();

		// Ignore initial empty lines
		while (tokenizer.nextToken() == StreamTokenizer.TT_EOL)
			;
		if (tokenizer.ttype == StreamTokenizer.TT_EOF)
			throw new java.io.IOException("Unexpected EOF on matrix read.");
		do {
			v.addElement(Double.valueOf(tokenizer.sval)); // Read & store 1st
															// row.
		} while (tokenizer.nextToken() == StreamTokenizer.TT_WORD);

		int n = v.size(); // Now we've got the number of columns!
		float row[] = new float[n];
		for (int j = 0; j < n; j++)
			// extract the elements of the 1st row.
			row[j] = ((Double) v.elementAt(j)).floatValue();
		v.removeAllElements();
		v.addElement(row); // Start storing rows instead of columns.
		while (tokenizer.nextToken() == StreamTokenizer.TT_WORD) {
			// While non-empty lines
			v.addElement(row = new float[n]);
			int j = 0;
			do {
				if (j >= n)
					throw new java.io.IOException("Row " + v.size() + " is too long.");
				row[j++] = Double.valueOf(tokenizer.sval).floatValue();
			} while (tokenizer.nextToken() == StreamTokenizer.TT_WORD);
			if (j < n)
				throw new java.io.IOException("Row " + v.size() + " is too short.");
		}
		int m = v.size(); // Now we've got the number of rows.
		float[][] A = new float[m][];
		v.copyInto(A); // copy the rows out of the vector
		return new FloatMatrix(A);
	}

	/* ================================================ */
	/* ============= Hashcode and Equals ============== */
	/* ================================================ */

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(A);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FloatMatrix other = (FloatMatrix) obj;
		if (!Arrays.deepEquals(A, other.A))
			return false;
		return true;
	}

	/* ================================================ */
	/* ============= Private Methods ================== */
	/* ================================================ */

	/** Check if size(A) == size(B) **/

	private void checkFloatMatrixDimensions(FloatMatrix B) {
		if (B.m != m || B.n != n) {
			throw new IllegalArgumentException("FloatMatrix dimensions must agree.");
		}
	}
}
