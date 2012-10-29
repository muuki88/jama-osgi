package Jama.test;

import static Jama.test.MatrixAsserts.assertDeepArraysEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import Jama.Matrix;

/**
 * 
 * @author Nepomuk Seiler
 * 
 */
public class MatrixTest {

    private final double[] columnwise = { 1., 2., 3., 4., 5., 6., 7., 8., 9., 10., 11., 12. };
    private final double[] rowwise = { 1., 4., 7., 10., 2., 5., 8., 11., 3., 6., 9., 12. };
    private final double[][] rvals = { { 1., 4., 7. }, { 2., 5., 8., 11. }, { 3., 6., 9., 12. } };
    private final double[][] ivals = { { 1., 0., 0., 0. }, { 0., 1., 0., 0. }, { 0., 0., 1., 0. } };
    private final double[][] subavals = { { 5., 8., 11. }, { 6., 9., 12. } };

    private double[][] avals;

    // should trigger bad shape for construction with val
    private final int invalidld = 5;

    // leading dimension of intended test Matrices
    private final int validld = 3;

    // (raggedr,raggedc) should be out of bounds in ragged array
    private final int raggedr = 0;
    private final int raggedc = 4;

    // leading dimension which is valid, but nonconforming
    int nonconformld = 4;

    // rows and cols
    private final int rows = 3, cols = 4;

    private final int[] rowindexset = { 1, 2 };
    private final int[] badrowindexset = { 1, 3 };
    private final int[] columnindexset = { 1, 2, 3 };
    private final int[] badcolumnindexset = { 1, 2, 4 };

    // index ranges for sub Matrix
    private int ib = 1, ie = 2, jb = 1, je = 3;

    private Matrix A, B, M, R, S;

    /* ==================================== */

    @Before
    public void setUp() {
        avals = new double[][] { { 1., 4., 7., 10. }, { 2., 5., 8., 11. }, { 3., 6., 9., 12. } };
        A = new Matrix(columnwise, validld);
        B = new Matrix(avals);
        M = new Matrix(2, 3, 0.0);
        S = new Matrix(columnwise, nonconformld);
        R = Matrix.random(A.getRowDimension(), A.getColumnDimension());
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

    @Test(expected = IllegalArgumentException.class)
    public void testMinusWithNonConformantColumns() {
        S = A.minus(S);
    }

    @Test
    public void testMinusDifferenceOfIdentity() {
        assertEquals("difference of identical Matrices is nonzero, Subsequent use of minus should be suspect", 0.0, R.minus(R).norm1(), 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMinusEqualsConformanceCheck() {
        Matrix TMP = R.copy();
        TMP.minusEquals(R);
        TMP.minusEquals(S);
    }

    public void testMinusEqualsDifferenceOfIdentity() {
        Matrix Z = new Matrix(A.getRowDimension(), A.getColumnDimension());
        assertEquals("difference of identical Matrices is nonzero, Subsequent use of minus should be suspect", 0.0, R.minus(Z).norm1(), 0.0);
    }

    @Test
    public void testMinus() {
        Matrix TMP = Matrix.random(A.getRowDimension(), A.getColumnDimension());
        R.minus(TMP);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPlusWithNonConformantColumns() {
        S = A.plus(S);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPlusEqualsConformanceCheck() {
        Matrix TMP = R.copy();
        TMP.minusEquals(R);
        TMP.plusEquals(S);
    }

    @Test
    public void testMinusPlusRoundtrip() {
        Matrix TMP = Matrix.random(A.getRowDimension(), A.getColumnDimension());
        Matrix C = R.minus(TMP);
        assertEquals(R, C.plus(TMP));
    }

    @Test
    public void testMinusPlusEqualsRoundtrip() {
        Matrix TMP = Matrix.random(A.getRowDimension(), A.getColumnDimension());
        Matrix C = R.minus(TMP);
        C.plusEquals(TMP);
        assertEquals(C, R);
    }

    @Test
    public void testUnaryMinusRoundtrip() {
        Matrix TMP = new Matrix(A.getRowDimension(), A.getColumnDimension());
        Matrix C = R.uminus();
        assertEquals(TMP, C.plus(R));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testArrayLeftDivideConformanceCheck() {
        R.arrayLeftDivide(S);
    }

    @Test
    public void testArrayLeftDivide() {
        Matrix TMP = new Matrix(A.getRowDimension(), A.getColumnDimension(), 1.0);
        Matrix C = R.arrayLeftDivide(R);
        assertEquals(TMP, C);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testArrayLeftDivideEqualsConformanceCheck() {
        R.arrayLeftDivideEquals(S);
    }

    @Test
    public void testArrayLeftDivideEquals() {
        Matrix COMPARE = new Matrix(A.getRowDimension(), A.getColumnDimension(), 1.0);
        Matrix TMP = R.copy();
        TMP.arrayLeftDivideEquals(R);
        assertEquals(TMP, COMPARE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testArrayRightDivideConformanceCheck() {
        R.arrayRightDivide(S);
    }

    @Test
    public void testArrayRightDivide() {
        Matrix TMP = new Matrix(A.getRowDimension(), A.getColumnDimension(), 1.0);
        Matrix C = R.arrayRightDivide(R);
        assertEquals(TMP, C);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testArrayRightDivideEqualsConformanceCheck() {
        R.arrayRightDivideEquals(S);
    }

    @Test
    public void testArrayRightDivideEquals() {
        Matrix COMPARE = new Matrix(A.getRowDimension(), A.getColumnDimension(), 1.0);
        Matrix TMP = R.copy();
        TMP.arrayRightDivideEquals(R);
        assertEquals(TMP, COMPARE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTimesConformanceCheck() {
        S = A.arrayTimes(S);
    }

    @Test
    public void testTimes() {
        Matrix C = A.arrayTimes(B);
        assertEquals(A, C.arrayRightDivideEquals(B));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTimesEqualsConformanceCheck() {
        A.arrayTimesEquals(S);
    }

    @Test
    public void testTimesEquals() {
        Matrix TMP = A.copy();
        A.arrayTimesEquals(B);
        assertEquals(TMP, A.arrayRightDivideEquals(B));
    }

}
