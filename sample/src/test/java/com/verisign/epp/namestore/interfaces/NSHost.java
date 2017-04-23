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
package com.verisign.epp.namestore.interfaces;

import com.verisign.epp.interfaces.EPPSession;
import com.verisign.epp.interfaces.EPPHost;
import com.verisign.epp.codec.namestoreext.EPPNamestoreExtNamestoreExt;
import com.verisign.epp.codec.resellerext.EPPResellerExtCreate;
import com.verisign.epp.codec.resellerext.EPPResellerExtUpdate;
import com.verisign.epp.codec.resellerext.EPPResellerExtUpdate.Action;

/**
 * NameStore Host interface that extends that standard
 * <code>EPPHost</code> by adding new methods like @link{#setSubProductID(String)}.  
 * <code>EPPHost</code> could be used directly, but <code>NSHost</code> 
 * can be enhanced independent of the EPP specification.   
 */
public class NSHost extends EPPHost {
	
			
	/**
	 * Creates an <code>NSHost</code> with an 
	 * established <code>EPPSession</code>. 
	 * 
	 * @param aSession Established session
	 */
	public NSHost(EPPSession aSession) {
		super(aSession);
	}

	
	
	/**
	 * Sets the host sub-product id which specifies which is the 
	 * target registry for the host operation.  Some possible 
	 * values include dotCC, dotTV, dotBZ, dotCOM, dotNET.  This results 
	 * in a <code>EPPNamestoreExtNamestoreExt</code> extension being 
	 * added to the command.
	 * 
	 * @param aSubProductID Sub-product id of host operation.  Should use one 
	 * of the @link{NSSubProduct} constants.  Passing <code>null</code> 
	 * will not add any extension.
	 */
	public void setSubProductID(String aSubProductID) {
		if (aSubProductID != null) {
			super.addExtension(new EPPNamestoreExtNamestoreExt(aSubProductID));
		}
	}
	
	/**
	 * Sets the reseller identifier used with <code>sendCreate()</code>.
	 * 
	 * @param aResellerId
	 *            Reseller identifier
	 */
	public void setResellerId(String aResellerId) {
		super.addExtension(new EPPResellerExtCreate(aResellerId));
	}

	/**
	 * Updates the reseller identifier used with <code>sendUpdate()</code>.
	 * There are three possible actions add, remove, and change defined in the
	 * {@link Action} <code>aAction</code> parameter that can be taken with the
	 * reseller identifier defined by the <code>aResellerId</code> parameter.
	 * 
	 * @param aAction
	 *            Update action to take using the <code>Action</code> enumerated
	 *            values.
	 * @param aResellerId
	 *            Reseller identifier
	 */
	public void updateResellerId(Action aAction, String aResellerId) {
		super.addExtension(new EPPResellerExtUpdate(aAction, aResellerId));
	}
	
	
	/**
	 * Resets the host attributes for the next command.
	 */
	protected void resetHost() {
		super.resetHost();
	}
	
} // End class NSHost
