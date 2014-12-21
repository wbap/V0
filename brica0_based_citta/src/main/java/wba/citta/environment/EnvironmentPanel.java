/**
 * ���̕`��A������s�Ȃ��N���X
 * EnvironmentPanel.java
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2001.05 BSC miyamoto
 */
package wba.citta.environment;

import java.awt.*;
import java.awt.event.*;

/**
 * ���̕`��A������s�Ȃ��N���X�ł��B
 */
public class EnvironmentPanel extends Panel {

	/* ���̕`����s�Ȃ��N���X */
	private EnvironmentCanvas canvas;

	/* �n�}��񂩂�s�����Ǘ�����N���X */
	private ActionController actionController;

	/* �n�}�����Ǘ�����N���X */
	private MapController mapController;

	/* ���{�b�g�̈ʒu */
	private int[] robotState;

	/* �n�}�ɑΉ�������V�̃e�[�u�� */
	private String[][] rewardMap;

	String[][] colorMap = null;

	/* �J�M��ێ����Ă��邩�ǂ��� */
//	private boolean key = false;
	/* ������ */
	public final int NOTHING = 0;
	public final int KEY = 1;
	public final int TELEPHON = 2;
public final int A = 3;
public final int B = 4;
public final int a = 5;
public final int b = 6;


	/* ���̕`����s�Ȃ��t���O */
	private boolean isShow = true;

	/* �N���b�N���ɐݒ肷��� ""=�� "W"=�� "O"=��V "n"=�ݒ肵�Ȃ� */
	private String renewValue = "n";

	/* �S�[���𕡐��ݒ�\�ɂ��邩 */
	private boolean flagGoals;

	///////////////////////////////////////////////////////
	// �R���X�g���N�^

	/**
	 * �R���X�g���N�^
	 * @param String fileName Map�t�@�C����
	 */
	public EnvironmentPanel(String fileName) {
		/* ��V�̕\������A�S�[����1�̂� */
		this(fileName, true, false);
	}

	/**
	 * �R���X�g���N�^ ��V�̕\���ɂ��Ă̐ݒ��
	 * @param String fileName Map�t�@�C����
	 * @param boolean isShowReward ��V��\�����邩�ǂ���
	 * @param boolean flagGoals    �S�[���𕡐��ݒ�\�ɂ��邩
	 *                             true:�����\  false:�����s�� �ȑO�̂��폜
	 */
	public EnvironmentPanel(String fileName, boolean isShowReward,
	        boolean flagGoals) {
		/* �n�}���Ǘ�����N���X�̐��� */
		mapController = new MapController(fileName);

		/* �n�}��񂩂�s�����Ǘ�����N���X�̐��� */
		actionController = new ActionController(mapController.getMap());

//		robotState = new int[2];
		robotState = new int[3];
		this.flagGoals = flagGoals;

		/* ���{�b�g�̈ʒu������ */
		initRobotPos();

		/* �`�敔���̏����� */
		initCanvas(isShowReward);

	}

	public EnvironmentPanel(String mapFileName, String colorMapFileName,
	        boolean isShowReward, boolean flagGoals) {
		/* �n�}���Ǘ�����N���X�̐��� */
		mapController = new MapController(mapFileName);

		MapFileToArray mapFileToArray = new MapFileToArray(colorMapFileName);
		colorMap = mapFileToArray.getFileArray();

		/* �n�}��񂩂�s�����Ǘ�����N���X�̐��� */
		actionController = new ActionController(mapController.getMap());

//		robotState = new int[2];
		robotState = new int[3];
		this.flagGoals = flagGoals;

		/* ���{�b�g�̈ʒu������ */
		initRobotPos();

		/* �`�敔���̏����� */
		initCanvas(isShowReward);
	}


