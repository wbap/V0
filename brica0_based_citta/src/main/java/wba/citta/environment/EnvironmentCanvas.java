/**
 * EnvironmentCanvas.java
 *  ���O���t�B�b�N�������s���N���X
 *  COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2000.09 BSC miyamoto
 */
package wba.citta.environment;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.net.*;

/**
 *  ���O���t�B�b�N�������s���N���X�ł�
 */
public class EnvironmentCanvas extends Canvas {

	/* �n�}��� */
	private String[][] map;

	/* ���{�b�g�̈ʒu��� */
	private int[] robotState;

	/* ��V�̏�� */
	private String[][] rewardMap;

	/* ���̐F��� */
	private String[][] colorMap;

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

	private Color redFloor = new Color(255, 200, 200);
	private Color greenFloor = new Color(200, 255, 200);
	private Color blueFloor = new Color(200, 200, 255);

	////////////////////////////////////////////////////////////
	// �R���X�g���N�^  ����������

	/**
	 * �R���X�g���N�^
	 */
	public EnvironmentCanvas() {
		super();
		/* �T�C�Y�ύX�ɂ��Ă̏������s�Ȃ��C�x���g���X�i�̓o�^ */
		addComponentListener(new CanvasComponentAdapter());
		/* �ϐ��̏����� */
		resized = false;
		/*  */
		initImage();
	}

	/**
	 * ����������
	 * @param String[][] map    ���̒n�}���
	 * @param int[] robotState  ���{�b�g�̈ʒu  int[0]=x���W  int[1]=y���W
	 */
	public void initCanvas(String[][] map, int[] robotState) {
		this.map = map;
		this.robotState = robotState;

		/* �T�C�Y���̐ݒ� */
		setSizeInfo();
	} 

	/**
	 * ����������
	 * @param String[][] map       ���̒n�}���
	 * @param int[] robotState     ���{�b�g�̈ʒu  int[0]=x���W  int[1]=y���W
	 * @param String[][] rewardMap ��V�̃e�[�u��
	 *                             �n�}���Ɠ����T�C�Y�̃e�[�u���őΉ�����
	 *                             �ʒu�ɕ�V���ݒ肳�ꂽ����
	 */
	public void initCanvas(String[][] map, int[] robotState,
	        String[][] rewardMap) {
		initCanvas(map,robotState);
		this.rewardMap = rewardMap;
	}

	/**
	 * ����������
	 * @param String[][] map       ���̒n�}���
	 * @param int[] robotState     ���{�b�g�̈ʒu  int[0]=x���W  int[1]=y���W
	 * @param String[][] rewardMap ��V�̃e�[�u��
	 *                             �n�}���Ɠ����T�C�Y�̃e�[�u���őΉ�����
	 *                             �ʒu�ɕ�V���ݒ肳�ꂽ����
	 * @param String[][] colorMap  �t���A�̐F��ݒ肵���e�[�u��
	 *                             �n�}���Ɠ����T�C�Y�̃e�[�u���őΉ�����
	 *                             �ʒu�ɐF���ݒ肳�ꂽ����
	 */
	public void initCanvas(String[][] map, int[] robotState,
	        String[][] rewardMap, String[][] colorMap) {
		initCanvas(map, robotState, rewardMap);
		this.colorMap = colorMap;
	}

	private Image telephoneImage = null;
	private Image keyImage = null;
//	private Image telephoneImage2 = null;
//	private Image keyImage2 = null;
	private void initImage() {
		ResourceLoader loader = new ResourceLoader();
		telephoneImage = loader.getImage("telephone2.gif");
		keyImage = loader.getImage("key2.gif");
//		telephoneImage2= loader.getImage("telephone3.gif");
//		keyImage2= loader.getImage("key3.gif");
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
	 * �e�}�X��x�������̃T�C�Y
	 * @return int
	 */
	public int getXSpace() {
		return xSpace;
	}

	/**
	 * �e�}�X��y�������̃T�C�Y
	 * @return int
	 */
	public int getYSpace() {
		return ySpace;
	}

	private boolean flagFlash = false;
	/**
	 * ��ʂ�_�ł����܂��B
	 */
	public void flash() {
		flagFlash = true;
	}

	////////////////////////////////////////////////////////////
	// private

