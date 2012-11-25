package jama;

import static jama.MatrixAsserts.assertMatrixEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import jama.CholeskyDecomposition;
import jama.EigenvalueDecomposition;
import jama.LUDecomposition;
import jama.Matrix;
import jama.QRDecomposition;
import jama.SingularValueDecomposition;

import org.junit.Before;
import org.junit.Test;


public class MatrixAlgebraTest {

	private final double[] columnwise = { 1., 2., 3., 4., 5., 6., 7., 8., 9., 10., 11., 12. };
	private final double[][] tvals = { { 1., 2., 3. }, { 4., 5., 6. }, { 7., 8., 9. }, { 10., 11., 12. } };
	private final double[][] pvals = { { 4., 1., 1. }, { 1., 2., 3. }, { 1., 3., 6. } };
	private final double[][] evals = { { 0., 1., 0., 0. }, { 1., 0., 2.e-7, 0. }, { 0., -2.e-7, 0., 1. }, { 0., 0., 1., 0. } };
	private final double[][] subavals = { { 5., 8., 11. }, { 6., 9., 12. } };
	private final double[][] square = { { 166., 188., 210. }, { 188., 214., 240. }, { 210., 240., 270. } };
	private final double[][] sqSolution = { { 13. }, { 15. } };
	private final double[][] condmat = { { 1., 3. }, { 7., 9. } };
	private final double[] rowVector = { 3.0, 2.0, 1.0 };
	private final double[] colVector = { 3.0, 2.0, 1.0, 0.0 };

	private double[][] avals;
	private double[][] rankdef;

	// leading dimension of intended test Matrices
	private final int validld = 3;

	// leading dimension which is valid, but nonconforming
	private final int validld2 = 4;

	private final double columnsummax = 33.;
	private final double rowsummax = 30.;
	private final double sumofdiagonals = 15;
	private final double sumofsquares = 650;

	private Matrix A, B, T;

	/* ==================================== */

	@Before
	public void setUp() {
		avals = new double[][] { { 1., 4., 7., 10. }, { 2., 5., 8., 11. }, { 3., 6., 9., 12. } };
		rankdef = avals;
		A = new Matrix(columnwise, validld);
		B = new Matrix(columnwise, validld2);
		T = new Matrix(tvals);
	}

	@Test
	public void testTranspose() {
		assertEquals(A.transpose(), T);
	}

	@Test
	public void testNorm1() {
		assertEquals(columnsummax, A.norm1(), 0.0);
	}

	@Test
	public void testInfinityNorm() {
		assertEquals(rowsummax, A.normInf(), 0.0);
	}

	@Test
	public void testNormF() {
		assertEquals(Math.sqrt(sumofsquares), A.normF(), 0.001);
	}

	@Test
	public void testSumOfDiagonals() {
		assertEquals(sumofdiagonals, A.trace(), 0.0);
	}

	@Test
	public void testDeterminant() {
		assertEquals(0.0, A.getMatrix(0, A.getRowDimension() - 1, 0, A.getRowDimension() - 1).det(), 0.0);
	}

	@Test
	public void testSquare() {
		assertEquals(new Matrix(square), A.times(A.transpose()));
	}

	@Test
	public void testTimesScalar() {
		assertEquals("Scalarmultiplication with one must be identical matrix", A, A.times(1.0));
		Matrix TMP = new Matrix(A.getRowDimension(), A.getColumnDimension(), 0.0);
		assertEquals(TMP, A.times(0.0));
	}

	@Test
	public void testTimesEqualsScalar() {
		Matrix C = A.timesEquals(1.0);
		assertEquals("Scalarmultiplication with one must be identical matrix", A, C);
		assertTrue("Must return the identical Matrix", A == C);
	}

	@Test
	public void testTimesRowVector() {
		Matrix matrix = A.timesRowVector(rowVector);
		assertEquals(10.0, matrix.get(0, 0), 0.0);
		assertEquals(28.0, matrix.get(0, 1), 0.0);
		assertEquals(46.0, matrix.get(0, 2), 0.0);
		assertEquals(64.0, matrix.get(0, 3), 0.0);
	}

