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

package com.verisign.epp.codec.fee.v06;

import java.math.BigDecimal;
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
 * Abstract base class for the transform results (create, renew, update, delete,
 * transfer) for enabling the server to return the currency, fees, credits,
 * balance, and credit limit where appropriate.
 */
public abstract class EPPFeeTransformResult implements EPPCodecComponent {

	/** 
	 * XML root tag for <code>EPPFeeTransformResult</code>. 
	 */
	private String ELM_NAME = EPPFeeExtFactory.NS_PREFIX + ":" + getLocalName();

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPFeeTransformResult.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * XML local name for the currency
	 */
	private static final String ELM_CURRENCY = "currency";

	/**
	 * XML local name for the balance
	 */
	private static final String ELM_BALANCE = "balance";

	/**
	 * XML local name for the credit limit
	 */
	private static final String ELM_CREDIT_LIMIT = "creditLimit";

	/**
	 * Currency of the fees or credits.
	 */
	private String currency;

	/**
	 * OPTIONAL period for use only with transfer query response.
	 */
	private EPPFeePeriod period;

	/**
	 * OPTIONAL list of fees
	 */
	private List<EPPFeeValue> fees = new ArrayList<EPPFeeValue>();

	/**
	 * OPTIONAL list of credits, used for the delete response
	 */
	private List<EPPFeeCredit> credits = new ArrayList<EPPFeeCredit>();

	/**
	 * OPTIONAL available balance for the client.
	 */
	private BigDecimal balance;

	/**
	 * OPTIONAL maximum credit available to the client.
	 */
	private BigDecimal creditLimit;

	/**
	 * Abstract method that the sub-class must define to return the local name
	 * for the root element.
	 * 
	 * @return Local name of the root element of the result.
	 */
	protected abstract String getLocalName();

	/**
	 * Validate the set of attributes on <code>encode</code>. Each subclass must
	 * provide their own implementation of <code>validateAttributes</code> to
	 * validate that the required attributes are set and invalid attribute are
	 * not set prior to the <code>encode</code>.
	 * 
	 * @throws EPPEncodeException
	 *             When an attribute is incorrect set or not set.
	 */
	protected abstract void validateAttributes() throws EPPEncodeException;

	/**
	 * Default constructor for <code>EPPFeeTransformResult</code>.
	 */
	public EPPFeeTransformResult() {
	}

	/**
	 * Constructor for <code>EPPFeeTransformResult</code> that takes the
	 * required currency parameter along with a single fee.
	 * 
	 * @param aCurrency
	 *            Currency of the fee
	 * @param aFee
	 *            A single fee value
	 */
	public EPPFeeTransformResult(String aCurrency, EPPFeeValue aFee) {
		this.setCurrency(aCurrency);
		this.addFee(aFee);
	}

	/**
	 * Constructor for <code>EPPFeeTransformResult</code> that takes the
	 * required currency parameter along with a single credit.
	 * 
	 * @param aCurrency
	 *            Currency of the fee
	 * @param aCredit
	 *            A single credit value
	 */
	public EPPFeeTransformResult(String aCurrency, EPPFeeCredit aCredit) {
		this.setCurrency(aCurrency);
		this.addCredit(aCredit);
	}
	
	
	/**
	 * Constructor for <code>EPPFeeTransformResult</code> that takes all
	 * attributes that includes fees.
	 * 
	 * @param aCurrency
	 *            The currency of the fees
	 * @param aFees
	 *            The fees of the transform command
	 * @param aBalance
	 *            The remaining client balance
	 * @param aCreditLimit
	 *            The maximum credit available to the client
	 */
	public EPPFeeTransformResult(String aCurrency, List<EPPFeeValue> aFees,
			BigDecimal aBalance, BigDecimal aCreditLimit) {
		this.setCurrency(aCurrency);
		this.setFees(aFees);
		this.setBalance(aBalance);
		this.setCreditLimit(aCreditLimit);
	}

