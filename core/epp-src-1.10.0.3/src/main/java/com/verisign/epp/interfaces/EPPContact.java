/***********************************************************
Copyright (C) 2004 VeriSign, Inc.

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
package com.verisign.epp.interfaces;

import java.util.Vector;

import com.verisign.epp.codec.contact.EPPContactAddChange;
import com.verisign.epp.codec.contact.EPPContactCheckCmd;
import com.verisign.epp.codec.contact.EPPContactCheckResp;
import com.verisign.epp.codec.contact.EPPContactCreateCmd;
import com.verisign.epp.codec.contact.EPPContactDeleteCmd;
import com.verisign.epp.codec.contact.EPPContactInfoCmd;
import com.verisign.epp.codec.contact.EPPContactInfoResp;
import com.verisign.epp.codec.contact.EPPContactPostalDefinition;
import com.verisign.epp.codec.contact.EPPContactStatus;
import com.verisign.epp.codec.contact.EPPContactTransferCmd;
import com.verisign.epp.codec.contact.EPPContactTransferResp;
import com.verisign.epp.codec.contact.EPPContactUpdateCmd;
import com.verisign.epp.codec.gen.EPPAuthInfo;
import com.verisign.epp.codec.gen.EPPCodecComponent;
import com.verisign.epp.codec.gen.EPPCommand;
import com.verisign.epp.codec.gen.EPPResponse;

/**
 * <code>EPPContact</code> is the primary client interface class used for
 * contact management. An instance of <code>EPPContact</code> is created with an
 * initialized <code>EPPSession</code>, and can be used for more than one
 * request within a single thread. A set of setter methods are provided to set
 * the attributes before a call to one of the send action methods. The responses
 * returned from the send action methods are either instances of
 * <code>EPPResponse</code> or instances of response classes in the
 * <code>com.verisign.epp.codec.contact</code> package. <br>
 * <br>
 *
 * @author $Author: jim $
 * @version $Revision: 1.3 $
 *
 * @see com.verisign.epp.codec.gen.EPPResponse
 * @see com.verisign.epp.codec.contact.EPPContactCreateResp
 * @see com.verisign.epp.codec.contact.EPPContactInfoResp
 * @see com.verisign.epp.codec.contact.EPPContactCheckResp
 * @see com.verisign.epp.codec.contact.EPPContactTransferResp
 */
public class EPPContact {
	/** Status constants */
	public final static String STAT_OK = EPPContactStatus.ELM_STATUS_OK;

	/** pendingDelete status */
	public final static String STAT_PENDING_DELETE = EPPContactStatus.ELM_STATUS_PENDING_DELETE;

	/** pendingTransfer status */
	public final static String STAT_PENDING_TRANSFER = EPPContactStatus.ELM_STATUS_PENDING_TRANSFER;

	/** clientDeleteProhibited status */
	public final static String STAT_CLIENT_DELETE_PROHIBITED = EPPContactStatus.ELM_STATUS_CLIENT_DELETE_PROHIBITED;

	/** clientUpdateProhibited status */
	public final static String STAT_CLIENT_UPDATE_PROHIBITED = EPPContactStatus.ELM_STATUS_CLIENT_UPDATE_PROHIBITED;

	/** linked status */
	public final static String STAT_STATUS_LINKED = EPPContactStatus.ELM_STATUS_LINKED;

	/** serverDeleteProhibited status */
	public final static String STAT_SERVER_DELETE_PROHIBITED = EPPContactStatus.ELM_STATUS_SERVER_DELETE_PROHIBITED;

	/** serverUpdateProhibited status */
	public final static String STAT_SERVER_UPDATE_PROHIBITED = EPPContactStatus.ELM_STATUS_SERVER_UPDATE_PROHIBITED;

	/** clientTransferProhibited status */
	public final static String STAT_CLIENT_TRANSFER_PROHIBITED = EPPContactStatus.ELM_STATUS_CLIENT_TRANSFER_PROHIBITED;

	/** serverTransferProhibited status */
	public final static String STAT_SERVER_TRANSFER_PROHIBITED = EPPContactStatus.ELM_STATUS_SERVER_TRANSFER_PROHIBITED;

