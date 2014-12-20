/**
 * Environment.java
 *  環境の処理を行うクラス
 *  COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2000.09 BSC miyamoto
 */
package environment;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
//import java.util.*;

/**
 * 環境の処理を行います。
 */
public class Environment {

	/* メニューバー */
	private MenuBar menuBar;       /* メニューバー */
	private Menu fileMenu;         /* ファイルメニュー */
	private MenuItem loadMenuItem; /* ロードメニュー */

	/* グラフィック表示部 */
	private Frame frame;
	private EnvironmentPanel envPanel;

	/* ボタンパネル */
	private Panel buttonPanel;
	private CheckboxGroup  cbg;   /* チェックボックスのグループ化 */
	private Checkbox wallCB;      /* 壁 */
	private Checkbox rewardCB;    /* 報酬 */
	private Checkbox clearCB;     /* クリア */
	private Button clearButton;   /* オールクリアボタン */
	private Button repaintButton; /* 再描画ボタン */

	/* 文字列の設定 */
//	private MessageCanvas titleCanvas;
	private MessageCanvas itemCanvas;

	private String TITLE;

	////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * コンストラクタ
	 * @param String fileName Mapファイル名
	 */
	public Environment(String fileName) {

		envPanel = new EnvironmentPanel(fileName, false/*true*/, false);

		/* フレームの初期化 */
		initFrame();

	}

	public Environment(String fileName, String title) {
		TITLE = title;
		envPanel = new EnvironmentPanel(fileName, false/*true*/, false);
		/* フレームの初期化 */
		initFrame();
	}

//	public Environment(String mapFileName, String colorMapFileName) {
//		envPanel = new EnvironmentPanel(mapFileName, colorMapFileName,
//		       false, false);
//
//		/* フレームの初期化 */
//		initFrame();
//
//	}


	/**
	 * コンストラクタ
	 * @param String fileName Mapファイル名
	 */
	public Environment(String[][] fileArray) {

		envPanel = new EnvironmentPanel(fileArray, false, false);

		/* フレームの初期化 */
		initFrame();

	}


	////////////////////////////////////////////////////////////
	// 初期化処理

	public void initRobotPos() {
		envPanel.initRobotPos();
	}

	public void initMap() {
		/* カギを無くす */
//		envPanel.clearKey();
		envPanel.clearItem();
		/* ドアを閉じる */
		envPanel.closeDoor();
	}

	/**
	 * フレームの初期化
	 */
	private void initFrame() {

		/* メニューバー */
//		menuBar = new MenuBar();
//		fileMenu = new Menu("ファイル");
//		loadMenuItem = new MenuItem("読込み");
//		loadMenuItem.addActionListener(new ButtonActionListener());
//		fileMenu.add(loadMenuItem);
//		menuBar.add(fileMenu);

		/* ボタンパネル */
//		buttonPanel = new Panel();
//		cbg = new CheckboxGroup();
//		wallCB = new Checkbox("Wall ", cbg, false);
//		wallCB.addItemListener(new CBItemAdapter());
//		rewardCB = new Checkbox("reward ", cbg, false);
//		rewardCB.addItemListener(new CBItemAdapter());
//		clearCB = new Checkbox("Clear", cbg, false);
//		clearCB.addItemListener(new CBItemAdapter());

//		clearButton = new Button("All Clear");
//		clearButton.addActionListener(new ButtonActionListener());
//		repaintButton = new Button("Repaint");
//		repaintButton.addActionListener(new ButtonActionListener());

//		buttonPanel.setLayout(new GridLayout(7, 1));
//		buttonPanel.add(repaintButton);
//		buttonPanel.add(new Label(""));
//		buttonPanel.add(new Label(""));
//		buttonPanel.add(wallCB);
//		buttonPanel.add(rewardCB);
//		buttonPanel.add(clearCB);
//		buttonPanel.add(clearButton);
//		buttonPanel.setSize(100, 400);

		/*  */
//		if(TITLE != null) {
//			titleCanvas = new MessageCanvas(TITLE, 45, 45);
//			titleCanvas.setSize(700, 50);
//		}
		itemCanvas = new MessageCanvas(
		        "持ち物                            "
		        + "ゴール到達回数 0" , 45, 25);
		itemCanvas.setSize(524, 40);

		/* フレームの作成 */
		frame = new Frame("environment");
		frame.setLayout(new BorderLayout(5, 5));
//		frame.setMenuBar(menuBar);
//		if(TITLE != null) {
//			frame.add(titleCanvas, "North");
//		}
		frame.add(itemCanvas, "South");
		frame.add(envPanel, "Center");
//		frame.add(buttonPanel, "East");
//		frame.setSize(840, 865);
		frame.setSize(524, 368);
//		frame.setSize(1280, 1000);
		frame.setVisible(true);

		initImage();
	}

	private int goalReachCount = 0;

