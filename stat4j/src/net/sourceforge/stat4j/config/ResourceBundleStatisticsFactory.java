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
package net.sourceforge.stat4j.config;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import net.sourceforge.stat4j.Alert;
import net.sourceforge.stat4j.Calculator;
import net.sourceforge.stat4j.Statistic;
import net.sourceforge.stat4j.util.Util;



/**
 * Name:		ResourceBundleStatisticsFactory.java
 * Date:		Sep 1, 2004
 * Description:
 * 
 * 
 * @author Lara D'Abreo
 */
public class ResourceBundleStatisticsFactory implements StatisticsFactory {

	protected ResourceBundle bundle;

	public ResourceBundleStatisticsFactory() {
	}

	public Calculator createCalculator(String name, Statistic statistic) {
		String prefix = "calculator." + name + ".";
		//System.out.println(prefix + "classname");
		String className = bundle.getString(prefix + "classname");
		Calculator c = (Calculator) Util.createObject(className);
		Properties p = Util.createProperties(prefix);
		c.init(name, p);
		c.setStatistic(statistic);
		return c;
	}

	public Alert createAlert(String name) {
		String prefix = "alert." + name + ".";
		Alert a = new Alert();
		Properties p = Util.createProperties(prefix);
		a.init(name, p);
		return a;
	}

	public Statistic createStatistic(String name) {
		String prefix = "statistic." + name + ".";
		Statistic s = new Statistic();
		Properties p = Util.createProperties(prefix);
		s.init(name, p);
		return s;
	}

	public void init(Properties properties) {
		String baseName = properties.getProperty("bundle");
		bundle = ResourceBundle.getBundle(baseName);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.stat4j.config.StatisticsFactory#loadStatistics()
	 */
	public Statistic[] loadStatistics() {
		// get all keys prefixed wth statistic
		Map stats = new HashMap();
		String prefix = "statistic.";
		Enumeration keys = bundle.getKeys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			if (key.startsWith(prefix)) {

				int dot = key.indexOf('.', prefix.length());
				String name = key.substring(prefix.length(), dot);

				if (!stats.containsKey(name)) {

					Statistic s = createStatistic(name);
					stats.put(name, s);
				}
			} //fi
		} // end while
		return (Statistic[]) stats.values().toArray(new Statistic[0]);

	}

	public Alert[] loadAlerts() {
		// get all keys prefixed wth statistic
		Map alerts = new HashMap();
		String prefix = "alert.";
		Enumeration keys = bundle.getKeys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			if (key.startsWith(prefix)) {

				int dot = key.indexOf('.', prefix.length());
				String name = key.substring(prefix.length(), dot);

				if (!alerts.containsKey(name)) {

					Alert a = createAlert(name);
					alerts.put(name, a);
				}
			} //fi
		} // end while
		return (Alert[]) alerts.values().toArray(new Alert[0]);

	}

}
