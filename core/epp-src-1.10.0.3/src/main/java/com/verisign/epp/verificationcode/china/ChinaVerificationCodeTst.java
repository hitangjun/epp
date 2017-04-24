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

 http://www.verisign.com/nds/naming/namestore/techdocs.html
 ***********************************************************/
package com.verisign.epp.verificationcode.china;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.tools.ant.util.FileUtils;

import com.verisign.epp.codec.changepoll.EPPChangeData;
import com.verisign.epp.codec.contact.EPPContactTransferResp;
import com.verisign.epp.codec.domain.EPPDomainCheckResp;
import com.verisign.epp.codec.domain.EPPDomainCheckResult;
import com.verisign.epp.codec.domain.EPPDomainCreateResp;
import com.verisign.epp.codec.domain.EPPDomainInfoResp;
import com.verisign.epp.codec.domain.EPPDomainPendActionMsg;
import com.verisign.epp.codec.domain.EPPDomainTransferResp;
import com.verisign.epp.codec.gen.EPPResponse;
import com.verisign.epp.codec.gen.EPPResult;
import com.verisign.epp.codec.lowbalancepoll.EPPLowBalancePollResponse;
import com.verisign.epp.codec.nv.EPPDomainNameVerification;
import com.verisign.epp.codec.nv.EPPNameVerificationCheckResp;
import com.verisign.epp.codec.nv.EPPNameVerificationCheckResult;
import com.verisign.epp.codec.nv.EPPNameVerificationCreateResp;
import com.verisign.epp.codec.nv.EPPNameVerificationCreateSuccess;
import com.verisign.epp.codec.nv.EPPRealNameVerification;
import com.verisign.epp.codec.rgppoll.EPPRgpPollResponse;
import com.verisign.epp.codec.verificationcode.EPPEncodedSignedCode;
import com.verisign.epp.codec.verificationcode.EPPEncodedSignedCodeValue;
import com.verisign.epp.codec.verificationcode.EPPVerificationCodeInfData;
import com.verisign.epp.codec.verificationcode.EPPVerificationCodeInfo;
import com.verisign.epp.exception.EPPException;
import com.verisign.epp.interfaces.EPPApplicationSingle;
import com.verisign.epp.interfaces.EPPCommandException;
import com.verisign.epp.interfaces.EPPDomain;
import com.verisign.epp.interfaces.EPPNameVerification;
import com.verisign.epp.interfaces.EPPSession;
import com.verisign.epp.namestore.interfaces.NSDomain;
import com.verisign.epp.pool.EPPSessionPool;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.InvalidateSessionException;
import com.verisign.epp.util.TestThread;
import com.verisign.epp.util.TestUtil;

import junit.extensions.TestSetup;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Execute and test different china verification end-to-end flows.
 * 
 * @see com.verisign.epp.namestore.interfaces.NSDomain
 * @see com.verisign.epp.interfaces.EPPNameVerification
 */
public class ChinaVerificationCodeTst extends TestCase {

	/**
	 * Handle to the Singleton EPP Application instance (
	 * <code>EPPApplicationSingle</code>)
	 */
	private static EPPApplicationSingle app = EPPApplicationSingle
			.getInstance();

	/** Name of configuration file to use for test (default = epp.config). */
	private static String configFileName = "epp.config";

	/** Logging category */
	private static final Logger cat = Logger.getLogger(
			ChinaVerificationCodeTst.class.getName(),
			EPPCatFactory.getInstance().getFactory());

	/** EPP Session pool associated with test */
	private static EPPSessionPool sessionPool = null;

	/**
	 * Name of Registry #1 session pool.
	 */
	private static final String POOL_REGISTRY_1 = "registry1";

	/**
	 * Name of Registry #2 session pool, which is used for transfers.
	 */
	private static final String POOL_REGISTRY_2 = "registry2";

	/**
	 * Name of VSP session pool.
	 */
	private static final String POOL_VSP = "vsp";

	/**
	 * Is the VSP session pool defined? If not, the DNVC and the RNVC will
	 * attempt to be read from the file dnvc.b64 and rnvc.b64, respectively.
	 */
	private static boolean vspPoolExists = true;

	/**
	 * Domain Name Verification Code (DNVC) read from the file dnvc.b64.
	 */
	private static EPPEncodedSignedCodeValue dnvc;

	/**
	 * Real Name Verification Code (DNVC) read from the file rnvc.b64.
	 */
	private static EPPEncodedSignedCodeValue rnvc;

	/**
	 * Random instance for the generation of unique objects (hosts, IP
	 * addresses, etc.).
	 */
	private Random rd = new Random(System.currentTimeMillis());

	/**
	 * Allocates an <code>ChinaVerificationCodeTst</code> with a logical name.
	 * The constructor will initialize the base class <code>TestCase</code> with
	 * the logical name.
	 *
	 * @param name
	 *            Logical name of the test
	 */
	public ChinaVerificationCodeTst(String name) {
		super(name);
	}

	/**
	 * Execute the China Normal Registration Operations Flow that includes the
	 * following steps:<br>
	 * <ol>
	 * <li>Check availability of domain name in Registry, where the domain must
	 * be available
	 * <li>Check availability (not restricted or prohibited) of domain label in
	 * VSP, where the domain label must be available
	 * <li>Create Domain Name Verification Code (DNVC) with VSP using the domain
	 * label
	 * <li>Create Domain Name in Registry with DNVC
	 * <li>Create Real Name Verification Code (RNVC) with VSP using test
	 * registrant data.
	 * <li>Update Domain Name in Registry with RNVC
	 * </ol>
	 */
	public void testNormalRegistrationOperations() {
		printStart("testNormalRegistrationOperations");

		EPPSession theRegistrySession = null;
		EPPSession theVspSession = null;
		EPPNameVerification theVsp = null;

		try {
			// --- Get sessions to the VSP and to the Registry

			// Initialize Registry Domain Interface
			theRegistrySession = this.borrowSession(POOL_REGISTRY_1);
			NSDomain theDomain = new NSDomain(theRegistrySession);

			System.out.println(
					"testUpfrontVerification: Got session to the Registry");

			// Initialize VSP Interface
			if (vspPoolExists) {
				theVspSession = this.borrowSession(POOL_VSP);
				theVsp = new EPPNameVerification(theVspSession);
				System.out.println(
						"testNormalRegistrationOperations: Got session to the Vsp");
			}

			// --- Generate unique domain label and domain name

			String theDomainName = this.makeDomainName("cvct-normal-reg-op-",
					".com");
			System.out.println("theDomainName = " + theDomainName);
			String theLabels[] = theDomainName.split("\\.");
			String theLabel = theLabels[0];
			String theTld = theLabels[1];

			EPPEncodedSignedCodeValue theDnvc = null;
			EPPEncodedSignedCodeValue theRnvc = null;

			if (!vspPoolExists) {
				theDnvc = dnvc;
				theRnvc = rnvc;
			}

			System.out.println(
					"testNormalRegistrationOperations: Generated unique domain label ("
							+ theLabel + ") and domain name (" + theDomainName
							+ ") for flow");

			// --- STEP 1 - Check availability of domain name in Registry

			try {
				theDomain.setTransId(this.makeClientTransId());

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(theTld);

				EPPDomainCheckResp theResponse = theDomain.sendCheck();

				Vector theResults = theResponse.getCheckResults();

				if (theResults.size() != 1) {
					Assert.fail(
							"Domain availability check result of 1 domain expected, but received "
									+ theResults.size());
				}

				EPPDomainCheckResult theResult = (EPPDomainCheckResult) theResults
						.get(0);

				if (!theResult.isAvailable()) {
					Assert.fail("Domain name " + theDomainName
							+ " is not available with reason = "
							+ theResult.getDomainReason());
				}

				System.out.println(
						"testNormalRegistrationOperations: Step 1 - Domain name "
								+ theDomainName + " is available");

			}
			catch (Exception ex) {
				TestUtil.handleException(theRegistrySession, ex);
			}

			// --- STEP 2 - Check availability (not restricted or prohibited) of
			// domain label in VSP
			if (vspPoolExists) {

				try {
					theVsp.setTransId(this.makeClientTransId());

					theVsp.addLabel(theLabel);

					EPPNameVerificationCheckResp theResponse = theVsp
							.sendCheck();

					List<EPPNameVerificationCheckResult> theResults = theResponse
							.getCheckResults();

					if (theResults.size() != 1) {
						Assert.fail(
								"Vsp label check result of 1 label expected, but received "
										+ theResults.size());
					}

					if (!theResults.get(0).isAvailable()) {
						Assert.fail("Label " + theLabel
								+ " is not available (not restricted or prohibited) with reason = "
								+ theResults.get(0).getReason());
					}

					System.out.println(
							"testNormalRegistrationOperations: Step 2 - Label "
									+ theLabel
									+ " is available (not restricted or prohibited)");
				}
				catch (Exception ex) {
					TestUtil.handleException(theVspSession, ex);
				}
			}
			else {
				System.out.println(
						"testNormalRegistrationOperations: Step 2 - Skipped due to no VSP pool");
			}

			// --- STEP 3 - Create Domain Name Verification Code (DNVC) with
			// VSP

			if (vspPoolExists) {
				try {
					theVsp.setTransId(this.makeClientTransId());

					theVsp.setDnv(new EPPDomainNameVerification(theLabel));

					theVsp.setAuthInfo("normalRegistrationOperations-3");

					EPPNameVerificationCreateResp theResponse = theVsp
							.sendCreate();

					if (!(theResponse
							.getCreateResult() instanceof EPPNameVerificationCreateSuccess)) {
						Assert.fail(
								"DNVC create was not successful with result = "
										+ theResponse.getCreateResult());
					}

					EPPNameVerificationCreateSuccess theSuccessResult = (EPPNameVerificationCreateSuccess) theResponse
							.getCreateResult();
					theDnvc = theSuccessResult.getSignedCode();
					System.out.println(
							"testNormalRegistrationOperations: Step 3 - DNVC successfully created, type = "
									+ theSuccessResult.getCodeType()
									+ ", code  = "
									+ theSuccessResult.getCode());
				}
				catch (Exception ex) {
					TestUtil.handleException(theVspSession, ex);
				}
			}
			else {
				System.out.println(
						"testNormalRegistrationOperations: Step 3 - Skipped due to no VSP pool");
			}

			// --- STEP 4 - Create Domain Name in Registry with DNVC.

			try {
				theDomain.setTransId(this.makeClientTransId());

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(theTld);

				theDomain.setAuthString("normalRegistrationOperations-4");

				// Add DNVC as extension to create
				EPPEncodedSignedCode theSignedCodeExt = new EPPEncodedSignedCode();
				theSignedCodeExt.addCode(theDnvc);
				theDomain.addExtension(theSignedCodeExt);

				EPPDomainCreateResp theResponse = theDomain.sendCreate();

				System.out.println(
						"testNormalRegistrationOperations: Step 4 - Domain name "
								+ theDomainName
								+ " successfully created with DNVC (type = "
								+ theDnvc.getCodeType() + ", code = "
								+ theDnvc.getCodeValue() + ")");
			}
			catch (Exception ex) {
				TestUtil.handleException(theRegistrySession, ex);
			}

			// --- STEP 5 - Create Real Name Verification Code (RNVC) with VSP
			if (vspPoolExists) {

				try {
					theVsp.setTransId(this.makeClientTransId());

					theVsp.setRnv(new EPPRealNameVerification(
							EPPRealNameVerification.Role.PERSON, "\u5f20\u7b56",
							"110108199002161234",
							EPPRealNameVerification.ProofType.POC));

					theVsp.setAuthInfo("normalRegistrationOperations-5");

					EPPNameVerificationCreateResp theResponse = theVsp
							.sendCreate();

					if (!(theResponse
							.getCreateResult() instanceof EPPNameVerificationCreateSuccess)) {
						Assert.fail(
								"RNVC create was not successful with result = "
										+ theResponse.getCreateResult());
					}

					EPPNameVerificationCreateSuccess theSuccessResult = (EPPNameVerificationCreateSuccess) theResponse
							.getCreateResult();
					theRnvc = theSuccessResult.getSignedCode();
					System.out.println(
							"testNormalRegistrationOperations: Step 5 - RNVC successfully created, type = "
									+ theSuccessResult.getCodeType()
									+ ", code  = "
									+ theSuccessResult.getCode());
				}
				catch (Exception ex) {
					TestUtil.handleException(theVspSession, ex);
				}
			}
			else {
				System.out.println(
						"testNormalRegistrationOperations: Step 5 - Skipped due to no VSP pool");
			}

			// --- STEP 6 - Update Domain Name in Registry with RNVC.

			try {
				theDomain.setTransId(this.makeClientTransId());

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(theTld);

				// Add DNVC as extension to create
				EPPEncodedSignedCode theSignedCodeExt = new EPPEncodedSignedCode();
				theSignedCodeExt.addCode(theRnvc);
				theDomain.addExtension(theSignedCodeExt);

				EPPResponse theResponse = theDomain.sendUpdate();

				System.out.println(
						"testNormalRegistrationOperations: Step 6 - Domain name "
								+ theDomainName
								+ " successfully update with RNVC (type = "
								+ theRnvc.getCodeType() + ", code = "
								+ theRnvc.getCodeValue() + ")");
			}
			catch (Exception ex) {
				TestUtil.handleException(theRegistrySession, ex);
			}

			// --- Get Domain Verification Status Information from the Registry

			try {
				theDomain.setTransId(this.makeClientTransId());

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(theTld);

				// Enable return of verification status information
				theDomain.addExtension(new EPPVerificationCodeInfo());

				EPPDomainInfoResp theResponse = theDomain.sendInfo();

				Assert.assertTrue(theResponse
						.hasExtension(EPPVerificationCodeInfData.class));

				EPPVerificationCodeInfData theVerificationExt = (EPPVerificationCodeInfData) theResponse
						.getExtension(EPPVerificationCodeInfData.class);

				Assert.assertTrue(theVerificationExt
						.getStatus() == EPPVerificationCodeInfData.Status.COMPLIANT);

				System.out.println(
						"testNormalRegistrationOperations: Domain name "
								+ theDomainName + " verification is compliant");
			}
			catch (Exception ex) {
				TestUtil.handleException(theRegistrySession, ex);
			}

			// --- Cleanup domain

			try {
				theDomain.setTransId(this.makeClientTransId());

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(theTld);

				theDomain.sendDelete();
			}
			catch (Exception ex) {
				TestUtil.handleException(theRegistrySession, ex);
			}

		}
		catch (InvalidateSessionException ex) {
			if (ex.getSession() == theRegistrySession) {
				this.invalidateSession(POOL_REGISTRY_1, theRegistrySession);
				theRegistrySession = null;
			}
			else if (ex.getSession() == theVspSession) {
				this.invalidateSession(POOL_VSP, theVspSession);
				theVspSession = null;
			}
			else {
				Assert.fail(
						"InvalidateSessionException received with no matching session");
			}
		}
		finally {
			if (theRegistrySession != null) {
				this.returnSession(POOL_REGISTRY_1, theRegistrySession);
			}
			if (theVspSession != null) {
				this.returnSession(POOL_VSP, theVspSession);
			}
		}

		printEnd("testNormalRegistrationOperations");
	}

