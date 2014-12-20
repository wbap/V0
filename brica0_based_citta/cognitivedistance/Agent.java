/**
 * Agent.java
 * �F�m�����ɂ����������s�Ȃ��N���X
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * 2000.10 BSC miyamoto
 */
package cognitivedistance;

import java.util.*;
import java.io.*;
import cognitivedistance.viewer.*;

/**
 * �F�m�����ɂ����������s�Ȃ��N���X�ł��B<BR><BR>
 * ���R���X�g���N�^�Őݒ�\�ȃp�����[�^�̃f�t�H���g�l��<BR>
 *  maxCDLngth                 10<BR>
 *  shallowSearchLngth          3<BR>
 *  deepSearchLngth           200<BR>
 *  minSearchLngth              2<BR>
 *  maxSegmentSize              5<BR>
 *  minSegmentSize              3(���݂͋@�\���܂���)<BR>
 *  maxFamiliarCount           10<BR>
 *  flagNovelSearch          true<BR>
 *  flagLandmarkSearchDirection false<BR>
 */
public class Agent {

	/* ���Ƃ̃C���^�[�t�F�[�X�̕ϊ����s�Ȃ��N���X */
//	private InterfaceAgent interfaceAgent;
	InterfaceAgent interfaceAgent;

	/* LayeredAgent�̔z�� */
	private LayeredAgent[] layeredAgentArray;

	/* �g�p����LayeredAgent�� */
	private int layerNum;

	/* �V�K�T���̗L���̐؂芷���p�t���O */
	private boolean flagNovelSearch = true;

	//////////////////////////////////////////////////////////////////
	// �R���X�g���N�^�A����������

	/**
	 * �R���X�g���N�^
	 * @param int layerNum �F�m�������W���[�����K�w�����Ďg�p����ꍇ��
	 * ���C�����B�P�w�ł̎g�p����ꍇ�͂P���w��B
	 */
	public Agent(int layerNum) {
		this.layerNum = layerNum;
		initAgent();
	}


	/**
	 * �R���X�g���N�^
	 * @param int layerNum  �F�m�������W���[�����K�w�����Ďg�p����ꍇ��
	 * ���C�����B�P�w�ł̎g�p����ꍇ�͂P���w��B
	 * @param int maxCDLngth  �w�K����ő�̔F�m����
	 * @param int shallowSearchLngth  �S�[����󂭒T������ꍇ�̍ő�̐[��
	 * @param int deepSearchLngth  �S�[����[���T������ꍇ�̍ő�̐[��
	 * @param int minSearchLngth  �S�[����T������ŏ��̐[���B-1���w�肳�ꂽ
	 * �ꍇ�͊m���I�ɒT���ŏ��̐[����ω�������B�[����1�2�3�4�̂��Âꂩ�ŁA
	 * ����8:4:2:1�̊����őI�������B
	 * @param int maxSegmentSize  �����h�}�[�N�Ԃ̍ő勗���B�����Ŏw�肳�ꂽ
	 * �����͈̔͂Ń����h�}�[�N��T�����A�����h�}�[�N��������ΐV���ȃ����h
	 * �}�[�N�𐶐����܂��B
	 * @param int minSegmentSize  �����h�}�[�N�Ԃ̍ŏ�����(���݂͋@�\���܂���)
	 */
	public Agent(int layerNum, int maxCDLngth, int shallowSearchLngth,
	        int deepSearchLngth, int minSearchLngth, int maxSegmentSize,
	        int minSegmentSize) {
		this.layerNum = layerNum;
		Node.maxCDLngth = maxCDLngth;
		LayeredAgent.shallowSearchLngth = shallowSearchLngth;
		LayeredAgent.deepSearchLngth = deepSearchLngth;
		LayeredAgent.minSearchLngth = minSearchLngth;
		LayeredAgent.maxSegmentSize = maxSegmentSize;
		LayeredAgent.minSegmentSize = minSegmentSize;
		initAgent();
	}

