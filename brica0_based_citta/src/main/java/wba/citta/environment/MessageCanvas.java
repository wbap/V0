/**
 * MessageCanvas.java
 *  COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2000.09 BSC miyamoto
 */
package wba.citta.environment;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 *  環境グラフィック処理を行うクラスです
 */
public class MessageCanvas extends Canvas {

	/* キャンバスのサイズ */
	private int height;
	private int width;

	/* サイズ変更のフラグ */
	private boolean resized;

	/* ダブルバッファリング用 オフスクリーンイメージ */
	private Image offImage;
	private Graphics offGraphics;

	private String message = new String("");
	private int messageXPos = 0;
	private int messageYPos = 0;

	private Image image = null;
	private int imageXPos = 0;
	private int imageYPos = 0;

	////////////////////////////////////////////////////////////
	// コンストラクタ  初期化処理

	/**
	 * コンストラクタ
	 */
	public MessageCanvas(String message, int x, int y) {
		super();
		/* サイズ変更についての処理を行なうイベントリスナの登録 */
		addComponentListener(new CanvasComponentAdapter());
		/* 変数の初期化 */
		resized = false;
		/* 表示位置 */
		messageXPos = x;
		messageYPos = y;
		this.message = message;
	}

	////////////////////////////////////////////////////////////
	// public 

	public void setMessage(String message) {
		this.message = message;
	}

	public void setImage(Image image, int x, int y) {
		this.image = image;
		imageXPos = x;
		imageYPos = y;
	}

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


	////////////////////////////////////////////////////////////
	// private

	/**
	 * オフスクリーンへの描画
	 * @param Graphics graphics
	 */
	private void drawOffImage(Graphics graphics) {

		/* イメージのクリア */
//		graphics.setColor(getBackground());
		graphics.setColor(Color.orange);
		graphics.fillRect(0, 0, width, height);

		Font f = new Font("Dialog", Font.BOLD, 20);
		graphics.setFont(f);
		graphics.setColor(Color.black);
		graphics.drawString(message, messageXPos, messageYPos);
		if(image != null) {
			graphics.drawImage(image, imageXPos, imageYPos, this);
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


