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

package com.verisign.epp.codec.fee.v08;

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
 * <code>EPPFeeDomainResult</code> represents a domain result to a check.
 */
public class EPPFeeDomainResult implements EPPCodecComponent {

	/**
	 * XML local name for <code>EPPFeeDomainResult</code>.
	 */
	public static final String ELM_LOCALNAME = "cd";

	/**
	 * XML root tag for <code>EPPFeeDomainResult</code>.
	 */
	public static final String ELM_NAME = EPPFeeExtFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPFeeDomainResult.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * XML local name for the name
	 */
	private static final String ELM_NAMEVAL = "name";

	/**
	 * XML local name for the currency
	 */
	private static final String ELM_CURRENCY = "currency";

	/**
	 * XML local name for the classification
	 */
	private static final String ELM_CLASSIFICATION = "class";

	/**
	 * The domain name.
	 */
	private String name;

	/**
	 * Currency of the fees.
	 */
	private String currency;

	/**
	 * Command of the fee.
	 */
	private EPPFeeCommand command;

	/**
	 * OPTIONAL period of the fee
	 */
	private EPPFeePeriod period;

	/**
	 * OPTIONAL list of fees.
	 */
	private List<EPPFeeValue> fees = new ArrayList<EPPFeeValue>();

	/**
	 * OPTIONAL list of credits.
	 */
	private List<EPPFeeCredit> credits = new ArrayList<EPPFeeCredit>();

	/**
	 * OPTIONAL classification of the domain name.
	 */
	private String classification;

	/**
	 * Default constructor for <code>EPPFeeDomainResult</code>.
	 */
	public EPPFeeDomainResult() {
	}

	/**
	 * Constructor for <code>EPPFeeDomainResult</code> that takes the required
	 * name, currency, and command attributes.
	 * 
	 * @param aName
	 *            Domain Name
	 * @param aCurrency
	 *            Currency of the fees
	 * @param aCommand
	 *            Command associated with the fees
	 */
	public EPPFeeDomainResult(String aName, String aCurrency,
			EPPFeeCommand aCommand) {
		this.setName(aName);
		this.setCurrency(aCurrency);
		this.setCommand(aCommand);
	}

	/**
	 * Constructor for <code>EPPFeeDomainResult</code> that takes all the
	 * primary attributes.
	 * 
	 * @param aName
	 *            Domain Name
	 * @param aCurrency
	 *            Currency of the fees
	 * @param aCommand
	 *            Command associated with the fees
	 * @param aPeriod
	 *            Registration period of the fees. Set to <code>null</code> to
	 *            specify no period.
	 * @param aFee
	 *            A single fee value
	 */
	public EPPFeeDomainResult(String aName, String aCurrency,
			EPPFeeCommand aCommand, EPPFeePeriod aPeriod, EPPFeeValue aFee) {
		this.setName(aName);
		this.setCurrency(aCurrency);
		this.setCommand(aCommand);
		this.setPeriod(aPeriod);
		this.addFee(aFee);
	}

	/**
	 * Constructor for <code>EPPFeeDomainResult</code> that takes all
	 * attributes.
	 * 
	 * @param aName
	 *            Domain Name
	 * @param aCurrency
	 *            Currency of the fees
	 * @param aCommand
	 *            Command associated with the fees
	 * @param aPeriod
	 *            Registration period of the fees. Set to <code>null</code> to
	 *            specify no period.
	 * @param aFees
	 *            A list of fees that if set to <code>null</code> indicates no
	 *            fees
	 * @param aCredits
	 *            A list of credits that if set to <code>null</code> indicates
	 *            no credits.
	 * @param aClassification
	 *            An optional classification of the domain name. If set to
	 *            <code>null</code> there is no classification.
	 */
	public EPPFeeDomainResult(String aName, String aCurrency,
			EPPFeeCommand aCommand, EPPFeePeriod aPeriod,
			List<EPPFeeValue> aFees, List<EPPFeeCredit> aCredits,
			String aClassification) {
		this.setName(aName);
		this.setCurrency(aCurrency);
		this.setCommand(aCommand);
		this.setPeriod(aPeriod);
		this.setFees(aFees);
		this.setCredits(aCredits);
		this.setClassification(aClassification);
	}

