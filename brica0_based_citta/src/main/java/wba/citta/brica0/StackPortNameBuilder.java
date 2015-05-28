package wba.citta.brica0;

public class StackPortNameBuilder {
    final String perNodeStackPortName = "goalStack(%d)";
    final String perNodeStackPushAvailPortName = "doPush(%d)";
    final String perNodeStackTopPortName = "stackTop(%d)";
    final String perNodeStackRemoveAllOpPortName = "doRemoveAll(%d)";
    final String perNodeStackRemoveOpPortName = "doRemove(%d)";
    final String perNodeStackTopDesignationStatePortName = "designated(%d)";

    public String getPerNodeStackPortNameFor(int nodeIndex) {
        return String.format(perNodeStackPortName, nodeIndex);
    }

    public String getPerNodeStackPushAvailPortNameFor(int nodeIndex) {
        return String.format(perNodeStackPushAvailPortName, nodeIndex);
    }
    public String getPerNodeStackTopPortNameFor(int nodeIndex) {
        return String.format(perNodeStackTopPortName, nodeIndex);
    }
    public String getPerNodeStackRemoveAllOpPortNameFor(int nodeIndex) {
        return String.format(perNodeStackRemoveAllOpPortName, nodeIndex);
    }
    public String getPerNodeStackRemoveOpPortNameFor(int nodeIndex) {
        return String.format(perNodeStackRemoveOpPortName, nodeIndex);
    }
    public String getPerNodeStackTopDesignationStatePortNameFor(int nodeIndex) {
        return String.format(perNodeStackTopDesignationStatePortName, nodeIndex);
    }

    
}
