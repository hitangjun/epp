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
package com.verisign.epp.codec.reseller;

import java.security.InvalidParameterException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.verisign.epp.codec.gen.EPPCodecComponent;
import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EqualityUtil;

/**
 * Represents a reseller contact. The valid contact types are defined by the
 * {@link Type} enumeration.
 */
public class EPPResellerContact implements EPPCodecComponent {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPResellerContact.class
			.getName(), EPPCatFactory.getInstance().getFactory());
	
	/**
	 * Contact type enumeration.
	 */
	public enum Type {
		ADMINISTRATIVE("admin"), TECHNICAL("tech"), BILLING("billing");

		/**
		 * What value is used for the type attribute?
		 */
		private final String typeVal;

		/**
		 * Define type attribute value
		 * 
		 * @param aTypeVal
		 *            Type attribute value
		 */
		Type(String aTypeVal) {
			this.typeVal = aTypeVal;
		}

		/**
		 * Get the <code>Type</code> instance given the type attribute value.
		 * 
		 * @param aTypeVal
		 *            Type attribute value.
		 * 
		 * @return Enumerated <code>Type</code> instance matching the
		 *         <code>aTypeVal</code> value.
		 * 
		 * @throws InvalidParameterException
		 *             If <code>aTypeVal</code> does not match an enumerated
		 *             <code>Type</code> string value.
		 */
		public static Type getType(String aTypeVal) {
			if (aTypeVal == null || aTypeVal.isEmpty()) {
				throw new InvalidParameterException(
						"Type value of null or empty is not valid.");
			}
			if (aTypeVal.equals(ADMINISTRATIVE.typeVal)) {
				return ADMINISTRATIVE;
			}
			else if (aTypeVal.equals(TECHNICAL.typeVal)) {
				return TECHNICAL;
			}
			else if (aTypeVal.equals(BILLING.typeVal)) {
				return BILLING;
			}
			else {
				throw new InvalidParameterException("Type enum value of "
						+ aTypeVal + " is not valid.");
			}
		}

		/**
		 * Convert the enumerated <code>Type</code> value to a type attribute
		 * <code>String</code>.
		 */
		public String toString() {
			return this.typeVal;
		}
	}

	/**
	 * XML local name for <code>EPPResellerContact</code>.
	 */
	public static final String ELM_LOCALNAME = "contact";

	/**
	 * XML root tag for <code>EPPResellerContact</code>.
	 */
	public static final String ELM_NAME = EPPResellerMapFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * XML tag name for the <code>type</code> attribute.
	 */
	private final static String ATTR_TYPE = "type";

	/**
	 * Type of contact defined by the {@link Type} enumeration.
	 */
	private Type type;

	/**
	 * Contact identifier
	 */
	private String contactId;

	/**
	 * Default constructor for <code>EPPResellerContact</code>.
	 */
	public EPPResellerContact() {
	}

	/**
	 * <code>EPPResellerContact</code> that takes all attributes as arguments
	 * (contactId and type).
	 *
	 * @param aContactId
	 *            Contact Identifier
	 * @param aType
	 *            Contact Type using {@link Type} enumeration.
	 */
	public EPPResellerContact(String aContactId, Type aType) {
		this.contactId = aContactId;
		this.type = aType;
	}

	/**
	 * Gets the contact type using the {@link Type} enumeration.
	 * 
	 * @return Contact type if defined; <code>null</code> otherwise.
	 */
	public Type getType() {
		return this.type;
	}

	/**
	 * Sets the contact type using the {@link Type} enumeration.
	 * 
	 * @param aType
	 *            Contact type
	 */
	public void setType(Type aType) {
		this.type = aType;
	}

	/**
	 * Gets the contact identifier of the contact.
	 * 
	 * @return Contact identifier if defined; <code>null</code> otherwise.
	 */
	public String getContactId() {
		return this.contactId;
	}

	/**
	 * Sets the contact identifier of the contact.
	 * 
	 * @param aContactId
	 *            Contact identifier of the contact
	 */
	public void setContactId(String aContactId) {
		this.contactId = aContactId;
	}

	/**
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPResellerContact</code> instance.
	 *
	 * @param aDocument
	 *            DOM Document that is being built. Used as an Element factory.
	 *
	 * @return Element Root DOM Element representing the
	 *         <code>EPPResellerContact</code> instance.
	 *
	 * @exception EPPEncodeException
	 *                Unable to encode <code>EPPResellerContact</code> instance.
	 */
	public Element encode(Document aDocument) throws EPPEncodeException {
		Element root = aDocument.createElementNS(EPPResellerMapFactory.NS,
				ELM_NAME);

		// Check required attributes
		if (type == null) {
			throw new EPPEncodeException(
					"EPPResellerContact type is null on call to encode");
		}
		if (contactId == null) {
			throw new EPPEncodeException(
					"EPPResellerContact contactId is null on call to encode");
		}

		// Type
		root.setAttribute(ATTR_TYPE, this.type.toString());

		// Contact Identifier
		Text textNode = aDocument.createTextNode(this.contactId);
		root.appendChild(textNode);

		return root;
	}

	/**
	 * Decode the <code>EPPResellerContact</code> attributes from the aElement
	 * DOM Element tree.
	 *
	 * @param aElement
	 *            Root DOM Element to decode <code>EPPResellerContact</code>
	 *            from.
	 *
	 * @exception EPPDecodeException
	 *                Unable to decode aElement.
	 */
	public void decode(Element aElement) throws EPPDecodeException {
		// Type
		this.type = Type.getType(aElement.getAttribute(ATTR_TYPE));

		// Contact Identifier
		this.contactId = aElement.getFirstChild().getNodeValue();
	}


	/**
	 * implements a deep <code>EPPResellerContact</code> compare.
	 *
	 * @param aObject
	 *            <code>EPPResellerContact</code> instance to compare with
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPResellerContact)) {
			return false;
		}

		EPPResellerContact theComp = (EPPResellerContact) aObject;

		// Contact Identifier
		if (!EqualityUtil.equals(this.contactId, theComp.contactId)) {
			cat.error("EPPResellerContact.equals(): contactId not equal");
			return false;
		}

		// Type
		if (!EqualityUtil.equals(this.type, theComp.type)) {
			cat.error("EPPResellerContact.equals(): type not equal");
			return false;
		}

		return true;
	}


	/**
	 * Clone <code>EPPResellerContact</code>.
	 *
	 * @return clone of <code>EPPResellerContact</code>
	 *
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPResellerContact clone = null;

		clone = (EPPResellerContact) super.clone();

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
