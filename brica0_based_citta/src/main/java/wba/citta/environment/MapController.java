/**
 * MapController.java
 * �n�}�t�@�C���̊Ǘ����s�Ȃ��N���X
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * 2001.05 BSC miyamoto
 */
package wba.citta.environment;

import java.io.*;
import java.util.*;
//import jp.ac.wakhok.tomoharu.csv.*;

/**
 * �n�}�t�@�C���̊Ǘ����s�Ȃ��N���X�ł�
 */
public class MapController {

	/* �t�@�C���̓��e�����̂܂ܔz��ɐݒ� String[x][y] */
	private String[][] fileArray;

	/**
	 * �R���X�g���N�^
	 * @param String fileName �t�@�C�����ł̒n�}���
	 */
	public MapController(String fileName) {
		/* �e�[�u�����̍쐬 */
		makeFileArray(fileName);
	}


	/**
	 * �R���X�g���N�^
	 * @param String[][] map String�̔z��ł̒n�}���
	 */
	public MapController(String[][] fileArray) {
		this.fileArray = new String[fileArray.length][fileArray[0].length];
		this.fileArray = fileArray;
	}


	//////////////////////////////////////////////////////
	// public 

	/**
	 * String[][] �̒n�}�����擾���܂��B
	 * @return String[][] �n�}���
	 */
	public String[][] getMap() {
		return fileArray;
	}

	/**
	 * �w�肳�ꂽ�ʒu�Ɏw�肳�ꂽ����ݒ肵�܂��B
	 * @param int x X���W
	 * @param int y Y���W
	 * @param String str �ݒ肷��n�}���
	 */
	public void set(int x, int y, String str) {
		fileArray[x][y] = str;
	}

	/**
	 * �w�肳�ꂽ�ʒu�̏����擾���܂��B
	 * @param int x X���W
	 * @param int y Y���W
	 * @return String �n�}���
	 */
	public String getString(int x, int y) {
		return fileArray[x][y];
	}

	/**
	 * �w�肳�ꂽ���̐ݒ肳��Ă���ʒu���擾���܂��B
	 * @param String �n�}���
	 * @return int[]  int[0] X���W   int[1] Y���W
	 */
	public int[] getPos(String str) {
		return searchMap(str);
	}


	/**
	 * �n�}�̃T�C�Y���擾���܂��B
	 * @return int[] int[0]:x�����̃T�C�Y int[1]:y�����̃T�C�Y
	 */
	public int[] getSize() {
		int[] size = new int[2];
		size[0] = fileArray.length;
		size[1] = fileArray[0].length;
		return size;
	}

	/**
	 * �w�肳�ꂽ�ʒu�̕�V���擾���܂��B
	 * @param int x �����W
	 * @param int y �����W
	 * @return String ��V
	 */
	public String getReward(int x, int y) {

		String fileArrayID = "";
		if(fileArray[x][y].length() > 0) {
			fileArrayID = fileArray[x][y].substring(0, 1);
		}

		String reward = "";

		if(fileArrayID.equals("W")) {              /* �� */
			/* �������Ȃ� */
		}else if(fileArrayID.equals("O")) {        /* ��V */
			/* �����񂩂��V�Ɋւ�������擾 */
			int startIndex = fileArray[x][y].indexOf("(");
			int endIndex = fileArray[x][y].indexOf(")");
			reward = fileArray[x][y].substring(startIndex+1,endIndex);
		}else if(fileArrayID.equals("J")) {        /* Jamp */
			/* �����񂩂��V�Ɋւ�������擾 */
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
		}else {                              /* �ǁE��V�EJAMP�ȊO */
			/* �ǂɗאڂ���ʒu�Ȃ��V��ݒ� */
			for(int n = -1; n < 2; n++) {
				for(int m = -1; m < 2; m++) {
					/* �͈͓����`�F�b�N */
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
	 * �w�肵���ʒu�̃Z���T�����擾���܂��B
	 * @param   int x  x���W
	 * @param   int y  y���W
	 * @return  int[]  int[8]�̃Z���T���
	 */
	public int[] getState(int x, int y){

		/* �Z���T���������� */
		int[] state = new int[8];
		for(int i=0; i<8; i++){
			if(i<4){           /* ShortRenge */
				state[i] = 5;  /* �U��؂��� */
			}else{             /* LongRenge */
				state[i] = 0;  /* �U��؂��� */
			}
		}
		/* �����_���� */
		if(fileArray[x][y].equals("W")){
			for(int i=0; i<8; i++){
				state[i] = -1;
			}
			return state;
		}

		/* ������ */
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

		/* �E���� */
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

		/* ����� */
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

		/* ������ */
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
			if(str.equals("x")) {
				str = "";
			}
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


}


