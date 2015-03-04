package wba.citta.gsa;

import java.util.List;

public class SerialExecutionStrategy implements AgentExecutionStrategy {
    static class ContextImpl implements Context {
        final GSAIteration gsaIteration;
        int sequenceSelectIndex;
    
        ContextImpl(GSAIteration gsaIteration) {
            this.gsaIteration = gsaIteration;
            this.sequenceSelectIndex = 0;
        }

        /**
         * 実行処理を行なうエージェントを順番に取得します。
         * agents(エージェントの配列)に設定された順に取得。
         * @param Agent エージェント
         */
        public Agent nextAgent() {
            final List<Agent> agents = gsaIteration.getGSA().getAgents();
            assert sequenceSelectIndex < agents.size();
            final Agent agent = agents.get(sequenceSelectIndex);
            if (++sequenceSelectIndex >= agents.size())
                sequenceSelectIndex = 0;
            return agent;
        }
    }
    
    public Context createContext(GSAIteration gsaIteration) {
        return new ContextImpl(gsaIteration);
    }
}
