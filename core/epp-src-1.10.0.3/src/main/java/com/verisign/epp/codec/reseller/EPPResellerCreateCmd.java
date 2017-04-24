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
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.verisign.epp.codec.gen.EPPCreateCmd;
import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EqualityUtil;

/**
 * Command used to create a reseller object.
 * 
 * @see com.verisign.epp.codec.reseller.EPPResellerCreateResp
 */
public class EPPResellerCreateCmd extends EPPCreateCmd {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPResellerCreateCmd.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * XML local name for <code>EPPResellerCreateCmd</code>.
	 */
	public static final String ELM_LOCALNAME = "create";

	/**
	 * XML root tag for <code>EPPResellerCreateCmd</code>.
	 */
	public static final String ELM_NAME = EPPResellerMapFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * XML local name for the reseller identifier element
	 */
	private static final String ELM_RESELLER_ID = "id";

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
	 * XML extension "x" attribute value for voice or fax elements.
	 */
	private final static String ATTR_EXT = "x";

	/**
	 * The reseller identifier of the reseller of a sponsoring registrar.
	 */
	private String resellerId;

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
	 * Disclosure policies for reseller.
	 */
	private EPPResellerDisclose disclose;

	/**
	 * <code>EPPResellerCreateCmd</code> default constructor with the default
	 * form of <code>Form.LIST_FORM</code>.
	 */
	public EPPResellerCreateCmd() {
	}

