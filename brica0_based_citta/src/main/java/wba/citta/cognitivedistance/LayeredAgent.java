/**
 * LayeredAgent.java
 * CognitiveDistance�̂P�̑w�ɂ��Ă̏������s���G�[�W�F���g�̃N���X
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * 2000.10 BSC miyamoto
 */
package wba.citta.cognitivedistance;

import java.util.*;
import java.io.*;
import wba.citta.cognitivedistance.viewer.*;

/**
 * CognitiveDistance�̂P�̑w�ɂ��Ă̏������s���G�[�W�F���g�̃N���X�ł��B
 */
public class LayeredAgent {

	/* ���̃G�[�W�F���g�̃��C��ID */
	private int layerID;

	/* ��ʑw�̃G�[�W�F���g */
	private LayeredAgent upperLayerAgent;
	/* ������Node������Vector */
	private Vector nodes;
	/* �ړ������m�[�hID(Integer)�̗��� */
	private StateBuffer stateBuffer;

	/**
	 * �󂢒T�����s�Ȃ��ő�̐[��
	 * ���B�\�ȃm�[�h�������邩�A���̒l�̐[���܂ł̒T�����s��
	 */
	public static int shallowSearchLngth = 3;

	/**
	 * �[���T�����s�Ȃ��ő�̐[��
	 * ���B�\�ȃm�[�h�������邩�A���̒l�̐[���܂ł̒T�����s��
	 */
	public static int deepSearchLngth = 200;

	/**
	 * �ŏ��ōs���T���̐[��
	 * �����Ŏw�肳�ꂽ�[���܂ŒT�����s���A�ł��߂������̃m�[�h��I��
	 * (�T�u�S�[���ɂ̒T���ɂ�����)
	 */
	public static int minSearchLngth = 2;

	/**
	 * �����h�}�[�N��ݒ肵�Ă����Ԋu
	 * ���͈͓̔��Ń����h�}�[�N��T�����A�Ȃ���Ύ���������h�}�[�N�ɐݒ�
	 * (0�̏ꍇ���ׂĂ������h�}�[�N��)
	 */
	public static int maxSegmentSize = 5;

	/**
	 * �����h�}�[�N�Ԃ̍ŏ��̊Ԋu
	 * ���͈͓̔��ɑ��̃����h�}�[�N������ꍇ�͌��݂̃����h�}�[�N���폜����
	 * (0�̏ꍇ�͍폜�͍s�Ȃ�Ȃ�)
	 */
	public static int minSegmentSize = 3;

	/**
	 * �V�K�T���ɂ��T�u�S�[����ݒ肷��܂ł̊Ԋu
	 */
	public static int maxFamiliarCount = 10;

	// 2001.05.22 �ǉ� miyamoto 
	/**
	 * �Z�O�����g���̃����h�}�[�N�T���̌�����؂芷����t���O
	 * true:ForwardModel�ŃZ�O�����g��  false:InverseModel�ŃZ�O�����g��
	 */
	public static boolean flagLandmarkSearchDirection = false;


	/* �Z�O�����g���̃J�E���g */
	private int segmentCount;

	/* �S�[���T���Ɏg�p����ϐ� */
	/* ��O�̏�ʑw�̎��̏�� */
	private Integer id_Vu0;
	/* ��O�̃S�[���̏�� */
	private Integer id_Gu0;
	/* ��ʑw�𗘗p���邩�ǂ����̃t���O */
	private boolean useUpperFlag = true;

	/* �V�K��Ԃ̒T���Ɏg�p����ϐ� */
	/* �O�T�C�N���̏�� */
	private Integer id_S0;
	/* �O�T�C�N���̎���̑w�ł̐V�K�T���ɂ��T�u�S�[�� */
	private Integer id_F0;
	/* �O�T�C�N���̐V�K�T�������ŏ�ʑw����擾�����T�u�S�[�� */
	private Integer id_FVu0;
	/* �m���Ă����Ԃ��A���������̃J�E���^�[ */
	private int familiarCount;

	/* �e�X�g�E�����p�̕ϐ� */
	/* �K�w���E�Z�O�����g���Ɋւ��Ă̏��̐ݒ�p */
	private ExecInfo execInfo = new ExecInfo();
	/* �S�[���T�����̏��̐ݒ�p */
	private GoalSearchInfo goalSearchInfo = new GoalSearchInfo();

	/** �w�K���s�Ȃ����ǂ����̃t���O */
	private boolean learningFlag = true;
	/* �����h�}�[�N�̊w�K���s�Ȃ��ǂ����̃t���O */
	private boolean landmarkLearningFlag = true;

	/////////////////////////////////////////////////////////////
	// �R���X�g���N�^

	/**
	 * �R���X�g���N�^
	 * @param LayeredAgent layeredAgent  ��ʑw�̏������s���G�[�W�F���g
	 * @param int layerID               ���̃G�[�W�F���g���������s�����C����ID
	 */
	public LayeredAgent(LayeredAgent layeredAgent, int layerID) {

		/* ���C����ID */
		this.layerID = layerID;
		/* ��ʑw�̃G�[�W�F���g */
		upperLayerAgent = layeredAgent;
		/* ���݂̑w�ł̏�Ԃ��Ǘ�����Vector */
//		nodes = new Vector();
		nodes = new Vector(10000);
		/* StateBuffer�̍ő�̃T�C�Y�͂b�c�̍ő�l�{�P */
		stateBuffer = new StateBuffer(Node.maxCDLngth+1);

		/* �p�����[�^�̕\�� */
		System.out.println("LayerID " + layerID);
		System.out.println(" Max CD Length             " + Node.maxCDLngth);
		System.out.println(" Shallow Search Length     " + shallowSearchLngth);
		System.out.println(" Deep Search Length        " + deepSearchLngth);
		System.out.println(" Min Search Length         " + minSearchLngth);
		System.out.println(" Max Segment Size          " + maxSegmentSize);
		System.out.println(" Min Segment Size          " + minSegmentSize);

	}


	//////////////////////////////////////////////////////////////////////////
	// Node�̑���

	/**
	 * ������ID�ɑΉ�����m�[�h�I�u�W�F�N�g���擾���܂��B
	 * @param Integer id �m�[�h��ID
	 * @return Node      �m�[�h
	 */
	public Node getNode(Integer id) {

		if(id == null) {
			return null;
		}

		int intID = id.intValue();
		if( nodes.size() <= intID ) {
			return null;
		}
		// 2001.05.24 �C�� miyamoto �Â��o�[�W������java�ɑΉ�
//		return (Node)nodes.get(intID);
		return (Node)nodes.elementAt(intID);
	}

	/**
	 * �V�����m�[�h�I�u�W�F�N�g�𐶐����܂��B
	 * @param Integer lowerID ���ʑw�ł̃m�[�h��ID
	 * @return Node            �������ꂽ�m�[�h
	 */
	public Node newNode(Integer lowerID) {
		Node node = new Node(new Integer(nodes.size()), lowerID);
		// 2001.05.24 �C�� miyamoto �Â��o�[�W������java�ɑΉ�
//		nodes.add(node);
		nodes.addElement(node);
		return node;
	}

	/**
	 * �m�[�h�𖳌��ɂ��܂��B
	 * @param Integer nodeID    �����ɂ���m�[�hID
	 * @param Integer refID ����̎Q�Ɛ��ID
	 * @param int refStep   �Q�Ɛ�܂ł̋���
	 * @return boolean      true �폜���� false �폜���s
	 */
	private boolean deleteNode(Integer nodeID, Integer refID, int refStep) {
		Node node = getNode(nodeID);
		return node.delete(refID, refStep);
	}

	/**
	 * �����̃m�[�hID�ɑΉ������ʑw�̃m�[�hID���擾���܂��B
	 * @param Integer nodeID �m�[�hID
	 * @return Integer       ��ʑw�̃m�[�hID
	 */
	private Integer getUpperLayerNodeID(Integer nodeID) {

		if(nodeID == null) {
			return null;
		}
		/* ��ʑw�̃m�[�h�������ɂȂ��Ă��Ȃ����`�F�b�N�E�����Ȃ�X�V */
		Integer upperLayerNodeID = renewUpperIDAndStep(nodeID);
		return upperLayerNodeID;
	}

	/**
	 * �����̃m�[�hID�ɑΉ����鉺�ʑw�̃m�[�hID���擾���܂��B
	 * @param Integer nodeID �m�[�hID
	 * @return Integer       ���ʑw�̃m�[�hID
	 */
	private Integer getLowerLayerNodeID(Integer nodeID) {

		if(nodeID == null) {
			return null;
		}
		/* �m�[�h���牺�ʑw��ID���擾 */
		Node node = getNode(nodeID);
		Integer lowerLayerNodeID = node.getLowerID();

		return lowerLayerNodeID;
	}

	/**
	 * �����h�}�[�N�܂ł̃X�e�b�v�����擾���܂��B
	 * @param Integer currentNodeID ���ݑw�ł̏��
	 * @return int                  �X�e�b�v��
	 */
	private int getToLandmarkStep(Integer nodeID) {
		if(nodeID == null) {
			return -1;
		}
		/* �m�[�h�̗L�������`�F�b�N���A�����X�V */
		renewUpperIDAndStep(nodeID);
		Node node = getNode(nodeID);
		int step = node.getToLandmarkStep();
		return step;
	}


