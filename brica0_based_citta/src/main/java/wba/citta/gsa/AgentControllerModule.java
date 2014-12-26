package wba.citta.gsa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import brica0.Connection;
import brica0.Module;

public class AgentControllerModule extends Module {

	
	@Override
	public void fire() {
		// GSA.getExecAgentRandomOder()と同じ方針の処理
		final int AGENT_COUNT = GSA.AGENT_COUNT;
		
		// select agent randomly
		Random rnd = new Random();
        int agentId = rnd.nextInt(AGENT_COUNT);
     
		for (int i=0; i<AGENT_COUNT; i++) {
	        short[] outputData = {0};
			if (agentId == i) {
				outputData[0] = GSA.EXEC;
			} else {
				outputData[0] = GSA.DO_NOTHING;
			}
			results.put("out" + String.valueOf(i), outputData);
		}
	}

}
