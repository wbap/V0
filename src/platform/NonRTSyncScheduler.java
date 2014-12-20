package platform;

public class NonRTSyncScheduler extends Scheduler{

	public float interval;
	
	public NonRTSyncScheduler(double interval) {
		super();
		this.interval = 1.0f;
	}
	
	public void reset() {
		super.reset();
		this.interval = 1.0f;
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
