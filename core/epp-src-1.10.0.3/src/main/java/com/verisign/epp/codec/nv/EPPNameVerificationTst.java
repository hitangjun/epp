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

package com.verisign.epp.codec.nv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.PKIXParameters;
import java.util.GregorianCalendar;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.verisign.epp.codec.gen.EPPAuthInfo;
import com.verisign.epp.codec.gen.EPPCodecException;
import com.verisign.epp.codec.gen.EPPCodecTst;
import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeDecodeStats;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPFactory;
import com.verisign.epp.codec.gen.EPPMsgQueue;
import com.verisign.epp.codec.gen.EPPResult;
import com.verisign.epp.codec.gen.EPPTransId;
import com.verisign.epp.codec.verificationcode.EPPEncodedSignedCodeValue;
import com.verisign.epp.codec.verificationcode.EPPSignedCode;
import com.verisign.epp.codec.verificationcode.VerificationCodeRevocationList;
import com.verisign.epp.exception.EPPException;
import com.verisign.epp.util.TestThread;

/**
 * Is a unit test of the com.verisign.epp.codec.idntable package.
 */
public class EPPNameVerificationTst extends TestCase {
	/**
	 * Number of unit test iterations to run. This is set in
	 * <code>EPPCodecTst.main</code>
	 */
	static private long numIterations = 1;

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
	 * Creates a new EPPNameVerificationTst object.
	 *
	 * @param name
	 *            Name of test
	 */
	public EPPNameVerificationTst(String name) {
		super(name);
	}

	/**
	 * Create a signed code from a code value.
	 * 
	 * 
	 * @param aCode
	 *            Code value to sign
	 * @param aType
	 *            Type of code
	 * 
	 * @return Encoded signed code
	 */
	private EPPEncodedSignedCodeValue createSignedCode(String aCode,
			String aType) {

		EPPSignedCode signedCode = null;
		EPPEncodedSignedCodeValue encodedSignedCodeValue = null;

		try {
			// Domain verification code
			signedCode = new EPPSignedCode(aCode, aType);
			signedCode.sign(privateKey, certChain);

			if (!signedCode.validate(publicKey)) {
				Assert.fail("Signed code validation error using public key");
			}

			if (!signedCode.validate(pkixParameters)) {
				Assert.fail("Signed code validation error using PKIX");
			}

			encodedSignedCodeValue = new EPPEncodedSignedCodeValue(signedCode);
		}
		catch (EPPEncodeException e) {
			e.printStackTrace();
			Assert.fail("EPPEncodeException: " + e);
		}
		catch (EPPException e) {
			e.printStackTrace();
			Assert.fail("EPPException: " + e);
		}

		return encodedSignedCodeValue;
	}

	/**
	 * Unit test of the Check Command by sending a check command with three
	 * domain labels.
	 */
	public void testCheckCommand() {
		EPPCodecTst.printStart("testCheckCommand");

		EPPEncodeDecodeStats theStats;
		
		EPPNameVerificationCheckCmd theCommand = new EPPNameVerificationCheckCmd(
				"ABC-12345");
		theCommand.addLabel("example1");
		theCommand.addLabel("example2");
		theCommand.addLabel("example3");

		theStats = EPPCodecTst.testEncodeDecode(theCommand);
		System.out.println(theStats);

		EPPCodecTst.printEnd("testCheckCommand");
	}

	/**
	 * Unit test of the Check Response by returning the result of checking three
	 * domain labels.
	 */
	public void testCheckResponse() {
		EPPCodecTst.printStart("testCheckResponse");

		EPPEncodeDecodeStats theStats;

		EPPTransId theTransId = new EPPTransId("ABC-12345", "54321-XYZ");

		EPPNameVerificationCheckResp theResp = new EPPNameVerificationCheckResp(
				theTransId);
		theResp.addResult(new EPPNameVerificationCheckResult("example1", true));
		theResp.addResult(new EPPNameVerificationCheckResult("example2", false,
				"In Prohibited Lists."));
		EPPNameVerificationCheckResult theCheckResult = new EPPNameVerificationCheckResult(
				"example3", false);
		theCheckResult.setRestricted(true);
		theResp.addResult(theCheckResult);

		theStats = EPPCodecTst.testEncodeDecode(theResp);
		System.out.println(theStats);

		EPPCodecTst.printEnd("testCheckResponse");
	}

