/**
 * CDViewerFrame.java
 *  �e�w���Ƃ̃O���t�B�b�N�\��������N���X
 *  COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2000.11 BSC miyamoto
 */
package wba.citta.cognitivedistance.viewer;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 *  �e�w���Ƃ̃O���t�B�b�N�\��������N���X�ł��B
 */
public class CDViewerFrame extends Frame {

	/* ���C��ID */
	private int layerID;

	/* �`����s�Ȃ��L�����o�X */
	private CDViewerCanvas canvas;
	/* �m�[�hID��\�����邽�߂̃��x�� */
	private Label lID;
	private Label lState;
	private Label lGoal;
	private Label lSubgoal;
	private Label lNextState;
	/* ���x����ݒ肷��p�l�� */
	private Panel panel;


	////////////////////////////////////////////////////////////
	// �R���X�g���N�^

	/**
	 * �R���X�g���N�^
	 * @param int layerID ���C��ID
	 * @param int xNum x�������̂܂��̐�
	 * @param int yNum y�������̂܂��̐�
	 */
	public CDViewerFrame(int layerID, int xNum, int yNum) {
//		super("Layer " + layerID);
//		this.layerID = layerID;
//		initViewerFrame(xNum, yNum);
		this(layerID, xNum, yNum, true);
	}

	public CDViewerFrame(int layerID, int xNum, int yNum,
	        boolean flagSeparate) {
		super("Layer " + layerID);
		this.layerID = layerID;
		initViewerFrame(xNum, yNum, flagSeparate);
	}


	/**
	 * ����������
	 * @param int xNum
	 * @param int yNum
	 */
	private void initViewerFrame(int xNum, int yNum, boolean flagSeparate) {

		/* �e�w�̃O���t�B�b�N�\�����s�Ȃ��L�����o�X�̐��� */
		canvas = new CDViewerCanvas(xNum, yNum, flagSeparate);
		panel = new Panel();
		lID = new Label(" [ Layer   ]");
		lState = new Label("   state          ");
		lGoal = new Label("   goal           ");
		lSubgoal = new Label("   subgoal        ");
		lNextState = new Label("   next state     ");

		/* ��̑w���珇�ɉ�ʂɐݒ� */
		panel.setLayout(new GridLayout(8, 1));
		panel.add(new Label());
		panel.add(lID);
		panel.add(new Label());
		panel.add(lState);
		panel.add(lGoal);
		panel.add(lSubgoal);
		panel.add(lNextState);
		panel.add(new Label());
		setLayout(new BorderLayout());
		add(panel, "West");
		add(canvas, "Center");

		setSize(400, 250);
		setVisible(true);
	}

	/**
	 * state,goal,upperSubgoal,currentSubgoal�ɑ�������̏�Ԃ�
	 * ���X�g��ݒ肵�܂��B
	 * @param LinkedList currentStateList ���݂̏�Ԃɑ�������
	 * @param LinkedList goalStateList �S�[���̏�Ԃɑ�������
	 * @param LinkedList upperSubgoalList ��ʑw�̃T�u�S�[���ɑ�������
	 * @param LinkedList currentSubgoalList ���݂̑w�̃T�u�S�[���ɑ�������
	 */
	public void setSegmentInfo(LinkedList currentStateList, 
	        LinkedList goalStateList, LinkedList upperSubgoalList,
	        LinkedList currentSubgoalList, LinkedList optionList) {
		/* �Ή�����w�̃L�����o�X�ɏ�Ԃ̃��X�g�ݒ� */
		canvas.setCurrentStateList(currentStateList);
		canvas.setGoalStateList(goalStateList);
		canvas.setUpperSubgoalList(upperSubgoalList);
		canvas.setCurrentSubgoalList(currentSubgoalList);
		canvas.setOptionList(optionList);
	}


	/**
	 * �m�[�h�̏���ݒ肵�܂��B
	 * @param int[] nodeInfo
	 */
	public void setNodeInfo(int[] nodeInfo) {
		if(nodeInfo[5] == 1) {
			canvas.setRenewFlg(true);
		}
		lID.setText       (" [ Layer " + layerID + " ]");
		lState.setText    ("   state  " + nodeInfo[0]);
		lGoal.setText     ("   goal  " + nodeInfo[1]);
		lSubgoal.setText  ("   subgoal  " + nodeInfo[2]);
		lNextState.setText("   next state  " + nodeInfo[3]);
	}


	/**
	 * repaint�̃I�[�o�[���C�h
	 */
	public void repaint() {
		canvas.repaint();
	}


}


