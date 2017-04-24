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

import com.verisign.epp.codec.gen.EPPCodecComponent;
import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EqualityUtil;

/**
 * Represents a Domain Name Verification (DNV) used in
 * {@link EPPNameVerificationCreateCmd} and the
 * {@link EPPNameVerificationInfoResp}.
 * 
 * @see com.verisign.epp.codec.nv.EPPNameVerificationCreateCmd
 * @see com.verisign.epp.codec.nv.EPPNameVerificationInfoResp
 */
public class EPPDomainNameVerification implements EPPCodecComponent {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(
			EPPDomainNameVerification.class.getName(), EPPCatFactory
					.getInstance().getFactory());

	/**
	 * XML local name for <code>EPPDomainNameVerification</code>.
	 */
	public static final String ELM_LOCALNAME = "dnv";

	/**
	 * XML root tag for <code>EPPDomainNameVerification</code>.
	 */
	public static final String ELM_NAME = EPPNameVerificationMapFactory.NS_PREFIX
			+ ":" + ELM_LOCALNAME;

	/**
	 * XML Element Name for the <code>name</code> element.
	 */
	private final static String ELM_LABEL_NAME = "name";

	/**
	 * XML Element Name for the <code>rnvCode</code> element.
	 */
	private final static String ELM_RNV_CODE = "rnvCode";

	/**
	 * Domain label
	 */
	private String name;

	/**
	 * OPTIONAL Real Name Verification code used for verification of a
	 * Restricted Name.
	 */
	private String rnvCode;

	/**
	 * <code>EPPDomainNameVerification</code> default constructor.
	 */
	public EPPDomainNameVerification() {
	}

	/**
	 * <code>EPPDomainNameVerification</code> constructor that takes the
	 * required domain label.
	 * 
	 * @param aName
	 *            Domain label to verify
	 */
	public EPPDomainNameVerification(String aName) {
		this.name = aName;
	}

	/**
	 * <code>EPPDomainNameVerification</code> constructor that takes the
	 * required domain label and the optional Real Name Verification Code Token
	 * value for verification of a Restricted Name.
	 * 
	 * @param aName
	 *            Domain label to verify
	 * @param aRnvCode
	 *            OPTIONAL Real Name Verification Code Token value. Set to
	 *            <code>null</code> if undefined.
	 */
	public EPPDomainNameVerification(String aName, String aRnvCode) {
		this.name = aName;
		this.rnvCode = aRnvCode;
	}

	/**
	 * Gets the domain label for verification
	 * 
	 * @return The domain label for verification
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the domain label for verification.
	 * 
	 * @param aName
	 *            Domain label for verification
	 */
	public void setName(String aName) {
		this.name = aName;
	}

	/**
	 * Has the Real Name Verification Code Token value been set?
	 * 
	 * @return <code>true</code> if the code has been set; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasRnvCode() {
		return (this.rnvCode != null ? true : false);
	}

	/**
	 * Gets the Real Name Verification Code Token value.
	 * 
	 * @return Real Name Verification Code Token value if defined;
	 *         <code>null</code> otherwise.
	 */
	public String getRnvCode() {
		return this.rnvCode;
	}

	/**
	 * Sets the Real Name Verification Code Token value.
	 * 
	 * @param aRnvCode
	 *            Real Name Verification Code Token value. Set to
	 *            <code>null</code> if undefined.
	 */
	public void setRnvCode(String aRnvCode) {
		this.rnvCode = aRnvCode;
	}

	/**
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPDomainNameVerification</code> instance.
	 * 
	 * @param aDocument
	 *            DOM Document that is being built. Used as an Element factory.
	 * 
	 * @return Element Root DOM Element representing the
	 *         <code>EPPDomainNameVerification</code> instance.
	 * 
	 * @exception EPPEncodeException
	 *                Unable to encode <code>EPPDomainNameVerification</code>
	 *                instance.
	 */
	@Override
	public Element encode(Document aDocument) throws EPPEncodeException {

		// Check required attributes
		if (this.name == null) {
			throw new EPPEncodeException(
					"Undefined name in EPPDomainNameVerification");
		}

		// Create root element
		Element root = aDocument.createElementNS(
				EPPNameVerificationMapFactory.NS, ELM_NAME);

		// Name
		EPPUtil.encodeString(aDocument, root, this.name,
				EPPNameVerificationMapFactory.NS,
				EPPNameVerificationMapFactory.NS_PREFIX + ":" + ELM_LABEL_NAME);

		// RnvCode
		if (this.hasRnvCode()) {
			EPPUtil.encodeString(aDocument, root, this.rnvCode,
					EPPNameVerificationMapFactory.NS,
					EPPNameVerificationMapFactory.NS_PREFIX + ":"
							+ ELM_RNV_CODE);
		}

		return root;
	}

	/**
	 * Decode the <code>EPPDomainNameVerification</code> attributes from the
	 * aElement DOM Element tree.
	 * 
	 * @param aElement
	 *            Root DOM Element to decode
	 *            <code>EPPDomainNameVerification</code> from.
	 * 
	 * @exception EPPDecodeException
	 *                Unable to decode aElement
	 */
	@Override
	public void decode(Element aElement) throws EPPDecodeException {

		// Name
		this.name = EPPUtil.decodeString(aElement,
				EPPNameVerificationMapFactory.NS, ELM_LABEL_NAME);

		// RnvCode
		// this.rnvCode = EPPUtil.decodeString(aElement,
		// EPPNameVerificationMapFactory.NS, ELM_RNV_CODE);
	}

	/**
	 * Compare an instance of <code>EPPDomainNameVerification</code> with this
	 * instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return {@code true} if this object is the same as the aObject argument;
	 *         {@code false} otherwise.
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPDomainNameVerification)) {
			cat.error("EPPDomainNameVerification.equals(): object "
					+ aObject.getClass().getName()
					+ "!= EPPDomainNameVerification");
			return false;
		}

		EPPDomainNameVerification other = (EPPDomainNameVerification) aObject;

		// Name
		if (!EqualityUtil.equals(this.name, other.name)) {
			cat.error("EPPDomainNameVerification.equals(): name not equal");
			return false;
		}

		// RnvCode
		// if (!EqualityUtil.equals(this.rnvCode, other.rnvCode)) {
		// cat.error("EPPDomainNameVerification.equals(): rnvCode not equal");
		// return false;
		// }

		return true;
	}

	/**
	 * Clone <code>EPPDomainNameVerification</code>.
	 * 
	 * @return Deep copy clone of <code>EPPDomainNameVerification</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPDomainNameVerification clone = (EPPDomainNameVerification) super
				.clone();

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
