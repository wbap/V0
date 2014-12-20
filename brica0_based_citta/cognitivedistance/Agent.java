/**
 * Agent.java
 * 擣抦嫍棧偵傛傞栤戣夝寛傪峴側偆僋儔僗
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * 2000.10 BSC miyamoto
 */
package cognitivedistance;

import java.util.*;
import java.io.*;
import cognitivedistance.viewer.*;

/**
 * 擣抦嫍棧偵傛傞栤戣夝寛傪峴側偆僋儔僗偱偡丅<BR><BR>
 * 亙僐儞僗僩儔僋僞偱愝掕壜擻側僷儔儊乕僞偺僨僼僅儖僩抣亜<BR>
 *  maxCDLngth                 10<BR>
 *  shallowSearchLngth          3<BR>
 *  deepSearchLngth           200<BR>
 *  minSearchLngth              2<BR>
 *  maxSegmentSize              5<BR>
 *  minSegmentSize              3(尰嵼偼婡擻偟傑偣傫)<BR>
 *  maxFamiliarCount           10<BR>
 *  flagNovelSearch          true<BR>
 *  flagLandmarkSearchDirection false<BR>
 */
public class Agent {

	/* 娐嫬偲偺僀儞僞乕僼僃乕僗偺曄姺傪峴側偆僋儔僗 */
//	private InterfaceAgent interfaceAgent;
	InterfaceAgent interfaceAgent;

	/* LayeredAgent偺攝楍 */
	private LayeredAgent[] layeredAgentArray;

	/* 巊梡偡傞LayeredAgent悢 */
	private int layerNum;

	/* 怴婯扵嶕偺桳柍偺愗傝姺偊梡僼儔僌 */
	private boolean flagNovelSearch = true;

	//////////////////////////////////////////////////////////////////
	// 僐儞僗僩儔僋僞丄弶婜壔張棟

	/**
	 * 僐儞僗僩儔僋僞
	 * @param int layerNum 擣抦嫍棧儌僕儏乕儖傪奒憌壔偟偰巊梡偡傞応崌偺
	 * 儗僀儎悢丅扨憌偱偺巊梡偡傞応崌偼侾傪巜掕丅
	 */
	public Agent(int layerNum) {
		this.layerNum = layerNum;
		initAgent();
	}


	/**
	 * 僐儞僗僩儔僋僞
	 * @param int layerNum  擣抦嫍棧儌僕儏乕儖傪奒憌壔偟偰巊梡偡傞応崌偺
	 * 儗僀儎悢丅扨憌偱偺巊梡偡傞応崌偼侾傪巜掕丅
	 * @param int maxCDLngth  妛廗偡傞嵟戝偺擣抦嫍棧
	 * @param int shallowSearchLngth  僑乕儖傪愺偔扵嶕偡傞応崌偺嵟戝偺怺偝
	 * @param int deepSearchLngth  僑乕儖傪怺偔扵嶕偡傞応崌偺嵟戝偺怺偝
	 * @param int minSearchLngth  僑乕儖傪扵嶕偡傞嵟彫偺怺偝丅-1偑巜掕偝傟偨
	 * 応崌偼妋棪揑偵扵嶕嵟彫偺怺偝傪曄壔偝偣傞丅怺偝偼1񑓛�4偺偄偯傟偐偱丄
	 * 弴偵8:4:2:1偺妱崌偱慖戰偝傟傞丅
	 * @param int maxSegmentSize  儔儞僪儅乕僋娫偺嵟戝嫍棧丅偙偙偱巜掕偝傟偨
	 * 嫍棧偺斖埻偱儔儞僪儅乕僋傪扵嶕偟丄儔儞僪儅乕僋偑柍偗傟偽怴偨側儔儞僪
	 * 儅乕僋傪惗惉偟傑偡丅
	 * @param int minSegmentSize  儔儞僪儅乕僋娫偺嵟彫嫍棧(尰嵼偼婡擻偟傑偣傫)
	 */
	public Agent(int layerNum, int maxCDLngth, int shallowSearchLngth,
	        int deepSearchLngth, int minSearchLngth, int maxSegmentSize,
	        int minSegmentSize) {
		this.layerNum = layerNum;
		Node.maxCDLngth = maxCDLngth;
		LayeredAgent.shallowSearchLngth = shallowSearchLngth;
		LayeredAgent.deepSearchLngth = deepSearchLngth;
		LayeredAgent.minSearchLngth = minSearchLngth;
		LayeredAgent.maxSegmentSize = maxSegmentSize;
		LayeredAgent.minSegmentSize = minSegmentSize;
		initAgent();
	}

