/**
 * AgentViewer.java
 *  �G�[�W�F���g�̓���󋵂��O���t�B�b�N�\������N���X
 *  COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2000.10 BSC miyamoto
 */
package gsa.viewer;

import java.awt.*;
import gsa.*;

/**
 *  �G�[�W�F���g�̓���󋵂��O���t�B�b�N�\������N���X
 */
public class AgentViewer extends Frame {

	private ScrollPane scrollPane = null;
	private AgentViewerCanvas canvas = null;

	/* �E�B���h�E�̃^�C�g�� */
	private static final String TITLE = "Agent Viewer";

	/* �E�B���h�E�̏����T�C�Y */
	private int initXSize = 460;
	private int initYSize = 120;

	/**
	 * �R���X�g���N�^
	 * @param int[] agnets �S�G�[�W�F���g��ID�̔z��
	 * @param boolean[] removeAgents ���B�S�[���폜�������s�Ȃ����G�[�W�F���g
	 * �̏��
	 */
	public AgentViewer(int[] agents, boolean[] removeAgents) {
		super(TITLE);

		canvas = new AgentViewerCanvas(agents, removeAgents);
		scrollPane = new ScrollPane(ScrollPane.SCROLLBARS_ALWAYS);
		scrollPane.add(canvas, null, 0);
		add(scrollPane);

		initXSize = ViewerProperty.agentViewerInitSize[0];
		initYSize = ViewerProperty.agentViewerInitSize[1];

		setSize(initXSize, initYSize);
		setVisible(true);
	}

	/**
	 * ���s�G�[�W�F���g��ID��ݒ肵�܂��B
	 * @param int execAgentID ���s�G�[�W�F���g��ID
	 */
	public void setExecAgentID(int execAgentID) {
		canvas.setExecAgentID(execAgentID);
	}

	/**
	 * �`����X�V���܂��B
	 */
	private void renew() {
		Dimension d = scrollPane.getViewportSize();
		canvas.setViewportSize(d.width, d.height);
		canvas.repaint();
		validateTree();
	}

	/**
	 * paint���\�b�h�̃I�[�o�[���C�h
	 * @param Graphics g
	 */
	public void paint(Graphics graphics) {
		renew();
	}

}


