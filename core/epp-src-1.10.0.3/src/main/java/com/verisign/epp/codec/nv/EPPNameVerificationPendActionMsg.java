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

import java.util.Date;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.verisign.epp.codec.gen.EPPCodecException;
import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPResponse;
import com.verisign.epp.codec.gen.EPPTransId;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EqualityUtil;

/**
 * Poll message used to indicate the result of a pending action.
 *
 * @see com.verisign.epp.codec.nv.EPPNameVerificationCreateCmd
 */
public class EPPNameVerificationPendActionMsg extends EPPResponse {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(
			EPPNameVerificationPendActionMsg.class.getName(), EPPCatFactory
					.getInstance().getFactory());

	/**
	 * XML local name for <code>EPPNameVerificationPendActionMsg</code>.
	 */
	public static final String ELM_LOCALNAME = "panData";

	/**
	 * XML root tag for <code>EPPNameVerificationPendActionMsg</code>.
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
	 * XML Element Name for the <code>paStatus</code> element.
	 */
	private final static String ELM_STATUS = "paStatus";

	/**
	 * The status attribute name
	 */
	private static final String ATTR_STATUS = "s";

	/**
	 * XML Element Name for the <code>msg</code>.
	 */
	private final static String ELM_MSG = "msg";

	/**
	 * XML Element Name for the <code>paDate</code> element.
	 */
	private final static String ELM_PA_DATE = "paDate";

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
	 * Human-readable description of the result.
	 */
	private String msg;

	/**
	 * Pending action completion date
	 */
	private Date paDate;

	/**
	 * <code>EPPNameVerificationPendActionMsg</code> default constructor. Must
	 * call required setter methods before encode.
	 */
	public EPPNameVerificationPendActionMsg() {
	}

