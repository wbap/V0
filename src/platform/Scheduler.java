package platform;

import java.util.ArrayList;

public abstract class Scheduler {

	public long num_steps;
	public double current_time;
	
	ArrayList<Module> modules;
	
	public Scheduler() {
		this.num_steps = 0;
		this.current_time = 0.0;
	}

	public void reset() {
		modules.clear();
		this.num_steps = 0;
		this.current_time = 0.0;
	}
	
	public void addModule(Module m) {
		modules.add(m);
	}
	
	public abstract double step();


	
}
