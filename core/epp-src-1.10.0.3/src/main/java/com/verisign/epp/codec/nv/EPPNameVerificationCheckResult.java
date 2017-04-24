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
package com.verisign.epp.codec.nv;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
//----------------------------------------------
//
// imports...
//
//----------------------------------------------
// Java Core Imports
// W3C Imports
import org.w3c.dom.Element;
import org.w3c.dom.Text;

// EPP Imports
import com.verisign.epp.codec.gen.EPPCodecComponent;
import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EqualityUtil;

/**
 * <code>EPPNameVerificationCheckResult</code> represents the result of an
 * individual Name Verification label check. The attributes of
 * <code>EPPNameVerificationCheckResult</code> include the label and a boolean
 * value indicating if the label can be created as a Domain Name Verification
 * (DNV) object. The OPTIONAL reason defines why a label cannot create a Domain
 * Name Verification (DNV) object. An OPTIONAL &quot;restricted&quot; is used to
 * indicate whether or not the label is a restricted label, with a default value
 * of <code>false</code>.
 * 
 * @see com.verisign.epp.codec.nv.EPPNameVerificationCheckResp
 */
public class EPPNameVerificationCheckResult implements EPPCodecComponent {
	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(
			EPPNameVerificationCheckResult.class.getName(), EPPCatFactory
					.getInstance().getFactory());

	/**
	 * Constant for the result local name
	 */
	public static final String ELM_LOCALNAME = "cd";

	/**
	 * Constant for the profile tag
	 */
	public static final String ELM_NAME = EPPNameVerificationMapFactory.NS_PREFIX
			+ ":" + ELM_LOCALNAME;

	/** 
	 * XML Element Name for the label <code>name</code>. 
	 */
	private final static String ELM_LABEL_NAME = "name";

	/** 
	 * XML Element Name for the <code>reason</code>. 
	 */
	private final static String ELM_REASON = "reason";

	/** 
	 * XML attribute name for the <code>avail</code> attribute. 
	 */
	private final static String ATTR_AVAIL = "avail";

	/** 
	 * XML attribute name for the <code>restricted</code> attribute. 
	 */
	private final static String ATTR_RESTRICTED = "restricted";

	/** 
	 * XML attribute name for the <code>lang</code> attribute. 
	 */
	private final static String ATTR_LANG = "lang";

	/** 
	 * Default XML attribute value for reason language. 
	 */
	private final static String VALUE_LANG = "en";

	/** 
	 * XML attribute value for the <code>lang</code> attribute. 
	 */
	private String language = VALUE_LANG;

	/** 
	 * Label Name associated with result. 
	 */
	private String label;

	/** 
	 * Is the label available to create? 
	 */
	private boolean available = false;

	/** 
	 * Label unavailable reason. 
	 */
	private String reason;

	/**
	 * Is the label restricted?
	 */
	private boolean restricted = false;

	/**
	 * Default constructor for <code>EPPNameVerificationCheckResult</code>.
	 */
	public EPPNameVerificationCheckResult() {
	}

	/**
	 * Constructor for <code>EPPNameVerificationCheckResult</code> that includes
	 * the label and the available flag.
	 *
	 * @param aName
	 *            Label associated with result
	 * @param aIsAvailable
	 *            Is the label available to be created?
	 */
	public EPPNameVerificationCheckResult(String aName, boolean aIsAvailable) {
		this.label = aName;
		this.available = aIsAvailable;
	}

	/**
	 * Constructor for <code>EPPNameVerificationCheckResult</code> that includes
	 * the label, availability flag, and the availability reason.
	 *
	 * @param aName
	 *            Label associated with result
	 * @param aIsAvailable
	 *            Is the label available to be created?
	 * @param aReason
	 *            Reason that label is not available
	 */
	public EPPNameVerificationCheckResult(String aName, boolean aIsAvailable,
			String aReason) {
		this.label = aName;
		this.available = aIsAvailable;
		this.reason = aReason;
	}

