/**
 * CDViewerCanvas.java
 *  CognitiveDistanceの各レイヤのセグメント情報をグラフィック表示するクラス
 *  COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2000.11 BSC miyamoto
 */
package cognitivedistance.viewer;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * CognitiveDistanceの各レイヤのセグメント情報をグラフィック表示するクラス
 * です。
 */
public class CDViewerCanvas extends Canvas {

	/* ますの数 */
	private int xNum;
	private int yNum;
	/* 罫線の間隔 */
	private int xSpace;
	private int ySpace;
	/* キャンバスのサイズ */
	private int height;
	private int width;

	/* サイズ変更のフラグ */
	private boolean resized;

	/* ダブルバッファリング用 オフスクリーンイメージ */
	private Image offImage;
	private Graphics offGraphics;

	/* 現在の状態に属する環境の状態のリスト */
	private LinkedList currentStateList;
	/* ゴールの状態に属する環境の状態のリスト */
	private LinkedList goalStateList;
	/* 上位層からのサブゴールの状態に属する環境の状態のリスト */
	private LinkedList upperSubgoalList;
	/* 現在層でのサブゴールの状態に属する環境の状態のリスト */
	private LinkedList currentSubgoalList;
	/* オプション情報を設定するリスト */
	private LinkedList optionList;

	/* 矩形を描画するための値を設定する配列 */
	private int[] rectInfo;
	private int[] innerRectInfo;
	private int[] center;
	private int[] xPoints;
	private int[] yPoints;
	private int pointNum;

	private boolean renewFlg;

	/* 各マスの分割についての設定  true:5つ fasle:4つ */
	private boolean flagSeparate = true;

	////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * コンストラクタ
	 * @param int xNum x軸方向のますの数
	 * @param int yNum y軸方向のますの数
	 */
	public CDViewerCanvas(int xNum, int yNum) {
		super();
		/* イベントリスナの登録 */
		addComponentListener(new CanvasComponentAdapter());
		/* 初期化 */
		resized = false;
		initCanvas(xNum, yNum);

		rectInfo = new int[4];
		innerRectInfo = new int[4];
		center = new int[2];
		xPoints = new int[4];
		yPoints = new int[4];
		pointNum = 4;
	}

	/**
	 * コンストラクタ
	 * 各マスの分割について指定可能
	 * @param int xNum x軸方向のますの数
	 * @param int yNum y軸方向のますの数
	 * @param boolean flagSeparate 各マスの分割 true:5つに分割 false:4つに分割
	 */
	public CDViewerCanvas(int xNum, int yNum, boolean flagSeparate) {
		this(xNum, yNum);
		this.flagSeparate = flagSeparate;
	}

	////////////////////////////////////////////////////////////
	// public 

	/**
	 * updateメソッドのオーバーライド
	 */
	public void update(Graphics g) {
		paint(g);
	}

	/**
	 * paintメソッドのオーバーライド
	 */
	public void paint(Graphics g) {

		/* 始めとサイズ変更時はオフスクリーンイメージの初期化 */
		if( (offGraphics == null) || (resized == true) ){
			offImage = createImage(width, height);
			offGraphics = offImage.getGraphics();
			resized = false;
		}
		/* オフスクリーンへの描画 */
		drawOffImage(offGraphics);
		/* オフスクリーンイメージを描画 */
		g.drawImage(offImage, 0, 0, this);
	}

	/**
	 * この層での現在の状態に属する環境の状態のリストを設定します。
	 * @param LinkedList currentStateList  環境の状態のリスト
	 */
	public void setCurrentStateList(LinkedList currentStateList) {
		this.currentStateList = currentStateList;
	}

	/**
	 * この層でのゴールの状態に属する環境の状態のリストを設定します。
	 * @param LinkedList goalStateList  環境の状態のリスト
	 */
	public void setGoalStateList(LinkedList goalStateList) {
		this.goalStateList = goalStateList;
	}

	/**
	 * この層の上位層が設定したサブゴールの状態に属する環境の状態のリストを
	 * 設定します。
	 * @param LinkedList goalStateList  環境の状態のリスト
	 */
	public void setUpperSubgoalList(LinkedList upperSubgoalList) {
		this.upperSubgoalList = upperSubgoalList;
	}

