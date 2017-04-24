/***********************************************************
Copyright (C) 2015 VeriSign, Inc.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-0107  USA

http://www.verisign.com/nds/naming/namestore/techdocs.html
 ***********************************************************/
package com.verisign.epp.codec.reseller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPResponse;
import com.verisign.epp.codec.gen.EPPTransId;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EqualityUtil;

/**
 * Represents an EPP Internationalized Domain Name (IDN) Table &lt;info&gt;
 * response, which support three different forms:<br>
 * <ul>
 * <li>Domain Info Form - Used to validate the domain name code points against
 * the IDN Tables and IDN Policies, and to return the matching IDN Table
 * meta-data.</li>
 * <li>Table Info Form - Used to retrieve information associated with an IDN
 * Table object.</li>
 * <li>List Info Form - Used to retrieve the list of IDN Tables supported by the
 * server.</li>
 * </ul>
 * 
 * <br>
 * <code>EPPResellerInfoCmd</code> is the concrete <code>EPPCommand</code>
 * associated with <code>EPPResellerInfoCmd</code>.
 * 
 * @see com.verisign.epp.codec.reseller.EPPResellerInfoResp
 */
public class EPPResellerInfoResp extends EPPResponse {


	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPResellerInfoResp.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * XML local name for <code>EPPResellerInfoResp</code>.
	 */
	public static final String ELM_LOCALNAME = "infData";

	/**
	 * XML root tag for <code>EPPResellerInfoResp</code>.
	 */
	public static final String ELM_NAME = EPPResellerMapFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * XML local name for the reseller identifier element
	 */
	private static final String ELM_RESELLER_ID = "id";

	/**
	 * XML local name for the Registry Object IDentifier (<code>roid</code>)
	 * element.
	 */
	private final static String ELM_ROID = "roid";

	/**
	 * XML local name for the state (<code>state</code>) element.
	 */
	private final static String ELM_STATE = "state";

	/**
	 * XML local name for the OPTIONAL parent identifier (<code>parentId</code>)
	 * element.
	 */
	private final static String ELM_PARENT_ID = "parentId";

	/**
	 * XML local name for the postal info (<code>postalInfo</code>) element.
	 */
	private final static String ELM_POSTAL_INFO = "postalInfo";

	/**
	 * XML local name for the OPTIONAL voice number (<code>voice</code>)
	 * element.
	 */
	private final static String ELM_VOICE = "voice";

	/**
	 * XML local name for the OPTIONAL fax number (<code>fax</code>) element.
	 */
	private final static String ELM_FAX = "fax";

	/**
	 * XML local name for the email (<code>email</code>) element.
	 */
	private final static String ELM_EMAIL = "email";

	/**
	 * XML local name for the OPTIONAL URL (<code>url</code>) element.
	 */
	private final static String ELM_URL = "url";

	/**
	 * XML local name for the client identifier (<code>clID</code>) element.
	 */
	private final static String ELM_CLIENT_ID = "clID";

	/**
	 * XML local name for the created by identifier (<code>crID</code>) element.
	 */
	private final static String ELM_CREATED_BY = "crID";

	/**
	 * XML local name for the created date (<code>crDate</code>) element.
	 */
	private final static String ELM_CREATED_DATE = "crDate";

	/**
	 * XML local name for the OPTIONAL updated by identifier (<code>upID</code>)
	 * element.
	 */
	private final static String ELM_LAST_UPDATED_BY = "upID";

	/**
	 * XML local name for the OPTIONAL updated date (<code>upDate</code>)
	 * element.
	 */
	private final static String ELM_LAST_UPDATED_DATE = "upDate";

	/**
	 * XML local name for the OPTIONAL disclose (<code>disclose</code>) element.
	 */
	private final static String ELM_DISCLOSE = "disclose";

	/**
	 * XML extension "x" attribute value for voice or fax elements.
	 */
	private final static String ATTR_EXT = "x";

