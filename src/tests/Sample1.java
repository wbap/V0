package tests;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import platform.*;

public class Sample1 {

	@Test
	public void testCAConstruct() {
		try {
			Scheduler s = new NonRTSyncScheduler(1.0);
			@SuppressWarnings("unused")
			CognitiveArchitecture ca = new CognitiveArchitecture(s);
		}
		catch(Exception e) {
			fail("failed to create a new CognitiveArchitecture with a NonRTSyncScheduler.");
		}
		
	}

	@Test
	public void testAddModule() {
		Scheduler s = new NonRTSyncScheduler(1.0);
		CognitiveArchitecture ca = new CognitiveArchitecture(s);

		try {
			Module m = new PipeModule();
			
			ca.add_module("M1", m);

			Module mm = ca.get_module("M1");
			assertSame("failed to get module from ca.", m, mm);
		}
		catch(Exception e) {
			fail("failed to create a module and add it to CognitiveArchitecture.");
		}
		
	}

	
	@Test
	public void testConstantModule() {
		Scheduler s = new NonRTSyncScheduler(1.0);
		CognitiveArchitecture ca = new CognitiveArchitecture(s);
		Module cm = new ConstantModule();

		short[] out1 = {0,1,2};

		cm.set_state("out1", out1);
		ca.add_module("M1", cm);
		
		
		double t = ca.step();
		
		assertEquals("wrong time after one step.", 1.0, t, 1e-18);
		
		Module mm = ca.get_module("M1");
		assertSame("failed to get module from ca.", cm, mm);

		
	}

}
