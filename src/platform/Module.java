package platform;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Module {

//	double last_time;
	double interval;

	HashMap<String, Short[]> inputs;
	HashMap<String, Short[]> state;
	HashMap<String, Short[]> outputs;
	HashMap<String, Short[]> results;

	// double output buffers
	HashMap<String, Short[]> buffer_0;
	HashMap<String, Short[]> buffer_1;

	ArrayList<Connection> connections;
	
	public Module() {
		// prepare buffer 0 and 1 for output double buffering
		this.buffer_0 = new HashMap<String, Short[]>();
		this.buffer_1 = new HashMap<String, Short[]>();
		
		this.outputs = this.buffer_0;
		this.results = this.buffer_1;
	}	

	// 
	// outputs, state <- fire(inputs, state)
	//
	abstract void fire();
	
	public void collect_input() {
		for(Connection c: this.connections) {
			this.update_input(c);
		}
	}
	
	public void update_input(Connection c) {
		Short[] o = c.from_module.get_output(c.from_port_id).clone();
		this.outputs.put(c.to_port_id, o);
	}

	public void update_output() {
		// swap double buffers
		HashMap<String, Short[]> tmp;
		tmp = this.outputs;
		this.outputs = this.results;
		this.results = tmp;
	}

	public Short[] get_output(String name) {
		return outputs.get(name);
	}

}
