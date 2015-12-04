/*
 * Tigase Jabber/XMPP Server
 * Copyright (C) 2004-2015 "Tigase, Inc." <office@tigase.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, version 3 of the License,
 * or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. Look for COPYING file in the top folder.
 * If not, see http://www.gnu.org/licenses/.
 */
package tigase.xml;

import java.util.Queue;
import java.util.logging.Level;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Wojtek
 */
public class SimpleParserTest {

	private final SimpleParser parser = SingletonFactory.getParserInstance();

	public SimpleParserTest() {
	}

	@Before
	public void setUp() {
	}

	@Test
	public void testParse() {

		String input = "<message><body>body</body><html><body><p><em>Wow</em>*, I&apos;m* <span>green</span>with <strong>envy</strong>!</p></body></html></message>";

		DomBuilderHandler domHandler = new DomBuilderHandler();
		Queue<Element> parsedElements = null;

		char[] data = input.toCharArray();

		parser.parse( domHandler, data, 0, data.length );
		parsedElements = domHandler.getParsedElements();

		Element el;
		if ( parsedElements != null && parsedElements.size() > 0 ){
			el = parsedElements.poll();
			boolean equals = input.equals( el.toString() );
			System.out.println( "input:  " + input );
			System.out.println( "output: " + el );
			System.out.println( "equals: " + equals );
			assertTrue( "Input and output are different!", equals );
		}

	}

}
