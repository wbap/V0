package wba.citta.gsa;
import java.io.PrintWriter;
import java.util.ListIterator;


public class FailAgentTreeDumper {
    final PrintWriter out;

    public FailAgentTreeDumper(PrintWriter out) {
        this.out = out;
    }

    /**
     * 引数で指定されたノードの子に関しての情報を文字列で取得します。
     */
    private String getTree(FailAgentTreeElement currentElement, int depth, FailAgentTreeElement goal) {
        StringBuffer stringBuffer = new StringBuffer();
        ListIterator<FailAgentTreeElement> li = goal.next.listIterator();
        while(li.hasNext()) {
            FailAgentTreeElement nextGoal = (FailAgentTreeElement)li.next();
            stringBuffer.append(getString(depth, nextGoal));
            if(nextGoal == currentElement) {
                stringBuffer.append("<--- *");
            }
            stringBuffer.append("\n");
            stringBuffer.append(getTree(currentElement, depth + 1, nextGoal));
        }
        return stringBuffer.toString();
    }

    private String getString(int depth, FailAgentTreeElement goal) {
        StringBuffer stringBuffer = new StringBuffer();
        for(int i = 0; i < depth; i++) {
            stringBuffer.append("  ");
        }
        stringBuffer.append(goal.toString());
        return stringBuffer.toString();
    }

    /**
     * ツリーの状態を表示します。
     */
    public void printTree(FailAgentTree tree) {
        out.println("");
        out.println("[ goal tree ]");
        out.println(getTree(tree.getCurrentElement(), 0, tree.getRootElement()));
    }
}