	/**
	 * Constructor for <code>EPPFeeTransformResult</code> that takes all
	 * attributes that includes credits.
	 * 
	 * @param aCurrency
	 *            The currency of the fees
	 * @param aBalance
	 *            The remaining client balance
	 * @param aCreditLimit
	 *            The maximum credit available to the client
	 * @param aCredits
	 *            The credits of the command
	 */
	public EPPFeeTransformResult(String aCurrency, 
			BigDecimal aBalance, BigDecimal aCreditLimit,
			List<EPPFeeCredit> aCredits) {
		this.setCurrency(aCurrency);
		this.setCredits(aCredits);
		this.setBalance(aBalance);
		this.setCreditLimit(aCreditLimit);
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
	 * Is the period defined?
	 * 
	 * @return <code>true</code> if the period is defined; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasPeriod() {
		return (this.period != null ? true : false);
	}

	/**
	 * Gets the period value.
	 * 
	 * @return Period if defined; <code>null</code> otherwise.
	 */
	public EPPFeePeriod getPeriod() {
		return this.period;
	}

	/**
	 * Sets the period value.
	 * 
	 * @param aPeriod
	 *            Period value
	 */
	public void setPeriod(EPPFeePeriod aPeriod) {
		this.period = aPeriod;
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
	 * Are the credits defined? The credits are used on a delete response.
	 * 
	 * @return <code>true</code> if the credits are defined; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasCredits() {
		return !this.credits.isEmpty();
	}

	/**
	 * Gets the list of credits if defined. The credits are used on a delete
	 * response.
	 * 
	 * @return List of credits if defined; empty list otherwise.
	 */
	public List<EPPFeeCredit> getCredits() {
		return this.credits;
	}

	/**
	 * Adds a credit to the list of credits. The credits are used on a delete
	 * response.
	 * 
	 * @param aCredit
	 *            The credit to add.
	 */
	public void addCredit(EPPFeeCredit aCredit) {
		if (aCredit == null)
			return;

		this.credits.add(aCredit);
	}

	/**
	 * Sets the list of credits. The credits are used on a delete response.
	 * 
	 * @param aCredits
	 *            The credits to set.
	 */
	public void setCredits(List<EPPFeeCredit> aCredits) {
		if (aCredits == null)
			this.credits = new ArrayList<EPPFeeCredit>();
		else
			this.credits = aCredits;
	}

	/**
	 * Is the balance defined?
	 * 
	 * @return <code>true</code> if the balance is defined; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasBalance() {
		return (this.balance != null ? true : false);
	}

	/**
	 * Gets the available balance of the client. The balance can be negative to
	 * indicate that the server has provided the client with credit.
	 * 
	 * @return the balance if defined; <code>null</code> otherwise.
	 */
	public BigDecimal getBalance() {
		return this.balance;
	}

	/**
	 * Sets the available balance of the client. The balance can be negative to
	 * indicate that the server has provided the client with credit.
	 * 
	 * @param aBalance
	 *            The available balance.
	 */
	public void setBalance(BigDecimal aBalance) {
		this.balance = aBalance;
	}

	/**
	 * Is the credit limit defined?
	 * 
	 * @return <code>true</code> if the credit limit is defined;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasCreditLimit() {
		return (this.creditLimit != null ? true : false);
	}

	/**
	 * Gets the maximum credit available to the client. This reflects how
	 * negative the <code>balance</code> attribute can go to.
	 * 
	 * @return The maximum credit available to the client if defiend;
	 *         <code>null</code> otherwise.
	 */
	public BigDecimal getCreditLimit() {
		return this.creditLimit;
	}

	/**
	 * Sets the maximum credit available to the client. This reflects how
	 * negative the <code>balance</code> attribute can go to.
	 * 
	 * @param aCreditLimit
	 *            The maximum credit available to the client.
	 */
	public void setCreditLimit(BigDecimal aCreditLimit) {
		this.creditLimit = aCreditLimit;
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
	 *                Error encoding <code>EPPFeeTransformResult</code>
	 */
	public Element encode(Document aDocument) throws EPPEncodeException {

		if (aDocument == null) {
			throw new EPPEncodeException("aDocument is null"
					+ " on in EPPFeeTransformResult.encode(Document)");
		}

		// Check for required attributes
		validateAttributes();

		// Create root element
		Element root = aDocument.createElementNS(EPPFeeExtFactory.NS, ELM_NAME);

		// Currency
		EPPUtil.encodeString(aDocument, root, this.currency,
				EPPFeeExtFactory.NS, EPPFeeExtFactory.NS_PREFIX + ":"
						+ ELM_CURRENCY);
		// Period
		EPPUtil.encodeComp(aDocument, root, this.period);

		// Fees
		EPPUtil.encodeCompList(aDocument, root, this.fees);

		// Credits
		EPPUtil.encodeCompList(aDocument, root, this.credits);

		// Balance
		EPPUtil.encodeBigDecimal(aDocument, root, this.balance,
				EPPFeeExtFactory.NS, EPPFeeExtFactory.NS_PREFIX + ":"
						+ ELM_BALANCE, null);

		// creditLimit
		EPPUtil.encodeBigDecimal(aDocument, root, this.creditLimit,
				EPPFeeExtFactory.NS, EPPFeeExtFactory.NS_PREFIX + ":"
						+ ELM_CREDIT_LIMIT, null);

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

		// Period
		this.period = (EPPFeePeriod) EPPUtil.decodeComp(aElement,
				EPPFeeExtFactory.NS, EPPFeePeriod.ELM_NAME, EPPFeePeriod.class);

		// Fees
		this.fees = EPPUtil.decodeCompList(aElement, EPPFeeExtFactory.NS,
				EPPFeeValue.ELM_LOCALNAME, EPPFeeValue.class);

		// Credits
		this.credits = EPPUtil.decodeCompList(aElement, EPPFeeExtFactory.NS,
				EPPFeeCredit.ELM_LOCALNAME, EPPFeeCredit.class);

		// Balance
		this.balance = EPPUtil.decodeBigDecimal(aElement, EPPFeeExtFactory.NS,
				ELM_BALANCE);

		// Credit Limit
		this.creditLimit = EPPUtil.decodeBigDecimal(aElement,
				EPPFeeExtFactory.NS, ELM_CREDIT_LIMIT);

	}

	/**
	 * Clone an <code>EPPFeeTransformResult</code> instance.
	 * 
	 * @return clone of concrete <code>EPPFeeTransformResult</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPFeeTransformResult clone = (EPPFeeTransformResult) super.clone();

		// Currency
		clone.currency = this.currency;

		// Period
		if (this.period == null) {
			clone.period = null;
		}
		else {
			clone.period = (EPPFeePeriod) this.period.clone();
		}

		// Fees
		clone.fees = new ArrayList<EPPFeeValue>();
		for (EPPFeeValue fee : this.fees) {
			clone.fees.add((EPPFeeValue) fee.clone());
		}

		// Credits
		clone.credits = new ArrayList<EPPFeeCredit>();
		for (EPPFeeCredit credit : this.credits) {
			clone.credits.add((EPPFeeCredit) credit.clone());
		}

		// Balance
		if (this.balance != null) {
			clone.balance = new BigDecimal(this.balance.toString());
		}
		else {
			clone.balance = null;
		}

		// Credit Limit
		if (this.creditLimit != null) {
			clone.creditLimit = new BigDecimal(this.creditLimit.toString());
		}
		else {
			clone.creditLimit = null;
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
	 * Compare an instance of <code>EPPFeeTransformResult</code> with this
	 * instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return <code>true</code> if equal; <code>false</code> otherwise.
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPFeeTransformResult)) {
			cat.error("EPPFeeTransformResult.equals(): "
					+ aObject.getClass().getName()
					+ " not EPPFeeTransformResult instance");

			return false;
		}

		EPPFeeTransformResult other = (EPPFeeTransformResult) aObject;

		// Currency
		if (!EqualityUtil.equals(this.currency, other.currency)) {
			cat.error("EPPFeeTransformResult.equals(): currency not equal");
			return false;
		}

		// Period
		if (!EqualityUtil.equals(this.period, other.period)) {
			cat.error("EPPFeeInfo.equals(): period not equal");
			return false;
		}

		// Fees
		if (!EqualityUtil.equals(this.fees, other.fees)) {
			cat.error("EPPFeeTransformResult.equals(): fees not equal");
			return false;
		}

		// Credits
		if (!EqualityUtil.equals(this.credits, other.credits)) {
			cat.error("EPPFeeTransformResult.equals(): credits not equal");
			return false;
		}

		// Balance
		if (!EqualityUtil.equals(this.balance, other.balance)) {
			cat.error("EPPFeeTransformResult.equals(): balance not equal");
			return false;
		}

		// Credit Limit
		if (!EqualityUtil.equals(this.creditLimit, other.creditLimit)) {
			cat.error("EPPFeeTransformResult.equals(): creditLimit not equal");
			return false;
		}

		return true;
	}

}
