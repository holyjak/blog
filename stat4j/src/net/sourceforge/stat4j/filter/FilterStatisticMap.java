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

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Name:		FilterStatisticMap.java
 * Date:		Sep 2, 2004
 * Description:
 * 
 * 
 * @author Lara D'Abreo
 */
public class FilterStatisticMap {

	public Pattern 	pattern;
	public ArrayList firsts;
	public ArrayList seconds;

	public FilterStatisticMap(String regexp) {
		this.pattern = Pattern.compile(regexp);
		this.firsts = new ArrayList();
		this.seconds = new ArrayList();
	}

	public boolean isMatch(String str) {
		Matcher m = pattern.matcher(str);
		return m.matches();
	}

	
	/**
	 * @return
	 */
	public ArrayList getFirsts() {
		return firsts;
	}

	/**
	 * @return
	 */
	public Pattern getPattern() {
		return pattern;
	}

	/**
	 * @return
	 */
	public ArrayList getSeconds() {
		return seconds;
	}

};
