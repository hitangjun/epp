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

package com.verisign.epp.codec.fee.v06;

import java.util.List;

/**
 * Fee Create Extension that enables a client to pass the expected fee for the
 * create command.
 */
public class EPPFeeCreate extends EPPFeeTransform {

	/**
	 * XML local name for <code>EPPFeeCreate</code>.
	 */
	public static final String ELM_LOCALNAME = "create";

	/**
	 * XML root tag for <code>EPPFeeCreate</code>.
	 */
	public static final String ELM_NAME = EPPFeeExtFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * Returns the local name &quot;create&quot; for the create extension.
	 * 
	 * @return Local name &quot;create&quot; for the create extension.
	 */
	protected String getLocalName() {
		return ELM_LOCALNAME;
	}

	/**
	 * Default constructor for <code>EPPFeeCreate</code>.
	 */
	public EPPFeeCreate() {
	}

	/**
	 * Constructor for <code>EPPFeeCreate</code> that takes the required
	 * currency parameter along with a single fee.
	 * 
	 * @param aCurrency
	 *            Currency of the fee
	 * @param aFee
	 *            A single fee value
	 */
	public EPPFeeCreate(String aCurrency, EPPFeeValue aFee) {
		super(aCurrency, aFee);
	}

	/**
	 * Constructor for <code>EPPFeeCreate</code> that takes all attributes.
	 * 
	 * @param aCurrency
	 *            Currency of the fee
	 * @param aFees
	 *            List of fees
	 */
	public EPPFeeCreate(String aCurrency, List<EPPFeeValue> aFees) {
		super(aCurrency, aFees);

	}

}
