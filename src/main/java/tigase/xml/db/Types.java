/*
 * Tigase Jabber/XMPP XML Tools
 * Copyright (C) 2004-2012 "Artur Hefczyc" <artur.hefczyc@tigase.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. Look for COPYING file in the top folder.
 * If not, see http://www.gnu.org/licenses/.
 *
 * $Rev$
 * Last modified by $Author$
 * $Date$
 */
package tigase.xml.db;

import java.util.Map;
import java.util.TreeMap;

/**
 * Describe class Types here.
 *
 *
 * Created: Wed Dec 28 21:54:43 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public abstract class Types {

	public static Map<String, DataType> dataTypeMap =
	  new TreeMap<String, DataType>();

	/**
	 * Describe class DataType here.
	 *
	 *
	 * Created: Tue Dec  6 17:34:22 2005
	 *
	 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
	 * @version $Rev$
	 */
	public enum DataType {

		INTEGER(Integer.class.getSimpleName()),
			INTEGER_ARR(int[].class.getSimpleName()),
			LONG(Long.class.getSimpleName()),
			LONG_ARR(long[].class.getSimpleName()),
			STRING(String.class.getSimpleName()),
			STRING_ARR(String[].class.getSimpleName()),
			DOUBLE(Double.class.getSimpleName()),
			DOUBLE_ARR(double[].class.getSimpleName()),
			BOOLEAN(Boolean.class.getSimpleName()),
			BOOLEAN_ARR(boolean[].class.getSimpleName()),
			UNDEFINED(null)
			;

		private String javaType = null;

		/**
		 * Creates a new <code>DataType</code> instance.
		 *
		 */
		private DataType(String java_type) {
			this.javaType = java_type;
			if (java_type != null) {
				dataTypeMap.put(java_type, this);
			} // end of if (java_type != null)
		}

		public static DataType valueof(String javaType) {
			DataType result = UNDEFINED;
			if (javaType != null && !javaType.equals("")) {
				result = dataTypeMap.get(javaType);
			} // end of if (javaType != null && !javaType.equals(""))
			return result == null ? UNDEFINED : result;
		}

		public String toString() {
			if (javaType == null) {
				return String.class.getSimpleName();
			} // end of if (javaType == null)
			else {
				return javaType;
			} // end of if (javaType == null) else
		}

	} // DataType

} // Types