/**
 * EnvironmentCanvas.java
 *  環境グラフィック処理を行うクラス
 *  COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2000.09 BSC miyamoto
 */
package wba.citta.environment;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.net.*;

/**
 *  環境グラフィック処理を行うクラスです
 */
public class EnvironmentCanvas extends Canvas {

	/* 地図情報 */
	private String[][] map;

	/* ロボットの位置情報 */
	private int[] robotState;

	/* 報酬の情報 */
	private String[][] rewardMap;

	/* 床の色情報 */
	private String[][] colorMap;

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

	private Color redFloor = new Color(255, 200, 200);
	private Color greenFloor = new Color(200, 255, 200);
	private Color blueFloor = new Color(200, 200, 255);

	////////////////////////////////////////////////////////////
	// コンストラクタ  初期化処理

	/**
	 * コンストラクタ
	 */
	public EnvironmentCanvas() {
		super();
		/* サイズ変更についての処理を行なうイベントリスナの登録 */
		addComponentListener(new CanvasComponentAdapter());
		/* 変数の初期化 */
		resized = false;
		/*  */
		initImage();
	}

	/**
	 * 初期化処理
	 * @param String[][] map    環境の地図情報
	 * @param int[] robotState  ロボットの位置  int[0]=x座標  int[1]=y座標
	 */
	public void initCanvas(String[][] map, int[] robotState) {
		this.map = map;
		this.robotState = robotState;

		/* サイズ情報の設定 */
		setSizeInfo();
	} 

	/**
	 * 初期化処理
	 * @param String[][] map       環境の地図情報
	 * @param int[] robotState     ロボットの位置  int[0]=x座標  int[1]=y座標
	 * @param String[][] rewardMap 報酬のテーブル
	 *                             地図情報と同じサイズのテーブルで対応する
	 *                             位置に報酬が設定されたもの
	 */
	public void initCanvas(String[][] map, int[] robotState,
	        String[][] rewardMap) {
		initCanvas(map,robotState);
		this.rewardMap = rewardMap;
	}

	/**
	 * 初期化処理
	 * @param String[][] map       環境の地図情報
	 * @param int[] robotState     ロボットの位置  int[0]=x座標  int[1]=y座標
	 * @param String[][] rewardMap 報酬のテーブル
	 *                             地図情報と同じサイズのテーブルで対応する
	 *                             位置に報酬が設定されたもの
	 * @param String[][] colorMap  フロアの色を設定したテーブル
	 *                             地図情報と同じサイズのテーブルで対応する
	 *                             位置に色が設定されたもの
	 */
	public void initCanvas(String[][] map, int[] robotState,
	        String[][] rewardMap, String[][] colorMap) {
		initCanvas(map, robotState, rewardMap);
		this.colorMap = colorMap;
	}

