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

/**
 * Name:		Metric.java
 * Date:		Aug 29, 2004
 * Description:
 * 
 * A metric is collected from a log either at a point in time (single reading) or
 * over 2 points in time (dual reading). A dual reading enables deltas such as durations and
 * memory usage to be calculated.
 * 
 * A metric will only be generated for a statsictic if the log message matches the 
 * statistic filter. The filter also defines an optional expression for
 * scaping user defined values from the log.
 * 
 * A reading holds the time,free memory and a user defined
 * value for a point in time. 
 * 
 * Typically metrics that require dual readings will be cached
 * for the first reading and then sent to the statstic calculator once
 * the second reading has been captured. The statistic defines the filters
 * for each reading.
 * 
 * Calculators may override caching by setting  setApplyImmediate to true. RunningTotals are
 * examples of calculators that must be sent  metrics
 * when a match occurs on either statisitic filter.
 * 
 * Metrics may be collected across all threads or 
 * locally within a given thread (thread local). This
 * is so that we can support metrics for a given call stack such
 * as method duration and global metrics such as average user session
 * duration.
 * 
 * 
 * 
 * @see Filter
 * @see Reading 
 * @see Calculator
 * @see Statistic
 * 
 * value scraped from the log taken at one or 2 points in time
 * @author Lara D'Abreo
 */
public class Metric {

	protected String	statisticName;
	protected Reading	firstReading;
	protected Reading	secondReading;
	
	public Metric(String statisticName,Reading reading) {
		this.statisticName = statisticName;
		this.firstReading = reading;
	}
	
	public Metric(String statisticName,Reading first,Reading second) {
			this.statisticName = statisticName;
			this.firstReading = first;
			this.secondReading = second;
		}
	
	
	public boolean isSingle() {
		return ! isDual();
	}
	
	public boolean isDual() {
			return hasFirstReading() && hasSecondReading();
	}
		
	public boolean hasFirstReading() {
		return (firstReading != null);
	}
	
	public boolean hasSecondReading() {
		return (secondReading != null);
	}
	/**
	 * @return
	 */
	public Reading getSecondReading() {
		return secondReading;
	}

	/**
	 * @return
	 */
	public Reading getFirstReading() {
		return firstReading;
	}

	/**
	 * @param reading
	 */
	public void setSecondReading(Reading reading) {
		secondReading = reading;
	}

	
	
	public double getReadingDelta(Unit unit) {
		return secondReading.getReading(unit) - firstReading.getReading(unit);
	}
	
	public double getReading(Unit unit) {
		return firstReading.getReading(unit);
	}
	
	public long getTimestamp() {
		if (hasSecondReading())
			return secondReading.getTimestamp();
		else
			return firstReading.getTimestamp();
	}
	
	/**
	 * @return
	 */
	public String getStatisticName() {
		return statisticName;
	}

}
