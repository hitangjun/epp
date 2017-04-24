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
import com.verisign.epp.codec.gen.EPPResponse;
import com.verisign.epp.codec.idnmap.EPPIdnMap;
import com.verisign.epp.framework.EPPEvent;
import com.verisign.epp.framework.EPPEventResponse;
import com.verisign.epp.framework.EPPHandleEventException;
import com.verisign.epp.util.EPPCatFactory;

/**
 * The <code>IdnMapDomainHandler</code> class extends <code>DomainHandler</code>
 * to include handling the IDN Map Extension.
 */
public class IdnMapDomainHandler extends DomainHandler {

	/** Log4j category for logging */
	private static Logger cat = Logger.getLogger(IdnMapDomainHandler.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * Constructs an instance of IdnMapDomainHandler
	 */
	public IdnMapDomainHandler() {
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
	 * Override base handler <code>doDomainInfo</code> method and add handling
	 * of the IDN Map Extension.
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

		// Add extension to response
		EPPResponse theResponse = (EPPResponse) theEventResponse.getResponse();
		theResponse
				.addExtension(new EPPIdnMap("es", "spa\u00F1ol.example.com"));

		cat.info("doDomainInfo: EPPIdnMap added to response");
		System.out.println("doDomainInfo: EPPIdnMap added to response");

		return theEventResponse;
	}

	/**
	 * Override base handler <code>doDomainCreate</code> method and add handling
	 * of the IDN Map Extension.
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

		if (theCommand.hasExtension(EPPIdnMap.class)) {
			EPPIdnMap theExt = (EPPIdnMap) theCommand
					.getExtension(EPPIdnMap.class);
			cat.info("doDomainCreate: IDN table = " + theExt.getTable());
			System.out.println("doDomainCreate: IDN table = "
					+ theExt.getTable());
			if (theExt.hasUname()) {
				cat.info("domain:info: Response IDN uname = "
						+ theExt.getUname());
				System.out.println("domain:info: Response IDN uname = "
						+ theExt.getUname());
			}
			else {
				cat.info("domain:info: Response IDN uname is undefined");
			}
		}
		else {
			cat.info("doDomainCreate: IDN Map = null");
			System.out.println("doDomainCreate: IDN Map = null");
		}

		EPPEventResponse theEventResponse = super.doDomainCreate(aEvent, aData);

		return theEventResponse;
	}

}
