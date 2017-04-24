/***********************************************************
Copyright (C) 2015 VeriSign, Inc.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

http://www.verisign.com/nds/naming/namestore/techdocs.html
 ***********************************************************/

package com.verisign.epp.codec.idnmap;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
// W3C Imports
import org.w3c.dom.Element;

//----------------------------------------------
// Imports
//----------------------------------------------
// SDK Imports
import com.verisign.epp.codec.gen.EPPCodecComponent;
import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EqualityUtil;

/**
 * <code>EPPIdnMap</code> is used in the extension to the domain create command
 * to define the IDN table and optionally the Unicode NFC of the domain name,
 * and in the extension to the domain info response to return the same
 * information about the domain name.
 */
public class EPPIdnMap implements EPPCodecComponent {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPIdnMap.class.getName(),
			EPPCatFactory.getInstance().getFactory());

	/**
	 * XML local name for <code>EPPIdnMap</code>.
	 */
	public static final String ELM_LOCALNAME = "data";

	/**
	 * XML root tag for <code>EPPIdnMap</code>.
	 */
	public static final String ELM_NAME = EPPIdnMapExtFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * XML local name for the table element
	 */
	private static final String ELM_TABLE = "table";

	/**
	 * XML local name for the uname element
	 */
	private static final String ELM_UNAME = "uname";

	/**
	 * The IDN table identifier as provided by the server.
	 */
	private String table;

	/**
	 * OPTIONAL domain name in Unicode NFC.
	 */
	private String uname;

	/**
	 * Default constructor for <code>EPPIdnMap</code>.
	 */
	public EPPIdnMap() {
	}

	/**
	 * Constructor for <code>EPPIdnMap</code> that takes the IDN table
	 * identifier.
	 * 
	 * @param aTable
	 *            IDN table identifier as provided by the server
	 */
	public EPPIdnMap(String aTable) {
		this.setTable(aTable);
	}

	/**
	 * Constructor for <code>EPPIdnMap</code> that takes both the IDN table
	 * identifier and the domain name uname in Unicode NFC.
	 * 
	 * @param aTable
	 *            IDN table identifier as provided by the server
	 * @param aUname
	 *            Domain name in Unicode NFC
	 */
	public EPPIdnMap(String aTable, String aUname) {
		this.setTable(aTable);
		this.setUname(aUname);
	}

	/**
	 * Gets the IDN table identifier.
	 * 
	 * @return The IDN table identifier as provided by the server.
	 */
	public String getTable() {
		return this.table;
	}

	/**
	 * Sets the IDN table identifier.
	 * 
	 * @param aTable
	 *            The IDN table identifier as provided by the server.
	 */
	public void setTable(String aTable) {
		this.table = aTable;
	}

	/**
	 * Is the uname defined?
	 * 
	 * @return <code>true</code> if the uname is defined; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasUname() {
		return (this.uname != null ? true : false);
	}

	/**
	 * Gets the uname value, which is the domain name in Unicode NFC.
	 * 
	 * @return Domain uname value if defined; <code>null</code> otherwise.
	 */
	public String getUname() {
		return this.uname;
	}

	/**
	 * Sets the uname value.
	 * 
	 * @param aUname
	 *            Domain name in Unicode NFC
	 */
	public void setUname(String aUname) {
		this.uname = aUname;
	}

	/**
	 * encode instance into a DOM element tree. A DOM Document is passed as an
	 * argument and functions as a factory for DOM objects. The root element
	 * associated with the instance is created and each instance attribute is
	 * appended as a child node.
	 * 
	 * @param aDocument
	 *            DOM Document, which acts is an Element factory
	 * 
	 * @return Element Root element associated with the object
	 * 
	 * @exception EPPEncodeException
	 *                Error encoding <code>EPPIdnMap</code>
	 */
	public Element encode(Document aDocument) throws EPPEncodeException {

		// Create root element
		Element root = aDocument.createElementNS(EPPIdnMapExtFactory.NS,
				ELM_NAME);

		// Table
		EPPUtil.encodeString(aDocument, root, this.table,
				EPPIdnMapExtFactory.NS, EPPIdnMapExtFactory.NS_PREFIX + ":"
						+ ELM_TABLE);

		// Uname
		EPPUtil.encodeString(aDocument, root, this.uname,
				EPPIdnMapExtFactory.NS, EPPIdnMapExtFactory.NS_PREFIX + ":"
						+ ELM_UNAME);

		return root;
	}

	/**
	 * decode a DOM element tree to initialize the instance attributes. The
	 * <code>aElement</code> argument represents the root DOM element and is
	 * used to traverse the DOM nodes for instance attribute values.
	 * 
	 * @param aElement
	 *            <code>Element</code> to decode
	 * 
	 * @exception EPPDecodeException
	 *                Error decoding <code>Element</code>
	 */
	public void decode(Element aElement) throws EPPDecodeException {

		// Table
		this.table = EPPUtil.decodeString(aElement, EPPIdnMapExtFactory.NS,
				ELM_TABLE);

		// Uname
		this.uname = EPPUtil.decodeString(aElement, EPPIdnMapExtFactory.NS,
				ELM_UNAME);
	}

	/**
	 * clone an <code>EPPCodecComponent</code>.
	 * 
	 * @return clone of concrete <code>EPPIdnMap</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPIdnMap clone = (EPPIdnMap) super.clone();

		return clone;
	}

	/**
	 * Implementation of <code>Object.toString</code>, which will result in an
	 * indented XML <code>String</code> representation of the concrete
	 * <code>EPPCodecComponent</code>.
	 * 
	 * @return Indented XML <code>String</code> if successful;
	 *         <code>ERROR</code> otherwise.
	 */
	public String toString() {
		return EPPUtil.toString(this);
	}

	/**
	 * Compare an instance of <code>EPPIdnMap</code> with this instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return <code>true</code> if equal; <code>false</code> otherwise.
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPIdnMap)) {
			return false;
		}

		EPPIdnMap other = (EPPIdnMap) aObject;

		// Table
		if (!EqualityUtil.equals(this.table, other.table)) {
			cat.error("EPPIdnMap.equals(): table not equal");
			return false;
		}

		// Uname
		if (!EqualityUtil.equals(this.uname, other.uname)) {
			cat.error("EPPIdnMap.equals(): uname not equal");
			return false;
		}

		return true;
	}

}
