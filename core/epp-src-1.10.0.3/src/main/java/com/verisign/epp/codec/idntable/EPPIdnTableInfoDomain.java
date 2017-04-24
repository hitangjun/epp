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

import java.util.ArrayList;
import java.util.List;

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
 * <code>EPPIdnTableInfoDomain</code> is used to represent the Internationalized
 * Domain Name (IDN) information for a domain in a IDN table response to a
 * &lt;info&gt; command. The domain information includes whether the domain is a
 * valid IDN domain name, whether or not the use of the IDN mapping extension is
 * needed, and the matching list of valid IDN table identifiers.
 */
public class EPPIdnTableInfoDomain implements EPPCodecComponent {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPIdnTableInfoDomain.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * XML local name for <code>EPPIdnTableInfoDomain</code>.
	 */
	public static final String ELM_LOCALNAME = "domain";

	/**
	 * XML root tag for <code>EPPIdnTableInfoDomain</code>.
	 */
	public static final String ELM_NAME = EPPIdnTableMapFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * XML local name for the name element
	 */
	private static final String ELM_DOMAIN_NAME = "name";

	/**
	 * XML local name for the aname element
	 */
	private static final String ELM_ANAME = "aname";

	/**
	 * XML local name for the uname element
	 */
	private static final String ELM_UNAME = "uname";

	/**
	 * XML local name for the valid attribute
	 */
	private static final String ATTR_VALID = "valid";

	/**
	 * XML local name for the idnmap attribute
	 */
	private static final String ATTR_IDNMAP = "idnmap";

	/**
	 * Value for the domain name
	 */
	private String name;

	/**
	 * Value for the domain name A-label (aname).
	 */
	private String aname;

	/**
	 * Value for the domain name U-label (uname).
	 */
	private String uname;

	/**
	 * Is the <code>name</code> attribute a valid IDN domain name?
	 */
	private boolean valid;

	/**
	 * OPTIONAL indication that the server requires the use of the IDN mapping
	 * extension with a domain create of the domain name. The default value is
	 * set to <code>true</code>.
	 */
	private boolean idnmap = true;

	/**
	 * List of matching IDN tables
	 */
	private List<EPPIdnTableInfoDomainTable> tables = new ArrayList<EPPIdnTableInfoDomainTable>();

	/**
	 * Default constructor for <code>EPPIdnTableInfoDomain</code>.
	 */
	public EPPIdnTableInfoDomain() {
	}

	/**
	 * Constructor for <code>EPPIdnTableInfoDomain</code> that takes the domain
	 * name and whether the domain name is a valid IDN domain name.
	 * 
	 * @param aName
	 *            Domain name
	 * @param aValid
	 *            Is the domain name a valid IDN domain name?
	 */
	public EPPIdnTableInfoDomain(String aName, boolean aValid) {
		this.setName(aName);
		this.setValid(aValid);
	}

	/**
	 * Constructor for <code>EPPIdnTableInfoDomain</code> that takes the domain
	 * name, whether the domain name is a valid IDN domain name, and whether the
	 * server requires the IDN mapping extension with a domain create of the
	 * domain name.
	 * 
	 * @param aName
	 *            Domain name
	 * @param aValid
	 *            Is the domain name a valid IDN domain name?
	 * @param aIdnmap
	 *            <cod>true</code> if the IDN mapping extension is required on a
	 *            domain create of the domain name; <code>false</code>
	 *            otherwise.
	 */
	public EPPIdnTableInfoDomain(String aName, boolean aValid, boolean aIdnmap) {
		this.setName(aName);
		this.setValid(aValid);
		this.setIdnmap(aIdnmap);
	}

	/**
	 * Constructor for <code>EPPIdnTableInfoDomain</code> that is used for valid
	 * IDN domain names by providing the list of IDN table identifiers.
	 * 
	 * @param aName
	 *            Domain name
	 * @param aTables
	 *            List of matching IDN tables.
	 */
	public EPPIdnTableInfoDomain(String aName,
			List<EPPIdnTableInfoDomainTable> aTables) {
		this.setName(aName);
		this.setValid(true);
		this.setTables(aTables);
	}

	/**
	 * Returns the domain name.
	 * 
	 * @return Domain name if defined: <code>null</code> otherwise.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the domain name.
	 * 
	 * @param aDomain
	 *            Domain name
	 */
	public void setName(String aDomain) {
		this.name = aDomain;
	}

