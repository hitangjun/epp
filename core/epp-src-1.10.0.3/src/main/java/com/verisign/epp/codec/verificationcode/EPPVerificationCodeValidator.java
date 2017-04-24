/***********************************************************
 Copyright (C) 2016 VeriSign, Inc.

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
 ***********************************************************/

package com.verisign.epp.codec.verificationcode;

import com.verisign.epp.exception.EPPException;

/**
 * Validates a verification code. A implementing class can define different
 * mechanisms for verifying the {@link EPPVerificationCode} instance using
 * server-specific policies. The implementing class should be thread-safe.
 */
public interface EPPVerificationCodeValidator {

	/**
	 * Validate the contents of a verification code using local server policy.
	 * For example, the code value and the type can be verified against the
	 * user's profile or against the matching trust anchor certificate. Many
	 * mechanisms can be created.
	 * 
	 * @param aVerificationCode
	 *            Verification code to validate
	 * @param aData
	 *            Optional extra server-specific data. Pass as <code>null</code>
	 *            if not needed.
	 * 
	 * @return <code>true</code> if the verification code is valid;
	 *         <code>false</code> otherwise.
	 * 
	 * @exception EPPException
	 *                Error executing the validation.
	 */
	boolean validate(EPPVerificationCode aVerificationCode, Object aData)
			throws EPPException;
}
