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

package net.sourceforge.stat4j.filter;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import net.sourceforge.stat4j.Alert;
import net.sourceforge.stat4j.Calculator;
import net.sourceforge.stat4j.Filter;
import net.sourceforge.stat4j.Metric;
import net.sourceforge.stat4j.Reading;
import net.sourceforge.stat4j.Statistic;
import net.sourceforge.stat4j.Unit;
import net.sourceforge.stat4j.config.StatisticsFactory;
import net.sourceforge.stat4j.util.Util;



/**
 * Name:		MetricCollector.java
 * Date:		Sep 1, 2004
 * Description:
 * 
 * 
 * @author Lara D'Abreo
 */
public class MetricCollector {

	private static MetricCollector instance = null;

	// Reading caches - one for within threads
	// and the other global
	protected ThreadLocal localReadingCache = new ThreadLocal() {
		protected synchronized Object initialValue() {
			return new HashMap();
		}
	};
	protected Map readingCache;

	// Stats
	protected Map statistics;
	protected Map calculators;
	protected Map alerts;

	// Scraper
	protected RegExpScraper scraper;

	// Filters
	protected FilterStatisticMap[] filters;
	
	// Alerts

	public MetricCollector() {
		this.scraper = new RegExpScraper();
		this.readingCache = new HashMap();
		this.calculators = new HashMap();
		this.statistics = new HashMap();
		this.alerts = new HashMap();
	}

	/**
	 * Reset all statistic values to 0
	 */
	public void reset() {
		Iterator itr = calculators.keySet().iterator();
		while (itr.hasNext()) {
			Object key = itr.next();
			Calculator c = (Calculator) calculators.get(key);
			c.reset();
		}
	}

	public void init() {
		// load factory
		String factoryName = Util.getValue("factory.name");
		String prefix = "factory." + factoryName + ".";
		String className = Util.getValue(prefix + "classname");

		StatisticsFactory factory =
			(StatisticsFactory) Util.createObject(className);
		Properties p = Util.createProperties(prefix);
		factory.init(p);

		// load stats to collect/calculate
		Statistic[] stats = factory.loadStatistics();
		
		// load alerts
		Alert[] alerts = factory.loadAlerts();
		
		for (int i = 0; i < alerts.length; ++i) {
			Alert a = alerts[i];

			this.alerts.put(a.getStatisticName(), a);
		}
		

		HashMap filterMap = new HashMap();

		// Map statistics
		for (int i = 0; i < stats.length; ++i) {
			Statistic s = stats[i];

			statistics.put(s.getName(), s);

			Calculator c = factory.createCalculator(s.getCalculator(), s);
			calculators.put(s.getName(), c);

			// Map filters by regexpression
			// to ensure we only match on a given 
			// expression once
			String matchExp = s.getFirst().getMatch();
			FilterStatisticMap f = (FilterStatisticMap) filterMap.get(matchExp);
			if (f == null) {
				f = new FilterStatisticMap(matchExp);
				filterMap.put(matchExp, f);
			}
			f.getFirsts().add(s);

			if (s.getSecond() != null) {

				matchExp = s.getSecond().getMatch();
				f = (FilterStatisticMap) filterMap.get(matchExp);
				if (f == null) {
					f = new FilterStatisticMap(matchExp);
					filterMap.put(matchExp, f);
				}
				f.getSeconds().add(s);
			} //fi
		}

		filters =
			(FilterStatisticMap[]) filterMap.values().toArray(
				new FilterStatisticMap[0]);

	};

	public static synchronized MetricCollector getInstance() {
		if (instance == null) {
			instance = new MetricCollector();
			instance.init();
		}
		return instance;
	}

	public void applyLog(String log, Throwable throwable) {
		// Iterate over filters and see if
		// a match has occured
        String throwableInfo = (throwable == null)? "" : throwable.toString() + " <- ";
        String fullLog = throwableInfo + log;
		for (int i = 0; i < filters.length; ++i) {
			if (filters[i].isMatch(fullLog)) {
				generateMetrics(filters[i], fullLog);
			}
		} //rof

	}

