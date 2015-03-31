package wba.citta.gsa;

import java.util.List;

public interface SharedMemoryFactory {
    public ISharedMemory createInstance(int nodeNum, List<AgentInfo> agentInfoList); 
}
