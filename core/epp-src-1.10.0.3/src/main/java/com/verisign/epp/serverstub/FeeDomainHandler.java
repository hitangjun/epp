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

// Logging Imports
import java.util.Enumeration;

import org.apache.log4j.Logger;

import com.verisign.epp.codec.domain.EPPDomainCheckCmd;
import com.verisign.epp.codec.domain.EPPDomainCreateCmd;
import com.verisign.epp.codec.domain.EPPDomainDeleteCmd;
import com.verisign.epp.codec.domain.EPPDomainInfoCmd;
import com.verisign.epp.codec.domain.EPPDomainRenewCmd;
import com.verisign.epp.codec.domain.EPPDomainTransferCmd;
import com.verisign.epp.codec.domain.EPPDomainUpdateCmd;
import com.verisign.epp.codec.gen.EPPCommand;
import com.verisign.epp.codec.gen.EPPResponse;
import com.verisign.epp.codec.gen.EPPService;
import com.verisign.epp.framework.EPPEvent;
import com.verisign.epp.framework.EPPEventResponse;
import com.verisign.epp.serverstub.v06.FeeV06DomainHandler;
import com.verisign.epp.serverstub.v07.FeeV07DomainHandler;
import com.verisign.epp.serverstub.v08.FeeV08DomainHandler;
import com.verisign.epp.serverstub.v09.FeeV09DomainHandler;
import com.verisign.epp.serverstub.v11.FeeV11DomainHandler;
import com.verisign.epp.util.EPPCatFactory;

/**
 * The <code>FeeDomainHandler</code> class is a concrete implementation of the
 * abstract <code>com.verisign.epp.framework.EPPDomainHandler</code> class. It
 * handles accepting commands in a server that supports more than one version of
 * the fee extension. The version specific <code>FeeSubDomainHandler</code> is
 * called based on the fee extension version passed or the set of login services
 * provided by the client.
 *
 * @see com.verisign.epp.framework.EPPEvent
 * @see com.verisign.epp.framework.EPPEventResponse
 */
public class FeeDomainHandler extends DomainHandler {

	/** Used for logging */
	private static Logger cat = Logger.getLogger(
			FeeDomainHandler.class.getName(),
			EPPCatFactory.getInstance().getFactory());

	// Sub-handler for handling version 0.6 extension messages.
	FeeV06DomainHandler v06SubHandler = new FeeV06DomainHandler();

	// Sub-handler for handling version 0.7 extension messages.
	FeeV07DomainHandler v07SubHandler = new FeeV07DomainHandler();

	// Sub-handler for handling version 0.8 extension messages.
	FeeV08DomainHandler v08SubHandler = new FeeV08DomainHandler();

	// Sub-handler for handling version 0.9 extension messages.
	FeeV09DomainHandler v09SubHandler = new FeeV09DomainHandler();

	// Sub-handler for handling version 0.9 extension messages.
	FeeV11DomainHandler v11SubHandler = new FeeV11DomainHandler();

