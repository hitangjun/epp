/***********************************************************
 Copyright (C) 2006 VeriSign, Inc.

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
package com.verisign.epp.util;

import com.verisign.epp.interfaces.EPPSession;

/**
 * Simple <code>Exception</code> that is thrown by the utility
 * <code>TestUtil.handleException(EPPSession, Exception)</code> method to
 * indicate that the session should be invalidated by the calling method and
 * further use of the session should be terminated.
 * 
 * @see TestUtil#handleException(EPPSession, Exception)
 */
public class InvalidateSessionException extends Exception {

	/**
	 * Session that should be invalidated.
	 */
	EPPSession session;

	/**
	 * Default constructor
	 */
	public InvalidateSessionException() {
		super();
	}

	/**
	 * Create with text message describing the exception.
	 * 
	 * @param aMessage
	 *            description message
	 */
	public InvalidateSessionException(String aMessage) {
		super(aMessage);
	}

	/**
	 * Create with text message describing the exception and an associated cause
	 * <code>Throwable</code>.
	 * 
	 * @param aMessage
	 *            description message
	 * @param aCause
	 *            <code>Throwable</code> that caused the exception
	 */
	public InvalidateSessionException(String aMessage, Throwable aCause) {
		super(aMessage, aCause);
	}

	/**
	 * Create with an associated cause <code>Throwable</code>.
	 * 
	 * @param aCause
	 *            <code>Throwable</code> that caused the exception
	 */
	public InvalidateSessionException(Throwable aCause) {
		super(aCause);
	}

	/**
	 * Constructor that takes the session that should be invalidated.
	 * 
	 * @param aSession
	 *            Session that should be invalidated
	 */
	public InvalidateSessionException(EPPSession aSession) {
		super();
		this.session = aSession;
	}

	/**
	 * Create with text message describing the exception along with the session
	 * that should be invalidated.
	 * 
	 * @param aSession
	 *            Session that should be invalidated
	 * @param aMessage
	 *            description message
	 */
	public InvalidateSessionException(EPPSession aSession, String aMessage) {
		super(aMessage);
		this.session = aSession;
	}

	/**
	 * Create with text message describing the exception and an associated cause
	 * <code>Throwable</code> and the session that should be invalidated.
	 * 
	 * @param aSession
	 *            Session that should be invalidated
	 * @param aMessage
	 *            description message
	 * @param aCause
	 *            <code>Throwable</code> that caused the exception
	 */
	public InvalidateSessionException(EPPSession aSession, String aMessage,
			Throwable aCause) {
		super(aMessage, aCause);
		this.session = aSession;
	}

	/**
	 * Create with an associated cause <code>Throwable</code> and the session
	 * that should be invalidated.
	 * 
	 * @param aSession
	 *            Session that should be invalidated
	 * @param cause
	 *            <code>Throwable</code> that caused the exception
	 */
	public InvalidateSessionException(EPPSession aSession, Throwable cause) {
		super(cause);
		this.session = aSession;
	}

	/**
	 * Has the session to be invalidated been set?
	 * 
	 * @return <code>true</code> of the session has been set;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasSession() {
		return (this.session != null ? true : false);
	}
	
	/**
	 * Gets the session that should be invalidated.
	 * 
	 * @return Session that should be invalidated if set; <code>null</code> otherwise.
	 */
	public EPPSession getSession() {
		return this.session;
	}

	/**
	 * Sets the session that should be invalidated.
	 * 
	 * @param aSession Session that should be invalidated
	 */
	public void setSession(EPPSession aSession) {
		this.session = aSession;
	}
	
	

}
