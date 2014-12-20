/**
 * FailAgentTreeElement.java
 * FailAgentTree�ň������̒P��
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.08
 */
package gsa;

import java.util.*;

/**
 * FailAgentTree�ň������̒P��<BR>
 * �c���[�̃m�[�h<BR>
 */
public class FailAgentTreeElement {

	/**
	 * �e�̃m�[�h(FailAgentTreeElement)
	 */
	public final FailAgentTreeElement parentElement;

	/**
	 * �G�[�W�F���gID
	 */
	public final int agid;

	/**
	 * �S�[���̒l
	 */
	public final Vector goal;

	/**
	 * ���s�����̌���
	 */
	public final int agr;

	/**
	 * �q�̃m�[�h(FailAgentTreeElement)�̃��X�g
	 */
	public final LinkedList next;

	/**
	 * �R���X�g���N�^
	 * @param FailAgentTreeElement parentElement �e�m�[�h�ւ̎Q��
	 * @param int agid �G�[�W�F���gID
	 * @param Vector goal �S�[��
	 * @param int agr ���s�����̌��ʂ�\��ID
	 */
	public FailAgentTreeElement(FailAgentTreeElement parentElement, int agid,
	        Vector goal, int agr) {
		this.parentElement = parentElement;
		this.agid = agid;
		this.goal = goal;
		this.agr = agr;
		next = new LinkedList();
	}

	/**
	 * ������FailAgentTreeElement���q�m�[�h�̃��X�g�ɒǉ����܂��B
	 * @param FailAgentTreeElement nextElement �q�ƂȂ�m�[�h
	 */
	public void addNext(FailAgentTreeElement nextElement) {
		next.add(nextElement);
	}

	/**
	 * �q�m�[�h�̃��X�g���������FailAgentTreeElement���폜���܂��B
	 * @param FailAgentTreeElement nextElement �폜����m�[�h
	 */
	public void removeNext(FailAgentTreeElement nextElement) {
		next.remove(nextElement);
	}

	/**
	 * �q��S�č폜���܂��B
	 */
	public void removeNextAll() {
		next.clear();
	}

	/**
	 * �q�m�[�h�̃��X�g����A�����Ŏw�肳�ꂽ�G�[�W�F���gID�����m�[�h��
	 * agr���擾���܂��B<BR>
	 * �����Ŏw�肳�ꂽ�G�[�W�F���g�̐ݒ肵���m�[�h���Ȃ����-1��Ԃ��܂��B<BR>
	 * @param int agid �G�[�W�F���g��ID
	 * @return int ���s�������ʂ�\��ID
	 */
	public int getChildAgr(int agid) {
		ListIterator li = next.listIterator();
		while(li.hasNext()) {
			FailAgentTreeElement e = (FailAgentTreeElement)li.next();
			if( e.agid == agid ) {
				return e.agr;
			}
		}
		return -1;
	}


	/**
	 * ���̃m�[�h�̕���������擾���܂��B
	 * @return String 
	 */
	public String toString() {
		return "id:" + agid + " goal:" + goal + " agr:" + agr;
	}


}

