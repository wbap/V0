/**
 * LogAgent.java
 * ���O��ǂݍ��݁A���O�̏��œ��삷��G�[�W�F���g
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.10
 */
package wba.citta.gsa;

import java.io.*;
import java.util.*;

/**
 * ���O��ǂݍ��݁A���O�̏��œ��삷��G�[�W�F���g
 */
public class LogAgent extends Agent {

	private final String LOG_FILE_NAME = "AgentLog_";

	private FileInputStream fileInputStream = null;
	private ObjectInputStream objectInputStream = null;

	/**
	 * �R���X�g���N�^
	 * @param int agid  �G�[�W�F���gID
	 * @param SharedMemory sharedMemory  state�Egoal���Ǘ����鋤�L������
	 * @param boolean[] useNode  �m�[�h�̎g�p�A�s�g�p��ݒ肵���z��
	 */
	public LogAgent(int agid, boolean[] useNode, SharedMemory sharedMemory) {
		super(agid, useNode, sharedMemory);

		/* ���O�̓ǂݍ��݃X�g���[������ */
		String fileName = LOG_FILE_NAME + agid + ".log";
		try{
			fileInputStream = new FileInputStream(fileName);
			objectInputStream = new ObjectInputStream(fileInputStream);
		}catch(Exception e){
			System.out.println(e);
			System.exit(0);
		}

	}


	///////////////////////////////////////////////////////////////////////
	// public

	/**
	 * Agent�N���X���p�����č쐬���Ă��邽�߁A�`���I�Ɏ���<BR>
	 */
	public void learn(Vector state, boolean flagGoalReach, double profit) {
	}

	/**
	 * �G�[�W�F���g�ŗL�̎��s�������s�Ȃ��܂��B<BR>
	 * ���O�t�@�C����������擾���A�擾���������T�u�S�[���Ƃ��ďo�͂��܂��B
	 * <BR>
	 * @param Vector state ���݂̏��
	 * @param Vector goalElementArray GoalStackElement��Vector
	 * @return Vector �T�u�S�[��
	 */
	public Vector execProcess(Vector state, Vector goalElement) {

//		System.out.println("   state:" + state);
//		System.out.println("   goal :" + goalValue);

		/* ���O�̓ǂݍ��� �o�� */
		Vector nextState = null;
		try{
			nextState = (Vector)objectInputStream.readObject();
		}catch(Exception e){
			System.out.println("AGID " + AGID);
			System.out.println(e);
		}

//		System.out.println("   next state :" + nextState);

		return nextState;

	}

	/**
	 * Agent�N���X���p�����č쐬���Ă��邽�߁A�`���I�Ɏ���<BR>
	 */
	public void reset() {
	}

	/**
	 * Agent�N���X���p�����č쐬���Ă��邽�߁A�`���I�Ɏ���<BR>
	 */
	public void suspend() {
	}

	/**
	 * Agent�N���X���p�����č쐬���Ă��邽�߁A�`���I�Ɏ���<BR>
	 */
	public void save(String fileName) { 
	}

	/**
	 * Agent�N���X���p�����č쐬���Ă��邽�߁A�`���I�Ɏ���<BR>
	 */
	public void load(String fileName) {
	}

}

