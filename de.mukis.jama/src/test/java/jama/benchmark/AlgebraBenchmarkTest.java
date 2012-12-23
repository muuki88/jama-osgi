package jama.benchmark;

import jama.Matrix;

import org.junit.Before;
import org.junit.Test;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;

public class AlgebraBenchmarkTest extends AbstractBenchmark {

    private Matrix A;
    private Matrix C;
    private Matrix SQUARE;

    @Before
    public void setUp() {
        A = Matrix.random(512, 512);
        C = Matrix.random(512, 512);
        SQUARE = Matrix.random(512, 512);
    }

    @BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 2, callgc = true)
    @Test
    public void testMatrixMultiplication() {
        A.times(A);
    }

    @BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 2, callgc = true)
    @Test
    public void testSolve() {
        try {
            A.solve(C);
        } catch (RuntimeException e) {
            onFailure(e);
        }

    }

    @BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 0, callgc = true)
    @Test
    public void testEigenvalueDecomposition() {
        try {
            A.eig();

        } catch (RuntimeException e) {
            onFailure(e);
        }
    }

    @BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 2, callgc = true)
    @Test
    public void testLUDecomposition() {
        try {
            SQUARE.lu();
        } catch (RuntimeException e) {
            onFailure(e);
        }
    }

    @BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 2, callgc = true)
    @Test
    public void testCholeskyDecomposition() {
        try {
            SQUARE.chol();
        } catch (RuntimeException e) {
            onFailure(e);
        }
    }

    @BenchmarkOptions(benchmarkRounds = 2, warmupRounds = 1, callgc = true)
    @Test
    public void testSingularValueDecomposition() {
        try {
            A.svd();
        } catch (RuntimeException e) {
            onFailure(e);
        }
    }

    @BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 2, callgc = true)
    @Test
    public void testQRDecomposition() {
        try {
            A.qr();
        } catch (RuntimeException e) {
            onFailure(e);
        }
    }

    @BenchmarkOptions(benchmarkRounds = 2, warmupRounds = 1, callgc = true)
    @Test
    public void testRank() {
        try {
            A.rank();
        } catch (RuntimeException e) {
            onFailure(e);
        }
    }

    @BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 2, callgc = true)
    @Test
    public void testDeterminant() {
        try {
            SQUARE.det();
        } catch (RuntimeException e) {
            onFailure(e);
        }
    }

    @BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 2, callgc = true)
    @Test
    public void testNorm1() {
        try {
            A.norm1();
        } catch (RuntimeException e) {
            onFailure(e);
        }
    }

    @BenchmarkOptions(benchmarkRounds = 2, warmupRounds = 1, callgc = true)
    @Test
    public void testNorm2() {
        try {
            A.norm2();
        } catch (RuntimeException e) {
            onFailure(e);
        }
    }

    @BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 2, callgc = true)
    @Test
    public void testNormF() {
        try {
            A.normF();
        } catch (RuntimeException e) {
            onFailure(e);
        }
    }

    @BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 2, callgc = true)
    @Test
    public void testNormInf() {
        A.normInf();
    }

    private void onFailure(Throwable t) {
        System.err.println("[WARNING] " + t.getMessage());
    }
}
