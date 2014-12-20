/**
 * Environment.java
 *  ���̏������s���N���X
 *  COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2000.09 BSC miyamoto
 */
package environment;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
//import java.util.*;

/**
 * ���̏������s���܂��B
 */
public class Environment {

	/* ���j���[�o�[ */
	private MenuBar menuBar;       /* ���j���[�o�[ */
	private Menu fileMenu;         /* �t�@�C�����j���[ */
	private MenuItem loadMenuItem; /* ���[�h���j���[ */

	/* �O���t�B�b�N�\���� */
	private Frame frame;
	private EnvironmentPanel envPanel;

	/* �{�^���p�l�� */
	private Panel buttonPanel;
	private CheckboxGroup  cbg;   /* �`�F�b�N�{�b�N�X�̃O���[�v�� */
	private Checkbox wallCB;      /* �� */
	private Checkbox rewardCB;    /* ��V */
	private Checkbox clearCB;     /* �N���A */
	private Button clearButton;   /* �I�[���N���A�{�^�� */
	private Button repaintButton; /* �ĕ`��{�^�� */

	/* ������̐ݒ� */
//	private MessageCanvas titleCanvas;
	private MessageCanvas itemCanvas;

	private String TITLE;

	////////////////////////////////////////////////////////////
	// �R���X�g���N�^

	/**
	 * �R���X�g���N�^
	 * @param String fileName Map�t�@�C����
	 */
	public Environment(String fileName) {

		envPanel = new EnvironmentPanel(fileName, false/*true*/, false);

		/* �t���[���̏����� */
		initFrame();

	}

	public Environment(String fileName, String title) {
		TITLE = title;
		envPanel = new EnvironmentPanel(fileName, false/*true*/, false);
		/* �t���[���̏����� */
		initFrame();
	}

//	public Environment(String mapFileName, String colorMapFileName) {
//		envPanel = new EnvironmentPanel(mapFileName, colorMapFileName,
//		       false, false);
//
//		/* �t���[���̏����� */
//		initFrame();
//
//	}


	/**
	 * �R���X�g���N�^
	 * @param String fileName Map�t�@�C����
	 */
	public Environment(String[][] fileArray) {

		envPanel = new EnvironmentPanel(fileArray, false, false);

		/* �t���[���̏����� */
		initFrame();

	}


	////////////////////////////////////////////////////////////
	// ����������

	public void initRobotPos() {
		envPanel.initRobotPos();
	}

	public void initMap() {
		/* �J�M�𖳂��� */
//		envPanel.clearKey();
		envPanel.clearItem();
		/* �h�A����� */
		envPanel.closeDoor();
	}

	/**
	 * �t���[���̏�����
	 */
	private void initFrame() {

		/* ���j���[�o�[ */
//		menuBar = new MenuBar();
//		fileMenu = new Menu("�t�@�C��");
//		loadMenuItem = new MenuItem("�Ǎ���");
//		loadMenuItem.addActionListener(new ButtonActionListener());
//		fileMenu.add(loadMenuItem);
//		menuBar.add(fileMenu);

		/* �{�^���p�l�� */
//		buttonPanel = new Panel();
//		cbg = new CheckboxGroup();
//		wallCB = new Checkbox("Wall ", cbg, false);
//		wallCB.addItemListener(new CBItemAdapter());
//		rewardCB = new Checkbox("reward ", cbg, false);
//		rewardCB.addItemListener(new CBItemAdapter());
//		clearCB = new Checkbox("Clear", cbg, false);
//		clearCB.addItemListener(new CBItemAdapter());

//		clearButton = new Button("All Clear");
//		clearButton.addActionListener(new ButtonActionListener());
//		repaintButton = new Button("Repaint");
//		repaintButton.addActionListener(new ButtonActionListener());

//		buttonPanel.setLayout(new GridLayout(7, 1));
//		buttonPanel.add(repaintButton);
//		buttonPanel.add(new Label(""));
//		buttonPanel.add(new Label(""));
//		buttonPanel.add(wallCB);
//		buttonPanel.add(rewardCB);
//		buttonPanel.add(clearCB);
//		buttonPanel.add(clearButton);
//		buttonPanel.setSize(100, 400);

		/*  */
//		if(TITLE != null) {
//			titleCanvas = new MessageCanvas(TITLE, 45, 45);
//			titleCanvas.setSize(700, 50);
//		}
		itemCanvas = new MessageCanvas(
		        "������                            "
		        + "�S�[�����B�� 0" , 45, 25);
		itemCanvas.setSize(524, 40);

		/* �t���[���̍쐬 */
		frame = new Frame("environment");
		frame.setLayout(new BorderLayout(5, 5));
//		frame.setMenuBar(menuBar);
//		if(TITLE != null) {
//			frame.add(titleCanvas, "North");
//		}
		frame.add(itemCanvas, "South");
		frame.add(envPanel, "Center");
//		frame.add(buttonPanel, "East");
//		frame.setSize(840, 865);
		frame.setSize(524, 368);
//		frame.setSize(1280, 1000);
		frame.setVisible(true);

		initImage();
	}

