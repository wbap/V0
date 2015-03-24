package wba.citta.gsa;

import java.util.Random;

public class RandomExecutionStrategy implements AgentExecutionStrategy {
    static class ContextImpl implements Context {
        IGSAIteration gsaIteration;

        /* エージェントの選択状況を示すbooleanの配列 */
        Random random;

        ContextImpl(IGSAIteration gsaIteration, Random random) {
            this.gsaIteration = gsaIteration;
            this.random = random;
        }
    
        /**
         * 実行処理を行なうエージェントをランダムに取得します。
         */
        @Override
        public IGSAAgent nextAgent() {
            final int unusedAgentCount = gsaIteration.getUnusedAgents().size();
            assert unusedAgentCount > 0;
            return gsaIteration.getUnusedAgents().get(random.nextInt(unusedAgentCount));
        }
    }

    public Context createContext(IGSAIteration gsaIteration) {
        return new ContextImpl(gsaIteration, new Random());
    }
}
