package brica0;

public class NonRTSyncScheduler extends Scheduler{

	public double interval;
	
	public NonRTSyncScheduler(double interval) {
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

		for (Module m: modules) {
			m.collectInput();
		}

		for (Module m: modules) {
			m.fire();
		}

		for (Module m: modules) {
			m.updateOutput();
		}
		
		return this.currentTime;
	}

}
