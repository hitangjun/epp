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

package com.verisign.epp.codec.fee.v09;

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
 * Abstract base class for the transform commands (create, renew, update,
 * transfer) for enabling the client to pass the expected fee for a billable
 * transform command.
 */
public abstract class EPPFeeTransform implements EPPCodecComponent {

	/**
	 * XML root tag for <code>EPPFeeTransform</code>.
	 */
	private String ELM_NAME = EPPFeeExtFactory.NS_PREFIX + ":" + getLocalName();

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPFeeTransform.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * XML local name for the currency
	 */
	private static final String ELM_CURRENCY = "currency";

	/**
	 * Currency desired by the client.
	 */
	private String currency;

	/**
	 * List of fees
	 */
	private List<EPPFeeValue> fees = new ArrayList<EPPFeeValue>();

	/**
	 * Abstract method that the sub-class must define to return the local name
	 * for the root element.
	 * 
	 * @return Local name of the root element of the transform command.
	 */
	protected abstract String getLocalName();

	/**
	 * Default constructor for <code>EPPFeeTransform</code>.
	 */
	public EPPFeeTransform() {
	}

	/**
	 * Constructor for <code>EPPFeeTransform</code> that takes a single fee.
	 * 
	 * @param aFee
	 *            A single fee of the transform command
	 */
	public EPPFeeTransform(EPPFeeValue aFee) {
		this.addFee(aFee);
	}
	
	/**
	 * Constructor for <code>EPPFeeTransform</code> that takes a single fee 
	 * and the optional currency.
	 * 
	 * @param aFee
	 *            A single fee of the transform command
	 * @param aCurrency
	 *            Currency of the fee            
	 */
	public EPPFeeTransform(EPPFeeValue aFee, String aCurrency) {
		this.addFee(aFee);
		this.setCurrency(aCurrency);
	}
	

	/**
	 * Constructor for <code>EPPFeeTransform</code> that takes all attributes.
	 * 
	 * @param aFees
	 *            The fees of the transform command
	 * @param aCurrency
	 *            Currency of the fees
	 */
	public EPPFeeTransform(List<EPPFeeValue> aFees, String aCurrency) {
		this.setFees(aFees);
		this.setCurrency(aCurrency);
	}

	/**
	 * Is the currency defined?
	 * 
	 * @return <code>true</code> if the currency is defined; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasCurrency() {
		return (this.currency != null ? true : false);
	}

	/**
	 * Gets the currency value.
	 * 
	 * @return Currency if defined; <code>null</code> otherwise.
	 */
	public String getCurrency() {
		return this.currency;
	}

	/**
	 * Sets the currency value.
	 * 
	 * @param aCurrency
	 *            Currency value
	 */
	public void setCurrency(String aCurrency) {
		this.currency = aCurrency;
	}

	/**
	 * Are the fees defined?
	 * 
	 * @return <code>true</code> if the fees are defined; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasFees() {
		return !this.fees.isEmpty();
	}

	/**
	 * Gets the list of fees if defined.
	 * 
	 * @return List of fees if defined; empty list otherwise.
	 */
	public List<EPPFeeValue> getFees() {
		return this.fees;
	}

	/**
	 * Adds a fee to the list of fees.
	 * 
	 * @param aFee
	 *            The fee to add.
	 */
	public void addFee(EPPFeeValue aFee) {
		if (aFee == null)
			return;

		this.fees.add(aFee);
	}

	/**
	 * Sets the list of fees.
	 * 
	 * @param aFees
	 *            The fees to set.
	 */
	public void setFees(List<EPPFeeValue> aFees) {
		if (aFees == null)
			this.fees = new ArrayList<EPPFeeValue>();
		else
			this.fees = aFees;
	}

	/**
	 * Encode instance into a DOM element tree. A DOM Document is passed as an
	 * argument and functions as a factory for DOM objects. The root element
	 * associated with the instance is created and each instance attribute is
	 * appended as a child node.
	 * 
	 * @param aDocument
	 *            DOM Document, which acts is an Element factory
	 * 
	 * @return Element Root element associated with the object
	 * 
	 * @exception EPPEncodeException
	 *                Error encoding <code>EPPFeeTransform</code>
	 */
	public Element encode(Document aDocument) throws EPPEncodeException {

		if (aDocument == null) {
			throw new EPPEncodeException("aDocument is null"
					+ " on in EPPFeeTransform.encode(Document)");
		}

		// Check for required attributes
		if (this.fees.isEmpty()) {
			throw new EPPEncodeException(
					"At least one fee is required in EPPFeeTransform.encode(Document).");
		}

		// Create root element
		Element root = aDocument.createElementNS(EPPFeeExtFactory.NS, ELM_NAME);

		// Currency
		EPPUtil.encodeString(aDocument, root, this.currency,
				EPPFeeExtFactory.NS, EPPFeeExtFactory.NS_PREFIX + ":"
						+ ELM_CURRENCY);

		// Fees
		EPPUtil.encodeCompList(aDocument, root, this.fees);

		return root;
	}

	/**
	 * Decode a DOM element tree to initialize the instance attributes. The
	 * <code>aElement</code> argument represents the root DOM element and is
	 * used to traverse the DOM nodes for instance attribute values.
	 * 
	 * @param aElement
	 *            <code>Element</code> to decode
	 * 
	 * @exception EPPDecodeException
	 *                Error decoding <code>Element</code>
	 */
	public void decode(Element aElement) throws EPPDecodeException {

		// Currency
		this.currency = EPPUtil.decodeString(aElement, EPPFeeExtFactory.NS,
				ELM_CURRENCY);
		if (this.currency != null && this.currency.isEmpty()) {
			this.currency = null;
		}

		// Fees
		this.fees = EPPUtil.decodeCompList(aElement, EPPFeeExtFactory.NS,
				EPPFeeValue.ELM_LOCALNAME, EPPFeeValue.class);
	}

	/**
	 * clone an <code>EPPCodecComponent</code>.
	 * 
	 * @return clone of concrete <code>EPPFeeTransform</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPFeeTransform clone = (EPPFeeTransform) super.clone();

		// Currency
		clone.currency = this.currency;

		// Fees
		clone.fees = new ArrayList<EPPFeeValue>();
		for (EPPFeeValue fee : this.fees) {
			clone.fees.add((EPPFeeValue) fee.clone());
		}

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
	 * Compare an instance of <code>EPPFeeTransform</code> with this instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return <code>true</code> if equal; <code>false</code> otherwise.
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPFeeTransform)) {
			cat.error("EPPFeeTransform.equals(): "
					+ aObject.getClass().getName()
					+ " not EPPFeeTransform instance");

			return false;
		}

		EPPFeeTransform other = (EPPFeeTransform) aObject;

		// Currency
		if (!EqualityUtil.equals(this.currency, other.currency)) {
			cat.error("EPPFeeTransform.equals(): currency not equal");
			return false;
		}

		// Fees
		if (!EqualityUtil.equals(this.fees, other.fees)) {
			cat.error("EPPFeeTransform.equals(): fees not equal");
			return false;
		}

		return true;
	}

}
