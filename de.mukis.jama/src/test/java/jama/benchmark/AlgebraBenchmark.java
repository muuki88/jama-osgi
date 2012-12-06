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
        try {
            for (int i = 0; i < reps; i++) {
                A.solve(C);
            }
        } catch (RuntimeException e) {
            onFailure(e);
        }

    }

    public void timeEigenvalueDecomposition(int reps) {
        try {
            for (int i = 0; i < reps; i++) {
                A.eig();
            }

        } catch (RuntimeException e) {
            onFailure(e);
        }
    }

    public void timeLUDecomposition(int reps) {
        try {
            for (int i = 0; i < reps; i++) {
                SQUARE.lu();
            }
        } catch (RuntimeException e) {
            onFailure(e);
        }
    }

    public void timeCholeskyDecomposition(int reps) {
        try {
            for (int i = 0; i < reps; i++) {
                SQUARE.chol();
            }
        } catch (RuntimeException e) {
            onFailure(e);
        }
    }

    public void timeSingularValueDecomposition(int reps) {
        try {
            for (int i = 0; i < reps; i++) {
                A.svd();
            }
        } catch (RuntimeException e) {
            onFailure(e);
        }
    }

    public void timeQRDecomposition(int reps) {
        try {
            for (int i = 0; i < reps; i++) {
                A.qr();
            }
        } catch (RuntimeException e) {
            onFailure(e);
        }
    }

    public void timeRank(int reps) {
        try {
            for (int i = 0; i < reps; i++) {
                A.rank();
            }
        } catch (RuntimeException e) {
            onFailure(e);
        }
    }

    public void timeDeterminant(int reps) {
        try {
            for (int i = 0; i < reps; i++) {
                SQUARE.det();
            }
        } catch (RuntimeException e) {
            onFailure(e);
        }
    }

    public void timeNorm1(int reps) {
        try {
            for (int i = 0; i < reps; i++) {
                A.norm1();
            }
        } catch (RuntimeException e) {
            onFailure(e);
        }
    }

    public void timeNorm2(int reps) {
        try {
            for (int i = 0; i < reps; i++) {
                A.norm2();
            }
        } catch (RuntimeException e) {
            onFailure(e);
        }
    }

    public void timeNormF(int reps) {
        try {
            for (int i = 0; i < reps; i++) {
                A.normF();
            }
        } catch (RuntimeException e) {
            onFailure(e);
        }
    }

    public void timeNormInf(int reps) {
        for (int i = 0; i < reps; i++) {
            A.normInf();
        }
    }

    private void onFailure(Throwable t) {
        try {
            Thread.sleep(2 * (m1 + nm + n2));
            System.out.println("[WARNING] " + t.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        // programm arguments
        // -JmemoryMax=-Xmx512M,-Xmx1024M,-Xmx2048M
        Runner.main(AlgebraBenchmark.class, args);
    }
}
