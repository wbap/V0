package wba.citta.gsa;


public interface ISharedMemory {
    public abstract void bind(GSAAgentEventSource gsa);
    
    public abstract IGoalStack getGoalStack();

    public abstract ILatch getLatch();

    public abstract int getSize();
}