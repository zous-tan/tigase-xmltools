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

import java.util.List;
import java.util.Iterator;
import java.util.StringTokenizer;
import tigase.xml.Element;

/**
 * <code>DBElement</code> class extends <code>tigase.xml.Element</code>. It
 * adds some extra functionality useful for data base operations like searching
 * for some specific nodes, add data entries, remove data, and all other common
 * operations not directly related to pure <em>XML</em> processing. Pure
 * <em>XML</em> processing is of course implemented in
 * <code>tigase.xml.Element</code>. The are also some methods which make it
 * easier to save <em>XML</em> tree from memory to disk file in a form which is
 * easier to read by a human.
 *
 * <p>
 * Created: Tue Oct 26 22:01:47 2004
 * </p>
 * @author <a href="mailto:artur.hefczyc@gmail.com">Artur Hefczyc</a>
 * @version $Rev$
 */
public class DBElement extends Element<DBElement> {

  public static final String NODE  = "node";
  public static final String MAP   = "map";
  public static final String ENTRY = "entry";
  public static final String NAME  = "name";
  public static final String VALUE = "value";
  public static final String KEY   = "key";

  public boolean removed = false;

  public DBElement(String argName) {
    super(argName);
  }

  public DBElement(String argName, String attname, String attvalue) {
    super(argName, null,
      new String[] {attname}, new String[] {attvalue});
  }

  public DBElement(String argName, String argCData,
    StringBuilder[] att_names, StringBuilder[] att_values) {
    super(argName, argCData, att_names, att_values);
  }

  public final String formatedString(int indent, int step) {
    StringBuilder result = new StringBuilder();
    result.append("\n");
    for (int i = 0; i < indent; i++) {
      result.append(" ");
    }
    result.append("<"+name);
    if (attributes != null) {
      for (String key : attributes.keySet()) {
        result.append(" "+key+"='"+attributes.get(key)+"'");
      } // end of for ()
    } // end of if (attributes != null)
    String childrenStr = childrenFormatedString(indent+step, step);
    if (cdata != null || childrenStr.length() > 0) {
      result.append(">");
      if (cdata != null) {
        result.append(cdata);
      } // end of if (cdata != null)
      result.append(childrenStr);
      result.append("\n");
      for (int i = 0; i < indent; i++) {
        result.append(" ");
      }
      result.append("</"+name+">");
    } else {
      result.append("/>");
    }
    return result.toString();
  }

  public final String childrenFormatedString(int indent, int step) {
    StringBuilder result = new StringBuilder();
    if (children != null) {
      synchronized (children) {
        for (DBElement child : children) {
          result.append(child.formatedString(indent, step));
        } // end of for ()
      }
    } // end of if (child != null)
    return result.toString();
  }

  public final DBElement getSubnode(String name) {
    if (children == null) {
      return null;
    } // end of if (children == null)
    synchronized (children) {
      for (DBElement elem : children) {
        if (elem.getName().equals(NODE) &&
          elem.getAttribute(NAME).equals(name)) {
          return elem;
        } //
      } // end of for (DBElement node : children)
    }
    return null;
  }

  public final String[] getSubnodes() {
    if (children == null || children.size() == 1) {
      return null;
    } // end of if (children == null)
    // Minus <map/> element
    String[] result = new String[children.size()-1];
    synchronized (children) {
      int idx = 0;
      for (DBElement elem : children) {
        if (elem.getName().equals(NODE)) {
          result[idx++] = elem.getAttribute(NAME);
        } //
      } // end of for (DBElement node : children)
    }
    return result;
  }

  public final DBElement findNode(String nodePath) {
    StringTokenizer strtok = new StringTokenizer(nodePath, "/", false);
    if (!getName().equals(NODE) ||
      !getAttribute(NAME).equals(strtok.nextToken())) {
      return null;
    } // end of if (!strtok.nextToken().equals(child.getName()))
    DBElement node = this;
    while (strtok.hasMoreTokens() && node != null) {
      node = node.getSubnode(strtok.nextToken());
    } // end of while (strtok.hasMoreTokens())
    return node;
  }