	/**
	 * The reseller identifier of the reseller of a sponsoring registrar.
	 */
	private String resellerId;

	/**
	 * Registry Object IDentifier
	 */
	private String roid;

	/**
	 * Operational state of reseller with default of <code>State.OK</code>.
	 */
	private State state = State.OK;

	/**
	 * OPTIONAL Parent Identifier
	 */
	private String parentId;

	/**
	 * One or two postal information elements, represented by the
	 * {@link EPPResellerPostalDefinition} class.
	 */
	private List<EPPResellerPostalDefinition> postalInfo = new ArrayList<EPPResellerPostalDefinition>();

	/**
	 * OPTIONAL Voice number
	 */
	private String voice;

	/**
	 * OPTIONAL Voice Extension
	 */
	private String voiceExt;

	/**
	 * OPTIONAL FAX number
	 */
	private String fax;

	/**
	 * OPTIONAL FAX Extension
	 */
	private String faxExt;

	/**
	 * Email Address
	 */
	private String email;

	/**
	 * URL
	 */
	private String url;

	/**
	 * Reseller contacts
	 */
	private List<EPPResellerContact> contacts = new ArrayList<EPPResellerContact>();

	/**
	 * Sponsoring Client Identifier
	 */
	private String clientId;

	/**
	 * Identifier of the client that created the reseller
	 */
	private String createdBy;

	/**
	 * Date and time the reseller was created.
	 */
	private Date createdDate;

	/**
	 * Identifier of the client that last updated the reseller
	 */
	private String lastUpdatedBy;

	/**
	 * Date and time the reseller was last updated.
	 */
	private Date lastUpdatedDate;

	/**
	 * Disclosure policies for reseller.
	 */
	private EPPResellerDisclose disclose;

	/**
	 * <code>EPPResellerInfoResp</code> default constructor.
	 */
	public EPPResellerInfoResp() {
	}

	/**
	 * <code>EPPResellerInfoResp</code> constructor that only takes the
	 * transaction identifier.
	 * 
	 * @param aTransId
	 *            Transaction Id associated with response.
	 */
	public EPPResellerInfoResp(EPPTransId aTransId) {
		super(aTransId);
	}

	/**
	 * <code>EPPResellerInfoResp</code> constructor that only takes the
	 * transaction identifier and the reseller identifier.
	 * 
	 * @param aTransId
	 *            Transaction Id associated with command. Set to
	 *            <code>null</code> if a client transaction identifier is not
	 *            desired.
	 * @param aResellerId
	 *            Reseller identifier
	 * 
	 */
	public EPPResellerInfoResp(EPPTransId aTransId, String aResellerId) {
		super(aTransId);
		this.resellerId = aResellerId;
	}

	/**
	 * Gets the reseller identifier.
	 * 
	 * @return The reseller identifier if defined;<code>null</code> otherwise.
	 */
	public String getResellerId() {
		return this.resellerId;
	}

	/**
	 * Sets the reseller identifier.
	 * 
	 * @param aResellerId
	 *            The reseller identifier
	 */
	public void setResellerId(String aResellerId) {
		this.resellerId = aResellerId;
	}

	/**
	 * @return the roid
	 */
	public String getRoid() {
		return this.roid;
	}

	/**
	 * @param aRoid
	 *            the roid to set
	 */
	public void setRoid(String aRoid) {
		this.roid = aRoid;
	}

	/**
	 * Gets the operational state of the reseller.
	 * 
	 * @return Operational state
	 */
	public State getState() {
		return this.state;
	}

	/**
	 * Sets the operational state of the reseller.
	 * 
	 * @param aState
	 *            Operation state
	 */
	public void setState(State aState) {
		this.state = aState;
	}

	/**
	 * @return the parentId
	 */
	public String getParentId() {
		return this.parentId;
	}

	/**
	 * @param aParentId
	 *            the parentId to set
	 */
	public void setParentId(String aParentId) {
		this.parentId = aParentId;
	}

