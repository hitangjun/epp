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

import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPUpdateCmd;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EqualityUtil;

/**
 * Command used to update a reseller object.
 */
public class EPPResellerUpdateCmd extends EPPUpdateCmd {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPResellerUpdateCmd.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * XML local name for <code>EPPResellerUpdateCmd</code>.
	 */
	public static final String ELM_LOCALNAME = "update";

	/**
	 * XML root tag for <code>EPPResellerUpdateCmd</code>.
	 */
	public static final String ELM_NAME = EPPResellerMapFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * XML local name for the add element
	 */
	private static final String ELM_ADD = "add";

	/**
	 * XML local name for the rem element
	 */
	private static final String ELM_REM = "rem";

	/**
	 * XML local name for the chg element
	 */
	private static final String ELM_CHG = "chg";

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
	 * Operational state of reseller.
	 */
	private State state;

	/**
	 * Parent Identifier
	 */
	private String parentId;

	/**
	 * One or two postal information elements, represented by the
	 * {@link EPPResellerPostalDefinition} class.
	 */
	private List<EPPResellerPostalDefinition> postalInfo = new ArrayList<EPPResellerPostalDefinition>();

	/**
	 * Voice number
	 */
	private String voice;

	/**
	 * Voice Extension
	 */
	private String voiceExt;

	/**
	 * FAX number
	 */
	private String fax;

	/**
	 * FAX Extension
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
	 * Add Reseller contacts
	 */
	private List<EPPResellerContact> addContacts = new ArrayList<EPPResellerContact>();

	/**
	 * Remove Reseller contacts
	 */
	private List<EPPResellerContact> remContacts = new ArrayList<EPPResellerContact>();

	/**
	 * Disclosure policies for reseller.
	 */
	private EPPResellerDisclose disclose;

	/**
	 * <code>EPPResellerUpdateCmd</code> default constructor with the default
	 * form of <code>Form.LIST_FORM</code>.
	 */
	public EPPResellerUpdateCmd() {
	}

