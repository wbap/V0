/**
 * Agent.java
 * �G�[�W�F���g�ɋ��ʂ̏���(���L�������Ƃ̏��̎�n����)���s�Ȃ��N���X
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.07
 */
package gsa;

import java.util.*;
import java.io.*;

import brica0.Module;

/**
 * �G�[�W�F���g�ɋ��ʂ̏���(���L�������Ƃ̏��̎�n����)���s�Ȃ��N���X
 */
public abstract class Agent extends Module {

	/*
	 * �G�[�W�F���g�̎��s�����̌��ʂ�����ID
	 * �����ȊO�̓T�u�S�[�����o�͏���
	 */

	/**
	 * ���s��������
	 */ 
	public static final int AGR_SUCCESS = 0;

	/**
	 * ���ł�FailAgentTree�ɐݒ肳��Ă��邱�Ƃɂ��T�u�S�[�����o��
	 */
	public static final int AGR_FAIL_AGENT = 1;

	/**
	 * �S�[�����B�ɂ��T�u�S�[�����o��
	 */
	public static final int AGR_REACH_GOAL = 2;

	/**
	 * �T�u�S�[�������B�ɂ��T�u�S�[�����o��
	 */
	public static final int AGR_UNREACH_SUBGOAL = 3;

	/**
	 * �T���s�\�ɂ��T�u�S�[�����o��
	 */
	public static final int AGR_SEARCH_FAIL = 4;

	/** 
	 * �d���T�u�S�[���ɂ��T�u�S�[�����o��
	 */
	public static final int AGR_SAME_SUBGOAL = 5;

	/**
	 * ����S�[���ɂ��T�u�S�[�����o��
	 */
	public static final int AGR_SAME_GOAL = 6;


	/**
	 * �G�[�W�F���gID
	 */
	public final int AGID;

	/* ���L������ */
	private SharedMemory sharedMemory = null;

	/* �G�[�W�F���g���̃m�[�h�̎g�p�A�s�g�p��ݒ肵��boolean�̔z�� */
	private boolean[] useNode = null;

	/* �G�[�W�F���g���g�p����m�[�h��(useNode��true�̐�) */
	private int useNodeNum;

	/*
	 * ���̒��ۃN���X�̎��s�����őO��o�͂����T�u�S�[��
	 * �T�u�S�[�������B�̔���Ɏg�p
	 */
	private Vector subgoalOld;

	/*
	 * ���̒��ۃN���X�̎����N���X�̎��s�����őO��o�͂����T�u�S�[��
	 * �����N���X����o�͂��ꂽ�T�u�S�[�����K���G�[�W�F���g�̃T�u�S�[��
	 * �Ƃ��ďo�͂���镪���ł͂Ȃ��̂ŁAsubgoalOld�Ƃ͈قȂ�
	 * �d���T�u�S�[���̔���Ɏg�p
	 */
	private Vector impleAgSubgoalOld = null;


	///////////////////////////////////////////////////////////////////
	// �R���X�g���N�^

