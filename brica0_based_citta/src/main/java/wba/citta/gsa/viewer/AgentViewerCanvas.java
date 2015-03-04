/**
 * AgentViewerCanvas.java
 *  エージェントの動作状況を描画するクラス
 *  COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2001.10 BSC miyamoto
 */
package wba.citta.gsa.viewer;

import java.awt.*;
import java.util.*;
import wba.citta.gsa.*;

/**
 *  エージェントの動作状況を描画するクラス
 */
public class AgentViewerCanvas extends Canvas {

	/* エージェントIDの配列 */
	private int[] agents;
	/* 選択エージェントの情報 */
	private int execAgentID;
	/* 削除処理エージェントの情報 */
	private boolean[] removeAgents;

	/* 要素間の間隔 */
	private final int X_SPACE = 20;
	private final int Y_SPACE = 20;
	/* 要素のサイズ */
	private final int X_ELEMENT_SIZE = 30;
	private final int Y_ELEMENT_SIZE = 30;

	/* 表示されている領域のサイズ */
	private int height;
	private int width;

	/* ダブルバッファリング用 オフスクリーンイメージ */
	private Image offImage;
	private Graphics offGraphics;

	////////////////////////////////////////////////////////////
	// コンストラクタ  初期化処理

	/**
	 * コンストラクタ
	 * @param int[] agents エージェントIDの配列
	 * @param boolean[] removeAgents 到達ゴール削除処理を行なったエージェント
	 * の情報
	 */
	public AgentViewerCanvas(int[] agents, boolean[] removeAgents) {
		super();

		this.agents = agents;
		this.removeAgents = removeAgents;
	}

	////////////////////////////////////////////////////////////
	// public 

	/**
	 * 実行エージェントのIDを設定します。
	 * @param int execAgentID 実行エージェントのID
	 */
	public void setExecAgentID(int execAgentID) {
		this.execAgentID = execAgentID;
	}

	/**
	 * updateメソッドのオーバーライド
	 * @param Graphics g
	 */
	public void update(Graphics g) {
		paint(g);
	}

private int xSizeOld = 0;
private int ySizeOld = 0;

	/**
	 * paintメソッドのオーバーライド
	 * @param Graphics g
	 */
	public void paint(Graphics g) {

		/* 描画するエリアのサイズを取得 */
		int[] size = getUseCanvasSize();

		if(xSizeOld != size[0] || ySizeOld != size[1]) {
			setSize(size[0], size[1]);
			/* オフスクリーンイメージの作成 */
			offImage = createImage(size[0], size[1]);
			offGraphics = offImage.getGraphics();
		}

		xSizeOld = size[0];
		ySizeOld = size[1];

		/* オフスクリーンへの描画 */
		drawOffImage(offGraphics);
		/* オフスクリーンイメージを描画 */
		g.drawImage(offImage, 0, 0, this);
	}

	/**
	 * Canvas中の表示されている領域のサイズを設定します。
	 * @param int width  幅
	 * @param int height 高さ
	 */
	public void setViewportSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	////////////////////////////////////////////////////////////
	// private

	/**
	 * オフスクリーンへの描画
	 * @param Graphics graphics
	 */
	private void drawOffImage(Graphics graphics) {

		/* イメージのクリア */
		clearOffImage(graphics);

		/* 全エージェントの描画 */
		drawAgents(graphics);
	}

	/**
	 * オフスクリーンイメージのクリア
	 * @param Graphics graphics
	 */
	private void clearOffImage(Graphics graphics) {
		graphics.setColor(getBackground());
		graphics.fillRect(0, 0, width, height);
	}

	/**
	 * エージェントの動作状況の描画を行ないます。
	 * @param Graphics g
	 */
	private void drawAgents(Graphics graphics) {
		for(int i = 0; i < agents.length; i++) {
			drawAgent(graphics, i);
		}
	}

	/**
	 * 指定されたエージェントを描画します。
	 * @param Graphics g
	 * @param int index 描画するエージェントの配列上の位置
	 */
	private void drawAgent(Graphics graphics, int index) {

		/* 描画する情報の取得 */
		int rectInfo[] = getAgentRectSize(index);
		String agid = " " + agents[index];

		/* エージェントごとの色をテーブルから取得 */
		Color color = (Color)(ViewerProperty.colorTable).get(
		        new Integer(agents[index]));

		/* 描画処理 */
		if(color != null) {
			graphics.setColor(color);
			graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
			        rectInfo[3]);
		}

		/* 実行エージェンの場合青枠で囲む */
		if(agents[index] == execAgentID) {
			graphics.setColor(Color.blue);
			for(int i = 0; i < 5; i++) {
				graphics.drawRect(rectInfo[0]-i, rectInfo[1]-i,
				        rectInfo[2]+(2*i), rectInfo[3]+(2*i));
			}
		}

		/* 到達ゴール削除エージェントの場合の灰枠で囲む */
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
	 * 指定された位置に描画する矩形の情報を取得します。
	 * @param int index 配列上の位置
	 * @return int[] int[4]の配列 順にキャンバス上の X座標・Y座標・幅・高さ
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
	 * キャンバスに必要なサイズを取得します。
	 * キャンバスに必要なサイズは、描画エリアとウィンドウのサイズのうち
	 * どちらか大きい方を利用します。
	 * @return int[] [0]幅  [1]高さ
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
	 * 描画に必要なサイズを取得します。
	 * @return int[] [0]幅  [1]高さ
	 */
	private int[] getDrawAreaSize() {
		int drawNum = getDrawNum();
		int[] drawSize = new int[2];
		drawSize[0] = (drawNum * (X_ELEMENT_SIZE+X_SPACE)) + X_SPACE;
		drawSize[1] = Y_ELEMENT_SIZE + (2*Y_SPACE);
		return drawSize;
	}

	/**
	 * 描画する要素数を取得します。
	 * @return int 描画する要素数
	 */
	private int getDrawNum() {
		return agents.length;
	}

}


