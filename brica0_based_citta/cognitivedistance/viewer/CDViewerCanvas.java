/**
 * CDViewerCanvas.java
 *  CognitiveDistance�̊e���C���̃Z�O�����g�����O���t�B�b�N�\������N���X
 *  COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2000.11 BSC miyamoto
 */
package cognitivedistance.viewer;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * CognitiveDistance�̊e���C���̃Z�O�����g�����O���t�B�b�N�\������N���X
 * �ł��B
 */
public class CDViewerCanvas extends Canvas {

	/* �܂��̐� */
	private int xNum;
	private int yNum;
	/* �r���̊Ԋu */
	private int xSpace;
	private int ySpace;
	/* �L�����o�X�̃T�C�Y */
	private int height;
	private int width;

	/* �T�C�Y�ύX�̃t���O */
	private boolean resized;

	/* �_�u���o�b�t�@�����O�p �I�t�X�N���[���C���[�W */
	private Image offImage;
	private Graphics offGraphics;

	/* ���݂̏�Ԃɑ�������̏�Ԃ̃��X�g */
	private LinkedList currentStateList;
	/* �S�[���̏�Ԃɑ�������̏�Ԃ̃��X�g */
	private LinkedList goalStateList;
	/* ��ʑw����̃T�u�S�[���̏�Ԃɑ�������̏�Ԃ̃��X�g */
	private LinkedList upperSubgoalList;
	/* ���ݑw�ł̃T�u�S�[���̏�Ԃɑ�������̏�Ԃ̃��X�g */
	private LinkedList currentSubgoalList;
	/* �I�v�V��������ݒ肷�郊�X�g */
	private LinkedList optionList;

	/* ��`��`�悷�邽�߂̒l��ݒ肷��z�� */
	private int[] rectInfo;
	private int[] innerRectInfo;
	private int[] center;
	private int[] xPoints;
	private int[] yPoints;
	private int pointNum;

	private boolean renewFlg;

	/* �e�}�X�̕����ɂ��Ă̐ݒ�  true:5�� fasle:4�� */
	private boolean flagSeparate = true;

	////////////////////////////////////////////////////////////
	// �R���X�g���N�^

	/**
	 * �R���X�g���N�^
	 * @param int xNum x�������̂܂��̐�
	 * @param int yNum y�������̂܂��̐�
	 */
	public CDViewerCanvas(int xNum, int yNum) {
		super();
		/* �C�x���g���X�i�̓o�^ */
		addComponentListener(new CanvasComponentAdapter());
		/* ������ */
		resized = false;
		initCanvas(xNum, yNum);

		rectInfo = new int[4];
		innerRectInfo = new int[4];
		center = new int[2];
		xPoints = new int[4];
		yPoints = new int[4];
		pointNum = 4;
	}

	/**
	 * �R���X�g���N�^
	 * �e�}�X�̕����ɂ��Ďw��\
	 * @param int xNum x�������̂܂��̐�
	 * @param int yNum y�������̂܂��̐�
	 * @param boolean flagSeparate �e�}�X�̕��� true:5�ɕ��� false:4�ɕ���
	 */
	public CDViewerCanvas(int xNum, int yNum, boolean flagSeparate) {
		this(xNum, yNum);
		this.flagSeparate = flagSeparate;
	}

	////////////////////////////////////////////////////////////
	// public 

	/**
	 * update���\�b�h�̃I�[�o�[���C�h
	 */
	public void update(Graphics g) {
		paint(g);
	}

	/**
	 * paint���\�b�h�̃I�[�o�[���C�h
	 */
	public void paint(Graphics g) {

		/* �n�߂ƃT�C�Y�ύX���̓I�t�X�N���[���C���[�W�̏����� */
		if( (offGraphics == null) || (resized == true) ){
			offImage = createImage(width, height);
			offGraphics = offImage.getGraphics();
			resized = false;
		}
		/* �I�t�X�N���[���ւ̕`�� */
		drawOffImage(offGraphics);
		/* �I�t�X�N���[���C���[�W��`�� */
		g.drawImage(offImage, 0, 0, this);
	}

	/**
	 * ���̑w�ł̌��݂̏�Ԃɑ�������̏�Ԃ̃��X�g��ݒ肵�܂��B
	 * @param LinkedList currentStateList  ���̏�Ԃ̃��X�g
	 */
	public void setCurrentStateList(LinkedList currentStateList) {
		this.currentStateList = currentStateList;
	}

	/**
	 * ���̑w�ł̃S�[���̏�Ԃɑ�������̏�Ԃ̃��X�g��ݒ肵�܂��B
	 * @param LinkedList goalStateList  ���̏�Ԃ̃��X�g
	 */
	public void setGoalStateList(LinkedList goalStateList) {
		this.goalStateList = goalStateList;
	}