	/**
	 * Is there any postal information set?
	 * 
	 * @return <code>true</code> if there is at least one
	 *         {@link EPPResellerPostalDefinition} set in the postal information;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasPostalInfo() {
		if (this.postalInfo != null && !this.postalInfo.isEmpty()) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds a postal definition to the postal information for the reseller.
	 * 
	 * @param aPostalInfo
	 *            Postal definition to add to the postal information.
	 */
	public void addPostalInfo(EPPResellerPostalDefinition aPostalInfo) {
		this.postalInfo.add(aPostalInfo);
	}

	/**
	 * Gets the postal information for the reseller. There can be one or two
	 * {@link EPPResellerPostalDefinition} objects in the postal information
	 * list.
	 * 
	 * @return Postal information for the reseller
	 */
	public List<EPPResellerPostalDefinition> getPostalInfo() {
		return this.postalInfo;
	}

	/**
	 * Sets the postal information for the reseller. There can be one or two
	 * {@link EPPResellerPostalDefinition} objects in the postal information
	 * list.
	 * 
	 * @param aPostalInfo
	 *            Postal information for the reseller.
	 */
	public void setPostalInfo(List<EPPResellerPostalDefinition> aPostalInfo) {
		this.postalInfo = aPostalInfo;
	}

	/**
	 * @return the voice
	 */
	public String getVoice() {
		return this.voice;
	}

	/**
	 * @param aVoice
	 *            the voice to set
	 */
	public void setVoice(String aVoice) {
		this.voice = aVoice;
	}

	/**
	 * @return the voiceExt
	 */
	public String getVoiceExt() {
		return this.voiceExt;
	}

	/**
	 * @param aVoiceExt
	 *            the voiceExt to set
	 */
	public void setVoiceExt(String aVoiceExt) {
		this.voiceExt = aVoiceExt;
	}

	/**
	 * @return the fax
	 */
	public String getFax() {
		return this.fax;
	}

	/**
	 * @param aFax
	 *            the fax to set
	 */
	public void setFax(String aFax) {
		this.fax = aFax;
	}

	/**
	 * @return the faxExt
	 */
	public String getFaxExt() {
		return this.faxExt;
	}

	/**
	 * @param aFaxExt
	 *            the faxExt to set
	 */
	public void setFaxExt(String aFaxExt) {
		this.faxExt = aFaxExt;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return this.email;
	}

	/**
	 * @param aEmail
	 *            the email to set
	 */
	public void setEmail(String aEmail) {
		this.email = aEmail;
	}

	/**
	 * Gets the URL of the website of the reseller.
	 * 
	 * @return URL of the website of the reseller.
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * Sets the URL of the website of the reseller.
	 * 
	 * @param aUrl
	 *            URL of the website of the reseller.
	 */
	public void setUrl(String aUrl) {
		this.url = aUrl;
	}

	/**
	 * Is there any contacts set?
	 * 
	 * @return <code>true</code> if there is at least one
	 *         {@link EPPResellerContact} set; <code>false</code> otherwise.
	 */
	public boolean hasContacts() {
		if (this.contacts != null && !this.contacts.isEmpty()) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds a contact to the list of contacts.
	 * 
	 * @param aContact
	 *            Contact add to the list of contacts.
	 */
	public void addContact(EPPResellerContact aContact) {
		this.contacts.add(aContact);
	}

	/**
	 * @return the contacts
	 */
	public List<EPPResellerContact> getContacts() {
		return this.contacts;
	}

	/**
	 * @param aContacts
	 *            the contacts to set
	 */
	public void setContacts(List<EPPResellerContact> aContacts) {
		this.contacts = aContacts;
	}

	/**
	 * @return the clientId
	 */
	public String getClientId() {
		return this.clientId;
	}

	/**
	 * @param aClientId
	 *            the clientId to set
	 */
	public void setClientId(String aClientId) {
		this.clientId = aClientId;
	}

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return this.createdBy;
	}

