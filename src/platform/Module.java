package platform;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Module {

	protected double last_time;
	protected double interval;

// Input / output ports of this module.
// in_ports and out_ports are updated automatically.
// Users instead should work on state and results below.
	protected HashMap<String, short[]> in_ports;
	protected HashMap<String, short[]> out_ports;

	protected HashMap<String, short[]> states;
	protected HashMap<String, short[]> results;

// Double output buffers.  
//	These buffers are used internally by this class to hide calculation
//	process of this module (defined in the fire() method) from other modules.
	protected HashMap<String, short[]> buffer_0;
	protected HashMap<String, short[]> buffer_1;

	protected ArrayList<Connection> connections;
	
	public Module() {
		// prepare buffer 0 and 1 for output double buffering
		buffer_0 = new HashMap<String, short[]>();
		buffer_1 = new HashMap<String, short[]>();
		
		states = new HashMap<String, short[]>();
		
		out_ports = this.buffer_0;
		results = this.buffer_1;
	}	

	// 
	// outputs, state <- fire(inputs, state)
	//
	public abstract void fire();
	
	// make an in_port or an out_port of this Module.
	// In this prototype, vector lengths of ports are fixed.
	public void make_in_port(String id, int length) {
		this.in_ports.put(id, new short[length]);
	}

	public void make_out_port(String id, int length) {
		this.buffer_0.put(id, new short[length]);
		this.buffer_1.put(id, new short[length]);
	}

	public void delete_in_port(String id) {
		this.in_ports.remove(id);
	}

	public void delete_out_port(String id) {
		this.buffer_0.remove(id);
		this.buffer_1.remove(id);
	}

	public void set_state(String id, short[] v)	{
		this.states.put(id, v);
	}

	public void get_state(String id) {
		this.states.get(id);
	}

	public void clear_state() {
		this.states.clear();
	}
	
	
	public void collect_input() {
		for(Connection c: this.connections) {
			this.update_in_ports(c);
		}
	}
	
	public void update_in_ports(Connection c) {
		short[] o = c.from_module.get_out_port(c.from_port_id).clone();
		this.out_ports.put(c.to_port_id, o);
	}

	public void update_output() {
		// swap double buffers
		HashMap<String, short[]> tmp;
		tmp = this.out_ports;
		this.out_ports = this.results;
		this.results = tmp;
	}

	public short[] get_in_port(String id) {
		return this.in_ports.get(id);
	}
	
	public short[] get_out_port(String id) {
		return this.out_ports.get(id);
	}

}
