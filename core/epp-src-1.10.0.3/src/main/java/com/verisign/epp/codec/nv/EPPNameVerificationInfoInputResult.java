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

import com.verisign.epp.codec.gen.EPPAuthInfo;
import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.codec.verificationcode.EPPEncodedSignedCodeValue;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EqualityUtil;

/**
 * Info result for a command for the input, where the
 * {@link EPPNameVerificationInfoCmd} type is set to
 * <code>EPPNameVerificationInfoCmd.Type.INPUT</code>.
 * 
 * @see com.verisign.epp.codec.nv.EPPNameVerificationInfoCmd
 */
public class EPPNameVerificationInfoInputResult implements
		EPPNameVerificationInfoResult {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(
			EPPNameVerificationInfoInputResult.class.getName(), EPPCatFactory
					.getInstance().getFactory());

	/**
	 * XML local name for <code>EPPNameVerificationInfoInputResult</code>.
	 */
	public static final String ELM_LOCALNAME = "input";

	/**
	 * XML root tag for <code>EPPNameVerificationInfoInputResult</code>.
	 */
	public static final String ELM_NAME = EPPNameVerificationMapFactory.NS_PREFIX
			+ ":" + ELM_LOCALNAME;

	/**
	 * Domain Name Verification (DNV) to execute.
	 */
	private EPPDomainNameVerification dnv;

	/**
	 * Real Name Verification (RNV) to execute.
	 */
	private EPPRealNameVerification rnv;

	/**
	 * Authorization information.
	 */
	private EPPAuthInfo authInfo;

	/**
	 * <code>EPPNameVerificationInfoInputResult</code> default constructor.
	 */
	public EPPNameVerificationInfoInputResult() {
	}

	/**
	 * <code>EPPNameVerificationInfoInputResult</code> constructor for a Domain
	 * Name Verification (DNV).
	 * 
	 * @param aDnv
	 *            Domain Name Verification (DNV) object to verify and create
	 */
	public EPPNameVerificationInfoInputResult(EPPDomainNameVerification aDnv) {
		this.dnv = aDnv;
	}

	/**
	 * <code>EPPNameVerificationInfoInputResult</code> constructor for a Domain
	 * Name Verification (DNV) with all attributes.
	 * 
	 * @param aDnv
	 *            Domain Name Verification (DNV) object to verify and create
	 * @param aAuthInfo
	 *            Authorization info for the DNV object
	 */
	public EPPNameVerificationInfoInputResult(EPPDomainNameVerification aDnv,
			EPPAuthInfo aAuthInfo) {
		this.dnv = aDnv;
		this.setAuthInfo(aAuthInfo);
	}

	/**
	 * <code>EPPNameVerificationInfoInputResult</code> constructor for a Real
	 * Name Verification (RNV).
	 * 
	 * @param aRnv
	 *            Real Name Verification (RNV) object to verify and create
	 */
	public EPPNameVerificationInfoInputResult(EPPRealNameVerification aRnv) {
		this.rnv = aRnv;
	}

	/**
	 * <code>EPPNameVerificationInfoInputResult</code> constructor for a Real
	 * Name Verification (RNV) with all attributes.
	 * 
	 * @param aRnv
	 *            Real Name Verification (RNV) object to verify and create
	 * @param aAuthInfo
	 *            Authorization info for the RNV object
	 */
	public EPPNameVerificationInfoInputResult(EPPRealNameVerification aRnv,
			EPPAuthInfo aAuthInfo) {
		this.rnv = aRnv;
		this.setAuthInfo(aAuthInfo);
	}

	/**
	 * Gets the EPP command Namespace associated with
	 * <code>EPPNameVerificationInfoInputResult</code>.
	 * 
	 * @return <code>EPPNameVerificationMapFactory.NS</code>
	 */
	public String getNamespace() {
		return EPPNameVerificationMapFactory.NS;
	}

	/**
	 * Has the Domain Name Verification (DNV) object information been set?
	 * 
	 * @return <code>true</code> if the Domain Name Verification (DNV) object
	 *         information has been set; <code>false</code> otherwise.
	 */
	public boolean hasDnv() {
		return (this.dnv != null ? true : false);
	}

	/**
	 * Gets the Domain Name Verification (DNV) object to verify and create.
	 * 
	 * @return The Domain Name Verification (DNV) object if defined;
	 *         <code>null</code> otherwise.
	 */
	public EPPDomainNameVerification getDnv() {
		return this.dnv;
	}

	/**
	 * Sets the Domain Name Verification (DNV) object information to verify and
	 * create.
	 * 
	 * @param aDnv
	 *            The Domain Name Verification (DNV) object or <code>null</code>
	 *            to undefined it.
	 */
	public void setDnv(EPPDomainNameVerification aDnv) {
		this.dnv = aDnv;
	}

	/**
	 * Has the Real Name Verification (RNV) object information been set?
	 * 
	 * @return <code>true</code> if the Real Name Verification (RNV) object
	 *         information has been set; <code>false</code> otherwise.
	 */
	public boolean hasRnv() {
		return (this.rnv != null ? true : false);
	}

	/**
	 * Gets the Real Name Verification (RNV) object information to verify and
	 * create.
	 * 
	 * @return The Real Name Verification (RNV) object if defined;
	 *         <code>null</code> otherwise.
	 */
	public EPPRealNameVerification getRnv() {
		return this.rnv;
	}

	/**
	 * Sets the Real Name Verification (RNV) object information to verify and
	 * create.
	 * 
	 * @param aRnv
	 *            The Real Name Verification (RNV) object or <code>null</code>
	 *            to undefined it.
	 */
	public void setRnv(EPPRealNameVerification aRnv) {
		this.rnv = aRnv;
	}

	/**
	 * Has the authorization information for the Name Verification (NV) object
	 * been set?
	 * 
	 * @return <code>true</code> if the authorization information for the Name
	 *         Verification (NV) object has been set; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasAuthInfo() {
		return (this.authInfo != null ? true : false);
	}

	/**
	 * Gets the authorization information for the Name Verification (NV) object.
	 * 
	 * @return Authorization information if defined; <code>null</code>
	 *         otherwise.
	 */
	public EPPAuthInfo getAuthInfo() {
		return this.authInfo;
	}

	/**
	 * Sets the authorization information for the Name Verification (NV) object.
	 *
	 * @param aAuthInfo
	 *            Authorization information of NV object
	 */
	public void setAuthInfo(EPPAuthInfo aAuthInfo) {
		if (aAuthInfo != null) {
			this.authInfo = aAuthInfo;
			this.authInfo.setRootName(EPPNameVerificationMapFactory.NS,
					EPPNameVerificationMapFactory.ELM_NV_AUTHINFO);
		}
	}

	/**
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPNameVerificationInfoInputResult</code> instance.
	 * 
	 * @param aDocument
	 *            DOM Document that is being built. Used as an Element factory.
	 * 
	 * @return Element Root DOM Element representing the
	 *         <code>EPPNameVerificationInfoInputResult</code> instance.
	 * 
	 * @exception EPPEncodeException
	 *                Unable to encode
	 *                <code>EPPNameVerificationInfoInputResult</code> instance.
	 */
	@Override
	public Element encode(Document aDocument) throws EPPEncodeException {

		// Check required attributes
		if (this.dnv != null && this.rnv != null) {
			throw new EPPEncodeException(
					"Both DNV and RNV cannot be set in EPPNameVerificationInfoInputResult");
		}
		if (this.dnv == null && this.rnv == null) {
			throw new EPPEncodeException(
					"Either DNV and RNV must be set in EPPNameVerificationInfoInputResult");
		}

		// Create root element
		Element root = aDocument.createElementNS(
				EPPNameVerificationMapFactory.NS, ELM_NAME);

		// DNV
		if (this.hasDnv()) {
			EPPUtil.encodeComp(aDocument, root, this.dnv);
		}

		// RNV
		if (this.hasRnv()) {
			EPPUtil.encodeComp(aDocument, root, this.rnv);
		}

		// Auth Info
		if (this.authInfo != null) {
			EPPUtil.encodeComp(aDocument, root, this.authInfo);
		}

		return root;
	}

	/**
	 * Decode the <code>EPPNameVerificationInfoInputResult</code> attributes
	 * from the aElement DOM Element tree.
	 * 
	 * @param aElement
	 *            Root DOM Element to decode
	 *            <code>EPPNameVerificationInfoInputResult</code> from.
	 * 
	 * @exception EPPDecodeException
	 *                Unable to decode aElement
	 */
	@Override
	public void decode(Element aElement) throws EPPDecodeException {

		// DNV
		this.dnv = (EPPDomainNameVerification) EPPUtil.decodeComp(aElement,
				EPPNameVerificationMapFactory.NS,
				EPPDomainNameVerification.ELM_NAME,
				EPPDomainNameVerification.class);

		// RNV
		this.rnv = (EPPRealNameVerification) EPPUtil
				.decodeComp(aElement, EPPNameVerificationMapFactory.NS,
						EPPRealNameVerification.ELM_NAME,
						EPPRealNameVerification.class);

		// Auth Info
		this.authInfo = (EPPAuthInfo) EPPUtil.decodeComp(aElement,
				EPPNameVerificationMapFactory.NS,
				EPPNameVerificationMapFactory.ELM_NV_AUTHINFO,
				EPPAuthInfo.class);
	}

	/**
	 * Compare an instance of <code>EPPNameVerificationInfoInputResult</code>
	 * with this instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return {@code true} if this object is the same as the aObject argument;
	 *         {@code false} otherwise.
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPNameVerificationInfoInputResult)) {
			cat.error("EPPNameVerificationInfoInputResult.equals(): object "
					+ aObject.getClass().getName()
					+ "!= EPPNameVerificationInfoInputResult");
			return false;
		}

		EPPNameVerificationInfoInputResult other = (EPPNameVerificationInfoInputResult) aObject;

		// DNV
		if (!EqualityUtil.equals(this.dnv, other.dnv)) {
			cat.error("EPPNameVerificationInfoInputResult.equals(): dnv not equal");
			return false;
		}

		// RNV
		if (!EqualityUtil.equals(this.rnv, other.rnv)) {
			cat.error("EPPNameVerificationInfoInputResult.equals(): rnv not equal");
			return false;
		}

		// Auth Info
		if (!EqualityUtil.equals(this.authInfo, other.authInfo)) {
			cat.error("EPPNameVerificationInfoInputResult.equals(): authInfo not equal");
			return false;
		}

		return true;
	}

	/**
	 * Clone <code>EPPNameVerificationInfoInputResult</code>.
	 * 
	 * @return Deep copy clone of
	 *         <code>EPPNameVerificationInfoInputResult</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPNameVerificationInfoInputResult clone = (EPPNameVerificationInfoInputResult) super
				.clone();

		if (this.dnv != null) {
			clone.dnv = (EPPDomainNameVerification) this.dnv.clone();
		}

		if (this.rnv != null) {
			clone.rnv = (EPPRealNameVerification) this.rnv.clone();
		}

		if (this.authInfo != null) {
			clone.authInfo = (EPPAuthInfo) this.authInfo.clone();
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