	@Test
	public void testTimesColumnVector() {
		Matrix matrix = A.timesColumnVector(colVector);
		assertEquals(18.0, matrix.get(0, 0), 0.0);
		assertEquals(24.0, matrix.get(1, 0), 0.0);
		assertEquals(30.0, matrix.get(2, 0), 0.0);
	}

	@Test(timeout = 500L)
	public void testQRDecomposition() {
		Matrix TMP = new Matrix(columnwise, 4);
		QRDecomposition QR = TMP.qr();
		Matrix C = QR.getR();
		assertMatrixEquals(QR.getQ().times(C), TMP, 0.001);
	}

	@Test(timeout = 500L)
	public void testSingularValueDecomposition() {
		SingularValueDecomposition SVD = B.svd();
		assertMatrixEquals(SVD.getU().times(SVD.getS().times(SVD.getV().transpose())), B, 0.0001);
	}

	@Test(timeout = 500L)
	public void testSingularValues() {
		Matrix TMP = new Matrix(condmat);
		SingularValueDecomposition SVD = TMP.svd();
		double[] singularvalues = SVD.getSingularValues();
		assertEquals(singularvalues[0] / singularvalues[Math.min(TMP.getRowDimension(), TMP.getColumnDimension()) - 1], TMP.cond(), 0.0001);
	}

	@Test(timeout = 500L)
	public void testRank() {
		Matrix DEF = new Matrix(rankdef);
		assertEquals(Math.min(DEF.getRowDimension(), DEF.getColumnDimension()) - 1, DEF.rank(), 0.0001);
	}

	@Test(timeout = 500L)
	public void testLUDecomposition() {
		int n = B.getColumnDimension();
		Matrix LUMATRIX = B.getMatrix(0, n - 1, 0, n - 1);
		LUMATRIX.set(0, 0, 0.);
		LUDecomposition LU = LUMATRIX.lu();
		assertMatrixEquals(LU.getL().times(LU.getU()), LUMATRIX.getMatrix(LU.getPivot(), 0, n - 1), 0.0001);
	}

	@Test(timeout = 500L)
	public void testInverse() {
		int n = B.getColumnDimension();
		Matrix INV = B.getMatrix(0, n - 1, 0, n - 1);
		INV.set(0, 0, 0.);
		Matrix X = INV.inverse();
		assertMatrixEquals(Matrix.identity(3, 3), INV.times(X), 0.0001);
	}

	@Test(timeout = 500L)
	public void testSolve() {
		Matrix SUB = new Matrix(subavals);
		Matrix O = new Matrix(SUB.getRowDimension(), 1, 1.0);
		Matrix SOL = new Matrix(sqSolution);
		Matrix SQ = SUB.getMatrix(0, SUB.getRowDimension() - 1, 0, SUB.getRowDimension() - 1);
		assertMatrixEquals(O, SQ.solve(SOL), 0.0001);
	}

	@Test(timeout = 500L)
	public void testCholeskyDecomposition() {
		Matrix P = new Matrix(pvals);
		CholeskyDecomposition Chol = P.chol();
		Matrix L = Chol.getL();
		assertEquals(L.times(L.transpose()), P);
	}

	@Test(timeout = 500L)
	public void testCholeskyDecompositionSolve() {
		Matrix P = new Matrix(pvals);
		CholeskyDecomposition Chol = P.chol();
		Matrix X = Chol.solve(Matrix.identity(3, 3));
		assertMatrixEquals(Matrix.identity(3, 3), P.times(X), 0.0001);
	}

	@Test(timeout = 500L)
	public void testEigenvalueDecomposition() {
		Matrix B = new Matrix(pvals);
		EigenvalueDecomposition Eig = B.eig();
		Matrix D = Eig.getD();
		Matrix V = Eig.getV();

		assertMatrixEquals(B.times(V), V.times(D), 0.0001);

		B = new Matrix(evals);
		Eig = B.eig();
		D = Eig.getD();
		V = Eig.getV();
		assertMatrixEquals(B.times(V), V.times(D), 0.0001);
	}

	@Test(timeout = 500L)
	public void testEigenvalueDecompositionWithComplexValues() {
		double[][] vals = new double[][] { { 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 1 }, { 0, 0, 0, 1, 0 }, { 1, 1, 0, 0, 1 }, { 1, 0, 1, 0, 1 } };
		Matrix M = new Matrix(vals);
		M.eig();
	}

}
