package brica0;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

public class SchedulerBenchmarks {
    
    public static void main(String args[]) throws Exception {
        double timing;

        System.out.println("\n=== VirtualTimeSyncScheduler ===\n");

        // invoke JIT
        bench2(new VirtualTimeSyncScheduler(1.0), 100);
        bench2(new VirtualTimeSyncScheduler(1.0), 100);
        bench2(new VirtualTimeSyncScheduler(1.0), 100);

        
        timing = bench1(new VirtualTimeSyncScheduler(1.0));
        System.out.println("bench1: " + timing + " ms.");

        timing = bench2(new VirtualTimeSyncScheduler(1.0), 1);
        System.out.println("bench2: " + timing + " ms.");
        
        timing = bench2(new VirtualTimeSyncScheduler(1.0), 500);
        System.out.println("bench2 (1kB): " + timing + " ms.");
        
        timing = bench2(new VirtualTimeSyncScheduler(1.0), 5000);
        System.out.println("bench2 (10kB): " + timing + " ms.");

        
        
        System.out.println("\n=== VirtualTimeScheduler ===\n");

        // invoke JIT
        bench2(new VirtualTimeScheduler(), 100);
        bench2(new VirtualTimeScheduler(), 100);
        bench2(new VirtualTimeScheduler(), 100);
        
        timing = bench1(new VirtualTimeScheduler());
        System.out.println("bench1: " + timing + " ms.");


        timing = bench2(new VirtualTimeScheduler(), 1);
        System.out.println("bench2: " + timing * 2 + " ms."); // timing * 2 where 2 is num modules.
        
        timing = bench2(new VirtualTimeScheduler(), 500);
        System.out.println("bench2 (1kB): " + timing * 2 + " ms.");
        
        timing = bench2(new VirtualTimeScheduler(), 5000);
        System.out.println("bench2 (10kB): " + timing * 2 + " ms.");

    }


    public static double bench1(Scheduler s) {
        int count = 1000000;
        
        //Scheduler s = new VirtualTimeSyncScheduler(1.0);
        CognitiveArchitecture ca = new CognitiveArchitecture(s);

        Module m = new NullModule();

        ca.addSubModule("M1", m);

        Module mm = ca.getSubModule("M1");

        long startTime = System.currentTimeMillis();
        
        for(int i = 0; i < count; i++)
        {
               ca.step();
        }
        
        long endTime = System.currentTimeMillis();
        
        return (endTime - startTime) / (double)count;
    }

    public static double bench2(Scheduler s, int dataLength) throws Exception {
        int count = 1000000;
        
        //Scheduler s = new VirtualTimeSyncScheduler(1.0);
        CognitiveArchitecture ca = new CognitiveArchitecture(s);

        ConstantModule M1 = new ConstantModule();
        NullModule  M2 = new NullModule();

        short[] v = new short[dataLength];

        M1.setState("out1", v);

        M1.makeOutPort("out1", 3);
            
        M2.connect(M1, "out1", "in1"); // connection from B:out1 to C:in1

        ca.addSubModule("M1", M1);
        ca.addSubModule("M2", M2);
        
        long startTime = System.currentTimeMillis();
        
        for(int i = 0; i < count; i++)
        {
               ca.step();
        }
        
        long endTime = System.currentTimeMillis();
        
        return (endTime - startTime) / (double)count;
    }

}
