/*******************************************************************************
 * The information in this document is proprietary to VeriSign and the VeriSign
 * Registry Business. It may not be used, reproduced, or disclosed without the
 * written approval of the General Manager of VeriSign Information Services.
 * 
 * PRIVILEGED AND CONFIDENTIAL VERISIGN PROPRIETARY INFORMATION (REGISTRY
 * SENSITIVE INFORMATION)
 * Copyright (c) 2015 VeriSign, Inc. All rights reserved.
 * **********************************************************
 */

package com.verisign.epp.codec.verificationcode;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EqualityUtil;

/**
 * Class that holds the attributes for a Verification Code Revocation List and
 * provides the following:<br>
 * <ol>
 * <li>Encode the Verification Code Revocation List to a <code>String</code>,
 * that can be written to a file.
 * <li>Decode the Verification Code Revocation List from a <code>String</code>
 * or an <code>InputStream</code>.
 * <li>Check if a specific {@link EPPSignedCode} is revoked.
 * </ol>
 */
public class VerificationCodeRevocationList {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(
			VerificationCodeRevocationList.class.getName(), EPPCatFactory
					.getInstance().getFactory());

	/**
	 * Default value of the <code>version</code> attribute.
	 */
	public static final int DEFAULT_VERSION = 1;

	/**
	 * Value of the header line (second line in file).
	 */
	private static final String HEADER_LINE = "verification-code,insertion-datetime";

	/**
	 * Version of the Verification Code Revocation List format.
	 */
	private int version = DEFAULT_VERSION;

	/**
	 * Datetime in UTC that the Verification Code Revocation List was created.
	 */
	private Date createdDate;

	/**
	 * List of revoked Verification Code's
	 */
	private List<RevokedVerificationCode> revokedVerificationCodes = new ArrayList<RevokedVerificationCode>();

	/**
	 * Default constructor. The createdDate must be set prior to calling the
	 * <code>encode()</code> method.
	 */
	public VerificationCodeRevocationList() {
	}

	/**
	 * Constructor that takes the requirement attribute value. The version
	 * defaults to the <code>DEFAULT_VERSION</code> value, and the removed
	 * VerificationCode list defaults to an empty list.
	 * 
	 * @param aCreatedDate
	 *            Datetime in UTC that the Verification Code Revocation List was
	 *            created.
	 */
	public VerificationCodeRevocationList(Date aCreatedDate) {
		this.createdDate = aCreatedDate;
	}

	/**
	 * Constructor that takes the requirement created date attribute value and
	 * the optional list of revoked VerificationCode's. The version defaults to
	 * the <code>DEFAULT_VERSION</code> value.
	 * 
	 * @param aCreatedDate
	 *            Datetime in UTC that the Verification Code Revocation List was
	 *            created.
	 * @param aRevokedVerificationCodes
	 *            List of revoked VerificationCode's
	 */
	public VerificationCodeRevocationList(Date aCreatedDate,
			List<RevokedVerificationCode> aRevokedVerificationCodes) {
		this.createdDate = aCreatedDate;
		this.setRevokedVerificationCodes(revokedVerificationCodes);
	}

	/**
	 * Gets the version of the Verification Code Revocation List format.
	 * 
	 * @return Version of the Verification Code Revocation List format with the
	 *         default of <code>DEFAULT_VERSION</code>.
	 */
	public int getVersion() {
		return this.version;
	}

	/**
	 * Sets the version of the Verification Code Revocation List format.
	 * 
	 * @param aVersion
	 *            Version of the Verification Code Revocation List format
	 */
	public void setVersion(int aVersion) {
		this.version = aVersion;
	}

	/**
	 * Gets the datetime in UTC that the Verification Code Revocation List was
	 * created.
	 * 
	 * @return Datetime in UTC that the Verification Code Revocation List was
	 *         created.
	 */
	public Date getCreatedDate() {
		return this.createdDate;
	}

	/**
	 * Sets the datetime in UTC that the Verification Code Revocation List was
	 * created.
	 * 
	 * @param aCreatedDate
	 *            Datetime in UTC that the Verification Code Revocation List was
	 *            created.
	 */
	public void setCreatedDate(Date aCreatedDate) {
		this.createdDate = aCreatedDate;
	}

