package platform;

import java.util.HashMap;

public class CognitiveArchitecture {

	Scheduler scheduler;
	HashMap<String, Module> modules;		
	
	// how does this get connected to sensors / actuators?
	
	
	public CognitiveArchitecture(Scheduler s) {
		this.scheduler = s;
	}

	public void add_module(String id, Module module) {
		this.modules.put(id, module);
	}
	
	public Module get_module(String id) {
		return this.modules.get(id);
	}
	
	
	
}
