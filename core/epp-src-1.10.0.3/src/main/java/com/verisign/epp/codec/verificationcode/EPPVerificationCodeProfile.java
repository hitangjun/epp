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
 ***********************************************************/

package com.verisign.epp.codec.verificationcode;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.verisign.epp.codec.gen.EPPCodecComponent;
import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EqualityUtil;

/**
 * Information associated with compliance with a verification code profile. A
 * Verification Profile defines the set of verification code types, the commands
 * that the verification code types are required, supported, or not supported,
 * and the grace period by which the verification code types MUST be set. A
 * server MAY support many verification profiles, each with a unique name and a
 * unique verification policy that is implemented by the server.
 */
public class EPPVerificationCodeProfile implements EPPCodecComponent {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(
			EPPVerificationCodeProfile.class.getName(), EPPCatFactory
					.getInstance().getFactory());

	/**
	 * Constant for the profile local name
	 */
	public static final String ELM_LOCALNAME = "profile";

	/**
	 * Constant for the profile tag
	 */
	public static final String ELM_NAME = EPPVerificationCodeExtFactory.NS_PREFIX
			+ ":" + ELM_LOCALNAME;

	/**
	 * Verification statuses that include:<br>
	 * <ul>
	 * <li><code>NOT_APPLICABLE</code> - The profile status is not applicable to
	 * the client based on the assigned verification profiles or the profile
	 * specified.</li>
	 * <li><code>NON_COMPLIANT</code> - The object is non-compliant according to
	 * the verification profile.</li>
	 * <li><code>PENDING_COMPLIANCE</code> - The object is not in compliance
	 * with the verification profile, but has a grace period to set the required
	 * set of verification codes, as reflected by the due date of the
	 * verification code type.</li>
	 * <li><code>COMPLIANT</code> The object is compliant with the verification
	 * profile.</li>
	 */
	public enum Status {
		NOT_APPLICABLE("notApplicable"), NON_COMPLIANT("nonCompliant"), PENDING_COMPLIANCE(
				"pendingCompliance"), COMPLIANT("compliant");

		private final String statusStr;

		/**
		 * Define the string value for the enumerated value.
		 * 
		 * @param aStatusStr
		 *            Enumerated value string
		 */
		Status(String aStatusStr) {
			this.statusStr = aStatusStr;
		}

		/**
		 * Get the status enumerated value given the matching string.
		 * 
		 * @param aString
		 *            <code>Status</code> enumerated string to convert to an
		 *            enumerated <code>Status</code> instance.
		 * 
		 * @return Enumerated <code>Status</code> value matching the
		 *         <code>String</code>.
		 * 
		 * @throws InvalidParameterException
		 *             If <code>aString</code> does not match an enumerated
		 *             <code>Status</code> string value.
		 */
		public static Status getStatus(String aString) {
			if (aString.equals(NOT_APPLICABLE.statusStr)) {
				return NOT_APPLICABLE;
			}
			else if (aString.equals(NON_COMPLIANT.statusStr)) {
				return NON_COMPLIANT;
			}
			else if (aString.equals(PENDING_COMPLIANCE.statusStr)) {
				return PENDING_COMPLIANCE;
			}
			else if (aString.equals(COMPLIANT.statusStr)) {
				return COMPLIANT;
			}
			else {
				throw new InvalidParameterException("Status enum value of "
						+ aString + " is not valid.");
			}
		}

		/**
		 * Convert the enumerated <code>Status</code> value to a
		 * <code>String</code>.
		 */
		public String toString() {
			return this.statusStr;
		}
	}

	/**
	 * Attribute name of the the profile name
	 */
	private static final String ATTR_PROFILE_NAME = "name";

	/**
	 * XML local name for the status
	 */
	private static final String ELM_STATUS = "status";

	/**
	 * XML local name for the missing codes container element
	 */
	private static final String ELM_MISSING = "missing";

	/**
	 * XML local name for the set codes container element
	 */
	private static final String ELM_SET = "set";

	/**
	 * Status of the verification with a default value of
	 * <code>Status.NON_COMPLIANT</code>.
	 */
	Status status = Status.NON_COMPLIANT;

	/**
	 * The required name of the profile
	 */
	String profileName;

	/**
	 * List of missing verification codes.
	 */
	private List<EPPVerificationCode> missingCodes = new ArrayList<EPPVerificationCode>();

	/**
	 * List of set verification codes.
	 */
	private List<EPPVerificationCode> setCodes = new ArrayList<EPPVerificationCode>();

	/**
	 * Create an <code>EPPVerificationCodeInfData</code> instance.
	 */
	public EPPVerificationCodeProfile() {
	}

	/**
	 * Create a EPPVerificationCodeInfData instance with the required attributes
	 * status.
	 * 
	 * @param aProfileName
	 *            Name of the profile
	 * @param aStatus
	 *            Status of the verification
	 */
	public EPPVerificationCodeProfile(String aProfileName, Status aStatus) {
		this.profileName = aProfileName;
		this.status = aStatus;
	}

