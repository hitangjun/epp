/**************************************************************************
 *                                                                        *
 * The information in this document is proprietary to VeriSign, Inc.      *
 * It may not be used, reproduced or disclosed without the written        *
 * approval of VeriSign.                                                  *
 *                                                                        *
 * VERISIGN PROPRIETARY & CONFIDENTIAL INFORMATION                        *
 *                                                                        *
 *                                                                        *
 * Copyright (c) 2016 VeriSign, Inc.  All rights reserved.                *
 *                                                                        *
 *************************************************************************/

package com.verisign.epp.serverstub;

import org.apache.log4j.Logger;

import com.verisign.epp.codec.contact.EPPContactCheckCmd;
import com.verisign.epp.codec.contact.EPPContactCreateCmd;
import com.verisign.epp.codec.contact.EPPContactDeleteCmd;
import com.verisign.epp.codec.contact.EPPContactInfoCmd;
import com.verisign.epp.codec.contact.EPPContactTransferCmd;
import com.verisign.epp.codec.contact.EPPContactUpdateCmd;
import com.verisign.epp.codec.jobscontact.EPPJobsContactCreateCmd;
import com.verisign.epp.codec.jobscontact.EPPJobsContactUpdateCmd;
import com.verisign.epp.framework.EPPContactHandler;
import com.verisign.epp.framework.EPPEvent;
import com.verisign.epp.framework.EPPEventResponse;
import com.verisign.epp.util.EPPCatFactory;

/**
 * The <code>NSContactHandler</code> class is a concrete implementation of the abstract
 * {@link com.verisign.epp.framework.EPPContactHandler} class. It defines the Server's response to all received EPP
 * contact Commands.
 * 
 * @author ssarpotdar
 * @version 1.0 Dec 13, 2016
 */
public class NSContactHandler extends EPPContactHandler {

	/** Constant server transaction id */
	private static final String svrTransId = "54322-XYZ";

	/** Constant ROID */
	private static final String roid = "NS1EXAMPLE1-VRSN";

	/** Logger category */
	private static Logger cat = Logger.getLogger( NSContactHandler.class.getName(), EPPCatFactory
			.getInstance()
			.getFactory() );

	/** Jobs contact handler when jobs extension is passed. */
	JobsContactHandler jobsContactHandler = new JobsContactHandler();
	
	
	/** Epp contact handler as default handler for all contact commands. */
	ContactHandler contactHandler = new ContactHandler();
	
		
		

	/**
	 * Invoked when a Contact Create Command is received.  The handler routes the command 
	 * to the appropriate handler based on the extensions and data included in the command.
	 * 
	 * @param aEvent The <code>EPPEvent</code> that is being handled
	 * 
	 * @param aData Any data that a Server needs to send to this <code>EPPContactHandler</code>
	 * 
	 * @return The response that should be sent back to the client.
	 * 
	 * @see com.verisign.epp.framework.EPPContactHandler#doContactCreate(com.verisign.epp.framework.EPPEvent,
	 *      java.lang.Object)
	 */
	@Override
	protected EPPEventResponse doContactCreate ( EPPEvent aEvent, Object aData ) {
		EPPContactCreateCmd theCommand = (EPPContactCreateCmd) aEvent.getMessage();
		cat.debug( "doContactCreate: command = [" + theCommand + "]" );

		// Jobs Contact Create Command?
		if ( theCommand.hasExtension(EPPJobsContactCreateCmd.class)) {
			EPPEventResponse jobsContactCreateResponse = this.jobsContactHandler.doContactCreate( aEvent, aData );

			return jobsContactCreateResponse;
		}

		EPPEventResponse contactCreateResponse = this.contactHandler.doContactCreate( aEvent, aData );
		return contactCreateResponse;
	}


	/**
	 * Invoked when a Contact Delete Command is received.  The handler routes the command 
	 * to the appropriate handler based on the extensions and data included in the command.
	 * 
	 * @param aEvent The <code>EPPEvent</code> that is being handled
	 * 
	 * @param aData Any data that a Server needs to send to this <code>EPPContactHandler</code>
	 * 
	 * @return The response that should be sent back to the client.
	 * 
	 * @see com.verisign.epp.framework.EPPContactHandler#doContactDelete(com.verisign.epp.framework.EPPEvent,
	 *      java.lang.Object)
	 */
	@Override
	protected EPPEventResponse doContactDelete ( EPPEvent aEvent, Object aData ) {
		EPPContactDeleteCmd theCommand = (EPPContactDeleteCmd) aEvent.getMessage();
		cat.debug( "doContactDelete: command = [" + theCommand + "]" );

		EPPEventResponse contactDeleteResponse = this.contactHandler.doContactDelete( aEvent, aData );

		return contactDeleteResponse;
	}


