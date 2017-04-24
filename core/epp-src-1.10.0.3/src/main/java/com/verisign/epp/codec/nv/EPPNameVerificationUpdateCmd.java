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
import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPUpdateCmd;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EqualityUtil;

/**
 * Update command used to change the authorization information for a Name
 * Verificaiton (NV) object.
 */
public class EPPNameVerificationUpdateCmd extends EPPUpdateCmd {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(
			EPPNameVerificationUpdateCmd.class.getName(), EPPCatFactory
					.getInstance().getFactory());

	/**
	 * XML local name for <code>EPPNameVerificationUpdateCmd</code>.
	 */
	public static final String ELM_LOCALNAME = "update";

	/**
	 * XML root tag for <code>EPPNameVerificationUpdateCmd</code>.
	 */
	public static final String ELM_NAME = EPPNameVerificationMapFactory.NS_PREFIX
			+ ":" + ELM_LOCALNAME;

	/** 
	 * XML Element Name for the <code>code</code> element. 
	 */
	private final static String ELM_CODE = "code";
	
	/**
	 * XML Element Name for the <code>chg</code> element.
	 */
	private final static String ELM_CHG = "chg";

	/**
	 * Verification code
	 */
	private String code;
	
	/**
	 * Authorization information.
	 */
	private EPPAuthInfo authInfo;

	/**
	 * <code>EPPNameVerificationUpdateCmd</code> default constructor.
	 */
	public EPPNameVerificationUpdateCmd() {
	}

	/**
	 * <code>EPPNameVerificationUpdateCmd</code> constructor that takes just the
	 * client transaction id. The authorization information must be set via
	 * {@link #setAuthInfo(EPPAuthInfo)}.
	 * 
	 * @param aTransId
	 *            Client transaction id. Set to <code>null</code> of no client
	 *            transaction id should be used.
	 */
	public EPPNameVerificationUpdateCmd(String aTransId) {
		super(aTransId);
	}

	/**
	 * <code>EPPNameVerificationUpdateCmd</code> constructor that takes the
	 * authorization information.
	 * 
	 * @param aTransId
	 *            Client transaction id. Set to <code>null</code> of no client
	 *            transaction id should be used.
	 * @param aCode
	 *            Verification code           
	 * @param aAuthInfo
	 *            Authorization info for the Name Verification (NV) object
	 */
	public EPPNameVerificationUpdateCmd(String aTransId, String aCode, EPPAuthInfo aAuthInfo) {
		super(aTransId);
		this.code = aCode;		
		this.setAuthInfo(aAuthInfo);
	}

	/**
	 * Gets the EPP command Namespace associated with
	 * <code>EPPNameVerificationUpdateCmd</code>.
	 * 
	 * @return <code>EPPNameVerificationMapFactory.NS</code>
	 */
	public String getNamespace() {
		return EPPNameVerificationMapFactory.NS;
	}

	/**
	 * Gets the verification code value.
	 * 
	 * @return The verification code value.
	 */
	public String getCode() {
		return this.code;
	}

	/**
	 * Sets the verification code value.
	 * 
	 * @param aCode
	 *            Verification code value
	 */
	public void setCode(String aCode) {
		this.code = aCode;
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
	 * <code>EPPNameVerificationUpdateCmd</code> instance.
	 * 
	 * @param aDocument
	 *            DOM Document that is being built. Used as an Element factory.
	 * 
	 * @return Element Root DOM Element representing the
	 *         <code>EPPNameVerificationUpdateCmd</code> instance.
	 * 
	 * @exception EPPEncodeException
	 *                Unable to encode <code>EPPNameVerificationUpdateCmd</code>
	 *                instance.
	 */
	protected Element doEncode(Document aDocument) throws EPPEncodeException {

		// Check required attributes
		if (this.code == null) {
			throw new EPPEncodeException(
					"Undefined code in EPPNameVerificationUpdateCmd");
		}
		if (this.authInfo == null) {
			throw new EPPEncodeException(
					"Undefined authInfo in EPPNameVerificationUpdateCmd");
		}

		// Create root element
		Element root = aDocument.createElementNS(
				EPPNameVerificationMapFactory.NS, ELM_NAME);

		// Code
		EPPUtil.encodeString(aDocument, root, this.code,
				EPPNameVerificationMapFactory.NS,
				EPPNameVerificationMapFactory.NS_PREFIX + ":" + ELM_CODE);
		
		// Change element
		Element chg = aDocument.createElementNS(
				EPPNameVerificationMapFactory.NS,
				EPPNameVerificationMapFactory.NS_PREFIX + ":" + ELM_CHG);
		root.appendChild(chg);

		// Auth Info
		EPPUtil.encodeComp(aDocument, chg, this.authInfo);

		return root;
	}

	/**
	 * Decode the <code>EPPNameVerificationUpdateCmd</code> attributes from the
	 * aElement DOM Element tree.
	 * 
	 * @param aElement
	 *            Root DOM Element to decode
	 *            <code>EPPNameVerificationUpdateCmd</code> from.
	 * 
	 * @exception EPPDecodeException
	 *                Unable to decode aElement
	 */
	protected void doDecode(Element aElement) throws EPPDecodeException {

		// Code
		this.code = EPPUtil.decodeString(aElement,
				EPPNameVerificationMapFactory.NS, ELM_CODE);
		
		Element theChgElm = EPPUtil.getElementByTagNameNS(aElement,
				EPPNameVerificationMapFactory.NS, ELM_CHG);

		if (theChgElm == null) {
			throw new EPPDecodeException(
					"EPPNameVerificationUpdateCmd.doDecode(): Couldn't find the chg element");
		}

		// Auth Info
		this.authInfo = (EPPAuthInfo) EPPUtil.decodeComp(theChgElm,
				EPPNameVerificationMapFactory.NS,
				EPPNameVerificationMapFactory.ELM_NV_AUTHINFO,
				EPPAuthInfo.class);
	}

	/**
	 * Compare an instance of <code>EPPNameVerificationUpdateCmd</code> with
	 * this instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return {@code true} if this object is the same as the aObject argument;
	 *         {@code false} otherwise.
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPNameVerificationUpdateCmd)) {
			return false;
		}

		if (!super.equals(aObject)) {
			return false;
		}

		EPPNameVerificationUpdateCmd other = (EPPNameVerificationUpdateCmd) aObject;

		// Code
		if (!EqualityUtil.equals(this.code, other.code)) {
			cat.error("EPPNameVerificationInfoCmd.equals(): code not equal");
			return false;
		}
	
		// Auth Info
		if (!EqualityUtil.equals(this.authInfo, other.authInfo)) {
			cat.error("EPPNameVerificationUpdateCmd.equals(): authInfo not equal");
			return false;
		}

		return true;
	}

	/**
	 * Clone <code>EPPNameVerificationUpdateCmd</code>.
	 * 
	 * @return Deep copy clone of <code>EPPNameVerificationUpdateCmd</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPNameVerificationUpdateCmd clone = (EPPNameVerificationUpdateCmd) super
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
