/**
 * TreeViewerCanvas.java
 *  ツリーの状態を描画するクラス
 *  COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2001.10 BSC miyamoto
 */
package wba.citta.gsa.viewer;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import wba.citta.gsa.*;

/**
 *  ツリーの状態を描画するクラス
 */
public class TreeViewerCanvas extends Canvas {

	/* ツリーのルート */
	private FailAgentTreeElement rootElement = null;
	/* ツリーのカレント */
	private FailAgentTreeElement currentElement = null;

	/* 描画する要素の間隔 */
	private final int X_SPACE = 30;
	private final int Y_SPACE = 20;
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
	private Font agrFont = new Font("Dialog", Font.PLAIN , 12);

	/* y方向の描画位置 */
	private int yPos = 0;

	////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * コンストラクタ
	 * @param FailAgentTreeElement rootElement ツリーのルート
	 */
	public TreeViewerCanvas(FailAgentTreeElement rootElement) {
		super();
		this.rootElement = rootElement;
	}

	////////////////////////////////////////////////////////////
	// public 

	/**
	 * ツリーのカレントを設定します。
	 * @param FailAgentTreeElement currentElement ツリーのカレント
	 */
	public void setCurrentElement(FailAgentTreeElement currentElement) {
		this.currentElement = currentElement;
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
//		int[] size = getUseCanvasSize();
//		if(xSizeOld != size[0] || ySizeOld != size[1]) {
//			setSize(size[0], size[1]);
//			/* オフスクリーンイメージの作成 */
//			offImage = createImage(size[0], size[1]);
//			offGraphics = offImage.getGraphics();
//		}
//		xSizeOld = size[0];
//		ySizeOld = size[1];

// 描画エリアのサイズを固定
		if(offImage == null) {
			/* オフスクリーンイメージの作成 */
			setSize(1500, 2000);
			offImage = createImage(1500, 2000);
			offGraphics = offImage.getGraphics();
		}
// ここまで

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
		/* ツリーの描画 */
		drawTree(graphics);
	}

	/**
	 * オフスクリーン上のイメージのクリア
	 * @param Graphics graphics
	 */
	private void clearOffImage(Graphics graphics) {
		graphics.setColor(getBackground());
//		graphics.fillRect(0, 0, width, height);
// 描画エリアを固定
		graphics.fillRect(0, 0, 1500, 2000);
	}

	/**
	 * ツリー全体を描画します。
	 * @param Graphics g
	 */
	private void drawTree(Graphics graphics) {
		yPos = 0;

		drawRoot(graphics);
		drawChild(graphics, 0, 0, rootElement);
	}


	/**
	 * 指定された要素の子のツリーを描画します。
	 * @param Graphics g
	 * @param int x elementのツリー上のx方向の位置
	 * @param int y elementのツリー上のy方向の位置
	 * @param int FailAgentTreeElement element 描画する子の親となる要素
	 */
	private void drawChild(Graphics graphics, int x, int y,
	        FailAgentTreeElement element) {
//		for(int i = 0; i < element.next.size(); i++) {
//			FailAgentTreeElement nextElement = 
//			        (FailAgentTreeElement)element.next.get(i);
		// 子のリストを逆にたどる
		for(int i = element.next.size()-1; i >= 0 ; i--) {
			FailAgentTreeElement nextElement = 
			        (FailAgentTreeElement)element.next.get(i);

			drawTreeElement(graphics, nextElement, x+1, yPos);
			drawLink(graphics, x, y, x+1, yPos);
			drawChild(graphics, x+1, yPos, nextElement);
			yPos = yPos + 1;
		}
	}

	/**
	 * 親子関係のリンクを描画
	 * @param Graphics graphics
	 * @param int parentX 親のx方向の位置
	 * @param int parentY 親のy方向の位置
	 * @param int childX 子のx方向の位置
	 * @param int childY 子のy方向の位置
	 */
	private void drawLink(Graphics graphics, int parentX, int parentY,
	        int childX, int childY) {
		int parentRectInfo[] = getTreeElementRectSize(parentX, parentY);
		int childRectInfo[] = getTreeElementRectSize(childX, childY);
		int startX = parentRectInfo[0] + parentRectInfo[2];
		int startY = parentRectInfo[1] + (parentRectInfo[3]/2);
		int endX = childRectInfo[0];
		int endY = childRectInfo[1] + (childRectInfo[3]/2);

		graphics.drawLine(startX, startY, endX, endY);
	}