	private int goalReachCount = 0;

	/* �A�C�e���̕\�� */
	private void showItemMessage() {
		int itemID = getItem();
		String item;
		if(itemID == 2) {
			item = "������                            �S�[�����B�� "
			         + goalReachCount;
		}else if(itemID == 1) {
			item = "������                            �S�[�����B�� "
			         + goalReachCount;
		}else {
			item = "������                            �S�[�����B�� "
			         + goalReachCount;
		}
		itemCanvas.setMessage(item);
		itemCanvas.repaint();
	}

	public void countGoalReachCount() {
		goalReachCount++;
	}

	private Image telephoneImage = null;
	private Image keyImage = null;
	private void initImage() {
		ResourceLoader loader = new ResourceLoader();
		telephoneImage = loader.getImage("telephone2.gif");
		keyImage = loader.getImage("key2.gif");
	}

	private void showItemImage() {
		int itemID = getItem();
		if(itemID == 2) {
			itemCanvas.setImage(telephoneImage, 120, 10);
		}else if(itemID == 1) {
			itemCanvas.setImage(keyImage, 120, 10);
		}else {
			itemCanvas.setImage(null, 120, 10);
		}
		itemCanvas.repaint();
	}

	////////////////////////////////////////////////////////////
	// public

	/**
	 * ���{�b�g���w�肳�ꂽ�����ֈړ����܂��B
	 * @param int action  �ړ����� �O�`�V�Őݒ�
	 * @return boolean    true=�ړ����� false=�ړ����s
	 */
	public boolean run(int action) {
//		return envPanel.run(action);
		boolean b = envPanel.run(action);
		showItemMessage();
		showItemImage();
		return b;
	}
	public boolean run(int[] xy, int action) {
		return envPanel.run(xy, action);
	}

	/**
	 * �w�肳�ꂽ���W�ɃS�[����ݒ肵�܂��B
	 * @param int x X���W
	 * @param int y Y���W
	 */
	public void setGoal(int x, int y) {
		envPanel.setGoal(x, y);
	}

	/**
	 * �w�肳�ꂽ���W���X�^�[�g�ʒu�ɂ��܂��B
	 * @param int x X���W
	 * @param int y Y���W
	 */
	public void setStart(int x, int y) {
		envPanel.setStart(x, y);
	}

	// 2001.09.05 �ǉ� miyamoto
	public void setItem(int newItem) {
		envPanel.setItem(newItem);
	}

	/**
	 * ���{�b�g�̈ʒu���Z���T���Ŏ擾���܂��B
	 * @return int[8] 
	 */
	public int[] getSenserState() {
		return envPanel.getSenserState();
	}


	/**
	 * ���{�b�g�̈ʒu���w�x���W�Ŏ擾���܂��B
	 * @return int[] ���݂̍��W
	 *               int[0] x���W
	 *               int[1] y���W
	 */
	public int[] getXYState() {
		return envPanel.getXYState();
	}

	/**
	 * �S�[���̈ʒu���Z���T���Ŏ擾���܂��B
	 * @return int[] �S�[���̈ʒu
	 */
	public int[] getSensorGoalState() {
		return envPanel.getSensorGoalState();
	}

