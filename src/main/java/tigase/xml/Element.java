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

import java.util.Collections;
import java.util.TreeMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Arrays;

import tigase.annotations.TODO;

/**
 * <code>Element</code> - basic document tree node implementation.
 *  Supports Java 5.0 generic feature to make it easier to extend this class and
 *  still preserve some useful functionality. Sufficient for simple cases but
 *  probably in the most more advanced cases should be extended with additional
 *  features. Look in API documentation for more details and information about
 *  existing extensions. The most important features apart from abvious tree
 *  implementation are:
 *  <ul>
 *   <li><code>toString()</code> implementation so it can generate valid
 *    <em>XML</em> content from this element and all children.</li>
 *   <li><code>addChild(...)</code>, <code>getChild(childName)</code> supporting
 *    generic types.</li>
 *   <li><code>findChild(childPath)</code> finding child in subtree by given
 *    path to element.</li>
 *   <li><code>getChildCData(childPath)</code>, <code>getAttribute(childPath,
 *     attName)</code> returning element CData from child in subtree by given
 *    path to element.</li>
 *  </ul>
 *
 * <p>
 * Created: Mon Oct  4 17:55:16 2004
 * </p>
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
@TODO(note="Make it a bit lighter.")
public class Element implements Comparable<Element>, Cloneable {

  protected String name = null;
  protected String cdata = null;
  protected String xmlns = null;
  protected TreeMap<String, String> attributes = null;
  protected LinkedList<Element> children = null;

	@SuppressWarnings({"unchecked"})
	public Element clone() {
		Element result = null;
		try {
			result = (Element)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		} // end of try-catch
		if (attributes != null) {
			result.attributes = (TreeMap<String, String>)attributes.clone();
		} else {
			result.attributes = null;
		} // end of else
		if (children != null) {
			result.setChildren(children);
		} else {
			result.children = null;
		} // end of else
		return result;
	}

	public Element(final Element element) {
		Element src =  element.clone();
		this.attributes = src.attributes;
		this.name  = src.name;
		this.cdata  = src.cdata;
		this.xmlns  = src.xmlns;
		this.children  = src.children;
	}

	public Element(final String argName) {
    setName(argName);
  }

	public Element(final String argName, final String argCData) {
    setName(argName);
    setCData(argCData);
  }

  public Element(final String argName, final String argCData,
    final StringBuilder[] att_names, final StringBuilder[] att_values) {
    setName(argName);
    setCData(argCData);
    if (att_names != null) {
      setAttributes(att_names, att_values);
    } // end of if (att_names != null)
  }

  public Element(final String argName, final String argCData,
    final String[] att_names, final String[] att_values) {
    setName(argName);
    setCData(argCData);
    if (att_names != null) {
      setAttributes(att_names, att_values);
    } // end of if (att_names != null)
  }

  public Element(final String argName,
    final String[] att_names, final String[] att_values) {
    setName(argName);
    if (att_names != null) {
      setAttributes(att_names, att_values);
    } // end of if (att_names != null)
  }

  public Element(final String argName, final Element[] children,
    final String[] att_names, final String[] att_values) {
    setName(argName);
    if (att_names != null) {
      setAttributes(att_names, att_values);
    } // end of if (att_names != null)
		addChildren(Arrays.asList(children));
  }

  public List<Element> getChildren() {
    return children;
  }

  public List<Element> getChildren(final String elementPath) {
    final Element child = findChild(elementPath);
    return child != null ? child.getChildren() : null;
  }

  public void setChildren(final List<Element> children) {
    this.children = new LinkedList<Element>();
    synchronized (this.children) {
			for (Element child: children) {
				this.children.add(child.clone());
			} // end of for (Element child: children)
			Collections.sort(children);
		}
  }

  public void addChildren(final List<Element> children) {
		if (children == null) {
			return;
		} // end of if (children == null)
    if (this.children == null) {
      this.children = new LinkedList<Element>();
    } // end of if (children == null)
    synchronized (this.children) {
			this.children.addAll(children);
			Collections.sort(children);
    }
  }

  public String toStringNoChildren() {
    StringBuilder result = new StringBuilder();
    result.append("<"+name);
    if (attributes != null) {
      for (String key : attributes.keySet()) {
        result.append(" "+key+"=\""+attributes.get(key)+"\"");
      } // end of for ()
    } // end of if (attributes != null)
    if (cdata != null) {
      result.append(">");
      if (cdata != null) {
        result.append(cdata);
      } // end of if (cdata != null)
      result.append("</"+name+">");
    } else {
      result.append("/>");
    }
    return result.toString();
  }

  public String toString() {
    StringBuilder result = new StringBuilder();
    result.append("<"+name);
    if (attributes != null) {
      for (String key : attributes.keySet()) {
        result.append(" "+key+"=\""+attributes.get(key)+"\"");
      } // end of for ()
    } // end of if (attributes != null)
    final String childrenStr = childrenToString();
    if (cdata != null || childrenStr.length() > 0) {
      result.append(">");
      if (cdata != null) {
        result.append(cdata);
      } // end of if (cdata != null)
      result.append(childrenStr);
      result.append("</"+name+">");
    } else {
      result.append("/>");
    }
    return result.toString();
  }

  public String childrenToString() {
    StringBuilder result = new StringBuilder();
    if (children != null) {
      synchronized (children) {
        for (Element child : children) {
          result.append(child.toString());
        } // end of for ()
      }
    } // end of if (child != null)
    return result.toString();
  }

  public void addChild(final Element child) {
    if (children == null) {
      children = new LinkedList<Element>();
    } // end of if (children == null)
    synchronized (children) {
      children.add(child);
			Collections.sort(children);
    }
  }

  public boolean removeChild(final Element child) {
    boolean res = false;
    if (children != null) {
      synchronized (children) {
        res = children.remove(child);
      }
    } // end of if (children == null)
    return res;
  }

  public Element getChild(final String name) {
    if (children != null) {
      synchronized (children) {
        for (Element el : children) {
          if (el.getName().equals(name)) {
            return el;
          }
        }
      }
    } // end of if (children != null)
    return null;
  }

  public Element getChild(final String name, final String child_xmlns) {
		if (child_xmlns == null) {
			return getChild(name);
		}
    if (children != null) {
      synchronized (children) {
        for (Element el : children) {
          if (el.getName().equals(name)
						&& ((el.getXMLNS() == null && child_xmlns == null)
							|| (el.getXMLNS() != null && child_xmlns != null
								&& el.getXMLNS().equals(child_xmlns)))) {
            return el;
          }
        }
      }
    } // end of if (children != null)
    return null;
  }

  public Element findChild(final String elementPath) {
    final StringTokenizer strtok = new StringTokenizer(elementPath, "/", false);
    if (!strtok.nextToken().equals(getName())) {
      return null;
    } // end of if (!strtok.nextToken().equals(child.getName()))
    Element child = this;
    while (strtok.hasMoreTokens() && child != null) {
      child = child.getChild(strtok.nextToken());
    } // end of while (strtok.hasMoreTokens())
    return child;
  }

  public String getChildCData(final String elementPath) {
    final Element child = findChild(elementPath);
    return child != null ? child.getCData() : null;
  }

  public String getCData(final String elementPath) {
		return getChildCData(elementPath);
  }

  /**
   * Get the Attributes value.
   * @return the Attributes value.
   */
  public Map<String, String> getAttributes() {
    return attributes;
  }

  /**
   * Set the Attributes value.
   * @param newAttributes The new Attributes value.
   */
  public void setAttributes(final Map<String, String> newAttributes) {
    attributes = new TreeMap<String, String>(newAttributes);
  }

  public String getAttribute(final String attName) {
    if (attributes != null) {
      synchronized (attributes) {
        return attributes.get(attName);
      }
    } // end of if (attributes != null)
    return null;
  }

  public void addAttribute(final String attName, final String attValue) {
    setAttribute(attName, attValue);
  }

	public void addAttributes(Map<String, String> attrs) {
		attributes.putAll(attrs);
	}

  public void setDefXMLNS(final String ns) {
    xmlns = ns;
  }

	public void setXMLNS(final String ns) {
		setAttribute("xmlns", ns);
	}

  /**
   *
   */
  public String getXMLNS() {
    String ns = getAttribute("xmlns");
    return ns != null ? ns : xmlns;
  }

  /**
   *
   */
  public String getXMLNS(final String elementPath) {
    Element child = findChild(elementPath);
    return child != null ? child.getXMLNS() : null;
  }

  public String getAttribute(final String elementPath,
		final String att_name) {
    final Element child = findChild(elementPath);
    return child != null ? child.getAttribute(att_name) : null;
  }

  public void setAttribute(final String elementPath,
    final String att_name, final String att_value) {
    final Element child = findChild(elementPath);
    if (child != null) {
      child.setAttribute(att_name, att_value);
    } // end of if (child != null)
  }

  public void setAttribute(final String key, final String value) {
    if (attributes == null) {
      attributes = new TreeMap<String, String>();
    } // end of if (attributes == null)
    synchronized (attributes) {
      attributes.put(key, value);
    }
  }

	public void removeAttribute(final String key) {
    if (attributes != null) {
			synchronized (attributes) {
				attributes.remove(key);
			}
    } // end of if (attributes == null)
	}

  public void setAttributes(final StringBuilder[] names,
		final StringBuilder[] values) {
    attributes = new TreeMap<String, String>();
    synchronized (attributes) {
      for (int i = 0; i < names.length; i++) {
        if (names[i] != null) {
          attributes.put(names[i].toString(), values[i].toString());
        } // end of if (names[i] != null)
      } // end of for (int i = 0; i < names.length; i++)
    }
  }

  public void setAttributes(final String[] names, final String[] values) {
    attributes = new TreeMap<String, String>();
    synchronized (attributes) {
      for (int i = 0; i < names.length; i++) {
        if (names[i] != null) {
          attributes.put(names[i], values[i]);
        } // end of if (names[i] != null)
      } // end of for (int i = 0; i < names.length; i++)
    }
  }

  /**
   * Gets the value of name
   *
   * @return the value of name
   */
  public String getName()  {
    return this.name;
  }

  /**
   * Sets the value of name
   *
   * @param argName Value to assign to this.name
   */
  public void setName(final String argName) {
    this.name = argName;
  }

  /**
   * Gets the value of cdata
   *
   * @return the value of cdata
   */
  public String getCData()  {
    return this.cdata;
  }

  /**
   * Sets the value of cdata
   *
   * @param argCData Value to assign to this.cdata
   */
  public void setCData(final String argCData) {
    this.cdata = argCData;
  }

  // Implementation of java.lang.Comparable

  /**
   * Method <code>compareTo</code> is used to perform 
   *
   * @param elem an <code>Object</code> value
   * @return an <code>int</code> value
   */
  public int compareTo(final Element elem) {
// 		int result = name.compareTo(elem.getName());
// 		if (result == 0) {
// 			if (getXMLNS() != null) {
// 				if (elem.getXMLNS() != null) {
// 					result = getXMLNS().compareTo(elem.getXMLNS());
// 				} else {
// 					result = 1;
// 				}
// 			} else {
// 				if (elem.getXMLNS() != null) {
// 					result = -1;
// 				} else {
// 					result = 0;
// 				}
// 			}
// 		}
//     return result;
		return toStringNoChildren().compareTo(elem.toStringNoChildren());
  }

	public boolean equals(Object obj) {
		if (obj instanceof Element) {
 			Element elem = (Element)obj;
// 			boolean result = name.equals(elem.getName());
// 			if (result) {
// 				if (getXMLNS() != null && elem.getXMLNS() != null) {
// 					result = getXMLNS().equals(elem.getXMLNS());
// 				} else {
// 					result = getXMLNS() == elem.getXMLNS();
// 				}
// 			}
// 			return result;
			return toStringNoChildren().equals(elem.toStringNoChildren());
		}
		return false;
	}

	public int hashCode() {
// 		String hash_str = name + (getXMLNS() != null ? getXMLNS() : "");
// 		return hash_str.hashCode();
		return toStringNoChildren().hashCode();
	}

	public static void main(final String[] args) {
		Element elem = new Element("Test", "This is a test",
			new String[] {"first-name", "last-name"},
			new String[] {"Artur", "Hefczyc"});
		elem.addChild(new Element("Chile-element"));
		Element clone = elem.clone();
		System.out.println(clone.toString());
	}


}// Element