	/**
	 * Execute the China Upfront Verification Flow that includes the following
	 * steps:<br>
	 * <ol>
	 * <li>Check availability of domain name in Registry, where the domain must
	 * be available
	 * <li>Check availability (not restricted or prohibited) of domain label in
	 * VSP, where the domain label must be available
	 * <li>Create Domain Name Verification Code (DNVC) with VSP using the domain
	 * label
	 * <li>Create Real Name Verification Code (RNVC) with VSP using test
	 * registrant data.
	 * <li>Create Domain Name in Registry with DNVC and RNVC
	 * </ol>
	 */
	public void testUpfrontVerification() {
		printStart("testUpfrontVerification");

		EPPSession theRegistrySession = null;
		EPPSession theVspSession = null;
		EPPNameVerification theVsp = null;

		try {
			// --- Get sessions to the VSP and to the Registry

			// Initialize Registry Domain Interface
			theRegistrySession = this.borrowSession(POOL_REGISTRY_1);
			NSDomain theDomain = new NSDomain(theRegistrySession);

			System.out.println(
					"testUpfrontVerification: Got session to the Registry");

			// Initialize VSP Interface
			if (vspPoolExists) {
				theVspSession = this.borrowSession(POOL_VSP);
				theVsp = new EPPNameVerification(theVspSession);
				System.out.println(
						"testUpfrontVerification: Got session to the Vsp");
			}

			// --- Generate unique domain label and domain name

			String theDomainName = this.makeDomainName("cvct-upfront-ver-",
					".com");
			System.out.println("theDomainName = " + theDomainName);
			String theLabels[] = theDomainName.split("\\.");
			String theLabel = theLabels[0];
			String theTld = theLabels[1];

			EPPEncodedSignedCodeValue theDnvc = null;
			EPPEncodedSignedCodeValue theRnvc = null;

			if (!vspPoolExists) {
				theDnvc = dnvc;
				theRnvc = rnvc;
			}

			System.out.println(
					"testUpfrontVerification: Generated unique domain label ("
							+ theLabel + ") and domain name (" + theDomainName
							+ ") for flow");

			// --- STEP 1 - Check availability of domain name in Registry

			try {
				theDomain.setTransId(this.makeClientTransId());

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(theTld);

				EPPDomainCheckResp theResponse = theDomain.sendCheck();

				Vector theResults = theResponse.getCheckResults();

				if (theResults.size() != 1) {
					Assert.fail(
							"Domain availability check result of 1 domain expected, but received "
									+ theResults.size());
				}

				EPPDomainCheckResult theResult = (EPPDomainCheckResult) theResults
						.get(0);

				if (!theResult.isAvailable()) {
					Assert.fail("Domain name " + theDomainName
							+ " is not available with reason = "
							+ theResult.getDomainReason());
				}

				System.out.println(
						"testUpfrontVerification: Step 1 - Domain name "
								+ theDomainName + " is available");

			}
			catch (Exception ex) {
				TestUtil.handleException(theRegistrySession, ex);
			}

			// --- STEP 2 - Check availability (not restricted or prohibited) of
			// domain label in VSP

			if (vspPoolExists) {
				try {
					theVsp.setTransId(this.makeClientTransId());

					theVsp.addLabel(theLabel);

					EPPNameVerificationCheckResp theResponse = theVsp
							.sendCheck();

					List<EPPNameVerificationCheckResult> theResults = theResponse
							.getCheckResults();

					if (theResults.size() != 1) {
						Assert.fail(
								"Vsp label check result of 1 label expected, but received "
										+ theResults.size());
					}

					if (!theResults.get(0).isAvailable()) {
						Assert.fail("Label " + theLabel
								+ " is not available (not restricted or prohibited) with reason = "
								+ theResults.get(0).getReason());
					}

					System.out
							.println("testUpfrontVerification: Step 2 - Label "
									+ theLabel
									+ " is available (not restricted or prohibited)");
				}
				catch (Exception ex) {
					TestUtil.handleException(theVspSession, ex);
				}
			}
			else {
				System.out.println(
						"testUpfrontVerification: Step 2 - Skipped due to no VSP pool");
			}

			// --- STEP 3 - Create Domain Name Verification Code (DNVC) with VSP

			if (vspPoolExists) {
				try {
					theVsp.setTransId(this.makeClientTransId());

					theVsp.setDnv(new EPPDomainNameVerification(theLabel));

					theVsp.setAuthInfo("upfrontVerification-5");

					EPPNameVerificationCreateResp theResponse = theVsp
							.sendCreate();

					if (!(theResponse
							.getCreateResult() instanceof EPPNameVerificationCreateSuccess)) {
						Assert.fail(
								"DNVC create was not successful with result = "
										+ theResponse.getCreateResult());
					}

					EPPNameVerificationCreateSuccess theSuccessResult = (EPPNameVerificationCreateSuccess) theResponse
							.getCreateResult();
					theDnvc = theSuccessResult.getSignedCode();
					System.out.println(
							"testUpfrontVerification: Step 3 - DNVC successfully created, type = "
									+ theSuccessResult.getCodeType()
									+ ", code  = "
									+ theSuccessResult.getCode());
				}
				catch (Exception ex) {
					TestUtil.handleException(theVspSession, ex);
				}
			}
			else {
				System.out.println(
						"testUpfrontVerification: Step 3 - Skipped due to no VSP pool");
			}

			// --- STEP 4 - Create Real Name Verification Code (RNVC) with VSP

			if (vspPoolExists) {
				try {
					theVsp.setTransId(this.makeClientTransId());

					theVsp.setRnv(new EPPRealNameVerification(
							EPPRealNameVerification.Role.PERSON, "\u5f20\u7b56",
							"110108199002161234",
							EPPRealNameVerification.ProofType.POC));

					theVsp.setAuthInfo("upfrontVerification-6");

					EPPNameVerificationCreateResp theResponse = theVsp
							.sendCreate();

					if (!(theResponse
							.getCreateResult() instanceof EPPNameVerificationCreateSuccess)) {
						Assert.fail(
								"RNVC create was not successful with result = "
										+ theResponse.getCreateResult());
					}

					EPPNameVerificationCreateSuccess theSuccessResult = (EPPNameVerificationCreateSuccess) theResponse
							.getCreateResult();
					theRnvc = theSuccessResult.getSignedCode();
					System.out.println(
							"testUpfrontVerification: Step 4 - RNVC successfully created, type = "
									+ theSuccessResult.getCodeType()
									+ ", code  = "
									+ theSuccessResult.getCode());
				}
				catch (Exception ex) {
					TestUtil.handleException(theVspSession, ex);
				}
			}
			else {
				System.out.println(
						"testUpfrontVerification: Step 4 - Skipped due to no VSP pool");
			}

			// --- STEP 5 - Create Domain Name in Registry with DNVC and RNVC.

			try {
				theDomain.setTransId(this.makeClientTransId());

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(theTld);

				theDomain.setAuthString("testUpfrontVerification-7");

				// Add DNVC and RNVC as extension to create
				EPPEncodedSignedCode theSignedCodeExt = new EPPEncodedSignedCode();
				theSignedCodeExt.addCode(theDnvc);
				theSignedCodeExt.addCode(theRnvc);
				theDomain.addExtension(theSignedCodeExt);

				EPPDomainCreateResp theResponse = theDomain.sendCreate();

				System.out.println(
						"testUpfrontVerification: Step 5 - Domain name "
								+ theDomainName
								+ " successfully created with DNVC (type = "
								+ theDnvc.getCodeType() + ", code = "
								+ theDnvc.getCodeValue() + ") and RNVC (type = "
								+ theRnvc.getCodeType() + ", code = "
								+ theRnvc.getCodeValue());
			}
			catch (Exception ex) {
				TestUtil.handleException(theRegistrySession, ex);
			}

			// --- Get Domain Verification Status Information from the Registry

			try {
				theDomain.setTransId(this.makeClientTransId());

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(theTld);

				// Enable return of verification status information
				theDomain.addExtension(new EPPVerificationCodeInfo());

				EPPDomainInfoResp theResponse = theDomain.sendInfo();

				Assert.assertTrue(theResponse
						.hasExtension(EPPVerificationCodeInfData.class));

				EPPVerificationCodeInfData theVerificationExt = (EPPVerificationCodeInfData) theResponse
						.getExtension(EPPVerificationCodeInfData.class);

				Assert.assertTrue(theVerificationExt
						.getStatus() == EPPVerificationCodeInfData.Status.COMPLIANT);

				System.out.println("testUpfrontVerification: Domain name "
						+ theDomainName + " verification is compliant");

			}
			catch (Exception ex) {
				TestUtil.handleException(theRegistrySession, ex);
			}

			// --- Cleanup domain

			try {
				theDomain.setTransId(this.makeClientTransId());

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(theTld);

				theDomain.sendDelete();
			}
			catch (Exception ex) {
				TestUtil.handleException(theRegistrySession, ex);
			}

		}
		catch (InvalidateSessionException ex) {
			if (ex.getSession() == theRegistrySession) {
				this.invalidateSession(POOL_REGISTRY_1, theRegistrySession);
				theRegistrySession = null;
			}
			else if (ex.getSession() == theVspSession) {
				this.invalidateSession(POOL_VSP, theVspSession);
				theVspSession = null;
			}
			else {
				Assert.fail(
						"InvalidateSessionException received with no matching session");
			}
		}
		finally {
			if (theRegistrySession != null) {
				this.returnSession(POOL_REGISTRY_1, theRegistrySession);
			}
			if (theVspSession != null) {
				this.returnSession(POOL_VSP, theVspSession);
			}
		}

		printEnd("testUpfrontVerification");
	}

