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

/**
 * Name:		Unit.java
 * Date:		Aug 30, 2004
 * Description:
 * 
 *  A Unit of measurement for a statistic.
 * 
 * @author Lara D'Abreo
 */
public class Unit {

	public static final Unit TIME = new Unit(0,"time");
	public static final Unit MEMORY = new Unit(1,"memory");
	public static final Unit VALUE = new Unit(2,"value");
	
	public static final Unit[] units = new Unit[]{TIME,MEMORY,VALUE};
	
	private	int	type;
	private String name;
	
	private Unit(int type,String name) {
		this.type = type;
		this.name = name;
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
	public int getType() {
		return type;
	}
	
	public static Unit parse(String unitName) {
		for (int i = 0; i < units.length; ++i){
			if (units[i].getName().equals(unitName)) {
				return units[i];
			}
			
		}
		return null;
	}


}
