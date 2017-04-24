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
 ***********************************************************/

package com.verisign.epp.codec.verificationcode;

import java.util.Date;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.verisign.epp.codec.gen.EPPCodecComponent;
import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EqualityUtil;

/**
 * Class for representing the various forms of the verification code, which
 * include:
 * <ul>
 * <br>
 * Verification code signed and passed along with transform commands.</br> <br>
 * Verification code type returned in the missing element of the info
 * response.</br> <br>
 * Verification code returned in the set element of the info response.</br>.
 * </ul>
 */
public class EPPVerificationCode implements EPPCodecComponent {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPVerificationCode.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * Constant for the local name
	 */
	public static final String ELM_LOCALNAME = "code";

	/**
	 * Constant for the tag name
	 */
	public static final String ELM_NAME = EPPVerificationCodeExtFactory.NS_PREFIX
			+ ":" + ELM_LOCALNAME;

	/**
	 * Undefined integer value
	 */
	public static int UNDEFINED = -1;

	/**
	 * The type attribute name
	 */
	private static final String ATTR_TYPE = "type";

	/**
	 * The set date attribute name
	 */
	private static final String ATTR_SET_DATE = "date";

	/**
	 * The due date attribute name
	 */
	private static final String ATTR_DUE_DATE = "due";

	/**
	 * OPTIONAL verification code value.
	 */
	private String code;

	/**
	 * Verification code type.
	 */
	private String type;

	/**
	 * OPTIONAL date that the code was set.
	 */
	private Date setDate;

	/**
	 * OPTIONAL due date when the code needed to be set or has to be set.
	 */
	private Date dueDate;

	/**
	 * Create an <code>EPPVerificationCode</code> instance.
	 */
	public EPPVerificationCode() {
	}

	/**
	 * Create an <code>EPPVerificationCode</code> with the code value and the
	 * type. This constructor is used for passing the code with a transform
	 * command.
	 * 
	 * @param aCode
	 *            Verification code value
	 * @param aType
	 *            Verification code type
	 */
	public EPPVerificationCode(String aCode, String aType) {
		this.code = aCode;
		this.type = aType;
	}

	/**
	 * Create an <code>EPPVerificationCode</code> with the code type and due
	 * date. This constructor is used for the list of codes in the missing list.
	 * 
	 * @param aType
	 *            Verification code type
	 * @param aDueDate
	 *            Due date that the code must be set
	 */
	public EPPVerificationCode(String aType, Date aDueDate) {
		this.type = aType;
		this.dueDate = aDueDate;
	}

	/**
	 * Create an <code>EPPVerificationCode</code> with the code, type, and set
	 * date. This constructor is used for the list of codes in the set list.
	 * 
	 * @param aCode
	 *            Verification code value. Pass <code>null</code> if the code
	 *            value should not be set.
	 * @param aType
	 *            Verification code type
	 * @param aSetDate
	 *            Date that the code was set
	 */
	public EPPVerificationCode(String aCode, String aType, Date aSetDate) {
		this.code = aCode;
		this.type = aType;
		this.setDate = aSetDate;
	}

	/**
	 * Decode the <code>EPPVerificationCode</code> component
	 * 
	 * @param aElement
	 *            Root element of the <code>EPPVerificationCode</code>
	 * @throws EPPDecodeException
	 *             Error decoding the <code>EPPVerificationCode</code>
	 */
	@Override
	public void decode(Element aElement) throws EPPDecodeException {

		// Type
		this.type = aElement.getAttribute(ATTR_TYPE);

		// Set Date
		if (aElement.getAttribute(ATTR_SET_DATE) != null
				&& !aElement.getAttribute(ATTR_SET_DATE).isEmpty()) {
			this.setDate = EPPUtil.decodeTimeInstant(aElement
					.getAttribute(ATTR_SET_DATE));
		}
		else {
			this.setDate = null;
		}

		// Due Date
		if (aElement.getAttribute(ATTR_DUE_DATE) != null
				&& !aElement.getAttribute(ATTR_DUE_DATE).isEmpty()) {
			this.dueDate = EPPUtil.decodeTimeInstant(aElement
					.getAttribute(ATTR_DUE_DATE));
		}
		else {
			this.dueDate = null;
		}

		// Code
		this.code = EPPUtil.decodeStringValue(aElement);
		if (this.code.isEmpty()) {
			this.code = null;
		}
	}

	/**
	 * Sets all this instance's data in the given XML document
	 * 
	 * @param aDocument
	 *            a DOM Document to attach data to.
	 * @return The root element of this component.
	 * @throws EPPEncodeException
	 *             Thrown if any errors prevent encoding.
	 */
	@Override
	public Element encode(Document aDocument) throws EPPEncodeException {
		cat.debug("EPPVerificationCode.encode(Document): enter");

		if (aDocument == null) {
			throw new EPPEncodeException("aDocument is null"
					+ " on in EPPVerificationCode.encode(Document)");
		}

		// Validate required attributes
		if (this.type == null) {
			throw new EPPEncodeException("Type is required.");
		}

		Element root = aDocument.createElementNS(
				EPPVerificationCodeExtFactory.NS, ELM_NAME);

		// Type
		root.setAttribute(ATTR_TYPE, this.type);

		// Set Date
		if (this.hasSetDate()) {
			root.setAttribute(ATTR_SET_DATE,
					EPPUtil.encodeTimeInstant(this.setDate));
		}

		// Due Date
		if (this.hasDueDate()) {
			root.setAttribute(ATTR_DUE_DATE,
					EPPUtil.encodeTimeInstant(this.dueDate));
		}

		// Code
		if (this.code != null) {
			Text codeVal = aDocument.createTextNode(this.code);
			root.appendChild(codeVal);
		}

		cat.debug("EPPVerificationCode.encode(Document): exit");

		return root;
	}

