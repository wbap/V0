/**
 * MapFileToArray.java
 * CSV�`���̒n�}�t�@�C����z��ɕϊ�����N���X
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * 2001.08 miyamoto
 */
package environment;

import java.io.*;
import java.util.*;
//import jp.ac.wakhok.tomoharu.csv.*;

/**
 * CSV�`���̒n�}�t�@�C����z��ɕϊ�����N���X
 */
public class MapFileToArray {

	/* �t�@�C���̓��e�����̂܂ܔz��ɐݒ� String[x][y] */
	private String[][] fileArray;

	/**
	 * �R���X�g���N�^
	 * @param String fileName �t�@�C�����ł̒n�}���
	 */
	public MapFileToArray(String fileName) {
		/* �e�[�u�����̍쐬 */
		makeFileArray(fileName);
	}


	public String[][] getFileArray() {
		return fileArray;
	}

	/**
	 * �n�}�t�@�C���̏��𕶎���̔z��ɐݒ�
	 * @param String fileName �t�@�C����
	 */
	private void makeFileArray(String fileName) {

		/* �t�@�C���̏���ǂݍ��� ��s���̕�����������X�g�Ŏ擾 */
		LinkedList mapStringList = loadMap(fileName);

		/* �z��̃T�C�Y�ݒ� */
		String s = (String)mapStringList.get(0);
// 2001.12.20 miyamoto
// CSVTokenizer�̎g�p����߁AStringTokenizer�őΉ�
//		CSVTokenizer csvTokenizer = new CSVTokenizer(s);
//		int tokenNum = csvTokenizer.countTokens();
		StringTokenizer stringTokenizer = new StringTokenizer(s, ",");
		int tokenNum = stringTokenizer.countTokens();
// �����܂�

		fileArray = new String[tokenNum][mapStringList.size()];

		/* 1�s���̃f�[�^����� �z��ɐݒ� */
		int lineCnt = 0;
		ListIterator literator = mapStringList.listIterator();
		while(literator.hasNext()) {
			String str = (String)literator.next();
			parseString(str, lineCnt);
			lineCnt ++;
		}
	}


	/**
	 * �n�}�����t�@�C������ǂݍ��݂܂��B
	 * @ param String fileName �n�}���̃t�@�C����
	 * @ return LinkedList �n�}����ǂݍ���String�̃��X�g
	 */
	private LinkedList loadMap(String fileName) {

		LinkedList lList = new LinkedList();
		try {
			/* �t�@�C���̓Ǎ��� */
			FileReader fReader = new FileReader(fileName);
			BufferedReader bReader = new BufferedReader(fReader);

			try {
				while(true) {
					if(bReader.ready() == false) {
						break;
					}
					/* �t�@�C���̓��e��1�s���擾��������̃��X�g�Ƃ��Đݒ� */
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
	 * �J���}�ŋ�؂�ꂽ��������String�̔z��ɕϊ����܂��B
	 * @param String mapData ������̒n�}���
	 * @param int lineCnt �s��(y���W)�̃J�E���g
	 */
	private void parseString(String mapData, int lineCnt) {
// 2001.12.20 miyamoto
// CSVTokenizer�̎g�p����߁AStringTokenizer�őΉ�
//		CSVTokenizer csvTokenizer = new CSVTokenizer(mapData);
//		int tokenCnt = 0;
//		while(csvTokenizer.hasMoreElements()) {
//			String str = csvTokenizer.nextToken();
//
//			/* �t�@�C���̏���z��Ƃ��ĕێ� */
//			fileArray[tokenCnt][lineCnt] = str;
//
//			tokenCnt ++;
//		}
		StringTokenizer stringTokenizer = new StringTokenizer(mapData, ",");
		int tokenCnt = 0;
		while(stringTokenizer.hasMoreElements()) {
			String str = stringTokenizer.nextToken();

			/* �t�@�C���̏���z��Ƃ��ĕێ� */
			fileArray[tokenCnt][lineCnt] = str;

			tokenCnt ++;
		}
// �����܂�
	}


	/**
	 * �n�}�ォ��w�肳�ꂽ������̂�����W���擾���܂��B
	 * @param String targerID �T��������
	 * @param int[]           ���W
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


