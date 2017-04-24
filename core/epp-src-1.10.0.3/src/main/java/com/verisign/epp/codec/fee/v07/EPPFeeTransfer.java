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

package com.verisign.epp.codec.fee.v07;

import java.util.List;

/**
 * Fee Transfer Extension that enables a client to pass the expected fee for the
 * transfer command.
 */
public class EPPFeeTransfer extends EPPFeeTransform {

	/**
	 * XML local name for <code>EPPFeeTransfer</code>.
	 */
	public static final String ELM_LOCALNAME = "transfer";

	/** 
	 * XML root tag for <code>EPPFeeTransfer</code>. 
	 */
	public static final String ELM_NAME = EPPFeeExtFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * Returns the local name &quot;transfer&quot; for the transfer extension.
	 * 
	 * @return Local name &quot;transfer&quot; for the transfer extension.
	 */
	protected String getLocalName() {
		return ELM_LOCALNAME;
	}
	
	
	/**
	 * Default constructor for <code>EPPFeeTransfer</code>.
	 */
	public EPPFeeTransfer() {
	}

	/**
	 * Constructor for <code>EPPFeeTransfer</code> that takes the required
	 * parameter of at least one fee.
	 * 
	 * @param aFee
	 *            A single fee value
	 */
	public EPPFeeTransfer(EPPFeeValue aFee) {
		super(aFee);
	}

	/**
	 * Constructor for <code>EPPFeeTransfer</code> that takes all attributes.
	 * 
	 * @param aFees
	 *            List of fees
	 * @param aCurrency
	 *            Currency of the fee
	 */
	public EPPFeeTransfer(List<EPPFeeValue> aFees, String aCurrency) {
		super(aFees, aCurrency);

	}

}