	/**
	 * ���̑w�̏�ʑw���ݒ肵���T�u�S�[���̏�Ԃɑ�������̏�Ԃ̃��X�g��
	 * �ݒ肵�܂��B
	 * @param LinkedList goalStateList  ���̏�Ԃ̃��X�g
	 */
	public void setUpperSubgoalList(LinkedList upperSubgoalList) {
		this.upperSubgoalList = upperSubgoalList;
	}

	/**
	 * ���̑w�Őݒ肵���T�u�S�[���̏�Ԃɑ�������̏�Ԃ̃��X�g��ݒ肵�܂��B
	 * @param LinkedList goalStateList  ���̏�Ԃ̃��X�g
	 */
	public void setCurrentSubgoalList(LinkedList currentSubgoalList) {
		this.currentSubgoalList = currentSubgoalList;
	}

	/**
	 * �I�v�V�������̃��X�g��ݒ肵�܂��B
	 * @param LinkedList optionList
	 */
	public void setOptionList(LinkedList optionList) {
		this.optionList = optionList;
	}

	/**
	 * �T�u�S�[�����V�����Ȃ������Ƃ������t���O��ݒ肵�܂�
	 * @param boolean  true �V�����T�u�S�[��
	 */
	public void setRenewFlg(boolean b) {
		renewFlg = b;
	}

	////////////////////////////////////////////////////////////
	// private

	/**
	 * ����������
	 * @param int xNum �������̂܂��̐�
	 * @param int yNum �������̂܂��̐�
	 */
	private void initCanvas(int xNum, int yNum) {

		this.xNum = xNum;
		this.yNum = yNum;

		/* �T�C�Y���̐ݒ� */
		setSizeInfo();
	} 


	/**
	 * �T�C�Y�Ɋւ������ݒ肵�܂��B
	 */
	private void setSizeInfo() {
		/* �L�����o�X�̃T�C�Y */
		Dimension d = getSize();
		height = d.height;
		width = d.width;
		/* �r���̊Ԋu */
		xSpace = d.width / (xNum+2);
		ySpace = d.height / (yNum+2);
	}


	/**
	 * �I�t�X�N���[���ւ̕`��
	 * @param Graphics graphics
	 */
	private void drawOffImage(Graphics graphics) {

		/* �C���[�W�̃N���A */
		graphics.setColor(getBackground());
		graphics.fillRect(0, 0, width, height);

		/* option */
		graphics.setColor(Color.cyan);
		fillStateListPolygon(graphics, optionList);

		/* goaltState */
		graphics.setColor(Color.green);
		fillStateListPolygon(graphics, goalStateList);

		/* upperSubgoal */
		Color subgoalColor = Color.magenta/* new Color(0, 0, 150)*/;
		if(renewFlg) {
			graphics.setColor(Color.yellow);
			renewFlg = false;
		}else {
			graphics.setColor(subgoalColor);
		}
		fillStateListPolygon(graphics, upperSubgoalList);

		/* currentSubgoal */
		graphics.setColor(Color.red);
		fillStateListPolygon(graphics, currentSubgoalList);

		/* ���X�g���̏�Ԃ̂܂���h��Ԃ� */
		/* currentState */
		graphics.setColor(Color.blue);
		fillStateListPolygon(graphics, currentStateList);

		/* �e�܂������������`�� */
		graphics.setColor(Color.gray);
		if(flagSeparate == true) {
			/* ��~��Ԃ��� 5�ɕ��� */
			for(int x = 0; x < xNum; x++) {
				for(int y = 0; y < yNum; y++) {
					drawSeparateLine(graphics, x, y);
				}
			}
		}else {
			/* ��~��ԂȂ� 4�ɕ��� */
			for(int x = 0; x < xNum; x++) {
				for(int y = 0; y < yNum; y++) {
					drawSeparateLine2(graphics, x, y);
				}
			}
		}

		/* �r���̕`�� */
		graphics.setColor(Color.gray/*black*/);
		/* ���������̌r�� */
		for(int i = ySpace; i <= ySpace * (yNum+1); i = i+ySpace) {
			graphics.drawLine(xSpace, i, xSpace * (xNum+1), i);
		}
		/* ���������̌r�� */
		for(int i = xSpace; i <= xSpace * (xNum+1); i = i+xSpace) {
			graphics.drawLine(i, ySpace, i, ySpace * (yNum+1));
		}

	}


