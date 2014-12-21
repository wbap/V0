/**
 * Node.java
 * �m�[�h�̏����Ǘ�����N���X
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * 2000.10 BSC miyamoto
 */
package wba.citta.cognitivedistance;

import java.util.*;
import java.io.*;

/**
 * �m�[�h�̏����Ǘ�����N���X�ł��B
 */
public class Node implements Serializable{

	private Integer ID;            /* �����ID */
	private Integer upperID;       /* ��ʑw�ł̏�� */
	private Integer lowerID;       /* ���ʑw�ł̏�� */
	private int toLandmarkLngth;   /* �����h�}�[�N�܂ł̃X�e�b�v�� */

	private boolean valid;               /* ���̏�Ԃ̗L���� */
	private Integer referenceNodeID;     /* ���̃m�[�h�������ȏꍇ�̎Q�Ɛ� */
	/* �Q�Ɛ�̉��̑w�ł̃����h�}�[�N�܂ł̋��� */
	private int referenceLowerToLandmarkLngth;

	/* �����Ԃ܂ł̋�����ێ�����e�[�u�� Key=nodeID Element=���� */
	private Hashtable cognitiveDistance;

	/* ���ڈړ��\�ȏ�Ԃ̃��X�g �ړ��\��nodeID�̃��X�g*/
	private LinkedList forwardNodeIDList;  /* ������ */
	private LinkedList inverseNodeIDList;  /* �t���� */

	private int visitCount;              /* ���̏�Ԃւ̈ړ��� */
	/* ��ԑJ�ڂɊւ�����̃��X�g */
	private Hashtable transitionCounterTable;

	/**
	 * �w�K�\�ȍő�̋���
	 * ���̒����{�P��StateBuffer�̃T�C�Y
	 */
	public static int maxCDLngth = 10;

	///////////////////////////////////////////////////////////
	// �R���X�g���N�^

	/**
	 * �R���X�g���N�^
	 * @param Integer ID      ���̃m�[�h��ID
	 * @param Integer lowerID ���ʑw�ł�ID
	 */
	public Node(Integer ID, Integer lowerID) {

		/* �l�̏����� */
		this.ID = ID;
		this.lowerID = lowerID;
		valid = true;
		toLandmarkLngth = -1;

//		cognitiveDistance = new Hashtable();
		cognitiveDistance = new Hashtable(1000); // ���s
		forwardNodeIDList = new LinkedList();
		inverseNodeIDList = new LinkedList();
		transitionCounterTable = new Hashtable();
	}


	///////////////////////////////////////////////////////////
	// ���̎擾

	/**
	 * ���̃m�[�h��ID���擾���܂��B
	 */
	public Integer getID() {
		return ID;
	}

	/**
	 * ���̃m�[�h�̏�ʑw�ł�ID���擾���܂��B
	 * @param Integer   ��ʑw�ł�ID
	 */
	public Integer getUpperID() {
		return upperID;
	}

	/**
	 * ���̃m�[�h�̉��ʑw�ł�ID���擾���܂��B
	 * @param Integer   ���ʑw�ł�ID
	 */
	public Integer getLowerID() {
		return lowerID;
	}

	/**
	 * �����h�}�[�N�܂ł̃X�e�b�v�����擾���܂��B
	 * @return int �X�e�b�v��
	 */
	public int getToLandmarkStep() {
		return toLandmarkLngth;
	}


	/**
	 * �����Őݒ肳�ꂽ�m�[�h�܂ł̋������擾���܂��B
	 *	@param Integer nodeID �������擾����m�[�h��ID
	 *	@return int           �m�[�h�܂ł̋���(�X�e�b�v��)
	 *                        �����Őݒ肳�ꂽ�m�[�h�ւ̋������w�K����Ă��Ȃ�
	 *                        �ꍇ��-1
	 *                        ���̃m�[�h�Ɠ����Ȃ�0��Ԃ��B
	 */
	public int getCognitiveDistance(Integer nodeID) {

		/* �Q�̏�Ԃ������Ȃ狗����0�ɂ��� */
		if(getID().equals(nodeID)) {
			return 0;
		}

		/* �Ή�����l������΂��̒l���A�Ȃ����-1��Ԃ� */
		Integer distanceObj = (Integer)cognitiveDistance.get(nodeID);
		int distance = -1;
		if(distanceObj != null){
			distance = distanceObj.intValue();
		}

		/*
		 * maxCDLngth�𓮓I�ɕω��������ꍇ�ɁA���łɊw�K�ς݂�CD�̂���
		 * �ω����������maxCDLngth��蒷��CD�͎g�p���Ȃ��悤�ɂ��邽�߂̏���
		 */
// 2001.09.07 �h�A�L�[�̃f���p�ɉ��ɃR�����g�A�E�g
//		if(distance > maxCDLngth) {
//			distance = -1;
//		}

		return distance;
	}

