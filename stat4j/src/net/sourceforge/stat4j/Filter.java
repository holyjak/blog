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

/**
 * Name:		Filter.java
 * Date:		Aug 29, 2004
 * Description:
 * 
 * A filter encapculates a regular 
 * expression that may be 'matched' or
 * filtered from a log.
 * 
 * A filter may optionally specify a scrape expression
 * The scrape expression is used to scrap a value 
 * from the log message itself - use standard regular
 * expression groups. Group(1) will be the value string
 * extracted.
 *  
 * 
 * @author Lara D'Abreo
 */
public class Filter {

	public String	match;
	public String	scape;
	
	public void init(Properties properties) {
		
	}
	/**
	 * @return
	 */
	public String getMatch() {
		return match;
	}

	/**
	 * @return
	 */
	public String getScape() {
		return scape;
	}

	/**
	 * @param string
	 */
	public void setMatch(String string) {
		match = string;
	}

	/**
	 * @param string
	 */
	public void setScape(String string) {
		scape = string;
	}

}
