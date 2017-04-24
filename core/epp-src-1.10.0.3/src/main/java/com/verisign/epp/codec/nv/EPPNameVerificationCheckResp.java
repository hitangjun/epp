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

import java.util.ArrayList;
import java.util.List;
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
 * Provides the response to a {@link EPPNameVerificationCheckCmd} that indicates
 * whether or not the labels can be used to create a Domain Name Verification
 * (DNV) object.
 * 
 * @see com.verisign.epp.codec.nv.EPPNameVerificationCheckCmd
 * @see com.verisign.epp.codec.nv.EPPNameVerificationCheckResult
 */
public class EPPNameVerificationCheckResp extends EPPResponse {

	/**
	 * Constant for the name verification check response
	 */
	public static final String ELM_LOCALNAME = "chkData";

	/**
	 * XML Element Name of <code>EPPNameVerificationCheckResp</code> root
	 * element.
	 */
	final static String ELM_NAME = EPPNameVerificationMapFactory.NS_PREFIX
			+ ":" + ELM_LOCALNAME;

	/** Log4j category for logging */
	private static Logger cat = Logger.getLogger(
			EPPNameVerificationCheckResp.class.getName(), EPPCatFactory
					.getInstance().getFactory());

	private List<EPPNameVerificationCheckResult> results = new ArrayList<EPPNameVerificationCheckResult>();

	/**
	 * <code>EPPNameVerificationCheckResp</code> default constructor.
	 */
	public EPPNameVerificationCheckResp() {
	}

	/**
	 * <code>EPPNameVerificationCheckResp</code> constructor that sets the
	 * transaction identifier.
	 *
	 * @param aTransId
	 *            Transaction Id associated with response.
	 */
	public EPPNameVerificationCheckResp(EPPTransId aTransId) {
		super(aTransId);
	}

	/**
	 * <code>EPPNameVerificationCheckResp</code> constructor that will set the
	 * result of an individual label.
	 *
	 * @param aTransId
	 *            Transaction Id associated with response.
	 * @param aResult
	 *            Result of a single label.
	 */
	public EPPNameVerificationCheckResp(EPPTransId aTransId,
			EPPNameVerificationCheckResult aResult) {
		super(aTransId);
		this.results.add(aResult);
	}

	/**
	 * <code>EPPNameVerificationCheckResp</code> constructor that will set the
	 * result of multiple labels.
	 *
	 * @param aTransId
	 *            Transaction Id associated with response.
	 * @param aResults
	 *            List of <code>EPPNameVerificationCheckResult</code> instances.
	 */
	public EPPNameVerificationCheckResp(EPPTransId aTransId,
			List<EPPNameVerificationCheckResult> aResults) {
		super(aTransId);
		this.results = aResults;
	}

	/**
	 * Get the EPP response type associated with
	 * <code>EPPNameVerificationCheckResp</code>.
	 *
	 * @return EPPNameVerificationCheckResp.ELM_NAME
	 */
	public String getType() {
		return ELM_NAME;
	}

	/**
	 * Get the EPP command Namespace associated with
	 * <code>EPPNameVerificationCheckResp</code>.
	 *
	 * @return <code>EPPNameVerificationMapFactory.NS</code>
	 */
	public String getNamespace() {
		return EPPNameVerificationMapFactory.NS;
	}

	/**
	 * Compare an instance of <code>EPPNameVerificationCheckResp</code> with
	 * this instance.
	 *
	 * @param aObject
	 *            Object to compare with.
	 *
	 * @return <code>true</code> if equal; <code>false</code> otherwise.
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPNameVerificationCheckResp)) {
			return false;
		}

		if (!super.equals(aObject)) {
			return false;
		}

		EPPNameVerificationCheckResp other = (EPPNameVerificationCheckResp) aObject;

		// results
		if (!EqualityUtil.equals(this.results, other.results)) {
			cat.error("EPPNameVerificationCheckResp.equals(): results not equal");
			return false;
		}

		return true;
	}

	/**
	 * Clone <code>EPPNameVerificationCheckResp</code>.
	 *
	 * @return clone of <code>EPPNameVerificationCheckResp</code>
	 *
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPNameVerificationCheckResp clone = (EPPNameVerificationCheckResp) super
				.clone();

		clone.results = new ArrayList<EPPNameVerificationCheckResult>(
				this.results.size());
		for (EPPNameVerificationCheckResult item : this.results)
			clone.results.add((EPPNameVerificationCheckResult) item.clone());

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

	/**
	 * Adds a result to the list of check results.
	 * 
	 * @param aResult
	 *            Result to add.
	 */
	public void addResult(EPPNameVerificationCheckResult aResult) {
		this.results.add(aResult);
	}

	/**
	 * Set the results of a <code>EPPNameVerificationCheckResp</code> Response.
	 * There is one <code>EPPNameVerificationCheckResult</code> instance in
	 * <code>aResults</code> for each label requested in the
	 * <code>EPPNameVerificationCheckCmd</code> Command.
	 *
	 * @param aResults
	 *            List of <code>EPPNameVerificationCheckResult</code> instances.
	 */
	public void setCheckResults(List<EPPNameVerificationCheckResult> aResults) {
		this.results = aResults;
	}

	/**
	 * Get the results of a <code>EPPNameVerificationCheckResp</code> Response.
	 *
	 * @return List of <code>EPPNameVerificationCheckResult</code> instances.
	 */
	public List<EPPNameVerificationCheckResult> getCheckResults() {
		return this.results;
	}

	/**
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPNameVerificationCheckResp</code> instance.
	 *
	 * @param aDocument
	 *            DOM Document that is being built. Used as an Element factory.
	 *
	 * @return Element Root DOM Element representing the
	 *         <code>EPPNameVerificationCheckResp</code> instance.
	 *
	 * @exception EPPEncodeException
	 *                Unable to encode <code>EPPNameVerificationCheckResp</code>
	 *                instance.
	 */
	protected Element doEncode(Document aDocument) throws EPPEncodeException {
		Element root = aDocument.createElementNS(
				EPPNameVerificationMapFactory.NS, ELM_NAME);

		// Results
		EPPUtil.encodeCompList(aDocument, root, this.results);

		return root;
	}

	/**
	 * Decode the <code>EPPNameVerificationCheckResp</code> attributes from the
	 * aElement DOM Element tree.
	 *
	 * @param aElement
	 *            Root DOM Element to decode
	 *            <code>EPPNameVerificationCheckResp</code> from.
	 *
	 * @exception EPPDecodeException
	 *                Unable to decode aElement
	 */
	protected void doDecode(Element aElement) throws EPPDecodeException {
		// Results
		this.results = EPPUtil.decodeCompList(aElement,
				EPPNameVerificationMapFactory.NS,
				EPPNameVerificationCheckResult.ELM_NAME,
				EPPNameVerificationCheckResult.class);
	}

}
