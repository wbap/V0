package wba.citta.gsa;

import java.io.PrintWriter;

public class GSADumper {
    final PrintWriter out;
    final SharedMemoryDumper sharedMemoryDumper;
    final FailAgentTreeDumper failAgentTreeDumper;

    public GSADumper(PrintWriter out) {
        this.out = out;
        this.sharedMemoryDumper = new SharedMemoryDumper(out);
        this.failAgentTreeDumper = new FailAgentTreeDumper(out);
    }

    /**
     * 共有メモリの状態を表示します。
     */
    public void printSharedMemory(final GSA gsa) {
        sharedMemoryDumper.printState(gsa.getSharedMemory());
        sharedMemoryDumper.printGoalStack(gsa.getSharedMemory());
    }

    /**
     * ツリーの状態を表示します。
     */
    public void printFailAgentTree(final GSA gsa) {
        failAgentTreeDumper.printTree(gsa.getFailAgentTree());
    }


}