	/**
	 * Clone <code>EPPVerificationCode</code>. Signature element is not cloned.
	 * 
	 * @return clone of <code>EPPVerificationCode</code>
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPVerificationCode clone = (EPPVerificationCode) super.clone();

		return clone;
	}

	/**
	 * implements a deep <code>EPPVerificationCode</code> compare.
	 * 
	 * @param aObject
	 *            <code>EPPVerificationCode</code> instance to compare with
	 * @return <code>true</code> if equal <code>false</code> otherwise
	 */
	public boolean equals(Object aObject) {

		if (!(aObject instanceof EPPVerificationCode)) {
			cat.error("EPPVerificationCode.equals(): aObject is not an EPPVerificationCode");
			return false;
		}

		EPPVerificationCode other = (EPPVerificationCode) aObject;

		// Type
		if (!EqualityUtil.equals(this.type, other.type)) {
			cat.error("EPPVerificationCode.equals(): type not equal");
			return false;
		}

		// Code
		if (!EqualityUtil.equals(this.code, other.code)) {
			cat.error("EPPVerificationCode.equals(): code not equal");
			return false;
		}

		// Set Date
		if (!EqualityUtil.equals(this.setDate, other.setDate)) {
			cat.error("EPPVerificationCode.equals(): setDate not equal");
			return false;
		}

		// Due Date
		if (!EqualityUtil.equals(this.dueDate, other.dueDate)) {
			cat.error("EPPVerificationCode.equals(): dueDate not equal");
			return false;
		}

		return true;
	}

	/**
	 * Has the code been set?
	 * 
	 * @return <code>true</code> if the code has been set; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasCode() {
		return (this.code != null ? true : false);
	}

	/**
	 * Gets the code value.
	 * 
	 * @return The code value if defined: <code>null</code> otherwise.
	 */
	public String getCode() {
		return this.code;
	}

	/**
	 * Sets the code value.
	 * 
	 * @param aCode
	 *            Code value to set
	 */
	public void setCode(String aCode) {
		this.code = aCode;
	}

	/**
	 * Gets the VSP Identifier (vsp-id) from the code.
	 * 
	 * @return VSP Identifier (vsp-id) if defined; <code>UNDEFINED</code>
	 *         otherwise.
	 */
	public int getVspId() {
		int theVspId = UNDEFINED;

		if (!this.hasCode()) {
			return UNDEFINED;
		}

		String[] tokens = this.code.split("-");
		if (tokens.length != 2) {
			return UNDEFINED;
		}

		try {
			theVspId = Integer.parseInt(tokens[0]);
		}
		catch (Exception ex) {
			cat.debug("getVspId(): Exception on call to Integer.parseInt("
					+ tokens[0] + "): " + ex);
		}

		return theVspId;
	}

	/**
	 * Gets the Verification Identifier (verification-id) from the code.
	 * 
	 * @return Verification Identifier (verification-id) if defined;
	 *         <code>null</code> otherwise.
	 */
	public String getVerificationId() {
		if (!this.hasCode()) {
			return null;
		}

		String[] tokens = this.code.split("-");
		if (tokens.length != 2) {
			return null;
		}

		return tokens[1];
	}

	/**
	 * Gets the code type.
	 * 
	 * @return The code type set.
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Sets the code type.
	 * 
	 * @param aType
	 *            Code type to set
	 */
	public void setType(String aType) {
		this.type = aType;
	}

	/**
	 * Has the set date been set?
	 * 
	 * @return <code>true</code> if the set date has been set;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasSetDate() {
		return (this.setDate != null ? true : false);
	}

	/**
	 * Gets the date that the verification code was set.
	 * 
	 * @return The set date if defined: <code>null</code> otherwise.
	 */
	public Date getSetDate() {
		return this.setDate;
	}

	/**
	 * Sets the date that the verification code was set.
	 * 
	 * @param aSetDate
	 *            Date that the verification code was set.
	 */
	public void setSetDate(Date aSetDate) {
		this.setDate = aSetDate;
	}

	/**
	 * Has the due date been set?
	 * 
	 * @return <code>true</code> if the due date has been set;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasDueDate() {
		return (this.dueDate != null ? true : false);
	}

	/**
	 * Gets the due date for the verification code.
	 * 
	 * @return The due date if defined: <code>null</code> otherwise.
	 */
	public Date getDueDate() {
		return this.dueDate;
	}

	/**
	 * Sets the due date for the verification code.
	 * 
	 * @param aDueDate
	 *            Due date for the verification code
	 */
	public void setDueDate(Date aDueDate) {
		this.dueDate = aDueDate;
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