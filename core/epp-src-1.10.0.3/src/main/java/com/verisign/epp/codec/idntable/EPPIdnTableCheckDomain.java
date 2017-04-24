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
 * <code>EPPIdnTableCheckDomain</code> is used to represent the
 * Internationalized Domain Name (IDN) information for a domain in a IDN table
 * response to a &lt;check&gt; command. The domain information includes whether
 * the domain is a valid IDN domain name, whether or not the use of the IDN
 * mapping extension is needed, and the matching list of valid IDN table
 * identifiers.
 */
public class EPPIdnTableCheckDomain implements EPPCodecComponent {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPIdnTableCheckDomain.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * XML local name for <code>EPPIdnTableCheckDomain</code>.
	 */
	public static final String ELM_LOCALNAME = "domain";

	/**
	 * XML root tag for <code>EPPIdnTableCheckDomain</code>.
	 */
	public static final String ELM_NAME = EPPIdnTableMapFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * XML local name for the name element
	 */
	private static final String ELM_DOMAIN_NAME = "name";

	/**
	 * XML local name for the table element
	 */
	private static final String ELM_TABLE = "table";

	/**
	 * XML local name for the reason element
	 */
	private static final String ELM_REASON = "reason";

	/**
	 * XML local name for the valid attribute
	 */
	private static final String ATTR_VALID = "valid";

	/**
	 * XML local name for the idnmap attribute
	 */
	private static final String ATTR_IDNMAP = "idnmap";

	/**
	 * XML local name for the reason lang attribute.
	 */
	private static final String ATTR_LANG = "lang";

	/**
	 * Value for the domain name
	 */
	private String name;

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
	 * Table identifiers that are valid for the IDN domain name. This is a
	 * <code>List</code> of <code>String</code> instances.
	 */
	private List<String> tables = new ArrayList<String>();

	/**
	 * OPTIONAL reason element
	 */
	private String reason;

	/**
	 * OPTIONAL language of the reason.
	 */
	private String reasonLang;

	/**
	 * Default constructor for <code>EPPIdnTableCheckDomain</code>.
	 */
	public EPPIdnTableCheckDomain() {
	}

	/**
	 * Constructor for <code>EPPIdnTableCheckDomain</code> that takes the domain
	 * name and whether the domain name is a valid IDN domain name.
	 * 
	 * @param aName
	 *            Domain name
	 * @param aValid
	 *            Is the domain name a valid IDN domain name?
	 */
	public EPPIdnTableCheckDomain(String aName, boolean aValid) {
		this.setName(aName);
		this.setValid(aValid);
	}

	/**
	 * Constructor for <code>EPPIdnTableCheckDomain</code> that takes the domain
	 * name and whether the domain name is a valid IDN domain name, and whether
	 * the domain name requires the IDN mapping extension with a domain create
	 * of the domain name.
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
	public EPPIdnTableCheckDomain(String aName, boolean aValid, boolean aIdnmap) {
		this.setName(aName);
		this.setValid(aValid);
		this.setIdnmap(aIdnmap);
	}

	/**
	 * Constructor for <code>EPPIdnTableCheckDomain</code> that is used for
	 * invalid IDN domain names by providing the reason the domain name is not
	 * valid.
	 * 
	 * @param aDomain
	 *            Domain name
	 * @param aReason
	 *            Reason that the IDN domain name is not valid.
	 */
	public EPPIdnTableCheckDomain(String aDomain, String aReason) {
		this.setName(aDomain);
		this.setValid(false);
		this.setReason(aReason);
	}

