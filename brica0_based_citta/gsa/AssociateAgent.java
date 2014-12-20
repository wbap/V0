/**
 * AssociateAgent.java
 * �A�z�������s�Ȃ�GSA�̃G�[�W�F���g
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.07
 */
package gsa;

import java.util.*;
import java.io.*;

/**
 * �A�z�������s�Ȃ�GSA�̃G�[�W�F���g
 */
public class AssociateAgent extends Agent{

	/* �o��������Ԃ�ێ�����e�[�u�� */
	private LinkedList stateList = null;
	private Hashtable stateTable = null;

	/* �A�z���邽�߂̃L�[�Ƃ��̕]���l��ێ�����e�[�u�� */
	private Hashtable profitTable = null;

	/* �A�z���邽�߂̃L�[�̑J�ڂ�ێ�����e�[�u�� */
	private LinkedList keyBuffer = null;


	//////////////////////////////////////////////////////////////////////////
	// �R���X�g���N�^

	/**
	 * �R���X�g���N�^
	 * @param int agid  �G�[�W�F���gID
	 * @param boolean[] useNode  �m�[�h�̎g�p�A�s�g�p��ݒ肵���z��
	 * @param SharedMemory sharedMemory  state�Egoal���Ǘ����鋤�L������
	 */
	public AssociateAgent(int agid, boolean[] useNode,
	         SharedMemory sharedMemory) {
		super(agid, useNode, sharedMemory);

		stateList = new LinkedList();
		stateTable = new Hashtable();
		profitTable = new Hashtable();
		keyBuffer = new LinkedList();
	}


	//////////////////////////////////////////////////////////////////////////
	// public

	/**
	 * �G�[�W�F���g�ŗL�̊w�K�������s�Ȃ��܂��B<BR>
	 * @param Vector state ���݂̏��
	 * @param boolean flagGoalReach �S�[���ւ̓��B��\���t���O
	 * @param double profit ��V
	 */
	public void learn(Vector state, boolean flagGoalReach, double profit) {
		stateLearning(state);
		if(flagGoalReach) {
//			profitLearning();
			profitLearning(profit);
		}
	}


	/**
	 * �G�[�W�F���g�ŗL�̎��s�������s�Ȃ��܂��B<BR>
	 * @param Vector state ���݂̏��
	 * @param Vector goalElementArray GoalStackElement��Vector
	 * @return Vector �T�u�S�[��
	 */
	public Vector execProcess(Vector state, Vector goalElement) {

		/* �ݒ�G�[�W�F���g���Ƃɕ��� */
		Vector[] subsetGoalsElement = getSubsetGoals(goalElement);

		/* �L�[��I�� */
		Vector selectedKeyElement
		        =  selectKey(goalElement, subsetGoalsElement);

		/* �L�[�ɑΉ������Ԃ�A�z */
		Vector selectedKeyValue = getGoalValueArray(selectedKeyElement);
		Vector subgoal = associateSubgoal(selectedKeyValue);

		/* �A�z�Ɏg�p�����L�[���X�^�b�N����폜 */
//		remove(selectedKeyElement);

		return subgoal;
	}

	/**
	 * �w�K���ʂ��t�@�C���ɕۑ����܂��B
	 * @param String fileName�t�@�C����
	 */
	public void save(String fileName) {
		System.out.println("Saving learning data....");
		try{
			/* �X�g���[���̍쐬 */
			FileOutputStream ostream = new FileOutputStream(fileName, false);
			ObjectOutputStream oOutputStream = new ObjectOutputStream(ostream);

			/* �I�u�W�F�N�g�̏������� */
			oOutputStream.writeObject(stateList);
//			oOutputStream.writeObject(profitTable);

			oOutputStream.flush();

			oOutputStream.close();
			ostream.close();

		}catch(Exception e){
			System.out.println(e);
		}
	}


