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
 ***********************************************************/
package com.verisign.epp.codec.changepoll;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import com.verisign.epp.codec.gen.EPPCodecComponent;
import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EqualityUtil;

/**
 * Change Case Identifier that includes both the identifier as well as the case
 * type.
 */
public class EPPChangeCaseId implements EPPCodecComponent {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPChangeCaseId.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * A Uniform Domain-Name Dispute-Resolution Policy (UDRP) case.
	 */
	public static final String TYPE_UDRP = "udrp";

	/**
	 * A Uniform Rapid Suspension (URS) case.
	 */
	public static final String TYPE_URS = "urs";

	/**
	 * A custom case that is defined using the &quot;name&quot; attribute.
	 */
	public static final String TYPE_CUSTOM = "custom";

	/**
	 * Constant for the phase local name
	 */
	public static final String ELM_LOCALNAME = "caseId";

	/**
	 * Constant for the phase qualified name (prefix and local name)
	 */
	public static final String ELM_NAME = EPPChangePollExtFactory.NS_PREFIX
			+ ":" + ELM_LOCALNAME;

	/**
	 * Name of XML attribute for the &quot;type&quot; attribute.
	 */
	private static final String ATTR_TYPE = "type";

	/**
	 * Name of XML attribute for the &quot;name&quot; attribute.
	 */
	private static final String ATTR_NAME = "name";

	/**
	 * Case identifier value.
	 */
	private String identifier;

	/**
	 * Case type that should be one of the <code>TYPE</code> constants.
	 */
	private String type;

	/**
	 * Name of the type when the case type is defined as
	 * <code>TYPE_CUSTOM</code>.
	 */
	private String name;

	/**
	 * Default constructor. The identifier value MUST be set using the
	 * {@link #setValue(String)} method and the type MUST be set using the
	 * {@link #setType(String)}.
	 */
	public EPPChangeCaseId() {
	}

	/**
	 * Create <code>EPPChangeCaseId</code> instance with the required identifier
	 * and type attributes.
	 * 
	 * @param aIdentifier
	 *            Case identifier value.
	 * @param aType
	 *            Case type using one of the <code>TYPE</code> constants.
	 */
	public EPPChangeCaseId(String aIdentifier, String aType) {
		this.identifier = aIdentifier;
		this.type = aType;
	}

	/**
	 * Create <code>EPPChangeCaseId</code> instance all of the attributes
	 * including the identifier, type, and custom type name.
	 * 
	 * @param aIdentifier
	 *            Case identifier value.
	 * @param aType
	 *            Case type using one of the <code>TYPE</code> constants.
	 * @param aName
	 *            Name of the type when <code>aType</code> is set with the
	 *            <code>TYPE_CUSTOM</code> value.
	 */
	public EPPChangeCaseId(String aIdentifier, String aType, String aName) {
		this.identifier = aIdentifier;
		this.type = aType;
		this.name = aName;
	}

	/**
	 * Gets the case identifier value.
	 * 
	 * @return Case identifier value if defined; <code>null</code> otherwise.
	 */
	public String getIdentifier() {
		return this.identifier;
	}

	/**
	 * Sets the case identifier value.
	 * 
	 * @param aIdentifier
	 *            Case identifier
	 */
	public void setValue(String aIdentifier) {
		this.identifier = aIdentifier;
	}

	/**
	 * Gets the case type. The case type should use one of the <code>TYPE</code>
	 * constant values.
	 * 
	 * @return Case type if defined; <code>null</code> otherwise.
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Sets the case type. The case type should use one of the <code>TYPE</code>
	 * constant values.
	 * 
	 * @param aType
	 *            One of the <code>TYPE</code> constant values.
	 */
	public void setType(String aType) {
		this.type = aType;
	}

