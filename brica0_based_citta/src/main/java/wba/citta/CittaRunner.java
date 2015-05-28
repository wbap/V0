package wba.citta;

public interface CittaRunner {
    public void addStepEventListener(StepEventListener listener);

    public void removeStepEventListener(StepEventListener listener);    

    public void addIterationEventListener(IterationEventListener listener);

    public void removeIterationEventListener(IterationEventListener listener);    
}
