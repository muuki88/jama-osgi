package jama.util;

import static jama.test.MatrixAsserts.assertMatrixEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import jama.FloatMatrix;

import java.io.IOException;

import org.bridj.Pointer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.nativelibs4java.opencl.CLContext;
import com.nativelibs4java.opencl.CLPlatform;
import com.nativelibs4java.opencl.JavaCL;

public class GPUTest {

    private final float[][] vals = { { 1f, 4f, 7f }, { 2f, 5f, 8f }, { 3f, 6f, 9f } };
    private final float[] valsColumn = { 1f, 4f, 7f, 2f, 5f, 8f, 3f, 6f, 9f };
    private CLContext context;

    @Before
    public void setUp() {
        context = JavaCL.createBestContext();
    }

    @After
    public void tearDown() {
        context.release();
    }

    @Test
    public void testGPUAvailable() {
        CLPlatform[] platforms = JavaCL.listPlatforms();
        CLPlatform[] gpuPlatforms = JavaCL.listGPUPoweredPlatforms();
        assertTrue("There must a platform available", platforms.length != 0);
        assertTrue("There must a platform available", gpuPlatforms.length != 0);
    }

    @Test
    public void testMatrixMultiplication() throws IOException {
        FloatMatrix A = FloatMatrix.random(8, 8);
        FloatMatrix B = FloatMatrix.random(8, 8);
        FloatMatrix actual = GPU.multiply(A, B);

        FloatMatrix expected = A.times(B);

        assertMatrixEquals(expected, actual, 0.001f);
    }

    @Test
    public void testMatrixMultiplicationFails() throws IOException {
        FloatMatrix A = FloatMatrix.random(16, 16);
        FloatMatrix B = FloatMatrix.random(16, 16);
        FloatMatrix actual = GPU.multiply(A, B);

        FloatMatrix expected = A.times(B);

        System.out.println("EXPECTED [ A.times(B) ]");
        expected.print(5, 5);
        System.out.println("ACTUAL [ GPU.multiply(A, B) ]");
        actual.print(5, 5);

        assertMatrixEquals(expected, actual, 0.001f);
    }

    @Test
    public void testMatrixPointerRoundtrip() {
        FloatMatrix matrix = new FloatMatrix(vals);

        Pointer<Float> pointer = GPU.matrixToPointer(matrix);
        FloatMatrix matrix2 = GPU.pointerToFloatMatrix(pointer, matrix.getRowDimension(), matrix.getColumnDimension());

        assertMatrixEquals(matrix, matrix2, 0f);
    }

    @Test
    public void testMatrixToPointer() {
        FloatMatrix matrix = new FloatMatrix(valsColumn, 3);

        Pointer<Float> pointer = GPU.matrixToPointer(matrix);
        for (int row = 0; row < matrix.getRowDimension(); row++) {
            for (int col = 0; col < matrix.getColumnDimension(); col++) {
                assertEquals("Must be equals", matrix.get(row, col), pointer.get(row + matrix.getRowDimension() * col).floatValue(), 0f);
            }
        }
    }

    @Test
    public void testColumnwiseMatrix() {
        FloatMatrix matrix = new FloatMatrix(valsColumn, 3);
        FloatMatrix matrix2 = new FloatMatrix(vals).transpose();
        assertMatrixEquals(matrix, matrix2, 0f);
    }
}
