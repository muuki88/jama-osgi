package jama.gpu;

import static jama.MatrixAsserts.assertMatrixEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import jama.FloatMatrix;
import jama.rules.GpuRequirement;
import jama.rules.Prerequisite;
import jama.rules.PrerequisiteRule;
import jama.rules.PrintMatrixOnFail;

import java.io.IOException;
import java.util.Arrays;

import org.bridj.Pointer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

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

    @Rule
    public PrintMatrixOnFail onFail = new PrintMatrixOnFail();

    @Rule
    public TestName name = new TestName();

    @Rule
    public PrerequisiteRule prerequisite = new PrerequisiteRule();

    @Test
    @Prerequisite({ GpuRequirement.class })
    public void testGPUAvailable() {
        CLPlatform[] platforms = JavaCL.listPlatforms();
        CLPlatform[] gpuPlatforms = JavaCL.listGPUPoweredPlatforms();
        assertTrue("There must a platform available", platforms.length != 0);
        assertTrue("There must a platform available", gpuPlatforms.length != 0);

        CLDevice[] devices = gpuPlatforms[0].listGPUDevices(true);
        assertTrue("There must a device available", devices.length != 0);
        printDeviceInfo(gpuPlatforms[0].getBestDevice());

    }

    @Test
    @Prerequisite({ GpuRequirement.class })
    public void testSquarePowerOfTwoMatrixMultiplication() throws Exception {
        int exponent = 10;
        for (int e = 3; e <= exponent; e++) {
            int dim = (int) Math.pow(2, e);
            checkMultiplicationFloat(dim, dim, dim);
        }
    }

    @Test
    @Prerequisite({ GpuRequirement.class })
    public void testNonSquarePowerOfTwoMatrixMultiplication() throws Exception {
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
    @Prerequisite({ GpuRequirement.class })
    public void testSquareMatrixMultiplication() throws Exception {
        int[] dimension = new int[] { 8, 16, 17, 23, 32, 256 };
        for (int dim : dimension) {
            checkMultiplicationFloat(dim, dim, dim);
        }
    }

    @Test
    @Prerequisite({ GpuRequirement.class })
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
    @Prerequisite({ GpuRequirement.class })
    public void testSquarePowerOfTwoMatrixMultiplicationLocal() throws IOException {
        int exponent = 10;
        for (int e = 3; e <= exponent; e++) {
            int dim = (int) Math.pow(2, e);
            checkMultiplicationLocalFloat(dim, dim, dim);
        }
    }

    @Test
    @Prerequisite({ GpuRequirement.class })
    public void testSquareMatrixMultiplicationLocal() throws IOException {
        int[] dimension = new int[] { 8, 16, 17, 23, 32, 256 };
        for (int dim : dimension) {
            checkMultiplicationLocalFloat(dim, dim, dim);
        }
    }

    @Test
    @Prerequisite({ GpuRequirement.class })
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
    @Prerequisite({ GpuRequirement.class })
    public void testMatrixPointerRoundtrip() {
        FloatMatrix matrix = new FloatMatrix(vals);

        Pointer<Float> pointer = GPU.matrixToPointer(matrix);
        FloatMatrix matrix2 = GPU.pointerToFloatMatrix(pointer, matrix.getRowDimension(), matrix.getColumnDimension());

        assertMatrixEquals(matrix, matrix2, 0f);
        pointer.release();
    }

    @Test
    @Prerequisite({ GpuRequirement.class })
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
        int blocksize = 2;
        assertEquals(2, GPU.workgroupSize(2, blocksize));
        assertEquals(4, GPU.workgroupSize(3, blocksize));
        assertEquals(4, GPU.workgroupSize(4, blocksize));
        assertEquals(8, GPU.workgroupSize(7, blocksize));
        assertEquals(28, GPU.workgroupSize(27, blocksize));
        assertEquals(34, GPU.workgroupSize(33, blocksize));
        assertEquals(48, GPU.workgroupSize(48, blocksize));

        blocksize = 8;
        for (int i = 1; i < 8; i++) {
            assertEquals(8, GPU.workgroupSize(i, blocksize));
        }
        assertEquals(32, GPU.workgroupSize(27, blocksize));
        assertEquals(40, GPU.workgroupSize(33, blocksize));

        blocksize = 16;
        for (int i = 1; i < 16; i++) {
            assertEquals(16, GPU.workgroupSize(i, blocksize));
        }
        assertEquals(32, GPU.workgroupSize(27, blocksize));
        assertEquals(48, GPU.workgroupSize(33, blocksize));
    }

    @Test
    public void testZeroPaddingRoundtrip() {
        int workgroupsize = 8;
        FloatMatrix matrix = FloatMatrix.random(17, 30);
        FloatMatrix padded = GPU.zeroPadding(matrix, workgroupsize);
        assertEquals("RowPadding incorrect", 24, padded.getRowDimension());
        assertEquals("ColumnPadding incorrect", 32, padded.getColumnDimension());

        FloatMatrix dePadded = GPU.removeZeroPadding(padded, matrix, matrix);
        assertMatrixEquals(matrix, dePadded, 0f);
    }

    @Test
    public void testZeroPaddingRoundtripWithMultiplication() {
        int workgroupsize = 8;
        FloatMatrix A = FloatMatrix.random(17, 30);
        FloatMatrix B = FloatMatrix.random(30, 14);
        FloatMatrix result = A.times(B);

        assertEquals("RowPadding incorrect", 17, result.getRowDimension());
        assertEquals("ColumnPadding incorrect", 14, result.getColumnDimension());

        FloatMatrix paddedA = GPU.zeroPadding(A, workgroupsize);
        FloatMatrix paddedB = GPU.zeroPadding(B, workgroupsize);
        FloatMatrix paddedResult = paddedA.times(paddedB);

        assertEquals("RowPadding incorrect", 24, paddedResult.getRowDimension());
        assertEquals("ColumnPadding incorrect", 16, paddedResult.getColumnDimension());

        FloatMatrix dePaddedResult = GPU.removeZeroPadding(paddedResult, A, B);

        assertMatrixEquals(result, dePaddedResult, 0f);
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
        GPU gpu = GPU.create();
        FloatMatrix actual = gpu.multiply(A, B);
        FloatMatrix expected = A.times(B);
        assertMatrixEquals(expected, actual, 0.001f);
    }

    private void checkMultiplicationLocalFloat(int m1, int nn, int n2) throws IOException {
        trace("Multiply [" + m1 + " x " + nn + "] times [" + nn + " x " + n2 + "]");
        FloatMatrix A = FloatMatrix.random(m1, nn);
        FloatMatrix B = FloatMatrix.random(nn, n2);
        onFail.setMatrices(A, B);

        GPU gpu = GPU.create();
        FloatMatrix actual = gpu.multiplyLocal(A, B);
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
