package jama.benchmark;

import jama.FloatMatrix;
import jama.gpu.GPU;

import java.io.IOException;

import com.google.caliper.Param;
import com.google.caliper.SimpleBenchmark;
import com.google.common.base.Stopwatch;

public class GPUBenchmark extends SimpleBenchmark {

    @Param({ "512", "1024", "2048", })
    private int dim;

    private FloatMatrix A;
    private FloatMatrix B;

    @Override
    protected void setUp() throws Exception {
        A = FloatMatrix.random(dim, dim);
        B = FloatMatrix.random(dim, dim);
    }

    /* Takes too long and causes exception
    public void timeMatrixMultiplicationCPU(int reps) {
        for (int i = 0; i < reps; i++) {
            A.times(B);
        }
    }
    */

    public void timeMatrixMultiplication(int reps) throws IOException {
        GPU gpu = GPU.create();
        for (int i = 0; i < reps; i++) {
            gpu.multiply(A, B);
        }
    }

    public void timeMatrixMultiplicationLocal(int reps) throws IOException {
        GPU gpu = GPU.create();
        for (int i = 0; i < reps; i++) {
            gpu.multiplyLocal(A, B);
        }
    }

    public static void main(String[] args) throws Exception {
        // programm arguments
        // -JmemoryMax=-Xmx512M,-Xmx1024M,-Xmx2048M
        // Runner.main(GPUBenchmark.class, args);
        // stopWatchTest();
        stopWatchTestGpuOnly();
    }

    private static void stopWatchTest() throws IOException {
        System.out.println("Start stopwatch benchmark");
        int iters = 3;
        int dim = 2048;
        FloatMatrix A = FloatMatrix.random(dim, dim);
        FloatMatrix B = FloatMatrix.random(dim, dim);

        // warm up
        for (int i = 0; i < 3; i++) {
            A.times(B);
        }
        System.out.println("Warming up finished");

        Stopwatch watch = new Stopwatch().start();
        for (int i = 0; i < iters; i++) {
            A.times(B);
        }
        long time = watch.stop().elapsedMillis();
        System.out.println("Non GPU " + time + " ms [AVG: " + (time / iters) + " ms] ");
        watch.reset().start();

        GPU gpu = GPU.create();
        for (int i = 0; i < iters; i++) {
            gpu.multiplyLocal(A, B);
        }
        time = watch.stop().elapsedMillis();
        System.out.println("GPU " + time + " ms [AVG: " + (time / iters) + " ms] ");
    }

    private static void stopWatchTestGpuOnly() throws IOException {
        int iters = 3;
        int dim = 4096;
        FloatMatrix A = FloatMatrix.random(dim, dim);
        FloatMatrix B = FloatMatrix.random(dim, dim);
        GPU gpu = GPU.create();
        Stopwatch watch = new Stopwatch().start();
        for (int i = 0; i < iters; i++) {
            gpu.multiplyLocal(A, B);
        }
        long time = watch.stop().elapsedMillis();
        System.out.println("GPU " + time + " ms [AVG: " + (time / iters) + " ms] ");
    }

}
