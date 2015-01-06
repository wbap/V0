package brica0;

import java.util.HashMap;

/**
 * PipeModule simply copies inputs to output ports of the same names.
 */
public class PipeModule extends Module {

    protected HashMap<String, String> portMap;

    public PipeModule() {
        super();

        portMap = new HashMap<String, String>();
    }

    public void mapPort(String inId, String outId) {
        portMap.put(inId, outId);
    }

    // Simply copy values of in ports to out ports.
    //
    // For this copy to occur,
    // 1) there must be an out port of the same id as the in port, and
    // 2) the out port and the in port must have an identical length.
    //
    @Override
    public void fire() {
        for (String inId : portMap.keySet()) {
            String outId = portMap.get(inId);
            short[] v = this.getInPort(inId);
            if (this.results.containsKey(outId)) {
                this.results.put(outId, v);
            }
        }
    }

}
