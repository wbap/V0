/**
 * EnvironmentAgent.java
 * 
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.07
 */
package wba.citta.doorkeydemo;

import wba.citta.environment.*;

import java.util.*;

/**
 * 
 */
public class EnvironmentAgent {

	/*  */
	private Environment environment = null;

	/*  */
	private String fileName = null;
	/*  */
	private int doorOpenMode = 1;

	/*  */
	private Random randomMoveAction = new Random(0);
	/*  */
	private Random randomStart = new Random(1);
	/*  */
	private Random randomItem = new Random(0);

	/*  */
	LinkedList actionList = new LinkedList();

	//////////////////////////////////////////////////
	// 

	/**
	 * 
	 * @param String fileName 
	 * @param int doorOpenMode  0: 1:
	 * @param int agentNum 
	 */
	public EnvironmentAgent(String fileName, int doorOpenMode, int agentNum) {

		this.fileName = fileName;
		this.doorOpenMode = doorOpenMode;
		StringBuffer title = new StringBuffer();
		title.append("");
		if(fileName.equals("DoorKeyMap_a.csv")) {
			title.append("a");
		}else {
			title.append("b");
		}
		
		title.append("       ");
		if(doorOpenMode == 1) {
			title.append("");
		}else {
			title.append("");
		}

		if(agentNum == 1) {
			title.append("        ");
		}else {
			title.append("        ");
		}

		environment = new Environment(fileName, title.toString());
		environment.changeDoorOpenItem(doorOpenMode);

		/*  */
//		initActionList();
	}


	//////////////////////////////////////////////////
	// public

	/**
	 * action
	 * action-1
	 * @param int action 
	 * @return Vector 
	 */
	public Vector move(int action) {

		if(action == -1) {
			/*  */
			Integer integer = getInitAction();
			if(integer != null) {
				action = integer.intValue();
			}else {
				action = getRandomAction();
			}
		}

		environment.run(action);

		return getState(action);
	}


	/**
	 * 
	 * 
	 * 
	 */
	public void restart() {
		chageStart();
		environment.initRobotPos();
		environment.initMap();
		chageItem();
	}

	/**
	 * 
	 * 
	 */
	public void goalReach() {
		environment.flash();
		environment.countGoalReachCount();
	}

	//////////////////////////////////////////////////
	// private

	/**
	 * action
	 * @param int action 
	 * @return Vector 
	 */
	private Vector getState(int action) {

		Vector state = new Vector();

		/*  */
		int[] xystate = environment.getXYState();
		state.add(new Integer(xystate[0]));
		state.add(new Integer(xystate[1]));
		state.add(new Integer(action));

		/*  */
		String str = environment.getMapInfo(xystate[0], xystate[1]);
		if(str.equals("T")) {
			state.add(new Integer(4));
		}else if(str.equals("d")) {
			state.add(new Integer(3));
		}else if(str.equals("K")) {
			state.add(new Integer(2));
		}else if(str.equals("O(1)")) {
			state.add(new Integer(1));
		}else {
			state.add(new Integer(0));
		}

		/*  */
		state.add(new Integer(environment.getItem()) );

		/* ID */
		if(fileName.equals("DoorKeyMap_a.csv")) {
			state.add(new Integer(1) );
		}else {
			state.add(new Integer(2) );
		}

		/*  */
		state.add(new Integer(doorOpenMode) );

		return state;
	}

	/**
	 * 
	 */
	private void chageStart() {
		/*  */
		int[] newStart = getRandomState(randomStart);
		environment.setStart(newStart[0], newStart[1]);
	}


	/**
	 * 
	 */
	private void chageItem() {
		int newItem = randomItem.nextInt(3);
// 
//		int newItem = randomItem.nextInt(7);
		environment.setItem(newItem);
	}


	/**
	 * 
	 * @return int 
	 */
	private int getRandomAction() {
		/*  */
		int randomNum = randomMoveAction.nextInt(4)*2;
		return randomNum;
	}

	/**
	 * 
	 * @return int[] 
	 *               int[0] x
	 *               int[1] y
	 */
	private int[] getRandomState(Random random) {

		int[] randomState = new int[2];

		/*
		 * 
		 * 
		 */
		while(true) {
			/*  */
			int[] mapSize = environment.getMapSize();
			randomState[0] = random.nextInt(mapSize[0]-1) + 1;
			randomState[1] = random.nextInt(mapSize[1]-1) + 1;

			/*  */
			if(fileName.equals("DoorKeyMap_a.csv")) { // a
				if( randomState[0] < 19 || randomState[1] < 19 ) {
					String newState = environment.getMapInfo(randomState[0],
					        randomState[1]);
					if(newState.equals("")) {
						break;
					}
				}
			}else { // b
				if( randomState[0] < 17 ) {
					String newState = environment.getMapInfo(randomState[0],
					        randomState[1]);
					if(newState.equals("")) {
						break;
					}
				}
			}
		}

		return randomState;
	}

	/**
	 * action
	 */
	private Integer getInitAction() {
		Integer manualAction = null;
		if(actionList.size() > 0) {
			manualAction = (Integer)actionList.removeFirst();
		}
		return manualAction;
	}

	/**
	 * 
	 */
	private void initActionList() {
		for(int i = 0; i < 8; i++) {
			actionList.add(new Integer(0));
		}
		for(int i = 0; i < 5; i++) {
			actionList.add(new Integer(6));
		}
		for(int i = 0; i < 4; i++) {
			actionList.add(new Integer(0));
		}
		for(int i = 0; i < 11; i++) {
			actionList.add(new Integer(6));
		}
		for(int i = 0; i < 9; i++) {
			actionList.add(new Integer(0));
		}
		for(int i = 0; i < 8; i++) {
			actionList.add(new Integer(6));
		}
		for(int i = 0; i < 8; i++) {
			actionList.add(new Integer(2));
		}
		for(int i = 0; i < 16; i++) {
			actionList.add(new Integer(4));
		}
		for(int i = 0; i < 4; i++) {
			actionList.add(new Integer(6));
		}
		for(int i = 0; i < 6; i++) {
			actionList.add(new Integer(4));
		}
		for(int i = 0; i < 4; i++) {
			actionList.add(new Integer(6));
		}
	}


}

