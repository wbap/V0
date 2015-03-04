
/** 
 * GSA.java
 * GSAの本体
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.09
 */
package wba.citta.gsa;

import java.util.*;

import brica0.CognitiveArchitecture;
import brica0.VirtualTimeSyncScheduler;
import wba.citta.gsa.viewer.*;

/**
 * GSAの本体
 */
public class GSA {

	// GSA 
	/* エージェントの設定ファイルを読み込むクラス */
	private GSAProperty prop;
	/* ManualAgentを使用するかどうか */
	private boolean useMana = false;
	/* 外部から設定されたゴール */
	private Vector target = null;
	/* エージェントの動作状況を表示するviewer */
	private AgentViewer viewer = null;
	/* エージェント数 */
	private int agentNum;
	/* ノード数 */
	private int nodeNum;
	/* 外部から設定されたゴール用の仮のエージェントID */
	private final int GOAL_AGID = 0;
	/* ManualAgentのエージェントID */
	private final int MANUAL_AGENT_ID = 10;
	/*
	 * 実行エージェントの配列中のインデックス
	 * 実行エージェントを順に選択する場合に使用
	 */
	private int sequenceSelectIndex = 0;
	/* 実行エージェントをランダムに選択するための乱数 */
	private int randamSelectSeed = 1;
	private Random random = new Random(randamSelectSeed);

	
	// AgentController Module
	/* エージェントの配列 */
	private Agent[] agents = null;
	/* 実行エージェントの選択方法 0:配列の順 1:ランダム */
	private int agentSelectMode = 1;
	/* 到達に失敗したゴールをツリーで管理するクラス */
	private FailAgentTree failAgentTree = null;
	/* 実行処理を行なうために選択されているエージェント */
	private Agent agent = null;
	/* エージェントの選択状況を示すbooleanの配列 */
	private boolean[] useAgentFlags = null;
	
	
	// SharedMemory Module
	/* State・Goalを管理する共有メモリ */
	private SharedMemory sharedMemory = null;
	
	
	// Brica Modules
	VirtualTimeSyncScheduler scheduler;
	CognitiveArchitecture cognitiveArchitecture;
	Agent[] agentModules;
	AgentControllerModule agentController;
	public final static int AGENT_COUNT = 8;
	final double SCHEDULER_INTERVAL = 1;
	final String AGENT_MODULE_ID_PREFIX = "agent_";
	final String AGENT_CONTROLLER_MODULE_ID = "agentController";
	public final static int DO_NOTHING = 0;
	public final static int EXEC = 1;

	////////////////////////////////////////////////////////////////
	// コンストラクタ 初期化メソッド

	/**
	 * コンストラクタ
	 * @param String propFileName GSAの設定ファイル名
	 */
	public GSA(String propFileName) throws Exception {

		initPropFile(propFileName);

		agentNum = prop.getAgentNum();
		nodeNum = prop.getNodeNum();
		useMana = prop.getUseMana();

		sharedMemory = new SharedMemory(prop.getNodeNum(),
		        prop.isShowGoalStackViewer());
		failAgentTree = new FailAgentTree(prop.isShowFailAgentTreeViewer());

		initAgent();

		if(prop.isShowAgentViewer()) {
			initViewer();
		}
//util = new Util(agentNum);
		
		// initialize brica
		scheduler = new VirtualTimeSyncScheduler(SCHEDULER_INTERVAL);
		cognitiveArchitecture = new CognitiveArchitecture(scheduler);

		short[] tmp = {0};
		agentController = new AgentControllerModule();
		for (int i=0; i<AGENT_COUNT; i++) {
			agentController.makeOutPort("out" + String.valueOf(i), 1);
			agentController.setState("out" + String.valueOf(i), tmp);
		}
		cognitiveArchitecture.addModule(AGENT_CONTROLLER_MODULE_ID, agentController);
		
		for (int i=0; i<AGENT_COUNT; i++) {
			String id = AGENT_MODULE_ID_PREFIX + String.valueOf(i);
			agents[i].makeOutPort("out", 1);
			agents[i].setState("out", tmp);
			cognitiveArchitecture.addModule(id, agents[i]);
		}
		
		for (int i=0; i<AGENT_COUNT; i++) {
			agents[i].connect(agentController, "out" + String.valueOf(i), "in");
			agentController.connect(agents[i], "out", "in" + String.valueOf(i));
		}
	}

