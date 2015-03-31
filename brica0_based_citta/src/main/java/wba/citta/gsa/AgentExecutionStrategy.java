package wba.citta.gsa;

public interface AgentExecutionStrategy {
    public interface Context {
        public IGSAAgent nextAgent();
    }

    public Context createContext(IGSAIteration igsaIteration);
}