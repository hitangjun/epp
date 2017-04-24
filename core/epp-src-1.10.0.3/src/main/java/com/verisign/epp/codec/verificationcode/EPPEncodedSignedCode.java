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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.verisign.epp.codec.gen.EPPCodecComponent;
import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.exception.EPPException;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EqualityUtil;

/**
 * Class for the encoded signed code, which contains the code and the
 * <code>XMLSignature</code> itself. This class extends
 * <code>EPPSignedCode</code>.
 */
public class EPPEncodedSignedCode implements EPPCodecComponent {

	/**
	 * Serial version id for this class.
	 */
	private static final long serialVersionUID = -2581814950269930902L;

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPEncodedSignedCode.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * Constant for the code local name for encoded signedCode element
	 */
	public static final String ELM_LOCALNAME = "encodedSignedCode";

	/**
	 * Constant for the code tag for signedCode element
	 */
	public static final String ELM_NAME = EPPVerificationCodeExtFactory.NS_PREFIX
			+ ":" + ELM_LOCALNAME;

	/**
	 * List of encoded signed code values.
	 */
	List<EPPEncodedSignedCodeValue> codes = new ArrayList<EPPEncodedSignedCodeValue>();

	/**
	 * Default constructor
	 */
	public EPPEncodedSignedCode() {
	}

	/**
	 * Construct an encoded signed code with a single signed code value.
	 * 
	 * @param aSignedCode
	 *            Signed code value that will be encoded and added to the list.
	 * @throws EPPEncodeException
	 *             Error encoding the SignedCode <code>byte[]</code>.
	 * @throws EPPDecodeException
	 *             Error decoding the encoded SignedCode <code>byte[]</code>.
	 */
	public EPPEncodedSignedCode(EPPSignedCode aSignedCode)
			throws EPPEncodeException, EPPDecodeException {
		this.codes.add(new EPPEncodedSignedCodeValue(aSignedCode));
	}

	/**
	 * Construct an encoded signed code with a single encoded signed code value.
	 * 
	 * @param aEncodedSignedCodeValue
	 *            Encoded signed code value to add to the list.
	 */
	public EPPEncodedSignedCode(
			EPPEncodedSignedCodeValue aEncodedSignedCodeValue) {
		this.codes.add(aEncodedSignedCodeValue);
	}

	/**
	 * Create an <code>EPPEncodedSignedCode</code> with the code of the signed
	 * code.
	 * 
	 * @param aCode
	 *            Verification code
	 * @throws EPPEncodeException
	 *             Thrown if any errors prevent encoding.
	 */
	public EPPEncodedSignedCode(EPPVerificationCode aCode)
			throws EPPEncodeException {
		this.codes.add(new EPPEncodedSignedCodeValue(aCode));
	}

	/**
	 * Create an <code>EPPEncodedSignedCode</code> with the code of the signed
	 * code and code type.
	 * 
	 * @param aCode
	 *            Verification code
	 * @param aType
	 *            OPTIONAL verification code type. Set to <code>null</code> if
	 *            no type is defined.
	 * @throws EPPEncodeException
	 *             Thrown if any errors prevent encoding.
	 */
	public EPPEncodedSignedCode(String aCode, String aType)
			throws EPPEncodeException {
		this.codes.add(new EPPEncodedSignedCodeValue(aCode, aType));
	}

	/**
	 * Create an <code>EPPEncodedSignedCode</code> with a list of encoded signed
	 * code values.
	 * 
	 * @param aCodes
	 *            List of encoded signed code values
	 */
	public EPPEncodedSignedCode(List<EPPEncodedSignedCodeValue> aCodes) {
		if (aCodes != null) {
			this.codes = aCodes;
		}
		else {
			this.codes = new ArrayList<EPPEncodedSignedCodeValue>();
		}
	}

	/**
	 * Are encoded signed codes defined?
	 * 
	 * @return <code>true</code> if encoded signed codes are defined;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasCodes() {
		return (this.codes != null && !this.codes.isEmpty() ? true : false);
	}

	/**
	 * Gets the list of encoded signed codes.
	 * 
	 * @return List of encoded signed codes if set; <code>null</code> otherwise.
	 */
	public List<EPPEncodedSignedCodeValue> getSignedCodes() {
		return this.codes;
	}

