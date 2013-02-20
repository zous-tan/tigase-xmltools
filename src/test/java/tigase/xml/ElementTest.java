/*
 * ElementTest.java
 * 
 * Tigase Jabber/XMPP Server
 * Copyright (C) 2013 "Andrzej Wójcik" <andrzej.wojcik@tigase.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
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

import junit.framework.TestCase;
import org.junit.Test;

/**
 * Simple tests for Element class
 * 
 * @author andrzej
 */
public class ElementTest extends TestCase {
        
        @Test
        public void testFindChild() {
             Element parent = new Element("parent");
             Element child = new Element("child");
             parent.addChild(child);
             
             Element result = parent.findChild("parent/child");
             
             assertEquals(child, result);
        }
        
        public void testFindChildWithSlashAtBegining() {
             Element parent = new Element("parent");
             Element child = new Element("child");
             parent.addChild(child);
             
             Element result = parent.findChild("/parent/child");
             
             assertEquals(child, result);
        }
        
        @Test
        public void testGetChildCData() {
             String value = "correct-value";
             Element parent = new Element("parent");
             Element child = new Element("child");
             child.setCData(value);
             parent.addChild(child);
             
             String result = parent.getChildCData("parent/child");
             
             assertEquals(value, result);
        }
        
}