	/**
	 * @param aCreatedBy
	 *            the createdBy to set
	 */
	public void setCreatedBy(String aCreatedBy) {
		this.createdBy = aCreatedBy;
	}

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return this.createdDate;
	}

	/**
	 * @param aCreatedDate
	 *            the createdDate to set
	 */
	public void setCreatedDate(Date aCreatedDate) {
		this.createdDate = aCreatedDate;
	}

	/**
	 * @return the lastUpdatedBy
	 */
	public String getLastUpdatedBy() {
		return this.lastUpdatedBy;
	}

	/**
	 * @param aLastUpdatedBy
	 *            the lastUpdatedBy to set
	 */
	public void setLastUpdatedBy(String aLastUpdatedBy) {
		this.lastUpdatedBy = aLastUpdatedBy;
	}

	/**
	 * Gets the last updated date.
	 * 
	 * @return the lastUpdatedDate Last updated date if set; <code>null</code> otherwise.
	 */
	public Date getLastUpdatedDate() {
		return this.lastUpdatedDate;
	}

	/**
	 * Sets the last updated date.
	 * 
	 * @param aLastUpdatedDate
	 *            Last updated date and time
	 */
	public void setLastUpdatedDate(Date aLastUpdatedDate) {
		this.lastUpdatedDate = aLastUpdatedDate;
	}

	/**
	 * @return the disclose
	 */
	public EPPResellerDisclose getDisclose() {
		return this.disclose;
	}

	/**
	 * @param aDisclose
	 *            the disclose to set
	 */
	public void setDisclose(EPPResellerDisclose aDisclose) {
		this.disclose = aDisclose;
	}

	/**
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPResellerInfoResp</code> instance.
	 * 
	 * @param aDocument
	 *            DOM Document that is being built. Used as an Element factory.
	 * 
	 * @return Element Root DOM Element representing the EPPResellerInfoResp
	 *         instance.
	 * 
	 * @exception EPPEncodeException
	 *                Unable to encode EPPResellerInfoResp instance.
	 */
	protected Element doEncode(Document aDocument) throws EPPEncodeException {

		Element theElm = null;
		Text theVal = null;

		// Check required attributes
		if (this.resellerId == null) {
			throw new EPPEncodeException(
					"Undefined resellerId in EPPResellerInfoResp");
		}
		if (!this.hasPostalInfo()) {
			throw new EPPEncodeException(
					"Undefined postal information in EPPResellerInfoResp");
		}
		if (this.email == null) {
			throw new EPPEncodeException(
					"Undefined email in EPPResellerInfoResp");
		}
		if (this.clientId == null) {
			throw new EPPEncodeException(
					"Undefined clientId in EPPResellerInfoResp");
		}
		if (this.createdBy == null) {
			throw new EPPEncodeException(
					"Undefined createdBy in EPPResellerInfoResp");
		}
		if (this.createdDate == null) {
			throw new EPPEncodeException(
					"Undefined createdDate in EPPResellerInfoResp");
		}

		// Create root element
		Element root = aDocument.createElementNS(EPPResellerMapFactory.NS,
				ELM_NAME);

		// Reseller Identifier
		EPPUtil.encodeString(aDocument, root, this.resellerId,
				EPPResellerMapFactory.NS, EPPResellerMapFactory.NS_PREFIX + ":"
						+ ELM_RESELLER_ID);

		// Roid
		EPPUtil.encodeString(aDocument, root, this.roid,
				EPPResellerMapFactory.NS, EPPResellerMapFactory.NS_PREFIX + ":"
						+ ELM_ROID);

		// State
		EPPUtil.encodeString(aDocument, root, this.state.toString(),
				EPPResellerMapFactory.NS, EPPResellerMapFactory.NS_PREFIX + ":"
						+ ELM_STATE);

		// Parent Id
		EPPUtil.encodeString(aDocument, root, this.parentId,
				EPPResellerMapFactory.NS, EPPResellerMapFactory.NS_PREFIX + ":"
						+ ELM_PARENT_ID);

		// Postal Information
		EPPUtil.encodeCompList(aDocument, root, this.postalInfo);

		// Voice and Voice Extension
		if (this.voice != null) {
			theElm = aDocument.createElementNS(EPPResellerMapFactory.NS,
					EPPResellerMapFactory.NS_PREFIX + ":" + ELM_VOICE);
			theVal = aDocument.createTextNode(this.voice);
			theElm.appendChild(theVal);

			if (this.voiceExt != null) {
				theElm.setAttribute(ATTR_EXT, this.voiceExt);
			}

			root.appendChild(theElm);
		}

		// Fax and Fax Extension
		if (this.fax != null) {
			theElm = aDocument.createElementNS(EPPResellerMapFactory.NS,
					EPPResellerMapFactory.NS_PREFIX + ":" + ELM_FAX);
			theVal = aDocument.createTextNode(this.fax);
			theElm.appendChild(theVal);

			if (this.faxExt != null) {
				theElm.setAttribute(ATTR_EXT, this.faxExt);
			}

			root.appendChild(theElm);
		}

		// Email
		EPPUtil.encodeString(aDocument, root, this.email,
				EPPResellerMapFactory.NS, EPPResellerMapFactory.NS_PREFIX + ":"
						+ ELM_EMAIL);

		// URL
		EPPUtil.encodeString(aDocument, root, this.url,
				EPPResellerMapFactory.NS, EPPResellerMapFactory.NS_PREFIX + ":"
						+ ELM_URL);

		// Contacts
		EPPUtil.encodeCompList(aDocument, root, this.contacts);

		// Client Id
		EPPUtil.encodeString(aDocument, root, this.clientId,
				EPPResellerMapFactory.NS, EPPResellerMapFactory.NS_PREFIX + ":"
						+ ELM_CLIENT_ID);

		// Created By
		EPPUtil.encodeString(aDocument, root, this.createdBy,
				EPPResellerMapFactory.NS, EPPResellerMapFactory.NS_PREFIX + ":"
						+ ELM_CREATED_BY);

		// Created Date
		EPPUtil.encodeTimeInstant(aDocument, root, this.createdDate,
				EPPResellerMapFactory.NS, EPPResellerMapFactory.NS_PREFIX + ":"
						+ ELM_CREATED_DATE);

		// Last Updated By
		EPPUtil.encodeString(aDocument, root, this.lastUpdatedBy,
				EPPResellerMapFactory.NS, EPPResellerMapFactory.NS_PREFIX + ":"
						+ ELM_LAST_UPDATED_BY);

		// Last Updated Date
		EPPUtil.encodeTimeInstant(aDocument, root, this.lastUpdatedDate,
				EPPResellerMapFactory.NS, EPPResellerMapFactory.NS_PREFIX + ":"
						+ ELM_LAST_UPDATED_DATE);

		// Disclose
		EPPUtil.encodeComp(aDocument, root, this.disclose);

		return root;
	}

	/**
	 * Decode the <code>EPPResellerInfoResp</code> attributes from the aElement
	 * DOM Element tree.
	 * 
	 * @param aElement
	 *            Root DOM Element to decode <code>EPPResellerInfoResp</code>
	 *            from.
	 * 
	 * @exception EPPDecodeException
	 *                Unable to decode aElement
	 */
	protected void doDecode(Element aElement) throws EPPDecodeException {

		Element theElm = null;

		// Reseller Identifier
		this.resellerId = EPPUtil.decodeString(aElement,
				EPPResellerMapFactory.NS, ELM_RESELLER_ID);

		// Roid
		this.roid = EPPUtil.decodeString(aElement, EPPResellerMapFactory.NS,
				ELM_ROID);

		// State
		this.state = State.getState(EPPUtil.decodeString(aElement,
				EPPResellerMapFactory.NS, ELM_STATE));

		// Parent Id
		this.parentId = EPPUtil.decodeString(aElement,
				EPPResellerMapFactory.NS, ELM_PARENT_ID);

		// Postal Information
		this.postalInfo = EPPUtil.decodeCompList(aElement,
				EPPResellerMapFactory.NS, EPPResellerPostalDefinition.ELM_NAME,
				EPPResellerPostalDefinition.class);

		// Voice and Voice Extension
		theElm = EPPUtil.getElementByTagNameNS(aElement,
				EPPResellerMapFactory.NS, ELM_VOICE);
		if (theElm != null) {
			this.voice = EPPUtil.getTextContent(theElm);
			this.voiceExt = EPPUtil.decodeStringAttr(theElm, ATTR_EXT);
		}
		else {
			this.voice = null;
			this.voiceExt = null;
		}

		// Fax and Fax Extension
		theElm = EPPUtil.getElementByTagNameNS(aElement,
				EPPResellerMapFactory.NS, ELM_FAX);
		if (theElm != null) {
			this.fax = EPPUtil.getTextContent(theElm);
			this.faxExt = EPPUtil.decodeStringAttr(theElm, ATTR_EXT);
		}
		else {
			this.fax = null;
			this.faxExt = null;
		}

		// Email
		this.email = EPPUtil.decodeString(aElement, EPPResellerMapFactory.NS,
				ELM_EMAIL);

		// URL
		this.url = EPPUtil.decodeString(aElement, EPPResellerMapFactory.NS,
				ELM_URL);

		// Contacts
		this.contacts = EPPUtil.decodeCompList(aElement,
				EPPResellerMapFactory.NS, EPPResellerContact.ELM_NAME,
				EPPResellerContact.class);

		// Client Id
		this.clientId = EPPUtil.decodeString(aElement,
				EPPResellerMapFactory.NS, ELM_CLIENT_ID);

		// Created By
		this.createdBy = EPPUtil.decodeString(aElement,
				EPPResellerMapFactory.NS, ELM_CREATED_BY);

		// Created Date
		this.createdDate = EPPUtil.decodeTimeInstant(aElement,
				EPPResellerMapFactory.NS, ELM_CREATED_DATE);

		// Last Updated By
		this.lastUpdatedBy = EPPUtil.decodeString(aElement,
				EPPResellerMapFactory.NS, ELM_LAST_UPDATED_BY);

		// Last Updated Date
		this.lastUpdatedDate = EPPUtil.decodeTimeInstant(aElement,
				EPPResellerMapFactory.NS, ELM_LAST_UPDATED_DATE);

		// Disclose
		this.disclose = (EPPResellerDisclose) EPPUtil.decodeComp(aElement,
				EPPResellerMapFactory.NS, EPPResellerDisclose.ELM_NAME,
				EPPResellerDisclose.class);
	}

	/**
	 * Clone <code>EPPResellerInfoResp</code>.
	 * 
	 * @return clone of <code>EPPResellerInfoResp</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPResellerInfoResp clone = (EPPResellerInfoResp) super.clone();

		if (this.hasPostalInfo()) {
			clone.postalInfo = (List) ((ArrayList) this.postalInfo).clone();
		}

		if (this.hasContacts()) {
			clone.contacts = (List) ((ArrayList) this.contacts).clone();
		}

		return clone;
	}

	/**
	 * Gets the EPP response type associated with
	 * <code>EPPResellerInfoResp</code>.
	 * 
	 * @return <code>EPPResellerInfoResp.ELM_NAME</code>
	 */
	public String getType() {
		return ELM_NAME;
	}

	/**
	 * Gets the EPP command namespace associated with
	 * <code>EPPResellerInfoResp</code>.
	 * 
	 * @return <code>EPPResellerMapFactory.NS</code>
	 */
	public String getNamespace() {
		return EPPResellerMapFactory.NS;
	}

	/**
	 * Compare an instance of <code>EPPResellerInfoResp</code> with this
	 * instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return {@code true} if this object is the same as the aObject argument;
	 *         {@code false} otherwise
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPResellerInfoResp)) {
			return false;
		}

		EPPResellerInfoResp other = (EPPResellerInfoResp) aObject;

		// Reseller Identifier
		if (!EqualityUtil.equals(this.resellerId, other.resellerId)) {
			cat.error("EPPResellerInfoResp.equals(): resellerId not equal");
			return false;
		}

		// Roid
		if (!EqualityUtil.equals(this.roid, other.roid)) {
			cat.error("EPPResellerInfoResp.equals(): roid not equal");
			return false;
		}

		// State
		if (!EqualityUtil.equals(this.state, other.state)) {
			cat.error("EPPResellerInfoResp.equals(): state not equal");
			return false;
		}

		// Parent Id
		if (!EqualityUtil.equals(this.parentId, other.parentId)) {
			cat.error("EPPResellerInfoResp.equals(): parentId not equal");
			return false;
		}

		// Parent Id
		if (!EqualityUtil.equals(this.parentId, other.parentId)) {
			cat.error("EPPResellerInfoResp.equals(): parentId not equal");
			return false;
		}

		// Postal Information
		if (!EPPUtil.equalLists(this.postalInfo, other.postalInfo)) {
			cat.error("EPPResellerInfoResp.equals(): postalInfo not equal");
			return false;
		}

		// Voice
		if (!EqualityUtil.equals(this.voice, other.voice)) {
			cat.error("EPPResellerInfoResp.equals(): voice not equal");
			return false;
		}

		// Voice Ext
		if (!EqualityUtil.equals(this.voiceExt, other.voiceExt)) {
			cat.error("EPPResellerInfoResp.equals(): voiceExt not equal");
			return false;
		}

		// Fax
		if (!EqualityUtil.equals(this.fax, other.fax)) {
			cat.error("EPPResellerInfoResp.equals(): fax not equal");
			return false;
		}

		// Fax Ext
		if (!EqualityUtil.equals(this.faxExt, other.faxExt)) {
			cat.error("EPPResellerInfoResp.equals(): faxExt not equal, this.faxExt = " + this.faxExt + ", other.faxExt = " + other.faxExt);
			return false;
		}

		// Email
		if (!EqualityUtil.equals(this.email, other.email)) {
			cat.error("EPPResellerInfoResp.equals(): email not equal");
			return false;
		}

		// Url
		if (!EqualityUtil.equals(this.url, other.url)) {
			cat.error("EPPResellerInfoResp.equals(): url not equal");
			return false;
		}

		// Contacts
		if (!EPPUtil.equalLists(this.contacts, other.contacts)) {
			cat.error("EPPResellerInfoResp.equals(): contacts not equal");
			return false;
		}

		// Client Id
		if (!EqualityUtil.equals(this.clientId, other.clientId)) {
			cat.error("EPPResellerInfoResp.equals(): clientId not equal");
			return false;
		}

		// Created By
		if (!EqualityUtil.equals(this.createdBy, other.createdBy)) {
			cat.error("EPPResellerInfoResp.equals(): createdBy not equal");
			return false;
		}

		// Created Date
		if (!EqualityUtil.equals(this.createdDate, other.createdDate)) {
			cat.error("EPPResellerInfoResp.equals(): createdDate not equal");
			return false;
		}

		// Last Updated By
		if (!EqualityUtil.equals(this.lastUpdatedBy, other.lastUpdatedBy)) {
			cat.error("EPPResellerInfoResp.equals(): lastUpdatedBy not equal");
			return false;
		}

		// Last Updated Date
		if (!EqualityUtil.equals(this.lastUpdatedDate, other.lastUpdatedDate)) {
			cat.error("EPPResellerInfoResp.equals(): lastUpdateDate not equal");
			return false;
		}

		// Disclose
		if (!EqualityUtil.equals(this.disclose, other.disclose)) {
			cat.error("EPPResellerInfoResp.equals(): disclose not equal");
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