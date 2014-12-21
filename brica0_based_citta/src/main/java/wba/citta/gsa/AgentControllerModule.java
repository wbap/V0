package wba.citta.gsa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import brica0.Connection;
import brica0.Module;

public class AgentControllerModule extends Module {

	@Override
	public void fire() {
		// GSA.getExecAgentRandomOder()と同じ方針の処理
		
		// TODO erase
		final int AGENT_COUNT = 8;
		
		ArrayList<Integer> ids = new ArrayList<Integer>();
	    for (int i=0; i<AGENT_COUNT; i++) {
	    	ids.add(i);
	    }
	    Collections.shuffle(ids);
	     
	    System.out.println("AgentControllerModule.fire()");
		
		for (int i: ids) {
			String port = String.valueOf(i);
			short[] inputData = get_in_port(port);
			
			int agentActionResult = inputData[0];
			if (agentActionResult == Agent.AGR_SUCCESS) {
				// TODO set output
				return;
			} 
		}
		
		// TODO set output
		return;
	}

}
