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

package com.verisign.epp.codec.fee.v07;

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
 * Fee Info Extension that enables a client to pass fee query information to get
 * fee information for the domain name back from the server.
 */
public class EPPFeeInfo implements EPPCodecComponent {

	/**
	 * XML local name for <code>EPPFeeInfo</code>.
	 */
	public static final String ELM_LOCALNAME = "info";

	/** 
	 * XML root tag for <code>EPPFeeInfo</code>. 
	 */
	public static final String ELM_NAME = EPPFeeExtFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPFeeInfo.class.getName(),
			EPPCatFactory.getInstance().getFactory());

	/**
	 * XML local name for the currency
	 */
	private static final String ELM_CURRENCY = "currency";

	/**
	 * Optional currency desired by the client.
	 */
	private String currency;

	/**
	 * Command to check for the fee
	 */
	private EPPFeeCommand command;

	/**
	 * Optional period
	 */
	private EPPFeePeriod period;

	/**
	 * Default constructor for <code>EPPFeeInfo</code>.
	 */
	public EPPFeeInfo() {
	}

	/**
	 * Constructor for <code>EPPFeeInfo</code> that takes the required command
	 * parameter.
	 * 
	 * @param aCommand
	 *            Command to check
	 */
	public EPPFeeInfo(EPPFeeCommand aCommand) {
		this.setCommand(aCommand);
	}

	/**
	 * Constructor for <code>EPPFeeInfo</code> that takes all attributes.
	 * 
	 * @param aCurrency
	 *            desired currency to return
	 * @param aCommand
	 *            Command to check
	 * @param aPeriod
	 *            Registration period to check
	 */
	public EPPFeeInfo(String aCurrency, EPPFeeCommand aCommand,
			EPPFeePeriod aPeriod) {
		this.setCurrency(aCurrency);
		this.setCommand(aCommand);
		this.setPeriod(aPeriod);
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
	 * Sets the period value.
	 * 
	 * @param aPeriod
	 *            Period value
	 */
	public void setPeriod(EPPFeePeriod aPeriod) {
		this.period = aPeriod;
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
	 *                Error encoding <code>EPPFeeInfo</code>
	 */
	public Element encode(Document aDocument) throws EPPEncodeException {

		if (aDocument == null) {
			throw new EPPEncodeException("aDocument is null"
					+ " on in EPPFeeInfo.encode(Document)");
		}

		// Check for required attributes
		if (this.command == null) {
			throw new EPPEncodeException(
					"Required command attribute is null in EPPFeeInfo.encode(Document).");
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

		// Command
		this.command = (EPPFeeCommand) EPPUtil.decodeComp(aElement,
				EPPFeeExtFactory.NS, EPPFeeCommand.ELM_NAME,
				EPPFeeCommand.class);

		// Period
		this.period = (EPPFeePeriod) EPPUtil.decodeComp(aElement,
				EPPFeeExtFactory.NS, EPPFeePeriod.ELM_NAME, EPPFeePeriod.class);
	}

	/**
	 * Clone an <code>EPPCodecComponent</code> instance.
	 * 
	 * @return clone of concrete <code>EPPFeeInfo</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPFeeInfo clone = (EPPFeeInfo) super.clone();

		// Currency
		clone.currency = this.currency;

		// Command
		if (this.command == null) {
			clone.command = null;
		}
		else {
			clone.command = (EPPFeeCommand) this.command.clone();
		}

		// Period
		if (this.period == null) {
			clone.period = null;
		}
		else {
			clone.period = (EPPFeePeriod) this.period.clone();
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
	 * Compare an instance of <code>EPPFeeInfo</code> with this instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return <code>true</code> if equal; <code>false</code> otherwise.
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPFeeInfo)) {
			cat.error("EPPFeeInfo.equals(): " + aObject.getClass().getName()
					+ " not EPPFeeInfo instance");

			return false;
		}

		EPPFeeInfo other = (EPPFeeInfo) aObject;

		// Currency
		if (!EqualityUtil.equals(this.currency, other.currency)) {
			cat.error("EPPFeeInfo.equals(): currency not equal");
			return false;
		}

		// Command
		if (!EqualityUtil.equals(this.command, other.command)) {
			cat.error("EPPFeeInfo.equals(): command not equal");
			return false;
		}

		// Period
		if (!EqualityUtil.equals(this.period, other.period)) {
			cat.error("EPPFeeInfo.equals(): period not equal");
			return false;
		}

		return true;
	}

}