	private Image telephoneImage = null;
	private Image keyImage = null;
//	private Image telephoneImage2 = null;
//	private Image keyImage2 = null;
	private void initImage() {
		ResourceLoader loader = new ResourceLoader();
		telephoneImage = loader.getImage("telephone2.gif");
		keyImage = loader.getImage("key2.gif");
//		telephoneImage2= loader.getImage("telephone3.gif");
//		keyImage2= loader.getImage("key3.gif");
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
	 * 各マスのx軸方向のサイズ
	 * @return int
	 */
	public int getXSpace() {
		return xSpace;
	}

	/**
	 * 各マスのy軸方向のサイズ
	 * @return int
	 */
	public int getYSpace() {
		return ySpace;
	}

	private boolean flagFlash = false;
	/**
	 * 画面を点滅させます。
	 */
	public void flash() {
		flagFlash = true;
	}

	////////////////////////////////////////////////////////////
	// private

	/**
	 * オフスクリーンへの描画
	 * @param Graphics graphics
	 */
	private void drawOffImage(Graphics graphics) {

		/* イメージのクリア */
		graphics.setColor(getBackground());

		/* 点滅させる場合 */
		if(flagFlash) {
			graphics.setColor(Color.red);
			flagFlash = false;
		}

		graphics.fillRect(0, 0, width, height);

		/* 罫線の描画 */
		graphics.setColor(Color.black);

		int xNum = map.length+1;    /* x軸方向のます数 */
		int yNum = map[0].length+1; /* y軸方向のます数 */

		/* ｘ軸方向の罫線 */
		for(int i = ySpace; i <= ySpace * yNum; i = i+ySpace) {
			graphics.drawLine(xSpace, i, xSpace * xNum, i);
		}
		/* ｙ軸方向の罫線 */
		for(int i = xSpace; i <= xSpace * xNum; i = i+xSpace) {
			graphics.drawLine(i, ySpace, i, ySpace * yNum);
		}

		/* 地図の描画 */
		for(int x = 0; x < map.length; x++) {
			for(int y = 0; y < map[0].length; y++) {

				/* 地図上の一マスのサイズに関する情報を取得 */
				int[] rectInfo = getMapRectInfo(x, y);

				/* 床の描画 */
				if(colorMap != null) {
					drawFloor(graphics, x, y, rectInfo);
				}

				String mapID = "";
				if(map[x][y].length() > 0) {
					mapID = map[x][y].substring(0, 1);
				}

				/* 報酬 */
				if( mapID.equals("O") ) {
					graphics.setColor(Color.green);
					graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
					        rectInfo[3]);
//					drawAttributeRect(graphics, rectInfo);
					graphics.setColor(Color.black);
					graphics.drawString("O", rectInfo[0],
					        rectInfo[1]+rectInfo[3]);
				}
				// 2001.03.22 追加 miyamoto
				// ドアとカギの表示を追加
				/* ドア クローズ */
				if( mapID.equals("D") ) {
					graphics.setColor(Color.darkGray);
					graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
					        rectInfo[3]);
//					drawAttributeRect(graphics, rectInfo);
					graphics.setColor(Color.black);
					graphics.drawString("D", rectInfo[0],
					        rectInfo[1]+rectInfo[3]);
				}
				/* ドア オープン*/
				if( mapID.equals("d") ) {
					graphics.setColor(Color.lightGray);
					graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
					        rectInfo[3]);
//					drawAttributeRect(graphics, rectInfo);
					graphics.setColor(Color.black);
					graphics.drawString("d", rectInfo[0],
					        rectInfo[1]+rectInfo[3]);
				}
				/* カギ */
				if( mapID.equals("K") ) {
					graphics.setColor(Color.yellow);
					graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
					        rectInfo[3]);
//					graphics.setColor(Color.black);
// 画像で表示
					if(keyImage != null) {
						graphics.drawImage(keyImage, rectInfo[0], 
						        rectInfo[1], this);
//						graphics.drawImage(keyImage, rectInfo[0]-2, 
//						        rectInfo[1]-2, this);
					}
//					graphics.drawString("K", rectInfo[0],
//					        rectInfo[1]+rectInfo[3]);
				}
				// 2001.08.08 追加 電話の位置
				if( mapID.equals("T") ) {
//System.out.println("rectInfo");
//System.out.println("x:" + rectInfo[0] + " y:" + rectInfo[1]);
//System.out.println("x size:" + rectInfo[2] + " y size:" + rectInfo[3]);
					graphics.setColor(Color.yellow);
					graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
					        rectInfo[3]);
//					graphics.setColor(Color.black);
// 画像で表示
					if(telephoneImage != null) {
						graphics.drawImage(telephoneImage, rectInfo[0],
						        rectInfo[1], this);
//						graphics.drawImage(telephoneImage, rectInfo[0]-2,
//						        rectInfo[1]-2, this);
					}
//					graphics.drawString("T", rectInfo[0],
//					        rectInfo[1]+rectInfo[3]);
				}

