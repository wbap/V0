package wba.citta.gsa;

import java.util.EventListener;

public interface FailAgentTreeEventListener extends EventListener {
    public void treeChanged(FailAgentTreeEvent evt); 
}