	/** Transfer approve constant */
	public static final String TRANSFER_APPROVE = EPPCommand.OP_APPROVE;

	/** Transfer cancel constant */
	public static final String TRANSFER_CANCEL = EPPCommand.OP_CANCEL;

	/** Transfer query constant */
	public static final String TRANSFER_QUERY = EPPCommand.OP_QUERY;

	/** Transfer reject constant */
	public static final String TRANSFER_REJECT = EPPCommand.OP_REJECT;

	/** Transfer request constant */
	public static final String TRANSFER_REQUEST = EPPCommand.OP_REQUEST;

	/** EPP session to use */
	private EPPSession session = null;

	/** Client transaction identifier */
	private String transId = null;

	/** <code>Vector</code> of contact identifier <code>String</code> objects */
	private Vector contactIds = new Vector();

	/** Transfer operation code using one of the <code>TRANSFER</code> constants */
	private String transferOpCode = null;

	/** Authorization Id specified for a call to <code>encodeTransfer</code>. */
	private String authString = null;

	/**
	 * This is Attribute Contains This Object Contains Information about the
	 * Contact
	 */
	private Vector postalContacts = new Vector();

	/** This is Attribute Contains Voice Phone Number */
	private String voicePhone = null;

	/** Voice Extension Number */
	private String voiceExt = null;

	/** This is Attribute Contains Fax Number */
	private String faxNumber = null;

	/** Fax extension */
	private String faxExt = null;

	/** This is Attribute Contains Email Address */
	private String email = null;

	/** This is Attribute Contains Vector of Add Status for Update Command */
	private Vector addStatuses = null;

	/** This is Attribute Contains Vector of Remove Status for Update Command */
	private Vector removeStatuses = null;

	/**
	 * Extension objects associated with the command. This is a
	 * <code>Vector</code> of <code>EPPCodecComponent</code> objects.
	 */
	private Vector extensions = null;

	/** disclose information of contact */
	private com.verisign.epp.codec.contact.EPPContactDisclose disclose = null;

