/**
 * ExecInfo.java
 * ���s���̏����Ǘ�����N���X�ł�
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * 2001.03 BSC miyamoto
 */
package cognitivedistance;

/**
 * ���s���̏����Ǘ�����N���X
 */
public class ExecInfo {

	private Integer nodeID;           /* ���݂̏�� */
	private Integer goalNodeID;       /* �S�[���̏�� */
	private Integer subgoal;          /* ��ʑw����̃T�u�S�[���̏��  */
	private Integer nextNodeID;       /* ���̏�� */
	private int processID;            /* ���̏�Ԃ��o�͂��Ă��鏈����ID */
	private boolean flagRenewSubgoal; /* �T�u�S�[�����ύX���ꂽ���̃t���O */

	/**
	 * ���s���̏����擾���܂��B
	 * @return int[] ���s���̃m�[�h�Ɋւ�����
	 *               �l�̂Ȃ���Ԃɂ��Ă�-1���ݒ肳���
	 *               int[0] ���݂̏�Ԃ�ID
	 *               int[1] �S�[���̏�Ԃ�ID
	 *               int[2] ��ʑw����̃T�u�S�[���̏�Ԃ�ID
	 *               int[3] ���̏�Ԃ�ID
	 *               int[4] ���̏�Ԃ��o�͂��Ă��鏈����ID
	 *               int[5] �T�u�S�[�����X�V����Ă��邩
	 *                      0�F�X�V����Ă��Ȃ� 1�F�X�V����Ă���
	 */
	public int[] getExecInfo() {
		int[] execInfo = new int[6];

		if(nodeID != null) {
			execInfo[0] = nodeID.intValue();
		}else {
			execInfo[0] = -1;
		}

		if(goalNodeID != null) {
			execInfo[1] = goalNodeID.intValue();
		}else {
			execInfo[1] = -1;
		}

		if(subgoal != null) {
			execInfo[2] = subgoal.intValue();
		}else {
			execInfo[2] = -1;
		}

		if(nextNodeID != null) {
			execInfo[3] = nextNodeID.intValue();
		}else {
			execInfo[3] = -1;
		}

		execInfo[4] = processID;

		if(flagRenewSubgoal == false) {
			execInfo[5] = 0;
		}else {
			execInfo[5] = 1;
		}

		return execInfo;
	}

	/**
	 * ���݂̏�Ԃ�ݒ肵�܂��B
	 * @param Integer nodeID ���݂̏�Ԃ�ID
	 */
	public void setNodeID(Integer nodeID) {
		this.nodeID = nodeID;
	}

	/**
	 * �S�[���̏�Ԃ�ݒ肵�܂��B
	 * @param Integer goalNodeID �S�[���̏�Ԃ�ID
	 */
	public void setGoalNodeID(Integer goalNodeID) {
		this.goalNodeID = goalNodeID;
	}

	/**
	 * ���̏�ԂƁA���̏�Ԃ̎擾�Ɋւ������ݒ肵�܂��B
	 * @param Integer nextNodeID       ���̏�Ԃ�ID
	 * @param int processID            ���̏�Ԃ��o�͂��Ă��鏈����ID
	 * @param Integer subgoal          ��ʑw����̃T�u�S�[���̏�Ԃ�ID
	 * @param boolean flagRenewSubgoal �T�u�S�[�����X�V����Ă��邩
	 */
	public void setNextNodeID(Integer nextNodeID, int processID,
	        Integer subgoal, boolean flagRenewSubgoal) {
		this.nextNodeID = nextNodeID;
		this.processID = processID;
		this.subgoal = subgoal;
		this.flagRenewSubgoal = flagRenewSubgoal;
	}

	/**
	 * �e�p�����[�^�����������܂��B
	 */
	public void paramReset() {
// ���݂̏�Ԃ̓N���A���Ȃ� 2001.03.08 �C�� miyamoto 
//		nodeID = null;
		goalNodeID = null;
		subgoal = null;
		nextNodeID = null;
		flagRenewSubgoal = false;
	}

}
