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
 * Name:		TestRate.java
 * Date:		Sep 6, 2004
 * Description:
 * 
 * 
 * @author Lara D'Abreo
 */
public class TestRate extends TestCase {

	public Category category;
	/**
	 * 
	 */
	public TestRate() {
		super();
		
	}

	/**
	 * @param arg0
	 */
	public TestRate(String arg0) {
		super(arg0);
		URL url = TestRunningCount.class.getResource("log.properties");
		PropertyConfigurator.configure(url);
		category = Category.getInstance("test");
	}

	public void testErrorRate() {

		MetricCollector.getInstance().reset();
		int count = 0;
		long start = System.currentTimeMillis();

		String log = "java.lang.Exception Problem with...";

		for (int i = 0; i < 100; ++i) {
			++count;
			category.info(log);
			pause();
		} //rof
		long end = System.currentTimeMillis();
		long diff = end - start;
		double noSecs = diff / 1000; // per second

		System.out.println("Errors=" + count);
		System.out.println("No secconds=" + noSecs);
		System.out.println("rate=" + count / noSecs);
		MetricCollector.getInstance().report(System.out);
	}

	public void pause() {
		try {
			Thread.sleep((long) (Math.random() * 100));
		} catch (Exception e) {
		}

	}
}