	/**
	 * �R���X�g���N�^ ��V�̕\���ɂ��Ă̐ݒ��
	 * @param String[][] String�̔z��ł̒n�}���
	 * @param boolean isShowReward ��V��\�����邩�ǂ���
	 * @param boolean flagGoals    �S�[���𕡐��ݒ�\�ɂ��邩
	 *                             true:�����\  false:�����s�� �ȑO�̂��폜
	 */
	public EnvironmentPanel(String[][] map, boolean isShowReward,
	        boolean flagGoals) {
		/* �n�}���Ǘ�����N���X�̐��� */
		mapController = new MapController(map);

		/* �n�}��񂩂�s�����Ǘ�����N���X�̐��� */
		actionController = new ActionController(mapController.getMap());

//		robotState = new int[2];
		robotState = new int[3];
		this.flagGoals = flagGoals;

		/* ���{�b�g�̈ʒu������ */
		initRobotPos();

		/* �`�敔���̏����� */
		initCanvas(isShowReward);
	}

	////////////////////////////////////////////////////////////
	// ����������

	/**
	 * ���{�b�g�̈ʒu�̏�����
	 */
	public void initRobotPos() {
		int[] pos = mapController.getPos("S");
		robotState[0] = pos[0];
		robotState[1] = pos[1];
	}

	/**
	 * �`�揈�������̏�����
	 * @param boolean ��V�̕\�����s�Ȃ����ǂ���
	 */
	private void initCanvas(boolean b) {
		canvas = new EnvironmentCanvas();
		canvas.addMouseListener(new CanvasMouseAdapter());
		/* ��V�̕\�����s�Ȃ��ꍇ�͕�V�p�̃e�[�u���������� */
		if(b) {
			int[] size = mapController.getSize();
			rewardMap = new String[size[0]][size[1]];
			/* ��V�̍X�V */
			renewReward();
		}
//		canvas.initCanvas(mapController.getMap(), robotState, rewardMap);
		canvas.initCanvas(mapController.getMap(), robotState, rewardMap,
		        colorMap);
		setLayout(new BorderLayout());
		add(canvas);
	}

	////////////////////////////////////////////////////////////
	// public

	/**
	 * ���{�b�g���w�肳�ꂽ�����ֈړ����܂��B
	 * @param int action  �ړ����� �O�`�V�Őݒ�
	 * @return boolean    true=�ړ����� false=�ړ����s
	 */
	public boolean run(int action) {

		int[] newState = actionController.move(robotState[0], robotState[1],
		        action);

		/* ���ۂɈړ��ł������`�F�b�N�� �ړ����Ă���Ώ�Ԃ�ω� */
		boolean isMove = true;
		if( (robotState[0] == newState[0]) && (robotState[1]==newState[1]) ) {
			isMove = false;
		}else{
			robotState[0] = newState[0];
			robotState[1] = newState[1];
			/* �J�M�̈ʒu���`�F�b�N */
//			checkKey(robotState[0], robotState[1]);
			// 2001.08.08 �C�� miyamoto
			checkItem(robotState[0], robotState[1]);

			/* �t���O�ɂ��`�揈�����s�Ȃ� */
			if(isShow) {
				canvas.repaint();
			}
		}
		return isMove;
	}


	/**
	 * ���{�b�g���w�肳�ꂽ�����ֈړ����܂��B
	 * @param int[] xy    �ړ���̍��W
	 * @param int action  �ړ����� �O�`�V�Őݒ�
	 * @return boolean    true=�����Őݒ肳�ꂽ���W�ɁA�����Őݒ肳�ꂽAction
	 *                         �ňړ�����
	 *                    false=�����Őݒ肳�ꂽ���W�ɁA�����Őݒ肳�ꂽAction
	 *                         �ňړ����s
	 */
	public boolean run(int[] xy, int action) {

		int[] newState = actionController.move(robotState[0], robotState[1],
		        action);

		/*
		 * �����Ŏw�肳�ꂽ�ʒu�Ɏ��ۂɈړ��ł���̂��`�F�b�N���ړ��ł����
		 * ��Ԃ�ω�
		 */
		boolean isMove = false;

		if( (xy[0] == newState[0]) && (xy[1]==newState[1]) ) {
			robotState[0] = newState[0];
			robotState[1] = newState[1];
			/* �J�M�̈ʒu���`�F�b�N */
//			checkKey(robotState[0], robotState[1]);
			// 2001.08.08 �C�� miyamoto
			checkItem(robotState[0], robotState[1]);

			/* �t���O�ɂ��`�揈�����s�Ȃ� */
			if(isShow) {
				canvas.repaint();
			}
			isMove = true;
		}

		return isMove;
	}