	/**
	 * ��ʑw�̏�Ԃ��X�V����Ă��Ȃ����`�F�b�N�A�X�V����Ă���ΐV�������
	 * �ɏC�����A���̃m�[�hID���擾
	 * @param Integer nodeID  ���݂̑w�ł̃m�[�hID
	 * @return Integer        �C����̏�ʑw�̃m�[�hID
	 */
	private Integer renewUpperIDAndStep(Integer nodeID) {
		Node node = getNode(nodeID);
		/* ��ʑw��ID���擾 */
		Integer upperLayerNodeID = node.getUpperID();

		if(upperLayerNodeID == null) {
			return null;
		}

		/* ��ʑw�̃m�[�h���擾 */
		Node upperLayerNode = upperLayerAgent.getNode(upperLayerNodeID);

		/* ��ʑw�̃m�[�h�̗L�������`�F�b�N �����̏ꍇ�͏����Q�Ɛ�ɏC��*/
		boolean validty = upperLayerNode.isValid();
		if( validty == false ) {
			// 2000.11.30 �C��
			// �����h�}�[�N���폜����Ă���ꍇ�̓Z�O�����g���̏�Ԃ�
			// �ǂ̃Z�O�����g�ɂ������Ȃ��悤�ɂ���
			node.setUpperIDAndStep(null, -1);
			upperLayerNodeID = null;
			/* �Q�Ɛ�ɏ���u������ */
//			Integer newUpperNodeID = upperNode.getReferenceNodeID();
//			int newLandmarkStep = upperNode.getRefarenceStep();
//			int nowStep = currentNode.getToLandmarkStep();
//			node.setUpperIDAndStep(newUpperNodeID,
//			        nowStep+newLandmarkStep);
//			/* �X�V��̏�ʑw��ID�ɂ��Ă��`�F�b�N */
//			upperLayerNodeID = renewUpperIDAndStep(nodeID);
		}
		return upperLayerNodeID;
	}


	/**
	 * �����̃m�[�hID���珇�����ɒ��ڈړ��\�ȃm�[�h��ID�����X�g�Ŏ擾���܂��B
	 * @param Integer nodeID �m�[�hID
	 * @return LinkedList    �����̃m�[�hID���珇�����ɒ��ڈړ��\�ȃm�[�hID
	 *                       �̃��X�g
	 */
	private LinkedList getForwardNodeIDList(Integer nodeID) {
		Node node = getNode(nodeID);
		LinkedList forwardNodeIDList = node.getForwardNodeIDList();
// �����h�}�[�N�폜�̏����ɂ��Ă͕ۗ���
		/* ���X�g���̍폜���ꂽ�m�[�hID���Q�Ɛ�ɒu������ */
//		renewNodeList(forwardNodeIDList);
		/* ����Ɠ���ID�����X�g���ɂ���ꍇ�͍폜 */
		node.removeSameForwardNodeID();
		/* �u�������Ȃ��ŁA�폜�̂ݍs�Ȃ� */
		ListIterator li = forwardNodeIDList.listIterator();
		while(li.hasNext()) {
			Integer forwardNodeID = (Integer)li.next();
			Node forwardNode = getNode(forwardNodeID);
			if(forwardNode.isValid() == false) {
				li.remove();
			}
		}
		return forwardNodeIDList;
	}

	/**
	 * �����̃m�[�hID����t�����ɒ��ڈړ��\�ȃm�[�h��ID�����X�g�Ŏ擾���܂��B
	 * @param Integer nodeID �m�[�h��ID
	 * @return LinkedList    �����̃m�[�hID����t�����ɒ��ڈړ��\�ȃm�[�hID
	 *                       �̃��X�g
	 */
	private LinkedList getInverseNodeIDList(Integer nodeID) {
		Node node = getNode(nodeID);
		LinkedList inversNodeIDList = node.getInverseNodeIDList();
// �����h�}�[�N�폜�̏����ɂ��Ă͕ۗ���
		/* ���X�g���̍폜���ꂽ�m�[�hID���Q�Ɛ�ɒu������ */
//		renewNodeList(inversNodeIDList);
		/* ����Ɠ���ID�����X�g���ɂ���ꍇ�͍폜 */
		node.removeSameInverseNodeID();
		/* �u�������Ȃ��ŁA�폜�̂ݍs�Ȃ� */
		ListIterator li = inversNodeIDList.listIterator();
		while(li.hasNext()) {
			Integer inverseNodeID = (Integer)li.next();
			Node inverseNode = getNode(inverseNodeID);
			if(inverseNode.isValid() == false) {
				li.remove();
			}
		}
		return inversNodeIDList;
	}

	/**
	 * ���X�g���̊e��Ԃ��`�F�b�N���A�����ȃm�[�h�ɂ��Ă͎Q�Ɛ�̃m�[�h��
	 * �u�������܂��B
	 * @param LinkedList nodeIDList �m�[�hID�̃��X�g
	 */
	private void renewNodeIDList(LinkedList nodeIDList) {
		ListIterator li = nodeIDList.listIterator();
		while(li.hasNext()) {
			Integer nodeID = (Integer)li.next();
			nodeID = renewNodeID(nodeID);
			li.set(nodeID);
		}
	}

	/**
	 * �m�[�h�̗L�������`�F�b�N���A�����Ȃ�ID���Q�Ɛ��ID�ɒu�������܂��B
	 * @param Integer nodeID  �L�������`�F�b�N����m�[�hID
	 * @return Integer        �L���ȃm�[�h�ɏC����̃m�[�hID
	 */
	private Integer renewNodeID(Integer nodeID) {
		Node node = getNode(nodeID);
		if(node.isValid() == false) {
			nodeID = node.getReferenceNodeID();
			/* �Q�Ɛ�̃m�[�hID�ɂ��Ă��L�������`�F�b�N */
			renewNodeID(nodeID);
		}
		return nodeID;
	}

	///////////////////////////////////////////////////////////////////////////
	// �����̕��� 2001.01.29 miyamoto

	/**
	 * �F�m�����̊w�K���s�Ȃ��܂��B
	 * @param Integer id_S ���݂̃m�[�hID
	 */
	public void learn(Integer id_S) {

		/* �t���O�ɂ��w�K�𐧌� */
		if(learningFlag) {
			/* ��Ԃ��ω����Ă��Ȃ���Ίw�K���s�Ȃ�Ȃ� */
			if( (id_S != null) && ( (stateBuffer.size() == 0) ||
			        (!id_S.equals(stateBuffer.getLast())) ) ) {
				/* ���̑w�̊w�K */
//				System.out.println();
//				System.out.println("[layerID:" + layerID + "] learn");
//				System.out.println("  CurrentNodeID " + id_S);
				learning(id_S);

				/* ���s�����Ɋւ������ݒ� */
				execInfo.setNodeID(id_S);

				/* ��ʑw������Ώ�ʑw�̊w�K */
				if(upperLayerAgent != null) {
					/* ��Ԃ�ϊ� */
					Integer id_Su = getUpperLayerNodeID(id_S);
					upperLayerAgent.learn(id_Su);
				}
			}
		}
	}


