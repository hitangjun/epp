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

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.verisign.epp.codec.gen.EPPCodecComponent;
import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EqualityUtil;

/**
 * <code>EPPFeeObject</code> represents an object to check for the fee using the
 * {@link EPPFeeCheck} extension.
 */
public class EPPFeeObject implements EPPCodecComponent {

	/**
	 * XML local name for <code>EPPFeeObject</code>.
	 */
	public static final String ELM_LOCALNAME = "object";

	/**
	 * XML root tag for <code>EPPFeeObject</code>.
	 */
	public static final String ELM_NAME = EPPFeeExtFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * Default value of the &quot;objURI&quot; attribute of the object element.
	 */
	public static final String DEFAULT_OBJ_URI = "urn:ietf:params:xml:ns:domain-1.0";

	/**
	 * Default value of the &quot;objURI&quot; &quot;element&quot; attribute.
	 */
	public static final String DEFAULT_OBJ_ELEMENT = "name";

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPFeeObject.class.getName(),
			EPPCatFactory.getInstance().getFactory());

	/**
	 * OPTIONAL &quot;objURI&quot; attribute which is used to identify the type
	 * of the object. The default value is set to {@link #DEFAULT_OBJ_URI}.
	 */
	private String objURI = DEFAULT_OBJ_URI;

	/**
	 * XML local name for the object identifier
	 */
	private static final String ELM_OBJ_ID = "objID";

	/**
	 * XML local name for the currency
	 */
	private static final String ELM_CURRENCY = "currency";

	/**
	 * &quot;objURI&quot; attribute constant.
	 */
	private static final String ATTR_OBJ_URI = "objURI";

	/**
	 * &quot;element&quot; attribute constant.
	 */
	private static final String ATTR_OBJ_ELEMENT = "element";

	/**
	 * The string that identifies the object, which most likely corresponds to
	 * the &lt;domain:name&gt; element that appears in the &lt;domain:check&gt;
	 * element.
	 */
	private String objID;

	/**
	 * OPTIONAL &quot;element&quot; attribute which identifies the local name of
	 * the object attribute which is the unique identifier for the object. The
	 * default value is {@link #DEFAULT_OBJ_ELEMENT}.
	 */
	private String objElement = DEFAULT_OBJ_ELEMENT;

	/**
	 * OPTIONAL currency desired by the client.
	 */
	private String currency;

	/**
	 * Command to check for the fee
	 */
	private EPPFeeCommand command;

	/**
	 * OPTIONAL period
	 */
	private EPPFeePeriod period;

	/**
	 * Default constructor for <code>EPPFeeObject</code>.
	 */
	public EPPFeeObject() {
	}

	/**
	 * Constructor for <code>EPPFeeObject</code> that takes the required object
	 * identifier and command parameters.
	 * 
	 * @param aObjID
	 *            Object identifier to check
	 * @param aCommand
	 *            Command to check
	 */
	public EPPFeeObject(String aObjID, EPPFeeCommand aCommand) {
		this.setObjID(aObjID);
		this.setCommand(aCommand);
	}

	/**
	 * Constructor for <code>EPPFeeObject</code> that takes the most likely
	 * attributes.
	 * 
	 * @param aObjID
	 *            Object identifier to check
	 * @param aCurrency
	 *            desired currency to return
	 * @param aCommand
	 *            Command to check
	 * @param aPeriod
	 *            Registration period to check
	 */
	public EPPFeeObject(String aObjID, String aCurrency,
			EPPFeeCommand aCommand, EPPFeePeriod aPeriod) {
		this.setObjID(aObjID);
		this.setCurrency(aCurrency);
		this.setCommand(aCommand);
		this.setPeriod(aPeriod);
	}

	/**
	 * Constructor for <code>EPPFeeObject</code> that takes all attributes.
	 * 
	 * @param aObjID
	 *            Object identifier to check
	 * @param aCurrency
	 *            desired currency to return
	 * @param aCommand
	 *            Command to check
	 * @param aPeriod
	 *            Registration period to check
	 * @param aObjURI
	 *            Type of object with the default value of
	 *            {@link #DEFAULT_OBJ_URI}.
	 * @param aObjElement
	 *            Local name of the object attribute with the default value of
	 *            {@link #DEFAULT_OBJ_ELEMENT}.
	 */
	public EPPFeeObject(String aObjID, String aCurrency,
			EPPFeeCommand aCommand, EPPFeePeriod aPeriod, String aObjURI,
			String aObjElement) {
		this.setObjID(aObjID);
		this.setCurrency(aCurrency);
		this.setCommand(aCommand);
		this.setPeriod(aPeriod);
		this.setObjURI(aObjURI);
		this.setObjElement(aObjElement);
	}

	/**
	 * Gets the object identifier value.
	 * 
	 * @return Object identifier value if defined; <code>null</code> otherwise.
	 */
	public String getObjID() {
		return this.objID;
	}

	/**
	 * Sets the object identifier value.
	 * 
	 * @param aObjID
	 *            Object identifier value
	 */
	public void setObjID(String aObjID) {
		this.objID = aObjID;
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
	 * Gets the &quot;objURI&quot; attribute that identifies the type of the
	 * object.
	 * 
	 * @return Type of object with the default value of {@link #DEFAULT_OBJ_URI}
	 *         .
	 */
	public String getObjURI() {
		return this.objURI;
	}

	/**
	 * Sets the &quot;objURI&quot; attribute that identifies the type of the
	 * object.
	 * 
	 * @param aObjURI
	 *            Object URI of the type of object.
	 */
	public void setObjURI(String aObjURI) {
		this.objURI = aObjURI;
	}

	/**
	 * Gets the local name of the object attribute that uniquely identifies the
	 * object.
	 * 
	 * @return Local name of the object attribute that uniquely identifies the
	 *         object with the default value of {@link #DEFAULT_OBJ_ELEMENT}.
	 */
	public String getObjElement() {
		return this.objElement;
	}

	/**
	 * Sets the local name of the object attribute that uniquely identifies the
	 * object.
	 * 
	 * @param aObjElement
	 *            Local name of the object attribute that uniquely identifies
	 *            the object
	 */
	public void setObjElement(String aObjElement) {
		this.objElement = aObjElement;
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
	 *                Error encoding <code>EPPFeeObject</code>
	 */
	public Element encode(Document aDocument) throws EPPEncodeException {

		if (aDocument == null) {
			throw new EPPEncodeException("aDocument is null"
					+ " on in EPPFeeObject.encode(Document)");
		}

		// Check for required attributes
		if (this.objID == null) {
			throw new EPPEncodeException(
					"Required objID attribute is null in EPPFeeObject.encode(Document).");
		}
		if (this.command == null) {
			throw new EPPEncodeException(
					"Required command attribute is null in EPPFeeObject.encode(Document).");
		}

		// Create root element
		Element root = aDocument.createElementNS(EPPFeeExtFactory.NS, ELM_NAME);

		// Object URI
		if (this.objURI != null) {
			root.setAttribute(ATTR_OBJ_URI, this.objURI);
		}

		// Object ID
		Element theElm = aDocument.createElementNS(EPPFeeExtFactory.NS,
				EPPFeeExtFactory.NS_PREFIX + ":" + ELM_OBJ_ID);
		Text theVal = aDocument.createTextNode(this.objID);
		theElm.appendChild(theVal);
		root.appendChild(theElm);

		// Object Element
		if (this.objElement != null) {
			theElm.setAttribute(ATTR_OBJ_ELEMENT, this.objElement);
		}

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

		// Object URI
		this.objURI = aElement.getAttribute(ATTR_OBJ_URI);

		// Object ID
		Element theElm = EPPUtil.getElementByTagNameNS(aElement,
				EPPFeeExtFactory.NS, ELM_OBJ_ID);
		if (theElm != null) {
			this.objID = EPPUtil.decodeStringValue(theElm);

			// Object Element
			this.objElement = theElm.getAttribute(ATTR_OBJ_ELEMENT);
		}

		this.objID = EPPUtil.decodeString(aElement, EPPFeeExtFactory.NS,
				ELM_OBJ_ID);

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
	 * clone an <code>EPPCodecComponent</code>.
	 * 
	 * @return clone of concrete <code>EPPFeeObject</code>.
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPFeeObject clone = (EPPFeeObject) super.clone();

		// Object URI
		clone.objURI = this.objURI;

		// Object ID
		clone.objID = this.objID;

		// Object Element
		clone.objElement = this.objElement;

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
	 * Compare an instance of <code>EPPFeeObject</code> with this instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return <code>true</code> if equal; <code>false</code> otherwise.
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPFeeObject)) {
			cat.error("EPPFeeObject.equals(): " + aObject.getClass().getName()
					+ " not EPPFeeObject instance");

			return false;
		}

		EPPFeeObject other = (EPPFeeObject) aObject;

		// Object URI
		if (!EqualityUtil.equals(this.objURI, other.objURI)) {
			cat.error("EPPFeeObject.equals(): objURI not equal");
			return false;
		}

		// Object ID
		if (!EqualityUtil.equals(this.objID, other.objID)) {
			cat.error("EPPFeeObject.equals(): objID not equal");
			return false;
		}

		// Object Element
		if (!EqualityUtil.equals(this.objElement, other.objElement)) {
			cat.error("EPPFeeObject.equals(): objElement not equal");
			return false;
		}

		// Currency
		if (!EqualityUtil.equals(this.currency, other.currency)) {
			cat.error("EPPFeeObject.equals(): currency not equal");
			return false;
		}

		// Command
		if (!EqualityUtil.equals(this.command, other.command)) {
			cat.error("EPPFeeObject.equals(): command not equal");
			return false;
		}

		// Period
		if (!EqualityUtil.equals(this.period, other.period)) {
			cat.error("EPPFeeObject.equals(): period not equal");
			return false;
		}

		return true;
	}

}
