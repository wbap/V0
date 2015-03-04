package wba.citta.gsa;

public class AgentInfo {
    int id;
    AgentType type;
    boolean[] useNode;
    String eventFileName;

    public AgentInfo() {}

    public AgentInfo(int id, AgentType type, boolean[] useNode, String eventFileName) {
        this.id = id;
        this.type = type;
        this.useNode = useNode;
        this.eventFileName = eventFileName;
    }

    public void setId(int value) {
        this.id = value;
    }

    public int getId() {
        return id;
    }

    public void setType(AgentType value) {
        this.type = value;
    }

    public AgentType getType() {
        return type;
    }

    public void setUseNode(boolean[] value) {
        useNode = value;
    }
    
    public boolean[] getUseNode() {
        return useNode;
    }

    public void setEventFileName(String value) {
        eventFileName = value;
    }

    public String getEventFileName() {
        return eventFileName;
    }
}