	/**
	 * Gets the name value.
	 * 
	 * @return Name value if defined; <code>null</code> otherwise.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name value.
	 * 
	 * @param aName
	 *            Name value
	 */
	public void setName(String aName) {
		this.name = aName;
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
	 * Sets the optional period value.
	 * 
	 * @param aPeriod
	 *            Period value. Set to <code>null</code> to specify no period.
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
	 * Are the credits defined?
	 * 
	 * @return <code>true</code> if the credits are defined; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasCredits() {
		return !this.credits.isEmpty();
	}

	/**
	 * Gets the list of credits if defined.
	 * 
	 * @return List of credits if defined; empty list otherwise.
	 */
	public List<EPPFeeCredit> getCredits() {
		return this.credits;
	}

	/**
	 * Adds a credit to the list of credits.
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
	 * Sets the list of credits.
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
	 *            the classification to set
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
	 *                Error encoding <code>EPPFeeDomainResult</code>
	 */
	public Element encode(Document aDocument) throws EPPEncodeException {

		if (aDocument == null) {
			throw new EPPEncodeException("aDocument is null"
					+ " on in EPPFeeDomainResult.encode(Document)");
		}

		// Check for required attributes
		if (this.name == null) {
			throw new EPPEncodeException(
					"Required name attribute is null in EPPFeeDomainResult.encode(Document).");
		}
		if (this.currency == null) {
			throw new EPPEncodeException(
					"Required currency attribute is null in EPPFeeDomainResult.encode(Document).");
		}
		if (this.command == null) {
			throw new EPPEncodeException(
					"Required command attribute is null in EPPFeeDomainResult.encode(Document).");
		}

		// Create root element
		Element root = aDocument.createElementNS(EPPFeeExtFactory.NS, ELM_NAME);

		// Name
		EPPUtil.encodeString(aDocument, root, this.name, EPPFeeExtFactory.NS,
				EPPFeeExtFactory.NS_PREFIX + ":" + ELM_NAMEVAL);

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

		// Credits
		EPPUtil.encodeCompList(aDocument, root, this.credits);

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

		// Name
		this.name = EPPUtil.decodeString(aElement, EPPFeeExtFactory.NS,
				ELM_NAMEVAL);

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

		// Credits
		this.credits = EPPUtil.decodeCompList(aElement, EPPFeeExtFactory.NS,
				EPPFeeCredit.ELM_LOCALNAME, EPPFeeCredit.class);

		// Classification
		this.classification = EPPUtil.decodeString(aElement,
				EPPFeeExtFactory.NS, ELM_CLASSIFICATION);

	}

	/**
	 * Clone an <code>EPPCodecComponent</code> instance.
	 * 
	 * @return clone of concrete <code>EPPFeeDomainResult</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPFeeDomainResult clone = (EPPFeeDomainResult) super.clone();

		// Name
		clone.name = this.name;

		// Currency
		clone.currency = this.currency;

		// Command
		clone.command = (EPPFeeCommand) this.command.clone();

		// Period
		if (this.period != null) {
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
	 * Compare an instance of <code>EPPFeeDomainResult</code> with this
	 * instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return <code>true</code> if equal; <code>false</code> otherwise.
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPFeeDomainResult)) {
			cat.error("EPPFeeDomainResult.equals(): "
					+ aObject.getClass().getName()
					+ " not EPPFeeDomainResult instance");

			return false;
		}

		EPPFeeDomainResult other = (EPPFeeDomainResult) aObject;

		// Name
		if (!EqualityUtil.equals(this.name, other.name)) {
			cat.error("EPPFeeDomainResult.equals(): name not equal");
			return false;
		}

		// Currency
		if (!EqualityUtil.equals(this.currency, other.currency)) {
			cat.error("EPPFeeDomainResult.equals(): currency not equal");
			return false;
		}

		// Command
		if (!EqualityUtil.equals(this.command, other.command)) {
			cat.error("EPPFeeDomainResult.equals(): command not equal");
			return false;
		}

		// Period
		if (!EqualityUtil.equals(this.period, other.period)) {
			cat.error("EPPFeeDomainResult.equals(): period not equal");
			return false;
		}

		// Fees
		if (!EqualityUtil.equals(this.fees, other.fees)) {
			cat.error("EPPFeeDomainResult.equals(): fees not equal");
			return false;
		}

		// Credits
		if (!EqualityUtil.equals(this.credits, other.credits)) {
			cat.error("EPPFeeDomainResult.equals(): credits not equal");
			return false;
		}

		// Classification
		if (!EqualityUtil.equals(this.classification, other.classification)) {
			cat.error("EPPFeeDomainResult.equals(): classification not equal");
			return false;
		}
		return true;
	}

}
