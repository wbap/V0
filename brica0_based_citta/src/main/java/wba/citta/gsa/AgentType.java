package wba.citta.gsa;

public enum AgentType {
    /**
     * CognitiveDistanceのエージェントを表すID
     */
    CD(0),
    /**
     * Associateエージェントを表すID
     */
    ASSOCIATE(1),
    /**
     * Logエージェントを表すID
     */
    LOG(2),
    /**
     * Manualエージェント
     */
    MANUAL(3);

    int value;

    private AgentType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
