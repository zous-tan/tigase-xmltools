/*
 * Tigase Jabber/XMPP XML Tools
 * Copyright (C) 2004-2007 "Artur Hefczyc" <artur.hefczyc@tigase.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. Look for COPYING file in the top folder.
 * If not, see http://www.gnu.org/licenses/.
 *
 * $Rev$
 * Last modified by $Author$
 * $Date$
 */
package tigase.xml;

import java.util.Queue;
import java.io.FileReader;

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

	private static final String[] decoded = {"&", "<", ">"};
	private static final String[] encoded = {"&amp;", "&lt;", "&gt;"};

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

  public static void main(final String[] args) throws Exception {

    if (args.length < 1) {
      System.err.println("You must give file name as parameter.");
      System.exit(1);
    } // end of if (args.length < 1)

    FileReader file = new FileReader(args[0]);
    char[] buff = new char[16*1024];
    SimpleParser parser = new SimpleParser();
    DomBuilderHandler dombuilder = new DomBuilderHandler();
    int result = -1;
    while((result = file.read(buff)) != -1) {
      parser.parse(dombuilder, buff, 0, result);
    }
    file.close();
		Queue<Element> results = dombuilder.getParsedElements();
		for (Element elem: results) {
			System.out.println(elem.toString());
		}
  }

}