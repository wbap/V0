/**
 * �蓮�ŃX�^�b�N�ɃS�[����ݒ肷�邽�߃��[�U�[�C���^�[�t�F�[�X
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.08
 */
package wba.citta.gsa;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * �蓮�ŃX�^�b�N�ɃS�[����ݒ肷�邽�߃��[�U�[�C���^�[�t�F�[�X
 */
public class ManualAgentFrame extends Frame {

	private TextField[] textFields = null;
	private Button okButton = null;

	/**
	 * �R���X�g���N�^
	 * @param int nodeNum �m�[�h��
	 */
	public ManualAgentFrame(int nodeNum) {
		super("Manual Agent");

		setLayout(new GridLayout(1, nodeNum+1));

		textFields = new TextField[nodeNum];
		for(int i = 0; i < nodeNum; i++) {
			textFields[i] = new TextField("null");
			add(textFields[i]);
		}

		okButton = new Button("OK");
		add(okButton);
		okButton.addActionListener(new ButtonActionListener());

		setSize(200, 50);
		setVisible(true);

	}

	///////////////////////////////////////////////////////////////////////
	// public

	Vector subgoal = null;
	/**
	 * �蓮�Őݒ肳�ꂽ�T�u�S�[�����擾���܂��B
	 * @return Vector �T�u�S�[��
	 */
	public Vector getSubgoal() {
		return subgoal;
	}

	/**
	 * �����ŕێ����Ă���T�u�S�[�����N���A���܂��B
	 */
	public void clearSubgoal() {
		subgoal = null;
	}

	///////////////////////////////////////////////////////////////////////
	// private

	/**
	 * �����Őݒ肳�ꂽ������̔z����S�[���Ƃ��ē����̕ϐ��ɐݒ肵�܂��B
	 * @param String[] input ������̔z��
	 */
	private void setSubgoal(String[] input) {
		subgoal = new Vector();
		for(int i = 0; i < input.length; i++) {
			if(input[i].equals("null")) {
				subgoal.add(null);
			}else {
				Integer elm = null;
				try {
					elm = new Integer(input[i]);
				}catch(NumberFormatException e) {
					// ���l�ȊO���ݒ肳��Ă���ꍇ��null��ݒ�
				}
				subgoal.add(elm);
			}
		}
	}

	/**
	 * �e�L�X�g�t�B�[���h�̏����������܂��B
	 * �S�Ă�null�Ŗ��߂܂��B
	 */
	private void initTextFields() {
		for(int i = 0; i < textFields.length; i++) {
			textFields[i].setText("null");
		}
	}

	///////////////////////////////////////////////////////////////////////
	// �C�x���g����

	/**
	 * �{�^���̃C�x���g�������s�Ȃ��C���i�[�N���X
	 */
	class ButtonActionListener implements ActionListener {

		public void actionPerformed(ActionEvent evt) {
			if(okButton == evt.getSource()) {
				String[] str = new String[textFields.length];
				for(int i = 0; i < textFields.length; i++) {
					str[i] = textFields[i].getText();
				}
				initTextFields();
				setSubgoal(str);
			}
		}

	}

}

