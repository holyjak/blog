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

package net.sourceforge.stat4j;

import java.util.Properties;

/**
 * Name:		Calculator.java
 * Date:		Aug 29, 2004
 * Description:
 * 
 * Calculator contract for all Calculators that calculate/derive
 * statistic values from metrics. Metrics are are collected/scraped 
 * from log messages and applies to calculators to calculate 
 * the statstic value.
 * 
 * @see Metric
 * @see Statistic 
 * 
 * @author Lara D'Abreo
 */
public interface Calculator {
	

	public String getName();
	public boolean isApplyImmediate();
	public void init(String name,Properties properties);
	public Statistic getStatistic();
	public void setStatistic(Statistic statistc);
	public void applyMetric(Metric metric);
	public void reset();
	public double getResult();
	public long	getTimestamp();

}
