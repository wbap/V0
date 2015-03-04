/**
 * GSAProp.java
 * エージェントの設定情報を管理するクラス
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.07
 */
package wba.citta.gsa;

import java.io.*;
import java.util.*;

/**
 * エージェントの設定を管理するクラス
 */
public class GSAProperty {

	/* ノード数 */
	private int nodeNum;

	/* エージェントに関する設定 */
	/**
	 * CognitiveDistanceのエージェントを表すID
	 */
	public static final int CD = 0;
	/**
	 * Associateエージェントを表すID
	 */
	public static final int ASSOCIATE = 1;
	/**
	 * Logエージェントを表すID
	 */
	public static final int LOG = 2;

	private int agentNum;
	private AgentInfo[] agentsInfo;

	private boolean useMana;

	/* viewerに関する設定 */
	private boolean agentViewer = false;
	private boolean goalStackViewer = false;
	private boolean failAgentTreeViewer = false;

	////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * コンストラクタ
	 * @param String propFileName 設定ファイル名
	 * @exception FileNotFoundException
	 * @exception IOException
	 * @exception Exception
	 */
	public GSAProperty(String propFileName)
	        throws FileNotFoundException, IOException, Exception {
		loadProperty(propFileName);
	}

	////////////////////////////////////////////////////////////////
	// public

	/**
	 * ManualAgentを使用するかどうかのフラグを取得します。
	 * @return boolean  true：使用する false：使用しない
	 */
	public boolean getUseMana() {
		return useMana;
	}

	/**
	 * 環境で使用されるノード数を取得します。
	 * @return int ノード数
	 */
	public int getNodeNum() {
		return nodeNum;
	}

	/**
	 * GSAで使用するエージェントの数を取得します。
	 * @return int エージェント数
	 */
	public int getAgentNum() {
		return agentNum;
	}

	/**
	 * 引数で指定されたindexのエージェントの種類を取得します。
	 * @param int index
	 * @return int エージェントの種類 0：ＣＤエージェント 1：連想エージェント
	 */
	public int getAgentType(int index) {
		return agentsInfo[index].agentType;
	}

	/**
	 * 引数で指定されたindexのエージェントIDを取得します。
	 * @param int index
	 * @return int エージェントID
	 */
	public int getAgentID(int index) {
		return agentsInfo[index].agentID;
	}

	/**
	 * エージェントが使用するノードの情報を取得します。
	 * @param int index
	 * @return boolean[] 使用するノードがtrueに設定されたbooleanの配列
	 */
	public boolean[] getUseNode(int index) {
		return agentsInfo[index].useNode;
	}

	// 2001.12.14 追加 miyamoto
	/**
	 * エージェントが使用するイベント情報のファイル名を取得します。
	 * @param int index
	 * @return String イベント情報のファイル名
	 */
	public String getEventFileName(int index) {
		return agentsInfo[index].eventFileName;
	}
	// ここまで

	/**
	 * エージェントの動作状況を表示するViewerを表示するかどうか。
	 * @return boolean true 表示する  false 表示しない
	 */
	public boolean isShowAgentViewer() {
		 return agentViewer;
	}

	/**
	 * ゴールスタックの状況を表示するViewerを表示するかどうか。
	 * @return boolean true 表示する  false 表示しない
	 */
	public boolean isShowGoalStackViewer() {
		return goalStackViewer;
	}

	/**
	 * 失敗エージェントのツリーの状況を表示するViewerを表示するかどうか。
	 * @return boolean true 表示する  false 表示しない
	 */
	public boolean isShowFailAgentTreeViewer() {
		return failAgentTreeViewer;
	}

	////////////////////////////////////////////////////////////////
	// private

	/**
	 * ファイルから情報の読み込み
	 * @param String fileName ファイル名
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
			/* manaの使用 */
			contents = new StringTokenizer(prop.getProperty("UseMana") );
			useMana = new Boolean(contents.nextToken()).booleanValue();

			/* ノード数の読込み */
			contents = new StringTokenizer(prop.getProperty("NodeNum") );
			nodeNum = new Integer(contents.nextToken()).intValue();
			/* エージェント数の読込み */
			contents = new StringTokenizer(prop.getProperty("AgentNum") );
			agentNum = new Integer(contents.nextToken()).intValue();