	/**
	 * 設定ファイルの情報を管理するクラスの初期化
	 * @param String propFileName 設定ファイル名
	 */
	private void initPropFile(String propFileName) {
		try {
			prop = new GSAProperty(propFileName);
		}catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * エージェントの初期化
	 */
	private void initAgent() {

		/*
		 * マニュアルエージェントを利用する場合は、エージェント数を１増やす
		 * 通常のエージェント生成のためのカウンタは別途用意
		 */
		int loopNum = agentNum;
		if(useMana) {
			agentNum+=1;
		}

		agents = new Agent[agentNum];

		if(useMana) {
			initManualAgent();
		}

		for(int i = 0; i < loopNum; i++) {
			int agentType = prop.getAgentType(i);
			int agid = prop.getAgentID(i);
			boolean[] useNode = prop.getUseNode(i);
			// 2001.12.14 追加 miyamoto
			String eventFileName = prop.getEventFileName(i);
			// ここまで

			System.out.println("");
			System.out.println(" [ agid " + agid + " ]");
			System.out.println("  agent type:" + agentType);
			System.out.print("  use node:[");
			for(int m = 0; m < useNode.length; m++) {
				System.out.print(useNode[m] + ",");
			}
			System.out.println("]");
			System.out.println("  event file name:" + eventFileName);

			if(agentType == prop.CD) {
				agents[i] = new CDAgent(agid, useNode, sharedMemory);
			}else if(agentType == prop.ASSOCIATE) {
				agents[i] = new AssociateAgent(agid, useNode, sharedMemory);
			}else if(agentType == prop.LOG) {
				agents[i] = new LogAgent(agid, useNode, sharedMemory);
			}

			// 2001.12.14 追加 miyamoto
			if(eventFileName != null) {
				agents[i].learnEvent(eventFileName);
			}
			// ここまで
		}
	}

	/* 到達ゴールの削除を行なったエージェントを設定する変数 */
	private boolean[] removeReachGoalAgents = null;
	/**
	 * エージェントの動作状況を表示するViewerの初期化
	 */
	private void initViewer() {
		int[] agentIDs = new int[agents.length];
		for(int i = 0; i < agents.length; i++) {
			agentIDs[i] = agents[i].AGID;
		}
		removeReachGoalAgents = new boolean[agents.length];
		viewer = new AgentViewer(agentIDs, removeReachGoalAgents);
	}

	/**
	 * ManualAgentの生成
	 */
	private void initManualAgent() {
		boolean[] allNode = new boolean[prop.getNodeNum()];
		for(int i = 0; i < allNode.length; i++) {
			allNode[i] = true;
		}
		agents[agentNum-1] = new ManualAgent(MANUAL_AGENT_ID, allNode,
		        sharedMemory);
	}


	////////////////////////////////////////////////////////////////
	// public

	/**
	 * 実行処理を行ないます。
	 * @param Vector state 現在の状態
	 * @return Vector サブゴール
	 */
	public Vector exec(Vector state) {
		/* 引数で設定された現在の状態をスタックに設定 */
		sharedMemory.setState(state);

		useAgentFlags = new boolean[agentNum];
		clearUseFlag();

//util.reset();

		/*
		 * スタック上のゴールに到達した場合、そのゴールをスタック・ツリーから
		 * 削除
		 */
		removeReachGoal();

		/*
		 * ツリー上のゴールに到達した場合、スタック、ツリーをクリア
		 */
		if( isReachTreeGoal() ) {
			clearGoalStackAndTree();
			setGoal(target);
		}

		/* エージェントの学習 */
		double reward = 0;
		learn(false/*ゴール到達フラグ*/, reward);
		
		
		/*
		 * 実行可能なエージェントが選択されるか、すべてのエージェントが失敗
		 * するまで繰り返し
		 */
		while(true) {
			
			while (agent == null) {
				cognitiveArchitecture.step();
				for (int i=0; i<agents.length; i++) {
					if (agentController.getOutPort("out" + String.valueOf(i))[0] == GSA.EXEC) {
						agent = agents[i];
						useAgentFlags[i] = true;
						break;
					}
				}
			}

			/* エージェントの実行処理 */
			/* このエージェントがすでに失敗済みなら実行処理を行なわない */
			int isSuccess = -1;
//			if( failAgentTree.isFail(agent.AGID) ) {
			if( failAgentTree.getChildAgr(agent.AGID) > -1) {
//System.out.println("   already fail");
				isSuccess = Agent.AGR_FAIL_AGENT;
			}else {
				isSuccess = agent.exec();
			}

			// 実行処理を行なったエージェントを表示
			if(viewer != null) {
				viewer.setExecAgentID(agent.AGID);
				viewer.repaint();
			}

			if(isSuccess == Agent.AGR_SUCCESS) {
				/* ゴールをツリーに設定 */
				Vector agentGoalElementArray
				        = agent.getSelfSetGoalElementArray();
				Vector agentGoalValueArray
				        = agent.getGoalValueArray(agentGoalElementArray);
				failAgentTree.addTreeNode(agent.AGID, agentGoalValueArray);

				/* 処理成功時にゴールを返す */
				Vector goal = sharedMemory.getGoalValueArray();
				return goal;
			}else {
				/* 処理失敗のエージェントをツリーで管理 */
				failAgentTree.addTreeNode(agent.AGID, isSuccess);
				/* エージェント切り替え時に前エージェントの保持情報をクリア */
				agent.suspend();
				agent = null;

				if(getNotUseAgentNum() == 0) {
//				if(util.getNotUseNum() == 0) {
					removeUnsolvedGoal();
					break;
				}
			}
		}

		if(viewer != null) {
			viewer.setExecAgentID(-1);
			viewer.repaint();
		}

		/* ゴールを返す */
		Vector goal = sharedMemory.getGoalValueArray();
		return goal;
	}
	

	/**
	 * 学習結果をファイルに保存します。<BR>
	 * エージェントごとの学習結果保存処理を呼び出します。<BR>
	 * 各エージェントのファイル名は引数で設定されたファイル名に各エージェントの
	 * IDを追加したものになります。(fileName+agid.dat)
	 * @param String fileName ファイル名
	 */
	public void save(String fileName) {
		for(int i = 0; i < agents.length; i++) {
			agents[i].save(fileName + agents[i].AGID + ".dat");
		}
	}

	/**
	 * 学習結果をファイルから読み込みます。<BR>
	 * エージェントごとの学習結果読み込み処理を呼び出します。<BR>
	 * 各エージェントのファイル名は引数で設定されたファイル名に各エージェントの
	 * IDを追加したものになります。(fileName+agid.dat)
	 * @param String fileName ファイル名
	 */
	public void load(String fileName) {
		for(int i = 0; i < agents.length; i++) {
			agents[i].load(fileName + agents[i].AGID + ".dat");
		}
	}

	/**
	 * スタック、ツリーをクリアし、各エージェントのreset()メソッドを呼び出し
	 * ます。<BR>
	 * 学習結果はリセットされません。<BR>
	 * 学習結果を残したまま再スタートする場合など、学習、実行処理の連続性が
	 * 途切れる場合の処理を行ないます。
	 */
	public void reset() {
		/* 各エージェントのreset()の呼び出し */
		for(int i = 0; i < agentNum; i++) {
			agents[i].reset();
		}
		/* スタック、ツリーをクリア */
		failAgentTree.clear();
		sharedMemory.removeAllGoal();
		/* 使用するエージェントをクリア */
		agent = null;
	}

	/**
	 * ゴールを共有メモリに設定します。
	 * @param Vector goal ゴール
	 */
	public void setGoal(Vector goal) {
		target = goal;
		if(goal != null && goal.size() == nodeNum) {
			for(int i = 0; i < goal.size(); i++) {
				Integer goalValue = (Integer)goal.get(i);
				if(goalValue != null) {
					GoalStackElement goalElement = new GoalStackElement(
					         goalValue.intValue(), GOAL_AGID);
					sharedMemory.pushGoal(i, goalElement);
				}
			}
			failAgentTree.addTreeNode(GOAL_AGID, goal);
		}
	}

	/**
	 * 共有メモリの状態を表示します。
	 */
	public void printSharedMemory() {
		sharedMemory.printState();
		sharedMemory.printGoalStack();
	}

	/**
	 * ツリーの状態を表示します。
	 */
	public void printFailAgentTree() {
		failAgentTree.printTree();
	}


	////////////////////////////////////////////////////////////////
	// private

	/**
	 * ツリーのカレントノードの子ノードに、全てのエージェントからのノードが
	 * 設定されている場合、カレントノードのゴールを失敗ゴールとして、スタック
	 * から削除します。<BR>
	 * ツリーは現在の位置を移動させる。
	 */
// 現在はある時点で自己設定ゴールが取得できるエージェントは１つに特定されるが
// 複数のエージェントが自己設定ゴールを取得できるような場合、現在の実装では
// 問題が発生する可能性がある。
	private void removeUnsolvedGoal() {
		for(int i = 0; i < agentNum; i++) {
			boolean b = agents[i].removeSelfSetGoal();
			if(b == true) {
				failAgentTree.moveParent();
				break;
			}
		}
	}


	////////////////////////////////////////////////////////////////
	// 実行エージェントの選択

//	private Agent getExecAgent() {
//		int num = util.getRandomNum();
//		return agents[num];
//	}

	/**
	 * 実行処理を行なうエージェントを取得します。
	 * @param Agent エージェント
	 */
	private Agent getExecAgent() {
		Agent agent = null;
		if(agentSelectMode == 0) {
			agent = getExecAgentArrayOder();
		}else {
			agent = getExecAgentRandomOder();
		}
		return agent;
	}

	/**
	 * 実行処理を行なうエージェントを順番に取得します。
	 * agents(エージェントの配列)に設定された順に取得。
	 * @param Agent エージェント
	 */
	private Agent getExecAgentArrayOder() {
		Agent agent = agents[sequenceSelectIndex];
		sequenceSelectIndex++;
		if(sequenceSelectIndex >= agentNum ) {
			sequenceSelectIndex = 0;
		}
		return agent;
	}

	/**
	 * 実行処理を行なうエージェントをランダムに取得します。
	 * @param Agent エージェント
	 */
	private Agent getExecAgentRandomOder() {
		int index = getExecAgentIndex();
		useAgentFlags[index] = true;
		return agents[index];
	}

	/**
	 * 実行するエージェントの配列中のIndexを取得します。
	 * @return int 実行するエージェントの配列中のIndex
	 */
	private int getExecAgentIndex() {
		/* 未使用なエージェントの数を取得 */
		int notUseAgentNum = getNotUseAgentNum();
		/* すべて使用されていれば全てを未使用に設定し全てから選択 */
		if(notUseAgentNum == 0) {
			clearUseFlag();
			notUseAgentNum = getNotUseAgentNum();
		}
		int randomNum = random.nextInt(notUseAgentNum);
		int index = 0;
		int notUseNum = 0;
		for(; index < useAgentFlags.length; index++) {
			if(useAgentFlags[index] == false) {
				if(notUseNum == randomNum) {
					break;
				}
				notUseNum++;
			}
		}
		return index;
	}

	/**
	 * 未使用なエージェントの数を取得します。
	 * @return int 実行処理を行なっていないエージェント数
	 */
	private int getNotUseAgentNum() {
		int num = 0;
		for(int i = 0; i < useAgentFlags.length; i++) {
			if(useAgentFlags[i] == false) {
				num++;
			}
		}
		return num;
	}

	/**
	 * 使用状態に設定されたエージェントを未使用状態に設定します。
	 */
	private void clearUseFlag() {
		for(int i = 0; i < useAgentFlags.length; i++) {
			useAgentFlags[i] = false;
		}
	}

	/**
	 * スタックから到達したゴールを削除します。
	 */
	private void removeReachGoal() {

		if(viewer != null) {
			for(int i = 0; i < removeReachGoalAgents.length; i++) {
				removeReachGoalAgents[i] = false;
			}
		}

		while(true) {
			/* 全てのエージェントが削除できなくなるまで */
			boolean flagRemove = false;
			for(int i = 0; i < agentNum; i++) {
				if(agents[i].removeReachGoal()) {
					// 2001.09.26 追加 
					/* 到達ゴール削除時にツリーも操作 */
					failAgentTree.removeCurrent();
					flagRemove = true;

					/* 削除したエージェントを設定(viewer用) */
					if(viewer != null) {
						removeReachGoalAgents[i] = true;
					}

				}
			}
			if(flagRemove == false) {
				break;
			}
		}
	}

	/**
	 * ツリー上のいづれかの状態に到達したかどうか判定します。
	 * @return boolean true:到達 false:未到達
	 */
	private boolean isReachTreeGoal() {
		for(int i = 0; i < agents.length; i++) {
			boolean b = failAgentTree.isContain(agents[i].AGID, 
			        agents[i].getStateReference());
			if(b == true) {
				return true;
			}
		}
		return false;
	}

	/**
	 * ゴールスタックとツリーの要素をすべてクリアします。
	 */
	private void clearGoalStackAndTree() {
		sharedMemory.removeAllGoal();
		failAgentTree.clear();
	}

	/**
	 * エージェントの学習を行います。
	 * @param boolean flagGoalReach
	 * @param double p
	 */
	private void learn(boolean flagGoalReach, double p) {
		/* 全エージェントの学習処理 */
		for(int i = 0; i < agents.length; i++) {
			agents[i].learn(flagGoalReach, p);
		}
	}


}
