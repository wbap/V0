/**
 * Agent.java
 * エージェントに共通の処理(共有メモリとの情報の受渡し等)を行なうクラス
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.07
 */
package wba.citta.gsa;

import java.util.*;
import java.io.*;

import brica0.Module;

/**
 * エージェントに共通の処理(共有メモリとの情報の受渡し等)を行なうクラス
 */
public abstract class Agent extends Module {

	/*
	 * エージェントの実行処理の結果を示すID
	 * 成功以外はサブゴール未出力条件
	 */

	/**
	 * 実行処理成功
	 */ 
	public static final int AGR_SUCCESS = 0;

	/**
	 * すでにFailAgentTreeに設定されていることによりサブゴール未出力
	 */
	public static final int AGR_FAIL_AGENT = 1;

	/**
	 * ゴール到達によりサブゴール未出力
	 */
	public static final int AGR_REACH_GOAL = 2;

	/**
	 * サブゴール未到達によりサブゴール未出力
	 */
	public static final int AGR_UNREACH_SUBGOAL = 3;

	/**
	 * 探索不能によりサブゴール未出力
	 */
	public static final int AGR_SEARCH_FAIL = 4;

	/** 
	 * 重複サブゴールによりサブゴール未出力
	 */
	public static final int AGR_SAME_SUBGOAL = 5;

	/**
	 * 同一ゴールによりサブゴール未出力
	 */
	public static final int AGR_SAME_GOAL = 6;


	/**
	 * エージェントID
	 */
	public final int AGID;

	/* 共有メモリ */
	private SharedMemory sharedMemory = null;

	/* エージェント毎のノードの使用、不使用を設定したbooleanの配列 */
	private boolean[] useNode = null;

	/* エージェントが使用するノード数(useNodeのtrueの数) */
	private int useNodeNum;

	/*
	 * この抽象クラスの実行処理で前回出力したサブゴール
	 * サブゴール未到達の判定に使用
	 */
	private Vector subgoalOld;

	/*
	 * この抽象クラスの実装クラスの実行処理で前回出力したサブゴール
	 * 実装クラスから出力されたサブゴールが必ずエージェントのサブゴール
	 * として出力される分けではないので、subgoalOldとは異なる
	 * 重複サブゴールの判定に使用
	 */
	private Vector impleAgSubgoalOld = null;


	///////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * コンストラクタ
	 * @param int agid  エージェントID
	 * @param boolean[] useNode  ノードの使用、不使用を設定した配列
	 * @param SharedMemory sharedMemory  state・goalを管理する共有メモリ
	 */
	public Agent(int agid, boolean[] useNode, SharedMemory sharedMemory) {
		this.AGID = agid;
		this.useNode = useNode;
		this.sharedMemory = sharedMemory;
		useNodeNum = getUseNodeNum();
	}


	///////////////////////////////////////////////////////////////////
	// public
	
	@Override
	public void fire() {
		// TODO
		String port = "main";
		short[] inputData = get_in_port(port);
		
		// input protocol
		// 0: do nothing
		// 1: run exec()
		System.out.println("AgentId:" + String.valueOf(AGID) + " fire() " + String.valueOf(inputData[0]));
		if (inputData[0] == 1) {
			exec();
		}
	}

	// 2001.12.14 追加 miyamoto
	/**
	 * イベント情報を学習データとして利用し、学習処理を行ないます。
	 * @param String eventFileName イベント情報の記述されたファイル名
	 */
	public void learnEvent(String eventFileName) {

		System.out.println(" Load Event File ・・・");
		/* ファイルの初期化 */
		try{
			FileReader fr = new FileReader(eventFileName);
			BufferedReader br = new BufferedReader(fr);

			try {
				while( br.ready() ) {
					/* イベントファイルからイベントを１つ取得 */
					String event = br.readLine();

					/* 取得したイベントをエージェントの状態に変換 */
					StringTokenizer stringTokenizer
					         = new StringTokenizer(event, ",");
					Vector eventState = new Vector();
					while(stringTokenizer.hasMoreTokens()) {
						eventState.add(
						        new Integer(stringTokenizer.nextToken()));
					}

					/* イベントを学習処理 */
					learn(eventState, false, 0);
				}
			}catch(Exception e) {
				System.out.println(" Event File error");
				System.out.println(e);
				System.exit(0);
			}finally {
				br.close();
				fr.close();
			}
		}catch(Exception e) {
			System.out.println(" Event File error");
			System.out.println(e);
			System.exit(0);
		}

		/* 通常の学習とは連続性がないのでリセットする */
		reset();
	}
	// ここまで

