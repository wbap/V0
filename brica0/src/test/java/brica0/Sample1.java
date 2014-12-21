package brica0;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import brica0.CognitiveArchitecture;
import brica0.ConstantModule;
import brica0.Module;
import brica0.NonRTSyncScheduler;
import brica0.PipeModule;
import brica0.Scheduler;

public class Sample1 {

    @Test
    public void testCAConstruct() {
        Scheduler s = new NonRTSyncScheduler(1.0);
        @SuppressWarnings("unused")
        CognitiveArchitecture ca = new CognitiveArchitecture(s);
    }

    @Test
    public void testAddModule() {
        Scheduler s = new NonRTSyncScheduler(1.0);
        CognitiveArchitecture ca = new CognitiveArchitecture(s);

        Module m = new NullModule();

        ca.add_module("M1", m);

        Module mm = ca.get_module("M1");
        assertSame("failed to get module from ca.", m, mm);
    }

    @Test
    public void testConstantModule() {
        Scheduler s = new NonRTSyncScheduler(1.0);
        CognitiveArchitecture ca = new CognitiveArchitecture(s);

        Module cm = new ConstantModule();
        cm.make_out_port("out1", 3);

        short[] setout = { 0, 1, 2 };

        cm.set_state("out1", setout);
        ca.add_module("M1", cm);

        double t = ca.step();
        assertEquals("wrong time after one step.", 1.0, t, 1e-18);

        Module mm = ca.get_module("M1");
        assertSame("failed to get module from ca.", cm, mm);

        assertTrue(Arrays.equals(setout, mm.get_out_port("out1")));

        short[] setout2 = { 3, 4, 5 };
        mm.set_state("out1", setout2);

        assertTrue(Arrays.equals(setout, mm.get_out_port("out1")));

        ca.step();
        ca.step();

        assertTrue(Arrays.equals(setout2, mm.get_out_port("out1")));
    }

    @Test
    public void testConstPipeNullModule() {
        // A simple test to run the following three modules configuration.
        // ConstantModule A -> PipeModule B -> NullModule C
        
        Module A = new ConstantModule();
        Module B = new PipeModule();
        Module C = new NullModule();
        
        short[] zero = {0,0,0};
        short[] v = {1,2,3};
        A.set_state("p1", v);
        
        assertTrue(Arrays.equals(A.get_state("p1"), v));
        assertNotSame(A.get_state("p1"), v);
        
        A.make_out_port("p1", 3);

        B.make_in_port("p1", 3);
        B.make_out_port("p1", 3);
        B.make_connection(A, "p1", "p1");

        C.make_in_port("p1", 3);
        C.make_connection(B, "p1", "p1");
        
        Scheduler s = new NonRTSyncScheduler(1.0);
        CognitiveArchitecture ca = new CognitiveArchitecture(s);

        ca.add_module("A", A);
        ca.add_module("B", B);
        ca.add_module("C", C);

        assertTrue(Arrays.equals(zero, A.get_out_port("p1")));
        assertTrue(Arrays.equals(zero, B.get_in_port("p1")));
        assertTrue(Arrays.equals(zero, B.get_out_port("p1")));
        assertTrue(Arrays.equals(zero, C.get_in_port("p1")));
        
        // 1
        ca.step();

        assertTrue(Arrays.equals(v, A.get_out_port("p1")));
        assertTrue(Arrays.equals(zero, B.get_in_port("p1")));
        assertTrue(Arrays.equals(zero, B.get_out_port("p1")));
        assertTrue(Arrays.equals(zero, C.get_in_port("p1")));
        
        // 2
        ca.step();

        assertTrue(Arrays.equals(v, A.get_out_port("p1")));
        assertTrue(Arrays.equals(v, B.get_in_port("p1")));
        assertTrue(Arrays.equals(zero, B.get_out_port("p1")));
        assertTrue(Arrays.equals(zero, C.get_in_port("p1")));
        
        // 3
        ca.step();

        assertTrue(Arrays.equals(v, A.get_out_port("p1")));
        assertTrue(Arrays.equals(v, B.get_in_port("p1")));
        assertTrue(Arrays.equals(v, B.get_out_port("p1")));
        assertTrue(Arrays.equals(zero, C.get_in_port("p1")));
        
        // 4
        ca.step();

        assertTrue(Arrays.equals(v, A.get_out_port("p1")));
        assertTrue(Arrays.equals(v, B.get_in_port("p1")));
        assertTrue(Arrays.equals(v, B.get_out_port("p1")));
        assertTrue(Arrays.equals(v, C.get_in_port("p1")));
        
    }
}