	/**
	 * �R���X�g���N�^
	 * @param int layerNum  �F�m�������W���[�����K�w�����Ďg�p����ꍇ��
	 * ���C�����B�P�w�ł̎g�p����ꍇ�͂P���w��B
	 * @param int maxCDLngth  �w�K����ő�̔F�m����
	 * @param int shallowSearchLngth  �S�[����󂭒T������ꍇ�̍ő�̐[��
	 * @param int deepSearchLngth  �S�[����[���T������ꍇ�̍ő�̐[��
	 * @param int minSearchLngth  �S�[����T������ŏ��̐[���B-1���w�肳�ꂽ
	 * �ꍇ�͊m���I�ɒT���ŏ��̐[����ω�������B�[����1�2�3�4�̂��Âꂩ�ŁA
	 * ����8:4:2:1�̊����őI�������B
	 * @param int maxSegmentSize  �����h�}�[�N�Ԃ̍ő勗���B�����Ŏw�肳�ꂽ
	 * �����͈̔͂Ń����h�}�[�N��T�����A�����h�}�[�N��������ΐV���ȃ����h
	 * �}�[�N�𐶐����܂��B
	 * @param int minSegmentSize  �����h�}�[�N�Ԃ̍ŏ�����(���݂͋@�\���܂���)
	 * @param boolean flagNovelSearch  �V�K�T���������s�Ȃ����ǂ����B
	 * true:�s�Ȃ� false:�s�Ȃ�Ȃ�
	 * @param int maxFamiliarCount �����Ŏw�肳�ꂽ�񐔘A�����āA���łɈړ�
	 * �ς݂̏�Ԃֈړ�����ƁA�V�K�T���������s���܂��B
	 * @param boolean flagLandmarkSearchDirection  �Z�O�����g�����s�Ȃ����߂�
	 * �s�Ȃ������h�}�[�N�̒T���̌����B true:������ false:�t����
	 */
	public Agent(int layerNum, int maxCDLngth, int shallowSearchLngth,
	        int deepSearchLngth, int minSearchLngth, int maxSegmentSize,
	        int minSegmentSize, boolean flagNovelSearch, int maxFamiliarCount,
	        boolean flagLandmarkSearchDirection) {
		this(layerNum, maxCDLngth, shallowSearchLngth, deepSearchLngth,
		        minSearchLngth, maxSegmentSize, minSegmentSize);
		this.flagNovelSearch = flagNovelSearch;
		LayeredAgent.maxFamiliarCount = maxFamiliarCount;
		LayeredAgent.flagLandmarkSearchDirection = flagLandmarkSearchDirection;
	}

	/**
	 * Agent�N���X�̏����������B
	 * LayeredAgent,InterfaceAgent�𐶐����܂��B
	 */
	private void initAgent() {

		/*
		 * LayeredAgent�̐��� ��ʑw�ւ̎Q�Ƃ����ʑw�̃R���X�g���N�^�ɐݒ�
		 */
		layeredAgentArray = new LayeredAgent[layerNum];
		for(int i = layerNum-1; i >= 0; i--) {
			if(i == layerNum-1) {
				/* �ŏ�ʂ̑w�ɂ͏�ʑw�ւ̎Q�Ƃ�ݒ肵�Ȃ� */
				layeredAgentArray[i] = new LayeredAgent(null, i);
			}else{
				/* ����ȊO�̑w�ɂ͏�ʑw�ւ̎Q�Ƃ�ݒ� */
				layeredAgentArray[i] = new LayeredAgent(layeredAgentArray[i+1],
				        i);
			}
		}

		/* �C���^�[�t�F�[�X�G�[�W�F���g�ɂ͍ŉ��w��layeredAgentArray��ݒ� */
		interfaceAgent = new InterfaceAgent(layeredAgentArray[0]);
	}


	//////////////////////////////////////////////////////////////////
	// public

