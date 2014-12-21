/**
 * InterfaceAgent.java
 * ���ƔF�m�����̏������s�Ȃ��G�[�W�F���g�̃C���^�[�t�F�[�X�Ƃ��ď�Ԃ̕ϊ���	 * �̏������s���N���X
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * 2000.10 BSC miyamoto
 */
package wba.citta.cognitivedistance;

import java.util.*;
import java.io.*;


/**
 * ���ƔF�m�����̏������s�Ȃ��G�[�W�F���g�̃C���^�[�t�F�[�X�Ƃ��ď�Ԃ̕ϊ���
 * �̏������s���N���X�ł��B
 */
public class InterfaceAgent {

	/* �ŉ��w��LayeredAgent */
	private LayeredAgent bottomLayeredAgent;
	/* ��Ԃ���ID�֕ϊ����s�Ȃ��e�[�u�� */
	private Hashtable stateToId;
	/* ID�����Ԃ֋t�ϊ����s�Ȃ��e�[�u�� */
	private Vector idToState;

	/* ���(Vector)�̗v�f�����Œ�ɂ��邽�߂̕ϐ� */
	private int elementNum = -1;

	//////////////////////////////////////////////////////////
	// �R���X�g���N�^

	/**
	 * �R���X�g���N�^
	 * @param LayeredAgnet bottomLayeredAgent  �ŉ��w��LayeredAgent
	 */
	public InterfaceAgent(LayeredAgent bottomLayeredAgent) {
		this.bottomLayeredAgent = bottomLayeredAgent;
		/* �ϊ��e�[�u���̐��� */
//		stateToId = new Hashtable();
//		idToState = new Vector();
		stateToId = new Hashtable(10000);
		idToState = new Vector(10000);
	}


	//////////////////////////////////////////////////////////
	// private

	/**
	 * ��Ԃ̕ϊ����s�Ȃ��܂��B
	 * ������̏�ԂɑΉ�����m�[�h��ID���擾���܂��B
	 * @param Object state ������̏��
	 * @return Integer     �Ή�����m�[�h��ID
	 */
	private Integer getID(Vector state) {

		if(state == null) {
			return null;
		}

		/* ��ԂɑΉ�����ID���擾 */
		Integer id = (Integer)stateToId.get(state);

		return id;
	}


	/**
	 * ��Ԃ̋t�ϊ����s�Ȃ��܂��B
	 * �m�[�h��ID�ɑΉ�������̏�Ԃ��擾���܂��B
	 * @param Integer id  �m�[�h��ID
	 * @return Object     �Ή�������̏��
	 */
	private Vector getState(Integer id) {

		if(id == null) {
			return null;
		}

		/* int�ɕϊ� */
		int intID = id.intValue();

		/* ���݂��Ȃ�ID�̎w�� */
		if( (idToState.size()) <= (intID) ) {
			return null;
		}

		return (Vector)idToState.elementAt(intID);
	}

	/**
	 * �V������Ԃ��e�[�u���ɐݒ肵�A�Ή�����m�[�h��bottomLayeredAgent�ɐݒ�
	 * ���܂��B
	 * @param Object state �V�������
	 * @return Integer     �Ή�����V����ID
	 * @exception NullPointerException ��Ԃ�null�̏ꍇ
	 * @exception ElementNumberException ��Ԃ̗v�f�����s���ȏꍇ
	 */
	private  Integer newNode(Vector state) throws NullPointerException,
	        ElementNumberException{

		/* ���݂̏�Ԃ�null */
		if(state == null) {
			throw new NullPointerException();
		}

		/* �V������Ԃ�ϊ��p�̃e�[�u���ɐݒ� */
		int newId = idToState.size();

		/* �ŏ��ɓ��͂��ꂽ��Ԃ̗v�f����L���ȗv�f���ɐݒ� */ 
		if(newId == 0) {
			elementNum = state.size();
		}

		/* �v�f�����s���łȂ����`�F�b�N */
		if( !isSameSize(state) ) {
			throw new ElementNumberException();
		}

		idToState.addElement(state);

		Integer id = new Integer(newId);
		stateToId.put(state, id);

		/* LayeredAgent�̂P�w�ڂɏ�Ԃ��쐬 */
		bottomLayeredAgent.newNode(null);

		return id;
	}

	///////////////////////////////////////////////////////////////////
	// public