	/**
	 * �w�肳�ꂽ���W���X�^�[�g�ʒu�ɂ��܂��B
	 * @param int x X���W
	 * @param int y Y���W
	 */
	public void setStart(int x, int y) {
		/* �O�̃X�^�[�g������ */
		int[] pos = mapController.getPos("S");
		mapController.set(pos[0], pos[1], "");
		/* �V�����X�^�[�g��ݒ� */
		mapController.set(x, y, "S");
	}

	// 2001.09.05 �ǉ� miyamoto
	public void setItem(int newItem) {
		robotState[2] = newItem;
		controlDoor(newItem);
	}

	/**
	 * �w�肳�ꂽ���W�ɃS�[����ݒ肵�܂��B
	 * @param int x X���W
	 * @param int y Y���W
	 */
	public void setGoal(int x, int y) {

		/* �S�[���������ݒ�s�Ȃ�ȑO�̃S�[�����폜 */
		if(!flagGoals) {
			int[] goalState = getXYGoalState();
			if(goalState != null) {
				mapController.set(goalState[0], goalState[1], "");
			}
		}
		mapController.set(x, y, "O(1)");

		/* ��V�̍X�V */
		renewReward();
	}

	/**
	 * �S�[�����N���A���܂��B
	 */
	public void clearGoal() {
		int[] goalState = getXYGoalState();
		if(goalState != null) {
			mapController.set(goalState[0], goalState[1], "");
		}
	}

	/**
	 * ���̕`��̈���N���b�N���Ċ���ύX����Ƃ��́A�ύX�������
	 * �ݒ肵�܂�
	 * @param String str  ���ɐݒ肷�镶����
	 */
	public void setRenewValue(String str) {
		renewValue = str;
	}

	/**
	 * �`����s�Ȃ����̃t���O��ݒ肵�܂��B
	 * @param boolean b  true:�`��  false:�`��Ȃ�
	 */
	public void setFlagShow(boolean b) {
		isShow = b;
	}


	/**
	 * ���{�b�g�̈ʒu���Z���T���Ŏ擾���܂��B
	 * @return int[8] 
	 */
	public int[] getSenserState() {
		return mapController.getState(robotState[0], robotState[1]);
	}


	private int[] robotPos = new int[2];
	/**
	 * ���{�b�g�̈ʒu���w�x���W�Ŏ擾���܂��B
	 * @return int[] ���݂̍��W
	 *               int[0] x���W
	 *               int[1] y���W
	 */
	public int[] getXYState() {
//		return robotState;
		robotPos[0] = robotState[0];
		robotPos[1] = robotState[1];
		return robotPos;
	}

	/**
	 * �S�[���̈ʒu���Z���T���Ŏ擾���܂��B
	 * @return int[] �S�[���̈ʒu
	 */
	public int[] getSensorGoalState() {
		int[] goalState = getXYGoalState();
		if(goalState == null) {
			return null;
		}
		return mapController.getState(goalState[0], goalState[1]);
	}

	/**
	 * �S�[���̈ʒu���w�x���W�Ŏ擾���܂��B
	 * @return int[] �S�[���̍��W
	 *               int[0] x���W
	 *               int[1] y���W
	 */
	public int[] getXYGoalState() {
		int[] goalState = mapController.getPos("O");
		return goalState;
	}

	/**
	 * �L�[�̈ʒu���w�x���W�Ŏ擾���܂��B
	 * @return int[] �L�[�̈ʒu
	 *               int[0] x���W
	 *               int[1] y���W
	 */
	public int[] getXYKeyState() {
		int[] keyState = mapController.getPos("K");
		return keyState;
	}

	/**
	 * �X�^�[�g�̈ʒu���w�x���W�Ŏ擾���܂��B
	 * @return int[] �X�^�[�g�̈ʒu
	 *               int[0] x���W
	 *               int[1] y���W
	 */
	public int[] getXYStartState() {
		int[] startState = mapController.getPos("S");
		return startState;
	}

	/**
	 * ��V���擾���܂��B
	 * @return double ��V
	 */
	public double getReward() {
		String rewardStr = mapController.getReward(robotState[0],
		        robotState[1]);
		int reward = 0;
		if(!rewardStr.equals("")) {
			reward = Integer.parseInt(rewardStr);
		}
		return reward;
	}

