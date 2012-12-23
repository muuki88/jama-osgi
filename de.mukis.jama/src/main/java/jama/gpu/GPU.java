package jama.gpu;

import static org.bridj.Pointer.allocateDoubles;
import static org.bridj.Pointer.allocateFloats;
import static org.bridj.Pointer.allocateInt;
import jama.FloatMatrix;
import jama.Matrix;

import java.io.IOException;

import org.bridj.Pointer;

import com.nativelibs4java.opencl.CLBuffer;
import com.nativelibs4java.opencl.CLContext;
import com.nativelibs4java.opencl.CLEvent;
import com.nativelibs4java.opencl.CLException;
import com.nativelibs4java.opencl.CLMem.Usage;
import com.nativelibs4java.opencl.CLQueue;
import com.nativelibs4java.opencl.JavaCL;

/**
 * <p>
 * This is the main class to perform computation on the GPU. Create a GPU via
 * the factory methods {@code create(..)}
 * <p>
 * 
 * <h3>Example</h3>
 * <p>
 * 
 * <pre>
 * GPU gpu = GPU.create(); // Creates a gpu with the JavaCL.createBestContext()
 * FloatMatrix C = gpu.multiplyLocal(A, B);
 * </pre>
 * 
 * </p>
 * 
 * <p>
 * Note that this is currently in experimental stadium. You cannot influence <br>
 * the parameters of the openCL kernels. So <em>blocksize</em>,
 * <em>workgroupsize</em> and <br>
 * <em>localworkgroupsize</em> are hardcoded. This may change in future
 * releases.
 * </p>
 * 
 * @author Nepomuk Seiler
 * @version 2.0.0-SNAPSHOT
 * 
 */
public class GPU {

    private final CLContext context;
    private static CLContext defaultContext;

    public static GPU create() {
        if (defaultContext == null) {
            defaultContext = JavaCL.createBestContext();
        }
        return new GPU(defaultContext);
    }

    public static GPU create(CLContext context) {
        return new GPU(context);
    }

    private GPU(CLContext context) {
        this.context = context;
    }

    /* ====================================================== */
    /* ================ Computation methods ================= */
    /* ====================================================== */

    /**
     * Use the naive openCL kernel, which computes more exact results than the
     * local variant.
     * 
     * @param A - Left input matrix
     * @param B - Right input matrix
     * @return A * B
     */
    public FloatMatrix multiply(FloatMatrix A, FloatMatrix B) throws IOException {
        return multiply(A, B, true);
    }

    /**
     * Use the local optimized openCL kernel, which may yield worse results than
     * the non local variant.
     * 
     * @param A - left input matrix
     * @param B - right input matrix
     * @return A * B
     */
    public FloatMatrix multiplyLocal(FloatMatrix A, FloatMatrix B) throws IOException {
        return multiply(A, B, false);
    }

    private FloatMatrix multiply(FloatMatrix A, FloatMatrix B, boolean local) throws IOException {
        if (A.getColumnDimension() != B.getRowDimension()) {
            throw new IllegalArgumentException("Matrix inner dimensions must agree.");
        }
        CLQueue queue = context.createDefaultQueue();

        FloatMatrix padA = zeroPadding(A, MultiplicationKernel.BLOCK_SIZE);
        FloatMatrix padB = zeroPadding(B, MultiplicationKernel.BLOCK_SIZE);
        int resultLength = padA.getRowDimension() * padB.getColumnDimension();

        Pointer<Float> aPtr = matrixToPointer(padA);
        Pointer<Float> bPtr = matrixToPointer(padB);
        Pointer<Float> resultPtr = allocateFloats(resultLength);
        Pointer<Integer> q = allocateInt();
        q.set(padA.getColumnDimension()); // q is inner dimension

        // Create OpenCL input buffers (using the native memory pointers aPtr
        // and bPtr) :
        CLBuffer<Float> aInputBuffer = context.createBuffer(Usage.Input, aPtr);
        CLBuffer<Float> bInputBuffer = context.createBuffer(Usage.Input, bPtr);
        CLBuffer<Integer> qInputBuffer = context.createIntBuffer(Usage.Input, q);

        // Create an OpenCL output buffer :
        CLBuffer<Float> resultBuffer = context.createBuffer(Usage.Output, resultPtr);

        // Get and call the kernel :
        MultiplicationKernel kernel = new MultiplicationKernel(context);
        int[] localWorkSizes = new int[] { MultiplicationKernel.BLOCK_SIZE, MultiplicationKernel.BLOCK_SIZE };
        int[] globalWorkSizes = new int[] { padA.getRowDimension(), padB.getColumnDimension() };

        CLEvent clEvent = null;
        Pointer<Float> outPtr = null;
        FloatMatrix matrix = null;
        try {
            if (local) {
                clEvent = kernel.floatMatrixMultLocals(queue, //
                        resultBuffer, //
                        aInputBuffer, //
                        bInputBuffer, //
                        qInputBuffer, //
                        globalWorkSizes, //
                        localWorkSizes);
            } else {
                clEvent = kernel.floatMatrixMult(queue, //
                        resultBuffer, //
                        aInputBuffer, //
                        bInputBuffer, //
                        qInputBuffer, //
                        globalWorkSizes, //
                        localWorkSizes);
            }
            // blocks until
            outPtr = resultBuffer.read(queue, clEvent);

            // mulitiplication finished
            matrix = pointerToFloatMatrix(outPtr, padA.getRowDimension(), padB.getColumnDimension());
            matrix = removeZeroPadding(matrix, A, B);
        } catch (CLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            Pointer.release(aPtr, bPtr, outPtr, resultPtr, q);
            aInputBuffer.release();
            bInputBuffer.release();
            qInputBuffer.release();
            resultBuffer.release();
            queue.release();
            clEvent.release();
        }
        return matrix;
    }

