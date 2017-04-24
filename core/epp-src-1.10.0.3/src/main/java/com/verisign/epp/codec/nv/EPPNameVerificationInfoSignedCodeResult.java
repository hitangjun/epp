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
 * Info result for a command for the signed code, where the
 * {@link EPPNameVerificationInfoCmd} type is set to
 * <code>EPPNameVerificationInfoCmd.Type.SIGNED_CODE</code>.
 * 
 * @see com.verisign.epp.codec.nv.EPPNameVerificationInfoCmd
 */
public class EPPNameVerificationInfoSignedCodeResult implements
		EPPNameVerificationInfoResult {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(
			EPPNameVerificationInfoSignedCodeResult.class.getName(), EPPCatFactory
					.getInstance().getFactory());

	/**
	 * XML local name for <code>EPPNameVerificationInfoSignedCodeResult</code>.
	 */
	public static final String ELM_LOCALNAME = "signedCode";

	/**
	 * XML root tag for <code>EPPNameVerificationInfoSignedCodeResult</code>.
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
	 * XML Element Name for the <code>name</code> element.
	 */
	private final static String ELM_SIGNED_CODE = "encodedSignedCode";

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
	 * OPTIONAL authorization information.
	 */
	private EPPAuthInfo authInfo;

	/**
	 * Created signed code
	 */
	private EPPEncodedSignedCodeValue signedCode;

	/**
	 * <code>EPPNameVerificationInfoSignedCodeResult</code> default constructor.
	 */
	public EPPNameVerificationInfoSignedCodeResult() {
	}

	/**
	 * <code>EPPNameVerificationInfoSignedCodeResult</code> constructor that takes all
	 * required attributes.
	 * 
	 * @param aCode
	 *            The verification code value
	 * @param aType
	 *            The verification code type
	 * @param aSignedCode
	 *            Signed code providing proof of verification
	 */
	public EPPNameVerificationInfoSignedCodeResult(String aCode, String aType,
			EPPEncodedSignedCodeValue aSignedCode) {
		this.code = aCode;
		this.type = aType;
		this.signedCode = aSignedCode;
	}

	/**
	 * <code>EPPNameVerificationInfoSignedCodeResult</code> constructor that takes all
	 * attributes.
	 * 
	 * @param aCode
	 *            The verification code value
	 * @param aType
	 *            The verification code type
	 * @param aSignedCode
	 *            Signed code providing proof of verification
	 * @param aStatus
	 *            The verification status
	 * @param aAuthInfo
	 *            Authorization information for Name Verification (NV) object.
	 */
	public EPPNameVerificationInfoSignedCodeResult(String aCode, String aType,
			EPPEncodedSignedCodeValue aSignedCode,
			EPPNameVerificationStatus aStatus, EPPAuthInfo aAuthInfo) {
		this.code = aCode;
		this.type = aType;
		this.signedCode = aSignedCode;
		this.status = aStatus;
		this.setAuthInfo(aAuthInfo);
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
	 * Gets the verification code type.
	 * 
	 * @return Verification code type
	 */
	public String getCodeType() {
		return this.type;
	}

	/**
	 * Sets the verification code type.
	 * 
	 * @param aType
	 *            The verification code type
	 */
	public void setCodeType(String aType) {
		this.type = aType;
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
	 * Gets the OPTIONAL authorization information for the Name Verification
	 * (NV) object.
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
	 *            Authorization information of NV object.
	 */
	public void setAuthInfo(EPPAuthInfo aAuthInfo) {
		if (aAuthInfo != null) {
			this.authInfo = aAuthInfo;
			this.authInfo.setRootName(EPPNameVerificationMapFactory.NS,
					EPPNameVerificationMapFactory.ELM_NV_AUTHINFO);
		}
	}

	/**
	 * Gets the signed code that provides proof of verification.
	 * 
	 * @return Signed code
	 */
	public EPPEncodedSignedCodeValue getSignedCode() {
		return this.signedCode;
	}

	/**
	 * Sets the signed code that provides proof of verification.
	 * 
	 * @param aSignedCode
	 *            Signed code that provides proof of verification
	 */
	public void setSignedCode(EPPEncodedSignedCodeValue aSignedCode) {
		this.signedCode = aSignedCode;
	}

	/**
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPNameVerificationInfoSignedCodeResult</code> instance.
	 * 
	 * @param aDocument
	 *            DOM Document that is being built. Used as an Element factory.
	 * 
	 * @return Element Root DOM Element representing the
	 *         EPPNameVerificationInfoSignedCodeResult instance.
	 * 
	 * @exception EPPEncodeException
	 *                Unable to encode EPPNameVerificationInfoSignedCodeResult
	 *                instance.
	 */
	@Override
	public Element encode(Document aDocument) throws EPPEncodeException {

		// Check required attributes
		if (this.code == null) {
			throw new EPPEncodeException(
					"Undefined code in EPPNameVerificationInfoSignedCodeResult");
		}
		if (this.type == null) {
			throw new EPPEncodeException(
					"Undefined type in EPPNameVerificationInfoSignedCodeResult");
		}
		if (this.signedCode == null) {
			throw new EPPEncodeException(
					"Undefined signedCode in EPPNameVerificationInfoSignedCodeResult");
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
		if (this.status != null) {
			Element theStatusElm = aDocument.createElementNS(
					EPPNameVerificationMapFactory.NS,
					EPPNameVerificationMapFactory.NS_PREFIX + ":" + ELM_STATUS);
			root.appendChild(theStatusElm);
			theStatusElm.setAttribute(ATTR_STATUS, this.status.toString());
		}

		// Auth Info
		if (this.authInfo != null) {
			EPPUtil.encodeComp(aDocument, root, this.authInfo);
		}

		// Signed Code
		EPPUtil.encodeString(aDocument, root, this.signedCode.encodeValue(),
				EPPNameVerificationMapFactory.NS,
				EPPNameVerificationMapFactory.NS_PREFIX + ":" + ELM_SIGNED_CODE);

		return root;
	}

	/**
	 * Decode the <code>EPPNameVerificationInfoSignedCodeResult</code> attributes from
	 * the aElement DOM Element tree.
	 * 
	 * @param aElement
	 *            Root DOM Element to decode
	 *            <code>EPPNameVerificationInfoSignedCodeResult</code> from.
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
		if (theElm != null) {
			String theStatusStr = theElm.getAttribute(ATTR_STATUS);
			this.status = EPPNameVerificationStatus.getStatus(theStatusStr);			
		}

		// Auth Info
		this.authInfo = (EPPAuthInfo) EPPUtil.decodeComp(aElement,
				EPPNameVerificationMapFactory.NS,
				EPPNameVerificationMapFactory.ELM_NV_AUTHINFO,
				EPPAuthInfo.class);

		// Build XML encoded signed code
		String encodedSignedCodeXML = "<verificationCode:code xmlns:verificationCode=\"urn:ietf:params:xml:ns:verificationCode-1.0\">"
				+ EPPUtil.decodeString(aElement,
						EPPNameVerificationMapFactory.NS, ELM_SIGNED_CODE)
				+ "</verificationCode:code>";

		this.signedCode = new EPPEncodedSignedCodeValue(
				encodedSignedCodeXML.getBytes());
	}

	/**
	 * Clone <code>EPPNameVerificationInfoSignedCodeResult</code>.
	 * 
	 * @return clone of <code>EPPNameVerificationInfoSignedCodeResult</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPNameVerificationInfoSignedCodeResult clone = (EPPNameVerificationInfoSignedCodeResult) super
				.clone();

		// Code
		clone.code = this.code;

		// Type
		clone.type = this.type;

		// Status
		clone.status = this.status;

		// Auth Info
		if (this.authInfo != null) {
			clone.authInfo = (EPPAuthInfo) this.authInfo.clone();
		}
		else {
			clone.authInfo = null;
		}

		// Signed Code
		if (this.signedCode != null) {
			clone.signedCode = (EPPEncodedSignedCodeValue) this.signedCode
					.clone();
		}
		else {
			clone.signedCode = null;
		}

		return clone;
	}

	/**
	 * Gets the EPP response type associated with
	 * <code>EPPNameVerificationInfoSignedCodeResult</code>.
	 * 
	 * @return <code>EPPNameVerificationInfoSignedCodeResult.ELM_NAME</code>
	 */
	public String getType() {
		return ELM_NAME;
	}

	/**
	 * Gets the EPP command namespace associated with
	 * <code>EPPNameVerificationInfoSignedCodeResult</code>.
	 * 
	 * @return <code>EPPNameVerificationMapFactory.NS</code>
	 */
	public String getNamespace() {
		return EPPNameVerificationMapFactory.NS;
	}

	/**
	 * Compare an instance of <code>EPPNameVerificationInfoSignedCodeResult</code>
	 * with this instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return {@code true} if this object is the same as the aObject argument;
	 *         {@code false} otherwise
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPNameVerificationInfoSignedCodeResult)) {
			return false;
		}

		EPPNameVerificationInfoSignedCodeResult other = (EPPNameVerificationInfoSignedCodeResult) aObject;

		// Code
		if (!EqualityUtil.equals(this.code, other.code)) {
			cat.error("EPPNameVerificationInfoSignedCodeResult.equals(): code not equal");
			return false;
		}

		// Type
		if (!EqualityUtil.equals(this.type, other.type)) {
			cat.error("EPPNameVerificationInfoSignedCodeResult.equals(): type not equal");
			return false;
		}

		// Status
		if (!EqualityUtil.equals(this.status, other.status)) {
			cat.error("EPPNameVerificationInfoSignedCodeResult.equals(): status not equal");
			return false;
		}

		// Auth Info
		if (!EqualityUtil.equals(this.authInfo, other.authInfo)) {
			cat.error("EPPNameVerificationInfoSignedCodeResult.equals(): authInfo not equal");
			return false;
		}

		// Signed Code
		if (!EqualityUtil.equals(this.signedCode, other.signedCode)) {
			cat.error("EPPNameVerificationInfoSignedCodeResult.equals(): signedCode not equal");
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