	/**
	 * �R���X�g���N�^
	 * @param int agid  �G�[�W�F���gID
	 * @param boolean[] useNode  �m�[�h�̎g�p�A�s�g�p��ݒ肵���z��
	 * @param SharedMemory sharedMemory  state�Egoal���Ǘ����鋤�L������
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

	// 2001.12.14 �ǉ� miyamoto
	/**
	 * �C�x���g�����w�K�f�[�^�Ƃ��ė��p���A�w�K�������s�Ȃ��܂��B
	 * @param String eventFileName �C�x���g���̋L�q���ꂽ�t�@�C����
	 */
	public void learnEvent(String eventFileName) {

		System.out.println(" Load Event File ���");
		/* �t�@�C���̏����� */
		try{
			FileReader fr = new FileReader(eventFileName);
			BufferedReader br = new BufferedReader(fr);

			try {
				while( br.ready() ) {
					/* �C�x���g�t�@�C������C�x���g���P�擾 */
					String event = br.readLine();

					/* �擾�����C�x���g���G�[�W�F���g�̏�Ԃɕϊ� */
					StringTokenizer stringTokenizer
					         = new StringTokenizer(event, ",");
					Vector eventState = new Vector();
					while(stringTokenizer.hasMoreTokens()) {
						eventState.add(
						        new Integer(stringTokenizer.nextToken()));
					}

					/* �C�x���g���w�K���� */
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

		/* �ʏ�̊w�K�Ƃ͘A�������Ȃ��̂Ń��Z�b�g���� */
		reset();
	}
	// �����܂�

	/**
	 * �w�K�������s�Ȃ��܂��B<BR>
	 * (������flagGoalReach�Aprofit�́A�A�z�G�[�W�F���g�̋����w�K�p�B
	 * �b�c�G�[�W�F���g�ł͎g�p���Ă��Ȃ��B)
	 * @param flagGoalReach �S�[���ւ̓��B��\���t���O
	 * @param double profit ��V
	 */
	public void learn(boolean flagGoalReach, double profit) {
		Vector state = getState();
		learn(state, flagGoalReach, profit);
	}

	/**
	 * ���s�������s�Ȃ��܂��B<BR>
	 * ���L����������state�Agoal���擾���A���[�U��`�̎��s����(protected��
	 * exec(Vector, Vector)�o�R�ŁAabstruct��execProcess(Vector, Vector)��
	 * �Ăяo��)���s�Ȃ��A���[�U��`�̎��s�����Ő������ꂽsubgoal�����L������
	 * �ɐݒ肵�܂��B<BR>
	 * @return int ���s�����̌��ʂ�����ID<BR>
	 * AGR_SUCCESS�AAGR_REACH_GOAL�AAGR_UNREACH_SUBGOAL�AAGR_SEARCH_FAIL�A
	 * AGR_SAME_SUBGOAL�AAGR_SAME_GOAL�̂��Âꂩ
	 */
	public int exec() {

		Vector state = getState();

		/* �S�[�����擾 �I���͓����� */
		Vector goalElementArray = getGoalElementArray();

//		System.out.println("");
//		System.out.println("[" + AGID + "] Agent.java");
//		System.out.println(" state:" + state);
//		System.out.println(" goalElementArray:" + goalElementArray);

		/* ���s�������s���� */
		int isExexMode = isExec(state, goalElementArray);
		if( isExexMode != AGR_SUCCESS ) {
			subgoalOld = null;
			return isExexMode;
		}

		/* ���ۃ��\�b�h */
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
	 * ���B�S�[���̍폜���s�Ȃ��܂��B<BR>
	 * ���Ȑݒ�S�[��(�ڑ��m�[�h�S�Ă�����ݒ肵�Ă���S�[��)�Ɍ��݂̏�Ԃ�
	 * ���B�����ꍇ�A���̃S�[�����S�[���X�^�b�N����폜���܂��B
	 * @return boolean true�F���Ȑݒ�S�[���ɓ��B���A�S�[���X�^�b�N����폜
	 * �����ꍇ
	 */
	public boolean removeReachGoal() {
		/* ����Ɏg�p���邾���Ȃ̂ŁA�Q�Ƃ��擾 */
		Vector state = getStateReference();

		Vector selfSetGoalElementArray = getSelfSetGoalElementArray();
		Vector selfSetGoalValueArray
		        = getGoalValueArray(selfSetGoalElementArray);

		/* ���炪�ݒ肵���T�u�S�[���ɓ��B���Ă���΍폜 */
		if( state.equals(selfSetGoalValueArray) ) {
			removeGoal();
			return true;
		}
		return false;
	}

	/**
	 * ���Ȑݒ�S�[��(�ڑ��m�[�h�S�Ă�����ݒ肵�Ă���S�[��)���S�[���X�^�b�N
	 * �ɂ���΁A���̃S�[�����X�^�b�N����폜���܂��B
	 * @return boolean true:���Ȑݒ�S�[��������A�폜�ł����ꍇ<BR>
	 * false:���Ȑݒ�S�[�����Ȃ����߁A�폜�ł��Ȃ������ꍇ<BR>
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
	 * ���݂̏��(State)�����L����������擾���܂�(�Q�Ƃ��擾)�B
	 * @return Vector ���݂̏��
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
	 * ����ݒ肵���S�[���̏�Ԃ��X�^�b�N����擾���܂��B<BR>
	 * �G�[�W�F���g���ڑ����Ă���m�[�h�̗v�f�S�Ă�����ݒ肵�Ă���ꍇ�ɁA
	 * �������G�[�W�F���g�̏�ԂƂ��Ď擾���܂��B<BR>
	 * ���̃G�[�W�F���g���ݒ肵�Ă���v�f������΁Anull���Ԃ�܂��B
	 * @return Vector �S�[���̏��<BR>
	 */
// GSA�N���X�AManualAgent�N���X��������p���邽��public��
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
	// ���ۃ��\�b�h

	/**
	 * �G�[�W�F���g�ŗL�̊w�K�������s�Ȃ��܂��B<BR>
	 * @param Vector state ���݂̏��
	 * @param boolean flagGoalReach �S�[���ւ̓��B��\���t���O
	 * @param double profit ��V
	 */
	public abstract void learn(Vector state, boolean flagGoalReach,
	        double profit);

	/**
	 * �G�[�W�F���g�ŗL�̎��s�������s�Ȃ��܂��B<BR>
	 * @param Vector state ���݂̏��
	 * @param Vector goalElementArray GoalStackElement��Vector
	 * @return Vector �T�u�S�[��
	 */
	public abstract Vector execProcess(Vector state, Vector goalElementArray);

	/**
	 * �w�K���ʂ��t�@�C���ɕۑ����܂��B
	 * @param String fileName�t�@�C����
	 */
	public abstract void save(String fileName);

	/**
	 * �w�K���ʂ��t�@�C������ǂݍ��݂܂��B
	 * @param String fileName �t�@�C����
	 */
	public abstract void load(String fileName);

	/**
	 * GSA�N���X��reset()���\�b�h����Ăяo����܂��B<BR>
	 * ��ԑJ�ڂ̗����̃N���A�A�O�T�C�N���̕ێ����̃N���A�ȂǁA�w�K�A
	 * ���s�����̘A�������r�؂��ꍇ�ɍs�Ȃ����̃N���A�Ȃǂ̏�����
	 * �L�q���܂��B<BR>
	 */
	public abstract void reset();

	/**
	 * GSA�N���X�ɂ���āA���s�������s�Ȃ��G�[�W�F���g�����g�̃G�[�W�F���g
	 * ���瑼�̃G�[�W�F���g�ɐ؂�ւ���ꂽ�Ƃ��ɌĂяo����܂��B<BR>
	 * ���̂��߁A�e�G�[�W�F���g�̎��s�����ŗ��p����O�T�C�N���̕ێ����
	 * �ȂǁA���s�����̘A�����Ɉˑ����ĕێ����Ă�����̃N���A�Ȃǂ̏�����
	 * �L�q���܂��B<BR>
	 */
	public abstract void suspend();

	////////////////////////////////////////////////////////////
	// protected

	/**
	 * ���s�������s�Ȃ��܂��B
	 * 
	 */
	protected Vector exec(Vector state, Vector goalElementArray) {;
		return execProcess(state, goalElementArray);
	}

	/**
	 * GoalStackElement��Vector����GoalValue��Vector���擾���܂��B
	 * @param Vector goalElementArray GoalStackElement��Vector
	 * @return Vector            goalValue��Vector
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
	 * ���s�������s�����ǂ����̔���
	 * @param Vector state ���݂̏��
	 * @param Vector goalElementArray �S�[���X�^�b�N�̏��
	 * @return int �T�u�S�[�����o�͏���
	 */
	private int isExec(Vector state, Vector goalElementArray) {

		/*
		 * �S�[���ɓ��B���Ă���Ύ��s�������s��Ȃ�
		 * �S�[���ɂ�null�̗v�f������\��������̂ŁAnull�ȊO�̗v�f�Ŕ���
		 */
		Vector goalValue = getGoalValueArray(goalElementArray);
		if( Util.equalsValidElement(state, goalValue) ) {
			return AGR_REACH_GOAL;
		}

		/*
		 * �O�T�C�N���̏��������s��������s�������s��Ȃ�
		 * �����G�[�W�F���g��؂�ւ��邽��
		 */
		if( subgoalOld != null && !state.equals(subgoalOld) ) {
			return AGR_UNREACH_SUBGOAL;
		}

		return AGR_SUCCESS;
	}

	/**
	 * �T�u�S�[�����o�͂��邩�ǂ����̔���
	 * @param Vector sugoal �T�u�S�[��
	 * @param Vector goalElementArray GoalStackElement��Vector
	 * @return int �T�u�S�[�����o�͏���
	 */
	private int isReturnSubgoal(Vector subgoal, Vector goalElementArray) {
		/* �T�u�S�[�����o�͂ł��Ȃ��ꍇ */
		if(subgoal == null) {
			impleAgSubgoalOld = subgoal;
			return AGR_SEARCH_FAIL;
		}

		/* �T�u�S�[�����O�T�C�N���̃T�u�S�[���Ɠ����ꍇ�͏o�͂��Ȃ� */
		if( (impleAgSubgoalOld != null) &&
		        (impleAgSubgoalOld.equals(subgoal)) ) {
			impleAgSubgoalOld = subgoal;
			return AGR_SAME_SUBGOAL;
		}
		impleAgSubgoalOld = subgoal;

		/* �T�u�S�[�����S�[���Ɠ����ꍇ�͏o�͂��Ȃ� */
		Vector goal = getGoalValueArray(goalElementArray);
		if(subgoal.equals(goal)) {
			return AGR_SAME_GOAL;
		}

		return AGR_SUCCESS;
	}


	/**
	 * ���̃G�[�W�F���g�̎g�p����m�[�h�����擾���܂��B
	 * @param int �g�p����m�[�h��
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
	 * �S�[���̍폜
	 * �ڑ���̃m�[�h�S�Ă���1�v�f�Â폜
	 */
	private void removeGoal() {
		for(int i = 0; i<sharedMemory.LENGTH; i++) {
			if( useNode[i] == true ) {
				sharedMemory.removeGoal(i);
			}
		}
	}

	/**
	 * ���݂̏��(State)�����L����������擾���܂��B
	 * @return Vector ���݂̏��
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
	 * �S�[�����擾���܂��B
	 * ���̃G�[�W�F���g���ݒ肵���S�[��������ꍇ�́A���̃G�[�W�F���g���ݒ�
	 * �����S�[���B�Ȃ���΁A���炪�ݒ肵���S�[���ƃS�[���Ƃ��ė��p���܂��B
	 * @return Vector GoalStackElement��Vector
	 */
	private Vector getGoalElementArray() {
// ���Ȑݒ�Ӑ}�A���ݒ�Ӑ}�̋�ʂ��s�Ȃ��ݒ�
		Vector goalElementArray = getOtherSetGoalElementArray();
		if(goalElementArray == null)  {
			goalElementArray = getSelfSetGoalElementArray();
		}
		return goalElementArray;

// ���Ȑݒ�Ӑ}�A���ݒ�Ӑ}�̋�ʂ��s�Ȃ�Ȃ��ݒ�
//		return getGoalElementArray2();
	}


	/**
	 * ���̃G�[�W�F���g���ݒ肵���S�[���̏�Ԃ��X�^�b�N����擾���܂��B
	 * �G�[�W�F���g���ڑ����Ă���m�[�h���瑼�̃G�[�W�F���g���ݒ肵������
	 * �݂̂��A�G�[�W�F���g�̏�ԂƂ��Ď擾���܂��B����ݒ肵�Ă���v�f��null
	 * ��ݒ肵�܂��B
	 * @return Vector GoalStackElement��Vector
	 * Vector�̑S�Ă̗v�f��null�Ȃ�Vector���̂�null�ɐݒ肵�ĕԂ��B
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
		/* �v�f�����ׂ�null�Ȃ�Vector���̂�null�ɐݒ� */
		if(nullNum == useNodeNum) {
			otherGoal = null;
		}

		return otherGoal;
	}

	/**
	 * �T�u�S�[����ݒ肵�܂��B
	 * @param Vector subgoal �T�u�S�[��(GoalValueArray)
	 */
	private void setSubgoal(Vector subgoal) {
		if(subgoal != null) {
			pushGoalToStack(subgoal);
		}
	}

	/**
	 * �T�u�S�[�����X�^�b�N�ɐݒ肵�܂��B
	 * @param Vector subgoal �T�u�S�[��(GoalValueArray)
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
	// �Ӑ}�̋�ʂ��s�Ȃ�Ȃ��Ă�����ɓ��삷�邩�m�F���邽�߂ɉ��̃��\�b�h

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
		/* �v�f�����ׂ�null�Ȃ�Vector���̂�null�ɐݒ� */
		if(nullNum == useNodeNum) {
			goal2 = null;
		}

		return goal2;
	}


}