	/**
	 * 僐儞僗僩儔僋僞
	 * @param int layerNum  擣抦嫍棧儌僕儏乕儖傪奒憌壔偟偰巊梡偡傞応崌偺
	 * 儗僀儎悢丅扨憌偱偺巊梡偡傞応崌偼侾傪巜掕丅
	 * @param int maxCDLngth  妛廗偡傞嵟戝偺擣抦嫍棧
	 * @param int shallowSearchLngth  僑乕儖傪愺偔扵嶕偡傞応崌偺嵟戝偺怺偝
	 * @param int deepSearchLngth  僑乕儖傪怺偔扵嶕偡傞応崌偺嵟戝偺怺偝
	 * @param int minSearchLngth  僑乕儖傪扵嶕偡傞嵟彫偺怺偝丅-1偑巜掕偝傟偨
	 * 応崌偼妋棪揑偵扵嶕嵟彫偺怺偝傪曄壔偝偣傞丅怺偝偼1񑓛�4偺偄偯傟偐偱丄
	 * 弴偵8:4:2:1偺妱崌偱慖戰偝傟傞丅
	 * @param int maxSegmentSize  儔儞僪儅乕僋娫偺嵟戝嫍棧丅偙偙偱巜掕偝傟偨
	 * 嫍棧偺斖埻偱儔儞僪儅乕僋傪扵嶕偟丄儔儞僪儅乕僋偑柍偗傟偽怴偨側儔儞僪
	 * 儅乕僋傪惗惉偟傑偡丅
	 * @param int minSegmentSize  儔儞僪儅乕僋娫偺嵟彫嫍棧(尰嵼偼婡擻偟傑偣傫)
	 * @param boolean flagNovelSearch  怴婯扵嶕張棟傪峴側偆偐偳偆偐丅
	 * true:峴側偆 false:峴側傢側偄
	 * @param int maxFamiliarCount 偙偙偱巜掕偝傟偨夞悢楢懕偟偰丄偡偱偵堏摦
	 * 嵪傒偺忬懺傊堏摦偡傞偲丄怴婯扵嶕張棟偑峴傢傟傑偡丅
	 * @param boolean flagLandmarkSearchDirection  僙僌儊儞僩壔傪峴側偆偨傔偵
	 * 峴側偆儔儞僪儅乕僋偺扵嶕偺岦偒丅 true:弴曽岦 false:媡曽岦
	 */
	public Agent(int layerNum, int maxCDLngth, int shallowSearchLngth,
	        int deepSearchLngth, int minSearchLngth, int maxSegmentSize,
	        int minSegmentSize, boolean flagNovelSearch, int maxFamiliarCount,
	        boolean flagLandmarkSearchDirection) {
		this(layerNum, maxCDLngth, shallowSearchLngth, deepSearchLngth,
		        minSearchLngth, maxSegmentSize, minSegmentSize);
		this.flagNovelSearch = flagNovelSearch;
		LayeredAgent.maxFamiliarCount = maxFamiliarCount;
		LayeredAgent.flagLandmarkSearchDirection = flagLandmarkSearchDirection;
	}

