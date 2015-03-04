/**
 * AssociateAgent.java
 * 連想処理を行なうGSAのエージェント
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.07
 */
package wba.citta.gsa;

import java.util.*;
import java.io.*;

/**
 * 連想処理を行なうGSAのエージェント
 */
public class AssociateAgent extends Agent{

	/* 経験した状態を保持するテーブル */
	private LinkedList stateList = null;
	private Hashtable stateTable = null;

	/* 連想するためのキーとその評価値を保持するテーブル */
	private Hashtable profitTable = null;

	/* 連想するためのキーの遷移を保持するテーブル */
	private LinkedList keyBuffer = null;


	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * コンストラクタ
	 * @param int agid  エージェントID
	 * @param boolean[] useNode  ノードの使用、不使用を設定した配列
	 * @param SharedMemory sharedMemory  state・goalを管理する共有メモリ
	 */
	public AssociateAgent(int agid, boolean[] useNode,
	         SharedMemory sharedMemory) {
		super(agid, useNode, sharedMemory);

		stateList = new LinkedList();
		stateTable = new Hashtable();
		profitTable = new Hashtable();
		keyBuffer = new LinkedList();
	}


	//////////////////////////////////////////////////////////////////////////
	// public

	/**
	 * エージェント固有の学習処理を行ないます。<BR>
	 * @param Vector state 現在の状態
	 * @param boolean flagGoalReach ゴールへの到達を表すフラグ
	 * @param double profit 報酬
	 */
	public void learn(Vector state, boolean flagGoalReach, double profit) {
		stateLearning(state);
		if(flagGoalReach) {
//			profitLearning();
			profitLearning(profit);
		}
	}


	/**
	 * エージェント固有の実行処理を行ないます。<BR>
	 * @param Vector state 現在の状態
	 * @param Vector goalElementArray GoalStackElementのVector
	 * @return Vector サブゴール
	 */
	public Vector execProcess(Vector state, Vector goalElement) {

		/* 設定エージェントごとに分解 */
		Vector[] subsetGoalsElement = getSubsetGoals(goalElement);

		/* キーを選択 */
		Vector selectedKeyElement
		        =  selectKey(goalElement, subsetGoalsElement);

		/* キーに対応する状態を連想 */
		Vector selectedKeyValue = getGoalValueArray(selectedKeyElement);
		Vector subgoal = associateSubgoal(selectedKeyValue);

		/* 連想に使用したキーをスタックから削除 */
//		remove(selectedKeyElement);

		return subgoal;
	}

	/**
	 * 学習結果をファイルに保存します。
	 * @param String fileNameファイル名
	 */
	public void save(String fileName) {
		System.out.println("Saving learning data....");
		try{
			/* ストリームの作成 */
			FileOutputStream ostream = new FileOutputStream(fileName, false);
			ObjectOutputStream oOutputStream = new ObjectOutputStream(ostream);

			/* オブジェクトの書き込み */
			oOutputStream.writeObject(stateList);
//			oOutputStream.writeObject(profitTable);

			oOutputStream.flush();

			oOutputStream.close();
			ostream.close();

		}catch(Exception e){
			System.out.println(e);
		}
	}


	/**
	 * 学習結果をファイルから読み込みます。
	 * @param String fileName ファイル名
	 */
	public void load(String fileName) {
		System.out.println("Loading learning data....");
		try{
			/* ストリームの作成 */
			FileInputStream istream = new FileInputStream(fileName);
			ObjectInputStream oInputStream = new ObjectInputStream(istream);

			/* オブジェクトの書き込み */
			stateList = (LinkedList)oInputStream.readObject();
//			profitTable = (Vector)oInputStream.readObject();

			oInputStream.close();
			istream.close();

		}catch(Exception e){
			System.out.println(e);
		}

		/* stateListの要素をstateTableに設定 */
		ListIterator listIterator = stateList.listIterator();
		while(listIterator.hasNext()) {
			Vector state = (Vector)listIterator.next();
			stateTable.put(state, state);
		}
	}

	/**
	 * GSAクラスのreset()メソッドから呼び出されます。<BR>
	 * 連想のキーとして使用したものの履歴をクリアします。<BR>
	 */
	public void reset() {
		keyBuffer.clear();
	}

	/**
	 * Agentクラスを継承して作成しているため、形式的に実装<BR>
	 */
	public void suspend() {
	}


	/////////////////////////////////////////////////////////////
	// private

	/**
	 * 状態の学習を行います。
	 */
	private void stateLearning(Vector state) {
		if(stateTable.get(state) == null) {
			stateList.add(state);
			/* 同じ状態を複数設定しないためのテーブル */
			stateTable.put(state, state);
		}
	}