	/**
	 * �S�[���̈ʒu���w�x���W�Ŏ擾���܂��B
	 * @return int[] �S�[���̍��W
	 *               int[0] x���W
	 *               int[1] y���W
	 */
	public int[] getXYGoalState() {
		return envPanel.getXYGoalState();
	}

	/**
	 * �L�[�̈ʒu���w�x���W�Ŏ擾���܂��B
	 * @return int[] �L�[�̈ʒu
	 *               int[0] x���W
	 *               int[1] y���W
	 */
	public int[] getXYKeyState() {
		return envPanel.getXYKeyState();
	}

	public int[] getXYStartState() {
		return envPanel.getXYStartState();
	}

	/**
	 * ��V���擾���܂��B
	 * @return double ��V
	 */
	public double getReward() {
		return envPanel.getReward();
	}

	/**
	 * �J�M���擾���Ă��邩�m�F���܂��B
	 * @param boolean  true:�J�M�������Ă��� false:�J�M�������Ă��Ȃ�
	 */
//	public boolean hasKey() {
//		return envPanel.hasKey();
//	}
	// 2001.08.08 �ǉ� miyamoto
	/**
	 * �ێ����Ă�����̂��擾���܂��B
	 * @return 
	 */
	public int getItem() {
		return envPanel.getItem();
	}

	/**
	 * �h�A���J���Ă��邩�m�F���܂��B
	 * @param boolean  true:�h�A���J���Ă��� false:�h�A�����Ă���
	 */
	public boolean isDoorOpen() {
		return envPanel.isDoorOpen();
	}

	/**
	 * �n�}��̎w�肳�ꂽ�ʒu�̏����擾���܂��B
	 * @param int x  x���W
	 * @param int y  y���W
	 */
	public String getMapInfo(int x, int y) {
		return envPanel.getMapInfo(x, y);
	}

	/**
	 * �n�}��̎w�肳�ꂽ�ʒu�̑������擾���܂��B
	 * @param int x  x���W
	 * @param int y  y���W
	 * @return String �ʒu�̑��� 
	 */
	public String getAttribute(int x, int y) {
		String str = envPanel.getMapInfo(x, y);
		String attr = null;
		if(str.equals("K")) {
			attr = "K";
		}else if(str.equals("d")) {
			attr = "d";
		}else if(str.equals("O(1)")) {
			attr = "O";
	// 2001.08.08 �ǉ� miyamoto
		}else if(str.equals("T")) {
			attr = "T";
		}else {
// 2001.07.13 �C�� �󔒂͂m���o��
			attr = "N";
//			attr = "";
		}
		return attr;
	}

	public String getFloorColor(int x, int y) {
		String str = envPanel.getColorInfo(x, y);
		String color = null;
		if(str == null) {
		}else if(str.equals("r")) {
			color = "r";
		}else if(str.equals("g")) {
			color = "g";
		}else if(str.equals("b")) {
			color = "b";
		}else {
			color = "w";
		}
		return color;
	}


	/**
	 * �n�}�̃T�C�Y���擾���܂��B
	 * @return int[] int[0] x�������̃T�C�Y
	 *               int[0] y�������̃T�C�Y
	 */
	public int[] getMapSize() {
		return envPanel.getMapSize();
	}

	public boolean isCollisionDoor() {
		return envPanel.isCollisionDoor();
	}

	/*
	 * ���̕`����s�Ȃ����ǂ����̃t���O��ݒ肵�܂��B
	 * @param boolean b   true�F�`����s�Ȃ�  false�F�`����s�Ȃ�Ȃ�
	 */
	public void setFlagShow(boolean b) {
		envPanel.setFlagShow(b);
	}

	public void changeDoorOpenItem(int doorOpenItem) {
		envPanel.changeDoorOpenItem(doorOpenItem);
	}

	public void flash() {
		try {
			for(int i = 0; i < 3; i++) {
				envPanel.flash();
				envPanel.repaint();
				Thread.sleep(100);
				envPanel.repaint();
				Thread.sleep(100);
			}
		}catch( Exception e ) {
			System.out.println(e.toString());
		}
	}

