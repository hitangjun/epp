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
package com.verisign.epp.interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.verisign.epp.codec.gen.EPPCodecComponent;
import com.verisign.epp.codec.gen.EPPResponse;
import com.verisign.epp.codec.reseller.EPPResellerCheckCmd;
import com.verisign.epp.codec.reseller.EPPResellerCheckResp;
import com.verisign.epp.codec.reseller.EPPResellerContact;
import com.verisign.epp.codec.reseller.EPPResellerCreateCmd;
import com.verisign.epp.codec.reseller.EPPResellerCreateResp;
import com.verisign.epp.codec.reseller.EPPResellerDeleteCmd;
import com.verisign.epp.codec.reseller.EPPResellerDisclose;
import com.verisign.epp.codec.reseller.EPPResellerInfoCmd;
import com.verisign.epp.codec.reseller.EPPResellerInfoResp;
import com.verisign.epp.codec.reseller.EPPResellerPostalDefinition;
import com.verisign.epp.codec.reseller.EPPResellerUpdateCmd;
import com.verisign.epp.codec.reseller.State;

/**
 * <code>EPPReseller</code> is the primary client interface class used for the
 * Reseller EPP mapping. An instance of <code>EPPReseller</code> is created with
 * an initialized <code>EPPSession</code>, and can be used for more than one
 * request within a single thread. A set of setter methods are provided to set
 * the attributes before a call to one of the send action methods. The responses
 * returned from the send action methods are either instances of
 * <code>EPPResponse</code> or instances of response classes in the
 * <code>com.verisign.epp.codec.reseller</code> package.
 *
 * @see com.verisign.epp.codec.reseller.EPPResellerCheckResp
 * @see com.verisign.epp.codec.reseller.EPPResellerInfoResp
 * @see com.verisign.epp.codec.reseller.EPPResellerCreateResp
 */
public class EPPReseller {

	/** An instance of a session. */
	private EPPSession session = null;

	/** Transaction Id provided by client */
	private String transId = null;

	/**
	 * Extension objects associated with the command. This is a
	 * <code>Vector</code> of <code>EPPCodecComponent</code> objects.
	 */
	private Vector extensions = null;

	/**
	 * Reseller identifiers
	 */
	private List<String> resellerIds = null;

	/**
	 * Operational state of reseller.
	 */
	private State state = null;

	/**
	 * Parent Identifier
	 */
	private String parentId = null;

	/**
	 * One or two postal information elements.
	 */
	private List<EPPResellerPostalDefinition> postalInfo = null;

	/**
	 * Voice number
	 */
	private String voice = null;

	/**
	 * Voice Extension
	 */
	private String voiceExt = null;

	/**
	 * FAX number
	 */
	private String fax = null;

	/**
	 * FAX Extension
	 */
	private String faxExt = null;

	/**
	 * Email Address
	 */
	private String email = null;

	/**
	 * URL
	 */
	private String url = null;

	/**
	 * Reseller contacts
	 */
	private List<EPPResellerContact> contacts = null;

	/**
	 * Add Reseller contacts
	 */
	private List<EPPResellerContact> addContacts = null;

	/**
	 * Remove Reseller contacts
	 */
	private List<EPPResellerContact> remContacts = null;

	/**
	 * Disclosure policies for reseller.
	 */
	private EPPResellerDisclose disclose = null;

	/**
	 * Constructs an <code>EPPReseller</code> given an initialized EPP session.
	 *
	 * @param aSession
	 *            Server session to use.
	 */
	public EPPReseller(EPPSession aSession) {
		this.session = aSession;

		return;
	}

	/**
	 * Adds a command extension object.
	 *
	 * @param aExtension
	 *            command extension object associated with the command
	 */
	public void addExtension(EPPCodecComponent aExtension) {
		if (this.extensions == null) {
			this.extensions = new Vector();
		}

		this.extensions.addElement(aExtension);
	}

	/**
	 * Sets the command extension objects.
	 *
	 * @param aExtensions
	 *            command extension objects associated with the command
	 */
	public void setExtensions(Vector aExtensions) {
		this.extensions = aExtensions;
	}

	/**
	 * Gets the command extensions.
	 *
	 * @return <code>Vector</code> of concrete <code>EPPCodecComponent</code>
	 *         associated with the command if exists; <code>null</code>
	 *         otherwise.
	 */
	public Vector getExtensions() {
		return this.extensions;
	}

	/**
	 * Sets the client transaction identifier.
	 *
	 * @param aTransId
	 *            Client transaction identifier
	 */
	public void setTransId(String aTransId) {
		this.transId = aTransId;
	}

