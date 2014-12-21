/**
 * MapController.java
 * 地図ファイルの管理を行なうクラス
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * 2001.05 BSC miyamoto
 */
package wba.citta.environment;

import java.io.*;
import java.util.*;
//import jp.ac.wakhok.tomoharu.csv.*;

/**
 * 地図ファイルの管理を行なうクラスです
 */
public class MapController {

	/* ファイルの内容をそのまま配列に設定 String[x][y] */
	private String[][] fileArray;

	/**
	 * コンストラクタ
	 * @param String fileName ファイル名での地図情報
	 */
	public MapController(String fileName) {
		/* テーブル情報の作成 */
		makeFileArray(fileName);
	}


	/**
	 * コンストラクタ
	 * @param String[][] map Stringの配列での地図情報
	 */
	public MapController(String[][] fileArray) {
		this.fileArray = new String[fileArray.length][fileArray[0].length];
		this.fileArray = fileArray;
	}


	//////////////////////////////////////////////////////
	// public 

	/**
	 * String[][] の地図情報を取得します。
	 * @return String[][] 地図情報
	 */
	public String[][] getMap() {
		return fileArray;
	}

	/**
	 * 指定された位置に指定された情報を設定します。
	 * @param int x X座標
	 * @param int y Y座標
	 * @param String str 設定する地図情報
	 */
	public void set(int x, int y, String str) {
		fileArray[x][y] = str;
	}

	/**
	 * 指定された位置の情報を取得します。
	 * @param int x X座標
	 * @param int y Y座標
	 * @return String 地図情報
	 */
	public String getString(int x, int y) {
		return fileArray[x][y];
	}

	/**
	 * 指定された情報の設定されている位置を取得します。
	 * @param String 地図情報
	 * @return int[]  int[0] X座標   int[1] Y座標
	 */
	public int[] getPos(String str) {
		return searchMap(str);
	}


	/**
	 * 地図のサイズを取得します。
	 * @return int[] int[0]:x方向のサイズ int[1]:y方向のサイズ
	 */
	public int[] getSize() {
		int[] size = new int[2];
		size[0] = fileArray.length;
		size[1] = fileArray[0].length;
		return size;
	}

	/**
	 * 指定された位置の報酬を取得します。
	 * @param int x ｘ座標
	 * @param int y ｙ座標
	 * @return String 報酬
	 */
	public String getReward(int x, int y) {

		String fileArrayID = "";
		if(fileArray[x][y].length() > 0) {
			fileArrayID = fileArray[x][y].substring(0, 1);
		}

		String reward = "";

		if(fileArrayID.equals("W")) {              /* 壁 */
			/* 処理しない */
		}else if(fileArrayID.equals("O")) {        /* 報酬 */
			/* 文字列から報酬に関する情報を取得 */
			int startIndex = fileArray[x][y].indexOf("(");
			int endIndex = fileArray[x][y].indexOf(")");
			reward = fileArray[x][y].substring(startIndex+1,endIndex);
		}else if(fileArrayID.equals("J")) {        /* Jamp */
			/* 文字列から報酬に関する情報を取得 */
			int startIndex = fileArray[x][y].indexOf("(");
			int endIndex = fileArray[x][y].indexOf(")");
			String str = fileArray[x][y].substring(startIndex+1, endIndex);

			StringTokenizer st = new StringTokenizer(str, ":");
			if(st.countTokens() == 4) {
				for(int i = 0; i < 3; i++) {
					st.nextToken();
				}
				reward = st.nextToken();
			}
		}else {                              /* 壁・報酬・JAMP以外 */
			/* 壁に隣接する位置なら報酬を設定 */
			for(int n = -1; n < 2; n++) {
				for(int m = -1; m < 2; m++) {
					/* 範囲内かチェック */
					if( (x+n >= 0)&&(y+m >= 0) && (x+n < fileArray.length)&&
					        (y+m < fileArray[0].length) ) {
						if(fileArray[x+n][y+m].equals("W")) {
							reward = "-1";
						}
					}
				}
			}
		}
		return reward;
	}



