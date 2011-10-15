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
package net.sourceforge.stat4j.calculators;

import java.util.Properties;

import net.sourceforge.stat4j.Metric;


/**
 * Name:		Average.java
 * Date:		Aug 29, 2004
 * Description:
 * 
 * 
 * @author Lara D'Abreo
 */
public class Average extends CalculatorAdapter {

	protected double totalValue;
	protected long numberOfReadings;
	protected double average;

	/**
	 * 
	 */
	public Average() {
		reset();
	}
	

	public void applyMetric(Metric metric) {
		

		double value;
		if (metric.isSingle()) {
			value = metric.getReading(getStatistic().getUnit());
		} else {
			value = metric.getReadingDelta(getStatistic().getUnit());

		}
		setTimestamp(metric.getTimestamp());
		applyValue(value);

	}

	protected void applyValue(double value) {
		totalValue = totalValue + value;

		// inc number of readings
		numberOfReadings = numberOfReadings + 1;

		// calculate average
		average = totalValue / numberOfReadings;
	}

	public double getResult() {
		return average;
	}

	public void reset() {
		this.totalValue = 0.0;
		this.numberOfReadings = 0;
		this.average = 0.0;
	}

	public void init(String name, Properties properties) {
		super.init(name);
	}

}
