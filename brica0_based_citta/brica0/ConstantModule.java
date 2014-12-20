package brica0;

import java.util.HashMap;

/*
 * ConstantModule simply copies states to out_ports.
 * 
 * Users could use set_state() method to define output of this Module.
 * Values of in_ports are not used.
 */
public class ConstantModule extends Module {

	//boolean dirty;
	
	public ConstantModule() {
		super();
//		dirty = false;
	}

//	@Override
//	public void set_state(String id, short[] v) {
//		dirty = true;
//		super.set_state(id, v);
//	}
	
	@Override
	public void fire() {
/*		if(! dirty) {
			return;
		}*/
		// this is a shallow copy.  does this work?
		results = new HashMap<String, short[]>(states);
	}
	
}