	/**
	 * <code>EPPNameVerificationPendActionMsg</code> constructor that takes all
	 * of the required attributes.
	 *
	 * @param aTransId
	 *            Poll command transaction id
	 * @param aCode
	 *            Verification code pending action
	 * @param aType
	 *            The verification code type
	 * @param aStatus
	 *            Status of pending action
	 * @param aMsg
	 *            Human-readable description of the result.
	 * @param aPaDate
	 *            Date of pending action completion
	 */
	public EPPNameVerificationPendActionMsg(EPPTransId aTransId, String aCode,
			String aType, EPPNameVerificationStatus aStatus, String aMsg,
			Date aPaDate) {

		super(aTransId);

		this.code = aCode;
		this.type = aType;
		this.status = aStatus;
		this.msg = aMsg;
		this.paDate = aPaDate;
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
	 * Gets the human-readable description of the result.
	 * 
	 * @return human-readable description of the result.
	 */
	public String getMsg() {
		return this.msg;
	}

	/**
	 * Sets the human-readable description of the result.
	 * 
	 * @param aMsg
	 *            human-readable description of the result.
	 */
	public void setMsg(String aMsg) {
		this.msg = aMsg;
	}

	/**
	 * Gets the the date and time describing when review of the requested action
	 * was completed.
	 * 
	 * @return Date and time describing when review of the requested action was
	 *         completed.
	 */
	public Date getPaDate() {
		return this.paDate;
	}

	/**
	 * Sets the date and time describing when review of the requested action was
	 * completed.
	 * 
	 * @param aPaDate
	 *            Date and time describing when review of the requested action
	 *            was completed.
	 */
	public void setPaDate(Date aPaDate) {
		this.paDate = aPaDate;
	}

	/**
	 * Validate the state of the <code>EPPNameVerificationPendActionMsg</code>
	 * instance. A valid state means that all of the required attributes have
	 * been set. If validateState returns without an exception, the state is
	 * valid. If the state is not valid, the EPPCodecException will contain a
	 * description of the error. throws EPPCodecException State error. This will
	 * contain the name of the attribute that is not valid.
	 *
	 * @throws EPPCodecException
	 *             State is not valid
	 */
	void validateState() throws EPPCodecException {
		if (this.code == null) {
			cat.error("EPPNameVerificationPendActionMsg.validateState(): required attribute code is not set");
			throw new EPPCodecException("required attribute code is not set");
		}

		if (this.type == null) {
			cat.error("EPPNameVerificationPendActionMsg.validateState(): required attribute type is not set");
			throw new EPPCodecException("required attribute type is not set");
		}

		if (this.status == null) {
			cat.error("EPPNameVerificationPendActionMsg.validateState(): required attribute status is not set");
			throw new EPPCodecException("required attribute status is not set");
		}

		if (this.msg == null) {
			cat.error("EPPNameVerificationPendActionMsg.validateState(): required attribute msg is not set");
			throw new EPPCodecException("required attribute msg is not set");
		}

		if (this.paDate == null) {
			cat.error("EPPNameVerificationPendActionMsg.validateState(): required attribute paDate is not set");
			throw new EPPCodecException("required attribute paDate is not set");
		}
	}

	/**
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPNameVerificationPendActionMsg</code> instance.
	 *
	 * @param aDocument
	 *            DOM Document that is being built. Used as an Element factory.
	 *
	 * @return Element Root DOM Element representing the
	 *         <code>EPPNameVerificationPendActionMsg</code> instance.
	 *
	 * @exception EPPEncodeException
	 *                Unable to encode
	 *                <code>EPPNameVerificationPendActionMsg</code> instance.
	 */
	protected Element doEncode(Document aDocument) throws EPPEncodeException {
		// Validate Attributes
		try {
			validateState();
		}
		catch (EPPCodecException e) {
			throw new EPPEncodeException(
					"Invalid state on EPPNameVerificationPendActionMsg.encode: "
							+ e);
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
		Element theStatusElm = aDocument.createElementNS(
				EPPNameVerificationMapFactory.NS,
				EPPNameVerificationMapFactory.NS_PREFIX + ":" + ELM_STATUS);
		root.appendChild(theStatusElm);
		theStatusElm.setAttribute(ATTR_STATUS, this.status.toString());

		// Msg
		Element reason = aDocument.createElementNS(
				EPPNameVerificationMapFactory.NS,
				EPPNameVerificationMapFactory.NS_PREFIX + ":" + ELM_MSG);
		Text reasonVal = aDocument.createTextNode(this.msg);
		reason.appendChild(reasonVal);
		root.appendChild(reason);

		// Reason Language
		/**
		 * @todo Uncomment once the lang attribute is added to the reason / msg
		 *       element. if (this.language != null) {
		 *       reason.setAttribute(ATTR_LANG, this.language); }
		 */

		// Pa Date
		EPPUtil.encodeTimeInstant(aDocument, root, this.paDate,
				EPPNameVerificationMapFactory.NS,
				EPPNameVerificationMapFactory.NS_PREFIX + ":" + ELM_PA_DATE);

		return root;
	}

	/**
	 * Decode the <code>EPPNameVerificationPendActionMsg</code> attributes from
	 * the <code>aElement</code> DOM Element tree.
	 *
	 * @param aElement
	 *            Root DOM Element to decode
	 *            <code>EPPNameVerificationPendActionMsg</code> from.
	 *
	 * @exception EPPDecodeException
	 *                Unable to decode <code>aElement</code>
	 */
	protected void doDecode(Element aElement) throws EPPDecodeException {

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
		String theStatusStr = theElm.getAttribute(ATTR_STATUS);
		this.status = EPPNameVerificationStatus.getStatus(theStatusStr);

		// Msg
		this.msg = EPPUtil.decodeString(aElement,
				EPPNameVerificationMapFactory.NS, ELM_MSG);

		// Language
		/**
		 * @todo Uncomment once the lang attribute is added to the reason / msg
		 *       element. Element theElm =
		 *       EPPUtil.getElementByTagNameNS(aElement,
		 *       EPPNameVerificationMapFactory.NS, ELM_REASON); this.language =
		 *       theElm.getAttribute(ATTR_LANG);
		 */

		// Pa Date
		this.paDate = EPPUtil.decodeTimeInstant(aElement,
				EPPNameVerificationMapFactory.NS, ELM_PA_DATE);

		// Validate Attributes
		try {
			validateState();
		}
		catch (EPPCodecException e) {
			throw new EPPDecodeException(
					"Invalid state on EPPNameVerificationPendActionMsg.decode: "
							+ e);
		}
	}

	/**
	 * Clone <code>EPPNameVerificationPendActionMsg</code>.
	 *
	 * @return clone of <code>EPPNameVerificationPendActionMsg</code>
	 *
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPNameVerificationPendActionMsg clone = (EPPNameVerificationPendActionMsg) super
				.clone();

		return clone;
	}

	/**
	 * Compare an instance of <code>EPPNameVerificationPendActionMsg</code> with
	 * this instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return {@code true} if this object is the same as the aObject argument;
	 *         {@code false} otherwise
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPNameVerificationPendActionMsg)) {
			return false;
		}

		EPPNameVerificationPendActionMsg other = (EPPNameVerificationPendActionMsg) aObject;

		// Code
		if (!EqualityUtil.equals(this.code, other.code)) {
			cat.error("EPPNameVerificationPendActionMsg.equals(): code not equal");
			return false;
		}

		// Type
		if (!EqualityUtil.equals(this.type, other.type)) {
			cat.error("EPPNameVerificationPendActionMsg.equals(): type not equal");
			return false;
		}

		// Status
		if (!EqualityUtil.equals(this.status, other.status)) {
			cat.error("EPPNameVerificationPendActionMsg.equals(): status not equal");
			return false;
		}

		// Msg
		if (!EqualityUtil.equals(this.msg, other.msg)) {
			cat.error("EPPNameVerificationCreateFailed.equals(): msg not equal");
			return false;
		}

		// Pa Date
		if (!EqualityUtil.equals(this.paDate, other.paDate)) {
			cat.error("EPPNameVerificationCreateFailed.equals(): paDate not equal");
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
