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

import java.util.Date;

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
 * <code>EPPIdnTableInfoTable</code> is used to represent the Internationalized
 * Domain Name (IDN) table information for an IDN Table in a Table Info Form
 * response.
 */
public class EPPIdnTableInfoTable implements EPPCodecComponent {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPIdnTableInfoTable.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * XML local name for <code>EPPIdnTableInfoTable</code>.
	 */
	public static final String ELM_LOCALNAME = "table";

	/**
	 * XML root tag for <code>EPPIdnTableInfoTable</code>.
	 */
	public static final String ELM_NAME = EPPIdnTableMapFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * XML local name for the name element
	 */
	private static final String ELM_TABLE_NAME = "name";

	/**
	 * XML local name for the type element
	 */
	private static final String ELM_TYPE = "type";

	/**
	 * XML local name for the description element
	 */
	private static final String ELM_DESCRIPTION = "description";

	/**
	 * XML local name for the upDate element
	 */
	private static final String ELM_UPDATE_DATE = "upDate";

	/**
	 * XML local name for the version element
	 */
	private static final String ELM_VERSION = "version";

	/**
	 * XML local name for the effectiveDate element
	 */
	private static final String ELM_EFFECTIVE_DATE = "effectiveDate";

	/**
	 * XML local name for the variantGen element
	 */
	private static final String ELM_VARIANT_GEN = "variantGen";

	/**
	 * XML local name for the url element
	 */
	private static final String ELM_URL = "url";

	/**
	 * XML local name for the lang attribute
	 */
	private static final String ATTR_LANG = "lang";

	/**
	 * Table types, that include:<br>
	 * <ul>
	 * <li><code>LANGUAGE</code> that represents a Language IDN Table.</li>
	 * <li><code>SCRIPT</code> that represents a Script IDN Table.</li>
	 * </ul>
	 */
	public enum Type {
		LANGUAGE, SCRIPT
	}

	/**
	 * Server defined IDN Table Identifier
	 */
	private String name;

	/**
	 * The type of the IDN table.
	 */
	private Type type;

	/**
	 * Server defined description of the IDN Table.
	 */
	private String description;

	/**
	 * OPTIONAL language of the description with default of &quot;en&quot;.
	 */
	private String descriptionLang = "en";

	/**
	 * Contains the date and time that the IDN Table was created or last
	 * updated.
	 */
	private Date upDate;

	/**
	 * OPTIONAL server defined version number of the IDN Table.
	 */
	private String version;

	/**
	 * OPTIONAL effective date for the IDN Table.
	 */
	private Date effectiveDate;

	/**
	 * OPTIONAL boolean flag indicating that domains created using the IDN Table
	 * will have IDN variants generated.
	 */
	private Boolean variantGen;

	/**
	 * OPTIONAL URL for downloading the IDN table with the applicable set of
	 * code points and rules.
	 */
	private String url;

	/**
	 * Default constructor for <code>EPPIdnTableInfoTable</code>.
	 */
	public EPPIdnTableInfoTable() {
	}

	/**
	 * Constructor for <code>EPPIdnTableInfoTable</code> that takes the required
	 * attributes.
	 * 
	 * @param aName
	 *            Table identifier
	 * @param aType
	 *            IDN Table type
	 * @param aDescription
	 *            Server defined description of the IDN Table.
	 * @param aUpdateDate
	 *            Date and time the IDN Table was created or last updated.
	 */
	public EPPIdnTableInfoTable(String aName, Type aType, String aDescription,
			Date aUpdateDate) {
		this.setName(aName);
		this.setType(aType);
		this.setDescription(aDescription);
		this.setUpdateDate(aUpdateDate);
	}

	/**
	 * Gets the IDN Table identifier.
	 * 
	 * @return The IDN Table identifier
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the IDN Table identifier.
	 * 
	 * @param aName
	 *            The IDN Table identifier.
	 */
	public void setName(String aName) {
		this.name = aName;
	}

	/**
	 * Gets the IDN Table type
	 * 
	 * @return Type of the IDN table
	 */
	public Type getType() {
		return this.type;
	}

	/**
	 * Sets the IDN Table type.
	 * 
	 * @param aType
	 *            IDN type
	 */
	public void setType(Type aType) {
		this.type = aType;
	}