	/**
	 * 学習処理を行ないます。<BR>
	 * (引数のflagGoalReach、profitは、連想エージェントの強化学習用。
	 * ＣＤエージェントでは使用していない。)
	 * @param flagGoalReach ゴールへの到達を表すフラグ
	 * @param double profit 報酬
	 */
	public void learn(boolean flagGoalReach, double profit) {
		Vector state = getState();
		learn(state, flagGoalReach, profit);
	}

	/**
	 * 実行処理を行ないます。<BR>
	 * 共有メモリからstate、goalを取得し、ユーザ定義の実行処理(protectedの
	 * exec(Vector, Vector)経由で、abstructのexecProcess(Vector, Vector)を
	 * 呼び出し)を行ない、ユーザ定義の実行処理で生成されたsubgoalを共有メモリ
	 * に設定します。<BR>
	 * @return int 実行処理の結果を示すID<BR>
	 * AGR_SUCCESS、AGR_REACH_GOAL、AGR_UNREACH_SUBGOAL、AGR_SEARCH_FAIL、
	 * AGR_SAME_SUBGOAL、AGR_SAME_GOALのいづれか
	 */
	public int exec() {

		Vector state = getState();

		/* ゴールを取得 選択は内部で */
		Vector goalElementArray = getGoalElementArray();

//		System.out.println("");
//		System.out.println("[" + AGID + "] Agent.java");
//		System.out.println(" state:" + state);
//		System.out.println(" goalElementArray:" + goalElementArray);

		/* 実行処理を行うか */
		int isExexMode = isExec(state, goalElementArray);
		if( isExexMode != AGR_SUCCESS ) {
			subgoalOld = null;
			return isExexMode;
		}

		/* 抽象メソッド */
		Vector subgoal = exec(state, goalElementArray);

//		System.out.println(" subgoal:" + subgoal);

		int isReturnMode = isReturnSubgoal(subgoal, goalElementArray);
		if( isReturnMode != AGR_SUCCESS ) {
			subgoalOld = null;
			return isReturnMode;
		}

		setSubgoal(subgoal);

		subgoalOld = subgoal;
		return AGR_SUCCESS;
	}

	/**
	 * 到達ゴールの削除を行ないます。<BR>
	 * 自己設定ゴール(接続ノード全てを自ら設定しているゴール)に現在の状態が
	 * 到達した場合、そのゴールをゴールスタックから削除します。
	 * @return boolean true：自己設定ゴールに到達し、ゴールスタックから削除
	 * した場合
	 */
	public boolean removeReachGoal() {
		/* 判定に使用するだけなので、参照を取得 */
		Vector state = getStateReference();

		Vector selfSetGoalElementArray = getSelfSetGoalElementArray();
		Vector selfSetGoalValueArray
		        = getGoalValueArray(selfSetGoalElementArray);

		/* 自らが設定したサブゴールに到達していれば削除 */
		if( state.equals(selfSetGoalValueArray) ) {
			removeGoal();
			return true;
		}
		return false;
	}

	/**
	 * 自己設定ゴール(接続ノード全てを自ら設定しているゴール)がゴールスタック
	 * にあれば、そのゴールをスタックから削除します。
	 * @return boolean true:自己設定ゴールがあり、削除できた場合<BR>
	 * false:自己設定ゴールがないため、削除できなかった場合<BR>
	 */
	public boolean removeSelfSetGoal() {
		Vector selfSetGoalStackElement = getSelfSetGoalElementArray();
		if(selfSetGoalStackElement != null) {
			removeGoal();
			return true;
		}
		return false;
	}

	private Vector stateReference = new Vector();
	/**
	 * 現在の状態(State)を共有メモリから取得します(参照を取得)。
	 * @return Vector 現在の状態
	 */
	public Vector getStateReference() {
		stateReference.clear();
		for(int i = 0; i < sharedMemory.LENGTH; i++) {
			if( useNode[i] == true ) {
				stateReference.add(sharedMemory.getState(i));
			}
		}
		return stateReference;
	}

	/**
	 * 自ら設定したゴールの状態をスタックから取得します。<BR>
	 * エージェントが接続しているノードの要素全てを自ら設定している場合に、
	 * それらをエージェントの状態として取得します。<BR>
	 * 他のエージェントが設定している要素があれば、nullが返ります。
	 * @return Vector ゴールの状態<BR>
	 */
// GSAクラス、ManualAgentクラスからも利用するためpublic化
	public Vector getSelfSetGoalElementArray() {
		Vector selfGoal = new Vector();
		for(int i = 0; i < sharedMemory.LENGTH; i++) {
			if( useNode[i] == true ) {
				GoalStackElement v = (GoalStackElement)sharedMemory.getGoal(i);
				if( (v != null) && (v.agid == AGID) ) {
					selfGoal.add(v);
				}else {
					selfGoal = null;
					break;
				}
			}
		}
		return selfGoal;
	}


