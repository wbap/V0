/**
 * Util.java
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.10
 */
package wba.citta.gsa;

import java.util.*;


/**
 *
 */
public class Util {

	/**
	 * �����Ŏw�肳�ꂽ�Q��Vector�̗L���ȗv�f���������ǂ������肵�܂��B<BR>
	 * �Q��Vector��null�łȂ��A�v�f���͓����ŁAVector�̗L���ȗv�f
	 * (null�łȂ��v�f)�����ׂē��������true��Ԃ��܂��B
	 * @param Vector v1
	 * @param Vector v2
	 * @return boolean 
	 */
	public static boolean equalsValidElement(Vector v1, Vector v2) {
		boolean b = true;
		if( (v1 != null) && (v2 != null) && (v1.size() == v2.size())) {
			ListIterator li1 = v1.listIterator();
			ListIterator li2 = v2.listIterator();
			while(li1.hasNext()) {
				Integer elm1 = (Integer)li1.next();
				Integer elm2 = (Integer)li2.next();
				if( (elm1 != null) && (elm2 != null) &&
				        (!elm1.equals(elm2)) ) {
					b = false;
				}
			}
		}else {
			b = false;
		}
		return b;
	}



	////////////////////////////////////////////////////////////////
	// ���s�G�[�W�F���g�̑I��

	/*  ���łɎ擾���ꂽ����ݒ肷��boolean�̔z�� */
	private boolean[] useIndex;

	/* �����̃V�[�h */
	private int seed = 1;
	/* ���� */
	private Random random = new Random(seed);

	/**
	 * �R���X�g���N�^
	 * @param int num ���p���鐔
	 */
	public Util(int num) {
		useIndex = new boolean[num];
	}

	/**
	 * �����_���Ȓl���擾���܂��B<BR>
	 * �S�Ď擾�ς݂Ȃ�-1��Ԃ��B
	 * @return int 
	 */
	public int getRandomNum() {
		return getRandomIndex();
	}

	/**
	 * �����_���Ɏ擾������Ԃ��N���A���܂��B
	 */
	public void reset() {
		for(int i = 0; i < useIndex.length; i++) {
			useIndex[i] = false;
		}
	}

	/**
	 * ���g�p��index�̐����擾���܂��B
	 * @return int ���s�������s�Ȃ��Ă��Ȃ��G�[�W�F���g��
	 */
	public int getNotUseNum() {
		int num = 0;
		for(int i = 0; i < useIndex.length; i++) {
			if(useIndex[i] == false) {
				num++;
			}
		}
		return num;
	}


	/**
	 * ���s����G�[�W�F���g�̔z�񒆂�Index���擾���܂��B
	 * @return int ���s����G�[�W�F���g�̔z�񒆂�Index
	 */
	private int getRandomIndex() {
		/* ���g�p�Ȑ����擾 */
		int notUseNum = getNotUseNum();

		/* ���ׂĎg�p����Ă���ΑS�Ă𖢎g�p�ɐݒ肵�S�Ă���I�� */
		if(notUseNum == 0) {
//			clearUseFlag();
//			notUseAgentNum = getNotUseAgentNum();
			return -1;
		}

		int randomNum = random.nextInt(notUseNum);

		int index = 0;
		int falseNum = 0;
		for(; index < useIndex.length; index++) {
			if(useIndex[index] == false) {
				if(falseNum == randomNum) {
					break;
				}
				falseNum++;
			}
		}
		return index;
	}

}
