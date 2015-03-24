package wba.citta.brica0;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wba.citta.gsa.GSAAgentEventSource;
import wba.citta.gsa.Goal;
import wba.citta.gsa.IGSAAgent;
import wba.citta.gsa.IGoalStack;
import wba.citta.gsa.ILatch;
import wba.citta.gsa.ISharedMemory;
import wba.citta.gsa.State;
import brica0.Module;

public abstract class BricaPerspective extends Module implements ISharedMemory, ILatch, IGoalStack {
    final Logger logger = LoggerFactory.getLogger(BricaPerspective.class);
    final int size;
    final String latchPort;
    final String latchAvailPort;
    final List<String> perNodeStackPorts;
    final List<String> perNodeStackPushAvailPorts;
    final List<String> perNodeStackTopPorts;
    final List<String> perNodeStackRemoveAllOpPorts;
    final List<String> perNodeStackRemoveOpPorts;
    final List<String> perNodeStackTopDesignationStatePorts;
    GSAAgentEventSource gsa;
    boolean stateAvailable;
    boolean removeOpPending;
    boolean removeAllOpPending;
    boolean[] removeOp;
    boolean pushOpPending;
    boolean[] pushOp;
    short[] pushedGoal;
    IGSAAgent agent;

    @Override
    public void bind(GSAAgentEventSource gsa) {
        this.gsa = gsa;
    }

    public void bind(IGSAAgent gsaAgent) {
        this.agent = gsaAgent;
    }

    @Override
    public IGoalStack getGoalStack() {
        return this;
    }

