/*******************************************************************************
 * The information in this document is proprietary to VeriSign and the VeriSign
 * Registry Business. It may not be used, reproduced, or disclosed without the
 * written approval of the General Manager of VeriSign Information Services.
 * 
 * PRIVILEGED AND CONFIDENTIAL VERISIGN PROPRIETARY INFORMATION (REGISTRY
 * SENSITIVE INFORMATION)
 * Copyright (c) 2006 VeriSign, Inc. All rights reserved.
 * **********************************************************
 */

// jgould -- Oct 9, 2015

package com.verisign.epp.codec.nv;

import java.security.InvalidParameterException;

/**
 * Name Verification (NV) statuses, which include the following 
 * possible values:<br>
 * <ul>
 * <li><code>PENDING_COMPLIANT</code> - The object verification is not complete and is pending.</li>
 * <li><code>COMPLIANT</code> - The object is in compliance with the policy.</li>
 * <li><code>NON_COMPLIANT</code> - The object is not in compliance with the policy.</li>
 * </ul>
 */
public enum EPPNameVerificationStatus {
	PENDING_COMPLIANT("pendingCompliant"), COMPLIANT("compliant"), NON_COMPLIANT("nonCompliant");

	private final String statusStr;

	/**
	 * Define the string value for the enumerated value.
	 * 
	 * @param aStatusStr
	 *            Enumerated value string
	 */
	EPPNameVerificationStatus(String aStatusStr) {
		this.statusStr = aStatusStr;
	}

	/**
	 * Get the status enumerated value given the matching string.
	 * 
	 * @param aString
	 *            <code>EPPNameVerificationStatus</code> enumerated string to convert to an
	 *            enumerated <code>EPPNameVerificationStatus</code> instance.
	 * 
	 * @return Enumerated <code>EPPNameVerificationStatus</code> value matching the
	 *         <code>String</code>.
	 * 
	 * @throws InvalidParameterException
	 *             If <code>aString</code> does not match an enumerated
	 *             <code>EPPNameVerificationStatus</code> string value.
	 */
	public static EPPNameVerificationStatus getStatus(String aString) {
		if (aString.equals(PENDING_COMPLIANT.statusStr)) {
			return PENDING_COMPLIANT;
		}
		else if (aString.equals(COMPLIANT.statusStr)) {
			return COMPLIANT;
		}
		else if (aString.equals(NON_COMPLIANT.statusStr)) {
			return NON_COMPLIANT;
		}
		else {
			throw new InvalidParameterException("Status enum value of "
					+ aString + " is not valid.");
		}
	}

	/**
	 * Convert the enumerated <code>EPPNameVerificationStatus</code> value to a
	 * <code>String</code>.
	 */
	public String toString() {
		return this.statusStr;
	}
}
