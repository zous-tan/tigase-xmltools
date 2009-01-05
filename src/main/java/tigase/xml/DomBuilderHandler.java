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

import java.util.EmptyStackException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.TreeMap;
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
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */

public class DomBuilderHandler implements SimpleHandler {

  private static Logger log =
    Logger.getLogger("tigase.xml.DomBuilderHandler");

	private static ElementFactory defaultFactory = new DefaultElementFactory();

	private ElementFactory customFactory = null;

  private Object parserState = null;
  private String top_xmlns = null;

  private LinkedList<Element> all_roots = new LinkedList<Element>();
  private Stack<Element> el_stack = new Stack<Element>();
	private Map<String, String> namespaces = new TreeMap<String, String>();

  public DomBuilderHandler(ElementFactory factory) {
    customFactory = factory;
  }

  public DomBuilderHandler() {
    customFactory = defaultFactory;
  }

  public Queue<Element> getParsedElements() {
    return all_roots;
  }

  public void error(String errorMessage) {
    log.warning("XML content parse error.");
		log.warning(errorMessage);
  }

  private Element newElement(String name, String cdata,
    StringBuilder[] attnames, StringBuilder[] attvals) {
    return customFactory.elementInstance(name, cdata, attnames, attvals);
  }

  public void startElement(StringBuilder name,
    StringBuilder[] attr_names, StringBuilder[] attr_values) {
    log.finest("Start element name: "+name);
    log.finest("Element attributes names: "+Arrays.toString(attr_names));
    log.finest("Element attributes values: "+Arrays.toString(attr_values));

		// Look for 'xmlns:' declarations:
		if (attr_names != null) {
			for (int i = 0; i < attr_names.length; ++i) {
				// Exit the loop as soon as we reach end of attributes set
				if (attr_names[i] == null) { break;	}
				if (attr_names[i].toString().startsWith("xmlns:")) {
					namespaces.put(attr_names[i].substring("xmlns:".length(),
							attr_names[i].length()),
						attr_values[i].toString());
				} // end of if (att_name.startsWith("xmlns:"))
			} // end of for (String att_name : attnames)
		} // end of if (attr_names != null)

    String tmp_name = name.toString();
		String new_xmlns = null;
		String prefix = null;
		String tmp_name_prefix = null;
		int idx = tmp_name.indexOf(':');
		if (idx > 0) {
			tmp_name_prefix = tmp_name.substring(0, idx);
		}
		if (tmp_name_prefix != null) {
			for (String pref : namespaces.keySet()) {
				if (tmp_name_prefix.equals(pref)) {
					new_xmlns = namespaces.get(pref);
					tmp_name = tmp_name.substring(pref.length() + 1, tmp_name.length());
					prefix = pref;
				} // end of if (tmp_name.startsWith(xmlns))
			} // end of for (String xmlns: namespaces.keys())
		}
    Element elem = newElement(tmp_name, null, attr_names, attr_values);
    String ns = elem.getXMLNS();
    if (ns == null) {
			if (el_stack.isEmpty() || el_stack.peek().getXMLNS() == null) {
				//elem.setDefXMLNS(top_xmlns);
			} else {
				elem.setDefXMLNS(el_stack.peek().getXMLNS());
			}
    }
		if (new_xmlns != null) {
			elem.setXMLNS(new_xmlns);
			elem.removeAttribute("xmlns:" + prefix);
		}
    el_stack.push(elem);
  }

  public void elementCData(StringBuilder cdata) {
    log.finest("Element CDATA: "+cdata);
		try {
			el_stack.peek().setCData(cdata.toString());
		} catch (EmptyStackException e) {
			// Do nothing here, it happens sometimes that client sends
			// some white characters after sending open stream data....
		}
  }

  public void endElement(StringBuilder name) {
    log.finest("End element name: "+name);

    if (el_stack.isEmpty()) {
      el_stack.push(newElement(name.toString(), null, null, null));
    } // end of if (tmp_name.equals())

    Element elem = el_stack.pop();
    if (el_stack.isEmpty()) {
      all_roots.offer(elem);
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