	/**
	 * 引数で指定されたゴールを設定したエージェント毎に分解。
	 * 設定エージェント毎のゴールとしてVectorの配列で取得します。
	 * @param Vector goal
	 * @return Vector[] エージェント毎のゴール配列
	 */
	private Vector[] getSubsetGoals(Vector goal) {

		if(goal == null) {
			return null;
		}

		int[] agids = getAgids(goal);

		/* エージェントID毎のVectorに分解 */
		Vector[] subsetGoals = new Vector[agids.length];
		for(int i = 0; i < agids.length; i++) {
			subsetGoals[i] = new Vector();
			for(int m = 0; m < goal.size(); m++) {
				GoalStackElement e = (GoalStackElement)goal.get(m);
				if( (e != null) && (e.agid == agids[i]) ) {
					subsetGoals[i].add(e);
				}else {
					subsetGoals[i].add(null);
				}
			}
		}

		return subsetGoals;
	}


	/**
	 * 引数のgoalから全エージェントIDを取得します。
	 * @return int[] エージェントIDの配列
	 */
	private int[] getAgids(Vector goal) {
		Hashtable agidTable = new Hashtable();
		Vector agidArray = new Vector();
		for(int i = 0; i < goal.size(); i++) {
			GoalStackElement e = (GoalStackElement)goal.get(i);
			if( e != null ) {
				Integer integer = (Integer)agidTable.get(new Integer(e.agid));
				if(integer == null) {
					agidTable.put(new Integer(e.agid),new Integer(e.agid));
					agidArray.add(new Integer(e.agid));
				}
			}
		}
		int[] agids = new int[agidArray.size()];
		for(int i = 0; i < agidArray.size(); i++) {
			agids[i] = ((Integer)agidArray.get(i)).intValue();
		}
		return agids;
	}


	/* 評価値が同じ場合ランダムにキーを選択するための乱数 */
	private Random random = new Random(0);
	/**
	 * 引数で設定された複数の部分ゴールからある部分ゴールを選択します。
	 * @param Vector   Element
	 * @param Vector[] Element
	 * @return Vector  Element
	 */
	private Vector selectKey(Vector goalElement, Vector[] subsetGoalsElement) {

		Vector selectedSubset = null;

		if(subsetGoalsElement != null) {

			Vector goalValue = getGoalValueArray(goalElement);

			// 無出力をありにするかどうか？
			int loopNum = subsetGoalsElement.length + 1;
//			int loopNum = subsetGoalsElement.length;

			double[] profits = new double[loopNum];

			for(int i = 0; i < loopNum; i++) {

				/* 評価値が設定されている型を作成 無出力についても取得 */
				Vector subsetGoalValue = null;
				if(i < subsetGoalsElement.length) {
					subsetGoalValue = getGoalValueArray(subsetGoalsElement[i]);
				}
				Vector obj = new Vector();
				obj.add(goalValue);
				obj.add(subsetGoalValue);

				/* 各評価値を配列に設定 */
				Double profitD = (Double)profitTable.get(obj);
				if(profitD != null) {
					profits[i] = profitD.doubleValue();
				}else{
					// 初期値５０
					profits[i] = 10;
				}
			}

			/* 評価値が最大のものを選択 */
//			int index = selectMaxProfit(profits);
			/* 評価値が高いものを高確率で選択 */
			int index = selectProfit(profits);
//			int index = selectProfitExp(profits);

			if(index < subsetGoalsElement.length) {
				selectedSubset = subsetGoalsElement[index];
			}

			/* 選択されたキーを履歴に登録 */
			Vector selectedSubsetValue = getGoalValueArray(selectedSubset);
			Vector obj = new Vector();
			obj.add(goalValue);
			obj.add(selectedSubsetValue);

			keyBuffer.add(obj);
		}

		return selectedSubset;
	}

	/**
	 * 引数の評価値の配列から最大の評価値を持つインデックスを取得します。
	 */
	private int selectMaxProfit(double[] profits) {
		/* 最大の評価値のものを選択 */
		int sameElmNum = 0;
		int[] sameElms = new int[profits.length];
		double selectedProfit = -1;
		for(int i = 0; i < profits.length; i++) {
			if(profits[i] > selectedProfit) {
				selectedProfit = profits[i];
				sameElms = new int[profits.length];
				sameElmNum = 0;
			}
			sameElms[sameElmNum] = i;
			sameElmNum++;
		}
		int randomValue = random.nextInt(sameElmNum);
		return sameElms[randomValue];
	}


	/**
	 * 引数の評価値の配列から確率的に評価値を選択しそのインデックスを
	 * 取得します。
	 * 評価値は高いものほど高確率で選択されます。
	 */
	Random randomKeySelect = new Random(0);
	private int selectProfit(double[] profits) {

		// 評価値の合計
		double totalProfit = 0;
		for(int i = 0; i < profits.length; i++) {
			totalProfit += profits[i];
		}

		int r = randomKeySelect.nextInt(100);

System.out.println(" total  " + totalProfit);
System.out.println(" random " + r);

		double d = 0;
		int index = 0;
		for(; index < profits.length; index++) {
System.out.println(" profit " + index + " " +  profits[index]);
			d += profits[index] / totalProfit * 100;
			if(d > r) {
				break;
			}
		}

System.out.println(" select index " + index );

		return index;
	}

