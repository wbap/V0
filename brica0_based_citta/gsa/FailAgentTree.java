/**
 * FailAgentTree.java
 * �X�^�b�N�̃S�[���̏�ԁA�����ł��Ȃ��S�[���A�S�[���������ł��Ȃ��G�[�W�F���g
 * �ɂ��ăc���[�\���ŊǗ�����N���X
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.08
 */
package gsa;

import java.util.*;
import gsa.viewer.*;

/**
 * �X�^�b�N�̃S�[���̏�ԁA���s�S�[���A�T�u�S�[�����o�̓G�[�W�F���g
 * �ɂ��ăc���[�\���ŊǗ�����N���X�B<BR>
 * �c���[��̌��݂̈ʒu���J�����g�Ƃ��ď������s���܂��B<BR>
 * ���̃c���[�ň����m�[�h�Ƃ��āAFailAgentTreeElement���g�p���܂��B<BR>
 */
public class FailAgentTree {

	private FailAgentTreeElement currentElement = null;
	private FailAgentTreeElement rootElement = null;

	/* ���s�G�[�W�F���g�̏�Ԃ�\������viewer */
	private TreeViewer viewer = null;

	/**
	 * �R���X�g���N�^
	 * @param boolean isShowViewer �c���[�̏�Ԃ��O���t�B�b�N�\�����邩
	 */
	public FailAgentTree(boolean isShowViewer) {
		rootElement = new FailAgentTreeElement(null, -1, null, 0);
		currentElement = rootElement;

		if(isShowViewer == true) {
			viewer = new TreeViewer(rootElement);
		}
	}


	//////////////////////////////////////////////////////////////////////
	// public

	/**
	 * �J�����g�m�[�h�Ɏq�m�[�h��ǉ����܂��B<BR>
	 * �ǉ��m�[�h�ɂ̓G�[�W�F���gID�ƃS�[����ݒ肵�܂��Bagr�ɂ�0��ݒ肵�܂��B
	 * <BR>
	 * �J�����g�̈ʒu�́A�ǉ������V���ȃS�[���Ɉړ����܂��B<BR>
	 * @param int agid �G�[�W�F���g��ID
	 * @param Vector goal �S�[��
	 */
	public void addTreeNode(int agid, Vector goal) {
		FailAgentTreeElement newFailAgentTreeElement
		        = new FailAgentTreeElement(currentElement, agid, goal, 0);
		currentElement.addNext(newFailAgentTreeElement);
		currentElement = newFailAgentTreeElement;
		if(viewer != null) {
			viewer.setCurrentElement(currentElement);
			viewer.repaint();
		}
	}

	/**
	 * �J�����g�m�[�h�Ɏq�m�[�h��ǉ����܂��B<BR>
	 * �ǉ�����m�[�h�ɂ̓G�[�W�F���gID��agr��ݒ肵�܂��B�S�[����null�ɂȂ�܂�	 * �B<BR>
	 * �J�����g�̈ʒu�͈ړ����܂���B<BR>
	 * @param int agid �G�[�W�F���g��ID
	 * @param int agr ���s�����̌��ʂ�\��ID
	 */
	public void addTreeNode(int agid, int agr) {
		/* ���[�g�ɂ͎��s�G�[�W�F���g��ǉ����Ȃ� */
		if(currentElement != rootElement) {
			if(getChildAgr(agid) == -1) {
				FailAgentTreeElement newFailAgentTreeElement
				       = new FailAgentTreeElement(currentElement, agid, null,
				       agr);
				currentElement.addNext(newFailAgentTreeElement);
				if(viewer != null) {
					viewer.setCurrentElement(currentElement);
					viewer.repaint();
				}
			}
		}
	}

	/**
	 * �c���[�̃J�����g�m�[�h���폜���܂��B<BR>
	 * �J�����g�͐e�m�[�h�Ɉړ����܂��B<BR>
	 */
	public void removeCurrent() {
		FailAgentTreeElement parent = currentElement.parentElement;
		parent.removeNext(currentElement);
		currentElement = parent;
		if(viewer != null) {
			viewer.setCurrentElement(currentElement);
			viewer.repaint();
		}
	}

	/**
	 * �c���[�̃J�����g�m�[�h��e�ֈړ����܂��B<BR>
	 */
	public void moveParent() {
		currentElement = currentElement.parentElement;
		if(viewer != null) {
			viewer.setCurrentElement(currentElement);
			viewer.repaint();
		}
	}