	/**
	 * ���s�������s�Ȃ��܂��B
	 * @param Integer id_S ���݂̃m�[�hID
	 * @param Integer id_G �S�[���̃m�[�hID
	 * @return Integer     ���̃m�[�hID
	 */
	public Integer exec(Integer id_S, Integer id_G) {

		/* ���݂̏�Ԃ��Ȃ���Ώ������Ȃ� */
		/* �w�K���\���łȂ���ԂŊw�K���~�����ꍇ���� */
		if( id_S == null) {
//			System.out.println("state == null");
			return null;
		}

		/* ��ʕ\�� */
//		System.out.println("");
//		System.out.println("[layerID:" + layerID + "] exec");
//		System.out.println("  CurrentNodeID " + id_S);
//		System.out.println("  GoalNodeID    " + id_G);

		/* ���s�����Ɋւ������ݒ� */
		execInfo.setGoalNodeID(id_G);

		/* �T�������N���A */
		/* (���̑w���̂��g�p���Ȃ��ꍇ�̃N���A�����͂ǂ����邩�H) */
		goalSearchInfo.clear();
		// 2001.04.10 �ǉ� miyamoto
		// �S�[���̏�Ԃ������ꍇ�Ɉȉ��̕ϐ��̒l���N���A���ꂸ�A
		// �ȑO�̒l��goalSearchInfo�ɐݒ肳��Ă��܂�
		goalSearchCDLngth = null;
		goalSearchLngth = 0;
		goalSearchNum = 0;

		/* ��ʑw���� */
		if( upperLayerAgent != null ) {
			/* ��ʑw�̏�Ԃɕϊ� */
			Integer id_Su = getUpperLayerNodeID(id_S);
			Integer id_Gu = getUpperLayerNodeID(id_G);

			/* ��ʑw�̌��݂̏�Ԃ��Ȃ���Ώ������Ȃ� */
			/* �t�F�[�Y�����ɂ�蔭������󋵂ɑΉ� */
			if(id_Su == null) {
				return null;
			}

			/* �S�[���̕ω���\�킷�t���O */
			boolean goalChangeFlag = true;

			/* ��ʑw���p�̏��� */
			if( id_Gu != null ) {
				/* �S�[���̕ω����`�F�b�N */
				if( id_Gu.equals(id_Gu0) ) {
					goalChangeFlag = false;
				}

				if( id_Su.equals(id_Gu) ) {
					/*
					 * ��ʑw�̏��(Su)���S�[��(Gu)�ɓ��B�����ꍇ��
					 * �g�p���Ȃ�
					 */
					useUpperFlag = false;
				}else if(goalChangeFlag) {
					/*
					 *�S�[��(Gu)���ύX���ꂽ�ꍇ�͎g�p����
					 */
					useUpperFlag = true;
				}
			}else {
				/* ��ʑw�̃S�[�����Ȃ���Ύg�p���Ȃ� */  
				useUpperFlag = false;
			}

			/* �P�T�C�N���O�̃S�[����ێ� */
			id_Gu0 = id_Gu;

			if(useUpperFlag) {
				/* ���g�̑w�̐󂢒T�������ŃS�[��(G)��T�� */
				Integer id_D1 = getNextNodeID(id_S, id_G, shallowSearchLngth);
				/* �T������ݒ� */
				goalSearchInfo.setGoalSearchInfo(0, id_D1, goalSearchCDLngth,
				        goalSearchLngth, goalSearchNum);
//System.out.println("");
//System.out.println(" LayerID " + layerID + " [id_D1]");
//System.out.println("  id_S " + id_S);
//System.out.println("  id_G " + id_G);
//System.out.println("  goalSearchCDLngth " + goalSearchCDLngth);
//System.out.println("  goalSearchLngth   " + goalSearchLngth);
//System.out.println("  SubgoalNodeID id_D1 " + id_D1);
				if(id_D1 != null) {
					/* Vu0���N���A */
					id_Vu0 = null;
					/* ���s�����Ɋւ������ݒ� */
					execInfo.setNextNodeID(id_D1, 0, null, false);
					return id_D1;
				}

				/*
				 * �ȉ��̂��Âꂩ�̏ꍇ�V����Vu���擾����B
				 * �E�O�T�C�N���̏�ʑw�̎��s�����ɂ��T�u�S�[��(Vu0)
				 *   ���Ȃ�
				 * �E�O�T�C�N���̏�ʑw�̎��s�����ɂ��T�u�S�[��(Vu0)
				 *   �ɓ��B
				 * �E��ʑw�̃S�[��(Gu)���O�T�C�N���̏�ʑw�̃S�[��
				 *   (Gu0)�ƈقȂ�
				 */
				if( ( !id_Su.equals(id_Vu0) ) && ( id_Vu0 != null) 
				        && (!goalChangeFlag) ) {

					/* V0��T�� */
					Integer id_V0 = upperLayerAgent.getLowerLayerNodeID(
					        id_Vu0);
					Integer id_D2 = getNextNodeID(id_S, id_V0,
					        deepSearchLngth);
					/* �T������ݒ� */
					goalSearchInfo.setGoalSearchInfo(1, id_D2,
					        goalSearchCDLngth, goalSearchLngth, goalSearchNum);
//System.out.println("");
//System.out.println(" LayerID " + layerID + " [id_D2]");
//System.out.println("  id_S " + id_S);
//System.out.println("  id_V0 " + id_V0);
//System.out.println("  goalSearchCDLngth " + goalSearchCDLngth);
//System.out.println("  goalSearchLngth   " + goalSearchLngth);
//System.out.println("  SubgoalNodeID id_D2 " + id_D2);
					if(id_D2 != null) {
						/* ���s�����Ɋւ������ݒ� */
						execInfo.setNextNodeID(id_D2, 1, id_V0, false);
						return id_D2;
					}
				}
				/* Vu���擾���AV��T�� */
				id_Vu0 = upperLayerAgent.exec(id_Su, id_Gu);
				Integer id_V0 = upperLayerAgent.getLowerLayerNodeID(id_Vu0);
				Integer id_D3 = getNextNodeID(id_S, id_V0,
				        deepSearchLngth);
				/* �T���̏��ݒ� */
				goalSearchInfo.setGoalSearchInfo(2, id_D3, goalSearchCDLngth,
				        goalSearchLngth, goalSearchNum);
//System.out.println("");
//System.out.println(" LayerID " + layerID + " [id_D3]");
//System.out.println("  id_S " + id_S);
//System.out.println("  id_V0 " + id_V0);
//System.out.println("  goalSearchCDLngth " + goalSearchCDLngth);
//System.out.println("  goalSearchLngth   " + goalSearchLngth);
//System.out.println("  SubgoalNodeID id_D3 " + id_D3);
				if(id_D3 != null) {
					/* ���s�����Ɋւ������ݒ� */
					execInfo.setNextNodeID(id_D3, 2, id_V0, true);
					return id_D3;
				}
			}
		}

		/*
		 * ��ʑw�𗘗p���Ȃ��A�܂��͏�ʑw�𗘗p���Ď��̏�Ԃ��擾�ł��Ȃ�
		 * �ꍇ��V0��ێ����Ȃ�
		 */
		id_Vu0 = null;

		/* �S�[���ɂ��Đ[���T���������s�Ȃ� */
		Integer id_D4 = getNextNodeID(id_S, id_G, deepSearchLngth);
		/* �T���̏��ݒ� */
		goalSearchInfo.setGoalSearchInfo(3, id_D4, goalSearchCDLngth,
		        goalSearchLngth, goalSearchNum);
//System.out.println("");
//System.out.println(" LayerID " + layerID + " [id_D4]");
//System.out.println("  id_S " + id_S);
//System.out.println("  id_G " + id_G);
//System.out.println("  goalSearchCDLngth " + goalSearchCDLngth);
//System.out.println("  goalSearchLngth   " + goalSearchLngth);
//System.out.println("  SubgoalNodeID id_D4 " + id_D4);
		if(id_D4 != null) {
			/* ���s�����Ɋւ������ݒ� */
			execInfo.setNextNodeID(id_D4, 3, null, false);
			// 2001.09.06 �ǉ� miyamoto
			// D4�̏������s�Ȃ�����S�[�����B�܂�D4�̏������s�Ȃ�
			useUpperFlag = false;
			return id_D4;
		}

		return null;
	}


	/**
	 * ���m��Ȃ���Ԃֈړ����邽�߂̒��ڈړ��\�ȏ�Ԃ��擾���܂��B
	 * @param Integer id_S ���݂̃m�[�hID
	 * @return Integer     ���̃m�[�hID
	 */
	public Integer novelSearch(Integer id_S) {

		/* ��ʕ\�� */
//		System.out.println();
//		System.out.println("[layerID:" + layerID + "] novelSearch");
//		System.out.println("  CurrentNodeID " + id_S);
//		System.out.println("  NodeID old" + id_S0);
//		System.out.println("  node num" + nodes.size());
//		System.out.println("  F0 " + id_F0 );
//		System.out.println("  FVu0 " + id_FVu0 );

		/* ��Ԃ̕ω����Ȃ���΃J�E���g�E���Z�b�g���������Ȃ� */
		if(!id_S.equals(id_S0)) {
			/* ���݂̏�Ԃւ̈ړ��񐔂��`�F�b�N */
			Node node = getNode(id_S);
			if(node.getVisitCount() > 0) {
				/* ���łɈړ��ς݂̏�ԂȂ�J�E���g */
				familiarCount++;
			}else {
				/* �V������ԂȂ猻�݂̑w�Ƃ���ȏ�̑w�̃J�E���^�����Z�b�g */
				resetUpperAndThisLayerFamiliarCount();
			}
		}

//		System.out.println("  familiarCount " + familiarCount);

		Integer id_D = novelSearchCore(id_S);

		/* D���擾�ł��Ă���ꍇ�̓J�E���g�����Z�b�g */
		if(id_D != null) {
			resetFamiliarCount();
		}

		/* �O�T�C�N���̏�Ԃ�ێ� */
		id_S0 = id_S;

		return id_D;
	}


	/**
	 * �V�K�T�������̎�p��
	 * ���m��Ȃ���Ԃֈړ����邽�߂̒��ڈړ��\�ȏ�Ԃ��擾���܂��B
	 * @param Integer id_S ���݂̏��
	 * @return Integer     ���̏��
	 */
	private Integer novelSearchCore(Integer id_S) {

		/* �@ �O�T�C�N���̐V�K�T���̃T�u�S�[����(F0)�ɓ��B���Ă��Ȃ� */
		if( (id_F0 != null) && (!id_S.equals(id_F0)) ) {
			/* F0��T�� */
			Integer id_D8 = getNextNodeID(id_S, id_F0, 1);
			if(id_D8 != null) {
				return id_D8;
			}
		}
		/* ���B�ł��Ȃ��ꍇ�̓N���A */
		id_F0 = null;

		/* ��ʑw������ꍇ�̏��� */
		if( upperLayerAgent != null ) {
			/* ��ʑw�̏�Ԃɕϊ� */
			Integer id_Su = getUpperLayerNodeID(id_S);

			/* �A ��ʑw�̏��Su��FVu0�ɓ��B���Ă��Ȃ� */
			if( (!id_Su.equals(id_FVu0)) && (id_FVu0 != null) ) {
				/* FVu0�ɑΉ�����FV0��T�� */
				Integer id_FV0 = upperLayerAgent.getLowerLayerNodeID(
				        id_FVu0);
				Integer id_D5 = getNextNodeID(id_S, id_FV0,
				        deepSearchLngth);
				if(id_D5 != null) {
					return id_D5;
				}
			}else {
			/* �B Su��FVu0�ɓ��B �܂��� FVu0���Ȃ� */
				/* �V����FVu���擾���A�Ή�����FV��T�� */
				Integer id_FVu = upperLayerAgent.novelSearch(id_Su);
				Integer id_FV = upperLayerAgent.getLowerLayerNodeID(id_FVu);
				Integer id_D6 = getNextNodeID(id_S, id_FV,
				        deepSearchLngth);
				if(id_D6 != null) {
					/* �V���Ɏ擾�����T�u�S�[����ێ� */
					id_FVu0 = id_FVu;
					return id_D6;
				}
			}
		}
		/* ���B�ł��Ȃ��ꍇ�̓N���A */
		id_FVu0 = null;

		/* �C �m���Ă����Ԃ��A�����A�J�E���^�����܂��Ă��� */
		if( familiarCount > maxFamiliarCount ) {
			/* �V�K�T�������ŃT�u�S�[��(F)���擾 */
			Integer id_F = getNovelNodeID(id_S);
			/* F��T�� */
			Integer id_D7 = getNextNodeID(id_S, id_F, 1);
			if(id_D7 != null) {
				/* �V���Ɏ擾����F��ێ� */
				id_F0 = id_F;
				return id_D7;
			}
		}

		return null;
	}

