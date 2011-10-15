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
 * Name:		Rate.java
 * Date:		Aug 29, 2004
 * Description:
 * 
 * 
 * @author Lara D'Abreo
 */
public class Rate extends CalculatorAdapter {

	
	protected long starttimestamp;
	protected double	rate;
	protected long 		period;
	protected long		occurances;
	
	
	public Rate() {
		reset();
	}

	
	public void applyMetric(Metric metric) {
		
		
		occurances = occurances + 1;
		if (metric.isSingle()) {
	
			double elapsedTime = metric.getTimestamp() - starttimestamp;
			
			if (elapsedTime == 0.0) return;
			double divisor = elapsedTime / period;
			rate = occurances / divisor;		
			//System.out.println("e:" + elapsedTime + " d:" + divisor + " rate:" + rate);
			setTimestamp(metric.getTimestamp());
		}
	}

	
	public double getResult() {
		return rate;
	}

	public void reset() {
		occurances = 0;
		this.rate = 0.0;
		starttimestamp = System.currentTimeMillis();
	}

	public void init(String name,Properties properties) {
		// read per
		String periodStr = properties.getProperty("period","1000");
		this.period = Long.parseLong(periodStr);
		
		super.init(name);
	}
}
