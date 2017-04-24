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
package com.verisign.epp.framework;

import org.apache.log4j.Logger;

import com.verisign.epp.codec.gen.EPPHello;
import com.verisign.epp.codec.gen.EPPLoginCmd;
import com.verisign.epp.codec.gen.EPPLogoutCmd;
import com.verisign.epp.codec.gen.EPPMessage;
import com.verisign.epp.codec.gen.EPPPollCmd;
import com.verisign.epp.util.EPPCatFactory;


/**
 * The <code>EPPGenHandler</code> class provides an interface for handling
 * General EPP Commands in a Server implementation.  EPPEvents are handled by
 * the handleEvent() method here and routed to the appropriate abstract member
 * function. Subclasses should override the abstract methods to define the
 * desired behavior of a particular command when it is received.  A subclassed
 * instance of <code>EPPGenHandler</code> should be registered with the
 * <code>EPPDispatcher</code> so that EEPEvents related to the General EPP
 * Mapping will be handled there.     
 *
 * @see EPPEventHandler
 * @see EPPEvent
 */
public abstract class EPPGenHandler implements EPPEventHandler {
	/** The Namespace that this handler supports.  In this case, general epp. */
	private static final String NS = "urn:ietf:params:xml:ns:epp-1.0";
	
	/** Log4j category for logging */
	private static Logger cat = Logger.getLogger(EPPGenHandler.class
			.getName(), EPPCatFactory.getInstance().getFactory());
	

	/**
	 * Construct an instance of <code>EPPGenHandler</code>
	 */
	public EPPGenHandler() {
	}

	/**
	 * Returns the Namespace that this handler supports. In this case, domain.
	 *
	 * @return String The Namespace that this handler supports
	 */
	public final String getNamespace() {
		return NS;
	}

	/**
	 * This method receives an <code>EPPEvent</code> and routes it to the
	 * appropriate abstract method.
	 *
	 * @param aEvent An <code>EPPEvent</code> that contains the
	 * 		  <code>EPPCommand</code>
	 * @param aData Any data that a Server needs to send to this
	 * 		  <code>EPPContactHandler</code> instance
	 *
	 * @return DOCUMENT ME!
	 *
	 * @exception EPPEventException Thrown if an unrecognized
	 * 			  <code>EPPEvent</code> is received
	 */
	public final EPPEventResponse handleEvent(EPPEvent aEvent, Object aData)
									   throws EPPEventException {
		cat.debug("handleEvent(EPPEvent, Object): Enter");
		try {
			cat.debug("handleEvent(EPPEvent, Object): Call preHandleEvent");
			this.preHandleEvent(aEvent, aData);
		}
		 catch (EPPHandleEventException e) {
			cat.info("handleEvent(EPPEvent, Object): Exception with preHandleEvent: " + e);
			return new EPPEventResponse(e.getResponse());
		}

		EPPMessage		 message  = aEvent.getMessage();
		EPPEventResponse response;

		if (message instanceof EPPLoginCmd) {
			cat.debug("handleEvent(EPPEvent, Object): Handle EPPLoginCmd");
			response = doLogin(aEvent, aData);
		}
		else if (message instanceof EPPLogoutCmd) {
			cat.debug("handleEvent(EPPEvent, Object): Handle EPPLogoutCmd");
			response = doLogout(aEvent, aData);
		}
		else if (message instanceof EPPPollCmd) {
			cat.debug("handleEvent(EPPEvent, Object): Handle EPPPollCmd");
			response = doPoll(aEvent, aData);
		}
		else if (message instanceof EPPHello) {
			cat.debug("handleEvent(EPPEvent, Object): Handle EPPHello");
			response = doHello(aEvent, aData);
		}
		else {
			cat.info("handleEvent(EPPEvent, Object): Event " + message.getClass().getName() + " not supported");
			throw new EPPEventException("In EPPGenHandler an event was sent that is not supported");
		}

		try {
			cat.debug("handleEvent(EPPEvent, Object): Call postHandleEvent");
			this.postHandleEvent(aEvent, aData);
		}
		 catch (EPPHandleEventException e) {
			cat.info("handleEvent(EPPEvent, Object): Exception with postHandleEvent: " + e);
			return new EPPEventResponse(e.getResponse());
		}

		cat.debug("handleEvent(EPPEvent, Object): Exit");
		return response;
	}

	/**
	 * Handles any common behavior that all gen commands need to execute before
	 * they execute their command specific behavior.  The default
	 * implementation does nothing.
	 *
	 * @param aEvent The <code>EPPEvent</code> that is being handled
	 * @param aData Any data that a Server needs to send to this
	 * 		  <code>EPPDomainHandler</code>
	 *
	 * @exception EPPHandleEventException Thrown if an error condition occurs.
	 * 			  It must contain an <code>EPPEventResponse</code>
	 */
	protected void preHandleEvent(EPPEvent aEvent, Object aData)
						   throws EPPHandleEventException {
	}

	/**
	 * Handles any common behavior that all gen commands need to execute after
	 * they execute their command specific behavior.  The default
	 * implementation does nothing
	 *
	 * @param aEvent The <code>EPPEvent</code> that is being handled
	 * @param aData Any data that a Server needs to send to this
	 * 		  <code>EPPDomainHandler</code>
	 *
	 * @exception EPPHandleEventException Thrown if an error condition occurs.
	 * 			  It must contain an <code>EPPEventResponse</code>
	 */
	protected void postHandleEvent(EPPEvent aEvent, Object aData)
							throws EPPHandleEventException {
	}

	/**
	 * Invoked when an EPP Login command is received.  Subclasses should define
	 * the behavior when a Login command is received.
	 *
	 * @param aEvent The <code>EPPEvent</code> that is being handled
	 * @param aData Any data that a Server needs to send to this
	 * 		  <code>EPPDomainHandler</code>
	 *
	 * @return EPPEventResponse The response that should be sent back to the
	 * 		   client.
	 */
	protected abstract EPPEventResponse doLogin(EPPEvent aEvent, Object aData);

	/**
	 * Invoked when an EPP Logout command is received.  Subclasses should
	 * define the behavior when a Logout command is received.
	 *
	 * @param aEvent The <code>EPPEvent</code> that is being handled
	 * @param aData Any data that a Server needs to send to this
	 * 		  <code>EPPDomainHandler</code>
	 *
	 * @return EPPEventResponse The response that should be sent back to the
	 * 		   client.
	 */
	protected abstract EPPEventResponse doLogout(EPPEvent aEvent, Object aData);

	/**
	 * Invoked when an EPP Poll command is received.  Subclasses should define
	 * the behavior when a Poll command is received.
	 *
	 * @param aEvent The <code>EPPEvent</code> that is being handled
	 * @param aData Any data that a Server needs to send to this
	 * 		  <code>EPPDomainHandler</code>
	 *
	 * @return EPPEventResponse The response that should be sent back to the
	 * 		   client.
	 */
	protected abstract EPPEventResponse doPoll(EPPEvent aEvent, Object aData);

	/**
	 * Invoked when an EPP Hello command is received.  Subclasses should define
	 * the behavior when a Hello command is received.
	 *
	 * @param aEvent The <code>EPPEvent</code> that is being handled
	 * @param aData Any data that a Server needs to send to this
	 * 		  <code>EPPDomainHandler</code>
	 *
	 * @return EPPEventResponse The response that should be sent back to the
	 * 		   client.
	 */
	protected abstract EPPEventResponse doHello(EPPEvent aEvent, Object aData);
}