	// 2001.08.14 �ǉ� miyamoto
	/**
	 * ��� a ������ b �ւ̓��B�\���𒲂ׂ܂��B
	 * @param Integer a
	 * @param Integer b
	 * @return boolean true ���B�\ false ���B�s�\
	 */
	public boolean isReach(Integer a, Integer b) {
		boolean isReach = false;
		Node node = getNode(a);
		LinkedList forwardNodeIDList = node.getForwardNodeIDList();
		if( searchReachableState(forwardNodeIDList, b) ) {
			return true;
		}

		if( isReach == false ) {
			if( upperLayerAgent != null ) {
				Integer upperA = getUpperLayerNodeID(a);
				Integer upperB = getUpperLayerNodeID(b);
				isReach = upperLayerAgent.isReach(upperA, upperB);
			}else {
				// �[���T��
//				Integer subgoal = exec(a, b);
//				if(subgoal != null) {
//					isReach = true;
//				}
				if( isReachSearchDeep(forwardNodeIDList, b) ) {
					isReach = true;
				}
			}
		}

		return isReach;
	}

	Hashtable checkTable = null;
	private boolean isReachSearchDeep(LinkedList list,
	        Integer target) {
		checkTable = new Hashtable();
		for(int i = 0; i < deepSearchLngth; i++) {
			LinkedList nextList = getNextList(list);
			boolean isReach = searchReachableState(nextList, target);
			if(isReach) {
				return true;
			}
			list = nextList;
		}
		return false;
	}

	/**
	 * �����ŗ^����ꂽ���X�g���̏�Ԃ���A�����ŗ^����ꂽ�ړI�ɓ��B�\��
	 * ��Ԃ����邩�T�����܂��B
	 */
	private boolean searchReachableState(LinkedList list, Integer target) {
		ListIterator li = list.listIterator();
		while(li.hasNext()) {
			Integer id = (Integer)li.next();
			Node node = getNode(id);
			int distance = node.getCognitiveDistance(target);
			if(distance != -1) {
				return true;
			}
		}
		return false;
	}

	private LinkedList getNextList(LinkedList list) {
		LinkedList nextList = new LinkedList();
		ListIterator li = list.listIterator();
		while(li.hasNext()) {
			Integer id = (Integer)li.next();
			Node node = getNode(id);
			LinkedList forwardNodeIDList = node.getForwardNodeIDList();
			ListIterator forwardNodeIDListIterator
			         = forwardNodeIDList.listIterator();
			while(forwardNodeIDListIterator.hasNext()) {
				Integer forwardNodeID
				        = (Integer)forwardNodeIDListIterator.next();
				if(checkTable.get(forwardNodeID) == null) {
					checkTable.put(forwardNodeID, forwardNodeID);
					nextList.add(forwardNodeID);
				}
			}
		}
		return nextList;
	}


	// �����܂�
	///////////////////////////////////////////////////////////////////////////

	/**
	 * nodeID����goalNodeID�ֈړ�����̂ɁA�ŒZ�̋����������ڈړ��\��
	 * ��Ԃ��擾���܂��B
	 * �ړ��\�ȏ�Ԃ�������Ȃ����maxSearchLength�Ŏw�肳�ꂽ�[���܂�
	 * �T�����s�Ȃ��܂��B
	 * @param Integer nodeID       �m�[�hID
	 * @param Integer goalNodeID   �S�[����ID
	 * @param int maxSearchLength  �T�����s�Ȃ��ő�̐[��
	 * @return Integer             ���̏�Ԃ̃m�[�hID
	 */
	private Integer getNextNodeID(Integer nodeID, Integer goalNodeID,
	        int maxSearchLength) {
		/* �S�[���̏�Ԃ��Ȃ���Ώ������Ȃ� */
		if( goalNodeID == null ) {
			return null;
		}

		/*
	 	 * �S�[���ւ̍ŒZ����������ԂƁA���̏�Ԃ�����StateList���擾
		 */
		Object[] selectedStateInfo = getNextNodeInfo(nodeID, goalNodeID,
		        maxSearchLength);

		/*
		 * �擾������Ԃ����ڈړ��\�ȏ�ԂłȂ���΁A���̏�Ԃɑ΂��Ă�
		 * �ړ��\�ȏ�Ԃ��擾
		 */
		Integer nextNodeID = pathLearning(selectedStateInfo, goalNodeID);

		return nextNodeID;
	}

private Random randomSearch = new Random(0);

	/**
	 * �S�[���ւ̍ŒZ�������m�[�h�Ɋւ�������擾���܂��B
	 * �T����minSearchLngth���ōs�Ȃ��A�����ɃS�[���ւ̍ŒZ����������Ԃ�
	 * �Ȃ��ꍇ�AmaxSearchLngth�܂ŒT���������s�Ȃ��܂��B
	 * @param Integer nodeID       �m�[�hID
	 * @param Integer goalNodeID   �S�[���̃m�[�hID
	 * @return Object[]            �ŒZ�̋��������m�[�h�Ɋւ�����
	 *                             Object[0] nodeID
	 *                             Object[1] Distance
	 *                             Object[2] StateList
	 */
	private Object[] getNextNodeInfo(Integer nodeID, Integer goalNodeID,
	        int maxSearchLength) {

		/* �����Ԃ��ēx�������Ȃ����߂̃e�[�u�� */
		Hashtable checkTable = new Hashtable();

		/* ������e�[�u���ɐݒ� */
		checkTable.put(nodeID, nodeID);

		/* �T�����s�Ȃ��m�[�h�̃��X�g�̔z�� */
		StateList[] stateListArray = null;

		/* �I�����ꂽ�m�[�h�̏���ݒ� */
		Object[] selectedObj = null;
		int selectedLngth = 0;
		int currentLngth = 0;

		/* minSearchLngth�܂ŒT�� */
//		for( ; currentLngth < minSearchLngth; currentLngth++) {
		// 2001.04.16 �C�� miyamoto
		/* �ŏ��ōs�Ȃ��T���̐[���������_���ɐݒ肷�� */
		int minLoopLngth = minSearchLngth;
		if(minLoopLngth == -1) {
//			// 2001.05.25 �C�� miyamoto �Â��o�[�W������java�ɑΉ�
			int num = randomSearch.nextInt(15);
			if(num < 8) {
				minLoopLngth = 1;
			}else if(num < 12) {
				minLoopLngth = 2;
			}else if(num < 14) {
				minLoopLngth = 3;
			}else {
				minLoopLngth = 4;
			}
//			double num = randomSearch.nextDouble();
//			if(num < 0.51) {
//				minLoopLngth = 1;
//			}else if(num < 0.79) {
//				minLoopLngth = 2;
//			}else if(num < 0.83) {
//				minLoopLngth = 3;
//			}else {
//				minLoopLngth = 4;
//			}
			// �����܂�
		}
		for( ; currentLngth < minLoopLngth; currentLngth++) {
		/* �����܂� */

			/* ���߂�stateListArray�̍쐬�͓���ȏ��� */
			if(stateListArray == null) {
				/* ���ڈړ��\�ȏ�Ԃ̃��X�g���擾 */
				StateList nextNodeIDList = getChildList(nodeID, null,
				        checkTable);
				stateListArray = new StateList[1];
				stateListArray[0] = nextNodeIDList;
			}else {
				/* ����StateList�̔z����擾 */
				stateListArray = getChildListArray(stateListArray, checkTable);
			}

			Object[] obj = getNextNodeInfoFromStateListArray(stateListArray,
			        goalNodeID);
			/* ��r���� */
			if( obj != null ) {
				if( selectedObj == null ) {
					selectedObj = obj;
				}else {
					/* �����̒Z������ݒ� �����ɂ͒T�������[�������� */
					if( ((Integer)obj[1]).intValue() + currentLngth <=
					        (((Integer)selectedObj[1]).intValue()
					        + selectedLngth)){
						selectedLngth = currentLngth;
						selectedObj = obj;
					}
				}
			}
		}

		/*
		 * minSearchFNLngth�ŃS�[���֓��B�\�ȃm�[�h���Ȃ��ꍇ�́A
		 * maxSearchFNdepth�܂ŒT�����s��
		 */
		for( ; currentLngth < maxSearchLength; currentLngth++) {
			if( selectedObj != null ) {
				break;
			}
			/* ���߂�stateListArray�̍쐬�͓���ȏ��� */
			if(stateListArray == null) {
				/* ���ڈړ��\�ȏ�Ԃ̃��X�g���擾 */
				StateList nextNodeIDList = getChildList(nodeID, null,
				        checkTable);
				stateListArray = new StateList[1];
				stateListArray[0] = nextNodeIDList;
			}else {
				/* ����StateList�̔z����擾 */
				stateListArray = getChildListArray(stateListArray, checkTable);
			}

			// 2001.04.20 �ǉ� miyamoto
			/* �T�����郊�X�g��������Ώ��������Ȃ� */
			if(stateListArray.length == 0) {
				break;
			}
			selectedObj = getNextNodeInfoFromStateListArray(stateListArray,
			        goalNodeID);
		}

		/* �e�X�g�p  �S�[���T���Ɋւ������ݒ� */
		/* CD�̒��� */
		if(selectedObj != null) {
			goalSearchCDLngth = (Integer)selectedObj[1];
		}else{
			goalSearchCDLngth = null;
		}
		/* �T���̐[�� */
		goalSearchLngth = currentLngth;
		/* �T��������� */
		goalSearchNum = checkTable.size();

		return selectedObj;
	}

