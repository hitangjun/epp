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
import com.verisign.epp.codec.idntable.EPPIdnTableCheckCmd;
import com.verisign.epp.codec.idntable.EPPIdnTableCheckResp;
import com.verisign.epp.codec.idntable.EPPIdnTableDomainLabel;
import com.verisign.epp.codec.idntable.EPPIdnTableInfoCmd;
import com.verisign.epp.codec.idntable.EPPIdnTableInfoResp;

/**
 * <code>EPPIdnTable</code> is the primary client interface class used for the
 * IDN Table EPP mapping. An instance of <code>EPPIdnTable</code> is created
 * with an initialized <code>EPPSession</code>, and can be used for more than
 * one request within a single thread. A set of setter methods are provided to
 * set the attributes before a call to one of the send action methods. The
 * responses returned from the send action methods are either instances of
 * <code>EPPResponse</code> or instances of response classes in the
 * <code>com.verisign.epp.codec.idntable</code> package.
 *
 * @see com.verisign.epp.codec.idntable.EPPIdnTableCheckResp
 * @see com.verisign.epp.codec.idntable.EPPIdnTableInfoResp
 */
public class EPPIdnTable {

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
	 * Domain names to check for the IDN table information.
	 */
	private List<EPPIdnTableDomainLabel> domains = new ArrayList<EPPIdnTableDomainLabel>();

	/**
	 * Table identifiers to check. This is a <code>List</code> of
	 * <code>String</code> instances.
	 */
	private List<String> tables = new ArrayList<String>();

