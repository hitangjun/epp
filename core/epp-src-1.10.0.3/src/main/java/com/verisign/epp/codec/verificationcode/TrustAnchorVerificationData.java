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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.verisign.epp.exception.EPPException;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EqualityUtil;

/**
 * A class to simulate validating a verification code value 
 * by utilizing the encoding of the trust anchor alias in a trust store, 
 * which could be formatted in the following way using Augmented Backus-Naur Form (ABNF) grammar:<br>
 * <pre>
 * alias = codevspid ":" vspid ":" profilename ":" codetypes ":" shortname ":" certnumber
 * codevspid  = 1*DIGIT ; Verification Code vsp-id value
 * vspid      = 1*DIGIT ; Surrogate VSP Identifier
 * profilename= 1*(DIGIT / ALPHA) ; Name of profile
 * codetypes  = codetype *( “~“ codetype) ; List of supported signed code types
 * codetype   = 1*(DIGIT / ALPHA / “-“) ; <verificationCode:signedCode> “type” attribute
 * shortname  = 1*(DIGIT / ALPHA) ; VSP Short name
 * certnumber = 1*DIGIT  ; Unique certificate number
 * </pre> 
 * An example alias for the EPPSDK could be &quot;0:-1:china:domain~real-name:eppsdk:1&quot;
 */
public class TrustAnchorVerificationData {

	/** Log4j category for logging */
	private static Logger cat = Logger.getLogger(
			TrustAnchorVerificationData.class.getName(), EPPCatFactory
					.getInstance().getFactory());

	/**
	 * Undefined integer value of the verification data.
	 */
	public static int UNDEFINED = -1;

	/**
	 * Verification Code vsp-id value per draft-gould-eppext-verificationcode.
	 */
	private int codeVspId = UNDEFINED;

	/**
	 * Internal (surrogate) Verification Service Provider (VSP) identifier. This
	 * can be matched up with records in a database.
	 */
	private int vspId = UNDEFINED;

	/**
	 * Name of the locality profile associated with the Verification Service
	 * Provider (VSP).
	 */
	private String profileName;

	/**
	 * List of code types, per the &quot;type&quot; attribute of
	 * &lt;verificationCode:code&gt;, supported by the Verification Service
	 * Provider (VSP).
	 */
	private List<String> codeTypes = new ArrayList<String>();

	/**
	 * Short name for the Verification Service Provider (VSP).
	 */
	private String shortName;

	/**
	 * Certificate number of the Trust Anchor for the Verification Service
	 * Provider (VSP).
	 */
	private int certNumber = UNDEFINED;

	/**
	 * Field separator used for the trust anchor alias.
	 */
	private static String FIELD_SEP = ":";

	/**
	 * Code types separator.
	 */
	private static String CODE_TYPE_SEP = "~";

	/**
	 * Number of tokens that must be in the trust anchor alias.
	 */
	private static int NUM_TOKENS = 6;

	/**
	 * Default constructor.
	 */
	public TrustAnchorVerificationData() {
	}

	/**
	 * Constructor that takes a trust anchor alias from a trust store to be
	 * decoded.
	 * 
	 * @param aTrustAnchorAlias
	 *            Alias of the trust anchor from a trust store.
	 *            
	 * @exception EPPException Error decoding the trust anchor alias
	 */
	public TrustAnchorVerificationData(String aTrustAnchorAlias) throws EPPException {
		this.decode(aTrustAnchorAlias);
	}

	/**
	 * Has the Verification Code vsp-id value been set?
	 * 
	 * @return <code>true</code> of the Verification Code vsp-id value has been
	 *         set; <code>false</code> otherwise.
	 */
	public boolean hasCodeVspId() {
		return (this.codeVspId != UNDEFINED ? true : false);
	}

	/**
	 * Gets the Verification Code vsp-id value per
	 * draft-gould-eppext-verificationcode.
	 * 
	 * @return Verification Code vsp-id value per
	 *         draft-gould-eppext-verificationcode if defined;
	 *         {@link #UNDEFINED} otherwise.
	 */
	public int getCodeVspId() {
		return this.codeVspId;
	}

	/**
	 * Sets the Verification Code vsp-id value per
	 * draft-gould-eppext-verificationcode.
	 * 
	 * @param aCodeVspId
	 *            Verification Code vsp-id value per
	 *            draft-gould-eppext-verificationcode
	 */
	public void setCodeVspId(int aCodeVspId) {
		this.codeVspId = aCodeVspId;
	}

	/**
	 * Has the internal (surrogate) Verification Service Provider (VSP)
	 * identifier been set?
	 * 
	 * @return <code>true</code> of the internal (surrogate) Verification
	 *         Service Provider (VSP) identifier has been set;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasVspId() {
		return (this.vspId != UNDEFINED ? true : false);
	}

	/**
	 * Gets the internal (surrogate) Verification Service Provider (VSP)
	 * identifier. This can be matched up with records in a database.
	 * 
	 * @return internal (surrogate) Verification Service Provider (VSP)
	 *         identifier if defined; {@link #UNDEFINED} otherwise.
	 */
	public int getVspId() {
		return this.vspId;
	}