			/* viewerの表示に関する設定の読み込み */
			contents = new StringTokenizer(prop.getProperty("AgentViewer") );
			agentViewer = new Boolean(contents.nextToken()).booleanValue();
			contents = new StringTokenizer(prop.getProperty(
			        "GoalStackViewer") );
			goalStackViewer = new Boolean(contents.nextToken()).booleanValue();
			contents = new StringTokenizer(prop.getProperty(
			        "FailAgentTreeViewer") );
			failAgentTreeViewer = new Boolean(
			        contents.nextToken()).booleanValue();

		} catch (NullPointerException e){
			NullPointerException ne = new NullPointerException
			    ("Format Error: on property file " + fileName);
			throw ne;
		} catch (NumberFormatException e) {
			NumberFormatException nfe = new NumberFormatException
			    ("Format Error: on property file " + fileName);
			throw nfe;
		}

		agentsInfo = new AgentInfo[agentNum];
		for (int i = 0;i < agentNum;i++) {
			agentsInfo[i] = new AgentInfo();
		}

		/* ノード毎の設定の読込み */
		int index = 0;
		for ( Enumeration e = prop.propertyNames(); e.hasMoreElements();) {
			try{
				/* エージェントID取得 */
				String key = (String)e.nextElement();
				int agid = new Integer(key.trim()).intValue();

				/* エージェントの種類取得 */
				StringTokenizer stringTokenizer
				        = new StringTokenizer(prop.getProperty(key));
				int agentType = -1;
				try{
					String agType = stringTokenizer.nextToken();
					if(agType.equals("CD")) {
						agentType = CD;
					}else if(agType.equals("ASSOCIATE")) {
						agentType = ASSOCIATE;
					}else if(agType.equals("LOG")) {
						agentType = LOG;
					}else {
						/* CD、ASSOCIATE以外の指定は例外をスロー */
						Exception ex = new Exception
						    ("Agent Type Error: on property file " + fileName);
						throw ex;
					}
				}catch(NoSuchElementException nsee) {
					NoSuchElementException ex = new NoSuchElementException
					        ("Use Node Property Error: on property file "
					         + fileName);
					throw ex;
				}

				/* 使用ノードの取得 */
				boolean[] useNode = null;
				try {
					String str = stringTokenizer.nextToken();
					useNode = loadDecomposeFeature(str);
				}catch(NoSuchElementException nsee) {
					NoSuchElementException ex = new NoSuchElementException
					        ("Use Node Property Error: on property file "
					         + fileName);
					throw ex;
				}

				/* イベントデータファイル名の取得 */
				String eventFileName = null;
				try {
					eventFileName = stringTokenizer.nextToken();
				}catch(NoSuchElementException nsee) {
					/* なくても可 */
					eventFileName = null;
				}

				agentsInfo[index].agentID = agid;
				agentsInfo[index].agentType = agentType;
				agentsInfo[index].useNode = useNode;
				agentsInfo[index].eventFileName = eventFileName;
				index++;
			} catch (NumberFormatException nfe) {
				// ノードの設定以外は処理をとばす
			}
		}

		/* Agent数のチェック */
		if( index != agentNum ) {
			Exception ex = new Exception
			        ("Agent Number Error: on property file " + fileName);
			throw ex;
		}

	}


	/////////////////////////////////////////////////////////
	// 状況分解データ読み込み

	/**
	 * 
	 * @param String fileName
	 * @return boolean[] 
	 */
	public boolean[] loadDecomposeFeature(String fileName) throws
	        FileNotFoundException, IOException {
		boolean[] useNode = null;
		/* ファイルの読み込み */
		try {
			FileReader fr = new FileReader(fileName);
			BufferedReader br = new BufferedReader(fr);

			while(true) {
				if(br.ready() == false) {
					break;
				}
				String str = br.readLine();
				useNode = parseString(str);
			}

			br.close();
			fr.close();
		} catch (FileNotFoundException fnfe) {
			throw fnfe;
		} catch (IOException ioe) {
			throw ioe;
		}
		return useNode;
	}

	/**
	 * ファイルから読み込んだ情報をbooleanの配列に変換します。
	 * @param String str
	 * @return boolean[]
	 */
	private boolean[] parseString(String str) {
		StringTokenizer stringTokenizer
		        = new StringTokenizer(str, ",");
		boolean[] useNode = new boolean[nodeNum];

		for(int i = 0; i < nodeNum; i++) {
			useNode[i] = false;
		}

		while(stringTokenizer.hasMoreTokens()) {
			String elm = stringTokenizer.nextToken();
			useNode[(new Integer(elm)).intValue()] = true;
		}
		return useNode;
	}


	////////////////////////////////////////////////////////////////
	// inner class

	private class AgentInfo {
		int agentID;
		int agentType;
		boolean[] useNode;
		String eventFileName;
	}



}