	/**
	 * �������ɒ��ڈړ��\�ȃm�[�h��ID�̃��X�g���擾���܂��B
	 * @return LinkedList ���ڈړ��\�ȃm�[�h��ID�̃��X�g
	 */
	public LinkedList getForwardNodeIDList() {
		return forwardNodeIDList;
	}

	/**
	 * �t�����ɒ��ڈړ��\�ȃm�[�h��ID�̃��X�g���擾���܂��B
	 * @return LinkedList ���ڈړ��\�ȃm�[�h��ID�̃��X�g
	 */
	public LinkedList getInverseNodeIDList() {
		return inverseNodeIDList;
	}

	/**
	 * ���̃m�[�h���L�����ǂ������`�F�b�N���܂��B
	 * @param boolean  true �L��  false ����
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * �Q�Ɛ�̃m�[�hID���擾���܂��B
	 * @return Integer �Q�Ɛ�̃m�[�hID
	 */
	public Integer getReferenceNodeID() {
		return referenceNodeID;
	}

	/**
	 * �Q�Ɛ�̃m�[�h�̃����h�}�[�N�܂ł̃X�e�b�v�����擾���܂��B
	 * @return int �Q�Ɛ�̃m�[�h�̃����h�}�[�N�܂ł̃X�e�b�v��
	 */
	public int getRefarenceStep() {
		return referenceLowerToLandmarkLngth;
	}


	/**
	 * ���̃m�[�h���狗���̕�����(�w�K���Ă���)�m�[�h��ID�����X�g�Ŏ擾���܂��B
	 * @return LinkedList �����̕�����m�[�h��ID�̃��X�g
	 */
	public LinkedList getCDKeys() {
// �ʂ�LinkedList�������Ă��������悢��
		LinkedList ll = new LinkedList();
		/* �n�b�V������S�L�[���擾 */
		Enumeration e = cognitiveDistance.keys();
		while(e.hasMoreElements()) {
			ll.add(e.nextElement());
		}
		return ll;
	} 

	/**
	 * ���̃m�[�h�ւ̈ړ��񐔂��擾���܂��B
	 * @return int  �ړ���
	 */
	public int getVisitCount() {
		return visitCount;
	}

	///////////////////////////////////////////////////////////
	// ���̐ݒ�

	/**
	 * ���̃m�[�h�̏�ʑw�ł̏�ԂɊւ������ݒ肵�܂��B
	 * @param Integer upperID ��ʑw�ł�ID
	 * @param int toUpperStep �����h�}�[�N�܂ł̃X�e�b�v��
	 */
	public void setUpperIDAndStep(Integer upperID, int toLandmarkLngth) {
		this.upperID = upperID;
		this.toLandmarkLngth = toLandmarkLngth;
	}


	/**
	 * �����Őݒ肳�ꂽ�m�[�h�܂ł̋������w�K���܂��B
	 * @param Integer nodeID �������w�K����m�[�h��ID
	 * @param int distance   �m�[�h�܂ł̋���
	 */
	public void setCognitiveDistance(Integer nodeID, int distance) {

// �T�����̒����������w�K?
// 2001.09.07 �h�A�L�[�̃f���p�ɉ��ɃR�����g�A�E�g
//		/* �w�K����CognitiveDistance�̍ő勗�� */
//		if(distance > maxCDLngth) {
//			return;
//		}

		/* ������ԂȂ狗�����O�ɐݒ� */ 
// ������Ԃł����������̂܂܊w�K������B������ԂȂ狗���̎擾���ɂO��Ԃ��B
//		if(getID().equals(nodeID)) {
//			cognitiveDistance.put(nodeID, new Integer(0));
//		}else{
			/* �L�[�ɑΉ�����l���Ȃ����A�l���������ꍇ�V����������ݒ� */
			Integer distanceObj = (Integer)cognitiveDistance.get(nodeID);
			if((distanceObj==null) || (distanceObj.intValue()>distance)) {
				cognitiveDistance.put(nodeID, new Integer(distance));
			}
//		}
	}


	/**
	 * �������ɒ��ڈړ��\�ȃm�[�h���w�K���܂��B
	 * @param Integer nodeID ���ڈړ��\�ȃm�[�h��ID
	 */
	public void setForwardNode(Integer nodeID) {

		/* ���X�g��T�����A���łɓo�^�ς݂Ȃ烊�X�g�ɒǉ����Ȃ� */
		ListIterator lIterater = forwardNodeIDList.listIterator(0);
		while(lIterater.hasNext()) {
			if(((Integer)lIterater.next()).equals(nodeID)) {
				return;
			}
		}

		/* �l�����X�g�ɒǉ� */
		forwardNodeIDList.add(nodeID);

	}


