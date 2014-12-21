/**
 * Util.java
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.10
 */
package wba.citta.gsa;

import java.util.*;


/**
 *
 */
public class Util {

	/**
	 * 引数で指定された２つのVectorの有効な要素が同じかどうか判定します。<BR>
	 * ２つのVectorがnullでなく、要素数は同じで、Vectorの有効な要素
	 * (nullでない要素)がすべて等しければtrueを返します。
	 * @param Vector v1
	 * @param Vector v2
	 * @return boolean 
	 */
	public static boolean equalsValidElement(Vector v1, Vector v2) {
		boolean b = true;
		if( (v1 != null) && (v2 != null) && (v1.size() == v2.size())) {
			ListIterator li1 = v1.listIterator();
			ListIterator li2 = v2.listIterator();
			while(li1.hasNext()) {
				Integer elm1 = (Integer)li1.next();
				Integer elm2 = (Integer)li2.next();
				if( (elm1 != null) && (elm2 != null) &&
				        (!elm1.equals(elm2)) ) {
					b = false;
				}
			}
		}else {
			b = false;
		}
		return b;
	}



	////////////////////////////////////////////////////////////////
	// 実行エージェントの選択

	/*  すでに取得された数を設定するbooleanの配列 */
	private boolean[] useIndex;

	/* 乱数のシード */
	private int seed = 1;
	/* 乱数 */
	private Random random = new Random(seed);

	/**
	 * コンストラクタ
	 * @param int num 利用する数
	 */
	public Util(int num) {
		useIndex = new boolean[num];
	}

	/**
	 * ランダムな値を取得します。<BR>
	 * 全て取得済みなら-1を返す。
	 * @return int 
	 */
	public int getRandomNum() {
		return getRandomIndex();
	}

	/**
	 * ランダムに取得した状態をクリアします。
	 */
	public void reset() {
		for(int i = 0; i < useIndex.length; i++) {
			useIndex[i] = false;
		}
	}

	/**
	 * 未使用なindexの数を取得します。
	 * @return int 実行処理を行なっていないエージェント数
	 */
	public int getNotUseNum() {
		int num = 0;
		for(int i = 0; i < useIndex.length; i++) {
			if(useIndex[i] == false) {
				num++;
			}
		}
		return num;
	}


	/**
	 * 実行するエージェントの配列中のIndexを取得します。
	 * @return int 実行するエージェントの配列中のIndex
	 */
	private int getRandomIndex() {
		/* 未使用な数を取得 */
		int notUseNum = getNotUseNum();

		/* すべて使用されていれば全てを未使用に設定し全てから選択 */
		if(notUseNum == 0) {
//			clearUseFlag();
//			notUseAgentNum = getNotUseAgentNum();
			return -1;
		}

		int randomNum = random.nextInt(notUseNum);

		int index = 0;
		int falseNum = 0;
		for(; index < useIndex.length; index++) {
			if(useIndex[index] == false) {
				if(falseNum == randomNum) {
					break;
				}
				falseNum++;
			}
		}
		return index;
	}

}