	/**
	 * Sets the internal (surrogate) Verification Service Provider (VSP)
	 * identifier. This can be matched up with records in a database.
	 * 
	 * @param aVspId
	 *            Internal (surrogate) Verification Service Provider (VSP)
	 *            identifier.
	 */
	public void setVspId(int aVspId) {
		this.vspId = aVspId;
	}

	/**
	 * Has the locality profile associated with the Verification Service
	 * Provider (VSP) been set?
	 * 
	 * @return <code>true</code> of the locality profile associated with the
	 *         Verification Service Provider (VSP) has been set;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasProfileName() {
		return (this.profileName != null ? true : false);
	}

	/**
	 * Gets the name of the locality profile associated with the Verification
	 * Service Provider (VSP).
	 * 
	 * @return The locality profile associated with the Verification Service
	 *         Provider (VSP) if defined; <code>null</code> otherwise.
	 */
	public String getProfileName() {
		return this.profileName;
	}

	/**
	 * Sets the name of the locality profile associated with the Verification
	 * Service Provider (VSP).
	 * 
	 * @param aProfileName
	 *            Locality profile associated with the Verification Service
	 *            Provider (VSP).
	 */
	public void setProfileName(String aProfileName) {
		this.profileName = aProfileName;
	}

	/**
	 * Are there any code types?
	 * 
	 * @return <code>true</code> if there are code types; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasCodeTypes() {
		return (this.codeTypes != null) && (!this.codeTypes.isEmpty());
	}

	/**
	 * Adds a code type to the list of code types.
	 * 
	 * @param aCodeType
	 *            Code type to add to the list of code types.
	 */
	public void addCodeType(String aCodeType) {
		if (this.codeTypes == null) {
			this.codeTypes = new ArrayList<String>();
		}

		this.codeTypes.add(aCodeType);
	}

	/**
	 * Gets the list of code types, per the &quot;type&quot; attribute of
	 * &lt;verificationCode:code&gt;, supported by the Verification Service
	 * Provider (VSP).
	 * 
	 * @return List of code types supported by the Verification Service Provider
	 *         (VSP).
	 */
	public List<String> getCodeTypes() {
		return this.codeTypes;
	}

	/**
	 * Sets the list of code types, per the &quot;type&quot; attribute of
	 * &lt;verificationCode:code&gt;, supported by the Verification Service
	 * Provider (VSP).
	 * 
	 * @param aCodeTypes
	 *            List of code types supported by the Verification Service
	 *            Provider (VSP).
	 */
	public void setCodeTypes(List<String> aCodeTypes) {
		this.codeTypes = aCodeTypes;
	}

	/**
	 * Has VSP short name been set?
	 * 
	 * @return <code>true</code> of the VSP short name has been set;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasShortName() {
		return (this.shortName != null ? true : false);
	}

	/**
	 * Gets the short name of the Verification Service Provider (VSP). The short
	 * name must only contain alphanumeric characters.
	 * 
	 * @return Short name of the Verification Service Provider (VSP) if defined;
	 *         <code>null</code> otherwise.
	 */
	public String getShortName() {
		return this.shortName;
	}

	/**
	 * Sets the short name of the Verification Service Provider (VSP). The short
	 * name must only contain alphanumeric characters.
	 * 
	 * @param aShortName
	 *            Short name of the Verification Service Provider (VSP).
	 */
	public void setShortName(String aShortName) {
		this.shortName = aShortName;
	}

	/**
	 * Has the Verification Service Provider (VSP) trust anchor certificate
	 * number been set?
	 * 
	 * @return <code>true</code> of the Verification Service Provider (VSP)
	 *         trust anchor certificate number has been set; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasCertNumber() {
		return (this.certNumber != UNDEFINED ? true : false);
	}

	/**
	 * Gets the Verification Service Provider (VSP) trust anchor certificate
	 * number. This must be unique across all trust anchor certificates for the
	 * VSP.
	 * 
	 * @return Verification Service Provider (VSP) trust anchor certificate
	 *         number if defined; <code>null</code> otherwise.
	 */
	public int getCertNumber() {
		return this.certNumber;
	}

	/**
	 * Sets the Verification Service Provider (VSP) trust anchor certificate
	 * number. This must be unique across all trust anchor certificates for the
	 * VSP.
	 * 
	 * @param aCertNumber
	 *            Unique certificate number for the VSP
	 */
	public void setCertNumber(int aCertNumber) {
		this.certNumber = aCertNumber;
	}