	/**
	 * �J�M���擾���Ă��邩�m�F���܂��B
	 * @param boolean  true:�J�M�������Ă��� false:�J�M�������Ă��Ȃ�
	 */
//	public boolean hasKey() {
//		return key;
//	}
	// 2001.08.08 �ǉ� miyamoto
	/**
	 * �ێ����Ă�����̂��擾
	 * @return 
	 */
	public int getItem() {
//		return item;
		return robotState[2];
	}

	/**
	 * �J�M���Ȃ����܂��B
	 */
//	public void clearKey() {
//		key = false;
//	}
	public void clearItem() {
//		item = NOTHING;
		robotState[2] = NOTHING;
	}

	/**
	 * �h�A���J���Ă��邩�m�F���܂��B
	 * @param boolean  true:�h�A���J���Ă��� false:�h�A�����Ă���
	 */
	public boolean isDoorOpen() {
		// 2001.08.08 �C�� miyamoto
//		return key;
//		if(item == KEY ) {
		if(robotState[2] == KEY ) {
			return true;
		}
		return false;
	}

	// 2001.08.03 �ǉ� miyamoto
	/**
	 * �J���Ă���h�A�����
	 */
	public void closeDoor() {
//	private void closeDoor() {
		/*
		 * �n�}��̑S�ʒu���`�F�b�N
		 * ���Ă���h�A("D")������ΊJ���Ă���h�A("d")�Ɋ�����
		 */
		int[] size = mapController.getSize();
		for(int x = 0; x < size[0]; x++) {
			for(int y = 0; y < size[1]; y++) {
				if( (mapController.getString(x, y)).equals("d") ) {
					mapController.set(x, y, "D");
				}
			}
		}
	}

	/**
	 * �n�}��̎w�肳�ꂽ�ʒu�̏����擾���܂��B
	 * @param int x  x���W
	 * @param int y  y���W
	 */
	public String getMapInfo(int x, int y) {
		return mapController.getString(x, y);
	}

	public String getColorInfo(int x, int y) {
		if(colorMap == null) {
			return null;
		}
		return colorMap[x][y];
	}

	/**
	 * �n�}�̃T�C�Y���擾���܂��B
	 * @return int[] int[0] x�������̃T�C�Y
	 *               int[0] y�������̃T�C�Y
	 */
	public int[] getMapSize() {
		return mapController.getSize();
	}

	public boolean isCollisionDoor() {
		return actionController.isCollisionDoor();
	}

	/**
	 * update���\�b�h�̃I�[�o�[���C�h
	 */
	public void update(Graphics g) {
		canvas.repaint();
		paint(g);
	}

	/**
	 * ��V�̃e�[�u�������݂̒n�}�ɍ��킹�čX�V���܂��B
	 */
	public void renewReward() {
		if( rewardMap != null) {
			int[] size = mapController.getSize();
			for(int x = 0; x < size[0]; x++) {
				for(int y = 0; y < size[1]; y++) {
					String rewardStr = mapController.getReward(x, y);
					rewardMap[x][y] = rewardStr;
				}
			}
		}
	}

	/**
	 * �_�ł����܂��B
	 */
	public void flash() {
		canvas.flash();
	}

	//////////////////////////////////////////////////////////
	// private

