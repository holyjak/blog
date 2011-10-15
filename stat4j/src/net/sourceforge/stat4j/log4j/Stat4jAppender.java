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

package net.sourceforge.stat4j.log4j;

import net.sourceforge.stat4j.filter.MetricCollector;
import net.sourceforge.stat4j.util.Util;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;


/**
 * Name:		Stat4JAppender.java
 * Date:		Sep 1, 2004
 * Description:
 * 
 * This log4j Appender is responsible for forwarding on all
 * log messages to the stat4j regexp statstic engine.
 * 
 * Logs from stat4j classes will be ignored.
 * 
 * @author Lara D'Abreo
 */
public class Stat4jAppender extends AppenderSkeleton {

	protected static String c;

	public Stat4jAppender() {

		c = Util.getCategory();

	}

	protected void append(LoggingEvent logEvent) {
		// dont scrap our own logs
		if (logEvent.categoryName.equals(c))
			return;
		// direct log to metric capture mechanism
        Throwable cause = (logEvent.getThrowableInformation() == null)? null : logEvent.getThrowableInformation().getThrowable();
		MetricCollector.getInstance().applyLog(logEvent.getRenderedMessage(), cause);
	}

	public boolean requiresLayout() {
		return false;
	}

	public void close() {
		MetricCollector.getInstance().close();

	}

}
