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

import java.math.BigDecimal;
import java.util.List;

import com.verisign.epp.codec.gen.EPPEncodeException;

/**
 * Fee Renew Result Extension that enables a server to pass the resulting fee
 * for the renew command.
 */
public class EPPFeeRenData extends EPPFeeTransformResult {

	/**
	 * XML local name for <code>EPPFeeRenData</code>.
	 */
	public static final String ELM_LOCALNAME = "renData";

	/**
	 * XML root tag for <code>EPPFeeRenData</code>.
	 */
	public static final String ELM_NAME = EPPFeeExtFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * Returns the local name &quot;renData&quot; for the renew data.
	 * 
	 * @return Local name &quot;renData&quot; for the renew data.
	 */
	protected String getLocalName() {
		return ELM_LOCALNAME;
	}

	/**
	 * Validate the set of attributes on <code>encode</code>.
	 * 
	 * @throws EPPEncodeException
	 *             When an attribute is incorrect set or not set.
	 */
	protected void validateAttributes() throws EPPEncodeException {
		// Required attributes
		if (!super.hasCurrency()) {
			throw new EPPEncodeException(
					"Required currency attribute is not set for EPPFeeRenData on call to encode(Document).");
		}
		if (!super.hasFees()) {
			throw new EPPEncodeException(
					"At least one fee is not set for EPPFeeRenData on call to encode(Document).");
		}

		// Invalid base class attributes
		if (super.hasPeriod()) {
			throw new EPPEncodeException(
					"Period is incorrectly set for EPPFeeRenData on call to encode(Document).");
		}
	}

	/**
	 * Default constructor for <code>EPPFeeRenData</code>.
	 */
	public EPPFeeRenData() {
	}

	/**
	 * Constructor for <code>EPPFeeRenData</code> that takes the required
	 * currency parameter along with a single fee.
	 * 
	 * @param aCurrency
	 *            Currency of the fee
	 * @param aFee
	 *            A single fee value
	 */
	public EPPFeeRenData(String aCurrency, EPPFeeValue aFee) {
		super(aCurrency, aFee);
	}

	/**
	 * Constructor for <code>EPPFeeRenData</code> that takes all attributes.
	 * 
	 * @param aCurrency
	 *            Currency of the fee
	 * @param aFees
	 *            List of fees
	 * @param aCredits
	 *            OPTIONAL list of credits. Set to <code>null</code> to specify
	 *            no credits.
	 * @param aBalance
	 *            Available balance for client
	 * @param aCreditLimit
	 *            Maximum credit for client.
	 */
	public EPPFeeRenData(String aCurrency, List<EPPFeeValue> aFees,
			List<EPPFeeCredit> aCredits, BigDecimal aBalance,
			BigDecimal aCreditLimit) {
		super(aCurrency, aFees, aBalance, aCreditLimit);
		super.setCredits(aCredits);
	}

}
