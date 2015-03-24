package wba.citta.brica0;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brica0.Module;

public class Latch extends Module {
    static final Logger logger = LoggerFactory.getLogger(Latch.class);
    final List<String> inPorts;
    final List<String> inAvailPorts;
    final String outPort;
    short[] v;
    final CellBackedSharedMemory sharedMemory;

    @Override
    public void fire() {
        for (int i = 0; i < inAvailPorts.size(); i++) {
            // XXX: what if multiple inports have values available?
            if (getInPort(inAvailPorts.get(i))[0] != 0) {
                v = getInPort(inPorts.get(i));
                sharedMemory.fireSharedMemoryChanged();
            }
        }
        results.put(outPort, v);
    }

    public short[] get() {
        return v;
    }

    public Latch(CellBackedSharedMemory sharedMemory, int size, List<String> inPorts, List<String> inAvailPorts, String outPort) {
        this.sharedMemory = sharedMemory;
        this.inPorts = inPorts;
        this.inAvailPorts = inAvailPorts;
        this.outPort = outPort;
        this.makeOutPort(outPort, size);
        this.v = new short[size];
    }
}
