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

import java.io.FileReader;
import java.util.LinkedHashMap;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Arrays;

import java.util.Queue;
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
public class Element implements XMLNodeIfc<Element> {

  protected String name = null;
  //protected String cdata = null;
  protected String defxmlns = null;
  protected String xmlns = null;
  protected IdentityHashMap<String, String> attributes = null;
  protected LinkedList<XMLNodeIfc> children = null;

	@SuppressWarnings({"unchecked"})
	@Override
	public Element clone() {
		Element result = null;
		try {
			result = (Element)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		} // end of try-catch
		if (attributes != null) {
			result.attributes = (IdentityHashMap<String, String>)attributes.clone();
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

	public Element(Element element) {
		Element src =  element.clone();
		this.attributes = src.attributes;
		this.name  = src.name;
		//this.cdata  = src.cdata;
		this.defxmlns  = src.defxmlns;
		this.xmlns  = src.xmlns;
		this.children  = src.children;
	}

	public Element(String argName) {
    setName(argName);
  }

	public Element(String argName, String argCData) {
    setName(argName);
		if (argCData != null) {
			setCData(argCData);
		}
  }

  public Element(String argName, String argCData,
    StringBuilder[] att_names, StringBuilder[] att_values) {
    setName(argName);
		if (argCData != null) {
			setCData(argCData);
		}
    if (att_names != null) {
      setAttributes(att_names, att_values);
    } // end of if (att_names != null)
  }

  public Element(String argName, String argCData,
    String[] att_names, String[] att_values) {
    setName(argName);
		if (argCData != null) {
			setCData(argCData);
		}
    if (att_names != null) {
      setAttributes(att_names, att_values);
    } // end of if (att_names != null)
  }

  public Element(String argName,
    String[] att_names, String[] att_values) {
    setName(argName);
    if (att_names != null) {
      setAttributes(att_names, att_values);
    } // end of if (att_names != null)
  }

  public Element(String argName, Element[] children,
    String[] att_names, String[] att_values) {
    setName(argName);
    if (att_names != null) {
      setAttributes(att_names, att_values);
    } // end of if (att_names != null)
		addChildren(Arrays.asList(children));
  }

  public List<Element> getChildren() {
    if (children != null) {
			LinkedList<Element> result = new LinkedList<Element>();
			for (XMLNodeIfc node : children) {
				if (node instanceof Element) {
					result.add((Element) node);
				}
			}
			return result;
		}
		return null;
  }

  public List<Element> getChildren(String elementPath) {
    Element child = findChild(elementPath);
    return child != null ? child.getChildren() : null;
  }

  public void setChildren(List<XMLNodeIfc> children) {
    this.children = new LinkedList<XMLNodeIfc>();
    synchronized (this.children) {
			for (XMLNodeIfc child: children) {
				this.children.add(child.clone());
			} // end of for (Element child: children)
			//Collections.sort(children);
		}
  }

  public void addChildren(List<Element> children) {
		if (children == null) {
			return;
		} // end of if (children == null)
    if (this.children == null) {
      this.children = new LinkedList<XMLNodeIfc>();
    } // end of if (children == null)
    synchronized (this.children) {
			for (XMLNodeIfc child: children) {
				this.children.add(child.clone());
			} // end of for (Element child: children)
			//this.children.addAll(children);
			//Collections.sort(children);
    }
  }

  public String toStringNoChildren() {
    StringBuilder result = new StringBuilder();
    result.append("<").append(name);
    if (attributes != null) {
      for (String key : attributes.keySet()) {
        result.append(" ").append(key).append("=\"").append(attributes.get(key)).append("\"");
      } // end of for ()
    } // end of if (attributes != null)
		String cdata = cdataToString();
    if (cdata != null) {
      result.append(">");
      if (cdata != null) {
        result.append(cdata);
      } // end of if (cdata != null)
      result.append("</").append(name).append(">");
    } else {
      result.append("/>");
    }
    return result.toString();
  }

	@Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    result.append("<").append(name);
    if (attributes != null) {
      for (String key : attributes.keySet()) {
        result.append(" ").append(key).append("=\"").append(attributes.get(key)).append("\"");
      } // end of for ()
    } // end of if (attributes != null)
    String childrenStr = childrenToString();
    if (childrenStr != null && childrenStr.length() > 0) {
      result.append(">");
      result.append(childrenStr);
      result.append("</").append(name).append(">");
    } else {
      result.append("/>");
    }
    return result.toString();
  }

	protected String cdataToString() {
    StringBuilder result = new StringBuilder();
    if (children != null) {
      synchronized (children) {
        for (XMLNodeIfc child : children) {
					// This is weird but if there is a bug in some other component
					// it may add null children to the element, let's be save here.
					if (child != null && child instanceof CData) {
						result.append(child.toString());
					}
        } // end of for ()
      }
    } // end of if (child != null)
    return result.length() > 0 ? result.toString() : null;
	}

  public String childrenToString() {
		StringBuilder result = new StringBuilder();
    if (children != null) {
      synchronized (children) {
        for (XMLNodeIfc child : children) {
					// This is weird but if there is a bug in some other component
					// it may add null children to the element, let's be save here.
					if (child != null) {
						result.append(child.toString());
					}
        } // end of for ()
      }
    } // end of if (child != null)
    return result.length() > 0 ? result.toString() : null;
  }

  public void addChild(XMLNodeIfc child) {
		if (child == null) {
			throw new NullPointerException("Element child can not be null.");
		}
    if (children == null) {
      children = new LinkedList<XMLNodeIfc>();
    } // end of if (children == null)
    synchronized (children) {
      children.add(child);
			//Collections.sort(children);
    }
  }

  public boolean removeChild(Element child) {
    boolean res = false;
    if (children != null) {
      synchronized (children) {
        res = children.remove(child);
      }
    } // end of if (children == null)
    return res;
  }

  public Element getChild(String name) {
    if (children != null) {
      synchronized (children) {
        for (XMLNodeIfc el : children) {
					if (el instanceof Element) {
						Element elem = (Element)el;
						if (elem.getName().equals(name)) {
							return elem;
						}
					}
        }
      }
    } // end of if (children != null)
    return null;
  }

  public Element getChild(String name, String child_xmlns) {
		if (child_xmlns == null) {
			return getChild(name);
		}
    if (children != null) {
      synchronized (children) {
        for (XMLNodeIfc el : children) {
					if (el instanceof Element) {
						Element elem = (Element) el;
						if (elem.getName().equals(name) &&
										((elem.getXMLNS() == child_xmlns) ||
										(elem.getXMLNS() != null &&
										elem.getXMLNS().equals(child_xmlns)))) {
							return elem;
						}
					}
        }
      }
    } // end of if (children != null)
    return null;
  }

  public Element findChild(String elementPath) {
    StringTokenizer strtok = new StringTokenizer(elementPath, "/", false);
    if (!strtok.nextToken().equals(getName())) {
      return null;
    } // end of if (!strtok.nextToken().equals(child.getName()))
    Element child = this;
    while (strtok.hasMoreTokens() && child != null) {
      child = child.getChild(strtok.nextToken());
    } // end of while (strtok.hasMoreTokens())
    return child;
  }

  public String getChildCData(String elementPath) {
    Element child = findChild(elementPath);
    return child != null ? child.getCData() : null;
  }

  public String getCData(String elementPath) {
		return getChildCData(elementPath);
  }

  /**
   * Get the Attributes value.
   * @return the Attributes value.
   */
  public Map<String, String> getAttributes() {
    return
      (attributes != null ? new LinkedHashMap<String, String>(attributes) : null);
  }

  /**
   * Set the Attributes value.
   * @param newAttributes The new Attributes value.
   */
  public void setAttributes(Map<String, String> newAttributes) {
    attributes = new IdentityHashMap<String, String>(newAttributes.size());
		synchronized (attributes) {
			for (Map.Entry<String, String> entry: newAttributes.entrySet()) {
				attributes.put(entry.getKey().intern(), entry.getValue());
			}
		}
  }

  public String getAttribute(String attName) {
    if (attributes != null) {
      synchronized (attributes) {
        return attributes.get(attName);
      }
    } // end of if (attributes != null)
    return null;
  }

  public void addAttribute(String attName, String attValue) {
    setAttribute(attName, attValue);
  }

	public void addAttributes(Map<String, String> attrs) {
		if (attributes == null) {
			attributes = new IdentityHashMap<String, String>(attrs.size());
		}
		synchronized (attributes) {
			for (Map.Entry<String, String> entry: attrs.entrySet()) {
				attributes.put(entry.getKey().intern(), entry.getValue());
			}
		}
	}

  public void setDefXMLNS(String ns) {
    defxmlns = ns.intern();
  }

	public void setXMLNS(String ns) {
		xmlns = ns.intern();
		setAttribute("xmlns", ns);
	}

  /**
   *
	 * @return
	 */
  public String getXMLNS() {
		if (xmlns == null) {
			xmlns = getAttribute("xmlns");
			xmlns = (xmlns != null ? xmlns.intern() : null);
		}
    return xmlns != null ? xmlns : defxmlns;
  }

  /**
   *
	 * @param elementPath
	 * @return 
	 */
  public String getXMLNS(String elementPath) {
    Element child = findChild(elementPath);
    return child != null ? child.getXMLNS() : null;
  }

  public String getAttribute(String elementPath,
		String att_name) {
    Element child = findChild(elementPath);
    return child != null ? child.getAttribute(att_name) : null;
  }

  public void setAttribute(String elementPath,
    String att_name, String att_value) {
    Element child = findChild(elementPath);
    if (child != null) {
      child.setAttribute(att_name.intern(), att_value);
    } // end of if (child != null)
  }

  public void setAttribute(String key, String value) {
    if (attributes == null) {
      attributes = new IdentityHashMap<String, String>(5);
    } // end of if (attributes == null)
    synchronized (attributes) {
      attributes.put(key.intern(), value);
    }
  }

	public void removeAttribute(String key) {
    if (attributes != null) {
			synchronized (attributes) {
				attributes.remove(key);
			}
    } // end of if (attributes == null)
	}

  public void setAttributes(StringBuilder[] names,
		StringBuilder[] values) {
    attributes = new IdentityHashMap<String, String>(names.length);
    synchronized (attributes) {
      for (int i = 0; i < names.length; i++) {
        if (names[i] != null) {
          attributes.put(names[i].toString().intern(), values[i].toString());
        } // end of if (names[i] != null)
      } // end of for (int i = 0; i < names.length; i++)
    }
  }

  public void setAttributes(String[] names, String[] values) {
    attributes = new IdentityHashMap<String, String>(names.length);
    synchronized (attributes) {
      for (int i = 0; i < names.length; i++) {
        if (names[i] != null) {
          attributes.put(names[i].intern(), values[i]);
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
  public void setName(String argName) {
    this.name = argName.intern();
  }

  /**
   * Gets the value of cdata
   *
   * @return the value of cdata
   */
  public String getCData()  {
    return cdataToString();
  }

  /**
   * Sets the value of cdata
   *
   * @param argCData Value to assign to this.cdata
   */
  public void setCData(String argCData) {
    addChild(new CData(argCData));
  }

  // Implementation of java.lang.Comparable

  /**
   * Method <code>compareTo</code> is used to perform 
   *
   * @param elem an <code>Object</code> value
   * @return an <code>int</code> value
   */
  public int compareTo(Element elem) {
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

	@Override
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

	@Override
	public int hashCode() {
// 		String hash_str = name + (getXMLNS() != null ? getXMLNS() : "");
// 		return hash_str.hashCode();
		return toStringNoChildren().hashCode();
	}

	public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      System.err.println("You must give file name as parameter.");
      System.exit(1);
    } // end of if (args.length < 1)

    FileReader file = new FileReader(args[0]);
    char[] buff = new char[1];
    SimpleParser parser = new SimpleParser();
		DomBuilderHandler dom = new DomBuilderHandler();
		int result = -1;
    while((result = file.read(buff)) != -1) {
      parser.parse(dom, buff, 0, result);
    }
    file.close();
		Queue<Element> elems = dom.getParsedElements();
		for (Element elem : elems) {
			Element clone = elem.clone();
			System.out.println(elem.toString());
		}
	}


}// Element
