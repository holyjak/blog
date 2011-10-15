/*
 *	Copyright 2005 stat4j.org
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package net.sourceforge.stat4j.util;

import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Name:		Util.java
 * Date:		Sep 4, 2004
 * Description:
 * 
 * 
 * @author Lara D'Abreo
 */
public class Util {
	
	public static ResourceBundle bundle =
		ResourceBundle.getBundle("stat4j");
	
	public static Object createObject(String className) {
		try {
			Class c = Class.forName(className);

			return c.newInstance();

		} catch (Exception e) {

			return null;
		}
	}

	public static String getCategory() {
		return bundle.containsKey("logcategory") ? bundle.getString("logcategory") : "stat4j";
	}
	
	public static Properties createProperties(String context) {
		Properties p = new Properties();
		
		Enumeration keys = bundle.getKeys();

		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			if (key.startsWith(context)) {
				String k = key.substring(context.length());
				String value = bundle.getString(key);

				p.put(k, value);

			} //fi
		} //end while

		return p;
	}

	public static String getValue(String key) {
		try {
			return bundle.getString(key);
		}catch (MissingResourceException e) {
			return null;
		}
	}

	public static boolean getAsBoolean(String val, boolean dfault) {
		try {
			if (val == null) {
				return dfault;
			}
			String s = val.trim().toLowerCase();
			if (s.length() == 0) {
				return dfault;
			};
			return (
				s.equals("true")
					|| s.equals("yes")
					|| s.equals("T")
					|| s.equals("Y"));
		} catch (Exception e) {
			return dfault;
		}
	}

}
