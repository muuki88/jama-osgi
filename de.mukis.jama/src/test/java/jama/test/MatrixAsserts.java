package jama.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import jama.FloatMatrix;
import jama.Matrix;

/**
 * 
 * @author muki
 * 
 */
public class MatrixAsserts {

    public static void assertMatrixEquals(Matrix A, Matrix B, double delta) {
        assertEquals("Rowdimension must be equals", A.getRowDimension(), B.getRowDimension());
        assertEquals("Columndimension must be equals", A.getColumnDimension(), B.getColumnDimension());
        double[][] a = A.getArray();
        double[][] b = B.getArray();

        for (int i = 0; i < a.length; i++) {
            assertArrayEquals("Row " + i + " is not equals", a[i], b[i], delta);
        }
    }

    public static void assertMatrixEquals(FloatMatrix A, FloatMatrix B, float delta) {
        assertEquals("Rowdimension must be equals", A.getRowDimension(), B.getRowDimension());
        assertEquals("Columndimension must be equals", A.getColumnDimension(), B.getColumnDimension());
        float[][] a = A.getArray();
        float[][] b = B.getArray();

        for (int i = 0; i < a.length; i++) {
            assertArrayEquals("Row " + i + " is not equals", a[i], b[i], delta);
        }
    }

    public static void assertDeepArraysEquals(double[][] a1, double[][] a2) {
        assertEquals("Length must be the same", a1.length, a2.length);
        for (int i = 0; i < a1.length; i++) {
            assertArrayEquals("Array in row " + i + " are not equals", a1[i], a2[i], 0.0);
        }
    }

}
