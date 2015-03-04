/**
 * InterfaceAgent.java
 * 環境と認知距離の処理を行なうエージェントのインターフェースとして状態の変換等     * の処理を行うクラス
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * 2000.10 BSC miyamoto
 */
package wba.citta.cognitivedistance;

import java.util.*;
import java.io.*;

/**
 * 環境と認知距離の処理を行なうエージェントのインターフェースとして状態の変換等
 * の処理を行うクラスです。
 */
public class InterfaceAgent {

    /* 最下層のLayeredAgent */
    private LayeredAgent bottomLayeredAgent;
    /* 状態からIDへ変換を行なうテーブル */
    private Map<List<Integer>, Integer> stateToId;
    /* IDから状態へ逆変換を行なうテーブル */
    private Map<Integer, List<Integer>> idToState;

    /* 状態(Vector)の要素数を固定にするための変数 */
    private int elementNum = -1;

    //////////////////////////////////////////////////////////
    // コンストラクタ

    /**
     * コンストラクタ
     * @param LayeredAgnet bottomLayeredAgent  最下層のLayeredAgent
     */
    public InterfaceAgent(LayeredAgent bottomLayeredAgent) {
        this.bottomLayeredAgent = bottomLayeredAgent;
        /* 変換テーブルの生成 */
        stateToId = new HashMap<List<Integer>, Integer>();
        idToState = new HashMap<Integer, List<Integer>>();
    }


    //////////////////////////////////////////////////////////
    // private

    /**
     * 状態の変換を行ないます。
     * 環境からの状態に対応するノードのIDを取得します。
     * @param Object state 環境からの状態
     * @return Integer     対応するノードのID
     */
    private Integer getID(List<Integer> state) {

        if(state == null) {
            return null;
        }

        /* 状態に対応するIDを取得 */
        Integer id = (Integer)stateToId.get(state);

        return id;
    }


    /**
     * 状態の逆変換を行ないます。
     * ノードのIDに対応する環境の状態を取得します。
     * @param Integer id  ノードのID
     * @return Object     対応する環境の状態
     */
    private List<Integer> getState(Integer id) {
        return idToState.get(id);
    }

    /**
     * 新しい状態をテーブルに設定し、対応するノードをbottomLayeredAgentに設定
     * します。
     * @param Object state 新しい状態
     * @return Integer     対応する新しいID
     * @exception NullPointerException 状態がnullの場合
     * @exception ElementNumberException 状態の要素数が不正な場合
     */
    private Integer newNode(List<Integer> state) {
        /* 新しい状態を変換用のテーブルに設定 */
        int newId = idToState.size();

        /* 最初に入力された状態の要素数を有効な要素数に設定 */ 
        if(newId == 0) {
            elementNum = state.size();
        }

        /* 要素数が不正でないかチェック */
        assert isSameSize(state);

        idToState.put(newId, state);
        stateToId.put(state, newId);

        /* LayeredAgentの１層目に状態を作成 */
        bottomLayeredAgent.newNode(null);

        return newId;
    }

    ///////////////////////////////////////////////////////////////////
    // public

    /**
     * 認知距離の学習を行ないます。
     * @param Vector currentState 現在の状態
     * @exception NullPointerException 引数で設定されたVectorがnullの場合
     * @exception ElementNumberException 引数で設定されたVectorのサイズが不正
     *                                   な場合。
     */
    public void learn(List<Integer> currentState) {
        /* 状態の変換 */
        Integer currentNodeID = getID(currentState);
        /* 登録されていない状態なら新しいIDを設定 */
        if(currentNodeID == null) {
            currentNodeID = newNode(currentState);
        }

        /* 1層目の学習 */
        bottomLayeredAgent.learn(currentNodeID);
    }

    /**
     * 実行処理を行い、引数で指定された現在の状態から、ゴールの状態へ遷移する
     * ため次の状態を取得します。
     * @param Vector currentState 現在の状態
     * @param Vector goalState    ゴールの状態
     * @return Vector             次の状態
     * @exception NullPointerException 引数で設定された現在の状態がnullの場合
     * @exception ElementNumberException 引数で設定された現在の状態のサイズが
     *                                   不正な場合。
     */
    public List<Integer> exec(List<Integer> currentState, List<Integer> goalState) {
        assert isSameSize(currentState);
        /* ゴールがなければ処理しない */
        if(goalState == null) {
            // 2001.04.05 追加 bsc miyamoto
            /* 保持している前サイクルの情報をクリア */
            bottomLayeredAgent.resetOldValue();
            return null;
        }

        /* 状態の変換 */
        Integer currentNodeID = getID(currentState);

        // 2001.06.07 修正 ゴールにnullの要素がある場合のみ補完処理
        // 2001.03.29 追加
        /* 取得したゴールから抜けている部分を補完 */
        List<Integer> newGoalState = null;
        if( checkNullElement(goalState) ) {
            newGoalState = goalState;
        }else {
            // 2001.08.14 修正 miyamoto 到達可能なゴールを選択。
            newGoalState = getSameStates(currentState, goalState);
        }

        Integer goalNodeID = getID(newGoalState);

        /* 移動先の状態の取得 */
        Integer nextNodeID = bottomLayeredAgent.exec(currentNodeID, goalNodeID);
        /* 取得した移動先の状態を逆変換 */
        List<Integer> nextState = getState(nextNodeID);

        return nextState;
    }