	/**
	 * Execute the China Expedient Registration Flow that includes the following
	 * steps:<br>
	 * <ol>
	 * <li>Check availability of domain name in Registry, where the domain must
	 * be available
	 * <li>Create Domain Name in Registry with no codes
	 * <li>Check availability (not restricted or prohibited) of domain label in
	 * VSP, where the domain label must be available
	 * <li>Create Domain Name Verification Code (DNVC) with VSP using the domain
	 * label
	 * <li>Create Real Name Verification Code (RNVC) with VSP using test
	 * registrant data.
	 * <li>Update Domain Name in Registry with DNVC and RNVC
	 * </ol>
	 */
	public void testExpedientRegistration() {
		printStart("testExpedientRegistration");

		EPPSession theRegistrySession = null;
		EPPSession theVspSession = null;
		EPPNameVerification theVsp = null;

		try {
			// --- Get sessions to the VSP and to the Registry

			// Initialize Registry Domain Interface
			theRegistrySession = this.borrowSession(POOL_REGISTRY_1);
			NSDomain theDomain = new NSDomain(theRegistrySession);

			System.out.println(
					"testExpedientRegistration: Got session to the Registry");

			// Initialize VSP Interface
			if (vspPoolExists) {
				theVspSession = this.borrowSession(POOL_VSP);
				theVsp = new EPPNameVerification(theVspSession);
				System.out.println(
						"testExpedientRegistration: Got session to the Vsp");
			}

			// --- Generate unique domain label and domain name

			String theDomainName = this.makeDomainName("cvct-expedient-reg-",
					".com");
			System.out.println("theDomainName = " + theDomainName);
			String theLabels[] = theDomainName.split("\\.");
			String theLabel = theLabels[0];
			String theTld = theLabels[1];

			EPPEncodedSignedCodeValue theDnvc = null;
			EPPEncodedSignedCodeValue theRnvc = null;

			if (!vspPoolExists) {
				theDnvc = dnvc;
				theRnvc = rnvc;
			}

			System.out.println(
					"testExpedientRegistration: Generated unique domain label ("
							+ theLabel + ") and domain name (" + theDomainName
							+ ") for flow");

			// --- STEP 1 - Check availability of domain name in Registry

			try {
				theDomain.setTransId(this.makeClientTransId());

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(theTld);

				EPPDomainCheckResp theResponse = theDomain.sendCheck();

				Vector theResults = theResponse.getCheckResults();

				if (theResults.size() != 1) {
					Assert.fail(
							"Domain availability check result of 1 domain expected, but received "
									+ theResults.size());
				}

				EPPDomainCheckResult theResult = (EPPDomainCheckResult) theResults
						.get(0);

				if (!theResult.isAvailable()) {
					Assert.fail("Domain name " + theDomainName
							+ " is not available with reason = "
							+ theResult.getDomainReason());
				}

				System.out.println(
						"testExpedientRegistration: Step 1 - Domain name "
								+ theDomainName + " is available");

			}
			catch (Exception ex) {
				TestUtil.handleException(theRegistrySession, ex);
			}

			// --- STEP 2 - Create Domain Name in Registry with no codes.

			try {
				theDomain.setTransId(this.makeClientTransId());

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(theTld);

				theDomain.setAuthString("expedientRegistration-2");

				EPPDomainCreateResp theResponse = theDomain.sendCreate();

				System.out.println(
						"testExpedientRegistration: Step 2 - Domain name "
								+ theDomainName
								+ " successfully created with no codes");
			}
			catch (Exception ex) {
				TestUtil.handleException(theRegistrySession, ex);
			}

			// --- STEP 3 - Check availability (not restricted or prohibited) of
			// domain label in VSP
			if (vspPoolExists) {

				try {
					theVsp.setTransId(this.makeClientTransId());

					theVsp.addLabel(theLabel);

					EPPNameVerificationCheckResp theResponse = theVsp
							.sendCheck();

					List<EPPNameVerificationCheckResult> theResults = theResponse
							.getCheckResults();

					if (theResults.size() != 1) {
						Assert.fail(
								"Vsp label check result of 1 label expected, but received "
										+ theResults.size());
					}

					if (!theResults.get(0).isAvailable()) {
						Assert.fail("Label " + theLabel
								+ " is not available (not restricted or prohibited) with reason = "
								+ theResults.get(0).getReason());
					}

					System.out.println(
							"testExpedientRegistration: Step 3 - Label "
									+ theLabel
									+ " is available (not restricted or prohibited)");
				}
				catch (Exception ex) {
					TestUtil.handleException(theVspSession, ex);
				}
			}
			else {
				System.out.println(
						"testExpedientRegistration: Step 3 - Skipped due to no VSP pool");
			}

			// --- STEP 4 - Create Domain Name Verification Code (DNVC) with VSP

			if (vspPoolExists) {
				try {
					theVsp.setTransId(this.makeClientTransId());

					theVsp.setDnv(new EPPDomainNameVerification(theLabel));

					theVsp.setAuthInfo("expedientRegistration-4");

					EPPNameVerificationCreateResp theResponse = theVsp
							.sendCreate();

					if (!(theResponse
							.getCreateResult() instanceof EPPNameVerificationCreateSuccess)) {
						Assert.fail(
								"DNVC create was not successful with result = "
										+ theResponse.getCreateResult());
					}

					EPPNameVerificationCreateSuccess theSuccessResult = (EPPNameVerificationCreateSuccess) theResponse
							.getCreateResult();
					theDnvc = theSuccessResult.getSignedCode();
					System.out.println(
							"testExpedientRegistration: Step 4 - DNVC successfully created, type = "
									+ theSuccessResult.getCodeType()
									+ ", code  = "
									+ theSuccessResult.getCode());
				}
				catch (Exception ex) {
					TestUtil.handleException(theVspSession, ex);
				}
			}
			else {
				System.out.println(
						"testExpedientRegistration: Step 4 - Skipped due to no VSP pool");
			}

			// --- STEP 5 - Create Real Name Verification Code (RNVC) with VSP

			if (vspPoolExists) {
				try {
					theVsp.setTransId(this.makeClientTransId());

					theVsp.setRnv(new EPPRealNameVerification(
							EPPRealNameVerification.Role.PERSON, "\u5f20\u7b56",
							"110108199002161234",
							EPPRealNameVerification.ProofType.POC));

					theVsp.setAuthInfo("expedientRegistration-5");

					EPPNameVerificationCreateResp theResponse = theVsp
							.sendCreate();

					if (!(theResponse
							.getCreateResult() instanceof EPPNameVerificationCreateSuccess)) {
						Assert.fail(
								"RNVC create was not successful with result = "
										+ theResponse.getCreateResult());
					}

					EPPNameVerificationCreateSuccess theSuccessResult = (EPPNameVerificationCreateSuccess) theResponse
							.getCreateResult();
					theRnvc = theSuccessResult.getSignedCode();
					System.out.println(
							"testExpedientRegistration: Step 5 - RNVC successfully created, type = "
									+ theSuccessResult.getCodeType()
									+ ", code  = "
									+ theSuccessResult.getCode());
				}
				catch (Exception ex) {
					TestUtil.handleException(theVspSession, ex);
				}
			}
			else {
				System.out.println(
						"testExpedientRegistration: Step 5 - Skipped due to no VSP pool");
			}

			// --- STEP 6 - Update Domain Name in Registry with DNVC and RNVC.

			try {
				theDomain.setTransId(this.makeClientTransId());

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(theTld);

				// Add DNVC as extension to create
				EPPEncodedSignedCode theSignedCodeExt = new EPPEncodedSignedCode();
				theSignedCodeExt.addCode(theDnvc);
				theSignedCodeExt.addCode(theRnvc);
				theDomain.addExtension(theSignedCodeExt);

				EPPResponse theResponse = theDomain.sendUpdate();

				System.out.println(
						"testExpedientRegistration: Step 6 - Domain name "
								+ theDomainName
								+ " successfully update with DNVC (type = "
								+ theDnvc.getCodeType() + ", code = "
								+ theDnvc.getCodeValue()
								+ ") and with RNVC (type = "
								+ theRnvc.getCodeType() + ", code = "
								+ theRnvc.getCodeValue() + ")");
			}
			catch (Exception ex) {
				TestUtil.handleException(theRegistrySession, ex);
			}

			// --- Get Domain Verification Status Information from the Registry

			try {
				theDomain.setTransId(this.makeClientTransId());

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(theTld);

				// Enable return of verification status information
				theDomain.addExtension(new EPPVerificationCodeInfo());

				EPPDomainInfoResp theResponse = theDomain.sendInfo();

				Assert.assertTrue(theResponse
						.hasExtension(EPPVerificationCodeInfData.class));

				EPPVerificationCodeInfData theVerificationExt = (EPPVerificationCodeInfData) theResponse
						.getExtension(EPPVerificationCodeInfData.class);

				Assert.assertTrue(theVerificationExt
						.getStatus() == EPPVerificationCodeInfData.Status.COMPLIANT);

				System.out.println("testExpedientRegistration: Domain name "
						+ theDomainName + " verification is compliant");

			}
			catch (Exception ex) {
				TestUtil.handleException(theRegistrySession, ex);
			}

			// --- Cleanup domain

			try {
				theDomain.setTransId(this.makeClientTransId());

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(theTld);

				theDomain.sendDelete();
			}
			catch (Exception ex) {
				TestUtil.handleException(theRegistrySession, ex);
			}

		}
		catch (InvalidateSessionException ex) {
			if (ex.getSession() == theRegistrySession) {
				this.invalidateSession(POOL_REGISTRY_1, theRegistrySession);
				theRegistrySession = null;
			}
			else if (ex.getSession() == theVspSession) {
				this.invalidateSession(POOL_VSP, theVspSession);
				theVspSession = null;
			}
			else {
				Assert.fail(
						"InvalidateSessionException received with no matching session");
			}
		}
		finally {
			if (theRegistrySession != null) {
				this.returnSession(POOL_REGISTRY_1, theRegistrySession);
			}
			if (theVspSession != null) {
				this.returnSession(POOL_VSP, theVspSession);
			}
		}

		printEnd("testExpedientRegistration");
	}

