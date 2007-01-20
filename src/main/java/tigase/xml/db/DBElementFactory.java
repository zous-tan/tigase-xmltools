/*  Package Tigase XMPP/Jabber Server
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
package tigase.xml.db;

import tigase.xml.Element;
import tigase.xml.ElementFactory;

/**
 * <code>DBElementFactory</code> is implementation of factory design pattern
 * required by <em>XML</em> <em>DOM</em> builder to create proper
 * <code>Element</code> instances for tree nodes.
 *
 * <p>
 * Created: Tue Oct 26 22:41:57 2004
 * </p>
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class DBElementFactory implements ElementFactory {

  private static DBElementFactory factory =
    new DBElementFactory();

  /**
   * Creates a new <code>DBElementFactory</code> instance.
   *
   */
  private DBElementFactory() { }

  // Implementation of tigase.xml.ElementFactory

  /**
   * Describe <code>elementInstance</code> method here.
   *
   * @param name a <code>String</code> value
   * @param cdata a <code>String</code> value
   * @param attnames a <code>StringBuilder[]</code> value
   * @param attvalues a <code>StringBuilder[]</code> value
   * @return an <code>DBElement</code> value
   */
  public Element elementInstance(final String name, final String cdata,
    final StringBuilder[] attnames, final StringBuilder[] attvalues) {
    return new DBElement(name, cdata, attnames, attvalues);
  }

  public static DBElementFactory getFactory() {
    return factory;
  }

} // DBElementFactory