	/**
	 * �t�����ɒ��ڈړ��\�ȃm�[�h���w�K���܂��B
	 * @param Integer nodeID ���ڈړ��\�ȃm�[�h��ID
	 */
	public void setInverseNode(Integer nodeID) {

		/* ���X�g���������A���łɓo�^�ς݂Ȃ烊�X�g�ɒǉ����Ȃ� */
		ListIterator lIterater = inverseNodeIDList.listIterator(0);
		while(lIterater.hasNext()) {
			if(((Integer)lIterater.next()).equals(nodeID)) {
				return;
			}
		}

		/* �l�����X�g�ɒǉ� */
		inverseNodeIDList.add(nodeID);

	}


	/**
	 * ���̃m�[�h�ւ̈ړ��񐔂��J�E���g���܂��B
	 * @param Integer nextNodeID ���̃m�[�h����ړ�������̃m�[�h��ID
	 */
	public void countVisitCount(Integer nextNodeID) {

		visitCount++;
		/* ���̃m�[�h����ړ�������̏�Ԃ̊Ǘ� */
		TransitionCounter tc
		        = (TransitionCounter)transitionCounterTable.get(nextNodeID);
		if(tc == null) {
			tc = new TransitionCounter(nextNodeID);
			transitionCounterTable.put(nextNodeID, tc);
		}else {
			/* �񐔂̃J�E���g */
			tc.count();
		}
	}


	/**
	 * ���̃m�[�h�𖳌��ɂ��܂��B
	 * ���̃m�[�h����ʑw�̃����h�}�[�N�̏ꍇ�͖����ɂ��܂���B
	 * @param Integer refID ����̎Q�Ɛ��ID
	 * @param int refStep   �Q�Ɛ�̃m�[�h�܂ł̋���
	 * @return boolean      true �����ɂ����ꍇ false �����ɂł��Ȃ������ꍇ
	 */
	public boolean delete(Integer referenceNodeID,
	         int referenceLowerToLandmarkLngth) {
		/* ���炪��ʑw�ւ̃����h�}�[�N�̏ꍇ�͍폜���Ȃ� */
		if(toLandmarkLngth == 0) {
			return false;
		}

		valid = false;
		/* �Q�Ɛ�̐ݒ� */
		this.referenceNodeID = referenceNodeID;
		this.referenceLowerToLandmarkLngth = referenceLowerToLandmarkLngth;
		return true;
	}


	/**
	 * �������Ɉړ��\�ȃm�[�h�̃��X�g���ɂ��̃m�[�h�Ɠ����m�[�h������ꍇ��
	 * ���̃m�[�h�����X�g����폜���܂��B
	 */
	public void removeSameForwardNodeID() {
		ListIterator li = forwardNodeIDList.listIterator();
		while(li.hasNext()) {
			Integer id = (Integer)li.next();
			if(id.equals(ID)) {
				li.remove();
			}
		}
	}

	/**
	 * �t�����Ɉړ��\�ȃm�[�h�̃��X�g���ɂ��̃m�[�h�Ɠ����m�[�h������ꍇ��
	 * ���̃m�[�h�����X�g����폜���܂��B
	 */
	public void removeSameInverseNodeID() {
		ListIterator li = inverseNodeIDList.listIterator();
		while(li.hasNext()) {
			Integer id = (Integer)li.next();
			if(id.equals(ID)) {
				li.remove();
			}
		}
	}


	/////////////////////////
	// �e�X�g�p�̏��擾

	/**
	 * �������w�K���Ă���m�[�h�����擾���܂��B
	 * @return int �������w�K���Ă���m�[�h��
	 */
	public int getCDSize() {

//		return cognitiveDistance.size();

		/*
		 * maxCDLngth�𓮓I�ɕω��������ꍇ�ɁA�ω����������maxCDLngth
		 * �Ŏg�p�\��CognitiveDistance�̃T�C�Y���擾���܂��B
		 */
		int validCDSize = 0;
		Enumeration e = cognitiveDistance.elements();
		while(e.hasMoreElements()) {
			Integer distance = (Integer)e.nextElement();
			if(distance.intValue() <= maxCDLngth) {
				validCDSize++;
			}
		}

		return validCDSize;
	}

	/**
	 * �������ɒ��ڈړ��\�ȃm�[�h�����擾���܂��B
	 * @return int ���ڈړ��\�ȃm�[�h��
	 */
	public int getForwardNodeIDListSize() {
		return forwardNodeIDList.size();
	}


}
