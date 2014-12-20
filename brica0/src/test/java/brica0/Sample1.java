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

        assertTrue(Arrays.equals(setout2, mm.get_out_port("out1")));
    }

}
