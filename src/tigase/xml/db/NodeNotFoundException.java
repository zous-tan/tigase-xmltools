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
 * Last modified by $Author$
 * $Date$
 */
package tigase.xml.db;

/**
 * Exception is thrown when user tries to access non-existen node on 1st level.
 * All subnodes on lower higher levels are automatically created when required
 * apart from nodes on 1st level. Nodes on 1st level have special maining. They
 * act in similar way as tables in relational data bases.
 *
 * <p>
 * Created: Thu Nov 11 20:51:20 2004
 * </p>
 * @author <a href="mailto:artur.hefczyc@gmail.com">Artur Hefczyc</a>
 * @version $Rev$
 */
public class NodeNotFoundException extends XMLDBException {

  private static final long serialVersionUID = 1L;

  public NodeNotFoundException() { super(); }
  public NodeNotFoundException(String message) { super(message); }
  public NodeNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
  public NodeNotFoundException(Throwable cause) { super(cause); }

} // NodeNotFoundException
