package jama.util;

import static org.bridj.Pointer.allocateDoubles;
import static org.bridj.Pointer.allocateFloats;
import static org.bridj.Pointer.allocateInt;
import jama.FloatMatrix;
import jama.Matrix;
import jama.MultiplicationKernel;

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
 * 
 * @author Nepomuk Seiler
 * @version 2.0.0-SNAPSHOT
 * 
 */
public class GPU {

    public static FloatMatrix multiply(FloatMatrix A, FloatMatrix B) throws IOException {
        return multiply(A, B, true);
    }

    public static FloatMatrix multiplyLocal(FloatMatrix A, FloatMatrix B) throws IOException {
        return multiply(A, B, false);
    }

    private static FloatMatrix multiply(FloatMatrix A, FloatMatrix B, boolean local) throws IOException {
        if (A.getColumnDimension() != B.getRowDimension()) {
            throw new IllegalArgumentException("Matrix inner dimensions must agree.");
        }
        CLContext context = JavaCL.createBestContext();
        CLQueue queue = context.createDefaultQueue();

        int resultLength = A.getRowDimension() * B.getColumnDimension();

        Pointer<Float> aPtr = matrixToPointer(A);
        Pointer<Float> bPtr = matrixToPointer(B);
        Pointer<Float> resultPtr = allocateFloats(resultLength);
        Pointer<Integer> q = allocateInt();
        q.set(A.getColumnDimension()); // q is inner dimension

        // Create OpenCL input buffers (using the native memory pointers aPtr
        // and bPtr) :
        CLBuffer<Float> aInputBuffer = context.createBuffer(Usage.Input, aPtr);
        CLBuffer<Float> bInputBuffer = context.createBuffer(Usage.Input, bPtr);
        CLBuffer<Integer> qInputBuffer = context.createIntBuffer(Usage.Input, q);

        // Create an OpenCL output buffer :
        CLBuffer<Float> resultBuffer = context.createBuffer(Usage.Output, resultPtr);

        // Get and call the kernel :
        MultiplicationKernel kernel = new MultiplicationKernel(context);
        int[] localWorkSizes = new int[] { 8, 8 };
        int[] globalWorkSizes = new int[] { A.getRowDimension(), B.getColumnDimension() };

        CLEvent clEvent = null;
        Pointer<Float> outPtr = null;
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
            FloatMatrix matrix = pointerToFloatMatrix(outPtr, A.getRowDimension(), B.getColumnDimension());
            return matrix;
        } catch (CLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            Pointer.release(aPtr, bPtr, resultPtr, q);
            aInputBuffer.release();
            bInputBuffer.release();
            qInputBuffer.release();
            outPtr.release();
            context.release();
        }

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
     * forumlar := 2^(ceil(log_2(size)))
     * 
     * @param size
     * @return
     */
    protected static int workgroupSize(int size) {
        return (int) Math.pow(2, Math.ceil(Math.log(size) / Math.log(2)));
    }

}
