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

package com.verisign.epp.codec.fee.v08;

import java.util.List;

/**
 * Fee Update Extension that enables a client to pass the expected fee for the
 * update command.
 */
public class EPPFeeUpdate extends EPPFeeTransform {

	/**
	 * XML local name for <code>EPPFeeUpdate</code>.
	 */
	public static final String ELM_LOCALNAME = "update";

	/**
	 * XML root tag for <code>EPPFeeUpdate</code>.
	 */
	public static final String ELM_NAME = EPPFeeExtFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * Returns the local name &quot;update&quot; for the update extension.
	 * 
	 * @return Local name &quot;update&quot; for the update extension.
	 */
	protected String getLocalName() {
		return ELM_LOCALNAME;
	}

	/**
	 * Default constructor for <code>EPPFeeUpdate</code>.
	 */
	public EPPFeeUpdate() {
	}

	/**
	 * Constructor for <code>EPPFeeUpdate</code> that takes the required
	 * parameter of at least one fee.
	 * 
	 * @param aFee
	 *            A single fee value
	 */
	public EPPFeeUpdate(EPPFeeValue aFee) {
		super(aFee);
	}

	/**
	 * Constructor for <code>EPPFeeUpdate</code> that takes all attributes.
	 * 
	 * @param aFees
	 *            List of fees
	 * @param aCurrency
	 *            Currency of the fee
	 */
	public EPPFeeUpdate(List<EPPFeeValue> aFees, String aCurrency) {
		super(aFees, aCurrency);
	}

}
