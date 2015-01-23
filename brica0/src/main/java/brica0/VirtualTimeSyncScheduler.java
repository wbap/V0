package brica0;

public class VirtualTimeSyncScheduler extends Scheduler {

    public double interval;

    public VirtualTimeSyncScheduler(double interval) {
        super();
        this.interval = 1.0;
    }

    public void reset() {
        super.reset();
        this.interval = 1.0;
    }

    @Override
    public double step() {
        this.numSteps += 1;
        this.currentTime += this.interval;

        for (Module m : modules) {
            m.input();
        }

        for (Module m : modules) {
            m.fire();
        }

        for (Module m : modules) {
            m.updateOutputs();
        }

        return this.currentTime;
    }

}
