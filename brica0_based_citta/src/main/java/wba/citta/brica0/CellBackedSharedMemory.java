package wba.citta.brica0;

import java.util.ArrayList;
import java.util.List;

import brica0.CognitiveArchitecture;
import wba.citta.gsa.GSAAgentEventSource;
import wba.citta.gsa.Goal;
import wba.citta.gsa.IGoalStack;
import wba.citta.gsa.ILatch;
import wba.citta.gsa.IListenableSharedMemory;
import wba.citta.gsa.SharedMemoryEvent;
import wba.citta.gsa.SharedMemoryEventListener;
import wba.citta.gsa.State;
import wba.citta.util.EventPublisherSupport;

public class CellBackedSharedMemory implements IListenableSharedMemory {
    GSAAgentEventSource gsa;
    final String stackTopPortName = "value";
    List<String> agentIdList;
    
    final IGoalStack goalStackView = new IGoalStack() {

        @Override
        public List<GoalStackElement> getGoalStackForNode(int i) {
            List<GoalStackElement> retval = new ArrayList<GoalStackElement>();
            final PerNodeStackArray a = perNodeStackArrayCluster.get(i);
            for (PerNodeStackArray.Element e: a.elements) {
                int numericAgentId = 0;
                try {
                    numericAgentId = Integer.valueOf(agentIdList.get(e.agentIndex));
                } catch (NumberFormatException ex) {}
                retval.add(new GoalStackElement(numericAgentId, (int)e.value[0]));
            }
            return retval;
        }

        @Override
        public GoalStackElement getGoalForNode(int index) {
            final PerNodeStackArray a = perNodeStackArrayCluster.get(index);
            if (a.elements.size() == 0)
                return null;
            PerNodeStackArray.Element e = a.elements.getLast();
            return new GoalStackElement(Integer.valueOf(agentIdList.get(e.agentIndex)), (int)e.value[0]);
        }

        @Override
        public void pushGoal(int agid, boolean[] useNode, State state) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeGoal(int agid, boolean[] useNode) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeAllGoal() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Goal getGoalValueArray() {
            return null;
        }

        @Override
        public State getCurrentGoal(int agid, boolean[] useNode) {
            return null;
        }        
    };

    final ILatch latchView = new ILatch() {
        @Override
        public Goal getState() {
            // TODO Auto-generated method stub
            return stateLatch.getState();
        }

        @Override
        public State getState(boolean[] useNode) {
            assert useNode.length == size;
            int c = 0;
            for (int i = 0; i < size; i++) {
                if (useNode[i])
                    c++;
            }
            State retval = new State(c);
            final Goal v = stateLatch.getState();
            for (int i = 0, j = 0; i < size; i++) {
                if (useNode[i]) {
                    retval.set(j, v.get(i));
                    j++;
                }
            }
            return retval;
        }

        @Override
        public void setState(Goal state) {
            throw new UnsupportedOperationException();
        }
    };

    int size;
    EventPublisherSupport<SharedMemoryEvent, SharedMemoryEventListener> changeListeners = new EventPublisherSupport<SharedMemoryEvent, SharedMemoryEventListener>(SharedMemoryEvent.class, SharedMemoryEventListener.class);
    List<PerNodeStackArray> perNodeStackArrayCluster;
    GSARunner stateLatch;
    private AgentPortNameBuilder nb;

    @Override
    public IGoalStack getGoalStack() {
        return goalStackView;
    }

    @Override
    public ILatch getLatch() {
        return latchView;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void addChangeListener(SharedMemoryEventListener listener) {
        changeListeners.addEventListener(listener);
    }

    @Override
    public void removeChangeListener(SharedMemoryEventListener listener) {
        changeListeners.removeEventListener(listener);
    }

    void fireSharedMemoryChanged() {
        changeListeners.fire("sharedMemoryChanged", new SharedMemoryEvent(this));
    }

    private static final String ID_FORMAT = "PerNodeStackArray[%d]";

    public void bindAgentModule(String agentId, BasicBricaPerspective m) {
        // bind NodeStackArrays
        {
            String valuePortName = nb.getValuePortNameFor(agentId);
            String removeOpAvailPortName = nb.getRemoveOpAvailPortNameFor(agentId);
            String removeAllOpPortName = nb.getRemoveAllOpPortNameFor(agentId);
            String pushOpAvailPortName = nb.getPushOpAvailPortNameFor(agentId);
            String stackTopDesignationPortName = nb.getStackTopDesignationPortFor(agentId);
            for (int i = 0; i < size; i++) {
                final PerNodeStackArray a = perNodeStackArrayCluster.get(i);
                a.connect(
                    m, m.perNodeStackPorts.get(i),
                    valuePortName
                );
                m.connect(
                    a, stackTopPortName,
                    m.perNodeStackPorts.get(i)
                );
                a.connect(
                    m, m.perNodeStackPushAvailPorts.get(i),
                    pushOpAvailPortName
                );
                a.connect(
                    m, m.perNodeStackRemoveAllOpPorts.get(i),
                    removeAllOpPortName
                );
                a.connect(
                    m, m.perNodeStackRemoveOpPorts.get(i),
                    removeOpAvailPortName
                );
                m.connect(
                    a, stackTopDesignationPortName,
                    m.perNodeStackTopDesignationStatePorts.get(i)
                );
            }          
        }
        if (m instanceof GSARunner) {
            stateLatch = (GSARunner)m;
        } else if (m instanceof BricaPerspective) {
            m.connect(stateLatch, stateLatch.currentStatePort, ((BricaPerspective)m).latchPort);
        }
    }
    
    public CellBackedSharedMemory(CognitiveArchitecture ca, AgentPortNameBuilder nb, int size, List<String> agentIds) {
        this.size = size;
        this.agentIdList = new ArrayList<String>(agentIds);
        final List<PerNodeStackArray> perNodeStackArrayCluster = new ArrayList<PerNodeStackArray>();
        final List<String> valuePorts = new ArrayList<String>();
        final List<String> removeOpAvailPorts = new ArrayList<String>();
        final List<String> removeAllOpPorts = new ArrayList<String>();
        final List<String> pushOpAvailPorts = new ArrayList<String>();
        final List<String> stackTopDesignationPorts = new ArrayList<String>();
        this.nb = nb;
        for (String agentId: agentIds) {
            valuePorts.add(nb.getValuePortNameFor(agentId));
            removeOpAvailPorts.add(nb.getRemoveOpAvailPortNameFor(agentId));
            removeAllOpPorts.add(nb.getRemoveAllOpPortNameFor(agentId));
            pushOpAvailPorts.add(nb.getPushOpAvailPortNameFor(agentId));
            stackTopDesignationPorts.add(nb.getStackTopDesignationPortFor(agentId));
        }
        for (int i = 0; i < size; i++) {
            PerNodeStackArray m = new PerNodeStackArray(
                this,
                stackTopPortName,
                valuePorts,
                removeOpAvailPorts,
                removeAllOpPorts,
                pushOpAvailPorts,
                stackTopDesignationPorts
            );
            ca.addModule(String.format(ID_FORMAT, i), m);
            perNodeStackArrayCluster.add(m);
        }
        this.perNodeStackArrayCluster = perNodeStackArrayCluster;
    }

    public void bind(GSAAgentEventSource gsa) {
        this.gsa = gsa;
    }

    public void fireAll() {
        for (PerNodeStackArray m: perNodeStackArrayCluster) {
            m.input(0.0);
            m.fire();
            m.output(0.0);
        }
    }
}
