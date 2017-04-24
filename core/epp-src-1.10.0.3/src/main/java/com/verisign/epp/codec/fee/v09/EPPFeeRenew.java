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

package com.verisign.epp.codec.fee.v09;

import java.util.List;

/**
 * Fee Renew Extension that enables a client to pass the expected fee for the
 * renew command.
 */
public class EPPFeeRenew extends EPPFeeTransform {

	/**
	 * XML local name for <code>EPPFeeRenew</code>.
	 */
	public static final String ELM_LOCALNAME = "renew";

	/** XML root tag for <code>EPPFeeRenew</code>. */
	public static final String ELM_NAME = EPPFeeExtFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * Returns the local name &quot;renew&quot; for the renew extension.
	 * 
	 * @return Local name &quot;renew&quot; for the renew extension.
	 */
	protected String getLocalName() {
		return ELM_LOCALNAME;
	}

	/**
	 * Default constructor for <code>EPPFeeRenew</code>.
	 */
	public EPPFeeRenew() {
	}

	/**
	 * Constructor for <code>EPPFeeRenew</code> that takes a single fee.
	 * 
	 * @param aFee
	 *            A single fee value
	 */
	public EPPFeeRenew(EPPFeeValue aFee) {
		super(aFee);
	}

	/**
	 * Constructor for <code>EPPFeeRenew</code> that takes a single fee 
	 * and the optional currency.
	 * 
	 * @param aFee
	 *            A single fee of the update command
	 * @param aCurrency
	 *            Currency of the fee            
	 */
	public EPPFeeRenew(EPPFeeValue aFee, String aCurrency) {
		super(aFee, aCurrency);
	}
	
	
	/**
	 * Constructor for <code>EPPFeeRenew</code> that takes all attributes.
	 * 
	 * @param aFees
	 *            List of fees
	 * @param aCurrency
	 *            Currency of the fees
	 */
	public EPPFeeRenew(List<EPPFeeValue> aFees, String aCurrency) {
		super(aFees, aCurrency);

	}

}
