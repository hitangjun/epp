/***********************************************************
Copyright (C) 2004 VeriSign, Inc.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-0107  USA

http://www.verisign.com/nds/naming/namestore/techdocs.html
 ***********************************************************/
package com.verisign.epp.codec.reseller;

import java.security.InvalidParameterException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.verisign.epp.codec.gen.EPPCodecComponent;
import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EqualityUtil;

/**
 * Represents a reseller postal address definition. The child elements
 * associated with an <code>EPPResellerPostalDefinition</code> include: <br>
 * 
 * <ul>
 * <li>
 * A localization type, represented by the {@link Type} enumeration. Use
 * {@link #getType()} and {@link #setType(Type)} to get and set the attribute.</li>
 * <li>
 * <li>
 * A name that contains the name of the reseller. Use {@link #getName()} and
 * {@link #setName(String)} to get and set the attribute.</li>
 * <li>
 * An address that contains address information associated with the reseller.
 * Use {@link #getAddress()} and {@link #setAddress(EPPResellerAddress)} to get
 * and set the attribute.</li>
 * </ul>
 */
public class EPPResellerPostalDefinition implements EPPCodecComponent {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(
			EPPResellerPostalDefinition.class.getName(), EPPCatFactory
					.getInstance().getFactory());

	/**
	 * Localized type with one of the following values:<br>
	 * <ul>
	 * <li><code>LOC</code> - Localized form of data that MAY be represented in
	 * unrestricted UTF-8.</li>
	 * <li><code>INT</code> - Internationalized form o data that MUST be
	 * represented in a subset of UTF-8 that can be represented in the 7-bit
	 * US-ASCII character set.</li>
	 */
	public enum Type implements java.io.Serializable, Cloneable {
		LOC("loc"), INT("int");

		private final String typeStr;

		/**
		 * Define the string value for the enumerated value.
		 * 
		 * @param aTypeStr
		 *            Enumerated value string
		 */
		Type(String aTypeStr) {
			this.typeStr = aTypeStr;
		}

		/**
		 * Get the type enumerated value given the matching string.
		 * 
		 * @param aType
		 *            <code>Type</code> enumerated string to convert to an
		 *            enumerated <code>Type</code> instance.
		 * 
		 * @return Enumerated <code>Type</code> value matching the
		 *         <code>String</code>.
		 * 
		 * @throws InvalidParameterException
		 *             If <code>aString</code> does not match an enumerated
		 *             <code>Type</code> string value.
		 */
		public static Type getType(String aType) {
			if (aType.equals(LOC.typeStr)) {
				return LOC;
			}
			else if (aType.equals(INT.typeStr)) {
				return INT;
			}
			else {
				throw new InvalidParameterException("Type enum value of "
						+ aType + " is not valid.");
			}
		}

		/**
		 * Convert the enumerated <code>Type</code> value to a
		 * <code>String</code>.
		 */
		public String toString() {
			return this.typeStr;
		}

	}

	/**
	 * XML local name for <code>EPPResellerDisclose</code>.
	 */
	public static final String ELM_LOCALNAME = "postalInfo";

	/**
	 * XML root tag for <code>EPPResellerPostalDefinition</code>.
	 */
	public static final String ELM_NAME = EPPResellerMapFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * XML tag name for the <code>org</code> attribute.
	 */
	private final static String ELM_CONTACT_NAME = "name";

	/**
	 * XML Attribute Name for postalInfo type.
	 */
	private final static String ATTR_TYPE = "type";

	/**
	 * Localization form
	 */
	private Type type = Type.INT;

	/**
	 * contact name
	 */
	private String name = null;

	/**
	 * contact address
	 */
	private EPPResellerAddress address = null;


	/**
	 * <code>EPPResellerPostalDefinition</code> default constructor.
	 */
	public EPPResellerPostalDefinition() {
	}

	/**
	 * <code>EPPResellerPostalDefinition</code> constructor that takes the
	 * contact postal type as an argument.
	 *
	 * @param aType
	 *            Postal type
	 */
	public EPPResellerPostalDefinition(Type aType) {
		this.type = aType;
	}

	/**
	 * <code>EPPResellerPostalDefinition</code> constructor that sets the
	 * required attributes with the parameters.
	 *
	 * @param aName
	 *            contact name
	 * @param aType
	 *            Postal type
	 * @param aAddress
	 *            contact address
	 */
	public EPPResellerPostalDefinition(Type aType, String aName,
			EPPResellerAddress aAddress) {
		this.name = aName;
		this.type = aType;
		this.address = aAddress;
	}

