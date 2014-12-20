/**
 * CDAgent.java
 * CognitiveDistance�̏������s�Ȃ�GSA�̃G�[�W�F���g
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.07
 */
package gsa;

import cognitivedistance.*;

import java.util.*;

/**
 * CognitiveDistance�̏������s�Ȃ�GSA�̃G�[�W�F���g
 */
public class CDAgent extends Agent {
//public class CDAgent extends LogOutAgent {

	/* CognitiveDistance�ɂ�鏈�����s�Ȃ��N���X */
	private cognitivedistance.Agent cognitiveDistance;

	/* CognitiveDistance�̃p�����[�^ */
	private int cdLayerNum = 3;
	private int maxCDLngth = 9;
	private int shallowSearchLngth = 1;
	private int deepSearchLngth = 200;
	private int minSearchLngth = 1;
	private int maxSegmentSize = 3;
	private int minSegmentSize = 0;
	private boolean flagNovelSearch = false;
	private boolean flagSegmentation = false;

	/**
	 * �R���X�g���N�^
	 * @param int agid  �G�[�W�F���gID
	 * @param boolean[] useNode  �m�[�h�̎g�p�A�s�g�p��ݒ肵���z��
	 * @param SharedMemory sharedMemory  state�Egoal���Ǘ����鋤�L������
	 */
	public CDAgent(int agid, boolean[] useNode, SharedMemory sharedMemory) {
		super(agid, useNode, sharedMemory);

		cognitiveDistance = new cognitivedistance.Agent(cdLayerNum, maxCDLngth,
		        shallowSearchLngth, deepSearchLngth, minSearchLngth,
		        maxSegmentSize, minSegmentSize, flagNovelSearch, 10,
		        flagSegmentation);

	}


	///////////////////////////////////////////////////////////////////////
	// public

	/**
	 * �G�[�W�F���g�ŗL�̊w�K�������s�Ȃ��܂��B<BR>
	 * ���A��O�����́ACDAgent�̊w�K�ł͎g�p���܂���B<BR>
	 * @param Vector state ���݂̏��
	 * @param boolean flagGoalReach �S�[���ւ̓��B��\���t���O
	 * @param double profit ��V
	 */
	public void learn(Vector state, boolean flagGoalReach, double profit) {
		try {
			cognitiveDistance.learn(state);
		}catch(Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * �G�[�W�F���g�ŗL�̎��s�������s�Ȃ��܂��B<BR>
	 * �����Ŏw�肳�ꂽ�A���݂̏�Ԃ���A�S�[���ֈړ����邽�߂̎��̏�Ԃ�
	 * �T�u�S�[���Ƃ��ďo�͂��܂��B<BR>
	 * @param Vector state ���݂̏��
	 * @param Vector goalElementArray GoalStackElement��Vector
	 * @return Vector �T�u�S�[��
	 */
	public Vector execProcess(Vector state, Vector goalElementArray) {

		/* �S�[���̒l�݂̂��擾 */
		Vector goalValueArray = getGoalValueArray(goalElementArray);

//		System.out.println("   state:" + state);
//		System.out.println("   goal :" + goalValueArray);

		/* CognitiveDistance�Ŏ��̏�Ԃ��擾 */
		Vector nextState = null;
		try {
			nextState = (Vector)cognitiveDistance.exec(state, goalValueArray);
		}catch(Exception e) {
			System.out.println(e);
		}

//		System.out.println("   next state :" + nextState);

		return nextState;

	}

	/**
	 * GSA�N���X��reset()���\�b�h����Ăяo����܂��B<BR>
	 * �F�m�������w�K���邽�߂ɕێ����Ă����Ԃ̗����ƁA�e�w�ŕێ����Ă���A
	 * �ȑO�̏�ԁE�S�[���Ɋւ�������N���A���܂��B
	 */
	public void reset() {
		cognitiveDistance.reset();
	}

	/**
	 * GSA�N���X�ɂ���āA���s�������s�Ȃ��G�[�W�F���g�����g�̃G�[�W�F���g
	 * ���瑼�̃G�[�W�F���g�ɐ؂�ւ���ꂽ�Ƃ��ɌĂяo����܂��B<BR>
	 * CognitiveDistance�̊e�w�ŕێ����Ă���A�ȑO�̏�ԁE�S�[���Ɋւ������
	 * �N���A���܂��B
	 */
	public void suspend() {
		cognitiveDistance.resetOldValue();
	}

	/**
	 * �w�K���ʂ��t�@�C���ɕۑ����܂��B
	 * @param String fileName�t�@�C����
	 */
	public void save(String fileName) { 
		cognitiveDistance.save(fileName);
	}

	/**
	 * �w�K���ʂ��t�@�C������ǂݍ��݂܂��B
	 * @param String fileName �t�@�C����
	 */
	public void load(String fileName) {
		cognitiveDistance.load(fileName);
	}

}