	/**
	 * Gets the list of revoked <code>VerificationCode</code>'s.
	 * 
	 * @return List of revoked <code>VerificationCode</code>'s
	 */
	public List<RevokedVerificationCode> getRevokedVerificationCodes() {
		return this.revokedVerificationCodes;
	}

	/**
	 * Sets the list of revoked <code>VerificationCode</code>'s.
	 * 
	 * @param aRevokedVerificationCodes
	 *            List of revoked <code>VerificationCode</code>'s
	 */
	public void setRevokedVerificationCodes(
			List<RevokedVerificationCode> aRevokedVerificationCodes) {
		if (aRevokedVerificationCodes == null) {
			this.revokedVerificationCodes = new ArrayList<RevokedVerificationCode>();
		}
		else {
			this.revokedVerificationCodes = aRevokedVerificationCodes;
		}
	}

	/**
	 * Adds a revoked <code>VerificationCode</code> to the list of revoked
	 * <code>VerificationCode</code>'s.
	 * 
	 * @param aRevokedVerificationCode
	 *            Revoked <code>VerificationCode</code> to add to the list of
	 *            revoked <code>VerificationCode</code>'s
	 */
	public void addRevokedVerificationCode(
			RevokedVerificationCode aRevokedVerificationCode) {
		this.revokedVerificationCodes.add(aRevokedVerificationCode);
	}

	/**
	 * Is the passed verification code revoked?
	 * 
	 * @param aVerificationCode
	 *            Signed mark to check if revoked.
	 * 
	 * @return <code>true</code> if the signed mark is revoked;
	 *         <code>false</code> otherwise.
	 */
	public boolean isRevoked(EPPSignedCode aVerificationCode) {
		cat.debug("isRevoked(EPPSignedCode): enter");

		String verificationCode = aVerificationCode.getCode().getCode();

		for (RevokedVerificationCode currVerificationCode : this.revokedVerificationCodes) {

			if (currVerificationCode.getCode().equals(verificationCode)) {
				cat.debug("isRevoked(EPPSignedCode): Verification Code = "
						+ verificationCode + " is revoked");
				cat.debug("isRevoked(EPPSignedCode): exit");
				return true;
			}
		}

		cat.debug("isRevoked(EPPSignedCode): Verification Code = "
				+ verificationCode + " is not revoked");
		cat.debug("isRevoked(EPPSignedCode): exit");
		return false;
	}

	/**
	 * Encodes the Verification Code Revocation List to a <code>String</code>.
	 * 
	 * @return Encoded Verification Code Revocation List
	 * 
	 * @throws EPPEncodeException
	 *             Error encoding the Verification Code Revocation List.
	 */
	public String encode() throws EPPEncodeException {
		cat.debug("encode(): enter");

		if (this.createdDate == null) {
			throw new EPPEncodeException(
					"encode(): create date required attribute is null.");
		}

		StringBuffer strBuffer = new StringBuffer();

		// Add first line with version and created date
		strBuffer.append(Integer.toString(this.version));
		strBuffer.append(',');
		strBuffer.append(EPPUtil.encodeTimeInstant(this.createdDate));
		strBuffer.append('\n');

		// Add second header line
		strBuffer.append(HEADER_LINE);
		strBuffer.append('\n');

		for (RevokedVerificationCode currRevokedVerificationCode : this.revokedVerificationCodes) {
			strBuffer.append(currRevokedVerificationCode.encode());
			strBuffer.append('\n');
		}

		cat.debug("encode(): exit");
		return strBuffer.toString();
	}

	/**
	 * Decodes the Verification Code Revocation List from a <code>String</code>.
	 * 
	 * @param aVerificationCodeRevocationListStr
	 *            String containing the full Verification Code Revocation List.
	 * 
	 * @throws EPPDecodeException
	 *             Error decoding the Verification Code Revocation List
	 */
	public void decode(String aVerificationCodeRevocationListStr)
			throws EPPDecodeException {
		this.decode(new ByteArrayInputStream(aVerificationCodeRevocationListStr
				.getBytes()));
	}

