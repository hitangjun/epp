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
package com.verisign.epp.serverstub;

import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.verisign.epp.codec.contact.EPPContactAddress;
import com.verisign.epp.codec.contact.EPPContactCheckCmd;
import com.verisign.epp.codec.contact.EPPContactCheckResp;
import com.verisign.epp.codec.contact.EPPContactCheckResult;
import com.verisign.epp.codec.contact.EPPContactCreateCmd;
import com.verisign.epp.codec.contact.EPPContactCreateResp;
import com.verisign.epp.codec.contact.EPPContactDeleteCmd;
import com.verisign.epp.codec.contact.EPPContactDisclose;
import com.verisign.epp.codec.contact.EPPContactDiscloseAddress;
import com.verisign.epp.codec.contact.EPPContactDiscloseName;
import com.verisign.epp.codec.contact.EPPContactDiscloseOrg;
import com.verisign.epp.codec.contact.EPPContactInfoCmd;
import com.verisign.epp.codec.contact.EPPContactInfoResp;
import com.verisign.epp.codec.contact.EPPContactMapFactory;
import com.verisign.epp.codec.contact.EPPContactPostalDefinition;
import com.verisign.epp.codec.contact.EPPContactStatus;
import com.verisign.epp.codec.contact.EPPContactTransferCmd;
import com.verisign.epp.codec.contact.EPPContactTransferResp;
import com.verisign.epp.codec.contact.EPPContactUpdateCmd;
import com.verisign.epp.codec.gen.EPPAuthInfo;
import com.verisign.epp.codec.gen.EPPCommand;
import com.verisign.epp.codec.gen.EPPResponse;
import com.verisign.epp.codec.gen.EPPResult;
import com.verisign.epp.codec.gen.EPPTransId;
import com.verisign.epp.framework.EPPContactHandler;
import com.verisign.epp.framework.EPPEvent;
import com.verisign.epp.framework.EPPEventResponse;
import com.verisign.epp.framework.EPPHandleEventException;
import com.verisign.epp.framework.EPPPollQueueException;
import com.verisign.epp.framework.EPPPollQueueMgr;
import com.verisign.epp.util.EPPCatFactory;

/**
 * The <code>ContactHandler</code> class is a concrete implementation of the
 * abstract <code>com.verisign.epp.framework.EPPContactHandler</code> class. It
 * defines the Server's response to all received EPP Host Commands. <br>
 * <br>
 *
 * @author $Author: jim $
 * @version $Revision: 1.2 $
 *
 * @see com.verisign.epp.framework.EPPEvent
 * @see com.verisign.epp.framework.EPPEventResponse
 */
public class ContactHandler extends EPPContactHandler {
	/** DOCUMENT ME! */
	private static final String	svrTransId	= "54322-XYZ";

	/** DOCUMENT ME! */
	private static final String	roid			= "NS1EXAMPLE1-VRSN";

	private static Logger			cat			= Logger.getLogger(ContactHandler.class.getName(),
	      EPPCatFactory.getInstance().getFactory());

	/**
	 * Constructs an instance of <code>ContactHandler</code>
	 */
	public ContactHandler() {
	}

	/**
	 * Handles any common behavior that all Contact commands need to execute
	 * before they execute their command specific behavior.
	 *
	 * @param aEvent
	 *           The <code>EPPEvent</code> that is being handled
	 * @param aData
	 *           Any data that a Server needs to send to this
	 *           <code>EPPDomainHandler</code> This is assumed to be an instance
	 *           of SessionData here.
	 *
	 * @exception EPPHandleEventException
	 *               Thrown if an error condition occurs. It must contain an
	 *               <code>EPPEventResponse</code>
	 */
	protected void preHandleEvent(EPPEvent aEvent, Object aData) throws EPPHandleEventException {
	}

	/**
	 * Handles any common behavior that all Contact commands need to execute
	 * after they execute their command specific behavior.
	 *
	 * @param aEvent
	 *           The <code>EPPEvent</code> that is being handled
	 * @param aData
	 *           Any data that a Server needs to send to this
	 *           <code>EPPDomainHandler</code>
	 *
	 * @exception EPPHandleEventException
	 *               Thrown if an error condition occurs. It must contain an
	 *               <code>EPPEventResponse</code>
	 */
	protected void postHandleEvent(EPPEvent aEvent, Object aData) throws EPPHandleEventException {
	}

