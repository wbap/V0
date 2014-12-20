/**
 * DemoProperty.java
 * �h�A�L�[�ۑ�̃f���̐ݒ���Ǘ�����N���X
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.09
 */
package doorkeydemo;

import java.io.*;
import java.util.*;

/**
 * �h�A�L�[�ۑ�̃f���̐ݒ���Ǘ�����N���X
 */
public class DemoProperty {

	/* ���̒n�}��� */
	private String envFileName;
	/* �h�A���J������ */
	private int doorOpenMode;

//	/* �m�[�h�� */
//	private int nodeNum;

	/* �����Ɋւ���ݒ� */
	private int saveStepNum;     /* �w�K�f�[�^���Z�[�u����X�e�b�v�� */
	private String saveFileName; /* �w�K�f�[�^���Z�[�u����t�@�C���� */
	private String loadFileName; /* �w�K�f�[�^�����[�h����t�@�C���� */

	private int timeOutStepNum;  /* 1�g���C�A���̃^�C���A�E�g�̃X�e�b�v�� */
	private int sleepTime;       /* 1�T�C�N�����ƂɃX���[�v�����鎞�� */

	/* GSA�̐ݒ�t�@�C���� */
	private String gsaPropFileName;

	////////////////////////////////////////////////////////////////
	// �R���X�g���N�^

	/**
	 * �R���X�g���N�^
	 * @param String propFileName 
	 */
	public DemoProperty(String propFileName)
	        throws FileNotFoundException, IOException, Exception {
		loadProperty(propFileName);
	}

	////////////////////////////////////////////////////////////////
	// public

	/**
	 * GSA�̐ݒ�t�@�C�������擾���܂��B
	 * @return String �ݒ�t�@�C����
	 */
	public String getGSAPropFileName() {
		return gsaPropFileName;
	}

	/**
	 * �w�K�f�[�^���Z�[�u����X�e�b�v�����擾���܂��B
	 * @return int �X�e�b�v��
	 */
	public int getSaveStepNum() {
		return saveStepNum;
	}

	/**
	 * �w�K�f�[�^���Z�[�u����t�@�C�������擾���܂��B
	 * @return String �t�@�C����
	 */
	public String getSaveFileName() {
		return saveFileName;
	}

	/**
	 * �w�K�f�[�^�����[�h����t�@�C�������擾���܂��B
	 * @return String �t�@�C����
	 */
	public String getLoadFileName() {
		return loadFileName;
	}

	/**
	 * 1�g���C�A���̃^�C���A�E�g�̃X�e�b�v�����擾���܂��B
	 * @return int �^�C���A�E�g�̃X�e�b�v��
	 */
	public int getTimeOutStepNum() {
		return timeOutStepNum;
	}

	/**
	 * �����̑��x�𒲐����邽�߂ɃX���[�v�����鎞�Ԃ��擾���܂��B
	 * @return int �X���[�v�^�C��
	 */
	public int getSleepTime() {
		return sleepTime;
	}

	/**
	 * ���̒n�}���̃t�@�C�������擾���܂��B
	 * @return String �t�@�C����
	 */
	public String getEnvFileName()  {
		return envFileName;
	}

	/**
	 * �h�A�̊J���������擾���܂��B
	 * @return int �h�A���J������
	 */
	public int getDoorOpenMode() {
		return doorOpenMode;
	}

	/**
	 * ���Ŏg�p�����m�[�h�����擾���܂��B
	 * @return int �m�[�h��
	 */
//	public int getNodeNum() {
//		return nodeNum;
//	}


	////////////////////////////////////////////////////////////////
	// private

	/**
	 * �t�@�C��������̓ǂݍ���
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
			/* ���̃t�@�C���̓Ǎ��� */
			contents = new StringTokenizer( prop.getProperty("Environment") );
			envFileName = contents.nextToken();

			contents = new StringTokenizer( prop.getProperty(
			        "GSAPropFileName") );
			gsaPropFileName = contents.nextToken();

			/* �h�A���J�������̎擾 */
			contents = new StringTokenizer( prop.getProperty("DoorOpenMode") );
			doorOpenMode = new Integer(contents.nextToken()).intValue();

			/* �m�[�h���̓Ǎ��� */
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
			/* �����ݒ�̓ǂݍ��� */
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