    /**
     * より知らない状態へ移動するための直接移動可能な状態を取得します。
     * @param Vector currentState 現在の状態
     * @return Vector             次の状態
     */ 
    public List<Integer> novelSearch(List<Integer> currentState) {
        /* 状態の変換 */
        Integer currentNodeID = getID(currentState);

        /* 移動先の状態の取得 */
        Integer nextNodeID = bottomLayeredAgent.novelSearch(currentNodeID);

        /* 取得した移動先の状態を逆変換 */
        List<Integer> nextState = getState(nextNodeID);

        return nextState;
    }


    /**
     * 新規探索を行なうためのカウンタをリセットします。
     */
    public void counterReset() {
        bottomLayeredAgent.resetUpperAndThisLayerFamiliarCount();
    }


    /**
     * 学習データを読み込みます。
     * 環境の状態からCognitiveDistance用の状態へ変換用、逆変換用を行なう
     * テーブルを読み込みます。
     * @param ObjectInputStream oInputStream
     */
    public void load(ObjectInputStream oInputStream) throws IOException {
        /* 有効な状態の要素数を読込み */
        try {
            elementNum = ((Integer)oInputStream.readObject()).intValue();
        } catch (ClassNotFoundException e) {
            throw new CDException(e);
        }
    }


    /**
     * 学習データを保存します。
     * 環境の状態からCognitiveDistance用の状態へ変換用、逆変換用を行なう
     * テーブルを保存します。
     * @param ObjectOutputStream oOutputStream
     */
    public void save(ObjectOutputStream oOutputStream) throws IOException  {
        /* 有効な状態の要素数を保存 */
        oOutputStream.writeObject(new Integer(elementNum));
    }

    ///////////////////////////////////////////////////////////////////
    // デバック用の情報の取得、動作の制御に使用するメソッド

    /**
     * 各層のStateBufferに設定されている履歴をクリアします。
     */
    public void reset() {
        bottomLayeredAgent.reset();
    }

    /**
     * 第一引数で指定された状態に対応する、第二引数で指定された層でのノードを
     * 取得します。
     * @param Vector state 環境での状態
     * @param int layer    ノードを取得するレイヤ
     * @return Node        ノード
     */
    public Node getNode(List<Integer> state, int layer) {
        Integer id = getID(state);
        return bottomLayeredAgent.getNode(id, layer);
    }

    /**
     * 一番目の引数の有効な要素が、二番目の引数の同じ位置の要素と同じか
     * どうかチェックします。
     * @param Vector a
     * @param Vector b
     * @return boolean  有効な要素の値が同じ場合はtrue
     *                  異なる要素がある場合はfalse
     */
    private boolean checkValidElement(List<Integer> a, List<Integer> b) {
        // 2001.05.29 追加 同じとする条件にサイズが同じことを追加
        if( a.size() != b.size() ) {
            return false;
        }
        // ここまで

        /* 全要素をチェック */
        for(int i = 0; i < a.size(); i++) {
            /* 要素が有効な場合は、その位置の要素が同じかチェック */
            if (a.get(i) != null) {
                if (!a.get(i).equals(b.get(i))) {
                    return false;
                }
            }
        }
        return true;
    }


    // 2001.05.29 追加 miyamoto
    /**
     * 引数のVectorの要素数が有効かどうかチェックします。
     * @Vector v
     */
    private boolean isSameSize(List<Integer> v) {
        if(v.size() != elementNum) {
            return false;
        }
        return true;
    }

    /**
     * 引数のVectorの要素にnullの要素がないかチェックします。
     * @param Vector v 
     * @return boolean  true:nullの要素なし   false:nullの要素あり
     */
    private static boolean checkNullElement(List<Integer> v) {
        for(int i = 0; i < v.size(); i++) {
            if( v.get(i) == null ) {
                return false;
            }
        }
        return true;
    }

    /**
     * IDから状態へ逆変換を行なうVectorのテーブルを取得します。
     * @return Vector IDから状態へ逆変換を行なうテーブル
     */
    public Map<Integer, List<Integer>> getIdToState() {
        return idToState;
    }

    // 2001.08.14 追加 miyamoto
    /**
     * テーブルに登録済みの状態から、引数で設定された状態の有効な要素が同じ
     * 状態を取得します。 
     * @param Vector a    
     * @return Vector   有効な要素の位置の値が同じ状態
     */
    private List<Integer> getSameStates(List<Integer> a, List<Integer> b) {
        for (List<Integer> element: idToState.values()) {
            if (checkValidElement(b, element)) {
                if( isReach(a, element) ) {
                    return element;
                }
            }
        }
        return null;
    }

    /**
     * 第一引数の状態から第二引数の状態への到達可能性を調べます。
     * @param Vector a
     * @param Vector b
     * @return boolean true 到達可能 false 到達不可能
     */
    public boolean isReach(List<Integer> a, List<Integer> b) {
        /* 状態の変換 */
        Integer aID = getID(a);
        Integer bID = getID(b);
        boolean isReach = bottomLayeredAgent.isReach(aID,bID);
        return isReach;
    }

    /**
     * 各層ごとに保持している以前のサイクルの情報をクリアします。
     */
    public void resetOldValue() {
        bottomLayeredAgent.resetOldValue();
    }

}


