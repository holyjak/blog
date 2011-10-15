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
 * Name:		Minimum
 * Date:		Aug 29, 2004
 * Description:
 * 
 * 
 * @author Lara D'Abreo
 */
public class Maximum extends CalculatorAdapter {

	protected double	maxiumum;
	
	public Maximum() {
		reset();
	}
	
	
	
	public void applyMetric(Metric metric) {
		
		if (metric.isSingle()) {
			applyMaximum(metric.getReading(getStatistic().getUnit()),metric.getTimestamp());
		}else {
			applyMaximum(metric.getReadingDelta(getStatistic().getUnit()),metric.getTimestamp());
		}
	}
	
	protected void applyMaximum(double value,long timestamp) {
		maxiumum = Math.max(maxiumum,value);
		if (maxiumum == value) {
			setTimestamp(timestamp);
		}
	}

	
	public double getResult() {
		return maxiumum;
	}

	public void reset() {
		this.maxiumum = 0.0;
	}

	public void init(String name,Properties properties) {
		super.init(name);
	}
	

}