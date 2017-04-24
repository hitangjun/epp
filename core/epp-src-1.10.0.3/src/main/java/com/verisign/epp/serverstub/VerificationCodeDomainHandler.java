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
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.PKIXParameters;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.verisign.epp.codec.domain.EPPDomainCreateCmd;
import com.verisign.epp.codec.domain.EPPDomainDeleteCmd;
import com.verisign.epp.codec.domain.EPPDomainInfoCmd;
import com.verisign.epp.codec.domain.EPPDomainInfoResp;
import com.verisign.epp.codec.domain.EPPDomainRenewCmd;
import com.verisign.epp.codec.domain.EPPDomainStatus;
import com.verisign.epp.codec.domain.EPPDomainTransferCmd;
import com.verisign.epp.codec.domain.EPPDomainUpdateCmd;
import com.verisign.epp.codec.gen.EPPCommand;
import com.verisign.epp.codec.gen.EPPResponse;
import com.verisign.epp.codec.gen.EPPResult;
import com.verisign.epp.codec.gen.EPPTransId;
import com.verisign.epp.codec.verificationcode.EPPEncodedSignedCode;
import com.verisign.epp.codec.verificationcode.EPPEncodedSignedCodeValue;
import com.verisign.epp.codec.verificationcode.EPPVerificationCode;
import com.verisign.epp.codec.verificationcode.EPPVerificationCodeInfData;
import com.verisign.epp.codec.verificationcode.EPPVerificationCodeInfo;
import com.verisign.epp.codec.verificationcode.EPPVerificationCodeProfile;
import com.verisign.epp.codec.verificationcode.TrustAnchorVerificationCodeValidator;
import com.verisign.epp.codec.verificationcode.VerificationCodeRevocationList;
import com.verisign.epp.exception.EPPException;
import com.verisign.epp.framework.EPPEvent;
import com.verisign.epp.framework.EPPEventResponse;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.Environment;

/**
 * The <code>VerificationCodeDomainHandler</code> class extends
 * <code>DomainHandler</code> to include handling the verification code
 * extension.
 */
public class VerificationCodeDomainHandler extends DomainHandler {

	/** Log4j category for logging */
	private static Logger cat = Logger.getLogger(
			VerificationCodeDomainHandler.class.getName(), EPPCatFactory
					.getInstance().getFactory());

	/**
	 * Trust store containing the verification trust anchor CA certificates.
	 */
	private KeyStore trustStore;

	/**
	 * PKIX parameters used to validate the certificate path in signed code.
	 */
	private PKIXParameters pkixParameters;

	/**
	 * verification code revocation list
	 */
	private VerificationCodeRevocationList verificationCodeRevocationList = new VerificationCodeRevocationList();

	/**
	 * Concrete verification code validator that used to simulate verification
	 * of the verification code value and type using the trust store aliases.
	 */
	private TrustAnchorVerificationCodeValidator verificationCodeValidator;