  public final void removeNode(String nodePath) {
    StringTokenizer strtok = new StringTokenizer(nodePath, "/", false);
    DBElement node = this;
    DBElement parent = null;
    while (strtok.hasMoreTokens() && node != null) {
      parent = node;
      node = node.getSubnode(strtok.nextToken());
    } // end of while (strtok.hasMoreTokens())
    if (parent != null && node != null) {
      boolean res = parent.removeChild(node);
    } // end of if (parent != null && node != null)
  }

  public final DBElement buildNodesTree(String nodePath) {
    StringTokenizer strtok = new StringTokenizer(nodePath, "/", false);
    DBElement node = this;
    while (strtok.hasMoreTokens()) {
      String token = strtok.nextToken();
      DBElement tmp = node.getSubnode(token);
      if (tmp != null) {
        node = tmp;
      } // end of if (node.getSubnode() != null)
      else {
        if (token.equals("") || token.equals("null")) {
          return null;
        } // end of if (token.equals("") || token.equals("null"))
        node = node.newSubnode(token);
      } // end of if (node.getSubnode() != null) else
    } // end of while (strtok.hasMoreTokens())
    return node;
  }

  public final DBElement newSubnode(String name) {
    DBElement node =
      new DBElement(NODE, NAME, name);
    node.addChild(new DBElement(MAP));
    addChild(node);
    return node;
  }

  public final DBElement findEntry(String key) {
    DBElement result = null;
    List<DBElement> entries = getChild(MAP).getChildren();
    if (entries != null) {
      synchronized (entries) {
        for (DBElement elem : entries) {
          if (elem.getAttribute(KEY).equals(key)) {
            result = elem;
            break;
          } //
        } // end of for (DBElement node : children)
      }
    }
    return result;
  }

  public final void removeEntry(String key) {
    DBElement result = null;
    List<DBElement> entries = getChild(MAP).getChildren();
    if (entries != null) {
      synchronized (entries) {
        for (Iterator<DBElement> it = entries.iterator();
             it.hasNext();) {
          if (it.next().getAttribute(KEY).equals(key)) {
            it.remove();
            break;
          } //
        } // end of for (DBElement node : children)
      }
    }
  }

  public final String[] getEntryKeys() {
    List<DBElement> entries = getChild(MAP).getChildren();
    if (entries != null) {
      String[] result = null;
      synchronized (entries) {
        result = new String[entries.size()];
        int cnt  = 0;
        for (DBElement dbe : entries) {
          result[cnt++] = dbe.getAttribute(KEY);
        } // end of for (DBElement dbe : entries)
      }
      return result;
    }
    return null;
  }

  public final DBElement getEntry(String key) {
    DBElement result = findEntry(key);
    if (result == null) {
      result = new DBElement(ENTRY,KEY, key);
      getChild(MAP).addChild(result);
    } // end of if (result == null)
    return result;
  }

  public final void setEntry(String key, String value) {
    DBElement entry = getEntry(key);
    entry.setAttribute(VALUE, value);
  }

  public final void setEntry(String key, String values[]) {
    DBElement entry = getEntry(key);
    for (String val : values) {
      entry.addChild(new DBElement("item", VALUE, val));
    } // end of for (String val : values)
  }

  public final String getEntryValue(String key, String def) {
    DBElement entry = findEntry(key);
    String result = null;
    if (entry != null) {
      result = entry.getAttribute(VALUE);
    } // end of if (entry != null)
    return result != null ? result : def;
  }

  public final String[] getEntryValues(String key) {
    DBElement entry = findEntry(key);
    if (entry != null) {
      List<DBElement> items = entry.getChildren();
      if (items != null) {
        String[] result = new String[items.size()];
        int cnt  = 0;
        for (DBElement item : items) {
          result[cnt++] = item.getAttribute(VALUE);
        } // end of for (DBElement dbe : entries)
        return result;
      } // end of if (items != null)
    } // end of if (entry != null)
    return null;
  }

} // DBElement