// アイテムを増やす
if( mapID.equals("A") ) {
	graphics.setColor(Color.yellow);
	graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
	        rectInfo[3]);
	graphics.setColor(Color.black);
	graphics.drawString("A", rectInfo[0],
	        rectInfo[1]+rectInfo[3]);
}
if( mapID.equals("B") ) {
	graphics.setColor(Color.yellow);
	graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
	        rectInfo[3]);
	graphics.setColor(Color.black);
	graphics.drawString("B", rectInfo[0],
	        rectInfo[1]+rectInfo[3]);
}
if( mapID.equals("a") ) {
	graphics.setColor(Color.yellow);
	graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
	        rectInfo[3]);
	graphics.setColor(Color.black);
	graphics.drawString("a", rectInfo[0],
	        rectInfo[1]+rectInfo[3]);
}
if( mapID.equals("b") ) {
	graphics.setColor(Color.yellow);
	graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
	        rectInfo[3]);
	graphics.setColor(Color.black);
	graphics.drawString("b", rectInfo[0],
	        rectInfo[1]+rectInfo[3]);
}

				/* スタート */
				if( mapID.equals("S") ) {
					graphics.setColor(Color.pink);
					graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
					        rectInfo[3]);
				}
				/* 2001.07.06 追加 ランダムに動作 */
				if( mapID.equals("R") ) {
					graphics.setColor(Color.magenta);
					graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
					        rectInfo[3]);
				}
				/* ジャンプ */
				if( mapID.equals("J") ) {
					graphics.setColor(Color.gray);
					graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
					        rectInfo[3]);
				}
				/* 流れ */
				if( mapID.equals("F") ) {
					graphics.setColor(Color.cyan);
					graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
					        rectInfo[3]);
					/* 方向の取得 */
					int startIndex = map[x][y].indexOf("(");
					int endIndex = map[x][y].indexOf(")");
					String dir = map[x][y].substring(startIndex+1, endIndex);
					/* 方向を描画 */
					drawDirection(graphics, rectInfo, dir);
				}
				/* 崖 */
				if( mapID.equals("C") ) {
					graphics.setColor(Color.orange);
					graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
					        rectInfo[3]);
					/* 方向の取得 */
					int startIndex = map[x][y].indexOf("(");
					int endIndex = map[x][y].indexOf(")");
					String dir = map[x][y].substring(startIndex+1, endIndex);
					/* 方向を描画 */
					drawDirection(graphics, rectInfo, dir);
				}
				/* 壁 */
				if( mapID.equals("W") ) {
					graphics.setColor(Color.black);
					graphics.fillRect(rectInfo[0], rectInfo[1], rectInfo[2],
					        rectInfo[3]);
				}

			}
		}

		/* ロボットの描画 */
		int[] rectInfo = getMapRectInfo(robotState[0], robotState[1]);
		graphics.setColor(Color.blue);
		graphics.fillOval(rectInfo[0], rectInfo[1], rectInfo[2], rectInfo[3]);
		/* ロボット位置にアイテムを表示 */
// 文字で表示
		graphics.setColor(Color.white);
		String item = null;
		if(robotState[2] == 3) {
			item = "A";
		}else if(robotState[2] == 4) {
			item = "B";
		}else {
			item = "";
		}
		graphics.drawString(item, rectInfo[0], rectInfo[1]+rectInfo[3]);