	/**
	 * Gets the server defined description of the IDN Table.
	 * 
	 * @return The server defined description of the IDN Table.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Sets the server defined description of the IDN Table.
	 * 
	 * @param aDescription
	 *            The server defined description of the IDN Table.
	 */
	public void setDescription(String aDescription) {
		this.description = aDescription;
	}

	/**
	 * Is the description language defined?
	 * 
	 * @return <code>true</code> if the description language is defined;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasDescriptionLang() {
		return (this.descriptionLang != null ? true : false);
	}

	/**
	 * Gets the language of the description of the IDN Table with the default
	 * value of &quot;en&quot;.
	 * 
	 * @return The language of the description.
	 */
	public String getDescriptionLang() {
		return this.descriptionLang;
	}

	/**
	 * Sets the language of the description of the IDN Table.
	 * 
	 * @param aDescriptionLang
	 *            The language of the description.
	 */
	public void setDescriptionLang(String aDescriptionLang) {
		this.descriptionLang = aDescriptionLang;
	}

	/**
	 * Gets the date and time that the IDN Table was created or last updated.
	 * 
	 * @return Date and time the IDN Table was created or last updated.
	 */
	public Date getUpdateDate() {
		return this.upDate;
	}

	/**
	 * Sets the date and time that the IDN Table was created or last updated.
	 * 
	 * @param aUpdateDate
	 *            Date and time the IDN Table was created or last updated.
	 */
	public void setUpdateDate(Date aUpdateDate) {
		this.upDate = aUpdateDate;
	}

	/**
	 * Is the version defined?
	 * 
	 * @return <code>true</code> if the version is defined; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasVersion() {
		return (this.version != null ? true : false);
	}

	/**
	 * Gets the OPTIONAL server defined version number of the IDN Table.
	 * 
	 * @return Server defined version of of the IDN Table if defined;
	 *         <code>null</code> otherwise.
	 */
	public String getVersion() {
		return this.version;
	}

	/**
	 * Sets the OPTIONAL server defined version number of the IDN Table.
	 * 
	 * @param aVersion
	 *            Server defined version of of the IDN Table. Set to
	 *            <code>null</code> to clear the attribute.
	 */
	public void setVersion(String aVersion) {
		this.version = aVersion;
	}

	/**
	 * Is the effective date defined?
	 * 
	 * @return <code>true</code> if the effective date is defined;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasEffectiveDate() {
		return (this.effectiveDate != null ? true : false);
	}

	/**
	 * Gets the OPTIONAL effective date of the IDN Table.
	 * 
	 * @return The effective date of the IDN Table if defined; <code>null</code>
	 *         otherwise.
	 */
	public Date getEffectiveDate() {
		return this.effectiveDate;
	}

	/**
	 * Sets the OPTIONAL effective date of the IDN Table.
	 * 
	 * @param aEffectiveDate
	 *            The effective date of the IDN Table. Set to <code>null</code>
	 *            to clear the attribute.
	 */
	public void setEffectiveDate(Date aEffectiveDate) {
		this.effectiveDate = aEffectiveDate;
		
		// Normalize the effective date by clearing anything other than the
		// month, day, and year.
		if (this.effectiveDate != null) {
			this.effectiveDate = EPPUtil.decodeDate(EPPUtil
					.encodeDate(this.effectiveDate));
		}
	}

	/**
	 * Is the variant generation flag defined?
	 * 
	 * @return <code>true</code> if the variant generation flag is defined;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasVariantGen() {
		return (this.variantGen != null ? true : false);
	}

	/**
	 * Gets the OPTIONAL boolean flag indicating that domains created using the
	 * IDN Table will have IDN variants generated.
	 * 
	 * @return <code>true</code> or <code>false</code> indicating that the
	 *         domains created using the IDN Table will have IDN variants
	 *         generated if defined; <code>null</code> otherwise.
	 */
	public Boolean getVariantGen() {
		return this.variantGen;
	}

	/**
	 * Sets the OPTIONAL boolean flag indicating that domains created using the
	 * IDN Table will have IDN variants generated.
	 * 
	 * @param aVariantGen
	 *            Non-<code>null</code> value indicating that the domains
	 *            created using the IDN Table will have IDN variants generated.
	 *            Use <code>null</code> to clear the flag.
	 */
	public void setVariantGen(Boolean aVariantGen) {
		this.variantGen = aVariantGen;
	}

