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

package com.verisign.epp.serverstub;

import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

import com.verisign.epp.codec.gen.EPPResponse;
import com.verisign.epp.codec.gen.EPPTransId;
import com.verisign.epp.codec.reseller.EPPResellerAddress;
import com.verisign.epp.codec.reseller.EPPResellerCheckCmd;
import com.verisign.epp.codec.reseller.EPPResellerCheckResp;
import com.verisign.epp.codec.reseller.EPPResellerCheckResult;
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
import com.verisign.epp.framework.EPPEvent;
import com.verisign.epp.framework.EPPEventResponse;
import com.verisign.epp.framework.EPPHandleEventException;
import com.verisign.epp.framework.EPPResellerHandler;
import com.verisign.epp.util.EPPCatFactory;

/**
 * The <code>ResellerHandler</code> class extends
 * <code>EPPResellerHandler</code> to include handling the Reseller Mapping
 * commands.
 */
public class ResellerHandler extends EPPResellerHandler {

	/** Log4j category for logging */
	private static Logger cat = Logger.getLogger(ResellerHandler.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * Constructs an instance of IdnMapDomainHandler
	 */
	public ResellerHandler() {
	}

	/**
	 * Do any pre-handling of commands.
	 *
	 * @param aEvent
	 *            The <code>EPPEvent</code> that is being handled
	 * @param aData
	 *            Any data that a Server needs to send to this
	 *            <code>ResellerHandler</code> This is assumed to be an instance
	 *            of SessionData here.
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
	 * Override base handler <code>doResellerCheck</code> method and add
	 * handling of the Reseller Check Command.
	 * 
	 * @param aEvent
	 *            The <code>EPPEvent</code> that is being handled
	 * @param aData
	 *            Any data that a Server needs to send to this
	 *            <code>ResellerHandler</code>
	 *
	 * @return EPPEventResponse The response that should be sent back to the
	 *         client.
	 */
	protected EPPEventResponse doResellerCheck(EPPEvent aEvent, Object aData) {
		cat.debug("doResellerCheck: enter");

		EPPResellerCheckCmd theCommand = (EPPResellerCheckCmd) aEvent
				.getMessage();

		EPPTransId theTransId = new EPPTransId(theCommand.getTransId(),
				"54321-XYZ");

		EPPResellerCheckResp theResp = new EPPResellerCheckResp(theTransId);

		for (String resellerId : theCommand.getResellerIds()) {
			if (resellerId.equalsIgnoreCase("re1523")
					|| resellerId.equalsIgnoreCase("notavailable")) {
				theResp.addCheckResult(new EPPResellerCheckResult(resellerId,
						"In use"));
			}
			else {
				theResp.addCheckResult(new EPPResellerCheckResult(resellerId,
						true));
			}
		}

		cat.debug("doResellerCheck: exit");
		return new EPPEventResponse(theResp);
	}

	/**
	 * Override base handler <code>doResellerInfo</code> method and add handling
	 * of the Reseller Info Command.
	 * 
	 * @param aEvent
	 *            The <code>EPPEvent</code> that is being handled
	 * @param aData
	 *            Any data that a Server needs to send to this
	 *            <code>ResellerHandler</code>
	 *
	 * @return EPPEventResponse The response that should be sent back to the
	 *         client.
	 */
	protected EPPEventResponse doResellerInfo(EPPEvent aEvent, Object aData) {
		cat.debug("doResellerInfo: enter");

		EPPResellerInfoCmd theCommand = (EPPResellerInfoCmd) aEvent
				.getMessage();

		EPPTransId theTransId = new EPPTransId(theCommand.getTransId(),
				"54321-XYZ");

		EPPResellerInfoResp theResp = new EPPResellerInfoResp(theTransId,
				theCommand.getResellerId());
		theResp.setRoid("res1523-REP");
		theResp.setState(State.OK);
		theResp.setParentId("1523res");

		EPPResellerAddress theAddress = new EPPResellerAddress();
		theAddress.setStreets("124 Example Dr.", "Suite 200");
		theAddress.setCity("Dulles");
		theAddress.setStateProvince("VA");
		theAddress.setPostalCode("20166-6503");
		theAddress.setCountry("US");

		theResp.addPostalInfo(new EPPResellerPostalDefinition(
				EPPResellerPostalDefinition.Type.INT, "Example Reseller Inc.",
				theAddress));
		theResp.setVoice("+1.7035555555");
		theResp.setVoiceExt("1234");
		theResp.setFax("+1.7035555556");
		theResp.setEmail("contact@reseller.example");
		theResp.setUrl("http://reseller.example");
		theResp.addContact(new EPPResellerContact("sh8013",
				EPPResellerContact.Type.ADMINISTRATIVE));
		theResp.addContact(new EPPResellerContact("sh8013",
				EPPResellerContact.Type.BILLING));
		theResp.setClientId("ClientY");
		theResp.setCreatedBy("ClientX");
		theResp.setCreatedDate(new GregorianCalendar(1999, 04, 03, 22, 0)
				.getTime());
		theResp.setLastUpdatedBy("ClientX");
		theResp.setLastUpdatedDate(new GregorianCalendar(1999, 12, 03, 0, 0)
				.getTime());

		EPPResellerDisclose disclose = new EPPResellerDisclose(false);
		disclose.setVoice(true);
		disclose.setEmail(true);

		theResp.setDisclose(disclose);

		cat.debug("doResellerInfo: exit");
		return new EPPEventResponse(theResp);
	}

	/**
	 * Override base handler <code>doResellerCreate</code> method and add
	 * handling of the Reseller Create Command.
	 * 
	 * @param aEvent
	 *            The <code>EPPEvent</code> that is being handled
	 * @param aData
	 *            Any data that a Server needs to send to this
	 *            <code>ResellerHandler</code>
	 *
	 * @return EPPEventResponse The response that should be sent back to the
	 *         client.
	 */
	protected EPPEventResponse doResellerCreate(EPPEvent aEvent, Object aData) {
		cat.debug("doResellerCreate: enter");

		EPPResellerCreateCmd theCommand = (EPPResellerCreateCmd) aEvent
				.getMessage();

		EPPTransId theTransId = new EPPTransId(theCommand.getTransId(),
				"54321-XYZ");

		EPPResellerCreateResp theResp = new EPPResellerCreateResp(theTransId,
				theCommand.getResellerId(), new Date());

		theResp.setResellerId(theCommand.getResellerId());

		cat.debug("doResellerCreate: exit");
		return new EPPEventResponse(theResp);
	}

	/**
	 * Override base handler <code>doResellerDelete</code> method and add
	 * handling of the Reseller Delete Command.
	 * 
	 * @param aEvent
	 *            The <code>EPPEvent</code> that is being handled
	 * @param aData
	 *            Any data that a Server needs to send to this
	 *            <code>ResellerHandler</code>
	 *
	 * @return EPPEventResponse The response that should be sent back to the
	 *         client.
	 */
	protected EPPEventResponse doResellerDelete(EPPEvent aEvent, Object aData) {
		cat.debug("doResellerDelete: enter");

		EPPResellerDeleteCmd theCommand = (EPPResellerDeleteCmd) aEvent
				.getMessage();

		EPPTransId theTransId = new EPPTransId(theCommand.getTransId(),
				"54321-XYZ");

		EPPResponse theResp = new EPPResponse(theTransId);

		cat.debug("doResellerDelete: exit");
		return new EPPEventResponse(theResp);
	}

	/**
	 * Override base handler <code>doResellerUpdate</code> method and add
	 * handling of the Reseller Update Command.
	 * 
	 * @param aEvent
	 *            The <code>EPPEvent</code> that is being handled
	 * @param aData
	 *            Any data that a Server needs to send to this
	 *            <code>ResellerHandler</code>
	 *
	 * @return EPPEventResponse The response that should be sent back to the
	 *         client.
	 */
	protected EPPEventResponse doResellerUpdate(EPPEvent aEvent, Object aData) {
		cat.debug("doResellerUpdate: enter");

		EPPResellerUpdateCmd theCommand = (EPPResellerUpdateCmd) aEvent
				.getMessage();

		EPPTransId theTransId = new EPPTransId(theCommand.getTransId(),
				"54321-XYZ");

		EPPResponse theResp = new EPPResponse(theTransId);

		cat.debug("doResellerUpdate: exit");
		return new EPPEventResponse(theResp);
	}

}
