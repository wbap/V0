/**
 * GSAProp.java
 * �G�[�W�F���g�̐ݒ�����Ǘ�����N���X
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.07
 */
package wba.citta.gsa;

import java.io.*;
import java.util.*;

/**
 * �G�[�W�F���g�̐ݒ���Ǘ�����N���X
 */
public class GSAProperty {

	/* �m�[�h�� */
	private int nodeNum;

	/* �G�[�W�F���g�Ɋւ���ݒ� */
	/**
	 * CognitiveDistance�̃G�[�W�F���g��\��ID
	 */
	public static final int CD = 0;
	/**
	 * Associate�G�[�W�F���g��\��ID
	 */
	public static final int ASSOCIATE = 1;
	/**
	 * Log�G�[�W�F���g��\��ID
	 */
	public static final int LOG = 2;

	private int agentNum;
	private AgentInfo[] agentsInfo;

	private boolean useMana;

	/* viewer�Ɋւ���ݒ� */
	private boolean agentViewer = false;
	private boolean goalStackViewer = false;
	private boolean failAgentTreeViewer = false;

	////////////////////////////////////////////////////////////////
	// �R���X�g���N�^

	/**
	 * �R���X�g���N�^
	 * @param String propFileName �ݒ�t�@�C����
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
	 * ManualAgent���g�p���邩�ǂ����̃t���O���擾���܂��B
	 * @return boolean  true�F�g�p���� false�F�g�p���Ȃ�
	 */
	public boolean getUseMana() {
		return useMana;
	}

	/**
	 * ���Ŏg�p�����m�[�h�����擾���܂��B
	 * @return int �m�[�h��
	 */
	public int getNodeNum() {
		return nodeNum;
	}

	/**
	 * GSA�Ŏg�p����G�[�W�F���g�̐����擾���܂��B
	 * @return int �G�[�W�F���g��
	 */
	public int getAgentNum() {
		return agentNum;
	}

	/**
	 * �����Ŏw�肳�ꂽindex�̃G�[�W�F���g�̎�ނ��擾���܂��B
	 * @param int index
	 * @return int �G�[�W�F���g�̎�� 0�F�b�c�G�[�W�F���g 1�F�A�z�G�[�W�F���g
	 */
	public int getAgentType(int index) {
		return agentsInfo[index].agentType;
	}

	/**
	 * �����Ŏw�肳�ꂽindex�̃G�[�W�F���gID���擾���܂��B
	 * @param int index
	 * @return int �G�[�W�F���gID
	 */
	public int getAgentID(int index) {
		return agentsInfo[index].agentID;
	}

	/**
	 * �G�[�W�F���g���g�p����m�[�h�̏����擾���܂��B
	 * @param int index
	 * @return boolean[] �g�p����m�[�h��true�ɐݒ肳�ꂽboolean�̔z��
	 */
	public boolean[] getUseNode(int index) {
		return agentsInfo[index].useNode;
	}

	// 2001.12.14 �ǉ� miyamoto
	/**
	 * �G�[�W�F���g���g�p����C�x���g���̃t�@�C�������擾���܂��B
	 * @param int index
	 * @return String �C�x���g���̃t�@�C����
	 */
	public String getEventFileName(int index) {
		return agentsInfo[index].eventFileName;
	}
	// �����܂�

	/**
	 * �G�[�W�F���g�̓���󋵂�\������Viewer��\�����邩�ǂ����B
	 * @return boolean true �\������  false �\�����Ȃ�
	 */
	public boolean isShowAgentViewer() {
		 return agentViewer;
	}

	/**
	 * �S�[���X�^�b�N�̏󋵂�\������Viewer��\�����邩�ǂ����B
	 * @return boolean true �\������  false �\�����Ȃ�
	 */
	public boolean isShowGoalStackViewer() {
		return goalStackViewer;
	}

	/**
	 * ���s�G�[�W�F���g�̃c���[�̏󋵂�\������Viewer��\�����邩�ǂ����B
	 * @return boolean true �\������  false �\�����Ȃ�
	 */
	public boolean isShowFailAgentTreeViewer() {
		return failAgentTreeViewer;
	}

	////////////////////////////////////////////////////////////////
	// private

	/**
	 * �t�@�C��������̓ǂݍ���
	 * @param String fileName �t�@�C����
	 */
	private void loadProperty(String fileName) throws FileNotFoundException, 
	        IOException, NullPointerException, NumberFormatException, 
	        NoSuchElementException, Exception {
		Properties prop = new Properties();

		/* �t�@�C���̓ǂݍ��� */
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
			/* mana�̎g�p */
			contents = new StringTokenizer(prop.getProperty("UseMana") );
			useMana = new Boolean(contents.nextToken()).booleanValue();

			/* �m�[�h���̓Ǎ��� */
			contents = new StringTokenizer(prop.getProperty("NodeNum") );
			nodeNum = new Integer(contents.nextToken()).intValue();
			/* �G�[�W�F���g���̓Ǎ��� */
			contents = new StringTokenizer(prop.getProperty("AgentNum") );
			agentNum = new Integer(contents.nextToken()).intValue();

			/* viewer�̕\���Ɋւ���ݒ�̓ǂݍ��� */
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

		/* �m�[�h���̐ݒ�̓Ǎ��� */
		int index = 0;
		for ( Enumeration e = prop.propertyNames(); e.hasMoreElements();) {
			try{
				/* �G�[�W�F���gID�擾 */
				String key = (String)e.nextElement();
				int agid = new Integer(key.trim()).intValue();

				/* �G�[�W�F���g�̎�ގ擾 */
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
						/* CD�AASSOCIATE�ȊO�̎w��͗�O���X���[ */
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

				/* �g�p�m�[�h�̎擾 */
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

				/* �C�x���g�f�[�^�t�@�C�����̎擾 */
				String eventFileName = null;
				try {
					eventFileName = stringTokenizer.nextToken();
				}catch(NoSuchElementException nsee) {
					/* �Ȃ��Ă��� */
					eventFileName = null;
				}

				agentsInfo[index].agentID = agid;
				agentsInfo[index].agentType = agentType;
				agentsInfo[index].useNode = useNode;
				agentsInfo[index].eventFileName = eventFileName;
				index++;
			} catch (NumberFormatException nfe) {
				// �m�[�h�̐ݒ�ȊO�͏������Ƃ΂�
			}
		}

		/* Agent���̃`�F�b�N */
		if( index != agentNum ) {
			Exception ex = new Exception
			        ("Agent Number Error: on property file " + fileName);
			throw ex;
		}

	}


	/////////////////////////////////////////////////////////
	// �󋵕����f�[�^�ǂݍ���

	/**
	 * 
	 * @param String fileName
	 * @return boolean[] 
	 */
	public boolean[] loadDecomposeFeature(String fileName) throws
	        FileNotFoundException, IOException {
		boolean[] useNode = null;
		/* �t�@�C���̓ǂݍ��� */
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
	 * �t�@�C������ǂݍ��񂾏���boolean�̔z��ɕϊ����܂��B
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