	/**
	 * �w�K���ʂ��t�@�C������ǂݍ��݂܂��B
	 * @param String fileName �t�@�C����
	 */
	public void load(String fileName) {
		System.out.println("Loading learning data....");
		try{
			/* �X�g���[���̍쐬 */
			FileInputStream istream = new FileInputStream(fileName);
			ObjectInputStream oInputStream = new ObjectInputStream(istream);

			/* �I�u�W�F�N�g�̏������� */
			stateList = (LinkedList)oInputStream.readObject();
//			profitTable = (Vector)oInputStream.readObject();

			oInputStream.close();
			istream.close();

		}catch(Exception e){
			System.out.println(e);
		}

		/* stateList�̗v�f��stateTable�ɐݒ� */
		ListIterator listIterator = stateList.listIterator();
		while(listIterator.hasNext()) {
			Vector state = (Vector)listIterator.next();
			stateTable.put(state, state);
		}
	}

	/**
	 * GSA�N���X��reset()���\�b�h����Ăяo����܂��B<BR>
	 * �A�z�̃L�[�Ƃ��Ďg�p�������̗̂������N���A���܂��B<BR>
	 */
	public void reset() {
		keyBuffer.clear();
	}

	/**
	 * Agent�N���X���p�����č쐬���Ă��邽�߁A�`���I�Ɏ���<BR>
	 */
	public void suspend() {
	}


	/////////////////////////////////////////////////////////////
	// private

	/**
	 * ��Ԃ̊w�K���s���܂��B
	 */
	private void stateLearning(Vector state) {
		if(stateTable.get(state) == null) {
			stateList.add(state);
			/* ������Ԃ𕡐��ݒ肵�Ȃ����߂̃e�[�u�� */
			stateTable.put(state, state);
		}
	}


	/**
	 * �����Ŏw�肳�ꂽ�S�[����ݒ肵���G�[�W�F���g���ɕ����B
	 * �ݒ�G�[�W�F���g���̃S�[���Ƃ���Vector�̔z��Ŏ擾���܂��B
	 * @param Vector goal
	 * @return Vector[] �G�[�W�F���g���̃S�[���z��
	 */
	private Vector[] getSubsetGoals(Vector goal) {

		if(goal == null) {
			return null;
		}

		int[] agids = getAgids(goal);

		/* �G�[�W�F���gID����Vector�ɕ��� */
		Vector[] subsetGoals = new Vector[agids.length];
		for(int i = 0; i < agids.length; i++) {
			subsetGoals[i] = new Vector();
			for(int m = 0; m < goal.size(); m++) {
				GoalStackElement e = (GoalStackElement)goal.get(m);
				if( (e != null) && (e.agid == agids[i]) ) {
					subsetGoals[i].add(e);
				}else {
					subsetGoals[i].add(null);
				}
			}
		}

		return subsetGoals;
	}


	/**
	 * ������goal����S�G�[�W�F���gID���擾���܂��B
	 * @return int[] �G�[�W�F���gID�̔z��
	 */
	private int[] getAgids(Vector goal) {
		Hashtable agidTable = new Hashtable();
		Vector agidArray = new Vector();
		for(int i = 0; i < goal.size(); i++) {
			GoalStackElement e = (GoalStackElement)goal.get(i);
			if( e != null ) {
				Integer integer = (Integer)agidTable.get(new Integer(e.agid));
				if(integer == null) {
					agidTable.put(new Integer(e.agid),new Integer(e.agid));
					agidArray.add(new Integer(e.agid));
				}
			}
		}
		int[] agids = new int[agidArray.size()];
		for(int i = 0; i < agidArray.size(); i++) {
			agids[i] = ((Integer)agidArray.get(i)).intValue();
		}
		return agids;
	}