	////////////////////////////////////////////////////////////
	// 抽象メソッド

	/**
	 * エージェント固有の学習処理を行ないます。<BR>
	 * @param Vector state 現在の状態
	 * @param boolean flagGoalReach ゴールへの到達を表すフラグ
	 * @param double profit 報酬
	 */
	public abstract void learn(Vector state, boolean flagGoalReach,
	        double profit);

	/**
	 * エージェント固有の実行処理を行ないます。<BR>
	 * @param Vector state 現在の状態
	 * @param Vector goalElementArray GoalStackElementのVector
	 * @return Vector サブゴール
	 */
	public abstract Vector execProcess(Vector state, Vector goalElementArray);

	/**
	 * 学習結果をファイルに保存します。
	 * @param String fileNameファイル名
	 */
	public abstract void save(String fileName);

	/**
	 * 学習結果をファイルから読み込みます。
	 * @param String fileName ファイル名
	 */
	public abstract void load(String fileName);

	/**
	 * GSAクラスのreset()メソッドから呼び出されます。<BR>
	 * 状態遷移の履歴のクリア、前サイクルの保持情報のクリアなど、学習、
	 * 実行処理の連続性が途切れる場合に行なう情報のクリアなどの処理を
	 * 記述します。<BR>
	 */
	public abstract void reset();

	/**
	 * GSAクラスによって、実行処理を行なうエージェントが自身のエージェント
	 * から他のエージェントに切り替えられたときに呼び出されます。<BR>
	 * このため、各エージェントの実行処理で利用する前サイクルの保持情報
	 * など、実行処理の連続性に依存して保持している情報のクリアなどの処理を
	 * 記述します。<BR>
	 */
	public abstract void suspend();

	////////////////////////////////////////////////////////////
	// protected

	/**
	 * 実行処理を行ないます。
	 * 
	 */
	protected Vector exec(Vector state, Vector goalElementArray) {;
		return execProcess(state, goalElementArray);
	}

	/**
	 * GoalStackElementのVectorからGoalValueのVectorを取得します。
	 * @param Vector goalElementArray GoalStackElementのVector
	 * @return Vector            goalValueのVector
	 */
	protected Vector getGoalValueArray(Vector goalElementArray) {
		if(goalElementArray == null) {
			return null;
		}
		Vector goalValueArray = new Vector();
		for(int i = 0; i < goalElementArray.size(); i++) {
			GoalStackElement e = (GoalStackElement)goalElementArray.get(i);
			if(e != null) {
				goalValueArray.add(new Integer(e.value));
			}else {
				goalValueArray.add(null);
			}
		}
		return goalValueArray;
	}


	////////////////////////////////////////////////////////////
	// private

	/**
	 * 実行処理を行うかどうかの判定
	 * @param Vector state 現在の状態
	 * @param Vector goalElementArray ゴールスタックの状態
	 * @return int サブゴール未出力条件
	 */
	private int isExec(Vector state, Vector goalElementArray) {

		/*
		 * ゴールに到達していれば実行処理を行わない
		 * ゴールにはnullの要素がある可能性があるので、null以外の要素で判定
		 */
		Vector goalValue = getGoalValueArray(goalElementArray);
		if( Util.equalsValidElement(state, goalValue) ) {
			return AGR_REACH_GOAL;
		}

		/*
		 * 前サイクルの処理が失敗したら実行処理を行わない
		 * 処理エージェントを切り替えるため
		 */
		if( subgoalOld != null && !state.equals(subgoalOld) ) {
			return AGR_UNREACH_SUBGOAL;
		}

		return AGR_SUCCESS;
	}

	/**
	 * サブゴールを出力するかどうかの判定
	 * @param Vector sugoal サブゴール
	 * @param Vector goalElementArray GoalStackElementのVector
	 * @return int サブゴール未出力条件
	 */
	private int isReturnSubgoal(Vector subgoal, Vector goalElementArray) {
		/* サブゴールを出力できない場合 */
		if(subgoal == null) {
			impleAgSubgoalOld = subgoal;
			return AGR_SEARCH_FAIL;
		}

		/* サブゴールが前サイクルのサブゴールと同じ場合は出力しない */
		if( (impleAgSubgoalOld != null) &&
		        (impleAgSubgoalOld.equals(subgoal)) ) {
			impleAgSubgoalOld = subgoal;
			return AGR_SAME_SUBGOAL;
		}
		impleAgSubgoalOld = subgoal;

		/* サブゴールがゴールと同じ場合は出力しない */
		Vector goal = getGoalValueArray(goalElementArray);
		if(subgoal.equals(goal)) {
			return AGR_SAME_GOAL;
		}

		return AGR_SUCCESS;
	}


