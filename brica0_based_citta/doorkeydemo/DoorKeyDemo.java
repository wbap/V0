/**
 * DoorKeyDemo.java
 * Citta�𗘗p���A�h�A�L�[�ۑ�̃f�����s�Ȃ��N���X�B
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.09
 */
package doorkeydemo;

import java.io.*;
import java.util.*;
import gsa.*;


/**
 * Citta�𗘗p���A�h�A�L�[�ۑ�̃f�����s�Ȃ��N���X�B
 */
public class DoorKeyDemo {

	/* �� */
	private EnvironmentAgent environmentAgent = null;
	/* Citta */
	private GSA gsa = null;

	/* �f���̐ݒ�����Ǘ�����N���X */
	private DemoProperty prop = null;

	/* �����Ɋւ���ݒ� */
	private int saveCount;
	private String saveFileName;
	private String loadFileName;
	private final int TIME_OUT_STEP;
	private final int SLEEP_TIME;

	/* �S�[�� */
	private Vector goal = null;


	////////////////////////////////////////////////////////////////
	// �R���X�g���N�^

	/**
	 * �R���X�g���N�^
	 */
	public DoorKeyDemo(String propFileName) {

		/* �ݒ�t�@�C���̓ǂݍ��� */
		try {
			prop = new DemoProperty(propFileName);
		}catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

		gsa = new GSA(prop.getGSAPropFileName());

		environmentAgent = new EnvironmentAgent(prop.getEnvFileName(), 
		         prop.getDoorOpenMode(), 1/*�G�[�W�F���g��*/);

		/* �����ݒ� */
		saveCount = prop.getSaveStepNum();
		saveFileName = prop.getSaveFileName();
		loadFileName = prop.getLoadFileName();
		TIME_OUT_STEP = prop.getTimeOutStepNum();
		SLEEP_TIME = prop.getSleepTime();

		/* �w�K�f�[�^�̓ǂݍ��� */
		if(!loadFileName.equals("")) {
			gsa.load(loadFileName);
		}

		/* �S�[���𐶐����Agsa�ɐݒ� */
		initGoal();
		setGoal();

		// �g���C�A�����Ƃ̃X�e�b�v�����o�͂���t�@�C���𐶐�
		initLogFile("Step.log");

	}

	/**
	 * �S�[���̐���
	 */
	private void initGoal() {
		goal = new Vector();
		goal.add(null);
		goal.add(null);
		goal.add(null);
		goal.add(new Integer(1));
		goal.add(null);
		goal.add(null);
		goal.add(null);
	}

	////////////////////////////////////////////////////////////////
	// public

	/**
	 * �J��Ԃ�����
	 */
	public void repeatProcess() {

		int toGoalStepCount = 0; /* �S�[���܂ł̃X�e�b�v�� */
		int stepCount = 0;       /* ���s�X�e�b�v���̃J�E���g */

		/*
		 * ����̃f���Ŏg�p���������̃m�[�h
		 * x���W�Fy���W�F�s���F�ꏊ�̑����F�A�C�e���F�n�}��ID�F�h�A���J������
		 */
		Vector state = null;    /* �����o�͂��錻�݂̏�� */
		Vector subgoal = null;  /* CITTA���o�͂���T�u�S�[�� */

		/* ����GSA�̏��������݂ɍs�Ȃ� */
		while(true) {

			/* �����p ���x�𒲐����邽�߂ɃX���[�v������ */
			try {
				Thread.sleep(SLEEP_TIME);
			}catch(InterruptedException e) {
				System.out.println(e);
			}


			/* �S�[�����B �܂��� �^�C���A�E�g�ŃX�^�[�g�ʒu����ăX�^�[�g */
			if( isReachGoal(state, goal) || toGoalStepCount==TIME_OUT_STEP ) {

				/* �S�[�����B�܂��̓^�C���A�E�g�܂ł̃X�e�b�v�����o�� */
				System.out.println("++++++++++++++++++++++++++++++++++++++++");
				System.out.println("   " + toGoalStepCount);
				logOut(toGoalStepCount);
				System.out.println("++++++++++++++++++++++++++++++++++++++++");

				toGoalStepCount = 0;
				restart();
				subgoal = null;

				if(isReachGoal(state, goal) ) {
					goalReachProcess();
				}
			}

			/* �X�e�b�v���̃J�E���g�A�\�� */
			toGoalStepCount++;
			stepCount++;
			System.out.println("");
			System.out.println(" step count " + stepCount);

			/* ���̏��� */
			/*
			 * CITTA�̏o�͂����T�u�S�[������s�����擾 �擾�����s����
			 * ���𓮍삳����
			 */
			int action = -1;
			if(subgoal != null) {
				/* ���̏o�͂�3�Ԗڂ̗v�f��action */
				if(subgoal.get(2) != null) {
					action = ((Integer)subgoal.get(2)).intValue();
				}
			}
			state = environmentAgent.move(action);

			/* CITTA�̏��� */
			/* �w�K�f�[�^�̕ۑ� */
			if(stepCount == saveCount) {
				if(!saveFileName.equals("")) {
					gsa.save(saveFileName);
				}
			}
			/* state����T�u�S�[�����擾 */
			subgoal = gsa.exec(state);

			/* �X�^�b�N�A�c���[�̏�Ԃ�\�� */
//			gsa.printStack();
//			gsa.printGoalTree();

		}
	}


	////////////////////////////////////////////////////////////////
	// private

	/**
	 * �S�[���֓��B�������ǂ�������
	 * �S�[���̗L���ȗv�f(null�ȊO�̗v�f)��State�̗v�f�Ɠ����Ȃ�true��Ԃ��B
	 */
	private boolean isReachGoal(Vector state, Vector goal) {

		if(goal == null || state == null) {
			return false;
		}

		for(int i = 0; i < state.size(); i++) {
			Integer sElement = (Integer)state.get(i);
			Integer gElement = (Integer)goal.get(i);
			if(gElement != null && !sElement.equals(gElement)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * �S�[�����B���̏���
	 */
	private void goalReachProcess() {
		/* �S�[�����B���ɉ�ʂ�_�ł����� */
		environmentAgent.goalReach();
	}

	/**
	 * �X�^�[�g�ʒu�ɖ߂��ēx�������s�Ȃ��Ƃ��̏���
	 */
	private void restart() {
		gsa.reset();
		environmentAgent.restart();
		/* �S�[����gsa�ɍĐݒ� */
		setGoal();
	}

	/**
	 * �S�[����CITTA�ɐݒ肵�܂��B
	 */
	private void setGoal() {
		gsa.setGoal(goal);
	}


	//////////////////////////////////////////////////////////////
	// ���O�̐����A�o�͗p�̃��\�b�h

	private PrintWriter printWriter; // �t�@�C���o�͗p

	private void initLogFile(String fileName) {
		try{
			FileOutputStream fileOutputStream = new FileOutputStream(fileName,
			        false);
			printWriter = new PrintWriter(fileOutputStream, true);
		}catch(Exception e) {
		}
	}

	private void logOut(int stepNum) {
		printWriter.println(stepNum);
	}


	////////////////////////////////////////////
	// ���C�����\�b�h 

	public static void main(String[] args) {
		DoorKeyDemo dkDemo = new DoorKeyDemo(args[0]);
		dkDemo.repeatProcess();
	}


}
