package brica0;

/**
 * PipeModule simply copies inputs to output ports of the same names.
 */
public class PipeModule extends Module {

	public PipeModule() {
		super();
	}
	
	
	public void mapPort(String inId, String outId) {
	    
	}
	
	
	// Simply copy values of in ports to out ports.
	//
	// For this copy to occur, 
	// 1) there must be an out port of the same id as the in port, and
	// 2) the out port and the in port must have an identical length.
	//
	@Override
	public void fire() {
		for (String s: this.inPorts.keySet()) {
			short[] v = this.getInPort(s);
			if(this.results.containsKey(s)) {
				short[] out = this.outPorts.get(s);
				if(out.length == v.length)
				{
					java.lang.System.arraycopy(v, 0, out, 0, v.length);
				}
			}
		}
	}

}
