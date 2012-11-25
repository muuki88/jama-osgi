package jama.rules;

import jama.FloatMatrix;

import java.io.PrintWriter;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class PrintMatrixOnFail extends TestWatcher {

    private FloatMatrix A, B;

    @Override
    protected void failed(Throwable e, Description description) {
        System.out.println("## Failed [" + description + "]");
        System.out.println("## " + e.getMessage());
        PrintWriter out = new PrintWriter(System.out, true);
        A.print(out, 5, 5);
        B.print(out, 5, 5);
    }

    public void setMatrices(FloatMatrix A, FloatMatrix B) {
        this.A = A;
        this.B = B;
    }
}
