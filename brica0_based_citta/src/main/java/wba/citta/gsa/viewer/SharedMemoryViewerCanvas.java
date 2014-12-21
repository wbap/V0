/**
 * SharedMemoryViewerCanvas.java
 *  共有メモリの状態を描画するクラス
 *  COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2001.10 BSC miyamoto
 */
package wba.citta.gsa.viewer;

import java.awt.*;
import java.util.*;
import wba.citta.gsa.*;

/**
 *  共有メモリの状態を描画するクラス
 */
public class SharedMemoryViewerCanvas extends Canvas {

	/* 現在の状態への参照 */
	private Integer[] stateArray;
	/* ゴールスタックへの参照 */
	private LinkedList[] goalStackArray;

	/* 左右の間隔 */
	private final int X_SPACE = 40;
	/* 下の間隔 */
	private final int Y_SPACE = 10;

	/* 描画する要素のサイズ */
	private final int X_ELEMENT_SIZE = 30;
	private final int Y_ELEMENT_SIZE = 30;

	/* 表示されている領域のサイズ */
	private int height;
	private int width;

	/* ダブルバッファリング用 オフスクリーンイメージ */
	private Image offImage;
	private Graphics offGraphics;

	/* 文字のフォント */
	private Font agidFont = new Font("Dialog", Font.BOLD, 20);
	private Font valueFont = new Font("Dialog", Font.PLAIN , 12);

	////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * コンストラクタ
	 * @param Integer[] stateArray 現在の状態への参照
	 * @param LinkedList[] goalStackArray ゴールスタックへの参照
	 */
	public SharedMemoryViewerCanvas(Integer[] stateArray,
	        LinkedList[] goalStackArray) {
		super();
		this.stateArray = stateArray;
		this.goalStackArray = goalStackArray;
	}

	////////////////////////////////////////////////////////////
	// public 

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