	/**
	 * Execute the China Delayed Real-Name Verification Flow that includes the
	 * following steps:<br>
	 * <ol>
	 * <li>Check availability of domain name in Registry, where the domain must
	 * be available
	 * <li>Check availability (not restricted or prohibited) of domain label in
	 * VSP, where the domain label must be available
	 * <li>Create Domain Name Verification Code (DNVC) with VSP using the domain
	 * label
	 * <li>Create Domain Name in Registry with DNVC
	 * <li>After Real-Name Compliance Grace Period, Poll Request to get the
	 * Change Poll Message.
	 * <li>Create Real Name Verification Code (RNVC) with VSP using test
	 * registrant data.
	 * <li>Update Domain Name in Registry with RNVC
	 * </ol>
	 * 
	 * Steps 1-4 are handled in a separate execution of
	 * testNormalRegistrationOperations, that will register the domain name with
	 * the DNVC. Steps 5 - 7 are executed after the Real-Name Compliance Grace
	 * Period (e.g. 5 days) that will process the Change Poll Message, do the
	 * Real Name Verification, and pass the Real Name Verification Code (RNVC)
	 * in a domain update.
	 */
	public void testDelayedRealNameVerification() {
		printStart("testDelayedRealNameVerification");

		EPPSession theRegistrySession = null;
		EPPSession theVspSession = null;
		EPPNameVerification theVsp = null;

		try {
			// --- Get sessions to the VSP and to the Registry

			// Initialize Registry Domain Interface
			theRegistrySession = this.borrowSession(POOL_REGISTRY_1);
			NSDomain theDomain = new NSDomain(theRegistrySession);

			System.out.println(
					"testDelayedRealNameVerification: Got session to the Registry");

			// Determine if the registration or verification steps should be
			// executed.
			EPPDomainInfoResp theChangePollMsg = null;
			try {
				theChangePollMsg = findChangePoll(theRegistrySession,
						"cvct-delayed-real-name");
			}
			catch (Exception ex) {
				TestUtil.handleException(theRegistrySession, ex);
			}

			// Initialize VSP Interface
			if (vspPoolExists) {
				theVspSession = this.borrowSession(POOL_VSP);
				theVsp = new EPPNameVerification(theVspSession);
				System.out.println(
						"testDelayedRealNameVerification: Got session to the Vsp");
			}

			// Execute the registration steps?
			if (theChangePollMsg == null) {
				// --- Generate unique domain label and domain name

				String theDomainName = this
						.makeDomainName("cvct-delayed-real-name-", ".com");
				System.out.println("theDomainName = " + theDomainName);
				String theLabels[] = theDomainName.split("\\.");
				String theLabel = theLabels[0];
				String theTld = theLabels[1];

				EPPEncodedSignedCodeValue theDnvc = null;

				if (!vspPoolExists) {
					theDnvc = dnvc;
				}

				System.out.println(
						"testDelayedRealNameVerification: Generated unique domain label ("
								+ theLabel + ") and domain name ("
								+ theDomainName + ") for flow");

				// --- STEP 1 - Check availability of domain name in Registry

				try {
					theDomain.setTransId(this.makeClientTransId());

					theDomain.addDomainName(theDomainName);
					theDomain.setSubProductID(theTld);

					EPPDomainCheckResp theResponse = theDomain.sendCheck();

					Vector theResults = theResponse.getCheckResults();

					if (theResults.size() != 1) {
						Assert.fail(
								"Domain availability check result of 1 domain expected, but received "
										+ theResults.size());
					}

					EPPDomainCheckResult theResult = (EPPDomainCheckResult) theResults
							.get(0);

					if (!theResult.isAvailable()) {
						Assert.fail("Domain name " + theDomainName
								+ " is not available with reason = "
								+ theResult.getDomainReason());
					}

					System.out.println(
							"testDelayedRealNameVerification: Step 1 - Domain name "
									+ theDomainName + " is available");

				}
				catch (Exception ex) {
					TestUtil.handleException(theRegistrySession, ex);
				}

				// --- STEP 2 - Check availability (not restricted or
				// prohibited) of
				// domain label in VSP

				if (vspPoolExists) {
					try {
						theVsp.setTransId(this.makeClientTransId());

						theVsp.addLabel(theLabel);

						EPPNameVerificationCheckResp theResponse = theVsp
								.sendCheck();

						List<EPPNameVerificationCheckResult> theResults = theResponse
								.getCheckResults();

						if (theResults.size() != 1) {
							Assert.fail(
									"Vsp label check result of 1 label expected, but received "
											+ theResults.size());
						}

						if (!theResults.get(0).isAvailable()) {
							Assert.fail("Label " + theLabel
									+ " is not available (not restricted or prohibited) with reason = "
									+ theResults.get(0).getReason());
						}

						System.out.println(
								"testDelayedRealNameVerification: Step 2 - Label "
										+ theLabel
										+ " is available (not restricted or prohibited)");
					}
					catch (Exception ex) {
						TestUtil.handleException(theVspSession, ex);
					}
				}
				else {
					System.out.println(
							"testDelayedRealNameVerification: Step 2 - Skipped due to no VSP pool");
				}

				// --- STEP 3 - Create Domain Name Verification Code (DNVC) with
				// VSP

				if (vspPoolExists) {

					try {
						theVsp.setTransId(this.makeClientTransId());

						theVsp.setDnv(new EPPDomainNameVerification(theLabel));

						theVsp.setAuthInfo("delayedRealNameVerification-3");

						EPPNameVerificationCreateResp theResponse = theVsp
								.sendCreate();

						if (!(theResponse
								.getCreateResult() instanceof EPPNameVerificationCreateSuccess)) {
							Assert.fail(
									"DNVC create was not successful with result = "
											+ theResponse.getCreateResult());
						}

						EPPNameVerificationCreateSuccess theSuccessResult = (EPPNameVerificationCreateSuccess) theResponse
								.getCreateResult();
						theDnvc = theSuccessResult.getSignedCode();
						System.out.println(
								"testDelayedRealNameVerification: Step 3 - DNVC successfully created, type = "
										+ theSuccessResult.getCodeType()
										+ ", code  = "
										+ theSuccessResult.getCode());
					}
					catch (Exception ex) {
						TestUtil.handleException(theVspSession, ex);
					}
				}
				else {
					System.out.println(
							"testDelayedRealNameVerification: Step 3 - Skipped due to no VSP pool");
				}

				// --- STEP 4 - Create Domain Name in Registry with DNVC.

				try {
					theDomain.setTransId(this.makeClientTransId());

					theDomain.addDomainName(theDomainName);
					theDomain.setSubProductID(theTld);

					theDomain.setAuthString("delayedRealNameVerification-4");

					// Add DNVC as extension to create
					EPPEncodedSignedCode theSignedCodeExt = new EPPEncodedSignedCode();
					theSignedCodeExt.addCode(theDnvc);
					theDomain.addExtension(theSignedCodeExt);

					EPPDomainCreateResp theResponse = theDomain.sendCreate();

					System.out.println(
							"testDelayedRealNameVerification: Step 4 - Domain name "
									+ theDomainName
									+ " successfully created with DNVC (type = "
									+ theDnvc.getCodeType() + ", code = "
									+ theDnvc.getCodeValue() + ")");
				}
				catch (Exception ex) {
					TestUtil.handleException(theRegistrySession, ex);
				}

				// --- Get Domain Verification Status Information from the
				// Registry

				try {
					theDomain.setTransId(this.makeClientTransId());

					theDomain.addDomainName(theDomainName);
					theDomain.setSubProductID(theTld);

					// Enable return of verification status information.
					// Set the profile explicitly to trigger the stub server to
					// return pendingCompliance.
					theDomain.addExtension(new EPPVerificationCodeInfo());

					EPPDomainInfoResp theResponse = theDomain.sendInfo();

					Assert.assertTrue(theResponse
							.hasExtension(EPPVerificationCodeInfData.class));

					EPPVerificationCodeInfData theVerificationExt = (EPPVerificationCodeInfData) theResponse
							.getExtension(EPPVerificationCodeInfData.class);

					Assert.assertTrue(theVerificationExt
							.getStatus() == EPPVerificationCodeInfData.Status.PENDING_COMPLIANCE);

					// Assert there is a profile set
					Assert.assertTrue(theVerificationExt.hasProfile());

					// Assert there are missing codes
					Assert.assertTrue(
							theVerificationExt.getProfile().hasMissingCodes());

					// Assert the missing code is a real-name code
					Assert.assertTrue(
							theVerificationExt.getProfile().getMissingCodes()
									.get(0).getType().equals("real-name"));

					// Get due date for RNVC

					System.out.println(
							"testDelayedRealNameVerification: Domain name "
									+ theDomainName
									+ " is pendingCompliance with RNVC due "
									+ theVerificationExt.getProfile()
											.getMissingCodes().get(0)
											.getDueDate());
				}
				catch (Exception ex) {
					TestUtil.handleException(theRegistrySession, ex);
				}
			}
			else { // Execute the verification steps

				String theDomainName = theChangePollMsg.getName();
				String theLabels[] = theDomainName.split("\\.");
				String theTld = theLabels[1];

				EPPEncodedSignedCodeValue theRnvc = null;

				if (!vspPoolExists) {
					theRnvc = rnvc;
				}

				// --- STEP 5 - Get Change Poll Message
				System.out.println(
						"testDelayedRealNameVerification: Step 5 - Got Change Poll Message for domain "
								+ theDomainName);

				// --- STEP 6 - Create Real Name Verification Code (RNVC) with
				// VSP

				if (vspPoolExists) {
					try {
						theVsp.setTransId(this.makeClientTransId());

						theVsp.setRnv(new EPPRealNameVerification(
								EPPRealNameVerification.Role.PERSON,
								"\u5f20\u7b56", "110108199002161234",
								EPPRealNameVerification.ProofType.POC));

						theVsp.setAuthInfo("delayedRealNameVerification-6");

						EPPNameVerificationCreateResp theResponse = theVsp
								.sendCreate();

						if (!(theResponse
								.getCreateResult() instanceof EPPNameVerificationCreateSuccess)) {
							Assert.fail(
									"RNVC create was not successful with result = "
											+ theResponse.getCreateResult());
						}

						EPPNameVerificationCreateSuccess theSuccessResult = (EPPNameVerificationCreateSuccess) theResponse
								.getCreateResult();
						theRnvc = theSuccessResult.getSignedCode();
						System.out.println(
								"testDelayedRealNameVerification: Step 6 - RNVC successfully created, type = "
										+ theSuccessResult.getCodeType()
										+ ", code  = "
										+ theSuccessResult.getCode());
					}
					catch (Exception ex) {
						TestUtil.handleException(theVspSession, ex);
					}
				}
				else {
					System.out.println(
							"testDelayedRealNameVerification: Step 6 - Skipped due to no VSP pool");
				}

				// --- STEP 7 - Update Domain Name in Registry with RNVC.

				try {
					theDomain.setTransId(this.makeClientTransId());

					theDomain.addDomainName(theDomainName);
					theDomain.setSubProductID(theTld);

					// Add DNVC as extension to create
					EPPEncodedSignedCode theSignedCodeExt = new EPPEncodedSignedCode();
					theSignedCodeExt.addCode(theRnvc);
					theDomain.addExtension(theSignedCodeExt);

					EPPResponse theResponse = theDomain.sendUpdate();

					System.out.println(
							"testDelayedRealNameVerification: Step 7 - Domain name "
									+ theDomainName
									+ " successfully update with RNVC (type = "
									+ theRnvc.getCodeType() + ", code = "
									+ theRnvc.getCodeValue() + ")");
				}
				catch (Exception ex) {
					TestUtil.handleException(theRegistrySession, ex);
				}

				// --- Get Domain Verification Status Information from the
				// Registry

				try {
					theDomain.setTransId(this.makeClientTransId());

					theDomain.addDomainName(theDomainName);
					theDomain.setSubProductID(theTld);

					// Enable return of verification status information
					theDomain
							.addExtension(new EPPVerificationCodeInfo("china"));

					EPPDomainInfoResp theResponse = theDomain.sendInfo();

					Assert.assertTrue(theResponse
							.hasExtension(EPPVerificationCodeInfData.class));

					EPPVerificationCodeInfData theVerificationExt = (EPPVerificationCodeInfData) theResponse
							.getExtension(EPPVerificationCodeInfData.class);

					Assert.assertTrue(theVerificationExt
							.getStatus() == EPPVerificationCodeInfData.Status.COMPLIANT);

					System.out.println(
							"testDelayedRealNameVerification: Domain name "
									+ theDomainName
									+ " verification is compliant");
				}
				catch (Exception ex) {
					TestUtil.handleException(theRegistrySession, ex);
				}

				// --- Cleanup domain

				try {
					theDomain.setTransId(this.makeClientTransId());

					theDomain.addDomainName(theDomainName);
					theDomain.setSubProductID(theTld);

					theDomain.sendDelete();
				}
				catch (Exception ex) {
					TestUtil.handleException(theRegistrySession, ex);
				}
			}

		}
		catch (InvalidateSessionException ex) {
			if (ex.getSession() == theRegistrySession) {
				this.invalidateSession(POOL_REGISTRY_1, theRegistrySession);
				theRegistrySession = null;
			}
			else if (ex.getSession() == theVspSession) {
				this.invalidateSession(POOL_VSP, theVspSession);
				theVspSession = null;
			}
			else {
				Assert.fail(
						"InvalidateSessionException received with no matching session");
			}
		}
		finally {
			if (theRegistrySession != null) {
				this.returnSession(POOL_REGISTRY_1, theRegistrySession);
			}
			if (theVspSession != null) {
				this.returnSession(POOL_VSP, theVspSession);
			}
		}

		printEnd("testDelayedRealNameVerification");
	}