	/**
	 * Gets the contact postal type.
	 *
	 * @return Contact postal type
	 */
	public Type getType() {
		return this.type;
	}

	/**
	 * Sets the contact type.
	 *
	 * @param aType
	 *            Contact postal type
	 */
	public void setType(Type aType) {
		this.type = aType;
	}

	/**
	 * Gets the contact name
	 *
	 * @return Contact Name if defined; <code>null</code> otherwise.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the contact name.
	 *
	 * @param aName
	 *            Contact Name
	 */
	public void setName(String aName) {
		name = aName;
	}

	/**
	 * Gets the contact address
	 *
	 * @return Contact address if defined; <code>null</code> otherwise.
	 */
	public EPPResellerAddress getAddress() {
		return this.address;
	}

	/**
	 * Sets the contact address
	 *
	 * @param aAddress
	 *            Contact address
	 */
	public void setAddress(EPPResellerAddress aAddress) {
		this.address = aAddress;
	}

	/**
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPResellerPostalDefinition</code> instance.
	 *
	 * @param aDocument
	 *            DOM Document that is being built. Used as an Element factory.
	 *
	 * @return Encoded DOM <code>Element</code>
	 *
	 * @exception EPPEncodeException
	 */
	public Element encode(Document aDocument) throws EPPEncodeException {
		// Check required attributes
		if (name == null) {
			throw new EPPEncodeException("name required attribute is not set");
		}

		if (type == null) {
			throw new EPPEncodeException("required attribute type is not set");
		}

		if (address == null) {
			throw new EPPEncodeException(
					"address required attribute is not set");
		}

		// Create root element
		Element root = aDocument.createElementNS(EPPResellerMapFactory.NS,
				ELM_NAME);

		// Type
		root.setAttribute(ATTR_TYPE, this.type.toString());

		// Name
		EPPUtil.encodeString(aDocument, root, this.name,
				EPPResellerMapFactory.NS, EPPResellerMapFactory.NS_PREFIX + ":"
						+ ELM_CONTACT_NAME);

		// Address
		EPPUtil.encodeComp(aDocument, root, this.address);

		return root;
	}

	/**
	 * Decode the <code>EPPResellerPostalDefinition</code> attributes from the
	 * aElement DOM Element tree.
	 *
	 * @param aElement
	 *            Root DOM Element to decode
	 *            <code>EPPResellerPostalDefinition</code> from.
	 *
	 * @exception EPPDecodeException
	 *                Unable to decode aElement
	 */
	public void decode(Element aElement) throws EPPDecodeException {
		// Type
		this.type = Type.getType(aElement.getAttribute(ATTR_TYPE));

		// Name
		this.name = EPPUtil.decodeString(aElement, EPPResellerMapFactory.NS,
				ELM_CONTACT_NAME);

		// Address
		this.address = (EPPResellerAddress) EPPUtil.decodeComp(aElement,
				EPPResellerMapFactory.NS, EPPResellerAddress.ELM_NAME,
				EPPResellerAddress.class);
	}

	/**
	 * implements a deep <code>EPPResellerPostalDefinition</code> compare.
	 *
	 * @param aObject
	 *            <code>EPPResellerPostalDefinition</code> instance to compare
	 *            with
	 *
	 * @return <code>true</code> if the object is equal to <code>aObject</code>;
	 *         <code>false</code> otherwise.
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPResellerPostalDefinition)) {
			return false;
		}

		EPPResellerPostalDefinition other = (EPPResellerPostalDefinition) aObject;

		// Type
		if (!EqualityUtil.equals(this.type, other.type)) {
			cat.error("EPPResellerPostalDefinition.equals(): type not equal");
			return false;
		}

		// Name
		if (!EqualityUtil.equals(this.name, other.name)) {
			cat.error("EPPResellerPostalDefinition.equals(): name not equal");
			return false;
		}

		// Address
		if (!EqualityUtil.equals(this.address, other.address)) {
			cat.error("EPPResellerPostalDefinition.equals(): address not equal");
			return false;
		}

		return true;
	}

	/**
	 * Clone <code>EPPResellerPostalDefinition</code>.
	 *
	 * @return clone of <code>EPPResellerPostalDefinition</code>
	 *
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPResellerPostalDefinition clone = (EPPResellerPostalDefinition) super
				.clone();

		if (address != null) {
			clone.address = (EPPResellerAddress) address.clone();
		}

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

}
