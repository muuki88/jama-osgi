package jama.util;

import static jama.MatrixAsserts.assertMatrixEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import jama.FloatMatrix;
import jama.rules.PrintMatrixOnFail;

import java.io.IOException;
import java.util.Arrays;

import org.bridj.Pointer;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.nativelibs4java.opencl.CLContext;
import com.nativelibs4java.opencl.CLDevice;
import com.nativelibs4java.opencl.CLPlatform;
import com.nativelibs4java.opencl.JavaCL;

/**
 * <p>
 * Run this test with {@code javacl.debug=true} as VM argument to enable
 * KernelDebuggin
 * </p>
 * 
 * @author Nepomuk Seiler
 * @see <a
 *      href="http://code.google.com/p/javacl/wiki/DebuggingKernels">DebuggingKernels</a>
 * 
 */
public class GPUTest {

    private final float[][] vals = { { 1f, 4f, 7f }, { 2f, 5f, 8f }, { 3f, 6f, 9f } };
    private final float[] valsColumn = { 1f, 4f, 7f, 2f, 5f, 8f, 3f, 6f, 9f };
    private CLContext context;

    @Rule
    public PrintMatrixOnFail onFail = new PrintMatrixOnFail();

    @Rule
    public TestName name = new TestName();

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

        CLDevice[] devices = platforms[0].listGPUDevices(true);
        assertTrue("There must a device available", devices.length != 0);
        printDeviceInfo(devices[0]);
    }

    @Test
    public void testSquarePowerOfTwoMatrixMultiplication() throws IOException {
        int exponent = 10;
        for (int e = 3; e <= exponent; e++) {
            int dim = (int) Math.pow(2, e);
            checkMultiplicationFloat(dim, dim, dim);
        }
    }

    @Test
    public void testNonSquarePowerOfTwoMatrixMultiplication() throws IOException {
        int exponent = 10;
        for (int e1 = 3; e1 <= exponent; e1++) {
            for (int e2 = 3; e2 <= exponent; e2++) {
                int m = (int) Math.pow(2, e1);
                int n = (int) Math.pow(2, e2);
                checkMultiplicationFloat(m, m, n);
            }
        }
    }

    @Test
    public void testSquareMatrixMultiplication() throws IOException {
        int[] dimension = new int[] { 8, 16, 17, 23, 32, 256 };
        for (int dim : dimension) {
            checkMultiplicationFloat(dim, dim, dim);
        }
    }

    @Test
    public void testNonSquareMatrixMultiplication() throws IOException {
        int[] dim1 = new int[] { 17, 23, 256 };
        int[] dim2 = new int[] { 13, 117, 222 };
        for (int m : dim1) {
            for (int n : dim2) {
                checkMultiplicationFloat(m, n, n + m);
            }
        }
    }

    @Test
    public void testSquarePowerOfTwoMatrixMultiplicationLocal() throws IOException {
        int exponent = 10;
        for (int e = 3; e <= exponent; e++) {
            int dim = (int) Math.pow(2, e);
            checkMultiplicationLocalFloat(dim, dim, dim);
        }
    }

    @Test
    public void testSquareMatrixMultiplicationLocal() throws IOException {
        int[] dimension = new int[] { 8, 16, 17, 23, 32, 256 };
        for (int dim : dimension) {
            checkMultiplicationLocalFloat(dim, dim, dim);
        }
    }

    @Test
    public void testNonSquareMatrixMultiplicationLocal() throws IOException {
        int[] dim1 = new int[] { 17, 23, 256 };
        int[] dim2 = new int[] { 13, 117, 222 };
        for (int m : dim1) {
            for (int n : dim2) {
                checkMultiplicationLocalFloat(m, n, n + m);
            }
        }
    }

    @Test
    public void testMatrixPointerRoundtrip() {
        FloatMatrix matrix = new FloatMatrix(vals);

        Pointer<Float> pointer = GPU.matrixToPointer(matrix);
        FloatMatrix matrix2 = GPU.pointerToFloatMatrix(pointer, matrix.getRowDimension(), matrix.getColumnDimension());

        assertMatrixEquals(matrix, matrix2, 0f);
        pointer.release();
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
        pointer.release();
    }

    @Test
    public void testWorkgroupSize() {
        assertEquals(2, GPU.workgroupSize(2));
        assertEquals(4, GPU.workgroupSize(3));
        assertEquals(4, GPU.workgroupSize(4));
        assertEquals(8, GPU.workgroupSize(7));
        assertEquals(32, GPU.workgroupSize(27));
        assertEquals(64, GPU.workgroupSize(33));
        assertEquals(64, GPU.workgroupSize(48));
    }

    @Test
    public void testColumnwiseMatrix() {
        FloatMatrix matrix = new FloatMatrix(valsColumn, 3);
        FloatMatrix matrix2 = new FloatMatrix(vals).transpose();
        assertMatrixEquals(matrix, matrix2, 0f);
    }

    private void checkMultiplicationFloat(int m1, int nn, int n2) throws IOException {
        trace("Multiply [" + m1 + " x " + nn + "] times [" + nn + " x " + n2 + "]");
        FloatMatrix A = FloatMatrix.random(m1, nn);
        FloatMatrix B = FloatMatrix.random(nn, n2);
        onFail.setMatrices(A, B);
        FloatMatrix actual = GPU.multiply(A, B);
        FloatMatrix expected = A.times(B);
        assertMatrixEquals(expected, actual, 0.001f);
    }

    private void checkMultiplicationLocalFloat(int m1, int nn, int n2) throws IOException {
        trace("Multiply [" + m1 + " x " + nn + "] times [" + nn + " x " + n2 + "]");
        FloatMatrix A = FloatMatrix.random(m1, nn);
        FloatMatrix B = FloatMatrix.random(nn, n2);
        onFail.setMatrices(A, B);
        FloatMatrix actual = GPU.multiplyLocal(A, B);
        FloatMatrix expected = A.times(B);
        assertMatrixEquals(expected, actual, 0.001f);
    }

    private void trace(String msg) {
        System.out.println("[" + name.getMethodName() + "] " + msg);
    }

    private void printDeviceInfo(CLDevice device) {
        trace("## " + device.getName());
        trace("   Driver version: " + device.getDriverVersion());
        trace("   OpenCL version: " + device.getOpenCLVersion());
        trace("   Max ComputeUnits: " + device.getMaxComputeUnits());
        trace("   Max WorkgroupSize: " + device.getMaxWorkGroupSize());
        trace("   Max WorkgroupItemDimension: " + device.getMaxWorkItemDimensions());
        trace("   Max WorkItemSizes[]: " + Arrays.toString(device.getMaxWorkItemSizes()));
        trace("   GlobalMemSize: " + toMegabyte(device.getGlobalMemSize()) + " Mbyte");
        trace("   GlobalMemCacheSize: " + toKilobyte(device.getGlobalMemCacheSize()) + " Kbyte");
        trace("   LocalMemSize: " + toKilobyte(device.getLocalMemSize()) + " Kbyte");
        trace("   MemAllocSize: " + toMegabyte(device.getMaxMemAllocSize()) + " Mbyte");

    }

    private double toMegabyte(long bytes) {
        return bytes / (1024.0 * 1024.0);
    }

    private double toKilobyte(long bytes) {
        return bytes / 1024.0;
    }
}
