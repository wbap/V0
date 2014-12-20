/**
 * ActionController.java
 * �n�}������͂��čs���ɂ��ĊǗ�����N���X
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 *    BSC miyamoto 2001.05
 */
package environment;

import java.util.*;

/**
 * �n�}������͂��čs���ɂ��ĊǗ�����N���X�ł��B
 */
public class ActionController {

	/* �t�@�C���̏��̔z�� */
	private String[][] mapArray;

	/**
	 * �R���X�g���N�^
	 * @return String[][] mapArray
	 */
	public ActionController(String[][] mapArray) {
		this.mapArray = mapArray;
	}


	//////////////////////////////////////////////////////////////////
	// public

	/**
	 * �w�肳�ꂽ�ʒu�EAction����̈ړ���̈ʒu���擾���܂��B
	 * @param int x x���W
	 * @param int y y���W
	 * @param int action �ړ�����
	 * @return int[] �ړ���̍��W
	 */
	public int[] move(int x, int y, int action) {

		/* �t�@�C������Ή�����ʒu�̏����擾 */
		String mapData = mapArray[x][y];

		int[] elem = null;

		if(mapData.length() != 0) { /* ��񂪐ݒ肳��Ă��� */
			String mapID = mapData.substring(0, 1);
			if( mapID.equals("S") ) {
				/* �X�^�[�g */
				elem = nomalMove(x, y, action);
			}else if( mapID.equals("O") ) {
				/* ���̕�V */
				elem = nomalMove(x, y, action);
			}else if( mapID.equals("d") ) {
				/* �J���Ă���h�A */
				elem = nomalMove(x, y, action);
			}else if( mapID.equals("K") ) {
				/* �J�M */
				// 2001.03.26 �ǉ� miyamoto
//				key = true;
				elem = nomalMove(x, y, action);
			}else if( mapID.equals("T") ) {
				elem = nomalMove(x, y, action);
			}else if( mapID.equals("J") ) {
				/* �W�����v */
				elem = jampMove(x, y, action, mapData);
			}else if( mapID.equals("F") ) {
				/* ���� */
				elem = flowMove(x, y, action, mapData);
			}else if( mapID.equals("C") ) {
				/* �R */
				elem = cliffMove(x, y, action, mapData);
			}else if( mapID.equals("R") ) {
				elem = randomMove(x, y, action, mapData);
			}else {
				// 2001.07.13 ���̑��͒ʏ�̓���
				elem = nomalMove(x, y, action);
			}
		}else {                                 /* �� */
			elem = nomalMove(x, y, action);
		}

		/* �ǂ̃`�F�b�N */
		if(!checkState(elem[0], elem[1])) {
			elem[0] = x;
			elem[1] = y;
		}

		return elem;
	}

	//////////////////////////////////////////////////////////////////
	// private

	/**
	 * �ʏ�̓����ݒ肵�܂��B
	 * @param int x      �����W
	 * @param int y      �����W
	 * @param int action �s��
	 * @return int[]     �ړ���̍��W
	 */
	private int[] nomalMove(int x, int y, int action) {
		/* �v�f�̎擾 */
		int[] elem = getNextState(x, y, action);
		return elem;
	}


	/**
	 * �W�����v�̍s�������܂��B
	 * @param int x           �����W
	 * @param int y           �����W
	 * @param int action      �s��
	 * @param String mapDate  �n�}���
	 * @return int[]          �ړ���̍��W
	 */
	private int[] jampMove(int x, int y, int action, String mapData) {

		/* �W�����v�����̏����擾 */
		int startIndex = mapData.indexOf( "(" );
		int endIndex = mapData.indexOf( ")" );
		/* ()�̒����擾 */
		String subStr = mapData.substring(startIndex+1, endIndex);
		StringTokenizer st = new StringTokenizer(subStr, ":");

		int toStateX = Integer.parseInt(st.nextToken());
		int toStateY = Integer.parseInt(st.nextToken());
		int jampAction = Integer.parseInt(st.nextToken());

		int[] elem = new int[2];
		if(action == jampAction) { /* �W�����v�̃A�N�V���� */
			elem[0] = toStateX;
			elem[1] = toStateY;
		}else {                    /* �ʏ�̍s��������A�N�V���� */
			elem = getNextState(x, y, action);
		}

		return elem;

	}


