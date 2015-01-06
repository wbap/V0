package brica0;

import java.util.ArrayList;

public abstract class Scheduler {

    protected long numSteps;
    protected double currentTime;

    protected ArrayList<Module> modules;

    public Scheduler() {
        numSteps = 0;
        currentTime = 0.0;

        modules = new ArrayList<Module>();
    }

    public void reset() {
        modules.clear();
        this.numSteps = 0;
        this.currentTime = 0.0;
    }

    public void addModule(Module m) {
        modules.add(m);
    }

    public void update(CognitiveArchitecture ca) {
        modules = new ArrayList<Module>(ca.modules.values());
    }

    public abstract double step();

}