	/**
	 * Invoked when a Contact Delete command is received.
	 *
	 * @param aEvent
	 *           The <code>EPPEvent</code> that is being handled
	 * @param aData
	 *           Any data that a Server needs to send to this
	 *           <code>EPPDomaindHandler</code>
	 *
	 * @return EPPEventResponse The response that should be sent back to the
	 *         client.
	 */
	protected EPPEventResponse doContactDelete(EPPEvent aEvent, Object aData) {
		EPPContactDeleteCmd theMessage = (EPPContactDeleteCmd) aEvent.getMessage();

		/**
		 * Create the transId for the response with the client trans id and the
		 * server trans id.
		 */
		EPPTransId transId = new EPPTransId(theMessage.getTransId(), svrTransId);

		// Create the Response (Standard EPPResponse)
		EPPResponse theResponse = new EPPResponse(transId);
		theResponse.setResult(EPPResult.SUCCESS);

		return new EPPEventResponse(theResponse);
	}

	/**
	 * Invoked when a Contact Create command is received.
	 *
	 * @param aEvent
	 *           The <code>EPPEvent</code> that is being handled
	 * @param aData
	 *           Any data that a Server needs to send to this
	 *           <code>EPPDomaindHandler</code>
	 *
	 * @return EPPEventResponse The response that should be sent back to the
	 *         client.
	 */
	protected EPPEventResponse doContactCreate(EPPEvent aEvent, Object aData) {
		EPPContactCreateCmd theMessage = (EPPContactCreateCmd) aEvent.getMessage();

		/**
		 * Create the transId for the response with the client trans id and the
		 * server trans id.
		 */
		EPPTransId transId = new EPPTransId(theMessage.getTransId(), svrTransId);

		// Create the Response (Standard EPPResponse)
		EPPContactCreateResp theResponse = new EPPContactCreateResp(transId, theMessage.getId(), new Date());
		theResponse.setResult(EPPResult.SUCCESS);

		return new EPPEventResponse(theResponse);
	}

	/**
	 * Invoked when a Contact Update command is received.
	 *
	 * @param aEvent
	 *           The <code>EPPEvent</code> that is being handled
	 * @param aData
	 *           Any data that a Server needs to send to this
	 *           <code>EPPDomaindHandler</code>
	 *
	 * @return EPPEventResponse The response that should be sent back to the
	 *         client.
	 */
	protected EPPEventResponse doContactUpdate(EPPEvent aEvent, Object aData) {
		EPPContactUpdateCmd theMessage = (EPPContactUpdateCmd) aEvent.getMessage();

		/**
		 * Create the transId for the response with the client trans id and the
		 * server trans id.
		 */
		EPPTransId transId = new EPPTransId(theMessage.getTransId(), svrTransId);

		// Create the Response (Standard EPPResponse)
		EPPResponse theResponse = new EPPResponse(transId);
		theResponse.setResult(EPPResult.SUCCESS);

		return new EPPEventResponse(theResponse);
	}

	/**
	 * Invoked when a Contact Info command is received.
	 *
	 * @param aEvent
	 *           The <code>EPPEvent</code> that is being handled
	 * @param aData
	 *           Any data that a Server needs to send to this
	 *           <code>EPPDomaindHandler</code>
	 *
	 * @return EPPEventResponse The response that should be sent back to the
	 *         client.
	 */
	protected EPPEventResponse doContactInfo(EPPEvent aEvent, Object aData) {
		EPPContactInfoCmd theCommand = (EPPContactInfoCmd) aEvent.getMessage();
		EPPContactInfoResp theResponse = null;

		if (theCommand.getId().contains("Minimal")) {
			theResponse = getMinimalInfoResponseWithAllElements(theCommand);
		}
		else if (theCommand.getId().contains("Partial")) {
			theResponse = getPartialInfoResponseWithAllElements(theCommand);
		}
		else {
			theResponse = getDefaultInfoResponseWithAllElements(theCommand);
		}

		return new EPPEventResponse(theResponse);
	}

