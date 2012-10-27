package Jama.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import Jama.Matrix;

public class MatrixTest {

	private final double[] columnwise = { 1., 2., 3., 4., 5., 6., 7., 8., 9., 10., 11., 12. };
	private final double[] rowwise = { 1., 4., 7., 10., 2., 5., 8., 11., 3., 6., 9., 12. };
	private final double[][] rvals = { { 1., 4., 7. }, { 2., 5., 8., 11. }, { 3., 6., 9., 12. } };
	private final double[][] ivals = { { 1., 0., 0., 0. }, { 0., 1., 0., 0. }, { 0., 0., 1., 0. } };
	private final double[][] tvals = { { 1., 2., 3. }, { 4., 5., 6. }, { 7., 8., 9. }, { 10., 11., 12. } };
	private final double[][] subavals = { { 5., 8., 11. }, { 6., 9., 12. } };
	private final double[][] pvals = { { 4., 1., 1. }, { 1., 2., 3. }, { 1., 3., 6. } };
	private final double[][] evals = { { 0., 1., 0., 0. }, { 1., 0., 2.e-7, 0. }, { 0., -2.e-7, 0., 1. }, { 0., 0., 1., 0. } };
	private final double[][] square = { { 166., 188., 210. }, { 188., 214., 240. }, { 210., 240., 270. } };
	private final double[][] sqSolution = { { 13. }, { 15. } };
	private final double[][] condmat = { { 1., 3. }, { 7., 9. } };

	private double[][] avals;
	private double[][] rankdef;

	// should trigger bad shape for construction with val
	private final int invalidld = 5;

	// leading dimension of intended test Matrices
	private final int validld = 3;

	// (raggedr,raggedc) should be out of bounds in ragged array
	private final int raggedr = 0;
	private final int raggedc = 4;

	private final int rows = 3, cols = 4;

	private final int[] rowindexset = { 1, 2 };
	private final int[] badrowindexset = { 1, 3 };
	private final int[] columnindexset = { 1, 2, 3 };
	private final int[] badcolumnindexset = { 1, 2, 4 };
	private final double columnsummax = 33.;
	private final double rowsummax = 30.;
	private final double sumofdiagonals = 15;
	private final double sumofsquares = 650;

	// index ranges for sub Matrix
	int ib = 1, ie = 2, jb = 1, je = 3;

	private Matrix A, B, M;

	/* ==================================== */

	@Before
	public void setUp() {
		avals = new double[][] { { 1., 4., 7., 10. }, { 2., 5., 8., 11. }, { 3., 6., 9., 12. } };
		rankdef = avals;
		A = new Matrix(columnwise, validld);
		B = new Matrix(avals);
		M = new Matrix(2, 3, 0.0);
	}