	/**
	 * ツリーの指定された要素を描画します。
	 * @param Graphics graphics
	 * @param FailAgentTreeElement element 描画する要素
	 * @param int x x方向の位置
	 * @param int y y方向の位置
	 */
	private void drawTreeElement(Graphics graphics,
	        FailAgentTreeElement element, int x, int y) {

		/* 描画する情報の取得 */
		int rectInfo[] = getTreeElementRectSize(x, y);
		String agidAndValue = element.agid + "";
//		String agidAndValue = element.agid + ":" + element.goal;
		String agr = " " + element.agr;

		/* 描画処理 */

		/* ツリーのカレントなら青枠で囲む */
		if(element == currentElement) {
			graphics.setColor(Color.blue);
			for(int i = 0; i < 5; i++) {
				graphics.drawRect(rectInfo[0]-i, rectInfo[1]-i,
				        rectInfo[2]+(2*i), rectInfo[3]+(2*i));
			}
		}

		/* エージェントごとの色をテーブルから取得 */
		Color color = (Color)(ViewerProperty.colorTable).get(
		        new Integer(element.agid));

		/* 各要素の描画 */
		if(element.agr == 0) {
			if(color != null) {
				graphics.setColor(color);
				graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
				        rectInfo[3]);
			}
			graphics.setColor(Color.black);
			graphics.drawRect(rectInfo[0], rectInfo[1], rectInfo[2],
			        rectInfo[3]);

		}else if( (element.agr==1) || (element.agr==2) ||
		        (element.agr==3) ) {

			if(color != null) {
				graphics.setColor(color);
				graphics.fillArc(rectInfo[0], rectInfo[1], rectInfo[2],
				        rectInfo[3], 0, 360);
			}
			graphics.setColor(Color.black);
			graphics.drawArc(rectInfo[0], rectInfo[1], rectInfo[2],
			        rectInfo[3], 0, 360);
			graphics.setFont(agrFont);
			graphics.drawString(agr, rectInfo[0]+rectInfo[2],
			        rectInfo[1]+(rectInfo[3]/2));

		}else if( (element.agr==4) || (element.agr==5) ||
		        (element.agr==6) ) {

			if(color != null) {
				graphics.setColor(color);
				graphics.fillArc(rectInfo[0], rectInfo[1], rectInfo[2],
				        rectInfo[3], 0, 360);
			}
			graphics.setColor(Color.black);
			graphics.drawRect(rectInfo[0], rectInfo[1], rectInfo[2],
			        rectInfo[3]);
			graphics.drawArc(rectInfo[0], rectInfo[1], rectInfo[2],
			        rectInfo[3], 0, 360);
			graphics.setFont(agrFont);
			graphics.drawString(agr, rectInfo[0]+rectInfo[2],
			        rectInfo[1]+(rectInfo[3]/2));

		}

		/* エージェントIDの描画 */
		graphics.setFont(agidFont);
		graphics.drawString(agidAndValue, rectInfo[0]+(rectInfo[2]/2),
		        rectInfo[1]+rectInfo[3] );
	}


	/**
	 * キャンバスに必要なサイズを取得します。
	 * キャンバスに必要なサイズは、描画に必要なサイズと、ウィンドウのサイズの
	 * うち大きなサイズを利用。
	 * @return int[] キャンバスに必要なサイズ int[0]:幅 int[1]:高さ
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
	 * @return int[] 描画に必要なサイズ int[0]:幅 int[1]:高さ
	 */
	private int[] getDrawAreaSize() {

		/* 描画する要素数を取得 */
		int[] drawNum = new int[2];
		childSize(rootElement, drawNum);

		/* 要素数をサイズに変換 */
		int[] drawSize = new int[2];
		drawSize[0] = (drawNum[0] * (X_SPACE + X_ELEMENT_SIZE)) + X_SPACE;
		drawSize[1] = (drawNum[1] * (Y_SPACE + Y_ELEMENT_SIZE)) + Y_SPACE;
		return drawSize;
	}

	/**
	 * 描画を行なう要素数を取得します。
	 * @param FailAgentTreeElement element
	 * @param int[] drawNum
	 */
	private void childSize(FailAgentTreeElement element, int[] drawNum) {
		for(int i = 0; i < element.next.size(); i++) {
			FailAgentTreeElement nextElement = null;
			nextElement = (FailAgentTreeElement)element.next.get(i);
			drawNum[0] ++;
			childSize(nextElement, drawNum);
			drawNum[1] ++;
		}
	}

	/**
	 * ツリーのルートを描画します。
	 * @param Graphics g
	 */
	private void drawRoot(Graphics graphics) {
		graphics.setFont(agrFont);
		graphics.setColor(Color.black);
		graphics.drawString("root", X_SPACE-(X_ELEMENT_SIZE/2),
		        Y_SPACE+(Y_ELEMENT_SIZE/4)-5);
		graphics.fillArc(X_SPACE-(X_ELEMENT_SIZE/2),
		        Y_SPACE+(Y_ELEMENT_SIZE/4), X_ELEMENT_SIZE/2, Y_ELEMENT_SIZE/2,
		        0, 360);
	}

	/**
	 * ツリー上の指定された位置の要素を描画する矩形の情報を取得します。
	 * @param int x 要素のx方向の位置
	 * @param int y 要素のy方向の位置
	 * @return int[] int[4]の配列 順にキャンバス上の X座標・Y座標・幅・高さ
	 */
	private int[] getTreeElementRectSize(int x, int y) {
		int[] rectInfo = new int[4];
		rectInfo[0] = X_SPACE + (x*(X_SPACE+X_ELEMENT_SIZE) - X_ELEMENT_SIZE);
		rectInfo[1] = Y_SPACE + (y*(Y_SPACE+Y_ELEMENT_SIZE));
		rectInfo[2] = X_ELEMENT_SIZE;
		rectInfo[3] = Y_ELEMENT_SIZE;
		return rectInfo;
	}

}


