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

import java.security.InvalidParameterException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.verisign.epp.codec.gen.EPPAuthInfo;
import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPInfoCmd;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EqualityUtil;

/**
 * Info command to get the information associated with the verification code,
 * which includes either the signed code associated with the verification code
 * or the input parameters passed for the domain verification.
 * 
 * @see com.verisign.epp.codec.nv.EPPNameVerificationInfoResp
 */
public class EPPNameVerificationInfoCmd extends EPPInfoCmd {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(
			EPPNameVerificationInfoCmd.class.getName(), EPPCatFactory
					.getInstance().getFactory());

	/**
	 * XML local name for <code>EPPNameVerificationInfoCmd</code>.
	 */
	public static final String ELM_LOCALNAME = "info";

	/**
	 * XML root tag for <code>EPPNameVerificationInfoCmd</code>.
	 */
	public static final String ELM_NAME = EPPNameVerificationMapFactory.NS_PREFIX
			+ ":" + ELM_LOCALNAME;

	/** 
	 * XML Element Name for the <code>code</code> element. 
	 */
	private final static String ELM_CODE = "code";

	/**
	 * The type attribute name
	 */
	private static final String ATTR_TYPE = "type";

	/**
	 * Verification code
	 */
	private String code;

	/**
	 * Info type with a default of <code>Type.SIGNED_CODE</code>.
	 */
	private Type type = Type.SIGNED_CODE;

	/**
	 * OPTIONAL authorization information.
	 */
	private EPPAuthInfo authInfo;

	/**
	 * Information type with one of the following values:<br>
	 * <ul>
	 * <li><code>INPUT</code> - Get the input parameters provided in executing
	 * the verification associated with the code.</li>
	 * <li><code>SIGNED_CODE</code> - Get the signed code associated with the
	 * code.</li>
	 */
	public enum Type {
		INPUT("input"), SIGNED_CODE("signedCode");

		private final String typeStr;

		/**
		 * Define the string value for the enumerated value.
		 * 
		 * @param aTypeStr
		 *            Enumerated value string
		 */
		Type(String aTypeStr) {
			this.typeStr = aTypeStr;
		}

		/**
		 * Get the type enumerated value given the matching string.
		 * 
		 * @param aType
		 *            <code>Type</code> enumerated string to convert to an
		 *            enumerated <code>Type</code> instance.
		 * 
		 * @return Enumerated <code>Type</code> value matching the
		 *         <code>String</code>.
		 * 
		 * @throws InvalidParameterException
		 *             If <code>aString</code> does not match an enumerated
		 *             <code>Type</code> string value.
		 */
		public static Type getType(String aType) {
			if (aType == null || aType.isEmpty()) {
				// return the default value of signedCode.
				return SIGNED_CODE;
			}
			if (aType.equals(INPUT.typeStr)) {
				return INPUT;
			}
			else if (aType.equals(SIGNED_CODE.typeStr)) {
				return SIGNED_CODE;
			}
			else {
				throw new InvalidParameterException("Type enum value of "
						+ aType + " is not valid.");
			}
		}

		/**
		 * Convert the enumerated <code>Type</code> value to a
		 * <code>String</code>.
		 */
		public String toString() {
			return this.typeStr;
		}
	}

	/**
	 * <code>EPPNameVerificationInfoCmd</code> default constructor.
	 */
	public EPPNameVerificationInfoCmd() {
	}

	/**
	 * <code>EPPNameVerificationInfoCmd</code> constructor that takes just the
	 * client transaction id.
	 * 
	 * @param aTransId
	 *            Client transaction id. Set to <code>null</code> of no client
	 *            transaction id should be used.
	 */
	public EPPNameVerificationInfoCmd(String aTransId) {
		super(aTransId);
	}

	/**
	 * <code>EPPNameVerificationInfoCmd</code> constructor that takes the
	 * required verification code value. The default info type is set to
	 * <code>Type.SIGNED_CODE</code>.
	 * 
	 * @param aTransId
	 *            Client transaction id. Set to <code>null</code> of no client
	 *            transaction id should be used.
	 * @param aCode
	 *            Verification code
	 */
	public EPPNameVerificationInfoCmd(String aTransId, String aCode) {
		super(aTransId);
		this.code = aCode;
	}

	/**
	 * <code>EPPNameVerificationInfoCmd</code> constructor that takes all of the 
	 * required attributes.</code>.
	 * 
	 * @param aTransId
	 *            Client transaction id. Set to <code>null</code> of no client
	 *            transaction id should be used.
	 * @param aCode
	 *            Verification code
	 * @param aType
	 *            Information type
	 */
	public EPPNameVerificationInfoCmd(String aTransId, String aCode, Type aType) {
		super(aTransId);
		this.code = aCode;
		this.type = aType;
	}

