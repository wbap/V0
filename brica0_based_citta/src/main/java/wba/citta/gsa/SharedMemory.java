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
public class SharedMemory {   
    public class GoalStack {
        public LinkedList<GoalStackElement>[] goalStackArray;

        @SuppressWarnings("unchecked")
        public GoalStack(int size) {
            goalStackArray = new LinkedList[size];
            for(int i = 0; i < size; i++) {
                goalStackArray[i] = new LinkedList<GoalStackElement>();
            }
        }

        /**
         * 現在のゴールスタックを取得します
         */
        public List<GoalStackElement> getGoalStackForNode(int i) {
            return Collections.unmodifiableList(goalStackArray[i]);
        }

        /**
         * Goalの指定された位置(ノード)の値をスタックからGETで取得します。
         * @param int index
         * @return GoalStackElement  ゴールの要素
         */
        public GoalStackElement getGoalForNode(int index) {
            GoalStackElement elm = null;
            if (goalStackArray[index].size() > 0) {
                elm = (GoalStackElement)goalStackArray[index].getLast();
            }
            return elm;
        }

        /**
         * 指定されたゴールの要素をGoalの指定された位置(ノード)のスタックにPUSHで
         * 設定します。       
         * @param int index
         * @param GoalStackElement elm ゴールの要素
         */
        public void pushGoal(int index, GoalStackElement elm) {
            goalStackArray[index].add(elm);
            fireSharedMemoryChanged();
        }

        /**
         * Goalの指定された位置(ノード)の値をスタックから削除します。
         * @param index 
         */
        public void removeGoal(int index, int agid) {
            if (goalStack.goalStackArray[index].getLast().agid != agid)
                throw new GSAException("WTF?");
            goalStackArray[index].removeLast();
            fireSharedMemoryChanged();
        }

        /**
         * Goalの要素を全てクリアします。
         */
        public void removeAllGoal() {
            for(int i = 0; i < size; i++) {
                goalStack.goalStackArray[i].clear();
            }
            fireSharedMemoryChanged();
        }

        /**
         * 全ノードのゴールをVectorで取得します。
         * @param Vector GoalValueのVector
         */
        public List<Integer> getGoalValueArray() {
            List<Integer> goal = new ArrayList<Integer>();
            for(int i = 0; i < size; i++) {
                GoalStackElement goalElement = goalStack.getGoalForNode(i);
                if(goalElement != null) {
                    goal.add(goalElement.value);
                }else {
                    goal.add(null);
                }
            }
            return goal;
        }
    }

    public class Latch {
        public Integer[] stateArray;

        public Latch(int size) {
            this.stateArray = new Integer[size];
        }

        /**
         * Stateの指定された位置(ノード)の値を取得します。
         * @param int index 
         * @return Integer  
         */
        public List<Integer> getState() {
            return Collections.unmodifiableList(Arrays.asList(stateArray));
        }

        /**
         * 現在の状態をVectorで設定します。
         * @param Vector state 現在の状態
         */
        public void setState(List<Integer> state) {
            assert state.size() == stateArray.length;
            for(int i = 0; i < size; i++) {
                latch.stateArray[i] = state.get(i);
            }
            fireSharedMemoryChanged();
        }
    }

    /**
     * 共有メモリで扱うゴールの情報の単位
     */
    public static class GoalStackElement {
        /**
         * ゴールの値
         */
        public final int value;
    
        /**
         * ゴールを設定したエージェントID
         */
        public final int agid;
    
        /**
         * コンストラクタ
         * @param int value ゴールの値
         * @param int agid  設定したエージェントのID
         */
        public GoalStackElement(int value, int agid) {
            this.value = value;
            this.agid = agid;
        }
    
        /**
         * ゴールの情報を表示します。
         * @return String ゴールの情報<BR>
         * 表示形式  val:21 id:701
         */
        public String toString() {
            String str = "val:" + value + " id:" + agid;
                return str;
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

    public GoalStack getGoalStack() {
        return goalStack;
    }

    public Latch getLatch() {
        return latch;
    }

    /**
     * ゴールスタックの数を返します
     */
    public int getSize() {
        return size;
    }

    public void addChangeListener(SharedMemoryEventListener listener) {
        changeListeners.addEventListener(listener);
    }

    public void removeChangeListener(SharedMemoryEventListener listener) {
        changeListeners.removeEventListener(listener);
    }
    
    protected void fireSharedMemoryChanged() {
        changeListeners.fire("sharedMemoryChanged", new SharedMemoryEvent(this));
    }
}
