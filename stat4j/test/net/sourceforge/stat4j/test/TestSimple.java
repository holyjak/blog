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
package net.sourceforge.stat4j.test;

import java.net.URL;

import junit.framework.TestCase;

import net.sourceforge.stat4j.filter.MetricCollector;

import org.apache.log4j.Category;
import org.apache.log4j.PropertyConfigurator;


/**
 * Name:		TestSimple.java
 * Date:		Sep 6, 2004
 * Description:
 * 
 * 
 * @author Lara D'Abreo
 */
public class TestSimple extends TestCase {
	public Category category;

	public TestSimple(String arg0) {
		super(arg0);

		URL url = TestRunningCount.class.getResource("log.properties");
		PropertyConfigurator.configure(url);
		category = Category.getInstance("test");
	}

	public void testSimpleValueStatistic() {

		MetricCollector.getInstance().reset();
		double random = 0.0;

		for (int i = 0; i < 1000; ++i) {
			random = Math.random() * 10;
			category.debug("Time to process post=" + random);
		} //rof

		System.out.println(">>> last reported value =" + random);
		MetricCollector.getInstance().report(System.out);
	}

	public void testSimpleTimeStatistic() {

		MetricCollector.getInstance().reset();
		long timestamp = 0;
		for (int i = 0; i < 1000; ++i) {

			timestamp = System.currentTimeMillis();
			category.debug("Time to process post=1.0");
		} //rof

		System.out.println(">>> time value =" + timestamp);
		MetricCollector.getInstance().report(System.out);
	}

	public void testSimpleMemoryStatistic() {

		MetricCollector.getInstance().reset();
		long mem = 0;
		for (int i = 0; i < 1000; ++i) {

			mem = Runtime.getRuntime().freeMemory();
			category.debug("Time to process post=2.0");
		} //rof

		System.out.println(">>> free mem value =" + mem);
		MetricCollector.getInstance().report(System.out);
	}

}
