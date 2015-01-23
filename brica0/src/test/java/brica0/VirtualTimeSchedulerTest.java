package brica0;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import brica0.CognitiveArchitecture;
import brica0.ConstantModule;
import brica0.Module;
import brica0.VirtualTimeScheduler;
import brica0.PipeModule;
import brica0.Scheduler;

public class VirtualTimeSchedulerTest {

    @Test
    public void testScheduling() {
        VirtualTimeScheduler s = new VirtualTimeScheduler();
        CognitiveArchitecture ca = new CognitiveArchitecture(s);

        Module M1 = new ConstantModule();
        Module M2 = new ConstantModule();
        Module M3 = new ConstantModule();

        M1.setInterval(2.0);
        M2.setInterval(3.0);
        M3.setInterval(5.0);
        
        ca.addModule("M1", M1);
        ca.addModule("M2", M2);
        ca.addModule("M3", M3);

        double t;

        assertSame(s.peekNextEvent().getModule(), M1);
        t = ca.step();
        assertEquals("wrong time.", 2.0, t, 1e-18);
        
        assertSame(s.peekNextEvent().getModule(), M2);
        t = ca.step(); 
        assertEquals("wrong time.", 3.0, t, 1e-18);

        assertSame(s.peekNextEvent().getModule(), M1);
        t = ca.step();
        assertEquals("wrong time.", 4.0, t, 1e-18);
        
        assertSame(s.peekNextEvent().getModule(), M3);
        t = ca.step();
        assertEquals("wrong time.", 5.0, t, 1e-18);
        
        assertSame(s.peekNextEvent().getModule(), M1);
        t = ca.step();
        assertEquals("wrong time.", 6.0, t, 1e-18);

    }

    @Test
    public void testConstPipeNullModule() throws Exception {
        // A simple test to run the following three modules configuration.
        // ConstantModule A -> PipeModule B -> NullModule C

        ConstantModule A = new ConstantModule();
        PipeModule B = new PipeModule();
        NullModule C = new NullModule();
        
        A.setInterval(1.0);
        B.setInterval(1.0);
        C.setInterval(1.0);

        short[] zero = { 0, 0, 0 };
        short[] v = { 1, 2, 3 };

        A.setState("out1", v);

        assertTrue(Arrays.equals(A.getState("out1"), v));
        assertNotSame(A.getState("out1"), v); // ensure that v is cloned.

        A.makeOutPort("out1", 3);

        B.makeOutPort("out1", 3);
        B.connect(A, "out1", "in1"); // connection from A:out1 to B:in1
        B.mapPort("in1", "out1"); // B:out1 is a simple reflection of B:in1.

        C.connect(B, "out1", "in1"); // connection from B:out1 to C:in1

        Scheduler s = new VirtualTimeScheduler();
        CognitiveArchitecture ca = new CognitiveArchitecture(s);

        ca.addModule("A", A);
        ca.addModule("B", B);
        ca.addModule("C", C);

        // initially everything is [0,0,0].
        assertArrayEquals(zero, A.getOutPort("out1"));
        assertArrayEquals(zero, B.getInPort("in1"));
        assertArrayEquals(zero, B.getOutPort("out1"));
        assertArrayEquals(zero, C.getInPort("in1"));

        double t;
        
        // 1
        ca.step();
        ca.step();
        t = ca.step();

        assertEquals("wrong time.", 1.0, t, 1e-18);

        // 2
        ca.step();
        ca.step();
        t = ca.step();

        assertEquals("wrong time.", 2.0, t, 1e-18);

        // 3
        ca.step();
        ca.step();
        t = ca.step();

        assertEquals("wrong time.", 3.0, t, 1e-18);
        assertArrayEquals(v, A.getOutPort("out1"));
        assertArrayEquals(v, B.getInPort("in1"));
        assertArrayEquals(v, B.getOutPort("out1"));
        assertArrayEquals(v, C.getInPort("in1"));
    }
}