	/**
	 * Execute the China Delayed Domain Verification Flow that includes the
	 * following steps:<br>
	 * <ol>
	 * <li>Check availability of domain name in Registry, where the domain must
	 * be available
	 * <li>Create Real Name Verification Code (RNVC) with VSP using test
	 * registrant data.
	 * <li>Create Domain Name in Registry with RNVC
	 * <li>Check availability (not restricted or prohibited) of domain label in
	 * VSP, where the domain label must be available
	 * <li>Create Domain Name Verification Code (DNVC) with VSP using the domain
	 * label
	 * <li>Update Domain Name in Registry with DNVC
	 * </ol>
	 */
	public void testDelayedDomainVerification() {
		printStart("testDelayedDomainVerification");

		EPPSession theRegistrySession = null;
		EPPSession theVspSession = null;
		EPPNameVerification theVsp = null;

		try {
			// --- Get sessions to the VSP and to the Registry

			// Initialize Registry Domain Interface
			theRegistrySession = this.borrowSession(POOL_REGISTRY_1);
			NSDomain theDomain = new NSDomain(theRegistrySession);

			System.out.println(
					"testDelayedDomainVerification: Got session to the Registry");

			// Initialize VSP Interface
			if (vspPoolExists) {
				theVspSession = this.borrowSession(POOL_VSP);
				theVsp = new EPPNameVerification(theVspSession);
				System.out.println(
						"testDelayedDomainVerification: Got session to the Vsp");
			}

			// --- Generate unique domain label and domain name

			String theDomainName = this
					.makeDomainName("cvct-delayed-domain-ver-", ".com");
			System.out.println("theDomainName = " + theDomainName);
			String theLabels[] = theDomainName.split("\\.");
			String theLabel = theLabels[0];
			String theTld = theLabels[1];

			EPPEncodedSignedCodeValue theDnvc = null;
			EPPEncodedSignedCodeValue theRnvc = null;

			if (!vspPoolExists) {
				theDnvc = dnvc;
				theRnvc = rnvc;
			}

			System.out.println(
					"testDelayedDomainVerification: Generated unique domain label ("
							+ theLabel + ") and domain name (" + theDomainName
							+ ") for flow");

			// --- STEP 1 - Check availability of domain name in Registry

			try {
				theDomain.setTransId(this.makeClientTransId());

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(theTld);

				EPPDomainCheckResp theResponse = theDomain.sendCheck();

				Vector theResults = theResponse.getCheckResults();

				if (theResults.size() != 1) {
					Assert.fail(
							"Domain availability check result of 1 domain expected, but received "
									+ theResults.size());
				}

				EPPDomainCheckResult theResult = (EPPDomainCheckResult) theResults
						.get(0);

				if (!theResult.isAvailable()) {
					Assert.fail("Domain name " + theDomainName
							+ " is not available with reason = "
							+ theResult.getDomainReason());
				}

				System.out.println(
						"testDelayedDomainVerification: Step 1 - Domain name "
								+ theDomainName + " is available");

			}
			catch (Exception ex) {
				TestUtil.handleException(theRegistrySession, ex);
			}

			// --- STEP 2 - Create Real Name Verification Code (RNVC) with VSP

			if (vspPoolExists) {
				try {
					theVsp.setTransId(this.makeClientTransId());

					theVsp.setRnv(new EPPRealNameVerification(
							EPPRealNameVerification.Role.PERSON, "\u5f20\u7b56",
							"110108199002161234",
							EPPRealNameVerification.ProofType.POC));

					theVsp.setAuthInfo("delayedDomainVerification-2");

					EPPNameVerificationCreateResp theResponse = theVsp
							.sendCreate();

					if (!(theResponse
							.getCreateResult() instanceof EPPNameVerificationCreateSuccess)) {
						Assert.fail(
								"RNVC create was not successful with result = "
										+ theResponse.getCreateResult());
					}

					EPPNameVerificationCreateSuccess theSuccessResult = (EPPNameVerificationCreateSuccess) theResponse
							.getCreateResult();
					theRnvc = theSuccessResult.getSignedCode();
					System.out.println(
							"testDelayedDomainVerification: Step 2 - RNVC successfully created, type = "
									+ theSuccessResult.getCodeType()
									+ ", code  = "
									+ theSuccessResult.getCode());
				}
				catch (Exception ex) {
					TestUtil.handleException(theVspSession, ex);
				}
			}
			else {
				System.out.println(
						"testDelayedDomainVerification: Step 2 - Skipped due to no VSP pool");
			}

			// --- STEP 3 - Create Domain Name in Registry with RNVC.

			try {
				theDomain.setTransId(this.makeClientTransId());

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(theTld);

				theDomain.setAuthString("delayedDomainVerification-3");

				// Add RNVC as extension to create
				EPPEncodedSignedCode theSignedCodeExt = new EPPEncodedSignedCode();
				theSignedCodeExt.addCode(theRnvc);
				theDomain.addExtension(theSignedCodeExt);

				EPPDomainCreateResp theResponse = theDomain.sendCreate();

				System.out.println(
						"testDelayedDomainVerification: Step 3 - Domain name "
								+ theDomainName
								+ " successfully created with RNVC (type = "
								+ theRnvc.getCodeType() + ", code = "
								+ theRnvc.getCodeValue() + ")");
			}
			catch (Exception ex) {
				TestUtil.handleException(theRegistrySession, ex);
			}

			// --- STEP 4 - Check availability (not restricted or prohibited) of
			// domain label in VSP

			if (vspPoolExists) {
				try {
					theVsp.setTransId(this.makeClientTransId());

					theVsp.addLabel(theLabel);

					EPPNameVerificationCheckResp theResponse = theVsp
							.sendCheck();

					List<EPPNameVerificationCheckResult> theResults = theResponse
							.getCheckResults();

					if (theResults.size() != 1) {
						Assert.fail(
								"Vsp label check result of 1 label expected, but received "
										+ theResults.size());
					}

					if (!theResults.get(0).isAvailable()) {
						Assert.fail("Label " + theLabel
								+ " is not available (not restricted or prohibited) with reason = "
								+ theResults.get(0).getReason());
					}

					System.out.println(
							"testDelayedDomainVerification: Step 4 - Label "
									+ theLabel
									+ " is available (not restricted or prohibited)");
				}
				catch (Exception ex) {
					TestUtil.handleException(theVspSession, ex);
				}
			}
			else {
				System.out.println(
						"testDelayedDomainVerification: Step 4 - Skipped due to no VSP pool");
			}

			// --- STEP 5 - Create Domain Name Verification Code (DNVC) with VSP

			if (vspPoolExists) {
				try {
					theVsp.setTransId(this.makeClientTransId());

					theVsp.setDnv(new EPPDomainNameVerification(theLabel));

					theVsp.setAuthInfo("delayedDomainVerification-5");

					EPPNameVerificationCreateResp theResponse = theVsp
							.sendCreate();

					if (!(theResponse
							.getCreateResult() instanceof EPPNameVerificationCreateSuccess)) {
						Assert.fail(
								"DNVC create was not successful with result = "
										+ theResponse.getCreateResult());
					}

					EPPNameVerificationCreateSuccess theSuccessResult = (EPPNameVerificationCreateSuccess) theResponse
							.getCreateResult();
					theDnvc = theSuccessResult.getSignedCode();
					System.out.println(
							"testDelayedDomainVerification: Step 5 - DNVC successfully created, type = "
									+ theSuccessResult.getCodeType()
									+ ", code  = "
									+ theSuccessResult.getCode());
				}
				catch (Exception ex) {
					TestUtil.handleException(theVspSession, ex);
				}
			}
			else {
				System.out.println(
						"testDelayedDomainVerification: Step 5 - Skipped due to no VSP pool");
			}

			// --- STEP 6 - Update Domain Name in Registry with DNVC.

			try {
				theDomain.setTransId(this.makeClientTransId());

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(theTld);

				// Add DNVC as extension to create
				EPPEncodedSignedCode theSignedCodeExt = new EPPEncodedSignedCode();
				theSignedCodeExt.addCode(theDnvc);
				theDomain.addExtension(theSignedCodeExt);

				EPPResponse theResponse = theDomain.sendUpdate();

				System.out.println(
						"testDelayedDomainVerification: Step 6 - Domain name "
								+ theDomainName
								+ " successfully update with DNVC (type = "
								+ theDnvc.getCodeType() + ", code = "
								+ theDnvc.getCodeValue() + ")");
			}
			catch (Exception ex) {
				TestUtil.handleException(theRegistrySession, ex);
			}

			// --- Get Domain Verification Status Information from the Registry

			try {
				theDomain.setTransId(this.makeClientTransId());

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(theTld);

				// Enable return of verification status information
				theDomain.addExtension(new EPPVerificationCodeInfo());

				EPPDomainInfoResp theResponse = theDomain.sendInfo();

				Assert.assertTrue(theResponse
						.hasExtension(EPPVerificationCodeInfData.class));

				EPPVerificationCodeInfData theVerificationExt = (EPPVerificationCodeInfData) theResponse
						.getExtension(EPPVerificationCodeInfData.class);

				Assert.assertTrue(theVerificationExt
						.getStatus() == EPPVerificationCodeInfData.Status.COMPLIANT);

				System.out.println("testDelayedDomainVerification: Domain name "
						+ theDomainName + " verification is compliant");
			}
			catch (Exception ex) {
				TestUtil.handleException(theRegistrySession, ex);
			}

			// --- Cleanup domain

			try {
				theDomain.setTransId(this.makeClientTransId());

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(theTld);

				theDomain.sendDelete();
			}
			catch (Exception ex) {
				TestUtil.handleException(theRegistrySession, ex);
			}

		}
		catch (InvalidateSessionException ex) {
			if (ex.getSession() == theRegistrySession) {
				this.invalidateSession(POOL_REGISTRY_1, theRegistrySession);
				theRegistrySession = null;
			}
			else if (ex.getSession() == theVspSession) {
				this.invalidateSession(POOL_VSP, theVspSession);
				theVspSession = null;
			}
			else {
				Assert.fail(
						"InvalidateSessionException received with no matching session");
			}
		}
		finally {
			if (theRegistrySession != null) {
				this.returnSession(POOL_REGISTRY_1, theRegistrySession);
			}
			if (theVspSession != null) {
				this.returnSession(POOL_VSP, theVspSession);
			}
		}

		printEnd("testDelayedDomainVerification");
	}

