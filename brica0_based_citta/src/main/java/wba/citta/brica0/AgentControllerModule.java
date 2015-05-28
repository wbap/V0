package wba.citta.brica0;

import java.util.Random;

import wba.citta.gsa.GSA;
import brica0.Module;

public class AgentControllerModule extends Module {
    Random rnd = new Random(0);

    @Override
    public void fire() {
        // GSA.getExecAgentRandomOder()と同じ方針の処理
        final int AGENT_COUNT = GSA.AGENT_COUNT;
        
        // select agent randomly
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
