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

import java.io.Serializable;

/**
 * Name:		Reading.java
 * Date:		Aug 29, 2004
 * Description:
 * 
 * A reading captured from the log at a point in time.
 * 
 * The reading value is derived from the log message itself
 * using a scrap expression. If no scrap expression is supplied 
 * the value defaults to 1.0.
 * 
 * 
 * @author Lara D'Abreo
 */
public class Reading implements Serializable{

	protected	long	timestamp;
	protected	long	freeMemory;
	protected	double	value;	
	
	
	public Reading() {
		this.timestamp = System.currentTimeMillis();
		this.freeMemory = Runtime.getRuntime().freeMemory();
		this.value = 1.0;
	}
	
	public Reading(double value) {
		this();
		this.value = value;
	}
		
	public double getReading(Unit unit) {
	
		if (unit == Unit.MEMORY) {
			return freeMemory;	
		}else if (unit == Unit.TIME) {
			return timestamp;
		}else {
			return value;
		}
	}
	/**
	 * @return
	 */
	public long getFreeMemory() {
		return freeMemory;
	}

	/**
	 * @return
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @return
	 */
	public double getValue() {
		return value;
	}

}
