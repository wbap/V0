package brica0;

/**
 * PipeModule simply copies inputs to output ports of the same names.
 */
public class PipeModule extends Module {

	public PipeModule() {
		super();
	}
	
	// Simply copy values of in_ports to out_ports.
	//
	// For this copy to occur, 
	// 1) there must be an out_port of the same id as the in_port, and
	// 2) the out_port and the in_port must have an identical length.
	//
	@Override
	public void fire() {
		for (String s: this.in_ports.keySet()) {
			short[] v = this.get_in_port(s);
			if(this.results.containsKey(s)) {
				short[] out = this.out_ports.get(s);
				if(out.length == v.length)
				{
					java.lang.System.arraycopy(v, 0, out, 0, v.length);
				}
			}
		}
	}

}
