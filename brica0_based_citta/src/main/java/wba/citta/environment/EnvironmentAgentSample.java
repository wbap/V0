/**
 * EnvironmentAgentSample.java
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2001.12 BSC miyamoto
 */
package wba.citta.environment;

import java.util.*;

public class EnvironmentAgentSample {

	private Environment environment = null;

	/* ランダムな行動を行なうための乱数 */
	private Random randomAction = new Random(0);

	/* 行動を設定する変数 */
	private int action = -1;

	/**
	 * コンストラクタ
	 */
	public EnvironmentAgentSample(String fileName) {
		environment = new Environment(fileName);
	}

	/**
	 * 現在の状態を取得します。
	 */
	public Vector getState() {
		Vector state = new Vector();
		int[] xystate = environment.getXYState();
		state.add(new Integer(xystate[0]));
		state.add(new Integer(xystate[1]));
		state.add(new Integer(action));
		return state;
	}

	/**
	 * 環境を動作させます。
	 */
	public void run(Vector nextState) {
		if(nextState != null) {
			if(nextState.get(2) != null) {
				action = ((Integer)nextState.get(2)).intValue();
			}else {
				action = randomAction.nextInt(4)*2;
			}
		}else {
			action = randomAction.nextInt(4)*2;
		}
		environment.run(action);
	}

}

