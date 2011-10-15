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

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.stat4j.util.Util;

/**
 * Name:		RegExpScraper.java
 * Date:		Sep 4, 2004
 * Description:
 * 
 * Class that implements basic scraping logic given a scap pattern 
 * conforming to standard regular expressions.
 * 
 * For example: 	
 * 	
 * pattern:		
 * text:
 * value:
 * 
 * @author Lara D'Abreo
 */

public class RegExpScraper  {

	protected String decimalFormat = null;
	// A Cache of precompiled scrap patterns, this is
	// loaded on demand
	protected Map patterns;
	// As Formatters are not threadsafe we use ThreadLocal
	// to guaranttee one (and only on instance per Thread).
	protected ThreadLocal format = new ThreadLocal() {
		protected synchronized Object initialValue() {
			if (decimalFormat == null)
				return new DecimalFormat();
			else
				return new DecimalFormat(decimalFormat);
		}
	};


	public RegExpScraper() {
		patterns = new HashMap();
		decimalFormat = Util.getValue("decimalformat");
	}
	
	
	public Double scrapUserDefinedValue(String text, String pattern) {

		// if the text is empty or null just return no reading
		if ((text == null)
			|| (text.length() == 0)
			|| (pattern == null)
			|| (pattern.length() == 0)) {
			return null;
		}
		// get pre-compiled pattern, compile one if it isnt available
		Pattern p  = getPattern(pattern);
		
		// Create a matcher
		Matcher m = p.matcher(text);
		
		try {
			if (!m.matches())
				return null;
				
			// Get first match group
			String str = m.group(1);

			// Use the formater to parse out the value . The formatter will
			// take account of the country specific formatting of the number
			// whereas Double.parse will not.
			// Any extraneous text after the number (if it is present) will be ignored
			// by the formatter. This means we can be lazy with what the str ends with
			DecimalFormat df = (DecimalFormat) format.get();
			Number value = df.parse(str);
			return new Double(value.doubleValue());
		} catch (Exception ex) {
			return null;
		}

	}

	public final Pattern getPattern(String regexp) {

		Pattern pattern = (Pattern) patterns.get(regexp);
		if (pattern == null) {
			pattern = Pattern.compile(regexp);
			patterns.put(regexp, pattern);
		}
		return pattern;

	}

	public void dispose() {
		patterns.clear();
		format = null;
	}
	
	
	public static void main(String[] args) {
		try {
			RegExpScraper scraper = new RegExpScraper();
			
			String pattern = ".*post=([0-9].*)+";
			String log = "Time to process post=1.0";
			
			Double value = scraper.scrapUserDefinedValue(log,pattern);
			
			System.out.println("Scrape Value=" + value);
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	

}
