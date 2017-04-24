/***********************************************************
Copyright (C) 2015 VeriSign, Inc.

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
package com.verisign.epp.codec.resellerext;

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
 * Reseller Extension to the info response to return the reseller identifier and
 * optionally the reseller name.
 * 
 * @see com.verisign.epp.codec.reseller.EPPResellerInfoResp
 */
public class EPPResellerExtInfData implements EPPCodecComponent {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPResellerExtInfData.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * XML local name for <code>EPPResellerInfoResp</code>.
	 */
	public static final String ELM_LOCALNAME = "infData";

	/**
	 * XML root tag for <code>EPPResellerInfoResp</code>.
	 */
	public static final String ELM_NAME = EPPResellerExtFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * XML local name for the reseller identifier element
	 */
	private static final String ELM_RESELLER_ID = "id";

	/**
	 * XML local name for the reseller name element
	 */
	private static final String ELM_RESELLER_NAME = "name";

	/**
	 * The reseller identifier of the reseller of a sponsoring registrar.
	 */
	private String resellerId;

	/**
	 * The OPTIONAL reseller name of the reseller of a sponsoring registrar.
	 */
	private String resellerName;

	/**
	 * <code>EPPResellerInfoResp</code> default constructor.
	 */
	public EPPResellerExtInfData() {
	}

	/**
	 * <code>EPPResellerInfoResp</code> constructor that takes the
	 * the required reseller identifier.
	 * 
	 * @param aResellerId
	 *            Reseller identifier.
	 * 
	 */
	public EPPResellerExtInfData(String aResellerId) {
		this.setResellerId(aResellerId);
	}
	
	/**
	 * <code>EPPResellerInfoResp</code> constructor that takes the
	 * the required reseller identifier and the optional reseller name.
	 * 
	 * @param aResellerId
	 *            Reseller identifier.
	 * @param aResellerName
	 * 	          Reseller name
	 */
	public EPPResellerExtInfData(String aResellerId, String aResellerName) {
		this.setResellerId(aResellerId);
		this.setResellerName(aResellerName);
	}
	

	/**
	 * Gets the reseller identifier.
	 * 
	 * @return The reseller identifier if defined;<code>null</code> otherwise.
	 */
	public String getResellerId() {
		return this.resellerId;
	}

	/**
	 * Sets the reseller identifier.
	 * 
	 * @param aResellerId
	 *            The reseller identifier
	 */
	public void setResellerId(String aResellerId) {
		this.resellerId = aResellerId;
	}

	/**
	 * Is the reseller name defined?
	 * 
	 * @return <code>true</code> if the reseller name is defined;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasResellerName() {
		return (this.resellerName != null ? true : false);
	}

	/**
	 * Gets the OPTIONAL reseller name.
	 * 
	 * @return The reseller identifier if defined;<code>null</code> otherwise.
	 */
	public String getResellerName() {
		return this.resellerName;
	}

	/**
	 * Sets the OPTIONAL reseller name.
	 * 
	 * @param aResellerName
	 *            The reseller name
	 */
	public void setResellerName(String aResellerName) {
		this.resellerName = aResellerName;
	}

	/**
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPResellerExtInfoResp</code> instance.
	 * 
	 * @param aDocument
	 *            DOM Document that is being built. Used as an Element factory.
	 * 
	 * @return Element Root DOM Element representing the EPPResellerInfoResp
	 *         instance.
	 * 
	 * @exception EPPEncodeException
	 *                Unable to encode EPPResellerInfoResp instance.
	 */
	public Element encode(Document aDocument) throws EPPEncodeException {

		// Check required attributes
		if (this.resellerId == null) {
			throw new EPPEncodeException(
					"Undefined resellerId in EPPResellerExtInfoResp");
		}

		// Create root element
		Element root = aDocument.createElementNS(EPPResellerExtFactory.NS,
				ELM_NAME);

		// Reseller Identifier
		EPPUtil.encodeString(aDocument, root, this.resellerId,
				EPPResellerExtFactory.NS, EPPResellerExtFactory.NS_PREFIX + ":"
						+ ELM_RESELLER_ID);

		// Reseller Name
		EPPUtil.encodeString(aDocument, root, this.resellerName,
				EPPResellerExtFactory.NS, EPPResellerExtFactory.NS_PREFIX + ":"
						+ ELM_RESELLER_NAME);

		return root;
	}

	/**
	 * Decode the <code>EPPResellerInfoResp</code> attributes from the aElement
	 * DOM Element tree.
	 * 
	 * @param aElement
	 *            Root DOM Element to decode <code>EPPResellerInfoResp</code>
	 *            from.
	 * 
	 * @exception EPPDecodeException
	 *                Unable to decode aElement
	 */
	public void decode(Element aElement) throws EPPDecodeException {

		// Reseller Identifier
		this.resellerId = EPPUtil.decodeString(aElement,
				EPPResellerExtFactory.NS, ELM_RESELLER_ID);

		// Reseller Name
		this.resellerName = EPPUtil.decodeString(aElement,
				EPPResellerExtFactory.NS, ELM_RESELLER_NAME);

	}

	/**
	 * Clone <code>EPPResellerInfoResp</code>.
	 * 
	 * @return clone of <code>EPPResellerInfoResp</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPResellerExtInfData clone = (EPPResellerExtInfData) super.clone();

		return clone;
	}

	/**
	 * Gets the EPP response type associated with
	 * <code>EPPResellerInfoResp</code>.
	 * 
	 * @return <code>EPPResellerInfoResp.ELM_NAME</code>
	 */
	public String getType() {
		return ELM_NAME;
	}

	/**
	 * Gets the EPP command namespace associated with
	 * <code>EPPResellerInfoResp</code>.
	 * 
	 * @return <code>EPPResellerExtFactory.NS</code>
	 */
	public String getNamespace() {
		return EPPResellerExtFactory.NS;
	}

	/**
	 * Compare an instance of <code>EPPResellerInfoResp</code> with this
	 * instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return {@code true} if this object is the same as the aObject argument;
	 *         {@code false} otherwise
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPResellerExtInfData)) {
			return false;
		}

		EPPResellerExtInfData other = (EPPResellerExtInfData) aObject;

		// Reseller Identifier
		if (!EqualityUtil.equals(this.resellerId, other.resellerId)) {
			cat.error("EPPResellerInfoResp.equals(): resellerId not equal");
			return false;
		}

		// Reseller Name
		if (!EqualityUtil.equals(this.resellerName, other.resellerName)) {
			cat.error("EPPResellerInfoResp.equals(): resellerName not equal");
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