	/**
	 * Constructs an <code>EPPContact</code> with an initialized
	 * <code>EPPSession</code> instance.
	 *
	 * @param aSession
	 *            Session to use to send contact commands
	 */
	public EPPContact(EPPSession aSession) {
		this.session = aSession;
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
	 * Sets a command extension object.
	 *
	 * @param aExtension
	 *            command extension object associated with the command
	 *
	 * @deprecated Replaced by {@link #addExtension(EPPCodecComponent)}. This
	 *             method will add the extension as is done in
	 *             {@link #addExtension(EPPCodecComponent)}.
	 */
	public void setExtension(EPPCodecComponent aExtension) {
		this.addExtension(aExtension);
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
	 * Sets the transfer operation for a call to <code>encodeTransfer</code>.
	 * The transfer code must be set to one of the
	 * <code>EPPContact.TRANSFER_</code> constants.
	 *
	 * @param aTransferOpCode
	 *            One of the <code>EPPContact.TRANSFER_</code> constants
	 */
	public void setTransferOpCode(String aTransferOpCode) {
		this.transferOpCode = aTransferOpCode;
	}

	/**
	 * This is a Getter Method for PostalContact private Attribute This
	 * Attribute maintains This Object Contains Information about the Contact
	 *
	 * @return Gets the <code>Vector</code> of
	 *         {@link EPPContactPostalDefinition} objects if set;
	 *         <code>null</code> otherwise.
	 */
	public Vector getPostalInfo() {
		return this.postalContacts;
	}

	/**
	 * Gets the first postal info object.
	 *
	 * @return First postal info object (<code>EPPContactPostalDefinition</code>
	 *         ) if it exists; <code>null</code> otherwise.
	 */
	public EPPContactPostalDefinition getFirstPostalInfo() {
		if (this.postalContacts != null && !this.postalContacts.isEmpty()) {
			return (EPPContactPostalDefinition) this.postalContacts.get(0);
		}
		else {
			return null;
		}
	}

	/**
	 * Set contact postalInfo.
	 *
	 * @param newPostalContacts
	 *            java.util.Vector
	 */
	public void setPostalInfo(java.util.Vector newPostalContacts) {
		this.postalContacts = newPostalContacts;
	}

	/**
	 * This is a Setter Method for PostalContact private Attribute This
	 * Attribute maintains This Object Contains Information about the Contact
	 *
	 * @param newPostalContact
	 *            DOCUMENT ME!
	 */
	public void addPostalInfo(EPPContactPostalDefinition newPostalContact) {
		this.postalContacts.add(newPostalContact);

		return;
	}

	/**
	 * Get disclose information.
	 *
	 * @return Disclose information if defined; <code>null</code> otherwise;
	 */
	public com.verisign.epp.codec.contact.EPPContactDisclose getDisclose() {
		return this.disclose;
	}

	/**
	 * Set disclose information.
	 *
	 * @param newDisclose
	 *            com.verisign.epp.codec.gen.EPPContactDisclose
	 */
	public void setDisclose(
			com.verisign.epp.codec.contact.EPPContactDisclose newDisclose) {
		if (newDisclose != null) {
			this.disclose = newDisclose;
		}
	}

	/**
	 * Sets the voice phone number.
	 *
	 * @param newVoicePhone
	 *            Voice phone number.
	 */
	public void setVoicePhone(String newVoicePhone) {
		this.voicePhone = newVoicePhone;
	}

	/**
	 * Sets the voice extension number.
	 *
	 * @param newVoiceExt
	 *            Voice extension number
	 */
	public void setVoiceExt(String newVoiceExt) {
		this.voiceExt = newVoiceExt;
	}

	/**
	 * Sets the fax number.
	 *
	 * @param newFaxNumber
	 *            Fax number.
	 */
	public void setFaxNumber(String newFaxNumber) {
		this.faxNumber = newFaxNumber;
	}

	/**
	 * Sets the fax extension number.
	 *
	 * @param newFaxExt
	 *            Fax extension number
	 */
	public void setFaxExt(String newFaxExt) {
		this.faxExt = newFaxExt;
	}

	/**
	 * This is a Setter Method for Email private Attribute This Attribute
	 * maintains Email Address
	 *
	 * @param newEmail
	 *            DOCUMENT ME!
	 */
	public void setEmail(String newEmail) {
		this.email = newEmail;

		return;
	}

	/**
	 * This is a Getter Method for VoicePhone private Attribute This Attribute
	 * maintains Voice Phone Number
	 *
	 * @return DOCUMENT ME!
	 */
	public String getVoicePhone() {
		return this.voicePhone;
	}

	/**
	 * This is a Getter Method for FaxNumber private Attribute This Attribute
	 * maintains Fax Number
	 *
	 * @return DOCUMENT ME!
	 */
	public String getFaxNumber() {
		return this.faxNumber;
	}

	/**
	 * Gets the fax extension.
	 *
	 * @return Fax extension if defined; <code>null</code> otherwise.
	 */
	public String getFaxExt() {
		return this.faxExt;
	}

	/**
	 * This is a Getter Method for Email private Attribute This Attribute
	 * maintains Email Address
	 *
	 * @return DOCUMENT ME!
	 */
	public String getEmail() {
		return this.email;
	}

	/**
	 * Adds a status to the host.
	 *
	 * @param aStatus
	 *            One of the <code>STAT_</code> constants
	 */
	public void addStatus(String aStatus) {
		if (this.addStatuses == null) {
			this.addStatuses = new Vector();
		}

		this.addStatuses.addElement(new EPPContactStatus(aStatus));
	}

	/**
	 * Removes a status from the host.
	 *
	 * @param aStatus
	 *            One of the <code>STAT_</code> constants
	 */
	public void removeStatus(String aStatus) {
		if (this.removeStatuses == null) {
			this.removeStatuses = new Vector();
		}

		this.removeStatuses.addElement(new EPPContactStatus(aStatus));
	}

	/**
	 * Adds a status to the host with a description.
	 *
	 * @param aStatus
	 *            One of the <code>STAT_</code> constants
	 * @param aDesc
	 *            Description of the rationale for the status change
	 * @param aLang
	 *            Language of <code>aDesc</code> Use <code>DEFAULT_LANG</code>
	 *            for the default language ("us").
	 */
	public void addStatus(String aStatus, String aDesc, String aLang) {
		if (this.addStatuses == null) {
			this.addStatuses = new Vector();
		}

		this.addStatuses
				.addElement(new EPPContactStatus(aStatus, aDesc, aLang));
	}

	/**
	 * Removes a status from the host with a description.
	 *
	 * @param aStatus
	 *            One of the <code>STAT_</code> constants
	 * @param aDesc
	 *            Description of the rationale for the status change
	 * @param aLang
	 *            Language of <code>aDesc</code> Use <code>DEFAULT_LANG</code>
	 *            for the default language ("us").
	 */
	public void removeStatus(String aStatus, String aDesc, String aLang) {
		if (this.removeStatuses == null) {
			this.removeStatuses = new Vector();
		}

		this.removeStatuses
				.addElement(new EPPContactStatus(aStatus, aDesc, aLang));
	}

	/**
	 * This is a Getter Method for AddStatus private Attribute This Attribute
	 * maintains Vector of Add Status for Update Command
	 *
	 * @return DOCUMENT ME!
	 */
	public Vector getAddStatus() {
		return this.addStatuses;
	}

	/**
	 * This is a Getter Method for RemoveStatus private Attribute This Attribute
	 * maintains Vector of Remove Status for Update Command
	 *
	 * @return DOCUMENT ME!
	 */
	public Vector getRemoveStatus() {
		return this.removeStatuses;
	}

	/**
	 * This method set the myContactInfo attribute.
	 *
	 * @param newContactId
	 *            DOCUMENT ME!
	 */
	public void addContactId(String newContactId) {
		this.contactIds.addElement(newContactId);

		return;
	}

	/**
	 * Sets the authorization identifier for transfer operations using the
	 * <code>encodeTransfer</code> method.
	 *
	 * @param newAuthorizationId
	 *            unique daily client code<code>String</code>.
	 */
	public void setAuthorizationId(String newAuthorizationId) {
		this.authString = newAuthorizationId;
	}

	/**
	 * Setter method for TransID instance variable
	 *
	 * @param newTransId
	 *            DOCUMENT ME!
	 */
	public void setTransId(String newTransId) {
		this.transId = newTransId;

		return;
	}

	/**
	 * Getter method for TransID instance variable
	 *
	 * @return DOCUMENT ME!
	 */
	public String getTransId() {
		return this.transId;
	}

	/**
	 * gets the authorization identifier for transfer operations using the
	 * <code>encodeTransfer</code> method.
	 *
	 * @return DOCUMENT ME!
	 */
	public String getAuthorizationId() {
		return this.authString;
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

	/**
	 * Sends a Contact Create Command to the server.<br>
	 * <br>
	 * The required attributes have been set with the following methods:<br>
	 * <br>
	 *
	 * <ul>
	 * <li><code>addContactId</code> - Sets the contact to update. Only one
	 * contact is valid.</li>
	 * <li><code>setPostalContact</code> - Set the postal information</li>
	 * <li><code>setEmail</code> - Set the email address</li>
	 * <li><code>setAuthString</code> - Sets the contact authorization string.
	 * </li>
	 * </ul>
	 *
	 * <br>
	 * <br>
	 * The optional attributes have been set with the following:<br>
	 * <br>
	 *
	 * <ul>
	 * <li><code>setTransId</code> - Sets the client transaction identifier</li>
	 * <li><code>setExtension</code> - Command extension to send with command
	 * </li>
	 * <li><code>setVoicePhone</code> - Set the voice phone</li>
	 * <li><code>setFaxNumber</code> - Set the fax number</li>
	 * <li><code>setInterPostalPostalContact</code> - Set the international
	 * postal information</li>
	 * </ul>
	 *
	 *
	 * @return <code>EPPContactCreateResp</code> containing the contact create
	 *         result.
	 *
	 * @exception EPPCommandException
	 *                Error executing the update command. Use
	 *                <code>getResponse</code> to get the associated server
	 *                error response.
	 */
	public EPPResponse sendCreate() throws EPPCommandException {
		// Invalid number of Contact Ids?
		if (this.contactIds.size() != 1) {
			throw new EPPCommandException(
					"One Contact Id is required for sendCreate()");
		}

		// Authorization String not specified?
		if (this.authString == null) {
			throw new EPPCommandException("Authorization String is required.");
		}

		// Create the command
		EPPContactCreateCmd theCommand = new EPPContactCreateCmd(this.transId);

		// Check for Contact ID
		theCommand.setId((String) this.contactIds.firstElement());

		// Set the Authorization String
		theCommand.setAuthInfo(new EPPAuthInfo(this.authString));

		theCommand.setDisclose(this.disclose);

		// Set the E-Mail
		theCommand.setEmail(this.email);

		// Set the Fax Number
		theCommand.setFax(this.faxNumber);

		// Set the Fax Extension
		theCommand.setFaxExt(this.faxExt);

		// Set Postal Information
		for (int i = 0; i < this.postalContacts.size(); i++) {
			theCommand.addPostalInfo(
					(EPPContactPostalDefinition) this.postalContacts
							.elementAt(i));
		}

		// Set Voice Phone Number
		theCommand.setVoice(this.voicePhone);

		// Set Voice Phone Extension
		theCommand.setVoiceExt(this.voiceExt);

		// Set command extension
		theCommand.setExtensions(this.extensions);

		// Reset contact attributes
		resetContact();

		// process the command and response
		return this.session.processDocument(theCommand, EPPResponse.class);
	}

	/**
	 * Sends a Contact Check Command to the server.<br>
	 * <br>
	 * The required attributes have been set with the following methods:<br>
	 * <br>
	 *
	 * <ul>
	 * <li><code>addContactName</code> - Adds a contact to check. More than one
	 * contact can be checked in <code>sendCheck</code></li>
	 * </ul>
	 *
	 * <br>
	 * <br>
	 * The optional attributes have been set with the following:<br>
	 * <br>
	 *
	 * <ul>
	 * <li><code>setTransId</code> - Sets the client transaction identifier</li>
	 * <li><code>setExtension</code> - Command extension to send with command
	 * </li>
	 * </ul>
	 *
	 *
	 * @return <code>EPPContactCheckResp</code> containing the contact check
	 *         information.
	 *
	 * @exception EPPCommandException
	 *                Error executing the check command. Use
	 *                <code>getResponse</code> to get the associated server
	 *                error response.
	 */
	public EPPContactCheckResp sendCheck() throws EPPCommandException {
		// Invalid number of Contact Ids?
		if (this.contactIds.size() == 0) {
			throw new EPPCommandException(
					"At least One Contact Id is required for sendCheck()");
		}

		// Create the command
		EPPContactCheckCmd theCommand = new EPPContactCheckCmd(this.transId,
				this.contactIds);

		// Set command extension
		theCommand.setExtensions(this.extensions);

		// Reset contact attributes
		resetContact();

		// process the command and response
		return (EPPContactCheckResp) this.session.processDocument(theCommand,
				EPPContactCheckResp.class);
	}

	/**
	 * Sends a Contact Info Command to the server.<br>
	 * <br>
	 * The required attributes have been set with the following methods:<br>
	 * <br>
	 *
	 * <ul>
	 * <li><code>addContactId</code> - Sets the contact to get info for. Only
	 * one contact is valid.</li>
	 * </ul>
	 *
	 * <br>
	 * <br>
	 * The optional attributes have been set with the following:<br>
	 * <br>
	 *
	 * <ul>
	 * <li><code>setTransId</code> - Sets the client transaction identifier</li>
	 * <li><code>setExtension</code> - Command extension to send with command
	 * </li>
	 * </ul>
	 *
	 *
	 * @return <code>EPPContactInfoResp</code> containing the contact
	 *         information.
	 *
	 * @exception EPPCommandException
	 *                Error executing the info command. Use
	 *                <code>getResponse</code> to get the associated server
	 *                error response.
	 */
	public EPPContactInfoResp sendInfo() throws EPPCommandException {
		// Invalid number of Contact Names?
		if (this.contactIds.size() != 1) {
			throw new EPPCommandException(
					"One Contact Id is required for sendInfo()");
		}

		// Create the command
		EPPContactInfoCmd theCommand = new EPPContactInfoCmd(this.transId,
				(String) this.contactIds.firstElement());

		// Authorization string was provided?
		if (this.authString != null) {
			EPPAuthInfo theAuthInfo = new EPPAuthInfo(this.authString);
			theCommand.setAuthInfo(theAuthInfo);
		}

		// Set command extension
		theCommand.setExtensions(this.extensions);

		// Reset contact attributes
		resetContact();

		// process the command and response
		return (EPPContactInfoResp) this.session.processDocument(theCommand,
				EPPContactInfoResp.class);
	}

	/**
	 * Sends a Contact Update Command to the server.<br>
	 * <br>
	 * The required attributes have been set with the following methods:<br>
	 * <br>
	 *
	 * <ul>
	 * <li><code>addContactId</code> - Sets the contact to update. Only one
	 * contact is valid.</li>
	 * </ul>
	 *
	 * <br>
	 * <br>
	 * The optional attributes have been set with the following:<br>
	 * <br>
	 *
	 * <ul>
	 * <li><code>setTransId</code> - Sets the client transaction identifier</li>
	 * <li><code>setExtension</code> - Command extension to send with command
	 * </li>
	 * <li><code>addStatus</code> - Add status</li>
	 * <li><code>removeStatus</code> - Remove status</li>
	 * <li><code>setPostalContact</code> - Set the postal information</li>
	 * <li><code>setVoicePhone</code> - Set the voice phone</li>
	 * <li><code>setFaxNumber</code> - Set the fax number</li>
	 * <li><code>setEmail</code> - Set the email address</li>
	 * <li><code>setInterPostalPostalContact</code> - Set the international
	 * postal information</li>
	 * <li><code>setAuthString</code> - Sets the contact authorization string.
	 * </li>
	 * </ul>
	 *
	 * At least one update attribute needs to be set.
	 *
	 * @return <code>EPPResponse</code> containing the contact update result.
	 *
	 * @exception EPPCommandException
	 *                Error executing the update command. Use
	 *                <code>getResponse</code> to get the associated server
	 *                error response.
	 */
	public EPPResponse sendUpdate() throws EPPCommandException {
		// Invalid number of Contact ids?
		if (this.contactIds.size() != 1) {
			throw new EPPCommandException(
					"One Contact Id is required for sendUpdate()");
		}

		// Add attributes specified?
		EPPContactAddChange addItems = null;

		if (this.addStatuses != null) {
			addItems = new EPPContactAddChange(this.addStatuses);
		}

		// Remove attributes specified?
		EPPContactAddChange removeItems = null;

		if (this.removeStatuses != null) {
			removeItems = new EPPContactAddChange(this.removeStatuses);
		}

		// Change attributes specified?
		EPPContactAddChange changeItems = null;

		if (this.postalContacts.size() > 0 || this.voicePhone != null
				|| this.faxNumber != null || this.email != null
				|| this.authString != null) {
			changeItems = new EPPContactAddChange(this.postalContacts,
					this.voicePhone, this.faxNumber, this.email,
					this.authString != null ? new EPPAuthInfo(this.authString)
							: null);

			changeItems.setVoiceExt(this.voiceExt);
			changeItems.setFaxExt(this.faxExt);
			changeItems.setDisclose(this.disclose);
		}

		// Create the command
		EPPContactUpdateCmd theCommand = new EPPContactUpdateCmd(this.transId,
				(String) this.contactIds.firstElement(), addItems, removeItems,
				changeItems);

		// Set command extension
		theCommand.setExtensions(this.extensions);

		// Reset contact attributes
		resetContact();

		// process the command and response
		return this.session.processDocument(theCommand, EPPResponse.class);
	}

	/**
	 * Sends a Contact Transfer Command to the server.<br>
	 * <br>
	 * The required attributes have been set with the following methods:<br>
	 * <br>
	 *
	 * <ul>
	 * <li><code>addContactId</code> - Sets the contact for transfer command.
	 * Only one contact is valid.</li>
	 * <li><code>setTransferOpCode</code> - Sets the contact transfer operation.
	 * </li>
	 * <li><code>setAuthString</code> - Sets the contact authorization string.
	 * </li>
	 * </ul>
	 *
	 * <br>
	 * <br>
	 * The optional attributes have been set with the following:<br>
	 * <br>
	 *
	 * <ul>
	 * <li><code>setTransId</code> - Sets the client transaction identifier</li>
	 * <li><code>setExtension</code> - Command extension to send with command
	 * </li>
	 * </ul>
	 *
	 *
	 * @return <code>EPPContactTransferResp</code> containing the contact
	 *         transfer result.
	 *
	 * @exception EPPCommandException
	 *                Error executing the create command. Use
	 *                <code>getResponse</code> to get the associated server
	 *                error response.
	 */
	public EPPContactTransferResp sendTransfer() throws EPPCommandException {
		// Invalid number of Contact Ids?
		if (this.contactIds.size() != 1) {
			throw new EPPCommandException(
					"One Contact Id is required for sendTransfer()");
		}

		// Transfer Operation Code not specified?
		if (this.transferOpCode == null) {
			throw new EPPCommandException(
					"Transfer Operation Code is required.");
		}

		// Create the command
		EPPContactTransferCmd theCommand = new EPPContactTransferCmd(
				this.transId, this.transferOpCode,
				(String) this.contactIds.firstElement());

		if (this.authString != null) {
			theCommand.setAuthInfo(new EPPAuthInfo(this.authString));
		}

		// Set command extension
		theCommand.setExtensions(this.extensions);

		// Reset contact attributes
		resetContact();

		// process the command and response
		return (EPPContactTransferResp) this.session.processDocument(theCommand,
				EPPContactTransferResp.class);
	}

	/**
	 * Sends a Contact Delete Command to the server.<br>
	 * <br>
	 * The required attributes have been set with the following methods:<br>
	 * <br>
	 *
	 * <ul>
	 * <li><code>addContactId</code> - Sets the contact to delete. Only one
	 * contact is valid.</li>
	 * </ul>
	 *
	 * <br>
	 * <br>
	 * The optional attributes have been set with the following:<br>
	 * <br>
	 *
	 * <ul>
	 * <li><code>setTransId</code> - Sets the client transaction identifier</li>
	 * <li><code>setExtension</code> - Command extension to send with command
	 * </li>
	 * </ul>
	 *
	 *
	 * @return <code>EPPResponse</code> containing the delete result
	 *         information.
	 *
	 * @exception EPPCommandException
	 *                Error executing the delete command. Use
	 *                <code>getResponse</code> to get the associated server
	 *                error response.
	 */
	public EPPResponse sendDelete() throws EPPCommandException {
		// Invalid number of Contact ids?
		if (this.contactIds.size() != 1) {
			throw new EPPCommandException(
					"One Contact Id is required for sendDelete()");
		}

		// Create the command
		EPPContactDeleteCmd theCommand = new EPPContactDeleteCmd(this.transId,
				(String) this.contactIds.firstElement());

		// Set command extension
		theCommand.setExtensions(this.extensions);

		// Reset contact attributes
		resetContact();

		// process the command and response
		return this.session.processDocument(theCommand, EPPResponse.class);
	}

	/**
	 * Resets the contact instance to its initial state.
	 */
	protected void resetContact() {
		this.transId = null;
		this.postalContacts = new Vector();
		this.voicePhone = null;
		this.voiceExt = null;
		this.faxNumber = null;
		this.faxExt = null;
		this.email = null;
		this.contactIds = new Vector();
		this.addStatuses = null;
		this.removeStatuses = null;
		this.transferOpCode = null;
		this.extensions = null;
	}
}