// 画像で表示
//		if(robotState[2] == 2) {
//			if(telephoneImage != null) {
//				graphics.drawImage(telephoneImage2, rectInfo[0],
//				        rectInfo[1], this);
//			}
//		}else if(robotState[2] == 1) {
//			if(telephoneImage != null) {
//				graphics.drawImage(keyImage2, rectInfo[0],
//				        rectInfo[1], this);
//			}
//		}
// ここまで

		/* 報酬のテーブルが無ければ(==null)なら表示をしない */
		if(rewardMap != null) {
			drawRewardValue(graphics);
		}
	}


	/**
	 * Flow・Cliffの矢印の描画
	 * @param Graphics graphics 
	 * @param int[] rectInfo 矩形の情報
	 * @param Stirng dir     方向
	 */
	private void drawDirection(Graphics graphics, int[] rectInfo, String dir) {
		graphics.setColor(Color.black);
		/* 上方向の矢印 */
		if( dir.equals("U") ) {
			graphics.drawLine(rectInfo[0]+rectInfo[2]/2, rectInfo[1],
			        rectInfo[0], rectInfo[1]+rectInfo[3]);
			graphics.drawLine(rectInfo[0]+rectInfo[2]/2, rectInfo[1],
			        rectInfo[0]+rectInfo[2], rectInfo[1]+rectInfo[3]);
		}
		/* 下方向の矢印 */
		if( dir.equals("D") ) {
			graphics.drawLine(rectInfo[0]+rectInfo[2]/2,
			        rectInfo[1]+rectInfo[3], rectInfo[0], rectInfo[1]);
			graphics.drawLine(rectInfo[0]+rectInfo[2]/2,
			        rectInfo[1]+rectInfo[3], rectInfo[0]+rectInfo[2],
			        rectInfo[1]);
		}
		/* 左方向の矢印 */
		if( dir.equals("L") ) {
			graphics.drawLine(rectInfo[0], rectInfo[1]+rectInfo[3]/2,
			        rectInfo[0]+rectInfo[2], rectInfo[1]);
			graphics.drawLine(rectInfo[0], rectInfo[1]+rectInfo[3]/2,
			        rectInfo[0]+rectInfo[2], rectInfo[1]+rectInfo[3]);
		}
		/* 右方向の矢印 */
		if( dir.equals("R") ) {
			graphics.drawLine(rectInfo[0]+rectInfo[2],
			        rectInfo[1]+rectInfo[3]/2, rectInfo[0], rectInfo[1]);
			graphics.drawLine(rectInfo[0]+rectInfo[2],
			        rectInfo[1]+rectInfo[3]/2, rectInfo[0],
			        rectInfo[1]+rectInfo[3]);
		}
	}


	/**
	 * 報酬の値を描画します。
	 */
	private void drawRewardValue(Graphics graphics) {
		/* x軸方向への繰り返し */
		for(int x = 0; x < map.length; x++) {
			/* y軸方向への繰り返し */
			for(int y = 0; y < map[0].length; y++) {
				int[] rectInfo = getMapRectInfo(x, y);
				/* 報酬を取得 */
				String reward = rewardMap[x][y];
				graphics.setColor(Color.black);
				graphics.drawString(reward, rectInfo[0],
				        rectInfo[1]+rectInfo[3]);
			}
		}
	}


	int[] xPoints = new int[4];
	int[] yPoints = new int[4];
	private void drawAttributeRect(Graphics graphics, int[] rectInfo) {
			xPoints[0] = rectInfo[0] + (rectInfo[2]/2);
			xPoints[1] = rectInfo[0];
			xPoints[2] = rectInfo[0] + (rectInfo[2]/2);
			xPoints[3] = rectInfo[0] + rectInfo[2];

			yPoints[0] = rectInfo[1];
			yPoints[1] = rectInfo[1] + (rectInfo[3]/2);
			yPoints[2] = rectInfo[1] + rectInfo[3];
			yPoints[3] = rectInfo[1] + (rectInfo[3]/2);

			graphics.setColor(Color.white);
			graphics.fillPolygon(xPoints, yPoints, 4);
			graphics.setColor(Color.black);
			graphics.drawLine(xPoints[0], yPoints[0], xPoints[1], yPoints[1]);
			graphics.drawLine(xPoints[1], yPoints[1], xPoints[2], yPoints[2]);
			graphics.drawLine(xPoints[2], yPoints[2], xPoints[3], yPoints[3]);
			graphics.drawLine(xPoints[3], yPoints[3], xPoints[0], yPoints[0]);
	}


	private void drawFloor(Graphics graphics, int x, int y, int[] rectInfo) {

		String color = "";
		if(colorMap[x][y].length() > 0) {
			color = colorMap[x][y].substring(0, 1);
		}

		// 2001.08.08 追加 miyamoto
		/* 床に色を設定 */
		if( color.equals("w") ) {
//			graphics.setColor(Color.black);
//			graphics.fillRect(rectInfo[0], rectInfo[1],
//			        rectInfo[2], rectInfo[3]);
		}
		if( color.equals("r") ) {
			graphics.setColor(redFloor);
			graphics.fillRect(rectInfo[0], rectInfo[1],
			        rectInfo[2], rectInfo[3]);
		}
		if( color.equals("g") ) {
			graphics.setColor(greenFloor);
			graphics.fillRect(rectInfo[0], rectInfo[1],
			        rectInfo[2], rectInfo[3]);
		}
		if( color.equals("b") ) {
			graphics.setColor(blueFloor);
			graphics.fillRect(rectInfo[0], rectInfo[1],
			        rectInfo[2], rectInfo[3]);
		}
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
		xSpace = d.width / (map.length+2);
		ySpace = d.height / (map[0].length+2);
//System.out.println("EnvironmentCanvas");
//System.out.println(" height:" + height);
//System.out.println(" width:" + width);

	}


	/**
	 * 指定された地図上のＸＹ座標に対応する矩形の情報を取得します。
	 * @param int x 地図上のＸ座標
	 * @param int y 地図上のＹ座標
	 * @return int[] int[4]の配列 順にキャンバス上の X座標・Y座標・幅・高さ
	 */
	private int[] getMapRectInfo(int x, int y) {
		int[] rectInfo = null;
		/* 範囲内かチェック */
		if( (x >= 0)&&(y >= 0) && (x < map.length)&&(y < map[0].length) ) {
			rectInfo = new int[4];
			rectInfo[0] = ((x+1)*xSpace) + 1;
			rectInfo[1] = ((y+1)*ySpace) + 1;
			rectInfo[2] = xSpace-1;
			rectInfo[3] = ySpace-1;
		}
		return rectInfo;
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


