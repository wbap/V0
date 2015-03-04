/**
 * GoalStackElement.java
 * 共有メモリで扱うゴールの情報の単位
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.07
 */
package wba.citta.gsa;

/**
 * 共有メモリで扱うゴールの情報の単位
 */
public class GoalStackElement {

	/**
	 * ゴールの値
	 */
	public final int value;

	/**
	 * ゴールを設定したエージェントID
	 */
	public final int agid;

	/**
	 * コンストラクタ
	 * @param int value ゴールの値
	 * @param int agid  設定したエージェントのID
	 */
	public GoalStackElement(int value, int agid) {
		this.value = value;
		this.agid = agid;
	}

	/**
	 * ゴールの情報を表示します。
	 * @return String ゴールの情報<BR>
	 * 表示形式  val:21 id:701
	 */
	public String toString() {
		String str = "val:" + value + " id:" + agid;
		return str;
	}

}

