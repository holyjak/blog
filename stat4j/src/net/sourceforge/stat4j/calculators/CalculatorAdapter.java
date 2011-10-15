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

import net.sourceforge.stat4j.Calculator;
import net.sourceforge.stat4j.Statistic;

/**
 * Name:		CalculatorAdapter.java
 * Date:		Aug 30, 2004
 * Description:
 * 
 * 
 * @author Lara D'Abreo
 */
public abstract class CalculatorAdapter implements Calculator {

	protected Statistic statistic;
	protected String name;
	protected boolean applyImmediate;
	protected boolean applyImmediatePreset;
	protected long timestamp;
	


	public CalculatorAdapter() {
		applyImmediatePreset = false;
	}


	public String getName() {
		return name;
	}

	public void init(String name) {
		this.name = name;
	}

	

	/**
	 * @return
	 */
	public Statistic getStatistic() {
		return statistic;
	}

	/**
	 * @param statistic
	 */
	public void setStatistic(Statistic statistic) {
		this.statistic = statistic;
		if (!isApplyImmediatePreset()) {
			setApplyImmediate(!statistic.isDual());
		}
	}

	/**
	 * @return
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param l
	 */
	public void setTimestamp(long l) {
		timestamp = l;
	}


	
	/**
	 * @return
	 */
	public boolean isApplyImmediate() {
		return applyImmediate;
	}

	/**
	 * @param b
	 */
	public void setApplyImmediate(boolean b) {
		applyImmediate = b;
	}

	/**
	 * @return
	 */
	public boolean isApplyImmediatePreset() {
		return applyImmediatePreset;
	}

	/**
	 * @param b
	 */
	public void setApplyImmediatePreset(boolean b) {
		applyImmediatePreset = b;
	}

}
