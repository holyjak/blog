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

import net.sourceforge.stat4j.util.Log;

/**
 * Name: Alert.java Date: Sep 6, 2004 Description:
 * 
 * Simple threshold based alert.
 * 
 * This class represents a pre-configured alert for a statistic.
 * 
 * The alert will be evaluated every time the statistic is updated. (alert
 * suppresion is not supported in this version)
 * 
 * If the alert fires (either WARN or CRITICAL) then an alert message will be
 * sent to the nominated log4j category (console,email,NT event log etc)
 * 
 * @see Threashold
 * 
 * @author Lara D'Abreo
 */
/**
 * @author Lara D'Abreo
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
/**
 * @author Lara D'Abreo
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
/**
 * @author Lara D'Abreo
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class Alert {

    protected String name;

    protected String statisticName;

    protected String category;

    protected String description;

    protected Threshold warn;

    protected Threshold critical;

    public Alert() {

    }

    public void init(String name, Properties properties) {
        this.name = name;

        statisticName = properties.getProperty("statistic");

        description = properties.getProperty("description");
        if (description == null) {
            description = name;
        }
        // init category
        category = (String) properties.get("category");
        if (category == null) {
            category = "alerts";
        }

        // init warn
        String str = (String) properties.get("warn");
        if (str != null) {
            warn = Threshold.toThreshold(str);
        }

        // init critical
        str = (String) properties.get("critical");
        if (str != null) {
            critical = Threshold.toThreshold(str);
        }
    }

    public void evaluateAlert(double value) {
        if (isTriggered(critical, value)) {
            Log.warn(category, "CRITICAL Alert " + description + " rule "
                    + critical + " value " + value);
        } else if (isTriggered(warn, value)) {
            Log.warn(category, "WARN Alert " + description + " rule " + warn
                    + " value " + value);
        }

    }

    public boolean isTriggered(Threshold t, double value) {
        if (t == null)
            return false;
        return t.isTriggered(value);
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @return
     */
    public String getStatisticName() {
        return statisticName;
    }

}