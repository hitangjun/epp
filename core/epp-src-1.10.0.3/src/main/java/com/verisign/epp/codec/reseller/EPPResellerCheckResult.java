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
 * <code>EPPResellerCheckResult</code> is used to represent the for the check of
 * an individual reseller. The reseller information includes the reseller
 * identifier, whether the reseller identifier is available, and optionally the
 * reason that the reseller identifier is not available.
 */
public class EPPResellerCheckResult implements EPPCodecComponent {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPResellerCheckResult.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * XML local name for <code>EPPResellerCheckResult</code>.
	 */
	public static final String ELM_LOCALNAME = "cd";

	/**
	 * XML root tag for <code>EPPResellerCheckResult</code>.
	 */
	public static final String ELM_NAME = EPPResellerMapFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * XML local name for the reseller identifier element
	 */
	private static final String ELM_RESELLER_ID = "id";

	/**
	 * XML local name for the reason element
	 */
	private static final String ELM_REASON = "reason";

	/**
	 * XML local name for the avail attribute
	 */
	private static final String ATTR_AVAIL = "avail";

	/**
	 * XML local name for the reason lang attribute.
	 */
	private static final String ATTR_LANG = "lang";

	/**
	 * Value for the reseller identifier
	 */
	private String resellerId;

	/**
	 * Is the <code>resellerId</code> available?
	 */
	private boolean available;

	/**
	 * OPTIONAL reason element
	 */
	private String reason;

	/**
	 * OPTIONAL language of the reason.
	 */
	private String reasonLang;

	/**
	 * Default constructor for <code>EPPResellerCheckResult</code>.
	 */
	public EPPResellerCheckResult() {
	}

	/**
	 * Constructor for <code>EPPResellerCheckResult</code> that takes reseller
	 * identifier and whether the reseller identifier is available.
	 * 
	 * @param aResellerId
	 *            The reseller identifier
	 * @param aAvailable
	 *            Is the reseller identifier available?
	 */
	public EPPResellerCheckResult(String aResellerId, boolean aAvailable) {
		this.setResellerId(aResellerId);
		this.setAvailable(aAvailable);
	}

	/**
	 * Constructor for <code>EPPResellerCheckResult</code> that is used for
	 * unavailable reseller identifiers providing the reason the reseller
	 * identifier is not available.
	 * 
	 * @param aResellerId
	 *            The reseller identifier
	 * @param aReason
	 *            Reason that the reseller identifier is not available.
	 */
	public EPPResellerCheckResult(String aResellerId, String aReason) {
		this.setResellerId(aResellerId);
		this.setAvailable(false);
		this.setReason(aReason);
	}

	/**
	 * Constructor for <code>EPPResellerCheckResult</code> that is used for
	 * unavailable reseller identifiers providing the reason the reseller
	 * identifier is not available with the reason language.
	 * 
	 * @param aResellerId
	 *            The reseller identifier
	 * @param aReason
	 *            Reason that the reseller identifier is not available.
	 * @param aReasonLang
	 *            Reason language for reason value.
	 */
	public EPPResellerCheckResult(String aResellerId, String aReason,
			String aReasonLang) {
		this.setResellerId(aResellerId);
		this.setAvailable(false);
		this.setReason(aReason);
		this.setReasonLang(aReasonLang);
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
	 * Is the reseller identifier available?
	 * 
	 * @return <code>true</code> if the reseller identifier is available;
	 *         <code>false</code> otherwise.
	 */
	public boolean isAvailable() {
		return this.available;
	}

	/**
	 * Sets the reseller identifier is available.
	 * 
	 * @param aAvailable
	 *            <code>true</code> if the reseller identifier is available;
	 *            <code>false</code> otherwise.
	 */
	public void setAvailable(boolean aAvailable) {
		this.available = aAvailable;
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
	 * Gets the reason that the reseller identifier is not available.
	 * 
	 * @return Reason that the reseller identifier is not available if defined;
	 *         <code>null</code> otherwise.
	 */
	public String getReason() {
		return this.reason;
	}

	/**
	 * Sets the reason that the reseller identifier is not available.
	 * 
	 * @param aReason
	 *            Reason that the reseller identifier is not available. Set to
	 *            <code>null</code> to clear the reason.
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
	 *                Error encoding <code>EPPResellerCheckResult</code>
	 */
	public Element encode(Document aDocument) throws EPPEncodeException {

		// Check required attributes
		if (this.resellerId == null) {
			throw new EPPEncodeException(
					"Undefined name in EPPResellerCheckResult");
		}

		// Domain
		Element root = aDocument.createElementNS(EPPResellerMapFactory.NS,
				ELM_NAME);

		// Reseller Identifier
		Element theElm = aDocument.createElementNS(EPPResellerMapFactory.NS,
				EPPResellerMapFactory.NS_PREFIX + ":" + ELM_RESELLER_ID);

		theElm.appendChild(aDocument.createTextNode(this.resellerId));

		// Available
		EPPUtil.encodeBooleanAttr(theElm, ATTR_AVAIL, this.available);

		root.appendChild(theElm);

		// Reason and ReasonLang
		if (this.hasReason()) {
			Element reasonElm = aDocument.createElementNS(
					EPPResellerMapFactory.NS, EPPResellerMapFactory.NS_PREFIX
							+ ":" + ELM_REASON);

			if (this.hasReasonLang()) {
				reasonElm.setAttribute(ATTR_LANG, this.reasonLang);
			}

			reasonElm.appendChild(aDocument.createTextNode(this.reason));

			root.appendChild(reasonElm);
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

		// Reseller Identifier
		Element theNameElm = EPPUtil.getElementByTagNameNS(aElement,
				EPPResellerMapFactory.NS, ELM_RESELLER_ID);
		if (theNameElm != null) {
			Node textNode = theNameElm.getFirstChild();
			if (textNode != null) {
				this.resellerId = textNode.getNodeValue();
			}
			else {
				this.resellerId = null;
			}

			// Available
			this.available = EPPUtil.decodeBooleanAttr(theNameElm, ATTR_AVAIL);
		}

		// Reason and ReasonLang
		Element theReasonElm = EPPUtil.getElementByTagNameNS(aElement,
				EPPResellerMapFactory.NS, ELM_REASON);

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
	}

	/**
	 * clone an <code>EPPCodecComponent</code>.
	 * 
	 * @return clone of concrete <code>EPPResellerCheckResult</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPResellerCheckResult clone = (EPPResellerCheckResult) super.clone();

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
	 * Compare an instance of <code>EPPResellerCheckResult</code> with this
	 * instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return <code>true</code> if equal; <code>false</code> otherwise.
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPResellerCheckResult)) {
			return false;
		}

		EPPResellerCheckResult other = (EPPResellerCheckResult) aObject;

		// Reseller Identifier
		if (!EqualityUtil.equals(this.resellerId, other.resellerId)) {
			cat.error("EPPResellerCheckResult.equals(): resellerId not equal");
			return false;
		}

		// Available
		if (!EqualityUtil.equals(this.available, other.available)) {
			cat.error("EPPResellerCheckResult.equals(): available not equal");
			return false;
		}

		// Reason and ReasonLang
		if (!EqualityUtil.equals(this.reason, other.reason)) {
			cat.error("EPPResellerCheckResult.equals(): reason not equal");
			return false;
		}
		if (!EqualityUtil.equals(this.reasonLang, other.reasonLang)) {
			cat.error("EPPResellerCheckResult.equals(): reasonLang not equal");
			return false;
		}

		return true;
	}

}