	/**
	 * Invoked when a Contact Info Command is received.  The handler routes the command 
	 * to the appropriate handler based on the extensions and data included in the command.
	 * 
	 * @param aEvent The <code>EPPEvent</code> that is being handled
	 * 
	 * @param aData Any data that a Server needs to send to this <code>EPPContactHandler</code>
	 * 
	 * @return The response that should be sent back to the client.
	 * 
	 * @see com.verisign.epp.framework.EPPContactHandler#doContactInfo(com.verisign.epp.framework.EPPEvent,
	 *      java.lang.Object)
	 */
	@Override
	protected EPPEventResponse doContactInfo ( EPPEvent aEvent, Object aData ) {
		EPPContactInfoCmd theCommand = (EPPContactInfoCmd) aEvent.getMessage();
		cat.debug( "doContactInfo: command = [" + theCommand + "]" );

		// Jobs Contact Info Command?
		if ( theCommand.getId().contains( "jobsContactInfo" ) ) {
			EPPEventResponse jobsContactInfoResponse = this.jobsContactHandler.doContactInfo( aEvent, aData );

			return jobsContactInfoResponse;
		}

		EPPEventResponse contactInfoResponse = this.contactHandler.doContactInfo( aEvent, aData );
		return contactInfoResponse;
	}


	/**
	 * Invoked when a Contact Check Command is received.  The handler routes the command 
	 * to the appropriate handler based on the extensions and data included in the command.
	 * 
	 * @param aEvent The <code>EPPEvent</code> that is being handled
	 * 
	 * @param aData Any data that a Server needs to send to this <code>EPPContactHandler</code>
	 * 
	 * @return The response that should be sent back to the client.
	 * 
	 * @see com.verisign.epp.framework.EPPContactHandler#doContactCheck(com.verisign.epp.framework.EPPEvent,
	 *      java.lang.Object)
	 */
	@Override
	protected EPPEventResponse doContactCheck ( EPPEvent aEvent, Object aData ) {
		EPPContactCheckCmd theCommand = (EPPContactCheckCmd) aEvent.getMessage();
		cat.debug( "doContactCheck: command = [" + theCommand + "]" );

		EPPEventResponse contactCheckResponse = this.contactHandler.doContactCheck( aEvent, aData );

		return contactCheckResponse;
	}


	/**
	 * Invoked when a Contact Transfer Command is received.  The handler routes the command 
	 * to the appropriate handler based on the extensions and data included in the command.
	 * 
	 * @param aEvent The <code>EPPEvent</code> that is being handled
	 * 
	 * @param aData Any data that a Server needs to send to this <code>EPPContactHandler</code>
	 * 
	 * @return The response that should be sent back to the client.
	 * 
	 * @see com.verisign.epp.framework.EPPContactHandler#doContactTransfer(com.verisign.epp.framework.EPPEvent,
	 *      java.lang.Object)
	 */
	@Override
	protected EPPEventResponse doContactTransfer ( EPPEvent aEvent, Object aData ) {
		EPPContactTransferCmd theCommand = (EPPContactTransferCmd) aEvent.getMessage();
		cat.debug( "doContactTransfer: command = [" + theCommand + "]" );

		EPPEventResponse contactTransferResponse = this.contactHandler.doContactTransfer( aEvent, aData );

		return contactTransferResponse;
	}


	/**
	 * Invoked when a Contact Update Command is received.  The handler routes the command 
	 * to the appropriate handler based on the extensions and data included in the command.
	 * 
	 * @param aEvent The <code>EPPEvent</code> that is being handled
	 * 
	 * @param aData Any data that a Server needs to send to this <code>EPPContactHandler</code>
	 * 
	 * @return The response that should be sent back to the client.
	 * 
	 * @see com.verisign.epp.framework.EPPContactHandler#doContactUpdate(com.verisign.epp.framework.EPPEvent,
	 *      java.lang.Object)
	 */
	@Override
	protected EPPEventResponse doContactUpdate ( EPPEvent aEvent, Object aData ) {
		EPPContactUpdateCmd theCommand = (EPPContactUpdateCmd) aEvent.getMessage();
		cat.debug( "doContactUpdate: command = [" + theCommand + "]" );

		// Jobs Contact Update Command?
		if ( theCommand.hasExtension(EPPJobsContactUpdateCmd.class)) {
			EPPEventResponse jobsContactUpdateResponse = this.jobsContactHandler.doContactUpdate( aEvent, aData );

			return jobsContactUpdateResponse;
		}

		EPPEventResponse contactUpdateResponse = this.contactHandler.doContactUpdate( aEvent, aData );
		return contactUpdateResponse;
	}

}