	/**
	 * �I�t�X�N���[���ւ̕`��
	 * @param Graphics graphics
	 */
	private void drawOffImage(Graphics graphics) {

		/* �C���[�W�̃N���A */
		graphics.setColor(getBackground());

		/* �_�ł�����ꍇ */
		if(flagFlash) {
			graphics.setColor(Color.red);
			flagFlash = false;
		}

		graphics.fillRect(0, 0, width, height);

		/* �r���̕`�� */
		graphics.setColor(Color.black);

		int xNum = map.length+1;    /* x�������̂܂��� */
		int yNum = map[0].length+1; /* y�������̂܂��� */

		/* ���������̌r�� */
		for(int i = ySpace; i <= ySpace * yNum; i = i+ySpace) {
			graphics.drawLine(xSpace, i, xSpace * xNum, i);
		}
		/* ���������̌r�� */
		for(int i = xSpace; i <= xSpace * xNum; i = i+xSpace) {
			graphics.drawLine(i, ySpace, i, ySpace * yNum);
		}

		/* �n�}�̕`�� */
		for(int x = 0; x < map.length; x++) {
			for(int y = 0; y < map[0].length; y++) {

				/* �n�}��̈�}�X�̃T�C�Y�Ɋւ�������擾 */
				int[] rectInfo = getMapRectInfo(x, y);

				/* ���̕`�� */
				if(colorMap != null) {
					drawFloor(graphics, x, y, rectInfo);
				}

				String mapID = "";
				if(map[x][y].length() > 0) {
					mapID = map[x][y].substring(0, 1);
				}

				/* ��V */
				if( mapID.equals("O") ) {
					graphics.setColor(Color.green);
					graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
					        rectInfo[3]);
//					drawAttributeRect(graphics, rectInfo);
					graphics.setColor(Color.black);
					graphics.drawString("O", rectInfo[0],
					        rectInfo[1]+rectInfo[3]);
				}
				// 2001.03.22 �ǉ� miyamoto
				// �h�A�ƃJ�M�̕\����ǉ�
				/* �h�A �N���[�Y */
				if( mapID.equals("D") ) {
					graphics.setColor(Color.darkGray);
					graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
					        rectInfo[3]);
//					drawAttributeRect(graphics, rectInfo);
					graphics.setColor(Color.black);
					graphics.drawString("D", rectInfo[0],
					        rectInfo[1]+rectInfo[3]);
				}
				/* �h�A �I�[�v��*/
				if( mapID.equals("d") ) {
					graphics.setColor(Color.lightGray);
					graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
					        rectInfo[3]);
//					drawAttributeRect(graphics, rectInfo);
					graphics.setColor(Color.black);
					graphics.drawString("d", rectInfo[0],
					        rectInfo[1]+rectInfo[3]);
				}
				/* �J�M */
				if( mapID.equals("K") ) {
					graphics.setColor(Color.yellow);
					graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
					        rectInfo[3]);
//					graphics.setColor(Color.black);
// �摜�ŕ\��
					if(keyImage != null) {
						graphics.drawImage(keyImage, rectInfo[0], 
						        rectInfo[1], this);
//						graphics.drawImage(keyImage, rectInfo[0]-2, 
//						        rectInfo[1]-2, this);
					}
//					graphics.drawString("K", rectInfo[0],
//					        rectInfo[1]+rectInfo[3]);
				}
				// 2001.08.08 �ǉ� �d�b�̈ʒu
				if( mapID.equals("T") ) {
//System.out.println("rectInfo");
//System.out.println("x:" + rectInfo[0] + " y:" + rectInfo[1]);
//System.out.println("x size:" + rectInfo[2] + " y size:" + rectInfo[3]);
					graphics.setColor(Color.yellow);
					graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
					        rectInfo[3]);
//					graphics.setColor(Color.black);
// �摜�ŕ\��
					if(telephoneImage != null) {
						graphics.drawImage(telephoneImage, rectInfo[0],
						        rectInfo[1], this);
//						graphics.drawImage(telephoneImage, rectInfo[0]-2,
//						        rectInfo[1]-2, this);
					}
//					graphics.drawString("T", rectInfo[0],
//					        rectInfo[1]+rectInfo[3]);
				}

