package wba.citta;

import java.util.EventListener;


public interface StepEventListener extends EventListener {
    public void nextStep(StepEvent evt);
}