	/**
	 * Creates default RFC compliant contact info response that sets all the
	 * mandatory attributes in the response per RFC.
	 * 
	 * @param theCommand
	 *           The contact info command to return the response for.
	 * 
	 * @return contact info response associated with the command
	 */
	private EPPContactInfoResp getDefaultInfoResponseWithAllElements(EPPContactInfoCmd theCommand) {
		// Encode EPPContactInfo Response
		EPPContactInfoResp theResponse = null;

		// Streets
		Vector streets = new Vector();
		streets.addElement("123 Example Dr.");
		streets.addElement("Suite 100");

		// Address
		EPPContactAddress address = new EPPContactAddress(streets, "Dulles", "VA", "20166-6503", "US");

		// Name Postal Definition
		EPPContactPostalDefinition name = new EPPContactPostalDefinition("John Doe", "Example Inc.",
		      EPPContactPostalDefinition.ATTR_TYPE_LOC, address);

		// i15d Streets
		Vector i15dStreets = new Vector();
		i15dStreets.addElement("i15d 123 Example Dr.");
		i15dStreets.addElement("i15d Suite 100");

		// Address
		EPPContactAddress i15dAddress = new EPPContactAddress(i15dStreets, "Dulles", "VA", "20166-6503", "US");

		// Name Postal Definition
		EPPContactPostalDefinition i15dName = null;
		i15dName = new EPPContactPostalDefinition("i15d John Doe", "i15d Example Inc.",
		      EPPContactPostalDefinition.ATTR_TYPE_INT, i15dAddress);

		// infoStatuses
		Vector<EPPContactStatus> infoStatuses = new Vector<EPPContactStatus>();
		infoStatuses.addElement(new EPPContactStatus(EPPContactStatus.ELM_STATUS_LINKED));
		infoStatuses.addElement(new EPPContactStatus(EPPContactStatus.ELM_STATUS_CLIENT_UPDATE_PROHIBITED));

		// Test with just required EPPContactInfoResp attributes.
		EPPTransId respTransId = new EPPTransId(theCommand.getTransId(), svrTransId);

		theResponse = new EPPContactInfoResp(respTransId, "SH8013-VRSN", "sh8013", infoStatuses, name, "jdoe@example.com",
		      "ClientY", "ClientX", new Date(),
		      new EPPAuthInfo(EPPContactMapFactory.NS, EPPContactMapFactory.ELM_CONTACT_AUTHINFO, "2fooBAR"));

		theResponse.setVoice("+1.7035555555");
		theResponse.setVoiceExt("123");
		theResponse.setFax("+1.7035555556");
		theResponse.setFaxExt("456");

		theResponse.addPostalInfo(i15dName);

		EPPContactDisclose disclose = new EPPContactDisclose(true, true, false, true, true, true, true, true, true, true);

		theResponse.setDisclose(disclose);

		theResponse.setResult(EPPResult.SUCCESS);
		return theResponse;
	}

	/**
	 * Creates relaxed validation compliant contact info response that sets only
	 * some of the mandatory attributes per the RFC. The fields required by RFC
	 * that are NOT provided include:<br>
	 * <ol>
	 * <li>name</li>
	 * <li>street</li>
	 * <li>city</li>
	 * </ol>
	 * 
	 * @param theCommand
	 *           The contact info command to return the response for.
	 * 
	 * @return contact info response associated with the command
	 */
	private EPPContactInfoResp getPartialInfoResponseWithAllElements(EPPContactInfoCmd theCommand) {
		// Encode EPPContactInfo Response
		EPPContactInfoResp theResponse = null;

		// Name Postal Definition
		EPPContactPostalDefinition postalInfo = new EPPContactPostalDefinition(null, null,
		      EPPContactPostalDefinition.ATTR_TYPE_INT, null);

		// infoStatuses
		Vector<EPPContactStatus> infoStatuses = new Vector<EPPContactStatus>();
		infoStatuses.addElement(new EPPContactStatus(EPPContactStatus.ELM_STATUS_OK));

		EPPTransId respTransId = new EPPTransId(theCommand.getTransId(), svrTransId);

		theResponse = new EPPContactInfoResp();
		theResponse.setTransId(respTransId);
		theResponse.setRoid("SH8013-VRSN");
		theResponse.setId(theCommand.getId());

		EPPContactAddress address = new EPPContactAddress();
		address.setCountry("US");
		postalInfo.setAddress(address);
		theResponse.addPostalInfo(postalInfo);

		theResponse.setEmail("jdoe@example.com");

		theResponse.setStatuses(infoStatuses);
		theResponse.setClientId("ClientY");
		theResponse.setCreatedBy("ClientX");
		theResponse.setCreatedDate(new Date());
		theResponse.setAuthInfo(
		      new EPPAuthInfo(EPPContactMapFactory.NS, EPPContactMapFactory.ELM_CONTACT_AUTHINFO, "2fooBAR"));

		theResponse.setResult(EPPResult.SUCCESS);
		return theResponse;
	}

