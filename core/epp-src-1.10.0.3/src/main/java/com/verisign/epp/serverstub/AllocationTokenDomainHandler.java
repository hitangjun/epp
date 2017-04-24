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

import org.apache.log4j.Logger;

import com.verisign.epp.codec.allocationtoken.EPPAllocationToken;
import com.verisign.epp.codec.domain.EPPDomainCheckCmd;
import com.verisign.epp.codec.domain.EPPDomainCreateCmd;
import com.verisign.epp.codec.domain.EPPDomainInfoCmd;
import com.verisign.epp.codec.domain.EPPDomainTransferCmd;
import com.verisign.epp.codec.domain.EPPDomainUpdateCmd;
import com.verisign.epp.codec.gen.EPPResponse;
import com.verisign.epp.framework.EPPEvent;
import com.verisign.epp.framework.EPPEventResponse;
import com.verisign.epp.framework.EPPHandleEventException;
import com.verisign.epp.util.EPPCatFactory;

/**
 * The <code>AllocationTokenDomainHandler</code> class extends
 * <code>DomainHandler</code> to include handling the allocation token
 * extension.
 */
public class AllocationTokenDomainHandler extends DomainHandler {

	/** Log4j category for logging */
	private static Logger cat = Logger.getLogger(
			AllocationTokenDomainHandler.class.getName(), EPPCatFactory
					.getInstance().getFactory());

	/**
	 * Constructs an instance of AllocationTokenDomainHandler
	 */
	public AllocationTokenDomainHandler() {
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
	 * Override base handler <code>doDomainCheck</code> method and add handling
	 * of the Allocation Token Extension.
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
	protected EPPEventResponse doDomainCheck(EPPEvent aEvent, Object aData) {

		EPPDomainCheckCmd theCommand = (EPPDomainCheckCmd) aEvent.getMessage();

		if (theCommand.hasExtension(EPPAllocationToken.class)) {
			EPPAllocationToken theExt = (EPPAllocationToken) theCommand
					.getExtension(EPPAllocationToken.class);
			cat.info("doDomainCheck: allocation token = " + theExt.getToken());
			System.out.println("doDomainCheck: allocation token = "
					+ theExt.getToken());
		}
		else {
			cat.info("doDomainCheck: allocation token = null");
			System.out.println("doDomainCheck: allocation token = null");
		}

		EPPEventResponse theEventResponse = super.doDomainCheck(aEvent, aData);

		return theEventResponse;
	}
	
	/**
	 * Override base handler <code>doDomainInfo</code> method and add handling
	 * of the Allocation Token Extension.
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

		EPPDomainInfoCmd theCommand = (EPPDomainInfoCmd) aEvent
				.getMessage();

		EPPEventResponse theEventResponse = super.doDomainInfo(aEvent, aData);
		
		if (theCommand.hasExtension(EPPAllocationToken.class)) {
			
			// Add allocation token extension to response
			// Add extension to response
			EPPResponse theResponse = (EPPResponse) theEventResponse.getResponse();
			theResponse.addExtension(new EPPAllocationToken("abc123"));
			
			cat.info("doDomainInfo: allocation token added to response");
			System.out.println("doDomainInfo: allocation token added to response");
		}
		else {
			cat.info("doDomainInfo: allocation token extension not passed");
			System.out.println("doDomainInfo: allocation token extension not passed");
		}
		

		return theEventResponse;
	}
	

	/**
	 * Override base handler <code>doDomainCreate</code> method and add handling
	 * of the Allocation Token Extension.
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

		if (theCommand.hasExtension(EPPAllocationToken.class)) {
			EPPAllocationToken theExt = (EPPAllocationToken) theCommand
					.getExtension(EPPAllocationToken.class);
			cat.info("doDomainCreate: allocation token = " + theExt.getToken());
			System.out.println("doDomainCreate: allocation token = "
					+ theExt.getToken());
		}
		else {
			cat.info("doDomainCreate: allocation token = null");
			System.out.println("doDomainCreate: allocation token = null");
		}

		EPPEventResponse theEventResponse = super.doDomainCreate(aEvent, aData);

		return theEventResponse;
	}

	/**
	 * Override base handler <code>doDomainUpdate</code> method and add handling
	 * of the Allocation Token Extension.
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

		if (theCommand.hasExtension(EPPAllocationToken.class)) {
			EPPAllocationToken theExt = (EPPAllocationToken) theCommand
					.getExtension(EPPAllocationToken.class);
			cat.info("doDomainUpdate: allocation token = " + theExt.getToken());
			System.out.println("doDomainUpdate: allocation token = "
					+ theExt.getToken());
		}
		else {
			cat.info("doDomainUpdate: allocation token = null");
			System.out.println("doDomainUpdate: allocation token = null");
		}

		EPPEventResponse theEventResponse = super.doDomainUpdate(aEvent, aData);

		return theEventResponse;
	}

	/**
	 * Override base handler <code>doDomainTransfer</code> method and add
	 * handling of the Allocation Token Extension.
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
	protected EPPEventResponse doDomainTransfer(EPPEvent aEvent, Object aData) {

		EPPDomainTransferCmd theCommand = (EPPDomainTransferCmd) aEvent
				.getMessage();

		if (theCommand.hasExtension(EPPAllocationToken.class)) {
			EPPAllocationToken theExt = (EPPAllocationToken) theCommand
					.getExtension(EPPAllocationToken.class);
			cat.info("doDomainTransfer: allocation token = "
					+ theExt.getToken());
			System.out.println("doDomainTransfer: allocation token = "
					+ theExt.getToken());
		}
		else {
			cat.info("doDomainTransfer: allocation token = null");
			System.out.println("doDomainTransfer: allocation token = null");
		}

		EPPEventResponse theEventResponse = super.doDomainTransfer(aEvent,
				aData);

		return theEventResponse;
	}

}
