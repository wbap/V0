package brica0;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

public class SchedulerBenchmarks {
    
    public static void main(String args[]) throws Exception {
        double timing;
        
        timing = bench1();
        System.out.println("bench1: " + timing + " ms.");

        for (int i = 0; i< 5; i++) {
            timing = bench2(1);
        }
        System.out.println("bench2: " + timing + " ms.");
        
        for (int i = 0; i< 5; i++) {
            timing = bench2(1);
        }
        timing = bench2(500); // 1kB
        System.out.println("bench2 (1kB): " + timing + " ms.");
        
        for (int i = 0; i< 5; i++) {
            timing = bench2(1);
        }
        timing = bench2(5000); // 10kB
        System.out.println("bench2 (10kB): " + timing + " ms.");

    }


    public static double bench1() {
        int count = 1000000;
        
        Scheduler s = new VirtualTimeSyncScheduler(1.0);
        CognitiveArchitecture ca = new CognitiveArchitecture(s);

        Module m = new NullModule();

        ca.addModule("M1", m);

        Module mm = ca.getModule("M1");

        long startTime = System.currentTimeMillis();
        
        for(int i = 0; i < count; i++)
        {
               ca.step();
        }
        
        long endTime = System.currentTimeMillis();
        
        return (endTime - startTime) / (double)count;
    }

    public static double bench2(int dataLength) throws Exception {
        int count = 1000000;
        
        Scheduler s = new VirtualTimeSyncScheduler(1.0);
        CognitiveArchitecture ca = new CognitiveArchitecture(s);

        ConstantModule M1 = new ConstantModule();
        NullModule  M2 = new NullModule();

        short[] v = new short[dataLength];

        M1.setState("out1", v);

        M1.makeOutPort("out1", 3);
            
        M2.connect(M1, "out1", "in1"); // connection from B:out1 to C:in1

        ca.addModule("M1", M1);
        ca.addModule("M2", M2);
        
        long startTime = System.currentTimeMillis();
        
        for(int i = 0; i < count; i++)
        {
               ca.step();
        }
        
        long endTime = System.currentTimeMillis();
        
        return (endTime - startTime) / (double)count;
    }

}
