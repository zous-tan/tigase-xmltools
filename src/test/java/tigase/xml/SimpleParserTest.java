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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

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
			public boolean endElement(StringBuilder name) {
				return true;
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
			public boolean endElement(StringBuilder name) {
				return true;
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
	
	@Test
	public void testEntities() throws InstantiationException, IllegalAccessException {
		char[] data;
		final AtomicBoolean error = new AtomicBoolean(false);
		DomBuilderHandler handler = new DomBuilderHandlerImpl(error);

		data = "<message from=\"test@example.com\"><body>© §      ∉ ⇒ </body></message>".toCharArray();
		parser.parse(handler, data, 0, data.length);
		assertNotEquals(SimpleParser.State.ERROR, ((SimpleParser.ParserState)handler.restoreParserState()).state);
		assertFalse(error.get());
		assertEquals("© §      ∉ ⇒ ", handler.getParsedElements().poll().getChild("body").getCData());
		handler.saveParserState(null);
		handler = new DomBuilderHandlerImpl(error);
		
		data = "<message from=\"test@example.com\"><body>123 - &#123;</body></message>".toCharArray();
		parser.parse(handler, data, 0, data.length);
		assertFalse(error.get());
		assertNotEquals(SimpleParser.State.ERROR, ((SimpleParser.ParserState)handler.restoreParserState()).state);
		assertEquals("123 - &#123;", handler.getParsedElements().poll().getChild("body").getCData());
		handler.saveParserState(null);
		handler = new DomBuilderHandlerImpl(error);

		data = "<message from=\"test@example.com\"><body>123 - &a123;</body></message>".toCharArray();
		parser.parse(handler, data, 0, data.length);
		assertFalse(error.get());
		assertNotEquals(SimpleParser.State.ERROR, ((SimpleParser.ParserState)handler.restoreParserState()).state);
		assertEquals("123 - &a123;", handler.getParsedElements().poll().getChild("body").getCData());
		handler.saveParserState(null);
		handler = new DomBuilderHandlerImpl(error);
		
		data = "<message from=\"test@example.com\"><body>123 - &123;</body></message>".toCharArray();
		parser.parse(handler, data, 0, data.length);
		assertTrue(error.get());
//		assertEquals(SimpleParser.State.ERROR, ((SimpleParser.ParserState)handler.restoreParserState()).state);
		handler.saveParserState(null);
		handler = new DomBuilderHandlerImpl(error);

		data = "<message from=\"test@example.com\"><body>123 - &#123</body></message>".toCharArray();
		parser.parse(handler, data, 0, data.length);
		assertTrue(error.get());
//		assertEquals(SimpleParser.State.ERROR, ((SimpleParser.ParserState)handler.restoreParserState()).state);
		handler.saveParserState(null);
		handler = new DomBuilderHandlerImpl(error);

		data = "<message from=\"test@example.com\"><body>123 - &a123</body></message>".toCharArray();
		parser.parse(handler, data, 0, data.length);
		assertTrue(error.get());
//		assertEquals(SimpleParser.State.ERROR, ((SimpleParser.ParserState)handler.restoreParserState()).state);
		handler.saveParserState(null);
		handler = new DomBuilderHandlerImpl(error);

		data = "<message from=\"test@example.com\" id=\"&a123;\"></message>".toCharArray();
		parser.parse(handler, data, 0, data.length);
		assertFalse(error.get());
		assertNotEquals(SimpleParser.State.ERROR, ((SimpleParser.ParserState)handler.restoreParserState()).state);
		assertEquals("&a123;", handler.getParsedElements().poll().getAttribute("id"));
		handler.saveParserState(null);
		handler = new DomBuilderHandlerImpl(error);

		data = "<message from=\"test@example.com\" id=\"&#123;\"></message>".toCharArray();
		parser.parse(handler, data, 0, data.length);
		assertFalse(error.get());
		assertNotEquals(SimpleParser.State.ERROR, ((SimpleParser.ParserState)handler.restoreParserState()).state);
		assertEquals("&#123;", handler.getParsedElements().poll().getAttribute("id"));
		handler.saveParserState(null);
		handler = new DomBuilderHandlerImpl(error);

		data = "<message from=\"test@example.com\" id=\"&123;\"></message>".toCharArray();
		parser.parse(handler, data, 0, data.length);
		assertTrue(error.get());
//		assertEquals(SimpleParser.State.ERROR, ((SimpleParser.ParserState)handler.restoreParserState()).state);
		handler.saveParserState(null);
		handler = new DomBuilderHandlerImpl(error);
		
		data = "<message from=\"test@example.com\" id=\"&a123\"></message>".toCharArray();
		parser.parse(handler, data, 0, data.length);
		assertTrue(error.get());
//		assertEquals(SimpleParser.State.ERROR, ((SimpleParser.ParserState)handler.restoreParserState()).state);
		handler.saveParserState(null);
		handler = new DomBuilderHandlerImpl(error);

		data = "<mes&sage from=\"test@example.com\"></message>".toCharArray();
		parser.parse(handler, data, 0, data.length);
		assertTrue(error.get());
//		assertEquals(SimpleParser.State.ERROR, ((SimpleParser.ParserState)handler.restoreParserState()).state);
		handler.saveParserState(null);
		handler = new DomBuilderHandlerImpl(error);

		data = "<mes&amp;sage from=\"test@example.com\"></message>".toCharArray();
		parser.parse(handler, data, 0, data.length);
		assertTrue(error.get());
//		assertEquals(SimpleParser.State.ERROR, ((SimpleParser.ParserState)handler.restoreParserState()).state);
		handler.saveParserState(null);		
		handler = new DomBuilderHandlerImpl(error);

		data = "<message from=\"test@example.com\"><<body>Test</body></message>".toCharArray();
		parser.parse(handler, data, 0, data.length);
		assertTrue(error.get());
//		assertEquals(SimpleParser.State.ERROR, ((SimpleParser.ParserState)handler.restoreParserState()).state);
		handler.saveParserState(null);		
		handler = new DomBuilderHandlerImpl(error);

		data = "<message from=\"test@example.com\"><body>Test</body1></message>".toCharArray();
		parser.parse(handler, data, 0, data.length);
		assertTrue(error.get());
//		assertEquals(SimpleParser.State.ERROR, ((SimpleParser.ParserState)handler.restoreParserState()).state);
		handler.saveParserState(null);		
		handler = new DomBuilderHandlerImpl(error);

		data = "<message to=\"test@zeus\" type=\"chat\" id=\"t&amp;t<\"><body>Test &amp; done</body></message>".toCharArray();
		parser.parse(handler, data, 0, data.length);
		assertTrue(error.get());
//		assertEquals(SimpleParser.State.ERROR, ((SimpleParser.ParserState)handler.restoreParserState()).state);
		handler.saveParserState(null);		
		handler = new DomBuilderHandlerImpl(error);
		
		data = "<db:result to=\"malkowscy.net\" from=\"brzezinski.mobi\">CAESBxCXyf6RqCoaEGFkhIYrlYto/kK5GTcsaTw=</db:result><db:verify to=\"malkowscy.net\" from=\"brzezinski.mobi\" id=\"1B6476BAD2C0BF34\">4ab1c1d99f40263822ae7d4851854eb158e46325009e928a66b1148ee4fed331</db:verify><db:result to=\"malkowscy.net\" from=\"gmail.com\">CAESBxCXyf6RqCoaEGPHnXDLTIeKBNx9ZJ1SmzM=</db:result>".toCharArray();
		parser.parse(handler, data, 0, data.length);
		assertFalse(error.get());
//		assertEquals(SimpleParser.State.ERROR, ((SimpleParser.ParserState)handler.restoreParserState()).state);
		handler.saveParserState(null);		
		handler = new DomBuilderHandlerImpl(error);

		data = "<body xmlns:xmpp=\"urn:xmpp:xbosh\" ack=\"6621206\" from=\"test\" xmlns=\"http://jabber.org/protocol/httpbind\" secure=\"true\" xmpp:version=\"1.0\" xmlns:stream=\"http://etherx.jabber.org/streams\" host=\"mbp-andrzej.local\"><stream:features xmlns=\"jabber:client\"><auth xmlns=\"http://jabber.org/features/iq-auth\"/><mechanisms xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\"><mechanism>SCRAM-SHA-1</mechanism><mechanism>PLAIN</mechanism><mechanism>ANONYMOUS</mechanism></mechanisms><register xmlns=\"http://jabber.org/features/iq-register\"/><ver xmlns=\"urn:xmpp:features:rosterver\"/><starttls xmlns=\"urn:ietf:params:xml:ns:xmpp-tls\"/><compression xmlns=\"http://jabber.org/features/compress\"><method>zlib</method></compression></stream:features></body>".toCharArray();
		parser.parse(handler, data, 0, data.length);
		assertFalse(error.get());
//		assertEquals(SimpleParser.State.ERROR, ((SimpleParser.ParserState)handler.restoreParserState()).state);
		handler.saveParserState(null);
		handler = new DomBuilderHandlerImpl(error);

	}

	private class DomBuilderHandlerImpl extends DomBuilderHandler {

		private AtomicBoolean error;

		public DomBuilderHandlerImpl(AtomicBoolean error) {
			this.error = error;
		}

		@Override
		public void error(String errorMessage) {
			error.set(true);
		}

		@Override
		public void saveParserState(Object state) {
			super.saveParserState(state);
			if (state == null) {
				error.set(false);
			}
		}

	};
	
}
