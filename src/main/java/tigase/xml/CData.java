/*
 * Tigase Jabber/XMPP Server
 * Copyright (C) 2004-2012 "Artur Hefczyc" <artur.hefczyc@tigase.org>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, version 3 of the License.
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
 * $Rev$
 * Last modified by $Author$
 * $Date$
 */

package tigase.xml;

/**
 * Created: Feb 9, 2009 12:21:43 PM
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class CData implements XMLNodeIfc<CData> {

	private String cdata = null;

	@Override
	public CData clone() {
		CData result = null;
		try {
			result = (CData)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		} // end of try-catch
		return result;
	}

	public CData(String cdata) {
		this.cdata = cdata;
	}

	public String getCData() {
		return cdata;
	}

	@Override
	public String toString() {
		return cdata;
	}

	@Override
	public String toStringSecure() {
		return (cdata != null && cdata.length() > 2 ? "CData size: " + cdata.length() : cdata);
	}

	public int compareTo(CData o) {
		return cdata.compareTo(o.cdata);
	}

}