	/**
	 * Is the domain name a valid IDN domain name?
	 * 
	 * @return <code>true</code> if it is a valid IDN domain name;
	 *         <code>false</code> otherwise.
	 */
	public boolean isValid() {
		return this.valid;
	}

	/**
	 * Sets whether the domain name is a valid IDN domain name.
	 * 
	 * @param aValid
	 *            <code>true</code> if the domain name is a valid IDN domain
	 *            name; <code>false</code> otherwise.
	 */
	public void setValid(boolean aValid) {
		this.valid = aValid;
	}

	/**
	 * Is the IDN mapping extension required on a domain create of the domain
	 * name?
	 * 
	 * @return <code>true</code> if the IDN mapping extension is required on a
	 *         domain create of the domain name; <code>false</code> otherwise.
	 */
	public boolean isIdnmap() {
		return this.idnmap;
	}

	/**
	 * Set whether the IDN mapping extension is required on a domain create of
	 * the domain name.
	 * 
	 * @param aIdnmap
	 *            <cod>true</code> if the IDN mapping extension is required on a
	 *            domain create of the domain name; <code>false</code>
	 *            otherwise.
	 */
	public void setIdnmap(boolean aIdnmap) {
		this.idnmap = aIdnmap;
	}

	/**
	 * Is the A-label domain name defined?
	 * 
	 * @return <code>true</code> if the A-label domain name is defined;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasAname() {
		return (this.aname != null ? true : false);
	}

	/**
	 * Returns the OPTIONAL A-label domain name.
	 * 
	 * @return A-label domain name if defined: <code>null</code> otherwise.
	 */
	public String getAname() {
		return this.aname;
	}

	/**
	 * Sets the A-label domain name.
	 * 
	 * @param aDomain
	 *            A-label Domain name
	 */
	public void setAname(String aDomain) {
		this.aname = aDomain;
	}

	/**
	 * Is the U-label domain name defined?
	 * 
	 * @return <code>true</code> if the U-label domain name is defined;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasUname() {
		return (this.uname != null ? true : false);
	}

	/**
	 * Returns the OPTIONAL U-label domain name.
	 * 
	 * @return U-label domain name if defined: <code>null</code> otherwise.
	 */
	public String getUname() {
		return this.uname;
	}

	/**
	 * Sets the U-label domain name.
	 * 
	 * @param aDomain
	 *            U-label Domain name
	 */
	public void setUname(String aDomain) {
		this.uname = aDomain;
	}

