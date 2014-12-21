/**
 * ViewerProperty.java
 * Viewer�̏����Ǘ�����N���X
 * COPYRIGHT FUJITSU LIMITED 2001-2002
 * BSC miyamoto 2001.11
 */
package wba.citta.gsa.viewer;

import java.io.*;
import java.util.*;
import java.awt.*;

/**
 * Viewer�̏����Ǘ�����N���X
 */
public class ViewerProperty {

	/* �ݒ�t�@�C���� */
	private static final String PROP_FILE_NAME = "viewer.prop";

	public static int[] agentViewerInitSize = null;
	public static int[] sharedMemoryViewerInitSize = null;
	public static int[] treeViwerInitSize = null;

	/**
	 * �G�[�W�F���gID�ƃG�[�W�F���g�̕\���F�̃e�[�u��
	 */
	public static Hashtable colorTable = null; 

	static {
		colorTable = new Hashtable();
		loadProperty(PROP_FILE_NAME);
	}

	/**
	 * �t�@�C��������̓ǂݍ���
	 * @param String fileName �t�@�C����
	 */
	private static void loadProperty(String fileName) {

		Properties prop = new Properties();

		/* �t�@�C���̓ǂݍ��� */
		try {
			FileInputStream fin = new FileInputStream(fileName);
			prop.load(fin);
			fin.close();
		} catch (FileNotFoundException fnfe) {
			System.out.println(fnfe);
			return;
		} catch (IOException ioe) {
			System.out.println(ioe);
			return;
		}

		StringTokenizer contents;

		try {

			agentViewerInitSize = new int[2];
			contents = new StringTokenizer(prop.getProperty
			        ("AgentViewerInitSize"), "," );
			agentViewerInitSize[0] = new Integer(contents.nextToken()).
			        intValue();
			agentViewerInitSize[1] = new Integer(contents.nextToken()).
			        intValue();

			sharedMemoryViewerInitSize = new int[2];
			contents = new StringTokenizer(prop.getProperty
			        ("SharedMemoryViewerInitSize"), "," );
			sharedMemoryViewerInitSize[0]
			        = new Integer(contents.nextToken()).intValue();
			sharedMemoryViewerInitSize[1]
			        = new Integer(contents.nextToken()).intValue();

			treeViwerInitSize = new int[2];
			contents = new StringTokenizer(prop.getProperty
			        ("FailAgentTreeViewerInitSize"), "," );
			treeViwerInitSize[0] = new Integer(contents.nextToken()).
			        intValue();
			treeViwerInitSize[1] = new Integer(contents.nextToken()).
			        intValue();

		} catch (NullPointerException e){
			System.out.println(e);
			return;
		} catch (NumberFormatException e) {
			System.out.println(e);
			return;
		}


		/* �G�[�W�F���g���̐ݒ�̓Ǎ��� */
		for ( Enumeration e = prop.propertyNames(); e.hasMoreElements();) {
			try{
				/* �G�[�W�F���gID�擾 */
				String key = (String)e.nextElement();
				Integer agid = new Integer(key.trim());

				/* �G�[�W�F���g�̐F�̐ݒ�擾 */
				StringTokenizer stringTokenizer
				        = new StringTokenizer(prop.getProperty(key), ",");
				int[] rgb = new int[3];

				for(int i = 0; i < 3; i++) {
					rgb[i] = new Integer(stringTokenizer.nextToken()).
					        intValue();
				}
				Color color = new Color(rgb[0], rgb[1], rgb[2]);

				/* �G�[�W�F���gID���̕\���F���e�[�u���ɐݒ� */
				colorTable.put(agid, color); 

			} catch (NumberFormatException nfe) {
//				System.out.println(nfe);
			}catch(NoSuchElementException nsee) {
				System.out.println(nsee);
			}
		}

	}
}


