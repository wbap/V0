package brica0;

import java.util.ArrayList;

public abstract class Scheduler {

	protected long num_steps;
	protected double current_time;
	
	protected ArrayList<Module> modules;
	
	public Scheduler() {
		num_steps = 0;
		current_time = 0.0;
		
		modules = new ArrayList<Module>();
	}

	public void reset() {
		modules.clear();
		this.num_steps = 0;
		this.current_time = 0.0;
	}
	
	public void addModule(Module m) {
		modules.add(m);
	}
	
	public void update(CognitiveArchitecture ca) {
	    modules = new ArrayList<Module>(ca.modules.values());
	}
	
	public abstract double step();


	
}