	/**
	 * Is the URL defined?
	 * 
	 * @return <code>true</code> if the URL is defined; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasUrl() {
		return (this.url != null ? true : false);
	}

	/**
	 * Gets the OPTIONAL URL for downloading the IDN Table with the applicable
	 * set of code points and rules.
	 * 
	 * @return The URL for downloading the IDN Table with the applicable set of
	 *         code points and rules if defined; <code>null</code> otherwise.
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * Sets the OPTIONAL URL for downloading the IDN Table with the applicable
	 * set of code points and rules.
	 * 
	 * @param aUrl
	 *            The URL for downloading the IDN Table with the applicable set
	 *            of code points and rules. Set to <code>null</code> to clear
	 *            the attribute.
	 */
	public void setUrl(String aUrl) {
		this.url = aUrl;
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
	 *                Error encoding <code>EPPIdnTableInfoTable</code>
	 */
	public Element encode(Document aDocument) throws EPPEncodeException {

		// Check required attributes
		if (this.name == null) {
			throw new EPPEncodeException(
					"Undefined name in EPPIdnTableInfoTable");
		}
		if (this.type == null) {
			throw new EPPEncodeException(
					"Undefined type in EPPIdnTableInfoTable");
		}
		if (this.description == null) {
			throw new EPPEncodeException(
					"Undefined description in EPPIdnTableInfoTable");
		}
		if (this.upDate == null) {
			throw new EPPEncodeException(
					"Undefined upDate in EPPIdnTableInfoTable");
		}

		// Table
		Element root = aDocument.createElementNS(EPPIdnTableMapFactory.NS,
				ELM_NAME);

		// Name
		EPPUtil.encodeString(aDocument, root, this.name,
				EPPIdnTableMapFactory.NS, EPPIdnTableMapFactory.NS_PREFIX + ":"
						+ ELM_TABLE_NAME);

		// Type
		switch (this.type) {
			case LANGUAGE:
				EPPUtil.encodeString(aDocument, root, "language",
						EPPIdnTableMapFactory.NS,
						EPPIdnTableMapFactory.NS_PREFIX + ":" + ELM_TYPE);
				break;
			case SCRIPT:
				EPPUtil.encodeString(aDocument, root, "script",
						EPPIdnTableMapFactory.NS,
						EPPIdnTableMapFactory.NS_PREFIX + ":" + ELM_TYPE);
		}

		// Description and DescriptionLang
		Element descriptionElm = aDocument.createElementNS(
				EPPIdnTableMapFactory.NS, EPPIdnTableMapFactory.NS_PREFIX + ":"
						+ ELM_DESCRIPTION);

		if (this.hasDescriptionLang()) {
			descriptionElm.setAttribute(ATTR_LANG, this.descriptionLang);
		}

		descriptionElm.appendChild(aDocument.createTextNode(this.description));

		root.appendChild(descriptionElm);

		// Update Date
		EPPUtil.encodeTimeInstant(aDocument, root, this.upDate,
				EPPIdnTableMapFactory.NS, EPPIdnTableMapFactory.NS_PREFIX + ":"
						+ ELM_UPDATE_DATE);

		// Version
		EPPUtil.encodeString(aDocument, root, this.version,
				EPPIdnTableMapFactory.NS, EPPIdnTableMapFactory.NS_PREFIX + ":"
						+ ELM_VERSION);

		// Effective Date
		EPPUtil.encodeDate(aDocument, root, this.effectiveDate,
				EPPIdnTableMapFactory.NS, EPPIdnTableMapFactory.NS_PREFIX + ":"
						+ ELM_EFFECTIVE_DATE);

		// VariantGen
		EPPUtil.encodeBoolean(aDocument, root, this.variantGen,
				EPPIdnTableMapFactory.NS, EPPIdnTableMapFactory.NS_PREFIX + ":"
						+ ELM_VARIANT_GEN);

		// URL
		EPPUtil.encodeString(aDocument, root, this.url,
				EPPIdnTableMapFactory.NS, EPPIdnTableMapFactory.NS_PREFIX + ":"
						+ ELM_URL);

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
		this.name = EPPUtil.decodeString(aElement, EPPIdnTableMapFactory.NS,
				ELM_TABLE_NAME);

		// Type
		String theType = EPPUtil.decodeString(aElement,
				EPPIdnTableMapFactory.NS, ELM_TYPE);
		if (theType == null) {
			this.type = null;
		}
		else if (theType.equals("language")) {
			this.type = Type.LANGUAGE;
		}
		else {
			this.type = Type.SCRIPT;
		}

		// Description and DecriptionLang
		Element theDescriptionElm = EPPUtil.getElementByTagNameNS(aElement,
				EPPIdnTableMapFactory.NS, ELM_DESCRIPTION);

		if (theDescriptionElm != null) {
			Node textNode = theDescriptionElm.getFirstChild();
			if (textNode != null) {
				this.description = textNode.getNodeValue();

				String theDescriptionLang = theDescriptionElm
						.getAttribute(ATTR_LANG);
				if (theDescriptionLang != null && !theDescriptionLang.isEmpty()) {
					this.descriptionLang = theDescriptionLang;
				}
				else {
					this.descriptionLang = null;
				}

			}
			else {
				this.description = null;
				this.descriptionLang = null;
			}
		}
		else {
			this.description = null;
			this.descriptionLang = null;
		}

		// Update Date
		this.upDate = EPPUtil.decodeTimeInstant(aElement,
				EPPIdnTableMapFactory.NS, ELM_UPDATE_DATE);

		// Version
		this.version = EPPUtil.decodeString(aElement, EPPIdnTableMapFactory.NS,
				ELM_VERSION);

		// Effective Date
		this.effectiveDate = EPPUtil.decodeDate(aElement,
				EPPIdnTableMapFactory.NS, ELM_EFFECTIVE_DATE);

		// VariantGen
		this.variantGen = EPPUtil.decodeBoolean(aElement,
				EPPIdnTableMapFactory.NS, ELM_VARIANT_GEN);

		// URL
		this.url = EPPUtil.decodeString(aElement, EPPIdnTableMapFactory.NS,
				ELM_URL);
	}

	/**
	 * clone an <code>EPPCodecComponent</code>.
	 * 
	 * @return clone of concrete <code>EPPIdnTableInfoTable</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPIdnTableInfoTable clone = (EPPIdnTableInfoTable) super.clone();

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
	 * Compare an instance of <code>EPPIdnTableInfoTable</code> with this
	 * instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return <code>true</code> if equal; <code>false</code> otherwise.
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPIdnTableInfoTable)) {
			return false;
		}

		EPPIdnTableInfoTable other = (EPPIdnTableInfoTable) aObject;

		// Name
		if (!EqualityUtil.equals(this.name, other.name)) {
			cat.error("EPPIdnTableInfoTable.equals(): name not equal");
			return false;
		}

		// Type
		if (!EqualityUtil.equals(this.type, other.type)) {
			cat.error("EPPIdnTableInfoTable.equals(): type not equal");
			return false;
		}

		// Description
		if (!EqualityUtil.equals(this.description, other.description)) {
			cat.error("EPPIdnTableInfoTable.equals(): description not equal");
			return false;
		}

		// DescriptionLang
		if (!EqualityUtil.equals(this.descriptionLang, other.descriptionLang)) {
			cat.error("EPPIdnTableInfoTable.equals(): descriptionLang not equal");
			return false;
		}

		// Update Date
		if (!EqualityUtil.equals(this.upDate, other.upDate)) {
			cat.error("EPPIdnTableInfoTable.equals(): upDate not equal");
			return false;
		}

		// Version
		if (!EqualityUtil.equals(this.version, other.version)) {
			cat.error("EPPIdnTableInfoTable.equals(): version not equal");
			return false;
		}

		// Effective Date
		if (!EqualityUtil.equals(this.effectiveDate, other.effectiveDate)) {
			cat.error("EPPIdnTableInfoTable.equals(): effectiveDate not equal");
			return false;
		}

		// VariantGen
		if (!EqualityUtil.equals(this.variantGen, other.variantGen)) {
			cat.error("EPPIdnTableInfoTable.equals(): variantGen not equal");
			return false;
		}

		// URL
		if (!EqualityUtil.equals(this.url, other.url)) {
			cat.error("EPPIdnTableInfoTable.equals(): url not equal");
			return false;
		}

		return true;
	}

}