	/**
	 * <code>EPPNameVerificationInfoCmd</code> constructor that takes all of the 
	 * required attributes.</code>.
	 * 
	 * @param aTransId
	 *            Client transaction id. Set to <code>null</code> of no client
	 *            transaction id should be used.
	 * @param aCode
	 *            Verification code
	 * @param aType
	 *            Information type
	 * @param aAuthInfo
	 *            Authorization information for Name Verification (NV) object.
	 */
	public EPPNameVerificationInfoCmd(String aTransId, String aCode, Type aType, EPPAuthInfo aAuthInfo) {
		super(aTransId);
		this.code = aCode;
		this.type = aType;
		this.setAuthInfo(aAuthInfo);
	}
	
	/**
	 * Gets the EPP command Namespace associated with
	 * <code>EPPNameVerificationInfoCmd</code>.
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
	 * Gets the input type defined by the <code>Type</code> enumeration.
	 * 
	 * @return input type
	 */
	public Type getInfoType() {
		return this.type;
	}

	/**
	 * Sets the input type using one of the <code>Type</code> enumeration
	 * values.
	 * 
	 * @param aType
	 *            Input type
	 */
	public void setInfoType(Type aType) {
		this.type = aType;
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
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPNameVerificationInfoCmd</code> instance.
	 * 
	 * @param aDocument
	 *            DOM Document that is being built. Used as an Element factory.
	 * 
	 * @return Element Root DOM Element representing the
	 *         <code>EPPNameVerificationInfoCmd</code> instance.
	 * 
	 * @exception EPPEncodeException
	 *                Unable to encode <code>EPPNameVerificationInfoCmd</code>
	 *                instance.
	 */
	protected Element doEncode(Document aDocument) throws EPPEncodeException {

		// Check required attributes
		if (this.code == null) {
			throw new EPPEncodeException(
					"Undefined code in EPPNameVerificationInfoCmd");
		}

		// Info root element
		Element root = aDocument.createElementNS(
				EPPNameVerificationMapFactory.NS, ELM_NAME);

		// Type
		root.setAttribute(ATTR_TYPE, this.type.toString());

		// Code
		EPPUtil.encodeString(aDocument, root, this.code,
				EPPNameVerificationMapFactory.NS,
				EPPNameVerificationMapFactory.NS_PREFIX + ":" + ELM_CODE);

		// Auth Info
		if (this.authInfo != null) {
			EPPUtil.encodeComp(aDocument, root, this.authInfo);
		}
		return root;
	}

	/**
	 * Decode the <code>EPPNameVerificationInfoCmd</code> attributes from the
	 * aElement DOM Element tree.
	 * 
	 * @param aElement
	 *            Root DOM Element to decode
	 *            <code>EPPNameVerificationInfoCmd</code> from.
	 * 
	 * @exception EPPDecodeException
	 *                Unable to decode aElement
	 */
	protected void doDecode(Element aElement) throws EPPDecodeException {

		// Type
		this.type = Type.getType(aElement.getAttribute(ATTR_TYPE));

		// Code
		this.code = EPPUtil.decodeString(aElement,
				EPPNameVerificationMapFactory.NS, ELM_CODE);

		// Auth Info
		this.authInfo = (EPPAuthInfo) EPPUtil.decodeComp(aElement,
				EPPNameVerificationMapFactory.NS,
				EPPNameVerificationMapFactory.ELM_NV_AUTHINFO,
				EPPAuthInfo.class);
	}

	/**
	 * Compare an instance of <code>EPPNameVerificationInfoCmd</code> with this
	 * instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return {@code true} if this object is the same as the aObject argument;
	 *         {@code false} otherwise.
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPNameVerificationInfoCmd)) {
			return false;
		}

		if (!super.equals(aObject)) {
			return false;
		}

		EPPNameVerificationInfoCmd other = (EPPNameVerificationInfoCmd) aObject;

		// Type
		if (!EqualityUtil.equals(this.type, other.type)) {
			cat.error("EPPNameVerificationInfoCmd.equals(): type not equal");
			return false;
		}

		// Code
		if (!EqualityUtil.equals(this.code, other.code)) {
			cat.error("EPPNameVerificationInfoCmd.equals(): code not equal");
			return false;
		}

		// Auth Info
		if (!EqualityUtil.equals(this.authInfo, other.authInfo)) {
			cat.error("EPPNameVerificationInfoCmd.equals(): authInfo not equal");
			return false;
		}

		return true;
	}

	/**
	 * Clone <code>EPPNameVerificationInfoCmd</code>.
	 * 
	 * @return Deep copy clone of <code>EPPNameVerificationInfoCmd</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPNameVerificationInfoCmd clone = (EPPNameVerificationInfoCmd) super
				.clone();

		// Type
		clone.type = this.type;

		// Code
		clone.code = this.code;

		// Auth Info
		if (this.authInfo != null) {
			clone.authInfo = (EPPAuthInfo) this.authInfo.clone();
		}
		else {
			clone.authInfo = null;
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
