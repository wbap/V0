/**
 * TreeViewer.java
 *  ツリーの状態をグラフィックス表示するクラス
 *  COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2000.10 BSC miyamoto
 */
package wba.citta.gsa.viewer;

import java.awt.*;
import wba.citta.gsa.*;

/**
 *  ツリーの状態をグラフィックス表示するクラス
 */
public class TreeViewer extends Frame {

	private ScrollPane scrollPane = null;
	private TreeViewerCanvas canvas = null;

	/* ウィンドウのタイトル */
	private static final String TITLE = "Fail Agent Tree Viewer";

	/* ウィンドウの初期サイズ */
	private int initXSize = 460;
	private int initYSize = 740;

	/**
	 * コンストラクタ
	 * @param FailAgentTreeElement rootElement ツリーのルートへの参照
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
	 * 描画を更新
	 */
	private void renew() {
		Dimension d = scrollPane.getViewportSize();
		canvas.setViewportSize(d.width, d.height);
		canvas.repaint();
		validateTree();
	}

	/**
	 * paintメソッドのオーバーライド
	 * @param Graphics g
	 */
	public void paint(Graphics graphics) {
		renew();
	}

	/**
	 * ツリーのカレントを設定します。
	 * @param FailAgentTreeElement currentElement ツリーのカレント
	 */
	public void setCurrentElement(FailAgentTreeElement currentElement) {
		canvas.setCurrentElement(currentElement);
	}

}


