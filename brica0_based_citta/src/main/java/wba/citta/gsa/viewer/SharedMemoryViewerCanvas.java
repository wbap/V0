/**
 * SharedMemoryViewerCanvas.java
 *  ���L�������̏�Ԃ�`�悷��N���X
 *  COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2001.10 BSC miyamoto
 */
package wba.citta.gsa.viewer;

import java.awt.*;
import java.util.*;
import wba.citta.gsa.*;

/**
 *  ���L�������̏�Ԃ�`�悷��N���X
 */
public class SharedMemoryViewerCanvas extends Canvas {

	/* ���݂̏�Ԃւ̎Q�� */
	private Integer[] stateArray;
	/* �S�[���X�^�b�N�ւ̎Q�� */
	private LinkedList[] goalStackArray;

	/* ���E�̊Ԋu */
	private final int X_SPACE = 40;
	/* ���̊Ԋu */
	private final int Y_SPACE = 10;

	/* �`�悷��v�f�̃T�C�Y */
	private final int X_ELEMENT_SIZE = 30;
	private final int Y_ELEMENT_SIZE = 30;

	/* �\������Ă���̈�̃T�C�Y */
	private int height;
	private int width;

	/* �_�u���o�b�t�@�����O�p �I�t�X�N���[���C���[�W */
	private Image offImage;
	private Graphics offGraphics;

	/* �����̃t�H���g */
	private Font agidFont = new Font("Dialog", Font.BOLD, 20);
	private Font valueFont = new Font("Dialog", Font.PLAIN , 12);

	////////////////////////////////////////////////////////////
	// �R���X�g���N�^

	/**
	 * �R���X�g���N�^
	 * @param Integer[] stateArray ���݂̏�Ԃւ̎Q��
	 * @param LinkedList[] goalStackArray �S�[���X�^�b�N�ւ̎Q��
	 */
	public SharedMemoryViewerCanvas(Integer[] stateArray,
	        LinkedList[] goalStackArray) {
		super();
		this.stateArray = stateArray;
		this.goalStackArray = goalStackArray;
	}

	////////////////////////////////////////////////////////////
	// public 

	/**
	 * update���\�b�h�̃I�[�o�[���C�h
	 * @param Graphics g
	 */
	public void update(Graphics g) {
		paint(g);
	}

private int xSizeOld = 0;
private int ySizeOld = 0;

	/**
	 * paint���\�b�h�̃I�[�o�[���C�h
	 * @param Graphics g
	 */
	public void paint(Graphics g) {

		/* �`�悷��G���A�̃T�C�Y���擾 */
		int[] size = getUseCanvasSize();

		if(xSizeOld != size[0] || ySizeOld != size[1]) {
			setSize(size[0], size[1]);
			/* �I�t�X�N���[���C���[�W�̍쐬 */
			offImage = createImage(size[0], size[1]);
			offGraphics = offImage.getGraphics();
		}

		xSizeOld = size[0];
		ySizeOld = size[1];

		/* �I�t�X�N���[���ւ̕`�� */
		drawOffImage(offGraphics);
		/* �I�t�X�N���[���C���[�W��`�� */
		g.drawImage(offImage, 0, 0, this);
	}

	/**
	 * Canvas���̕\������Ă���̈�̃T�C�Y��ݒ肵�܂��B
	 * @param int width  ��
	 * @param int height ����
	 */
	public void setViewportSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	////////////////////////////////////////////////////////////
	// private

	/**
	 * �I�t�X�N���[���ւ̕`��
	 * @param Graphics graphics
	 */
	private void drawOffImage(Graphics graphics) {

		/* �C���[�W�̃N���A */
		clearOffImage(graphics);

		/* �x�[�X�����̕`�� */
		drawBase(graphics);
		/* ���݂̏�Ԃ̕`�� */
		drawState(graphics);
		/* �S�[���X�^�b�N�S�̂̕`�� */
		drawStackArray(graphics);
	}

	/**
	 * �I�t�X�N���[����̃C���[�W�̃N���A
	 * @param Graphics graphics
	 */
	private void clearOffImage(Graphics graphics) {
		graphics.setColor(getBackground());
		graphics.fillRect(0, 0, width, height);
	}

	/**
	 * ���݂̏�Ԃ�`�悵�܂��B
	 * @param Graphics graphics
	 */
	private void drawState(Graphics graphics) {
		for(int i = 0; i < stateArray.length; i++) {
			if(stateArray[i] != null) {
				drawStateElement(graphics, i, stateArray[i]);
			}
		}
	}

	/**
	 * ���݂̏�Ԃ̊e�v�f��`�悵�܂��B
	 * @param Graphics g
	 * @param int nodeIndex �`�悷��m�[�h
	 * @param Integer element �`�悷��l
	 */
	private void drawStateElement(Graphics graphics, int nodeIndex,
	        Integer element) {

		/* �`�悷��Canvas��̈ʒu���擾 */
		int rectInfo[] = getStackElementRectSize(nodeIndex, 0);

		graphics.setColor(Color.black);
		graphics.drawRect(rectInfo[0], rectInfo[1], rectInfo[2], rectInfo[3]);

		graphics.setFont(valueFont);
		graphics.drawString(element.toString(), rectInfo[0],
		        rectInfo[1]+(rectInfo[3]/2) );
	}


	/**
	 * �S�[���X�^�b�N�S�̂�`�悵�܂��B
	 * @param Graphics g
	 */
	private void drawStackArray(Graphics graphics) {
		for(int i = 0; i < goalStackArray.length; i++) {
			drawStack(graphics, i);
		}
	}

