package brica0;

import java.util.HashMap;

public class CognitiveArchitecture {

	Scheduler scheduler;
	HashMap<String, Module> modules;		
	
	// how does this get connected to sensors / actuators?
	
	
	public CognitiveArchitecture(Scheduler s) {
		scheduler = s;
		modules = new HashMap<String, Module>();
	}

	public void add_module(String id, Module module) {
		modules.put(id, module);

		update_scheduler();
	}
	
	public Module get_module(String id) {
		return modules.get(id);
	}
	
	public double step() {
		return scheduler.step();
	}

	protected void update_scheduler() {
	    scheduler.update(this);
	}
	
}