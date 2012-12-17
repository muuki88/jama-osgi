package jama.rules;

import com.nativelibs4java.opencl.JavaCL;

public class GpuRequirement implements IRequirement {

    @Override
    public boolean available() {
        try {
            JavaCL.listPlatforms();
            JavaCL.listGPUPoweredPlatforms();
            return JavaCL.getBestDevice() != null;
        } catch (Throwable e) {
            System.err.println("Error on loading gpuPlatforms: " + e.getMessage());
            return false;
        }
    }
}
