package brica0;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Module {

	protected double lastTime;
	protected double interval;

// Input / output ports of this module.
// in ports and out ports are updated automatically.
// Users instead should work on state and results below.
	protected HashMap<String, short[]> inPorts;
	protected HashMap<String, short[]> outPorts;

	protected HashMap<String, short[]> states;
	protected HashMap<String, short[]> results;

// Double output buffers.  
//	These buffers are used internally by this class to hide calculation
//	process of this module (defined in the fire() method) from other modules.
	protected HashMap<String, short[]> buffer0;
	protected HashMap<String, short[]> buffer1;

	protected ArrayList<Connection> connections;
	
	public Module() {
	    connections = new ArrayList<Connection>();
	    
		// prepare buffer 0 and 1 for output double buffering
		buffer0 = new HashMap<String, short[]>();
		buffer1 = new HashMap<String, short[]>();

		inPorts = new HashMap<String, short[]>();
		states = new HashMap<String, short[]>();
		
		outPorts = buffer0;
		results = buffer1;
	}	

	// 
	// outputs, state <- fire(inputs, state)
	//
	public abstract void fire();
	
	// make an in port or an out port of this Module.
	// In this prototype, vector lengths of ports are fixed.
	public void makeInPort(String id, int length) {
		inPorts.put(id, new short[length]);
	}

	public void makeOutPort(String id, int length) {
		buffer0.put(id, new short[length]);
		buffer1.put(id, new short[length]);
	}

	public void removeInPort(String id) {
		inPorts.remove(id);
	}

	public void removeOutPort(String id) {
		buffer0.remove(id);
		buffer1.remove(id);
	}

	public void setState(String id, short[] v)	{
		states.put(id, (short[])v.clone());
	}

    public short[] getState(String id) {
        return states.get(id);
    }

	public void clearState() {
		states.clear();
	}

	public void connect(Module from, String fromId, String toId) {
	    Connection c = new Connection(from, fromId, toId);
	    connections.add(c);
	    
	    // make a in port of the same length as the target out port.
	    int length = from.getOutPort(fromId).length;
	    makeInPort(toId, length);
	}
	
	public short[] getResult(String id) {
	    return results.get(id);
	}
	
	public void collectInput() {
		for(Connection c: connections) {
			updateInPort(c);
		}
	}
	
	public void updateInPort(Connection c) {
	    short[] o = c.fromModule.getOutPort(c.fromPortId);
	    o = o.clone();   // copy
		inPorts.put(c.toPortId, o);
	}

	public void updateOutput() {
		// swap double buffers
		HashMap<String, short[]> tmp;
		tmp = outPorts;
		outPorts = results;
		results = tmp;
	}

	public short[] getInPort(String id) {
		return inPorts.get(id);
	}
	
	public short[] getOutPort(String id) {
		return outPorts.get(id);
	}

}
