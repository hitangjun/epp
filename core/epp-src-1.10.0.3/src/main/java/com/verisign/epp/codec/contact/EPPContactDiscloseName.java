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
package com.verisign.epp.codec.contact;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.verisign.epp.codec.gen.EPPCodecComponent;
import com.verisign.epp.codec.gen.EPPCodecException;
import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;

/**
 * Represents a contact disclose name definition.
 * 
 * @deprecated Use the <code>nameInt</code> and <code>nameLoc</code> attributes
 *             defined in {@link EPPContactDisclose}.
 */
@Deprecated
public class EPPContactDiscloseName implements EPPCodecComponent {

	/**
	 * XML Attribute Name for disclose type.
	 */
	private final static String	ATTR_TYPE		= "type";

	/**
	 * Value of the INT in contact disclose type mapping.
	 */
	public final static String		ATTR_TYPE_INT	= "int";

	/**
	 * Value of the LOC in contact disclose type mapping.
	 */
	public final static String		ATTR_TYPE_LOC	= "loc";

	/**
	 * Constant for disclose name local name.
	 */
	public static final String		ELM_LOCALNAME	= "name";

	/**
	 * XML Element Name of <code>EPPContactDiscloseName</code> root element.
	 */
	public static final String		ELM_NAME			= EPPContactMapFactory.NS_PREFIX + ":" + ELM_LOCALNAME;

	/**
	 * The type value of this Type component.
	 */
	private String						type				= ATTR_TYPE_LOC;

	/**
	 * Create a new instance of EPPContactDiscloseName
	 */
	public EPPContactDiscloseName() {
	}

	/**
	 * Create a new instance of EPPContactDiscloseName with the given type
	 *
	 * @param aType
	 *           the type value to use for this instance. Should use either
	 *           {@link #ATTR_TYPE_INT} or {@link #ATTR_TYPE_LOC}.
	 */
	public EPPContactDiscloseName(String aType) {
		this.type = aType;
	}

	/**
	 * Clone <code>EPPContactDiscloseName</code>.
	 *
	 * @return clone of <code>EPPContactDiscloseName</code>
	 *
	 * @exception CloneNotSupportedException
	 *               standard Object.clone exception
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		EPPContactDiscloseName clone = null;

		clone = (EPPContactDiscloseName) super.clone();

		clone.type = this.type;

		return clone;
	}

	/**
	 * Decode the <code>EPPContactDisclose</code> attributes from the aElement
	 * DOM Element tree.
	 *
	 * @param aElement
	 *           The root element of the report fragment of XML
	 *
	 * @throws EPPDecodeException
	 */
	@Override
	public void decode(Element aElement) throws EPPDecodeException {
		// Type
		this.type = aElement.getAttribute(ATTR_TYPE);
	}

	/**
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPContactDiscloseName</code> instance.
	 *
	 * @param aDocument
	 *           The DOM Document to append data to
	 *
	 * @return Encoded DOM <code>Element</code>
	 *
	 * @throws EPPEncodeException
	 */
	@Override
	public Element encode(Document aDocument) throws EPPEncodeException {
		try {
			validateState();
		}
		catch (EPPCodecException e) {
			throw new EPPEncodeException("EPPContactDiscloseName invalid state: " + e);
		}

		// creditThreshold with Attributes
		Element root = aDocument.createElementNS(EPPContactMapFactory.NS, ELM_NAME);

		// add attribute type
		root.setAttribute(ATTR_TYPE, this.type);

		return root;
	}

	/**
	 * implements a deep <code>EPPContactDiscloseName</code> compare.
	 *
	 * @param aObject
	 *           <code>EPPContactDiscloseName</code> instance to compare with
	 *
	 * @return true if equal false otherwise
	 */
	@Override
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPContactDiscloseName)) {
			return false;
		}

		EPPContactDiscloseName theComp = (EPPContactDiscloseName) aObject;

		// type
		if (!this.type.equals(theComp.type)) {
			return false;
		}

		return true;
	}

	/**
	 * Gets the type of the name element.
	 *
	 * @return Should be either {@link #ATTR_TYPE_INT} or {@link #ATTR_TYPE_LOC}.
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Sets the type of the name element.
	 *
	 * @param aType
	 *           Should be either {@link #ATTR_TYPE_INT} or
	 *           {@link #ATTR_TYPE_LOC}.
	 */
	public void setType(String aType) {
		this.type = aType;
	}

	/**
	 * Validate the state of the <code>EPPContactDiscloseName</code> instance. A
	 * valid state means that all of the required attributes have been set. If
	 * validateState returns without an exception, the state is valid. If the
	 * state is not valid, the <code>EPPCodecException</code> will contain a
	 * description of the error. throws EPPCodecException State error. This will
	 * contain the name of the attribute that is not valid.
	 *
	 * @throws EPPCodecException
	 *            Thrown if the instance is in an invalid state
	 */
	void validateState() throws EPPCodecException {
		// type
		if (this.type == null) {
			throw new EPPCodecException("EPPContactDiscloseName required type attribute is not set");
		}
	}
}
