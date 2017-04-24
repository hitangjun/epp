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

package com.verisign.epp.serverstub;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.PKIXParameters;
import java.util.Date;

import junit.framework.Assert;

import org.apache.log4j.Logger;

import com.verisign.epp.codec.gen.EPPAuthInfo;
import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPResponse;
import com.verisign.epp.codec.gen.EPPResult;
import com.verisign.epp.codec.gen.EPPTransId;
import com.verisign.epp.codec.nv.EPPDomainNameVerification;
import com.verisign.epp.codec.nv.EPPNameVerificationCheckCmd;
import com.verisign.epp.codec.nv.EPPNameVerificationCheckResp;
import com.verisign.epp.codec.nv.EPPNameVerificationCheckResult;
import com.verisign.epp.codec.nv.EPPNameVerificationCreateCmd;
import com.verisign.epp.codec.nv.EPPNameVerificationCreateFailed;
import com.verisign.epp.codec.nv.EPPNameVerificationCreatePending;
import com.verisign.epp.codec.nv.EPPNameVerificationCreateResp;
import com.verisign.epp.codec.nv.EPPNameVerificationCreateSuccess;
import com.verisign.epp.codec.nv.EPPNameVerificationDocument;
import com.verisign.epp.codec.nv.EPPNameVerificationInfoCmd;
import com.verisign.epp.codec.nv.EPPNameVerificationInfoInputResult;
import com.verisign.epp.codec.nv.EPPNameVerificationInfoResp;
import com.verisign.epp.codec.nv.EPPNameVerificationInfoSignedCodeResult;
import com.verisign.epp.codec.nv.EPPNameVerificationPendActionMsg;
import com.verisign.epp.codec.nv.EPPNameVerificationStatus;
import com.verisign.epp.codec.nv.EPPNameVerificationUpdateCmd;
import com.verisign.epp.codec.nv.EPPRealNameVerification;
import com.verisign.epp.codec.verificationcode.EPPEncodedSignedCodeValue;
import com.verisign.epp.codec.verificationcode.EPPSignedCode;
import com.verisign.epp.codec.verificationcode.VerificationCodeRevocationList;
import com.verisign.epp.exception.EPPException;
import com.verisign.epp.framework.EPPEvent;
import com.verisign.epp.framework.EPPEventResponse;
import com.verisign.epp.framework.EPPHandleEventException;
import com.verisign.epp.framework.EPPNameVerificationHandler;
import com.verisign.epp.framework.EPPPollQueueException;
import com.verisign.epp.framework.EPPPollQueueMgr;
import com.verisign.epp.util.EPPCatFactory;

/**
 * Stub handler for the Name Verification commands.
 */
public class NameVerificationHandler extends EPPNameVerificationHandler {

