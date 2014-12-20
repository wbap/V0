/** 
 * GSA.java
 * GSA�̖{��
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.09
 */
package gsa;

import java.util.*;
import gsa.viewer.*;

/**
 * GSA�̖{��
 */
public class GSA {

	/* �G�[�W�F���g�̐ݒ�t�@�C����ǂݍ��ރN���X */
	private GSAProperty prop;

	/* �G�[�W�F���g�̔z�� */
	private Agent[] agents = null;

	/* ���s�G�[�W�F���g�̑I����@ 0:�z��̏� 1:�����_�� */
	private int agentSelectMode = 1;

	/* State�EGoal���Ǘ����鋤�L������ */
	private SharedMemory sharedMemory = null;

	/* ���B�Ɏ��s�����S�[�����c���[�ŊǗ�����N���X */
	private FailAgentTree failAgentTree = null;

	/* �G�[�W�F���g�� */
	private int agentNum;
	/* �m�[�h�� */
	private int nodeNum;

	/* ManualAgent���g�p���邩�ǂ��� */
	private boolean useMana = false;

	/* �O������ݒ肳�ꂽ�S�[�� */
	private Vector target = null;

	/* �G�[�W�F���g�̓���󋵂�\������viewer */
	private AgentViewer viewer = null;

	/* ���s�������s�Ȃ����߂ɑI������Ă���G�[�W�F���g */
	private Agent agent = null;

	/* �O������ݒ肳�ꂽ�S�[���p�̉��̃G�[�W�F���gID */
	private final int GOAL_AGID = 0;

	/* ManualAgent�̃G�[�W�F���gID */
	private final int MANUAL_AGENT_ID = 10;


	/*
	 * ���s�G�[�W�F���g�̔z�񒆂̃C���f�b�N�X
	 * ���s�G�[�W�F���g�����ɑI������ꍇ�Ɏg�p
	 */
	private int sequenceSelectIndex = 0;

	/* ���s�G�[�W�F���g�������_���ɑI�����邽�߂̗��� */
	private int randamSelectSeed = 1;
	private Random random = new Random(randamSelectSeed);
	/* �G�[�W�F���g�̑I���󋵂�����boolean�̔z�� */
	private boolean[] useAgentFlags = null;

//	private Util util;

	////////////////////////////////////////////////////////////////
	// �R���X�g���N�^ ���������\�b�h

	/**
	 * �R���X�g���N�^
	 * @param String propFileName GSA�̐ݒ�t�@�C����
	 */
	public GSA(String propFileName) {

		initPropFile(propFileName);

		agentNum = prop.getAgentNum();
		nodeNum = prop.getNodeNum();
		useMana = prop.getUseMana();

		sharedMemory = new SharedMemory(prop.getNodeNum(),
		        prop.isShowGoalStackViewer());
		failAgentTree = new FailAgentTree(prop.isShowFailAgentTreeViewer());

		initAgent();

		if(prop.isShowAgentViewer()) {
			initViewer();
		}
//util = new Util(agentNum);
	}

