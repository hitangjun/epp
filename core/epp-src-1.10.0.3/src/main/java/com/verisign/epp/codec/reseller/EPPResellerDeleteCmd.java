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

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPDeleteCmd;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EqualityUtil;

/**
 * Command used to delete a reseller object.
 */
public class EPPResellerDeleteCmd extends EPPDeleteCmd {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPResellerDeleteCmd.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * XML local name for <code>EPPResellerDeleteCmd</code>.
	 */
	public static final String ELM_LOCALNAME = "delete";

	/**
	 * XML root tag for <code>EPPResellerDeleteCmd</code>.
	 */
	public static final String ELM_NAME = EPPResellerMapFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * XML local name for the reseller identifier element
	 */
	private static final String ELM_RESELLER_ID = "id";

	/**
	 * The reseller identifier of the reseller of a sponsoring registrar.
	 */
	private String resellerId;

	/**
	 * <code>EPPResellerDeleteCmd</code> default constructor.
	 */
	public EPPResellerDeleteCmd() {
	}

	/**
	 * <code>EPPResellerDeleteCmd</code> constructor that only takes the client
	 * transaction identifier
	 * 
	 * @param aTransId
	 *            Transaction Id associated with command.
	 */
	public EPPResellerDeleteCmd(String aTransId) {
		super(aTransId);
	}

	/**
	 * <code>EPPResellerDeleteCmd</code> constructor that takes the transaction
	 * identifier and the reseller identifier.
	 * 
	 * @param aTransId
	 *            Transaction Id associated with command. Set to
	 *            <code>null</code> if a client transaction identifier is not
	 *            desired.
	 * @param aResellerId
	 *            Reseller identifier
	 */
	public EPPResellerDeleteCmd(String aTransId, String aResellerId) {
		super(aTransId);
		this.resellerId = aResellerId;
	}

	/**
	 * Gets the EPP command Namespace associated with
	 * <code>EPPResellerDeleteCmd</code>.
	 * 
	 * @return <code>EPPResellerMapFactory.NS</code>
	 */
	public String getNamespace() {
		return EPPResellerMapFactory.NS;
	}

	/**
	 * Gets the reseller identifier.
	 * 
	 * @return The reseller identifier if defined;<code>null</code> otherwise.
	 */
	public String getResellerId() {
		return this.resellerId;
	}

	/**
	 * Sets the reseller identifier.
	 * 
	 * @param aResellerId
	 *            The reseller identifier
	 */
	public void setResellerId(String aResellerId) {
		this.resellerId = aResellerId;
	}

	/**
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPResellerDeleteCmd</code> instance.
	 * 
	 * @param aDocument
	 *            DOM Document that is being built. Used as an Element factory.
	 * 
	 * @return Element Root DOM Element representing the
	 *         <code>EPPResellerDeleteCmd</code> instance.
	 * 
	 * @exception EPPEncodeException
	 *                Unable to encode <code>EPPResellerDeleteCmd</code>
	 *                instance.
	 */
	protected Element doEncode(Document aDocument) throws EPPEncodeException {

		// Check required attributes
		if (this.resellerId == null) {
			throw new EPPEncodeException(
					"Undefined resellerId in EPPResellerDeleteCmd");
		}

		// Create root element
		Element root = aDocument.createElementNS(EPPResellerMapFactory.NS,
				ELM_NAME);

		// Reseller Identifier
		EPPUtil.encodeString(aDocument, root, this.resellerId,
				EPPResellerMapFactory.NS, EPPResellerMapFactory.NS_PREFIX + ":"
						+ ELM_RESELLER_ID);

		return root;
	}

	/**
	 * Decode the <code>EPPResellerDeleteCmd</code> attributes from the aElement
	 * DOM Element tree.
	 * 
	 * @param aElement
	 *            Root DOM Element to decode <code>EPPResellerDeleteCmd</code>
	 *            from.
	 * 
	 * @exception EPPDecodeException
	 *                Unable to decode aElement
	 */
	protected void doDecode(Element aElement) throws EPPDecodeException {

		// Reseller Identifier
		this.resellerId = EPPUtil.decodeString(aElement,
				EPPResellerMapFactory.NS, ELM_RESELLER_ID);
	}

	/**
	 * Compare an instance of <code>EPPResellerDeleteCmd</code> with this
	 * instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return {@code true} if this object is the same as the aObject argument;
	 *         {@code false} otherwise.
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPResellerDeleteCmd)) {
			return false;
		}

		EPPResellerDeleteCmd other = (EPPResellerDeleteCmd) aObject;

		// Reseller Identifier
		if (!EqualityUtil.equals(this.resellerId, other.resellerId)) {
			cat.error("EPPResellerDeleteCmd.equals(): resellerId not equal");
			return false;
		}

		return true;
	}

	/**
	 * Clone <code>EPPResellerDeleteCmd</code>.
	 * 
	 * @return Deep copy clone of <code>EPPResellerDeleteCmd</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPResellerDeleteCmd clone = (EPPResellerDeleteCmd) super.clone();

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
