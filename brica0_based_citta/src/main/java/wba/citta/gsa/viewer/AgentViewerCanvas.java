/**
 * AgentViewerCanvas.java
 *  �G�[�W�F���g�̓���󋵂�`�悷��N���X
 *  COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2001.10 BSC miyamoto
 */
package wba.citta.gsa.viewer;

import java.awt.*;
import java.util.*;
import wba.citta.gsa.*;

/**
 *  �G�[�W�F���g�̓���󋵂�`�悷��N���X
 */
public class AgentViewerCanvas extends Canvas {

	/* �G�[�W�F���gID�̔z�� */
	private int[] agents;
	/* �I���G�[�W�F���g�̏�� */
	private int execAgentID;
	/* �폜�����G�[�W�F���g�̏�� */
	private boolean[] removeAgents;

	/* �v�f�Ԃ̊Ԋu */
	private final int X_SPACE = 20;
	private final int Y_SPACE = 20;
	/* �v�f�̃T�C�Y */
	private final int X_ELEMENT_SIZE = 30;
	private final int Y_ELEMENT_SIZE = 30;

	/* �\������Ă���̈�̃T�C�Y */
	private int height;
	private int width;

	/* �_�u���o�b�t�@�����O�p �I�t�X�N���[���C���[�W */
	private Image offImage;
	private Graphics offGraphics;

	////////////////////////////////////////////////////////////
	// �R���X�g���N�^  ����������

	/**
	 * �R���X�g���N�^
	 * @param int[] agents �G�[�W�F���gID�̔z��
	 * @param boolean[] removeAgents ���B�S�[���폜�������s�Ȃ����G�[�W�F���g
	 * �̏��
	 */
	public AgentViewerCanvas(int[] agents, boolean[] removeAgents) {
		super();

		this.agents = agents;
		this.removeAgents = removeAgents;
	}

	////////////////////////////////////////////////////////////
	// public 

	/**
	 * ���s�G�[�W�F���g��ID��ݒ肵�܂��B
	 * @param int execAgentID ���s�G�[�W�F���g��ID
	 */
	public void setExecAgentID(int execAgentID) {
		this.execAgentID = execAgentID;
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

		/* �S�G�[�W�F���g�̕`�� */
		drawAgents(graphics);
	}

	/**
	 * �I�t�X�N���[���C���[�W�̃N���A
	 * @param Graphics graphics
	 */
	private void clearOffImage(Graphics graphics) {
		graphics.setColor(getBackground());
		graphics.fillRect(0, 0, width, height);
	}

	/**
	 * �G�[�W�F���g�̓���󋵂̕`����s�Ȃ��܂��B
	 * @param Graphics g
	 */
	private void drawAgents(Graphics graphics) {
		for(int i = 0; i < agents.length; i++) {
			drawAgent(graphics, i);
		}
	}

	/**
	 * �w�肳�ꂽ�G�[�W�F���g��`�悵�܂��B
	 * @param Graphics g
	 * @param int index �`�悷��G�[�W�F���g�̔z���̈ʒu
	 */
	private void drawAgent(Graphics graphics, int index) {

		/* �`�悷����̎擾 */
		int rectInfo[] = getAgentRectSize(index);
		String agid = " " + agents[index];

		/* �G�[�W�F���g���Ƃ̐F���e�[�u������擾 */
		Color color = (Color)(ViewerProperty.colorTable).get(
		        new Integer(agents[index]));

		/* �`�揈�� */
		if(color != null) {
			graphics.setColor(color);
			graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
			        rectInfo[3]);
		}

		/* ���s�G�[�W�F���̏ꍇ�g�ň͂� */
		if(agents[index] == execAgentID) {
			graphics.setColor(Color.blue);
			for(int i = 0; i < 5; i++) {
				graphics.drawRect(rectInfo[0]-i, rectInfo[1]-i,
				        rectInfo[2]+(2*i), rectInfo[3]+(2*i));
			}
		}

		/* ���B�S�[���폜�G�[�W�F���g�̏ꍇ�̊D�g�ň͂� */
		if(removeAgents[index] == true) {
			graphics.setColor(Color.gray);
			for(int i = 0; i < 5; i++) {
				graphics.drawRect(rectInfo[0]+i, rectInfo[1]+i,
				        rectInfo[2]-(2*i), rectInfo[3]-(2*i));
			}
		}

		graphics.setColor(Color.black);
		graphics.drawRect(rectInfo[0], rectInfo[1], rectInfo[2], rectInfo[3]);

		Font f = new Font("Dialog", Font.BOLD, 20);
		graphics.setFont(f);
		graphics.drawString(agid, rectInfo[0]+(rectInfo[2]/2),
		        rectInfo[1]+rectInfo[3] );
	}

	/**
	 * �w�肳�ꂽ�ʒu�ɕ`�悷���`�̏����擾���܂��B
	 * @param int index �z���̈ʒu
	 * @return int[] int[4]�̔z�� ���ɃL�����o�X��� X���W�EY���W�E���E����
	 */
	private int[] getAgentRectSize(int index) {
		int[] rectInfo = new int[4];
		rectInfo[0] = X_SPACE + (index * (X_ELEMENT_SIZE+X_SPACE));
		rectInfo[1] = Y_SPACE;
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
		int drawNum = getDrawNum();
		int[] drawSize = new int[2];
		drawSize[0] = (drawNum * (X_ELEMENT_SIZE+X_SPACE)) + X_SPACE;
		drawSize[1] = Y_ELEMENT_SIZE + (2*Y_SPACE);
		return drawSize;
	}

	/**
	 * �`�悷��v�f�����擾���܂��B
	 * @return int �`�悷��v�f��
	 */
	private int getDrawNum() {
		return agents.length;
	}

}


