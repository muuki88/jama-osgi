package jama.benchmark;

import jama.Matrix;

import com.google.caliper.Param;
import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

/**
 * 
 * @author Nepomuk Seiler
 * 
 */
public class AlgebraBenchmark extends SimpleBenchmark {

    @Param({ "100", "250", "500", })
    private int m1;
    @Param({ "99", "248", "497" })
    private int nm;
    @Param({ "100", "250", "500" })
    private int n2;

    private Matrix A;
    private Matrix B;
    private Matrix C;
    private Matrix SQUARE;

    @Override
    protected void setUp() throws Exception {
        A = Matrix.random(m1, nm);
        B = Matrix.random(nm, n2);
        C = Matrix.random(m1, nm);
        SQUARE = Matrix.random(m1, m1);
    }

    public void timeMatrixMultiplication(int reps) {
        for (int i = 0; i < reps; i++) {
            A.times(B);
        }
    }

    public void timeSolve(int reps) {
        for (int i = 0; i < reps; i++) {
            A.solve(C);
        }
    }

    public void timeEigenvalueDecomposition(int reps) {
        for (int i = 0; i < reps; i++) {
            A.eig();
        }
    }

    public void timeLUDecomposition(int reps) {
        for (int i = 0; i < reps; i++) {
            SQUARE.lu();
        }
    }

    public void timeCholeskyDecomposition(int reps) {
        for (int i = 0; i < reps; i++) {
            SQUARE.chol();
        }
    }

    public void timeSingularValueDecomposition(int reps) {
        for (int i = 0; i < reps; i++) {
            A.svd();
        }
    }

    public void timeQRDecomposition(int reps) {
        for (int i = 0; i < reps; i++) {
            A.qr();
        }
    }

    public void timeRank(int reps) {
        for (int i = 0; i < reps; i++) {
            A.rank();
        }
    }

    public void timeDeterminant(int reps) {
        for (int i = 0; i < reps; i++) {
            SQUARE.det();
        }
    }

    public void timeNorm1(int reps) {
        for (int i = 0; i < reps; i++) {
            A.norm1();
        }
    }

    public void timeNorm2(int reps) {
        for (int i = 0; i < reps; i++) {
            A.norm2();
        }
    }

    public void timeNormF(int reps) {
        for (int i = 0; i < reps; i++) {
            A.normF();
        }
    }

    public void timeNormInf(int reps) {
        for (int i = 0; i < reps; i++) {
            A.normInf();
        }
    }

    public static void main(String[] args) throws Exception {
        // programm arguments
        // -JmemoryMax=-Xmx512M,-Xmx1024M,-Xmx2048M
        Runner.main(AlgebraBenchmark.class, args);
    }
}