	/**
	 * Agent僋儔僗偺弶婜壔張棟丅
	 * LayeredAgent,InterfaceAgent傪惗惉偟傑偡丅
	 */
	private void initAgent() {

		/*
		 * LayeredAgent偺惗惉 忋埵憌傊偺嶲徠傪壓埵憌偺僐儞僗僩儔僋僞偵愝掕
		 */
		layeredAgentArray = new LayeredAgent[layerNum];
		for(int i = layerNum-1; i >= 0; i--) {
			if(i == layerNum-1) {
				/* 嵟忋埵偺憌偵偼忋埵憌傊偺嶲徠傪愝掕偟側偄 */
				layeredAgentArray[i] = new LayeredAgent(null, i);
			}else{
				/* 偦傟埲奜偺憌偵偼忋埵憌傊偺嶲徠傪愝掕 */
				layeredAgentArray[i] = new LayeredAgent(layeredAgentArray[i+1],
				        i);
			}
		}

		/* 僀儞僞乕僼僃乕僗僄乕僕僃儞僩偵偼嵟壓憌偺layeredAgentArray傪愝掕 */
		interfaceAgent = new InterfaceAgent(layeredAgentArray[0]);
	}


	//////////////////////////////////////////////////////////////////
	// public

	/**
	 * 尰嵼偺忬懺偐傜僑乕儖偺傊宱楬扵嶕傪峴側偄丄僑乕儖傊堏摦偡傞偨傔偺
	 * 師偺忬懺傪庢摼偟傑偡丅
	 * 僑乕儖傊偺宱楬偑尒偮偐傜側偄応崌偼null傪曉偟傑偡丅
	 * 傑偨堷悢偱愝掕偝傟偨尰嵼偺忬懺偵偮偄偰偺擣抦嫍棧丒ForwardModel丒
	 * InverseMovel偺妛廗傪峴側偄丄奒憌壔偝傟偰偄傟偽僙僌儊儞僩壔傕峴側偄傑偡丅
	 * @param Vector currentState 尰嵼偺忬懺
	 * @param Vector goalState    僑乕儖偺忬懺
	 * @return Vector             師偺忬懺
	 * @exception NullPointerException 堷悢偱愝掕偝傟偨尰嵼偺忬懺偑null偺応崌
	 * @exception ElementNumberException 尰嵼偺忬懺偺梫慺悢(Vector偺僒僀僘)偑
	 * 晄惓側応崌丅忬懺偺梫慺悢偼巒傔偵擖椡偝傟偨忬懺偺梫慺悢偑婎弨偲側傝丄
	 * 埲崀偵擖椡偝傟傞忬懺偺梫慺悢偼婎弨偲側傞梫慺悢偲摨偠偱側偗傟偽側傝傑偣傫
	 */
	public Vector getNextState(Vector currentState, Vector goalState) 
	        throws ElementNumberException {

		/* 妛廗張棟 */
		interfaceAgent.learn(currentState);

		/* 師偺忬懺傪庢摼 */
		Vector nextState = interfaceAgent.exec(currentState,goalState);

		/* 怴婯扵嶕張棟 僼儔僌偵傛傝怴婯扵嶕偺桳柍偺愗傝姺偊 */
		if(flagNovelSearch) {
			if(nextState == null) {
				nextState = interfaceAgent.novelSearch(currentState);
			}else {
				interfaceAgent.counterReset();
			}
		}

		return nextState;
	}

	// 2001.04.19 捛壛 miyamoto
	/**
	 * 擣抦嫍棧偺妛廗傪峴側偄傑偡丅
	 * @param Vector currentState 尰嵼偺忬懺
	 * @param Exception NullPointerException 尰嵼偺忬懺偑null偺応崌
	 * @param Exception ElementNumberException 尰嵼偺忬懺偺梫慺悢偑晄惓側応崌
	 */
	public void learn(Vector currentState) throws ElementNumberException {
		interfaceAgent.learn(currentState);
	}

