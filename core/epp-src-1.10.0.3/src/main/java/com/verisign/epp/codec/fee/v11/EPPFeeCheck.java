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

package com.verisign.epp.codec.fee.v11;

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
 * Fee Check Extension that enables a client to pass a command along with
 * optionally the currency, period, and fee class to apply to the objects in the
 * check command.
 */
public class EPPFeeCheck implements EPPCodecComponent {

	/**
	 * XML local name for <code>EPPFeeCheck</code>.
	 */
	public static final String ELM_LOCALNAME = "check";

	/**
	 * XML root tag for <code>EPPFeeCheck</code>.
	 */
	public static final String ELM_NAME = EPPFeeExtFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPFeeCheck.class.getName(),
			EPPCatFactory.getInstance().getFactory());

	/**
	 * XML local name for the currency
	 */
	private static final String ELM_CURRENCY = "currency";

	/**
	 * XML local name for the fee classification
	 */
	private static final String ELM_CLASSIFICATION = "class";

	/**
	 * Command to check for the fee
	 */
	private EPPFeeCommand command;

	/**
	 * OPTIONAL currency desired by the client.
	 */
	private String currency;

	/**
	 * OPTIONAL period
	 */
	private EPPFeePeriod period;

	/**
	 * OPTIONAL fee classification
	 */
	private String classification;

	/**
	 * Default constructor for <code>EPPFeeCheck</code>.
	 */
	public EPPFeeCheck() {
	}

	/**
	 * Constructor for <code>EPPFeeCheck</code> that takes the required command
	 * attribute.
	 * 
	 * @param aCommand
	 *            Command to check
	 */
	public EPPFeeCheck(EPPFeeCommand aCommand) {
		this.setCommand(aCommand);
	}

	/**
	 * Constructor for <code>EPPFeeCheck</code> that takes the required command
	 * attribute and all of the optional attributes..
	 * 
	 * @param aCommand
	 *            Command to check
	 * @param aCurrency
	 *            OPTIONAL desired currency to return
	 * @param aPeriod
	 *            OPTIONAL Registration period to check
	 * @param aClassification
	 *            OPTIONAL classification value
	 */
	public EPPFeeCheck(EPPFeeCommand aCommand, String aCurrency,
			EPPFeePeriod aPeriod, String aClassification) {
		this.setCommand(aCommand);
		this.setCurrency(aCurrency);
		this.setPeriod(aPeriod);
		this.setClassification(aClassification);
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
	 * Is the fee classification defined?
	 * 
	 * @return <code>true</code> if the fee classification is defined; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasClassification() {
		return (this.classification != null ? true : false);
	}

	/**
	 * Gets the fee classification value.
	 * 
	 * @return Fee classification if defined; <code>null</code> otherwise.
	 */
	public String getClassification() {
		return this.classification;
	}

	/**
	 * Sets the fee classification value.
	 * 
	 * @param aClassification
	 *            fee classification value
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
	 *                Error encoding <code>EPPFeeCheck</code>
	 */
	public Element encode(Document aDocument) throws EPPEncodeException {

		if (aDocument == null) {
			throw new EPPEncodeException("aDocument is null"
					+ " on in EPPFeeCheck.encode(Document)");
		}

		// Create root element
		Element root = aDocument.createElementNS(EPPFeeExtFactory.NS, ELM_NAME);

		// Command
		EPPUtil.encodeComp(aDocument, root, this.command);

		// Currency
		EPPUtil.encodeString(aDocument, root, this.currency,
				EPPFeeExtFactory.NS,
				EPPFeeExtFactory.NS_PREFIX + ":" + ELM_CURRENCY);

		// Period
		EPPUtil.encodeComp(aDocument, root, this.period);

		// Fee classification
		EPPUtil.encodeString(aDocument, root, this.classification,
				EPPFeeExtFactory.NS,
				EPPFeeExtFactory.NS_PREFIX + ":" + ELM_CLASSIFICATION);

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

		// Command
		this.command = (EPPFeeCommand) EPPUtil.decodeComp(aElement,
				EPPFeeExtFactory.NS, EPPFeeCommand.ELM_NAME,
				EPPFeeCommand.class);

		// Currency
		this.currency = EPPUtil.decodeString(aElement, EPPFeeExtFactory.NS,
				ELM_CURRENCY);
		if (this.currency != null && this.currency.isEmpty()) {
			this.currency = null;
		}

		// Period
		this.period = (EPPFeePeriod) EPPUtil.decodeComp(aElement,
				EPPFeeExtFactory.NS, EPPFeePeriod.ELM_NAME, EPPFeePeriod.class);

		// Fee classification
		this.classification = EPPUtil.decodeString(aElement, EPPFeeExtFactory.NS,
				ELM_CLASSIFICATION);
		if (this.classification != null && this.classification.isEmpty()) {
			this.classification = null;
		}

	}

	/**
	 * Clone an <code>EPPCodecComponent</code> instance.
	 * 
	 * @return clone of concrete <code>EPPFeeCheck</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {

		EPPFeeCheck clone = (EPPFeeCheck) super.clone();

		// Command
		if (this.command == null) {
			clone.command = null;
		}
		else {
			clone.command = (EPPFeeCommand) this.command.clone();
		}

		// Currency
		clone.currency = this.currency;

		// Period
		if (this.period == null) {
			clone.period = null;
		}
		else {
			clone.period = (EPPFeePeriod) this.period.clone();
		}

		// Fee classification
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
	 * Compare an instance of <code>EPPFeeCheck</code> with this instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return <code>true</code> if equal; <code>false</code> otherwise.
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPFeeCheck)) {
			cat.error("EPPFeeCheck.equals(): " + aObject.getClass().getName()
					+ " not EPPFeeCheck instance");

			return false;
		}

		EPPFeeCheck other = (EPPFeeCheck) aObject;

		// Command
		if (!EqualityUtil.equals(this.command, other.command)) {
			cat.error("EPPFeeCheck.equals(): command not equal");
			return false;
		}

		// Currency
		if (!EqualityUtil.equals(this.currency, other.currency)) {
			cat.error("EPPFeeCheck.equals(): currency not equal");
			return false;
		}

		// Period
		if (!EqualityUtil.equals(this.period, other.period)) {
			cat.error("EPPFeeCheck.equals(): period not equal");
			return false;
		}

		// Fee classification
		if (!EqualityUtil.equals(this.classification, other.classification)) {
			cat.error("EPPFeeCheck.equals(): fee classification not equal");
			return false;
		}

		return true;
	}

}