	/**
	 * Constructs an instance of VerificationCodeDomainHandler
	 */
	public VerificationCodeDomainHandler() {

		cat.debug("VerificationCodeDomainHandler.VerificationCodeDomainHandler(): enter");

		// Load the PKIXParameters using the truststore
		String truststore = Environment
				.getProperty("EPP.VerificationCode.truststore");
		if (truststore == null) {
			cat.error("VerificationCodeDomainHandler.VerificationCodeDomainHandler(): EPP.SignedCode.truststore NOT defined in configuration");
			System.err
					.println("VerificationCodeDomainHandler.VerificationCodeDomainHandler(): EPP.SignedCode.truststore NOT defined in configuration");
			System.exit(1);
		}

		cat.debug("VerificationCodeDomainHandler.VerificationCodeDomainHandler(): Signed Code Truststore = "
				+ truststore);

		try {
			this.pkixParameters = this.loadPKIXParameters(truststore);
		}
		catch (Exception ex) {
			cat.error("VerificationCodeDomainHandler.VerificationCodeDomainHandler(): Error loading the public key: "
					+ ex);
			ex.printStackTrace();
			System.exit(1);
		}

		// Initialize the verification code validator
		this.verificationCodeValidator = new TrustAnchorVerificationCodeValidator(
				this.trustStore);

		// Initialize the verification code revocation list
		String revocationListProp = Environment
				.getOption("EPP.VerificationCode.revocationList");

		if (revocationListProp == null) {

			File verificationCodeRevocationListFile = new File(
					revocationListProp);

			if (verificationCodeRevocationListFile.exists()) {

				try {
					FileInputStream verificationCodeRevocationListStream = new FileInputStream(
							verificationCodeRevocationListFile);
					this.verificationCodeRevocationList
							.decode(verificationCodeRevocationListStream);
				}
				catch (Exception ex) {
					cat.error("VerificationCodeDomainHandler.VerificationCodeDomainHandler(): Error loading verification code revocation list : "
							+ ex);
					ex.printStackTrace();
					System.exit(1);
				}

				cat.debug("VerificationCodeDomainHandler.VerificationCodeDomainHandler(): No verification code revocation list found");

			}
			else {
				cat.error("VerificationCodeDomainHandler.VerificationCodeDomainHandler(): Error finding verification code revocation list \""
						+ revocationListProp + "\"");
				System.exit(1);
			}
		}
		else {
			cat.debug("VerificationCodeDomainHandler.VerificationCodeDomainHandler(): EPP.VerificationCode.revocationList property NOT defined");
		}

		cat.debug("VerificationCodeDomainHandler.VerificationCodeDomainHandler(): exit");
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
	private PKIXParameters loadPKIXParameters(String aTrustStoreName)
			throws Exception {

		this.trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		FileInputStream trustStoreFile = new FileInputStream(aTrustStoreName);
		this.trustStore.load(trustStoreFile, null);

		PKIXParameters pkixParameters = new PKIXParameters(this.trustStore);
		pkixParameters.setRevocationEnabled(false);

		return pkixParameters;
	}

	/**
	 * Create an error <code>EPPEventResponse</code> to return from one of the
	 * handler methods.
	 * 
	 * @param aCode
	 *            <code>EPPResult</code> error constants
	 * @param aClientTransId
	 *            Optional client transaction identifier. Set to
	 *            <code>null</code> if there is no client transaction
	 *            identifier.
	 * @param aReason
	 *            Free-form text reason for the error.
	 * @return <code>EPPEventResponse</code> instance to return from one of the
	 *         handler methods.
	 */
	private EPPEventResponse returnError(int aCode, String aClientTransId,
			String aReason) {

		cat.error("VerificationCodeDomainHandler.returnError: code = " + aCode
				+ ", reason = " + aReason);
		EPPResponse theResponse = new EPPResponse();
		EPPResult theResult = new EPPResult(aCode);
		theResult.addExtValueReason(aReason);
		theResponse.setResult(theResult);
		theResponse.setTransId(new EPPTransId(aClientTransId, "54321-XYZ"));
		return new EPPEventResponse(theResponse);
	}

	/**
	 * Handle the passing of the verification code extension on a transform
	 * command.
	 * 
	 * @param aMethod
	 *            Transform command method name to include in the log messages
	 * @param aCommand
	 *            Transform command object
	 * @return <code>EPPEventResponse</code> instance in the event of an error
	 *         to return out to the client; <code>null</code> otherwise.
	 */
	private EPPEventResponse handleVerificationCode(String aMethod,
			EPPCommand aCommand) {
		cat.debug("VerificationCodeDomainHandler.handleVerificationCode: enter");

		if (aCommand.hasExtension(EPPEncodedSignedCode.class)) {
			EPPEncodedSignedCode theExt = (EPPEncodedSignedCode) aCommand
					.getExtension(EPPEncodedSignedCode.class);

			for (EPPEncodedSignedCodeValue signedCode : theExt.getSignedCodes()) {

				// Signature valid?
				if (signedCode.validate(pkixParameters)) {
					cat.debug("VerificationCodeDomainHandler." + aMethod
							+ ": Signature is valid");

					try {
						if (this.verificationCodeValidator.validate(
								signedCode.getCode(),
								signedCode.getTrustAnchor())) {
							cat.debug("VerificationCodeDomainHandler."
									+ aMethod + ": Code is valid");
						}
						else {
							cat.error("VerificationCodeDomainHandler."
									+ aMethod + ": Code is NOT valid");
							cat.debug("VerificationCodeDomainHandler.handleVerificationCode: exit");
							return this.returnError(
									EPPResult.PARAM_VALUE_POLICY_ERROR,
									aCommand.getTransId(), "Code is invalid");
						}
					}
					catch (EPPException e) {
						cat.error("VerificationCodeDomainHandler."
								+ aMethod
								+ ": Exception validating the verification code: "
								+ e);
						cat.debug("VerificationCodeDomainHandler.handleVerificationCode: exit");
						return this.returnError(
								EPPResult.PARAM_VALUE_POLICY_ERROR,
								aCommand.getTransId(),
								"Error validating verification code");
					}

					// Trust Anchor set for validated code?
					if (signedCode.hasTrustAnchor()) {
						try {
							cat.debug("VerificationCodeDomainHandler."
									+ aMethod
									+ ": Matching Trust Anchor Alias = \""
									+ trustStore.getCertificateAlias(signedCode
											.getTrustAnchor().getTrustedCert())
									+ "\"");
						}
						catch (KeyStoreException e) {
							cat.error("VerificationCodeDomainHandler."
									+ aMethod
									+ ": Error getting Trust Anchor Alias: "
									+ e);
						}
					}
				}
				else {
					cat.error("VerificationCodeDomainHandler." + aMethod
							+ ": Signature is NOT valid");
					cat.debug("VerificationCodeDomainHandler.handleVerificationCode: exit");
					return this.returnError(EPPResult.PARAM_VALUE_POLICY_ERROR,
							aCommand.getTransId(),
							"Signed code signature invalid");
				}

				// Verification code is not revoked?
				if (!this.verificationCodeRevocationList.isRevoked(signedCode)) {
					cat.debug("VerificationCodeDomainHandler." + aMethod
							+ ": signed code is not revoked");
				}
				else {
					cat.error("VerificationCodeDomainHandler." + aMethod
							+ ": signed code is revoked");
					cat.debug("VerificationCodeDomainHandler.handleVerificationCode: exit");
					return this.returnError(EPPResult.PARAM_VALUE_POLICY_ERROR,
							aCommand.getTransId(), "Signed code is revoked");
				}

				cat.info("VerificationCodeDomainHandler." + aMethod
						+ ": verification code type = "
						+ signedCode.getCodeType());
				System.out.println("VerificationCodeDomainHandler." + aMethod
						+ ": verification code type = "
						+ signedCode.getCodeType());

				cat.info("VerificationCodeDomainHandler." + aMethod
						+ ": verification code = " + signedCode.getCodeValue());
				System.out.println("VerificationCodeDomainHandler." + aMethod
						+ ": verification code = " + signedCode.getCodeValue());

			}

		}
		else {
			cat.info("VerificationCodeDomainHandler." + aMethod
					+ ": verification code = null");
			System.out.println("VerificationCodeDomainHandler." + aMethod
					+ ": verification code = null");
		}

		cat.debug("VerificationCodeDomainHandler.handleVerificationCode: exit");
		return null;
	}

	/**
	 * Invoked when a Domain Info command is received that includes support for
	 * the {@link EPPVerificationCodeInfo} extension.
	 * 
	 * @param aEvent
	 *            The <code>EPPEvent</code> that is being handled
	 * @param aData
	 *            Any data that a Server needs to send to this
	 *            <code>VerificationCodeDomainHandler</code>
	 * 
	 * @return The <code>EPPEventResponse</code> that should be sent back to the
	 *         client.
	 */
	protected EPPEventResponse doDomainInfo(EPPEvent aEvent, Object aData) {
		cat.debug("VerificationCodeDomainHandler.doDomainInfo: enter");

		EPPDomainInfoCmd theCommand = (EPPDomainInfoCmd) aEvent.getMessage();

		EPPEventResponse theEventResponse = super.doDomainInfo(aEvent, aData);

		// If error, immediately return
		if (!(theEventResponse.getResponse() instanceof EPPDomainInfoResp)) {
			cat.debug("VerificationCodeDomainHandler.doDomainInfo: return on error");
			return theEventResponse;
		}

		// Extension provided?
		if (theCommand.hasExtension(EPPVerificationCodeInfo.class)) {

			EPPDomainInfoResp theResponse = (EPPDomainInfoResp) theEventResponse
					.getResponse();

			EPPVerificationCodeInfData theRespExt = null;

			EPPVerificationCodeInfo theCmdExt = (EPPVerificationCodeInfo) theCommand
					.getExtension(EPPVerificationCodeInfo.class);

			// Get attributes
			String profileName = "china";
			if (theCmdExt.hasProfile()) {
				profileName = theCmdExt.getProfile();
			}

			// -- Case 1 : Status
			if (theCommand.getName().equalsIgnoreCase("domain1.example")) {
				theRespExt = new EPPVerificationCodeInfData(
						EPPVerificationCodeInfData.Status.NOT_APPLICABLE);
			} // -- Case 2 : Status and Profile
			else if (theCommand.getName().equalsIgnoreCase("domain2.example")) {
				theRespExt = new EPPVerificationCodeInfData(
						EPPVerificationCodeInfData.Status.COMPLIANT,
						new EPPVerificationCodeProfile("china",
								EPPVerificationCodeProfile.Status.COMPLIANT));
			} // -- Case 3 : Status, Profile, and Set Codes from Sponsoring
				// Client
			else if (theCommand.getName().equalsIgnoreCase("domain3.example")) {
				EPPVerificationCodeProfile theProfile = new EPPVerificationCodeProfile(
						profileName,
						EPPVerificationCodeProfile.Status.COMPLIANT);
				theProfile.addSetCode(new EPPVerificationCode("0-abc333",
						"domain", new GregorianCalendar(2015, 4, 3, 22, 0, 0)
								.getTime()));
				theProfile.addSetCode(new EPPVerificationCode("0-abc444",
						"real-name",
						new GregorianCalendar(2015, 4, 3, 22, 0, 0).getTime()));

				theRespExt = new EPPVerificationCodeInfData(
						EPPVerificationCodeInfData.Status.COMPLIANT, theProfile);
			} // Case 4: Status, Profile, and Set Codes from Non-Sponsoring
				// Client
			else if (theCommand.getName().equalsIgnoreCase("domain4.example")) {
				EPPVerificationCodeProfile theProfile = new EPPVerificationCodeProfile(
						profileName,
						EPPVerificationCodeProfile.Status.COMPLIANT);
				theProfile.addSetCode(new EPPVerificationCode(null, "domain",
						new GregorianCalendar(2015, 4, 3, 22, 0, 0).getTime()));
				theProfile.addSetCode(new EPPVerificationCode(null,
						"real-name",
						new GregorianCalendar(2015, 4, 3, 22, 0, 0).getTime()));

				theRespExt = new EPPVerificationCodeInfData(
						EPPVerificationCodeInfData.Status.COMPLIANT, theProfile);
			} // Case 5: Status, Profile, and Missing Codes from Non-Sponsoring
				// Client
			else if (theCommand.getName().equalsIgnoreCase("domain5.example")) {
				EPPVerificationCodeProfile theProfile = new EPPVerificationCodeProfile(
						profileName,
						EPPVerificationCodeProfile.Status.NON_COMPLIANT);
				theProfile.addMissingCode(new EPPVerificationCode("domain",
						new GregorianCalendar(2015, 4, 3, 22, 0).getTime()));
				theProfile.addMissingCode(new EPPVerificationCode("real-name",
						new GregorianCalendar(2015, 4, 3, 22, 0).getTime()));

				theRespExt = new EPPVerificationCodeInfData(
						EPPVerificationCodeInfData.Status.NON_COMPLIANT,
						theProfile);

				// Override the domain status to serverHold
				Vector statuses = new Vector();
				statuses.add(new EPPDomainStatus(
						EPPDomainStatus.ELM_STATUS_SERVER_HOLD));
				theResponse.setStatuses(statuses);

			} // Case 6: Status, Profile, Missing Codes, and Set Codes from
				// Sponsoring Client
			else if (theCommand.getName().equalsIgnoreCase("domain6.example")) {
				EPPVerificationCodeProfile theProfile = new EPPVerificationCodeProfile(
						profileName,
						EPPVerificationCodeProfile.Status.PENDING_COMPLIANCE);
				theProfile.addSetCode(new EPPVerificationCode("0-abc333",
						"domain", new GregorianCalendar(2015, 4, 3, 22, 0, 0)
								.getTime()));
				theProfile.addMissingCode(new EPPVerificationCode("real-name",
						new GregorianCalendar(2015, 4, 3, 22, 0, 0).getTime()));

				theRespExt = new EPPVerificationCodeInfData(
						EPPVerificationCodeInfData.Status.PENDING_COMPLIANCE,
						theProfile);
			}
			// Case 7: Status with multiple profiles
			else if (theCommand.getName().equalsIgnoreCase("domain7.example")) {
				theRespExt = new EPPVerificationCodeInfData(
						EPPVerificationCodeInfData.Status.PENDING_COMPLIANCE);

				EPPVerificationCodeProfile theProfile = new EPPVerificationCodeProfile(
						"sample1",
						EPPVerificationCodeProfile.Status.PENDING_COMPLIANCE);
				theProfile.addSetCode(new EPPVerificationCode("0-abc333",
						"domain", new GregorianCalendar(2015, 4, 3, 22, 0, 0)
								.getTime()));
				theProfile.addMissingCode(new EPPVerificationCode("real-name",
						new GregorianCalendar(2015, 4, 3, 22, 0, 0).getTime()));
				theRespExt.addProfile(theProfile);

				theProfile = new EPPVerificationCodeProfile("sample2",
						EPPVerificationCodeProfile.Status.COMPLIANT);
				theProfile.addSetCode(new EPPVerificationCode("0-abc333",
						"domain", new GregorianCalendar(2015, 4, 3, 22, 0, 0)
								.getTime()));
				theProfile.addSetCode(new EPPVerificationCode("0-abc444",
						"real-name",
						new GregorianCalendar(2015, 4, 3, 22, 0, 0).getTime()));
				theRespExt.addProfile(theProfile);

			} // Just return Case 1 with the compliant status
				// -- Case 8 : Status, Profile, and Set Codes from Sponsoring
				// Client with codes set from a second profile
			else if (theCommand.getName().equalsIgnoreCase("domain8.example")) {
				EPPVerificationCodeProfile theProfile = new EPPVerificationCodeProfile(
						profileName,
						EPPVerificationCodeProfile.Status.COMPLIANT);
				theProfile.addSetCode(new EPPVerificationCode("0-abc333",
						"domain", new GregorianCalendar(2015, 4, 3, 22, 0, 0)
								.getTime()));
				theProfile.addSetCode(new EPPVerificationCode("0-abc444",
						"real-name",
						new GregorianCalendar(2015, 4, 3, 22, 0, 0).getTime()));

				theRespExt = new EPPVerificationCodeInfData(
						EPPVerificationCodeInfData.Status.COMPLIANT, theProfile);

				theProfile = new EPPVerificationCodeProfile("sample2",
						EPPVerificationCodeProfile.Status.NOT_APPLICABLE);
				theProfile.addSetCode(new EPPVerificationCode("2-abc555",
						"domain", new GregorianCalendar(2015, 4, 3, 22, 0, 0)
								.getTime()));
				theRespExt.addProfile(theProfile);

			} // ChinaVerificationCodeTst domain?
			else if (theCommand.getName().startsWith("cvct")) {

				if (theCommand.getName().startsWith(
						"cvct-transfer-noncompliant")) {
					EPPVerificationCodeProfile theProfile = new EPPVerificationCodeProfile(
							profileName,
							EPPVerificationCodeProfile.Status.PENDING_COMPLIANCE);
					theProfile.addSetCode(new EPPVerificationCode("0-abc333",
							"domain", new GregorianCalendar(2015, 4, 3, 22, 0,
									0).getTime()));
					theProfile.addMissingCode(new EPPVerificationCode(
							"real-name", new GregorianCalendar(2015, 4, 3, 22,
									0, 0).getTime()));

					theRespExt = new EPPVerificationCodeInfData(
							EPPVerificationCodeInfData.Status.PENDING_COMPLIANCE,
							theProfile);
				}
				else if (theCommand.getName().startsWith("cvct-delayed-real-name")) {
					// Was the profile explicitly set, which indicates to set
					// the compliant status.
					if (theCmdExt.hasProfile()) {
						EPPVerificationCodeProfile theProfile = new EPPVerificationCodeProfile(
								profileName,
								EPPVerificationCodeProfile.Status.COMPLIANT);
						theProfile.addSetCode(new EPPVerificationCode(
								"0-abc333", "domain", new Date(System
										.currentTimeMillis())));
						theProfile.addSetCode(new EPPVerificationCode(
								"0-abc444", "real-name", new Date(System
										.currentTimeMillis())));

						theRespExt = new EPPVerificationCodeInfData(
								EPPVerificationCodeInfData.Status.COMPLIANT,
								theProfile);
					}
					else { // Set the pendingCompliance status.
						EPPVerificationCodeProfile theProfile = new EPPVerificationCodeProfile(
								profileName,
								EPPVerificationCodeProfile.Status.PENDING_COMPLIANCE);
						theProfile.addSetCode(new EPPVerificationCode(
								"0-abc333", "domain", new Date(System
										.currentTimeMillis())));
						theProfile.addMissingCode(new EPPVerificationCode(
								"real-name", new Date(System
										.currentTimeMillis())));

						theRespExt = new EPPVerificationCodeInfData(
								EPPVerificationCodeInfData.Status.PENDING_COMPLIANCE,
								theProfile);						
					}
				}
				else {
					EPPVerificationCodeProfile theProfile = new EPPVerificationCodeProfile(
							profileName,
							EPPVerificationCodeProfile.Status.COMPLIANT);
					theProfile.addSetCode(new EPPVerificationCode("0-abc333",
							"domain", new GregorianCalendar(2015, 4, 3, 22, 0,
									0).getTime()));
					theProfile.addSetCode(new EPPVerificationCode("0-abc444",
							"real-name", new GregorianCalendar(2015, 4, 3, 22,
									0, 0).getTime()));

					theRespExt = new EPPVerificationCodeInfData(
							EPPVerificationCodeInfData.Status.COMPLIANT,
							theProfile);
				}

			}
			else {
				theRespExt = new EPPVerificationCodeInfData(
						EPPVerificationCodeInfData.Status.NOT_APPLICABLE);
			}

			if (theRespExt != null) {
				theResponse.addExtension(theRespExt);
			}

		}
		else {
			cat.debug("VerificationCodeDomainHandler.doDomainInfo: No EPPVerificationCodeInfo extension passed");
		}

		cat.debug("VerificationCodeDomainHandler.doDomainInfo: exit");
		return theEventResponse;
	}

	/**
	 * Override base handler <code>doDomainCreate</code> method and add handling
	 * of the Verification Code Extension.
	 * 
	 * @param aEvent
	 *            The <code>EPPEvent</code> that is being handled
	 * @param aData
	 *            Any data that a Server needs to send to this
	 *            <code>EPPDomaindHandler</code>
	 *
	 * @return EPPEventResponse The response that should be sent back to the
	 *         client.
	 */
	protected EPPEventResponse doDomainCreate(EPPEvent aEvent, Object aData) {
		cat.debug("VerificationCodeDomainHandler.doDomainCreate: enter");

		EPPDomainCreateCmd theCommand = (EPPDomainCreateCmd) aEvent
				.getMessage();

		EPPEventResponse theEventResponse = this.handleVerificationCode(
				"doDomainCreate", theCommand);

		if (theEventResponse == null) {
			theEventResponse = super.doDomainCreate(aEvent, aData);
		}

		cat.debug("VerificationCodeDomainHandler.doDomainCreate: exit");
		return theEventResponse;
	}

	/**
	 * Override base handler <code>doDomainUpdate</code> method and add handling
	 * of the Verification Code Extension.
	 * 
	 * @param aEvent
	 *            The <code>EPPEvent</code> that is being handled
	 * @param aData
	 *            Any data that a Server needs to send to this
	 *            <code>EPPDomaindHandler</code>
	 *
	 * @return EPPEventResponse The response that should be sent back to the
	 *         client.
	 */
	protected EPPEventResponse doDomainUpdate(EPPEvent aEvent, Object aData) {
		cat.debug("VerificationCodeDomainHandler.doDomainUpdate: enter");

		EPPDomainUpdateCmd theCommand = (EPPDomainUpdateCmd) aEvent
				.getMessage();

		EPPEventResponse theEventResponse = this.handleVerificationCode(
				"doDomainUpdate", theCommand);

		if (theEventResponse == null) {
			theEventResponse = super.doDomainUpdate(aEvent, aData);
		}

		cat.debug("VerificationCodeDomainHandler.doDomainUpdate: exit");
		return theEventResponse;
	}

	/**
	 * Override base handler <code>doDomainRenew</code> method and add handling
	 * of the Verification Code Extension.
	 * 
	 * @param aEvent
	 *            The <code>EPPEvent</code> that is being handled
	 * @param aData
	 *            Any data that a Server needs to send to this
	 *            <code>EPPDomaindHandler</code>
	 *
	 * @return EPPEventResponse The response that should be sent back to the
	 *         client.
	 */
	protected EPPEventResponse doDomainRenew(EPPEvent aEvent, Object aData) {
		cat.debug("VerificationCodeDomainHandler.doDomainRenew: enter");

		EPPDomainRenewCmd theCommand = (EPPDomainRenewCmd) aEvent.getMessage();

		EPPEventResponse theEventResponse = this.handleVerificationCode(
				"doDomainRenew", theCommand);

		if (theEventResponse == null) {
			theEventResponse = super.doDomainRenew(aEvent, aData);
		}

		cat.debug("VerificationCodeDomainHandler.doDomainRenew: exit");
		return theEventResponse;
	}

	/**
	 * Override base handler <code>doDomainDelete</code> method and add handling
	 * of the Verification Code Extension.
	 * 
	 * @param aEvent
	 *            The <code>EPPEvent</code> that is being handled
	 * @param aData
	 *            Any data that a Server needs to send to this
	 *            <code>EPPDomaindHandler</code>
	 *
	 * @return EPPEventResponse The response that should be sent back to the
	 *         client.
	 */
	protected EPPEventResponse doDomainDelete(EPPEvent aEvent, Object aData) {
		cat.debug("VerificationCodeDomainHandler.doDomainDelete: enter");

		EPPDomainDeleteCmd theCommand = (EPPDomainDeleteCmd) aEvent
				.getMessage();

		EPPEventResponse theEventResponse = this.handleVerificationCode(
				"doDomainDelete", theCommand);

		if (theEventResponse == null) {
			theEventResponse = super.doDomainDelete(aEvent, aData);
		}

		cat.debug("VerificationCodeDomainHandler.doDomainDelete: exit");
		return theEventResponse;
	}

	/**
	 * Override base handler <code>doDomainTransfer</code> method and add
	 * handling of the Verification Code Extension.
	 * 
	 * @param aEvent
	 *            The <code>EPPEvent</code> that is being handled
	 * @param aData
	 *            Any data that a Server needs to send to this
	 *            <code>EPPDomaindHandler</code>
	 *
	 * @return EPPEventResponse The response that should be sent back to the
	 *         client.
	 */
	protected EPPEventResponse doDomainTransfer(EPPEvent aEvent, Object aData) {
		cat.debug("VerificationCodeDomainHandler.doDomainTransfer: enter");

		EPPDomainTransferCmd theCommand = (EPPDomainTransferCmd) aEvent
				.getMessage();

		EPPEventResponse theEventResponse = this.handleVerificationCode(
				"doDomainTransfer", theCommand);

		if (theEventResponse == null) {
			theEventResponse = super.doDomainTransfer(aEvent, aData);
		}

		cat.debug("VerificationCodeDomainHandler.doDomainTransfer: exit");
		return theEventResponse;
	}

}
