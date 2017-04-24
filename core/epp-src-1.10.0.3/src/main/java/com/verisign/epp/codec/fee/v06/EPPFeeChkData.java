/***********************************************************
Copyright (C) 2015 VeriSign, Inc.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.? See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA? 02111-1307? USA

http://www.verisign.com/nds/naming/namestore/techdocs.html
 ***********************************************************/
package com.verisign.epp.codec.fee.v06;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.verisign.epp.codec.gen.EPPCodecComponent;
import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EqualityUtil;

/**
 * The extension to a response to a check command with the fee check extension.
 * 
 * @see com.verisign.epp.codec.fee.v06.EPPFeeCheck
 * @see com.verisign.epp.codec.fee.v06.EPPFeeDomainResult
 */
public class EPPFeeChkData implements EPPCodecComponent {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPFeeChkData.class.getName(),
			EPPCatFactory.getInstance().getFactory());

	/**
	 * XML local name for <code>EPPFeeChkData</code>.
	 */
	public static final String ELM_LOCALNAME = "chkData";

	/**
	 * XML root tag for <code>EPPFeeChkData</code>.
	 */
	public static final String ELM_NAME = EPPFeeExtFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * List of claims check results
	 */
	private List<EPPFeeDomainResult> results = new ArrayList<EPPFeeDomainResult>();

	/**
	 * <code>EPPFeeChkData</code> default constructor. The results list will be
	 * empty.
	 */
	public EPPFeeChkData() {
	}

	/**
	 * <code>EPPFeeChkData</code> constructor that will set the result of an
	 * individual domain.
	 * 
	 * @param aResult
	 *            Result of a single domain name.
	 */
	public EPPFeeChkData(EPPFeeDomainResult aResult) {
		this.addCheckResult(aResult);
	}

	/**
	 * <code>EPPFeeChkData</code> constructor that will set the result of
	 * multiple domains.
	 * 
	 * @param aResults
	 *            List of check results
	 */
	public EPPFeeChkData(List<EPPFeeDomainResult> aResults) {
		this.setCheckResults(aResults);
	}

	/**
	 * Get the EPP command Namespace associated with <code>EPPFeeChkData</code>.
	 * 
	 * @return <code>EPPFeeExtFactory.NS</code>
	 */
	public String getNamespace() {
		return EPPFeeExtFactory.NS;
	}

	/**
	 * Implements a deep <code>EPPFeeChkData</code> compare.
	 * 
	 * @param aObject
	 *            <code>EPPFeeChkData</code> instance to compare with
	 * 
	 * @return <code>true</code> if equal <code>false</code> otherwise.
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPFeeChkData)) {
			cat.error("EPPFeeChkData.equals(): aObject is not an EPPFeeChkData");
			return false;
		}

		EPPFeeChkData other = (EPPFeeChkData) aObject;

		// Results
		if (!EqualityUtil.equals(this.results, other.results)) {
			cat.error("EPPFeeChkData.equals(): results not equal");
			return false;
		}

		return true;
	}

	/**
	 * Clone <code>EPPFeeChkData</code> instance.
	 * 
	 * @return clone of <code>EPPFeeChkData</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPFeeChkData clone = (EPPFeeChkData) super.clone();

		clone.results = new ArrayList<EPPFeeDomainResult>();

		for (EPPFeeDomainResult result : this.results) {
			clone.results.add((EPPFeeDomainResult) result.clone());
		}

		return clone;
	}

	/**
	 * Get the results of a <code>EPPFeeChkData</code> Response. There is one
	 * <code>EPPFeeDomainResult</code> instance in <code>this.results</code> for
	 * each domain requested in the Check Command.
	 * 
	 * @return List of results if defined; empty list otherwise.
	 */
	public List<EPPFeeDomainResult> getCheckResults() {
		return this.results;
	}

	/**
	 * Set the results of a <code>EPPFeeChkData</code> Response. There is one
	 * <code>EPPFeeDomainResult</code> instance in <code>this.results</code> for
	 * each domain requested in the Check Command.
	 * 
	 * @param aResults
	 *            List of claims check results
	 */
	public void setCheckResults(List<EPPFeeDomainResult> aResults) {
		if (aResults == null) {
			this.results = new ArrayList<EPPFeeDomainResult>();
		}
		else {
			this.results = aResults;
		}
	}

	/**
	 * Adds a check result to the list of results.
	 * 
	 * @param aResult
	 *            Check result to add to the list.
	 */
	public void addCheckResult(EPPFeeDomainResult aResult) {
		this.results.add(aResult);
	}

	/**
	 * Sets the result of an individual domain name.
	 * 
	 * @param aResult
	 *            Check result to set
	 */
	public void setCheckResult(EPPFeeDomainResult aResult) {
		this.results = new ArrayList<EPPFeeDomainResult>();
		this.results.add(aResult);
	}

	/**
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPFeeChkData</code> instance.
	 * 
	 * @param aDocument
	 *            DOM Document that is being built. Used as an Element factory.
	 * 
	 * @return Element Root DOM Element representing the
	 *         <code>EPPFeeChkData</code> instance.
	 * 
	 * @exception EPPEncodeException
	 *                Unable to encode <code>EPPFeeChkData</code> instance.
	 */
	public Element encode(Document aDocument) throws EPPEncodeException {

		if (aDocument == null) {
			throw new EPPEncodeException("aDocument is null"
					+ " on in EPPFeeChkData.encode(Document)");
		}

		Element root = aDocument.createElementNS(EPPFeeExtFactory.NS, ELM_NAME);

		// Results
		EPPUtil.encodeCompList(aDocument, root, this.results);

		return root;
	}

	/**
	 * Decode the <code>EPPFeeChkData</code> attributes from the aElement DOM
	 * Element tree.
	 * 
	 * @param aElement
	 *            Root DOM Element to decode <code>EPPFeeChkData</code> from.
	 * 
	 * @exception EPPDecodeException
	 *                Unable to decode aElement
	 */
	public void decode(Element aElement) throws EPPDecodeException {

		// Results
		this.results = EPPUtil.decodeCompList(aElement, EPPFeeExtFactory.NS,
				EPPFeeDomainResult.ELM_NAME, EPPFeeDomainResult.class);
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
