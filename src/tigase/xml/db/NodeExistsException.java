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

/**
 * This exception is thrown when user tries to add subnode with name which
 * already exists in data base. Data base implementation requires that all nodes
 * have unique names thus adding multiple nodes with the same name is not
 * permitted.
 *
 * <p>
 * Created: Thu Nov 11 20:52:34 2004
 * </p>
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class NodeExistsException extends XMLDBException {

  private static final long serialVersionUID = 1L;

  public NodeExistsException() { super(); }
  public NodeExistsException(String message) { super(message); }
  public NodeExistsException(String message, Throwable cause) {
    super(message, cause);
  }
  public NodeExistsException(Throwable cause) { super(cause); }

} // NodeExistsException