	/**
	 * Execute the China Transfer of Compliant Domain Flow that includes the
	 * following steps:<br>
	 * <ol>
	 * <li>Gaining Registrar Info Domain for Verification Compliance
	 * Information, which is compliant.
	 * <li>Gaining Registrar Transfer Request with no codes.
	 * <li>Losing Registrar Approves Transfer
	 * </ol>
	 */
	public void testTransferOfCompliantDomain() {
		printStart("testTransferOfCompliantDomain");

		// Pre-condition check that POOL_REGISTRY_2 is defined
		if (!this.sessionPool.hasSystemSessionPool(POOL_REGISTRY_2)) {
			System.out.println("testTransferOfCompliantDomain: "
					+ POOL_REGISTRY_2
					+ " session pool not defined for optional test, skipping test");
			printEnd("testTransferOfCompliantDomain");
			return;
		}

		EPPSession theRegistry1Session = null;
		EPPSession theRegistry2Session = null;
		EPPSession theVspSession = null;
		EPPNameVerification theVsp = null;

		try {
			// --- Get sessions to the VSP and to the Registry

			// Initialize Registry Domain #1 (Losing) Interface
			theRegistry1Session = this.borrowSession(POOL_REGISTRY_1);
			NSDomain theDomain1 = new NSDomain(theRegistry1Session);

			// Initialize Registry Domain #2 (Gaining) Interface
			theRegistry2Session = this.borrowSession(POOL_REGISTRY_2);
			NSDomain theDomain2 = new NSDomain(theRegistry2Session);

			System.out.println(
					"testTransferOfCompliantDomain: Got sessions (1 and 2) to the Registry");

			// Initialize VSP Interface
			if (vspPoolExists) {
				theVspSession = this.borrowSession(POOL_VSP);
				theVsp = new EPPNameVerification(theVspSession);
				System.out.println(
						"testTransferOfCompliantDomain: Got session to the Vsp");
			}

			// --- Generate unique domain label and domain name

			String theDomainName = this
					.makeDomainName("cvct-transfer-compliant-", ".com");
			System.out.println("theDomainName = " + theDomainName);
			String theLabels[] = theDomainName.split("\\.");
			String theLabel = theLabels[0];
			String theTld = theLabels[1];

			EPPEncodedSignedCodeValue theDnvc = null;
			EPPEncodedSignedCodeValue theRnvc = null;

			if (!vspPoolExists) {
				theDnvc = dnvc;
				theRnvc = rnvc;
			}

			System.out.println(
					"testTransferOfCompliantDomain: Generated unique domain label ("
							+ theLabel + ") and domain name (" + theDomainName
							+ ") for flow");

			// --- Create Domain Name Verification Code (DNVC) with VSP

			if (vspPoolExists) {
				try {
					theVsp.setTransId(this.makeClientTransId());

					theVsp.setDnv(new EPPDomainNameVerification(theLabel));

					theVsp.setAuthInfo("transferOfCompliantDomain-1");

					EPPNameVerificationCreateResp theResponse = theVsp
							.sendCreate();

					if (!(theResponse
							.getCreateResult() instanceof EPPNameVerificationCreateSuccess)) {
						Assert.fail(
								"DNVC create was not successful with result = "
										+ theResponse.getCreateResult());
					}

					EPPNameVerificationCreateSuccess theSuccessResult = (EPPNameVerificationCreateSuccess) theResponse
							.getCreateResult();
					theDnvc = theSuccessResult.getSignedCode();
					System.out.println(
							"testTransferOfCompliantDomain: DNVC successfully created, type = "
									+ theSuccessResult.getCodeType()
									+ ", code  = "
									+ theSuccessResult.getCode());
				}
				catch (Exception ex) {
					TestUtil.handleException(theVspSession, ex);
				}
			}
			else {
				System.out.println(
						"testTransferOfCompliantDomain: Skipped Create Domain Name Verification Code (DNVC) due to no VSP pool");
			}

			// --- Create Real Name Verification Code (RNVC) with VSP

			if (vspPoolExists) {
				try {
					theVsp.setTransId(this.makeClientTransId());

					theVsp.setRnv(new EPPRealNameVerification(
							EPPRealNameVerification.Role.PERSON, "\u5f20\u7b56",
							"110108199002161234",
							EPPRealNameVerification.ProofType.POC));

					theVsp.setAuthInfo("transferOfCompliantDomain-2");

					EPPNameVerificationCreateResp theResponse = theVsp
							.sendCreate();

					if (!(theResponse
							.getCreateResult() instanceof EPPNameVerificationCreateSuccess)) {
						Assert.fail(
								"RNVC create was not successful with result = "
										+ theResponse.getCreateResult());
					}

					EPPNameVerificationCreateSuccess theSuccessResult = (EPPNameVerificationCreateSuccess) theResponse
							.getCreateResult();
					theRnvc = theSuccessResult.getSignedCode();
					System.out.println(
							"testTransferOfCompliantDomain: RNVC successfully created, type = "
									+ theSuccessResult.getCodeType()
									+ ", code  = "
									+ theSuccessResult.getCode());
				}
				catch (Exception ex) {
					TestUtil.handleException(theVspSession, ex);
				}
			}
			else {
				System.out.println(
						"testTransferOfCompliantDomain: Skipped Create Real Name Verification Code (RNVC) due to no VSP pool");
			}

			// --- Create Domain Name in Registry with DNVC and RNVC.

			try {
				theDomain1.setTransId(this.makeClientTransId());

				theDomain1.addDomainName(theDomainName);
				theDomain1.setSubProductID(theTld);

				theDomain1.setAuthString("transferOfCompliantDomain-3");

				// Add DNVC and RNVC as extension to create
				EPPEncodedSignedCode theSignedCodeExt = new EPPEncodedSignedCode();
				theSignedCodeExt.addCode(theDnvc);
				theSignedCodeExt.addCode(theRnvc);
				theDomain1.addExtension(theSignedCodeExt);

				EPPDomainCreateResp theResponse = theDomain1.sendCreate();

				System.out.println("testTransferOfCompliantDomain: Domain name "
						+ theDomainName
						+ " successfully created with DNVC (type = "
						+ theDnvc.getCodeType() + ", code = "
						+ theDnvc.getCodeValue() + ") and RNVC (type = "
						+ theRnvc.getCodeType() + ", code = "
						+ theRnvc.getCodeValue());
			}
			catch (Exception ex) {
				TestUtil.handleException(theRegistry1Session, ex);
			}

			// --- STEP 1 - Gaining Registrar Info Domain for Verification
			// Compliance Information, which is compliant.

			try {
				theDomain2.setTransId(this.makeClientTransId());

				theDomain2.addDomainName(theDomainName);
				theDomain2.setSubProductID(theTld);

				// Enable return of verification status information
				theDomain2.addExtension(new EPPVerificationCodeInfo());

				// Specify only delegated hosts wanted
				theDomain2.setHosts(EPPDomain.HOSTS_DELEGATED);

				EPPDomainInfoResp theResponse = theDomain2.sendInfo();

				Assert.assertTrue(theResponse
						.hasExtension(EPPVerificationCodeInfData.class));

				EPPVerificationCodeInfData theVerificationExt = (EPPVerificationCodeInfData) theResponse
						.getExtension(EPPVerificationCodeInfData.class);

				Assert.assertTrue(theVerificationExt
						.getStatus() == EPPVerificationCodeInfData.Status.COMPLIANT);

				System.out.println(
						"testTransferOfCompliantDomain: Step 1 - Domain name "
								+ theDomainName + " verification is compliant");

			}
			catch (Exception ex) {
				TestUtil.handleException(theRegistry1Session, ex);
			}

			// --- STEP 2 - Gaining Registrar Transfer Request with no codes.

			try {
				theDomain2.setTransId(this.makeClientTransId());

				theDomain2.addDomainName(theDomainName);
				theDomain2.setSubProductID(theTld);

				theDomain2.setAuthString("transferOfCompliantDomain-3");

				theDomain2.setTransferOpCode(EPPDomain.TRANSFER_REQUEST);

				EPPDomainTransferResp theResponse = theDomain2.sendTransfer();

				System.out.println(
						"testTransferOfCompliantDomain: Step 2 - Domain name "
								+ theDomainName
								+ " Transfer Request with no codes is successful");

			}
			catch (Exception ex) {
				TestUtil.handleException(theRegistry1Session, ex);
			}

			// --- STEP 3 - Losing Registrar Approves Transfer

			try {
				theDomain1.setTransId(this.makeClientTransId());

				theDomain1.addDomainName(theDomainName);
				theDomain1.setSubProductID(theTld);

				theDomain1.setTransferOpCode(EPPDomain.TRANSFER_APPROVE);

				EPPDomainTransferResp theResponse = theDomain1.sendTransfer();

				System.out.println(
						"testTransferOfCompliantDomain: Step 3 - Domain name "
								+ theDomainName
								+ " Transfer Approve is successful");

			}
			catch (Exception ex) {
				TestUtil.handleException(theRegistry1Session, ex);
			}

			// --- Cleanup domain

			try {
				theDomain2.setTransId(this.makeClientTransId());

				theDomain2.addDomainName(theDomainName);
				theDomain2.setSubProductID(theTld);

				theDomain2.sendDelete();
			}
			catch (Exception ex) {
				TestUtil.handleException(theRegistry1Session, ex);
			}

		}
		catch (InvalidateSessionException ex) {
			if (ex.getSession() == theRegistry1Session) {
				this.invalidateSession(POOL_REGISTRY_1, theRegistry1Session);
				theRegistry1Session = null;
			}
			else if (ex.getSession() == theRegistry2Session) {
				this.invalidateSession(POOL_REGISTRY_2, theRegistry2Session);
				theRegistry2Session = null;
			}
			else if (ex.getSession() == theVspSession) {
				this.invalidateSession(POOL_VSP, theVspSession);
				theVspSession = null;
			}
			else {
				Assert.fail(
						"InvalidateSessionException received with no matching session");
			}
		}
		finally {
			if (theRegistry1Session != null) {
				this.returnSession(POOL_REGISTRY_1, theRegistry1Session);
			}
			if (theRegistry2Session != null) {
				this.returnSession(POOL_REGISTRY_2, theRegistry2Session);
			}
			if (theVspSession != null) {
				this.returnSession(POOL_VSP, theVspSession);
			}
		}

		printEnd("testTransferOfCompliantDomain");
	}

