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
import org.junit.After;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Wojtek
 */
public class SimpleParserTest {

	private SimpleParser parser;

	public SimpleParserTest() {
	}

	@Before
	public void setUp() {
		parser = new SimpleParser();
	}
	
	@After
	public void tearDown() {
		parser = null;
	}
	
	@Test
	public void testNPE() {
		SimpleHandler handler = new SimpleHandler() {
			Object state;

			@Override
			public void error(String errorMessage) {
			}

			@Override
			public void startElement(StringBuilder name, StringBuilder[] attr_names, StringBuilder[] attr_values) {
			}

			@Override
			public void elementCData(StringBuilder cdata) {
			}

			@Override
			public void endElement(StringBuilder name) {
			}

			@Override
			public void otherXML(StringBuilder other) {
			}

			@Override
			public void saveParserState(Object state) {
				this.state = state;
			}

			@Override
			public Object restoreParserState() {
				return this.state;
			}
		};
		
		String input = "<root test1 \"test2\"/>";
		
		char[] data = input.toCharArray();
		parser.parse(handler, data, 0, data.length);
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

	@Test
	public void testChars() {
		SimpleHandler handler = new SimpleHandler() {
			Object state;

			@Override
			public void error(String errorMessage) {
			}

			@Override
			public void startElement(StringBuilder name, StringBuilder[] attr_names, StringBuilder[] attr_values) {
			}

			@Override
			public void elementCData(StringBuilder cdata) {
			}

			@Override
			public void endElement(StringBuilder name) {
			}

			@Override
			public void otherXML(StringBuilder other) {
			}

			@Override
			public void saveParserState(Object state) {
				this.state = state;
			}

			@Override
			public Object restoreParserState() {
				return this.state;
			}
		};
				
		char[] data = "<test/>".toCharArray();
		parser.parse(handler, data, 0, data.length);

		handler.saveParserState(null);
		String dataStr = new StringBuilder("<test>").append(Character.toChars(127479)).append("</test>").toString(); 
		data = dataStr.toCharArray();

 		parser.parse(handler, data, 0, data.length);
		assertNotEquals(SimpleParser.State.ERROR, ((SimpleParser.ParserState)handler.restoreParserState()).state);

		data = "<test>\u0000</test".toCharArray();
 		parser.parse(handler, data, 0, data.length);
		assertEquals(SimpleParser.State.ERROR, ((SimpleParser.ParserState)handler.restoreParserState()).state);
	}
	
	protected boolean checkIsCharValidInXML(char chr) {
		return (chr == 0x09 || chr ==0x0a || chr == 0x0d || (chr >= 0x20 && chr <= 0xD7FF) || (chr >= 0xE000 && chr <= 0xFFFD) || (chr >= 0x10000 && chr <= 0x10FFFF));
	}	

}
