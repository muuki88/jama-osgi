package jama.benchmark;

import jama.FloatMatrix;
import jama.gpu.GPU;
import jama.rules.GpuRequirement;
import jama.rules.Prerequisite;
import jama.rules.PrerequisiteRule;

import org.junit.Rule;
import org.junit.Test;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;

public class GPUBenchmarkTest extends AbstractBenchmark {

    @Rule
    public PrerequisiteRule prerequisite = new PrerequisiteRule();

    @BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 2, callgc = true)
    @Prerequisite({ GpuRequirement.class })
    @Test
    public void testGpuMultiply1025Naive() throws Exception {
        FloatMatrix A = FloatMatrix.random(1025, 1025);
        FloatMatrix B = FloatMatrix.random(1025, 1025);
        GPU.create().multiply(A, B);
    }

    @BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 2, callgc = true)
    @Prerequisite({ GpuRequirement.class })
    @Test
    public void testGpuMultiply1025Local() throws Exception {
        FloatMatrix A = FloatMatrix.random(1025, 1025);
        FloatMatrix B = FloatMatrix.random(1025, 1025);
        GPU.create().multiplyLocal(A, B);
    }

    @BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 0, callgc = true)
    @Prerequisite({ GpuRequirement.class })
    @Test
    public void testGpuMultiply1024Naive() throws Exception {
        FloatMatrix A = FloatMatrix.random(1025, 1025);
        FloatMatrix B = FloatMatrix.random(1025, 1025);
        GPU.create().multiply(A, B);
    }

    @BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 0, callgc = true)
    @Prerequisite({ GpuRequirement.class })
    @Test
    public void testGpuMultiply1024Local() throws Exception {
        FloatMatrix A = FloatMatrix.random(1024, 1024);
        FloatMatrix B = FloatMatrix.random(1024, 1024);
        GPU.create().multiplyLocal(A, B);
    }

    @BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 0, callgc = true)
    @Prerequisite({ GpuRequirement.class })
    @Test
    public void testGpuMultiply2048Naive() throws Exception {
        FloatMatrix A = FloatMatrix.random(2048, 2048);
        FloatMatrix B = FloatMatrix.random(2048, 2048);
        GPU.create().multiply(A, B);
    }

    @BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 0, callgc = true)
    @Prerequisite({ GpuRequirement.class })
    @Test
    public void testGpuMultiply2048Local() throws Exception {
        FloatMatrix A = FloatMatrix.random(2048, 2048);
        FloatMatrix B = FloatMatrix.random(2048, 2048);
        GPU.create().multiplyLocal(A, B);
    }

    @BenchmarkOptions(benchmarkRounds = 2, warmupRounds = 0, callgc = true)
    @Prerequisite({ GpuRequirement.class })
    @Test
    public void testGpuMultiply4098Naive() throws Exception {
        FloatMatrix A = FloatMatrix.random(4098, 4098);
        FloatMatrix B = FloatMatrix.random(4098, 4098);
        GPU.create().multiply(A, B);
    }

    @BenchmarkOptions(benchmarkRounds = 2, warmupRounds = 0, callgc = true)
    @Prerequisite({ GpuRequirement.class })
    @Test
    public void testGpuMultiply4098Local() throws Exception {
        FloatMatrix A = FloatMatrix.random(4098, 4098);
        FloatMatrix B = FloatMatrix.random(4098, 4098);
        GPU.create().multiplyLocal(A, B);
    }
}