// �A�C�e���𑝂₷
if( mapID.equals("A") ) {
	graphics.setColor(Color.yellow);
	graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
	        rectInfo[3]);
	graphics.setColor(Color.black);
	graphics.drawString("A", rectInfo[0],
	        rectInfo[1]+rectInfo[3]);
}
if( mapID.equals("B") ) {
	graphics.setColor(Color.yellow);
	graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
	        rectInfo[3]);
	graphics.setColor(Color.black);
	graphics.drawString("B", rectInfo[0],
	        rectInfo[1]+rectInfo[3]);
}
if( mapID.equals("a") ) {
	graphics.setColor(Color.yellow);
	graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
	        rectInfo[3]);
	graphics.setColor(Color.black);
	graphics.drawString("a", rectInfo[0],
	        rectInfo[1]+rectInfo[3]);
}
if( mapID.equals("b") ) {
	graphics.setColor(Color.yellow);
	graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
	        rectInfo[3]);
	graphics.setColor(Color.black);
	graphics.drawString("b", rectInfo[0],
	        rectInfo[1]+rectInfo[3]);
}

				/* �X�^�[�g */
				if( mapID.equals("S") ) {
					graphics.setColor(Color.pink);
					graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
					        rectInfo[3]);
				}
				/* 2001.07.06 �ǉ� �����_���ɓ��� */
				if( mapID.equals("R") ) {
					graphics.setColor(Color.magenta);
					graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
					        rectInfo[3]);
				}
				/* �W�����v */
				if( mapID.equals("J") ) {
					graphics.setColor(Color.gray);
					graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
					        rectInfo[3]);
				}
				/* ���� */
				if( mapID.equals("F") ) {
					graphics.setColor(Color.cyan);
					graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
					        rectInfo[3]);
					/* �����̎擾 */
					int startIndex = map[x][y].indexOf("(");
					int endIndex = map[x][y].indexOf(")");
					String dir = map[x][y].substring(startIndex+1, endIndex);
					/* ������`�� */
					drawDirection(graphics, rectInfo, dir);
				}
				/* �R */
				if( mapID.equals("C") ) {
					graphics.setColor(Color.orange);
					graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
					        rectInfo[3]);
					/* �����̎擾 */
					int startIndex = map[x][y].indexOf("(");
					int endIndex = map[x][y].indexOf(")");
					String dir = map[x][y].substring(startIndex+1, endIndex);
					/* ������`�� */
					drawDirection(graphics, rectInfo, dir);
				}
				/* �� */
				if( mapID.equals("W") ) {
					graphics.setColor(Color.black);
					graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
					        rectInfo[3]);
				}

			}
		}

		/* ���{�b�g�̕`�� */
		int[] rectInfo = getMapRectInfo(robotState[0], robotState[1]);
		graphics.setColor(Color.blue);
		graphics.fillOval(rectInfo[0], rectInfo[1], rectInfo[2], rectInfo[3]);
		/* ���{�b�g�ʒu�ɃA�C�e����\�� */
// �����ŕ\��
		graphics.setColor(Color.white);
		String item = null;
		if(robotState[2] == 3) {
			item = "A";
		}else if(robotState[2] == 4) {
			item = "B";
		}else {
			item = "";
		}
		graphics.drawString(item, rectInfo[0], rectInfo[1]+rectInfo[3]);