	/**
	 * �ݒ�t�@�C���̏����Ǘ�����N���X�̏�����
	 * @param String propFileName �ݒ�t�@�C����
	 */
	private void initPropFile(String propFileName) {
		try {
			prop = new GSAProperty(propFileName);
		}catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * �G�[�W�F���g�̏�����
	 */
	private void initAgent() {

		/*
		 * �}�j���A���G�[�W�F���g�𗘗p����ꍇ�́A�G�[�W�F���g�����P���₷
		 * �ʏ�̃G�[�W�F���g�����̂��߂̃J�E���^�͕ʓr�p��
		 */
		int loopNum = agentNum;
		if(useMana) {
			agentNum+=1;
		}

		agents = new Agent[agentNum];

		if(useMana) {
			initManualAgent();
		}

		for(int i = 0; i < loopNum; i++) {
			int agentType = prop.getAgentType(i);
			int agid = prop.getAgentID(i);
			boolean[] useNode = prop.getUseNode(i);
			// 2001.12.14 �ǉ� miyamoto
			String eventFileName = prop.getEventFileName(i);
			// �����܂�

			System.out.println("");
			System.out.println(" [ agid " + agid + " ]");
			System.out.println("  agent type:" + agentType);
			System.out.print("  use node:[");
			for(int m = 0; m < useNode.length; m++) {
				System.out.print(useNode[m] + ",");
			}
			System.out.println("]");
			System.out.println("  event file name:" + eventFileName);

			if(agentType == prop.CD) {
				agents[i] = new CDAgent(agid, useNode, sharedMemory);
			}else if(agentType == prop.ASSOCIATE) {
				agents[i] = new AssociateAgent(agid, useNode, sharedMemory);
			}else if(agentType == prop.LOG) {
				agents[i] = new LogAgent(agid, useNode, sharedMemory);
			}

			// 2001.12.14 �ǉ� miyamoto
			if(eventFileName != null) {
				agents[i].learnEvent(eventFileName);
			}
			// �����܂�
		}
	}

	/* ���B�S�[���̍폜���s�Ȃ����G�[�W�F���g��ݒ肷��ϐ� */
	private boolean[] removeReachGoalAgents = null;
	/**
	 * �G�[�W�F���g�̓���󋵂�\������Viewer�̏�����
	 */
	private void initViewer() {
		int[] agentIDs = new int[agents.length];
		for(int i = 0; i < agents.length; i++) {
			agentIDs[i] = agents[i].AGID;
		}
		removeReachGoalAgents = new boolean[agents.length];
		viewer = new AgentViewer(agentIDs, removeReachGoalAgents);
	}

	/**
	 * ManualAgent�̐���
	 */
	private void initManualAgent() {
		boolean[] allNode = new boolean[prop.getNodeNum()];
		for(int i = 0; i < allNode.length; i++) {
			allNode[i] = true;
		}
		agents[agentNum-1] = new ManualAgent(MANUAL_AGENT_ID, allNode,
		        sharedMemory);
	}


	////////////////////////////////////////////////////////////////
	// public

	/**
	 * ���s�������s�Ȃ��܂��B
	 * @param Vector state ���݂̏��
	 * @return Vector �T�u�S�[��
	 */
	public Vector exec(Vector state) {
		/* �����Őݒ肳�ꂽ���݂̏�Ԃ��X�^�b�N�ɐݒ� */
		sharedMemory.setState(state);

		useAgentFlags = new boolean[agentNum];
		clearUseFlag();

//util.reset();

		/*
		 * �X�^�b�N��̃S�[���ɓ��B�����ꍇ�A���̃S�[�����X�^�b�N�E�c���[����
		 * �폜
		 */
		removeReachGoal();

		/*
		 * �c���[��̃S�[���ɓ��B�����ꍇ�A�X�^�b�N�A�c���[���N���A
		 */
		if( isReachTreeGoal() ) {
			clearGoalStackAndTree();
			setGoal(target);
		}

		/* �G�[�W�F���g�̊w�K */
		double reward = 0;
		learn(false/*�S�[�����B�t���O*/, reward);

		/*
		 * ���s�\�ȃG�[�W�F���g���I������邩�A���ׂẴG�[�W�F���g�����s
		 * ����܂ŌJ��Ԃ�
		 */
		while(true) {
			if(agent == null) {
				agent = getExecAgent();
			}

			/* �G�[�W�F���g�̎��s���� */
			/* ���̃G�[�W�F���g�����łɎ��s�ς݂Ȃ���s�������s�Ȃ�Ȃ� */
			int isSuccess = -1;
//			if( failAgentTree.isFail(agent.AGID) ) {
			if( failAgentTree.getChildAgr(agent.AGID) > -1) {
//System.out.println("   already fail");
				isSuccess = Agent.AGR_FAIL_AGENT;
			}else {
				isSuccess = agent.exec();
			}

// ���s�������s�Ȃ����G�[�W�F���g��\��
			if(viewer != null) {
				viewer.setExecAgentID(agent.AGID);
				viewer.repaint();
			}

			if(isSuccess == Agent.AGR_SUCCESS) {
				/* �S�[�����c���[�ɐݒ� */
				Vector agentGoalElementArray
				        = agent.getSelfSetGoalElementArray();
				Vector agentGoalValueArray
				        = agent.getGoalValueArray(agentGoalElementArray);
				failAgentTree.addTreeNode(agent.AGID, agentGoalValueArray);

				/* �����������ɃS�[����Ԃ� */
				Vector goal = sharedMemory.getGoalValueArray();
				return goal;
			}else {
				/* �������s�̃G�[�W�F���g���c���[�ŊǗ� */
				failAgentTree.addTreeNode(agent.AGID, isSuccess);
				/* �G�[�W�F���g�؂�ւ����ɑO�G�[�W�F���g�̕ێ������N���A */
				agent.suspend();
				agent = null;

				if(getNotUseAgentNum() == 0) {
//				if(util.getNotUseNum() == 0) {
					removeUnsolvedGoal();
					break;
				}
			}
		}

		if(viewer != null) {
			viewer.setExecAgentID(-1);
			viewer.repaint();
		}

		/* �S�[����Ԃ� */
		Vector goal = sharedMemory.getGoalValueArray();
		return goal;
	}

	/**
	 * �w�K���ʂ��t�@�C���ɕۑ����܂��B<BR>
	 * �G�[�W�F���g���Ƃ̊w�K���ʕۑ��������Ăяo���܂��B<BR>
	 * �e�G�[�W�F���g�̃t�@�C�����͈����Őݒ肳�ꂽ�t�@�C�����Ɋe�G�[�W�F���g��
	 * ID��ǉ��������̂ɂȂ�܂��B(fileName+agid.dat)
	 * @param String fileName �t�@�C����
	 */
	public void save(String fileName) {
		for(int i = 0; i < agents.length; i++) {
			agents[i].save(fileName + agents[i].AGID + ".dat");
		}
	}

	/**
	 * �w�K���ʂ��t�@�C������ǂݍ��݂܂��B<BR>
	 * �G�[�W�F���g���Ƃ̊w�K���ʓǂݍ��ݏ������Ăяo���܂��B<BR>
	 * �e�G�[�W�F���g�̃t�@�C�����͈����Őݒ肳�ꂽ�t�@�C�����Ɋe�G�[�W�F���g��
	 * ID��ǉ��������̂ɂȂ�܂��B(fileName+agid.dat)
	 * @param String fileName �t�@�C����
	 */
	public void load(String fileName) {
		for(int i = 0; i < agents.length; i++) {
			agents[i].load(fileName + agents[i].AGID + ".dat");
		}
	}

	/**
	 * �X�^�b�N�A�c���[���N���A���A�e�G�[�W�F���g��reset()���\�b�h���Ăяo��
	 * �܂��B<BR>
	 * �w�K���ʂ̓��Z�b�g����܂���B<BR>
	 * �w�K���ʂ��c�����܂܍ăX�^�[�g����ꍇ�ȂǁA�w�K�A���s�����̘A������
	 * �r�؂��ꍇ�̏������s�Ȃ��܂��B
	 */
	public void reset() {
		/* �e�G�[�W�F���g��reset()�̌Ăяo�� */
		for(int i = 0; i < agentNum; i++) {
			agents[i].reset();
		}
		/* �X�^�b�N�A�c���[���N���A */
		failAgentTree.clear();
		sharedMemory.removeAllGoal();
		/* �g�p����G�[�W�F���g���N���A */
		agent = null;
	}

	/**
	 * �S�[�������L�������ɐݒ肵�܂��B
	 * @param Vector goal �S�[��
	 */
	public void setGoal(Vector goal) {
		target = goal;
		if(goal != null && goal.size() == nodeNum) {
			for(int i = 0; i < goal.size(); i++) {
				Integer goalValue = (Integer)goal.get(i);
				if(goalValue != null) {
					GoalStackElement goalElement = new GoalStackElement(
					         goalValue.intValue(), GOAL_AGID);
					sharedMemory.pushGoal(i, goalElement);
				}
			}
			failAgentTree.addTreeNode(GOAL_AGID, goal);
		}
	}

	/**
	 * ���L�������̏�Ԃ�\�����܂��B
	 */
	public void printSharedMemory() {
		sharedMemory.printState();
		sharedMemory.printGoalStack();
	}

	/**
	 * �c���[�̏�Ԃ�\�����܂��B
	 */
	public void printFailAgentTree() {
		failAgentTree.printTree();
	}


	////////////////////////////////////////////////////////////////
	// private

	/**
	 * �c���[�̃J�����g�m�[�h�̎q�m�[�h�ɁA�S�ẴG�[�W�F���g����̃m�[�h��
	 * �ݒ肳��Ă���ꍇ�A�J�����g�m�[�h�̃S�[�������s�S�[���Ƃ��āA�X�^�b�N
	 * ����폜���܂��B<BR>
	 * �c���[�͌��݂̈ʒu���ړ�������B
	 */
// ���݂͂��鎞�_�Ŏ��Ȑݒ�S�[�����擾�ł���G�[�W�F���g�͂P�ɓ��肳��邪
// �����̃G�[�W�F���g�����Ȑݒ�S�[�����擾�ł���悤�ȏꍇ�A���݂̎����ł�
// ��肪��������\��������B
	private void removeUnsolvedGoal() {
		for(int i = 0; i < agentNum; i++) {
			boolean b = agents[i].removeSelfSetGoal();
			if(b == true) {
				failAgentTree.moveParent();
				break;
			}
		}
	}


	////////////////////////////////////////////////////////////////
	// ���s�G�[�W�F���g�̑I��

//	private Agent getExecAgent() {
//		int num = util.getRandomNum();
//		return agents[num];
//	}

	/**
	 * ���s�������s�Ȃ��G�[�W�F���g���擾���܂��B
	 * @param Agent �G�[�W�F���g
	 */
	private Agent getExecAgent() {
		Agent agent = null;
		if(agentSelectMode == 0) {
			agent = getExecAgentArrayOder();
		}else {
			agent = getExecAgentRandomOder();
		}
		return agent;
	}

	/**
	 * ���s�������s�Ȃ��G�[�W�F���g�����ԂɎ擾���܂��B
	 * agents(�G�[�W�F���g�̔z��)�ɐݒ肳�ꂽ���Ɏ擾�B
	 * @param Agent �G�[�W�F���g
	 */
	private Agent getExecAgentArrayOder() {
		Agent agent = agents[sequenceSelectIndex];
		sequenceSelectIndex++;
		if(sequenceSelectIndex >= agentNum ) {
			sequenceSelectIndex = 0;
		}
		return agent;
	}

	/**
	 * ���s�������s�Ȃ��G�[�W�F���g�������_���Ɏ擾���܂��B
	 * @param Agent �G�[�W�F���g
	 */
	private Agent getExecAgentRandomOder() {
		int index = getExecAgentIndex();
		useAgentFlags[index] = true;
		return agents[index];
	}

	/**
	 * ���s����G�[�W�F���g�̔z�񒆂�Index���擾���܂��B
	 * @return int ���s����G�[�W�F���g�̔z�񒆂�Index
	 */
	private int getExecAgentIndex() {
		/* ���g�p�ȃG�[�W�F���g�̐����擾 */
		int notUseAgentNum = getNotUseAgentNum();
		/* ���ׂĎg�p����Ă���ΑS�Ă𖢎g�p�ɐݒ肵�S�Ă���I�� */
		if(notUseAgentNum == 0) {
			clearUseFlag();
			notUseAgentNum = getNotUseAgentNum();
		}
		int randomNum = random.nextInt(notUseAgentNum);
		int index = 0;
		int notUseNum = 0;
		for(; index < useAgentFlags.length; index++) {
			if(useAgentFlags[index] == false) {
				if(notUseNum == randomNum) {
					break;
				}
				notUseNum++;
			}
		}
		return index;
	}

	/**
	 * ���g�p�ȃG�[�W�F���g�̐����擾���܂��B
	 * @return int ���s�������s�Ȃ��Ă��Ȃ��G�[�W�F���g��
	 */
	private int getNotUseAgentNum() {
		int num = 0;
		for(int i = 0; i < useAgentFlags.length; i++) {
			if(useAgentFlags[i] == false) {
				num++;
			}
		}
		return num;
	}

	/**
	 * �g�p��Ԃɐݒ肳�ꂽ�G�[�W�F���g�𖢎g�p��Ԃɐݒ肵�܂��B
	 */
	private void clearUseFlag() {
		for(int i = 0; i < useAgentFlags.length; i++) {
			useAgentFlags[i] = false;
		}
	}

	/**
	 * �X�^�b�N���瓞�B�����S�[�����폜���܂��B
	 */
	private void removeReachGoal() {

		if(viewer != null) {
			for(int i = 0; i < removeReachGoalAgents.length; i++) {
				removeReachGoalAgents[i] = false;
			}
		}

		while(true) {
			/* �S�ẴG�[�W�F���g���폜�ł��Ȃ��Ȃ�܂� */
			boolean flagRemove = false;
			for(int i = 0; i < agentNum; i++) {
				if(agents[i].removeReachGoal()) {
					// 2001.09.26 �ǉ� 
					/* ���B�S�[���폜���Ƀc���[������ */
					failAgentTree.removeCurrent();
					flagRemove = true;

					/* �폜�����G�[�W�F���g��ݒ�(viewer�p) */
					if(viewer != null) {
						removeReachGoalAgents[i] = true;
					}

				}
			}
			if(flagRemove == false) {
				break;
			}
		}
	}

	/**
	 * �c���[��̂��Âꂩ�̏�Ԃɓ��B�������ǂ������肵�܂��B
	 * @return boolean true:���B false:�����B
	 */
	private boolean isReachTreeGoal() {
		for(int i = 0; i < agents.length; i++) {
			boolean b = failAgentTree.isContain(agents[i].AGID, 
			        agents[i].getStateReference());
			if(b == true) {
				return true;
			}
		}
		return false;
	}

	/**
	 * �S�[���X�^�b�N�ƃc���[�̗v�f�����ׂăN���A���܂��B
	 */
	private void clearGoalStackAndTree() {
		sharedMemory.removeAllGoal();
		failAgentTree.clear();
	}

	/**
	 * �G�[�W�F���g�̊w�K���s���܂��B
	 * @param boolean flagGoalReach
	 * @param double p
	 */
	private void learn(boolean flagGoalReach, double p) {
		/* �S�G�[�W�F���g�̊w�K���� */
		for(int i = 0; i < agents.length; i++) {
			agents[i].learn(flagGoalReach, p);
		}
	}


}
