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

import java.util.ArrayList;
import java.util.List;

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
 * <code>EPPFeeObjectResult</code> represents a domain result to a check.
 */
public class EPPFeeObjectResult implements EPPCodecComponent {

	/**
	 * XML local name for <code>EPPFeeObjectResult</code>.
	 */
	public static final String ELM_LOCALNAME = "cd";

	/**
	 * XML root tag for <code>EPPFeeObjectResult</code>.
	 */
	public static final String ELM_NAME = EPPFeeExtFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(
			EPPFeeObjectResult.class.getName(),
			EPPCatFactory.getInstance().getFactory());

	/**
	 * XML local name for the object
	 */
	private static final String ELM_OBJECT = "object";

	/**
	 * XML local name for the currency
	 */
	private static final String ELM_CURRENCY = "currency";

	/**
	 * XML local name for the classification
	 */
	private static final String ELM_CLASSIFICATION = "class";

	/**
	 * XML local name for the reason
	 */
	private static final String ELM_REASON = "reason";

	/**
	 * &quot;avail&quot; attribute constant.
	 */
	private final static String ATTR_AVAIL = "avail";

	/**
	 * Object XML Namespace. An example is
	 * <code>com.verisign.epp.codec.domain.EPPDomainMapFactory.NS</code> with
	 * the value &quot;urn:ietf:params:xml:ns:domain-1.0&quot; to reference the
	 * domain name element.
	 */
	private String objXmlNs;

	/**
	 * Object XML element name including the prefix and the local name as in
	 * &quot;domain:name&quot;, which has the prefix of &quot;domain&quot; and
	 * the local name &quot;name&quot;
	 */
	private String objXmlName;

	/**
	 * The string that identifies the object, which most likely corresponds to
	 * the &lt;domain:name&gt; element.
	 */
	private String objID;

	/**
	 * Is the fee information available?
	 */
	private boolean available;

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
	 * Reason for a &quot;false&quot; availability.
	 */
	private String reason;

	/**
	 * Default constructor for <code>EPPFeeObjectResult</code>.
	 */
	public EPPFeeObjectResult() {
	}

	/**
	 * Constructor for <code>EPPFeeObjectResult</code> that takes the required
	 * attributes for an available fee.
	 * 
	 * @param aObjXMLNs
	 *            Object XML Namespace. An example is
	 *            <code>com.verisign.epp.codec.domain.EPPDomainMapFactory.NS</code>
	 *            with the value &quot;urn:ietf:params:xml:ns:domain-1.0&quot;
	 *            to reference the domain name element.
	 * @param aObjectXMLName
	 *            Object XML element name including the prefix and the local
	 *            name as in &quot;domain:name&quot;, which has the prefix of
	 *            &quot;domain&quot; and the local name &quot;name&quot;
	 * @param aObjID
	 *            Object identifier. An example is the domain name for extension
	 *            of a domain check response.
	 * @param aCurrency
	 *            Currency of the fees
	 * @param aCommand
	 *            Command associated with the fees
	 */
	public EPPFeeObjectResult(String aObjXMLNs, String aObjectXMLName,
			String aObjID, String aCurrency, EPPFeeCommand aCommand) {
		this.setObjXmlNs(aObjXMLNs);
		this.setObjXmlName(aObjectXMLName);
		this.setObjID(aObjID);
		this.setAvailable(true);
		this.setCurrency(aCurrency);
		this.setCommand(aCommand);
	}

	/**
	 * Constructor for <code>EPPFeeObjectResult</code> that takes the attributes
	 * for an unavailable fee.
	 * 
	 * @param aObjXMLNs
	 *            Object XML Namespace. An example is
	 *            <code>com.verisign.epp.codec.domain.EPPDomainMapFactory.NS</code>
	 *            with the value &quot;urn:ietf:params:xml:ns:domain-1.0&quot;
	 *            to reference the domain name element.
	 * @param aObjectXMLName
	 *            Object XML element name including the prefix and the local
	 *            name as in &quot;domain:name&quot;, which has the prefix of
	 *            &quot;domain&quot; and the local name &quot;name&quot;
	 * @param aObjID
	 *            Object identifier. An example is the domain name for extension
	 *            of a domain check response.
	 * @param aCurrency
	 *            Currency of the fees
	 * @param aCommand
	 *            Command associated with the fees
	 * @param aReason
	 *            Reason that the fee is unavailable
	 */
	public EPPFeeObjectResult(String aObjXMLNs, String aObjectXMLName,
			String aObjID, String aCurrency, EPPFeeCommand aCommand,
			String aReason) {
		this.setObjXmlNs(aObjXMLNs);
		this.setObjXmlName(aObjectXMLName);
		this.setObjID(aObjID);
		this.setAvailable(false);
		this.setCurrency(aCurrency);
		this.setCommand(aCommand);
		this.setReason(aReason);
	}

