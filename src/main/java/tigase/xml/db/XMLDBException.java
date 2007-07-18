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
package tigase.xml.db;

/**
 * This is parrent exception for all data base related exceptions. It is not
 * directly thrown. They are a few descendants implementations which are thrown
 * in some particular cases.
 * <p>
 * Created: Thu Nov 11 20:49:08 2004
 * </p>
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class XMLDBException extends Exception {

  private static final long serialVersionUID = 1L;

  public XMLDBException() { super(); }
  public XMLDBException(String message) { super(message); }
  public XMLDBException(String message, Throwable cause) {
    super(message, cause);
  }
  public XMLDBException(Throwable cause) { super(cause); }

} // XMLDBException