/*  Package Tigase XMPP/Jabber Server
 *  Copyright (C) 2001, 2002, 2003, 2004, 2005
 *  "Artur Hefczyc" <artur.hefczyc@gmail.com>
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
 * $Author$
 * $Date$
 */

package tigase.xml;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.logging.Logger;

/**
 * <code>DomBuilderHandler</code> - implementation of
 *  <code>SimpleHandler</code> building <em>DOM</em> strctures during parsing
 *  time.
 *  It also supports creation multiple, sperate document trees if parsed
 *  buffer contains a few <em>XML</em> documents. As a result of work it returns
 *  always <code>Queue</code> containing all found <em>XML</em> trees in the
 *  same order as they were found in network data.<br/>
 *  Document trees created by this <em>DOM</em> builder consist of instances of
 *  <code>Element</code> class or instances of class extending
 *  <code>Element</code> class. To receive trees built with instances of proper
 *  class user must provide <code>ElementFactory</code> implementation creating
 *  instances of required <code>ELement</code> extension.
 *
 * <p>
 * Created: Sat Oct  2 22:01:34 2004
 * </p>
 * @author <a href="mailto:artur.hefczyc@gmail.com">Artur Hefczyc</a>
 * @version $Rev$
 */

public class DomBuilderHandler<E extends Element> implements SimpleHandler {

  private static Logger log =
    Logger.getLogger("tigase.protocols.xmpp.DomBuilderHandler");

  private static final String ELEM_STREAM_STREAM = "stream:stream";
  private ElementFactory<E> customFactory = null;

  private Object parserState = null;
  private String top_xmlns = null;
  private String def_xmlns = null;

  private LinkedList<E> all_roots = new LinkedList<E>();
  private Stack<E> el_stack = new Stack<E>();

  public DomBuilderHandler(ElementFactory<E> factory) {
    customFactory = factory;
  }

  public Queue<E> getParsedElements() {
    return all_roots;
  }

  public void error() {
    log.warning("XML content parse error.");
  }

  private E newElement(String name, String cdata,
    StringBuilder[] attnames, StringBuilder[] attvals) {
    return customFactory.elementInstance(name, cdata, attnames, attvals);
  }

  public void startElement(StringBuilder name,
    StringBuilder[] attr_names, StringBuilder[] attr_values) {
    log.finest("Start element name: "+name);
    log.finest("Element attributes names: "+Arrays.toString(attr_names));
    log.finest("Element attributes values: "+Arrays.toString(attr_values));

    String tmp_name = name.toString();

    E elem = newElement(tmp_name, null, attr_names, attr_values);
    String ns = elem.getXMLNS();
    if (ns == null) {
      elem.setDefXMLNS(def_xmlns);
    } // end of if (ns == null)
    else {
      def_xmlns = ns;
    } // end of if (ns == null) else
    el_stack.push(elem);
    if (tmp_name.equals(ELEM_STREAM_STREAM)) {
      top_xmlns = elem.getXMLNS();
      endElement(name);
    } // end of if (tmp_name.equals())
  }

  public void elementCData(StringBuilder cdata) {
    log.finest("Element CDATA: "+cdata);

    el_stack.peek().setCData(cdata.toString());
  }

  public void endElement(StringBuilder name) {
    log.finest("End element name: "+name);

    if (el_stack.isEmpty()) {
      el_stack.push(newElement(name.toString(), null, null, null));
    } // end of if (tmp_name.equals())

    E elem = el_stack.pop();
    if (el_stack.isEmpty()) {
      all_roots.offer(elem);
      def_xmlns = top_xmlns;
      log.finest("Adding new request: "+elem.toString());
    } // end of if (el_stack.isEmpty())
    else {
      el_stack.peek().addChild(elem);
    } // end of if (el_stack.isEmpty()) else
  }

  public void otherXML(StringBuilder other) {
    log.finest("Other XML content: "+other);

    // Just ignore
  }

  public void saveParserState(Object state) {
    parserState = state;
  }

  public Object restoreParserState() {
    return parserState;
  }

}// DomBuilderHandler

