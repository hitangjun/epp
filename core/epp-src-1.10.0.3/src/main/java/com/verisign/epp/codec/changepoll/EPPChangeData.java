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

package com.verisign.epp.codec.changepoll;

import java.util.Date;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
// W3C Imports
import org.w3c.dom.Element;
import org.w3c.dom.Node;

//----------------------------------------------
// Imports
//----------------------------------------------
// SDK Imports
import com.verisign.epp.codec.gen.EPPCodecComponent;
import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EqualityUtil;

/**
 * Change Data information that is added as an extension to a poll message
 * containing the info response of the object that was changed to define the
 * what, when, who, and why for the change.
 */
public class EPPChangeData implements EPPCodecComponent {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPChangeData.class.getName(),
			EPPCatFactory.getInstance().getFactory());

	/**
	 * XML local name for <code>EPPChangePoll</code>.
	 */
	public static final String ELM_LOCALNAME = "changeData";

	/** XML root tag for <code>EPPChangePoll</code>. */
	public static final String ELM_NAME = EPPChangePollExtFactory.NS_PREFIX
			+ ":" + ELM_LOCALNAME;

	/**
	 * Object reflects the state before the operation.
	 */
	public static final String STATE_BEFORE = "before";

	/**
	 * Object reflects the state after the operation.
	 */
	public static final String STATE_AFTER = "after";

	/**
	 * Name of XML attribute for the &quot;state&quot; attribute.
	 */
	private static final String ATTR_STATE = "state";

	/**
	 * XML local name for the date element
	 */
	private static final String ELM_DATE = "date";

	/**
	 * XML local name for the server transaction identifier element.
	 */
	private static final String ELM_SVRTRID = "svTRID";

	/**
	 * XML local name for the who element.
	 */
	private static final String ELM_WHO = "who";

	/**
	 * XML local name for the reason element.
	 */
	private static final String ELM_REASON = "reason";

	/**
	 * XML local name for the reason lang attribute.
	 */
	private static final String ATTR_LANG = "lang";

	/**
	 * State that reflects the whether the object reflects the state prior to
	 * the operation or after the operation, using the constants
	 * <code>STATE_BEFORE</code> and <code>STATE_AFTER</code>, respectively. The
	 * default value is set ot <code>STATE_AFTER</code>.
	 */
	private String state = STATE_AFTER;

	/**
	 * Transform operation executed on the object.
	 */
	private EPPChangeOperation operation;

	/**
	 * Date and time when the operation was executed.
	 */
	private Date date;

	/**
	 * Server transaction identifier of the operation.
	 */
	private String svrTrid;

	/**
	 * Who executed the operation.
	 */
	private String who;

	/**
	 * OPTIONAL Case identifier for the change.
	 */
	private EPPChangeCaseId caseId;

	/**
	 * OPTIONAL reason for executing the operation.
	 */
	private String reason;

	/**
	 * OPTIONAL language of the reason.
	 */
	private String reasonLang;

	/**
	 * Default constructor for <code>EPPChangePoll</code>.
	 */
	public EPPChangeData() {
	}

	/**
	 * Constructor for <code>EPPChangePoll</code> that takes the required
	 * attributes.
	 * 
	 * @param aOperation
	 *            Transform operation executed on the object.
	 * @param aDate
	 *            Date and time when the operation was executed.
	 * @param aSvrTrid
	 *            Server transaction identifier of the operation.
	 * @param aWho
	 *            Who executed the operation.
	 */
	public EPPChangeData(EPPChangeOperation aOperation, Date aDate,
			String aSvrTrid, String aWho) {
		this.setOperation(aOperation);
		this.setDate(aDate);
		this.setSvrTrid(aSvrTrid);
		this.setWho(aWho);
	}

	/**
	 * Constructor for <code>EPPChangePoll</code> that takes the all attributes.
	 * 
	 * @param aOperation
	 *            Transform operation executed on the object.
	 * @param aDate
	 *            Date and time when the operation was executed.
	 * @param aSvrTrid
	 *            Server transaction identifier of the operation.
	 * @param aWho
	 *            Who executed the operation.
	 * @param aState
	 *            Does the object reflect the state before or after the
	 *            operation using <code>STATE_BEFORE</code> and
	 *            <code>STATE_AFTER</code> constants, respectively.
	 * @param aCaseId
	 *            Optional case identifier for the change. Set to
	 *            <code>null</code> if there is no case.
	 * @param aReason
	 *            Reason for executing the operation. Set to <code>null</code>
	 *            for no reason.
	 * @param aReasonLang
	 *            Language for the reason. Set to <code>null</code> to use the
	 *            default value of &quot;en&quot;.
	 */
	public EPPChangeData(EPPChangeOperation aOperation, Date aDate,
			String aSvrTrid, String aWho, String aState,
			EPPChangeCaseId aCaseId, String aReason, String aReasonLang) {
		this.setOperation(aOperation);
		this.setDate(aDate);
		this.setSvrTrid(aSvrTrid);
		this.setWho(aWho);
		this.setState(aState);
		this.setCaseId(aCaseId);
		this.setReason(aReason);
		this.setReasonLang(aReasonLang);
	}

	/**
	 * Gets whether the object reflects the state before or after the operation.
	 * 
	 * @return Either <code>STATE_BEFORE</code> to indicate that the object
	 *         reflects the before state or <code>STATE_AFTER</code> to indicate
	 *         that the object reflects the after state.
	 */
	public String getState() {
		return this.state;
	}

	/**
	 * Sets whether the object reflects the state before or after the operation.
	 * 
	 * @param aState
	 *            <code>STATE_BEFORE</code> constant for before state and
	 *            <code>STATE_AFTER</code> for after state.
	 */
	public void setState(String aState) {
		this.state = aState;
	}

	/**
	 * Gets the transform operation executed on the object.
	 * 
	 * @return The transform operation if defined; <code>null</code> otherwise.
	 */
	public EPPChangeOperation getOperation() {
		return this.operation;
	}

	/**
	 * Sets the transform operation executed on the object.
	 * 
	 * @param aOperation
	 *            The transform operation executed on the object..
	 */
	public void setOperation(EPPChangeOperation aOperation) {
		this.operation = aOperation;
	}

	/**
	 * Gets the date and time when the operation was executed.
	 * 
	 * @return The date and time when the operation was executed if defined;
	 *         <code>null</code> otherwise.
	 */
	public Date getDate() {
		return this.date;
	}

	/**
	 * Sets the date and time when the operation was executed.
	 * 
	 * @param aDate
	 *            The date and time when the operation was executed.
	 */
	public void setDate(Date aDate) {
		this.date = aDate;
	}

	/**
	 * Gets the server transaction identifier of the operation.
	 * 
	 * @return the svrTrid Server transaction identifier of the operation if
	 *         defined; <code>null</code> otherwise.
	 */
	public String getSvrTrid() {
		return this.svrTrid;
	}

	/**
	 * Sets the server transaction identifier of the operation.
	 * 
	 * @param aSvrTrid
	 *            The server transaction identifier of the operation.
	 */
	public void setSvrTrid(String aSvrTrid) {
		this.svrTrid = aSvrTrid;
	}

	/**
	 * Gets who executed the operation.
	 * 
	 * @return Who executed the operation if defined; <code>null</code>
	 *         otherwise.
	 */
	public String getWho() {
		return this.who;
	}

	/**
	 * Sets who executed the operation.
	 * 
	 * @param aWho
	 *            Who executed the operation.
	 */
	public void setWho(String aWho) {
		this.who = aWho;
	}

	/**
	 * Is the case identifier defined?
	 * 
	 * @return <code>true</code> if the case identifier is defined;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasCaseId() {
		return (this.caseId != null ? true : false);
	}

	/**
	 * Gets the case identifier for the change.
	 * 
	 * @return Case identifier if defined; <code>null</code> otherwise.
	 */
	public EPPChangeCaseId getCaseId() {
		return this.caseId;
	}

	/**
	 * Sets the case identifier for the change.
	 * 
	 * @param aCaseId
	 *            The case identifier for the change.
	 */
	public void setCaseId(EPPChangeCaseId aCaseId) {
		this.caseId = aCaseId;
	}

	/**
	 * Is the reason defined?
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
	 * @return Reason value if defined; <code>null</code> otherwise.
	 */
	public String getReason() {
		return this.reason;
	}

	/**
	 * Sets the reason value.
	 * 
	 * @param aReason
	 *            Reason for executing the operation.
	 */
	public void setReason(String aReason) {
		this.reason = aReason;
	}

	/**
	 * Is the reason language defined?
	 * 
	 * @return <code>true</code> if the reason language is defined;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasReasonLang() {
		return (this.reasonLang != null ? true : false);
	}

	/**
	 * Gets the reason language value.
	 * 
	 * @return Reason language if defined; <code>null</code> otherwise.
	 */
	public String getReasonLang() {
		return this.reasonLang;
	}

	/**
	 * Sets the reason language value.
	 * 
	 * @param aReasonLang
	 *            Reason language for reason value.
	 */
	public void setReasonLang(String aReasonLang) {
		this.reasonLang = aReasonLang;
	}

	/**
	 * encode instance into a DOM element tree. A DOM Document is passed as an
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
	 *                Error encoding <code>EPPChangePoll</code>
	 */
	public Element encode(Document aDocument) throws EPPEncodeException {

		// Create root element
		Element root = aDocument.createElementNS(EPPChangePollExtFactory.NS,
				ELM_NAME);

		// State
		if (this.state != null) {
			root.setAttribute(ATTR_STATE, this.state);
		}

		// Operation
		EPPUtil.encodeComp(aDocument, root, this.operation);

		// Date
		EPPUtil.encodeTimeInstant(aDocument, root, this.date,
				EPPChangePollExtFactory.NS, EPPChangePollExtFactory.NS_PREFIX
						+ ":" + ELM_DATE);

		// SvTrid
		EPPUtil.encodeString(aDocument, root, this.svrTrid,
				EPPChangePollExtFactory.NS, EPPChangePollExtFactory.NS_PREFIX
						+ ":" + ELM_SVRTRID);

		// Who
		EPPUtil.encodeString(aDocument, root, this.who,
				EPPChangePollExtFactory.NS, EPPChangePollExtFactory.NS_PREFIX
						+ ":" + ELM_WHO);

		// Case Identifier
		EPPUtil.encodeComp(aDocument, root, this.caseId);

		// Reason and ReasonLang
		if (this.hasReason()) {
			Element reasonElm = aDocument.createElementNS(
					EPPChangePollExtFactory.NS,
					EPPChangePollExtFactory.NS_PREFIX + ":" + ELM_REASON);

			if (this.hasReasonLang()) {
				reasonElm.setAttribute(ATTR_LANG, this.reasonLang);
			}

			reasonElm.appendChild(aDocument.createTextNode(this.reason));

			root.appendChild(reasonElm);
		}

		return root;
	}

	/**
	 * decode a DOM element tree to initialize the instance attributes. The
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

		// State
		String theState = aElement.getAttribute(ATTR_STATE);
		if (theState != null && !theState.isEmpty()) {
			this.state = theState;
		}
		else {
			this.state = STATE_AFTER;
		}

		// Operation
		this.operation = (EPPChangeOperation) EPPUtil.decodeComp(aElement,
				EPPChangePollExtFactory.NS, EPPChangeOperation.ELM_NAME,
				EPPChangeOperation.class);

		// Date
		this.date = EPPUtil.decodeTimeInstant(aElement,
				EPPChangePollExtFactory.NS, ELM_DATE);

		// SvTrid
		this.svrTrid = EPPUtil.decodeString(aElement,
				EPPChangePollExtFactory.NS, ELM_SVRTRID);

		// Who
		this.who = EPPUtil.decodeString(aElement, EPPChangePollExtFactory.NS,
				ELM_WHO);

		// Case Identifier
		this.caseId = (EPPChangeCaseId) EPPUtil.decodeComp(aElement,
				EPPChangePollExtFactory.NS, EPPChangeCaseId.ELM_NAME,
				EPPChangeCaseId.class);

		// Reason and ReasonLang
		Element theReasonElm = EPPUtil.getElementByTagNameNS(aElement,
				EPPChangePollExtFactory.NS, ELM_REASON);

		if (theReasonElm != null) {
			Node textNode = theReasonElm.getFirstChild();
			if (textNode != null) {
				this.reason = textNode.getNodeValue();

				String theReasonLang = theReasonElm.getAttribute(ATTR_LANG);
				if (theReasonLang != null && !theReasonLang.isEmpty()) {
					this.reasonLang = theReasonLang;
				}
				else {
					this.reasonLang = null;
				}

			}
			else {
				this.reason = null;
				this.reasonLang = null;
			}
		}
		else {
			this.reason = null;
			this.reasonLang = null;
		}
	}

	/**
	 * clone an <code>EPPCodecComponent</code>.
	 * 
	 * @return clone of concrete <code>EPPChangePoll</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPChangeData clone = (EPPChangeData) super.clone();

		if (this.operation != null) {
			clone.operation = (EPPChangeOperation) this.operation.clone();
		}

		if (this.caseId != null) {
			clone.caseId = (EPPChangeCaseId) this.caseId.clone();
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
	 * Compare an instance of <code>EPPChangePoll</code> with this instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return <code>true</code> if equal; <code>false</code> otherwise.
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPChangeData)) {
			return false;
		}

		EPPChangeData other = (EPPChangeData) aObject;

		// State
		if (!EqualityUtil.equals(this.state, other.state)) {
			cat.error("EPPChangeData.equals(): state not equal");
			return false;
		}

		// Operation
		if (!EqualityUtil.equals(this.operation, other.operation)) {
			cat.error("EPPChangeData.equals(): operation not equal");
			return false;
		}

		// Date
		if (!EqualityUtil.equals(this.date, other.date)) {
			cat.error("EPPChangeData.equals(): date not equal");
			return false;
		}

		// SvTrid
		if (!EqualityUtil.equals(this.svrTrid, other.svrTrid)) {
			cat.error("EPPChangeData.equals(): svrTrid not equal");
			return false;
		}

		// Who
		if (!EqualityUtil.equals(this.who, other.who)) {
			cat.error("EPPChangeData.equals(): who not equal");
			return false;
		}

		// Case Identifier
		if (!EqualityUtil.equals(this.caseId, other.caseId)) {
			cat.error("EPPChangeData.equals(): caseId not equal");
			return false;
		}

		// Reason and ReasonLang
		if (!EqualityUtil.equals(this.reason, other.reason)) {
			cat.error("EPPChangeData.equals(): reason not equal");
			return false;
		}
		if (!EqualityUtil.equals(this.reasonLang, other.reasonLang)) {
			cat.error("EPPChangeData.equals(): reasonLang not equal");
			return false;
		}

		return true;
	}

}