	/**
	 * Constructor for <code>EPPIdnTableCheckDomain</code> that is used for
	 * valid IDN domain names by providing the list of IDN table identifiers.
	 * 
	 * @param aDomain
	 *            Domain name
	 * @param aTables
	 *            List of matching IDN table identifiers.
	 */
	public EPPIdnTableCheckDomain(String aDomain, List<String> aTables) {
		this.setName(aDomain);
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
	 * Is the reason defined?
	 * 
	 * @return <code>true</code> if the reason is defined; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasReason() {
		return (this.reason != null ? true : false);
	}

	/**
	 * Gets the reason that the domain name is invalid.
	 * 
	 * @return Reason that the domain name is invalid if defined;
	 *         <code>null</code> otherwise.
	 */
	public String getReason() {
		return this.reason;
	}

	/**
	 * Sets the reason that the domain name is invalid.
	 * 
	 * @param aReason
	 *            Reason that the domain is invalid. Set to <code>null</code> to
	 *            clear the reason.
	 */
	public void setReason(String aReason) {
		this.reason = aReason;
	}

	/**
	 * Is the reason language defined?
	 * 
	 * @return <code>true</code> if the reason language is defined;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasReasonLang() {
		return (this.reasonLang != null ? true : false);
	}

	/**
	 * Gets the reason language value.
	 * 
	 * @return Reason language if defined; <code>null</code> otherwise.
	 */
	public String getReasonLang() {
		return this.reasonLang;
	}

	/**
	 * Sets the reason language value.
	 * 
	 * @param aReasonLang
	 *            Reason language for reason value.
	 */
	public void setReasonLang(String aReasonLang) {
		this.reasonLang = aReasonLang;
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
	 * Get the list of table identifiers.
	 * 
	 * @return {@code List} of table identifier {@code String}'s
	 */
	public List<String> getTables() {
		return this.tables;
	}

	/**
	 * Set the list of table identifiers.
	 * 
	 * @param aTables
	 *            {@code List} of table identifier {@code String}'s
	 */
	public void setTables(List<String> aTables) {
		this.tables = aTables;
	}

	/**
	 * Set an individual table identifier. This method clears the existing list
	 * of table identifiers.
	 * 
	 * @param aTable
	 *            Table identifier
	 */
	public void setTable(String aTable) {
		this.tables = new ArrayList<String>();
		this.tables.add(aTable);
	}

	/**
	 * Append a table identifier to the list of table identifiers. This method
	 * does NOT clear the existing list of table identifiers.
	 * 
	 * @param aTable
	 *            Table identifier
	 */
	public void addTable(String aTable) {
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
	 *                Error encoding <code>EPPIdnTableCheckDomain</code>
	 */
	public Element encode(Document aDocument) throws EPPEncodeException {

		// Check required attributes
		if (this.name == null) {
			throw new EPPEncodeException(
					"Undefined name in EPPIdnTableCheckDomain");
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

		// Reason and ReasonLang
		if (this.hasReason()) {
			Element reasonElm = aDocument.createElementNS(
					EPPIdnTableMapFactory.NS, EPPIdnTableMapFactory.NS_PREFIX
							+ ":" + ELM_REASON);

			if (this.hasReasonLang()) {
				reasonElm.setAttribute(ATTR_LANG, this.reasonLang);
			}

			reasonElm.appendChild(aDocument.createTextNode(this.reason));

			root.appendChild(reasonElm);
		}

		// Tables
		EPPUtil.encodeList(aDocument, root, this.tables,
				EPPIdnTableMapFactory.NS, EPPIdnTableMapFactory.NS_PREFIX + ":"
						+ ELM_TABLE);

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
				cat.debug("EPPIdnTableCheckDomain.decode(): idnmap attribute undefined, setting to default of true.");
				this.idnmap = true;
			}

		}

		// Reason and ReasonLang
		Element theReasonElm = EPPUtil.getElementByTagNameNS(aElement,
				EPPIdnTableMapFactory.NS, ELM_REASON);

		if (theReasonElm != null) {
			Node textNode = theReasonElm.getFirstChild();
			if (textNode != null) {
				this.reason = textNode.getNodeValue();

				String theReasonLang = theReasonElm.getAttribute(ATTR_LANG);
				if (theReasonLang != null && !theReasonLang.isEmpty()) {
					this.reasonLang = theReasonLang;
				}
				else {
					this.reasonLang = null;
				}

			}
			else {
				this.reason = null;
				this.reasonLang = null;
			}
		}
		else {
			this.reason = null;
			this.reasonLang = null;
		}

		// Tables
		this.tables = EPPUtil.decodeList(aElement, EPPIdnTableMapFactory.NS,
				ELM_TABLE);
	}

	/**
	 * clone an <code>EPPCodecComponent</code>.
	 * 
	 * @return clone of concrete <code>EPPIdnTableCheckDomain</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPIdnTableCheckDomain clone = (EPPIdnTableCheckDomain) super.clone();

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
	 * Compare an instance of <code>EPPIdnTableCheckDomain</code> with this
	 * instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return <code>true</code> if equal; <code>false</code> otherwise.
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPIdnTableCheckDomain)) {
			return false;
		}

		EPPIdnTableCheckDomain other = (EPPIdnTableCheckDomain) aObject;

		// Name
		if (!EqualityUtil.equals(this.name, other.name)) {
			cat.error("EPPIdnTableCheckDomain.equals(): name not equal");
			return false;
		}

		// Valid
		if (!EqualityUtil.equals(this.valid, other.valid)) {
			cat.error("EPPIdnTableCheckDomain.equals(): valid not equal");
			return false;
		}

		// Idnmap
		if (!EqualityUtil.equals(this.idnmap, other.idnmap)) {
			cat.error("EPPIdnTableCheckDomain.equals(): idnmap not equal");
			return false;
		}

		// Reason and ReasonLang
		if (!EqualityUtil.equals(this.reason, other.reason)) {
			cat.error("EPPIdnTableCheckDomain.equals(): reason not equal");
			return false;
		}
		if (!EqualityUtil.equals(this.reasonLang, other.reasonLang)) {
			cat.error("EPPIdnTableCheckDomain.equals(): reasonLang not equal");
			return false;
		}

		// Tables
		if (!EPPUtil.equalLists(tables, other.tables)) {
			cat.error("EPPIdnTableCheckDomain.equals(): tables not equal");
			return false;
		}

		return true;
	}

}