	/**
	 * この層で設定したサブゴールの状態に属する環境の状態のリストを設定します。
	 * @param LinkedList goalStateList  環境の状態のリスト
	 */
	public void setCurrentSubgoalList(LinkedList currentSubgoalList) {
		this.currentSubgoalList = currentSubgoalList;
	}

	/**
	 * オプション情報のリストを設定します。
	 * @param LinkedList optionList
	 */
	public void setOptionList(LinkedList optionList) {
		this.optionList = optionList;
	}

	/**
	 * サブゴールが新しくなったことを示すフラグを設定します
	 * @param boolean  true 新しいサブゴール
	 */
	public void setRenewFlg(boolean b) {
		renewFlg = b;
	}

	////////////////////////////////////////////////////////////
	// private

	/**
	 * 初期化処理
	 * @param int xNum ｘ方向のますの数
	 * @param int yNum ｙ方向のますの数
	 */
	private void initCanvas(int xNum, int yNum) {

		this.xNum = xNum;
		this.yNum = yNum;

		/* サイズ情報の設定 */
		setSizeInfo();
	} 


	/**
	 * サイズに関する情報を設定します。
	 */
	private void setSizeInfo() {
		/* キャンバスのサイズ */
		Dimension d = getSize();
		height = d.height;
		width = d.width;
		/* 罫線の間隔 */
		xSpace = d.width / (xNum+2);
		ySpace = d.height / (yNum+2);
	}


	/**
	 * オフスクリーンへの描画
	 * @param Graphics graphics
	 */
	private void drawOffImage(Graphics graphics) {

		/* イメージのクリア */
		graphics.setColor(getBackground());
		graphics.fillRect(0, 0, width, height);

		/* option */
		graphics.setColor(Color.cyan);
		fillStateListPolygon(graphics, optionList);

		/* goaltState */
		graphics.setColor(Color.green);
		fillStateListPolygon(graphics, goalStateList);

		/* upperSubgoal */
		Color subgoalColor = Color.magenta/* new Color(0, 0, 150)*/;
		if(renewFlg) {
			graphics.setColor(Color.yellow);
			renewFlg = false;
		}else {
			graphics.setColor(subgoalColor);
		}
		fillStateListPolygon(graphics, upperSubgoalList);

		/* currentSubgoal */
		graphics.setColor(Color.red);
		fillStateListPolygon(graphics, currentSubgoalList);

		/* リスト内の状態のますを塗りつぶす */
		/* currentState */
		graphics.setColor(Color.blue);
		fillStateListPolygon(graphics, currentStateList);

		/* 各ます分割する線を描画 */
		graphics.setColor(Color.gray);
		if(flagSeparate == true) {
			/* 停止状態あり 5つに分割 */
			for(int x = 0; x < xNum; x++) {
				for(int y = 0; y < yNum; y++) {
					drawSeparateLine(graphics, x, y);
				}
			}
		}else {
			/* 停止状態なし 4つに分割 */
			for(int x = 0; x < xNum; x++) {
				for(int y = 0; y < yNum; y++) {
					drawSeparateLine2(graphics, x, y);
				}
			}
		}

		/* 罫線の描画 */
		graphics.setColor(Color.gray/*black*/);
		/* ｘ軸方向の罫線 */
		for(int i = ySpace; i <= ySpace * (yNum+1); i = i+ySpace) {
			graphics.drawLine(xSpace, i, xSpace * (xNum+1), i);
		}
		/* ｙ軸方向の罫線 */
		for(int i = xSpace; i <= xSpace * (xNum+1); i = i+xSpace) {
			graphics.drawLine(i, ySpace, i, ySpace * (yNum+1));
		}

	}


