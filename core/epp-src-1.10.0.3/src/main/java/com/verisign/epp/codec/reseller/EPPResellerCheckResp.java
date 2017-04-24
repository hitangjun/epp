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
package com.verisign.epp.codec.reseller;

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

/**
 * Represents a reseller &lt;check&gt; response that returns the availability of
 * a set of client-specified but server-unique reseller identifiers.
 * 
 * @see com.verisign.epp.codec.reseller.EPPResellerCheckCmd
 */
public class EPPResellerCheckResp extends EPPResponse {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPResellerCheckResp.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * XML local name for <code>EPPResellerCheckResp</code>.
	 */
	public static final String ELM_LOCALNAME = "chkData";

	/**
	 * XML root tag for <code>EPPResellerCheckCmd</code>.
	 */
	public static final String ELM_NAME = EPPResellerMapFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * Check results
	 */
	private List<EPPResellerCheckResult> results = new ArrayList<EPPResellerCheckResult>();

	/**
	 * <code>EPPResellerCheckResp</code> default constructor. It will set
	 * results attribute to an empty <code>List</code>.
	 */
	public EPPResellerCheckResp() {
	}

	/**
	 * <code>EPPResellerCheckResp</code> constructor that only takes the
	 * transaction identifier.
	 * 
	 * @param aTransId
	 *            transaction Id associated with response
	 */
	public EPPResellerCheckResp(EPPTransId aTransId) {
		super(aTransId);
	}

	/**
	 * <code>EPPResellerCheckResp</code> constructor that takes an individual
	 * result. Add more tables using {@link #addCheckResult(EPPResellerCheckResult)}.
	 * 
	 * @param aTransId
	 *            transaction Id associated with response
	 * @param aResult
	 *            An individual check result
	 */
	public EPPResellerCheckResp(EPPTransId aTransId,
			EPPResellerCheckResult aResult) {
		super(aTransId);
		this.addCheckResult(aResult);
	}

	/**
	 * Are any results defined in the list of results?
	 * 
	 * @return <code>true</code> if there is at least one result defined;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasCheckResults() {
		if (this.results != null && !this.results.isEmpty()) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Get the list of results.
	 * 
	 * @return {@code List} of results
	 */
	public List<EPPResellerCheckResult> getCheckResults() {
		return this.results;
	}

	/**
	 * Set the list of results.
	 * 
	 * @param aResults
	 *            {@code List} of results
	 */
	public void setCheckResults(List<EPPResellerCheckResult> aResults) {
		if (aResults == null) {
			this.results = new ArrayList<EPPResellerCheckResult>();
		}
		else {
			this.results = aResults;
		}
	}

	/**
	 * Set an individual check result. This method clears the existing list of
	 * check results.
	 * 
	 * @param aResult
	 *            An individual check result.
	 */
	public void setCheckResult(EPPResellerCheckResult aResult) {
		this.results = new ArrayList<EPPResellerCheckResult>();
		this.results.add(aResult);
	}

	/**
	 * Append a result to the list of results. This method does NOT clear the
	 * existing list of results.
	 * 
	 * @param aResult
	 *            A result to add to the list of results
	 */
	public void addCheckResult(EPPResellerCheckResult aResult) {
		this.results.add(aResult);
	}

	/**
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPResellerCheckResp</code> instance.
	 * 
	 * @param aDocument
	 *            DOM Document that is being built. Used as an Element factory.
	 * 
	 * @return Element Root DOM Element representing the
	 *         <code>EPPResellerCheckResp</code> instance.
	 * 
	 * @exception EPPEncodeException
	 *                Unable to encode <code>EPPResellerCheckResp</code>
	 *                instance.
	 */
	protected Element doEncode(Document aDocument) throws EPPEncodeException {

		// Check required attributes
		if (!this.hasCheckResults()) {
			throw new EPPEncodeException(
					"At least one result is needed with EPPResellerCheckResp");
		}

		// Create root element
		Element root = aDocument.createElementNS(EPPResellerMapFactory.NS,
				ELM_NAME);

		// Results
		EPPUtil.encodeCompList(aDocument, root, this.results);

		return root;
	}

	/**
	 * Decode the <code>EPPResellerCheckResp</code> attributes from the aElement
	 * DOM Element tree.
	 * 
	 * @param aElement
	 *            Root DOM Element to decode <code>EPPResellerCheckResp</code>
	 *            from.
	 * 
	 * @exception EPPDecodeException
	 *                Unable to decode aElement
	 */
	protected void doDecode(Element aElement) throws EPPDecodeException {
		// Results
		this.results = EPPUtil.decodeCompList(aElement,
				EPPResellerMapFactory.NS, EPPResellerCheckResult.ELM_NAME,
				EPPResellerCheckResult.class);
	}

	/**
	 * Get the EPP response type associated with
	 * <code>EPPResellerCheckResp</code>.
	 * 
	 * @return {@code EPPResellerCheckResp.ELM_NAME}
	 */
	public String getType() {
		return ELM_NAME;
	}

	/**
	 * Get the EPP command Namespace associated with
	 * <code>EPPResellerCheckResp</code>.
	 * 
	 * @return <code>EPPRegistryMapFactory.NS</code>
	 */
	public String getNamespace() {
		return EPPResellerMapFactory.NS;
	}

	/**
	 * Compare an instance of <code>EPPResellerCheckResp</code> with this
	 * instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return {@code true} if this object is the same as the aObject argument;
	 *         {@code false} otherwise
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPResellerCheckResp)) {
			return false;
		}

		if (!super.equals(aObject)) {
			return false;
		}

		EPPResellerCheckResp theMap = (EPPResellerCheckResp) aObject;

		// Results
		if (!EPPUtil.equalLists(this.results, theMap.results)) {
			cat.error("EPPResellerCheckResp.equals(): results not equal");
			return false;
		}

		return true;
	}

	/**
	 * Clone <code>EPPResellerCheckResp</code>.
	 * 
	 * @return clone of <code>EPPResellerCheckResp</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPResellerCheckResp clone = (EPPResellerCheckResp) super.clone();

		// Results
		clone.results = (List) ((ArrayList) results).clone();

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
