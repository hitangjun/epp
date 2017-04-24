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

import org.apache.log4j.Logger;

import com.verisign.epp.codec.domain.EPPDomainCreateCmd;
import com.verisign.epp.codec.domain.EPPDomainInfoCmd;
import com.verisign.epp.codec.domain.EPPDomainUpdateCmd;
import com.verisign.epp.codec.gen.EPPResponse;
import com.verisign.epp.codec.resellerext.EPPResellerExtCreate;
import com.verisign.epp.codec.resellerext.EPPResellerExtFactory;
import com.verisign.epp.codec.resellerext.EPPResellerExtInfData;
import com.verisign.epp.codec.resellerext.EPPResellerExtUpdate;
import com.verisign.epp.framework.EPPEvent;
import com.verisign.epp.framework.EPPEventResponse;
import com.verisign.epp.framework.EPPHandleEventException;
import com.verisign.epp.util.EPPCatFactory;

/**
 * The <code>ResellerExtDomainHandler</code> class extends
 * <code>DomainHandler</code> to include handling the Reseller Extension.
 */
public class ResellerExtDomainHandler extends DomainHandler {

	/** Log4j category for logging */
	private static Logger cat = Logger.getLogger(ResellerExtDomainHandler.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * Constructs an instance of ResellerExtDomainHandler
	 */
	public ResellerExtDomainHandler() {
	}

	/**
	 * Ensure that there is an active session.
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
	 * Override base handler <code>doDomainInfo</code> method and add handling
	 * of the Reseller Extension.
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
	protected EPPEventResponse doDomainInfo(EPPEvent aEvent, Object aData) {

		EPPDomainInfoCmd theCommand = (EPPDomainInfoCmd) aEvent.getMessage();

		EPPEventResponse theEventResponse = super.doDomainInfo(aEvent, aData);

		SessionData theSessionData = (SessionData) aData;

		// Should the reseller extension be added?
		if (theSessionData.getLoginCmd().hasExtensionService(
				EPPResellerExtFactory.NS)
				&& theCommand.getName().equalsIgnoreCase("reseller.example")) {
			EPPResponse theResponse = (EPPResponse) theEventResponse
					.getResponse();

			// Add extension to response
			theResponse.addExtension(new EPPResellerExtInfData("myreseller",
					"example"));
		}

		cat.info("doDomainInfo: EPPResellerExt added to response");
		System.out.println("doDomainInfo: EPPResellerExt added to response");

		return theEventResponse;
	}

	/**
	 * Override base handler <code>doDomainCreate</code> method and add handling
	 * of the Reseller Extension.
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

		if (theCommand.hasExtension(EPPResellerExtCreate.class)) {
			EPPResellerExtCreate theExt = (EPPResellerExtCreate) theCommand
					.getExtension(EPPResellerExtCreate.class);
			cat.info("doDomainCreate: reseller identifier = "
					+ theExt.getResellerId());
			System.out.println("doDomainCreate: reseller identifier = "
					+ theExt.getResellerId());
		}
		else {
			cat.info("doDomainCreate: Reseller Extension = null");
			System.out.println("doDomainCreate: Reseller Extension = null");
		}

		EPPEventResponse theEventResponse = super.doDomainCreate(aEvent, aData);

		return theEventResponse;
	}

	/**
	 * Override base handler <code>doDomainUpdate</code> method and add handling
	 * of the Reseller Extension.
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
	protected EPPEventResponse doDomainUpdate(EPPEvent aEvent, Object aData) {

		EPPDomainUpdateCmd theCommand = (EPPDomainUpdateCmd) aEvent
				.getMessage();

		if (theCommand.hasExtension(EPPResellerExtUpdate.class)) {
			EPPResellerExtUpdate theExt = (EPPResellerExtUpdate) theCommand
					.getExtension(EPPResellerExtUpdate.class);
			cat.info("doDomainUpdate: action = " + theExt.getAction()
					+ ", reseller identifier = " + theExt.getResellerId());
			System.out.println("doDomainUpdate: action = " + theExt.getAction()
					+ ", reseller identifier = " + theExt.getResellerId());
		}
		else {
			cat.info("doDomainUpdate: Reseller Extension = null");
			System.out.println("doDomainUpdate: Reseller Extension = null");
		}

		EPPEventResponse theEventResponse = super.doDomainUpdate(aEvent, aData);

		return theEventResponse;
	}

}
