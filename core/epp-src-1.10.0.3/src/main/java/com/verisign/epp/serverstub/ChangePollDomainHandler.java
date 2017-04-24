/***********************************************************
Copyright (C) 2004 VeriSign, Inc.

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

package com.verisign.epp.serverstub;

import java.util.GregorianCalendar;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.verisign.epp.codec.changepoll.EPPChangeCaseId;
import com.verisign.epp.codec.changepoll.EPPChangeData;
import com.verisign.epp.codec.changepoll.EPPChangeOperation;
import com.verisign.epp.codec.domain.EPPDomainContact;
import com.verisign.epp.codec.domain.EPPDomainCreateCmd;
import com.verisign.epp.codec.domain.EPPDomainInfoResp;
import com.verisign.epp.codec.domain.EPPDomainMapFactory;
import com.verisign.epp.codec.domain.EPPDomainStatus;
import com.verisign.epp.codec.gen.EPPFactory;
import com.verisign.epp.codec.gen.EPPResponse;
import com.verisign.epp.codec.gen.EPPResult;
import com.verisign.epp.codec.gen.EPPTransId;
import com.verisign.epp.framework.EPPEvent;
import com.verisign.epp.framework.EPPEventResponse;
import com.verisign.epp.framework.EPPHandleEventException;
import com.verisign.epp.framework.EPPPollQueueException;
import com.verisign.epp.framework.EPPPollQueueMgr;
import com.verisign.epp.util.EPPCatFactory;

/**
 * The <code>ChangePollDomainHandler</code> class extends
 * <code>DomainHandler</code> to include handling the change poll extension.
 */
public class ChangePollDomainHandler extends DomainHandler {

	/** Log4j category for logging */
	private static Logger cat = Logger.getLogger(ChangePollDomainHandler.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * Constructs an instance of ChangePollDomainHandler
	 */
	public ChangePollDomainHandler() {
	}

	/**
	 * Will ensure that the namestore extension is provided.
	 *
	 * @param aEvent
	 *            The <code>EPPEvent</code> that is being handled
	 * @param aData
	 *            Any data that a Server needs to send to this
	 *            <code>EPPDomainHandler</code> This is assumed to be an
	 *            instance of SessionData here.
	 *
	 * @exception EPPHandleEventException
	 *                Thrown if an error condition occurs. It must contain an
	 *                <code>EPPEventResponse</code>
	 */
	protected void preHandleEvent(EPPEvent aEvent, Object aData)
			throws EPPHandleEventException {

		super.preHandleEvent(aEvent, aData);

	}

	/**
	 * Override base handler <code>doDomainCreate</code> method to accept a
	 * domain create of "change-poll.tld" that will place a change poll message
	 * in the poll queue for consumption by the client.
	 * 
	 * @param aEvent
	 *            The <code>EPPEvent</code> that is being handled
	 * @param aData
	 *            Any data that a Server needs to send to this
	 *            <code>EPPDomaindHandler</code>
	 *
	 * @return EPPEventResponse The response that should be sent back to the
	 *         client.
	 */
	protected EPPEventResponse doDomainCreate(EPPEvent aEvent, Object aData) {

		EPPDomainCreateCmd theCommand = (EPPDomainCreateCmd) aEvent
				.getMessage();

		// Domain name "change-poll.tld" to insert a Change Poll Message?
		if (theCommand.getName().equalsIgnoreCase("change-poll.tld")) {
			EPPTransId changeTransId = new EPPTransId(theCommand.getTransId(),
					"54321-XYZ");

			Vector statuses = new Vector();
			statuses.addElement(new EPPDomainStatus(
					EPPDomainStatus.ELM_STATUS_SERVER_UPDATE_PROHIBITED));
			statuses.addElement(new EPPDomainStatus(
					EPPDomainStatus.ELM_STATUS_SERVER_DELETE_PROHIBITED));
			statuses.addElement(new EPPDomainStatus(
					EPPDomainStatus.ELM_STATUS_SERVER_TRANSFER_PROHIBITED));

			EPPDomainInfoResp thePollMsg = new EPPDomainInfoResp(changeTransId,
					"EXAMPLE1-REP", "change-poll.tld", "ClientX", statuses,
					"ClientY", new GregorianCalendar(2012, 04, 03).getTime(),
					null);

			thePollMsg.setRegistrant("jd1234");

			// Is contacts supported?
			if (EPPFactory.getInstance().hasService(
					EPPDomainMapFactory.NS_CONTACT)) {
				Vector contacts = new Vector();
				contacts.addElement(new EPPDomainContact("sh8013",
						EPPDomainContact.TYPE_ADMINISTRATIVE));
				contacts.addElement(new EPPDomainContact("sh8013",
						EPPDomainContact.TYPE_TECHNICAL));
				thePollMsg.setContacts(contacts);
			}

			thePollMsg.setLastUpdatedBy("ClientZ");
			thePollMsg.setLastUpdatedDate(new GregorianCalendar(2013, 10, 22)
					.getTime());
			thePollMsg.setExpirationDate(new GregorianCalendar(2014, 04, 03)
					.getTime());

			// Add Change Poll Extension
			EPPChangeData changeData = new EPPChangeData(
					new EPPChangeOperation(EPPChangeOperation.OPERATION_UPDATE),
					new GregorianCalendar(2013, 10, 22).getTime(), "12345-XYZ",
					"URS Admin", EPPChangeData.STATE_AFTER,
					new EPPChangeCaseId("urs123", EPPChangeCaseId.TYPE_URS),
					"URS Lock", null);

			thePollMsg.addExtension(changeData);

			try {
				EPPPollQueueMgr.getInstance().put(null, EPPDomainMapFactory.NS,
						thePollMsg, null);
			}
			catch (EPPPollQueueException ex) {
				cat.error("doDomainCreate: Error putting message ["
						+ thePollMsg + "]");

				EPPResult theResult = new EPPResult(EPPResult.COMMAND_FAILED);
				EPPResponse theResponse = new EPPResponse(changeTransId,
						theResult);

				return new EPPEventResponse(theResponse);
			}

			thePollMsg.setResult(EPPResult.SUCCESS_PENDING);
		}

		EPPEventResponse theEventResponse = super.doDomainCreate(aEvent, aData);

		return theEventResponse;
	}

}