	/**
	 * �F�m�����̊w�K���s�Ȃ��܂��B
	 * @param Vector currentState ���݂̏��
	 * @exception NullPointerException �����Őݒ肳�ꂽVector��null�̏ꍇ
	 * @exception ElementNumberException �����Őݒ肳�ꂽVector�̃T�C�Y���s��
	 *                                   �ȏꍇ�B
	 */
	public void learn(Vector currentState) throws NullPointerException,
	        ElementNumberException {

//		System.out.println();
//		System.out.println("[Interface Agent] learn");
//		System.out.println("  currentState " + currentState);

		/* ��Ԃ̕ϊ� */
		Integer currentNodeID = getID(currentState);
		/* �o�^����Ă��Ȃ���ԂȂ�V����ID��ݒ� */
		if(currentNodeID == null) {
			currentNodeID = newNode(currentState);
		}

		/* 1�w�ڂ̊w�K */
		bottomLayeredAgent.learn(currentNodeID);
	}

	/**
	 * ���s�������s���A�����Ŏw�肳�ꂽ���݂̏�Ԃ���A�S�[���̏�Ԃ֑J�ڂ���
	 * ���ߎ��̏�Ԃ��擾���܂��B
	 * @param Vector currentState ���݂̏��
	 * @param Vector goalState    �S�[���̏��
	 * @return Vector             ���̏��
	 * @exception NullPointerException �����Őݒ肳�ꂽ���݂̏�Ԃ�null�̏ꍇ
	 * @exception ElementNumberException �����Őݒ肳�ꂽ���݂̏�Ԃ̃T�C�Y��
	 *                                   �s���ȏꍇ�B
	 */
	public Vector exec(Vector currentState, Vector goalState) throws
	        NullPointerException, ElementNumberException {

//		System.out.println();
//		System.out.println("[Interface Agent] exec");
//		System.out.println("  currentState " + currentState);
//		System.out.println("  goalState    " + goalState);

		/* ���݂̏�Ԃ�null�łȂ����`�F�b�N */
		if( currentState == null ) {
			throw new NullPointerException();
		}

		/* �v�f�����s���łȂ����`�F�b�N */
		if( !isSameSize(currentState) ) {
			throw new ElementNumberException();
		}

		/* �S�[�����Ȃ���Ώ������Ȃ� */
		if(goalState == null) {
			// 2001.04.05 �ǉ� bsc miyamoto
			/* �ێ����Ă���O�T�C�N���̏����N���A */
			bottomLayeredAgent.resetOldValue();
			return null;
		}

		/* ��Ԃ̕ϊ� */
		Integer currentNodeID = getID(currentState);

		// 2001.06.07 �C�� �S�[����null�̗v�f������ꍇ�̂ݕ⊮����
		// 2001.03.29 �ǉ�
		/* �擾�����S�[�����甲���Ă��镔����⊮ */
		Vector newGoalState = null;
		if( checkNullElement(goalState) ) {
			newGoalState = goalState;
		}else {
			// 2001.08.14 �C�� miyamoto ���B�\�ȃS�[����I���B
//			newGoalState = getSameStates(goalState);
			newGoalState = getSameStates(currentState, goalState);
		}

//		System.out.println("  newgoalState    " + newGoalState);

		Integer goalNodeID = getID(newGoalState);

		/* �ړ���̏�Ԃ̎擾 */
		Integer nextNodeID = bottomLayeredAgent.exec(currentNodeID,
		        goalNodeID);
		/* �擾�����ړ���̏�Ԃ��t�ϊ� */
		Vector nextState = getState(nextNodeID);

//		System.out.println();
//		System.out.println("[Interface Agent] exec");
//		System.out.println("  nextState " + nextState);

		return nextState;
	}


	/**
	 * ���m��Ȃ���Ԃֈړ����邽�߂̒��ڈړ��\�ȏ�Ԃ��擾���܂��B
	 * @param Vector currentState ���݂̏��
	 * @return Vector             ���̏��
	 */ 
	public Vector novelSearch(Vector currentState) {

		/* ��ʕ\�� */
//		System.out.println();
//		System.out.println("[Interface Agent] novelSearch");
//		System.out.println("  currentState " + currentState);

		/* ��Ԃ̕ϊ� */
		Integer currentNodeID = getID(currentState);

		/* �ړ���̏�Ԃ̎擾 */
		Integer nextNodeID = bottomLayeredAgent.novelSearch(currentNodeID);

		/* �擾�����ړ���̏�Ԃ��t�ϊ� */
		Vector nextState = getState(nextNodeID);

//		System.out.println();
//		System.out.println("[Interface Agent] novelSearch");
//		System.out.println("  nextState " + nextState);

		return nextState;
	}


