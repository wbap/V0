/**
 * EnvironmentAgent.java
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2000.09 BSC miyamoto
 */
package wba.citta.environment;

import java.util.*;

public class EnvironmentAgent {

	private Environment environment = null;
	/* 環境のファイル名 */

	/* 環境の情報 */
	private String[][] MAP_ARRAY = {
		        {"W","W","W","W","W","W","W","W","W","W","W","W","W","W","W"},
		        {"W","","","","","","","","","","","","","","W"},
		        {"W","","S","","","","","","","","","","","","W"},
		        {"W","","W","W","W","W","W","W","","","","","","","W"},
		        {"W","","W","","","","","W","","","","","","","W"},
		        {"W","","W","","","","","W","","","","","","","W"},
		        {"W","","W","","W","","","W","","","","","","","W"},
		        {"W","","W","","W","","","W","","","","","","","W"},
		        {"W","","W","","W","","","W","","","","","","","W"},
		        {"W","","W","","W","","","W","","","W","","","","W"},
		        {"W","","W","","W","","","","","","","","W","","W"},
		        {"W","","W","","W","W","W","W","","","W","","","","W"},
		        {"W","","","","W","","","","","","","","W","","W"},
		        {"W","","","","W","","","","W","","W","","","","W"},
		        {"W","","W","","","","","","","","","","W","","W"},
		        {"W","","W","","","","","","W","","W","","","","W"},
		        {"W","","W","","","","","","","","","","","","W"},
		        {"W","","W","W","W","W","W","","","","","","","","W"},
		        {"W","","","","","","","","","","","","","","W"},
		        {"W","W","W","W","W","W","W","W","W","W","W","W","W","W","W"}
		};

	/* ゴールをランダムな位置に設定するための乱数 */
	private Random randomGoal = new Random(0);

	/**
	 * コンストラクタ
	 */
	public EnvironmentAgent() {
		/* 環境の生成 */
		environment = new Environment(MAP_ARRAY);
		environment.initRobotPos();
		/* ゴールを設定 */
		int[] goalEnv = getRandomState();
		environment.setGoal(goalEnv[0], goalEnv[1]);
	}


	/**
	 * 現在の位置を取得します。
	 * @return int[]  int[0] x座標  int[1] y座標
	 */
	public int[] getState() {
		return environment.getXYState();
	}

	/**
	 * ゴールの位置を取得します。
	 * @return int[]  int[0] x座標  int[1] y座標
	 */
	public int[] getGoal() {
		return environment.getXYGoalState();
	}

	/**
	 * 
	 */
	public void run(int action) {
		/* actionで環境を動作させる */
		environment.run(action);

		/* ゴール到達時はゴールの位置を変更 */
		int[] stateEnv = environment.getXYState();
		int[] goalEnv = environment.getXYGoalState();
		if( (goalEnv[0]==stateEnv[0] && goalEnv[1]==stateEnv[1]) ) {
			goalEnv = getRandomState();
			environment.setGoal(goalEnv[0], goalEnv[1]);
		}
	}


	/**
	 * ランダムに生成した状態を取得します。
	 * @return int[] ランダムに生成された状態
	 *               int[0] x座標
	 *               int[1] y座標
	 */
	private int[] getRandomState() {

		int[] randomState = new int[2];

		/*
		 * ランダムに座標を取得、新たに指定された位置にすでに何か設定されて
		 * いる場合は再度座標を取得
		 */
		while(true) {
			/* 地図のサイズを取得 */
			int[] mapSize = environment.getMapSize();
			randomState[0] = randomGoal.nextInt(mapSize[0]-1) + 1;
			randomState[1] = randomGoal.nextInt(mapSize[1]-1) + 1;
			String newState = environment.getMapInfo(randomState[0],
			        randomState[1]);
			if(newState.equals("")) {
				break;
			}
		}

		return randomState;
	}


}

