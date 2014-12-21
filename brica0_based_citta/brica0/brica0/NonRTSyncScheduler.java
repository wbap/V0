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
		this.num_steps += 1;
		this.current_time += this.interval;

		for (Module m: modules) {
			m.collect_input();
		}

		for (Module m: modules) {
			m.fire();
		}

		for (Module m: modules) {
			m.update_output();
		}
		
		return this.current_time;
	}

}