	/**
	 * Constructor for <code>EPPFeeObjectResult</code> that takes all the
	 * required attributes and a single fee for an available fee.
	 * 
	 * @param aObjXMLNs
	 *            Object XML Namespace. An example is
	 *            <code>com.verisign.epp.codec.domain.EPPDomainMapFactory.NS</code>
	 *            with the value &quot;urn:ietf:params:xml:ns:domain-1.0&quot;
	 *            to reference the domain name element.
	 * @param aObjectXMLName
	 *            Object XML element name including the prefix and the local
	 *            name as in &quot;domain:name&quot;, which has the prefix of
	 *            &quot;domain&quot; and the local name &quot;name&quot;
	 * @param aObjID
	 *            Object identifier. An example is the domain name for extension
	 *            of a domain check response.
	 * @param aCurrency
	 *            Currency of the fees
	 * @param aCommand
	 *            Command associated with the fees
	 * @param aFee
	 *            A single fee value
	 */
	public EPPFeeObjectResult(String aObjXMLNs, String aObjectXMLName,
			String aObjID, String aCurrency, EPPFeeCommand aCommand,
			EPPFeePeriod aPeriod, EPPFeeValue aFee) {
		this.setObjXmlNs(aObjXMLNs);
		this.setObjXmlName(aObjectXMLName);
		this.setObjID(aObjID);
		this.setAvailable(true);
		this.setCurrency(aCurrency);
		this.setCommand(aCommand);
		this.setPeriod(aPeriod);
		this.addFee(aFee);
	}

	/**
	 * Constructor for <code>EPPFeeObjectResult</code> that takes all attributes
	 * for an available fee.
	 * 
	 * @param aObjXMLNs
	 *            Object XML Namespace. An example is
	 *            <code>com.verisign.epp.codec.domain.EPPDomainMapFactory.NS</code>
	 *            with the value &quot;urn:ietf:params:xml:ns:domain-1.0&quot;
	 *            to reference the domain name element.
	 * @param aObjectXMLName
	 *            Object XML element name including the prefix and the local
	 *            name as in &quot;domain:name&quot;, which has the prefix of
	 *            &quot;domain&quot; and the local name &quot;name&quot;
	 * @param aObjID
	 *            Object identifier. An example is the domain name for extension
	 *            of a domain check response.
	 * @param aCurrency
	 *            Currency of the fees
	 * @param aCommand
	 *            Command associated with the fees
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
	public EPPFeeObjectResult(String aObjXMLNs, String aObjectXMLName,
			String aObjID, String aCurrency, EPPFeeCommand aCommand,
			EPPFeePeriod aPeriod, List<EPPFeeValue> aFees,
			List<EPPFeeCredit> aCredits, String aClassification) {

		this.setObjXmlNs(aObjXMLNs);
		this.setObjXmlName(aObjectXMLName);
		this.setObjID(aObjID);
		this.setAvailable(true);
		this.setCurrency(aCurrency);
		this.setCommand(aCommand);
		this.setPeriod(aPeriod);
		this.setFees(aFees);
		this.setCredits(aCredits);
		this.setClassification(aClassification);
	}

	/**
	 * Gets the object XML namespace. An example is
	 * <code>com.verisign.epp.codec.domain.EPPDomainMapFactory.NS</code> with
	 * the value &quot;urn:ietf:params:xml:ns:domain-1.0&quot; to reference the
	 * domain name element.
	 * 
	 * @return the objXmlNs object XML namespace
	 */
	public String getObjXmlNs() {
		return this.objXmlNs;
	}

	/**
	 * Sets the object XML namespace. An example is
	 * <code>com.verisign.epp.codec.domain.EPPDomainMapFactory.NS</code> with
	 * the value &quot;urn:ietf:params:xml:ns:domain-1.0&quot; to reference the
	 * domain name element.
	 * 
	 * @param aObjXmlNs
	 *            object XML namespace
	 */
	public void setObjXmlNs(String aObjXmlNs) {
		this.objXmlNs = aObjXmlNs;
	}

	/**
	 * Gets object XML element name including the prefix and the local name as
	 * in &quot;domain:name&quot;, which has the prefix of &quot;domain&quot;
	 * and the local name &quot;name&quot;.
	 * 
	 * @return the objXmlName object XML element name
	 */
	public String getObjXmlName() {
		return this.objXmlName;
	}