	protected void generateMetrics(FilterStatisticMap map, String log) {
		processReadingsThatMatchFirstFilters(map, log);
		processReadingsThatMatchSecondFilters(map, log);
	}

	private void processReadingsThatMatchSecondFilters(
		FilterStatisticMap filter,
		String log) {

		ArrayList seconds = filter.getSeconds();
		for (int i = 0; i < seconds.size(); ++i) {
			Statistic statistic = (Statistic) seconds.get(i);

			// Get Calculator
			Calculator calculator =
				(Calculator) calculators.get(statistic.getName());

			// Generate Reading
			Reading reading =
				generateReading(statistic, statistic.getSecond(), log);
			if (reading == null)
				continue;

			if (calculator.isApplyImmediate()) {
				Metric metric = new Metric(statistic.getName(), null, reading);
				calculateStatistic(calculator, metric);

			} else {
				matchReading(statistic, calculator, reading);
			}
		} // rof

	}

	private void processReadingsThatMatchFirstFilters(
		FilterStatisticMap filter,
		String log) {
		ArrayList firsts = filter.getFirsts();
		for (int i = 0; i < firsts.size(); ++i) {
			Statistic statistic = (Statistic) firsts.get(i);

			// Get Calculator
			Calculator calculator =
				(Calculator) calculators.get(statistic.getName());

			// Generate Reading
			Reading reading =
				generateReading(statistic, statistic.getFirst(), log);
			if (reading == null)
				continue;

			if (calculator.isApplyImmediate()) {
				Metric metric = new Metric(statistic.getName(), reading);
				calculateStatistic(calculator, metric);

			} else {
				cacheReading(statistic, reading);
			}
		} // rof
	}

	protected Reading generateReading(
		Statistic statistic,
		Filter filter,
		String log) {

		if (statistic.getUnit().equals(Unit.VALUE)) {
			double value = 1.0;

			// scrap value if scraping required
			if (filter.getScape() != null) {
				Double scrapValue =
					scraper.scrapUserDefinedValue(log, filter.getScape());

				if (scrapValue != null) {
					value = scrapValue.doubleValue();
				} else {
					// scrape failed
					return null;
				}
			}
			return new Reading(value);
		} else {
			return new Reading();
		}
	}

	protected Map getReadingCache(Statistic statistic) {
		if (statistic.isCaptureInThread()) {
			// look for match locally
			return (Map) localReadingCache.get();
		} else {
			return readingCache;
		}
	}
	protected void matchReading(
		Statistic statistic,
		Calculator calculator,
		Reading reading) {
		Map map = null;

		Map cache = getReadingCache(statistic);

		Reading first = (Reading) cache.get(statistic.getName());

		// found match to this reading
		if (first != null) {
			// generate metric
			Metric metric = new Metric(statistic.getName(), first, reading);
			calculateStatistic(calculator, metric);
			
			// clear first reading from cache 
			// ready to collect again
			cache.remove(statistic.getName());
		}

	}

	protected void cacheReading(Statistic statistic, Reading reading) {

		Map cache = getReadingCache(statistic);
		cache.put(statistic.getName(), reading);
	}

	protected void calculateStatistic(Calculator calculator, Metric metric) {
		calculator.applyMetric(metric);
	
		Alert a = (Alert) alerts.get(metric.getStatisticName());
		
		if (a != null) {
			a.evaluateAlert(calculator.getResult());
		}
		
	}

	public void close() {
		/*
		localReadingCache = null;
		readingCache.clear();
		statistics.clear();
		calculators.clear();
		*/
	}

	public void report(PrintStream out) {
		Calculator[] calcs =
			(Calculator[]) calculators.values().toArray(new Calculator[0]);
		for (int i = 0; i < calcs.length; ++i) {
			Calculator calculator = calcs[i];
			double result = calculator.getResult();
			long ts = calculator.getTimestamp();

			out.print(
				"Statistic("
					+ calculator.getStatistic().getDescription()
					+ ") value("
					+ result
					+ ") time ("
					+ new Date(ts)
					+ ")\n");
		}
	}
	
	
}
