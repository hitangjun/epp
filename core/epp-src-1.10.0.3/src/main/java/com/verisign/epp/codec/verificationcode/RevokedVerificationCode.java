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

import java.util.Date;

import org.apache.log4j.Logger;

import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EqualityUtil;

/**
 * A revoked verification code that includes the attributes:<br>
 * <ul>
 * <li>Revoked verification code.</li>
 * <li>Revocation datetime in UTC of the verification code.</li>
 * </ul>
 */
public class RevokedVerificationCode implements java.io.Serializable, Cloneable {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(RevokedVerificationCode.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * Verification code
	 */
	private String code;

	/**
	 * Revocation datetime in UTC of the SMD.
	 */
	private Date revokedDate;

	/**
	 * Default constructor. The <code>code</code> and the
	 * <code>revokedDate</code> attributes must be set prior to calling
	 * <code>encode()</code>.
	 */
	public RevokedVerificationCode() {
	}

	/**
	 * Constructor that takes the required <code>code</code> and
	 * <code>revokedDate</code> attribute values.
	 * 
	 * @param aCode
	 *            Verification code
	 * @param aRevokedDate
	 *            Revocation datetime of the verification code.
	 */
	public RevokedVerificationCode(String aCode, Date aRevokedDate) {
		this.code = aCode;
		this.revokedDate = aRevokedDate;
	}

	/**
	 * Gets the revoked verification code value.
	 * 
	 * @return Revoked verification code value.
	 */
	public String getCode() {
		return this.code;
	}

	/**
	 * Sets the revoked verification code value.
	 * 
	 * @param aCode
	 *            Verification code value
	 */
	public void setCode(String aCode) {
		this.code = aCode;
	}

	/**
	 * Gets the revocation datetime of the signed mark.
	 * 
	 * @return Revocation datetime of the signed mark.
	 */
	public Date getRevokedDate() {
		return this.revokedDate;
	}

	/**
	 * Sets the revocation datetime of the signed mark.
	 * 
	 * @param aRevokedDate
	 *            Revocation datetime of the signed mark.
	 */
	public void setRevokedDate(Date aRevokedDate) {
		this.revokedDate = aRevokedDate;
	}

	/**
	 * Encodes the revoked verification code attributes into a revoked
	 * verification code line.
	 * 
	 * @return Encoded revoked verification code line
	 * 
	 * @throws EPPEncodeException
	 *             Error encoding the verification code line.
	 */
	public String encode() throws EPPEncodeException {
		cat.debug("encode(): enter");

		if (this.code == null) {
			throw new EPPEncodeException(
					"encode(): code required attribute is null.");
		}
		if (this.revokedDate == null) {
			throw new EPPEncodeException(
					"encode(): revokedDate required attribute is null.");
		}

		String ret = this.code + ","
				+ EPPUtil.encodeTimeInstant(this.revokedDate);

		cat.debug("encode(): Encoded value = \"" + ret + "\"");

		cat.debug("encode(): exit");
		return ret;
	}

	public void decode(String aLine) throws EPPDecodeException {
		cat.debug("decode(String): enter");

		int sepIndex = aLine.indexOf(',');

		if (sepIndex == -1) {
			throw new EPPDecodeException(
					"decode(): , seperator not found in revoked verification code line");
		}

		this.code = aLine.substring(0, sepIndex);

		String revokedDateStr = aLine.substring(sepIndex + 1);
		this.revokedDate = EPPUtil.decodeTimeInstant(revokedDateStr);

		if (this.revokedDate == null) {
			throw new EPPDecodeException(
					"decode(): Error decoding the revoked date");

		}

		cat.debug("decode(String): exit");
	}

	/**
	 * Clone <code>RevokedVerificationCode</code>.
	 * 
	 * @return clone of <code>RevokedVerificationCode</code>
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		RevokedVerificationCode clone = (RevokedVerificationCode) super.clone();

		return clone;
	}

	/**
	 * Implements a deep <code>RevokedVerificationCode</code> compare.
	 * 
	 * @param aObject
	 *            <code>RevokedVerificationCode</code> instance to compare with
	 * 
	 * @return <code>true</code> if equal <code>false</code> otherwise
	 */
	public boolean equals(Object aObject) {

		if (!(aObject instanceof RevokedVerificationCode)) {
			cat.error("RevokedVerificationCode.equals(): aObject is not an RevokedVerificationCode");
			return false;
		}

		RevokedVerificationCode other = (RevokedVerificationCode) aObject;

		// Code
		if (!EqualityUtil.equals(this.code, other.code)) {
			cat.error("RevokedVerificationCode.equals(): code not equal");
			return false;
		}

		// Revoked Date
		if (!EqualityUtil.equals(this.revokedDate, other.revokedDate)) {
			cat.error("RevokedVerificationCode.equals(): revokedDate not equal");
			return false;
		}

		return true;
	}

	/**
	 * Implementation of <code>Object.toString</code>, which will result in
	 * encoding the revoked verification code attributes into a revoked
	 * verification code line. If there is an error encoding the verification
	 * code line, a <code>RuntimeException</code> is thrown.
	 * 
	 * @return Encoded revoked verification code line
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