	/**
	 * 幚峴張棟傪峴側偄丄僑乕儖傊堏摦偡傞偨傔偺師偺忬懺傪庢摼偟傑偡丅
	 * @param Vector currentState 尰嵼偺忬懺
	 * @param Vector goalState    僑乕儖偺忬懺
	 * @return Vector             師偺忬懺
	 * @param Exception NullPointerException 尰嵼偺忬懺偑null偺応崌
	 * @param Exception ElementNumberException 尰嵼偺忬懺偺梫慺悢偑晄惓側応崌
	 */
	public Vector exec(Vector currentState, Vector goalState)
	        throws ElementNumberException {
		return interfaceAgent.exec(currentState, goalState);
	}
	// 偙偙傑偱

	/**
	 * 妛廗僨乕僞傪僼傽僀儖偐傜撉崬傒傑偡丅
	 * @param String fileName 僼傽僀儖柤
	 */
	public void load(String fileName) {
		System.out.println("Loading learning data....");
		try{
			FileInputStream istream = new FileInputStream(fileName);
			ObjectInputStream oInputStream = new ObjectInputStream(istream);

			/* 僆僽僕僃僋僩偺撉崬傒 */
			interfaceAgent.load(oInputStream);
			for(int i = 0; i < layerNum; i++) {
				layeredAgentArray[i].load(oInputStream);
			}

			oInputStream.close();
			istream.close();

		}catch(Exception e){
			System.out.println(e);
			System.exit(0);
		}
	}


	/**
	 * 妛廗僨乕僞傪僼傽僀儖偵曐懚偟傑偡丅
	 * @param String fileName 僼傽僀儖柤
	 */
	public void save(String fileName) {
		System.out.println("Saving learning data....");
		try{
			/* 僗僩儕乕儉偺嶌惉 */
			FileOutputStream ostream = new FileOutputStream(fileName, false);
			ObjectOutputStream oOutputStream = new ObjectOutputStream(ostream);

			/* 僆僽僕僃僋僩偺撉崬傒 */
			interfaceAgent.save(oOutputStream);
			for(int i = 0; i < layerNum; i++) {
				layeredAgentArray[i].save(oOutputStream);
			}

			oOutputStream.flush();

			oOutputStream.close();
			ostream.close();

		}catch(Exception e){
			System.out.println(e);
		}

	}


	///////////////////////////////////////////////////////////////////
	// 僨僶僢僋梡偺忣曬偺庢摼丄摦嶌偺惂屼偵巊梡偡傞儊僜僢僪

	/**
	 * 擣抦嫍棧傪妛廗偡傞偨傔偵曐帩偟偰偄傞忬懺偺棜楌傪僋儕傾偟傑偡丅
	 */
	public void reset() {
		interfaceAgent.reset();
	}


	/**
	 * 擣抦嫍棧丒ForwardModel丒InverseMovel偺妛廗傪峴側偆偐丄峴側傢側偄偐
	 * 愝掕偟傑偡丅僨僼僅儖僩偱偼慡偰偺憌偺妛廗傪峴側偄傑偡丅
	 * @param int layerID 愝掕偡傞儗僀儎 (0乣)
	 * @param boolean flag  true丗妛廗傪峴側偆  false丗妛廗傪峴側傢側偄
	 */
	public void setLearningFlag(int layerID, boolean flag) {
		layeredAgentArray[layerID].setLearningFlag(flag);
	}


	/**
	 * 僙僌儊儞僩壔(僙僌儊儞僩暘妱丄僙僌儊儞僩偺儔儞僪儅乕僋偺愝掕)傪峴側偆偐
	 * 丄峴側傢側偄偐愝掕偟傑偡丅僨僼僅儖僩偱偼慡偰偺憌偺僙僌儊儞僩壔傪峴側偄
	 * 傑偡丅
	 * @param int layerID 愝掕偡傞儗僀儎 (0乣)
	 * @param boolean flag  true丗妛廗傪峴側偆  false丗妛廗傪峴側傢側偄
	 */
	public void setSegmentationFlag(int layerID, boolean flag) {
		layeredAgentArray[layerID].setSegmentationFlag(flag);
	}


