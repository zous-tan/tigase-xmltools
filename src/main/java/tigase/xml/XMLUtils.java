/*  Package Jabber Server
 *  Copyright (C) 2001, 2002, 2003, 2004, 2005
 *  "Artur Hefczyc" <artur.hefczyc@tigase.org>
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software Foundation,
 *  Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * $Rev$
 * Last modified by $Author$
 * $Date$
 */
package main.java.tigase.xml;

/**
 * Describe class XMLUtil here.
 *
 *
 * Created: Tue Jan 23 20:59:30 2007
 *
 * @author <a href="mailto:artur.hefczyc@gmail.com">Artur Hefczyc</a>
 * @version $Rev$
 */
public abstract class XMLUtils {

	public static final String[] decoded = {"&", "<"};
	public static final String[] encoded = {"&amp;", "&lt;"};

	public static String translateAll(String input,
		String[] patterns, String[] replacements) {
		String result = input;
		for (int i = 0; i < patterns.length; i++) {
			result = result.replaceAll(patterns[i], replacements[i]);
		}
		return result;
	}

	public static String escape(String input) {
		return translateAll(input, decoded, encoded);
	}

	public static String unescape(String input) {
		return translateAll(input, encoded, decoded);
	}

}
