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

import com.verisign.epp.codec.domain.EPPDomainCheckCmd;
import com.verisign.epp.codec.domain.EPPDomainCreateCmd;
import com.verisign.epp.codec.domain.EPPDomainDeleteCmd;
import com.verisign.epp.codec.domain.EPPDomainInfoCmd;
import com.verisign.epp.codec.domain.EPPDomainRenewCmd;
import com.verisign.epp.codec.domain.EPPDomainTransferCmd;
import com.verisign.epp.codec.domain.EPPDomainUpdateCmd;
import com.verisign.epp.codec.gen.EPPResponse;

/**
 * Sub Domain Handler for use with the fee extension.  
 * This interface is used to support handling multiple versions of the 
 * fee extension, where each different version of the 
 * fee extension must implement this interface.  
 */
public interface FeeSubDomainHandler {
	
	/**
	 * Handle an EPP Domain Check Command.  
	 * 
	 * @param aCheckCommand Command sent by the client
	 * @param aResponse Response created previously up the stack
	 * @param aData Server data.  This is assumed to be an instance of <code>SessionData</code>.
	 */
	void doDomainCheck(EPPDomainCheckCmd aCheckCommand, EPPResponse aResponse, Object aData);
	
	
	/**
	 * Handle an EPP Domain Info Command.  
	 * 
	 * @param aInfoCommand Command sent by the client
	 * @param aResponse Response created previously up the stack
	 * @param aData Server data.  This is assumed to be an instance of <code>SessionData</code>.
	 */
	void doDomainInfo(EPPDomainInfoCmd aInfoCommand, EPPResponse aResponse, Object aData);
	
	
	/**
	 * Handle an EPP Domain Create Command.  
	 * 
	 * @param aCreateCommand Command sent by the client
	 * @param aResponse Response created previously up the stack
	 * @param aData Server data.  This is assumed to be an instance of <code>SessionData</code>.
	 */
	void doDomainCreate(EPPDomainCreateCmd aCreateCommand, EPPResponse aResponse, Object aData);
	
	/**
	 * Handle an EPP Domain Delete Command.  
	 * 
	 * @param aDeleteCommand Command sent by the client
	 * @param aResponse Response created previously up the stack
	 * @param aData Server data.  This is assumed to be an instance of <code>SessionData</code>.
	 */
	void doDomainDelete(EPPDomainDeleteCmd aDeleteCommand, EPPResponse aResponse, Object aData);
	
	/**
	 * Handle an EPP Domain Renew Command.  
	 * 
	 * @param aRenewCommand Command sent by the client
	 * @param aResponse Response created previously up the stack
	 * @param aData Server data.  This is assumed to be an instance of <code>SessionData</code>.
	 */
	void doDomainRenew(EPPDomainRenewCmd aRenewCommand, EPPResponse aResponse, Object aData);
	
	/**
	 * Handle an EPP Domain Update Command.  
	 * 
	 * @param aUpdateCommand Command sent by the client
	 * @param aResponse Response created previously up the stack
	 * @param aData Server data.  This is assumed to be an instance of <code>SessionData</code>.
	 */
	void doDomainUpdate(EPPDomainUpdateCmd aUpdateCommand, EPPResponse aResponse, Object aData);
	
	/**
	 * Handle an EPP Domain Transfer Command.  
	 * 
	 * @param aTransferCommand Command sent by the client
	 * @param aResponse Response created previously up the stack
	 * @param aData Server data.  This is assumed to be an instance of <code>SessionData</code>.
	 */
	void doDomainTransfer(EPPDomainTransferCmd aTransferCommand, EPPResponse aResponse, Object aData);
	
}
