package wba.citta.gsa;

public class GSAAgentEvent extends GSAEvent {
    private static final long serialVersionUID = 1L;
    IGSAAgent agent;
    IGSAAgent.Status status;

    public GSAAgentEvent(GSAAgentEventSource source, IGSAAgent agent) {
        super(source);
        this.agent = agent;
        this.status = IGSAAgent.Status.NONE;
    }

    public GSAAgentEvent(GSAAgentEventSource source, IGSAAgent agent, AbstractGSAAgent.Status status) {
        super(source);
        this.agent = agent;
        this.status = status;
    }

    public IGSAAgent getAgent() {
        return agent;
    }

    public AbstractGSAAgent.Status getStatus() {
        return status;
    }
}