	/**
	 * Get the list of reseller identifiers.
	 * 
	 * @return {@code List} of reseller identifier {@code String}'s
	 */
	public List<String> getResellerIds() {
		return this.resellerIds;
	}

	/**
	 * Adds a reseller identifier for use with a <code>send</code> method.
	 * Adding more than one reseller identifier is only supported by
	 * {@link #sendCheck()}.
	 *
	 * @param aResellerId
	 *            Reseller Identifier
	 */
	public void addResellerId(String aResellerId) {
		if (this.resellerIds == null) {
			this.resellerIds = new ArrayList<String>();
		}
		this.resellerIds.add(aResellerId);
	}

	/**
	 * Gets the operational state of the reseller.
	 * 
	 * @return Operational state if defined; <code>null</code> otherwise.
	 */
	public State getState() {
		return this.state;
	}

	/**
	 * Sets the operational state of the reseller supported.
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
		if (this.postalInfo == null) {
			this.postalInfo = new ArrayList<EPPResellerPostalDefinition>();
		}
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
		if (this.contacts == null) {
			this.contacts = new ArrayList<EPPResellerContact>();
		}
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
	 * Adds an add contact to the list of add contacts.
	 * 
	 * @param aContact
	 *            Contact to add to the list of add contacts.
	 */
	public void addAddContact(EPPResellerContact aContact) {
		if (this.addContacts == null) {
			this.addContacts = new ArrayList<EPPResellerContact>();
		}
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
		if (this.remContacts == null) {
			this.remContacts = new ArrayList<EPPResellerContact>();
		}
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
	 * Sends a Reseller Check Command to the server.<br>
	 * <br>
	 * There required attributes that must be set prior to executing
	 * {@link #sendCheck()} include:<br>
	 * <br>
	 * <ul>
	 * <li>{@link #addResellerId(String)} - Sets the reseller identifiers to
	 * check. At least one reseller identifier must be set.</li>
	 * </ul>
	 * <br>
	 * <br>
	 * The optional attributes can be set with the following:<br>
	 * <br>
	 * <ul>
	 * <li>{@link #setTransId(String)} - Sets the client transaction identifier.
	 * </li>
	 * </ul>
	 * 
	 * @return {@link EPPResellerCheckResp} with the check results</code>.
	 * 
	 * @throws EPPCommandException
	 *             On error
	 */
	public EPPResellerCheckResp sendCheck() throws EPPCommandException {
		if (this.resellerIds == null || this.resellerIds.isEmpty()) {
			throw new EPPCommandException(
					"At least one reseller identifier is required for sendCheck()");
		}

		// Create the command
		EPPResellerCheckCmd theCommand = new EPPResellerCheckCmd(this.transId);
		theCommand.setResellerIds(resellerIds);

		// Set command extension
		theCommand.setExtensions(this.extensions);

		// Reset Reseller attributes
		this.resetReseller();

		// process the command and response
		return (EPPResellerCheckResp) this.session.processDocument(theCommand,
				EPPResellerCheckResp.class);
	}

	/**
	 * Sends a Reseller Info Command to the server.<br>
	 * <br>
	 * There required attributes that must be set prior to executing
	 * {@link #sendInfo()} include:<br>
	 * <br>
	 * <ul>
	 * <li>{@link #addResellerId(String)} - Sets the reseller identifier to get
	 * the information for. Only one reseller identifier is valid.</li>
	 * </ul>
	 * <br>
	 * <br>
	 * The optional attributes can be set with the following:<br>
	 * <br>
	 * <ul>
	 * <li>{@link #setTransId(String)} - Sets the client transaction identifier.
	 * </li>
	 * </ul>
	 * 
	 * @return {@link EPPResellerInfoResp} that contains the reseller
	 *         information.
	 * 
	 * @throws EPPCommandException
	 *             On error
	 */
	public EPPResellerInfoResp sendInfo() throws EPPCommandException {
		if (this.resellerIds == null || this.resellerIds.size() != 1) {
			throw new EPPCommandException(
					"One reseller identifier is required for sendInfo()");
		}

		// Create the command
		EPPResellerInfoCmd theCommand = new EPPResellerInfoCmd(this.transId);
		theCommand.setResellerId(this.resellerIds.get(0));

		// Set command extension
		theCommand.setExtensions(this.extensions);

		// Reset Reseller attributes
		this.resetReseller();

		// process the command and response
		return (EPPResellerInfoResp) this.session.processDocument(theCommand,
				EPPResellerInfoResp.class);
	}

	/**
	 * Sends a Reseller Create Command to the server.<br>
	 * <br>
	 * There required attributes that must be set prior to executing
	 * <code>sendCheck()</code> include:<br>
	 * <br>
	 * <ul>
	 * <li>{@link #addResellerId(String)} - Sets the reseller identifier to
	 * create.</li>
	 * <li>{@link #addPostalInfo(EPPResellerPostalDefinition)} - Sets the postal
	 * information of the reseller.</li>
	 * <li>{@link #setEmail(String)} - Sets the reseller email.</li>
	 * </ul>
	 * <br>
	 * <br>
	 * The optional attributes can be set with the following:<br>
	 * <br>
	 * <ul>
	 * <li>{@link #setTransId(String)} - Sets the client transaction identifier.
	 * </li>
	 * <li>{@link #setState(State)} - Sets the state of the reseller with the
	 * default of {@link State#OK}.</li>
	 * <li>{@link #setParentId(String)} - Sets the parent reseller identifier of
	 * the reseller.</li>
	 * <li>{@link #setVoice(String)} - Sets the reseller voice number.</li>
	 * <li>{@link #setVoiceExt(String)} - Sets the reseller voice number
	 * extension.</li>
	 * <li>{@link #setFax(String)} - Sets the reseller fax number.</li>
	 * <li>{@link #setFaxExt(String)} - Sets the reseller fax number extension.</li>
	 * <li>{@link #setUrl(String)} - Sets the reseller URL.</li>
	 * <li>{@link #setContacts(List)} - Sets the reseller contacts.</li>
	 * <li>{@link #setDisclose(EPPResellerDisclose)} - Sets the client
	 * disclosure preferences for the reseller.</li>
	 * </ul>
	 * 
	 * @return {@link EPPResellerCreateResp} with the create result</code>.
	 * 
	 * @throws EPPCommandException
	 *             On error
	 */
	public EPPResellerCreateResp sendCreate() throws EPPCommandException {
		if (this.resellerIds == null || this.resellerIds.isEmpty()
				|| this.resellerIds.size() != 1) {
			throw new EPPCommandException(
					"A reseller identifier is required for sendCreate()");
		}
		if (this.postalInfo == null || this.postalInfo.isEmpty()
				|| this.postalInfo.size() > 2) {
			throw new EPPCommandException(
					"A reseller postal information must have 1 or 2 items");
		}
		if (this.email == null) {
			throw new EPPCommandException("A reseller email is required");
		}

		// Create the command
		EPPResellerCreateCmd theCommand = new EPPResellerCreateCmd(this.transId);
		theCommand.setResellerId(this.resellerIds.get(0));
		theCommand.setState(this.state);
		theCommand.setParentId(this.parentId);
		theCommand.setPostalInfo(this.postalInfo);
		theCommand.setVoice(this.voice);
		if (this.voice != null) {
			theCommand.setVoiceExt(this.voiceExt);
		}
		theCommand.setFax(this.fax);
		if (this.fax != null) {
			theCommand.setFaxExt(this.faxExt);
		}
		theCommand.setEmail(this.email);
		theCommand.setUrl(this.url);
		theCommand.setContacts(this.contacts);
		theCommand.setDisclose(this.disclose);

		// Set command extension
		theCommand.setExtensions(this.extensions);

		// Reset Reseller attributes
		this.resetReseller();

		// process the command and response
		return (EPPResellerCreateResp) this.session.processDocument(theCommand,
				EPPResellerCreateResp.class);
	}

	/**
	 * Sends a Reseller Delete Command to the server.<br>
	 * <br>
	 * There required attributes that must be set prior to executing
	 * {@link #sendInfo()} include:<br>
	 * <br>
	 * <ul>
	 * <li>{@link #addResellerId(String)} - Sets the reseller identifier to get
	 * deleted. Only one reseller identifier is valid.</li>
	 * </ul>
	 * <br>
	 * <br>
	 * The optional attributes can be set with the following:<br>
	 * <br>
	 * <ul>
	 * <li>{@link #setTransId(String)} - Sets the client transaction identifier.
	 * </li>
	 * </ul>
	 * 
	 * @return {@link EPPResponse} that contains the deletion result.
	 * 
	 * @throws EPPCommandException
	 *             On error
	 */
	public EPPResponse sendDelete() throws EPPCommandException {
		if (this.resellerIds == null || this.resellerIds.size() != 1) {
			throw new EPPCommandException(
					"One reseller identifier is required for sendDelete()");
		}

		// Create the command
		EPPResellerDeleteCmd theCommand = new EPPResellerDeleteCmd(this.transId);
		theCommand.setResellerId(this.resellerIds.get(0));

		// Set command extension
		theCommand.setExtensions(this.extensions);

		// Reset Reseller attributes
		this.resetReseller();

		// process the command and response
		return (EPPResponse) this.session.processDocument(theCommand,
				EPPResponse.class);
	}

	/**
	 * Sends a Reseller Update Command to the server.<br>
	 * <br>
	 * There required attributes that must be set prior to executing
	 * <code>sendCheck()</code> include:<br>
	 * <br>
	 * <ul>
	 * <li>{@link #addResellerId(String)} - Sets the reseller identifier to
	 * create.</li>
	 * </ul>
	 * <br>
	 * <br>
	 * The optional attributes can be set with the following:<br>
	 * <br>
	 * <ul>
	 * <li>{@link #setTransId(String)} - Sets the client transaction identifier.
	 * </li>
	 * <li>{@link #setState(State)} - Sets the state of the reseller with the
	 * default of {@link State#OK}.</li>
	 * <li>{@link #setParentId(String)} - Sets the parent reseller identifier of
	 * the reseller.</li>
	 * <li>{@link #addPostalInfo(EPPResellerPostalDefinition)} - Sets the postal
	 * information of the reseller.</li>
	 * <li>{@link #setVoice(String)} - Sets the reseller voice number.</li>
	 * <li>{@link #setVoiceExt(String)} - Sets the reseller voice number
	 * extension.</li>
	 * <li>{@link #setFax(String)} - Sets the reseller fax number.</li>
	 * <li>{@link #setFaxExt(String)} - Sets the reseller fax number extension.</li>
	 * <li>{@link #setEmail(String)} - Sets the reseller email.</li>
	 * <li>{@link #setUrl(String)} - Sets the reseller URL.</li>
	 * <li>{@link #setAddContacts(List)} - Sets the reseller contacts to add.</li>
	 * <li>{@link #setRemContacts(List)} - Sets the reseller contacts to remove.
	 * </li>
	 * <li>{@link #setDisclose(EPPResellerDisclose)} - Sets the client
	 * disclosure preferences for the reseller.</li>
	 * </ul>
	 * 
	 * @return {@link EPPResponse} with the update result</code>.
	 * 
	 * @throws EPPCommandException
	 *             On error
	 */
	public EPPResponse sendUpdate() throws EPPCommandException {
		if (this.resellerIds == null || this.resellerIds.isEmpty()
				|| this.resellerIds.size() != 1) {
			throw new EPPCommandException(
					"A reseller identifier is required for sendCreate()");
		}

		// Create the command
		EPPResellerUpdateCmd theCommand = new EPPResellerUpdateCmd(this.transId);
		theCommand.setResellerId(this.resellerIds.get(0));
		theCommand.setState(this.state);
		theCommand.setParentId(this.parentId);
		theCommand.setPostalInfo(this.postalInfo);
		theCommand.setVoice(this.voice);
		if (this.voice != null) {
			theCommand.setVoiceExt(this.voiceExt);
		}
		theCommand.setFax(this.fax);
		if (this.fax != null) {
			theCommand.setFaxExt(this.faxExt);
		}
		theCommand.setEmail(this.email);
		theCommand.setUrl(this.url);
		theCommand.setAddContacts(this.addContacts);
		theCommand.setRemContacts(this.remContacts);
		theCommand.setDisclose(this.disclose);

		// Set command extension
		theCommand.setExtensions(this.extensions);

		// Reset Reseller attributes
		this.resetReseller();

		// process the command and response
		return (EPPResponse) this.session.processDocument(theCommand,
				EPPResponse.class);
	}

	/**
	 * Resets the Reseller instance to its initial state.
	 */
	protected void resetReseller() {
		this.transId = null;
		this.extensions = null;
		this.resellerIds = null;
		this.state = null;
		this.parentId = null;
		this.postalInfo = null;
		this.voice = null;
		this.voiceExt = null;
		this.fax = null;
		this.faxExt = null;
		this.email = null;
		this.url = null;
		this.contacts = null;
		this.addContacts = null;
		this.remContacts = null;
		this.disclose = null;
	}

	/**
	 * Gets the response associated with the last command. This method can be
	 * used to retrieve the server error response in the catch block of
	 * EPPCommandException.
	 *
	 * @return Response associated with the last command
	 */
	public EPPResponse getResponse() {
		return this.session.getResponse();
	}

}