	@After
	public void tearDown() {

	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidLengthInPackedConstructor() {
		new Matrix(columnwise, invalidld);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRaggedConstructor() {
		Matrix A = new Matrix(rvals);

		// would throw ArrayIndexOutOfBoundsException
		A.get(raggedr, raggedc);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRaggedCopyWithConstructor() {
		Matrix A = Matrix.constructWithCopy(rvals);
		A.get(raggedr, raggedc);
	}

	public void testConstructWithCopyConsistency() {
		double tmp = B.get(0, 0);
		avals[0][0] = 0.0;
		B.minus(A);
		avals[0][0] = tmp;
		B = Matrix.constructWithCopy(avals);
		tmp = B.get(0, 0);
		avals[0][0] = 0.0;
		assertEquals("copy not effected. Data visible outside", tmp - B.get(0, 0), 0.0, 0.0);
	}

	public void testIdentityMatrix() {
		avals[0][0] = columnwise[0];
		Matrix I = new Matrix(ivals);
		assertEquals("identity Matrix not successfully created", I, Matrix.identity(rows, cols));
		// check(I, Matrix.identity(3, 4));
	}

	/* ========================================== */

	@Test
	public void testGetArray() {
		double[][] barray = B.getArray();
		assertEquals("Array has not the correct size", rows, barray.length);
		assertEquals("Array has not the correct size", cols, barray[0].length);
		assertTrue("Reference must the same array", barray == avals);
	}

	@Test
	public void testGetArrayCopy() {
		double[][] barray = B.getArrayCopy();
		assertEquals("Array has not the correct size", rows, barray.length);
		assertEquals("Array has not the correct size", cols, barray[0].length);
		assertTrue("Reference must not be the same array", barray != avals);
		assertDeepArraysEquals(avals, barray);
	}

	@Test
	public void testGetRowDimension() {
		assertEquals("Row dimension is not correct", rows, A.getRowDimension());
	}

	@Test
	public void testGetColumnDimension() {
		assertEquals("Column dimension is not correct", cols, A.getColumnDimension());
	}

	@Test
	public void testTimesDouble() {
		assertEquals("Scalarmultiplication with one must be identical matrix", A, A.times(1.0));
	}

	@Test
	public void testTimesEquals() {
		Matrix C = A.timesEquals(1.0);
		assertEquals("Scalarmultiplication with one must be identical matrix", A, C);
		assertTrue("Must return the identical Matrix", A == C);
	}

	@Test
	public void testReferenceEquality() {
		double[][] a1 = new double[2][2];
		double[][] a2 = a1;
		a1[0][0] = 2.0;
		assertTrue("Must return the identical Matrix", a1 == a2);
	}

	@Test
	public void testTimesMatrixIdentity() {
		assertEquals("A times identity matrix should be A", A, Matrix.identity(rows, rows).times(A));
	}

	public void testColumnPackedCopy() {
		double[] bpacked = B.getColumnPackedCopy();
		assertArrayEquals("data not successfully (deep) copied by cols", columnwise, bpacked, 0.0);
	}

	@Test
	public void testRowPackedCopy() {
		double[] bpacked = B.getRowPackedCopy();
		assertArrayEquals("data not successfully (deep) copied by rows", rowwise, bpacked, 0.0);
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testOutOfBoundsAccessOnRows() {
		B.get(B.getRowDimension(), B.getColumnDimension() - 1);
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testOutOfBoundsAccessOnColumns() {
		B.get(B.getRowDimension() - 1, B.getColumnDimension());
	}

	@Test
	public void testGetterMethods() {
		assertEquals(avals[B.getRowDimension() - 1][B.getColumnDimension() - 1],
				B.get(B.getRowDimension() - 1, B.getColumnDimension() - 1), 0.0);

	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testOutOfBoundsSetBadRow() {
		B.set(B.getRowDimension(), B.getColumnDimension() - 1, 0.);
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testOutOfBoundsSetBadColumn() {
		B.set(B.getRowDimension() - 1, B.getColumnDimension(), 0.);
	}

	@Test
	public void testSetMethods() {
		B.set(ib, jb, 0.0);
		double tmp = B.get(ib, jb);
		assertEquals(0.0, tmp, 0.0);
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testTooLargeRowSubMatrix() {
		B.getMatrix(ib, ie + B.getRowDimension() + 1, jb, je);
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testTooLargeColumnsSubMatrix() {
		B.getMatrix(ib, ie, jb, je + B.getColumnDimension() + 1);
	}

	@Test
	public void testSubMatrix() {
		Matrix SUB = new Matrix(subavals);
		Matrix C = B.getMatrix(ib, ie, jb, je);
		assertEquals("Submatrices should be equals", SUB, C);
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testBadColumnIndexsetxSubMatrixFirstRow() {
		B.getMatrix(ib, ie, badcolumnindexset);
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testBadColumnIndexsetSubMatrixLastRow() {
		B.getMatrix(ib, ie + B.getRowDimension() + 1, columnindexset);
	}

	@Test
	public void testColumnIndexsetSubMatrix() {
		Matrix SUB = new Matrix(subavals);
		Matrix C = B.getMatrix(ib, ie, columnindexset);
		assertEquals("Submatrices should be equals", SUB, C);
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testBadRowIndexsetxSubMatrixFirstColumn() {
		B.getMatrix(badrowindexset, jb, je);
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testBadRowIndexsetSubMatrixLastColumn() {
		B.getMatrix(rowindexset, jb, je + B.getColumnDimension() + 1);
	}

	@Test
	public void testRowIndexsetSubMatrix() {
		Matrix SUB = new Matrix(subavals);
		Matrix C = B.getMatrix(rowindexset, jb, je);
		assertEquals("Submatrices should be equals", SUB, C);
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testBadRowIndexsetColumnIndexsetSubMatrix() {
		B.getMatrix(badrowindexset, columnindexset);
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testRowIndexsetBadColumnIndexsetSubMatrix() {
		B.getMatrix(rowindexset, badcolumnindexset);
	}

	@Test
	public void testRowIndexsetColumnIndexsetSubMatrix() {
		B.getMatrix(rowindexset, columnindexset);
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testSetMtrixWithBadRowindex() {
		B.setMatrix(ib, ie + B.getRowDimension() + 1, jb, je, M);
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testSetMatrixWithBadColumnindex() {
		B.setMatrix(ib, ie, jb, je + B.getColumnDimension() + 1, M);
	}

	@Test
	public void testSetMatrix() {
		B.setMatrix(ib, ie, jb, je, M);
		assertEquals(M, M.minus(B.getMatrix(ib, ie, jb, je)));
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testSetSubmatrixWithBadColumnIndexsetFirstRow() {
		B.setMatrix(ib, ie, badcolumnindexset, M);
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testSetSubmatrixWithBadColumnIndexsetLastRow() {
		B.setMatrix(ib, ie + B.getRowDimension() + 1, columnindexset, M);
	}

	@Test
	public void testSetSubmatrixWithColumnIndexset() {
		B.setMatrix(ib, ie, columnindexset, M);
		assertEquals(M, M.minus(B.getMatrix(ib, ie, columnindexset)));
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testSetSubmatrixWithBadRowIndexsetFirstRow() {
		B.setMatrix(rowindexset, jb, je + B.getColumnDimension() + 1, M);
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testSetSubmatrixWithBadRowIndexsetLastRow() {
		B.setMatrix(badrowindexset, jb, je, M);
	}

	@Test
	public void testSetSubmatrixWithRowIndexset() {
		B.setMatrix(rowindexset, jb, je, M);
		assertEquals(M, M.minus(B.getMatrix(ib, ie, columnindexset)));
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testSetSubmatrixWithBadRowIndexsetColumnIndexset() {
		B.setMatrix(badrowindexset, columnindexset, M);
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testSetSubmatrixWithRowIndexBadColumnIndexset() {
		B.setMatrix(rowindexset, badcolumnindexset, M);
	}

	@Test
	public void testSetSubmatrixWithRowIndexsetColumnIndexset() {
		B.setMatrix(rowindexset, columnindexset, M);
		assertEquals(M, M.minus(B.getMatrix(rowindexset, columnindexset)));
	}

	/* ========================================= */

	private void assertDeepArraysEquals(double[][] a1, double[][] a2) {
		assertEquals("Length must be the same", a1.length, a2.length);
		for (int i = 0; i < a1.length; i++) {
			assertArrayEquals("Array in row " + i + " are not equals", a1[i], a2[i], 0.0);
		}
	}

	private void check(Matrix X, Matrix Y) {
		double eps = Math.pow(2.0, -52.0);
		if (X.norm1() == 0. & Y.norm1() < 10 * eps)
			return;
		if (Y.norm1() == 0. & X.norm1() < 10 * eps)
			return;
		if (X.minus(Y).norm1() > 1000 * eps * Math.max(X.norm1(), Y.norm1())) {
			throw new RuntimeException("The norm of (X-Y) is too large: " + Double.toString(X.minus(Y).norm1()));
		}
	}
}