	/* �S�[���T���̏���ێ� �T���󋵂̎擾�p */
	Integer goalSearchCDLngth; /* �T�����ꂽCD�̒��� */
	int goalSearchLngth;       /* �T�����ꂽ�[�� */
	int goalSearchNum;         /* �T��������Ԑ� */

	/**
	 * stateList�̔z�񂩂�S�[���ւ̍ŒZ����������Ԃ��擾���܂��B
	 * @param StateList[] stateListArray StateList�̔z��
	 * @param Integer goalNodeID         �S�[���̃m�[�hID
	 * @return Object[]                   �ŒZ�̋��������m�[�h�Ɋւ�����
	 *                                    Object[0] nodeID
	 *                                    Object[1] Distance
	 *                                    Object[2] StateList
	 */
	private Object[] getNextNodeInfoFromStateListArray(
	        StateList[] stateListArray, Integer goalNodeID) {

		/* �ŒZ�̋���������Ԃ�T�� */
		Object[] wkObj = null;
		StateList selectedList = null;
		for(int i = 0; i < stateListArray.length; i++) {
			Object[] obj = getNextNodeInfoFromStateList(stateListArray[i],
			        goalNodeID);
			if(obj != null) {
				if((wkObj == null) ||
				         ( ((Integer)wkObj[1]).intValue() >
				         ((Integer)obj[1]).intValue()) ){
					wkObj = obj;
					selectedList = stateListArray[i];
				}
			}
		}

		/* �ŒZ����������Ԃ̂��郊�X�g��ǉ� */
		Object[] selectedObj = null;
		if(wkObj != null) {
			selectedObj = new Object[3];
			selectedObj[0] = wkObj[0];
			selectedObj[1] = wkObj[1];
			selectedObj[2] = selectedList;
		}
		return selectedObj;
	}


	/**
	 * stateList����S�[���ւ̍ŒZ����������Ԃ��擾���܂��B
	 * @param StateList stateList   StateList
	 * @param Integer goalNodeID    �S�[���̃m�[�hID
	 * @return Object[]             �ŒZ�̋��������m�[�h�Ɋւ�����
	 *                              Object[0] nodeID
	 *                              Object[1] Distance
	 */
	private Object[] getNextNodeInfoFromStateList(StateList stateList,
	        Integer goalNodeID) {

		/* �I�����ꂽ��Ԃ̐ݒ�p */
		Integer selectedNodeID = null;       /* �ŒZ����������� */
		int shortestDistance = -1;           /* �ŒZ���� */
		Object[] obj = null;

		/* StateList����S�[���ւ̍ŒZ����������Ԃ��擾 */ 
		ListIterator stateIterator
		        = stateList.listIterator();
		while(stateIterator.hasNext()) {
			Integer directAccessNodeID
			        = (Integer)stateIterator.next();
			/* �S�[���Ƃ̋������擾 */
			Node directAccessNode = getNode(directAccessNodeID);
			int distance = directAccessNode.getCognitiveDistance(
			        goalNodeID);
			/* �S�[���ւ̍ŒZ�����ƁA���̏�Ԃ�ێ� */
			if(distance != -1) {
				if((shortestDistance==-1)||(shortestDistance>=distance)) {
					selectedNodeID = directAccessNodeID;
					shortestDistance = distance;
				}
			}
		}

		if(selectedNodeID != null) {
			obj = new Object[2];
			obj[0] = selectedNodeID;
			obj[1] = new Integer(shortestDistance);
		}

		return obj;
	}

	/**
	 * StateList�̔z��̊e�v�f���璼�ڈړ��\�ȃm�[�hID��StateList�̔z���
	 * �擾���܂��B
	 * @param StateList[] stateListArray StateList�̔z��
	 * @return StateList[]   ������StateList�̔z��̊e�v�f���璼�ڈړ��\��
	 *                       StateList�̔z��
	 */
	private StateList[] getChildListArray(StateList[] stateListArray,
	        Hashtable checkTable) {

		/* ���̏�Ԃ̃��X�g�����J�E���g */
		int nextStateCount = 0;
		for(int i = 0; i < stateListArray.length; i++) {
			nextStateCount += stateListArray[i].size();
		}

		/* ���̏�Ԃ̃��X�g���擾 */
		StateList[] nextStateList = new StateList[nextStateCount];
		int n = 0;
		for(int i = 0; i < stateListArray.length; i++) {
			ListIterator il = stateListArray[i].listIterator();
			while( il.hasNext() ) {
				Integer nodeID = (Integer)il.next();
				nextStateList[n] = getChildList(nodeID, stateListArray[i],
				        checkTable);
				n++;
			}
		}

		return nextStateList;
	}

	/**
	 * �w�肳�ꂽ��Ԃ��璼�ڈړ��\�ȏ�Ԃ̃��X�g���擾���܂��B
	 * @param Integer parentNodeID �e�ƂȂ�m�[�hID
	 * @param StateList stateList  �e�m�[�h�̂���StateList
	 * @return StateList           ���ڈړ��\�ȃm�[�hID�̃��X�g(�q���X�g)
	 */
	private StateList getChildList(Integer parentNodeID, StateList stateList, 
	        Hashtable checkTable) {

		/* �������Ɉړ��\�ȏ�Ԃ��擾 */
		LinkedList forwardNodeIDList = getForwardNodeIDList(parentNodeID);
		/* ���łɎg�p�ς݂̃m�[�h�ȊO���g�p */
		LinkedList checkedforwardNodeIDList = new LinkedList();
		ListIterator li = forwardNodeIDList.listIterator();
		while(li.hasNext()) {
			Integer nodeID = (Integer)li.next();
			/* �e�[�u���ɓo�^����Ă��Ȃ���ԂȂ烊�X�g�A�e�[�u���ɓo�^ */
			if(!checkTable.contains(nodeID)) {
				checkedforwardNodeIDList.add(nodeID);
				checkTable.put(nodeID, nodeID);
			}
		}

		/* �V����StateList���쐬 */
		StateList childList = new StateList(checkedforwardNodeIDList,
		        parentNodeID, stateList);

		return childList;
	}


	/**
	 * ���łɈړ��ς݂̏�Ԃւ̘A���ړ��񐔂̃J�E���^�����������܂��B
	 * ��ʑw�̃J�E���^�EupperNextNodeID�EnovelSubgoal�ɂ��Ă�������
	 * ���s�Ȃ��܂��B
	 */
	public void resetUpperAndThisLayerFamiliarCount() {
		resetFamiliarCount();
		id_FVu0 = null;
		id_F0 = null;
		if(upperLayerAgent != null) {
			upperLayerAgent.resetUpperAndThisLayerFamiliarCount();
		}
	}

	/**
	 * ���łɈړ��ς݂̏�Ԃւ̘A���ړ��񐔂̃J�E���^�����������܂��B
	 */
	private void resetFamiliarCount() {
		familiarCount = 0;
	}

	/**
	 * �����̃m�[�h�̂b�c���ł����Ƃ��ړ��񐔂̏��Ȃ���Ԃֈړ����܂��B
	 * @param Integer nodeID ���݂̃m�[�hID
	 * @return Integer       �ł��ړ��񐔂̏��Ȃ��m�[�h��ID
	 */
	private Integer getNovelNodeID(Integer nodeID) {

		/* �ł��ړ��񐔂̏��Ȃ��m�[�hID */
		Integer selectedNodeID = null;
		/* �ŏ��̈ړ��� */
		int minVisitCount = 0;

		/* �b�c�̃L�[�ƂȂ��Ă���m�[�h�̂h�c���擾 */
		Node node = getNode(nodeID);
		LinkedList ll = node.getCDKeys();
		/* �e�m�[�h�̈ړ��񐔂��`�F�b�N */
		ListIterator li = ll.listIterator();
		while(li.hasNext()) {
			Integer cdNodeID = (Integer)li.next();
			Node cdNode = getNode(cdNodeID);
			int visitCount = cdNode.getVisitCount();
			/* �ł��ړ��񐔂̏��Ȃ���Ԃ�ێ� */
			if( (selectedNodeID == null) || (minVisitCount > visitCount) ) {
				selectedNodeID = cdNodeID;
				minVisitCount = visitCount;
			}
		}
		return selectedNodeID;
	}