	/**
	 * Execute the China Transfer of Non-Compliant Domain Flow that includes the
	 * following steps:<br>
	 * <ol>
	 * <li>Gaining Registrar Info Domain for Verification Compliance
	 * Information, which is not compliant (pending compliance in test).
	 * <li>Gaining Registrar Create Real Name Verification Code (RNVC) with VSP
	 * using test registrant data.
	 * <li>Gaining Registrar Transfer Request with RNVC.
	 * <li>Losing Registrar Approves Transfer
	 * </ol>
	 */
	public void testTransferOfNonCompliantDomain() {
		printStart("testTransferOfNonCompliantDomain");

		// Pre-condition check that POOL_REGISTRY_2 is defined
		if (!this.sessionPool.hasSystemSessionPool(POOL_REGISTRY_2)) {
			System.out.println("testTransferOfNonCompliantDomain: "
					+ POOL_REGISTRY_2
					+ " session pool not defined for optional test, skipping test");
			printEnd("testTransferOfNonCompliantDomain");
			return;
		}

		EPPSession theRegistry1Session = null;
		EPPSession theRegistry2Session = null;
		EPPSession theVspSession = null;
		EPPNameVerification theVsp = null;

		try {
			// --- Get sessions to the VSP and to the Registry

			// Initialize Registry Domain #1 (Losing) Interface
			theRegistry1Session = this.borrowSession(POOL_REGISTRY_1);
			NSDomain theDomain1 = new NSDomain(theRegistry1Session);

			// Initialize Registry Domain #2 (Gaining) Interface
			theRegistry2Session = this.borrowSession(POOL_REGISTRY_2);
			NSDomain theDomain2 = new NSDomain(theRegistry2Session);

			System.out.println(
					"testTransferOfNonCompliantDomain: Got sessions to the Registry (1 and 2)");

			// Initialize VSP Interface
			if (vspPoolExists) {
				theVspSession = this.borrowSession(POOL_VSP);
				theVsp = new EPPNameVerification(theVspSession);
				System.out.println(
						"testTransferOfNonCompliantDomain: Got session to the Vsp");
			}

			// --- Generate unique domain label and domain name

			String theDomainName = this
					.makeDomainName("cvct-transfer-noncompliant-", ".com");
			System.out.println("theDomainName = " + theDomainName);
			String theLabels[] = theDomainName.split("\\.");
			String theLabel = theLabels[0];
			String theTld = theLabels[1];

			EPPEncodedSignedCodeValue theDnvc = null;
			EPPEncodedSignedCodeValue theRnvc = null;

			if (!vspPoolExists) {
				theDnvc = dnvc;
				theRnvc = rnvc;
			}

			System.out.println(
					"testTransferOfNonCompliantDomain: Generated unique domain label ("
							+ theLabel + ") and domain name (" + theDomainName
							+ ") for flow");

			// --- Create Domain Name Verification Code (DNVC) with VSP

			if (vspPoolExists) {
				try {
					theVsp.setTransId(this.makeClientTransId());

					theVsp.setDnv(new EPPDomainNameVerification(theLabel));

					theVsp.setAuthInfo("transferOfNonCompliantDomain-1");

					EPPNameVerificationCreateResp theResponse = theVsp
							.sendCreate();

					if (!(theResponse
							.getCreateResult() instanceof EPPNameVerificationCreateSuccess)) {
						Assert.fail(
								"DNVC create was not successful with result = "
										+ theResponse.getCreateResult());
					}

					EPPNameVerificationCreateSuccess theSuccessResult = (EPPNameVerificationCreateSuccess) theResponse
							.getCreateResult();
					theDnvc = theSuccessResult.getSignedCode();
					System.out.println(
							"testTransferOfNonCompliantDomain: DNVC successfully created, type = "
									+ theSuccessResult.getCodeType()
									+ ", code  = "
									+ theSuccessResult.getCode());
				}
				catch (Exception ex) {
					TestUtil.handleException(theVspSession, ex);
				}
			}
			else {
				System.out.println(
						"testTransferOfNonCompliantDomain: Skipped Create Domain Name Verification Code (DNVC) due to no VSP pool");
			}

			// --- Create Domain Name in Registry with DNVC.

			try {
				theDomain1.setTransId(this.makeClientTransId());

				theDomain1.addDomainName(theDomainName);
				theDomain1.setSubProductID(theTld);

				theDomain1.setAuthString("transferOfCompliantDomain-3");

				// Add DNVC as extension to create
				EPPEncodedSignedCode theSignedCodeExt = new EPPEncodedSignedCode();
				theSignedCodeExt.addCode(theDnvc);
				theDomain1.addExtension(theSignedCodeExt);

				EPPDomainCreateResp theResponse = theDomain1.sendCreate();

				System.out.println(
						"testTransferOfNonCompliantDomain: Domain name "
								+ theDomainName
								+ " successfully created with DNVC (type = "
								+ theDnvc.getCodeType() + ", code = "
								+ theDnvc.getCodeValue() + ")");
			}
			catch (Exception ex) {
				TestUtil.handleException(theRegistry1Session, ex);
			}

			// --- STEP 1 - Gaining Registrar Info Domain for Verification
			// Compliance Information, which is pendingCompliance.

			try {
				theDomain2.setTransId(this.makeClientTransId());

				theDomain2.addDomainName(theDomainName);
				theDomain2.setSubProductID(theTld);

				// Enable return of verification status information
				theDomain2.addExtension(new EPPVerificationCodeInfo());

				// Specify only delegated hosts wanted
				theDomain2.setHosts(EPPDomain.HOSTS_DELEGATED);

				EPPDomainInfoResp theResponse = theDomain2.sendInfo();

				Assert.assertTrue(theResponse
						.hasExtension(EPPVerificationCodeInfData.class));

				EPPVerificationCodeInfData theVerificationExt = (EPPVerificationCodeInfData) theResponse
						.getExtension(EPPVerificationCodeInfData.class);

				Assert.assertTrue(theVerificationExt
						.getStatus() == EPPVerificationCodeInfData.Status.PENDING_COMPLIANCE);

				// Assert there is a profile set
				Assert.assertTrue(theVerificationExt.hasProfile());

				// Assert there are missing codes
				Assert.assertTrue(
						theVerificationExt.getProfile().hasMissingCodes());

				// Assert the missing code is a real-name code
				Assert.assertTrue(
						theVerificationExt.getProfile().getMissingCodes().get(0)
								.getType().equals("real-name"));

				System.out.println(
						"testTransferOfNonCompliantDomain: Step 1 - Domain name "
								+ theDomainName
								+ " verification is pendingCompliance with missing RNVC");

			}
			catch (Exception ex) {
				TestUtil.handleException(theRegistry1Session, ex);
			}

			// --- STEP 2 - Create Real Name Verification Code (RNVC) with VSP

			if (vspPoolExists) {
				try {
					theVsp.setTransId(this.makeClientTransId());

					theVsp.setRnv(new EPPRealNameVerification(
							EPPRealNameVerification.Role.PERSON, "\u5f20\u7b56",
							"110108199002161234",
							EPPRealNameVerification.ProofType.POC));

					theVsp.setAuthInfo("transferOfNonCompliantDomain-2");

					EPPNameVerificationCreateResp theResponse = theVsp
							.sendCreate();

					if (!(theResponse
							.getCreateResult() instanceof EPPNameVerificationCreateSuccess)) {
						Assert.fail(
								"RNVC create was not successful with result = "
										+ theResponse.getCreateResult());
					}

					EPPNameVerificationCreateSuccess theSuccessResult = (EPPNameVerificationCreateSuccess) theResponse
							.getCreateResult();
					theRnvc = theSuccessResult.getSignedCode();
					System.out.println(
							"testTransferOfNonCompliantDomain: Step 2 - RNVC successfully created, type = "
									+ theSuccessResult.getCodeType()
									+ ", code  = "
									+ theSuccessResult.getCode());
				}
				catch (Exception ex) {
					TestUtil.handleException(theVspSession, ex);
				}
			}
			else {
				System.out.println(
						"testTransferOfNonCompliantDomain: Skipped Create Real Name Verification Code (RNVC) due to no VSP pool");
			}

			// --- STEP 3 - Gaining Registrar Transfer Request with RNVC.

			try {
				theDomain2.setTransId(this.makeClientTransId());

				theDomain2.addDomainName(theDomainName);
				theDomain2.setSubProductID(theTld);

				theDomain2.setAuthString("transferOfCompliantDomain-3");

				theDomain2.setTransferOpCode(EPPDomain.TRANSFER_REQUEST);

				// Add RNVC as extension to create
				EPPEncodedSignedCode theSignedCodeExt = new EPPEncodedSignedCode();
				theSignedCodeExt.addCode(theRnvc);
				theDomain2.addExtension(theSignedCodeExt);

				EPPDomainTransferResp theResponse = theDomain2.sendTransfer();

				System.out.println(
						"testTransferOfNonCompliantDomain: Step 3 - Domain name "
								+ theDomainName
								+ " Transfer Request with RNVC (type = "
								+ theRnvc.getCodeType() + ", code = "
								+ theRnvc.getCodeValue() + ")");

			}
			catch (Exception ex) {
				TestUtil.handleException(theRegistry1Session, ex);
			}

			// --- STEP 3 - Losing Registrar Approves Transfer

			try {
				theDomain1.setTransId(this.makeClientTransId());

				theDomain1.addDomainName(theDomainName);
				theDomain1.setSubProductID(theTld);

				theDomain1.setTransferOpCode(EPPDomain.TRANSFER_APPROVE);

				EPPDomainTransferResp theResponse = theDomain1.sendTransfer();

				System.out.println(
						"testTransferOfNonCompliantDomain: Step 3 - Domain name "
								+ theDomainName
								+ " Transfer Approve is successful");

			}
			catch (Exception ex) {
				TestUtil.handleException(theRegistry1Session, ex);
			}

			// --- Cleanup domain

			try {
				theDomain2.setTransId(this.makeClientTransId());

				theDomain2.addDomainName(theDomainName);
				theDomain2.setSubProductID(theTld);

				theDomain2.sendDelete();
			}
			catch (Exception ex) {
				TestUtil.handleException(theRegistry1Session, ex);
			}

		}
		catch (InvalidateSessionException ex) {
			if (ex.getSession() == theRegistry1Session) {
				this.invalidateSession(POOL_REGISTRY_1, theRegistry1Session);
				theRegistry1Session = null;
			}
			else if (ex.getSession() == theRegistry2Session) {
				this.invalidateSession(POOL_REGISTRY_2, theRegistry2Session);
				theRegistry2Session = null;
			}
			else if (ex.getSession() == theVspSession) {
				this.invalidateSession(POOL_VSP, theVspSession);
				theVspSession = null;
			}
			else {
				Assert.fail(
						"InvalidateSessionException received with no matching session");
			}
		}
		finally {
			if (theRegistry1Session != null) {
				this.returnSession(POOL_REGISTRY_1, theRegistry1Session);
			}
			if (theRegistry2Session != null) {
				this.returnSession(POOL_REGISTRY_2, theRegistry2Session);
			}
			if (theVspSession != null) {
				this.returnSession(POOL_VSP, theVspSession);
			}
		}

		printEnd("testTransferOfNonCompliantDomain");
	}

	/**
	 * JUNIT <code>setUp</code> method
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
	 * associated with <code>ChinaVerificationCodeTst</code>.
	 *
	 * @return Test Suite
	 */
	public static Test suite() {
		return new ChinaVerificationCodeTstSetup(
				new TestSuite(ChinaVerificationCodeTst.class));
	}

	/**
	 * Setup framework from running ChinaVerificationCodeTst tests.
	 */
	private static class ChinaVerificationCodeTstSetup extends TestSetup {