	/**
	 * このエージェントの使用するノード数を取得します。
	 * @param int 使用するノード数
	 */
	private int getUseNodeNum() {
		int counter = 0;
		for(int i = 0; i < sharedMemory.LENGTH; i++) {
			if(useNode[i]) {
				counter++;
			}
		}
		return counter;
	}


	/**
	 * ゴールの削除
	 * 接続先のノード全てから1要素づつ削除
	 */
	private void removeGoal() {
		for(int i = 0; i<sharedMemory.LENGTH; i++) {
			if( useNode[i] == true ) {
				sharedMemory.removeGoal(i);
			}
		}
	}

	/**
	 * 現在の状態(State)を共有メモリから取得します。
	 * @return Vector 現在の状態
	 */
	private Vector getState() {
		Vector state = new Vector();
		for(int i = 0; i < sharedMemory.LENGTH; i++) {
			if( useNode[i] == true ) {
				state.add(sharedMemory.getState(i));
			}
		}
		return state;
	}

	/**
	 * ゴールを取得します。
	 * 他のエージェントが設定したゴールがある場合は、他のエージェントが設定
	 * したゴール。なければ、自らが設定したゴールとゴールとして利用します。
	 * @return Vector GoalStackElementのVector
	 */
	private Vector getGoalElementArray() {
// 自己設定意図、他設定意図の区別を行なう設定
		Vector goalElementArray = getOtherSetGoalElementArray();
		if(goalElementArray == null)  {
			goalElementArray = getSelfSetGoalElementArray();
		}
		return goalElementArray;

// 自己設定意図、他設定意図の区別を行なわない設定
//		return getGoalElementArray2();
	}


	/**
	 * 他のエージェントが設定したゴールの状態をスタックから取得します。
	 * エージェントが接続しているノードから他のエージェントが設定したもの
	 * のみを、エージェントの状態として取得します。自ら設定している要素はnull
	 * を設定します。
	 * @return Vector GoalStackElementのVector
	 * Vectorの全ての要素がnullならVector自体をnullに設定して返す。
	 */
	private Vector getOtherSetGoalElementArray() {
		Vector otherGoal = new Vector();
		int nullNum = 0;

		for(int i = 0; i < sharedMemory.LENGTH; i++) {
			if( useNode[i] == true ) {
				GoalStackElement v = (GoalStackElement)sharedMemory.getGoal(i);
				if( (v != null) && (v.agid != AGID) ) {
					otherGoal.add(v);
				}else {
					otherGoal.add(null);
					nullNum++;
				}
			}
		}
		/* 要素がすべてnullならVector自体をnullに設定 */
		if(nullNum == useNodeNum) {
			otherGoal = null;
		}

		return otherGoal;
	}

	/**
	 * サブゴールを設定します。
	 * @param Vector subgoal サブゴール(GoalValueArray)
	 */
	private void setSubgoal(Vector subgoal) {
		if(subgoal != null) {
			pushGoalToStack(subgoal);
		}
	}

	/**
	 * サブゴールをスタックに設定します。
	 * @param Vector subgoal サブゴール(GoalValueArray)
	 */
	private void pushGoalToStack(Vector subgoal) {
		int index = 0;
		for(int i = 0; i < sharedMemory.LENGTH; i++) {
			if( useNode[i] == true ) {
				Integer integer = (Integer)subgoal.get(index);
				if(integer != null) {
					int value = integer.intValue();
					GoalStackElement elm = new GoalStackElement(value, AGID);
					sharedMemory.pushGoal(i, elm);
				}
				index++;
			}
		}
	}


	///////////////////////////////////////////////////////////////////////
	// 意図の区別を行なわなくても正常に動作するか確認するために仮のメソッド

	private Vector getGoalElementArray2() {
		Vector goal2 = new Vector();
		int nullNum = 0;

		for(int i = 0; i < sharedMemory.LENGTH; i++) {
			if( useNode[i] == true ) {
				GoalStackElement v = (GoalStackElement)sharedMemory.getGoal(i);
				if( v != null ) {
					goal2.add(v);
				}else {
					goal2.add(null);
					nullNum++;
				}
			}
		}
		/* 要素がすべてnullならVector自体をnullに設定 */
		if(nullNum == useNodeNum) {
			goal2 = null;
		}

		return goal2;
	}


}
