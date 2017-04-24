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

import com.verisign.epp.codec.gen.EPPAuthInfo;
import com.verisign.epp.codec.gen.EPPCreateCmd;
import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EqualityUtil;

/**
 * Create command to implement the domain name verification and to generate a
 * domain verification code that is contained in the response.
 * 
 * @see com.verisign.epp.codec.nv.EPPNameVerificationCreateResp
 */
public class EPPNameVerificationCreateCmd extends EPPCreateCmd {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(
			EPPNameVerificationCreateCmd.class.getName(), EPPCatFactory
					.getInstance().getFactory());

	/**
	 * XML local name for <code>EPPNameVerificationCreateCmd</code>.
	 */
	public static final String ELM_LOCALNAME = "create";

	/**
	 * XML root tag for <code>EPPNameVerificationCreateCmd</code>.
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
	 * <code>EPPNameVerificationCreateCmd</code> default constructor.
	 */
	public EPPNameVerificationCreateCmd() {
	}

	/**
	 * <code>EPPNameVerificationCreateCmd</code> constructor that takes just the
	 * client transaction id. The requirement attributes must be set using the
	 * setter methods.
	 */
	public EPPNameVerificationCreateCmd(String aTransId) {
		super(aTransId);
	}

	/**
	 * <code>EPPNameVerificationCreateCmd</code> constructor for a Domain Name
	 * Verification (DNV) with the required attributes.
	 * 
	 * @param aTransId
	 *            Client transaction id. Set to <code>null</code> of no client
	 *            transaction id should be used.
	 * @param aDnv
	 *            Domain Name Verification (DNV) object to verify and create
	 * @param aAuthInfo
	 *            Authorization info for the DNV object
	 */
	public EPPNameVerificationCreateCmd(String aTransId,
			EPPDomainNameVerification aDnv, EPPAuthInfo aAuthInfo) {
		super(aTransId);
		this.dnv = aDnv;
		this.setAuthInfo(aAuthInfo);
	}

	/**
	 * <code>EPPNameVerificationCreateCmd</code> constructor for a Real Name
	 * Verification (RNV) with the required attributes.
	 * 
	 * @param aTransId
	 *            Client transaction id. Set to <code>null</code> of no client
	 *            transaction id should be used.
	 * @param aRnv
	 *            Real Name Verification (RNV) object to verify and create
	 * @param aAuthInfo
	 *            Authorization info for the RNV object
	 */
	public EPPNameVerificationCreateCmd(String aTransId,
			EPPRealNameVerification aRnv, EPPAuthInfo aAuthInfo) {
		super(aTransId);
		this.rnv = aRnv;
		this.setAuthInfo(aAuthInfo);
	}

	/**
	 * Gets the EPP command Namespace associated with
	 * <code>EPPNameVerificationCreateCmd</code>.
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
	 * Gets the authorization information for the Name Verification (NV) object.
	 * 
	 * @return Authorization information if defined; <code>null</code> otherwise.
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
	 * <code>EPPNameVerificationCreateCmd</code> instance.
	 * 
	 * @param aDocument
	 *            DOM Document that is being built. Used as an Element factory.
	 * 
	 * @return Element Root DOM Element representing the
	 *         <code>EPPNameVerificationCreateCmd</code> instance.
	 * 
	 * @exception EPPEncodeException
	 *                Unable to encode <code>EPPNameVerificationCreateCmd</code>
	 *                instance.
	 */
	protected Element doEncode(Document aDocument) throws EPPEncodeException {

		// Check required attributes
		if (this.dnv != null && this.rnv != null) {
			throw new EPPEncodeException(
					"Both DNV and RNV cannot be set in EPPNameVerificationCreateCmd");
		}
		if (this.dnv == null && this.rnv == null) {
			throw new EPPEncodeException(
					"Either DNV and RNV must be set in EPPNameVerificationCreateCmd");
		}
		if (this.authInfo == null) {
			throw new EPPEncodeException(
					"Undefined authInfo in EPPNameVerificationCreateCmd");
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
		EPPUtil.encodeComp(aDocument, root, this.authInfo);

		return root;
	}

	/**
	 * Decode the <code>EPPNameVerificationCreateCmd</code> attributes from the
	 * aElement DOM Element tree.
	 * 
	 * @param aElement
	 *            Root DOM Element to decode
	 *            <code>EPPNameVerificationCreateCmd</code> from.
	 * 
	 * @exception EPPDecodeException
	 *                Unable to decode aElement
	 */
	protected void doDecode(Element aElement) throws EPPDecodeException {

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
	 * Compare an instance of <code>EPPNameVerificationCreateCmd</code> with
	 * this instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return {@code true} if this object is the same as the aObject argument;
	 *         {@code false} otherwise.
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPNameVerificationCreateCmd)) {
			return false;
		}

		if (!super.equals(aObject)) {
			return false;
		}

		EPPNameVerificationCreateCmd other = (EPPNameVerificationCreateCmd) aObject;

		// DNV
		if (!EqualityUtil.equals(this.dnv, other.dnv)) {
			cat.error("EPPNameVerificationCreateCmd.equals(): dnv not equal");
			return false;
		}

		// RNV
		if (!EqualityUtil.equals(this.rnv, other.rnv)) {
			cat.error("EPPNameVerificationCreateCmd.equals(): rnv not equal");
			return false;
		}

		// Auth Info
		if (!EqualityUtil.equals(this.authInfo, other.authInfo)) {
			cat.error("EPPNameVerificationCreateCmd.equals(): authInfo not equal");
			return false;
		}

		return true;
	}

	/**
	 * Clone <code>EPPNameVerificationCreateCmd</code>.
	 * 
	 * @return Deep copy clone of <code>EPPNameVerificationCreateCmd</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPNameVerificationCreateCmd clone = (EPPNameVerificationCreateCmd) super
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
