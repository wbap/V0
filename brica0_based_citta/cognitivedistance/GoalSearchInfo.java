/**
 * GoalSearchInfo.java
 * �S�[���T�����̏����Ǘ�����N���X
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * 2000.12 BSC miyamoto
 */
package cognitivedistance;

import java.util.*;

/**
 * �S�[���T�����̏����Ǘ�����N���X�ł��B
 */
public class GoalSearchInfo {

	/* �e�������Ƃ̒T������ێ�����z�� */
	GoalSearchInfoSingle[] gsis;

	/**
	 * �R���X�g���N�^
	 */
	public GoalSearchInfo() {
		/* �e�������Ƃ̒T������ێ�����N���X�𐶐� */
		gsis = new GoalSearchInfoSingle[4];
		for(int i = 0; i < 4; i++) {
			gsis[i] = new GoalSearchInfoSingle();
		}
	}

	/**
	 * �ݒ肳��Ă�������N���A���܂��B
	 */
	public void clear() {
		/* �e�������s�Ȃ������ǂ����̃t���O��false�ɐݒ� */
		for(int i = 0; i < 4 ; i++) {
			gsis[i].flagUsed = false;
		}
	}

	/**
	 * �S�[���T�����̏���ݒ肵�܂��B
	 * 
	 */
	public void setGoalSearchInfo(int dx, Integer nodeID, Integer cdLngth,
	        int searchLngth, int searchStateNum) {
		/* �g�p���ꂽ�t���O�𗧂āA�e�l��ݒ� */
		gsis[dx].flagUsed = true;
		gsis[dx].searchedNodeID = nodeID;
		gsis[dx].cdLngth = cdLngth;
		gsis[dx].searchLngth = searchLngth;
		gsis[dx].searchStateNum = searchStateNum;
	}


	/**
	 * �w�肳�ꂽ����(D1�`D4)�̒T�����̏����擾���܂��B
	 * @param int dx  �T�����̏����擾���鏈��(0����3�Ŏw��)
	 * @return int[]  �T�����
	 *                �w�肳�ꂽ�������s�Ȃ��Ă��Ȃ��ꍇ��null��Ԃ��B
	 *                int[0] �T�����ꂽ�m�[�h��ID
	 *                       �T���̌��ʌ�����Ȃ������ꍇ-1
	 *                int[1] �T�����ꂽ�m�[�h����S�[���܂ł�CD�̒���
	 *                       �T���̌��ʌ�����Ȃ������ꍇ-1
	 *                int[2] �T���ꂽ�[��
	 *                int[3] �T�����ꂽ��Ԑ�
	 */
	public int[] getGoalSearchInfo(int dx) {
		/* �w�肳�ꂽ������D1�`D4�łȂ��A�܂��͂��̏������s�Ȃ��Ă��Ȃ� */
		if( (dx > 3) || (dx < 0) || (gsis[dx].flagUsed == false ) ) {
			return null;
		}

		/* �߂�l�Ƃ��ĕԂ��ϐ��ɐݒ� */
		int[] gsi = new int[4];
		if(gsis[dx].searchedNodeID != null) {
			gsi[0] = gsis[dx].searchedNodeID.intValue();
		}else {
			gsi[0] = -1;
		}
		if(gsis[dx].cdLngth != null) {
			gsi[1] = gsis[dx].cdLngth.intValue();
		}else {
			gsi[1] = -1;
		}
		gsi[2] = gsis[dx].searchLngth;
		gsi[3] = gsis[dx].searchStateNum;

		return gsi;
	}

	//////////////////////////////////////////////////////////////
	// �e�������Ƃ̃S�[���T���Ɋւ������ێ�����C���i�[�N���X

	/**
	 * �e�������Ƃ̃S�[���T���Ɋւ������ێ�����C���i�[�N���X
	 */
	class GoalSearchInfoSingle {

		/* ���̏������s�Ȃ�ꂽ�������t���O */
		boolean flagUsed = false;
		/* �T�����ꂽ�S�[�����B�\�ȃm�[�h��ID */
		Integer searchedNodeID;
		/* �S�[���܂ł�CD�̒��� */
		Integer cdLngth;
		/* �T�������[�� */
		int searchLngth;
		/* �T��������Ԃ̐� */
		int searchStateNum;
	}

}


