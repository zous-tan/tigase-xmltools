/*
 * ElementTest.java
 *
 * Tigase Jabber/XMPP Server
 * Copyright (C) 2004-2012 "Artur Hefczyc" <artur.hefczyc@tigase.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. Look for COPYING file in the top folder.
 * If not, see http://www.gnu.org/licenses/.
 *
 */



package tigase.xml;

//~--- non-JDK imports --------------------------------------------------------

import junit.framework.TestCase;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

//~--- JDK imports ------------------------------------------------------------

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple tests for Element class
 *
 * @author andrzej
 */
public class ElementTest
				extends TestCase {
	private static final String[] static_str_arr = { "parent", "child" };
	private static final String str_path         = "parent/child";
	private static final String str_slash_path   = "/parent/child";
	private static final String xmlns            = "xmlns:1";

	//~--- fields ---------------------------------------------------------------

	private String c_att_name  = null;
	private String c_att_value = null;
	private Element child      = null;
	private String p_att_name  = null;
	private String p_att_value = null;
	private Element parent     = null;
	private String value       = null;

	//~--- set methods ----------------------------------------------------------

	/**
	 * Method description
	 *
	 *
	 * @throws Exception
	 */
	@BeforeClass
	public static void setUpClass() throws Exception {}

	//~--- methods --------------------------------------------------------------

	/**
	 * Method description
	 *
	 *
	 * @throws Exception
	 */
	@AfterClass
	public static void tearDownClass() throws Exception {}

	//~--- set methods ----------------------------------------------------------

	/**
	 * Method description
	 *
	 *
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		value  = "correct-value";
		parent = new Element("parent");
		child  = new Element("child");
		child.setXMLNS(xmlns);
		child.setCData(value);
		parent.addChild(child);
		p_att_name  = "parent_att_name";
		p_att_value = "parent_att_value";
		parent.setAttribute(p_att_name, p_att_value);
		c_att_name  = "child_att_name";
		c_att_value = "child_att_value";
		child.setAttribute(c_att_name, c_att_value);
	}

	//~--- methods --------------------------------------------------------------

	/**
	 * Method description
	 *
	 *
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
		parent      = null;
		child       = null;
		value       = null;
		p_att_name  = null;
		p_att_value = null;
		c_att_name  = null;
		c_att_value = null;
	}

	/**
	 * Method description
	 *
	 */
	@Test
	public void testGetChildCData() {
		String result = parent.getChildCData(str_path);

		assertEquals(value, result);
		result = parent.getChildCData(str_path.split("/"));
		assertEquals(value, result);
		result = parent.getChildCDataStaticStr(str_path.split("/"));
		assertNull(result);
		result = parent.getChildCDataStaticStr(static_str_arr);
		assertEquals(value, result);
	}

	/**
	 * Test of addAttribute method, of class Element.
	 */
	@Test
	public void testAddAttribute() {
		String attName  = "key";
		String attValue = "value";

		parent.addAttribute(attName, attValue);
		assertEquals(attValue, parent.getAttribute(attName));
	}

	/**
	 * Test of addAttributes method, of class Element.
	 */
	@Test
	public void testAddAttributes() {
		Map<String, String> attrs = new HashMap<String, String>();

		parent.removeAttribute(p_att_name);
		attrs.put("key1", "val1");
		attrs.put("key2", "val2");
		parent.addAttributes(attrs);
		assertEquals(attrs.size(), parent.getAttributes().size());
		assertEquals(attrs, parent.getAttributes());
	}

	/**
	 * Test of addChild method, of class Element.
	 */
	@Test
	public void testAddChild() {
		Element instance = new Element("elem1", "cdata1");

		parent.addChild(instance);
		assertEquals(instance, parent.getChild("elem1"));
	}

	/**
	 * Test of clone method, of class Element.
	 */
	@Test
	public void testClone() {
		Element result = parent.clone();

		assertEquals(parent, result);
	}

	/**
	 * Test of findChildStaticStr method, of class Element.
	 */
	@Test
	public void testFindChildStaticStr() {
		Element result = parent.findChildStaticStr(str_path.split("/"));

		assertNull(result);
		result = parent.findChildStaticStr(str_slash_path.split("/"));
		assertNull(result);
		result = parent.findChildStaticStr(static_str_arr);
		assertEquals(child, result);
	}

	/**
	 * Test of findChild method, of class Element.
	 */
	@Test
	public void testFindChild_StringArr() {
		Element result = parent.findChild(str_path.split("/"));

		assertEquals(child, result);
		result = parent.findChild(str_slash_path.split("/"));
		assertEquals(child, result);
	}

	/**
	 * Test of findChild method, of class Element.
	 */
	@Test
	public void testFindChild_String() {
		Element result = parent.findChild(str_path);

		assertEquals(child, result);
		result = parent.findChild(str_slash_path);
		assertEquals(child, result);
	}

	/**
	 * Test of getAttribute method, of class Element.
	 */
	@Test
	public void testGetAttribute_String() {
		assertEquals(p_att_value, parent.getAttribute(p_att_name));
	}

	/**
	 * Test of getChildAttribute method, of class Element.
	 */
	@Test
	public void testGetChildAttribute() {
		assertEquals(c_att_value, parent.getChildAttribute("child", c_att_name));
	}

	/**
	 * Test of getAttributeStaticStr method, of class Element.
	 */
	@Test
	public void testGetAttributeStaticStr() {
		assertEquals(p_att_value, parent.getAttributeStaticStr(p_att_name));
	}

	/**
	 * Test of getAttribute method, of class Element.
	 */
	@Test
	public void testGetAttribute_String_String() {
		assertEquals(c_att_value, parent.getAttribute(str_slash_path, c_att_name));
	}

	/**
	 * Test of getAttribute method, of class Element.
	 */
	@Test
	public void testGetAttribute_StringArr_String() {
		assertEquals(c_att_value, parent.getAttribute(str_slash_path.split("/"), c_att_name));
		assertEquals(c_att_value, parent.getAttribute(static_str_arr, c_att_name));
	}

	/**
	 * Test of getAttributeStaticStr_String_String method, of class Element.
	 */
	@Test
	public void testGetAttributeStaticStr_String_String() {
		assertNull(parent.getAttributeStaticStr(str_slash_path.split("/"), c_att_name));
		assertEquals(c_att_value, parent.getAttributeStaticStr(static_str_arr, c_att_name));
	}

	/**
	 * Test of getCData method, of class Element.
	 */
	@Test
	public void testGetCData_String() {
		assertEquals(value, parent.getCData(str_slash_path));
	}

	/**
	 * Test of getCData method, of class Element.
	 */
	@Test
	public void testGetCData_StringArr() {
		assertEquals(value, parent.getCData(str_slash_path.split("/")));
	}

	/**
	 * Test of getCDataStaticStr method, of class Element.
	 */
	@Test
	public void testGetCDataStaticStr() {
		assertNull(parent.getCDataStaticStr(str_slash_path.split("/")));
		assertEquals(value, parent.getCDataStaticStr(static_str_arr));
	}

	/**
	 * Test of getCData method, of class Element.
	 */
	@Test
	public void testGetCData_0args() {
		assertEquals(value, child.getCData());
	}

	/**
	 * Test of getChild method, of class Element.
	 */
	@Test
	public void testGetChild_String() {
		assertEquals(child, parent.getChild("child"));
		assertEquals(child, parent.getChild(new String("child")));
	}

	/**
	 * Test of getChildStaticStr method, of class Element.
	 */
	@Test
	public void testGetChildStaticStr() {
		assertEquals(child, parent.getChildStaticStr("child"));
		assertNull(parent.getChildStaticStr(new String("child")));
	}

	/**
	 * Test of getChild method, of class Element.
	 */
	@Test
	public void testGetChild_String_String() {
		assertEquals(child, parent.getChild("child", xmlns));
		assertEquals(child, parent.getChild(new String("child"), xmlns));
	}

	/**
	 * Test of getChildCData method, of class Element.
	 */
	@Test
	public void testGetChildCData_String() {
		assertEquals(value, parent.getChildCData(str_path));
		assertEquals(value, parent.getChildCData(str_slash_path));
	}

	/**
	 * Test of getChildCData method, of class Element.
	 */
	@Test
	public void testGetChildCData_StringArr() {
		assertEquals(value, parent.getChildCData(str_path.split("/")));
		assertEquals(value, parent.getChildCData(str_slash_path.split("/")));
		assertEquals(value, parent.getChildCData(static_str_arr));
	}

	/**
	 * Test of getChildCDataStaticStr method, of class Element.
	 */
	@Test
	public void testGetChildCDataStaticStr() {
		assertNull(parent.getChildCDataStaticStr(str_path.split("/")));
		assertNull(value, parent.getChildCDataStaticStr(str_slash_path.split("/")));
		assertEquals(value, parent.getChildCDataStaticStr(static_str_arr));
	}

	/**
	 * Test of getName method, of class Element.
	 */
	@Test
	public void testGetName() {
		assertEquals("parent", parent.getName());
	}

	/**
	 * Test of getXMLNS method, of class Element.
	 */
	@Test
	public void testGetXMLNS_0args() {
		assertEquals(xmlns, child.getXMLNS());
	}

	/**
	 * Test of getXMLNS method, of class Element.
	 */
	@Test
	public void testGetXMLNS_String() {
		assertEquals(xmlns, parent.getXMLNS(str_path));
		assertEquals(xmlns, parent.getXMLNS(str_slash_path));
	}

	/**
	 * Test of getXMLNS method, of class Element.
	 */
	@Test
	public void testGetXMLNS_StringArr() {
		assertEquals(xmlns, parent.getXMLNS(str_path.split("/")));
		assertEquals(xmlns, parent.getXMLNS(str_slash_path.split("/")));
		assertEquals(xmlns, parent.getXMLNS(static_str_arr));
	}

	/**
	 * Test of getXMLNSStaticStr method, of class Element.
	 */
	@Test
	public void testGetXMLNSStaticStr() {
		assertNull(parent.getXMLNSStaticStr(str_path.split("/")));
		assertNull(parent.getXMLNSStaticStr(str_slash_path.split("/")));
		assertEquals(xmlns, parent.getXMLNSStaticStr(static_str_arr));
	}

	/**
	 * Test of removeAttribute method, of class Element.
	 */
	@Test
	public void testRemoveAttribute() {
		parent.removeAttribute(p_att_name);
		assertNull(parent.getAttributeStaticStr(p_att_name));
	}

	/**
	 * Test of removeChild method, of class Element.
	 */
	@Test
	public void testRemoveChild() {
		parent.removeChild(child);
		assertNull(parent.getChild("child"));
	}

	/**
	 * Test of setAttribute method, of class Element.
	 */
	@Test
	public void testSetAttribute() {
		testAddAttribute();
	}

	/**
	 * Test of setCData method, of class Element.
	 */
	@Test
	public void testSetCData() {
		testGetCData_0args();
	}
}


//~ Formatted in Tigase Code Convention on 13/02/20
