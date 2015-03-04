package wba.citta.gsa;

public interface AgentExecutionStrategy {
    public interface Context {
        public Agent nextAgent();
    }

    public Context createContext(GSAIteration gsa);
}