	/* �]���l�������ꍇ�����_���ɃL�[��I�����邽�߂̗��� */
	private Random random = new Random(0);
	/**
	 * �����Őݒ肳�ꂽ�����̕����S�[�����炠�镔���S�[����I�����܂��B
	 * @param Vector   Element
	 * @param Vector[] Element
	 * @return Vector  Element
	 */
	private Vector selectKey(Vector goalElement, Vector[] subsetGoalsElement) {

		Vector selectedSubset = null;

		if(subsetGoalsElement != null) {

			Vector goalValue = getGoalValueArray(goalElement);

			// ���o�͂�����ɂ��邩�ǂ����H
			int loopNum = subsetGoalsElement.length + 1;
//			int loopNum = subsetGoalsElement.length;

			double[] profits = new double[loopNum];

			for(int i = 0; i < loopNum; i++) {

				/* �]���l���ݒ肳��Ă���^���쐬 ���o�͂ɂ��Ă��擾 */
				Vector subsetGoalValue = null;
				if(i < subsetGoalsElement.length) {
					subsetGoalValue = getGoalValueArray(subsetGoalsElement[i]);
				}
				Vector obj = new Vector();
				obj.add(goalValue);
				obj.add(subsetGoalValue);

				/* �e�]���l��z��ɐݒ� */
				Double profitD = (Double)profitTable.get(obj);
				if(profitD != null) {
					profits[i] = profitD.doubleValue();
				}else{
					// �����l�T�O
					profits[i] = 10;
				}
			}

			/* �]���l���ő�̂��̂�I�� */
//			int index = selectMaxProfit(profits);
			/* �]���l���������̂����m���őI�� */
			int index = selectProfit(profits);
//			int index = selectProfitExp(profits);

			if(index < subsetGoalsElement.length) {
				selectedSubset = subsetGoalsElement[index];
			}

			/* �I�����ꂽ�L�[�𗚗��ɓo�^ */
			Vector selectedSubsetValue = getGoalValueArray(selectedSubset);
			Vector obj = new Vector();
			obj.add(goalValue);
			obj.add(selectedSubsetValue);

			keyBuffer.add(obj);
		}

		return selectedSubset;
	}

	/**
	 * �����̕]���l�̔z�񂩂�ő�̕]���l�����C���f�b�N�X���擾���܂��B
	 */
	private int selectMaxProfit(double[] profits) {
		/* �ő�̕]���l�̂��̂�I�� */
		int sameElmNum = 0;
		int[] sameElms = new int[profits.length];
		double selectedProfit = -1;
		for(int i = 0; i < profits.length; i++) {
			if(profits[i] > selectedProfit) {
				selectedProfit = profits[i];
				sameElms = new int[profits.length];
				sameElmNum = 0;
			}
			sameElms[sameElmNum] = i;
			sameElmNum++;
		}
		int randomValue = random.nextInt(sameElmNum);
		return sameElms[randomValue];
	}


	/**
	 * �����̕]���l�̔z�񂩂�m���I�ɕ]���l��I�������̃C���f�b�N�X��
	 * �擾���܂��B
	 * �]���l�͍������̂قǍ��m���őI������܂��B
	 */
	Random randomKeySelect = new Random(0);
	private int selectProfit(double[] profits) {

		// �]���l�̍��v
		double totalProfit = 0;
		for(int i = 0; i < profits.length; i++) {
			totalProfit += profits[i];
		}

		int r = randomKeySelect.nextInt(100);

System.out.println(" total  " + totalProfit);
System.out.println(" random " + r);

		double d = 0;
		int index = 0;
		for(; index < profits.length; index++) {
System.out.println(" profit " + index + " " +  profits[index]);
			d += profits[index] / totalProfit * 100;
			if(d > r) {
				break;
			}
		}

System.out.println(" select index " + index );

		return index;
	}

	private final double T = 0.999;
	/**
	 * �����̕]���l�̔z�񂩂�m���I�ɕ]���l��I�������̃C���f�b�N�X��
	 * �擾���܂��B
	 * �]���l�͍������̂قǍ��m���őI������܂��B
	 */
	private int selectProfitExp(double[] profits) {

System.out.println(" ####################  ");
		// �]���l�̍��v
		double totalProfitExp = 0;
		for(int i = 0; i < profits.length; i++) {
			totalProfitExp += Math.exp(profits[i]/T);
System.out.println("   profit " + profits[i]);
System.out.println("   exp    " + Math.exp(profits[i]/T));
		}

		int r = randomKeySelect.nextInt(100);

System.out.println(" ########## select key ##########  ");
System.out.println("  total exp " + totalProfitExp);
System.out.println("  random " + r);

		double d = 0;
		int index = 0;
		for(; index < profits.length; index++) {
System.out.println("  index " + index);
System.out.println("   profit " + profits[index]);
			d += Math.exp(profits[index]/T) / totalProfitExp * 100;
System.out.println("   exp    " + Math.exp(profits[index]/T));
			if(d > r) {
				break;
			}
		}

System.out.println(" select index " + index );

		return index;
	}