    @Override
    public ILatch getLatch() {
        return this;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public List<GoalStackElement> getGoalStackForNode(int i) {
        // unused in perspective
        throw new UnsupportedOperationException();
    }

    @Override
    public GoalStackElement getGoalForNode(int index) {
        // unused in perspective
        throw new UnsupportedOperationException();
    }

    @Override
    public void pushGoal(int agid, boolean[] useNode, State state) {
        assert perNodeStackPorts.size() == useNode.length;
        if (pushOpPending)
            throw new IllegalArgumentException();
        for (int i = 0, j = 0; i < useNode.length; i++) {
            if (useNode[i]) {
                pushedGoal[i] = (short)(int)state.get(j);
                pushOp[i] = true;
                j++;
            }
        }
        logger.debug("pushGoal({}, {}, {})", agid, useNode, state);
        pushOpPending = true;
    }

    @Override
    public boolean removeGoal(int agid, boolean[] useNode) {
        assert size == useNode.length;
        for (int i = 0; i < size; i++) {
            if (useNode[i] && getInPort(perNodeStackTopDesignationStatePorts.get(i))[0] == 0) {
                return false;
            }
        }
        if (removeOpPending || removeAllOpPending)
            throw new IllegalStateException();
        System.arraycopy(useNode,  0,  removeOp,  0,  removeOp.length);
        removeOpPending = true;
        logger.debug("removeGoal()");
        return true;
    }

    @Override
    public void removeAllGoal() {
        removeAllOpPending = true;
        logger.debug("removeAllGoal()");
    }

    @Override
    public Goal getGoalValueArray() {
        final int l = getSize();
        final Goal g = new Goal(l);
        for (int i = 0; i < l; i++) {
        	final short[] v = getInPort(perNodeStackTopPorts.get(i));
        	logger.trace("i={}, v={}", i, v);
        	if (v != null)
        		g.set(i, (int)v[0]);
        }
        return g;
    }

    @Override
    public State getCurrentGoal(int agid, boolean[] useNode) {
        final int l = getSize();
        assert useNode.length == l;
        int c = 0;
        for (int i = 0; i < useNode.length; i++) {
            if (useNode[i])
                c++;
        }
        final Goal goal = getGoalValueArray();
        State retval = new State(c);
        for (int i = 0, j = 0; i < l; i++) {
            if (useNode[i]) {
                retval.set(j, goal.get(i));
                j++;
            }
        }
        return retval;
    }

    @Override
    public Goal getState() {
        final Goal retval = new Goal(size);
        final short[] state = getInPort(latchPort);
        assert state.length == retval.size();
        for (int i = 0; i < size; i++) {
            retval.set(i, (int)state[i]);
        }
        return retval;
    }

    @Override
    public State getState(boolean[] useNode) {
        final int l = getSize();
        assert useNode.length == l;
        int c = 0;
        for (int i = 0; i < useNode.length; i++) {
            if (useNode[i])
                c++;
        }
        final short[] state = getInPort(latchPort);
        State retval = new State(c);
        for (int i = 0, j = 0; i < l; i++) {
            if (useNode[i]) {
                retval.set(j, (int)state[i]);
                j++;
            }
        }
        return retval;
    }

    @Override
    public void setState(Goal state) {
        final int l = getSize();
        assert state.size() == l;
        short[] stateToSet = new short[l];
        for (int i = 0; i < l; i++) {
            stateToSet[i] = (short)(int)state.get(i);
        }
        results.put(latchPort, stateToSet);
        stateAvailable = true;
    }

    public abstract void doFire();

    protected void initState() {
        logger.trace("{}: initState()", this);
        stateAvailable = false;
        removeOpPending = false;
        removeAllOpPending = false;
        pushOpPending = false;
        {
            for (int i = 0; i < size; i++) {
                pushOp[i] = false;
                removeOp[i] = false;
            }
        }
    }

    protected void syncState() {
        logger.trace("{}: syncState()", this);
        results.put(latchAvailPort, new short[] { (short) (stateAvailable ? 1: 0) });
        {
            short[] v = new short[] { (short) (removeAllOpPending ? 1: 0) };
            for (int i = 0; i < size; i++)
                results.put(perNodeStackRemoveAllOpPorts.get(i), v);
        }
        {
            for (int i = 0; i < size; i++)
                results.put(perNodeStackRemoveOpPorts.get(i), new short[] { (short)(removeOp[i] ? 1: 0) });
        }
        {
            for (int i = 0; i < size; i++) {
                results.put(perNodeStackPushAvailPorts.get(i), new short[] { (short) (pushOp[i] ? 1: 0) });
                if (pushOp[i]) {
                    results.put(perNodeStackPorts.get(i), new short[] { pushedGoal[i] });
                }
            }
        }
    }

    @Override
    public void fire() {
        initState();
        doFire();
        syncState();
    }
    
    public BricaPerspective(int size, String latchPort,
            String latchAvailPort,
            List<String> perNodeStackPorts,
            List<String> perNodeStackPushAvailPorts,
            List<String> perNodeStackTopPorts,
            List<String> perNodeStackRemoveAllOpPorts,
            List<String> perNodeStackRemoveOpPorts,
            List<String> perNodeStackTopDesignationStatePorts) {
        super();
        this.size = size;
        this.latchPort = latchPort;
        this.latchAvailPort = latchAvailPort;
        this.perNodeStackPorts = perNodeStackPorts;
        this.perNodeStackPushAvailPorts = perNodeStackPushAvailPorts;
        this.perNodeStackTopPorts = perNodeStackTopPorts;
        this.perNodeStackRemoveAllOpPorts = perNodeStackRemoveAllOpPorts;
        this.perNodeStackRemoveOpPorts = perNodeStackRemoveOpPorts;
        this.perNodeStackTopDesignationStatePorts = perNodeStackTopDesignationStatePorts;
        this.makeOutPort(latchPort, size);
        this.makeOutPort(latchAvailPort, size);
        //this.makeInPort(latchPort, size);
        for (String portId: this.perNodeStackPorts) {
        	this.makeOutPort(portId, 1);
        }
        for (String portId: this.perNodeStackPushAvailPorts) {
        	this.makeOutPort(portId, 1);
        }
        for (String portId: this.perNodeStackRemoveAllOpPorts) {
        	this.makeOutPort(portId, 1);
        }
        for (String portId: this.perNodeStackRemoveOpPorts) {
        	this.makeOutPort(portId, 1);
        }
        /*
        for (String portId: this.perNodeStackTopPorts) {
        	this.makeInPort(portId, 1);
        }
        for (String portId: this.perNodeStackTopDesignationStatePorts) {
        	this.makeInPort(portId, 1);
        }
        */
        
        this.removeOp = new boolean[size];
        this.pushOp = new boolean[size];
        this.pushedGoal = new short[size];
    }
}
