/**
 * SharedMemory.java
 * State�EGoal���Ǘ����鋤�L������
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.07
 */
package wba.citta.gsa;

import java.util.*;
import wba.citta.gsa.viewer.*;

/**
 * State�EGoal���Ǘ����鋤�L�������ł��B<BR>
 * Goal�ɂ��Ă̓X�^�b�N�ŊǗ����܂��B<BR>
 * ���L�������ւ̑���́A��{�I�Ƀ��\�b�h�̈����Ŏw�肳�ꂽindex�̗v�f���Ƃ�
 * �s�Ȃ��܂��B
 */
public class SharedMemory {

	/* State�p Integer�̔z�� */
	private Integer[] stateArray = null;

	/* Goal�p LinkedList�̔z�� LinkedList�ɂ�Element��ݒ� */
	private LinkedList[] goalStackArray = null;

	/**
	 * ���L�������̃m�[�h��
	 */
	public final int LENGTH;

	/* �S�[���X�^�b�N�̏�Ԃ�\������viewer */
	private SharedMemoryViewer viewer;

	/**
	 * �R���X�g���N�^
	 * @param int size �m�[�h���i�z��̃T�C�Y�j
	 * @param boolean isShowViewer �X�^�b�N�̏�Ԃ��O���t�B�b�N�\�����邩�ǂ���
	 */
	public SharedMemory(int size, boolean isShowViewer) {
		LENGTH = size;
		stateArray = new Integer[LENGTH];
		goalStackArray = new LinkedList[LENGTH];
		for(int i = 0; i < LENGTH; i++) {
			goalStackArray[i] = new LinkedList();
		}

		if(isShowViewer == true) {
			viewer = new SharedMemoryViewer(stateArray, goalStackArray);
		}
	}


	/**
	 * State�̎w�肳�ꂽ�ʒu(�m�[�h)�̒l���擾���܂��B
	 * @param int index 
	 * @return Integer  
	 */
	public Integer getState(int index) {
		return stateArray[index];
	}

	/**
	 * �w�肳�ꂽ�l��State�̎w�肳�ꂽ�ʒu(�m�[�h)�ɐݒ肵�܂��B
	 * @param int index 
	 * @param Integer value 
	 */
	public void setState(int index, Integer value) {
		stateArray[index] = value;
	}

	/**
	 * Goal�̎w�肳�ꂽ�ʒu(�m�[�h)�̒l���X�^�b�N����GET�Ŏ擾���܂��B
	 * @param int index
	 * @return GoalStackElement  �S�[���̗v�f
	 */
	public GoalStackElement getGoal(int index) {
		GoalStackElement elm = null;
		if(goalStackArray[index].size() > 0) {
			elm = (GoalStackElement)goalStackArray[index].getLast();
		}
		return elm;
	}

	/**
	 * �w�肳�ꂽ�S�[���̗v�f��Goal�̎w�肳�ꂽ�ʒu(�m�[�h)�̃X�^�b�N��PUSH��
	 * �ݒ肵�܂��B
	 * @param int index
	 * @param GoalStackElement elm �S�[���̗v�f
	 */
	public void pushGoal(int index, GoalStackElement elm) {
		goalStackArray[index].add(elm);
		if(viewer != null) {
			viewer.repaint();
		}
	}

	/**
	 * Goal�̎w�肳�ꂽ�ʒu(�m�[�h)�̒l���X�^�b�N����폜���܂��B
	 * @int index 
	 */
	public void removeGoal(int index) {
		goalStackArray[index].removeLast();
		if(viewer != null) {
			viewer.repaint();
		}
	}

	/**
	 * Goal�̗v�f��S�ăN���A���܂��B
	 */
	public void removeAllGoal() {
		for(int i = 0; i < LENGTH; i++) {
			goalStackArray[i].clear();
		}
		if(viewer != null) {
			viewer.repaint();
		}
	}

	/**
	 * ���݂̏�Ԃ�Vector�Őݒ肵�܂��B
	 * @param Vector state ���݂̏��
	 */
	public void setState(Vector state) {
		for(int i = 0; i < LENGTH; i++) {
			setState(i, (Integer)state.get(i));
		}
		if(viewer != null) {
			viewer.repaint();
		}
	}

	/**
	 * �S�m�[�h�̃S�[����Vector�Ŏ擾���܂��B
	 * @param Vector GoalValue��Vector
	 */
	public Vector getGoalValueArray() {
		Vector goal = new Vector();
		for(int i = 0; i < LENGTH; i++) {
			GoalStackElement goalElement = getGoal(i);
			if(goalElement != null) {
				goal.add(new Integer(goalElement.value));
			}else {
				goal.add(null);
			}
		}
		return goal;
	}


	/**
	 * State�̏�Ԃ��o�͂��܂��B<BR>
	 * �o�͌`��<BR>
	 * [shared stack]<BR>
	 *  state<BR>
	 *   index:0 val:22<BR>
	 *   index:1 val:15<BR>
	 *   index:2 val:0<BR>
	 *   index:3 val:0<BR>
	 *   index:4 val:1<BR>
	 */
	public void printState() {
		System.out.println("");
		System.out.println(" state");
		for(int i = 0; i < LENGTH; i++) {
			System.out.println("  index:" + i + " val:" + getState(i));
		}
	}

	/**
	 * Goal�̏�Ԃ��o�͂��܂��B<BR>
	 * �o�͌`��<BR>
	 * [shared stack]<BR>
	 *  goal<BR>
	 *   index:0 | 26:200 | 18:100 |<BR>
	 *   index:1 | 1:200 | 10:100 |<BR>
	 *   index:2 | 6:100 |<BR>
	 *   index:3 | 1:1 | 2:101 |<BR>
	 *   index:4 | 1:101 | 1:101 |<BR>
	 */
	public void printGoalStack() {
		System.out.println("");
		System.out.println("[shared stack]");

		System.out.println(" goal");
		for(int i = 0; i < LENGTH; i++) {
			LinkedList goalStack = goalStackArray[i];
			int size = goalStack.size();
			StringBuffer sb = new StringBuffer();
			sb.append("  index:" + i + " | ");
			for(int m = 0; m < size; m++) {
				GoalStackElement elm = (GoalStackElement)goalStack.get(m);
				if(elm != null) {
					sb.append(elm.toString() + " | " );
				}else {
					sb.append(" no val | ");
				}
			}
			System.out.println(sb.toString());
		}
	}


}