	/**
	 * Unit test of the Info Command, with the following tests:
	 * <ol>
	 * <li>Info command for signed code.</li>
	 * <li>Info command for input data.</li>
	 * <li>Info command for signed code with authorization info.</li>
	 * </ol>
	 */
	public void testInfoCommand() {
		EPPCodecTst.printStart("testInfoCommand");

		EPPEncodeDecodeStats theStats;

		// -- Case 1: Info command for signed code
		EPPNameVerificationInfoCmd theCommand = new EPPNameVerificationInfoCmd(
				"ABC-12345", "1-abc123",
				EPPNameVerificationInfoCmd.Type.SIGNED_CODE);

		theStats = EPPCodecTst.testEncodeDecode(theCommand);
		System.out.println(theStats);

		// -- Case 2: Info command for input data
		theCommand = new EPPNameVerificationInfoCmd("ABC-12345", "1-abc123",
				EPPNameVerificationInfoCmd.Type.INPUT);

		theStats = EPPCodecTst.testEncodeDecode(theCommand);
		System.out.println(theStats);

		// -- Case 3: Info command for signed code with authorization info
		theCommand = new EPPNameVerificationInfoCmd("ABC-12345", "1-abc123",
				EPPNameVerificationInfoCmd.Type.SIGNED_CODE, new EPPAuthInfo(
						"2fooBAR"));

		theStats = EPPCodecTst.testEncodeDecode(theCommand);
		System.out.println(theStats);

		EPPCodecTst.printEnd("testInfoCommand");
	}

	/**
	 * Unit test of the Info Response, with the following tests:
	 * <ol>
	 * <li>Info response for DNV signed code</li>
	 * <li>Info response for DNV input</li>
	 * <li>Info response for RNV person input</li>
	 * <li>Info response for RNV org input</li>
	 * </ol>
	 */
	public void testInfoResponse() {
		EPPCodecTst.printStart("testInfoResponse");

		EPPEncodeDecodeStats theStats;

		EPPTransId theTransId = new EPPTransId("ABC-12345", "54321-XYZ");

		// -- Case 1: Info response for DNV signed code
		EPPEncodedSignedCodeValue signedCodeValue = createSignedCode("1-abc123",
				"domain");

		EPPNameVerificationInfoResp theResp = new EPPNameVerificationInfoResp(
				theTransId, new EPPNameVerificationInfoSignedCodeResult(
						"1-abc123", "domain", signedCodeValue,
						EPPNameVerificationStatus.COMPLIANT, new EPPAuthInfo(
								"2fooBAR")));

		theStats = EPPCodecTst.testEncodeDecode(theResp);
		System.out.println(theStats);

		// -- Case 2: Info response for DNV input
		theResp = new EPPNameVerificationInfoResp(theTransId,
				new EPPNameVerificationInfoInputResult(
						new EPPDomainNameVerification("example"),
						new EPPAuthInfo("2fooBAR")));

		theStats = EPPCodecTst.testEncodeDecode(theResp);
		System.out.println(theStats);

		// -- Case 3: Info response for RNV person input
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

		theStats = EPPCodecTst.testEncodeDecode(theResp);
		System.out.println(theStats);

		// -- Case 4: Info response for RNV org input
		theResp = new EPPNameVerificationInfoResp(
				theTransId,
				new EPPNameVerificationInfoInputResult(
						new EPPRealNameVerification(
								EPPRealNameVerification.Role.ORG,
								"John Xie",
								"1234567890",
								EPPRealNameVerification.ProofType.POE,
								new EPPNameVerificationDocument(
										EPPNameVerificationDocument.FileType.JPG,
										"EABQRAQAAAAAAAAAAAAAAAAAAAAD")),
						new EPPAuthInfo("2fooBAR")));

		theStats = EPPCodecTst.testEncodeDecode(theResp);
		System.out.println(theStats);

		EPPCodecTst.printEnd("testInfoResponse");
	}

