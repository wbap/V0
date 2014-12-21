/**
 * EnvironmentAgent.java
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2000.09 BSC miyamoto
 */
package wba.citta.environment;

import java.util.*;

public class EnvironmentAgent {

	private Environment environment = null;
	/* ���̃t�@�C���� */

	/* ���̏�� */
	private String[][] MAP_ARRAY = {
		        {"W","W","W","W","W","W","W","W","W","W","W","W","W","W","W"},
		        {"W","","","","","","","","","","","","","","W"},
		        {"W","","S","","","","","","","","","","","","W"},
		        {"W","","W","W","W","W","W","W","","","","","","","W"},
		        {"W","","W","","","","","W","","","","","","","W"},
		        {"W","","W","","","","","W","","","","","","","W"},
		        {"W","","W","","W","","","W","","","","","","","W"},
		        {"W","","W","","W","","","W","","","","","","","W"},
		        {"W","","W","","W","","","W","","","","","","","W"},
		        {"W","","W","","W","","","W","","","W","","","","W"},
		        {"W","","W","","W","","","","","","","","W","","W"},
		        {"W","","W","","W","W","W","W","","","W","","","","W"},
		        {"W","","","","W","","","","","","","","W","","W"},
		        {"W","","","","W","","","","W","","W","","","","W"},
		        {"W","","W","","","","","","","","","","W","","W"},
		        {"W","","W","","","","","","W","","W","","","","W"},
		        {"W","","W","","","","","","","","","","","","W"},
		        {"W","","W","W","W","W","W","","","","","","","","W"},
		        {"W","","","","","","","","","","","","","","W"},
		        {"W","W","W","W","W","W","W","W","W","W","W","W","W","W","W"}
		};

	/* �S�[���������_���Ȉʒu�ɐݒ肷�邽�߂̗��� */
	private Random randomGoal = new Random(0);

	/**
	 * �R���X�g���N�^
	 */
	public EnvironmentAgent() {
		/* ���̐��� */
		environment = new Environment(MAP_ARRAY);
		environment.initRobotPos();
		/* �S�[����ݒ� */
		int[] goalEnv = getRandomState();
		environment.setGoal(goalEnv[0], goalEnv[1]);
	}


	/**
	 * ���݂̈ʒu���擾���܂��B
	 * @return int[]  int[0] x���W  int[1] y���W
	 */
	public int[] getState() {
		return environment.getXYState();
	}

	/**
	 * �S�[���̈ʒu���擾���܂��B
	 * @return int[]  int[0] x���W  int[1] y���W
	 */
	public int[] getGoal() {
		return environment.getXYGoalState();
	}

	/**
	 * 
	 */
	public void run(int action) {
		/* action�Ŋ��𓮍삳���� */
		environment.run(action);

		/* �S�[�����B���̓S�[���̈ʒu��ύX */
		int[] stateEnv = environment.getXYState();
		int[] goalEnv = environment.getXYGoalState();
		if( (goalEnv[0]==stateEnv[0] && goalEnv[1]==stateEnv[1]) ) {
			goalEnv = getRandomState();
			environment.setGoal(goalEnv[0], goalEnv[1]);
		}
	}


	/**
	 * �����_���ɐ���������Ԃ��擾���܂��B
	 * @return int[] �����_���ɐ������ꂽ���
	 *               int[0] x���W
	 *               int[1] y���W
	 */
	private int[] getRandomState() {

		int[] randomState = new int[2];

		/*
		 * �����_���ɍ��W���擾�A�V���Ɏw�肳�ꂽ�ʒu�ɂ��łɉ����ݒ肳���
		 * ����ꍇ�͍ēx���W���擾
		 */
		while(true) {
			/* �n�}�̃T�C�Y���擾 */
			int[] mapSize = environment.getMapSize();
			randomState[0] = randomGoal.nextInt(mapSize[0]-1) + 1;
			randomState[1] = randomGoal.nextInt(mapSize[1]-1) + 1;
			String newState = environment.getMapInfo(randomState[0],
			        randomState[1]);
			if(newState.equals("")) {
				break;
			}
		}

		return randomState;
	}


}

