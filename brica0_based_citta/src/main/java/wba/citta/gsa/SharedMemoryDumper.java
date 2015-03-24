package wba.citta.gsa;

import java.io.PrintWriter;
import java.util.List;

public class SharedMemoryDumper {
    private final PrintWriter out;

    public SharedMemoryDumper(PrintWriter out) {
    this.out = out;
    }

    /**
     * Stateの状態を出力します。<BR>
     * 出力形式<BR>
     * [shared stack]<BR>
     *  state<BR>
     *   index:0 val:22<BR>
     *   index:1 val:15<BR>
     *   index:2 val:0<BR>
     *   index:3 val:0<BR>
     *   index:4 val:1<BR>
     */
    public void printState(final ISharedMemory sharedMemory) {
        out.println("");
        out.println(" state");
        final List<Integer> state = sharedMemory.getLatch().getState();
        for(int i = 0, s = sharedMemory.getSize(); i < s; i++) {
            out.println("  index:" + i + " val:" + state.get(i));
        }
    }

    /**
     * Goalの状態を出力します。<BR>
     * 出力形式<BR>
     * [shared stack]<BR>
     *  goal<BR>
     *   index:0 | 26:200 | 18:100 |<BR>
     *   index:1 | 1:200 | 10:100 |<BR>
     *   index:2 | 6:100 |<BR>
     *   index:3 | 1:1 | 2:101 |<BR>
     *   index:4 | 1:101 | 1:101 |<BR>
     */
    public void printGoalStack(final ISharedMemory sharedMemory) {
        out.println("");
        out.println("[shared stack]");

        out.println(" goal");
        final IGoalStack goalStack = sharedMemory.getGoalStack();
        for(int i = 0, s = sharedMemory.getSize(); i < s; i++) {
            final List<IGoalStack.GoalStackElement> goalStackForNode = goalStack.getGoalStackForNode(i);
            final int size = goalStackForNode.size();
            StringBuffer sb = new StringBuffer();
            sb.append("  index:" + i + " | ");
            for(int m = 0; m < size; m++) {
                IGoalStack.GoalStackElement elm = (IGoalStack.GoalStackElement)goalStackForNode.get(m);
                if(elm != null) {
                    sb.append(elm.toString() + " | " );
                }else {
                    sb.append(" no val | ");
                }
            }
            out.println(sb.toString());
        }
    }
}