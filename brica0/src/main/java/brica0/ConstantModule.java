package brica0;

import java.util.HashMap;

/*
 * ConstantModule simply copies states to out ports.
 * 
 * Users could use setState() method to define output of this Module.
 * Values of in ports are not used.
 */
public class ConstantModule extends Module {

	public ConstantModule() {
		super();
	}

	@SuppressWarnings("unchecked")
    @Override
	public void fire() {
		// this is a shallow copy.  does this work?
		results = (HashMap<String,short[]>)states.clone();
	}
	
}
