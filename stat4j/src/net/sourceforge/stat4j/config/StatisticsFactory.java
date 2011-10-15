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

import java.util.Properties;

import net.sourceforge.stat4j.Alert;
import net.sourceforge.stat4j.Calculator;
import net.sourceforge.stat4j.Statistic;


/**
 * Name:		StatisticsFactory.java
 * Date:		Sep 1, 2004
 * Description:
 * 
 * 
 * @author Lara D'Abreo
 */
public interface StatisticsFactory {
	
	public void init(Properties properties);
	public Calculator createCalculator(String name, Statistic statistic);
	public Statistic[] loadStatistics();
	public Alert[] loadAlerts();
	
}