	/** Log4j category for logging */
	private static Logger cat = Logger.getLogger(NameVerificationHandler.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * Private key used for signing
	 */
	private static PrivateKey privateKey;

	/**
	 * Public key used for verification
	 */
	private static PublicKey publicKey;

	/**
	 * PKIX parameters passed to PKIX <code>CertPathValidator</code> algorithm.
	 */
	private static PKIXParameters pkixParameters;

	/**
	 * verification code revocation list
	 */
	private static VerificationCodeRevocationList verificationCodeRevocationList = new VerificationCodeRevocationList();

	/**
	 * Certificate chain associated with the private key
	 */
	private static Certificate[] certChain;

	/**
	 * Keystore containing valid certificate and private key used to sign the
	 * code.
	 */
	private static final String KEYSTORE_FILENAME = "verificationCode.jks";

	/**
	 * Password used to access <code>KEYSTORE_FILENAME</code>.
	 */
	private static final String KEYSTORE_PASSWORD = "changeit";

	/**
	 * Alias for PrivateKeyEntry containing certificate and private used for
	 * signing
	 */
	private static final String KEYSTORE_KEY_ALIAS = "verificationcode";

	/**
	 * Alias for trustedCertEntry containing certificate used to verify
	 * signature, when not using certificate included in XML Signature.
	 */
	private static final String KEYSTORE_CERT_ALIAS = "0:-1:china:domain~real-name:eppsdk:1";

	/**
	 * Keystore containing valid certificate and private key used to signed the
	 * codes.
	 */
	private static final String TRUSTSTORE_FILENAME = "verificationCode.jks";

	/**
	 * Assigned VSP identifier used to create the verification code.
	 */
	private static final String VSP_ID = "0";

	/**
	 * Count of check results that will be mod 3 to determine the response.
	 */
	private static int checkResultCount = 0;

	/**
	 * Create a signed code from a code value.
	 * 
	 * @param aCode
	 *            Code value to sign
	 * 
	 * @return Encoded signed code
	 * 
	 * @throws EPPException
	 *             Error creating the signed code
	 */
	private EPPEncodedSignedCodeValue createSignedCode(String aCode,
			String aType) throws EPPException {
		cat.debug("createSignedCode: enter");

		cat.debug("createSignedCode: code = " + aCode + ", type = " + aType);
		System.out.println("createSignedCode: code = " + aCode + ", type = "
				+ aType);

		EPPSignedCode signedCode = null;
		EPPEncodedSignedCodeValue encodedSignedCodeValue = null;

		try {
			// Domain verification code
			signedCode = new EPPSignedCode(aCode, aType);
			signedCode.sign(privateKey, certChain);

			if (!signedCode.validate(publicKey)) {
				cat.error("Signed code validation error using public key");
				throw new EPPException(
						"Signed code validation error using public key");
			}

			if (!signedCode.validate(pkixParameters)) {
				cat.error("Signed code validation error using PKIX");
				throw new EPPException(
						"Signed code validation error using PKIX");
			}

			encodedSignedCodeValue = new EPPEncodedSignedCodeValue(signedCode);

			cat.debug("createSignedCode: encoded signed code value = ["
					+ encodedSignedCodeValue + "]");
		}
		catch (EPPEncodeException e) {
			throw new EPPException("EPPEncodeException: " + e);
		}
		catch (EPPException e) {
			throw new EPPException("EPPException: " + e);
		}

		cat.debug("createSignedCode: exit");
		return encodedSignedCodeValue;
	}

	/**
	 * Loads the private key used to digitally sign from a Java KeyStore with
	 * the <code>aKeyAliasName</code> alias and the <code>aPassword</code>
	 * password to access the Keystore and the key.
	 * 
	 * @param aKeyStoreName
	 *            Java Keystore to load the key from
	 * @param aKeyAliasName
	 *            Java Keystore alias of key
	 * @param aPassword
	 *            Password to access Java Keystore and key
	 * 
	 * @return Loaded <code>KeyStore.PrivateKeyEntry</code> that can be used to
	 *         get the private key and it's associated certificate chain.
	 * 
	 * @throws Exception
	 *             Error loading private key
	 */
	private static KeyStore.PrivateKeyEntry loadPrivateKeyEntry(
			String aKeyStoreName, String aKeyAliasName, String aPassword)
			throws Exception {

		// Load KeyStore
		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		FileInputStream keyStoreFile = new FileInputStream(aKeyStoreName);
		keyStore.load(keyStoreFile, aPassword.toCharArray());

		// Get Private Key
		assert keyStore.isKeyEntry(aKeyAliasName);
		KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry) keyStore
				.getEntry(aKeyAliasName, new KeyStore.PasswordProtection(
						aPassword.toCharArray()));

		return keyEntry;
	}

