package brica0;

import static org.junit.Assert.*;

import org.junit.Test;
import java.util.Arrays;

public class RealTimeSyncSchedulerTest {

    @Test
    public void testCAConstruct() {
        Scheduler s = new RealTimeSyncScheduler(1.0);
        @SuppressWarnings("unused")
        CognitiveArchitecture ca = new CognitiveArchitecture(s);
    }

    @Test
    public void testAddModule() {
        Scheduler s = new RealTimeSyncScheduler(1.0);
        CognitiveArchitecture ca = new CognitiveArchitecture(s);

        Module m = new NullModule();

        ca.addModule("M1", m);

        Module mm = ca.getModule("M1");
        assertSame("failed to get module from ca.", m, mm);
    }

    @Test
    public void testConstantModule() {
        Scheduler s = new RealTimeSyncScheduler(1.0);
        CognitiveArchitecture ca = new CognitiveArchitecture(s);

        Module cm = new ConstantModule();
        cm.makeOutPort("out1", 3);

        short[] setout = { 0, 1, 2 };

        cm.setState("out1", setout);
        ca.addModule("M1", cm);

        double t = ca.step();
        assertEquals("wrong time after one step.", 1.0, t, 1e-18);

        Module mm = ca.getModule("M1");
        assertSame("failed to get module from ca.", cm, mm);

        assertTrue(Arrays.equals(setout, mm.getOutPort("out1")));

        short[] setout2 = { 3, 4, 5 };
        mm.setState("out1", setout2);

        assertTrue(Arrays.equals(setout, mm.getOutPort("out1")));

        ca.step();
        ca.step();

        assertTrue(Arrays.equals(setout2, mm.getOutPort("out1")));
    }

    @Test
    public void testConstPipeNullModule() throws Exception {
        // A simple test to run the following three modules configuration.
        // ConstantModule A -> PipeModule B -> NullModule C

        ConstantModule A = new ConstantModule();
        PipeModule B = new PipeModule();
        NullModule C = new NullModule();

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

        Scheduler s = new RealTimeSyncScheduler(1.0);
        CognitiveArchitecture ca = new CognitiveArchitecture(s);

        ca.addModule("A", A);
        ca.addModule("B", B);
        ca.addModule("C", C);

        // initially everything is [0,0,0].
        assertTrue(Arrays.equals(zero, A.getOutPort("out1")));
        assertTrue(Arrays.equals(zero, B.getInPort("in1")));
        assertTrue(Arrays.equals(zero, B.getOutPort("out1")));
        assertTrue(Arrays.equals(zero, C.getInPort("in1")));

        // 1
        ca.step();

        assertTrue(Arrays.equals(v, A.getOutPort("out1")));
        assertTrue(Arrays.equals(zero, B.getInPort("in1")));
        assertTrue(Arrays.equals(zero, B.getOutPort("out1")));
        assertTrue(Arrays.equals(zero, C.getInPort("in1")));

        // 2
        ca.step();

        assertTrue(Arrays.equals(v, A.getOutPort("out1")));
        assertTrue(Arrays.equals(v, B.getInPort("in1")));
        assertTrue(Arrays.equals(v, B.getOutPort("out1")));
        assertTrue(Arrays.equals(zero, C.getInPort("in1")));

        // 3
        ca.step();

        assertTrue(Arrays.equals(v, A.getOutPort("out1")));
        assertTrue(Arrays.equals(v, B.getInPort("in1")));
        assertTrue(Arrays.equals(v, B.getOutPort("out1")));
        assertTrue(Arrays.equals(v, C.getInPort("in1")));

    }

}
