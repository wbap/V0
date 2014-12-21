/**
 * ResourceLoader.java
 * リソースの読み込みを行なうクラスです
 *  COPYRIGHT FUJITSU LIMITED 2001-2002
 *  2000.09 BSC miyamoto
 */ 
package wba.citta.environment;

import java.net.*;
import java.awt.*;

/**
 * リソースの読み込みを行なうクラスです
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


