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

package com.verisign.epp.framework;

import org.apache.log4j.Logger;

import com.verisign.epp.codec.gen.EPPCodecException;
import com.verisign.epp.codec.gen.EPPFactory;
import com.verisign.epp.codec.gen.EPPMessage;
import com.verisign.epp.codec.idntable.EPPIdnTableCheckCmd;
import com.verisign.epp.codec.idntable.EPPIdnTableInfoCmd;
import com.verisign.epp.codec.idntable.EPPIdnTableMapFactory;
import com.verisign.epp.util.EPPCatFactory;

/**
 * The <code>EPPIdnTableHandler</code> class provides an interface for handling
 * EPP IDN Table Commands in a Server implementation. EPPEvents are handled by
 * the handleEvent() method here and routed to the appropriate abstract member
 * function. Subclasses should override the abstract methods to define the
 * desired behavior of a particular command when it is received. A subclassed
 * instance of <code>EPPIdnTableHandler</code> should be registered with the
 * <code>EPPDispatcher</code> so that EEPEvents related to the Registry Mapping
 * will be handled there.<br>
 * 
 * @see EPPEventHandler
 * @see EPPEvent
 */
public abstract class EPPIdnTableHandler implements EPPEventHandler {
	/** Log4j category for logging */
	private static final Logger cat = Logger.getLogger(EPPIdnTableHandler.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * Construct an instance of <code>EPPIdnTableHandler</code>
	 */
	/**
	 * Whenever an EPPIdnTableHandler instance is created load the corresponding
	 * Map Factory into the Codec
	 */
	public EPPIdnTableHandler() {
		try {
			EPPFactory.getInstance().addMapFactory(
					EPPIdnTableMapFactory.class.getName());
		}
		catch (EPPCodecException e) {
			cat.error(
					"Couldn't load the Map Factory associated with the IDN Table Mapping",
					e);
			System.exit(1);
		}
	}

	/**
	 * Gets the XML Namespace for the Mapping.
	 * 
	 * @return <code>EPPIdnTableMapFactory.NS</code>
	 */
	public String getNamespace() {
		return EPPIdnTableMapFactory.NS;
	}

	/**
	 * This method receives an <code>EPPEvent</code> and routes it to the
	 * appropriate abstract method.
	 * 
	 * @param aEvent
	 *            An <code>EPPEvent</code> that contains the
	 *            <code>EPPCommand</code>
	 * @param aData
	 *            Any data that a Server needs to send to this
	 *            <code>EPPIdnTableHandler</code> instance
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @exception EPPEventException
	 *                Thrown if an unrecognized <code>EPPEvent</code> is
	 *                received
	 */
	public EPPEventResponse handleEvent(EPPEvent aEvent, Object aData)
			throws EPPEventException {
		try {
			this.preHandleEvent(aEvent, aData);
		}
		catch (EPPHandleEventException e) {
			return new EPPEventResponse(e.getResponse());
		}

		EPPMessage message = aEvent.getMessage();
		EPPEventResponse response = null;

		if (message instanceof EPPIdnTableCheckCmd) {
			response = doIdnTableCheck(aEvent, aData);
		}
		else if (message instanceof EPPIdnTableInfoCmd) {
			response = doIdnTableInfo(aEvent, aData);
		}

		try {
			this.postHandleEvent(aEvent, aData);
		}
		catch (EPPHandleEventException e) {
			return new EPPEventResponse(e.getResponse());
		}

		return response;
	}

	/**
	 * Invoked when an IDN Table Check command is received. Subclasses should
	 * define the behavior when an IDN Table Check command is received.
	 * 
	 * @param aEvent
	 *            The <code>EPPEvent</code> that is being handled
	 * @param aData
	 *            Any data that a Server needs to send to this
	 *            <code>EPPIdnTableHandler</code>
	 * 
	 * @return EPPEventResponse The response that should be sent back to the
	 *         client.
	 */
	protected abstract EPPEventResponse doIdnTableCheck(EPPEvent aEvent,
			Object aData);

	/**
	 * Invoked when an IDN Table Info command is received. Subclasses should
	 * define the behavior when a IDN Table Info command is received.
	 * 
	 * @param aEvent
	 *            The <code>EPPEvent</code> that is being handled
	 * @param aData
	 *            Any data that a Server needs to send to this
	 *            <code>EPPIdnTableHandler</code>
	 * 
	 * @return EPPEventResponse The response that should be sent back to the
	 *         client.
	 */
	protected abstract EPPEventResponse doIdnTableInfo(EPPEvent aEvent,
			Object aData);

	/**
	 * Handles any common behavior that all registry commands need to execute
	 * before they execute their command specific behavior. The default
	 * implementation does nothing.
	 * 
	 * @param aEvent
	 *            The <code>EPPEvent</code> that is being handled
	 * @param aData
	 *            Any data that a Server needs to send to this
	 *            <code>EPPIdnTableHandler</code>
	 * 
	 * @exception EPPHandleEventException
	 *                Thrown if an error condition occurs. It must contain an
	 *                <code>EPPEventResponse</code>
	 */
	protected void preHandleEvent(EPPEvent aEvent, Object aData)
			throws EPPHandleEventException {
	}

	/**
	 * Handles any common behavior that all registry commands need to execute
	 * after they execute their command specific behavior. The default
	 * implementation does nothing
	 * 
	 * @param aEvent
	 *            The <code>EPPEvent</code> that is being handled
	 * @param aData
	 *            Any data that a Server needs to send to this
	 *            <code>EPPIdnTableHandler</code>
	 * 
	 * @exception EPPHandleEventException
	 *                Thrown if an error condition occurs. It must contain an
	 *                <code>EPPEventResponse</code>
	 */
	protected void postHandleEvent(EPPEvent aEvent, Object aData)
			throws EPPHandleEventException {
	}
}
