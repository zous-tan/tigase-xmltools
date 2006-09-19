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

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;
import java.net.URLDecoder;
import java.net.URLEncoder;
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
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class DBElement extends Element {

  public static final String NODE  = "node";
  public static final String MAP   = "map";
  public static final String ENTRY = "entry";
  public static final String NAME  = "name";
  public static final String VALUE = "value";
  public static final String KEY   = "key";
	public static final String TYPE  = "type";

  public boolean removed = false;

  public DBElement(String argName) {
    super(argName);
  }

  public DBElement(String argName, String attname, String attvalue) {
    super(argName, new String[] {attname}, new String[] {attvalue});
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
        result.append(" "+key+"=\""+attributes.get(key)+"\"");
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
        for (Element child : children) {
          result.append(((DBElement)child).formatedString(indent, step));
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
      for (Element elem : children) {
        if (elem.getName().equals(NODE) &&
          elem.getAttribute(NAME).equals(name)) {
          return (DBElement)elem;
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
      for (Element elem : children) {
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
    List<Element> entries = getChild(MAP).getChildren();
    if (entries != null) {
      synchronized (entries) {
        for (Element elem : entries) {
          if (elem.getAttribute(KEY).equals(key)) {
            result = (DBElement)elem;
            break;
          } //
        } // end of for (DBElement node : children)
      }
    }
    return result;
  }

  public final void removeEntry(String key) {
    List<Element> entries = getChild(MAP).getChildren();
    if (entries != null) {
      synchronized (entries) {
        for (ListIterator<Element> it = entries.listIterator();
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
    List<Element> entries = getChild(MAP).getChildren();
    if (entries != null) {
      String[] result = null;
      synchronized (entries) {
        result = new String[entries.size()];
        int cnt  = 0;
        for (Element dbe : entries) {
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

  public final void setEntry(String key, Object value) {
		Types.DataType type =
			Types.DataType.valueof(value.getClass().getSimpleName());
    DBElement entry = getEntry(key);
		entry.setAttribute(TYPE, type.toString());
		if (value.getClass().isArray()) {
			if (entry.getChildren() != null) {
				entry.getChildren().clear();
			} // end of if (entry.getChildren() != null)
			switch (type) {
			case INTEGER_ARR:
				for (int val : (int[])value) {
					entry.addChild(new DBElement("item", VALUE, encode(val)));
				} // end of for (String val : values)
				break;
			case DOUBLE_ARR:
				for (double val : (double[])value) {
					entry.addChild(new DBElement("item", VALUE, encode(val)));
				} // end of for (String val : values)
				break;
			case BOOLEAN_ARR:
				for (boolean val : (boolean[])value) {
					entry.addChild(new DBElement("item", VALUE, encode(val)));
				} // end of for (String val : values)
				break;
			default:
				for (Object val : (Object[])value) {
					entry.addChild(new DBElement("item", VALUE, encode(val)));
				} // end of for (String val : values)
				break;
			} // end of switch (type)
		} // end of if (value.getClass().isArray())
		else {
			entry.setAttribute(VALUE,  encode(value));
		} // end of if (value.getClass().isArray()) else
  }

	private String encode(final Object source) {
		try {
			return URLEncoder.encode(source.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return source.toString();
		} // end of try-catch
	}

	private String decode(final String source) {
		try {
			return URLDecoder.decode(source, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return source;
		} // end of try-catch
	}

// 	private void setEntry(String key, Object value, String type) {
//     DBElement entry = getEntry(key);
// 		entry.setAttribute(TYPE, type);
//     entry.setAttribute(VALUE, value.toString());
// 	}

//   private void setEntry(String key, Object values[], String type) {
//     DBElement entry = getEntry(key);
// 		entry.setAttribute(TYPE, type);
//     for (Object val : values) {
//       entry.addChild(new DBElement("item", VALUE, val.toString()));
//     } // end of for (String val : values)
//   }

	public final String getEntryStringValue(String key, String def) {
		return (String)getEntryValue(key, def);
	}

	public final String[] getEntryStringArrValue(String key, String[] def) {
		return (String[])getEntryValue(key, def);
	}

	public final int getEntryIntValue(String key, int def) {
		return ((Integer)getEntryValue(key, new Integer(def))).intValue();
	}

	public final int[] getEntryIntArrValue(String key, int[] def) {
		return (int[])getEntryValue(key, def);
	}

	public final double getEntryDoubleValue(String key, double def) {
		return ((Double)getEntryValue(key, new Double(def))).doubleValue();
	}

	public final double[] getEntryDoubleArrValue(String key, double[] def) {
		return (double[])getEntryValue(key, def);
	}

	public final Object getEntryValue(String key, Object def) {
    DBElement entry = findEntry(key);

		if (entry == null) { return def; }

		Types.DataType type = Types.DataType.valueof(entry.getAttribute(TYPE));

    Object result = def;

		String[] tmp_s = getEntryValues(key);
		int idx = -1;
		try {
			switch (type) {
			case INTEGER:
				result = Integer.decode(entry.getAttribute(VALUE));
				break;
			case INTEGER_ARR:
				int[] tmp_i = new int[tmp_s.length];
				for (String tmp : tmp_s) {
					tmp_i[++idx] = Integer.decode(tmp).intValue();
				} // end of for (String tmp : tmp_s)
				result = tmp_i;
				break;
			case STRING_ARR:
				result = tmp_s;
				break;
			case DOUBLE:
				result = new Double(Double.parseDouble(entry.getAttribute(VALUE)));
				break;
			case DOUBLE_ARR:
				double[] tmp_f = new double[tmp_s.length];
				for (String tmp : tmp_s) {
					tmp_f[++idx] = Double.parseDouble(tmp);
				} // end of for (String tmp : tmp_s)
				result = tmp_f;
				break;
			case BOOLEAN:
				result = new Boolean(parseBool(entry.getAttribute(VALUE)));
				break;
			case BOOLEAN_ARR:
				boolean[] tmp_b = new boolean[tmp_s.length];
				for (String tmp : tmp_s) {
					tmp_b[++idx] = parseBool(tmp);
				} // end of for (String tmp : tmp_s)
				result = tmp_b;
				break;
			case STRING:
			default:
				result = decode(entry.getAttribute(VALUE));
				break;
			} // end of switch (type)
		} // end of try
		catch (NullPointerException e) {
			result = def;
		} // end of try-catch
		return result;
  }

	private boolean parseBool(final String val) {
		return val != null &&
			(val.equalsIgnoreCase("yes")
				|| val.equalsIgnoreCase("true")
				|| val.equalsIgnoreCase("on"));
	}

  private final String[] getEntryValues(String key) {
    Element entry = findEntry(key);
    if (entry != null) {
      List<Element> items = entry.getChildren();
      if (items != null) {
        String[] result = new String[items.size()];
        int cnt  = 0;
        for (Element item : items) {
          result[cnt++] = decode(item.getAttribute(VALUE));
        } // end of for (DBElement dbe : entries)
        return result;
      } // end of if (items != null)
    } // end of if (entry != null)
    return null;
  }

} // DBElement