	/**
	 * Sets object XML element name including the prefix and the local name as
	 * in &quot;domain:name&quot;, which has the prefix of &quot;domain&quot;
	 * and the local name &quot;name&quot;.
	 * 
	 * @param aObjXmlName
	 *            object XML element name
	 */
	public void setObjXmlName(String aObjXmlName) {
		this.objXmlName = aObjXmlName;
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
	 * Sets the name value.
	 * 
	 * @param aObjID
	 *            Object identifier value
	 */
	public void setObjID(String aObjID) {
		this.objID = aObjID;
	}

	/**
	 * Is the fee information available? See {@link #getReason()} to get the
	 * optional reason when available is <code>false</code>.
	 * 
	 * @return <code>true</code> if the fee information is available;
	 *         <code>false</code> otherwise.
	 */
	public boolean isAvailable() {
		return this.available;
	}

	/**
	 * Sets the fee information available value. See {@link #setReason(String)}
	 * to set the optional reason when available is <code>false</code>.
	 * 
	 * @param aAvailable
	 *            <code>true</code> if the fee is available; <code>false</code>
	 *            otherwise.
	 */
	public void setAvailable(boolean aAvailable) {
		this.available = aAvailable;
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
	 * Is the reason defined if the available attribute is <code>false</code>?
	 * 
	 * @return <code>true</code> if the reason is defined; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasReason() {
		return (this.reason != null ? true : false);
	}

	/**
	 * Gets the reason value.
	 * 
	 * @return Reason if defined; <code>null</code> otherwise.
	 */
	public String getReason() {
		return this.reason;
	}

	/**
	 * Sets the reason value.
	 * 
	 * @param aReason
	 *            reason value
	 */
	public void setReason(String aReason) {
		this.reason = aReason;
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
	 *                Error encoding <code>EPPFeeObjectResult</code>
	 */
	public Element encode(Document aDocument) throws EPPEncodeException {

		if (aDocument == null) {
			throw new EPPEncodeException("aDocument is null"
					+ " on in EPPFeeObjectResult.encode(Document)");
		}

		// Check for required attributes
		if (this.objXmlNs == null) {
			throw new EPPEncodeException(
					"Required objXmlNs attribute is null in EPPFeeObjectResult.encode(Document).");
		}
		if (this.objXmlName == null) {
			throw new EPPEncodeException(
					"Required objXmlName attribute is null in EPPFeeObjectResult.encode(Document).");
		}
		if (this.objID == null) {
			throw new EPPEncodeException(
					"Required objID attribute is null in EPPFeeObjectResult.encode(Document).");
		}
		if (this.currency == null) {
			throw new EPPEncodeException(
					"Required currency attribute is null in EPPFeeObjectResult.encode(Document).");
		}
		if (this.command == null) {
			throw new EPPEncodeException(
					"Required command attribute is null in EPPFeeObjectResult.encode(Document).");
		}

		// Create root element
		Element root = aDocument.createElementNS(EPPFeeExtFactory.NS, ELM_NAME);

		// Available
		EPPUtil.encodeBooleanAttr(root, ATTR_AVAIL, this.available);

		// Object ID
		Element theElm = aDocument.createElementNS(EPPFeeExtFactory.NS,
				EPPFeeExtFactory.NS_PREFIX + ":" + ELM_OBJECT);
		Element theObjectElm = aDocument.createElementNS(this.objXmlNs,
				this.objXmlName);
		Text theVal = aDocument.createTextNode(this.objID);
		theObjectElm.appendChild(theVal);
		theElm.appendChild(theObjectElm);
		root.appendChild(theElm);

		// Command
		EPPUtil.encodeComp(aDocument, root, this.command);

		// Currency
		EPPUtil.encodeString(aDocument, root, this.currency,
				EPPFeeExtFactory.NS,
				EPPFeeExtFactory.NS_PREFIX + ":" + ELM_CURRENCY);

		// Period
		EPPUtil.encodeComp(aDocument, root, this.period);

		// Fees
		EPPUtil.encodeCompList(aDocument, root, this.fees);

		// Credits
		EPPUtil.encodeCompList(aDocument, root, this.credits);

		// Classification
		EPPUtil.encodeString(aDocument, root, this.classification,
				EPPFeeExtFactory.NS,
				EPPFeeExtFactory.NS_PREFIX + ":" + ELM_CLASSIFICATION);

		// Reason
		EPPUtil.encodeString(aDocument, root, this.reason, EPPFeeExtFactory.NS,
				EPPFeeExtFactory.NS_PREFIX + ":" + ELM_REASON);

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

		// Available
		this.available = EPPUtil.decodeBooleanAttr(aElement, ATTR_AVAIL);

		// Object ID
		Element theElm = EPPUtil.getElementByTagNameNS(aElement,
				EPPFeeExtFactory.NS, ELM_OBJECT);
		if (theElm != null) {
			Element theObjectElm = EPPUtil.getFirstElementChild(theElm);

			if (theObjectElm != null) {
				this.objXmlNs = theObjectElm.getNamespaceURI();
				this.objXmlName = theObjectElm.getNodeName();
				this.objID = EPPUtil.decodeStringValue(theObjectElm);
			}
		}

		// Command
		this.command = (EPPFeeCommand) EPPUtil.decodeComp(aElement,
				EPPFeeExtFactory.NS, EPPFeeCommand.ELM_NAME,
				EPPFeeCommand.class);

		// Currency
		this.currency = EPPUtil.decodeString(aElement, EPPFeeExtFactory.NS,
				ELM_CURRENCY);

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

		// Reason
		this.reason = EPPUtil.decodeString(aElement, EPPFeeExtFactory.NS,
				ELM_REASON);
	}

	/**
	 * Clone an <code>EPPCodecComponent</code> instance.
	 * 
	 * @return clone of concrete <code>EPPFeeObjectResult</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPFeeObjectResult clone = (EPPFeeObjectResult) super.clone();

		// Available
		clone.available = this.available;

		// Object ID
		clone.objXmlNs = this.objXmlNs;
		clone.objXmlName = this.objXmlName;
		clone.objID = this.objID;

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

		// Reason
		clone.reason = this.reason;

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
	 * Compare an instance of <code>EPPFeeObjectResult</code> with this
	 * instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return <code>true</code> if equal; <code>false</code> otherwise.
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPFeeObjectResult)) {
			cat.error("EPPFeeObjectResult.equals(): "
					+ aObject.getClass().getName()
					+ " not EPPFeeObjectResult instance");

			return false;
		}

		EPPFeeObjectResult other = (EPPFeeObjectResult) aObject;

		// Available
		if (!EqualityUtil.equals(this.available, other.available)) {
			cat.error("EPPFeeObjectResult.equals(): available not equal");
			return false;
		}

		// Object ID
		if (!EqualityUtil.equals(this.objXmlNs, other.objXmlNs)) {
			cat.error("EPPFeeObjectResult.equals(): objXmlNs not equal");
			return false;
		}

		// Only compare the local names, since the prefix is not significant
		if (!EqualityUtil.equals(EPPUtil.getLocalName(this.objXmlName),
				EPPUtil.getLocalName(other.objXmlName))) {
			cat.error("EPPFeeObjectResult.equals(): local XML Name ("
					+ EPPUtil.getLocalName(this.objXmlName) + ", "
					+ EPPUtil.getLocalName(other.objXmlName) + ") not equal");
			return false;
		}
		if (!EqualityUtil.equals(this.objID, other.objID)) {
			cat.error("EPPFeeObjectResult.equals(): objID not equal");
			return false;
		}

		// Currency
		if (!EqualityUtil.equals(this.currency, other.currency)) {
			cat.error("EPPFeeObjectResult.equals(): currency not equal");
			return false;
		}

		// Command
		if (!EqualityUtil.equals(this.command, other.command)) {
			cat.error("EPPFeeObjectResult.equals(): command not equal");
			return false;
		}

		// Period
		if (!EqualityUtil.equals(this.period, other.period)) {
			cat.error("EPPFeeObjectResult.equals(): period not equal");
			return false;
		}

		// Fees
		if (!EqualityUtil.equals(this.fees, other.fees)) {
			cat.error("EPPFeeObjectResult.equals(): fees not equal");
			return false;
		}

		// Credits
		if (!EqualityUtil.equals(this.credits, other.credits)) {
			cat.error("EPPFeeObjectResult.equals(): credits not equal");
			return false;
		}

		// Classification
		if (!EqualityUtil.equals(this.classification, other.classification)) {
			cat.error("EPPFeeObjectResult.equals(): classification not equal");
			return false;
		}

		// Reason
		if (!EqualityUtil.equals(this.reason, other.reason)) {
			cat.error("EPPFeeObjectResult.equals(): reason not equal");
			return false;
		}

		return true;
	}

}
