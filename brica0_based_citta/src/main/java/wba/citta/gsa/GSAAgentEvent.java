package wba.citta.gsa;

public class GSAAgentEvent extends GSAEvent {
    private static final long serialVersionUID = 1L;
    Agent agent;
    Agent.Status status;

    public GSAAgentEvent(GSA source, Agent agent) {
        super(source);
        this.agent = agent;
        this.status = Agent.Status.NONE;
    }

    public GSAAgentEvent(GSA source, Agent agent, Agent.Status status) {
        super(source);
        this.agent = agent;
        this.status = status;
    }

    public Agent getAgent() {
        return agent;
    }

    public Agent.Status getStatus() {
        return status;
    }
}