	/**
	 * Unit test of the Create Command, with the following tests:
	 * <ol>
	 * <li>Create Domain Name Verification (DNV) object</li>
	 * <li>Create reserved Domain Name Verification (DNV) object</li>
	 * <li>Create Real Name Verification (RNV) object for person</li>
	 * <li>Create Real Name Verification (RNV) object for org</li>
	 * </ol>
	 */
	public void testCreateCommand() {
		EPPCodecTst.printStart("testCreateCommand");

		EPPEncodeDecodeStats theStats;

		// -- Case 1: Create Domain Name Verification (DNV) object
		EPPNameVerificationCreateCmd theCommand = new EPPNameVerificationCreateCmd(
				"ABC-12345", new EPPDomainNameVerification("example"),
				new EPPAuthInfo("2fooBAR"));

		theStats = EPPCodecTst.testEncodeDecode(theCommand);
		System.out.println(theStats);

		// -- Case 2: Create reserved Domain Name Verification (DNV) object
		theCommand = new EPPNameVerificationCreateCmd("ABC-12345",
				new EPPDomainNameVerification("example", "1-abc123"),
				new EPPAuthInfo("2fooBAR"));

		theStats = EPPCodecTst.testEncodeDecode(theCommand);
		System.out.println(theStats);

		// -- Case 3: Create Real Name Verification (RNV) object for person
		theCommand = new EPPNameVerificationCreateCmd("ABC-12345",
				new EPPRealNameVerification(
						EPPRealNameVerification.Role.PERSON, "John Xie",
						"1234567890", EPPRealNameVerification.ProofType.POE,
						new EPPNameVerificationDocument(
								EPPNameVerificationDocument.FileType.JPG,
								"EABQRAQAAAAAAAAAAAAAAAAAAAAD")),
				new EPPAuthInfo("2fooBAR"));

		theStats = EPPCodecTst.testEncodeDecode(theCommand);
		System.out.println(theStats);

		// -- Case 3: Create Real Name Verification (RNV) object for org
		theCommand = new EPPNameVerificationCreateCmd("ABC-12345",
				new EPPRealNameVerification(EPPRealNameVerification.Role.ORG,
						"John Xie", "1234567890",
						EPPRealNameVerification.ProofType.POE,
						new EPPNameVerificationDocument(
								EPPNameVerificationDocument.FileType.JPG,
								"EABQRAQAAAAAAAAAAAAAAAAAAAAD")),
				new EPPAuthInfo("2fooBAR"));

		theStats = EPPCodecTst.testEncodeDecode(theCommand);
		System.out.println(theStats);

		EPPCodecTst.printEnd("testCreateCommand");
	}

	/**
	 * Unit test of the Create Response, with the following tests:
	 * <ol>
	 * <li>Success create response with a signed code</li>
	 * <li>Failed create response</li>
	 * <li>Pending create response</li>
	 * </ol>
	 */
	public void testCreateResponse() {
		EPPCodecTst.printStart("testCreateResponse");

		EPPEncodeDecodeStats theStats;

		EPPTransId theTransId = new EPPTransId("ABC-12345", "54321-XYZ");

		// -- Case 1: Success create response with a signed code
		EPPEncodedSignedCodeValue signedCodeValue = createSignedCode("1-abc123",
				"domain");

		EPPNameVerificationCreateResp theResp = new EPPNameVerificationCreateResp(
				theTransId, new EPPNameVerificationCreateSuccess("1-abc123",
						"domain", EPPNameVerificationStatus.COMPLIANT,
						new GregorianCalendar(2015, 8, 17, 22, 0).getTime(),
						signedCodeValue));

		theStats = EPPCodecTst.testEncodeDecode(theResp);
		System.out.println(theStats);

		// -- Case 2: Failed create response
		theResp = new EPPNameVerificationCreateResp(theTransId,
				new EPPNameVerificationCreateFailed(
						EPPNameVerificationStatus.NON_COMPLIANT,
						"The name of the object is not correct."));
		theResp.setResult(EPPResult.PARAM_OUT_OF_RANGE);

		theStats = EPPCodecTst.testEncodeDecode(theResp);
		System.out.println(theStats);

		// -- Case 3: Pending create response
		theResp = new EPPNameVerificationCreateResp(theTransId,
				new EPPNameVerificationCreatePending("1-abc123", "domain", 
						EPPNameVerificationStatus.PENDING_COMPLIANT,
						new GregorianCalendar(2015, 9, 3, 22, 0).getTime()));
		theResp.setResult(EPPResult.SUCCESS_PENDING);

		theStats = EPPCodecTst.testEncodeDecode(theResp);
		System.out.println(theStats);

		EPPCodecTst.printEnd("testCreateResponse");
	}

