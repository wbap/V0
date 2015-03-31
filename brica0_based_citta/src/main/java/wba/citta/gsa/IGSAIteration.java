package wba.citta.gsa;

import java.util.List;

public interface IGSAIteration {
    public List<IGSAAgent> getAgents();

    public List<IGSAAgent> getUnusedAgents();

    public List<IGSAAgent> getUsedAgents();

    public boolean tryNext();
}