	/**
	 * 巊梡偡傞俠俢偺嵟戝僒僀僘傪曄峏偟傑偡丅
	 * (僥僗僩梡偺儊僜僢僪偱stateBuffer傊偺塭嬁偼峫椂偟偰偄側偄乯
	 * @param int lngth 怴偟偄挿偝
	 */
//	public void changeMaxCDLngth(int lngth) {
//		Node.maxCDLngth = lngth;
//	}


	/**
	 * 巊梡偡傞儗僀儎悢傪曄峏偟傑偡丅(儗僀儎悢傪尭傜偡帠偺傒壜擻乯
	 * @param int newLayerNum  怴偟偄儗僀儎悢
	 */
//	public void changeLayerNum(int newLayerNum) {
//		if(newLayerNum < layerNum) {
//			/* LayeredAgent偺攝楍偺撪巊梡偟側偄傕偺傕嶍彍 */
//			for(int i = 0; i < layerNum; layerNum++) {
//				if(i < newLayerNum) {
//				}else {
//					layeredAgentArray[i] = null;
//				}
//			}
//			/* 怴偟偄嵟忋埵憌偺忋埵憌傪嶍彍 */
//			layeredAgentArray[newLayerNum-1].deleteUpperLayer();
//			layerNum = newLayerNum;
//		}
//	}


	/**
	 * 巜掕偝傟偨忬懺丒儗僀儎偵懳墳偡傞僲乕僪僋儔僗偺僆僽僕僃僋僩傪庢摼偟傑偡丅
	 * @param Object state 娐嫬懁偱偺忬懺
	 * @param int layer    儗僀儎
	 * @return Node        僲乕僪
	 */
	public Node getNode(Vector state, int layer) {
		return interfaceAgent.getNode(state, layer);
	}


	/**
	 * 峴摦嵪傒偺慡忬懺傪庢摼偟傑偡丅
	 * @return Vector 峴摦嵪傒偺慡忬懺偺愝掕偝傟偨Vector
	 */
	public Vector getStateTable() {
		return interfaceAgent.getIdToState();
	}


	/**
	 * 堷悢偱巜掕偟偨憌偺幚峴帪偺張棟偵娭偡傞忣曬傪庢摼偟傑偡丅
	 * @param int layerNum  忣曬傪庢摼偡傞儗僀儎
	 * @return int[]        幚峴帪偺張棟偵娭偡傞忣曬<BR>
	 *                      抣偺側偄忬懺偵偮偄偰偼-1偑愝掕偝傟傞<BR>
	 *                      int[0] 尰嵼偺忬懺偺ID<BR>
	 *                      int[1] 僑乕儖偺忬懺偺ID<BR>
	 *                      int[2] 忋埵憌偐傜偺僒僽僑乕儖偺忬懺偺ID<BR>
	 *                      int[3] 師偺忬懺偺ID<BR>
	 *                      int[4] 師偺忬懺傪弌椡偟偰偄傞張棟偺ID<BR>
	 *                      int[5] 僒僽僑乕儖偑峏怴偝傟偰偄傞偐
	 *                             0丗峏怴偝傟偰偄側偄 1丗峏怴偝傟偰偄傞<BR>
	 */
	public int[] getExecInfo(int layerNum) {
		ExecInfo ei = layeredAgentArray[layerNum].getExecInfo();
		return ei.getExecInfo();
	}

	/**
	 * 慡偰偺憌偺幚峴帪偺張棟偵娭偡傞忣曬傪僋儕傾偟傑偡丅<BR>
	 * 奺憌偺幚峴帪偺張棟偵娭偡傞忣曬偼僑乕儖偑柍偔側偭偨応崌摍丄偦偺憌偺張棟偑
	 * 峴傢傟側偔側傞偨傔丄埲慜偺忣曬偑巆偭偰偟傑偄傑偡丅
	 * 偙偺偨傔昁梫偵墳偠偰忣曬傪僋儕傾偟傑偡丅
	 */
//	public void resetExecInfo() {
//		for(int i = 0; i < layerNum; i++) {
//			layeredAgentArray[i].resetExecInfo();
//		}
//	}