	/**
	 * Constructs an <code>EPPIdnTable</code> given an initialized EPP session.
	 *
	 * @param aSession
	 *            Server session to use.
	 */
	public EPPIdnTable(EPPSession aSession) {
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
	 * @param aTransId Client transaction identifier
	 */
	public void setTransId(String aTransId) {
		this.transId = aTransId;
	}
	
	/**
	 * Adds a domain for use with a <code>send</code> method. Adding more than
	 * one domain is only supported by <code>sendCheck</code>.
	 *
	 * @param aDomain
	 *            Domain name to add
	 * @param aForm
	 *            The form of the domain name, which is
	 *            <code>EPPIdnTableDomainLabel.A_LABEL</code> for an A-label and
//	 *            <code>EPPIdnTableDomainLabel.U_LABEL</code> for a U-label.
	 */
	public void addDomain(String aDomain, EPPIdnTableDomainLabel.Form aForm) {
		this.domains.add(new EPPIdnTableDomainLabel(aDomain, aForm));
	}

	/**
	 * Adds a table identifier for use with a <code>send</code> method. Adding
	 * more than one table is only supported by <code>sendCheck</code>.
	 * 
	 * @param aTable
	 *            Table identifier to add.
	 */
	public void addTable(String aTable) {
		this.tables.add(aTable);
	}

	/**
	 * Sends an IDN Table Check Command in Domain Check Form to the server.<br>
	 * <br>
	 * There required attributes that must be set prior to executing
	 * <code>sendDomainCheck()</code> include:<br>
	 * <br>
	 * <ul>
	 * <li><code>addDomain</code> - Sets the domains to check. At least
	 * one domain must be set.</li>
	 * </ul>
	 * <br>
	 * <br>
	 * The optional attributes can be set with the following:<br>
	 * <br>
	 * <ul>
	 * <li><code>setTransId</code> - Sets the client transaction identifier.</li>
	 * </ul>
	 * 
	 * @return <code>EPPIdnTableCheckResp</code> with form set to
	 *         <code>EPPIdnTableCheckResp.Form.DOMAIN_FORM</code>.
	 * 
	 * @throws EPPCommandException
	 *             On error
	 */
	public EPPIdnTableCheckResp sendDomainCheck() throws EPPCommandException {
		if (this.domains.isEmpty()) {
			throw new EPPCommandException(
					"At least one domain is required for sendDomainCheck()");
		}

		// Create the command
		EPPIdnTableCheckCmd theCommand = new EPPIdnTableCheckCmd(this.transId);
		theCommand.setDomains(this.domains);

		// Set command extension
		theCommand.setExtensions(this.extensions);

		// Reset IDN Table attributes
		this.resetIdnTable();

		// process the command and response
		return (EPPIdnTableCheckResp) this.session.processDocument(theCommand,
				EPPIdnTableCheckResp.class);
	}

	/**
	 * Sends an IDN Table Check Command in Table Check Form to the server.<br>
	 * <br>
	 * There required attributes that must be set prior to executing
	 * <code>sendTableCheck()</code> include:<br>
	 * <br>
	 * <ul>
	 * <li><code>addTable</code> - Sets the table identifiers to check. At least
	 * one table must be set.</li>
	 * </ul>
	 * <br>
	 * <br>
	 * The optional attributes can be set with the following:<br>
	 * <br>
	 * <ul>
	 * <li><code>setTransId</code> - Sets the client transaction identifier.</li>
	 * </ul>
	 * 
	 * @return <code>EPPIdnTableCheckResp</code> with form set to
	 *         <code>EPPIdnTableCheckResp.Form.TABLE_FORM</code>.
	 * 
	 * @throws EPPCommandException
	 *             On error
	 */
	public EPPIdnTableCheckResp sendTableCheck() throws EPPCommandException {
		if (this.tables.isEmpty()) {
			throw new EPPCommandException(
					"At least one table is required for sendTableCheck()");
		}

		// Create the command
		EPPIdnTableCheckCmd theCommand = new EPPIdnTableCheckCmd(this.transId);
		theCommand.setTables(this.tables);

		// Set command extension
		theCommand.setExtensions(this.extensions);

		// Reset IDN Table attributes
		this.resetIdnTable();

		// process the command and response
		return (EPPIdnTableCheckResp) this.session.processDocument(theCommand,
				EPPIdnTableCheckResp.class);
	}

	/**
	 * Sends an IDN Table Info Command in Domain Info Form to the server.<br>
	 * <br>
	 * There required attributes that must be set prior to executing
	 * <code>sendDomainInfo()</code> include:<br>
	 * <br>
	 * <ul>
	 * <li><code>addDomain</code> - Sets the domain name to get IDN Table info
	 * for. Only one domain is valid.</li>
	 * </ul>
	 * <br>
	 * <br>
	 * The optional attributes can be set with the following:<br>
	 * <br>
	 * <ul>
	 * <li><code>setTransId</code> - Sets the client transaction identifier.</li>
	 * </ul>
	 * 
	 * @return <code>EPPIdnTableInfoResp</code> with form set to
	 *         <code>EPPIdnTableInfoResp.Form.DOMAIN_FORM</code>.
	 * 
	 * @throws EPPCommandException
	 *             On error
	 */
	public EPPIdnTableInfoResp sendDomainInfo() throws EPPCommandException {
		if (this.domains.size() != 1) {
			throw new EPPCommandException(
					"One domain is required for sendDomainInfo()");
		}

		// Create the command
		EPPIdnTableInfoCmd theCommand = new EPPIdnTableInfoCmd(this.transId);
		theCommand.setForm(EPPIdnTableInfoCmd.Form.DOMAIN_FORM);
		theCommand.setDomain(this.domains.get(0).getDomain());

		// Set command extension
		theCommand.setExtensions(this.extensions);

		// Reset IDN Table attributes
		this.resetIdnTable();

		// process the command and response
		return (EPPIdnTableInfoResp) this.session.processDocument(theCommand,
				EPPIdnTableInfoResp.class);
	}

	/**
	 * Sends an IDN Table Info Command in Table Info Form to the server.<br>
	 * <br>
	 * There required attributes that must be set prior to executing
	 * <code>sendTableInfo()</code> include:<br>
	 * <br>
	 * <ul>
	 * <li><code>addTable</code> - Sets the table identifier to get info for.
	 * Only one table is valid.</li>
	 * </ul>
	 * <br>
	 * <br>
	 * The optional attributes can be set with the following:<br>
	 * <br>
	 * <ul>
	 * <li><code>setTransId</code> - Sets the client transaction identifier.</li>
	 * </ul>
	 * 
	 * @return <code>EPPIdnTableInfoResp</code> with form set to
	 *         <code>EPPIdnTableInfoResp.Form.TABLE_FORM</code>.
	 * 
	 * @throws EPPCommandException
	 *             On error
	 */
	public EPPIdnTableInfoResp sendTableInfo() throws EPPCommandException {
		if (this.tables.size() != 1) {
			throw new EPPCommandException(
					"One table is required for sendTableInfo()");
		}

		// Create the command
		EPPIdnTableInfoCmd theCommand = new EPPIdnTableInfoCmd(this.transId);
		theCommand.setForm(EPPIdnTableInfoCmd.Form.TABLE_FORM);
		theCommand.setTable(this.tables.get(0));

		// Set command extension
		theCommand.setExtensions(this.extensions);

		// Reset IDN Table attributes
		this.resetIdnTable();

		// process the command and response
		return (EPPIdnTableInfoResp) this.session.processDocument(theCommand,
				EPPIdnTableInfoResp.class);
	}

	/**
	 * Sends an IDN Table Info Command in List Info Form to the server.<br>
	 * <br>
	 * There are no required attributes that must be set prior to executing
	 * <code>sendListInfo()</code>.<br>
	 * <br>
	 * <br>
	 * The optional attributes can be set with the following:<br>
	 * <br>
	 * <ul>
	 * <li><code>setTransId</code> - Sets the client transaction identifier.</li>
	 * </ul>
	 * 
	 * @return <code>EPPIdnTableInfoResp</code> with form set to
	 *         <code>EPPIdnTableInfoResp.Form.LIST_FORM</code>.
	 * 
	 * @throws EPPCommandException
	 *             On error
	 */
	public EPPIdnTableInfoResp sendListInfo() throws EPPCommandException {

		// Create the command
		EPPIdnTableInfoCmd theCommand = new EPPIdnTableInfoCmd(this.transId);
		theCommand.setForm(EPPIdnTableInfoCmd.Form.LIST_FORM);

		// Set command extension
		theCommand.setExtensions(this.extensions);

		// Reset IDN Table attributes
		this.resetIdnTable();

		// process the command and response
		return (EPPIdnTableInfoResp) this.session.processDocument(theCommand,
				EPPIdnTableInfoResp.class);
	}


	/**
	 * Resets the IDN Table instance to its initial state.
	 */
	protected void resetIdnTable() {
		this.domains = new ArrayList<EPPIdnTableDomainLabel>();
		this.tables = new ArrayList<String>();
		this.transId = null;
		this.extensions = null;
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