	/**
	 * Loads the public key used to verify a digital signature signed with the
	 * associated private key, loaded by
	 * {@link #loadPrivateKeyEntry(String, String, String)}.
	 * 
	 * @param aKeyStoreName
	 *            Java Keystore containing the certificate
	 * @param aPublicKeyAlias
	 *            Java Keystore alias of the <code>trustedCertEntry</code>
	 *            containing the public key
	 * 
	 * @return Loaded <code>PublicKey</code> instance
	 * 
	 * @throws Exception
	 *             Error loading the public key
	 */
	public static PublicKey loadPublicKey(String aKeyStoreName,
			String aPublicKeyAlias) throws Exception {

		// Load KeyStore
		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		FileInputStream keyStoreFile = new FileInputStream(aKeyStoreName);
		keyStore.load(keyStoreFile, null);

		assert keyStore.isCertificateEntry(aPublicKeyAlias);

		KeyStore.TrustedCertificateEntry certEntry = (KeyStore.TrustedCertificateEntry) keyStore
				.getEntry(aPublicKeyAlias, null);

		return certEntry.getTrustedCertificate().getPublicKey();
	}

	/**
	 * Loads the trust store file into the <code>PKIXParameters</code> used to
	 * verify the certificate chain The Java Trust Store is loaded with the
	 * trusted VSP certificates.
	 * 
	 * @param aTrustStoreName
	 *            Trust store file name
	 * 
	 * @return Initialized <code>PKIXParameters</code> instance.
	 * 
	 * @throws Exception
	 *             Error initializing the PKIX parameters
	 */
	public static PKIXParameters loadPKIXParameters(String aTrustStoreName)
			throws Exception {

		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		FileInputStream trustStoreFile = new FileInputStream(aTrustStoreName);
		trustStore.load(trustStoreFile, null);

		PKIXParameters pkixParameters = new PKIXParameters(trustStore);
		pkixParameters.setRevocationEnabled(false);

		return pkixParameters;
	}

	/**
	 * Constructs an instance of IdnMapDomainHandler
	 */
	public NameVerificationHandler() {
		// Initialize the PKIX parameters and other DSIG properties
		try {
			KeyStore.PrivateKeyEntry keyEntry = loadPrivateKeyEntry(
					KEYSTORE_FILENAME, KEYSTORE_KEY_ALIAS, KEYSTORE_PASSWORD);
			privateKey = keyEntry.getPrivateKey();
			certChain = keyEntry.getCertificateChain();

			publicKey = loadPublicKey(KEYSTORE_FILENAME, KEYSTORE_CERT_ALIAS);

			pkixParameters = loadPKIXParameters(TRUSTSTORE_FILENAME);
		}
		catch (Exception ex) {
			Assert.fail("Error loading keys for signing and validating: " + ex);
		}

		// Initialize the Verification Code Revocation List
		File verificationCodeRevocationListFile = new File(
				"verification-code-test-revocation.csv");

		if (verificationCodeRevocationListFile.exists()) {

			try {
				FileInputStream smdRevocationListStream = new FileInputStream(
						verificationCodeRevocationListFile);
				verificationCodeRevocationList.decode(smdRevocationListStream);

			}
			catch (FileNotFoundException e) {
				Assert.fail("Error opening Verification Code Revocation List File: "
						+ e);
			}
			catch (EPPDecodeException e) {
				Assert.fail("Error decoding Verification Code Revocation List File: "
						+ e);
			}
		}

	}

	/**
	 * Do any pre-handling of commands.
	 *
	 * @param aEvent
	 *            The <code>EPPEvent</code> that is being handled
	 * @param aData
	 *            Any data that a Server needs to send to this
	 *            <code>NameVerificationHandler</code> This is assumed to be an
	 *            instance of SessionData here.
	 *
	 * @exception EPPHandleEventException
	 *                Thrown if an error condition occurs. It must contain an
	 *                <code>EPPEventResponse</code>
	 */
	protected void preHandleEvent(EPPEvent aEvent, Object aData)
			throws EPPHandleEventException {

		super.preHandleEvent(aEvent, aData);

	}

