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
 * Name:		TestDelta.java
 * Date:		Sep 6, 2004
 * Description:
 * 
 * 
 * @author Lara D'Abreo
 */
public class TestDelta extends TestCase {
	public Category category;

	/**
	 * @param arg0
	 */
	public TestDelta(String arg0) {
		super(arg0);
		URL url = TestRunningCount.class.getResource("log.properties");
		PropertyConfigurator.configure(url);
		category = Category.getInstance("test");
	}

	public void testFooMetrics() {
		MetricCollector.getInstance().reset();

		String begin = "BEGIN foo() ...";
		String end = "END foo()...";
		
		long max = 0;
		long total = 0;
		

		for (int i = 0; i < 100; ++i) {
			long wait = (long)(Math.random() * 100);

			category.info(begin);
			pause(wait);
			category.info(end);
			
			if (wait > max) {
				max = wait;
			}
			total = total + wait;
		} //rof

		System.out.println(">>> Max Duration of foo()=" + max);
		System.out.println(">>> Avg Duration of foo()=" + total/100);
		MetricCollector.getInstance().report(System.out);
	}

	public void pause() {
		try {
			Thread.sleep((long) (Math.random() * 10));
		} catch (Exception e) {
		}

	}

	public void pause(long wait) {
		try {
			Thread.sleep(wait);
		} catch (Exception e) {
		}

	}
}