	/**
	 * Are any table identifiers defined in the list of table identifiers?
	 * 
	 * @return <code>true</code> if there is at least one table identifier
	 *         defined; <code>false</code> otherwise.
	 */
	public boolean hasTables() {
		if (this.tables != null && !this.tables.isEmpty()) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Get the list of tables.
	 * 
	 * @return {@code List} of tables
	 */
	public List<EPPIdnTableInfoDomainTable> getTables() {
		return this.tables;
	}

	/**
	 * Set the list of tables.
	 * 
	 * @param aTables
	 *            {@code List} of tables
	 */
	public void setTables(List<EPPIdnTableInfoDomainTable> aTables) {
		this.tables = aTables;
	}

	/**
	 * Set an individual table. This method clears the existing list of tables.
	 * 
	 * @param aTable
	 *            Table to set
	 */
	public void setTable(EPPIdnTableInfoDomainTable aTable) {
		this.tables = new ArrayList<EPPIdnTableInfoDomainTable>();
		this.tables.add(aTable);
	}

	/**
	 * Append a table to the list of tables. This method does NOT clear the
	 * existing list of tables.
	 * 
	 * @param aTable
	 *            Table to append
	 */
	public void addTable(EPPIdnTableInfoDomainTable aTable) {
		this.tables.add(aTable);
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
	 *                Error encoding <code>EPPIdnTableInfoDomain</code>
	 */
	public Element encode(Document aDocument) throws EPPEncodeException {

		// Check required attributes
		if (this.name == null) {
			throw new EPPEncodeException(
					"Undefined name in EPPIdnTableInfoDomain");
		}

		// Check invalid combinations
		if (this.hasAname() && this.hasUname()) {
			throw new EPPEncodeException(
					"Both aname and uname can be set in EPPIdnTableInfoDomain");
		}

		// Domain
		Element root = aDocument.createElementNS(EPPIdnTableMapFactory.NS,
				ELM_NAME);

		// Name
		Element theElm = aDocument.createElementNS(EPPIdnTableMapFactory.NS,
				EPPIdnTableMapFactory.NS_PREFIX + ":" + ELM_DOMAIN_NAME);

		theElm.appendChild(aDocument.createTextNode(this.name));

		// Valid
		EPPUtil.encodeBooleanAttr(theElm, ATTR_VALID, this.valid);

		// Idnmap
		EPPUtil.encodeBooleanAttr(theElm, ATTR_IDNMAP, this.idnmap);

		root.appendChild(theElm);

		// Aname
		EPPUtil.encodeString(aDocument, root, this.aname,
				EPPIdnTableMapFactory.NS, EPPIdnTableMapFactory.NS_PREFIX + ":"
						+ ELM_ANAME);

		// Uname
		EPPUtil.encodeString(aDocument, root, this.uname,
				EPPIdnTableMapFactory.NS, EPPIdnTableMapFactory.NS_PREFIX + ":"
						+ ELM_UNAME);

		// Tables
		EPPUtil.encodeCompList(aDocument, root, this.tables);

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

		// Name
		Element theNameElm = EPPUtil.getElementByTagNameNS(aElement,
				EPPIdnTableMapFactory.NS, ELM_DOMAIN_NAME);
		if (theNameElm != null) {
			Node textNode = theNameElm.getFirstChild();
			if (textNode != null) {
				this.name = textNode.getNodeValue();
			}
			else {
				this.name = null;
			}

			// Valid
			this.valid = EPPUtil.decodeBooleanAttr(theNameElm, ATTR_VALID);

			// Idnmap
			try {
				this.idnmap = EPPUtil
						.decodeBooleanAttr(theNameElm, ATTR_IDNMAP);
			}
			catch (EPPDecodeException ex) {
				// Attribute is undefined, so setting to default of true.
				cat.debug("EPPIdnTableInfoDomain.decode(): idnmap attribute undefined, setting to default of true.");
				this.idnmap = true;
			}

		}

		// Aname
		this.aname = EPPUtil.decodeString(aElement, EPPIdnTableMapFactory.NS,
				ELM_ANAME);

		// Uname
		this.uname = EPPUtil.decodeString(aElement, EPPIdnTableMapFactory.NS,
				ELM_UNAME);

		// Tables
		this.tables = EPPUtil.decodeCompList(aElement,
				EPPIdnTableMapFactory.NS, EPPIdnTableInfoDomainTable.ELM_NAME,
				EPPIdnTableInfoDomainTable.class);
	}

	/**
	 * clone an <code>EPPCodecComponent</code>.
	 * 
	 * @return clone of concrete <code>EPPIdnTableInfoDomain</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPIdnTableInfoDomain clone = (EPPIdnTableInfoDomain) super.clone();

		clone.tables = (List) ((ArrayList) tables).clone();

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
	 * Compare an instance of <code>EPPIdnTableInfoDomain</code> with this
	 * instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return <code>true</code> if equal; <code>false</code> otherwise.
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPIdnTableInfoDomain)) {
			return false;
		}

		EPPIdnTableInfoDomain other = (EPPIdnTableInfoDomain) aObject;

		// Name
		if (!EqualityUtil.equals(this.name, other.name)) {
			cat.error("EPPIdnTableInfoDomain.equals(): name not equal");
			return false;
		}

		// Valid
		if (!EqualityUtil.equals(this.valid, other.valid)) {
			cat.error("EPPIdnTableInfoDomain.equals(): valid not equal");
			return false;
		}

		// Idnmap
		if (!EqualityUtil.equals(this.idnmap, other.idnmap)) {
			cat.error("EPPIdnTableInfoDomain.equals(): idnmap not equal");
			return false;
		}

		// Aname
		if (!EqualityUtil.equals(this.aname, other.aname)) {
			cat.error("EPPIdnTableInfoDomain.equals(): aname not equal");
			return false;
		}

		// Uname
		if (!EqualityUtil.equals(this.uname, other.uname)) {
			cat.error("EPPIdnTableInfoDomain.equals(): uname not equal");
			return false;
		}

		// Tables
		if (!EPPUtil.equalLists(tables, other.tables)) {
			cat.error("EPPIdnTableInfoDomain.equals(): tables not equal");
			return false;
		}

		return true;
	}

}