	/**
	 * �����Őݒ肳�ꂽ�ʒu�ɃJ�M�����邩�`�F�b�N���A
	 * �J�M������Ε��Ă���h�A���J���܂��B
	 * @param int x x���W
	 * @param int y y���W
	 */
	private void checkItem(int x, int y) {
		/* �J�M�̏ꏊ */
		if( (mapController.getString(x, y)).equals("K") ) {
			/*
			 * �J�M���Ȃ���΁A�J�M�擾�E�h�A�J��
			 * �J�M������΁A�J�M���Ȃ����E�h�A�����
			 */
			if(robotState[2] != KEY ) {
				robotState[2] = KEY;
//				openDoor();
			}else {
				robotState[2] = NOTHING;
//				closeDoor();
			}
//			controlDoor(robotState[2]);
		}
		/* �d�b�̏ꏊ */
		if( (mapController.getString(x, y)).equals("T") ) {
			/*
			 * �d�b���Ȃ���΁A�d�b�擾�E�h�A����
			 * �d�b������΁A�d�b���Ȃ���
			 */
			if(robotState[2] != TELEPHON ) {
				robotState[2] = TELEPHON;
//				closeDoor();
			}else {
				robotState[2] = NOTHING;
			}
		}
// �A�C�e���𑝂₵���ꍇ�p
if((mapController.getString(x, y)).equals("A")) {
	if(robotState[2] != A ) {
		robotState[2] = A;
	}else {
		robotState[2] = NOTHING;
	}
}
if((mapController.getString(x, y)).equals("B")) {
	if(robotState[2] != B ) {
		robotState[2] = B;
	}else {
		robotState[2] = NOTHING;
	}
}
if((mapController.getString(x, y)).equals("a")) {
	if(robotState[2] != a ) {
		robotState[2] = a;
	}else {
		robotState[2] = NOTHING;
	}
}
if((mapController.getString(x, y)).equals("b")) {
	if(robotState[2] != b ) {
		robotState[2] = b;
	}else {
		robotState[2] = NOTHING;
	}
}
// �����܂�
		controlDoor(robotState[2]);
	}

	/* �h�A���J����A�C�e�� */
	private int doorOpenItem = NOTHING;
	/**
	 * �h�A���J����A�C�e����ύX���܂��B
	 * NOTHING = 0;
	 * KEY = 1;
	 * TELEPHON = 2;
	 */
	public void changeDoorOpenItem(int doorOpenItem) {
		this.doorOpenItem = doorOpenItem;
	}

	/**
	 * �A�C�e���ɂ���ăh�A�̐�����s���܂��B
	 */
	private void controlDoor(int item) {
		if(item == doorOpenItem) {
			openDoor();
		}else {
			closeDoor();
		}
	}


	/**
	 * ���Ă���h�A���J����
	 */
	private void openDoor() {
		/*
		 * �n�}��̑S�ʒu���`�F�b�N
		 * ���Ă���h�A("D")������ΊJ���Ă���h�A("d")�Ɋ�����
		 */
		int[] size = mapController.getSize();
		for(int x = 0; x < size[0]; x++) {
			for(int y = 0; y < size[1]; y++) {
				if( (mapController.getString(x, y)).equals("D") ) {
					mapController.set(x, y, "d");
				}
			}
		}
	}

	//////////////////////////////////////////////////
	// �C�x���g����

	/**
	 * �}�E�X�N���b�N�̃C�x���g�������s���C���i�[�N���X
	 */
	class CanvasMouseAdapter extends MouseAdapter {

		/**
		 * �}�E�X���N���b�N���ꂽ���̏���
		 */
		public void mouseClicked(MouseEvent e) {
			/* �N���b�N���ꂽ�ʒu�̎擾 */
			int xPos = e.getX();
			int yPos = e.getY();

			/* �Ԋu���擾 */
			int xSpace = canvas.getXSpace();
			int ySpace = canvas.getYSpace();

			int[] size = mapController.getSize();

			/* �n�}�͈͓̔��ł���Ώ������s�� */
			if( ((xPos>xSpace)&&(yPos>ySpace)) &&
			        ((xPos<(xSpace*(size[0]+1))) &&
			        (yPos<(ySpace*(size[1]+1)))) ) {

				/* �N���b�N���ꂽ�ʒu��n�}��̍��W�ɕϊ� */
				int x = xPos / xSpace;
				int y = yPos / ySpace;

				if(!renewValue.equals("n")) {

					/* �����̃S�[����ݒ肵�Ȃ��ꍇ�͈ȑO�̒l���폜 */
					if(!flagGoals) {
						if(renewValue.equals("O(1)")) {
							int[] pos = mapController.getPos("O");
							if(pos != null) {
								mapController.set(pos[0], pos[1], "");
							}
						}
					}

					/* �n�}���̍X�V */
					mapController.set(x-1, y-1, renewValue);

					/* �ĕ`�� */
					canvas.repaint();
				}
				/* ��V�̍X�V */
				renewReward();
			}
		}

	}


}

