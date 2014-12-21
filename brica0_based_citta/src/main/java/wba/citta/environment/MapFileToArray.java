/**
 * MapFileToArray.java
 * CSV形式の地図ファイルを配列に変換するクラス
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * 2001.08 miyamoto
 */
package wba.citta.environment;

import java.io.*;
import java.util.*;
//import jp.ac.wakhok.tomoharu.csv.*;

/**
 * CSV形式の地図ファイルを配列に変換するクラス
 */
public class MapFileToArray {

	/* ファイルの内容をそのまま配列に設定 String[x][y] */
	private String[][] fileArray;

	/**
	 * コンストラクタ
	 * @param String fileName ファイル名での地図情報
	 */
	public MapFileToArray(String fileName) {
		/* テーブル情報の作成 */
		makeFileArray(fileName);
	}


	public String[][] getFileArray() {
		return fileArray;
	}

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


	private void printArray() {
		for(int y = 0; y < fileArray[0].length; y++) {
			StringBuffer stringBuffer = new StringBuffer();
			for(int x = 0; x < fileArray.length; x ++) {
				stringBuffer.append(fileArray[x][y] + " ");
			}
			System.out.println(stringBuffer.toString());
		}
	}

}


