package jama.benchmark;

import Jama.Matrix;

import com.google.caliper.Param;
import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

/**
 * 
 * @author Nepomuk Seiler
 * 
 */
public class AlgebraBenchmark extends SimpleBenchmark {

    @Param({ "100", "1000", })
    private int m1;
    @Param({ "10", "100", "1000" })
    private int nm;
    @Param({ "100", "1000" })
    private int n2;

    private Matrix A;
    private Matrix B;

    @Override
    protected void setUp() throws Exception {
        A = Matrix.random(m1, nm);
        B = Matrix.random(nm, n2);
    }

    public void timeMatrixMultiplication(int reps) {
        for (int i = 0; i < reps; i++) {
            A.times(B);
        }
    }

    public static void main(String[] args) throws Exception {
        // programm arguments
        // -JmemoryMax=-Xmx512M,-Xmx1024M,-Xmx2048M
        Runner.main(AlgebraBenchmark.class, args);
    }
}