	/**
	 * Decodes the Verification Code Revocation List from an
	 * <code>InputStream</code>.
	 * 
	 * @param aVerificationCodeRevocationListStream
	 *            <code>InputStream</code> containing the full Verification Code
	 *            Revocation List.
	 * 
	 * @throws EPPDecodeException
	 *             Error decoding the Verification Code Revocation List
	 */
	public void decode(InputStream aVerificationCodeRevocationListStream)
			throws EPPDecodeException {
		cat.debug("decode(InputStream): enter");

		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(aVerificationCodeRevocationListStream));
		String currLine;

		try {
			// Read version and created date line
			currLine = bufferedReader.readLine();

			if (currLine == null) {
				throw new EPPDecodeException(
						"decode(InputStream): Empty Verification Code revocation list.");
			}

			// Parse version and created date
			int sepIndex = currLine.indexOf(',');

			if (sepIndex == -1) {
				throw new EPPDecodeException(
						"decode(InputStream): , seperator first line of Verification Code revocation list");
			}

			// Version
			String versionStr = currLine.substring(0, sepIndex);
			this.version = Integer.parseInt(versionStr);

			// Created Date
			String createdDateStr = currLine.substring(sepIndex + 1);
			this.createdDate = EPPUtil.decodeTimeInstant(createdDateStr);

			// Read header line
			currLine = bufferedReader.readLine();

			if (currLine == null) {
				throw new EPPDecodeException(
						"decode(InputStream): Missing Verification Code revocation list header line.");
			}

			if (!currLine.equals(HEADER_LINE)) {
				throw new EPPDecodeException(
						"decode(InputStream): Invalid or missing Verification Code revocation list header line: "
								+ currLine);
			}

			// Read the revoked Verification Code's
			while ((currLine = bufferedReader.readLine()) != null) {
				RevokedVerificationCode revokedVerificationCode = new RevokedVerificationCode();

				revokedVerificationCode.decode(currLine);

				this.addRevokedVerificationCode(revokedVerificationCode);
			}
		}
		catch (IOException e) {
			throw new EPPDecodeException("Error reading Verification Code: "
					+ e);
		}

		cat.debug("decode(String): exit");
	}

	/**
	 * Clone <code>VerificationCodeRevocationList</code>.
	 * 
	 * @return clone of <code>VerificationCodeRevocationList</code>
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		VerificationCodeRevocationList clone = (VerificationCodeRevocationList) super
				.clone();

		return clone;
	}

	/**
	 * implements a deep <code>VerificationCodeRevocationList</code> compare.
	 * 
	 * @param aObject
	 *            <code>VerificationCodeRevocationList</code> instance to
	 *            compare with
	 * 
	 * @return true if equal false otherwise
	 */
	public boolean equals(Object aObject) {

		if (!(aObject instanceof VerificationCodeRevocationList)) {
			cat.error("VerificationCodeRevocationList.equals(): aObject is not an VerificationCodeRevocationList");
			return false;
		}

		VerificationCodeRevocationList other = (VerificationCodeRevocationList) aObject;

		// Version
		if (!EqualityUtil.equals(this.version, other.version)) {
			cat.error("VerificationCodeRevocationList.equals(): version not equal");
			return false;
		}

		// Created Date
		if (!EqualityUtil.equals(this.createdDate, other.createdDate)) {
			cat.error("VerificationCodeRevocationList.equals(): createdDate not equal");
			return false;
		}

		// Revoked VerificationCode's
		if (!EqualityUtil.equals(this.revokedVerificationCodes,
				other.revokedVerificationCodes)) {
			cat.error("EPPMark.equals(): revokedVerificationCodes not equal");
			return false;
		}

		return true;
	}

	/**
	 * Implementation of <code>Object.toString</code>, which will result in the
	 * full Verification OCode revocation list being converted to a
	 * <code>String</code>. If there is an error encoding the Revocation List, a
	 * <code>RuntimeException</code> is thrown.
	 * 
	 * @return Encoded Verification Code Revocation List
	 */
	public String toString() {

		try {
			return this.encode();
		}
		catch (EPPEncodeException e) {
			throw new RuntimeException(e);
		}

	}

}
