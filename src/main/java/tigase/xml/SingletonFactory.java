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
 * $Author$
 * $Date$
 */

package tigase.xml;

/**
 * <code>SingletonFactory</code> provides a way to use only one instance of
 *  <code>SimpleParser</code> in all your code.
 *  Since <code>SimpleParser</code> if fully thread safe implementation there is
 *  no sense to use multiple instances of this class. This in particular useful
 *  when processing a lot of network connections sending <em>XML</em> streams
 *  and using one instance for all connections can save some resources.<br/>
 *  Of course it is still possible to create as many instances of
 *  <code>SimpleParser</code> you like in normal way using public constructor.
 *
 * <p>
 * Created: Sat Oct  2 22:12:21 2004
 * </p>
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */

public class SingletonFactory {

  private static SimpleParser parser = null;

  public static SimpleParser getParserInstance() {
    if (parser == null) {
      parser = new SimpleParser();
    } // end of if (parser == null;)
    return parser;
  }

}// SingletonFactory

