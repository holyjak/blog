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

import net.sourceforge.stat4j.util.Util;


/**
 * Name:		Statistic.java
 * Date:		Aug 29, 2004
 * Description:
 * 
 * This class represtents a statistic to be evaluated/derived from a log
 * 
 * A statistic consists of a calculator and one or two fiters.
 * 
 * Filters detect matches or conditions in the log. 
 * A match on a filter will trigger a reading to be taken
 * for the statistic.
 *
 * A statistic may be expressed over the units: TIME, MEMORY or VALUE. 
 * Value is the default and is typically just a count or
 * optionally a value scraped from the log message itself using
 * the filter scrap expression.
 * 
 * Typically statistics that define 2 filters yield dual readings.
 * The first reading will be cached and then the statistic calculated
 * when the second readng has been captured. This allows us to
 * support the concept of deltas (durations, memory usage etc).
 * 
 * For a simple statstic that defines just one filter then the reading 
 * will be taken whenever a log matches the first filter. Once the reading 
 * is taken it will be wrapped inside a metric and forwarded on to the 
 * statistic calculator to derive the actual statstic value.
 * 
 * @see Filter
 * @see Metric
 * @see Calculator
 * @see Unit
 * 
 * @author Lara D'Abreo
 */
public class Statistic {

	protected String name;
	protected String description;
	protected String calculator;
	protected Unit unit;
	protected Filter first;
	protected Filter second;
	protected String tag;
	protected boolean captureInThread;

	public Statistic() {

	}
	public void init(String name, Properties properties) {
		this.name = name;
		this.unit = Unit.parse(properties.getProperty("unit", "value"));
		this.description = properties.getProperty("description");
		if (this.description == null) {
			this.description  = this.name;
		}
		this.calculator = properties.getProperty("calculator", "simple");

		this.captureInThread =
			Util.getAsBoolean(properties.getProperty("threadlocal"), true);

		//first filter (mandatory)

		this.first = new Filter();
		this.first.setMatch(properties.getProperty("first.match"));
		this.first.setScape(properties.getProperty("first.scrape"));
		//		second filter (optional)
		String match = properties.getProperty("second.match");
		if (match != null) {
			this.second = new Filter();
			this.second.setMatch(match);
			this.second.setScape(properties.getProperty("second.scrape"));
		} //fi
		
		this.tag = properties.getProperty("tag.match");

	}
	/**
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public Unit getUnit() {
		return unit;
	}

	/**
	 * @param string
	 */
	public void setDescription(String string) {
		description = string;
	}

	/**
	 * @param string
	 */
	public void setName(String string) {
		name = string;
	}

	/**
	 * @param filter
	 */
	public void setStart(Filter filter) {
		first = filter;
	}

	/**
	 * @param unit
	 */
	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	/**
	 * @return
	 */
	public String getCalculator() {
		return calculator;
	}

	/**
	 * @return
	 */
	public Filter getFirst() {
		return first;
	}

	/**
	 * @return
	 */
	public Filter getSecond() {
		return second;
	}

	/**
	 * @param string
	 */
	public void setCalculator(String string) {
		calculator = string;
	}

	/**
	 * @param filter
	 */
	public void setFirst(Filter filter) {
		first = filter;
	}

	/**
	 * @param filter
	 */
	public void setSecond(Filter filter) {
		second = filter;
	}

	/**
	 * @return
	 */
	public boolean isCaptureInThread() {
		return captureInThread;
	}

	/**
	 * @return
	 */
	public String getTag() {
		return tag;
	}
	
	public boolean isDual() {
		return (first != null) & (second != null);
	}

}