	/**
	 * Sets the list of encoded signed codes.
	 * 
	 * @param aCodes
	 *            List of encoded signed codes
	 */
	public void setSignedCodes(List<EPPEncodedSignedCodeValue> aCodes) {
		this.codes = aCodes;
	}

	/**
	 * Sets the encoded signed codes to a single code.
	 * 
	 * @param aCode
	 *            Encoded signed code to set
	 */
	public void setCode(EPPEncodedSignedCodeValue aCode) {
		this.codes = new ArrayList<EPPEncodedSignedCodeValue>();
		this.codes.add(aCode);
	}

	/**
	 * Adds a encoded signed code to the list of encoded signed codes.
	 * 
	 * @param aCode
	 *            Encoded signed code to add to the list of encoded signed
	 *            codes.
	 */
	public void addCode(EPPEncodedSignedCodeValue aCode) {
		if (this.codes == null) {
			this.codes = new ArrayList<EPPEncodedSignedCodeValue>();
		}

		this.codes.add(aCode);
	}

	/**
	 * Decode the <code>EPPSignedCode</code> component
	 * 
	 * @param aElement
	 *            Root element of the <code>EPPSignedCode</code>
	 * @throws EPPDecodeException
	 *             Error decoding the <code>EPPSignedCode</code>
	 */
	public void decode(Element aElement) throws EPPDecodeException {
		cat.debug("EPPEncodedSignedCode.decode(Element): enter");

		// Codes
		this.codes = new ArrayList<EPPEncodedSignedCodeValue>();

		Vector theChildren = EPPUtil.getElementsByTagNameNS(aElement,
				EPPVerificationCodeExtFactory.NS,
				EPPEncodedSignedCodeValue.ELM_LOCALNAME);

		if (theChildren.size() != 0) {
			// For each signed code
			for (int i = 0; i < theChildren.size(); i++) {
				EPPEncodedSignedCodeValue signedCode;

				signedCode = new EPPEncodedSignedCodeValue();
				signedCode.decode((Element) theChildren.elementAt(i));

				this.codes.add(signedCode);
			}
		}

		cat.debug("EPPEncodedSignedCode.decode(Element): exit - normal");
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
	public Element encode(Document aDocument) throws EPPEncodeException {
		cat.debug("EPPEncodedSignedCode.encode(Document): enter");

		if (aDocument == null) {
			throw new EPPEncodeException("aDocument is null"
					+ " on in EPPSignedCode.encode(Document)");
		}

		Element root = aDocument.createElementNS(
				EPPVerificationCodeExtFactory.NS,
				EPPVerificationCodeExtFactory.NS_PREFIX + ":" + ELM_LOCALNAME);

		// Codes
		EPPUtil.encodeCompList(aDocument, root, this.codes);

		cat.debug("EPPEncodedSignedCode.encode(Document): exit");
		return root;
	}

	/**
	 * implements a deep <code>EPPEncodedSignedCode</code> compare.
	 * 
	 * @param aObject
	 *            <code>EPPEncodedSignedCode</code> instance to compare with
	 * 
	 * @return true if equal false otherwise
	 */
	public boolean equals(Object aObject) {

		if (!(aObject instanceof EPPEncodedSignedCode)) {
			cat.error("EPPEncodedSignedCode.equals(): "
					+ aObject.getClass().getName()
					+ " not EPPEncodedSignedCode instance");

			return false;
		}

		EPPEncodedSignedCode other = (EPPEncodedSignedCode) aObject;

		// Codes
		if (!EqualityUtil.equals(this.codes, other.codes)) {
			cat.error("EPPEncodedSignedCode.equals(): codes not equal");
			return false;
		}

		return true;
	}

	/**
	 * Clone <code>EPPEncodedSignedCode</code>.
	 * 
	 * @return clone of <code>EPPEncodedSignedCode</code>
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPEncodedSignedCode clone = (EPPEncodedSignedCode) super.clone();

		// Codes
		clone.codes = new ArrayList<EPPEncodedSignedCodeValue>(
				this.codes.size());
		for (EPPEncodedSignedCodeValue item : this.codes)
			clone.codes.add((EPPEncodedSignedCodeValue) item.clone());

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
