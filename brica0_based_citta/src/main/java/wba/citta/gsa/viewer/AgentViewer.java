/**
 * AgentViewer.java
 *  エージェントの動作状況をグラフィック表示するクラス
 *  COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2000.10 BSC miyamoto
 */
package wba.citta.gsa.viewer;

import java.awt.*;
import wba.citta.gsa.*;

/**
 *  エージェントの動作状況をグラフィック表示するクラス
 */
public class AgentViewer extends Frame {

	private ScrollPane scrollPane = null;
	private AgentViewerCanvas canvas = null;

	/* ウィンドウのタイトル */
	private static final String TITLE = "Agent Viewer";

	/* ウィンドウの初期サイズ */
	private int initXSize = 460;
	private int initYSize = 120;

	/**
	 * コンストラクタ
	 * @param int[] agnets 全エージェントのIDの配列
	 * @param boolean[] removeAgents 到達ゴール削除処理を行なったエージェント
	 * の情報
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
	 * 実行エージェントのIDを設定します。
	 * @param int execAgentID 実行エージェントのID
	 */
	public void setExecAgentID(int execAgentID) {
		canvas.setExecAgentID(execAgentID);
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


