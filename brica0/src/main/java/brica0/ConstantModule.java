package brica0;

import java.util.HashMap;

/*
 * ConstantModule simply copies states to out ports.
 * 
 * Users could use setState() method to define output of this Module.
 * Values of in ports are not used.
 */
public class ConstantModule extends Module {

	boolean dirty;
	
	public ConstantModule() {
		super();
		dirty = false;
	}

	@Override
	public void setState(String id, short[] v) {
		dirty = true;
		super.setState(id, v);
	}
	
	@Override
	public void fire() {
		if(! dirty) {
			return;
		}

		// this is a shallow copy.  does this work?
		results = new HashMap<String, short[]>(states);
	}
	
}
