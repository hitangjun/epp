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
 * The extension to a response to an info command with the fee info extension.
 * 
 * @see com.verisign.epp.codec.fee.v06.EPPFeeInfo
 */
public class EPPFeeInfData implements EPPCodecComponent {
	/**
	 * XML local name for <code>EPPFeeInfData</code>.
	 */
	public static final String ELM_LOCALNAME = "infData";

	/** 
	 * XML root tag for <code>EPPFeeInfData</code>. 
	 */
	public static final String ELM_NAME = EPPFeeExtFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPFeeInfData.class.getName(),
			EPPCatFactory.getInstance().getFactory());

	/**
	 * XML local name for the currency
	 */
	private static final String ELM_CURRENCY = "currency";

	/**
	 * XML local name for the classification
	 */
	private static final String ELM_CLASSIFICATION = "class";

	/**
	 * Currency of the fees.
	 */
	private String currency;

	/**
	 * Command of the fee.
	 */
	private EPPFeeCommand command;

	/**
	 * Period of the fee.
	 */
	private EPPFeePeriod period = new EPPFeePeriod(1);

	/**
	 * OPTIONAL list of fees.
	 */
	private List<EPPFeeValue> fees = new ArrayList<EPPFeeValue>();

	/**
	 * OPTIONAL classification of the domain name
	 */
	private String classification;

	/**
	 * Default constructor for <code>EPPFeeInfData</code>.
	 */
	public EPPFeeInfData() {
	}

	/**
	 * Constructor for <code>EPPFeeInfData</code> that takes the required
	 * currency, command, and period attributes.
	 * 
	 * @param aCurrency
	 *            Currency of the fees
	 * @param aCommand
	 *            Command associated with the fees
	 * @param aPeriod
	 *            Registration period of the fees
	 */
	public EPPFeeInfData(String aCurrency, EPPFeeCommand aCommand,
			EPPFeePeriod aPeriod) {
		this.setCurrency(aCurrency);
		this.setCommand(aCommand);
		this.setPeriod(aPeriod);
	}

	/**
	 * Constructor for <code>EPPFeeInfData</code> that takes all the primary
	 * attributes.
	 * 
	 * @param aCurrency
	 *            Currency of the fees
	 * @param aCommand
	 *            Command associated with the fees
	 * @param aPeriod
	 *            Registration period of the fees
	 * @param aFee
	 *            A single fee value
	 */
	public EPPFeeInfData(String aCurrency, EPPFeeCommand aCommand,
			EPPFeePeriod aPeriod, EPPFeeValue aFee) {
		this.setCurrency(aCurrency);
		this.setCommand(aCommand);
		this.setPeriod(aPeriod);
		this.addFee(aFee);
	}

	/**
	 * Constructor for <code>EPPFeeInfData</code> that takes all attributes.
	 * 
	 * @param aCurrency
	 *            Currency of the fees
	 * @param aCommand
	 *            Command associated with the fees
	 * @param aPeriod
	 *            Registration period of the fees
	 * @param aFees
	 *            A list of fees that if set to <code>null</code> indicates no
	 *            fees
	 * @param aClassification An optional classification of the domain name. If
	 *        set to <code>null</code> there is no classification.
	 */
	public EPPFeeInfData(String aCurrency, EPPFeeCommand aCommand,
			EPPFeePeriod aPeriod, List<EPPFeeValue> aFees,
			String aClassification) {
		this.setCurrency(aCurrency);
		this.setCommand(aCommand);
		this.setPeriod(aPeriod);
		this.setFees(aFees);
		this.setClassification(aClassification);
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
	 * Gets the command value.
	 * 
	 * @return Command value if defined; <code>null</code> otherwise.
	 */
	public EPPFeeCommand getCommand() {
		return this.command;
	}

	/**
	 * Sets the command value.
	 * 
	 * @param aCommand
	 *            Command value
	 */
	public void setCommand(EPPFeeCommand aCommand) {
		this.command = aCommand;
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
		if (aPeriod != null) {
			this.period = aPeriod;
		}
		else {
			this.period = new EPPFeePeriod(1);
		}
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
	 * Is the classification defined?
	 * 
	 * @return <code>true</code> if the classification is defined;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasClassification() {
		return (this.classification != null ? true : false);
	}

	/**
	 * Gets the classification.
	 * 
	 * @return The classification value if defined; <code>null</code> otherwise.
	 */
	public String getClassification() {
		return this.classification;
	}

	/**
	 * Sets the classification.
	 * 
	 * @param aClassification
	 *            The classification to set
	 */
	public void setClassification(String aClassification) {
		this.classification = aClassification;
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
	 *                Error encoding <code>EPPFeeInfData</code>
	 */
	public Element encode(Document aDocument) throws EPPEncodeException {

		if (aDocument == null) {
			throw new EPPEncodeException("aDocument is null"
					+ " on in EPPFeeInfData.encode(Document)");
		}

		// Check for required attributes
		if (this.currency == null) {
			throw new EPPEncodeException(
					"Required currency attribute is null in EPPFeeInfData.encode(Document).");
		}
		if (this.command == null) {
			throw new EPPEncodeException(
					"Required command attribute is null in EPPFeeInfData.encode(Document).");
		}
		if (this.period == null) {
			throw new EPPEncodeException(
					"Required period attribute is null in EPPFeeInfData.encode(Document).");
		}

		// Create root element
		Element root = aDocument.createElementNS(EPPFeeExtFactory.NS, ELM_NAME);

		// Currency
		EPPUtil.encodeString(aDocument, root, this.currency,
				EPPFeeExtFactory.NS, EPPFeeExtFactory.NS_PREFIX + ":"
						+ ELM_CURRENCY);

		// Command
		EPPUtil.encodeComp(aDocument, root, this.command);

		// Period
		EPPUtil.encodeComp(aDocument, root, this.period);

		// Fees
		EPPUtil.encodeCompList(aDocument, root, this.fees);

		// Classification
		EPPUtil.encodeString(aDocument, root, this.classification,
				EPPFeeExtFactory.NS, EPPFeeExtFactory.NS_PREFIX + ":"
						+ ELM_CLASSIFICATION);

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

		// Command
		this.command = (EPPFeeCommand) EPPUtil.decodeComp(aElement,
				EPPFeeExtFactory.NS, EPPFeeCommand.ELM_NAME,
				EPPFeeCommand.class);

		// Period
		this.period = (EPPFeePeriod) EPPUtil.decodeComp(aElement,
				EPPFeeExtFactory.NS, EPPFeePeriod.ELM_NAME, EPPFeePeriod.class);

		// Fees
		this.fees = EPPUtil.decodeCompList(aElement, EPPFeeExtFactory.NS,
				EPPFeeValue.ELM_LOCALNAME, EPPFeeValue.class);

		// Classification
		this.classification = EPPUtil.decodeString(aElement,
				EPPFeeExtFactory.NS, ELM_CLASSIFICATION);

	}

	/**
	 * clone an <code>EPPCodecComponent</code>.
	 * 
	 * @return clone of concrete <code>EPPFeeInfData</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPFeeInfData clone = (EPPFeeInfData) super.clone();

		// Currency
		clone.currency = this.currency;

		// Command
		clone.command = (EPPFeeCommand) this.command.clone();

		// Period
		clone.period = (EPPFeePeriod) this.period.clone();

		// Fees
		clone.fees = new ArrayList<EPPFeeValue>();
		for (EPPFeeValue fee : this.fees) {
			clone.fees.add((EPPFeeValue) fee.clone());
		}

		// Classification
		clone.classification = this.classification;

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
	 * Compare an instance of <code>EPPFeeInfData</code> with this instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return <code>true</code> if equal; <code>false</code> otherwise.
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPFeeInfData)) {
			cat.error("EPPFeeInfData.equals(): " + aObject.getClass().getName()
					+ " not EPPFeeInfData instance");

			return false;
		}

		EPPFeeInfData other = (EPPFeeInfData) aObject;

		// Currency
		if (!EqualityUtil.equals(this.currency, other.currency)) {
			cat.error("EPPFeeInfData.equals(): currency not equal");
			return false;
		}

		// Command
		if (!EqualityUtil.equals(this.command, other.command)) {
			cat.error("EPPFeeInfData.equals(): command not equal");
			return false;
		}

		// Period
		if (!EqualityUtil.equals(this.period, other.period)) {
			cat.error("EPPFeeInfData.equals(): period not equal");
			return false;
		}

		// Fees
		if (!EqualityUtil.equals(this.fees, other.fees)) {
			cat.error("EPPFeeInfData.equals(): fees not equal");
			return false;
		}

		// Classification
		if (!EqualityUtil.equals(this.classification, other.classification)) {
			cat.error("EPPFeeInfData.equals(): classification not equal");
			return false;
		}
		return true;
	}
}
