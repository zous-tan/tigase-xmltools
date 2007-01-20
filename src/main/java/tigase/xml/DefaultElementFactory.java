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
package tigase.xml;


/**
 * <code>DefaultElementFactory</code> is an <code>ElementFactory</code>
 *  implementation creating instances of basic <code>Element</code> class. This
 *  implementation exists to offer complementary implementation of
 *  <em>DOM</em>. It can be used when basic <code>Element</code> class is
 *  sufficient for particular needs.
 * <p>
 * Created: Mon Oct 25 22:08:37 2004
 * </p>
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class DefaultElementFactory implements ElementFactory {

  /**
   * Creates a new <code>DefaultElementFactory</code> instance.
   *
   */
  public DefaultElementFactory() { }

  // Implementation of tigase.xml.ElementFactory

  public final Element elementInstance(final String name,
		final String cdata,
    final StringBuilder[] attnames, final StringBuilder[] attvals) {
    return new Element(name, cdata, attnames, attvals);
  }

} // DefaultElementFactory
