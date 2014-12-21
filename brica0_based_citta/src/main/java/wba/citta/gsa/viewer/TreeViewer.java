/**
 * TreeViewer.java
 *  �c���[�̏�Ԃ��O���t�B�b�N�X�\������N���X
 *  COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2000.10 BSC miyamoto
 */
package wba.citta.gsa.viewer;

import java.awt.*;
import wba.citta.gsa.*;

/**
 *  �c���[�̏�Ԃ��O���t�B�b�N�X�\������N���X
 */
public class TreeViewer extends Frame {

	private ScrollPane scrollPane = null;
	private TreeViewerCanvas canvas = null;

	/* �E�B���h�E�̃^�C�g�� */
	private static final String TITLE = "Fail Agent Tree Viewer";

	/* �E�B���h�E�̏����T�C�Y */
	private int initXSize = 460;
	private int initYSize = 740;

	/**
	 * �R���X�g���N�^
	 * @param FailAgentTreeElement rootElement �c���[�̃��[�g�ւ̎Q��
	 */
	public TreeViewer(FailAgentTreeElement rootElement) {
		super(TITLE);

		canvas = new TreeViewerCanvas(rootElement);
		scrollPane = new ScrollPane(ScrollPane.SCROLLBARS_ALWAYS);
		scrollPane.add(canvas, null, 0);
		add(scrollPane);

		initXSize = ViewerProperty.treeViwerInitSize[0];
		initYSize = ViewerProperty.treeViwerInitSize[1];

		setSize(initXSize, initYSize);
		setVisible(true);
	}

	/**
	 * �`����X�V
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

	/**
	 * �c���[�̃J�����g��ݒ肵�܂��B
	 * @param FailAgentTreeElement currentElement �c���[�̃J�����g
	 */
	public void setCurrentElement(FailAgentTreeElement currentElement) {
		canvas.setCurrentElement(currentElement);
	}

}