	/**
	 * Invoked when a Domain Check command is received.
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

		EPPEventResponse theEventResponse = super.doDomainCheck(aEvent, aData);

		// v06 of fee check?
		if (theCommand.hasExtension(
				com.verisign.epp.codec.fee.v06.EPPFeeCheck.class)) {
			this.v06SubHandler.doDomainCheck(theCommand,
					(EPPResponse) theEventResponse.getResponse(), aData);
		} // v07 of fee check?
		else if (theCommand.hasExtension(
				com.verisign.epp.codec.fee.v07.EPPFeeCheck.class)) {
			this.v07SubHandler.doDomainCheck(theCommand,
					(EPPResponse) theEventResponse.getResponse(), aData);
		} // v08 of fee check?
		else if (theCommand.hasExtension(
				com.verisign.epp.codec.fee.v08.EPPFeeCheck.class)) {
			this.v08SubHandler.doDomainCheck(theCommand,
					(EPPResponse) theEventResponse.getResponse(), aData);
		} // v09 of fee check?
		else if (theCommand.hasExtension(
				com.verisign.epp.codec.fee.v09.EPPFeeCheck.class)) {
			this.v09SubHandler.doDomainCheck(theCommand,
					(EPPResponse) theEventResponse.getResponse(), aData);
		} // v11 of fee check?
		else if (theCommand.hasExtension(
				com.verisign.epp.codec.fee.v11.EPPFeeCheck.class)) {
			this.v11SubHandler.doDomainCheck(theCommand,
					(EPPResponse) theEventResponse.getResponse(), aData);
		}
		else {
			cat.info("doDomainCheck: fee check = null");
			System.out.println("doDomainCheck: fee check = null");
		}

		return theEventResponse;
	}

	/**
	 * Invoked when a Domain Info command is received.
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

		// v06 of fee info?
		if (theCommand.hasExtension(
				com.verisign.epp.codec.fee.v06.EPPFeeInfo.class)) {
			this.v06SubHandler.doDomainInfo(theCommand,
					(EPPResponse) theEventResponse.getResponse(), aData);
		} // v07 of fee info?
		else if (theCommand.hasExtension(
				com.verisign.epp.codec.fee.v07.EPPFeeInfo.class)) {
			this.v07SubHandler.doDomainInfo(theCommand,
					(EPPResponse) theEventResponse.getResponse(), aData);
		}
		else {
			cat.info("doDomainInfo: fee info = null");
			System.out.println("doDomainInfo: fee info = null");
		}

		return theEventResponse;
	}

	/**
	 * Invoked when a Domain Create command is received.
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

		EPPEventResponse theEventResponse = super.doDomainCreate(aEvent, aData);

		// v06 of fee create?
		if (theCommand.hasExtension(
				com.verisign.epp.codec.fee.v06.EPPFeeCreate.class)) {
			this.v06SubHandler.doDomainCreate(theCommand,
					(EPPResponse) theEventResponse.getResponse(), aData);
		} // v07 of fee create?
		else if (theCommand.hasExtension(
				com.verisign.epp.codec.fee.v07.EPPFeeCreate.class)) {
			this.v07SubHandler.doDomainCreate(theCommand,
					(EPPResponse) theEventResponse.getResponse(), aData);
		} // v08 of fee create?
		else if (theCommand.hasExtension(
				com.verisign.epp.codec.fee.v08.EPPFeeCreate.class)) {
			this.v08SubHandler.doDomainCreate(theCommand,
					(EPPResponse) theEventResponse.getResponse(), aData);
		} // v09 of fee create?
		else if (theCommand.hasExtension(
				com.verisign.epp.codec.fee.v09.EPPFeeCreate.class)) {
			this.v09SubHandler.doDomainCreate(theCommand,
					(EPPResponse) theEventResponse.getResponse(), aData);
		} // v11 of fee create?
		else if (theCommand.hasExtension(
				com.verisign.epp.codec.fee.v11.EPPFeeCreate.class)) {
			this.v11SubHandler.doDomainCreate(theCommand,
					(EPPResponse) theEventResponse.getResponse(), aData);
		}
		else {
			cat.info("doDomainCreate: fee create = null");
			System.out.println("doDomainCreate: fee create = null");
		}

		return theEventResponse;
	}

	/**
	 * Invoked when a Domain Delete command is received.
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
	protected EPPEventResponse doDomainDelete(EPPEvent aEvent, Object aData) {
		EPPDomainDeleteCmd theCommand = (EPPDomainDeleteCmd) aEvent
				.getMessage();

		EPPEventResponse theEventResponse = super.doDomainDelete(aEvent, aData);

		// Determine what version of the fee extension is supported
		SessionData theSessionData = (SessionData) aData;
		boolean hasV06ExtService = false;
		boolean hasV07ExtService = false;
		boolean hasV08ExtService = false;
		boolean hasV09ExtService = false;
		boolean hasV11ExtService = false;
		Enumeration extSvcEnum = theSessionData.getLoginCmd()
				.getExtensionServices().elements();
		while (extSvcEnum.hasMoreElements()) {
			EPPService theExtService = (EPPService) extSvcEnum.nextElement();

			if (theExtService.getNamespaceURI().equals(
					com.verisign.epp.codec.fee.v06.EPPFeeExtFactory.NS)) {
				hasV06ExtService = true;
			}
			if (theExtService.getNamespaceURI().equals(
					com.verisign.epp.codec.fee.v07.EPPFeeExtFactory.NS)) {
				hasV07ExtService = true;
			}
			if (theExtService.getNamespaceURI().equals(
					com.verisign.epp.codec.fee.v08.EPPFeeExtFactory.NS)) {
				hasV08ExtService = true;
			}
			if (theExtService.getNamespaceURI().equals(
					com.verisign.epp.codec.fee.v09.EPPFeeExtFactory.NS)) {
				hasV09ExtService = true;
			}
			if (theExtService.getNamespaceURI().equals(
					com.verisign.epp.codec.fee.v11.EPPFeeExtFactory.NS)) {
				hasV11ExtService = true;
			}
		}

		/*
		 * Pick all handlers that apply. This is not compliant with section 2
		 * "Migrating to Newer Versions of This Extension", but is needed to
		 * support isolated client tests that expect a specific extension
		 * version in the response.
		 */

