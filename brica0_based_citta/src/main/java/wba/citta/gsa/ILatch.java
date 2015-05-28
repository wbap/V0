package wba.citta.gsa;

public interface ILatch {

    /**
     * Stateの指定された位置(ノード)の値を取得します。
     * @return Goal
     */
    public abstract Goal getState();

    /**
     * Stateの指定された位置(ノード)の値を取得します。
     * @param useNode
     * @return State  
     */
    public abstract State getState(boolean[] useNode);


    /**
     * 現在の状態をVectorで設定します。
     * @param Vector state 現在の状態
     */
    public abstract void setState(Goal state);

}