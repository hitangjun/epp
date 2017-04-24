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

package com.verisign.epp.codec.verificationcode;

//----------------------------------------------
//
// imports...
//
//----------------------------------------------

// JUNIT Imports
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.PKIXParameters;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import org.apache.commons.codec.binary.Base64;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.verisign.epp.codec.domain.EPPDomainCreateCmd;
import com.verisign.epp.codec.domain.EPPDomainInfoCmd;
import com.verisign.epp.codec.domain.EPPDomainInfoResp;
import com.verisign.epp.codec.domain.EPPDomainStatus;
import com.verisign.epp.codec.gen.EPPAuthInfo;
import com.verisign.epp.codec.gen.EPPCodecException;
import com.verisign.epp.codec.gen.EPPCodecTst;
import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeDecodeStats;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPFactory;
import com.verisign.epp.codec.gen.EPPTransId;
import com.verisign.epp.exception.EPPException;
import com.verisign.epp.util.TestThread;

/**
 * Is a unit test of the com.verisign.epp.codec.verificationcode package.
 */
public class EPPVerificationCodeTst extends TestCase {
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
	 * Trust store containing the verification trust anchor CA certificates.
	 */
	private static KeyStore trustStore;

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
	 * Concrete verification code validator that used to simulate verification
	 * of the verification code value and type using the trust store aliases.
	 */
	private static TrustAnchorVerificationCodeValidator verificationCodeValidator;

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
	 * Creates a new EPPVerificationCodeTst object.
	 *
	 * @param name
	 *            Test name
	 */
	public EPPVerificationCodeTst(String name) {
		super(name);
	}

	/**
	 * Test the {@link EPPVerificationCode} class.
	 */
	public void testVerificationCode() {
		EPPCodecTst.printStart("testVerificationCode");

		EPPVerificationCode theCode = new EPPVerificationCode("abc-123",
				"domain");

		Assert.assertEquals(EPPVerificationCode.UNDEFINED, theCode.getVspId());

		theCode = new EPPVerificationCode("1-abc123", "domain");

		Assert.assertEquals(1, theCode.getVspId());

		EPPCodecTst.printEnd("testVerificationCode");
	}

	/**
	 * Unit test of extending the domain create command with a single untyped
	 * verification code contained in the verification code extension.
	 */
	public void testDomainWithVerificationCode() {
		EPPCodecTst.printStart("testDomainWithVerificationCode");

		// Generic objects
		EPPTransId respTransId = new EPPTransId("ABC-12345", "54321-XYZ");
		EPPEncodeDecodeStats theStats;

		// -- Extend Domain Create Command
		EPPDomainCreateCmd theDomainCommand;
		theDomainCommand = new EPPDomainCreateCmd("ABC-12345", "domain.example",
				new EPPAuthInfo("2fooBAR"));

		EPPSignedCode signedCode = null;
		EPPEncodedSignedCodeValue encodedSignedCodeValue = null;

		// Create signed code and add extension
		try {
			signedCode = new EPPSignedCode("0-abc000", "domain");
			signedCode.sign(privateKey, certChain);

			System.out.println("Signed Code = " + signedCode);

			if (!signedCode.validate(publicKey)) {
				Assert.fail("Signed code validation error using public key");
			}

			if (!signedCode.validate(pkixParameters)) {
				Assert.fail("Signed code validation error using PKIX");
			}

			if (!verificationCodeValidator.validate(signedCode.getCode(),
					signedCode.getTrustAnchor())) {
				Assert.fail("Verification code validation error");
			}

			encodedSignedCodeValue = new EPPEncodedSignedCodeValue(signedCode);

			System.out.println("Encoded Signed Code = " + signedCode);

			// Signed Code is revoked?
			if (verificationCodeRevocationList
					.isRevoked(encodedSignedCodeValue)) {
				Assert.fail(
						"testDomainWithVerificationCode(): Signed code with code = "
								+ encodedSignedCodeValue.getCode()
								+ " is unexpectedly revoked.");

			}

			theDomainCommand.addExtension(
					new EPPEncodedSignedCode(encodedSignedCodeValue));
		}
		catch (EPPEncodeException e) {
			e.printStackTrace();
			Assert.fail("EPPEncodeException: " + e);
		}
		catch (EPPException e) {
			e.printStackTrace();
			Assert.fail("EPPException: " + e);
		}
		catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception: " + e);
		}

