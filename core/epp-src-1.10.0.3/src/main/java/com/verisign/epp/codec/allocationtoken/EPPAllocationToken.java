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

package com.verisign.epp.codec.allocationtoken;

import org.w3c.dom.Document;
// W3C Imports
import org.w3c.dom.Element;
import org.w3c.dom.Node;

//----------------------------------------------
// Imports
//----------------------------------------------
// SDK Imports
import com.verisign.epp.codec.gen.EPPCodecComponent;
import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPUtil;

/**
 * Allocation Token Extension, defines an allocation token or code for
 * allocating an object like a domain name to the client. The allocation token
 * MAY be transferred out-of-band to a client to give them authorization to
 * allocate an object using one of the EPP transform commands including create,
 * update, and transfer.
 */
public class EPPAllocationToken implements EPPCodecComponent {

	/**
	 * XML local name for <code>EPPAllocationToken</code>.
	 */
	public static final String ELM_LOCALNAME = "allocationToken";

	/** XML root tag for <code>EPPAllocationToken</code>. */
	public static final String ELM_NAME = EPPAllocationTokenExtFactory.NS_PREFIX
			+ ":" + ELM_LOCALNAME;

	/** Sub-Product Identifier. */
	private String token;

	/**
	 * O Default constructor for <code>EPPAllocationToken</code>.
	 */
	public EPPAllocationToken() {
		token = null;
	}

	/**
	 * Constructor for <code>EPPAllocationToken</code> that takes the token.
	 * 
	 * @param aToken
	 *            Sub-product identifier
	 */
	public EPPAllocationToken(String aToken) {
		token = aToken;
	}

	/**
	 * Is the token defined?
	 * 
	 * @return <code>true</code> if the token is defined; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasToken() {
		return (this.token != null ? true : false);
	}

	/**
	 * Gets the allocation token value.
	 * 
	 * @return Allocation token value if defined; <code>null</code> otherwise.
	 */
	public String getToken() {
		return this.token;
	}

	/**
	 * Sets the allocation token value.
	 * 
	 * @param aToken
	 *            Allocation token
	 */
	public void setToken(String aToken) {
		this.token = aToken;
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
	 *                Error encoding <code>EPPAllocationToken</code>
	 */
	public Element encode(Document aDocument) throws EPPEncodeException {
		
		// Create root element
		Element root = aDocument.createElementNS(
				EPPAllocationTokenExtFactory.NS, ELM_NAME);
		
		// Add token value if set
		if (this.token != null) {
			root.appendChild(aDocument.createTextNode(this.token));
		}

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

		if (aElement != null) {
			Node textNode = aElement.getFirstChild();

			// Element does have a text node?
			if (textNode != null) {
				this.token = textNode.getNodeValue();
			}
			else {
				this.token = null;
			}
		}
	}

	/**
	 * clone an <code>EPPCodecComponent</code>.
	 * 
	 * @return clone of concrete <code>EPPAllocationToken</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPAllocationToken clone = (EPPAllocationToken) super.clone();

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
	 * Compare an instance of <code>EPPAllocationToken</code> with this
	 * instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return <code>true</code> if equal; <code>false</code> otherwise.
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPAllocationToken)) {
			return false;
		}

		EPPAllocationToken theComp = (EPPAllocationToken) aObject;

		if (!((this.token == null) ? (theComp.token == null) : this.token
				.equals(theComp.token))) {
			return false;
		}

		return true;
	}

}
