/**
 * SharedMemory.java
 * State・Goalを管理する共有メモリ
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.07
 */
package wba.citta.gsa;

import java.util.*;

import wba.citta.util.EventPublisherSupport;

/**
 * State・Goalを管理する共有メモリです。<BR>
 * Goalについてはスタックで管理します。<BR>
 * 共有メモリへの操作は、基本的にメソッドの引数で指定されたindexの要素ごとに
 * 行ないます。
 */
public class SharedMemory implements IListenableSharedMemory {   
    private class GoalStack implements IGoalStack {
        public LinkedList<IGoalStack.GoalStackElement>[] goalStackArray;

        @SuppressWarnings("unchecked")
        public GoalStack(int size) {
            goalStackArray = new LinkedList[size];
            for(int i = 0; i < size; i++) {
                goalStackArray[i] = new LinkedList<IGoalStack.GoalStackElement>();
            }
        }

        /* (non-Javadoc)
         * @see wba.citta.gsa.IGoalStack#getGoalStackForNode(int)
         */
        @Override
        public List<IGoalStack.GoalStackElement> getGoalStackForNode(int i) {
            return Collections.unmodifiableList(goalStackArray[i]);
        }

        /* (non-Javadoc)
         * @see wba.citta.gsa.IGoalStack#getGoalForNode(int)
         */
        @Override
        public IGoalStack.GoalStackElement getGoalForNode(int index) {
            IGoalStack.GoalStackElement elm = null;
            if (goalStackArray[index].size() > 0) {
                elm = (IGoalStack.GoalStackElement)goalStackArray[index].getLast();
            }
            return elm;
        }

        /* (non-Javadoc)
         * @see wba.citta.gsa.IGoalStack#pushGoal(int, wba.citta.gsa.SharedMemory.GoalStackElement)
         */
        @Override
        public void pushGoal(int agid, boolean[] useNode, State state) {
            if (useNode.length != goalStackArray.length)
                throw new IllegalArgumentException();
            for(int i = 0, j = 0; i < goalStackArray.length; i++) {
                if (useNode[i]) {
                    final Integer value = state.get(j);
                    goalStackArray[i].add(new IGoalStack.GoalStackElement(agid, value));
                    j++;
                }
            }
            fireSharedMemoryChanged();
        }

        /* (non-Javadoc)
         * @see wba.citta.gsa.IGoalStack#removeGoal(int, int)
         */
        @Override
        public boolean removeGoal(int agid, boolean[] useNode) {
            if (useNode.length != goalStackArray.length)
                throw new IllegalArgumentException();
            for (int i = 0; i < goalStackArray.length; i++) {
                if (useNode[i]) {
                    if (goalStackArray[i].size() == 0)
                        return false;
                    final IGoalStack.GoalStackElement last = goalStackArray[i].getLast();
                    if (last == null || last.agid != agid)
                        return false;
                }
            }
            for (int i = 0; i < goalStackArray.length; i++) {
                if (useNode[i]) {
                    goalStackArray[i].removeLast();
                }
            }
            fireSharedMemoryChanged();
            return true;
        }

        /* (non-Javadoc)
         * @see wba.citta.gsa.IGoalStack#removeAllGoal()
         */
        @Override
        public void removeAllGoal() {
            for(int i = 0; i < size; i++) {
                goalStack.goalStackArray[i].clear();
            }
            fireSharedMemoryChanged();
        }

        /* (non-Javadoc)
         * @see wba.citta.gsa.IGoalStack#getGoalValueArray()
         */
        @Override
        public Goal getGoalValueArray() {
            Goal goal = new Goal();
            for(int i = 0; i < size; i++) {
                IGoalStack.GoalStackElement goalElement = goalStack.getGoalForNode(i);
                if(goalElement != null) {
                    goal.add(goalElement.value);
                }else {
                    goal.add(null);
                }
            }
            return goal;
        }

        @Override
        public State getCurrentGoal(int agid, boolean[] useNode) {
            if (goalStackArray.length != useNode.length)
                throw new IllegalArgumentException();
            int useNodeCount = 0;
            for (int i = 0; i < useNode.length; i++) {
                if (useNode[i])
                    useNodeCount++;
            }
            State retval = new State(useNodeCount);
            for (int i = 0, j = 0; i < goalStackArray.length; i++) {
                if (useNode[i]) {
                    IGoalStack.GoalStackElement e = getGoalForNode(i);
                    retval.set(j, e != null ? e.value: null);
                    j++;
                }
            }
            return retval;
        }
    }

    private class Latch implements ILatch {
        public Goal stateArray;

        public Latch(int size) {
            this.stateArray = new Goal(size);
        }

        /* (non-Javadoc)
         * @see wba.citta.gsa.ILatch#getState()
         */
        @Override
        public Goal getState() {
            return (Goal)stateArray.clone();
        }

        @Override
        public State getState(boolean[] useNode) {
            return new State(stateArray, useNode); 
        }

        /* (non-Javadoc)
         * @see wba.citta.gsa.ILatch#setState(java.util.List)
         */
        @Override
        public void setState(Goal state) {
            assert state.size() == stateArray.size();
            for(int i = 0; i < size; i++) {
                stateArray.set(i, state.get(i));
            }
            fireSharedMemoryChanged();
        }
    }

    private Latch latch;

    private GoalStack goalStack;

    /**
     * 共有メモリのノード数
     */
    private final int size;
    

    EventPublisherSupport<SharedMemoryEvent, SharedMemoryEventListener> changeListeners = new EventPublisherSupport<SharedMemoryEvent, SharedMemoryEventListener>(SharedMemoryEvent.class, SharedMemoryEventListener.class);

    /**
     * コンストラクタ
     * @param int size ノード数（配列のサイズ）
     */
    public SharedMemory(int size) {
        this.size = size;
        this.latch = new Latch(size);
        this.goalStack = new GoalStack(size);
    }

    /* (non-Javadoc)
     * @see wba.citta.gsa.ISharedMemory#getGoalStack()
     */
    @Override
    public IGoalStack getGoalStack() {
        return goalStack;
    }

    /* (non-Javadoc)
     * @see wba.citta.gsa.ISharedMemory#getLatch()
     */
    @Override
    public ILatch getLatch() {
        return latch;
    }

    /* (non-Javadoc)
     * @see wba.citta.gsa.ISharedMemory#getSize()
     */
    @Override
    public int getSize() {
        return size;
    }

    /* (non-Javadoc)
     * @see wba.citta.gsa.ISharedMemory#addChangeListener(wba.citta.gsa.SharedMemoryEventListener)
     */
    @Override
    public void addChangeListener(SharedMemoryEventListener listener) {
        changeListeners.addEventListener(listener);
    }

    /* (non-Javadoc)
     * @see wba.citta.gsa.ISharedMemory#removeChangeListener(wba.citta.gsa.SharedMemoryEventListener)
     */
    @Override
    public void removeChangeListener(SharedMemoryEventListener listener) {
        changeListeners.removeEventListener(listener);
    }

    public void bind(GSAAgentEventSource gsa) {
        // do nothing
    }

    protected void fireSharedMemoryChanged() {
        changeListeners.fire("sharedMemoryChanged", new SharedMemoryEvent(this));
    }
}