	/**
	 * 矩形を分割する線を引きます。(停止状態有り 5つに分割)
	 * @param Graphics graphics 
	 * @param int x x座標
	 * @param int y y座標
	 */
	private void drawSeparateLine(Graphics graphics, int x, int y) {

		int[] rectInfo = getRectInfo(x, y);
		int[] innerRectInfo = getInnerRectInfo(rectInfo);

		graphics.drawLine(rectInfo[0], rectInfo[1],
		        innerRectInfo[0], innerRectInfo[1]);
		graphics.drawLine(rectInfo[0]+rectInfo[2], rectInfo[1],
		        innerRectInfo[0]+innerRectInfo[2], innerRectInfo[1]);
		graphics.drawLine(rectInfo[0], rectInfo[1]+rectInfo[3],
		        innerRectInfo[0], innerRectInfo[1]+innerRectInfo[3]);
		graphics.drawLine(rectInfo[0]+rectInfo[2], rectInfo[1]+rectInfo[3],
		        innerRectInfo[0]+innerRectInfo[2],
		        innerRectInfo[1]+innerRectInfo[3]);

		/* 中心の矩形の描画 */
		graphics.drawLine(innerRectInfo[0], innerRectInfo[1],
		        innerRectInfo[0]+innerRectInfo[2], innerRectInfo[1]);
		graphics.drawLine(innerRectInfo[0]+innerRectInfo[2], innerRectInfo[1],
		        innerRectInfo[0]+innerRectInfo[2],
		        innerRectInfo[1]+innerRectInfo[3]);
		graphics.drawLine(innerRectInfo[0]+innerRectInfo[2],
		        innerRectInfo[1]+innerRectInfo[3], innerRectInfo[0],
		        innerRectInfo[1]+innerRectInfo[3]);
		graphics.drawLine(innerRectInfo[0], innerRectInfo[1]+innerRectInfo[3],
		        innerRectInfo[0], innerRectInfo[1]);
	}


	/**
	 * 矩形を分割する線を引きます。(停止状態なし 4つに分割)
	 * @param Graphics graphics 
	 * @param int x x座標
	 * @param int y y座標
	 */
	private void drawSeparateLine2(Graphics graphics, int x, int y) {

		int[] rectInfo = getRectInfo(x, y);
		graphics.drawLine(rectInfo[0], rectInfo[1], rectInfo[0]+rectInfo[2],
		        rectInfo[1]+rectInfo[3]);
		graphics.drawLine(rectInfo[0]+rectInfo[2], rectInfo[1], rectInfo[0],
		        rectInfo[1]+rectInfo[3]);
	}


	/**
	 * 引数で設定された状態のリストに対応する多角形を塗りつぶします。
	 * @param Graphics g
	 * @param LinkedList stateList 状態のリスト
	 */
	private void fillStateListPolygon(Graphics g, LinkedList stateList) {

		/* リストがなければ処理しない */
		if(stateList == null) {
			return;
		}

		/* リストの各要素を塗りつぶす */
		try{
			ListIterator stateListIterator = stateList.listIterator();
			while(stateListIterator.hasNext()) {
				Vector state = (Vector)stateListIterator.next();
				// 2001.05.24 修正 miyamoto 古いバージョンのjavaに対応
//				fillStatePolygon(g, ((Integer)state.get(0)).intValue(),
//				        ((Integer)state.get(1)).intValue(),
//				        ((Integer)state.get(2)).intValue() );
				fillStatePolygon(g, ((Integer)state.elementAt(0)).intValue(),
				        ((Integer)state.elementAt(1)).intValue(),
				        ((Integer)state.elementAt(2)).intValue() );
			}
		}catch(ConcurrentModificationException e) {
		}
	}


