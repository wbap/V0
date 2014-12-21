/**
 * CDViewer.java
 *  CognitiveDistance�̊K�w���E�Z�O�����g�����O���t�B�b�N�\������N���X
 *  COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2000.11 BSC miyamoto
 */
package wba.citta.cognitivedistance.viewer;

import java.util.*;

/**
 * CognitiveDistance�̊K�w���E�Z�O�����g�����O���t�B�b�N�\������N���X�ł��B
 */
public class CDViewer {

	/* �K�w���̃��C���� */
	private int layerNum;
	/* �e�w���Ƃ̕`��t���[�� */
	private CDViewerFrame[] frame;


	////////////////////////////////////////////////////////////
	// �R���X�g���N�^

	/**
	 * �R���X�g���N�^
	 * @param int layerNum ���C����
	 * @param int xNum x�������̂܂��̐�
	 * @param int yNum y�������̂܂��̐�
	 */
	public CDViewer(int layerNum, int xNum, int yNum) {
		this(layerNum, xNum, yNum, true);
	}

	public CDViewer(int layerNum, int xNum, int yNum, boolean flagSeparate) {
		this.layerNum = layerNum;
		initViewer(xNum, yNum, flagSeparate);
	}

	/**
	 * ����������
	 * @param int xNum x�������̂܂��̐�
	 * @param int yNum y�������̂܂��̐�
	 */
	private void initViewer(int xNum, int yNum, boolean flagSeparate) {
		/* ���C�������̃t���[���̍쐬 */
		frame = new CDViewerFrame[layerNum];
		for(int i = 0; i < layerNum; i++) {
			frame[i] = new CDViewerFrame(i+1, xNum, yNum, flagSeparate);
		}
	}


	/////////////////////////////////////////////////////////////////
	// public 

	/**
	 * �e�w�ł�state,goal,upperSubgoal,currentSubgoal�ɑ�������̏�Ԃ�
	 * ���X�g��ݒ肵�܂��B
	 * @param LinkedList[] currentStateList ���݂̏�Ԃɑ�������
	 * @param LinkedList[] goalStateList �S�[���̏�Ԃɑ�������
	 * @param LinkedList[] upperSubgoalList ��ʑw�̃T�u�S�[���ɑ�������
	 * @param LinkedList[] currentSubgoalList ���݂̑w�̃T�u�S�[���ɑ�������
	 * @param LinkedList[]
	 */
	public void setSegmentInfo(LinkedList[] currentStateList, 
	        LinkedList[] goalStateList, LinkedList[] upperSubgoalList,
	        LinkedList[] currentSubgoalList, LinkedList[] optionList) {
		/* �Ή�����w�̃t���[���ɏ�Ԃ̃��X�g��ݒ� */
		for(int i = 0; i < layerNum; i++) {
			frame[i].setSegmentInfo(currentStateList[i], goalStateList[i],
			        upperSubgoalList[i], currentSubgoalList[i], optionList[i]);
		}
	}


	/**
	 * ���C�����Ƃ̊e�t���[���Ƀm�[�h�̏���ݒ�
	 * @param int[][] nodeIdInfo int[] ���C�����̔z��
	 *                           int[][0] ���݂̏�Ԃ�ID
	 *                           int[][1] �S�[���̏�Ԃ�ID
	 *                           int[][2] ��ʑw����̃T�u�S�[����ID
	 *                           int[][3] ���̏�Ԃ�ID
	 *                           int[][5] ��ʑw����̃T�u�S�[�����X�V���ꂽ��
	 *                                    �����t���O
	 */
	public void setNodeInfo(int[][] nodeIdInfo) {
		for(int i = 0; i < layerNum; i++) {
			frame[i].setNodeInfo(nodeIdInfo[i]);
		}
	}


	/**
	 * ���C�����Ƃ̊e�t���[���̕`�揈�����Ăяo��
	 */
	public void repaint() {
		for(int i = 0; i < layerNum; i++) {
			frame[i].repaint();
		}
		try{
			Thread.sleep(20);
		}catch (Exception e) {
			System.out.println(e);
		}
	}


}