	/**
	 * Constructor for <code>EPPNameVerificationCheckResult</code> that includes
	 * all attributes.
	 *
	 * @param aName
	 *            Label associated with result
	 * @param aIsAvailable
	 *            Is the label available to be created?
	 * @param aReason
	 *            Reason that label is not available
	 * @param aLanguage
	 *            Language of the <code>aReason</code> value.
	 * @param aRestricted
	 *            Is the label restricted?
	 */
	public EPPNameVerificationCheckResult(String aName, boolean aIsAvailable,
			String aReason, String aLanguage, boolean aRestricted) {
		this.label = aName;
		this.available = aIsAvailable;
		this.reason = aReason;
		this.language = aLanguage;
		this.restricted = aRestricted;
	}

	/**
	 * Gets the label associated with the result.
	 *
	 * @return Label associated with the result if defined; <code>null</code>
	 *         otherwise.
	 */
	public String getLabel() {
		return this.label;
	}

	/**
	 * Sets the label associated with the result.
	 *
	 * @param aLabel
	 *            Label associated with the result.
	 */
	public void setLabel(String aLabel) {
		this.label = aLabel;
	}

	/**
	 * Gets if the label associated with
	 * <code>EPPNameVerificationCheckResult</code> is availability to be
	 * created.
	 *
	 * @return Is the label available? If <code>false</code>, call
	 *         {@link #getReason()} for the unavailable reason.
	 */
	public boolean isAvailable() {
		return this.available;
	}

	/**
	 * Sets if the label associated with
	 * <code>EPPNameVerificationCheckResult</code> is availability to be
	 * created.
	 *
	 * @param aIsAvailable
	 *            Is the label available to be created?
	 */
	public void setIsAvailable(boolean aIsAvailable) {
		this.available = aIsAvailable;
	}

	/**
	 * Sets the OPTIONAL reason for the unavailable label.
	 *
	 * @param aReason
	 *            OPTIONAL reason value.
	 */
	public void setReason(String aReason) {
		this.reason = aReason;
	}

	/**
	 * Gets the reason for the unavailable label.
	 *
	 * @return String of domain reason.
	 */
	public String getReason() {
		return this.reason;
	}

	/**
	 * Sets language attribute of the reason.
	 *
	 * @param aLanguage
	 *            Sets the reason language attribute.
	 */
	public void setLanguage(String aLanguage) {
		this.language = aLanguage;
	}

	/**
	 * Gets the language attribute of the reason.
	 *
	 * @return The language of the reason
	 */
	public String getLanguage() {
		return this.language;
	}

	/**
	 * Is the label a restricted label?
	 * 
	 * @return <code>true</code> if the label is restricted; <code>false</code>
	 *         otherwise.
	 */
	public boolean isRestricted() {
		return this.restricted;
	}

	/**
	 * Sets if the label is a restricted label.
	 * 
	 * @param aRestricted
	 *            <code>true</code> if the label is restricted;
	 *            <code>false</code> otherwise.
	 * 
	 */
	public void setRestricted(boolean aRestricted) {
		this.restricted = aRestricted;
	}

	/**
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPNameVerificationCheckResult</code> instance.
	 *
	 * @param aDocument
	 *            DOM Document that is being built. Used as an Element factory.
	 *
	 * @return Element Root DOM Element representing the
	 *         <code>EPPNameVerificationCheckResult</code> instance.
	 *
	 * @exception EPPEncodeException
	 *                Unable to encode
	 *                <code>EPPNameVerificationCheckResult</code> instance.
	 */
	public Element encode(Document aDocument) throws EPPEncodeException {
		// Validate state
		if (label == null) {
			throw new EPPEncodeException("label required attribute is not set");
		}

		Element root = aDocument.createElementNS(
				EPPNameVerificationMapFactory.NS, ELM_NAME);

		// Label
		Element nameElm = aDocument.createElementNS(
				EPPNameVerificationMapFactory.NS,
				EPPNameVerificationMapFactory.NS_PREFIX + ":" + ELM_LABEL_NAME);
		root.appendChild(nameElm);
		Text textNode = aDocument.createTextNode(this.label);
		nameElm.appendChild(textNode);

		// Available
		EPPUtil.encodeBooleanAttr(nameElm, ATTR_AVAIL, this.available);

		// Restricted
		EPPUtil.encodeBooleanAttr(nameElm, ATTR_RESTRICTED, this.restricted);

		// Reason
		if (this.reason != null) {
			Element reasonElm = aDocument.createElementNS(
					EPPNameVerificationMapFactory.NS,
					EPPNameVerificationMapFactory.NS_PREFIX + ":" + ELM_REASON);
			root.appendChild(reasonElm);

			// Language
			if (!language.equals(VALUE_LANG)) {
				reasonElm.setAttribute(ATTR_LANG, language);
			}

			// Reason
			Text aReason = aDocument.createTextNode(reason);
			reasonElm.appendChild(aReason);
		}

		return root;
	}