	/**
	 * 指定された位置・アクションに対応する領域を塗りつぶします。
	 * @param int x ｘ軸方向の位置
	 * @param int y y軸方向の位置
	 * @param int action 行動
	 */
	private void fillStatePolygon(Graphics g, int x, int y, int action) {

		int[] rectInfo = getRectInfo(x, y);
		int[] innerRectInfo = getInnerRectInfo(rectInfo);

		/* 停止 */
		if(action == -1) {
			xPoints[0] = innerRectInfo[0];
			yPoints[0] = innerRectInfo[1];

			xPoints[1] = innerRectInfo[0] + innerRectInfo[2];
			yPoints[1] = innerRectInfo[1];

			xPoints[2] = innerRectInfo[0] + innerRectInfo[2];
			yPoints[2] = innerRectInfo[1] + innerRectInfo[3];

			xPoints[3] = innerRectInfo[0];
			yPoints[3] = innerRectInfo[1] + innerRectInfo[3];
		}

		/* 上 */
		if(action == 0) {
			xPoints[0] = rectInfo[0];
			yPoints[0] = rectInfo[1] + rectInfo[3];

			xPoints[1] = innerRectInfo[0];
			yPoints[1] = innerRectInfo[1] + innerRectInfo[3];

			xPoints[2] = innerRectInfo[0] + innerRectInfo[2];
			yPoints[2] = innerRectInfo[1] + innerRectInfo[3];

			xPoints[3] = rectInfo[0] + rectInfo[2];
			yPoints[3] = rectInfo[1] + rectInfo[3];
		}

		/* 左 */
		if(action == 2) {
			xPoints[0] = rectInfo[0] + rectInfo[2];
			yPoints[0] = rectInfo[1];

			xPoints[1] = innerRectInfo[0] + innerRectInfo[2];
			yPoints[1] = innerRectInfo[1];

			xPoints[2] = innerRectInfo[0] + innerRectInfo[2];
			yPoints[2] = innerRectInfo[1] + innerRectInfo[3];

			xPoints[3] = rectInfo[0] + rectInfo[2];
			yPoints[3] = rectInfo[1] + rectInfo[3];
		}

		/* 下 */
		if(action == 4) {
			xPoints[0] = rectInfo[0];
			yPoints[0] = rectInfo[1];

			xPoints[1] = innerRectInfo[0];
			yPoints[1] = innerRectInfo[1];

			xPoints[2] = innerRectInfo[0] + innerRectInfo[2];
			yPoints[2] = innerRectInfo[1];

			xPoints[3] = rectInfo[0] + rectInfo[2];
			yPoints[3] = rectInfo[1];
		}

		/* 右 */
		if(action == 6) {
			xPoints[0] = rectInfo[0];
			yPoints[0] = rectInfo[1];

			xPoints[1] = innerRectInfo[0];
			yPoints[1] = innerRectInfo[1];

			xPoints[2] = innerRectInfo[0];
			yPoints[2] = innerRectInfo[1] + innerRectInfo[3];

			xPoints[3] = rectInfo[0];
			yPoints[3] = rectInfo[1] + rectInfo[3];
		}

		g.fillPolygon(xPoints, yPoints, pointNum);
	}


	/**
	 * 指定された地図上のＸＹ座標に対応する矩形の情報を取得します。
	 * @param int x 地図上のＸ座標
	 * @param int y 地図上のＹ座標
	 * @return int[] int[4]の配列 順にキャンバス上の X座標・Y座標・幅・高さ
	 */
	private int[] getRectInfo(int x, int y) {
		/* 範囲内かチェック */
		if( (x >= 0)&&(y >= 0) && (x < xNum)&&(y < yNum) ) {
			rectInfo[0] = ((x+1)*xSpace) + 1;
			rectInfo[1] = ((y+1)*ySpace) + 1;
			rectInfo[2] = xSpace-1;
			rectInfo[3] = ySpace-1;
		}else {
			rectInfo[0] = 0;
			rectInfo[1] = 0;
			rectInfo[2] = 0;
			rectInfo[3] = 0;
		}
		return rectInfo;
	}


	/**
	 * 停止状態を表わす矩形内の矩形の情報を取得します。
	 * @param int[] rectInfo 矩形の情報
	 * @return int[] 内側の矩形の情報
	 */
	private int[] getInnerRectInfo(int[] rectInfo) {
		int[] center = getCenter(rectInfo);

		int innerXLength = rectInfo[2]/3;
		int innerYLength = rectInfo[3]/3;

		innerRectInfo[0] = center[0] - (innerXLength/2);
		innerRectInfo[1] = center[1] - (innerYLength/2);
		innerRectInfo[2] = innerXLength;
		innerRectInfo[3] = innerYLength;

		return innerRectInfo;
	}


	/**
	 * 矩形の中心位置を取得します。
	 * @param int[] rectInfo 矩形の情報
	 * @return int[] 中心位置のx、y座標
	 */
	private int[] getCenter(int[] rectInfo) {
		center[0] = rectInfo[0] + (rectInfo[2]/2);
		center[1] = rectInfo[1] + (rectInfo[3]/2);
		return center;
	}

	//////////////////////////////////////////////////
	// イベント処理

	/**
	 * サイズ変更のイベントを処理するインナークラス
	 */
	class CanvasComponentAdapter extends ComponentAdapter {

		/**
		 * サイズ変更時の処理
		 */
		public void componentResized(ComponentEvent e) {
			/* サイズ情報の設定 */
			setSizeInfo();
			resized = true;
			/* 再描画 */
			repaint();
		}
	}

}