	/**
	 * <code>EPPResellerCreateCmd</code> constructor that takes the client
	 * transaction identifier.
	 * 
	 * @param aTransId
	 *            Client transaction identifier.
	 */
	public EPPResellerCreateCmd(String aTransId) {
		super(aTransId);
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
	 *            Operation state. If <code>null</code> the code will be set to
	 *            the default of {@link State#OK}.
	 */
	public void setState(State aState) {
		if (aState == null) {
			this.state = State.OK;
		}
		else {
			this.state = aState;
		}
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
		if (aPostalInfo == null) {
			this.postalInfo = new ArrayList<EPPResellerPostalDefinition>();
		}
		else {
			this.postalInfo = aPostalInfo;			
		}
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
	 * Gets the list of reseller contacts.
	 * 
	 * @return The list of reseller contacts.
	 */
	public List<EPPResellerContact> getContacts() {
		return this.contacts;
	}

	/**
	 * Sets the list of reseller contacts.
	 * 
	 * @param aContacts
	 *            List of reseller contacts. If <code>null</code> will set to an
	 *            empty list.
	 */
	public void setContacts(List<EPPResellerContact> aContacts) {
		if (aContacts == null) {
			this.contacts = new ArrayList<EPPResellerContact>();
		}
		else {
			this.contacts = aContacts;
		}
	}

	/**
	 * Gets the disclosure information.
	 * 
	 * @return Disclosure information if defined; <code>null</code> otherwise.
	 */
	public EPPResellerDisclose getDisclose() {
		return this.disclose;
	}

	/**
	 * Sets the disclosure information.
	 * 
	 * @param aDisclose
	 *            Disclosure information
	 */
	public void setDisclose(EPPResellerDisclose aDisclose) {
		this.disclose = aDisclose;
	}

	/**
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPResellerCreateCmd</code> instance.
	 * 
	 * @param aDocument
	 *            DOM Document that is being built. Used as an Element factory.
	 * 
	 * @return Element Root DOM Element representing the EPPResellerCreateCmd
	 *         instance.
	 * 
	 * @exception EPPEncodeException
	 *                Unable to encode EPPResellerCreateCmd instance.
	 */
	protected Element doEncode(Document aDocument) throws EPPEncodeException {

		Element theElm = null;
		Text theVal = null;

		// Check required attributes
		if (this.resellerId == null) {
			throw new EPPEncodeException(
					"Undefined resellerId in EPPResellerCreateCmd");
		}
		if (!this.hasPostalInfo()) {
			throw new EPPEncodeException(
					"Undefined postal information in EPPResellerCreateCmd");
		}
		if (this.email == null) {
			throw new EPPEncodeException(
					"Undefined email in EPPResellerCreateCmd");
		}

		// Create root element
		Element root = aDocument.createElementNS(EPPResellerMapFactory.NS,
				ELM_NAME);

		// Reseller Identifier
		EPPUtil.encodeString(aDocument, root, this.resellerId,
				EPPResellerMapFactory.NS, EPPResellerMapFactory.NS_PREFIX + ":"
						+ ELM_RESELLER_ID);

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

		// Disclose
		EPPUtil.encodeComp(aDocument, root, this.disclose);

		return root;
	}

	/**
	 * Decode the <code>EPPResellerCreateCmd</code> attributes from the aElement
	 * DOM Element tree.
	 * 
	 * @param aElement
	 *            Root DOM Element to decode <code>EPPResellerCreateCmd</code>
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

		// Disclose
		this.disclose = (EPPResellerDisclose) EPPUtil.decodeComp(aElement,
				EPPResellerMapFactory.NS, EPPResellerDisclose.ELM_NAME,
				EPPResellerDisclose.class);
	}

	/**
	 * Clone <code>EPPResellerCreateCmd</code>.
	 * 
	 * @return clone of <code>EPPResellerCreateCmd</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPResellerCreateCmd clone = (EPPResellerCreateCmd) super.clone();

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
	 * <code>EPPResellerCreateCmd</code>.
	 * 
	 * @return <code>EPPResellerCreateCmd.ELM_NAME</code>
	 */
	public String getType() {
		return ELM_NAME;
	}

	/**
	 * Gets the EPP command namespace associated with
	 * <code>EPPResellerCreateCmd</code>.
	 * 
	 * @return <code>EPPResellerMapFactory.NS</code>
	 */
	public String getNamespace() {
		return EPPResellerMapFactory.NS;
	}

	/**
	 * Compare an instance of <code>EPPResellerCreateCmd</code> with this
	 * instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return {@code true} if this object is the same as the aObject argument;
	 *         {@code false} otherwise
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPResellerCreateCmd)) {
			return false;
		}

		EPPResellerCreateCmd other = (EPPResellerCreateCmd) aObject;

		// Reseller Identifier
		if (!EqualityUtil.equals(this.resellerId, other.resellerId)) {
			cat.error("EPPResellerCreateCmd.equals(): resellerId not equal");
			return false;
		}

		// State
		if (!EqualityUtil.equals(this.state, other.state)) {
			cat.error("EPPResellerCreateCmd.equals(): state not equal");
			return false;
		}

		// Parent Id
		if (!EqualityUtil.equals(this.parentId, other.parentId)) {
			cat.error("EPPResellerCreateCmd.equals(): parentId not equal");
			return false;
		}

		// Parent Id
		if (!EqualityUtil.equals(this.parentId, other.parentId)) {
			cat.error("EPPResellerCreateCmd.equals(): parentId not equal");
			return false;
		}

		// Postal Information
		if (!EPPUtil.equalLists(this.postalInfo, other.postalInfo)) {
			cat.error("EPPResellerCreateCmd.equals(): postalInfo not equal");
			return false;
		}

		// Voice
		if (!EqualityUtil.equals(this.voice, other.voice)) {
			cat.error("EPPResellerCreateCmd.equals(): voice not equal");
			return false;
		}

		// Voice Ext
		if (!EqualityUtil.equals(this.voiceExt, other.voiceExt)) {
			cat.error("EPPResellerCreateCmd.equals(): voiceExt not equal");
			return false;
		}

		// Fax
		if (!EqualityUtil.equals(this.fax, other.fax)) {
			cat.error("EPPResellerCreateCmd.equals(): fax not equal");
			return false;
		}

		// Fax Ext
		if (!EqualityUtil.equals(this.faxExt, other.faxExt)) {
			cat.error("EPPResellerCreateCmd.equals(): faxExt not equal, this.faxExt = "
					+ this.faxExt + ", other.faxExt = " + other.faxExt);
			return false;
		}

		// Email
		if (!EqualityUtil.equals(this.email, other.email)) {
			cat.error("EPPResellerCreateCmd.equals(): email not equal");
			return false;
		}

		// Url
		if (!EqualityUtil.equals(this.url, other.url)) {
			cat.error("EPPResellerCreateCmd.equals(): url not equal");
			return false;
		}

		// Contacts
		if (!EPPUtil.equalLists(this.contacts, other.contacts)) {
			cat.error("EPPResellerCreateCmd.equals(): contacts not equal");
			return false;
		}

		// Disclose
		if (!EqualityUtil.equals(this.disclose, other.disclose)) {
			cat.error("EPPResellerCreateCmd.equals(): disclose not equal");
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
