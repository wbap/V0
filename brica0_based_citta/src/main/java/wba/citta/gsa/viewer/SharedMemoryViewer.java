/**
 * SharedMemoryViewer.java
 *  �S�[���X�^�b�N�̏�Ԃ��O���t�B�b�N�\������N���X
 *  COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2000.10 BSC miyamoto
 */
package wba.citta.gsa.viewer;

import java.awt.*;
import java.util.*;

/**
 *  �S�[���X�^�b�N�̏�Ԃ��O���t�B�b�N�\������N���X
 */
public class SharedMemoryViewer extends Frame {

	private ScrollPane scrollPane = null;
	private SharedMemoryViewerCanvas canvas = null;

	/* �E�B���h�E�̃^�C�g�� */
	private static final String TITLE = "Shared Memory Viewer";

	/* �E�B���h�E�̏����T�C�Y */
	private int initXSize = 320;
	private int initYSize = 300;

	/**
	 * �R���X�g���N�^
	 * @param Integer[] stateArray ���L�������̌��݂̏�Ԃւ̎Q��
	 * @param LinkedList[] goalStackArray ���L�������̃S�[���X�^�b�N�ւ̎Q��
	 */
	public SharedMemoryViewer(Integer[] stateArray,
	        LinkedList[] goalStackArray) {
		super(TITLE);

		canvas = new SharedMemoryViewerCanvas(stateArray, goalStackArray);
		scrollPane = new ScrollPane(ScrollPane.SCROLLBARS_ALWAYS);
		scrollPane.add(canvas, null, 0);
		add(scrollPane);

		initXSize = ViewerProperty.sharedMemoryViewerInitSize[0];
		initYSize = ViewerProperty.sharedMemoryViewerInitSize[1];

		setSize(initXSize, initYSize);
		setVisible(true);
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


