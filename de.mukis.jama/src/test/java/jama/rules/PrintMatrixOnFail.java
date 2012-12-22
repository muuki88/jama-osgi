package jama.rules;

import jama.FloatMatrix;

import java.io.PrintWriter;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class PrintMatrixOnFail extends TestWatcher {

    private FloatMatrix A, B;
    private final int threshold = 16;

    @Override
    protected void failed(Throwable e, Description description) {
        if (A == null || B == null) {
            return;
        }
        System.out.println("## Failed [" + description + "]");
        System.out.println("## " + e.getMessage());
        PrintWriter out = new PrintWriter(System.out, true);
        if (isTooBig()) {
            System.out.println("Too big. A[" + A.getRowDimension() + "x" + A.getColumnDimension() + //
                    "], B[" + B.getRowDimension() + "x" + B.getColumnDimension() + "]");
        } else {
            A.print(out, 5, 5);
            B.print(out, 5, 5);
        }

    }

    public void setMatrices(FloatMatrix A, FloatMatrix B) {
        this.A = A;
        this.B = B;
    }

    private boolean isTooBig() {
        return A.getRowDimension() > threshold || //
                A.getColumnDimension() > threshold || //
                B.getRowDimension() > threshold || //
                B.getColumnDimension() > threshold;
    }
}