	/**
	 * ����̂���ꏊ�̈ړ����ݒ肵�܂��B
	 * @param int x           �����W
	 * @param int y           �����W
	 * @param int action      �s��
	 * @param String fileDate �t�@�C������̏��
	 * @return int[]          �ړ���̍��W
	 */
	private int[] flowMove(int x, int y, int action, String mapData) {

		/* ����̕����ɂ��Ă̏����擾 */
		int startIndex = mapData.indexOf( "(" );
		int endIndex = mapData.indexOf( ")" );
		String flow = mapData.substring(startIndex + 1, endIndex);

		/* �ʏ�̈ړ�����擾 */
		int[] elem = getNextState(x, y, action);

		if(checkState(elem[0], elem[1])) {
			/* ������ړ���̏�Ԃɒǉ� */
			if(flow.equals("U")) {
				elem[1] --; 
			}
			if(flow.equals("D")) {
				elem[1] ++; 
			}
			if(flow.equals("L")) {
				elem[0] --; 
			}
			if(flow.equals("R")) {
				elem[0] ++; 
			}
		}else {
			elem[0] = x;
			elem[1] = y;
		}

		return elem;
	}


	/**
	 * �R�ɂȂ��Ă���ꏊ�̈ړ����ݒ肵�܂��B
	 * @param int x           �����W
	 * @param int y           �����W
	 * @param int action      �s��
	 * @param String fileDate �t�@�C������̏��
	 * @return int[]          �ړ���̍��W
	 */
	private int[] cliffMove(int x, int y, int action, String mapData) {

		int index = mapData.indexOf( "(" );
		String savedMove = mapData.substring(index + 1, index + 2);

		int[] elem = new int[2];

		int saveAction = -1;
		if(savedMove.equals("U")) {
			saveAction = 0; 
		}
		if(savedMove.equals("D")) {
			saveAction = 4; 
		}
		if(savedMove.equals("L")) {
			saveAction = 2; 
		}
		if(savedMove.equals("R")) {
			saveAction = 6; 
		}

		if(action == saveAction) { /* �ړ��s�̕����ւ̈ړ� */
			elem[0] = x;
			elem[1] = y;
		}else {                   /* ���̑��̕����ւ̈ړ� */
			elem = getNextState(x, y, action);
		}

		return elem;
	}

	// 2001.07.06 �ǉ� miyamoto
	private Random randomMove = new Random(0);
	private int[] randomMove(int x, int y, int action, String mapData) {
		// ���ɂS�����Ɍ���
		int randomAction = randomMove.nextInt(4)*2;
		/* �v�f�̎擾 */
		int[] elem = getNextState(x, y, randomAction);
		return elem;
	}
	// �����܂�

	/**
	 * ��,�����W�̏�Ԃ���action���s�Ȃ����ꍇ�̏�Ԃ��擾���܂��B
	 * @param int x      x���W
	 * @param int y      y���W
	 * @param int action �s��
	 * @return int[]     �ړ���̏��
	 */
	private int[] getNextState(int x, int y, int action) {
		int[] elem = new int[2];

		/* ��~ */
		if(action == -1) {
			elem[0] = x;
			elem[1] = y;
		}
		/* �� */
		if(action == 0) {
			elem[0] = x;
			elem[1] = y-1;
		}
		/* ���� */
		if(action == 1) {
			elem[0] = x-1;
			elem[1] = y-1;
		}
		/* �� */
		if(action == 2) {
			elem[0] = x-1;
			elem[1] = y;
		}
		/* ���� */
		if(action == 3) {
			elem[0] = x-1;
			elem[1] = y+1;
		}
		/* �� */
		if(action == 4) {
			elem[0] = x;
			elem[1] = y+1;
		}
		/* �E�� */
		if(action == 5) {
			elem[0] = x+1;
			elem[1] = y+1;
		}
		/* �E */
		if(action == 6) {
			elem[0] = x+1;
			elem[1] = y;
		}
		/* �E�� */
		if(action == 7) {
			elem[0] = x+1;
			elem[1] = y-1;
		}

		return elem;
	}

	private boolean collisionDoor = false;
	public boolean isCollisionDoor() {
		return collisionDoor;
	}

	/**
	 * �w�肳�ꂽ�ʒu���ړ��\���`�F�b�N���܂��B
	 * @param int x �����W
	 * @param int y �����W
	 * @param boolean  true �ړ��\  false �ړ��s�\
	 */
	private boolean checkState(int x, int y) {

		collisionDoor = false;

		/* �n�}�͈͓̔� */
		if( (x >= 0) && (x < mapArray.length) && (y >= 0) &&
		        (y < mapArray[0].length) ) {

			/*  �� �܂��� ���Ă���h�A �łȂ� */ 
			if(!mapArray[x][y].equals("W") && !mapArray[x][y].equals("D")) {
				return true;
			}
			if(mapArray[x][y].equals("D")) {
				collisionDoor = true;
			}

		}
		return false;
	}


}
