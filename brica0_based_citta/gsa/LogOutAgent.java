/**
 * LogOutAgent.java
 * �G�[�W�F���g�̎��s�����ŏo�͂����T�u�S�[�����A���O�Ƃ��ăt�@�C���ɕۑ�����
 * ������ǉ�����Agent
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.10
 */
package gsa;

import java.io.*;
import java.util.*;

/**
 * �G�[�W�F���g�̎��s�����ŏo�͂����T�u�S�[�����A���O�Ƃ��ăt�@�C���ɕۑ�����
 * ������ǉ�����Agent
 */
public abstract class LogOutAgent extends Agent {

	private final String LOG_FILE_NAME = "AgentLog_";

	private FileOutputStream fileOutputStream = null;
	private ObjectOutputStream objectOutputStream = null;


	/**
	 * �R���X�g���N�^
	 * @param int agid  �G�[�W�F���gID
	 * @param boolean[] useNode  �m�[�h�̎g�p�A�s�g�p��ݒ肵���z��
	 * @param SharedMemory sharedMemory  state�Egoal���Ǘ����鋤�L������
	 */
	public LogOutAgent(int agid, boolean[] useNode,
	         SharedMemory sharedMemory) {
		super(agid, useNode, sharedMemory);
		initLogFile(agid + ".log");
	}

	/**
	 * ���O�t�@�C���̏�����
	 * �����Ŏw�肳�ꂽ�t�@�C�����ŁA���O�t�@�C���𐶐����܂��B
	 * @param String fileName �t�@�C����
	 */
	private void initLogFile(String fileName) {

		String logFileName = LOG_FILE_NAME + fileName;

		try {
			/* �X�g���[���̍쐬 */
			fileOutputStream = new FileOutputStream(logFileName, false);
			objectOutputStream = new ObjectOutputStream(fileOutputStream);
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println(e);
			System.exit(0);
		}

	}


	///////////////////////////////////////////////////////////////////////
	// public

	/**
	 * Agent�N���X��exec���I�[�o�[���C�h���A�T�u�S�[�������O�Ƃ��ăt�@�C���o��
	 * ���鏈����ǉ��B<BR>
	 * @param Vector state
	 * @param Vector goalElementArray
	 * @return subgoal
	 */
	public Vector exec(Vector state, Vector goalElementArray) {

		Vector subgoal = execProcess(state, goalElementArray);

		try {
			objectOutputStream.writeObject(subgoal);
		}catch(Exception e) {
			System.out.println(e);
		}

		return subgoal; 
	}


}

