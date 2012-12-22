package jama.rules;

import com.nativelibs4java.opencl.JavaCL;

public class GpuRequirement implements IRequirement {

    private Boolean available = null;

    @Override
    public boolean available() {
        if (available != null) {
            return available.booleanValue();
        }
        try {
            JavaCL.listPlatforms();
            JavaCL.listGPUPoweredPlatforms();
            available = JavaCL.getBestDevice() != null;
        } catch (Throwable e) {
            System.err.println("Error on loading gpuPlatforms: " + e.getMessage());
            available = false;
        }
        return available.booleanValue();
    }
}
