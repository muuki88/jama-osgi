package jama.benchmark;

import jama.FloatMatrix;

import com.google.caliper.Param;
import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

/**
 * 
 * @author Nepomuk Seiler
 * 
 */
public class AlgebraFloatMatrixBenchmark extends SimpleBenchmark {

    @Param({ "100", "250", "500", })
    private int m1;
    @Param({ "99", "248", "497" })
    private int nm;
    @Param({ "100", "250", "500" })
    private int n2;

    private FloatMatrix A;
    private FloatMatrix B;

    @Override
    protected void setUp() throws Exception {
        A = FloatMatrix.random(m1, nm);
        B = FloatMatrix.random(nm, n2);
    }

    public void timeMatrixMultiplication(int reps) {
        for (int i = 0; i < reps; i++) {
            A.times(B);
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
        Runner.main(AlgebraFloatMatrixBenchmark.class, args);
    }
}