	/* アイテムの表示 */
	private void showItemMessage() {
		int itemID = getItem();
		String item;
		if(itemID == 2) {
			item = "持ち物                            ゴール到達回数 "
			         + goalReachCount;
		}else if(itemID == 1) {
			item = "持ち物                            ゴール到達回数 "
			         + goalReachCount;
		}else {
			item = "持ち物                            ゴール到達回数 "
			         + goalReachCount;
		}
		itemCanvas.setMessage(item);
		itemCanvas.repaint();
	}

	public void countGoalReachCount() {
		goalReachCount++;
	}

	private Image telephoneImage = null;
	private Image keyImage = null;
	private void initImage() {
		ResourceLoader loader = new ResourceLoader();
		telephoneImage = loader.getImage("telephone2.gif");
		keyImage = loader.getImage("key2.gif");
	}

	private void showItemImage() {
		int itemID = getItem();
		if(itemID == 2) {
			itemCanvas.setImage(telephoneImage, 120, 10);
		}else if(itemID == 1) {
			itemCanvas.setImage(keyImage, 120, 10);
		}else {
			itemCanvas.setImage(null, 120, 10);
		}
		itemCanvas.repaint();
	}

	////////////////////////////////////////////////////////////
	// public

	/**
	 * ロボットを指定された方向へ移動します。
	 * @param int action  移動方向 ０～７で設定
	 * @return boolean    true=移動成功 false=移動失敗
	 */
	public boolean run(int action) {
//		return envPanel.run(action);
		boolean b = envPanel.run(action);
		showItemMessage();
		showItemImage();
		return b;
	}
	public boolean run(int[] xy, int action) {
		return envPanel.run(xy, action);
	}

	/**
	 * 指定された座標にゴールを設定します。
	 * @param int x X座標
	 * @param int y Y座標
	 */
	public void setGoal(int x, int y) {
		envPanel.setGoal(x, y);
	}

	/**
	 * 指定された座標をスタート位置にします。
	 * @param int x X座標
	 * @param int y Y座標
	 */
	public void setStart(int x, int y) {
		envPanel.setStart(x, y);
	}

	// 2001.09.05 追加 miyamoto
	public void setItem(int newItem) {
		envPanel.setItem(newItem);
	}

	/**
	 * ロボットの位置をセンサ情報で取得します。
	 * @return int[8] 
	 */
	public int[] getSenserState() {
		return envPanel.getSenserState();
	}


	/**
	 * ロボットの位置をＸＹ座標で取得します。
	 * @return int[] 現在の座標
	 *               int[0] x座標
	 *               int[1] y座標
	 */
	public int[] getXYState() {
		return envPanel.getXYState();
	}

	/**
	 * ゴールの位置をセンサ情報で取得します。
	 * @return int[] ゴールの位置
	 */
	public int[] getSensorGoalState() {
		return envPanel.getSensorGoalState();
	}

	/**
	 * ゴールの位置をＸＹ座標で取得します。
	 * @return int[] ゴールの座標
	 *               int[0] x座標
	 *               int[1] y座標
	 */
	public int[] getXYGoalState() {
		return envPanel.getXYGoalState();
	}

	/**
	 * キーの位置をＸＹ座標で取得します。
	 * @return int[] キーの位置
	 *               int[0] x座標
	 *               int[1] y座標
	 */
	public int[] getXYKeyState() {
		return envPanel.getXYKeyState();
	}

	public int[] getXYStartState() {
		return envPanel.getXYStartState();
	}

	/**
	 * 報酬を取得します。
	 * @return double 報酬
	 */
	public double getReward() {
		return envPanel.getReward();
	}

	/**
	 * カギを取得しているか確認します。
	 * @param boolean  true:カギを持っている false:カギを持っていない
	 */
//	public boolean hasKey() {
//		return envPanel.hasKey();
//	}
	// 2001.08.08 追加 miyamoto
	/**
	 * 保持しているものを取得します。
	 * @return 
	 */
	public int getItem() {
		return envPanel.getItem();
	}

	/**
	 * ドアが開いているか確認します。
	 * @param boolean  true:ドアが開いている false:ドアが閉じている
	 */
	public boolean isDoorOpen() {
		return envPanel.isDoorOpen();
	}

	/**
	 * 地図上の指定された位置の情報を取得します。
	 * @param int x  x座標
	 * @param int y  y座標
	 */
	public String getMapInfo(int x, int y) {
		return envPanel.getMapInfo(x, y);
	}

	/**
	 * 地図上の指定された位置の属性を取得します。
	 * @param int x  x座標
	 * @param int y  y座標
	 * @return String 位置の属性 
	 */
	public String getAttribute(int x, int y) {
		String str = envPanel.getMapInfo(x, y);
		String attr = null;
		if(str.equals("K")) {
			attr = "K";
		}else if(str.equals("d")) {
			attr = "d";
		}else if(str.equals("O(1)")) {
			attr = "O";
	// 2001.08.08 追加 miyamoto
		}else if(str.equals("T")) {
			attr = "T";
		}else {
// 2001.07.13 修正 空白はＮを出力
			attr = "N";
//			attr = "";
		}
		return attr;
	}

