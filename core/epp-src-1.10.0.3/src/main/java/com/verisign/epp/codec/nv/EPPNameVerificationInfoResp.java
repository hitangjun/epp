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
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EqualityUtil;

/**
 * Info response to an info command that contains either a signed code or input
 * result, based on the value of the {@link EPPNameVerificationInfoCmd} type
 * attribute.
 * 
 * @see com.verisign.epp.codec.nv.EPPNameVerificationInfoCmd
 */
public class EPPNameVerificationInfoResp extends EPPResponse {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(
			EPPNameVerificationInfoResp.class.getName(), EPPCatFactory
					.getInstance().getFactory());

	/**
	 * XML local name for <code>EPPNameVerificationInfoResp</code>.
	 */
	public static final String ELM_LOCALNAME = "infData";

	/**
	 * XML root tag for <code>EPPNameVerificationInfoResp</code>.
	 */
	public static final String ELM_NAME = EPPNameVerificationMapFactory.NS_PREFIX
			+ ":" + ELM_LOCALNAME;

	/**
	 * The concrete result of the info, which is either signed code or input.
	 */
	private EPPNameVerificationInfoResult result;

	/**
	 * <code>EPPNameVerificationInfoResp</code> default constructor.
	 */
	public EPPNameVerificationInfoResp() {
	}

	/**
	 * <code>EPPNameVerificationInfoResp</code> constructor that the transaction
	 * id associated with the response. The info result must be set.
	 * 
	 * @param aTransId
	 *            Transaction Id associated with response.
	 */
	public EPPNameVerificationInfoResp(EPPTransId aTransId) {
		super(aTransId);
	}

	/**
	 * <code>EPPNameVerificationInfoResp</code> constructor that includes the
	 * required result.
	 * 
	 * @param aTransId
	 *            Transaction Id associated with response.
	 * @param aResult
	 *            The concrete result of the info.
	 */
	public EPPNameVerificationInfoResp(EPPTransId aTransId,
			EPPNameVerificationInfoResult aResult) {
		super(aTransId);
		this.result = aResult;
	}

	/**
	 * Gets the concrete result of the info.
	 * 
	 * @return Concrete info result.
	 */
	public EPPNameVerificationInfoResult getCreateResult() {
		return this.result;
	}

	/**
	 * Sets the concrete result of the info.
	 * 
	 * @param aResult
	 *            Concrete info result.
	 */
	public void setCreateResult(EPPNameVerificationInfoResult aResult) {
		this.result = aResult;
	}

	/**
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPNameVerificationInfoResp</code> instance.
	 * 
	 * @param aDocument
	 *            DOM Document that is being built. Used as an Element factory.
	 * 
	 * @return Element Root DOM Element representing the
	 *         EPPNameVerificationInfoResp instance.
	 * 
	 * @exception EPPEncodeException
	 *                Unable to encode EPPNameVerificationInfoResp instance.
	 */
	protected Element doEncode(Document aDocument) throws EPPEncodeException {

		// Info root element
		Element root = aDocument.createElementNS(
				EPPNameVerificationMapFactory.NS, ELM_NAME);

		// Result
		EPPUtil.encodeComp(aDocument, root, this.result);

		return root;
	}

	/**
	 * Decode the <code>EPPNameVerificationInfoResp</code> attributes from the
	 * aElement DOM Element tree.
	 * 
	 * @param aElement
	 *            Root DOM Element to decode
	 *            <code>EPPNameVerificationInfoResp</code> from.
	 * 
	 * @exception EPPDecodeException
	 *                Unable to decode aElement
	 */
	protected void doDecode(Element aElement) throws EPPDecodeException {

		// -- Result
		// Signed Code?
		this.result = (EPPNameVerificationInfoResult) EPPUtil.decodeComp(
				aElement, EPPNameVerificationMapFactory.NS,
				EPPNameVerificationInfoSignedCodeResult.ELM_NAME,
				EPPNameVerificationInfoSignedCodeResult.class);
		if (this.result == null) {
			// Input?
			this.result = (EPPNameVerificationInfoResult) EPPUtil.decodeComp(
					aElement, EPPNameVerificationMapFactory.NS,
					EPPNameVerificationInfoInputResult.ELM_NAME,
					EPPNameVerificationInfoInputResult.class);
		}

		// Error decoding?
		if (this.result == null) {
			throw new EPPDecodeException(
					"EPPNameVerificationInfoResp.doDecode(): Unable to decode one of the possible info results");
		}
	}

	/**
	 * Clone <code>EPPNameVerificationInfoResp</code>.
	 * 
	 * @return clone of <code>EPPNameVerificationInfoResp</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPNameVerificationInfoResp clone = (EPPNameVerificationInfoResp) super
				.clone();

		// Result
		clone.result = (EPPNameVerificationInfoResult) this.result.clone();

		return clone;
	}

	/**
	 * Gets the EPP response type associated with
	 * <code>EPPNameVerificationInfoResp</code>.
	 * 
	 * @return <code>EPPNameVerificationInfoResp.ELM_NAME</code>
	 */
	public String getType() {
		return ELM_NAME;
	}

	/**
	 * Gets the EPP command namespace associated with
	 * <code>EPPNameVerificationInfoResp</code>.
	 * 
	 * @return <code>EPPNameVerificationMapFactory.NS</code>
	 */
	public String getNamespace() {
		return EPPNameVerificationMapFactory.NS;
	}

	/**
	 * Compare an instance of <code>EPPNameVerificationInfoResp</code> with this
	 * instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return {@code true} if this object is the same as the aObject argument;
	 *         {@code false} otherwise
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPNameVerificationInfoResp)) {
			return false;
		}

		EPPNameVerificationInfoResp other = (EPPNameVerificationInfoResp) aObject;

		// Result
		if (this.result instanceof EPPNameVerificationInfoSignedCodeResult) {
			if (!EqualityUtil.equals(
					(EPPNameVerificationInfoSignedCodeResult) this.result,
					(EPPNameVerificationInfoSignedCodeResult) other.result)) {
				cat.error("EPPNameVerificationInfoResp.equals(): SignedCode result not equal");
				return false;
			}
		}
		else {
			if (!EqualityUtil.equals(
					(EPPNameVerificationInfoInputResult) this.result,
					(EPPNameVerificationInfoInputResult) other.result)) {
				cat.error("EPPNameVerificationInfoResp.equals(): Input result not equal");
				return false;
			}
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