	/**
	 * 指定した位置のセンサ情報を取得します。
	 * @param   int x  x座標
	 * @param   int y  y座標
	 * @return  int[]  int[8]のセンサ情報
	 */
	public int[] getState(int x, int y){

		/* センサ情報を初期化 */
		int[] state = new int[8];
		for(int i=0; i<8; i++){
			if(i<4){           /* ShortRenge */
				state[i] = 5;  /* 振り切れ状態 */
			}else{             /* LongRenge */
				state[i] = 0;  /* 振り切れ状態 */
			}
		}
		/* 現時点が壁 */
		if(fileArray[x][y].equals("W")){
			for(int i=0; i<8; i++){
				state[i] = -1;
			}
			return state;
		}

		/* 左方向 */
		for(int i=1; i<20; i++){
			if(fileArray[x-i][y].equals("W")){
				if(i >= 11){
					state[5] = 5;
					break;
				}

				if(i < 6){
					state[1] = i-1;
					break;
				}

				if(i == 6){
					break;
				}

				if(i > 6){
					state[5] = i-6;
					break;
				}
			}
		}

		/* 右方向 */
		for(int i=1; i<20; i++){
			if(fileArray[x+i][y].equals("W")){
				if(i >= 11){
					state[7] = 5;
					break;
				}

				if(i < 6){
					state[3] = i-1;
					break;
				}

				if(i == 6){
					break;
				}

				if(i > 6){
					state[7] = i-6;
					break;
				}
			}
		}

		/* 上方向 */
		for(int i=1; i < 20; i++){
			if(fileArray[x][y-i].equals("W")){
				if(i >= 11){
					state[4] = 5;
					break;
				}

				if(i < 6){
					state[0] = i-1;
					break;
				}

				if(i == 6){
					break;
				}

				if(i > 6){
					state[4] = i-6;
					break;
				}
			}
		}

		/* 下方向 */
		for(int i=1; i < 20; i++){
			if(fileArray[x][y+i].equals("W")){
				if(i >= 11){
					state[6] = 5;
					break;
				}

				if(i < 6){
					state[2] = i-1;
					break;
				}

				if(i == 6){
					break;
				}

				if(i > 6){
					state[6] = i-6;
					break;
				}
			}
		}
		return state;
	}


	//////////////////////////////////////////////////////
	// private

	/**
	 * 地図ファイルの情報を文字列の配列に設定
	 * @param String fileName ファイル名
	 */
	private void makeFileArray(String fileName) {

		/* ファイルの情報を読み込み 一行ずつの文字列情報をリストで取得 */
		LinkedList mapStringList = loadMap(fileName);

		/* 配列のサイズ設定 */
		String s = (String)mapStringList.get(0);
// 2001.12.20 miyamoto
// CSVTokenizerの使用をやめ、StringTokenizerで対応
//		CSVTokenizer csvTokenizer = new CSVTokenizer(s);
//		int tokenNum = csvTokenizer.countTokens();
		StringTokenizer stringTokenizer = new StringTokenizer(s, ",");
		int tokenNum = stringTokenizer.countTokens();
// ここまで

		fileArray = new String[tokenNum][mapStringList.size()];

		/* 1行分のデータを解析 配列に設定 */
		int lineCnt = 0;
		ListIterator literator = mapStringList.listIterator();
		while(literator.hasNext()) {
			String str = (String)literator.next();
			parseString(str, lineCnt);
			lineCnt ++;
		}
	}


	/**
	 * 地図情報をファイルから読み込みます。
	 * @ param String fileName 地図情報のファイル名
	 * @ return LinkedList 地図情報を読み込んだStringのリスト
	 */
	private LinkedList loadMap(String fileName) {

		LinkedList lList = new LinkedList();
		try {
			/* ファイルの読込み */
			FileReader fReader = new FileReader(fileName);
			BufferedReader bReader = new BufferedReader(fReader);

			try {
				while(true) {
					if(bReader.ready() == false) {
						break;
					}
					/* ファイルの内容を1行ずつ取得し文字列のリストとして設定 */
					String mapData = bReader.readLine();
					lList.add(mapData);
				}
			}catch(Exception e) {
				System.out.println(e);
			}finally {
				bReader.close();
				fReader.close();
			}
		}catch(Exception e){
			System.out.println(e);
		}
		return lList;
	}


	/**
	 * カンマで区切られた文字情報をStringの配列に変換します。
	 * @param String mapData 文字列の地図情報
	 * @param int lineCnt 行数(y座標)のカウント
	 */
	private void parseString(String mapData, int lineCnt) {
// 2001.12.20 miyamoto
// CSVTokenizerの使用をやめ、StringTokenizerで対応
//		CSVTokenizer csvTokenizer = new CSVTokenizer(mapData);
//		int tokenCnt = 0;
//		while(csvTokenizer.hasMoreElements()) {
//			String str = csvTokenizer.nextToken();
//
//			/* ファイルの情報を配列として保持 */
//			fileArray[tokenCnt][lineCnt] = str;
//
//			tokenCnt ++;
//		}
		StringTokenizer stringTokenizer = new StringTokenizer(mapData, ",");
		int tokenCnt = 0;
		while(stringTokenizer.hasMoreElements()) {
			String str = stringTokenizer.nextToken();

			/* ファイルの情報を配列として保持 */
			if(str.equals("x")) {
				str = "";
			}
			fileArray[tokenCnt][lineCnt] = str;

			tokenCnt ++;
		}
// ここまで
	}


	/**
	 * 地図上から指定された文字列のある座標を取得します。
	 * @param String targerID 探す文字列
	 * @param int[]           座標
	 */
	private int[] searchMap(String targetID) {
		int[] pos = null;
		for(int x = 0; x < fileArray.length; x++) {
			for(int y = 0; y < fileArray[0].length; y++) {
				if(fileArray[x][y].length() > 0) {
					String id = fileArray[x][y].substring(0, 1);
					if(id.equals(targetID)) {
						pos = new int[2];
						pos[0] = x;
						pos[1] = y;
						return pos;
					}
				}
			}
		}
		return pos;
	}


}