	///////////////////////////////////////////////////////////
	// �C�x���g�����̃C���i�[�N���X

	/**
	 * �`�F�b�N�{�b�N�X�̃C�x���g�������s���C���i�[�N���X
	 */
	class CBItemAdapter implements ItemListener {

		/**
		 * �A�C�e�����I�����ꂽ���̏���
		 */
		public void itemStateChanged(ItemEvent e) {
			if(e.getSource() == wallCB) {        /* �� */
				envPanel.setRenewValue("W");
			}else if(e.getSource() == rewardCB) {  /* ��V */
				envPanel.setRenewValue("O(1)");
			}else if(e.getSource() == clearCB) { /* �N���A */
				envPanel.setRenewValue("");
			}
		}
	}


	/**
	 * �{�^���̃C�x���g�������s�Ȃ��C���i�[�N���X
	 */
	class ButtonActionListener implements ActionListener {

		/**
		 * �{�^�����N���b�N���ꂽ���̏���
		 */
		public void actionPerformed(ActionEvent e) {
//			/* �ĕ`�� */
//			if(e.getSource() == repaintButton) {
//				canvas.repaint();
//			/* �N���A */
//			}else if(e.getSource() == clearButton) {
//				for(int x = 0; x < map.length; x ++) {
//					for(int y = 0; y < map[0].length; y++) {
//						map[x][y] = "";
//					}
//				}
//				canvas.repaint();
//			/* ���j���[ �t�@�C���Ǎ��� */
//			}else if(e.getSource() == loadMenuItem) {
//				/* �t�@�C�����̎擾 */
//				FileDialog fDialog = new FileDialog(frame, "�t�@�C���̓Ǎ���");
//				fDialog.setVisible(true);
//				String dirName = fDialog.getDirectory();
//				String fileName = fDialog.getFile();
//
//				/* �t�@�C���̏����擾 */
//				initMapInfo(dirName + fileName);
//
//				/* �L�����o�X�̏����� */
//				canvas.initCanvas(map, robotState);
//				canvas.repaint();
//			}
		}

	}

	//////////////////////////////////////////
	// �e�X�g�p

	// �e�X�g�p�̃��C�����\�b�h
	/**
	 * @param String[] args args[0]:���̃t�@�C���� 
	 *                      args[1]:���̂h�c
	 *                      args[2]:�h�A���J������(���̂h�c)
	 *                      args[3]:���O�̏o�͐�
	 *                      args[4]:�\���̗L��
	 *                      args[5]:�J��Ԃ��񐔂̏��
	 */ 
	public static void main(String args[]) {
		try {
			Environment env = new Environment(args[0]);
			env.changeDoorOpenItem((new Integer(args[2])).intValue());
			java.util.Random random = new java.util.Random(0);

			/* ���O�̏o�͐� */
			String fileName = args[3];
			FileOutputStream fileOutputStream = new FileOutputStream(fileName);
			PrintStream printStream = new PrintStream(fileOutputStream);
//			PrintStream printStream = System.out;

			/* �\���̗L���̐؂芷�� */
			Boolean bl = new Boolean(args[4]);
			env.setFlagShow(bl.booleanValue());

			int maxIter = -1; // BABA ����񐔂�ݒ�
			if(args.length > 4) maxIter = Integer.parseInt(args[5]); // BABA ����񐔂�ݒ�
			int iter = 0; // BABA ����񐔂�ݒ�
			while((maxIter == -1) || (iter < maxIter)) { // BABA ����񐔂�ݒ�
				int action = random.nextInt(4);
				env.run(action*2);
				int[] state = env.getXYState();
				String str = state[0] + "," + state[1] + "," + (action*2) + ","
				        + env.getAttribute(state[0], state[1]) + ","
				        + env.getItem() + "," + args[1] + "," + args[2];
				printStream.println(str);
				iter++; // BABA ����񐔂�ݒ�
			}
			// 2001.08.10 �ǉ� miyamoto
			printStream.close();
			fileOutputStream.close();
		}catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		// 2001.08.10 �ǉ� miyamoto
		System.exit(0);
	}

}