    /* ================================================== */
    /* ================ Util methods ==================== */
    /* ================================================== */

    protected static Pointer<Double> matrixToPointer(Matrix matrix) {
        int size = matrix.getColumnDimension() * matrix.getRowDimension();
        Pointer<Double> pointer = allocateDoubles(size);
        for (int row = 0; row < matrix.getRowDimension(); row++) {
            for (int col = 0; col < matrix.getColumnDimension(); col++) {
                pointer.set(row + matrix.getRowDimension() * col, matrix.get(row, col));
            }
        }
        return pointer;
    }

    protected static Pointer<Float> matrixToPointer(FloatMatrix matrix) {
        int size = matrix.getColumnDimension() * matrix.getRowDimension();
        Pointer<Float> pointer = allocateFloats(size);
        for (int row = 0; row < matrix.getRowDimension(); row++) {
            for (int col = 0; col < matrix.getColumnDimension(); col++) {
                pointer.set(row + matrix.getRowDimension() * col, matrix.get(row, col));
            }
        }
        return pointer;
    }

    protected static Matrix pointerToMatrix(Pointer<Double> pointer, int rows, int cols) {
        Matrix matrix = new Matrix(rows, cols);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                matrix.set(row, col, pointer.get(row + matrix.getRowDimension() * col));
            }
        }
        return matrix;
    }

    protected static FloatMatrix pointerToFloatMatrix(Pointer<Float> pointer, int rows, int cols) {
        FloatMatrix matrix = new FloatMatrix(rows, cols);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                matrix.set(row, col, pointer.get(row + matrix.getRowDimension() * col));
            }
        }
        return matrix;
    }

    /**
     * 
     * @param size
     * @return
     */
    protected static int workgroupSize(int size, int blocksize) {
        if (size <= blocksize) {
            return blocksize;
        }
        // check modulo
        int rest = size % blocksize;
        if (rest == 0) {
            return size;
        }
        return (size + blocksize) - rest;
    }

    /**
     * Returns a padded matrix with width/height of a correct factor determined
     * by the workgroupSize.
     * 
     * If the dimensions are already correct the identical matrix is returned.
     * 
     * @param matrix
     * @param workgroupSize
     * @return
     */
    protected static FloatMatrix zeroPadding(FloatMatrix matrix, int workgroupSize) {
        int m = workgroupSize(matrix.getRowDimension(), workgroupSize);
        int n = workgroupSize(matrix.getColumnDimension(), workgroupSize);
        if (m == matrix.getRowDimension() && n == matrix.getColumnDimension()) {
            return matrix;
        }
        FloatMatrix paddedMatrix = new FloatMatrix(m, n);
        paddedMatrix.setFloatMatrix(0, matrix.getRowDimension() - 1, 0, matrix.getColumnDimension() - 1, matrix);
        return paddedMatrix;
    }

    /**
     * Removes the zero padding from the result matrix.
     * 
     * @param result - result matrix created with zero padded matrices
     * @param A - original non-padded matrix
     * @param B - original non-padded matrix
     * @return non padded result matrix
     */
    protected static FloatMatrix removeZeroPadding(FloatMatrix result, FloatMatrix A, FloatMatrix B) {
        if (result.getColumnDimension() == B.getColumnDimension() && result.getRowDimension() == A.getRowDimension()) {
            return result;
        }
        return result.getFloatMatrix(0, A.getRowDimension() - 1, 0, B.getColumnDimension() - 1);
    }
}