	/**
	 * Create a EPPVerificationCodeInfData instance with the all required and
	 * optional attributes.
	 * 
	 * @param aProfileName
	 *            Name of the profile
	 * @param aStatus
	 *            Status of the verification
	 * @param aMissingCodes
	 *            OPTIONAL missing codes. Set to <code>null</code> if there are
	 *            no missing codes.
	 * @param aSetCodes
	 *            OPTIONAL set codes. Set to <code>null</code> if there are no
	 *            set codes.
	 */
	public EPPVerificationCodeProfile(String aProfileName, Status aStatus,
			List<EPPVerificationCode> aMissingCodes,
			List<EPPVerificationCode> aSetCodes) {
		this.status = aStatus;
		this.profileName = aProfileName;
		this.setMissingCodes(aMissingCodes);
		this.setSetCodes(aSetCodes);
	}

	/**
	 * Clone <code>EPPVerificationCodeInfData</code>.
	 * 
	 * @return clone of <code>EPPVerificationCodeInfData</code>
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPVerificationCodeProfile clone = (EPPVerificationCodeProfile) super
				.clone();

		// Status
		clone.status = this.status;

		// Profile Name
		clone.profileName = this.profileName;

		// Missing Codes
		clone.missingCodes = new ArrayList<EPPVerificationCode>(
				this.missingCodes.size());
		for (EPPVerificationCode item : this.missingCodes)
			clone.missingCodes.add((EPPVerificationCode) item.clone());

		// Set Codes
		clone.setCodes = new ArrayList<EPPVerificationCode>(
				this.setCodes.size());
		for (EPPVerificationCode item : this.setCodes)
			clone.setCodes.add((EPPVerificationCode) item.clone());

		return clone;
	}

	/**
	 * Sets all this instance's data in the given XML document
	 * 
	 * @param aDocument
	 *            a DOM Document to attach data to.
	 * @return The root element of this component.
	 * 
	 * @throws EPPEncodeException
	 *             Thrown if any errors prevent encoding.
	 */
	public Element encode(Document aDocument) throws EPPEncodeException {

		if (aDocument == null) {
			throw new EPPEncodeException("aDocument is null"
					+ " on in EPPVerificationCodeInfData.encode(Document)");
		}

		if (this.profileName == null) {
			throw new EPPEncodeException(
					"Required profileName is null on in EPPVerificationCodeInfData.encode(Document)");
		}
		if (this.status == null) {
			throw new EPPEncodeException(
					"Required status is null on in EPPVerificationCodeInfData.encode(Document)");
		}

		Element root = aDocument.createElementNS(
				EPPVerificationCodeExtFactory.NS, ELM_NAME);

		// Profile Name
		root.setAttribute(ATTR_PROFILE_NAME, this.profileName);

		// Status
		EPPUtil.encodeString(aDocument, root, this.status.toString(),
				EPPVerificationCodeExtFactory.NS,
				EPPVerificationCodeExtFactory.NS_PREFIX + ":" + ELM_STATUS);

		// Missing Codes
		if (this.hasMissingCodes()) {
			Element missing = aDocument
					.createElementNS(EPPVerificationCodeExtFactory.NS,
							EPPVerificationCodeExtFactory.NS_PREFIX + ":"
									+ ELM_MISSING);
			EPPUtil.encodeCompList(aDocument, missing, this.missingCodes);
			root.appendChild(missing);
		}

		// Set Codes
		if (this.hasSetCodes()) {
			Element set = aDocument.createElementNS(
					EPPVerificationCodeExtFactory.NS,
					EPPVerificationCodeExtFactory.NS_PREFIX + ":" + ELM_SET);
			EPPUtil.encodeCompList(aDocument, set, this.setCodes);
			root.appendChild(set);
		}

		return root;
	}

	/**
	 * Decode the EPPVerificationCodeInfData component
	 * 
	 * @param aElement
	 * @throws EPPDecodeException
	 */
	public void decode(Element aElement) throws EPPDecodeException {

		// Profile Name
		this.profileName = aElement.getAttribute(ATTR_PROFILE_NAME);

		// Status
		this.status = Status.getStatus(EPPUtil.decodeString(aElement,
				EPPVerificationCodeExtFactory.NS, ELM_STATUS));

		// Missing Codes
		Element theMissingElm = EPPUtil.getElementByTagNameNS(aElement,
				EPPVerificationCodeExtFactory.NS, ELM_MISSING);
		if (theMissingElm != null) {
			this.missingCodes = EPPUtil.decodeCompList(theMissingElm,
					EPPVerificationCodeExtFactory.NS,
					EPPVerificationCode.ELM_NAME, EPPVerificationCode.class);
		}
		else {
			this.missingCodes = new ArrayList<EPPVerificationCode>();
		}

		// Set Codes
		Element theSetElm = EPPUtil.getElementByTagNameNS(aElement,
				EPPVerificationCodeExtFactory.NS, ELM_SET);
		if (theSetElm != null) {
			this.setCodes = EPPUtil.decodeCompList(theSetElm,
					EPPVerificationCodeExtFactory.NS,
					EPPVerificationCode.ELM_NAME, EPPVerificationCode.class);
		}
		else {
			this.setCodes = new ArrayList<EPPVerificationCode>();
		}

	}

