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

import java.math.BigDecimal;
import java.util.List;

import com.verisign.epp.codec.gen.EPPEncodeException;

/**
 * Fee Transfer Result Extension that enables a server to pass the resulting fee
 * for the transfer command.
 */
public class EPPFeeTrnData extends EPPFeeTransformResult {

	/**
	 * XML local name for <code>EPPFeeTrnData</code>.
	 */
	public static final String ELM_LOCALNAME = "trnData";

	/**
	 * XML root tag for <code>EPPFeeTrnData</code>.
	 */
	public static final String ELM_NAME = EPPFeeExtFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * Returns the local name &quot;trnData&quot; for the transfer data.
	 * 
	 * @return Local name &quot;trnData&quot; for the transfer data.
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
					"Required currency attribute is not set for EPPFeeTrnData on call to encode(Document).");
		}
	}

	/**
	 * Default constructor for <code>EPPFeeTrnData</code>.
	 */
	public EPPFeeTrnData() {
		super();
	}

	/**
	 * Constructor for <code>EPPFeeTrnData</code> that takes the required
	 * currency parameter along with a single fee.
	 * 
	 * @param aCurrency
	 *            Currency of the fee
	 * @param aFee
	 *            A single fee value
	 */
	public EPPFeeTrnData(String aCurrency, EPPFeeValue aFee) {
		super(aCurrency, aFee);
	}

	/**
	 * Constructor for <code>EPPFeeTrnData</code> that takes all transfer
	 * request response attributes.
	 * 
	 * @param aCurrency
	 *            Currency of the fee
	 * @param aFees
	 *            List of fees
	 */
	public EPPFeeTrnData(String aCurrency, List<EPPFeeValue> aFees) {
		super(aCurrency, aFees);

	}

	/**
	 * Constructor for <code>EPPFeeTrnData</code> that takes all transfer query
	 * response attributes.
	 * 
	 * @param aCurrency
	 *            Currency of the fee
	 * @param aPeriod
	 *            Transfer period of transfer request
	 * @param aFees
	 *            List of fees
	 */
	public EPPFeeTrnData(String aCurrency, EPPFeePeriod aPeriod,
			List<EPPFeeValue> aFees, List<EPPFeeCredit> aCredits) {
		super(aCurrency, aFees);
		super.setCredits(aCredits);
		super.setPeriod(aPeriod);
	}

}