	//////////////////////////////////////////////////////////////////////////
	// �w�K

	/**
	 * CognitiveDistance�̊w�K���s���܂��B
	 * @param Integer nodeID �m�[�hID
	 */
	private void learning(Integer nodeID) {

		/* �m�[�h�̎擾 */
		Node node = getNode(nodeID);

		/* �o�b�t�@�Ɍ��݂̃m�[�hID��ǉ� */
		stateBuffer.add(nodeID);

		/* �ړ��񐔂̃J�E���g */
		int sbSize = stateBuffer.size();
		if( sbSize >= 2) {
			/*
			 * �ړ���̃m�[�h�ɂ��Ă̏����K�v�Ȃ��߁A��O�̃m�[�h
			 * �ɂ��ď������s�Ȃ�
			 */
			Integer oldNodeID = (Integer)stateBuffer.get(sbSize - 2);
			Node oldNode = getNode(oldNodeID);
			/* �ړ��񐔂̃J�E���g�ƈړ���̐ݒ� */
			oldNode.countVisitCount(nodeID);
		}

		/*
		 * stateBuffer���̊e��ԂɁA���݂̏�Ԃ��L�[�ɂ��̋�����o�^
		 */
		int distance = 1;
		ListIterator stateBufferIterater
		        = stateBuffer.listIterator(stateBuffer.size());
		stateBufferIterater.previous();
		while(stateBufferIterater.hasPrevious()) {

			/* fromNode�̎擾 */
			Integer fromNodeID = (Integer)stateBufferIterater.previous();
			Node fromNode = getNode(fromNodeID);

			/* �b�c�̊w�K */
			fromNode.setCognitiveDistance(nodeID, distance);

			/*
			 * �m�[�h���璼�ڈړ��\�ȏ�Ԃɂ��Ă�CognitiveDistance���w�K
			 */
			LinkedList forwardNodeIDList = getForwardNodeIDList(nodeID);
			ListIterator forwardNodeIDListIterater
			        = forwardNodeIDList.listIterator(0);
			while(forwardNodeIDListIterater.hasNext()) {
				Integer forwardNodeID
				        = (Integer)forwardNodeIDListIterater.next();
				fromNode.setCognitiveDistance(forwardNodeID, distance+1);
			}

			/* ������1�̃m�[�hID��MSM�ɓo�^ */
			if(distance == 1){
				/* �������ɒ��ڈړ��\��Ԃ��w�K */
				fromNode.setForwardNode(nodeID);
				/* �t�����ɒ��ڈړ��\�ȏ�Ԃ��w�K */
				node.setInverseNode(fromNodeID);
			}

			distance++;

		}

		/* �t���O�ɂ�胉���h�}�[�N�̊w�K�𐧌� �e�X�g�p */
		if(landmarkLearningFlag) {
			/* ��ʑw������΃����h�}�[�N�̊w�K */
			if(upperLayerAgent != null) {
				/* Landmark�̊w�K */
				landmarkLearning(nodeID);
			}
		}

	}


	/**
	 * Landmark�̊w�K���s���܂��B
	 * @param Integer nodeID �m�[�hID
	 */
	private void landmarkLearning(Integer nodeID) {

		/* �߂��̃����h�}�[�N��T���A����ID�Ƃ����܂ł̋������擾 */
		Object[] nearestLandmarkAndStep = getNearestLandmark(nodeID);
		Integer nearestLandmarkID = (Integer)nearestLandmarkAndStep[0];
		int shortestStep = ((Integer)nearestLandmarkAndStep[1]).intValue();

		/* ���݂̃����h�}�[�N��ID�Ƃ����܂ł̃X�e�b�v�� */
		Integer currentLandmarkID = getUpperLayerNodeID(nodeID);
		int currentStep = getToLandmarkStep(nodeID);

		Node node = getNode(nodeID);
		if(nearestLandmarkID == null) {
			if(currentStep == -1) {
				/* �߂��Ƀ����h�}�[�N���Ȃ��A����ɂ��ݒ肳��Ă��Ȃ��ꍇ */
				/* ����������h�}�[�N�ɐݒ� */
				Integer newID = new Integer(segmentCount);
				node.setUpperIDAndStep(newID, 0);
				/* ��ʑw�̃m�[�h���쐬 */
				upperLayerAgent.newNode(nodeID);
				segmentCount++;
			}
		}else{
			if(currentStep == -1) {
				/* �߂��Ƀ����h�}�[�N������A����ɐݒ肳��Ă��Ȃ��ꍇ */
				/* �ŒZ�̃����h�}�[�N��ݒ� */
				node.setUpperIDAndStep(nearestLandmarkID, shortestStep);
			}else if(currentStep == 0) {
				/* �߂��Ƀ����h�}�[�N������A����ɂ��ݒ肳��Ă���ꍇ */
				/* �����h�}�[�N�폜�E�߂����֒u������ */
				if(shortestStep <= minSegmentSize) {
//					// 2000.11.30 �C�� �����h�}�[�N�폜���ɕʂ̃Z�O�����g�ւ�
//					// �������s�Ȃ�Ȃ�
//					/* �����h�}�[�N(��ʑw)�̍폜���� */
//					Integer upperID = getUpperLayerNodeID(nodeID);
//					// �u������
//					upperLayerAgent.deleteNode(upperID, nearestLandmarkID,
//					        shortestStep);
//					// �폜�̂�
//					boolean b = upperLayerAgent.deleteNode(upperID, null, -1);
//					/*
//					 * ��ʑw�̍폜������ɍs�Ȃ�ꂽ�ꍇ�͐V���ȍŒZ��
//					 * �����h�}�[�N��ݒ�
//					 */
//					if(b == true) {
//						node.setUpperIDAndStep(nearestLandmarkID,
//						        shortestStep);
//					}
				}
			}else{
				/*
				 * �߂��Ƀ����h�}�[�N�����邪�A���݂͕ʂ̃����h�}�[�N���ݒ�
				 * ����Ă���ꍇ
				 */
				/* �߂����֒u������ */
				if(currentStep > shortestStep) {
					/* �ŒZ�̃����h�}�[�N��ݒ� */
					node.setUpperIDAndStep(nearestLandmarkID,
					        shortestStep);
				}
			}
		}
	}

