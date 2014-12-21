/**
 * TransitionCounter.java
 * ���ڈړ��\�ȃm�[�h�ւ̑J�ڂ̉񐔂��Ǘ�����N���X
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * 2000.10 BSC miyamoto
 */
package wba.citta.cognitivedistance;

import java.io.*;

/**
 * ���ڈړ��\�ȃm�[�h�ւ̑J�ڂ̉񐔂��Ǘ�����N���X�ł��B
 */
public class TransitionCounter implements Serializable {

	private Integer nextNodeID;  /* �ړ���̃m�[�h��ID */
	private int trasitionCount;  /* ���̃m�[�h�ւ̈ړ��� */

	/**
	 * �R���X�g���N�^
	 * @param Integer nextNodeID  ���ڈړ��\�ȃm�[�h��ID
	 */
	public TransitionCounter(Integer nextNodeID) {
		this.nextNodeID = nextNodeID;
		trasitionCount = 1;
	}


	/**
	 * �ړ��񐔂��P���������܂��B
	 */
	public void count() {
		trasitionCount++;
	}

	/**
	 * �ړ��񐔂��擾���܂��B
	 * @return int   �ړ���
	 */
	public int getCount() {
		return trasitionCount;
	}
}