	/**
	 * 堷悢偱巜掕偟偨憌偺僑乕儖扵嶕偵娭偡傞忣曬傪庢摼偟傑偡丅
	 * @param int layerNum 忣曬傪庢摼偡傞儗僀儎
	 * @param int dx       忣曬傪庢摼偡傞張棟 D1乣D4 (0乣3偱巜掕)
	 * @return int[]       僑乕儖偺扵嶕偵娭偡傞忣曬偑愝掕偝傟偨攝楍<BR>
	 *                     巜掕偝傟偨張棟偑峴側傢傟偰偄側偄応崌偼null傪曉偡<BR>
	 *                     int[0] 扵嶕偝傟偨僲乕僪偺ID
	 *                            扵嶕偺寢壥尒偮偐傜側偐偭偨応崌-1<BR>
	 *                     int[1] 扵嶕偝傟偨僲乕僪偐傜僑乕儖傑偱偺CD偺挿偝
	 *                            扵嶕偺寢壥尒偮偐傜側偐偭偨応崌-1<BR>
	 *                     int[2] 扵嶕傟偨怺偝<BR>
	 *                     int[3] 扵嶕偝傟偨忬懺悢<BR>
	 */
//	public int[] getGoalSearchInfo(int layerNum, int dx) {
//		GoalSearchInfo gsi = layeredAgentArray[layerNum].getGoalSearchInfo();
//		return gsi.getGoalSearchInfo(dx);
//	}


	/**
	 * 堷悢偱巜掕偟偨憌偺妛廗忬嫷偵娭偡傞忣曬傪庢摼偟傑偡丅
	 * @param int layerNum  忣曬傪庢摼偡傞儗僀儎
	 * @return int[]        妛廗忬嫷偵娭偡傞忣曬<BR>
	 *                      int[0] 慡忬懺偺MoveableState偺僒僀僘偺崌寁<BR>
	 *                      int[1] 慡忬懺偺CognitiveDistance偺僒僀僘偺崌寁<BR>
	 *                      int[2] 慡忬懺悢<BR>
	 *                      int[3] 桳岠側忬懺悢(儔儞僪儅乕僋偺嶍彍帪偵塭嬁)<BR>
	 */
//	public int[] getLearningInfo(int layerNum) {
//		return layeredAgentArray[layerNum].getLearningInfo();
//	}

	// 2001.08.09 捛壛 miyamoto
	/**
	 * 嵟彫偱扵嶕傪峴偆怺偝傪庢摼偟傑偡丅
	 * @return int 嵟彫偱扵嶕傪峴偆怺偝
	 */
	public int getMinSearchLngth() {
		return LayeredAgent.minSearchLngth;
	}

	// 2001.08.09 捛壛 miyamoto
	/**
	 * 嵟彫偱扵嶕傪峴偆怺偝傪愝掕偟傑偡丅
	 * @param int minSearchLngth 嵟彫偱扵嶕傪峴偆怺偝
	 */
	public void setMinSearchLngth(int minSearchLngth) {
		LayeredAgent.minSearchLngth = minSearchLngth;
	}


	// 2001.08.14 捛壛 miyamoto
	/**
	 * 忬懺 a 偐傜忬懺 b 傊偺摓払壜擻惈傪挷傋傑偡丅
	 * @param Vector a
	 * @param Vector b
	 * @return boolean true 摓払壜擻 false 摓払晄壜擻
	 */
	public boolean isReach(Vector a, Vector b) {
		return interfaceAgent.isReach(a, b);
	}

	// 2001.08.15 捛壛 miyamoto
	/**
	 * 奺憌偱曐帩偟偰偄傞丄埲慜偺忬懺丒僑乕儖偵娭偡傞忣曬傪僋儕傾偟傑偡丅
	 */
	public void resetOldValue() {
		interfaceAgent.resetOldValue();
	}


}
