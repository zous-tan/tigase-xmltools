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

package tigase.xml;

import tigase.annotations.TODO;

/**
 * <code>SimpleHandler</code> - parser handler interface for event driven
 *  parser. It is very simplified version of
 *  <code>org.xml.sax.ContentHandler</code> interface created for
 *  <code>SimpleParser</code> needs. It allows to receive events like start
 *  element (with element attributes), end element, element cdata, other XML
 *  content and error event if XML error found.
 *
 * <p>
 * Created: Sat Oct  2 00:00:08 2004
 * </p>
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 * @see SimpleParser
 */

public interface SimpleHandler {

  void error(String errorMessage);

  void startElement(StringBuilder name,
    StringBuilder[] attr_names, StringBuilder[] attr_values);

  void elementCData(StringBuilder cdata);

  boolean endElement(StringBuilder name);

  void otherXML(StringBuilder other);

  @TODO(note="Use generic types to store parser data.")
  void saveParserState(Object state);

  @TODO(note="Use generic types to store parser data.")
  Object restoreParserState();

}// SimpleHandler
