package wba.citta.brica0;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brica0.Module;

public class PerNodeStackArray extends Module {
    static Logger logger = LoggerFactory.getLogger(PerNodeStackArray.class);
    static class Element {
        int agentIndex;
        short[] value;
        Element(int agentIndex, short[] value) {
            super();
            this.agentIndex = agentIndex;
            this.value = value;
        }
    }
    final CellBackedSharedMemory sharedMemory;
    final LinkedList<Element> elements = new LinkedList<>();
    final List<String> valuePorts;
    final String stackTopPort;
    final List<String> removeOpAvailPorts;
    final List<String> removeAllOpPorts;
    final List<String> pushOpAvailPorts;
    final List<String> stackTopDesignationPorts;

    private int lookup(List<String> ports) {
        for (int i = 0; i < ports.size(); i++) {
            if (getInPort(ports.get(i))[0] != 0)
                return i;
        }
        return -1;
    }

    @Override
    public void fire() {
        int removeAllOpInitiatingAgent = lookup(removeAllOpPorts);
        int removeOpInitiatingAgent = lookup(removeOpAvailPorts);
        int pushOpInitiatingAgent = lookup(pushOpAvailPorts);
        Element lastElement = elements.size() > 0 ? elements.getLast(): null;
        if (removeAllOpInitiatingAgent >= 0) {
            logger.debug("removeAll");
            if (removeOpInitiatingAgent >= 0 || pushOpInitiatingAgent >= 0)
                throw new IllegalStateException();
            elements.clear();
            sharedMemory.fireSharedMemoryChanged();
        } else if (removeOpInitiatingAgent >= 0) {
            logger.debug("remove");
            if (pushOpInitiatingAgent >= 0)
                throw new IllegalStateException();
            if (lastElement == null)
                throw new IllegalStateException();
            if (removeOpInitiatingAgent != lastElement.agentIndex)
                throw new IllegalStateException();
            elements.removeLast();
            sharedMemory.fireSharedMemoryChanged();
        } else if (pushOpInitiatingAgent >= 0) {
            logger.debug("push");
            short[] value = getInPort(valuePorts.get(pushOpInitiatingAgent));
            lastElement = new Element(pushOpInitiatingAgent, value);
            elements.add(lastElement);
            sharedMemory.fireSharedMemoryChanged();
        }
        if (lastElement != null) {
            results.put(stackTopPort, lastElement.value);
        }
        for (int i = 0; i < stackTopDesignationPorts.size(); i++) {
            final int v = lastElement != null && lastElement.agentIndex == i ? 1: 0;
            results.put(stackTopDesignationPorts.get(i), new short[] { (short)v });
        }
    }

    public PerNodeStackArray(
            CellBackedSharedMemory sharedMemory,
            String stackTopPort, List<String> valuePorts,
            List<String> removeOpAvailPorts, List<String> removeAllOpPorts,
            List<String> pushOpAvailPorts, List<String> stackTopDesignationPorts) {
        super();
        this.sharedMemory = sharedMemory;
        this.stackTopPort = stackTopPort;
        this.valuePorts = valuePorts;
        this.removeOpAvailPorts = removeOpAvailPorts;
        this.removeAllOpPorts = removeAllOpPorts;
        this.pushOpAvailPorts = pushOpAvailPorts;
        this.stackTopDesignationPorts = stackTopDesignationPorts;
    	this.makeOutPort(stackTopPort, 1);
    	/*
    	for (String portId: valuePorts) {
    		this.makeInPort(portId, 1);
    	}
    	for (String portId: removeOpAvailPorts) {
    		this.makeInPort(portId, 1);
    	}
    	for (String portId: removeAllOpPorts) {
    		this.makeInPort(portId, 1);
    	}    	
    	for (String portId: pushOpAvailPorts) {
    		this.makeInPort(portId, 1);
    	}
    	*/    	
    	for (String portId: stackTopDesignationPorts) {
    		this.makeOutPort(portId, 1);
    	}
    }
}
