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

import com.verisign.epp.codec.gen.EPPAuthInfo;
import com.verisign.epp.codec.gen.EPPCodecComponent;
import com.verisign.epp.codec.gen.EPPResponse;
import com.verisign.epp.codec.nv.EPPDomainNameVerification;
import com.verisign.epp.codec.nv.EPPNameVerificationCheckCmd;
import com.verisign.epp.codec.nv.EPPNameVerificationCheckResp;
import com.verisign.epp.codec.nv.EPPNameVerificationCreateCmd;
import com.verisign.epp.codec.nv.EPPNameVerificationCreateResp;
import com.verisign.epp.codec.nv.EPPNameVerificationInfoCmd;
import com.verisign.epp.codec.nv.EPPNameVerificationInfoResp;
import com.verisign.epp.codec.nv.EPPNameVerificationUpdateCmd;
import com.verisign.epp.codec.nv.EPPRealNameVerification;

/**
 * <code>EPPNameVerification</code> is the primary client interface class used
 * for the Name Verification EPP mapping. An instance of
 * <code>EPPNameVerification</code> is created with an initialized
 * <code>EPPSession</code>, and can be used for more than one request within a
 * single thread. A set of setter methods are provided to set the attributes
 * before a call to one of the send action methods. The responses returned from
 * the send action methods are either instances of <code>EPPResponse</code> or
 * instances of response classes in the
 * <code>com.verisign.epp.codec.nv</code> package.
 *
 * @see com.verisign.epp.codec.nv.EPPNameVerificationCheckResp
 * @see com.verisign.epp.codec.nv.EPPNameVerificationCreateResp
 * @see com.verisign.epp.codec.nv.EPPNameVerificationInfoResp
 */
public class EPPNameVerification {

	/**
	 * An instance of a session.
	 */
	private EPPSession session = null;

	/**
	 * Transaction Id provided by client
	 */
	private String transId = null;

	/**
	 * Extension objects associated with the command. This is a
	 * <code>Vector</code> of <code>EPPCodecComponent</code> objects.
	 */
	private Vector extensions = null;

	/**
	 * Domain Name Verification (DNV) used for a create.
	 */
	EPPDomainNameVerification dnv;

	/**
	 * Domain Name Verification (RNV) used for a create.
	 */
	EPPRealNameVerification rnv;

	/**
	 * Domain labels used for the check command.
	 */
	private List<String> labels = new ArrayList<String>();

	/**
	 * Verification code value used for info and update commands.
	 */
	private String code;

	/**
	 * Type of info used for an info command.
	 */
	private EPPNameVerificationInfoCmd.Type infoType;

	/**
	 * Authorization information value used for create and info commands.
	 */
	private EPPAuthInfo authInfo;

