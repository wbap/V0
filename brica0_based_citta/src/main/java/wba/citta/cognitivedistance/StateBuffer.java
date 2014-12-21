/**
 * StateBuffer.java
 * ��Ԃ̗�����ێ�����N���X
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * 2000.10 BSC miyamoto 
 */
package wba.citta.cognitivedistance;

import java.util.*;

/**
 *	��Ԃ̗�����ێ�����N���X�ł��B
 *	�R���X�g���N�^�̈����Őݒ肳�ꂽ�������A�m�[�hID��ێ����܂��B
 *	�ő�̗v�f�����z�����ꍇ�A�Â����ɍ폜����܂��B
 */
public class StateBuffer extends LinkedList {

	/* �ێ�����ő�̗v�f�� */
	private int maxCDLngth;

	/**
	 * �R���X�g���N�^
	 * @param int maxCDLngth �ێ�����ő�̗v�f��
	 */
	public StateBuffer(int maxCDLngth) {
		this.maxCDLngth = maxCDLngth;
	}


	/**
	 *	�m�[�hID�����X�g�ɒǉ����܂��B
	 *	@param Integer stateID �m�[�hID
	 */
	public void add(Integer stateID){
		/* ���X�g�ɒǉ� */
		super.add(stateID);
		/* ���X�g�̃T�C�Y��MAXSIZE�ɒ��� */
		if(size() > maxCDLngth){
			removeFirst();
		}
	}

	/* ���X�g���̗v�f��\�� */
// 2001.05.25 �폜 miyamoto �Â��o�[�W������java�ɑΉ�
// LinkedList��Vector�ɕύX��������toString���I�[�o�[���C�h�ł��Ȃ�(�K�v�Ȃ�)
//	public String toString() {
//		StringBuffer sb = new StringBuffer();
//		ListIterator li = listIterator();
//		while(li.hasNext()) {
//			Integer id = (Integer)li.next();
//			sb.append(id + ",");
//		}
//		return sb.toString();
//	}
}