	/**
	 * ���݂̏�Ԃ���S�[���̂֌o�H�T�����s�Ȃ��A�S�[���ֈړ����邽�߂�
	 * ���̏�Ԃ��擾���܂��B
	 * �S�[���ւ̌o�H��������Ȃ��ꍇ��null��Ԃ��܂��B
	 * �܂������Őݒ肳�ꂽ���݂̏�Ԃɂ��Ă̔F�m�����EForwardModel�E
	 * InverseMovel�̊w�K���s�Ȃ��A�K�w������Ă���΃Z�O�����g�����s�Ȃ��܂��B
	 * @param Vector currentState ���݂̏��
	 * @param Vector goalState    �S�[���̏��
	 * @return Vector             ���̏��
	 * @exception NullPointerException �����Őݒ肳�ꂽ���݂̏�Ԃ�null�̏ꍇ
	 * @exception ElementNumberException ���݂̏�Ԃ̗v�f��(Vector�̃T�C�Y)��
	 * �s���ȏꍇ�B��Ԃ̗v�f���͎n�߂ɓ��͂��ꂽ��Ԃ̗v�f������ƂȂ�A
	 * �ȍ~�ɓ��͂�����Ԃ̗v�f���͊�ƂȂ�v�f���Ɠ����łȂ���΂Ȃ�܂���
	 */
	public Vector getNextState(Vector currentState, Vector goalState) 
	        throws ElementNumberException {

		/* �w�K���� */
		interfaceAgent.learn(currentState);

		/* ���̏�Ԃ��擾 */
		Vector nextState = interfaceAgent.exec(currentState,goalState);

		/* �V�K�T������ �t���O�ɂ��V�K�T���̗L���̐؂芷�� */
		if(flagNovelSearch) {
			if(nextState == null) {
				nextState = interfaceAgent.novelSearch(currentState);
			}else {
				interfaceAgent.counterReset();
			}
		}

		return nextState;
	}

	// 2001.04.19 �ǉ� miyamoto
	/**
	 * �F�m�����̊w�K���s�Ȃ��܂��B
	 * @param Vector currentState ���݂̏��
	 * @param Exception NullPointerException ���݂̏�Ԃ�null�̏ꍇ
	 * @param Exception ElementNumberException ���݂̏�Ԃ̗v�f�����s���ȏꍇ
	 */
	public void learn(Vector currentState) throws ElementNumberException {
		interfaceAgent.learn(currentState);
	}

	/**
	 * ���s�������s�Ȃ��A�S�[���ֈړ����邽�߂̎��̏�Ԃ��擾���܂��B
	 * @param Vector currentState ���݂̏��
	 * @param Vector goalState    �S�[���̏��
	 * @return Vector             ���̏��
	 * @param Exception NullPointerException ���݂̏�Ԃ�null�̏ꍇ
	 * @param Exception ElementNumberException ���݂̏�Ԃ̗v�f�����s���ȏꍇ
	 */
	public Vector exec(Vector currentState, Vector goalState)
	        throws ElementNumberException {
		return interfaceAgent.exec(currentState, goalState);
	}
	// �����܂�

	/**
	 * �w�K�f�[�^���t�@�C������Ǎ��݂܂��B
	 * @param String fileName �t�@�C����
	 */
	public void load(String fileName) {
		System.out.println("Loading learning data....");
		try{
			FileInputStream istream = new FileInputStream(fileName);
			ObjectInputStream oInputStream = new ObjectInputStream(istream);

			/* �I�u�W�F�N�g�̓Ǎ��� */
			interfaceAgent.load(oInputStream);
			for(int i = 0; i < layerNum; i++) {
				layeredAgentArray[i].load(oInputStream);
			}

			oInputStream.close();
			istream.close();

		}catch(Exception e){
			System.out.println(e);
			System.exit(0);
		}
	}


	/**
	 * �w�K�f�[�^���t�@�C���ɕۑ����܂��B
	 * @param String fileName �t�@�C����
	 */
	public void save(String fileName) {
		System.out.println("Saving learning data....");
		try{
			/* �X�g���[���̍쐬 */
			FileOutputStream ostream = new FileOutputStream(fileName, false);
			ObjectOutputStream oOutputStream = new ObjectOutputStream(ostream);

			/* �I�u�W�F�N�g�̓Ǎ��� */
			interfaceAgent.save(oOutputStream);
			for(int i = 0; i < layerNum; i++) {
				layeredAgentArray[i].save(oOutputStream);
			}

			oOutputStream.flush();

			oOutputStream.close();
			ostream.close();

		}catch(Exception e){
			System.out.println(e);
		}

	}


	///////////////////////////////////////////////////////////////////
	// �f�o�b�N�p�̏��̎擾�A����̐���Ɏg�p���郁�\�b�h