	/**
	 * Decode the <code>EPPNameVerificationCheckResult</code> attributes from
	 * the aElement DOM Element tree.
	 *
	 * @param aElement
	 *            Root DOM Element to decode
	 *            <code>EPPNameVerificationCheckResult</code> from.
	 *
	 * @exception EPPDecodeException
	 *                Unable to decode aElement.
	 */
	public void decode(Element aElement) throws EPPDecodeException {

		// Label
		Element theNameElm = EPPUtil.getElementByTagNameNS(aElement,
				EPPNameVerificationMapFactory.NS, ELM_LABEL_NAME);
		this.label = theNameElm.getFirstChild().getNodeValue();

		// Available
		this.available = EPPUtil.decodeBooleanAttr(theNameElm, ATTR_AVAIL);

		// Restricted
		if (theNameElm.getAttribute(ATTR_RESTRICTED) != null) {
			this.restricted = EPPUtil.decodeBooleanAttr(theNameElm,
					ATTR_RESTRICTED);
		}
		else {
			this.restricted = false;
		}

		// Reason
		Element theReasonElm = EPPUtil.getElementByTagNameNS(aElement,
				EPPNameVerificationMapFactory.NS, ELM_REASON);

		if (theReasonElm != null) {
			this.reason = theReasonElm.getFirstChild().getNodeValue();

			String theLang = theReasonElm.getAttribute(ATTR_LANG);

			if (theLang.length() > 0) {
				if (!theLang.equals(VALUE_LANG)) {
					setLanguage(theLang);
				}
			}
		}
	}

	/**
	 * Compare an instance of <code>EPPNameVerificationCheckResult</code> with
	 * this instance.
	 *
	 * @param aObject
	 *            Object to compare with.
	 *
	 * @return <code>true</code> if equal; <code>false</code> otherwise.
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPNameVerificationCheckResult)) {
			return false;
		}

		EPPNameVerificationCheckResult other = (EPPNameVerificationCheckResult) aObject;

		// Label
		if (!EqualityUtil.equals(this.label, other.label)) {
			cat.error("EPPNameVerificationCheckResult.equals(): label not equal");
			return false;
		}

		// Available
		if (!EqualityUtil.equals(this.available, other.available)) {
			cat.error("EPPNameVerificationCheckResult.equals(): available not equal");
			return false;
		}

		// Restricted
		if (!EqualityUtil.equals(this.restricted, other.restricted)) {
			cat.error("EPPNameVerificationCheckResult.equals(): restricted not equal");
			return false;
		}

		// Reason
		if (!EqualityUtil.equals(this.reason, other.reason)) {
			cat.error("EPPNameVerificationCheckResult.equals(): reason not equal");
			return false;
		}

		// Language
		if (!EqualityUtil.equals(this.language, other.language)) {
			cat.error("EPPNameVerificationCheckResult.equals(): language not equal");
			return false;
		}

		return true;
	}

	/**
	 * Clone <code>EPPNameVerificationCheckResult</code>.
	 *
	 * @return clone of <code>EPPNameVerificationCheckResult</code>
	 *
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPNameVerificationCheckResult clone = null;

		clone = (EPPNameVerificationCheckResult) super.clone();

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
