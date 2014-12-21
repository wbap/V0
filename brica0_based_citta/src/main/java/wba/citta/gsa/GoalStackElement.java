/**
 * GoalStackElement.java
 * ���L�������ň����S�[���̏��̒P��
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.07
 */
package wba.citta.gsa;

/**
 * ���L�������ň����S�[���̏��̒P��
 */
public class GoalStackElement {

	/**
	 * �S�[���̒l
	 */
	public final int value;

	/**
	 * �S�[����ݒ肵���G�[�W�F���gID
	 */
	public final int agid;

	/**
	 * �R���X�g���N�^
	 * @param int value �S�[���̒l
	 * @param int agid  �ݒ肵���G�[�W�F���g��ID
	 */
	public GoalStackElement(int value, int agid) {
		this.value = value;
		this.agid = agid;
	}

	/**
	 * �S�[���̏���\�����܂��B
	 * @return String �S�[���̏��<BR>
	 * �\���`��  val:21 id:701
	 */
	public String toString() {
		String str = "val:" + value + " id:" + agid;
		return str;
	}

}

