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

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.TrustAnchor;

import org.apache.log4j.Logger;

import com.verisign.epp.exception.EPPException;
import com.verisign.epp.util.EPPCatFactory;

/**
 * Concrete verification code validator that leverages the encoding of the trust
 * anchor aliases in the verification code trust store to apply specific
 * validations, which include:<br>
 * <ol>
 * <li>VSP Identifier (vsp-id) in the code value
 * <li>Code type
 * </ol>
 * The encoding of the trust anchor aliases is defined by
 * {@link TrustAnchorVerificationData}.
 */
public class TrustAnchorVerificationCodeValidator implements
		EPPVerificationCodeValidator {

	/** 
	 * Log4j category for logging 
	 */
	private static Logger cat = Logger.getLogger(
			TrustAnchorVerificationCodeValidator.class.getName(), EPPCatFactory
					.getInstance().getFactory());

	/**
	 * Trust store for looking up the alias of the trust anchor.
	 */
	private KeyStore trustStore;

	/**
	 * Constructor of <code>TrustAnchorVerificationCodeValidator</code> that
	 * takes the trust store containing the verification code trust anchors with
	 * the aliases encoding according to the format defined in
	 * {@link TrustAnchorVerificationData}.
	 * 
	 * @param aTrustStore
	 *            Trust store to lookup the trust anchor alias to apply the
	 *            policy.
	 */
	public TrustAnchorVerificationCodeValidator(KeyStore aTrustStore) {
		this.trustStore = aTrustStore;
	}

	/**
	 * Validate the contents of a verification code using the trust anchor of
	 * the signed code to look up the trust store alias, and then using the
	 * trust store alias to apply the validation logic for the verification
	 * code.
	 * 
	 * @param aVerificationCode
	 *            Verification code to validate
	 * @param aData
	 *            Must be the {@link TrustAnchor} of the {@link EPPSignedCode}.
	 * 
	 * @return <code>true</code> if the verification code is valid;
	 *         <code>false</code> otherwise.
	 * 
	 * @exception EPPException
	 *                Error executing the validation.
	 */
	public boolean validate(EPPVerificationCode aVerificationCode, Object aData)
			throws EPPException {
		cat.debug("validate(EPPVerificationCode, Object): enter");

		boolean theRet = false;

		// Correct aData parameter?
		if (aData == null) {
			throw new EPPException(
					"TrustAnchorVerificationCodeValidator.validate:"
							+ " aData is null");
		}
		if (!(aData instanceof TrustAnchor)) {
			throw new EPPException(
					"TrustAnchorVerificationCodeValidator.validate:"
							+ " aData is null");
		}

		TrustAnchor theTrustAnchor = (TrustAnchor) aData;
		String theTrustAnchorAlias = "";

		// Get the trust anchor alias for the signed code
		try {
			theTrustAnchorAlias = this.trustStore
					.getCertificateAlias(theTrustAnchor.getTrustedCert());

			cat.debug("TrustAnchorVerificationCodeValidator.validate:"
					+ "theTrustAnchorAlias = " + theTrustAnchorAlias);

			// Parse trust anchor alias
			TrustAnchorVerificationData trustAnchorData = new TrustAnchorVerificationData(
					theTrustAnchorAlias);

			// Validate the verification code against the trust anchor data

			// vspId
			if (aVerificationCode.getVspId() != trustAnchorData.getCodeVspId()) {
				cat.debug("TrustAnchorVerificationCodeValidator.validate:"
						+ "verification code vspId "
						+ aVerificationCode.getVspId() + " != "
						+ "trust anchor code vspId "
						+ trustAnchorData.getCodeVspId());
				return false;
			}

			// codeType
			if (!trustAnchorData.getCodeTypes().contains(
					aVerificationCode.getType())) {
				cat.debug("TrustAnchorVerificationCodeValidator.validate:"
						+ "verification code type "
						+ aVerificationCode.getType()
						+ " not contained in trust anchor types");
				return false;
			}

			cat.debug("TrustAnchorVerificationCodeValidator.validate:"
					+ "Code is valid with theTrustAnchorAlias = "
					+ theTrustAnchorAlias);

			theRet = true;
		}
		catch (KeyStoreException e) {
			throw new EPPException(
					"TrustAnchorVerificationCodeValidator.validate:"
							+ "Error getting Trust Anchor Alias: " + e);
		}

		cat.debug("validate(EPPVerificationCode, Object): exit");
		return theRet;
	}
}