	/* �����h�}�[�N��ݒ肹���ɏ�Ԃ�ێ����Ă������߂̃o�b�t�@ */
	LinkedList noLandmarkNodeIDBuffer = new LinkedList();
//
//	/**
//	 * Landmark�̊w�K���s���܂��B(ForwardModel�g�p)
//	 * @param Integer nodeID �m�[�hID
//	 */
//	private void landmarkLearning(Integer nodeID) {
//
//		/* �߂��̃����h�}�[�N��T���A����ID�Ƃ����܂ł̋������擾 */
//		Object[] nearestLandmarkAndStep = getNearestLandmark(nodeID);
//		Integer nearestLandmarkID = (Integer)nearestLandmarkAndStep[0];
//		int shortestStep = ((Integer)nearestLandmarkAndStep[1]).intValue();
//
//		/* ���݂̃����h�}�[�N��ID�Ƃ����܂ł̃X�e�b�v�� */
//		Integer currentLandmarkID = getUpperLayerNodeID(nodeID);
//		int currentStep = getToLandmarkStep(nodeID);
//
//		/* ���݂̏�Ԃ������h�}�[�N */
//		if(currentStep == 0) {
//			/* �o�b�t�@���̏�ԂɌ��݂̏�Ԃ������h�}�[�N�Ƃ��Đݒ� */
//			setLandmarkToBuffer(nodeID, currentStep+1);
//		/* ���݂̏�Ԃɉ����ݒ肳��ĂȂ� */
//		}else if(currentStep == -1) {
//			/* �߂��Ƀ����h�}�[�N�L */
//			if(nearestLandmarkID != null) {
//				/* �o�b�t�@�̏�� */
//				if(noLandmarkNodeIDBuffer.size() >= maxSegmentSize) {
//					/*
//					 * ���݂̏�Ԃ������h�}�[�N�ɂ��A�o�b�t�@���̏�Ԃ�
//					 * �����h�}�[�N�Ƃ��Đݒ�
//					 */
//					makeLandmark(nodeID);
//					setLandmarkToBuffer(nodeID, 1);
//				}else {
//					/* �o�b�t�@�T�C�Y�Ƌ����̃`�F�b�N */
//					if( shortestStep + noLandmarkNodeIDBuffer.size()
//					        > maxSegmentSize) {
//						/*
//						 * ���݂̏�Ԃ������h�}�[�N�ɂ��A�o�b�t�@���̏�Ԃ�
//						 * �����h�}�[�N�Ƃ��Đݒ�
//						 */
//						makeLandmark(nodeID);
//						setLandmarkToBuffer(nodeID, 1);
//					}else {
//						/*
//						 * ���݂̏�ԁA�o�b�t�@���̏�ԂɒT���ł���
//						 * �����h�}�[�N��ݒ�
//						 */
//						Node node = getNode(nodeID);
//						node.setUpperIDAndStep(nearestLandmarkID,
//						        shortestStep);
//						setLandmarkToBuffer(nearestLandmarkID, shortestStep+1);
//					}
//				}
//			}else {
//				/* �o�b�t�@�̏�� */
//				if(noLandmarkNodeIDBuffer.size() >= maxSegmentSize) {
//					/*
//					 * ���݂̏�Ԃ������h�}�[�N�ɂ��A�o�b�t�@���̏�Ԃ�
//					 * �����h�}�[�N�Ƃ��Đݒ�
//					 */
//					makeLandmark(nodeID);
//					setLandmarkToBuffer(nodeID, 1);
//				}else {
//					/* ���݂̏�Ԃ��o�b�t�@�ɒǉ� */
//					noLandmarkNodeIDBuffer.add(nodeID);
//				}
//			}
//		/* �߂��̃����h�}�[�N���ݒ肳��Ă��� */
//		}else{
//			/* �����h�}�[�N �L */
//			if(nearestLandmarkID != null) {
//				/* �Z�����̃����h�}�[�N�ɒu���� */
//				Integer renewLandmarkID = currentLandmarkID;
//				int renewStep = currentStep;
//				if(currentStep > shortestStep) {
//					Node node = getNode(nodeID);
//					node.setUpperIDAndStep(nearestLandmarkID,
//					        shortestStep);
//					renewLandmarkID = nearestLandmarkID;
//					renewStep = shortestStep;
//				}
//				/* �o�b�t�@�T�C�Y�Ƌ����̃`�F�b�N */
//				if( renewStep + noLandmarkNodeIDBuffer.size()
//				        > maxSegmentSize) {
//					/*
//					 * ���݂̏�Ԃ������h�}�[�N�ɂ��A�o�b�t�@���̏�Ԃ�
//					 * �����h�}�[�N�Ƃ��Đݒ�
//					 */
//					makeLandmark(nodeID);
//					setLandmarkToBuffer(nodeID, 1);
//				}else {
//					/* �X�V��̃����h�}�[�N���o�b�t�@���̏�Ԃɐݒ� */
//					setLandmarkToBuffer(renewLandmarkID, renewStep);
//				}
//			}else {
//				/* �o�b�t�@�T�C�Y�Ƌ����̃`�F�b�N */
//				if( currentStep + noLandmarkNodeIDBuffer.size()
//				        > maxSegmentSize) {
//					/*
//					 * ���݂̏�Ԃ������h�}�[�N�ɂ��A�o�b�t�@���̏�Ԃ�
//					 * �����h�}�[�N�Ƃ��Đݒ�
//					 */
//					makeLandmark(nodeID);
//					setLandmarkToBuffer(nodeID, 1);
//				}else {
//					/* �o�b�t�@���̏�ԂɌ��݂̏�Ԃɐݒ肳��Ă��� */
//					/* �����h�}�[�N�������h�}�[�N�Ƃ��Đݒ�         */
//					setLandmarkToBuffer(currentLandmarkID, currentStep);
//				}
//			}
//		}
//	}

	/**
	 * ����̏�Ԃ������h�}�[�N�ɂ��܂��B
	 * @param Integer nodeID �����h�}�[�N�ɂ����Ԃ�ID
	 */
//	private void makeLandmark(Integer nodeID) {
//		/* ����������h�}�[�N�ɐݒ� */
//		Integer newID = new Integer(segmentCount);
//		Node node = getNode(nodeID);
//		node.setUpperIDAndStep(newID, 0);
//		/* ��ʑw�̃m�[�h���쐬 */
//		upperLayerAgent.newNode(nodeID);
//		segmentCount++;
//	}

	/**
	 * �����h�}�[�N�𖢐ݒ�̃o�b�t�@���̏�ԂɃ����h�}�[�N��ݒ肵�܂�
	 * @param Integer landmarkID �����h�}�[�N��ID
	 * @param int step           �o�b�t�@�̍ŐV�̏�Ԃ��烉���h�}�[�N�܂ł�
	 *                           �X�e�b�v��
	 */
	private void setLandmarkToBuffer(Integer landmarkID, int step) {
		/* ���X�g�̑S��ԂɃ����h�}�[�N��ݒ� */
		ListIterator li = noLandmarkNodeIDBuffer.listIterator();
		while(li.hasPrevious()) {
			Integer nodeID = (Integer)li.previous();
			Node node = getNode(nodeID);
			node.setUpperIDAndStep(landmarkID, step);
			step++;
		}
		/* ���X�g���N���A */
		noLandmarkNodeIDBuffer.clear();
	}

	// �����܂�
	///////////////////////////


	/**
	 * maxSegmentSize���ł����Ƃ��߂��ʒu�̃����h�}�[�N��T�����܂��B
	 * @param Integer nodeID                �m�[�hID
	 * @return Object[] nearestLandmarkInfo �ł��߂������h�}�[�N�Ɋւ�����
	 *                                      Object[0] landmarkID
	 *                                      Object[1] stepNum
	 */
	private Object[] getNearestLandmark(Integer nodeID) {

		/* searchNodeIDList�ɓ���̏�Ԃ��ēx�I�����Ȃ����߂̃e�[�u�� */
		Hashtable checkTable = new Hashtable();

		/* �I�����ꂽlandmark�̏���ݒ� */
		Integer selectedNodeID = null;
		int selectedStep = -1;

		/* �T���͈͓��̃m�[�hID���擾 */
		LinkedList[] searchNodeIDList = new LinkedList[maxSegmentSize];

	// 2001.05.22 �C�� miyamoto Model�̐؂芷�����t���O�ōs�Ȃ�
//// �K�w���̐��\�����p ForwardModel ���g�p
//		searchNodeIDList[0] = getInverseNodeIDList(nodeID);
////		searchNodeIDList[0] = getForwardNodeIDList(nodeID);
//		for(int i = 1; i < maxSegmentSize; i++) {
//// �K�w���̐��\�����p ForwardModel ���g�p
//			searchNodeIDList[i] = getMoveableNodeList(searchNodeIDList[i-1],
//			        false, checkTable);
////			searchNodeIDList[i] =getMoveableNodeList(searchNodeIDList[i-1],
////			        true, checkTable);

		/* �t���O�ɂ�胉���h�}�[�N�T���̌�����؂�ւ� */
		if(!flagLandmarkSearchDirection) {
			/* InverseModel�ł̃Z�O�����g�� */
			searchNodeIDList[0] = getInverseNodeIDList(nodeID);
			for(int i = 1; i < maxSegmentSize; i++) {
				searchNodeIDList[i] = getMoveableNodeList(
				        searchNodeIDList[i-1], false, checkTable);
			}
		}else {
			/* ForwardModel�ł̃Z�O�����g�� */
			searchNodeIDList[0] = getForwardNodeIDList(nodeID);
			for(int i = 1; i < maxSegmentSize; i++) {
				searchNodeIDList[i] = getMoveableNodeList(
				        searchNodeIDList[i-1], true, checkTable);
			}
		}
		// �����܂�

		/* �ŒZ�̏�Ԃ�T�� */
		for(int i = 0; i < maxSegmentSize; i++) {
			/* ���X�g���̊e��Ԃɂ��ă����h�}�[�N�܂ł̋������`�F�b�N */
			ListIterator lIterator = searchNodeIDList[i].listIterator();
			while(lIterator.hasNext()) {

				/* �����h�}�[�N�܂ł̋������擾 */
				Integer searchNodeID = (Integer)lIterator.next();
				int toLandmarkStep = getToLandmarkStep(searchNodeID);
				/* �����h�}�[�N���ݒ�ς� */
				if( toLandmarkStep != -1 ) {
					/* ��ʑw�������łȂ��� */
					Integer upperNodeID = getUpperLayerNodeID(nodeID);
					Integer upperSearchNodeID
					        = getUpperLayerNodeID(searchNodeID);
					if( (upperNodeID == null) || (!upperNodeID.equals(
					        upperSearchNodeID)) ) {
						/* �T���̐[���̕����X�e�b�v���ɑ��� */
						toLandmarkStep = toLandmarkStep + i + 1;
						/* �����h�}�[�N�܂ł̃X�e�b�v�����T���͈͓� */
						if( toLandmarkStep <= maxSegmentSize ) {
							/* �ŒZ�̋����Ɣ�r */
							if( (selectedStep == -1) || 
							        (selectedStep > toLandmarkStep) ) {
								/* �ŒZ�̃����h�}�[�N�̏���ݒ� */
								selectedNodeID = upperSearchNodeID;
								selectedStep = toLandmarkStep;
							}
						}
					}
				}
			}
		}

		/* �ł��߂������h�}�[�N�̏���ݒ� */
		Object[] nearestLandmarkInfo = new Object[2];
		nearestLandmarkInfo[0] = selectedNodeID;
		nearestLandmarkInfo[1] = new Integer(selectedStep);

		return nearestLandmarkInfo;
	}