	/**
	 * �F�m�������w�K���邽�߂ɕێ����Ă����Ԃ̗������N���A���܂��B
	 */
	public void reset() {
		interfaceAgent.reset();
	}


	/**
	 * �F�m�����EForwardModel�EInverseMovel�̊w�K���s�Ȃ����A�s�Ȃ�Ȃ���
	 * �ݒ肵�܂��B�f�t�H���g�ł͑S�Ă̑w�̊w�K���s�Ȃ��܂��B
	 * @param int layerID �ݒ肷�郌�C�� (0�`)
	 * @param boolean flag  true�F�w�K���s�Ȃ�  false�F�w�K���s�Ȃ�Ȃ�
	 */
	public void setLearningFlag(int layerID, boolean flag) {
		layeredAgentArray[layerID].setLearningFlag(flag);
	}


	/**
	 * �Z�O�����g��(�Z�O�����g�����A�Z�O�����g�̃����h�}�[�N�̐ݒ�)���s�Ȃ���
	 * �A�s�Ȃ�Ȃ����ݒ肵�܂��B�f�t�H���g�ł͑S�Ă̑w�̃Z�O�����g�����s�Ȃ�
	 * �܂��B
	 * @param int layerID �ݒ肷�郌�C�� (0�`)
	 * @param boolean flag  true�F�w�K���s�Ȃ�  false�F�w�K���s�Ȃ�Ȃ�
	 */
	public void setSegmentationFlag(int layerID, boolean flag) {
		layeredAgentArray[layerID].setSegmentationFlag(flag);
	}


	/**
	 * �g�p����b�c�̍ő�T�C�Y��ύX���܂��B
	 * (�e�X�g�p�̃��\�b�h��stateBuffer�ւ̉e���͍l�����Ă��Ȃ��j
	 * @param int lngth �V��������
	 */
//	public void changeMaxCDLngth(int lngth) {
//		Node.maxCDLngth = lngth;
//	}


	/**
	 * �g�p���郌�C������ύX���܂��B(���C���������炷���̂݉\�j
	 * @param int newLayerNum  �V�������C����
	 */
//	public void changeLayerNum(int newLayerNum) {
//		if(newLayerNum < layerNum) {
//			/* LayeredAgent�̔z��̓��g�p���Ȃ����̂��폜 */
//			for(int i = 0; i < layerNum; layerNum++) {
//				if(i < newLayerNum) {
//				}else {
//					layeredAgentArray[i] = null;
//				}
//			}
//			/* �V�����ŏ�ʑw�̏�ʑw���폜 */
//			layeredAgentArray[newLayerNum-1].deleteUpperLayer();
//			layerNum = newLayerNum;
//		}
//	}


	/**
	 * �w�肳�ꂽ��ԁE���C���ɑΉ�����m�[�h�N���X�̃I�u�W�F�N�g���擾���܂��B
	 * @param Object state �����ł̏��
	 * @param int layer    ���C��
	 * @return Node        �m�[�h
	 */
	public Node getNode(Vector state, int layer) {
		return interfaceAgent.getNode(state, layer);
	}


	/**
	 * �s���ς݂̑S��Ԃ��擾���܂��B
	 * @return Vector �s���ς݂̑S��Ԃ̐ݒ肳�ꂽVector
	 */
	public Vector getStateTable() {
		return interfaceAgent.getIdToState();
	}


	/**
	 * �����Ŏw�肵���w�̎��s���̏����Ɋւ�������擾���܂��B
	 * @param int layerNum  �����擾���郌�C��
	 * @return int[]        ���s���̏����Ɋւ�����<BR>
	 *                      �l�̂Ȃ���Ԃɂ��Ă�-1���ݒ肳���<BR>
	 *                      int[0] ���݂̏�Ԃ�ID<BR>
	 *                      int[1] �S�[���̏�Ԃ�ID<BR>
	 *                      int[2] ��ʑw����̃T�u�S�[���̏�Ԃ�ID<BR>
	 *                      int[3] ���̏�Ԃ�ID<BR>
	 *                      int[4] ���̏�Ԃ��o�͂��Ă��鏈����ID<BR>
	 *                      int[5] �T�u�S�[�����X�V����Ă��邩
	 *                             0�F�X�V����Ă��Ȃ� 1�F�X�V����Ă���<BR>
	 */
	public int[] getExecInfo(int layerNum) {
		ExecInfo ei = layeredAgentArray[layerNum].getExecInfo();
		return ei.getExecInfo();
	}