	/**
	 * Constructs an <code>EPPNameVerification</code> given an initialized EPP
	 * session.
	 *
	 * @param aSession
	 *            Server session to use.
	 */
	public EPPNameVerification(EPPSession aSession) {
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
	 * Add a label to the list of labels.
	 * 
	 * @param aLabel
	 *            Label to add
	 */
	public void addLabel(String aLabel) {
		this.labels.add(aLabel);
	}

	/**
	 * Set the auth info for a create or info command.
	 * 
	 * @param aAuthInfo
	 *            Authorization value
	 */
	public void setAuthInfo(String aAuthInfo) {
		this.authInfo = new EPPAuthInfo(aAuthInfo);
	}

	/**
	 * Sets the code for use with an info command or update command.
	 * 
	 * @param aCode
	 *            Verification code value
	 */
	public void setCode(String aCode) {
		this.code = aCode;
	}

	/**
	 * Sets the Domain Name Verification (DNV) information used on a create.
	 * 
	 * @param aDnv
	 *            Domain Name Verification (DNV) information
	 */
	public void setDnv(EPPDomainNameVerification aDnv) {
		this.dnv = aDnv;
	}

	/**
	 * Sets the Real Name Verification (RNV) information used on a create.
	 * 
	 * @param aRnv
	 *            Real Name Verification (RNV) information
	 */
	public void setRnv(EPPRealNameVerification aRnv) {
		this.rnv = aRnv;
	}

	/**
	 * Sets the info type that is used on an info command.
	 * 
	 * @param aInfoType
	 *            The info type (input or signedCode)
	 */
	public void setInfoType(EPPNameVerificationInfoCmd.Type aInfoType) {
		this.infoType = aInfoType;
	}

	/**
	 * Sends an Name Verification Check Command to the server.<br>
	 * <br>
	 * The required attributes that must be set prior to executing
	 * <code>sendCheck()</code> include:<br>
	 * <br>
	 * <ul>
	 * <li><code>addLabel(String)</code> - Add at least one domain label to
	 * check.</li>
	 * </ul>
	 * <br>
	 * <br>
	 * The optional attributes can be set with the following:<br>
	 * <br>
	 * <ul>
	 * <li><code>setTransId</code> - Sets the client transaction identifier.</li>
	 * </ul>
	 * 
	 * @return <code>EPPNameVerificationCheckResp</code>.
	 * 
	 * @throws EPPCommandException
	 *             On error
	 */
	public EPPNameVerificationCheckResp sendCheck() throws EPPCommandException {

		if (this.labels == null || this.labels.isEmpty()) {
			throw new EPPCommandException(
					"At least one label must be set for sendCheck()");
		}

		// Create the command
		EPPNameVerificationCheckCmd theCommand = new EPPNameVerificationCheckCmd(
				this.transId, this.labels);

		// Set command extension
		theCommand.setExtensions(this.extensions);

		// Reset IDN Table attributes
		this.resetNameVerification();

		// process the command and response
		return (EPPNameVerificationCheckResp) this.session.processDocument(
				theCommand, EPPNameVerificationCheckResp.class);
	}

	/**
	 * Sends an Name Verification Info Command to the server.<br>
	 * <br>
	 * The required attributes that must be set prior to executing
	 * <code>sendInfo()</code> include:<br>
	 * <br>
	 * <ul>
	 * <li><code>setCode(String)</code> - Sets the domain verification code to
	 * info.</li>
	 * <li><code>setType(EPPNameVerificationInfoCmd.Type)</code> - Sets the info
	 * type using one of the <code>EPPNameVerificationInfoCmd.Type</code>
	 * enumerated values.</li>
	 * </ul>
	 * <br>
	 * <br>
	 * The optional attributes can be set with the following:<br>
	 * <br>
	 * <ul>
	 * <li><code>setTransId</code> - Sets the client transaction identifier.</li>
	 * <li><code>setAuthInfo(String)</code> - Sets the authorization information
	 * to OPTIONAL authorize the info command.
	 * </ul>
	 * 
	 * @return <code>EPPNameVerificationInfoResp</code>.
	 * 
	 * @throws EPPCommandException
	 *             On error
	 */
	public EPPNameVerificationInfoResp sendInfo() throws EPPCommandException {
		if (this.code == null) {
			throw new EPPCommandException("Code must be set for sendInfo()");
		}
		if (this.infoType == null) {
			throw new EPPCommandException("infoType must be set for sendInfo()");
		}

		// Create the command
		EPPNameVerificationInfoCmd theCommand = new EPPNameVerificationInfoCmd(
				this.transId);
		theCommand.setCode(this.code);
		theCommand.setInfoType(this.infoType);

		if (this.authInfo != null) {
			theCommand.setAuthInfo(new EPPAuthInfo(this.authInfo));
		}

		// Set command extension
		theCommand.setExtensions(this.extensions);

		// Reset IDN Table attributes
		this.resetNameVerification();

		// process the command and response
		return (EPPNameVerificationInfoResp) this.session.processDocument(
				theCommand, EPPNameVerificationInfoResp.class);
	}

	/**
	 * Sends a Name Verification Create Command to the server.<br>
	 * <br>
	 * The required attributes that must be set prior to executing
	 * <code>sendCreate()</code> include:<br>
	 * <br>
	 * <ul>
	 * <li><code>setDnv(EPPDomainNameVerification)</code> or
	 * <code>setRnv(EPPRealNameVerification)</code> - Sets the domain or real
	 * name verification information needed to be verified.</li>
	 * <li><code>setAuthInfo(String)</code> - Sets the required authorization
	 * information for the Name Verification (NV) object.
	 * </ul>
	 * <br>
	 * <br>
	 * The optional attributes can be set with the following:<br>
	 * <br>
	 * <ul>
	 * <li><code>setTransId</code> - Sets the client transaction identifier.</li>
	 * </ul>
	 * 
	 * @return <code>EPPNameVerificationCreateResp</code>.
	 * 
	 * @throws EPPCommandException
	 *             On error
	 */
	public EPPNameVerificationCreateResp sendCreate()
			throws EPPCommandException {
		if (this.dnv != null && this.rnv != null) {
			throw new EPPCommandException("DNV or RNV both set on sendCreate()");
		}
		if (this.dnv == null && this.rnv == null) {
			throw new EPPCommandException(
					"DNV or RNV must be set on sendCreate()");
		}
		if (this.authInfo == null) {
			throw new EPPCommandException(
					"Auth Info must be set on sendCreate()");
		}

		EPPNameVerificationCreateCmd theCommand;
		if (this.dnv != null) {
			theCommand = new EPPNameVerificationCreateCmd(this.transId,
					this.dnv, this.authInfo);
		} // this.rnv != null
		else {
			theCommand = new EPPNameVerificationCreateCmd(this.transId,
					this.rnv, this.authInfo);
		}

		// Set command extension
		theCommand.setExtensions(this.extensions);

		// Reset Name Verification attributes
		this.resetNameVerification();

		// process the command and response
		return (EPPNameVerificationCreateResp) this.session.processDocument(
				theCommand, EPPNameVerificationCreateResp.class);
	}

	/**
	 * Sends an Name Verification Update Command to the server.<br>
	 * <br>
	 * The required attributes that must be set prior to executing
	 * <code>sendUpdate()</code> include:<br>
	 * <br>
	 * <ul>
	 * <li><code>setCode(String)</code> - Sets the verification code to update.
	 * <li><code>setAuthInfo(String)</code> - Sets the authorization information
	 * to set for the Name Verification (NV) object.</li>
	 * </ul>
	 * <br>
	 * <br>
	 * The optional attributes can be set with the following:<br>
	 * <br>
	 * <ul>
	 * <li><code>setTransId</code> - Sets the client transaction identifier.</li>
	 * </ul>
	 * 
	 * @return <code>EPPResponse</code>.
	 * 
	 * @throws EPPCommandException
	 *             On error
	 */
	public EPPResponse sendUpdate() throws EPPCommandException {

		if (this.code == null) {
			throw new EPPCommandException(
					"code must be set for sendUpdate()");
		}		
		if (this.authInfo == null) {
			throw new EPPCommandException(
					"authInfo must be set for sendUpdate()");
		}

		// Create the command
		EPPNameVerificationUpdateCmd theCommand = new EPPNameVerificationUpdateCmd(
				this.transId, this.code, this.authInfo);

		// Set command extension
		theCommand.setExtensions(this.extensions);

		// Reset IDN Table attributes
		this.resetNameVerification();

		// process the command and response
		return (EPPResponse) this.session.processDocument(
				theCommand, EPPResponse.class);
	}

	/**
	 * Resets the Name Verification instance to its initial state.
	 */
	protected void resetNameVerification() {
		this.code = null;
		this.labels = new ArrayList<String>();
		this.authInfo = null;
		this.dnv = null;
		this.rnv = null;
		this.infoType = null;
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
