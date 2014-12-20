/**
 * LogOutAgent.java
 * エージェントの実行処理で出力されるサブゴールを、ログとしてファイルに保存する
 * 処理を追加したAgent
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.10
 */
package gsa;

import java.io.*;
import java.util.*;

/**
 * エージェントの実行処理で出力されるサブゴールを、ログとしてファイルに保存する
 * 処理を追加したAgent
 */
public abstract class LogOutAgent extends Agent {

	private final String LOG_FILE_NAME = "AgentLog_";

	private FileOutputStream fileOutputStream = null;
	private ObjectOutputStream objectOutputStream = null;


	/**
	 * コンストラクタ
	 * @param int agid  エージェントID
	 * @param boolean[] useNode  ノードの使用、不使用を設定した配列
	 * @param SharedMemory sharedMemory  state・goalを管理する共有メモリ
	 */
	public LogOutAgent(int agid, boolean[] useNode,
	         SharedMemory sharedMemory) {
		super(agid, useNode, sharedMemory);
		initLogFile(agid + ".log");
	}

	/**
	 * ログファイルの初期化
	 * 引数で指定されたファイル名で、ログファイルを生成します。
	 * @param String fileName ファイル名
	 */
	private void initLogFile(String fileName) {

		String logFileName = LOG_FILE_NAME + fileName;

		try {
			/* ストリームの作成 */
			fileOutputStream = new FileOutputStream(logFileName, false);
			objectOutputStream = new ObjectOutputStream(fileOutputStream);
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println(e);
			System.exit(0);
		}

	}


	///////////////////////////////////////////////////////////////////////
	// public

	/**
	 * Agentクラスのexecをオーバーライドし、サブゴールをログとしてファイル出力
	 * する処理を追加。<BR>
	 * @param Vector state
	 * @param Vector goalElementArray
	 * @return subgoal
	 */
	public Vector exec(Vector state, Vector goalElementArray) {

		Vector subgoal = execProcess(state, goalElementArray);

		try {
			objectOutputStream.writeObject(subgoal);
		}catch(Exception e) {
			System.out.println(e);
		}

		return subgoal; 
	}


}

