/**
 * MessageCanvas.java
 *  COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2000.09 BSC miyamoto
 */
package wba.citta.environment;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 *  ���O���t�B�b�N�������s���N���X�ł�
 */
public class MessageCanvas extends Canvas {

	/* �L�����o�X�̃T�C�Y */
	private int height;
	private int width;

	/* �T�C�Y�ύX�̃t���O */
	private boolean resized;

	/* �_�u���o�b�t�@�����O�p �I�t�X�N���[���C���[�W */
	private Image offImage;
	private Graphics offGraphics;

	private String message = new String("");
	private int messageXPos = 0;
	private int messageYPos = 0;

	private Image image = null;
	private int imageXPos = 0;
	private int imageYPos = 0;

	////////////////////////////////////////////////////////////
	// �R���X�g���N�^  ����������

	/**
	 * �R���X�g���N�^
	 */
	public MessageCanvas(String message, int x, int y) {
		super();
		/* �T�C�Y�ύX�ɂ��Ă̏������s�Ȃ��C�x���g���X�i�̓o�^ */
		addComponentListener(new CanvasComponentAdapter());
		/* �ϐ��̏����� */
		resized = false;
		/* �\���ʒu */
		messageXPos = x;
		messageYPos = y;
		this.message = message;
	}

	////////////////////////////////////////////////////////////
	// public 

	public void setMessage(String message) {
		this.message = message;
	}

	public void setImage(Image image, int x, int y) {
		this.image = image;
		imageXPos = x;
		imageYPos = y;
	}

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


	////////////////////////////////////////////////////////////
	// private

	/**
	 * �I�t�X�N���[���ւ̕`��
	 * @param Graphics graphics
	 */
	private void drawOffImage(Graphics graphics) {

		/* �C���[�W�̃N���A */
//		graphics.setColor(getBackground());
		graphics.setColor(Color.orange);
		graphics.fillRect(0, 0, width, height);

		Font f = new Font("Dialog", Font.BOLD, 20);
		graphics.setFont(f);
		graphics.setColor(Color.black);
		graphics.drawString(message, messageXPos, messageYPos);
		if(image != null) {
			graphics.drawImage(image, imageXPos, imageYPos, this);
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


