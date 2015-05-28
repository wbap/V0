package brica0;

public class VirtualTimeSyncScheduler extends Scheduler {

    public double interval;

    public VirtualTimeSyncScheduler(double interval) {
        super();
        this.interval = interval;
    }

    public void reset() {
        super.reset();
    }

    @Override
    public double step() {
        for (Module m : modules) {
            m.input(this.currentTime);
        }

        for (Module m : modules) {
            m.fire();
        }

        this.currentTime += this.interval;  // time proceeds here.
        
        for (Module m : modules) {
            m.output(this.currentTime);
        }

        this.numSteps += 1;

        return this.currentTime;
    }

}