	private final double T = 0.999;
	/**
	 * 引数の評価値の配列から確率的に評価値を選択しそのインデックスを
	 * 取得します。
	 * 評価値は高いものほど高確率で選択されます。
	 */
	private int selectProfitExp(double[] profits) {

System.out.println(" ####################  ");
		// 評価値の合計
		double totalProfitExp = 0;
		for(int i = 0; i < profits.length; i++) {
			totalProfitExp += Math.exp(profits[i]/T);
System.out.println("   profit " + profits[i]);
System.out.println("   exp    " + Math.exp(profits[i]/T));
		}

		int r = randomKeySelect.nextInt(100);

System.out.println(" ########## select key ##########  ");
System.out.println("  total exp " + totalProfitExp);
System.out.println("  random " + r);

		double d = 0;
		int index = 0;
		for(; index < profits.length; index++) {
System.out.println("  index " + index);
System.out.println("   profit " + profits[index]);
			d += Math.exp(profits[index]/T) / totalProfitExp * 100;
System.out.println("   exp    " + Math.exp(profits[index]/T));
			if(d > r) {
				break;
			}
		}

System.out.println(" select index " + index );

		return index;
	}


	/**
	 * 引数で指定されたキーから経験済みの状態を連想
	 * @param Vector key 連想するキー(value)
	 * @return Vector    連想された状態
	 */
	private Vector associateSubgoal(Vector key) {
		ListIterator listIterator = stateList.listIterator();
		while(listIterator.hasNext()) {
			Vector state = (Vector)listIterator.next();
//			if( isEqualValidElement(key, state) ) {
			if( Util.equalsValidElement(key, state) ) {
				return state;
			}
		}
		return null;
	}

	/**
	 * 引数で指定された要素と、その要素を同じエージェントに設定されている
	 * 要素をスタックから削除します。
	 */
//	private void remove(Vector keyElement) {
//
//		if(keyElement == null) {
//			return;
//		}
//
//		for(int i = 0; ;i++) {
//			Element e = (Element)keyElement.get(i);
//			if(e != null) {
//				removeGoalAtAgid(e.agid);
//				break;
//			}
//		}
//	}


	///////////////////////////////////////////////////////////////////
	// 

	/* 定数 */
	private final double REWARD = 100; /* 報酬 */
	private final double GAMMA = 0.9;
	private final double BETA = 0.1;
	/**
	 * 評価値の学習を行います。
	 */
	private void profitLearning() {
		profitLearning(REWARD);
	}

	/**
	 * 評価値の学習を行います。
	 */
	private void profitLearning(double reward) {

		Hashtable checkTable = new Hashtable();

		ListIterator li = keyBuffer.listIterator(keyBuffer.size());
		int i = 0;
		while(li.hasPrevious()) {
			Vector key = (Vector)li.previous();

Integer count = (Integer)keyCountTable.get(key);
int newCount = 1;
if(count != null) {
	newCount = count.intValue() + 1;
}
keyCountTable.put(key, new Integer(newCount));

			/* テーブルに同じ状態があればプロフィットの更新を行なわない */
			Vector v = (Vector)checkTable.get(key);
			if(v == null) {
				checkTable.put(key, key);
				Double profitD = (Double)profitTable.get(key);
				double profit = 0;
				if(profitD != null) {
					profit = profitD.doubleValue();
				}else {
					// 初期値５０
					profit = 10;
				}
				/* ΔS = β(γ^nP(t) - Si(t)) */
				profit = profit + BETA*(Math.pow(GAMMA,i)*reward - profit);
				profitTable.put(key, new Double(profit));
			}
			i++;
		}

if(reward <= 0) {
	zeroRewardCount++;
}
		// 全テーブルの状況を表示
		printProfitTable();
	}


	private Hashtable keyCountTable = new Hashtable();
	

	/**
	 * 評価値のテーブルの状態を出力。
	 */
	private int zeroRewardCount = 0;
	private int count = 0;
	private void printProfitTable() {
		count++;
		Enumeration e = profitTable.keys();
		System.out.println(" ** Value Table ** ");
		System.out.println("  count " + count);
		System.out.println("  zero reward count " + zeroRewardCount);
		while( e.hasMoreElements() ) { 
			Vector key = (Vector)e.nextElement();
			Double d = (Double)profitTable.get(key);
			Integer count = (Integer)keyCountTable.get(key);
			System.out.println("      key: " + key.get(0)  + ":" + key.get(1)
				       +  " profit:" + d.doubleValue() + " count:" + count );
		}
if(count == 100) {
	while(true) {
	}
}
	}


}