	/**
	 * �����Ŏw�肳�ꂽ�L�[����o���ς݂̏�Ԃ�A�z
	 * @param Vector key �A�z����L�[(value)
	 * @return Vector    �A�z���ꂽ���
	 */
	private Vector associateSubgoal(Vector key) {
		ListIterator listIterator = stateList.listIterator();
		while(listIterator.hasNext()) {
			Vector state = (Vector)listIterator.next();
//			if( isEqualValidElement(key, state) ) {
			if( Util.equalsValidElement(key, state) ) {
				return state;
			}
		}
		return null;
	}

	/**
	 * �����Ŏw�肳�ꂽ�v�f�ƁA���̗v�f�𓯂��G�[�W�F���g�ɐݒ肳��Ă���
	 * �v�f���X�^�b�N����폜���܂��B
	 */
//	private void remove(Vector keyElement) {
//
//		if(keyElement == null) {
//			return;
//		}
//
//		for(int i = 0; ;i++) {
//			Element e = (Element)keyElement.get(i);
//			if(e != null) {
//				removeGoalAtAgid(e.agid);
//				break;
//			}
//		}
//	}


	///////////////////////////////////////////////////////////////////
	// 

	/* �萔 */
	private final double REWARD = 100; /* ��V */
	private final double GAMMA = 0.9;
	private final double BETA = 0.1;
	/**
	 * �]���l�̊w�K���s���܂��B
	 */
	private void profitLearning() {
		profitLearning(REWARD);
	}

	/**
	 * �]���l�̊w�K���s���܂��B
	 */
	private void profitLearning(double reward) {

		Hashtable checkTable = new Hashtable();

		ListIterator li = keyBuffer.listIterator(keyBuffer.size());
		int i = 0;
		while(li.hasPrevious()) {
			Vector key = (Vector)li.previous();

Integer count = (Integer)keyCountTable.get(key);
int newCount = 1;
if(count != null) {
	newCount = count.intValue() + 1;
}
keyCountTable.put(key, new Integer(newCount));

			/* �e�[�u���ɓ�����Ԃ�����΃v���t�B�b�g�̍X�V���s�Ȃ�Ȃ� */
			Vector v = (Vector)checkTable.get(key);
			if(v == null) {
				checkTable.put(key, key);
				Double profitD = (Double)profitTable.get(key);
				double profit = 0;
				if(profitD != null) {
					profit = profitD.doubleValue();
				}else {
					// �����l�T�O
					profit = 10;
				}
				/* ��S = ��(��^nP(t) - Si(t)) */
				profit = profit + BETA*(Math.pow(GAMMA,i)*reward - profit);
				profitTable.put(key, new Double(profit));
			}
			i++;
		}

if(reward <= 0) {
	zeroRewardCount++;
}
		// �S�e�[�u���̏󋵂�\��
		printProfitTable();
	}


	private Hashtable keyCountTable = new Hashtable();
	

	/**
	 * �]���l�̃e�[�u���̏�Ԃ��o�́B
	 */
	private int zeroRewardCount = 0;
	private int count = 0;
	private void printProfitTable() {
		count++;
		Enumeration e = profitTable.keys();
		System.out.println(" ** Value Table ** ");
		System.out.println("  count " + count);
		System.out.println("  zero reward count " + zeroRewardCount);
		while( e.hasMoreElements() ) { 
			Vector key = (Vector)e.nextElement();
			Double d = (Double)profitTable.get(key);
			Integer count = (Integer)keyCountTable.get(key);
			System.out.println("      key: " + key.get(0)  + ":" + key.get(1)
				       +  " profit:" + d.doubleValue() + " count:" + count );
		}
if(count == 100) {
	while(true) {
	}
}
	}


}