	/**
	 * ��`�𕪊�������������܂��B(��~��ԗL�� 5�ɕ���)
	 * @param Graphics graphics 
	 * @param int x x���W
	 * @param int y y���W
	 */
	private void drawSeparateLine(Graphics graphics, int x, int y) {

		int[] rectInfo = getRectInfo(x, y);
		int[] innerRectInfo = getInnerRectInfo(rectInfo);

		graphics.drawLine(rectInfo[0], rectInfo[1],
		        innerRectInfo[0], innerRectInfo[1]);
		graphics.drawLine(rectInfo[0]+rectInfo[2], rectInfo[1],
		        innerRectInfo[0]+innerRectInfo[2], innerRectInfo[1]);
		graphics.drawLine(rectInfo[0], rectInfo[1]+rectInfo[3],
		        innerRectInfo[0], innerRectInfo[1]+innerRectInfo[3]);
		graphics.drawLine(rectInfo[0]+rectInfo[2], rectInfo[1]+rectInfo[3],
		        innerRectInfo[0]+innerRectInfo[2],
		        innerRectInfo[1]+innerRectInfo[3]);

		/* ���S�̋�`�̕`�� */
		graphics.drawLine(innerRectInfo[0], innerRectInfo[1],
		        innerRectInfo[0]+innerRectInfo[2], innerRectInfo[1]);
		graphics.drawLine(innerRectInfo[0]+innerRectInfo[2], innerRectInfo[1],
		        innerRectInfo[0]+innerRectInfo[2],
		        innerRectInfo[1]+innerRectInfo[3]);
		graphics.drawLine(innerRectInfo[0]+innerRectInfo[2],
		        innerRectInfo[1]+innerRectInfo[3], innerRectInfo[0],
		        innerRectInfo[1]+innerRectInfo[3]);
		graphics.drawLine(innerRectInfo[0], innerRectInfo[1]+innerRectInfo[3],
		        innerRectInfo[0], innerRectInfo[1]);
	}


	/**
	 * ��`�𕪊�������������܂��B(��~��ԂȂ� 4�ɕ���)
	 * @param Graphics graphics 
	 * @param int x x���W
	 * @param int y y���W
	 */
	private void drawSeparateLine2(Graphics graphics, int x, int y) {

		int[] rectInfo = getRectInfo(x, y);
		graphics.drawLine(rectInfo[0], rectInfo[1], rectInfo[0]+rectInfo[2],
		        rectInfo[1]+rectInfo[3]);
		graphics.drawLine(rectInfo[0]+rectInfo[2], rectInfo[1], rectInfo[0],
		        rectInfo[1]+rectInfo[3]);
	}


	/**
	 * �����Őݒ肳�ꂽ��Ԃ̃��X�g�ɑΉ����鑽�p�`��h��Ԃ��܂��B
	 * @param Graphics g
	 * @param LinkedList stateList ��Ԃ̃��X�g
	 */
	private void fillStateListPolygon(Graphics g, LinkedList stateList) {

		/* ���X�g���Ȃ���Ώ������Ȃ� */
		if(stateList == null) {
			return;
		}

		/* ���X�g�̊e�v�f��h��Ԃ� */
		try{
			ListIterator stateListIterator = stateList.listIterator();
			while(stateListIterator.hasNext()) {
				Vector state = (Vector)stateListIterator.next();
				// 2001.05.24 �C�� miyamoto �Â��o�[�W������java�ɑΉ�
//				fillStatePolygon(g, ((Integer)state.get(0)).intValue(),
//				        ((Integer)state.get(1)).intValue(),
//				        ((Integer)state.get(2)).intValue() );
				fillStatePolygon(g, ((Integer)state.elementAt(0)).intValue(),
				        ((Integer)state.elementAt(1)).intValue(),
				        ((Integer)state.elementAt(2)).intValue() );
			}
		}catch(ConcurrentModificationException e) {
		}
	}