	/**
	 * �J�����g�m�[�h�̎q�m�[�h����A�����Ŏw�肳�ꂽ�G�[�W�F���gID������
	 * �m�[�h��T���A����agr���擾���܂��B<BR>
	 * �����Ŏw�肳�ꂽ�G�[�W�F���gID�����m�[�h���Ȃ����-1��Ԃ��܂��B<BR>
	 * @param int agid �G�[�W�F���g��ID
	 * @return int ���s�������ʂ�\��ID
	 */
	public int getChildAgr(int agid) {
		if(currentElement != null) {
			return currentElement.getChildAgr(agid);
		}
		if(viewer != null) {
			viewer.setCurrentElement(currentElement);
			viewer.repaint();
		}
		return -1;
	}

	/**
	 * �c���[�̃m�[�h��S�č폜���܂��B
	 */
	public void clear() {
		currentElement = rootElement;
		currentElement.removeNextAll();
		if(viewer != null) {
			viewer.setCurrentElement(currentElement);
			viewer.repaint();
		}
	}


	/**
	 * �c���[��ɂ���m�[�h�ɁA�����Ŏw�肳�ꂽ�G�[�W�F���gID�ƃS�[��������
	 * �m�[�h�����邩�ǂ������肵�܂��B<BR>
	 * �m�[�h�������true�A�Ȃ����false��Ԃ��܂��B<BR>
	 * @param int agid �G�[�W�F���gID
	 * @param Vector goal �S�[��
	 * @return boolean true�F�m�[�h������ false�F�m�[�h���Ȃ�
	 */
	public boolean isContain(int agid, Vector goal) {
		boolean b = isContainChild(rootElement, agid, goal);
		return b;
	}


	/**
	 * �c���[�̏�Ԃ�\�����܂��B
	 */
	public void printTree() {
		System.out.println("");
		System.out.println("[ goal tree ]");
		System.out.println(getTree(0, rootElement));
	}

	//////////////////////////////////////////////////////////////////////
	// private 

	/**
	 * �������Ŏw�肳�ꂽ�m�[�h�̎q�ɑ������E��O�����̃G�[�W�F���gID�A
	 * �S�[�������m�[�h�������true��Ԃ��B<BR>
	 * �ċA�I�ȌĂяo�����s���A�[���q�m�[�h�܂Ń`�F�b�N����B<BR>
	 * @param FailAgentTreeElement Element 
	 * @param int agid 
	 * @param Vector goal
	 */
	private boolean isContainChild(FailAgentTreeElement element, int agid, 
	        Vector goal) {
// ���݂̏������@�ł́A�����Ɏ��Ԃ�������B
// ���̃N���X�ŁAAgent�̔z��ւ̎Q�Ƃ������A�e�m�[�h�̃G�[�W�F���gID�ɑΉ�����
// �G�[�W�F���g�̂ݏ������s�Ȃ��悤�ɂ��邱�ƂőΉ��\�����AAgent�̔z��ւ�
// �Q�Ƃ��������邱�Ƃ͂��܂�s�Ȃ������Ȃ��B
		ListIterator li = element.next.listIterator();
		while(li.hasNext()) {
			FailAgentTreeElement nextElement = (FailAgentTreeElement)li.next();
			if( (nextElement.goal != null) && (nextElement.agid==agid) &&
			        (nextElement.goal.equals(goal)) ){
				return true;
			}
			boolean b = isContainChild(nextElement, agid, goal);
			if(b == true) {
				return true;
			}
		}
		return false;
	}

	/**
	 * �����Ŏw�肳�ꂽ�m�[�h�̎q�Ɋւ��Ă̏��𕶎���Ŏ擾���܂��B
	 */
	private String getTree(int depth, FailAgentTreeElement goal) {
		StringBuffer stringBuffer = new StringBuffer();
		ListIterator li = goal.next.listIterator();
		while(li.hasNext()) {
			FailAgentTreeElement nextGoal = (FailAgentTreeElement)li.next();
			stringBuffer.append(getString(depth, nextGoal));
			if(nextGoal == currentElement) {
				stringBuffer.append("<--- *");
			}
			stringBuffer.append("\n");
			stringBuffer.append(getTree(depth+1, nextGoal));
		}
		return stringBuffer.toString();
	}

	private String getString(int depth, FailAgentTreeElement goal) {
		StringBuffer stringBuffer = new StringBuffer();
		for(int i = 0; i < depth; i++) {
			stringBuffer.append("  ");
		}
		stringBuffer.append(goal.toString());
		return stringBuffer.toString();
	}

}