	/**
	 * <code>EPPResellerUpdateCmd</code> constructor that takes the client transaction 
	 * identifier.
	 * 
	 * @param aTransId Client transaction identifier.
	 */
	public EPPResellerUpdateCmd(String aTransId) {
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
	 * Has the state been set?
	 * 
	 * @return <code>true</code> if the state has been set; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasState() {
		return this.state != null ? true : false;
	}

	/**
	 * Gets the operational state of the reseller.
	 * 
	 * @return Operational state if set; <code>null</code> otherwise.
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
	 * Has the parent identifier been set?
	 * 
	 * @return <code>true</code> if the parent identifier has been set;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasParentId() {
		return this.parentId != null ? true : false;
	}

	/**
	 * Gets the parent reseller identifier for a hierarchy of resellers.
	 * 
	 * @return Parent identifier if set; <code>null</code> otherwise.
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
	 * Has the voice been set?
	 * 
	 * @return <code>true</code> if the voice has been set; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasVoice() {
		return this.voice != null ? true : false;
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
	 * Has the voice extension been set?
	 * 
	 * @return <code>true</code> if the voice extension has been set;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasVoiceExt() {
		return this.voiceExt != null ? true : false;
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
	 * Has the fax been set?
	 * 
	 * @return <code>true</code> if the fax has been set; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasFax() {
		return this.fax != null ? true : false;
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
	 * Has the fax extension been set?
	 * 
	 * @return <code>true</code> if the fax extension has been set;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasFaxExt() {
		return this.faxExt != null ? true : false;
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
	 * Has the email been set?
	 * 
	 * @return <code>true</code> if the email has been set; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasEmail() {
		return this.email != null ? true : false;
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
	 * Has the url been set?
	 * 
	 * @return <code>true</code> if the url has been set; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasUrl() {
		return this.url != null ? true : false;
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
	 * Is there any add contacts set?
	 * 
	 * @return <code>true</code> if there is at least one add
	 *         {@link EPPResellerContact} set; <code>false</code> otherwise.
	 */
	public boolean hasAddContacts() {
		if (this.addContacts != null && !this.addContacts.isEmpty()) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an add contact to the list of add contacts.
	 * 
	 * @param aContact
	 *            Contact to add to the list of add contacts.
	 */
	public void addAddContact(EPPResellerContact aContact) {
		this.addContacts.add(aContact);
	}

	/**
	 * Gets the add contacts.
	 * 
	 * @return List of add contacts set.
	 */
	public List<EPPResellerContact> getAddContacts() {
		return this.addContacts;
	}

	/**
	 * Sets the add contacts.
	 * 
	 * @param aContacts
	 *            List of add contacts
	 */
	public void setAddContacts(List<EPPResellerContact> aContacts) {
		this.addContacts = aContacts;
	}

	/**
	 * Is there any remove contacts set?
	 * 
	 * @return <code>true</code> if there is at least one remove
	 *         {@link EPPResellerContact} set; <code>false</code> otherwise.
	 */
	public boolean hasRemContacts() {
		if (this.remContacts != null && !this.remContacts.isEmpty()) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds a remove contact to the list of remove contacts.
	 * 
	 * @param aContact
	 *            Contact to add to the list of remove contacts.
	 */
	public void addRemContact(EPPResellerContact aContact) {
		this.remContacts.add(aContact);
	}

	/**
	 * Gets the remove contacts.
	 * 
	 * @return List of remove contacts set.
	 */
	public List<EPPResellerContact> getRemContacts() {
		return this.remContacts;
	}

	/**
	 * Sets the remove contacts.
	 * 
	 * @param aContacts
	 *            List of remove contacts
	 */
	public void setRemContacts(List<EPPResellerContact> aContacts) {
		this.remContacts = aContacts;
	}

	/**
	 * Has the disclose been set?
	 * 
	 * @return <code>true</code> if the disclose has been set;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasDisclose() {
		return this.disclose != null ? true : false;
	}

	/**
	 * Gets the disclose definition.
	 * 
	 * @return Disclose definition if defined; <code>null</code> other.
	 */
	public EPPResellerDisclose getDisclose() {
		return this.disclose;
	}

	/**
	 * Sets the disclose definition.
	 * 
	 * @param aDisclose
	 *            Disclose definition
	 */
	public void setDisclose(EPPResellerDisclose aDisclose) {
		this.disclose = aDisclose;
	}

	/**
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPResellerUpdateCmd</code> instance.
	 * 
	 * @param aDocument
	 *            DOM Document that is being built. Used as an Element factory.
	 * 
	 * @return Element Root DOM Element representing the EPPResellerUpdateCmd
	 *         instance.
	 * 
	 * @exception EPPEncodeException
	 *                Unable to encode EPPResellerUpdateCmd instance.
	 */
	protected Element doEncode(Document aDocument) throws EPPEncodeException {

		Element theElm = null;
		Text theVal = null;

		// Check required attributes
		if (this.resellerId == null) {
			throw new EPPEncodeException(
					"Undefined resellerId in EPPResellerUpdateCmd");
		}

		// Create root element
		Element root = aDocument.createElementNS(EPPResellerMapFactory.NS,
				ELM_NAME);

		// Reseller Identifier
		EPPUtil.encodeString(aDocument, root, this.resellerId,
				EPPResellerMapFactory.NS, EPPResellerMapFactory.NS_PREFIX + ":"
						+ ELM_RESELLER_ID);

		// Add elements
		if (this.hasAddContacts()) {
			Element addElm = aDocument.createElementNS(
					EPPResellerMapFactory.NS, EPPResellerMapFactory.NS_PREFIX
							+ ":" + ELM_ADD);
			EPPUtil.encodeCompList(aDocument, addElm, this.addContacts);
			root.appendChild(addElm);
		}

		// Remove elements
		if (this.hasRemContacts()) {
			Element remElm = aDocument.createElementNS(
					EPPResellerMapFactory.NS, EPPResellerMapFactory.NS_PREFIX
							+ ":" + ELM_REM);
			EPPUtil.encodeCompList(aDocument, remElm, this.remContacts);
			root.appendChild(remElm);
		}

		// Change elements
		if (this.hasState() || this.hasParentId() || this.hasPostalInfo()
				|| this.hasVoice() || this.hasFax() || this.hasEmail()
				|| this.hasUrl() || this.hasDisclose()) {
			Element chgElm = aDocument.createElementNS(
					EPPResellerMapFactory.NS, EPPResellerMapFactory.NS_PREFIX
							+ ":" + ELM_CHG);

			// State
			EPPUtil.encodeString(aDocument, chgElm, this.state.toString(),
					EPPResellerMapFactory.NS, EPPResellerMapFactory.NS_PREFIX
							+ ":" + ELM_STATE);

			// Parent Id
			EPPUtil.encodeString(aDocument, chgElm, this.parentId,
					EPPResellerMapFactory.NS, EPPResellerMapFactory.NS_PREFIX
							+ ":" + ELM_PARENT_ID);

			// Postal Information
			EPPUtil.encodeCompList(aDocument, chgElm, this.postalInfo);

			// Voice and Voice Extension
			if (this.voice != null) {
				theElm = aDocument.createElementNS(EPPResellerMapFactory.NS,
						EPPResellerMapFactory.NS_PREFIX + ":" + ELM_VOICE);
				theVal = aDocument.createTextNode(this.voice);
				theElm.appendChild(theVal);

				if (this.voiceExt != null) {
					theElm.setAttribute(ATTR_EXT, this.voiceExt);
				}

				chgElm.appendChild(theElm);
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

				chgElm.appendChild(theElm);
			}

			// Email
			EPPUtil.encodeString(aDocument, chgElm, this.email,
					EPPResellerMapFactory.NS, EPPResellerMapFactory.NS_PREFIX
							+ ":" + ELM_EMAIL);

			// URL
			EPPUtil.encodeString(aDocument, chgElm, this.url,
					EPPResellerMapFactory.NS, EPPResellerMapFactory.NS_PREFIX
							+ ":" + ELM_URL);

			// Disclose
			EPPUtil.encodeComp(aDocument, chgElm, this.disclose);
			root.appendChild(chgElm);
		}

		return root;
	}

	/**
	 * Decode the <code>EPPResellerUpdateCmd</code> attributes from the aElement
	 * DOM Element tree.
	 * 
	 * @param aElement
	 *            Root DOM Element to decode <code>EPPResellerUpdateCmd</code>
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
		
		// Add element
		Element theAddElm = EPPUtil.getElementByTagNameNS(aElement,
				EPPResellerMapFactory.NS, ELM_ADD);
		if (theAddElm != null) {
			// Add Contacts
			this.addContacts = EPPUtil.decodeCompList(theAddElm,
					EPPResellerMapFactory.NS, EPPResellerContact.ELM_NAME,
					EPPResellerContact.class);
		}

		// Remove element
		Element theRemElm = EPPUtil.getElementByTagNameNS(aElement,
				EPPResellerMapFactory.NS, ELM_REM);
		if (theRemElm != null) {
			// Remove Contacts
			this.remContacts = EPPUtil.decodeCompList(theRemElm,
					EPPResellerMapFactory.NS, EPPResellerContact.ELM_NAME,
					EPPResellerContact.class);
		}

		// Change element
		Element theChgElm = EPPUtil.getElementByTagNameNS(aElement,
				EPPResellerMapFactory.NS, ELM_CHG);
		if (theChgElm != null) {

			// State
			this.state = State.getState(EPPUtil.decodeString(theChgElm,
					EPPResellerMapFactory.NS, ELM_STATE));

			// Parent Id
			this.parentId = EPPUtil.decodeString(theChgElm,
					EPPResellerMapFactory.NS, ELM_PARENT_ID);

			// Postal Information
			this.postalInfo = EPPUtil.decodeCompList(theChgElm,
					EPPResellerMapFactory.NS,
					EPPResellerPostalDefinition.ELM_NAME,
					EPPResellerPostalDefinition.class);

			// Voice and Voice Extension
			theElm = EPPUtil.getElementByTagNameNS(theChgElm,
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
			theElm = EPPUtil.getElementByTagNameNS(theChgElm,
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
			this.email = EPPUtil.decodeString(theChgElm,
					EPPResellerMapFactory.NS, ELM_EMAIL);

			// URL
			this.url = EPPUtil.decodeString(theChgElm,
					EPPResellerMapFactory.NS, ELM_URL);

			// Disclose
			this.disclose = (EPPResellerDisclose) EPPUtil.decodeComp(theChgElm,
					EPPResellerMapFactory.NS, EPPResellerDisclose.ELM_NAME,
					EPPResellerDisclose.class);
		}

	}

	/**
	 * Clone <code>EPPResellerUpdateCmd</code>.
	 * 
	 * @return clone of <code>EPPResellerUpdateCmd</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPResellerUpdateCmd clone = (EPPResellerUpdateCmd) super.clone();

		if (this.hasPostalInfo()) {
			clone.postalInfo = (List) ((ArrayList) this.postalInfo).clone();
		}

		if (this.hasAddContacts()) {
			clone.addContacts = (List) ((ArrayList) this.addContacts).clone();
		}

		if (this.hasRemContacts()) {
			clone.remContacts = (List) ((ArrayList) this.remContacts).clone();
		}

		return clone;
	}

	/**
	 * Gets the EPP response type associated with
	 * <code>EPPResellerUpdateCmd</code>.
	 * 
	 * @return <code>EPPResellerUpdateCmd.ELM_NAME</code>
	 */
	public String getType() {
		return ELM_NAME;
	}

	/**
	 * Gets the EPP command namespace associated with
	 * <code>EPPResellerUpdateCmd</code>.
	 * 
	 * @return <code>EPPResellerMapFactory.NS</code>
	 */
	public String getNamespace() {
		return EPPResellerMapFactory.NS;
	}

	/**
	 * Compare an instance of <code>EPPResellerUpdateCmd</code> with this
	 * instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return {@code true} if this object is the same as the aObject argument;
	 *         {@code false} otherwise
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPResellerUpdateCmd)) {
			return false;
		}

		EPPResellerUpdateCmd other = (EPPResellerUpdateCmd) aObject;

		// Reseller Identifier
		if (!EqualityUtil.equals(this.resellerId, other.resellerId)) {
			cat.error("EPPResellerUpdateCmd.equals(): resellerId not equal");
			return false;
		}

		// State
		if (!EqualityUtil.equals(this.state, other.state)) {
			cat.error("EPPResellerUpdateCmd.equals(): state not equal");
			return false;
		}

		// Parent Id
		if (!EqualityUtil.equals(this.parentId, other.parentId)) {
			cat.error("EPPResellerUpdateCmd.equals(): parentId not equal");
			return false;
		}

		// Parent Id
		if (!EqualityUtil.equals(this.parentId, other.parentId)) {
			cat.error("EPPResellerUpdateCmd.equals(): parentId not equal");
			return false;
		}

		// Postal Information
		if (!EPPUtil.equalLists(this.postalInfo, other.postalInfo)) {
			cat.error("EPPResellerUpdateCmd.equals(): postalInfo not equal");
			return false;
		}

		// Voice
		if (!EqualityUtil.equals(this.voice, other.voice)) {
			cat.error("EPPResellerUpdateCmd.equals(): voice not equal");
			return false;
		}

		// Voice Ext
		if (!EqualityUtil.equals(this.voiceExt, other.voiceExt)) {
			cat.error("EPPResellerUpdateCmd.equals(): voiceExt not equal");
			return false;
		}

		// Fax
		if (!EqualityUtil.equals(this.fax, other.fax)) {
			cat.error("EPPResellerUpdateCmd.equals(): fax not equal");
			return false;
		}

		// Fax Ext
		if (!EqualityUtil.equals(this.faxExt, other.faxExt)) {
			cat.error("EPPResellerUpdateCmd.equals(): faxExt not equal, this.faxExt = "
					+ this.faxExt + ", other.faxExt = " + other.faxExt);
			return false;
		}

		// Email
		if (!EqualityUtil.equals(this.email, other.email)) {
			cat.error("EPPResellerUpdateCmd.equals(): email not equal");
			return false;
		}

		// Url
		if (!EqualityUtil.equals(this.url, other.url)) {
			cat.error("EPPResellerUpdateCmd.equals(): url not equal");
			return false;
		}

		// Add Contacts
		if (!EPPUtil.equalLists(this.addContacts, other.addContacts)) {
			cat.error("EPPResellerUpdateCmd.equals(): addContacts not equal");
			return false;
		}

		// Remove Contacts
		if (!EPPUtil.equalLists(this.remContacts, other.remContacts)) {
			cat.error("EPPResellerUpdateCmd.equals(): remContacts not equal");
			return false;
		}

		// Disclose
		if (!EqualityUtil.equals(this.disclose, other.disclose)) {
			cat.error("EPPResellerUpdateCmd.equals(): disclose not equal");
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
