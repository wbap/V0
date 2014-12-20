/**
 * StateList.java
 * �m�[�h�̃��X�g�Ɛe�q�֌W�ɂ��ĊǗ�����N���X
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * 2000.10 BSC miyamoto
 */
package cognitivedistance;

import java.util.*;

/**
 * �m�[�h�̃��X�g�Ƃ��̐e�q�֌W�ɂ��ĊǗ�����N���X�ł��B
 */
public class StateList extends LinkedList {

	private Integer parentNodeID;   /* �e�̃m�[�hID */
	private StateList parentList;   /* �e�m�[�h�̑����郊�X�g */

	/**
	 * �R���X�g���N�^
	 * @param LinkedList stateList       �m�[�hID�̃��X�g
	 * @param Integer parentNodeID �e�̃m�[�hID
	 * @param StateList parentList       �e�̃m�[�h��������StateList
	 */
	public StateList(LinkedList stateList, Integer parentNodeID,
	        StateList parentList) {
		super(stateList);
		this.parentNodeID = parentNodeID;
		this.parentList = parentList;
	}

	/**
	 * �e�̃m�[�hID���擾���܂��B
	 * @return Integer �e�̃m�[�hID
	 */
	public Integer getParentNodeID() {
		return parentNodeID;
	}

	/**
	 * �e�̃m�[�h��������StateList���擾���܂��B
	 * @return StateList
	 */
	public StateList getParentList() {
		return parentList;
	}

	/**
	 * ���̃��X�g�̑S�v�f��String�Ŏ擾���܂��B
	 * @return String
	 */
// 2001.05.25 �폜 miyamoto �Â��o�[�W������java�ɑΉ�
// LinkedList��Vector�ɕύX��������toString���I�[�o�[���C�h�ł��Ȃ�(�K�v�Ȃ�)
//	public String toString(){
//		StringBuffer sb = new StringBuffer();
//		ListIterator li = listIterator();
//		while(li.hasNext()) {
//			Integer state = (Integer)li.next();
//			sb.append(" ");
//			sb.append(state);
//		}
//		return sb.toString();
//	}

}


