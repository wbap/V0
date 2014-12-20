/**
 * DoorKeyDemo.java
 * Cittaを利用し、ドアキー課題のデモを行なうクラス。
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.09
 */
package doorkeydemo;

import java.io.*;
import java.util.*;
import gsa.*;


/**
 * Cittaを利用し、ドアキー課題のデモを行なうクラス。
 */
public class DoorKeyDemo {

	/* 環境 */
	private EnvironmentAgent environmentAgent = null;
	/* Citta */
	private GSA gsa = null;

	/* デモの設定情報を管理するクラス */
	private DemoProperty prop = null;

	/* 実験に関する設定 */
	private int saveCount;
	private String saveFileName;
	private String loadFileName;
	private final int TIME_OUT_STEP;
	private final int SLEEP_TIME;

	/* ゴール */
	private Vector goal = null;


	////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * コンストラクタ
	 */
	public DoorKeyDemo(String propFileName) {

		/* 設定ファイルの読み込み */
		try {
			prop = new DemoProperty(propFileName);
		}catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

		gsa = new GSA(prop.getGSAPropFileName());

		environmentAgent = new EnvironmentAgent(prop.getEnvFileName(), 
		         prop.getDoorOpenMode(), 1/*エージェント数*/);

		/* 実験設定 */
		saveCount = prop.getSaveStepNum();
		saveFileName = prop.getSaveFileName();
		loadFileName = prop.getLoadFileName();
		TIME_OUT_STEP = prop.getTimeOutStepNum();
		SLEEP_TIME = prop.getSleepTime();

		/* 学習データの読み込み */
		if(!loadFileName.equals("")) {
			gsa.load(loadFileName);
		}

		/* ゴールを生成し、gsaに設定 */
		initGoal();
		setGoal();

		// トライアルごとのステップ数を出力するファイルを生成
		initLogFile("Step.log");

	}

	/**
	 * ゴールの生成
	 */
	private void initGoal() {
		goal = new Vector();
		goal.add(null);
		goal.add(null);
		goal.add(null);
		goal.add(new Integer(1));
		goal.add(null);
		goal.add(null);
		goal.add(null);
	}

	////////////////////////////////////////////////////////////////
	// public

	/**
	 * 繰り返し処理
	 */
	public void repeatProcess() {

		int toGoalStepCount = 0; /* ゴールまでのステップ数 */
		int stepCount = 0;       /* 実行ステップ数のカウント */

		/*
		 * 今回のデモで使用する環境からのノード
		 * x座標：y座標：行動：場所の属性：アイテム：地図のID：ドアが開く条件
		 */
		Vector state = null;    /* 環境が出力する現在の状態 */
		Vector subgoal = null;  /* CITTAが出力するサブゴール */

		/* 環境をGSAの処理を交互に行なう */
		while(true) {

			/* 実験用 速度を調整するためにスリープさせる */
			try {
				Thread.sleep(SLEEP_TIME);
			}catch(InterruptedException e) {
				System.out.println(e);
			}


			/* ゴール到達 または タイムアウトでスタート位置から再スタート */
			if( isReachGoal(state, goal) || toGoalStepCount==TIME_OUT_STEP ) {

				/* ゴール到達またはタイムアウトまでのステップ数を出力 */
				System.out.println("++++++++++++++++++++++++++++++++++++++++");
				System.out.println("   " + toGoalStepCount);
				logOut(toGoalStepCount);
				System.out.println("++++++++++++++++++++++++++++++++++++++++");

				toGoalStepCount = 0;
				restart();
				subgoal = null;

				if(isReachGoal(state, goal) ) {
					goalReachProcess();
				}
			}

			/* ステップ数のカウント、表示 */
			toGoalStepCount++;
			stepCount++;
			System.out.println("");
			System.out.println(" step count " + stepCount);

			/* 環境の処理 */
			/*
			 * CITTAの出力したサブゴールから行動を取得 取得した行動で
			 * 環境を動作させる
			 */
			int action = -1;
			if(subgoal != null) {
				/* 環境の出力の3番目の要素がaction */
				if(subgoal.get(2) != null) {
					action = ((Integer)subgoal.get(2)).intValue();
				}
			}
			state = environmentAgent.move(action);

			/* CITTAの処理 */
			/* 学習データの保存 */
			if(stepCount == saveCount) {
				if(!saveFileName.equals("")) {
					gsa.save(saveFileName);
				}
			}
			/* stateからサブゴールを取得 */
			subgoal = gsa.exec(state);

			/* スタック、ツリーの状態を表示 */
//			gsa.printStack();
//			gsa.printGoalTree();

		}
	}


	////////////////////////////////////////////////////////////////
	// private

	/**
	 * ゴールへ到達したかどうか判定
	 * ゴールの有効な要素(null以外の要素)がStateの要素と同じならtrueを返す。
	 */
	private boolean isReachGoal(Vector state, Vector goal) {

		if(goal == null || state == null) {
			return false;
		}

		for(int i = 0; i < state.size(); i++) {
			Integer sElement = (Integer)state.get(i);
			Integer gElement = (Integer)goal.get(i);
			if(gElement != null && !sElement.equals(gElement)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * ゴール到達時の処理
	 */
	private void goalReachProcess() {
		/* ゴール到達時に画面を点滅させる */
		environmentAgent.goalReach();
	}

	/**
	 * スタート位置に戻し再度実験を行なうときの処理
	 */
	private void restart() {
		gsa.reset();
		environmentAgent.restart();
		/* ゴールをgsaに再設定 */
		setGoal();
	}

	/**
	 * ゴールをCITTAに設定します。
	 */
	private void setGoal() {
		gsa.setGoal(goal);
	}


	//////////////////////////////////////////////////////////////
	// ログの生成、出力用のメソッド

	private PrintWriter printWriter; // ファイル出力用

	private void initLogFile(String fileName) {
		try{
			FileOutputStream fileOutputStream = new FileOutputStream(fileName,
			        false);
			printWriter = new PrintWriter(fileOutputStream, true);
		}catch(Exception e) {
		}
	}

	private void logOut(int stepNum) {
		printWriter.println(stepNum);
	}


	////////////////////////////////////////////
	// メインメソッド 

	public static void main(String[] args) {
		DoorKeyDemo dkDemo = new DoorKeyDemo(args[0]);
		dkDemo.repeatProcess();
	}


}