	/**
	 * �w�肳�ꂽ�ʒu�E�A�N�V�����ɑΉ�����̈��h��Ԃ��܂��B
	 * @param int x ���������̈ʒu
	 * @param int y y�������̈ʒu
	 * @param int action �s��
	 */
	private void fillStatePolygon(Graphics g, int x, int y, int action) {

		int[] rectInfo = getRectInfo(x, y);
		int[] innerRectInfo = getInnerRectInfo(rectInfo);

		/* ��~ */
		if(action == -1) {
			xPoints[0] = innerRectInfo[0];
			yPoints[0] = innerRectInfo[1];

			xPoints[1] = innerRectInfo[0] + innerRectInfo[2];
			yPoints[1] = innerRectInfo[1];

			xPoints[2] = innerRectInfo[0] + innerRectInfo[2];
			yPoints[2] = innerRectInfo[1] + innerRectInfo[3];

			xPoints[3] = innerRectInfo[0];
			yPoints[3] = innerRectInfo[1] + innerRectInfo[3];
		}

		/* �� */
		if(action == 0) {
			xPoints[0] = rectInfo[0];
			yPoints[0] = rectInfo[1] + rectInfo[3];

			xPoints[1] = innerRectInfo[0];
			yPoints[1] = innerRectInfo[1] + innerRectInfo[3];

			xPoints[2] = innerRectInfo[0] + innerRectInfo[2];
			yPoints[2] = innerRectInfo[1] + innerRectInfo[3];

			xPoints[3] = rectInfo[0] + rectInfo[2];
			yPoints[3] = rectInfo[1] + rectInfo[3];
		}

		/* �� */
		if(action == 2) {
			xPoints[0] = rectInfo[0] + rectInfo[2];
			yPoints[0] = rectInfo[1];

			xPoints[1] = innerRectInfo[0] + innerRectInfo[2];
			yPoints[1] = innerRectInfo[1];

			xPoints[2] = innerRectInfo[0] + innerRectInfo[2];
			yPoints[2] = innerRectInfo[1] + innerRectInfo[3];

			xPoints[3] = rectInfo[0] + rectInfo[2];
			yPoints[3] = rectInfo[1] + rectInfo[3];
		}

		/* �� */
		if(action == 4) {
			xPoints[0] = rectInfo[0];
			yPoints[0] = rectInfo[1];

			xPoints[1] = innerRectInfo[0];
			yPoints[1] = innerRectInfo[1];

			xPoints[2] = innerRectInfo[0] + innerRectInfo[2];
			yPoints[2] = innerRectInfo[1];

			xPoints[3] = rectInfo[0] + rectInfo[2];
			yPoints[3] = rectInfo[1];
		}

		/* �E */
		if(action == 6) {
			xPoints[0] = rectInfo[0];
			yPoints[0] = rectInfo[1];

			xPoints[1] = innerRectInfo[0];
			yPoints[1] = innerRectInfo[1];

			xPoints[2] = innerRectInfo[0];
			yPoints[2] = innerRectInfo[1] + innerRectInfo[3];

			xPoints[3] = rectInfo[0];
			yPoints[3] = rectInfo[1] + rectInfo[3];
		}

		g.fillPolygon(xPoints, yPoints, pointNum);
	}


	/**
	 * �w�肳�ꂽ�n�}��̂w�x���W�ɑΉ������`�̏����擾���܂��B
	 * @param int x �n�}��̂w���W
	 * @param int y �n�}��̂x���W
	 * @return int[] int[4]�̔z�� ���ɃL�����o�X��� X���W�EY���W�E���E����
	 */
	private int[] getRectInfo(int x, int y) {
		/* �͈͓����`�F�b�N */
		if( (x >= 0)&&(y >= 0) && (x < xNum)&&(y < yNum) ) {
			rectInfo[0] = ((x+1)*xSpace) + 1;
			rectInfo[1] = ((y+1)*ySpace) + 1;
			rectInfo[2] = xSpace-1;
			rectInfo[3] = ySpace-1;
		}else {
			rectInfo[0] = 0;
			rectInfo[1] = 0;
			rectInfo[2] = 0;
			rectInfo[3] = 0;
		}
		return rectInfo;
	}


	/**
	 * ��~��Ԃ�\�킷��`���̋�`�̏����擾���܂��B
	 * @param int[] rectInfo ��`�̏��
	 * @return int[] �����̋�`�̏��
	 */
	private int[] getInnerRectInfo(int[] rectInfo) {
		int[] center = getCenter(rectInfo);

		int innerXLength = rectInfo[2]/3;
		int innerYLength = rectInfo[3]/3;

		innerRectInfo[0] = center[0] - (innerXLength/2);
		innerRectInfo[1] = center[1] - (innerYLength/2);
		innerRectInfo[2] = innerXLength;
		innerRectInfo[3] = innerYLength;

		return innerRectInfo;
	}


	/**
	 * ��`�̒��S�ʒu���擾���܂��B
	 * @param int[] rectInfo ��`�̏��
	 * @return int[] ���S�ʒu��x�Ay���W
	 */
	private int[] getCenter(int[] rectInfo) {
		center[0] = rectInfo[0] + (rectInfo[2]/2);
		center[1] = rectInfo[1] + (rectInfo[3]/2);
		return center;
	}

	//////////////////////////////////////////////////
	// �C�x���g����

	/**
	 * �T�C�Y�ύX�̃C�x���g����������C���i�[�N���X
	 */
	class CanvasComponentAdapter extends ComponentAdapter {

		/**
		 * �T�C�Y�ύX���̏���
		 */
		public void componentResized(ComponentEvent e) {
			/* �T�C�Y���̐ݒ� */
			setSizeInfo();
			resized = true;
			/* �ĕ`�� */
			repaint();
		}
	}

}