	/**
	 * Decodes a trust anchor alias from a trust store to set the trust anchor
	 * verification data.
	 * 
	 * @param aTrustAnchorAlias
	 *            Alias of the trust anchor from a trust store.
	 * 
	 * 
	 * @throws EPPException
	 *             Error decoding the trust anchor alias
	 */
	public void decode(String aTrustAnchorAlias) throws EPPException {
		String[] tokens = aTrustAnchorAlias.split(FIELD_SEP);

		if (tokens.length != NUM_TOKENS) {
			throw new EPPException("Trust anchor alias " + tokens.length
					+ " tokens != " + NUM_TOKENS);
		}

		// codeVspId
		try {
			this.codeVspId = Integer.parseInt(tokens[0]);
		}
		catch (NumberFormatException ex) {
			this.codeVspId = UNDEFINED;
		}
		
		// vspId
		try {
			this.vspId = Integer.parseInt(tokens[1]);			
		}
		catch (NumberFormatException ex) {
			this.vspId = UNDEFINED;
		}
		
		// profileName
		this.profileName = tokens[2];

		// codeTypes
		String theCodeTypesToken = tokens[3];
		String[] theCodeTypes = theCodeTypesToken.split(CODE_TYPE_SEP);
		this.codeTypes = Arrays.asList(theCodeTypes);

		// shortName
		this.shortName = tokens[4];
		
		// certNumber
		try {
			this.certNumber = Integer.parseInt(tokens[5]);			
		}
		catch (NumberFormatException ex) {
			this.certNumber = UNDEFINED;
		}
	}

	/**
	 * Encodes a trust anchor anchor for a trust store.
	 * 
	 * @return Trust anchor alias for a trust store.
	 * 
	 * @throws EPPException
	 *             Error encoding the trust anchor alias
	 */
	public String encode() throws EPPException {
		
		// Verify that all required fields have been set.
		if (!this.hasCodeVspId()) {
			throw new EPPException("Required codeVspId attribute is not set");
		}
		if (!this.hasProfileName()) {
			throw new EPPException("Required profileName attribute is not set");
		}
		if (!this.hasCodeTypes()) {
			throw new EPPException("Required codeTypes attribute is not set");
		}
		if (!this.hasShortName()) {
			throw new EPPException("Required shortName attribute is not set");
		}
		if (!this.hasCertNumber()) {
			throw new EPPException("Required certNumber attribute is not set");
		}
		
		// codeVspId
		String retStr = "" + this.codeVspId;
		
		// vspId
		retStr += FIELD_SEP + this.vspId;
		
		// profileName
		retStr += FIELD_SEP + this.profileName;
		
		// codeTypes
		boolean firstCodeType = true;
		for (String codeType : this.codeTypes) {
			if (!firstCodeType) {
				retStr += CODE_TYPE_SEP;
			}
			else {
				retStr += FIELD_SEP;
				firstCodeType = false;
			}
			
			retStr += codeType;
		}
		
		// shortName
		retStr += FIELD_SEP + this.shortName;
	
		// certNumber
		retStr += FIELD_SEP + this.certNumber;
		
		return retStr;
	}
	
	/**
	 * implements a deep <code>TrustAnchorVerificationData</code> compare.
	 * 
	 * @param aObject
	 *            <code>TrustAnchorVerificationData</code> instance to compare
	 *            with
	 * 
	 * @return <code>true</code> if equal <code>false</code> otherwise
	 */
	public boolean equals(Object aObject) {

		if (!(aObject instanceof TrustAnchorVerificationData)) {
			cat.error("TrustAnchorVerificationData.equals(): "
					+ aObject.getClass().getName()
					+ " not TrustAnchorVerificationData instance");

			return false;
		}

		TrustAnchorVerificationData other = (TrustAnchorVerificationData) aObject;

		// codeVspId
		if (!EqualityUtil.equals(this.codeVspId, other.codeVspId)) {
			cat.error("TrustAnchorVerificationData.equals(): codeVspId not equal");
			return false;
		}

		// vspId
		if (!EqualityUtil.equals(this.vspId, other.vspId)) {
			cat.error("TrustAnchorVerificationData.equals(): vspId not equal");
			return false;
		}

		// profileName
		if (!EqualityUtil.equals(this.profileName, other.profileName)) {
			cat.error("TrustAnchorVerificationData.equals(): profileName not equal");
			return false;
		}

		// codeTypes
		if (!EqualityUtil.equals(this.codeTypes, other.codeTypes)) {
			cat.error("TrustAnchorVerificationData.equals(): codeTypes not equal");
			return false;
		}

		// shortName
		if (!EqualityUtil.equals(this.shortName, other.shortName)) {
			cat.error("TrustAnchorVerificationData.equals(): shortName not equal");
			return false;
		}
		
		// certNumber
		if (!EqualityUtil.equals(this.certNumber, other.certNumber)) {
			cat.error("TrustAnchorVerificationData.equals(): certNumber not equal");
			return false;
		}
		
		return true;
	}
	
}
