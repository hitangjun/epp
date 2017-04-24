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

import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPResponse;
import com.verisign.epp.codec.gen.EPPTransId;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.codec.verificationcode.EPPEncodedSignedCodeValue;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EqualityUtil;

/**
 * Create response for a successful domain name verification, which results in
 * the return of a digitally signed domain verification code.
 * 
 * @see com.verisign.epp.codec.nv.EPPNameVerificationCreateCmd
 */
public class EPPNameVerificationCreateResp extends EPPResponse {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(
			EPPNameVerificationCreateResp.class.getName(), EPPCatFactory
					.getInstance().getFactory());

	/**
	 * XML local name for <code>EPPNameVerificationCreateResp</code>.
	 */
	public static final String ELM_LOCALNAME = "creData";

	/**
	 * XML root tag for <code>EPPNameVerificationCreateResp</code>.
	 */
	public static final String ELM_NAME = EPPNameVerificationMapFactory.NS_PREFIX
			+ ":" + ELM_LOCALNAME;

	/**
	 * The concrete result of the create, which is either success, pending, or
	 * failed.
	 */
	private EPPNameVerificationCreateResult result;

	/**
	 * <code>EPPNameVerificationCreateResp</code> default constructor.
	 */
	public EPPNameVerificationCreateResp() {
	}

	/**
	 * <code>EPPNameVerificationCreateResp</code> constructor that the transaction
	 * id associated with the response. The create result must be set.
	 * 
	 * @param aTransId
	 *            Transaction Id associated with response.
	 */
	public EPPNameVerificationCreateResp(EPPTransId aTransId) {
		super(aTransId);
	}
	
	
	/**
	 * <code>EPPNameVerificationCreateResp</code> constructor that includes the
	 * required result.
	 * 
	 * @param aTransId
	 *            Transaction Id associated with response.
	 * @param aResult
	 *            The concrete result of the create.
	 */
	public EPPNameVerificationCreateResp(EPPTransId aTransId,
			EPPNameVerificationCreateResult aResult) {
		super(aTransId);
		this.result = aResult;
	}

	/**
	 * Gets the concrete result of the create.
	 * 
	 * @return Concrete create result.
	 */
	public EPPNameVerificationCreateResult getCreateResult() {
		return this.result;
	}

	/**
	 * Sets the concrete result of the create.
	 * 
	 * @param aResult
	 *            Concrete create result.
	 */
	public void setCreateResult(EPPNameVerificationCreateResult aResult) {
		this.result = aResult;
	}

	/**
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPNameVerificationCreateResp</code> instance.
	 * 
	 * @param aDocument
	 *            DOM Document that is being built. Used as an Element factory.
	 * 
	 * @return Element Root DOM Element representing the
	 *         EPPNameVerificationCreateResp instance.
	 * 
	 * @exception EPPEncodeException
	 *                Unable to encode EPPNameVerificationCreateResp instance.
	 */
	protected Element doEncode(Document aDocument) throws EPPEncodeException {

		// Create root element
		Element root = aDocument.createElementNS(
				EPPNameVerificationMapFactory.NS, ELM_NAME);

		// Result
		EPPUtil.encodeComp(aDocument, root, this.result);

		return root;
	}

	/**
	 * Decode the <code>EPPNameVerificationCreateResp</code> attributes from the
	 * aElement DOM Element tree.
	 * 
	 * @param aElement
	 *            Root DOM Element to decode
	 *            <code>EPPNameVerificationCreateResp</code> from.
	 * 
	 * @exception EPPDecodeException
	 *                Unable to decode aElement
	 */
	protected void doDecode(Element aElement) throws EPPDecodeException {
		// -- Result
		// Success?
		this.result = (EPPNameVerificationCreateResult) EPPUtil.decodeComp(
				aElement, EPPNameVerificationMapFactory.NS,
				EPPNameVerificationCreateSuccess.ELM_NAME,
				EPPNameVerificationCreateSuccess.class);
		if (this.result == null) {
			// Failed?
			this.result = (EPPNameVerificationCreateResult) EPPUtil.decodeComp(
					aElement, EPPNameVerificationMapFactory.NS,
					EPPNameVerificationCreateFailed.ELM_NAME,
					EPPNameVerificationCreateFailed.class);
		}

		if (this.result == null) {
			// Pending?
			this.result = (EPPNameVerificationCreateResult) EPPUtil.decodeComp(
					aElement, EPPNameVerificationMapFactory.NS,
					EPPNameVerificationCreatePending.ELM_NAME,
					EPPNameVerificationCreatePending.class);
		}

		// Error decoding?
		if (this.result == null) {
			throw new EPPDecodeException(
					"EPPNameVerificationCreateResp.doDecode(): Unable to decode one of the possible create results");
		}
	}

	/**
	 * Clone <code>EPPNameVerificationCreateResp</code>.
	 * 
	 * @return clone of <code>EPPNameVerificationCreateResp</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPNameVerificationCreateResp clone = (EPPNameVerificationCreateResp) super
				.clone();

		// Result
		clone.result = (EPPNameVerificationCreateResult) this.result.clone();

		return clone;
	}

	/**
	 * Gets the EPP response type associated with
	 * <code>EPPNameVerificationCreateResp</code>.
	 * 
	 * @return <code>EPPNameVerificationCreateResp.ELM_NAME</code>
	 */
	public String getType() {
		return ELM_NAME;
	}

	/**
	 * Gets the EPP command namespace associated with
	 * <code>EPPNameVerificationCreateResp</code>.
	 * 
	 * @return <code>EPPNameVerificationMapFactory.NS</code>
	 */
	public String getNamespace() {
		return EPPNameVerificationMapFactory.NS;
	}

	/**
	 * Compare an instance of <code>EPPNameVerificationCreateResp</code> with
	 * this instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return {@code true} if this object is the same as the aObject argument;
	 *         {@code false} otherwise
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPNameVerificationCreateResp)) {
			return false;
		}

		EPPNameVerificationCreateResp other = (EPPNameVerificationCreateResp) aObject;

		// Result
		if (!EqualityUtil.equals(this.result, other.result)) {
			cat.error("EPPNameVerificationCreateResp.equals(): result not equal");
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