		/* ベース部分の描画 */
		drawBase(graphics);
		/* 現在の状態の描画 */
		drawState(graphics);
		/* ゴールスタック全体の描画 */
		drawStackArray(graphics);
	}

	/**
	 * オフスクリーン上のイメージのクリア
	 * @param Graphics graphics
	 */
	private void clearOffImage(Graphics graphics) {
		graphics.setColor(getBackground());
		graphics.fillRect(0, 0, width, height);
	}

	/**
	 * 現在の状態を描画します。
	 * @param Graphics graphics
	 */
	private void drawState(Graphics graphics) {
		for(int i = 0; i < stateArray.length; i++) {
			if(stateArray[i] != null) {
				drawStateElement(graphics, i, stateArray[i]);
			}
		}
	}

	/**
	 * 現在の状態の各要素を描画します。
	 * @param Graphics g
	 * @param int nodeIndex 描画するノード
	 * @param Integer element 描画する値
	 */
	private void drawStateElement(Graphics graphics, int nodeIndex,
	        Integer element) {

		/* 描画するCanvas上の位置を取得 */
		int rectInfo[] = getStackElementRectSize(nodeIndex, 0);

		graphics.setColor(Color.black);
		graphics.drawRect(rectInfo[0], rectInfo[1], rectInfo[2], rectInfo[3]);

		graphics.setFont(valueFont);
		graphics.drawString(element.toString(), rectInfo[0],
		        rectInfo[1]+(rectInfo[3]/2) );
	}


	/**
	 * ゴールスタック全体を描画します。
	 * @param Graphics g
	 */
	private void drawStackArray(Graphics graphics) {
		for(int i = 0; i < goalStackArray.length; i++) {
			drawStack(graphics, i);
		}
	}

	/**
	 * ゴールスタックの指定されたノードを描画します。
	 * @param Graphics g
	 * @param int nodeIndex 描画するノード
	 */
	private void drawStack(Graphics graphics, int nodeIndex) {
		for(int i = 0; i < goalStackArray[nodeIndex].size(); i++) {
			GoalStackElement element
			        = (GoalStackElement)goalStackArray[nodeIndex].get(i);
			drawStackElement(graphics, nodeIndex, i, element);
		}
	}

	/**
	 * ゴールスタックの指定された要素を描画します。
	 * @param Graphics g
	 * @param int x 描画するノード
	 * @param int y 描画する要素のノード中の位置
	 * @param GoalStackElement element 描画する要素
	 */
	private void drawStackElement(Graphics graphics, int x, int y,
	        GoalStackElement element) {

		String value = " " + element.value;
		String agid = " " + element.agid;

		/* 描画するCanvas上の位置を取得 */
		int rectInfo[] = getStackElementRectSize(x, y+2);

		/* 描画処理 */
		/* エージェントごとの色をテーブルから取得 */
		Color color = (Color)(ViewerProperty.colorTable).get(
		        new Integer(element.agid));

		if(color != null) {
			graphics.setColor(color);
			graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
			        rectInfo[3]);
		}

		graphics.setColor(Color.black);
		graphics.drawRect(rectInfo[0], rectInfo[1], rectInfo[2], rectInfo[3]);

		graphics.setFont(agidFont);
		graphics.drawString(agid, rectInfo[0]+(rectInfo[2]/2),
		        rectInfo[1]+rectInfo[3]);

		graphics.setFont(valueFont);
		graphics.drawString(value, rectInfo[0], rectInfo[1]+(rectInfo[3]/2) );
	}

	/**
	 * スタックのベース部分を描画します。
	 * @param Graphics g
	 */
	private void drawBase(Graphics graphics) {
		graphics.setColor(Color.blue);

		/* ノードのIDの描画 */
		for(int i = 0; i < goalStackArray.length; i++) {
			int[] rectInfo = getStackElementRectSize(i, 1);
			int xPos = rectInfo[0] + (rectInfo[2]/2);
			int yPos = rectInfo[1] + (rectInfo[3]/2);
			graphics.drawString(""+ i, xPos, yPos );
		}

		/* "GoalStack","State"の描画 */
		graphics.setFont(valueFont);

		int[] rectInfo = getStackElementRectSize(0, 2);
		graphics.drawString("Goal", 5, rectInfo[1]+(rectInfo[3]/2) );
		graphics.drawString("Stack", 5, rectInfo[1]+rectInfo[3] );

		rectInfo = getStackElementRectSize(0, 0);
		graphics.drawString("State", 5, rectInfo[1]+rectInfo[3] );
	}

	/**
	 * 指定された共有メモリの位置に描画する矩形の情報を取得します。
	 * @param int x x方向への位置
	 * @param int y y方向への位置
	 * @return int[] int[4]の配列 順にキャンバス上の X座標・Y座標・幅・高さ
	 */
	private int[] getStackElementRectSize(int x, int y) {
		Dimension d = getSize();
		int[] rectInfo = new int[4];
		rectInfo[0] = X_SPACE + (x * X_ELEMENT_SIZE);
		rectInfo[1] = d.height - ((y+1) * Y_ELEMENT_SIZE) - Y_SPACE;
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
		int[] drawNum = getDrawNum();
		int[] drawSize = new int[2];
		drawSize[0] = (drawNum[0] * X_ELEMENT_SIZE) + (X_SPACE*2);
		drawSize[1] = (drawNum[1]+1) * Y_ELEMENT_SIZE;
		return drawSize;
	}

	/**
	 * 描画する要素数を取得します。
	 * @return int[] 描画する要素数 [0]x軸方向への数 [1]y軸方向への数
	 */
	private int[] getDrawNum() {
		int[] drawNum = new int[2];
		drawNum[0] = goalStackArray.length;
		for(int i = 0; i < goalStackArray.length; i++) {
			if(drawNum[1] < goalStackArray[i].size()) {
				drawNum[1] = goalStackArray[i].size();
			}
		}

		/* ベースと、ステイトの分を追加 */
		drawNum[1] += 2;
		return drawNum;
	}

}


