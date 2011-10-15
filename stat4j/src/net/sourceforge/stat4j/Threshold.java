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
 * Name:		Threshold.java
 * Date:		Sep 6, 2004
 * Description:
 * 
 * A preset limit that may be applied to a statstic so
 * as to trigger an alert.
 * 
 * 
 * @see Alert
 * 
 * @author Lara D'Abreo
 */
public final class Threshold {

	//Threshold operators
	public interface Operators {
	
		public final static String LESS_THAN_OR_EQUAL = "<=";
		public final static String GREATER_THAN_OR_EQUAL = ">=";
		public final static String EQUAL_TO = "==";
		public final static String NOT_EQUAL_TO = "!=";
		public final static String GREATER_THAN = ">";
		public final static String LESS_THAN = "<";
	

		public final static String[] ALL_OPS =new String[] 
			{
				LESS_THAN_OR_EQUAL,
				GREATER_THAN_OR_EQUAL,
				EQUAL_TO,
				NOT_EQUAL_TO,
				GREATER_THAN,
				LESS_THAN,
				};

	}


	protected String operator;
	protected double limit;
	

	public Threshold() {
	}

	/**
	 * Evaluate if rule is triggered
	 * @param reading
	 * @return
	 */
	public boolean isTriggered(double value) {

		if (operator == null)
			return false;

		if (operator.equals(Operators.EQUAL_TO)) {
			return value == limit;
		} else if (operator.equals(Operators.NOT_EQUAL_TO)) {
			return value != limit;
		} else if (operator.equals(Operators.GREATER_THAN)) {
			return value > limit;
		} else if (operator.equals(Operators.GREATER_THAN_OR_EQUAL)) {
			return value >= limit;
		} else if (operator.equals(Operators.LESS_THAN)) {
			return value < limit;
		} else if (operator.equals(Operators.LESS_THAN_OR_EQUAL)) {
			return value <= limit;
		}
		return false;
	}

	


	public static Threshold toThreshold(String str) {
		if (str == null)
			return null;
		if (str.length() == 0)
			return null;
			
		 try {
		 		int idx = -1;String op= null;
		 		for (int i = 0; i < Operators.ALL_OPS.length; ++i) {
		 			String opstr = Operators.ALL_OPS[i];
		 			idx= str.indexOf(opstr);
					if (idx >= 0) {
						op = opstr;
						break;			 			
					} //fi
		 		}//rof
		 		
		 		// mo operator found
		 		if (idx == -1) return null;

				// value
				String valueStr = str.substring(idx+op.length());
				double value = Double.parseDouble(valueStr);
				
				Threshold rule = new Threshold();
				rule.setOperator(op);
				rule.setValue(value);
			
				return rule;

			} catch (Exception e) {
				return null;
			}

	}
	

	/**
	 * @param string
	 */
	public void setOperator(String string) {
		operator = string;
	}

	/**
	 * @param d
	 */
	public void setValue(double d) {
		limit = d;
	}
	
	public String toString() {
		return operator + limit;
	}

}
