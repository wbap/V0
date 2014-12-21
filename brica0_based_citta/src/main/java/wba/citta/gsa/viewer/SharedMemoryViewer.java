/**
 * SharedMemoryViewer.java
 *  ゴールスタックの状態をグラフィック表示するクラス
 *  COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2000.10 BSC miyamoto
 */
package wba.citta.gsa.viewer;

import java.awt.*;
import java.util.*;

/**
 *  ゴールスタックの状態をグラフィック表示するクラス
 */
public class SharedMemoryViewer extends Frame {

	private ScrollPane scrollPane = null;
	private SharedMemoryViewerCanvas canvas = null;

	/* ウィンドウのタイトル */
	private static final String TITLE = "Shared Memory Viewer";

	/* ウィンドウの初期サイズ */
	private int initXSize = 320;
	private int initYSize = 300;

	/**
	 * コンストラクタ
	 * @param Integer[] stateArray 共有メモリの現在の状態への参照
	 * @param LinkedList[] goalStackArray 共有メモリのゴールスタックへの参照
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
	 * 描画を更新します。
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

}


