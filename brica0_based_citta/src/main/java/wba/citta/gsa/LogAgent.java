/**
 * LogAgent.java
 * ログを読み込み、ログの情報で動作するエージェント
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.10
 */
package wba.citta.gsa;

import java.io.*;
import java.util.*;

/**
 * ログを読み込み、ログの情報で動作するエージェント
 */
public class LogAgent extends Agent {

    private final String LOG_FILE_NAME = "AgentLog_";

    private FileInputStream fileInputStream = null;
    private ObjectInputStream objectInputStream = null;

    /**
     * コンストラクタ
     * @param int agid  エージェントID
     * @param SharedMemory sharedMemory  state・goalを管理する共有メモリ
     * @param boolean[] useNode  ノードの使用、不使用を設定した配列
     */
    public LogAgent(int agid, boolean[] useNode, SharedMemory sharedMemory) {
        super(agid, useNode, sharedMemory);

        /* ログの読み込みストリーム生成 */
        String fileName = LOG_FILE_NAME + agid + ".log";
        try{
            fileInputStream = new FileInputStream(fileName);
            objectInputStream = new ObjectInputStream(fileInputStream);
        }catch(Exception e){
            System.out.println(e);
            System.exit(0);
        }

    }


    ///////////////////////////////////////////////////////////////////////
    // public

    /**
     * Agentクラスを継承して作成しているため、形式的に実装<BR>
     */
    public void learn(List<Integer> state, boolean flagGoalReach, double profit) {
    }

    /**
     * エージェント固有の実行処理を行ないます。<BR>
     * ログファイルから情報を取得し、取得した情報をサブゴールとして出力します。
     * <BR>
     * @param Vector state 現在の状態
     * @param Vector goalElementArray SharedMemory.GoalStackElementのVector
     * @return Vector サブゴール
     */
    @SuppressWarnings("unchecked")
    public List<Integer> execProcess(List<Integer> state, List<SharedMemory.GoalStackElement> goalElement) {
        /* ログの読み込み 出力 */
        List<Integer> nextState = null;
        try{
            nextState = (List<Integer>)objectInputStream.readObject();
        } catch (IOException e) {
            throw new GSAException(e);
        } catch (ClassNotFoundException e){
            throw new GSAException(e);
        }
        return nextState;

    }

    /**
     * Agentクラスを継承して作成しているため、形式的に実装<BR>
     */
    public void reset() {
    }

    /**
     * Agentクラスを継承して作成しているため、形式的に実装<BR>
     */
    public void suspend() {
    }

    /**
     * Agentクラスを継承して作成しているため、形式的に実装<BR>
     */
    public void save(String fileName) { 
    }

    /**
     * Agentクラスを継承して作成しているため、形式的に実装<BR>
     */
    public void load(String fileName) {
    }
}

