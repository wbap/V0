package brica0;

import static org.junit.Assert.*;

public class RealTimeSyncScheduler extends Scheduler {

    public double interval;

    public long intervalMillis;
    public long lastInputTime;
    public long lastOutputTime;
    public long lastDt;
    
    public RealTimeSyncScheduler(double interval) {
        super();
        setInterval(interval);
    }

    public void reset() {
        super.reset();
        this.interval = 1.0;
    }
    
    public void setInterval(double interval) {
        this.interval = interval;
        this.intervalMillis = (long)this.interval / 1000;
        assert(this.intervalMillis >= 1);
    }

    @Override
    public double step() {
        this.lastInputTime = System.currentTimeMillis();
        
        for (Module m : modules) {
            m.input((double)this.lastInputTime);
        }

        for (Module m : modules) {
            m.fire();
        }

        long spent = System.currentTimeMillis() - this.lastInputTime;
        this.lastDt = this.intervalMillis - spent;
        
        if(this.lastDt > 0) {
            synchronized (this) {
                try {
                    this.wait(this.lastDt);
                } catch (InterruptedException e) {
                    System.out.print("Unexpected interruption.");
                }
            }
        }
        
        this.lastOutputTime = System.currentTimeMillis();

        for (Module m : modules) {
            m.output((double)this.lastOutputTime);
        }

        this.numSteps += 1;

        return this.currentTime;
    }

}