		/**
		 * Creates setup instance for passed in tests.
		 * 
		 * @param aTest
		 *            Tests to execute
		 */
		public ChinaVerificationCodeTstSetup(Test aTest) {
			super(aTest);
		}

		/**
		 * Setup framework for running ChinaVerificationCodeTst tests.
		 */
		protected void setUp() throws Exception {
			super.setUp();

			String theConfigFileName = System.getProperty("EPP.ConfigFile");
			if (theConfigFileName != null)
				configFileName = theConfigFileName;

			try {
				app.initialize(configFileName);
			}
			catch (EPPCommandException e) {
				e.printStackTrace();
				Assert.fail("Error initializing the EPP Application: " + e);
			}

			// Initialize the session pool
			try {
				sessionPool = EPPSessionPool.getInstance();
				sessionPool.init();
			}
			catch (Exception ex) {
				ex.printStackTrace();
				Assert.fail("Error initializing the session pool: " + ex);
			}

			// Is the VSP session pool not configured?
			if (!sessionPool.hasSystemSessionPool(POOL_VSP)) {
				System.out.println(
						"ChinaVerificationCodeTstSetup: No VSP pool defined, load DNVC and RNVC from files");
				vspPoolExists = false;

				// Read the DNVC from the dnvc.b64 file to dnvc static attribute
				File theDnvcFile = new File("dnvc.b64");
				if (!theDnvcFile.exists() || !theDnvcFile.canRead()) {
					Assert.fail("dnvc.64 does not exist or is not readable");
				}
				String theDnvc = FileUtils
						.readFully(new FileReader(theDnvcFile));
				dnvc = new EPPEncodedSignedCodeValue();
				dnvc.decodeValue(theDnvc);
				System.out.println(
						"ChinaVerificationCodeTstSetup: Successfully loaded DNVC from dnvc.b64");

				// Read the RNVC from the rnvc.b64 file to rnvc static attribute
				File theRnvcFile = new File("rnvc.b64");
				if (!theRnvcFile.exists() || !theRnvcFile.canRead()) {
					Assert.fail("rnvc.64 does not exist or is not readable");
				}
				String theRnvc = FileUtils
						.readFully(new FileReader(theRnvcFile));
				rnvc = new EPPEncodedSignedCodeValue();
				rnvc.decodeValue(theRnvc);
				System.out.println(
						"ChinaVerificationCodeTstSetup: Successfully loaded RNVC from rnvc.b64");
			}

		}

		/**
		 * Tear down framework from running ChinaVerificationCodeTst tests.
		 */
		protected void tearDown() throws Exception {
			super.tearDown();
			EPPSessionPool.getInstance().close();
		}
	}

	/**
	 * Unit test main, which accepts the following system property options: <br>
	 * 
	 * <ul>
	 * <li>iterations Number of unit test iterations to run</li>
	 * <li>validate Turn XML validation on (<code>true</code>) or off (
	 * <code>false</code>). If validate is not specified, validation will be
	 * off.</li>
	 * </ul>
	 * 
	 * 
	 * @param args
	 *            Program arguments
	 */
	public static void main(String[] args) {
		// Override the default configuration file name?
		if (args.length > 0) {
			configFileName = args[0];
		}

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
						"ChinaVerificationCodeTst Thread " + i,
						ChinaVerificationCodeTst.suite());
				thread.start();
			}
		}
		else { // Single threaded mode.
			junit.textui.TestRunner.run(ChinaVerificationCodeTst.suite());
		}

		try {
			app.endApplication();
		}
		catch (EPPCommandException e) {
			e.printStackTrace();
			Assert.fail("Error ending the EPP Application: " + e);
		}
	}

	/**
	 * Consume the messages in the poll queue looking for a Change Poll Message
	 * with an optional domain prefix. All messages found in the poll queue up
	 * to and incuding the Change Poll Message will be acknowledged.
	 * 
	 * @param aSession
	 *            Session to execute the poll commands
	 * @param aDomainPrefix
	 *            Domain name prefix to look for. If <code>null</code> then look
	 *            for any Change Poll Message.
	 * 
	 * @return Change Poll Message if found; <code>null</code> otherwise.
	 * 
	 * @param EPPException
	 *            Raised if error sending poll commands
	 */
	private EPPDomainInfoResp findChangePoll(EPPSession aSession,
			String aDomainPrefix) throws EPPException {

		EPPDomainInfoResp theChangePollMsg = null;
		EPPResponse theResponse = null;

		aSession.setTransId(this.makeClientTransId());
		aSession.setPollOp(EPPSession.OP_REQ);
		theResponse = aSession.sendPoll();

		while ((theResponse.getResult().getCode() == EPPResult.SUCCESS_POLL_MSG)
				&& (theChangePollMsg == null)) {

			System.out.println("findChangePoll: Poll Response = [" + theResponse
					+ "]\n\n");

			if (theResponse instanceof EPPDomainTransferResp) {
				System.out.println("findChangePoll: ID = "
						+ theResponse.getMsgQueue().getId()
						+ ", domain transfer poll message");
			}
			else if (theResponse instanceof EPPContactTransferResp) {
				System.out.println("findChangePoll: ID = "
						+ theResponse.getMsgQueue().getId()
						+ ", contact transfer poll message");
			}
			else if (theResponse instanceof EPPLowBalancePollResponse) {
				System.out.println("findChangePoll: ID = "
						+ theResponse.getMsgQueue().getId()
						+ ", low balance poll message");
			}
			else if (theResponse instanceof EPPRgpPollResponse) {
				System.out.println("findChangePoll: ID = "
						+ theResponse.getMsgQueue().getId()
						+ ", RGP poll message");
			}
			else if (theResponse instanceof EPPDomainPendActionMsg) {
				System.out.println("findChangePoll: ID = "
						+ theResponse.getMsgQueue().getId()
						+ ", domain pending action poll message");
			}
			else if ((theResponse instanceof EPPDomainInfoResp)
					&& (theResponse.hasExtension(EPPChangeData.class))) {
				System.out.println("findChangePoll: ID = "
						+ theResponse.getMsgQueue().getId()
						+ ", change poll message");

				theChangePollMsg = (EPPDomainInfoResp) theResponse;

				EPPChangeData theChange = (EPPChangeData) theResponse
						.getExtension(EPPChangeData.class);

				if (theChange.getReason().equals("Locale Data Policy")) {
					System.out.println(
							"findChangePoll: Received Locale Data Policy Change Poll Message");
				}

				if (aDomainPrefix == null || theChangePollMsg.getName()
						.startsWith(aDomainPrefix)) {
					System.out.println(
							"findChangePoll: Found matching change poll message");
				}
				else {
					theChangePollMsg = null;
				}
			}
			else {
				System.out.println("findChangePoll: ID = "
						+ theResponse.getMsgQueue().getId()
						+ ", unknown poll message of type = "
						+ theResponse.getClass().getName());
			}

			// Acknowledge the current message
			aSession.setTransId(this.makeClientTransId());
			aSession.setPollOp(EPPSession.OP_ACK);
			aSession.setMsgID(theResponse.getMsgQueue().getId());
			theResponse = aSession.sendPoll();

			aSession.setTransId(this.makeClientTransId());
			aSession.setPollOp(EPPSession.OP_REQ);
			theResponse = aSession.sendPoll();
		}

		return theChangePollMsg;
	}

	/**
	 * Print the start of a test with the <code>Thread</code> name if the
	 * current thread is a <code>TestThread</code>.
	 *
	 * @param aTest
	 *            name for the test
	 */
	public static void printStart(String aTest) {
		if (Thread.currentThread() instanceof TestThread) {
			System.out.print(Thread.currentThread().getName() + ": ");
			cat.info(
					Thread.currentThread().getName() + ": " + aTest + " Start");
		}

		System.out.println("Start of " + aTest);
		System.out.println(
				"****************************************************************\n");
	}

	/**
	 * Print the end of a test with the <code>Thread</code> name if the current
	 * thread is a <code>TestThread</code>.
	 *
	 * @param aTest
	 *            name for the test
	 */
	public static void printEnd(String aTest) {
		System.out.println(
				"****************************************************************");

		if (Thread.currentThread() instanceof TestThread) {
			System.out.print(Thread.currentThread().getName() + ": ");
			cat.info(Thread.currentThread().getName() + ": " + aTest + " End");
		}

		System.out.println("End of " + aTest);
		System.out.println("\n");
	}

	/**
	 * Utility method to borrow a session from the session pool. All exceptions
	 * will result in the test failing. This method should only be used for
	 * positive session pool tests.
	 * 
	 * @param aPool
	 *            Pool name of session to borrow from.
	 * 
	 * @return Session from the session pool
	 */
	private EPPSession borrowSession(String aPool) {
		EPPSession theSession = null;
		try {
			theSession = this.sessionPool.borrowObject(aPool);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail("borrowSession(): Exception borrowing session from "
					+ aPool + ": " + ex);
		}

		return theSession;
	}

	/**
	 * Utility method to return a session to the session pool. This should be
	 * placed in a finally block. All exceptions will result in the test
	 * failing.
	 * 
	 * @param aPool
	 *            Pool name of session to return to.
	 * 
	 * @param aSession
	 *            Session to return to the pool
	 */
	private void returnSession(String aPool, EPPSession aSession) {
		try {
			if (aSession != null)
				sessionPool.returnObject(aPool, aSession);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail("returnSession(): Exception returning session to "
					+ aPool + ": " + ex);
		}
	}

	/**
	 * Utility method to invalidate a session in the session pool. This should
	 * be placed in an exception block.
	 * 
	 * @param aPool
	 *            Pool name of session to invalidate.
	 * @param aSession
	 *            Session to invalidate in the pool
	 */
	private void invalidateSession(String aPool, EPPSession aSession) {
		try {
			if (aSession != null)
				sessionPool.invalidateObject(aPool, aSession);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail(
					"invalidateSession(): Exception invalidating session in "
							+ aPool + ": " + ex);
		}
	}

	/**
	 * Handle a response by printing out the result details.
	 * 
	 * @param aResponse
	 *            the response to handle
	 */
	private void handleResponse(EPPResponse aResponse) {

		for (int i = 0; i < aResponse.getResults().size(); i++) {
			EPPResult theResult = (EPPResult) aResponse.getResults()
					.elementAt(i);

			System.out.println("Result Code    : " + theResult.getCode());
			System.out.println("Result Message : " + theResult.getMessage());
			System.out.println("Result Lang    : " + theResult.getLang());

			if (theResult.isSuccess()) {
				System.out.println("Command Passed ");
			}
			else {
				System.out.println("Command Failed ");
			}

			if (theResult.getAllValues() != null) {
				for (int k = 0; k < theResult.getAllValues().size(); k++) {
					System.out.println("Result Values  : "
							+ theResult.getAllValues().elementAt(k));
				}
			}
		}
	}

	/**
	 * This method tries to generate a unique String as Domain Name and Name
	 * Server
	 *
	 * @param aPrefix
	 *            Domain name prefix to use
	 * @param aTld
	 *            TLD to use in the domain name
	 *
	 * @return Unique domain name using <code>aPrefix</code> and
	 *         <code>aTld</code>.
	 */
	public String makeDomainName(String aPrefix, String aTld) {
		long tm = System.currentTimeMillis();

		return new String(aPrefix + tm + rd.nextInt(12) + aTld);
	}

	/**
	 * Make unique client transaction identifier.
	 * 
	 * @return Unique client transaction identifer
	 */
	public String makeClientTransId() {
		long tm = System.currentTimeMillis();

		return new String("china-ver-tst-" + Thread.currentThread()
				+ String.valueOf(tm + rd.nextInt(12)).substring(10));
	}

}