	/**
	 * Creates relaxed validation compliant contact info response that sets the
	 * minimum set of attributes. The fields required by RFC that are NOT
	 * provided include:<br>
	 * <ol>
	 * <li>name</li>
	 * <li>street</li>
	 * <li>city</li>
	 * <li>country</li>
	 * <li>e-mail</li>
	 * </ol>
	 * 
	 * @param theCommand
	 *           The contact info command to return the response for.
	 * 
	 * @return contact info response associated with the command
	 */
	private EPPContactInfoResp getMinimalInfoResponseWithAllElements(EPPContactInfoCmd theCommand) {
		// Encode EPPContactInfo Response
		EPPContactInfoResp theResponse = null;

		// Name Postal Definition
		EPPContactPostalDefinition postalInfo = new EPPContactPostalDefinition();
		postalInfo.setType(EPPContactPostalDefinition.ATTR_TYPE_INT);

		// infoStatuses
		Vector<EPPContactStatus> infoStatuses = new Vector<EPPContactStatus>();
		infoStatuses.addElement(new EPPContactStatus(EPPContactStatus.ELM_STATUS_OK));

		EPPTransId respTransId = new EPPTransId(theCommand.getTransId(), svrTransId);

		theResponse = new EPPContactInfoResp();
		theResponse.setTransId(respTransId);
		theResponse.setRoid("SH8013-VRSN");
		theResponse.setId(theCommand.getId());
		theResponse.addPostalInfo(postalInfo);
		theResponse.setStatuses(infoStatuses);
		theResponse.setClientId("ClientY");
		theResponse.setCreatedBy("ClientX");
		theResponse.setCreatedDate(new Date());
		theResponse.setAuthInfo(
		      new EPPAuthInfo(EPPContactMapFactory.NS, EPPContactMapFactory.ELM_CONTACT_AUTHINFO, "2fooBAR"));

		theResponse.setResult(EPPResult.SUCCESS);
		return theResponse;
	}

	/**
	 * Invoked when a Contact Transfer command is received.
	 *
	 * @param aEvent
	 *           The <code>EPPEvent</code> that is being handled
	 * @param aData
	 *           Any data that a Server needs to send to this
	 *           <code>EPPDomaindHandler</code>
	 * @return EPPEventResponse The response that should be sent back to the
	 *         client.
	 */
	protected EPPEventResponse doContactTransfer(EPPEvent aEvent, Object aData) {
		EPPContactTransferCmd theCommand = (EPPContactTransferCmd) aEvent.getMessage();

		/**
		 * Create the transId for the response with the client trans id and the
		 * server trans id.
		 */
		EPPTransId transId = new EPPTransId(theCommand.getTransId(), svrTransId);

		if (theCommand.getOp().equals(EPPCommand.OP_REQUEST)) {
			// Insert transfer response in poll queue
			EPPContactTransferResp thePollMsg = new EPPContactTransferResp();

			thePollMsg.setId("example");
			thePollMsg.setTransferStatus(EPPResponse.TRANSFER_PENDING);
			thePollMsg.setRequestClient("ClientX");
			thePollMsg.setRequestDate(new Date());
			thePollMsg.setActionClient("ClientY");
			thePollMsg.setActionDate(new Date());
			thePollMsg.setResult(EPPResult.SUCCESS);

			try {
				EPPPollQueueMgr.getInstance().put(null, EPPContactMapFactory.NS, thePollMsg, null);
			}
			catch (EPPPollQueueException ex) {
				cat.error("doContactTransfer: Error putting message [" + thePollMsg + "]");

				EPPResult theResult = new EPPResult(EPPResult.COMMAND_FAILED);
				EPPResponse theResponse = new EPPResponse(transId, theResult);

				return new EPPEventResponse(theResponse);
			}
		}

		// EPPContactTransferResp
		EPPContactTransferResp theResponse;

		theResponse = new EPPContactTransferResp(transId, "SH0000", EPPResponse.TRANSFER_PENDING);
		theResponse.setRequestClient("ClientX");
		theResponse.setActionClient("ClientY");
		theResponse.setRequestDate(new Date());
		theResponse.setActionDate(new Date());

		theResponse.setResult(EPPResult.SUCCESS);

		return new EPPEventResponse(theResponse);
	}

	/**
	 * Invoked when a Contact Check command is received.
	 *
	 * @param aEvent
	 *           The <code>EPPEvent</code> that is being handled
	 * @param aData
	 *           Any data that a Server needs to send to this
	 *           <code>EPPDomaindHandler</code>
	 *
	 * @return EPPEventResponse The response that should be sent back to the
	 *         client.
	 */
	protected EPPEventResponse doContactCheck(EPPEvent aEvent, Object aData) {
		EPPContactCheckCmd theMessage = (EPPContactCheckCmd) aEvent.getMessage();

		// This is just a vector of strings representing Contact ID's
		Vector vIds = theMessage.getIds();
		Enumeration eIds = vIds.elements();

		// create a Vector of Ping Results.
		Vector vResults = new Vector();

		boolean available = true;

		while (eIds.hasMoreElements()) {
			String contactId = (String) eIds.nextElement();
			available = !available;
			vResults.addElement(new EPPContactCheckResult(contactId, available));
		}

		/**
		 * Create the transId for the response with the client trans id and the
		 * server trans id.
		 */
		EPPTransId transId = new EPPTransId(theMessage.getTransId(), svrTransId);

		EPPContactCheckResp theResponse = new EPPContactCheckResp(transId, vResults);
		theResponse.setResult(EPPResult.SUCCESS);

		return new EPPEventResponse(theResponse);
	}
}
