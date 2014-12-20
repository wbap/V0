/**
 * EnvironmentAgent.java
 * �h�A�L�[�ۑ�̊�
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.07
 */
package doorkeydemo;

import environment.*;

import java.util.*;

/**
 * �h�A�L�[�ۑ�̊�
 */
public class EnvironmentAgent {

	/* �� */
	private Environment environment = null;

	/* ���̒n�}�������t�@�C���� */
	private String fileName = null;
	/* �h�A���J������ */
	private int doorOpenMode = 1;

	/* �����_���ȍs���Ɏg�p���闐�� */
	private Random randomMoveAction = new Random(0);
	/* �ăX�^�[�g���̃X�^�[�g�ʒu�������_���ɐݒ肷�闐�� */
	private Random randomStart = new Random(1);
	/* �ăX�^�[�g���ɕێ����Ă���A�C�e���������_���ɐݒ肷�闐�� */
	private Random randomItem = new Random(0);

	/* �蓮�ōs����ݒ肷�郊�X�g */
	LinkedList actionList = new LinkedList();

	//////////////////////////////////////////////////
	// �R���X�g���N�^

	/**
	 * �R���X�g���N�^
	 * @param String fileName ���̒n�}���̃t�@�C����
	 * @param int doorOpenMode �h�A���J������ 0:�J�M 1:�d�b
	 * @param int agentNum �G�[�W�F���g��
	 */
	public EnvironmentAgent(String fileName, int doorOpenMode, int agentNum) {

		this.fileName = fileName;
		this.doorOpenMode = doorOpenMode;
		StringBuffer title = new StringBuffer();
		title.append("�����F");
		if(fileName.equals("DoorKeyMap_a.csv")) {
			title.append("a");
		}else {
			title.append("b");
		}
		
		title.append("       �h�A���J�������F");
		if(doorOpenMode == 1) {
			title.append("�J�M");
		}else {
			title.append("�d�b");
		}

		if(agentNum == 1) {
			title.append("        �]���@");
		}else {
			title.append("        ��Ė@");
		}

		environment = new Environment(fileName, title.toString());
		environment.changeDoorOpenItem(doorOpenMode);

		/* �����J�n���̍s�����蓮�Őݒ� */
//		initActionList();
	}


	//////////////////////////////////////////////////
	// public

	/**
	 * �����Őݒ肳�ꂽaction�Ŋ��𓮍삳���A�����̏�Ԃ��擾���܂��B
	 * action��-1�Ȃ烉���_���ȓ�����s�Ȃ��܂��B
	 * @param int action �s��
	 * @return Vector ���
	 */
	public Vector move(int action) {

		if(action == -1) {
			/* �������삪����Η��p */
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
	 * �ăX�^�[�g���̏����B
	 * �X�^�[�g�ʒu�������_���ɕύX���āA�X�^�[�g�ʒu�ɖ߂��B
	 * �ێ����Ă���A�C�e���������_���ɐݒ肷��B
	 */
	public void restart() {
		chageStart();
		environment.initRobotPos();
		environment.initMap();
		chageItem();
	}

	/**
	 * �S�[�����B���̏����B
	 * ��ʂ�_�ł����A�S�[���ւ̓��B�񐔂��J�E���g����B
	 */
	public void goalReach() {
		environment.flash();
		environment.countGoalReachCount();
	}

	//////////////////////////////////////////////////
	// private

	/**
	 * ���������Őݒ肵��action�œ��삳���A�����̏�Ԃ��擾���܂��B
	 * @param int action �s��
	 * @return Vector ���
	 */
	private Vector getState(int action) {

		Vector state = new Vector();

		/* �ʒu���擾�E�ݒ� */
		int[] xystate = environment.getXYState();
		state.add(new Integer(xystate[0]));
		state.add(new Integer(xystate[1]));
		state.add(new Integer(action));

		/* �������擾�E�ݒ� */
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

		/* �ێ����Ă���A�C�e�����擾�E�ݒ� */
		state.add(new Integer(environment.getItem()) );

		/* �n�}��ID */
		if(fileName.equals("DoorKeyMap_a.csv")) {
			state.add(new Integer(1) );
		}else {
			state.add(new Integer(2) );
		}

		/* �h�A�J�̏��� */
		state.add(new Integer(doorOpenMode) );

		return state;
	}

	/**
	 * �X�^�[�g�ʒu�������_���ɕύX���܂��B
	 */
	private void chageStart() {
		/* �X�^�[�g�ʒu��ύX */
		int[] newStart = getRandomState(randomStart);
		environment.setStart(newStart[0], newStart[1]);
	}


	/**
	 * �ێ����Ă���A�C�e���������_���ɕύX���܂��B
	 */
	private void chageItem() {
		int newItem = randomItem.nextInt(3);
// �A�C�e���𑝂₷�ꍇ�̐ݒ�
//		int newItem = randomItem.nextInt(7);
		environment.setItem(newItem);
	}


	/**
	 * �����_���ɐ������ꂽ�s�����擾���܂��B
	 * @return int �s��
	 */
	private int getRandomAction() {
		/* �΂߈ړ��Ȃ� */
		int randomNum = randomMoveAction.nextInt(4)*2;
		return randomNum;
	}

	/**
	 * �����_���ɐ���������Ԃ��擾���܂��B
	 * @return int[] �����_���ɐ������ꂽ���
	 *               int[0] x���W
	 *               int[1] y���W
	 */
	private int[] getRandomState(Random random) {

		int[] randomState = new int[2];

		/*
		 * �����_���ɍ��W���擾�A�V���Ɏw�肳�ꂽ�ʒu�ɂ��łɉ����ݒ肳���
		 * ����ꍇ�͍ēx���W���擾
		 */
		while(true) {
			/* �n�}�̃T�C�Y���擾 */
			int[] mapSize = environment.getMapSize();
			randomState[0] = random.nextInt(mapSize[0]-1) + 1;
			randomState[1] = random.nextInt(mapSize[1]-1) + 1;

			/* �h�A���ɂ͐ݒ肵�Ȃ� */
			if(fileName.equals("DoorKeyMap_a.csv")) { // ��a
				if( randomState[0] < 19 || randomState[1] < 19 ) {
					String newState = environment.getMapInfo(randomState[0],
					        randomState[1]);
					if(newState.equals("")) {
						break;
					}
				}
			}else { // ��b
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
	 * �蓮�Őݒ肳�ꂽaction���擾���܂��B
	 */
	private Integer getInitAction() {
		Integer manualAction = null;
		if(actionList.size() > 0) {
			manualAction = (Integer)actionList.removeFirst();
		}
		return manualAction;
	}

	/**
	 * �s�����蓮�Őݒ肵�܂�
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

