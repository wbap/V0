package brica0;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Module class represents a computational unit in a CognitiveArchitecture.
 * <p>
 * A <tt>Module</tt> has zero or more <tt>inPorts</tt>, zero or more
 * <tt>outPorts</tt>, and <tt>states</tt> that stores internal states, all of
 * these being named buffers (HashMaps that maps a string ID and a value of type
 * short[]).
 * 
 * @author shafi
 */
public abstract class Module {

    protected double lastInputTime;
    protected double lastOutputTime;
    protected double interval;
    
    // Input / output ports of this module.
    // in ports and out ports are updated automatically.
    // Users instead should work on state and results below.
    protected HashMap<String, short[]> inPorts;
    protected HashMap<String, short[]> outPorts;

    protected HashMap<String, short[]> states;
    protected HashMap<String, short[]> results;

    // Double output buffers.
    // These buffers are used internally by this class to hide calculation
    // process of this module (defined in the fire() method) from other modules.
    protected HashMap<String, short[]> buffer0;
    protected HashMap<String, short[]> buffer1;

    protected ArrayList<Connection> connections;

    public Module() {
        lastInputTime = 0.0;
        lastOutputTime = 0.0;
        interval = 1.0;
        
        connections = new ArrayList<Connection>();

        // prepare buffer 0 and 1 for output double buffering
        buffer0 = new HashMap<String, short[]>();
        buffer1 = new HashMap<String, short[]>();

        inPorts = new HashMap<String, short[]>();
        states = new HashMap<String, short[]>();

        outPorts = buffer0;
        results = buffer1;
    }

    double getLastInputTime() {
        return lastInputTime;
    }
    
    double getLastOutputTime() {
        return lastOutputTime;
    }
    
    double getInterval() {   // probably be renamed to MinimumInterval or so.
        return interval;
    }
    
    
    /**
     * Users may override <tt>fire()</tt> method to define a new sub-class of
     * <tt>Module</tt>. <tt>fire()</tt> method implements a function of the form<br>
     * <tt>results, states <- fire(inPorts, states)</tt>,<br>
     * which states that this method should be a mutator of these two member
     * variables (<tt>results</tt> and <tt>states</tt>), and the result of the
     * computation should solely depend on the values stores in (
     * <tt>inPorts</tt>, <tt>states</tt>).<br>
     * One important note here is that it shall mutate <tt>results</tt> member
     * variable, and not <tt>outPorts</tt>. Values stored in <tt>results</tt>
     * will automatically be copied to <tt>outPorts</tt> by the scheduler which
     * calls <tt>updateOutput()</tt> method of this <tt>Module</tt>, so these be
     * visible and accessible from other Modules. This procedure is especially
     * necessary in concurrent execution of multiple Modules to avoid
     * contention.
     */
    public abstract void fire();

    /**
     * Make an in-port of this Module.
     * 
     * @param id
     *            a string ID.
     * @param length
     *            an initial length of the value vector.
     */
    public void makeInPort(String id, int length) {
        inPorts.put(id, new short[length]);
    }

    /**
     * Remove an in-port of this Module.
     * 
     * @param id
     *            a string ID.
     */
    public void removeInPort(String id) {
        inPorts.remove(id);
    }

    /**
     * Get a value vector of an in port.
     * 
     * @param id
     *            a string ID of the in port.
     * @return a value vector
     */
    public short[] getInPort(String id) {
        return inPorts.get(id);
    }

    /**
     * Make an out-port of this Module.
     * 
     * @param id
     *            a string ID.
     * @param length
     *            an initial length of the value vector.
     */
    public void makeOutPort(String id, int length) {
        buffer0.put(id, new short[length]);
        buffer1.put(id, new short[length]);
    }

    /**
     * Remove an in-port of this Module.
     * 
     * @param id
     *            a string ID.
     */
    public void removeOutPort(String id) {
        buffer0.remove(id);
        buffer1.remove(id);
    }

    /**
     * Get a value vector of an out port.
     * 
     * @param id
     *            a string ID of the out port.
     * @return a value vector
     */
    public short[] getOutPort(String id) {
        return outPorts.get(id);
    }

    /**
     * Set a state of this Module. If the state value of the specified ID does
     * not exist, it creates one.
     * 
     * @param id
     *            a string ID.
     * @param v
     *            a value vector.
     */
    public void setState(String id, short[] v) {
        states.put(id, (short[]) v.clone());
    }

    /**
     * 
     * @param id
     *            a string ID.
     * @return a state value vector.
     */
    public short[] getState(String id) {
        return states.get(id);
    }

    /**
     * Delete all state values.
     */
    public void clearState() {
        states.clear();
    }

    /**
     * Set a result value.
     * 
     * @param id
     *            a string ID.
     * @param v
     *            a value vector.
     */
    public void setResult(String id, short[] v) {
        results.put(id, (short[]) v.clone());
    }

    /**
     * 
     * @param id
     *            a string ID.
     * @return a value vector.
     */
    public short[] getResult(String id) {
        return results.get(id);
    }

    /**
     * Delete all values in the result buffer.
     */
    public void clearResult() {
        results.clear();
    }

    /**
     * Add a <tt>Connection</tt> to this <tt>Module</tt>.
     * 
     * @param c
     *            a <tt>Connection</tt>.
     */
    public void makeConnection(Connection c) {
        connections.add(c);
    }

    /**
     * Add a connection from port <tt>fromID</tt> of <tt>from</tt> Module to
     * <tt>toID</tt> port of this <tt>Module</tt>.
     * 
     * @param from
     * @param fromId
     * @param toId
     * @throws Exception
     */
    public void connect(Module from, String fromID, String toID)
            throws Exception {
        // make sure that no connection to the same inPort already exists.
        for (Connection c : connections) {
            if (toID.equals(c.toPortID)) {
                // TODO: throw exception of proper type.
                throw new Exception("Connection to this port already exists.");
            }
        }

        // make a in port of the same length as the target out port.
        makeInPort(toID, from.getOutPort(fromID).length);
        makeConnection(new Connection(from, fromID, toID));
    }

    /**
     * Obtain inputs from outputs of other Modules.
     * 
     * Usually called by scheduler.
     */
    public void input(double time) {
        for (Connection c : connections) {
            updateInPort(c);
        }
        
        assert lastInputTime <= time;
        lastInputTime = time;
    }

    protected void updateInPort(Connection c) {
        short[] o = c.fromModule.getOutPort(c.fromPortID);
        o = o.clone(); // copy
        inPorts.put(c.toPortID, o);
    }

    /**
     * Expose results of the computation to outPorts.
     * 
     * Usually called by scheduler.
     */
    public void output(double time) {
        // swap double buffers
        HashMap<String, short[]> tmp;
        tmp = outPorts;
        outPorts = results;
        results = tmp;
        
        assert lastOutputTime <= time;
        lastOutputTime = time;
    }
}
