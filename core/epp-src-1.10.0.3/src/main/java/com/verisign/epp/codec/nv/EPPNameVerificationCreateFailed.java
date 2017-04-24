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
public class EPPNameVerificationCreateFailed implements
		EPPNameVerificationCreateResult {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(
			EPPNameVerificationCreateFailed.class.getName(), EPPCatFactory
					.getInstance().getFactory());

	/**
	 * XML local name for <code>EPPNameVerificationCreateFailed</code>.
	 */
	public static final String ELM_LOCALNAME = "failed";

	/**
	 * XML root tag for <code>EPPNameVerificationCreateFailed</code>.
	 */
	public static final String ELM_NAME = EPPNameVerificationMapFactory.NS_PREFIX
			+ ":" + ELM_LOCALNAME;

	/**
	 * XML Element Name for the <code>status</code> element.
	 */
	private final static String ELM_STATUS = "status";

	/**
	 * The status attribute name
	 */
	private static final String ATTR_STATUS = "s";
	
	/**
	 * XML Element Name for the <code>msg</code>.
	 */
	private final static String ELM_REASON = "msg";

	/**
	 * XML attribute name for the <code>lang</code> attribute.
	 */
	private final static String ATTR_LANG = "lang";

	/**
	 * Default XML attribute value for reason language.
	 */
	private final static String VALUE_LANG = "en";

	/**
	 * Human-readable description of the reason of the failure
	 */
	private String reason;

	/**
	 * XML attribute value for the <code>lang</code> attribute.
	 */
	private String language = VALUE_LANG;

	/**
	 * Status of the verification
	 */
	private EPPNameVerificationStatus status;

	/**
	 * <code>EPPNameVerificationCreateFailed</code> default constructor.
	 */
	public EPPNameVerificationCreateFailed() {
	}

	/**
	 * <code>EPPNameVerificationCreateFailed</code> constructor that takes the
	 * required attributes.
	 * 
	 * @param aStatus
	 *            The verification status
	 * @param aReason
	 *            Human-readable reason of the failure
	 */
	public EPPNameVerificationCreateFailed(EPPNameVerificationStatus aStatus,
			String aReason) {
		this.status = aStatus;
		this.reason = aReason;
	}

	/**
	 * <code>EPPNameVerificationCreateFailed</code> constructor that takes the
	 * all attributes.
	 * 
	 * @param aStatus
	 *            The verification status
	 * @param aReason
	 *            Human-readable reason of the failure
	 * @param aLanguage
	 *            Language of the reason with the default of &quot;en&quot;.
	 */
	public EPPNameVerificationCreateFailed(EPPNameVerificationStatus aStatus,
			String aReason, String aLanguage) {
		this.status = aStatus;
		this.reason = aReason;
		this.language = aLanguage;
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
	 * Gets the reason for the failure.
	 *
	 * @return Reason for the failure
	 */
	public String getReason() {
		return this.reason;
	}

	/**
	 * Sets the reason for the failure.
	 *
	 * @param aReason
	 *            Reason for the failure
	 */
	public void setReason(String aReason) {
		this.reason = aReason;
	}

	/**
	 * Gets the language of the failure with the default of &quot;en&quot;.
	 *
	 * @return Language of the failure
	 */
	public String getLanguage() {
		return this.language;
	}

	/**
	 * Sets language of the reason.
	 *
	 * @param aLanguage
	 *            Language of the reason
	 */
	public void setLanguage(String aLanguage) {
		this.language = aLanguage;
	}

	/**
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPNameVerificationCreateFailed</code> instance.
	 * 
	 * @param aDocument
	 *            DOM Document that is being built. Used as an Element factory.
	 * 
	 * @return Element Root DOM Element representing the
	 *         EPPNameVerificationCreateFailed instance.
	 * 
	 * @exception EPPEncodeException
	 *                Unable to encode EPPNameVerificationCreateFailed instance.
	 */
	@Override
	public Element encode(Document aDocument) throws EPPEncodeException {

		// Check required attributes
		if (this.status == null) {
			throw new EPPEncodeException(
					"Undefined status in EPPNameVerificationCreateFailed");
		}
		if (this.reason == null) {
			throw new EPPEncodeException(
					"Undefined reason in EPPNameVerificationCreateFailed");
		}

		// Create root element
		Element root = aDocument.createElementNS(
				EPPNameVerificationMapFactory.NS, ELM_NAME);

		// Status
		Element theStatusElm = aDocument.createElementNS(
				EPPNameVerificationMapFactory.NS,
				EPPNameVerificationMapFactory.NS_PREFIX + ":" + ELM_STATUS);
		root.appendChild(theStatusElm);
		theStatusElm.setAttribute(ATTR_STATUS, this.status.toString());

		// Reason
		Element reason = aDocument.createElementNS(
				EPPNameVerificationMapFactory.NS,
				EPPNameVerificationMapFactory.NS_PREFIX + ":" + ELM_REASON);
		Text reasonVal = aDocument.createTextNode(this.reason);
		reason.appendChild(reasonVal);
		root.appendChild(reason);

		// Reason Language
		/**
		 * @todo Uncomment once the lang attribute is added to the reason / msg
		 *       element. if (this.language != null) {
		 *       reason.setAttribute(ATTR_LANG, this.language); }
		 */

		return root;
	}

	/**
	 * Decode the <code>EPPNameVerificationCreateFailed</code> attributes from
	 * the aElement DOM Element tree.
	 * 
	 * @param aElement
	 *            Root DOM Element to decode
	 *            <code>EPPNameVerificationCreateFailed</code> from.
	 * 
	 * @exception EPPDecodeException
	 *                Unable to decode aElement
	 */
	@Override
	public void decode(Element aElement) throws EPPDecodeException {

		// Status
		Element theElm = EPPUtil.getElementByTagNameNS(aElement,
				EPPNameVerificationMapFactory.NS, ELM_STATUS);
		String theStatusStr = theElm.getAttribute(ATTR_STATUS);
		this.status = EPPNameVerificationStatus.getStatus(theStatusStr);

		// Reason
		this.reason = EPPUtil.decodeString(aElement,
				EPPNameVerificationMapFactory.NS, ELM_REASON);

		// Language
		/**
		 * @todo Uncomment once the lang attribute is added to the reason / msg
		 *       element. Element theElm =
		 *       EPPUtil.getElementByTagNameNS(aElement,
		 *       EPPNameVerificationMapFactory.NS, ELM_REASON); this.language =
		 *       theElm.getAttribute(ATTR_LANG);
		 */
	}

	/**
	 * Clone <code>EPPNameVerificationCreateFailed</code>.
	 * 
	 * @return clone of <code>EPPNameVerificationCreateFailed</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPNameVerificationCreateFailed clone = (EPPNameVerificationCreateFailed) super
				.clone();

		return clone;
	}

	/**
	 * Gets the EPP response type associated with
	 * <code>EPPNameVerificationCreateFailed</code>.
	 * 
	 * @return <code>EPPNameVerificationCreateFailed.ELM_NAME</code>
	 */
	public String getType() {
		return ELM_NAME;
	}

	/**
	 * Gets the EPP command namespace associated with
	 * <code>EPPNameVerificationCreateFailed</code>.
	 * 
	 * @return <code>EPPNameVerificationMapFactory.NS</code>
	 */
	public String getNamespace() {
		return EPPNameVerificationMapFactory.NS;
	}

	/**
	 * Compare an instance of <code>EPPNameVerificationCreateFailed</code> with
	 * this instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return {@code true} if this object is the same as the aObject argument;
	 *         {@code false} otherwise
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPNameVerificationCreateFailed)) {
			return false;
		}

		EPPNameVerificationCreateFailed other = (EPPNameVerificationCreateFailed) aObject;

		// Status
		if (!EqualityUtil.equals(this.status, other.status)) {
			cat.error("EPPNameVerificationCreateFailed.equals(): status not equal");
			return false;
		}

		// Reason
		if (!EqualityUtil.equals(this.reason, other.reason)) {
			cat.error("EPPNameVerificationCreateFailed.equals(): reason not equal");
			return false;
		}

		// Reason Language
		if (!EqualityUtil.equals(this.language, other.language)) {
			cat.error("EPPNameVerificationCreateFailed.equals(): language not equal");
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