	/**
	 * Is the name defined?
	 * 
	 * @return <code>true</code> if the name is defined; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasName() {
		return (this.name != null ? true : false);
	}

	/**
	 * Gets the name of the case type, which is used when the type is set to the
	 * <code>TYPE_CUSTOM</code> value.
	 * 
	 * @return The type name if defined; <code>null</code> otherwise.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name of the case type, which is used when the type is set to the
	 * <code>TYPE_CUSTOM</code> value.
	 * 
	 * @param aName
	 *            The custom type name.
	 */
	public void setName(String aName) {
		this.name = aName;
	}

	/**
	 * Clone <code>EPPChangeCaseId</code> instance.
	 *
	 * @return clone of <code>EPPChangeCaseId</code>
	 *
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPChangeCaseId clone = null;

		clone = (EPPChangeCaseId) super.clone();

		return clone;
	}

	/**
	 * Decode the <code>EPPChangeCaseId</code> element aElement DOM Element
	 * tree.
	 *
	 * @param aElement
	 *            - Root DOM Element to decode <code>EPPChangeCaseId</code>
	 *            from.
	 *
	 * @exception EPPDecodeException
	 *                Unable to decode aElement
	 */
	public void decode(Element aElement) throws EPPDecodeException {
		// Type
		String theType = aElement.getAttribute(ATTR_TYPE);
		if (theType != null && !theType.isEmpty()) {
			this.type = theType;
		}
		else {
			this.type = null;
		}

		// Name
		String theName = aElement.getAttribute(ATTR_NAME);
		if (theName != null && !theName.isEmpty()) {
			this.name = theName;
		}
		else {
			this.name = null;
		}

		// Identifier
		Node textNode = aElement.getFirstChild();

		if (textNode != null) {
			this.identifier = textNode.getNodeValue();
		}
	}

	/**
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPChangeCaseId</code> instance.
	 *
	 * @param aDocument
	 *            - DOM Document that is being built. Used as an Element
	 *            factory.
	 *
	 * @return Element - Root DOM Element representing the
	 *         <code>EPPChangeCaseId</code> instance.
	 *
	 * @exception EPPEncodeException
	 *                - Unable to encode <code>EPPChangeCaseId</code> instance.
	 */
	public Element encode(Document aDocument) throws EPPEncodeException {

		if (aDocument == null) {
			throw new EPPEncodeException("aDocument is null"
					+ " in EPPChangeCaseId.encode(Document)");
		}
		if (this.identifier == null) {
			throw new EPPEncodeException("identifier is null"
					+ " in EPPChangeCaseId.encode(Document)");
		}
		if (this.type == null) {
			throw new EPPEncodeException("type is null"
					+ " in EPPChangeCaseId.encode(Document)");
		}

		Element root = aDocument.createElementNS(EPPChangePollExtFactory.NS,
				ELM_NAME);

		// Type
		if (this.type != null) {
			root.setAttribute(ATTR_TYPE, this.type);
		}

		// Name
		if (this.name != null) {
			root.setAttribute(ATTR_NAME, this.name);
		}

		// Identifier
		Text phaseText = aDocument.createTextNode(this.identifier);
		root.appendChild(phaseText);

		return root;
	}

	/**
	 * Implements a deep <code>EPPChangeCaseId</code> compare.
	 *
	 * @param aObject
	 *            <code>EPPChangeCaseId</code> instance to compare with
	 *
	 * @return <code>true</code> if equal; <code>false</code> otherwise
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPChangeCaseId)) {
			return false;
		}

		EPPChangeCaseId other = (EPPChangeCaseId) aObject;

		// Type
		if (!EqualityUtil.equals(this.type, other.type)) {
			cat.error("EPPChangeCaseId.equals(): type not equal");
			return false;
		}

		// Name
		if (!EqualityUtil.equals(this.name, other.name)) {
			cat.error("EPPChangeCaseId.equals(): name not equal");
			return false;
		}

		// Identifier
		if (!EqualityUtil.equals(this.identifier, other.identifier)) {
			cat.error("EPPChangeCaseId.equals(): identifier not equal");
			return false;
		}

		return true;
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

}