	public String getFloorColor(int x, int y) {
		String str = envPanel.getColorInfo(x, y);
		String color = null;
		if(str == null) {
		}else if(str.equals("r")) {
			color = "r";
		}else if(str.equals("g")) {
			color = "g";
		}else if(str.equals("b")) {
			color = "b";
		}else {
			color = "w";
		}
		return color;
	}


	/**
	 * 地図のサイズを取得します。
	 * @return int[] int[0] x軸方向のサイズ
	 *               int[0] y軸方向のサイズ
	 */
	public int[] getMapSize() {
		return envPanel.getMapSize();
	}

	public boolean isCollisionDoor() {
		return envPanel.isCollisionDoor();
	}

	/*
	 * 環境の描画を行なうかどうかのフラグを設定します。
	 * @param boolean b   true：描画を行なう  false：描画を行なわない
	 */
	public void setFlagShow(boolean b) {
		envPanel.setFlagShow(b);
	}

	public void changeDoorOpenItem(int doorOpenItem) {
		envPanel.changeDoorOpenItem(doorOpenItem);
	}

	public void flash() {
		try {
			for(int i = 0; i < 3; i++) {
				envPanel.flash();
				envPanel.repaint();
				Thread.sleep(100);
				envPanel.repaint();
				Thread.sleep(100);
			}
		}catch( Exception e ) {
			System.out.println(e.toString());
		}
	}

	///////////////////////////////////////////////////////////
	// イベント処理のインナークラス

	/**
	 * チェックボックスのイベント処理を行うインナークラス
	 */
	class CBItemAdapter implements ItemListener {

		/**
		 * アイテムが選択された時の処理
		 */
		public void itemStateChanged(ItemEvent e) {
			if(e.getSource() == wallCB) {        /* 壁 */
				envPanel.setRenewValue("W");
			}else if(e.getSource() == rewardCB) {  /* 報酬 */
				envPanel.setRenewValue("O(1)");
			}else if(e.getSource() == clearCB) { /* クリア */
				envPanel.setRenewValue("");
			}
		}
	}


	/**
	 * ボタンのイベント処理を行なうインナークラス
	 */
	class ButtonActionListener implements ActionListener {

		/**
		 * ボタンがクリックされた時の処理
		 */
		public void actionPerformed(ActionEvent e) {
//			/* 再描画 */
//			if(e.getSource() == repaintButton) {
//				canvas.repaint();
//			/* クリア */
//			}else if(e.getSource() == clearButton) {
//				for(int x = 0; x < map.length; x ++) {
//					for(int y = 0; y < map[0].length; y++) {
//						map[x][y] = "";
//					}
//				}
//				canvas.repaint();
//			/* メニュー ファイル読込み */
//			}else if(e.getSource() == loadMenuItem) {
//				/* ファイル名の取得 */
//				FileDialog fDialog = new FileDialog(frame, "ファイルの読込み");
//				fDialog.setVisible(true);
//				String dirName = fDialog.getDirectory();
//				String fileName = fDialog.getFile();
//
//				/* ファイルの情報を取得 */
//				initMapInfo(dirName + fileName);
//
//				/* キャンバスの初期化 */
//				canvas.initCanvas(map, robotState);
//				canvas.repaint();
//			}
		}

	}

	//////////////////////////////////////////
	// テスト用

	// テスト用のメインメソッド
	/**
	 * @param String[] args args[0]:環境のファイル名 
	 *                      args[1]:環境のＩＤ
	 *                      args[2]:ドアが開く条件(そのＩＤ)
	 *                      args[3]:ログの出力先
	 *                      args[4]:表示の有無
	 *                      args[5]:繰り返し回数の上限
	 */ 
	public static void main(String args[]) {
		try {
			Environment env = new Environment(args[0]);
			env.changeDoorOpenItem((new Integer(args[2])).intValue());
			java.util.Random random = new java.util.Random(0);

			/* ログの出力先 */
			String fileName = args[3];
			FileOutputStream fileOutputStream = new FileOutputStream(fileName);
			PrintStream printStream = new PrintStream(fileOutputStream);
//			PrintStream printStream = System.out;

			/* 表示の有無の切り換え */
			Boolean bl = new Boolean(args[4]);
			env.setFlagShow(bl.booleanValue());

			int maxIter = -1; // BABA 上限回数を設定
			if(args.length > 4) maxIter = Integer.parseInt(args[5]); // BABA 上限回数を設定
			int iter = 0; // BABA 上限回数を設定
			while((maxIter == -1) || (iter < maxIter)) { // BABA 上限回数を設定
				int action = random.nextInt(4);
				env.run(action*2);
				int[] state = env.getXYState();
				String str = state[0] + "," + state[1] + "," + (action*2) + ","
				        + env.getAttribute(state[0], state[1]) + ","
				        + env.getItem() + "," + args[1] + "," + args[2];
				printStream.println(str);
				iter++; // BABA 上限回数を設定
			}
			// 2001.08.10 追加 miyamoto
			printStream.close();
			fileOutputStream.close();
		}catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		// 2001.08.10 追加 miyamoto
		System.exit(0);
	}

}



