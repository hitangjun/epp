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

import java.util.Date;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPResponse;
import com.verisign.epp.codec.gen.EPPTransId;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EqualityUtil;

/**
 * Represents an EPP Internationalized Domain Name (IDN) Table &lt;info&gt;
 * response, which support three different forms:<br>
 * <ul>
 * <li>Domain Info Form - Used to validate the domain name code points against
 * the IDN Tables and IDN Policies, and to return the matching IDN Table
 * meta-data.</li>
 * <li>Table Info Form - Used to retrieve information associated with an IDN
 * Table object.</li>
 * <li>List Info Form - Used to retrieve the list of IDN Tables supported by the
 * server.</li>
 * </ul>
 * 
 * <br>
 * <code>EPPResellerInfoCmd</code> is the concrete <code>EPPCommand</code>
 * associated with <code>EPPResellerInfoCmd</code>.
 * 
 * @see com.verisign.epp.codec.reseller.EPPResellerCreateResp
 */
public class EPPResellerCreateResp extends EPPResponse {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPResellerCreateResp.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * XML local name for <code>EPPResellerCreateResp</code>.
	 */
	public static final String ELM_LOCALNAME = "creData";

	/**
	 * XML root tag for <code>EPPResellerCreateResp</code>.
	 */
	public static final String ELM_NAME = EPPResellerMapFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * XML local name for the reseller identifier element
	 */
	private static final String ELM_RESELLER_ID = "id";

	/**
	 * XML local name for the created date (<code>crDate</code>) element.
	 */
	private final static String ELM_CREATED_DATE = "crDate";

	/**
	 * The reseller identifier of the reseller of a sponsoring registrar.
	 */
	private String resellerId;

	/**
	 * Date and time the reseller was created.
	 */
	private Date createdDate;

	/**
	 * <code>EPPResellerCreateResp</code> default constructor.
	 */
	public EPPResellerCreateResp() {
	}

	/**
	 * <code>EPPResellerCreateResp</code> constructor that only takes the
	 * transaction identifier.
	 * 
	 * @param aTransId
	 *            Transaction Id associated with response.
	 */
	public EPPResellerCreateResp(EPPTransId aTransId) {
		super(aTransId);
	}

	/**
	 * <code>EPPResellerCreateResp</code> constructor that all attributes as
	 * parameters.
	 * 
	 * @param aTransId
	 *            Transaction Id associated with command. Set to
	 *            <code>null</code> if a client transaction identifier is not
	 *            desired.
	 * @param aResellerId
	 *            Reseller identifier
	 * @param aCreatedDate
	 *            Reseller created date
	 * 
	 */
	public EPPResellerCreateResp(EPPTransId aTransId, String aResellerId,
			Date aCreatedDate) {
		super(aTransId);
		this.resellerId = aResellerId;
		this.createdDate = aCreatedDate;
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
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return this.createdDate;
	}

	/**
	 * @param aCreatedDate
	 *            the createdDate to set
	 */
	public void setCreatedDate(Date aCreatedDate) {
		this.createdDate = aCreatedDate;
	}

	/**
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPResellerCreateResp</code> instance.
	 * 
	 * @param aDocument
	 *            DOM Document that is being built. Used as an Element factory.
	 * 
	 * @return Element Root DOM Element representing the EPPResellerCreateResp
	 *         instance.
	 * 
	 * @exception EPPEncodeException
	 *                Unable to encode EPPResellerCreateResp instance.
	 */
	protected Element doEncode(Document aDocument) throws EPPEncodeException {

		Element theElm = null;
		Text theVal = null;

		// Check required attributes
		if (this.resellerId == null) {
			throw new EPPEncodeException(
					"Undefined resellerId in EPPResellerCreateResp");
		}
		if (this.createdDate == null) {
			throw new EPPEncodeException(
					"Undefined createdDate in EPPResellerCreateResp");
		}

		// Create root element
		Element root = aDocument.createElementNS(EPPResellerMapFactory.NS,
				ELM_NAME);

		// Reseller Identifier
		EPPUtil.encodeString(aDocument, root, this.resellerId,
				EPPResellerMapFactory.NS, EPPResellerMapFactory.NS_PREFIX + ":"
						+ ELM_RESELLER_ID);

		// Created Date
		EPPUtil.encodeTimeInstant(aDocument, root, this.createdDate,
				EPPResellerMapFactory.NS, EPPResellerMapFactory.NS_PREFIX + ":"
						+ ELM_CREATED_DATE);

		return root;
	}

	/**
	 * Decode the <code>EPPResellerCreateResp</code> attributes from the
	 * aElement DOM Element tree.
	 * 
	 * @param aElement
	 *            Root DOM Element to decode <code>EPPResellerCreateResp</code>
	 *            from.
	 * 
	 * @exception EPPDecodeException
	 *                Unable to decode aElement
	 */
	protected void doDecode(Element aElement) throws EPPDecodeException {

		Element theElm = null;

		// Reseller Identifier
		this.resellerId = EPPUtil.decodeString(aElement,
				EPPResellerMapFactory.NS, ELM_RESELLER_ID);

		// Created Date
		this.createdDate = EPPUtil.decodeTimeInstant(aElement,
				EPPResellerMapFactory.NS, ELM_CREATED_DATE);
	}

	/**
	 * Clone <code>EPPResellerCreateResp</code>.
	 * 
	 * @return clone of <code>EPPResellerCreateResp</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPResellerCreateResp clone = (EPPResellerCreateResp) super.clone();
		return clone;
	}

	/**
	 * Gets the EPP response type associated with
	 * <code>EPPResellerCreateResp</code>.
	 * 
	 * @return <code>EPPResellerCreateResp.ELM_NAME</code>
	 */
	public String getType() {
		return ELM_NAME;
	}

	/**
	 * Gets the EPP command namespace associated with
	 * <code>EPPResellerCreateResp</code>.
	 * 
	 * @return <code>EPPResellerMapFactory.NS</code>
	 */
	public String getNamespace() {
		return EPPResellerMapFactory.NS;
	}

	/**
	 * Compare an instance of <code>EPPResellerCreateResp</code> with this
	 * instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return {@code true} if this object is the same as the aObject argument;
	 *         {@code false} otherwise
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPResellerCreateResp)) {
			return false;
		}

		EPPResellerCreateResp other = (EPPResellerCreateResp) aObject;

		// Reseller Identifier
		if (!EqualityUtil.equals(this.resellerId, other.resellerId)) {
			cat.error("EPPResellerCreateResp.equals(): resellerId not equal");
			return false;
		}

		// Created Date
		if (!EqualityUtil.equals(this.createdDate, other.createdDate)) {
			cat.error("EPPResellerCreateResp.equals(): createdDate not equal");
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