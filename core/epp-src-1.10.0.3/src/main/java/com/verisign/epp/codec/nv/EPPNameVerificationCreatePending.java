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
package com.verisign.epp.codec.nv;

import java.util.Date;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EqualityUtil;

/**
 * Create response for a successful domain name verification, which results in
 * the return of a digitally signed domain verification code.
 * 
 * @see com.verisign.epp.codec.nv.EPPNameVerificationCreateCmd
 */
public class EPPNameVerificationCreatePending implements
		EPPNameVerificationCreateResult {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(
			EPPNameVerificationCreatePending.class.getName(), EPPCatFactory
					.getInstance().getFactory());

	/**
	 * XML local name for <code>EPPNameVerificationCreatePending</code>.
	 */
	public static final String ELM_LOCALNAME = "pending";

	/**
	 * XML root tag for <code>EPPNameVerificationCreatePending</code>.
	 */
	public static final String ELM_NAME = EPPNameVerificationMapFactory.NS_PREFIX
			+ ":" + ELM_LOCALNAME;

	/**
	 * XML Element Name for the <code>code</code> element.
	 */
	private final static String ELM_CODE = "code";

	/**
	 * The code type attribute name
	 */
	private static final String ATTR_TYPE = "type";
	
	/**
	 * XML Element Name for the <code>status</code> element.
	 */
	private final static String ELM_STATUS = "status";

	/**
	 * The status attribute name
	 */
	private static final String ATTR_STATUS = "s";
	
	/**
	 * XML Element Name for the <code>crDate</code> attribute.
	 */
	private final static String ELM_CREATION_DATE = "crDate";

	/**
	 * Verification code value
	 */
	private String code;

	/**
	 * Verification code type
	 */
	private String type;
	
	/**
	 * Status of the verification
	 */
	private EPPNameVerificationStatus status;

	/**
	 * Creation date of the signed code
	 */
	private Date creationDate;

	/**
	 * <code>EPPNameVerificationCreatePending</code> default constructor.
	 */
	public EPPNameVerificationCreatePending() {
	}

	/**
	 * <code>EPPNameVerificationCreatePending</code> constructor that takes the
	 * required attributes.
	 * 
	 * @param aCode
	 *            The verification code value
	 * @param aType
	 *            The verification code type
	 * @param aStatus
	 *            The verification status
	 * @param aCrDate
	 *            The creation date of the signed code
	 */
	public EPPNameVerificationCreatePending(String aCode, String aType,
			EPPNameVerificationStatus aStatus, Date aCrDate) {
		this.code = aCode;
		this.type = aType;
		this.status = aStatus;
		this.creationDate = aCrDate;
	}

	/**
	 * Gets the verification code value.
	 * 
	 * @return Verification code value
	 */
	public String getCode() {
		return this.code;
	}

	/**
	 * Sets the verification code value.
	 * 
	 * @param aCode
	 *            Verification code value.
	 */
	public void setCode(String aCode) {
		this.code = aCode;
	}

	/**
	 * Gets the verification status.
	 * 
	 * @return Verification status
	 */
	public EPPNameVerificationStatus getStatus() {
		return this.status;
	}

	/**
	 * Sets the verification status.
	 * 
	 * @param aStatus
	 *            The verification status
	 */
	public void setStatus(EPPNameVerificationStatus aStatus) {
		this.status = aStatus;
	}

	/**
	 * Gets the creation date of the signed code.
	 * 
	 * @return Creation date of the signed code
	 */
	public java.util.Date getCreationDate() {
		return this.creationDate;
	}

	/**
	 * Sets the creation date of the signed code
	 * 
	 * @param aCreationDate
	 *            Creation date of the signed code
	 */
	public void setCreationDate(Date aCreationDate) {
		this.creationDate = aCreationDate;
	}

	/**
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPNameVerificationCreatePending</code> instance.
	 * 
	 * @param aDocument
	 *            DOM Document that is being built. Used as an Element factory.
	 * 
	 * @return Element Root DOM Element representing the
	 *         EPPNameVerificationCreatePending instance.
	 * 
	 * @exception EPPEncodeException
	 *                Unable to encode EPPNameVerificationCreatePending
	 *                instance.
	 */
	@Override
	public Element encode(Document aDocument) throws EPPEncodeException {

		// Check required attributes
		if (this.code == null) {
			throw new EPPEncodeException(
					"Undefined code in EPPNameVerificationCreatePending");
		}
		if (this.status == null) {
			throw new EPPEncodeException(
					"Undefined status in EPPNameVerificationCreatePending");
		}
		if (this.creationDate == null) {
			throw new EPPEncodeException(
					"Undefined creationDate in EPPNameVerificationCreatePending");
		}

		// Create root element
		Element root = aDocument.createElementNS(
				EPPNameVerificationMapFactory.NS, ELM_NAME);

		// Code
		Element code = aDocument.createElementNS(
				EPPNameVerificationMapFactory.NS,
				EPPNameVerificationMapFactory.NS_PREFIX + ":" + ELM_CODE);
		Text codeVal = aDocument.createTextNode(this.code);
		code.appendChild(codeVal);
		root.appendChild(code);

		// Code Type
		 code.setAttribute(ATTR_TYPE, this.type);

		// Status
		Element theStatusElm = aDocument.createElementNS(
				EPPNameVerificationMapFactory.NS,
				EPPNameVerificationMapFactory.NS_PREFIX + ":" + ELM_STATUS);
		root.appendChild(theStatusElm);
		theStatusElm.setAttribute(ATTR_STATUS, this.status.toString());

		// Creation Date
		EPPUtil.encodeTimeInstant(aDocument, root, this.creationDate,
				EPPNameVerificationMapFactory.NS,
				EPPNameVerificationMapFactory.NS_PREFIX + ":"
						+ ELM_CREATION_DATE);

		return root;
	}

	/**
	 * Decode the <code>EPPNameVerificationCreatePending</code> attributes from
	 * the aElement DOM Element tree.
	 * 
	 * @param aElement
	 *            Root DOM Element to decode
	 *            <code>EPPNameVerificationCreatePending</code> from.
	 * 
	 * @exception EPPDecodeException
	 *                Unable to decode aElement
	 */
	@Override
	public void decode(Element aElement) throws EPPDecodeException {

		// Code
		this.code = EPPUtil.decodeString(aElement,
				EPPNameVerificationMapFactory.NS, ELM_CODE);

		// Type
		 Element theElm = EPPUtil.getElementByTagNameNS(aElement,
		 EPPNameVerificationMapFactory.NS, ELM_CODE);
		 this.type = theElm.getAttribute(ATTR_TYPE);

		// Status
		theElm = EPPUtil.getElementByTagNameNS(aElement,
				EPPNameVerificationMapFactory.NS, ELM_STATUS);
		String theStatusStr = theElm.getAttribute(ATTR_STATUS);
		this.status = EPPNameVerificationStatus.getStatus(theStatusStr);

		// Creation Date
		this.creationDate = EPPUtil.decodeTimeInstant(aElement,
				EPPNameVerificationMapFactory.NS, ELM_CREATION_DATE);
	}

	/**
	 * Clone <code>EPPNameVerificationCreatePending</code>.
	 * 
	 * @return clone of <code>EPPNameVerificationCreatePending</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPNameVerificationCreatePending clone = (EPPNameVerificationCreatePending) super
				.clone();

		// Code
		clone.code = this.code;

		// Type
		 clone.type = this.type;

		// Status
		clone.status = this.status;

		// Creation Date
		clone.creationDate = this.creationDate;

		return clone;
	}

	/**
	 * Gets the EPP response type associated with
	 * <code>EPPNameVerificationCreatePending</code>.
	 * 
	 * @return <code>EPPNameVerificationCreatePending.ELM_NAME</code>
	 */
	public String getType() {
		return ELM_NAME;
	}

	/**
	 * Gets the EPP command namespace associated with
	 * <code>EPPNameVerificationCreatePending</code>.
	 * 
	 * @return <code>EPPNameVerificationMapFactory.NS</code>
	 */
	public String getNamespace() {
		return EPPNameVerificationMapFactory.NS;
	}

	/**
	 * Compare an instance of <code>EPPNameVerificationCreatePending</code> with
	 * this instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return {@code true} if this object is the same as the aObject argument;
	 *         {@code false} otherwise
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPNameVerificationCreatePending)) {
			return false;
		}

		EPPNameVerificationCreatePending other = (EPPNameVerificationCreatePending) aObject;

		// Code
		if (!EqualityUtil.equals(this.code, other.code)) {
			cat.error("EPPNameVerificationCreatePending.equals(): code not equal");
			return false;
		}

		// Type
		 if (!EqualityUtil.equals(this.type, other.type)) {
		 cat.error("EPPNameVerificationCreatePending.equals(): type not equal");
		 return false;
		 }

		// Status
		if (!EqualityUtil.equals(this.status, other.status)) {
			cat.error("EPPNameVerificationCreatePending.equals(): status not equal");
			return false;
		}

		// Creation Date
		if (!EqualityUtil.equals(this.creationDate, other.creationDate)) {
			cat.error("EPPNameVerificationCreatePending.equals(): creationDate not equal");
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