	/**
	 * �S�Ă̑w�̎��s���̏����Ɋւ�������N���A���܂��B<BR>
	 * �e�w�̎��s���̏����Ɋւ�����̓S�[���������Ȃ����ꍇ���A���̑w�̏�����
	 * �s���Ȃ��Ȃ邽�߁A�ȑO�̏�񂪎c���Ă��܂��܂��B
	 * ���̂��ߕK�v�ɉ����ď����N���A���܂��B
	 */
//	public void resetExecInfo() {
//		for(int i = 0; i < layerNum; i++) {
//			layeredAgentArray[i].resetExecInfo();
//		}
//	}

	/**
	 * �����Ŏw�肵���w�̃S�[���T���Ɋւ�������擾���܂��B
	 * @param int layerNum �����擾���郌�C��
	 * @param int dx       �����擾���鏈�� D1�`D4 (0�`3�Ŏw��)
	 * @return int[]       �S�[���̒T���Ɋւ����񂪐ݒ肳�ꂽ�z��<BR>
	 *                     �w�肳�ꂽ�������s�Ȃ��Ă��Ȃ��ꍇ��null��Ԃ�<BR>
	 *                     int[0] �T�����ꂽ�m�[�h��ID
	 *                            �T���̌��ʌ�����Ȃ������ꍇ-1<BR>
	 *                     int[1] �T�����ꂽ�m�[�h����S�[���܂ł�CD�̒���
	 *                            �T���̌��ʌ�����Ȃ������ꍇ-1<BR>
	 *                     int[2] �T���ꂽ�[��<BR>
	 *                     int[3] �T�����ꂽ��Ԑ�<BR>
	 */
//	public int[] getGoalSearchInfo(int layerNum, int dx) {
//		GoalSearchInfo gsi = layeredAgentArray[layerNum].getGoalSearchInfo();
//		return gsi.getGoalSearchInfo(dx);
//	}


	/**
	 * �����Ŏw�肵���w�̊w�K�󋵂Ɋւ�������擾���܂��B
	 * @param int layerNum  �����擾���郌�C��
	 * @return int[]        �w�K�󋵂Ɋւ�����<BR>
	 *                      int[0] �S��Ԃ�MoveableState�̃T�C�Y�̍��v<BR>
	 *                      int[1] �S��Ԃ�CognitiveDistance�̃T�C�Y�̍��v<BR>
	 *                      int[2] �S��Ԑ�<BR>
	 *                      int[3] �L���ȏ�Ԑ�(�����h�}�[�N�̍폜���ɉe��)<BR>
	 */
//	public int[] getLearningInfo(int layerNum) {
//		return layeredAgentArray[layerNum].getLearningInfo();
//	}

	// 2001.08.09 �ǉ� miyamoto
	/**
	 * �ŏ��ŒT�����s���[�����擾���܂��B
	 * @return int �ŏ��ŒT�����s���[��
	 */
	public int getMinSearchLngth() {
		return LayeredAgent.minSearchLngth;
	}

	// 2001.08.09 �ǉ� miyamoto
	/**
	 * �ŏ��ŒT�����s���[����ݒ肵�܂��B
	 * @param int minSearchLngth �ŏ��ŒT�����s���[��
	 */
	public void setMinSearchLngth(int minSearchLngth) {
		LayeredAgent.minSearchLngth = minSearchLngth;
	}


	// 2001.08.14 �ǉ� miyamoto
	/**
	 * ��� a ������ b �ւ̓��B�\���𒲂ׂ܂��B
	 * @param Vector a
	 * @param Vector b
	 * @return boolean true ���B�\ false ���B�s�\
	 */
	public boolean isReach(Vector a, Vector b) {
		return interfaceAgent.isReach(a, b);
	}

	// 2001.08.15 �ǉ� miyamoto
	/**
	 * �e�w�ŕێ����Ă���A�ȑO�̏�ԁE�S�[���Ɋւ�������N���A���܂��B
	 */
	public void resetOldValue() {
		interfaceAgent.resetOldValue();
	}


}
