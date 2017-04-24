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
 * Extension to the info response to return the verification information. The
 * {@link EPPVerificationCodeInfo} info command extension defines interest in
 * returning the verification information in the extension to the info response.
 * 
 * @see com.verisign.epp.codec.verificationcode.EPPVerificationCodeInfo
 */
public class EPPVerificationCodeInfData implements EPPCodecComponent {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(
			EPPVerificationCodeInfData.class.getName(), EPPCatFactory
					.getInstance().getFactory());

	/**
	 * Constant for the info response extension local name
	 */
	public static final String ELM_LOCALNAME = "infData";

	/**
	 * Constant for the info response extension tag
	 */
	public static final String ELM_NAME = EPPVerificationCodeExtFactory.NS_PREFIX
			+ ":" + ELM_LOCALNAME;

	/**
	 * Verification statuses that include:<br>
	 * <ul>
	 * <li><code>NOT_APPLICABLE</code> - The status is not applicable to the
	 * client since there is no assigned verification profile.</li>
	 * <li><code>NON_COMPLIANT</code> - The object is non-compliant according to
	 * the verification profile.</li>
	 * <li><code>PENDING_COMPLIANCE</code> - The object is not in compliance
	 * with the verification profile, but has a grace period to set the required
	 * set of verification codes, as reflected by the due date of the
	 * verification code type.</li>
	 * <li><code>COMPLIANT</code> The object is compliant with the verification
	 * profile.</li>
	 * </ul>
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
	 * XML local name for the status
	 */
	private static final String ELM_STATUS = "status";

	/**
	 * Status of the verification with a default value of
	 * <code>Status.NON_COMPLIANT</code>.
	 */
	Status status = Status.NON_COMPLIANT;

	/**
	 * List of missing verification codes.
	 */
	private List<EPPVerificationCodeProfile> profiles = new ArrayList<EPPVerificationCodeProfile>();

	/**
	 * Create an <code>EPPVerificationCodeInfData</code> instance.
	 */
	public EPPVerificationCodeInfData() {
	}

	/**
	 * Create a EPPVerificationCodeInfData instance with the required status.
	 * 
	 * @param aStatus
	 *            Status of the verification
	 */
	public EPPVerificationCodeInfData(Status aStatus) {
		this.status = aStatus;
	}

	/**
	 * Create a EPPVerificationCodeInfData instance with the required status and
	 * the a single profile.
	 * 
	 * @param aStatus
	 *            Status of the verification
	 * @param aProfile
	 *            Single verification profile
	 */
	public EPPVerificationCodeInfData(Status aStatus,
			EPPVerificationCodeProfile aProfile) {
		this.status = aStatus;
		this.addProfile(aProfile);
	}

	/**
	 * Create a EPPVerificationCodeInfData instance with the required status and
	 * the optional profiles.
	 * 
	 * @param aStatus
	 *            Status of the verification
	 * @param aProfiles
	 *            OPTIONAL verification profiles
	 */
	public EPPVerificationCodeInfData(Status aStatus,
			List<EPPVerificationCodeProfile> aProfiles) {
		this.status = aStatus;
		this.profiles = aProfiles;
	}

	/**
	 * Clone <code>EPPVerificationCodeInfData</code>.
	 * 
	 * @return clone of <code>EPPVerificationCodeInfData</code>
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPVerificationCodeInfData clone = (EPPVerificationCodeInfData) super
				.clone();

		// Status
		clone.status = this.status;

		// Profiles
		clone.profiles = new ArrayList<EPPVerificationCodeProfile>(
				this.profiles.size());
		for (EPPVerificationCodeProfile profile : this.profiles)
			clone.profiles.add((EPPVerificationCodeProfile) profile.clone());

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

		Element root = aDocument.createElementNS(
				EPPVerificationCodeExtFactory.NS, ELM_NAME);

		// Status
		EPPUtil.encodeString(aDocument, root, this.status.toString(),
				EPPVerificationCodeExtFactory.NS,
				EPPVerificationCodeExtFactory.NS_PREFIX + ":" + ELM_STATUS);

		// Profiles
		if (this.hasProfiles()) {
			EPPUtil.encodeCompList(aDocument, root, this.profiles);
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

		// Status
		this.status = Status.getStatus(EPPUtil.decodeString(aElement,
				EPPVerificationCodeExtFactory.NS, ELM_STATUS));

		// Profiles
		this.profiles = EPPUtil.decodeCompList(aElement,
				EPPVerificationCodeExtFactory.NS,
				EPPVerificationCodeProfile.ELM_NAME,
				EPPVerificationCodeProfile.class);
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

		if (!(aObject instanceof EPPVerificationCodeInfData)) {
			cat.error("EPPVerificationCodeInfData.equals(): "
					+ aObject.getClass().getName()
					+ " not EPPVerificationCodeInfData instance");

			return false;
		}

		EPPVerificationCodeInfData other = (EPPVerificationCodeInfData) aObject;

		// Status
		if (!EqualityUtil.equals(this.status, other.status)) {
			cat.error("EPPVerificationCodeInfData.equals(): status not equal");
			return false;
		}

		// Profiles
		if (!EqualityUtil.equals(this.profiles, other.profiles)) {
			cat.error("EPPVerificationCodeInfData.equals(): profiles not equal");
			return false;
		}

		return true;
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
	 * Are there any profiles?
	 * 
	 * @return <code>true</code> if there are profiles; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasProfiles() {
		return !this.profiles.isEmpty();
	}

	/**
	 * Returns the list of profiles. An empty list indicates that there are no
	 * profiles.
	 * 
	 * @return List of profiles.
	 */
	public List<EPPVerificationCodeProfile> getProfiles() {
		return this.profiles;
	}

	/**
	 * Sets the profiles.
	 * 
	 * @param aProfiles
	 *            The list of profiles. Set to <code>null</code> or an empty
	 *            list to indicate that there are no profiles.
	 */
	public void setProfiles(List<EPPVerificationCodeProfile> aProfiles) {
		if (aProfiles == null) {
			this.profiles = new ArrayList<EPPVerificationCodeProfile>();
		}
		else {
			this.profiles = aProfiles;
		}
	}

	/**
	 * Add a profile to the list of profiles.
	 * 
	 * @param aProfile
	 *            Profile to add to the list of profiles.
	 */
	public void addProfile(EPPVerificationCodeProfile aProfile) {
		this.profiles.add(aProfile);
	}

	/**
	 * Is there a single profile?
	 * 
	 * @return <code>true</code> if there is one profiles; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasProfile() {
		return !this.profiles.isEmpty() && this.profiles.size() == 1;
	}

	/**
	 * Gets the single profile.
	 * 
	 * @return Single profile that is set if defined; <code>null</code>
	 *         otherwise.
	 */
	public EPPVerificationCodeProfile getProfile() {
		if (this.hasProfiles()) {
			return this.profiles.get(0);
		}
		else {
			return null;
		}
	}

	/**
	 * Sets the single profile. If there is a list of profiles currently set,
	 * this method will clear the list and set the profiles to a single profile
	 * consisting of <code>aProfile</code>.
	 * 
	 * @param aProfile
	 *            Single profile to set.
	 */
	public void setProfile(EPPVerificationCodeProfile aProfile) {
		this.profiles = new ArrayList<EPPVerificationCodeProfile>();
		this.profiles.add(aProfile);
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