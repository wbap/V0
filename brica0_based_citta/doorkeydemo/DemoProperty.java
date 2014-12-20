/**
 * DemoProperty.java
 * ドアキー課題のデモの設定を管理するクラス
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.09
 */
package doorkeydemo;

import java.io.*;
import java.util.*;

/**
 * ドアキー課題のデモの設定を管理するクラス
 */
public class DemoProperty {

	/* 環境の地図情報 */
	private String envFileName;
	/* ドアが開く条件 */
	private int doorOpenMode;

//	/* ノード数 */
//	private int nodeNum;

	/* 実験に関する設定 */
	private int saveStepNum;     /* 学習データをセーブするステップ数 */
	private String saveFileName; /* 学習データをセーブするファイル名 */
	private String loadFileName; /* 学習データをロードするファイル名 */

	private int timeOutStepNum;  /* 1トライアルのタイムアウトのステップ数 */
	private int sleepTime;       /* 1サイクルごとにスリープさせる時間 */

	/* GSAの設定ファイル名 */
	private String gsaPropFileName;

	////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * コンストラクタ
	 * @param String propFileName 
	 */
	public DemoProperty(String propFileName)
	        throws FileNotFoundException, IOException, Exception {
		loadProperty(propFileName);
	}

	////////////////////////////////////////////////////////////////
	// public

	/**
	 * GSAの設定ファイル名を取得します。
	 * @return String 設定ファイル名
	 */
	public String getGSAPropFileName() {
		return gsaPropFileName;
	}

	/**
	 * 学習データをセーブするステップ数を取得します。
	 * @return int ステップ数
	 */
	public int getSaveStepNum() {
		return saveStepNum;
	}

	/**
	 * 学習データをセーブするファイル名を取得します。
	 * @return String ファイル名
	 */
	public String getSaveFileName() {
		return saveFileName;
	}

	/**
	 * 学習データをロードするファイル名を取得します。
	 * @return String ファイル名
	 */
	public String getLoadFileName() {
		return loadFileName;
	}

	/**
	 * 1トライアルのタイムアウトのステップ数を取得します。
	 * @return int タイムアウトのステップ数
	 */
	public int getTimeOutStepNum() {
		return timeOutStepNum;
	}

	/**
	 * 実験の速度を調整するためにスリープさせる時間を取得します。
	 * @return int スリープタイム
	 */
	public int getSleepTime() {
		return sleepTime;
	}

	/**
	 * 環境の地図情報のファイル名を取得します。
	 * @return String ファイル名
	 */
	public String getEnvFileName()  {
		return envFileName;
	}

	/**
	 * ドアの開く条件を取得します。
	 * @return int ドアが開く条件
	 */
	public int getDoorOpenMode() {
		return doorOpenMode;
	}

	/**
	 * 環境で使用されるノード数を取得します。
	 * @return int ノード数
	 */
//	public int getNodeNum() {
//		return nodeNum;
//	}


	////////////////////////////////////////////////////////////////
	// private

	/**
	 * ファイルから情報の読み込み
	 */
	private void loadProperty(String fileName) throws FileNotFoundException, 
	        IOException, NullPointerException, NumberFormatException, 
	        NoSuchElementException, Exception {
		Properties prop = new Properties();

		/* ファイルの読み込み */
		try {
			FileInputStream fin = new FileInputStream(fileName);
			prop.load(fin);
			fin.close();
		} catch (FileNotFoundException fnfe) {
			throw fnfe;
		} catch (IOException ioe) {
			throw ioe;
		}

		StringTokenizer contents;
		try {
			/* 環境のファイルの読込み */
			contents = new StringTokenizer( prop.getProperty("Environment") );
			envFileName = contents.nextToken();

			contents = new StringTokenizer( prop.getProperty(
			        "GSAPropFileName") );
			gsaPropFileName = contents.nextToken();

			/* ドアが開く条件の取得 */
			contents = new StringTokenizer( prop.getProperty("DoorOpenMode") );
			doorOpenMode = new Integer(contents.nextToken()).intValue();

			/* ノード数の読込み */
//			contents = new StringTokenizer(prop.getProperty("NodeNum") );
//			nodeNum = new Integer(contents.nextToken()).intValue();

		} catch (NullPointerException e){
			NullPointerException ne = new NullPointerException
			    ("Format Error: on property file " + fileName);
			throw ne;
		} catch (NumberFormatException e) {
			NumberFormatException nfe = new NumberFormatException
			    ("Format Error: on property file " + fileName);
			throw nfe;
		}

		try {
			/* 実験設定の読み込み */
			contents = new StringTokenizer(prop.getProperty("SaveStepNum") );
			saveStepNum = new Integer(contents.nextToken()).intValue();

			saveFileName = prop.getProperty("SaveFileNeme", "");
			loadFileName = prop.getProperty("LoadFileName", "");

			contents = new StringTokenizer(prop.getProperty("TimeOutStepNum"));
			timeOutStepNum = new Integer(contents.nextToken()).intValue();

			contents = new StringTokenizer(prop.getProperty("SleepTime"));
			sleepTime = new Integer(contents.nextToken()).intValue();
		} catch (NumberFormatException e) {
			NumberFormatException nfe = new NumberFormatException
			    ("Format Error: on property file " + fileName);
			throw nfe;
		}

	}

}