	/**
	 * ���X�g���̊e��Ԃ���ړ��\�ȏ�Ԃ����X�g�Ŏ擾���܂��B
	 * @param LinkedList nodeList  ��Ԃ̃��X�g
	 * @param boolean direction    �ړ�����  true=forward  false=inverse
	 * @param Hashtable checkTable ������Ԃ𕡐��ݒ肵�Ȃ����߂̃e�[�u��
	 * @return LinkedList �����̃��X�g�̊e��Ԃ���ړ��\�ȏ�Ԃ̃��X�g
	 */
	private LinkedList getMoveableNodeList(LinkedList nodeList,
	        boolean direction, Hashtable checkTable){

		/* ���Ɉړ��\�ȏ�Ԃ̃��X�g�ݒ�p */
		LinkedList nextNodeList = new LinkedList();

		/* �����Ŏ擾�������X�g�̊e��Ԃɂ��Ă̌J��Ԃ� */
		ListIterator nodeIterator = nodeList.listIterator();
		while(nodeIterator.hasNext()) {

			/* �e��Ԃ̎擾 */
			Integer nodeID = (Integer)nodeIterator.next();

			/* �擾������Ԃ����MoveableStateList���擾 */
			LinkedList linkedList = null;
			if(direction) {
				linkedList = getForwardNodeIDList(nodeID);
			}else {
				linkedList = getInverseNodeIDList(nodeID);
			}

			ListIterator listIterator = linkedList.listIterator();
			while(listIterator.hasNext()) {
				/* ����̏�Ԃ͒ǉ����Ȃ� */
				Integer nextNodeID = (Integer)listIterator.next();
				if(!checkTable.containsKey(nextNodeID)) {
					nextNodeList.add(nextNodeID);
					checkTable.put(nextNodeID, nextNodeID);
				}
			}
		}

		return nextNodeList;
	}


	/**
	 * �S�[���T�����̌o�H�ɂ��Ċw�K���A�S�[���ւ̍ŒZ�̏�Ԃ�����Ԃ��擾
	 * �܂��B
	 * @param Object[] stateInfo ���
	 * @param Integer goalNodeID �S�[����NodeID
	 * @return Integer           ���Ɉړ�����m�[�h��ID
	 */
	private Integer pathLearning(Object[] stateInfo, Integer goalNodeID) {

		if(stateInfo == null) {
			return null;
		}

		/* ���݂̏�ԂɊւ��Ă̏�� */
		Integer nodeID = (Integer)stateInfo[0];
		int distance = ((Integer)stateInfo[1]).intValue();
		StateList stateList = (StateList)stateInfo[2];

		/* �w�K */
		Node node = getNode(nodeID);
// ���\�����̏ꍇ�ɂ̓S�[���T���J�n��̊w�K���s�Ȃ�Ȃ��p�ɂ��邽��
// �p�X�̊w�K���s�Ȃ�Ȃ��悤�ɂ���
		node.setCognitiveDistance(goalNodeID, distance);

		/* �e�̏����擾 */
		StateList parentList = stateList.getParentList();

		/*
		 * �e�̏�Ԃ�����΁A���̏�Ԃɂ��Ċw�K�E���̏�Ԃ̎擾�A
		 * �Ȃ���Ύ���̏�Ԃ�Ԃ��B
		 */
		Integer parentNodeID = null;
		if(parentList != null) {
			Object[] parentObj = new Object[3];
			parentObj[0] = stateList.getParentNodeID();
			parentObj[1] = new Integer(distance+1);
			parentObj[2] = parentList;
			parentNodeID = pathLearning(parentObj, goalNodeID);
		}else {
			parentNodeID = nodeID;
		}
		return parentNodeID;
	}


	////////////////////////////////////////////////////////////////////
	// �w�K��Ԃ̓Ǎ��݁E�ۑ�

	/**
	 * �w�K�f�[�^��ǂݍ��݂܂��B
	 * @param ObjectInputStream oInputStream
	 */
	public void load(ObjectInputStream oInputStream) throws IOException,
	        ClassNotFoundException {
		nodes = (Vector)oInputStream.readObject();
		// 2001.06.07 �ǉ� miyamoto
		/* �Z�O�����g���̓Ǎ��� */
		segmentCount = ((Integer)oInputStream.readObject()).intValue();
	}

	/**
	 * �w�K�f�[�^��ۑ����܂��B
	 * @param ObjectOutputStream oOutputStream
	 */
	public void save(ObjectOutputStream oOutputStream) throws IOException {
		oOutputStream.writeObject(nodes);
		// 2001.06.07 �ǉ� miyamoto
		/* �Z�O�����g���̕ۑ� */
		oOutputStream.writeObject(new Integer(segmentCount));
	}


	/////////////////////////////////////////////////////////////////////
	// �e�X�g�E�����p�̃��\�b�h

	/**
	 * �w�K���s�Ȃ����ǂ����̃t���O��ݒ肵�܂��B
	 * @param boolean flag  true:�w�K���s�Ȃ�  false:�w�K�����Ȃ�
	 */
	public void setLearningFlag(boolean flag) {
		learningFlag = flag;
	}

	/**
	 * �����h�}�[�N�̊w�K���s�Ȃ����ǂ����t���O��ݒ肵�܂��B
	 * @param boolean flag   true:�w�K����  false:�w�K���Ȃ�
	 */
	public void setSegmentationFlag(boolean flag) {
		landmarkLearningFlag = flag;
	}


	/**
	 * �������Ŏw�肳�ꂽ��ԂɑΉ�����A�������Ŏw�肳�ꂽ�w�ł̃m�[�h��
	 * �擾���܂��B
	 * ��ʑw�ł�id�ɕϊ����A�m�[�h���擾���܂��B
	 * @param Integer id ���݂̑w�ł�ID
	 * @param int layer  �擾����m�[�h�����݂̑w��艽�w��̑w��
	 * @return Node      �m�[�h
	 */
	public Node getNode(Integer id, int layer) {
		Node node = null;
		if(layer == 0) { /* ����̃m�[�h���擾 */
			node = getNode(id);
		}else{           /* ��ʑw�̃m�[�h���擾 */
			if(upperLayerAgent == null) {
			}else{
				Integer upperID = getUpperLayerNodeID(id);
				node = upperLayerAgent.getNode(upperID, layer-1);
			}
		}
		return node;
	}


	/**
	 * CognitiveDistance���w�K���邽�߂̈ړ�������Ԃ̗����A��O�̃T�C�N��
	 * �̏�Ԃ�ێ�����ϐ����N���A���܂��B
	 */
	public void reset() {
		/* StateBuffer�̃N���A */
		stateBuffer.clear();

		/* �O��̏�Ԃ̏����� */
		id_Vu0 = null;
		id_Gu0 = null;

		// 2001.04.10 �폜 miyamoto
		// �e�X�g�p /* �S�[���T������ݒ肷��N���X�������� */
//		goalSearchInfo = new GoalSearchInfo();

		/* ��ʑw������΁A��ʑw��StateBuffer���N���A */
		if(upperLayerAgent != null) {
			upperLayerAgent.reset();
		}
	}

	// 2001.04.05 �ǉ� bsc miyamoto
	/**
	 * �ێ����Ă���O�T�C�N���̏����N���A���܂��B
	 */
	public void resetOldValue() {
		id_Vu0 = null;
		id_Gu0 = null;
		/* ��ʑw������΁A��ʑw�̒l���N���A */
		if(upperLayerAgent != null) {
			upperLayerAgent.resetOldValue();
		}
	}


	/**
	 * ��ʑw���폜���܂��B
	 * ���p����ő�̏�ʑw�𓮓I�ɕύX���邽�߂̃��\�b�h�B
	 */
	public void deleteUpperLayer() {
		upperLayerAgent = null;
	}

	/**
	 * ���s���̏��ɂ��Ď擾���܂��B
	 * @return ExecInfo ���s���̏��
	 */
	public ExecInfo getExecInfo() {
		return execInfo;
	}

	// 2001.05.23 �ǉ� miyamoto
	/**
	 * ���s���̏����N���A���܂��B
	 */
	public void resetExecInfo() {
		execInfo.paramReset();
	}

	/**
	 * �S�[���̒T���Ɋւ��Ă̏����擾���܂��B
	 * @return GoalSearchInfo �S�[���T���Ɋւ��Ă̏���ݒ肵���N���X
	 */
	public GoalSearchInfo getGoalSearchInfo() {
		return goalSearchInfo;
	}


	/**
	 * �w�K�󋵂Ɋւ��Ă̏����擾���܂��B
	 * @return int[]   int[0]  MoveableState�̃T�C�Y
	 *                 int[1]  CognitiveDsitance�̃T�C�Y
	 *                 int[2]  �m�[�h��
	 *                 int[3]  �L���ȃm�[�h�̐�
	 */
	public int[] getLearningInfo() {
		int msSize = 0;
		int cdSize = 0;
		int validNodeNum = 0;
		for(int i = 0; i < nodes.size(); i++) {
			// 2001.05.24 �C�� miyamoto �Â��o�[�W������java�ɑΉ�
//			Node node = (Node)nodes.get(i);
			Node node = (Node)nodes.elementAt(i);

			cdSize += node.getCDSize();
			msSize += node.getForwardNodeIDListSize();
			if(node.isValid()) {
				validNodeNum ++;
			}
		}
		int[] learningSize = new int[4];
		learningSize[0] = msSize;       /* MoveableState�̃T�C�Y */
		learningSize[1] = cdSize;       /* CognitiveDsitance�̃T�C�Y */
		learningSize[2] = nodes.size(); /* �m�[�h�� */
		learningSize[3] = validNodeNum; /* �L���ȃm�[�h�̐� */
		return learningSize;
	}


}


