package wba.citta.brica0;

public class AgentPortNameBuilder {
    final String valuePortName = "value[%s]";
    final String stackTopPortName = "stackTop[%s]";
    final String removeOpAvailPortName = "doRemove[%s]";
    final String removeAllOpPortName = "doRemoveAll[%s]";
    final String pushOpAvailPortName = "doPush[%s]";
    final String stackTopDesignationPortName = "designated[%s]";
    final String stateLatchInPortName = "state[%s]";
    final String stateLatchAvailPortName = "stateAvail[%s]";

    public String getValuePortNameFor(String agentId) {
        return String.format(valuePortName, agentId);
    }

    public String getStackTopPortNameFor(String agentId) {
        return String.format(stackTopPortName, agentId);
    }

    public String getRemoveOpAvailPortNameFor(String agentId) {
        return String.format(removeOpAvailPortName, agentId);
    }

    public String getRemoveAllOpPortNameFor(String agentId) {
        return String.format(removeAllOpPortName, agentId);
    }

    public String getPushOpAvailPortNameFor(String agentId) {
        return String.format(pushOpAvailPortName, agentId);
    }

    public String getStackTopDesignationPortFor(String agentId) {
        return String.format(stackTopDesignationPortName, agentId);
    }

    public String getStateLatchInPortNameFor(String agentId) {
        return String.format(stateLatchInPortName, agentId);
    }

    public String getStateLatchAvailPortName(String agentId) {
        return String.format(stateLatchAvailPortName, agentId);
    }
    public AgentPortNameBuilder() {}
}