	/**
	 * implements a deep <code>EPPVerificationCodeInfData</code> compare.
	 * 
	 * @param aObject
	 *            <code>EPPVerificationCodeInfData</code> instance to compare
	 *            with
	 * 
	 * @return true if equal false otherwise
	 */
	public boolean equals(Object aObject) {

		if (!(aObject instanceof EPPVerificationCodeProfile)) {
			cat.error("EPPVerificationCodeInfData.equals(): "
					+ aObject.getClass().getName()
					+ " not EPPVerificationCodeInfData instance");

			return false;
		}

		EPPVerificationCodeProfile other = (EPPVerificationCodeProfile) aObject;

		// Profile Name
		if (!EqualityUtil.equals(this.profileName, other.profileName)) {
			cat.error("EPPVerificationCodeInfData.equals(): profile not equal");
			return false;
		}

		// Status
		if (!EqualityUtil.equals(this.status, other.status)) {
			cat.error("EPPVerificationCodeInfData.equals(): status not equal");
			return false;
		}

		// Missing Codes
		if (!EqualityUtil.equals(this.missingCodes, other.missingCodes)) {
			cat.error("EPPVerificationCodeInfData.equals(): missingCodes not equal");
			return false;
		}

		// Set Codes
		if (!EqualityUtil.equals(this.setCodes, other.setCodes)) {
			cat.error("EPPVerificationCodeInfData.equals(): setCodes not equal");
			return false;
		}

		return true;
	}

	/**
	 * Gets the verification profile name.
	 * 
	 * @return Verification profile if defined; <code>null</code> otherwise.
	 */
	public String getProfileName() {
		return this.profileName;
	}

	/**
	 * Sets the verification profile name.
	 * 
	 * @param aProfileName
	 *            Verification profile
	 */
	public void setProfileName(String aProfileName) {
		this.profileName = aProfileName;
	}

	/**
	 * Gets the verification status.
	 * 
	 * @return Verification status
	 */
	public Status getStatus() {
		return this.status;
	}

	/**
	 * Sets the verification status.
	 * 
	 * @param aStatus
	 *            Verification status
	 */
	public void setStatus(Status aStatus) {
		this.status = aStatus;
	}

	/**
	 * Are there any missing codes?
	 * 
	 * @return <code>true</code> if there are missing codes; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasMissingCodes() {
		return (this.missingCodes != null) && (!this.missingCodes.isEmpty());
	}

	/**
	 * Returns the list of missing codes. An empty list indicates that there are
	 * no missing codes.
	 * 
	 * @return List of missing codes.
	 */
	public List<EPPVerificationCode> getMissingCodes() {
		return this.missingCodes;
	}

	/**
	 * Sets the missing codes.
	 * 
	 * @param aMissingCodes
	 *            The list of missing codes. Set to <code>null</code> or an
	 *            empty list to indicate that there are no missing codes.
	 */
	public void setMissingCodes(List<EPPVerificationCode> aMissingCodes) {
		if (aMissingCodes == null) {
			this.missingCodes = new ArrayList<EPPVerificationCode>();
		}
		else {
			this.missingCodes = aMissingCodes;
		}
	}

	/**
	 * Add a missing code to the list of missing codes.
	 * 
	 * @param aMissingCode
	 *            Missing code to add to the list of missing codes.
	 */
	public void addMissingCode(EPPVerificationCode aMissingCode) {
		this.missingCodes.add(aMissingCode);
	}

	/**
	 * Are there any set codes?
	 * 
	 * @return <code>true</code> if there are set codes; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasSetCodes() {
		return !this.setCodes.isEmpty();
	}

	/**
	 * Returns the list of set codes. An empty list indicates that there are no
	 * set codes.
	 * 
	 * @return List of set codes.
	 */
	public List<EPPVerificationCode> getSetCodes() {
		return this.setCodes;
	}

	/**
	 * Sets the set codes.
	 * 
	 * @param aSetCodes
	 *            The list of set codes. Set to <code>null</code> or an empty
	 *            list to indicate that there are no set codes.
	 */
	public void setSetCodes(List<EPPVerificationCode> aSetCodes) {
		if (aSetCodes == null) {
			this.setCodes = new ArrayList<EPPVerificationCode>();
		}
		else {
			this.setCodes = aSetCodes;
		}
	}

	/**
	 * Add a set code to the list of set codes.
	 * 
	 * @param aSetCode
	 *            Set code to add to the list of set codes.
	 */
	public void addSetCode(EPPVerificationCode aSetCode) {
		this.setCodes.add(aSetCode);
	}

	/**
	 * Implementation of <code>Object.toString</code>, which will result in an
	 * indented XML <code>String</code> representation of the concrete
	 * <code>EPPCodecComponent</code>.
	 *
	 * @return Indented XML <code>String</code> if successful;
	 *         <code>ERROR</code> otherwise.
	 */
	public String toString() {
		return EPPUtil.toString(this);
	}

}