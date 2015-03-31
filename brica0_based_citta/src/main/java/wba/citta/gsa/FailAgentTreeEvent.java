package wba.citta.gsa;

import java.util.EventObject;

public class FailAgentTreeEvent extends EventObject {
    private static final long serialVersionUID = 1L;

    public FailAgentTreeEvent(FailAgentTree source) {
        super(source);
    }

    @Override
    public FailAgentTree getSource() {
        return (FailAgentTree)this.source; 
    }
}
