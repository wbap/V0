package wba.citta.gsa;

import java.util.List;

public class DefaultSharedMemoryFactory implements SharedMemoryFactory {

    @Override
    public ISharedMemory createInstance(int nodeNum,
            List<AgentInfo> agentInfoList) {
        return new SharedMemory(nodeNum);
    }
}