	/**
	 * �V�K�T�����s�Ȃ����߂̃J�E���^�����Z�b�g���܂��B
	 */
	public void counterReset() {
		bottomLayeredAgent.resetUpperAndThisLayerFamiliarCount();
	}


	/**
	 * �w�K�f�[�^��ǂݍ��݂܂��B
	 * ���̏�Ԃ���CognitiveDistance�p�̏�Ԃ֕ϊ��p�A�t�ϊ��p���s�Ȃ�
	 * �e�[�u����ǂݍ��݂܂��B
	 * @param ObjectInputStream oInputStream
	 */
	public void load(ObjectInputStream oInputStream) throws IOException,
	        ClassNotFoundException {
		/* ��Ԃ���ID�ւ̕ϊ����s�Ȃ��e�[�u����ǂݍ��� */
		stateToId = (Hashtable)oInputStream.readObject();
		/* ID�����Ԃւ̕ϊ����s�Ȃ��e�[�u����Ǎ��� */
		idToState = (Vector)oInputStream.readObject();
		/* �L���ȏ�Ԃ̗v�f����Ǎ��� */
		elementNum = ((Integer)oInputStream.readObject()).intValue();
	}


	/**
	 * �w�K�f�[�^��ۑ����܂��B
	 * ���̏�Ԃ���CognitiveDistance�p�̏�Ԃ֕ϊ��p�A�t�ϊ��p���s�Ȃ�
	 * �e�[�u����ۑ����܂��B
	 * @param ObjectOutputStream oOutputStream
	 */
	public void save(ObjectOutputStream oOutputStream) throws IOException  {
		/* ��Ԃ���ID�ւ̕ϊ����s�Ȃ��e�[�u����ۑ� */
		oOutputStream.writeObject(stateToId);
		/* ID�����Ԃւ̕ϊ����s�Ȃ��e�[�u����ۑ� */
		oOutputStream.writeObject(idToState);
		/* �L���ȏ�Ԃ̗v�f����ۑ� */
		oOutputStream.writeObject(new Integer(elementNum));
	}

	///////////////////////////////////////////////////////////////////
	// �f�o�b�N�p�̏��̎擾�A����̐���Ɏg�p���郁�\�b�h

	/**
	 * �e�w��StateBuffer�ɐݒ肳��Ă��闚�����N���A���܂��B
	 */
	public void reset() {
		bottomLayeredAgent.reset();
	}

	/**
	 * �������Ŏw�肳�ꂽ��ԂɑΉ�����A�������Ŏw�肳�ꂽ�w�ł̃m�[�h��
	 * �擾���܂��B
	 * @param Vector state ���ł̏��
	 * @param int layer    �m�[�h���擾���郌�C��
	 * @return Node        �m�[�h
	 */
	public Node getNode(Vector state, int layer) {
		Integer id = getID(state);
		return bottomLayeredAgent.getNode(id, layer);
	}

	/**
	 * ID�����Ԃ֋t�ϊ����s�Ȃ�Vector�̃e�[�u�����擾���܂��B
	 * @return Vector ID�����Ԃ֋t�ϊ����s�Ȃ��e�[�u��
	 */
	public Vector getIdToState() {
		return idToState;
	}

	//////////////////////////////////////////////////////////////////////////
	// 2001.03 �ǉ��@�\ 

	/**
	 * �e�[�u���ɓo�^�ς݂̏�Ԃ���A�����Őݒ肳�ꂽ��Ԃ̗L���ȗv�f������
	 * ��Ԃ��擾���܂��B 
	 * @param Vector a    
	 * @return Vector   �L���ȗv�f�̈ʒu�̒l���������
	 */
	private Vector getSameStates(Vector a) {
		/* �e�[�u�����̑S�Ă̏�Ԃɂ��ă`�F�b�N */
		for(int i = 0; i < idToState.size(); i++) {
			/* �L���ȗv�f�̒l�������Ȃ�΁A���X�g�ɒǉ� */
			// 2001.05.24 �C�� miyamoto �Â��o�[�W������Java�ɑΉ�
//			if( checkValidElement(a, (Vector)idToState.get(i)) ) {
//				return (Vector)idToState.get(i);
			if( checkValidElement(a, (Vector)idToState.elementAt(i)) ) {
				return (Vector)idToState.elementAt(i);
			}
		}
		return null;
	}

