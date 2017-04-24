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

import com.verisign.epp.codec.gen.EPPCheckCmd;
import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.util.EPPCatFactory;

/**
 * Represents a reseller &lt;check&gt; command that enables checking on the
 * availability of a set of client-specified but server-unique reseller
 * identifiers.
 * 
 * @see com.verisign.epp.codec.reseller.EPPResellerCheckResp
 */
public class EPPResellerCheckCmd extends EPPCheckCmd {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPResellerCheckCmd.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * XML local name for <code>EPPResellerCheckCmd</code>.
	 */
	public static final String ELM_LOCALNAME = "check";

	/**
	 * XML root tag for <code>EPPResellerCheckCmd</code>.
	 */
	public static final String ELM_NAME = EPPResellerMapFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * XML local name for the reseller identifier element
	 */
	private static final String ELM_RESELLER_ID = "id";

	/**
	 * Reseller identifiers to check for availability.
	 */
	private List<String> resellerIds = new ArrayList<String>();

	/**
	 * <code>EPPResellerCheckCmd</code> default constructor.
	 */
	public EPPResellerCheckCmd() {
	}

	/**
	 * <code>EPPResellerCheckCmd</code> constructor that only takes the client
	 * transaction identifier
	 * 
	 * @param aTransId
	 *            Transaction Id associated with command.
	 */
	public EPPResellerCheckCmd(String aTransId) {
		super(aTransId);
	}

	/**
	 * <code>EPPResellerCheckCmd</code> constructor for setting an individual
	 * reseller identifier to check.
	 * 
	 * @param aTransId
	 *            Transaction Id associated with command.
	 * @param aResellerId
	 *            Reseller identifier to check
	 */
	public EPPResellerCheckCmd(String aTransId, String aResellerId) {
		super(aTransId);
		this.addResellerId(aResellerId);
	}

	/**
	 * Gets the EPP command Namespace associated with
	 * <code>EPPResellerCheckCmd</code>.
	 * 
	 * @return <code>EPPResellerMapFactory.NS</code>
	 */
	public String getNamespace() {
		return EPPResellerMapFactory.NS;
	}

	/**
	 * Are any reseller identifiers defined in the list of reseller identifiers?
	 * 
	 * @return <code>true</code> if there is at least one reseller identifier
	 *         defined; <code>false</code> otherwise.
	 */
	public boolean hasResellerIds() {
		if (this.resellerIds != null && !this.resellerIds.isEmpty()) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Get the list of reseller identifiers to check.
	 * 
	 * @return {@code List} of reseller identifier {@code String}'s
	 */
	public List<String> getResellerIds() {
		return this.resellerIds;
	}

	/**
	 * Set the list of reseller identifiers to check.
	 * 
	 * @param aResellerIds
	 *            {@code List} of reseller identifier {@code String}'s
	 */
	public void setResellerIds(List<String> aResellerIds) {
		if (aResellerIds == null) {
			this.resellerIds = new ArrayList<String>();
		}
		this.resellerIds = aResellerIds;
	}

	/**
	 * Set an individual reseller identifier to check. This method clears the
	 * existing list of reseller identifiers.
	 * 
	 * @param aResellerId
	 *            Reseller identifier to check
	 */
	public void setResellerId(String aResellerId) {
		this.resellerIds = new ArrayList<String>();
		this.resellerIds.add(aResellerId);
	}

	/**
	 * Append a reseller identifier to the list of reseller identifiers to
	 * check. This method does NOT clear the existing list of reseller
	 * identifiers.
	 * 
	 * @param aResellerId
	 *            Reseller identifier to check
	 */
	public void addResellerId(String aResellerId) {
		this.resellerIds.add(aResellerId);
	}

	/**
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPResellerCheckCmd</code> instance.
	 * 
	 * @param aDocument
	 *            DOM Document that is being built. Used as an Element factory.
	 * 
	 * @return Element Root DOM Element representing the
	 *         <code>EPPResellerCheckCmd</code> instance.
	 * 
	 * @exception EPPEncodeException
	 *                Unable to encode <code>EPPResellerCheckCmd</code>
	 *                instance.
	 */
	protected Element doEncode(Document aDocument) throws EPPEncodeException {

		// Check required attributes
		if (!this.hasResellerIds()) {
			throw new EPPEncodeException(
					"At least one reseller identifier is needed with EPPResellerCheckCmd");
		}

		// Create root element
		Element root = aDocument.createElementNS(EPPResellerMapFactory.NS,
				ELM_NAME);

		// Reseller Identifiers
		EPPUtil.encodeList(aDocument, root, this.resellerIds,
				EPPResellerMapFactory.NS, EPPResellerMapFactory.NS_PREFIX + ":"
						+ ELM_RESELLER_ID);

		return root;
	}

	/**
	 * Decode the <code>EPPResellerCheckCmd</code> attributes from the aElement
	 * DOM Element tree.
	 * 
	 * @param aElement
	 *            Root DOM Element to decode <code>EPPResellerCheckCmd</code>
	 *            from.
	 * 
	 * @exception EPPDecodeException
	 *                Unable to decode aElement
	 */
	protected void doDecode(Element aElement) throws EPPDecodeException {

		// Reseller Identifiers
		this.resellerIds = EPPUtil.decodeList(aElement,
				EPPResellerMapFactory.NS, ELM_RESELLER_ID);
	}

	/**
	 * Compare an instance of <code>EPPResellerCheckCmd</code> with this
	 * instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return {@code true} if this object is the same as the aObject argument;
	 *         {@code false} otherwise.
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPResellerCheckCmd)) {
			return false;
		}

		if (!super.equals(aObject)) {
			return false;
		}

		EPPResellerCheckCmd theMap = (EPPResellerCheckCmd) aObject;

		// Reseller Identifiers
		if (!EPPUtil.equalLists(this.resellerIds, theMap.resellerIds)) {
			cat.error("EPPResellerCheckCmd.equals(): resellerIds not equal");
			return false;
		}

		return true;
	}

	/**
	 * Clone <code>EPPResellerCheckCmd</code>.
	 * 
	 * @return Deep copy clone of <code>EPPResellerCheckCmd</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPResellerCheckCmd clone = (EPPResellerCheckCmd) super.clone();

		// Reseller Identifiers
		clone.resellerIds = (List) ((ArrayList) this.resellerIds).clone();

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
