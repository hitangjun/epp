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
package com.verisign.epp.codec.fee.v07;

import java.math.BigDecimal;

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
 * <code>EPPFeeCredit</code> represents the credit value information returned by
 * the server.
 */
public class EPPFeeCredit implements EPPCodecComponent {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPFeeCredit.class.getName(),
			EPPCatFactory.getInstance().getFactory());

	/**
	 * Constant for the phase local name
	 */
	public static final String ELM_LOCALNAME = "credit";

	/**
	 * Constant for the phase qualified name (prefix and local name)
	 */
	public static final String ELM_NAME = EPPFeeExtFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * OPTIONAL human-readable description of the credit.
	 */
	private static final String ATTR_DESCRIPTION = "description";

	/**
	 * OPTIONAL Human-readable description
	 */
	private String description;

	/**
	 * Value of the credit
	 */
	private BigDecimal credit;

	/**
	 * Default constructor. The command value MUST be set using the
	 * {@link #setCredit(BigDecimal)} method.
	 */
	public EPPFeeCredit() {
	}

	/**
	 * Create <code>EPPFeeCredit</code> instance with the required credit value.
	 * 
	 * @param aCredit
	 *            The credit value
	 */
	public EPPFeeCredit(BigDecimal aCredit) {
		this.setCredit(aCredit);
	}

	/**
	 * Create <code>EPPFeeCredit</code> instance with the required credit value
	 * and the optional description.
	 * 
	 * @param aCredit
	 *            The credit value
	 * @param aDescription
	 *            Human-readable description
	 */
	public EPPFeeCredit(BigDecimal aCredit, String aDescription) {
		this.setCredit(aCredit);
		this.setDescription(aDescription);
	}

	/**
	 * Is the description defined?
	 * 
	 * @return <code>true</code> if the description is defined;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasDescription() {
		return (this.description != null ? true : false);
	}

	/**
	 * Gets the description value.
	 * 
	 * @return Description if defined; <code>null</code> otherwise.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Sets the description value.
	 * 
	 * @param aDescription
	 *            Description value.
	 */
	public void setDescription(String aDescription) {
		this.description = aDescription;
		cat.debug("setDescription: description = " + this.description);
	}

	/**
	 * Gets the credit value.
	 * 
	 * @return Credit value
	 */
	public BigDecimal getCredit() {
		return this.credit;
	}

	/**
	 * Sets the credit value.
	 * 
	 * @param aCredit
	 *            Credit value
	 */
	public void setCredit(BigDecimal aCredit) {
		if (aCredit != null) {
			this.credit = aCredit;
			this.credit.setScale(2, BigDecimal.ROUND_HALF_UP);
		}
	}

	/**
	 * Clone <code>EPPFeeCredit</code>.
	 *
	 * @return clone of <code>EPPFeeCredit</code>
	 *
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPFeeCredit clone = null;

		clone = (EPPFeeCredit) super.clone();

		return clone;
	}

	/**
	 * Decode the <code>EPPFeeCredit</code> element aElement DOM Element tree.
	 *
	 * @param aElement
	 *            - Root DOM Element to decode <code>EPPFeeCredit</code> from.
	 *
	 * @exception EPPDecodeException
	 *                Unable to decode aElement
	 */
	public void decode(Element aElement) throws EPPDecodeException {
		// Description
		String theDescription = aElement.getAttribute(ATTR_DESCRIPTION);
		if (theDescription != null && !theDescription.isEmpty()) {
			this.description = theDescription;
		}
		else {
			this.description = null;
		}

		// Credit
		this.credit = EPPUtil.decodeBigDecimal((Element) aElement);
	}

	/**
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPFeeCredit</code> instance.
	 *
	 * @param aDocument
	 *            - DOM Document that is being built. Used as an Element
	 *            factory.
	 *
	 * @return Element - Root DOM Element representing the
	 *         <code>EPPFeeCredit</code> instance.
	 *
	 * @exception EPPEncodeException
	 *                - Unable to encode <code>EPPFeeCredit</code> instance.
	 */
	public Element encode(Document aDocument) throws EPPEncodeException {

		if (aDocument == null) {
			throw new EPPEncodeException("aDocument is null"
					+ " on in EPPFeeCredit.encode(Document)");
		}
		if (this.credit == null) {
			throw new EPPEncodeException("fee is null"
					+ " on in EPPFeeCredit.encode(Document)");
		}

		Element root = aDocument.createElementNS(EPPFeeExtFactory.NS, ELM_NAME);

		// Description
		if (this.description != null) {
			root.setAttribute(ATTR_DESCRIPTION, this.description);
		}

		// Fee
		EPPUtil.encodeBigDecimal(aDocument, root, this.credit, null);

		return root;
	}

	/**
	 * implements a deep <code>EPPFeeCredit</code> compare.
	 *
	 * @param aObject
	 *            <code>EPPFeeCredit</code> instance to compare with
	 *
	 * @return <code>true</code> if equal; <code>false</code> otherwise
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPFeeCredit)) {
			return false;
		}

		EPPFeeCredit other = (EPPFeeCredit) aObject;

		// Description
		if (!EqualityUtil.equals(this.description, other.description)) {
			cat.error("EPPFeeCredit.equals(): description not equal");
			return false;
		}

		// Credit
		if (!EqualityUtil.equals(this.credit, other.credit)) {
			cat.error("EPPFeeCredit.equals(): credit not equal");
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
