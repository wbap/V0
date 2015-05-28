package wba.citta.gsa;

import java.util.List;

public class SerialExecutionStrategy implements AgentExecutionStrategy {
    static class ContextImpl implements Context {
        final IGSAIteration gsaIteration;
        int sequenceSelectIndex;
    
        ContextImpl(IGSAIteration gsaIteration) {
            this.gsaIteration = gsaIteration;
            this.sequenceSelectIndex = 0;
        }

        /**
         * 実行処理を行なうエージェントを順番に取得します。
         * agents(エージェントの配列)に設定された順に取得。
         * @param AbstractGSAAgent エージェント
         */
        public IGSAAgent nextAgent() {
            final List<IGSAAgent> agents = gsaIteration.getAgents();
            assert sequenceSelectIndex < agents.size();
            final IGSAAgent agent = agents.get(sequenceSelectIndex);
            if (++sequenceSelectIndex >= agents.size())
                sequenceSelectIndex = 0;
            return agent;
        }
    }
    
    public Context createContext(IGSAIteration gsaIteration) {
        return new ContextImpl(gsaIteration);
    }
}