		theStats = EPPCodecTst.testEncodeDecode(theDomainCommand);
		System.out.println(theStats);

		if (!encodedSignedCodeValue.validate(pkixParameters)) {
			Assert.fail(
					"testDomainWithVerificationCode(): Signed code signature is NOT valid after command encode/decode");
		}
		try {
			if (!verificationCodeValidator.validate(
					encodedSignedCodeValue.getCode(),
					encodedSignedCodeValue.getTrustAnchor())) {
				Assert.fail(
						"testDomainWithVerificationCode(): Verification code validation error");
			}
		}
		catch (EPPException e) {
			e.printStackTrace();
			Assert.fail("Exception: " + e);
		}

		EPPCodecTst.printEnd("testDomainWithVerificationCode");
	}

	/**
	 * Test of the info extension with the cases:<br>
	 * <ol>
	 * <li>Empty info extension - Includes the default case of an empty
	 * extension element.
	 * <li>Info extension with profile - Include the extension with the profile
	 * attribute set
	 * </ol>
	 */
	public void testInfo() {
		EPPCodecTst.printStart("testInfo");

		// Generic objects
		EPPEncodeDecodeStats theStats;

		// -- Case 1: Empty info extension
		EPPDomainInfoCmd theCommand;
		theCommand = new EPPDomainInfoCmd("ABC-12345", "domain.example");
		theCommand.addExtension(new EPPVerificationCodeInfo());
		theStats = EPPCodecTst.testEncodeDecode(theCommand);
		System.out.println(theStats);

		// -- Case 2: info extension with profile
		theCommand = new EPPDomainInfoCmd("ABC-12345", "domain.example");
		theCommand.addExtension(new EPPVerificationCodeInfo("sample"));
		theStats = EPPCodecTst.testEncodeDecode(theCommand);
		System.out.println(theStats);

		EPPCodecTst.printEnd("testInfo");
	}

	/**
	 * Test of the info response extension with the cases:<br>
	 * <ol>
	 * <li>Status - Return just the notApplicable verification status</li>
	 * <li>Status and Profile - Return the verification status and the profile
	 * </li>
	 * <li>Status, Profile, and Set Codes from Sponsoring Client - Return the
	 * status, profile, and two verification codes from the sponsoring
	 * registrar.</li>
	 * <li>Status, Profile, and Set Codes from Non-Sponsoring Client - Return
	 * the status, profile, and two verification codes from the non-sponsoring
	 * registrar.</li>
	 * <li>Status, Profile, and Missing Codes from Non-Sponsoring Client -
	 * Return the status, profile, and two missing codes from the non-sponsoring
	 * registrar.</li>
	 * <li>Status, Profile, Missing Codes, and Set Codes from Non-Sponsoring
	 * Client - Return the status, profile, and one missing code, and one set
	 * code from the non-sponsoring registrar.</li>
	 * <li>Status, Profile, and Set Codes from Sponsoring Client with Codes Set
	 * from Another Profile - Return the status, profile, and two verification
	 * codes from the sponsoring registrar, and include the set codes for the
	 * notApplicable profile.</li>
	 * </ol>
	 */
	public void testInfoResponse() {
		EPPCodecTst.printStart("testInfoResponse");

		// Generic objects
		EPPEncodeDecodeStats theStats;
		Vector statuses = new Vector();

		// -- Case 1: Status
		EPPDomainInfoResp theResponse;
		theResponse = new EPPDomainInfoResp();
		theResponse.setName("domain1.example");
		theResponse.setTransId(new EPPTransId("ABC-12345", "54322-XYZ"));
		theResponse.setRoid("EXAMPLE1-REP");
		statuses = new Vector();
		statuses.add(new EPPDomainStatus(EPPDomainStatus.ELM_STATUS_OK));
		theResponse.setStatuses(statuses);
		theResponse.setCreatedDate(new Date());
		theResponse.setCreatedBy("ClientY");
		theResponse.setClientId("ClientX");
		theResponse.addExtension(new EPPVerificationCodeInfData(
				EPPVerificationCodeInfData.Status.NOT_APPLICABLE));
		theStats = EPPCodecTst.testEncodeDecode(theResponse);
		System.out.println(theStats);

		// -- Case 2: Status and Profile
		theResponse = new EPPDomainInfoResp();
		theResponse.setName("domain2.example");
		theResponse.setTransId(new EPPTransId("ABC-12345", "54322-XYZ"));
		theResponse.setRoid("EXAMPLE1-REP");
		statuses = new Vector();
		statuses.add(new EPPDomainStatus(EPPDomainStatus.ELM_STATUS_OK));
		theResponse.setStatuses(statuses);
		theResponse.setCreatedDate(new Date());
		theResponse.setCreatedBy("ClientY");
		theResponse.setClientId("ClientX");
		theResponse.addExtension(new EPPVerificationCodeInfData(
				EPPVerificationCodeInfData.Status.COMPLIANT,
				new EPPVerificationCodeProfile("sample",
						EPPVerificationCodeProfile.Status.COMPLIANT)));
		theStats = EPPCodecTst.testEncodeDecode(theResponse);
		System.out.println(theStats);

		// -- Case 3: Status, Profile, and Set Codes from Sponsoring Client
		theResponse = new EPPDomainInfoResp();
		theResponse.setName("domain3.example");
		theResponse.setTransId(new EPPTransId("ABC-12345", "54322-XYZ"));
		theResponse.setRoid("EXAMPLE1-REP");
		statuses = new Vector();
		statuses.add(new EPPDomainStatus(EPPDomainStatus.ELM_STATUS_OK));
		theResponse.setStatuses(statuses);
		theResponse.setCreatedDate(
				new GregorianCalendar(1999, 4, 3, 22, 0).getTime());
		theResponse.setCreatedBy("ClientY");
		theResponse.setClientId("ClientX");
		theResponse.setExpirationDate(
				new GregorianCalendar(2005, 4, 3, 22, 0).getTime());
		theResponse.setAuthInfo(new EPPAuthInfo("2fooBAR"));
		EPPVerificationCodeProfile theProfile = new EPPVerificationCodeProfile(
				"sample", EPPVerificationCodeProfile.Status.COMPLIANT);
		theProfile.addSetCode(new EPPVerificationCode("0-abc333", "domain",
				new GregorianCalendar(1999, 4, 3, 22, 0, 0).getTime()));
		theProfile.addSetCode(new EPPVerificationCode("0-abc444", "real-name",
				new GregorianCalendar(1999, 4, 3, 22, 0, 0).getTime()));

		EPPVerificationCodeInfData theExt = new EPPVerificationCodeInfData(
				EPPVerificationCodeInfData.Status.COMPLIANT, theProfile);
		theResponse.addExtension(theExt);
		theStats = EPPCodecTst.testEncodeDecode(theResponse);
		System.out.println(theStats);

		// -- Case 4: Status, Profile, and Set Codes from Non-Sponsoring Client
		theResponse = new EPPDomainInfoResp();
		theResponse.setName("domain4.example");
		theResponse.setTransId(new EPPTransId("ABC-12345", "54322-XYZ"));
		theResponse.setRoid("EXAMPLE1-REP");
		statuses = new Vector();
		statuses.add(new EPPDomainStatus(EPPDomainStatus.ELM_STATUS_OK));
		theResponse.setStatuses(statuses);
		theResponse.setCreatedDate(
				new GregorianCalendar(1999, 4, 3, 22, 0).getTime());
		theResponse.setCreatedBy("ClientY");
		theResponse.setClientId("ClientX");
		theResponse.setExpirationDate(
				new GregorianCalendar(2005, 4, 3, 22, 0).getTime());

		theProfile = new EPPVerificationCodeProfile("sample",
				EPPVerificationCodeProfile.Status.COMPLIANT);
		theProfile.addSetCode(new EPPVerificationCode(null, "domain",
				new GregorianCalendar(1999, 4, 3, 22, 0, 0).getTime()));
		theProfile.addSetCode(new EPPVerificationCode(null, "real-name",
				new GregorianCalendar(1999, 4, 3, 22, 0, 0).getTime()));

		theExt = new EPPVerificationCodeInfData(
				EPPVerificationCodeInfData.Status.COMPLIANT, theProfile);

		theResponse.addExtension(theExt);
		theStats = EPPCodecTst.testEncodeDecode(theResponse);
		System.out.println(theStats);

		// -- Case 5: Status, Profile, and Missing Codes from Non-Sponsoring
		// Client
		theResponse = new EPPDomainInfoResp();
		theResponse.setName("domain5.example");
		theResponse.setTransId(new EPPTransId("ABC-12345", "54322-XYZ"));
		theResponse.setRoid("EXAMPLE1-REP");
		theResponse.setCreatedDate(
				new GregorianCalendar(1999, 4, 3, 22, 0).getTime());
		statuses = new Vector();
		statuses.add(
				new EPPDomainStatus(EPPDomainStatus.ELM_STATUS_SERVER_HOLD));
		theResponse.setStatuses(statuses);
		theResponse.setCreatedBy("ClientY");
		theResponse.setClientId("ClientX");
		theResponse.setExpirationDate(
				new GregorianCalendar(2005, 4, 3, 22, 0).getTime());

		theProfile = new EPPVerificationCodeProfile("sample",
				EPPVerificationCodeProfile.Status.NON_COMPLIANT);
		theProfile.addMissingCode(new EPPVerificationCode("domain",
				new GregorianCalendar(1999, 4, 3, 22, 0).getTime()));
		theProfile.addMissingCode(new EPPVerificationCode("real-name",
				new GregorianCalendar(1999, 4, 3, 22, 0).getTime()));

		theExt = new EPPVerificationCodeInfData(
				EPPVerificationCodeInfData.Status.NON_COMPLIANT, theProfile);

		theResponse.addExtension(theExt);
		theStats = EPPCodecTst.testEncodeDecode(theResponse);
		System.out.println(theStats);

		// -- Case 6: Status, Profile, Missing Codes, and Set Codes from
		// Sponsoring Client
		theResponse = new EPPDomainInfoResp();
		theResponse.setName("domain6.example");
		theResponse.setTransId(new EPPTransId("ABC-12345", "54322-XYZ"));
		theResponse.setRoid("EXAMPLE1-REP");
		statuses = new Vector();
		statuses.add(new EPPDomainStatus(EPPDomainStatus.ELM_STATUS_OK));
		theResponse.setStatuses(statuses);
		theResponse.setCreatedDate(
				new GregorianCalendar(1999, 4, 3, 22, 0).getTime());
		theResponse.setCreatedBy("ClientY");
		theResponse.setClientId("ClientX");
		theResponse.setExpirationDate(
				new GregorianCalendar(2005, 4, 3, 22, 0).getTime());

		theProfile = new EPPVerificationCodeProfile("sample",
				EPPVerificationCodeProfile.Status.PENDING_COMPLIANCE);
		theProfile.addSetCode(new EPPVerificationCode("0-abc333", "domain",
				new GregorianCalendar(1999, 4, 3, 22, 0, 0).getTime()));
		theProfile.addMissingCode(new EPPVerificationCode("real-name",
				new GregorianCalendar(1999, 4, 3, 22, 0, 0).getTime()));

		theExt = new EPPVerificationCodeInfData(
				EPPVerificationCodeInfData.Status.PENDING_COMPLIANCE,
				theProfile);

		theResponse.addExtension(theExt);
		theStats = EPPCodecTst.testEncodeDecode(theResponse);
		System.out.println(theStats);

		// -- Case 7: Status with multiple profiles
		theResponse = new EPPDomainInfoResp();
		theResponse.setName("domain7.example");
		theResponse.setTransId(new EPPTransId("ABC-12345", "54322-XYZ"));
		theResponse.setRoid("EXAMPLE1-REP");
		statuses = new Vector();
		statuses.add(new EPPDomainStatus(EPPDomainStatus.ELM_STATUS_OK));
		theResponse.setStatuses(statuses);
		theResponse.setCreatedDate(
				new GregorianCalendar(1999, 4, 3, 22, 0).getTime());
		theResponse.setCreatedBy("ClientY");
		theResponse.setClientId("ClientX");
		theResponse.setExpirationDate(
				new GregorianCalendar(2005, 4, 3, 22, 0).getTime());

		theExt = new EPPVerificationCodeInfData(
				EPPVerificationCodeInfData.Status.PENDING_COMPLIANCE);

		theProfile = new EPPVerificationCodeProfile("sample1",
				EPPVerificationCodeProfile.Status.PENDING_COMPLIANCE);
		theProfile.addSetCode(new EPPVerificationCode("0-abc333", "domain",
				new GregorianCalendar(1999, 4, 3, 22, 0, 0).getTime()));
		theProfile.addMissingCode(new EPPVerificationCode("real-name",
				new GregorianCalendar(1999, 4, 3, 22, 0, 0).getTime()));
		theExt.addProfile(theProfile);

		theProfile = new EPPVerificationCodeProfile("sample2",
				EPPVerificationCodeProfile.Status.COMPLIANT);
		theProfile.addSetCode(new EPPVerificationCode("0-abc333", "domain",
				new GregorianCalendar(1999, 4, 3, 22, 0, 0).getTime()));
		theProfile.addSetCode(new EPPVerificationCode("0-abc444", "real-name",
				new GregorianCalendar(1999, 4, 3, 22, 0, 0).getTime()));
		theExt.addProfile(theProfile);

		theResponse.addExtension(theExt);
		theStats = EPPCodecTst.testEncodeDecode(theResponse);
		System.out.println(theStats);

		// -- Case 8: Status, Profile, and Set Codes from Sponsoring Client and
		// with Codes Set from Second Profile
		theResponse = new EPPDomainInfoResp();
		theResponse.setName("domain8.example");
		theResponse.setTransId(new EPPTransId("ABC-12345", "54322-XYZ"));
		theResponse.setRoid("EXAMPLE1-REP");
		statuses = new Vector();
		statuses.add(new EPPDomainStatus(EPPDomainStatus.ELM_STATUS_OK));
		theResponse.setStatuses(statuses);
		theResponse.setCreatedDate(
				new GregorianCalendar(1999, 4, 3, 22, 0).getTime());
		theResponse.setCreatedBy("ClientY");
		theResponse.setClientId("ClientX");
		theResponse.setExpirationDate(
				new GregorianCalendar(2005, 4, 3, 22, 0).getTime());
		theResponse.setAuthInfo(new EPPAuthInfo("2fooBAR"));
		theProfile = new EPPVerificationCodeProfile("sample",
				EPPVerificationCodeProfile.Status.COMPLIANT);
		theProfile.addSetCode(new EPPVerificationCode("0-abc333", "domain",
				new GregorianCalendar(1999, 4, 3, 22, 0, 0).getTime()));
		theProfile.addSetCode(new EPPVerificationCode("0-abc444", "real-name",
				new GregorianCalendar(1999, 4, 3, 22, 0, 0).getTime()));

		theExt = new EPPVerificationCodeInfData(
				EPPVerificationCodeInfData.Status.COMPLIANT, theProfile);

		// Add "sample2" notApplicable profile
		theProfile = new EPPVerificationCodeProfile("sample2",
				EPPVerificationCodeProfile.Status.NOT_APPLICABLE);
		theProfile.addSetCode(new EPPVerificationCode("2-abc555", "domain",
				new GregorianCalendar(1999, 4, 3, 22, 0, 0).getTime()));
		theExt.addProfile(theProfile);

		theResponse.addExtension(theExt);
		theStats = EPPCodecTst.testEncodeDecode(theResponse);
		System.out.println(theStats);

		EPPCodecTst.printEnd("testInfoResponse");
	}

	/**
	 * Unit test of extending the domain create command with multiple typed
	 * verification codes contained in the verification code extension.
	 */
	public void testDomainWithMultiVerificationCode() {
		EPPCodecTst.printStart("testDomainWithMultiVerificationCode");

		// Generic objects
		EPPEncodeDecodeStats theStats;

		// -- Extend Domain Create Command
		EPPDomainCreateCmd theDomainCommand;
		theDomainCommand = new EPPDomainCreateCmd("ABC-12345", "domain.example",
				new EPPAuthInfo("2fooBAR"));

		EPPEncodedSignedCode encodedSignedCode = new EPPEncodedSignedCode();
		EPPSignedCode signedCode = null;
		EPPEncodedSignedCodeValue encodedSignedCodeValue = null;

		// Create signed code and add extension
		try {
			// Domain verification code
			signedCode = new EPPSignedCode("0-abc111", "domain");
			signedCode.sign(privateKey, certChain);

			System.out.println("Signed Code = " + signedCode);

			if (!signedCode.validate(publicKey)) {
				Assert.fail("Signed code validation error using public key");
			}

			if (!signedCode.validate(pkixParameters)) {
				Assert.fail("Signed code validation error using PKIX");
			}
			if (!verificationCodeValidator.validate(signedCode.getCode(),
					signedCode.getTrustAnchor())) {
				Assert.fail("Verification code validation error");
			}

			encodedSignedCodeValue = new EPPEncodedSignedCodeValue(signedCode);

			System.out.println("Encoded Signed Code = " + signedCode);

			encodedSignedCode.addCode(encodedSignedCodeValue);

			// Registrant verification code
			signedCode = new EPPSignedCode("0-abc222", "real-name");
			signedCode.sign(privateKey, certChain);

			System.out.println("Signed Code = " + signedCode);

			if (!signedCode.validate(publicKey)) {
				Assert.fail("Signed code validation error using public key");
			}

			if (!signedCode.validate(pkixParameters)) {
				Assert.fail("Signed code validation error using PKIX");
			}
			if (!verificationCodeValidator.validate(signedCode.getCode(),
					signedCode.getTrustAnchor())) {
				Assert.fail("Verification code validation error");
			}

			encodedSignedCodeValue = new EPPEncodedSignedCodeValue(signedCode);

			System.out.println("Encoded Signed Code = " + signedCode);

			encodedSignedCode.addCode(encodedSignedCodeValue);

			theDomainCommand.addExtension(encodedSignedCode);
		}
		catch (EPPEncodeException e) {
			e.printStackTrace();
			Assert.fail("EPPEncodeException: " + e);
		}
		catch (EPPException e) {
			e.printStackTrace();
			Assert.fail("EPPException: " + e);
		}

		theStats = EPPCodecTst.testEncodeDecode(theDomainCommand);
		System.out.println(theStats);

		EPPCodecTst.printEnd("testDomainWithMultiVerificationCode");
	}

	/**
	 * Test different encoded signed codes that include:<br>
	 * <ol>
	 * <li>Domain typed code</li>
	 * <li>Test encoding and decoding the Base64 encoded signed code value (no
	 * wrapper &lt;verificationCode:code&gt; XML element).</li>
	 * <li>Real-name typed code</li>
	 * <li>Negative test - Invalid vsp-id</li>
	 * <li>Negative test - Invalid code type</li>
	 * </ol>
	 */
	public void testEncodedSignedCode() {
		EPPCodecTst.printStart("testEncodedSignedCode");

		try {
			// Encoded signed code with domain type
			EPPEncodedSignedCodeValue encodedSignedCode = new EPPEncodedSignedCodeValue(
					"0-abc000", "domain");
			encodedSignedCode.sign(privateKey, certChain);

			System.out.println("testEncodedSignedCode: Domain type '0-abc000' = "
					+ encodedSignedCode);

			if (!encodedSignedCode.validate(pkixParameters)) {
				Assert.fail("Encoded signed code validation error using PKIX");
			}
			if (!verificationCodeValidator.validate(encodedSignedCode.getCode(),
					encodedSignedCode.getTrustAnchor())) {
				Assert.fail("Verification code validation error");
			}

			// Test encoding and decoding the Base64 encoded signed code.
			// Use old API
			String theEncodedValue = encodedSignedCode.encodeValue();
			System.out.println(
					"testEncodedSignedCode: (old API) Base64 encoded signed code = "
							+ theEncodedValue);
			encodedSignedCode = new EPPEncodedSignedCodeValue();
			encodedSignedCode.decode(Base64.decodeBase64(theEncodedValue));
			if (!encodedSignedCode.validate(pkixParameters)) {
				Assert.fail("Encoded signed code validation error using PKIX");
			}
			
			// Use new API 
			theEncodedValue = encodedSignedCode.encodeValue();
			encodedSignedCode = new EPPEncodedSignedCodeValue();
			encodedSignedCode.decodeValue(theEncodedValue);
			System.out.println(
					"testEncodedSignedCode: (new API) Base64 encoded signed code = "
							+ theEncodedValue);
			if (!encodedSignedCode.validate(pkixParameters)) {
				Assert.fail("Encoded signed code validation error using PKIX");
			}
			
			// Use new API (no chunking)
			theEncodedValue = encodedSignedCode.encodeValue(false);
			encodedSignedCode = new EPPEncodedSignedCodeValue();
			encodedSignedCode.decodeValue(theEncodedValue);
			System.out.println(
					"testEncodedSignedCode: (new API) Base64 encoded signed code (no chunking) = "
							+ theEncodedValue);
			if (!encodedSignedCode.validate(pkixParameters)) {
				Assert.fail("Encoded signed code validation error using PKIX");
			}
			

			// Encoded signed code with real-name type
			encodedSignedCode = new EPPEncodedSignedCodeValue("0-rnvc111",
					"real-name");
			encodedSignedCode.sign(privateKey, certChain);

			System.out.println(
					"testEncodedSignedCode: Real-name type '0-rnvc111' ) = "
							+ encodedSignedCode);

			if (!encodedSignedCode.validate(pkixParameters)) {
				Assert.fail("Encoded signed code validation error using PKIX");
			}
			if (!verificationCodeValidator.validate(encodedSignedCode.getCode(),
					encodedSignedCode.getTrustAnchor())) {
				Assert.fail("Verification code validation error");
			}

			// Encoded signed code with domain type
			encodedSignedCode = new EPPEncodedSignedCodeValue("0-dnvc222",
					"domain");
			encodedSignedCode.sign(privateKey, certChain);

			System.out.println(
					"testEncodedSignedCode: Domain type '0-dnvc222' ) = "
							+ encodedSignedCode);

			if (!encodedSignedCode.validate(pkixParameters)) {
				Assert.fail("Encoded signed code validation error using PKIX");
			}
			if (!verificationCodeValidator.validate(encodedSignedCode.getCode(),
					encodedSignedCode.getTrustAnchor())) {
				Assert.fail("Verification code validation error");
			}
			
			
			// Negative test - Invalid vsp-id
			encodedSignedCode = new EPPEncodedSignedCodeValue("99-abc333",
					"domain");
			encodedSignedCode.sign(privateKey, certChain);

			System.out.println(
					"testEncodedSignedCode: Invalid vsp-id '99-abc333' ) = "
							+ encodedSignedCode);

			if (!encodedSignedCode.validate(pkixParameters)) {
				Assert.fail("Encoded signed code validation error using PKIX");
			}
			if (verificationCodeValidator.validate(encodedSignedCode.getCode(),
					encodedSignedCode.getTrustAnchor())) {
				Assert.fail(
						"Incorrectly validated an invalid vsp-id in verification code");
			}

			// Negative test - Invalid code type
			encodedSignedCode = new EPPEncodedSignedCodeValue("0-abc444",
					"invalid");
			encodedSignedCode.sign(privateKey, certChain);

			System.out.println(
					"testEncodedSignedCode: Invalid code type '0-abc444' ) = "
							+ encodedSignedCode);

			if (!encodedSignedCode.validate(pkixParameters)) {
				Assert.fail("Encoded signed code validation error using PKIX");
			}
			if (verificationCodeValidator.validate(encodedSignedCode.getCode(),
					encodedSignedCode.getTrustAnchor())) {
				Assert.fail(
						"Incorrectly validated an invalid code type in verification code");
			}

		}
		catch (EPPException e) {
			e.printStackTrace();
			Assert.fail("EPPException: " + e);
		}

		EPPCodecTst.printEnd("testEncodedSignedCode");
	}

	/**
	 * JUNIT <code>setUp</code> method.
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
	 * associated with <code>EPPVerificationCodeTst</code>.
	 *
	 * @return Test suite
	 */
	public static Test suite() {
		EPPCodecTst.initEnvironment();

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
				Assert.fail(
						"Error opening Verification Code Revocation List File: "
								+ e);
			}
			catch (EPPDecodeException e) {
				Assert.fail(
						"Error decoding Verification Code Revocation List File: "
								+ e);
			}

		}

		TestSuite suite = new TestSuite(EPPVerificationCodeTst.class);

		// iterations Property
		String numIterProp = System.getProperty("iterations");

		if (numIterProp != null) {
			numIterations = Integer.parseInt(numIterProp);
		}

		// Add the EPPNSProductExtFactory to the EPPCodec.
		try {
			EPPFactory.getInstance().addMapFactory(
					"com.verisign.epp.codec.host.EPPHostMapFactory");
			EPPFactory.getInstance().addMapFactory(
					"com.verisign.epp.codec.domain.EPPDomainMapFactory");
			EPPFactory.getInstance().addExtFactory(
					"com.verisign.epp.codec.verificationcode.EPPVerificationCodeExtFactory");
		}
		catch (EPPCodecException e) {
			Assert.fail("EPPCodecException adding factories to EPPCodec: " + e);
		}

		return suite;
	}

	// End EPPVerificationCodeTst.suite()

	/**
	 * Unit test main, which accepts the following system property options:<br>
	 * 
	 * <ul>
	 * <li>iterations Number of unit test iterations to run</li>
	 * <li>validate Turn XML validation on (<code>true</code>) or off (
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
						"EPPVerificationCodeTst Thread " + i,
						EPPVerificationCodeTst.suite());
				thread.start();
			}
		}
		else { // Single threaded mode.
			junit.textui.TestRunner.run(EPPVerificationCodeTst.suite());
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

		trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		FileInputStream trustStoreFile = new FileInputStream(aTrustStoreName);
		trustStore.load(trustStoreFile, null);

		PKIXParameters pkixParameters = new PKIXParameters(trustStore);
		pkixParameters.setRevocationEnabled(false);

		// Initialize the verification code validator
		verificationCodeValidator = new TrustAnchorVerificationCodeValidator(
				trustStore);

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