	/**
	 * �S�[���X�^�b�N�̎w�肳�ꂽ�m�[�h��`�悵�܂��B
	 * @param Graphics g
	 * @param int nodeIndex �`�悷��m�[�h
	 */
	private void drawStack(Graphics graphics, int nodeIndex) {
		for(int i = 0; i < goalStackArray[nodeIndex].size(); i++) {
			GoalStackElement element
			        = (GoalStackElement)goalStackArray[nodeIndex].get(i);
			drawStackElement(graphics, nodeIndex, i, element);
		}
	}

	/**
	 * �S�[���X�^�b�N�̎w�肳�ꂽ�v�f��`�悵�܂��B
	 * @param Graphics g
	 * @param int x �`�悷��m�[�h
	 * @param int y �`�悷��v�f�̃m�[�h���̈ʒu
	 * @param GoalStackElement element �`�悷��v�f
	 */
	private void drawStackElement(Graphics graphics, int x, int y,
	        GoalStackElement element) {

		String value = " " + element.value;
		String agid = " " + element.agid;

		/* �`�悷��Canvas��̈ʒu���擾 */
		int rectInfo[] = getStackElementRectSize(x, y+2);

		/* �`�揈�� */
		/* �G�[�W�F���g���Ƃ̐F���e�[�u������擾 */
		Color color = (Color)(ViewerProperty.colorTable).get(
		        new Integer(element.agid));

		if(color != null) {
			graphics.setColor(color);
			graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
			        rectInfo[3]);
		}

		graphics.setColor(Color.black);
		graphics.drawRect(rectInfo[0], rectInfo[1], rectInfo[2], rectInfo[3]);

		graphics.setFont(agidFont);
		graphics.drawString(agid, rectInfo[0]+(rectInfo[2]/2),
		        rectInfo[1]+rectInfo[3]);

		graphics.setFont(valueFont);
		graphics.drawString(value, rectInfo[0], rectInfo[1]+(rectInfo[3]/2) );
	}

	/**
	 * �X�^�b�N�̃x�[�X������`�悵�܂��B
	 * @param Graphics g
	 */
	private void drawBase(Graphics graphics) {
		graphics.setColor(Color.blue);

		/* �m�[�h��ID�̕`�� */
		for(int i = 0; i < goalStackArray.length; i++) {
			int[] rectInfo = getStackElementRectSize(i, 1);
			int xPos = rectInfo[0] + (rectInfo[2]/2);
			int yPos = rectInfo[1] + (rectInfo[3]/2);
			graphics.drawString(""+ i, xPos, yPos );
		}

		/* "GoalStack","State"�̕`�� */
		graphics.setFont(valueFont);

		int[] rectInfo = getStackElementRectSize(0, 2);
		graphics.drawString("Goal", 5, rectInfo[1]+(rectInfo[3]/2) );
		graphics.drawString("Stack", 5, rectInfo[1]+rectInfo[3] );

		rectInfo = getStackElementRectSize(0, 0);
		graphics.drawString("State", 5, rectInfo[1]+rectInfo[3] );
	}

	/**
	 * �w�肳�ꂽ���L�������̈ʒu�ɕ`�悷���`�̏����擾���܂��B
	 * @param int x x�����ւ̈ʒu
	 * @param int y y�����ւ̈ʒu
	 * @return int[] int[4]�̔z�� ���ɃL�����o�X��� X���W�EY���W�E���E����
	 */
	private int[] getStackElementRectSize(int x, int y) {
		Dimension d = getSize();
		int[] rectInfo = new int[4];
		rectInfo[0] = X_SPACE + (x * X_ELEMENT_SIZE);
		rectInfo[1] = d.height - ((y+1) * Y_ELEMENT_SIZE) - Y_SPACE;
		rectInfo[2] = X_ELEMENT_SIZE;
		rectInfo[3] = Y_ELEMENT_SIZE;
		return rectInfo;
	}

	/**
	 * �L�����o�X�ɕK�v�ȃT�C�Y���擾���܂��B
	 * �L�����o�X�ɕK�v�ȃT�C�Y�́A�`��G���A�ƃE�B���h�E�̃T�C�Y�̂���
	 * �ǂ��炩�傫�����𗘗p���܂��B
	 * @return int[] [0]��  [1]����
	 */
	private int[] getUseCanvasSize() {

		int[] drawAreaSize = getDrawAreaSize();
		if(drawAreaSize[0] < width) {
			drawAreaSize[0] = width;
		}
		if(drawAreaSize[1] < height) {
			drawAreaSize[1] = height;
		}
		return drawAreaSize;
	}

	/**
	 * �`��ɕK�v�ȃT�C�Y���擾���܂��B
	 * @return int[] [0]��  [1]����
	 */
	private int[] getDrawAreaSize() {
		int[] drawNum = getDrawNum();
		int[] drawSize = new int[2];
		drawSize[0] = (drawNum[0] * X_ELEMENT_SIZE) + (X_SPACE*2);
		drawSize[1] = (drawNum[1]+1) * Y_ELEMENT_SIZE;
		return drawSize;
	}

	/**
	 * �`�悷��v�f�����擾���܂��B
	 * @return int[] �`�悷��v�f�� [0]x�������ւ̐� [1]y�������ւ̐�
	 */
	private int[] getDrawNum() {
		int[] drawNum = new int[2];
		drawNum[0] = goalStackArray.length;
		for(int i = 0; i < goalStackArray.length; i++) {
			if(drawNum[1] < goalStackArray[i].size()) {
				drawNum[1] = goalStackArray[i].size();
			}
		}

		/* �x�[�X�ƁA�X�e�C�g�̕���ǉ� */
		drawNum[1] += 2;
		return drawNum;
	}

}