	/**
	 * Unit test of the Update Command by sending an update command to change
	 * the authorization information.
	 */
	public void testUpdateCommand() {
		EPPCodecTst.printStart("testUpdateCommand");

		EPPEncodeDecodeStats theStats;

		EPPNameVerificationUpdateCmd theCommand = new EPPNameVerificationUpdateCmd(
				"ABC-12345", "1-abc123", new EPPAuthInfo("2BARfoo"));

		theStats = EPPCodecTst.testEncodeDecode(theCommand);
		System.out.println(theStats);

		EPPCodecTst.printEnd("testUpdateCommand");
	}

	/**
	 * Unit test of the pending action poll message.
	 */
	public void testPaPollResponse() {
		EPPCodecTst.printStart("testPaPollResponse");

		EPPEncodeDecodeStats theStats;

		EPPTransId theTransId = new EPPTransId("ABC-12345", "54321-XYZ");

		EPPNameVerificationPendActionMsg theResp = new EPPNameVerificationPendActionMsg(
				theTransId,
				"1-abc123",
				"domain",
				EPPNameVerificationStatus.COMPLIANT,
				"The object has passed verification, signed code was generated.",
				new GregorianCalendar(2015, 9, 4, 22, 0).getTime());

		theResp.setResult(EPPResult.SUCCESS_POLL_MSG);
		theResp.setMsgQueue(new EPPMsgQueue(new Long("5"), "12345",
				new GregorianCalendar(2015, 9, 4, 22, 1).getTime(),
				"Pending action completed successfully."));

		theStats = EPPCodecTst.testEncodeDecode(theResp);
		System.out.println(theStats);

		EPPCodecTst.printEnd("testPaPollResponse");
	}

	/**
	 * JUNIT <code>setUp</code> method, which sets the default client Id to
	 * "theRegistrar" and initializes the
	 * <code>EPPNameVerificationMapFactory</code> with the
	 * <code>EPPCodec</code>.
	 */
	protected void setUp() {
	}

	/**
	 * JUNIT <code>tearDown</code>, which currently does nothing.
	 */
	protected void tearDown() {
	}

	/**
	 * JUNIT <code>suite</code> static method, which returns the tests
	 * associated with <code>EPPNameVerificationTst</code>.
	 *
	 * @return Tests to run
	 */
	public static Test suite() {
		EPPCodecTst.initEnvironment();

		TestSuite suite = new TestSuite(EPPNameVerificationTst.class);

		// iterations Property
		String numIterProp = System.getProperty("iterations");

		if (numIterProp != null) {
			numIterations = Integer.parseInt(numIterProp);
		}

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

				System.out.println("Verification Code Revocation List = [\n"
						+ verificationCodeRevocationList + "]");
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

		// Add the EPPNameVerificationMapFactory to the EPPCodec.
		try {
			EPPFactory
					.getInstance()
					.addExtFactory(
							"com.verisign.epp.codec.verificationcode.EPPVerificationCodeExtFactory");
			EPPFactory
					.getInstance()
					.addMapFactory(
							"com.verisign.epp.codec.nv.EPPNameVerificationMapFactory");
		}
		catch (EPPCodecException e) {
			Assert.fail("EPPCodecException adding factories to EPPCodec: " + e);
		}

		return suite;
	}

	/**
	 * Unit test main, which accepts the following system property options:<br>
	 * 
	 * <ul>
	 * <li>
	 * iterations Number of unit test iterations to run</li>
	 * <li>
	 * validate Turn XML validation on (<code>true</code>) or off (
	 * <code>false</code>). If validate is not specified, validation will be
	 * off.</li>
	 * </ul>
	 *
	 * @param args
	 *            Program arguments
	 */
	public static void main(String[] args) {
		// Number of Threads
		int numThreads = 1;
		String threadsStr = System.getProperty("threads");

		if (threadsStr != null) {
			numThreads = Integer.parseInt(threadsStr);
		}

		// Run test suite in multiple threads?
		if (numThreads > 1) {
			// Spawn each thread passing in the Test Suite
			for (int i = 0; i < numThreads; i++) {
				TestThread thread = new TestThread(
						"EPPNameVerificationTst Thread " + i,
						EPPNameVerificationTst.suite());
				thread.start();
			}
		}
		else { // Single threaded mode.
			junit.textui.TestRunner.run(EPPNameVerificationTst.suite());
		}
	}

	/**
	 * Sets the number of iterations to run per test.
	 *
	 * @param aNumIterations
	 *            number of iterations to run per test
	 */
	public static void setNumIterations(long aNumIterations) {
		numIterations = aNumIterations;
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

}
