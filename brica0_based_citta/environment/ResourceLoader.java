/**
 * ResourceLoader.java
 * ���\�[�X�̓ǂݍ��݂��s�Ȃ��N���X�ł�
 *  COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2000.09 BSC miyamoto
 */ 
package environment;

import java.net.*;
import java.awt.*;

/**
 * ���\�[�X�̓ǂݍ��݂��s�Ȃ��N���X�ł�
 */
public class ResourceLoader extends ClassLoader{
	public Image getImage(String str){
		Image image = null;
		URL url = null;
		url = getResource(str);
//System.out.println("str : " + str);
//System.out.println("url : " + url);
		if (null != url) {
			image = Toolkit.getDefaultToolkit().createImage(url);
		}
		return image;
	}
}


