package jama.benchmark;

import jama.Matrix;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;

@RunWith(Parameterized.class)
public class AlgebraBenchmarkTest extends AbstractBenchmark {

    private Matrix A, B, C, SQUARE;
    private final int m1, nm, n2;

    @Parameters
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][] { { 128, 128, 128 }, { 128, 256, 256 }, { 512, 512, 512 } };
        return Arrays.asList(data);
    }

    public AlgebraBenchmarkTest(int m1, int nm, int n2) {
        this.m1 = m1;
        this.nm = nm;
        this.n2 = n2;
    }

    @Before
    public void setUp() {
        A = Matrix.random(m1, nm);
        B = Matrix.random(nm, n2);
        C = Matrix.random(m1, nm);
        SQUARE = Matrix.random(m1, m1);
    }

    @BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 2, callgc = true)
    @Test
    public void testMatrixMultiplication() {
        A.times(B);
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
        System.err.println("m1 = " + m1 + "; nm = " + nm + "; n2 = " + n2);
    }
}