		// v06 of fee delete?
		if (hasV06ExtService) {
			this.v06SubHandler.doDomainDelete(theCommand,
					(EPPResponse) theEventResponse.getResponse(), aData);
		}

		// v07 of fee delete?
		if (hasV07ExtService) {
			this.v07SubHandler.doDomainDelete(theCommand,
					(EPPResponse) theEventResponse.getResponse(), aData);
		}

		// v08 of fee delete?
		if (hasV08ExtService) {
			this.v08SubHandler.doDomainDelete(theCommand,
					(EPPResponse) theEventResponse.getResponse(), aData);
		}

		// v09 of fee delete?
		if (hasV09ExtService) {
			this.v09SubHandler.doDomainDelete(theCommand,
					(EPPResponse) theEventResponse.getResponse(), aData);
		}

		// v11 of fee delete?
		if (hasV11ExtService) {
			this.v11SubHandler.doDomainDelete(theCommand,
					(EPPResponse) theEventResponse.getResponse(), aData);
		}

		if (!hasV06ExtService && !hasV07ExtService & !hasV08ExtService
				&& !hasV09ExtService && !hasV11ExtService) {
			cat.info("doDomainDelete: fee extension not supported by client");
			System.out.println(
					"doDomainDelete: fee extension not supported by client");
		}

