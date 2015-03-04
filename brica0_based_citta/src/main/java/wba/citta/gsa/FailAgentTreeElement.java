/**
 * FailAgentTreeElement.java
 * FailAgentTreeで扱う情報の単位
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.08
 */
package wba.citta.gsa;

import java.util.*;

/**
 * FailAgentTreeで扱う情報の単位<BR>
 * ツリーのノード<BR>
 */
public class FailAgentTreeElement {

    /**
     * 親のノード(FailAgentTreeElement)
     */
    public final FailAgentTreeElement parentElement;

    /**
     * エージェントID
     */
    public final int agentId;

    /**
     * ゴールの値
     */
    public final List<Integer> goal;

    /**
     * 実行処理の結果
     */
    public final Agent.Status agr;

    /**
     * 子のノード(FailAgentTreeElement)のリスト
     */
    public final List<FailAgentTreeElement> next;

    /**
     * コンストラクタ
     * @param FailAgentTreeElement parentElement 親ノードへの参照
     * @param int agid エージェントID
     * @param Vector goal ゴール
     * @param int agr 実行処理の結果を表すID
     */
    public FailAgentTreeElement(FailAgentTreeElement parentElement, int agid,
            List<Integer> goal, Agent.Status agr) {
        this.parentElement = parentElement;
        this.agentId = agid;
        this.goal = goal;
        this.agr = agr;
        next = new LinkedList<FailAgentTreeElement>();
    }

    /**
     * 引数のFailAgentTreeElementを子ノードのリストに追加します。
     * @param FailAgentTreeElement nextElement 子となるノード
     */
    public void addNext(FailAgentTreeElement nextElement) {
        next.add(nextElement);
    }

    /**
     * 子ノードのリストから引数のFailAgentTreeElementを削除します。
     * @param FailAgentTreeElement nextElement 削除するノード
     */
    public void removeNext(FailAgentTreeElement nextElement) {
        next.remove(nextElement);
    }

    /**
     * 子を全て削除します。
     */
    public void removeNextAll() {
        next.clear();
    }

    /**
     * 子ノードのリストから、引数で指定されたエージェントIDをもつノードの
     * agrを取得します。<BR>
     * 引数で指定されたエージェントの設定したノードがなければ-1を返します。<BR>
     * @param int agentId エージェントのID
     * @return Agent.Status 実行処理結果を表すID
     */
    public Agent.Status getChildAgr(int agentId) {
        for (FailAgentTreeElement e: next) {
            if (e.agentId == agentId) {
                return e.agr;
            }
        }
        return Agent.Status.NONE;
    }


    /**
     * このノードの文字列情報を取得します。
     * @return String 
     */
    public String toString() {
        return "id:" + agentId + " goal:" + goal + " agr:" + agr;
    }


}

