/**
 * FailAgentTree.java
 * スタックのゴールの状態、解決できないゴール、ゴールを解決できないエージェント
 * についてツリー構造で管理するクラス
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.08
 */
package wba.citta.gsa;

import java.util.*;
import wba.citta.util.EventPublisherSupport;

/**
 * スタックのゴールの状態、失敗ゴール、サブゴール未出力エージェント
 * についてツリー構造で管理するクラス。<BR>
 * ツリー上の現在の位置をカレントとして処理を行います。<BR>
 * このツリーで扱うノードとして、FailAgentTreeElementを使用します。<BR>
 */
public class FailAgentTree {
    private FailAgentTreeElement currentElement = null;
    private FailAgentTreeElement rootElement = null;

    EventPublisherSupport<FailAgentTreeEvent, FailAgentTreeEventListener> changeEventListeners = new EventPublisherSupport<FailAgentTreeEvent, FailAgentTreeEventListener>(FailAgentTreeEvent.class, FailAgentTreeEventListener.class);
    
    /**
     * コンストラクタ
     * @param boolean isShowViewer ツリーの状態をグラフィック表示するか
     */
    public FailAgentTree() {
        rootElement = new FailAgentTreeElement(null, -1, null, Agent.Status.AGR_SUCCESS);
        currentElement = rootElement;
    }


    //////////////////////////////////////////////////////////////////////
    // public

    /**
     * カレントノードに子ノードを追加します。<BR>
     * 追加ノードにはエージェントIDとゴールを設定します。agrには0を設定します。
     * <BR>
     * カレントの位置は、追加した新たなゴールに移動します。<BR>
     * @param int agid エージェントのID
     * @param Vector goal ゴール
     */
    public void addTreeNode(int agid, List<Integer> goal) {
        FailAgentTreeElement newFailAgentTreeElement
                = new FailAgentTreeElement(currentElement, agid, goal, Agent.Status.AGR_SUCCESS);
        currentElement.addNext(newFailAgentTreeElement);
        currentElement = newFailAgentTreeElement;
        changeEventListeners.fire("treeChanged", new FailAgentTreeEvent(this));
    }

    /**
     * カレントノードに子ノードを追加します。<BR>
     * 追加するノードにはエージェントIDとagrを設定します。ゴールはnullになります     * 。<BR>
     * カレントの位置は移動しません。<BR>
     * @param int agid エージェントのID
     * @param int agr 実行処理の結果を表すID
     */
    public void addTreeNode(int agid, Agent.Status agr) {
        /* ルートには失敗エージェントを追加しない */
        if(currentElement != rootElement) {
            if (getChildAgr(agid) == Agent.Status.NONE) {
                FailAgentTreeElement newFailAgentTreeElement
                       = new FailAgentTreeElement(currentElement, agid, null, agr);
                currentElement.addNext(newFailAgentTreeElement);
                changeEventListeners.fire("treeChanged", new FailAgentTreeEvent(this));
            }
        }
    }

    /**
     * ツリーのカレントノードを削除します。<BR>
     * カレントは親ノードに移動します。<BR>
     */
    public void removeCurrent() {
        FailAgentTreeElement parent = currentElement.parentElement;
        parent.removeNext(currentElement);
        currentElement = parent;
        changeEventListeners.fire("treeChanged", new FailAgentTreeEvent(this));
    }

    /**
     * ツリーのカレントノードを親へ移動します。<BR>
     */
    public void moveParent() {
        currentElement = currentElement.parentElement;
        changeEventListeners.fire("treeChanged", new FailAgentTreeEvent(this));
    }

    /**
     * カレントノードの子ノードから、引数で指定されたエージェントIDをもつ
     * ノードを探し、そのagrを取得します。<BR>
     * 引数で指定されたエージェントIDをもつノードがなければ-1を返します。<BR>
     * @param int agid エージェントのID
     * @return int 実行処理結果を表すID
     */
    public Agent.Status getChildAgr(int agid) {
        if(currentElement != null) {
            return currentElement.getChildAgr(agid);
        }      
        return Agent.Status.NONE;
    }

    /**
     * ツリーのノードを全て削除します。
     */
    public void clear() {
        currentElement = rootElement;
        currentElement.removeNextAll();
        changeEventListeners.fire("treeChanged", new FailAgentTreeEvent(this));
    }


    /**
     * ツリー上にあるノードに、引数で指定されたエージェントIDとゴールをもつ
     * ノードがあるかどうか判定します。<BR>
     * ノードがあればtrue、なければfalseを返します。<BR>
     * @param int agid エージェントID
     * @param Vector goal ゴール
     * @return boolean true：ノードがある false：ノードがない
     */
    public boolean containsGoal(int agid, List<Integer> goal) {
        boolean b = childContainsGoal(rootElement, agid, goal);
        return b;
    }

    public FailAgentTreeElement getCurrentElement() {
        return currentElement;
    }

    public FailAgentTreeElement getRootElement() {
        return rootElement;
    }
    
    public void addFailAgentTreeEventListener(FailAgentTreeEventListener listener) {
        changeEventListeners.addEventListener(listener);
    }

    public void removeFailAgentTreeEventListener(FailAgentTreeEventListener listener) {
        changeEventListeners.removeEventListener(listener);
    }

    //////////////////////////////////////////////////////////////////////
    // private 

    /**
     * 第一引数で指定されたノードの子に第二引数・第三引数のエージェントID、
     * ゴールをもつノードがあればtrueを返す。<BR>
     * 再帰的な呼び出しを行い、深い子ノードまでチェックする。<BR>
     * @param FailAgentTreeElement Element 
     * @param int agid 
     * @param Vector goal
     */
    private boolean childContainsGoal(FailAgentTreeElement element, int agid, List<Integer> goal) {
        // 現在の処理方法では、処理に時間がかかる。
        // このクラスで、Agentの配列への参照をもち、各ノードのエージェントIDに対応する
        // エージェントのみ処理を行なうようにすることで対応可能だが、Agentの配列への
        // 参照を持たせることはあまり行ないたくない。
        ListIterator<FailAgentTreeElement> li = element.next.listIterator();
        while(li.hasNext()) {
            FailAgentTreeElement nextElement = (FailAgentTreeElement)li.next();
            if( (nextElement.goal != null) && (nextElement.agentId==agid) &&
                    (nextElement.goal.equals(goal)) ){
                return true;
            }
            boolean b = childContainsGoal(nextElement, agid, goal);
            if(b == true) {
                return true;
            }
        }
        return false;
    }
}

