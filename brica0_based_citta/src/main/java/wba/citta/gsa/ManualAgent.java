/**
 * ManualAgent.java
 * �蓮�œ��삷��G�[�W�F���g
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.08
 */
package wba.citta.gsa;

import java.util.*;

/**
 * �蓮�œ��삷��G�[�W�F���g
 */
public class ManualAgent extends Agent {

	private ManualAgentFrame manualAgentFrame = null;

	private SharedMemory sharedMemory = null;
	private boolean[] useNode = null;

	/* ���̃N���X����o�͂����T�u�S�[���̃��X�g */
	private LinkedList subgoalList = null;

	/**
	 * �R���X�g���N�^
	 * @param int agid  �G�[�W�F���gID
	 * @param boolean[] useNode  �m�[�h�̎g�p�A�s�g�p��ݒ肵���z��
	 * @param SharedMemory sharedMemory  state�Egoal���Ǘ����鋤�L������
	 */
	public ManualAgent(int agid, boolean[] useNode, SharedMemory sharedMemory) {
		super(agid, useNode, sharedMemory);


		this.sharedMemory = sharedMemory;
		this.useNode = useNode;

		subgoalList = new LinkedList();

		manualAgentFrame = new ManualAgentFrame(sharedMemory.LENGTH);

	}


	///////////////////////////////////////////////////////////////////////
	// public

	/**
	 * �G�[�W�F���g�ŗL�̎��s�������s�Ȃ��܂��B<BR>
	 * ���s�������s�Ȃ��G�[�W�F���g�Ƃ��đI�����ꂽ�Ƃ��ɁAGUI�ɐݒ肳�ꂽ
	 * �S�[���l���擾���āA���̒l���T�u�S�[���Ƃ��ĕԂ��܂��B
	 * @param Vector state ���݂̏��
	 * @param Vector goalElementArray GoalStackElement��Vector
	 * @return Vector �T�u�S�[��
	 */
	public Vector execProcess(Vector state, Vector goalElement) {
		Vector v = manualAgentFrame.getSubgoal();

		if(v != null) {
			subgoalList.add(v);
		}

		manualAgentFrame.clearSubgoal();
		return v;
	}

	/**
	 * Agent�N���X���p�����č쐬���Ă��邽�߁A�`���I�Ɏ���<BR>
	 */
	public void learn(Vector state, boolean flagGoalReach, double profit) {
	}

	/**
	 * Agent�N���X���p�����č쐬���Ă��邽�߁A�`���I�Ɏ���<BR>
	 */
	public void reset() {
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

	/**
	 * Agent�N���X���p�����č쐬���Ă��邽�߁A�`���I�Ɏ���<BR>
	 */
	public void suspend() {
	}



	/**
	 * �X�^�b�N��̎���ݒ肵���S�[���ɓ��B�����ꍇ�A���̃S�[�����X�^�b�N�E
	 * �c���[����폜���܂��B<BR>
	 * �ʏ�̃G�[�W�F���g�ł̓S�[���͐ڑ��m�[�h�S�Ăɏo�͂��邪�AManualAgent
	 * �ł̓S�[���Ƃ��Đڑ��m�[�h�̈ꕔ�����o�͂��邱�Ƃ�����̂œ��B�����
	 * ������v�ōs���K�v������B���̂���Agent�N���X�̃��\�b�h���I�[�o�[���C�h
	 * ���A�������e��ς��Ă���B
	 * @return boolean ���B�����ꍇtrue 
	 */
// �����S�[���̔���̂��߁A���ۂɃX�^�b�N�ɏo�͂����S�[���ƈقȂ�ꍇ�ł��A
// null�ȊO�̗v�f�������ł���΁A���B�Ƃ݂Ȃ��폜���Ă��܂��B
// �Ή����@�F�o�͂����T�u�S�[�������X�g�ŕێ�����悤�ɂ��A�ێ����Ă���T�u
// �S�[���Ƃ̈�v�𔻒肷��悤�ɂ���B
	public boolean removeReachGoal() {
		Vector state = getStateReference();

		Vector selfSetGoalElementArray = getSelfSetGoalElementArray();
		Vector selfSetGoalValueArray
		        = getGoalValueArray(selfSetGoalElementArray);

		if( subgoalList.size() > 0 ) {
			Vector lastSetSubgoal = (Vector)subgoalList.getLast();

			/* ���炪�ݒ肵���T�u�S�[���ɓ��B���Ă���΍폜 */
			if( lastSetSubgoal.equals(selfSetGoalValueArray) ) {
				if( Util.equalsValidElement(state, selfSetGoalValueArray) ) {
					/* �폜�����Ȑݒ蕔���̂ݍs�� */
					removeGoal(selfSetGoalValueArray);
					subgoalList.removeLast();
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * �S�[���̍폜���s�Ȃ��܂��B
	 * �����Őݒ肳�ꂽ�S�[���̗L���ȗv�f���ݒ肳��Ă���m�[�h�̂�
	 * �S�[���X�^�b�N����폜
	 * @param Vector goal �S�[���X�^�b�N����폜����S�[��
	 */
	private void removeGoal(Vector goal) {
		int useNodeIndex = 0;
		for(int i = 0; i<sharedMemory.LENGTH; i++) {
			if( useNode[i] == true ) {
				if(goal.get(useNodeIndex) != null) {
					sharedMemory.removeGoal(i);
				}
				useNodeIndex++;
			}
		}
	}



}