		return theEventResponse;
	}

	/**
	 * Invoked when a Domain Renew command is received.
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
	protected EPPEventResponse doDomainRenew(EPPEvent aEvent, Object aData) {
		EPPDomainRenewCmd theCommand = (EPPDomainRenewCmd) aEvent.getMessage();

		EPPEventResponse theEventResponse = super.doDomainRenew(aEvent, aData);

		// v06 of fee renew?
		if (theCommand.hasExtension(
				com.verisign.epp.codec.fee.v06.EPPFeeRenew.class)) {
			this.v06SubHandler.doDomainRenew(theCommand,
					(EPPResponse) theEventResponse.getResponse(), aData);
		} // v07 of fee renew?
		else if (theCommand.hasExtension(
				com.verisign.epp.codec.fee.v07.EPPFeeRenew.class)) {
			this.v07SubHandler.doDomainRenew(theCommand,
					(EPPResponse) theEventResponse.getResponse(), aData);
		} // v08 of fee renew?
		else if (theCommand.hasExtension(
				com.verisign.epp.codec.fee.v08.EPPFeeRenew.class)) {
			this.v08SubHandler.doDomainRenew(theCommand,
					(EPPResponse) theEventResponse.getResponse(), aData);
		} // v09 of fee renew?
		else if (theCommand.hasExtension(
				com.verisign.epp.codec.fee.v09.EPPFeeRenew.class)) {
			this.v09SubHandler.doDomainRenew(theCommand,
					(EPPResponse) theEventResponse.getResponse(), aData);
		} // v11 of fee renew?
		else if (theCommand.hasExtension(
				com.verisign.epp.codec.fee.v11.EPPFeeRenew.class)) {
			this.v11SubHandler.doDomainRenew(theCommand,
					(EPPResponse) theEventResponse.getResponse(), aData);
		}
		else {
			cat.info("doDomainRenew: fee renew = null");
			System.out.println("doDomainRenew: fee renew = null");
		}

		return theEventResponse;
	}

	/**
	 * Invoked when a Domain Transfer command is received.
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

		EPPEventResponse theEventResponse = super.doDomainTransfer(aEvent,
				aData);

		// Determine what version of the fee extension is supported
		SessionData theSessionData = (SessionData) aData;
		boolean hasV06ExtService = false;
		boolean hasV07ExtService = false;
		boolean hasV08ExtService = false;
		boolean hasV09ExtService = false;
		boolean hasV11ExtService = false;
		Enumeration extSvcEnum = theSessionData.getLoginCmd()
				.getExtensionServices().elements();
		while (extSvcEnum.hasMoreElements()) {
			EPPService theExtService = (EPPService) extSvcEnum.nextElement();

			if (theExtService.getNamespaceURI().equals(
					com.verisign.epp.codec.fee.v06.EPPFeeExtFactory.NS)) {
				hasV06ExtService = true;
			}

			if (theExtService.getNamespaceURI().equals(
					com.verisign.epp.codec.fee.v07.EPPFeeExtFactory.NS)) {
				hasV07ExtService = true;
			}

			if (theExtService.getNamespaceURI().equals(
					com.verisign.epp.codec.fee.v08.EPPFeeExtFactory.NS)) {
				hasV08ExtService = true;
			}

			if (theExtService.getNamespaceURI().equals(
					com.verisign.epp.codec.fee.v09.EPPFeeExtFactory.NS)) {
				hasV09ExtService = true;
			}
			
			if (theExtService.getNamespaceURI().equals(
					com.verisign.epp.codec.fee.v11.EPPFeeExtFactory.NS)) {
				hasV11ExtService = true;
			}
		}

		// Transfer Query?
		if (theCommand.getOp().equals(EPPCommand.OP_QUERY)) {

			/*
			 * Pick all handlers that apply. This is not compliant with section
			 * 2 "Migrating to Newer Versions of This Extension", but is needed
			 * to support isolated client tests that expect a specific extension
			 * version in the response.
			 */

			// v06 of transfer query?
			if (hasV06ExtService) {
				this.v06SubHandler.doDomainTransfer(theCommand,
						(EPPResponse) theEventResponse.getResponse(), aData);
			}

			// v07 of transfer query?
			if (hasV07ExtService) {
				this.v07SubHandler.doDomainTransfer(theCommand,
						(EPPResponse) theEventResponse.getResponse(), aData);
			}

			// v08 of transfer query?
			if (hasV08ExtService) {
				this.v08SubHandler.doDomainTransfer(theCommand,
						(EPPResponse) theEventResponse.getResponse(), aData);
			}

			// v09 of transfer query?
			if (hasV09ExtService) {
				this.v09SubHandler.doDomainTransfer(theCommand,
						(EPPResponse) theEventResponse.getResponse(), aData);
			}

			// v11 of transfer query?
			if (hasV11ExtService) {
				this.v11SubHandler.doDomainTransfer(theCommand,
						(EPPResponse) theEventResponse.getResponse(), aData);
			}

			if (!hasV06ExtService && !hasV07ExtService && !hasV08ExtService
					&& !hasV09ExtService && !hasV11ExtService) {
				cat.info(
						"doDomainTransfer: fee extension not supported by client");
				System.out.println(
						"doDomainTransfer: fee extension not supported by client");
			}

		}
		else if (theCommand.getOp().equals(EPPCommand.OP_REQUEST)) {

			// v06 of fee transfer request?
			if (theCommand.hasExtension(
					com.verisign.epp.codec.fee.v06.EPPFeeTransfer.class)) {
				this.v06SubHandler.doDomainTransfer(theCommand,
						(EPPResponse) theEventResponse.getResponse(), aData);
			} // v07 of fee transfer request?
			else if (theCommand.hasExtension(
					com.verisign.epp.codec.fee.v07.EPPFeeTransfer.class)) {
				this.v07SubHandler.doDomainTransfer(theCommand,
						(EPPResponse) theEventResponse.getResponse(), aData);
			} // v08 of fee transfer request?
			else if (theCommand.hasExtension(
					com.verisign.epp.codec.fee.v08.EPPFeeTransfer.class)) {
				this.v08SubHandler.doDomainTransfer(theCommand,
						(EPPResponse) theEventResponse.getResponse(), aData);
			} // v09 of fee transfer request?
			else if (theCommand.hasExtension(
					com.verisign.epp.codec.fee.v09.EPPFeeTransfer.class)) {
				this.v09SubHandler.doDomainTransfer(theCommand,
						(EPPResponse) theEventResponse.getResponse(), aData);
			} // v11 of fee transfer request?
			else if (theCommand.hasExtension(
					com.verisign.epp.codec.fee.v11.EPPFeeTransfer.class)) {
				this.v11SubHandler.doDomainTransfer(theCommand,
						(EPPResponse) theEventResponse.getResponse(), aData);
			}
			else {
				cat.info("doDomainTransfer: fee transfer request = null");
				System.out.println(
						"doDomainTransfer: fee transfer request = null");
			}
		}

		return theEventResponse;
	}

	/**
	 * Invoked when a Domain Update command is received.
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

		EPPEventResponse theEventResponse = super.doDomainUpdate(aEvent, aData);

		// v06 of fee update?
		if (theCommand.hasExtension(
				com.verisign.epp.codec.fee.v06.EPPFeeUpdate.class)) {
			this.v06SubHandler.doDomainUpdate(theCommand,
					(EPPResponse) theEventResponse.getResponse(), aData);
		} // v07 of fee update?
		else if (theCommand.hasExtension(
				com.verisign.epp.codec.fee.v07.EPPFeeUpdate.class)) {
			this.v07SubHandler.doDomainUpdate(theCommand,
					(EPPResponse) theEventResponse.getResponse(), aData);
		} // v08 of fee update?
		else if (theCommand.hasExtension(
				com.verisign.epp.codec.fee.v08.EPPFeeUpdate.class)) {
			this.v08SubHandler.doDomainUpdate(theCommand,
					(EPPResponse) theEventResponse.getResponse(), aData);
		} // v09 of fee update?
		else if (theCommand.hasExtension(
				com.verisign.epp.codec.fee.v09.EPPFeeUpdate.class)) {
			this.v09SubHandler.doDomainUpdate(theCommand,
					(EPPResponse) theEventResponse.getResponse(), aData);
		} // v11 of fee update?
		else if (theCommand.hasExtension(
				com.verisign.epp.codec.fee.v11.EPPFeeUpdate.class)) {
			this.v11SubHandler.doDomainUpdate(theCommand,
					(EPPResponse) theEventResponse.getResponse(), aData);
		}
		else {
			cat.info("doDomainUpdate: fee update = null");
			System.out.println("doDomainUpdate: fee update = null");
		}

		return theEventResponse;
	}

}
