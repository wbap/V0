/**
 * TreeViewerCanvas.java
 *  �c���[�̏�Ԃ�`�悷��N���X
 *  COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2001.10 BSC miyamoto
 */
package gsa.viewer;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import gsa.*;

/**
 *  �c���[�̏�Ԃ�`�悷��N���X
 */
public class TreeViewerCanvas extends Canvas {

	/* �c���[�̃��[�g */
	private FailAgentTreeElement rootElement = null;
	/* �c���[�̃J�����g */
	private FailAgentTreeElement currentElement = null;

	/* �`�悷��v�f�̊Ԋu */
	private final int X_SPACE = 30;
	private final int Y_SPACE = 20;
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
	private Font agrFont = new Font("Dialog", Font.PLAIN , 12);

	/* y�����̕`��ʒu */
	private int yPos = 0;

	////////////////////////////////////////////////////////////
	// �R���X�g���N�^

	/**
	 * �R���X�g���N�^
	 * @param FailAgentTreeElement rootElement �c���[�̃��[�g
	 */
	public TreeViewerCanvas(FailAgentTreeElement rootElement) {
		super();
		this.rootElement = rootElement;
	}

	////////////////////////////////////////////////////////////
	// public 

	/**
	 * �c���[�̃J�����g��ݒ肵�܂��B
	 * @param FailAgentTreeElement currentElement �c���[�̃J�����g
	 */
	public void setCurrentElement(FailAgentTreeElement currentElement) {
		this.currentElement = currentElement;
	}

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
//		int[] size = getUseCanvasSize();
//		if(xSizeOld != size[0] || ySizeOld != size[1]) {
//			setSize(size[0], size[1]);
//			/* �I�t�X�N���[���C���[�W�̍쐬 */
//			offImage = createImage(size[0], size[1]);
//			offGraphics = offImage.getGraphics();
//		}
//		xSizeOld = size[0];
//		ySizeOld = size[1];

// �`��G���A�̃T�C�Y���Œ�
		if(offImage == null) {
			/* �I�t�X�N���[���C���[�W�̍쐬 */
			setSize(1500, 2000);
			offImage = createImage(1500, 2000);
			offGraphics = offImage.getGraphics();
		}
// �����܂�

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
		/* �c���[�̕`�� */
		drawTree(graphics);
	}

	/**
	 * �I�t�X�N���[����̃C���[�W�̃N���A
	 * @param Graphics graphics
	 */
	private void clearOffImage(Graphics graphics) {
		graphics.setColor(getBackground());
//		graphics.fillRect(0, 0, width, height);
// �`��G���A���Œ�
		graphics.fillRect(0, 0, 1500, 2000);
	}

	/**
	 * �c���[�S�̂�`�悵�܂��B
	 * @param Graphics g
	 */
	private void drawTree(Graphics graphics) {
		yPos = 0;

		drawRoot(graphics);
		drawChild(graphics, 0, 0, rootElement);
	}


	/**
	 * �w�肳�ꂽ�v�f�̎q�̃c���[��`�悵�܂��B
	 * @param Graphics g
	 * @param int x element�̃c���[���x�����̈ʒu
	 * @param int y element�̃c���[���y�����̈ʒu
	 * @param int FailAgentTreeElement element �`�悷��q�̐e�ƂȂ�v�f
	 */
	private void drawChild(Graphics graphics, int x, int y,
	        FailAgentTreeElement element) {
//		for(int i = 0; i < element.next.size(); i++) {
//			FailAgentTreeElement nextElement = 
//			        (FailAgentTreeElement)element.next.get(i);
		// �q�̃��X�g���t�ɂ��ǂ�
		for(int i = element.next.size()-1; i >= 0 ; i--) {
			FailAgentTreeElement nextElement = 
			        (FailAgentTreeElement)element.next.get(i);

			drawTreeElement(graphics, nextElement, x+1, yPos);
			drawLink(graphics, x, y, x+1, yPos);
			drawChild(graphics, x+1, yPos, nextElement);
			yPos = yPos + 1;
		}
	}

	/**
	 * �e�q�֌W�̃����N��`��
	 * @param Graphics graphics
	 * @param int parentX �e��x�����̈ʒu
	 * @param int parentY �e��y�����̈ʒu
	 * @param int childX �q��x�����̈ʒu
	 * @param int childY �q��y�����̈ʒu
	 */
	private void drawLink(Graphics graphics, int parentX, int parentY,
	        int childX, int childY) {
		int parentRectInfo[] = getTreeElementRectSize(parentX, parentY);
		int childRectInfo[] = getTreeElementRectSize(childX, childY);
		int startX = parentRectInfo[0] + parentRectInfo[2];
		int startY = parentRectInfo[1] + (parentRectInfo[3]/2);
		int endX = childRectInfo[0];
		int endY = childRectInfo[1] + (childRectInfo[3]/2);

		graphics.drawLine(startX, startY, endX, endY);
	}

	/**
	 * �c���[�̎w�肳�ꂽ�v�f��`�悵�܂��B
	 * @param Graphics graphics
	 * @param FailAgentTreeElement element �`�悷��v�f
	 * @param int x x�����̈ʒu
	 * @param int y y�����̈ʒu
	 */
	private void drawTreeElement(Graphics graphics,
	        FailAgentTreeElement element, int x, int y) {

		/* �`�悷����̎擾 */
		int rectInfo[] = getTreeElementRectSize(x, y);
		String agidAndValue = element.agid + "";
//		String agidAndValue = element.agid + ":" + element.goal;
		String agr = " " + element.agr;

		/* �`�揈�� */

		/* �c���[�̃J�����g�Ȃ�g�ň͂� */
		if(element == currentElement) {
			graphics.setColor(Color.blue);
			for(int i = 0; i < 5; i++) {
				graphics.drawRect(rectInfo[0]-i, rectInfo[1]-i,
				        rectInfo[2]+(2*i), rectInfo[3]+(2*i));
			}
		}

		/* �G�[�W�F���g���Ƃ̐F���e�[�u������擾 */
		Color color = (Color)(ViewerProperty.colorTable).get(
		        new Integer(element.agid));

		/* �e�v�f�̕`�� */
		if(element.agr == 0) {
			if(color != null) {
				graphics.setColor(color);
				graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
				        rectInfo[3]);
			}
			graphics.setColor(Color.black);
			graphics.drawRect(rectInfo[0], rectInfo[1], rectInfo[2],
			        rectInfo[3]);

		}else if( (element.agr==1) || (element.agr==2) ||
		        (element.agr==3) ) {

			if(color != null) {
				graphics.setColor(color);
				graphics.fillArc(rectInfo[0], rectInfo[1], rectInfo[2],
				        rectInfo[3], 0, 360);
			}
			graphics.setColor(Color.black);
			graphics.drawArc(rectInfo[0], rectInfo[1], rectInfo[2],
			        rectInfo[3], 0, 360);
			graphics.setFont(agrFont);
			graphics.drawString(agr, rectInfo[0]+rectInfo[2],
			        rectInfo[1]+(rectInfo[3]/2));

		}else if( (element.agr==4) || (element.agr==5) ||
		        (element.agr==6) ) {

			if(color != null) {
				graphics.setColor(color);
				graphics.fillArc(rectInfo[0], rectInfo[1], rectInfo[2],
				        rectInfo[3], 0, 360);
			}
			graphics.setColor(Color.black);
			graphics.drawRect(rectInfo[0], rectInfo[1], rectInfo[2],
			        rectInfo[3]);
			graphics.drawArc(rectInfo[0], rectInfo[1], rectInfo[2],
			        rectInfo[3], 0, 360);
			graphics.setFont(agrFont);
			graphics.drawString(agr, rectInfo[0]+rectInfo[2],
			        rectInfo[1]+(rectInfo[3]/2));

		}

		/* �G�[�W�F���gID�̕`�� */
		graphics.setFont(agidFont);
		graphics.drawString(agidAndValue, rectInfo[0]+(rectInfo[2]/2),
		        rectInfo[1]+rectInfo[3] );
	}


	/**
	 * �L�����o�X�ɕK�v�ȃT�C�Y���擾���܂��B
	 * �L�����o�X�ɕK�v�ȃT�C�Y�́A�`��ɕK�v�ȃT�C�Y�ƁA�E�B���h�E�̃T�C�Y��
	 * �����傫�ȃT�C�Y�𗘗p�B
	 * @return int[] �L�����o�X�ɕK�v�ȃT�C�Y int[0]:�� int[1]:����
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
	 * @return int[] �`��ɕK�v�ȃT�C�Y int[0]:�� int[1]:����
	 */
	private int[] getDrawAreaSize() {

		/* �`�悷��v�f�����擾 */
		int[] drawNum = new int[2];
		childSize(rootElement, drawNum);

		/* �v�f�����T�C�Y�ɕϊ� */
		int[] drawSize = new int[2];
		drawSize[0] = (drawNum[0] * (X_SPACE + X_ELEMENT_SIZE)) + X_SPACE;
		drawSize[1] = (drawNum[1] * (Y_SPACE + Y_ELEMENT_SIZE)) + Y_SPACE;
		return drawSize;
	}

	/**
	 * �`����s�Ȃ��v�f�����擾���܂��B
	 * @param FailAgentTreeElement element
	 * @param int[] drawNum
	 */
	private void childSize(FailAgentTreeElement element, int[] drawNum) {
		for(int i = 0; i < element.next.size(); i++) {
			FailAgentTreeElement nextElement = null;
			nextElement = (FailAgentTreeElement)element.next.get(i);
			drawNum[0] ++;
			childSize(nextElement, drawNum);
			drawNum[1] ++;
		}
	}

	/**
	 * �c���[�̃��[�g��`�悵�܂��B
	 * @param Graphics g
	 */
	private void drawRoot(Graphics graphics) {
		graphics.setFont(agrFont);
		graphics.setColor(Color.black);
		graphics.drawString("root", X_SPACE-(X_ELEMENT_SIZE/2),
		        Y_SPACE+(Y_ELEMENT_SIZE/4)-5);
		graphics.fillArc(X_SPACE-(X_ELEMENT_SIZE/2),
		        Y_SPACE+(Y_ELEMENT_SIZE/4), X_ELEMENT_SIZE/2, Y_ELEMENT_SIZE/2,
		        0, 360);
	}

	/**
	 * �c���[��̎w�肳�ꂽ�ʒu�̗v�f��`�悷���`�̏����擾���܂��B
	 * @param int x �v�f��x�����̈ʒu
	 * @param int y �v�f��y�����̈ʒu
	 * @return int[] int[4]�̔z�� ���ɃL�����o�X��� X���W�EY���W�E���E����
	 */
	private int[] getTreeElementRectSize(int x, int y) {
		int[] rectInfo = new int[4];
		rectInfo[0] = X_SPACE + (x*(X_SPACE+X_ELEMENT_SIZE) - X_ELEMENT_SIZE);
		rectInfo[1] = Y_SPACE + (y*(Y_SPACE+Y_ELEMENT_SIZE));
		rectInfo[2] = X_ELEMENT_SIZE;
		rectInfo[3] = Y_ELEMENT_SIZE;
		return rectInfo;
	}

}


