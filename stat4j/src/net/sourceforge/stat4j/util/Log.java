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

package net.sourceforge.stat4j.util;

import org.apache.log4j.Category;

/**
 * Name:		Log.java
 * Date:		Sep 4, 2004
 * Description:
 * 
 * 
 * @author Lara D'Abreo
 */
public class Log {

	public static final Category category = Category.getInstance(Util.getCategory());
	
	public static void debug(Object message) {
		category.debug(message);
	}

	public static void debug(Object message, Throwable t) {

		category.debug(message, t);
	}

	public static void error(Object message) {
		category.error(message);
	}

	public static void error(Object message, Throwable t) {
		category.error(message, t);
	}

	public static void fatal(Object message) {
		category.fatal(message);
	}

	public static void fatal(Object message, Throwable t) {
		category.fatal(message, t);
	}

	public static void info(Object message) {
		category.info(message);
	}

	public static void info(Object message, Throwable t) {
		category.info(message, t);
	}

	public static void warn(Object message) {
		category.warn(message);
	}
	
	public static void warn(String category,Object message) {
		Category c = Category.getInstance(category);
		c.warn(message);
	}

	public static void warn(Object message, Throwable t) {
		category.warn(message, t);
	}

	public static boolean isDebugEnabled() {
		return category.isDebugEnabled();
	}

}