	/**
	 * ��Ԗڂ̈����̗L���ȗv�f���A��Ԗڂ̈����̓����ʒu�̗v�f�Ɠ�����
	 * �ǂ����`�F�b�N���܂��B
	 * @param Vector a
	 * @param Vector b
	 * @return boolean  �L���ȗv�f�̒l�������ꍇ��true
	 *                  �قȂ�v�f������ꍇ��false
	 */
	private boolean checkValidElement(Vector a, Vector b) {
		// 2001.05.29 �ǉ� �����Ƃ�������ɃT�C�Y���������Ƃ�ǉ�
		if( a.size() != b.size() ) {
			return false;
		}
		// �����܂�

		/* �S�v�f���`�F�b�N */
		for(int i = 0; i < a.size(); i++) {
			/* �v�f���L���ȏꍇ�́A���̈ʒu�̗v�f���������`�F�b�N */
			// 2001.05.24 �C�� miyamoto �Â��o�[�W������Java�ɑΉ�
//			if(a.get(i) != null) {
//				if(!a.get(i).equals(b.get(i))) {
			if(a.elementAt(i) != null) {
				if(!a.elementAt(i).equals(b.elementAt(i))) {
					return false;
				}
			}
		}
		return true;
	}


	// 2001.05.29 �ǉ� miyamoto
	/**
	 * ������Vector�̗v�f�����L�����ǂ����`�F�b�N���܂��B
	 * @Vector v
	 */
	private boolean isSameSize(Vector v) {
		if(v.size() != elementNum) {
			return false;
		}
		return true;
	}

	/**
	 * ������Vector�̗v�f��null�̗v�f���Ȃ����`�F�b�N���܂��B
	 * @param Vector v 
	 * @return boolean  true:null�̗v�f�Ȃ�   false:null�̗v�f����
	 */
	private boolean checkNullElement(Vector v) {
		for(int i = 0; i < v.size(); i++) {
			if( v.get(i) == null ) {
				return false;
			}
		}
		return true;
	}

	// 2001.08.14 �ǉ� miyamoto
	/**
	 * �e�[�u���ɓo�^�ς݂̏�Ԃ���A�����Őݒ肳�ꂽ��Ԃ̗L���ȗv�f������
	 * ��Ԃ��擾���܂��B 
	 * @param Vector a    
	 * @return Vector   �L���ȗv�f�̈ʒu�̒l���������
	 */
	private Vector getSameStates(Vector a, Vector b) {
		/* �e�[�u�����̑S�Ă̏�Ԃɂ��ă`�F�b�N */
//		for(int i = 0; i < idToState.size(); i++) {
//			Vector element = (Vector)idToState.elementAt(i);
//			if( checkValidElement(b, element) ) {
//				if( isReach(a, element) ) {
//					return element;
//				}
//			}
//		}
		// 2001.09.04 �C�� miyamoto
		ListIterator li = idToState.listIterator();
		while(li.hasNext()) {
			Vector element = (Vector)li.next();
//System.out.println(" element " + element);
			if( checkValidElement(b, element) ) {
//System.out.println("  check element " + element);
				if( isReach(a, element) ) {
//System.out.println("   true");
					return element;
				}
//System.out.println("   false");
			}
		}
		return null;
	}

	/**
	 * �������̏�Ԃ���������̏�Ԃւ̓��B�\���𒲂ׂ܂��B
	 * @param Vector a
	 * @param Vector b
	 * @return boolean true ���B�\ false ���B�s�\
	 */
	public boolean isReach(Vector a, Vector b) {
		/* ��Ԃ̕ϊ� */
		Integer aID = getID(a);
		Integer bID = getID(b);
		boolean isReach = bottomLayeredAgent.isReach(aID,bID);
		return isReach;
	} 

	/**
	 * �e�w���Ƃɕێ����Ă���ȑO�̃T�C�N���̏����N���A���܂��B
	 */
	public void resetOldValue() {
		bottomLayeredAgent.resetOldValue();
	}

}


