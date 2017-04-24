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

package com.verisign.epp.codec.idntable;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.verisign.epp.codec.gen.EPPCodecComponent;
import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EqualityUtil;

/**
 * <code>EPPIdnTableCheckTable</code> is used to represent the Internationalized
 * Domain Name (IDN) table identifier in a IDN table response to a &lt;check&lt;
 * command. The IDN table identifier information includes the identifier and a
 * boolean indicating that it exists or not.
 */
public class EPPIdnTableCheckTable implements EPPCodecComponent {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPIdnTableCheckTable.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * XML local name for <code>EPPIdnTableCheckTable</code>.
	 */
	public static final String ELM_LOCALNAME = "table";

	/**
	 * XML root tag for <code>EPPIdnTableCheckTable</code>.
	 */
	public static final String ELM_NAME = EPPIdnTableMapFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * XML local name for the exists element
	 */
	private static final String ATTR_EXISTS = "exists";

	/**
	 * Value for the table identifier
	 */
	private String table;

	/**
	 * Does the table identifier exist? The default value is <code>false</code>.
	 */
	private boolean exists = false;

	/**
	 * Default constructor for <code>EPPIdnTableCheckTable</code>.
	 */
	public EPPIdnTableCheckTable() {
	}

	/**
	 * Constructor for <code>EPPIdnTableCheckTable</code> that takes the
	 * required attributes of the table identifier and the exists flag.
	 * 
	 * @param aTable
	 *            Table identifier
	 * @param aExists
	 */
	public EPPIdnTableCheckTable(String aTable, boolean aExists) {
		this.setTable(aTable);
		this.setExists(aExists);
	}

	/**
	 * Gets the table identifier.
	 * 
	 * @return The table identifier
	 */
	public String getTable() {
		return this.table;
	}

	/**
	 * Sets the table identifier.
	 * 
	 * @param aTable
	 *            The table identifier.
	 */
	public void setTable(String aTable) {
		this.table = aTable;
	}

	/**
	 * Does the table identifier exist?
	 * 
	 * @return <code>true</code> if the table identifier exists;
	 *         <code>false</code> otherwise.
	 */
	public boolean isExists() {
		return this.exists;
	}

	/**
	 * Sets whether the table identifier exists.
	 * 
	 * @param aExists
	 *            <code>true</code> if the table identifier exists;
	 *            <code>false</code> otherwise.
	 */
	public void setExists(boolean aExists) {
		this.exists = aExists;
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
	 *                Error encoding <code>EPPIdnTableCheckTable</code>
	 */
	public Element encode(Document aDocument) throws EPPEncodeException {

		// Check required attributes
		if (this.table == null) {
			throw new EPPEncodeException(
					"Undefined table in EPPIdnTableCheckTable");
		}

		// Table
		Element root = aDocument.createElementNS(EPPIdnTableMapFactory.NS,
				ELM_NAME);

		root.appendChild(aDocument.createTextNode(this.table));

		// Exists
		EPPUtil.encodeBooleanAttr(root, ATTR_EXISTS, this.exists);

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
		Node textNode = aElement.getFirstChild();

		if (textNode != null) {
			this.table = textNode.getNodeValue();
		}
		else {
			this.table = null;
		}

		// Exists
		this.exists = EPPUtil.decodeBooleanAttr(aElement, ATTR_EXISTS);
	}

	/**
	 * clone an <code>EPPCodecComponent</code>.
	 * 
	 * @return clone of concrete <code>EPPIdnTableCheckTable</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPIdnTableCheckTable clone = (EPPIdnTableCheckTable) super.clone();

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
	 * Compare an instance of <code>EPPIdnTableCheckTable</code> with this
	 * instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return <code>true</code> if equal; <code>false</code> otherwise.
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPIdnTableCheckTable)) {
			return false;
		}

		EPPIdnTableCheckTable other = (EPPIdnTableCheckTable) aObject;

		// Table
		if (!EqualityUtil.equals(this.table, other.table)) {
			cat.error("EPPIdnTableCheckTable.equals(): table not equal");
			return false;
		}

		// Exists
		if (!EqualityUtil.equals(this.exists, other.exists)) {
			cat.error("EPPIdnTableCheckTable.equals(): exists not equal");
			return false;
		}

		return true;
	}

}