	/**
	 * Override base handler <code>doNameVerificationCheck</code> method and add
	 * handling of the Name Verification Check Command.
	 * 
	 * @param aEvent
	 *            The <code>EPPEvent</code> that is being handled
	 * @param aData
	 *            Any data that a Server needs to send to this
	 *            <code>NameVerificationHandler</code>
	 *
	 * @return EPPEventResponse The response that should be sent back to the
	 *         client.
	 */
	protected EPPEventResponse doNameVerificationCheck(EPPEvent aEvent,
			Object aData) {
		cat.debug("doNameVerificationCheck: enter");

		EPPNameVerificationCheckCmd theCommand = (EPPNameVerificationCheckCmd) aEvent
				.getMessage();

		EPPTransId theTransId = new EPPTransId(theCommand.getTransId(),
				"54321-XYZ");

		EPPNameVerificationCheckResp theResp = new EPPNameVerificationCheckResp(
				theTransId);

		for (String label : theCommand.getLabels()) {

			// ChinaVerificationCodeTst label?
			if (label.startsWith("cvct")) {
				theResp.addResult(new EPPNameVerificationCheckResult(label,
						true));
			}
			else {
				switch (checkResultCount % 3) {
				// Available
					case 0:
						theResp.addResult(new EPPNameVerificationCheckResult(
								label, true));
						break;
					// Not Available - Prohibited
					case 1:
						theResp.addResult(new EPPNameVerificationCheckResult(
								label, false, "In Prohibited Lists."));
						break;
					// Not Available - Restricted
					case 2:
						EPPNameVerificationCheckResult theCheckResult = new EPPNameVerificationCheckResult(
								label, false);
						theCheckResult.setRestricted(true);
						theResp.addResult(theCheckResult);
						break;
				}

				checkResultCount++;
			}

		}

		cat.debug("doNameVerificationCheck: exit");
		return new EPPEventResponse(theResp);
	}

	/**
	 * Override base handler <code>doNameVerificationInfo</code> method and add
	 * handling of the Name Verification Info Command.
	 * 
	 * @param aEvent
	 *            The <code>EPPEvent</code> that is being handled
	 * @param aData
	 *            Any data that a Server needs to send to this
	 *            <code>NameVerificationHandler</code>
	 *
	 * @return EPPEventResponse The response that should be sent back to the
	 *         client.
	 */
	protected EPPEventResponse doNameVerificationInfo(EPPEvent aEvent,
			Object aData) {
		cat.debug("doNameVerificationInfo: enter");
		EPPNameVerificationInfoResp theResp;

		EPPNameVerificationInfoCmd theCommand = (EPPNameVerificationInfoCmd) aEvent
				.getMessage();

		EPPTransId theTransId = new EPPTransId(theCommand.getTransId(),
				"54321-XYZ");

		if (theCommand.getInfoType() == EPPNameVerificationInfoCmd.Type.INPUT) {

			// RNV?
			if (theCommand.getCode().contains("rnv")) {
				theResp = new EPPNameVerificationInfoResp(
						theTransId,
						new EPPNameVerificationInfoInputResult(
								new EPPRealNameVerification(
										EPPRealNameVerification.Role.PERSON,
										"John Xie",
										"1234567890",
										EPPRealNameVerification.ProofType.POC,
										new EPPNameVerificationDocument(
												EPPNameVerificationDocument.FileType.JPG,
												"EABQRAQAAAAAAAAAAAAAAAAAAAAD")),
								new EPPAuthInfo("2fooBAR")));

			} // DNV
			else {
				theResp = new EPPNameVerificationInfoResp(theTransId,
						new EPPNameVerificationInfoInputResult(
								new EPPDomainNameVerification("example"),
								new EPPAuthInfo("2fooBAR")));

			}

		} // theCommand.getInfoType() ==
			// EPPNameVerificationInfoCmd.Type.SIGNED_CODE
		else {
			try {
				// RNV?
				if (theCommand.getCode().contains("rnv")) {
					theResp = new EPPNameVerificationInfoResp(theTransId,
							new EPPNameVerificationInfoSignedCodeResult(
									theCommand.getCode(), "real-name",
									this.createSignedCode(theCommand.getCode(),
											"real-name"),
									EPPNameVerificationStatus.COMPLIANT,
									new EPPAuthInfo("2fooBAR")));
				} // DNV
				else {
					theResp = new EPPNameVerificationInfoResp(theTransId,
							new EPPNameVerificationInfoSignedCodeResult(
									theCommand.getCode(), "domain",
									this.createSignedCode(theCommand.getCode(),
											"domain"),
									EPPNameVerificationStatus.COMPLIANT,
									new EPPAuthInfo("2fooBAR")));
				}
			}
			catch (EPPException ex) {
				cat.error("Error creating the verification code info response:"
						+ ex);
				EPPResult theResult = new EPPResult(EPPResult.COMMAND_FAILED);
				EPPResponse theResponse = new EPPResponse(theTransId, theResult);

				return new EPPEventResponse(theResponse);
			}

		}
		cat.debug("doNameVerificationInfo: exit");
		return new EPPEventResponse(theResp);
	}