// �摜�ŕ\��
//		if(robotState[2] == 2) {
//			if(telephoneImage != null) {
//				graphics.drawImage(telephoneImage2, rectInfo[0],
//				        rectInfo[1], this);
//			}
//		}else if(robotState[2] == 1) {
//			if(telephoneImage != null) {
//				graphics.drawImage(keyImage2, rectInfo[0],
//				        rectInfo[1], this);
//			}
//		}
// �����܂�

		/* ��V�̃e�[�u�����������(==null)�Ȃ�\�������Ȃ� */
		if(rewardMap != null) {
			drawRewardValue(graphics);
		}
	}


	/**
	 * Flow�ECliff�̖��̕`��
	 * @param Graphics graphics 
	 * @param int[] rectInfo ��`�̏��
	 * @param Stirng dir     ����
	 */
	private void drawDirection(Graphics graphics, int[] rectInfo, String dir) {
		graphics.setColor(Color.black);
		/* ������̖�� */
		if( dir.equals("U") ) {
			graphics.drawLine(rectInfo[0]+rectInfo[2]/2, rectInfo[1],
			        rectInfo[0], rectInfo[1]+rectInfo[3]);
			graphics.drawLine(rectInfo[0]+rectInfo[2]/2, rectInfo[1],
			        rectInfo[0]+rectInfo[2], rectInfo[1]+rectInfo[3]);
		}
		/* �������̖�� */
		if( dir.equals("D") ) {
			graphics.drawLine(rectInfo[0]+rectInfo[2]/2,
			        rectInfo[1]+rectInfo[3], rectInfo[0], rectInfo[1]);
			graphics.drawLine(rectInfo[0]+rectInfo[2]/2,
			        rectInfo[1]+rectInfo[3], rectInfo[0]+rectInfo[2],
			        rectInfo[1]);
		}
		/* �������̖�� */
		if( dir.equals("L") ) {
			graphics.drawLine(rectInfo[0], rectInfo[1]+rectInfo[3]/2,
			        rectInfo[0]+rectInfo[2], rectInfo[1]);
			graphics.drawLine(rectInfo[0], rectInfo[1]+rectInfo[3]/2,
			        rectInfo[0]+rectInfo[2], rectInfo[1]+rectInfo[3]);
		}
		/* �E�����̖�� */
		if( dir.equals("R") ) {
			graphics.drawLine(rectInfo[0]+rectInfo[2],
			        rectInfo[1]+rectInfo[3]/2, rectInfo[0], rectInfo[1]);
			graphics.drawLine(rectInfo[0]+rectInfo[2],
			        rectInfo[1]+rectInfo[3]/2, rectInfo[0],
			        rectInfo[1]+rectInfo[3]);
		}
	}


	/**
	 * ��V�̒l��`�悵�܂��B
	 */
	private void drawRewardValue(Graphics graphics) {
		/* x�������ւ̌J��Ԃ� */
		for(int x = 0; x < map.length; x++) {
			/* y�������ւ̌J��Ԃ� */
			for(int y = 0; y < map[0].length; y++) {
				int[] rectInfo = getMapRectInfo(x, y);
				/* ��V���擾 */
				String reward = rewardMap[x][y];
				graphics.setColor(Color.black);
				graphics.drawString(reward, rectInfo[0],
				        rectInfo[1]+rectInfo[3]);
			}
		}
	}


	int[] xPoints = new int[4];
	int[] yPoints = new int[4];
	private void drawAttributeRect(Graphics graphics, int[] rectInfo) {
			xPoints[0] = rectInfo[0] + (rectInfo[2]/2);
			xPoints[1] = rectInfo[0];
			xPoints[2] = rectInfo[0] + (rectInfo[2]/2);
			xPoints[3] = rectInfo[0] + rectInfo[2];

			yPoints[0] = rectInfo[1];
			yPoints[1] = rectInfo[1] + (rectInfo[3]/2);
			yPoints[2] = rectInfo[1] + rectInfo[3];
			yPoints[3] = rectInfo[1] + (rectInfo[3]/2);

			graphics.setColor(Color.white);
			graphics.fillPolygon(xPoints, yPoints, 4);
			graphics.setColor(Color.black);
			graphics.drawLine(xPoints[0], yPoints[0], xPoints[1], yPoints[1]);
			graphics.drawLine(xPoints[1], yPoints[1], xPoints[2], yPoints[2]);
			graphics.drawLine(xPoints[2], yPoints[2], xPoints[3], yPoints[3]);
			graphics.drawLine(xPoints[3], yPoints[3], xPoints[0], yPoints[0]);
	}


	private void drawFloor(Graphics graphics, int x, int y, int[] rectInfo) {

		String color = "";
		if(colorMap[x][y].length() > 0) {
			color = colorMap[x][y].substring(0, 1);
		}

		// 2001.08.08 �ǉ� miyamoto
		/* ���ɐF��ݒ� */
		if( color.equals("w") ) {
//			graphics.setColor(Color.black);
//			graphics.fillRect(rectInfo[0], rectInfo[1],
//			        rectInfo[2], rectInfo[3]);
		}
		if( color.equals("r") ) {
			graphics.setColor(redFloor);
			graphics.fillRect(rectInfo[0], rectInfo[1],
			        rectInfo[2], rectInfo[3]);
		}
		if( color.equals("g") ) {
			graphics.setColor(greenFloor);
			graphics.fillRect(rectInfo[0], rectInfo[1],
			        rectInfo[2], rectInfo[3]);
		}
		if( color.equals("b") ) {
			graphics.setColor(blueFloor);
			graphics.fillRect(rectInfo[0], rectInfo[1],
			        rectInfo[2], rectInfo[3]);
		}
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
		xSpace = d.width / (map.length+2);
		ySpace = d.height / (map[0].length+2);
//System.out.println("EnvironmentCanvas");
//System.out.println(" height:" + height);
//System.out.println(" width:" + width);

	}


	/**
	 * �w�肳�ꂽ�n�}��̂w�x���W�ɑΉ������`�̏����擾���܂��B
	 * @param int x �n�}��̂w���W
	 * @param int y �n�}��̂x���W
	 * @return int[] int[4]�̔z�� ���ɃL�����o�X��� X���W�EY���W�E���E����
	 */
	private int[] getMapRectInfo(int x, int y) {
		int[] rectInfo = null;
		/* �͈͓����`�F�b�N */
		if( (x >= 0)&&(y >= 0) && (x < map.length)&&(y < map[0].length) ) {
			rectInfo = new int[4];
			rectInfo[0] = ((x+1)*xSpace) + 1;
			rectInfo[1] = ((y+1)*ySpace) + 1;
			rectInfo[2] = xSpace-1;
			rectInfo[3] = ySpace-1;
		}
		return rectInfo;
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