	/**
	 * Override base handler <code>doNameVerificationCreate</code> method and
	 * add handling of the Name Verification Create Command.
	 * 
	 * @param aEvent
	 *            The <code>EPPEvent</code> that is being handled
	 * @param aData
	 *            Any data that a Server needs to send to this
	 *            <code>NameVerificationHandler</code>
	 *
	 * @return EPPEventResponse The response that should be sent back to the
	 *         client.
	 */
	protected EPPEventResponse doNameVerificationCreate(EPPEvent aEvent,
			Object aData) {
		cat.debug("doNameVerificationCreate: enter");

		EPPNameVerificationCreateResp theResp;

		EPPNameVerificationCreateCmd theCommand = (EPPNameVerificationCreateCmd) aEvent
				.getMessage();

		EPPTransId theTransId = new EPPTransId(theCommand.getTransId(),
				"54321-XYZ");

		// DNV?
		if (theCommand.hasDnv()) {

			cat.info("doNameVerificationCreate: Received DNV create with label = "
					+ theCommand.getDnv().getName()
					+ ", rnvCode = "
					+ theCommand.getDnv().getRnvCode());

			if (theCommand.getDnv().getName().equalsIgnoreCase("PENDING")) {
				String theCode = VSP_ID + "-" + System.currentTimeMillis();

				EPPNameVerificationPendActionMsg thePollMsg = new EPPNameVerificationPendActionMsg(
						theTransId,
						theCode,
						"domain",
						EPPNameVerificationStatus.COMPLIANT,
						"The object has passed verification, signed code was generated.",
						new Date(System.currentTimeMillis()));
				try {
					EPPPollQueueMgr.getInstance().put(null,
							NameVerificationPollHandler.KIND, thePollMsg, null);
				}
				catch (EPPPollQueueException ex) {
					cat.error("Error inserting poll queue message: " + ex);
					EPPResult theResult = new EPPResult(
							EPPResult.COMMAND_FAILED);
					EPPResponse theResponse = new EPPResponse(theTransId,
							theResult);

					return new EPPEventResponse(theResponse);
				}

				theResp = new EPPNameVerificationCreateResp(theTransId,
						new EPPNameVerificationCreatePending(theCode, "domain",
								EPPNameVerificationStatus.PENDING_COMPLIANT,
								new Date(System.currentTimeMillis())));
				theResp.setResult(EPPResult.SUCCESS_PENDING);
			}
			else if (theCommand.getDnv().getName().equalsIgnoreCase("FAILED")) {

				theResp = new EPPNameVerificationCreateResp(theTransId,
						new EPPNameVerificationCreateFailed(
								EPPNameVerificationStatus.NON_COMPLIANT,
								"The name of the object is not correct."));
				theResp.setResult(EPPResult.SUCCESS);

			}
			else if (theCommand.getDnv().getName()
					.equalsIgnoreCase("INVALID-TYPE")) {

				try {
					String theCode = VSP_ID + "-" + System.currentTimeMillis();
					theResp = new EPPNameVerificationCreateResp(theTransId,
							new EPPNameVerificationCreateSuccess(theCode,
									"invalid-type",
									EPPNameVerificationStatus.COMPLIANT,
									new Date(System.currentTimeMillis()),
									this.createSignedCode(theCode,
											"invalid-type")));
				}
				catch (EPPException ex) {
					cat.error("Error creating the DNV verification code create response:"
							+ ex);
					EPPResult theResult = new EPPResult(
							EPPResult.COMMAND_FAILED);
					EPPResponse theResponse = new EPPResponse(theTransId,
							theResult);

					return new EPPEventResponse(theResponse);
				}

			}
			else {
				try {
					String theCode = VSP_ID + "-" + System.currentTimeMillis();
					theResp = new EPPNameVerificationCreateResp(theTransId,
							new EPPNameVerificationCreateSuccess(theCode,
									"domain",
									EPPNameVerificationStatus.COMPLIANT,
									new Date(System.currentTimeMillis()),
									this.createSignedCode(theCode, "domain")));
				}
				catch (EPPException ex) {
					cat.error("Error creating the DNV verification code create response:"
							+ ex);
					EPPResult theResult = new EPPResult(
							EPPResult.COMMAND_FAILED);
					EPPResponse theResponse = new EPPResponse(theTransId,
							theResult);

					return new EPPEventResponse(theResponse);
				}
			}
		} // RNV
		else {
			cat.info("doNameVerificationCreate: Received RNV create with role = "
					+ theCommand.getRnv().getRole().toString()
					+ ", name = "
					+ theCommand.getRnv().getName()
					+ ", num = "
					+ theCommand.getRnv().getNum()
					+ ", proofType = "
					+ theCommand.getRnv().getProofType().toString());
			if (theCommand.getRnv().hasDocuments()) {
				for (EPPNameVerificationDocument doc : theCommand.getRnv()
						.getDocuments()) {
					cat.info("doNameVerificationCreate: doc type = "
							+ doc.getFileType().toString() + ", content = \""
							+ doc.getFileContent() + "\"");
				}
			}
			try {
				String theCode = VSP_ID + "-" + System.currentTimeMillis();
				theResp = new EPPNameVerificationCreateResp(theTransId,
						new EPPNameVerificationCreateSuccess(theCode,
								"real-name",
								EPPNameVerificationStatus.COMPLIANT, new Date(
										System.currentTimeMillis()),
								this.createSignedCode(theCode, "real-name")));
			}
			catch (EPPException ex) {
				cat.error("Error creating the RNV verification code create response:"
						+ ex);
				EPPResult theResult = new EPPResult(EPPResult.COMMAND_FAILED);
				EPPResponse theResponse = new EPPResponse(theTransId, theResult);

				return new EPPEventResponse(theResponse);
			}

		}

		cat.debug("doNameVerificationCreate: exit");
		return new EPPEventResponse(theResp);
	}

	/**
	 * Override base handler <code>doNameVerificationUpdate</code> method and
	 * add handling of the Name Verification Update Command.
	 * 
	 * @param aEvent
	 *            The <code>EPPEvent</code> that is being handled
	 * @param aData
	 *            Any data that a Server needs to send to this
	 *            <code>NameVerificationHandler</code>
	 *
	 * @return EPPEventResponse The response that should be sent back to the
	 *         client.
	 */
	protected EPPEventResponse doNameVerificationUpdate(EPPEvent aEvent,
			Object aData) {
		cat.debug("doNameVerificationUpdate: enter");

		EPPNameVerificationUpdateCmd theCommand = (EPPNameVerificationUpdateCmd) aEvent
				.getMessage();

		EPPTransId theTransId = new EPPTransId(theCommand.getTransId(),
				"54321-XYZ");
		cat.info("doNameVerificationUpdate: code = " + theCommand.getCode()
				+ ", authInfo = " + theCommand.getAuthInfo().getPassword());

		EPPResponse theResp = new EPPResponse(theTransId);

		cat.debug("doNameVerificationUpdate: exit");
		return new EPPEventResponse(theResp);
	}

}
