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

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.verisign.epp.codec.gen.EPPCodecComponent;
import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EqualityUtil;

/**
 * Extension to the info command to retrieve the verification information 
 * for the verification profile assigned to the client or using the 
 * verification profile explicitly specified.  
 */
public class EPPVerificationCodeInfo implements EPPCodecComponent {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPVerificationCodeInfo.class.getName(),
			EPPCatFactory.getInstance().getFactory());

	/**
	 * Constant for the verification code info extension local name
	 */
	public static final String ELM_LOCALNAME = "info";

	/**
	 * Constant for the verification code info extension tag
	 */
	public static final String ELM_NAME = EPPVerificationCodeExtFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * XML Attribute name for the profile attribute.
	 */
	private static final String ATTR_PROFILE = "profile";


	/**
	 * Client specified profile to base the verification information on.
	 */
	String profile;

	/**
	 * Create an <code>EPPVerificationCodeInfo</code> instance.
	 */
	public EPPVerificationCodeInfo() {
	}

	/**
	 * Create a <code>EPPVerificationCodeInfo</code> instance with the optional 
	 * client specified profile.
	 * 
	 * @param aProfile
	 *            Profile to base the verification information on.
	 */
	public EPPVerificationCodeInfo(String aProfile) {
		this.profile = aProfile;
	}

	/**
	 * Is the verification profile defined?
	 * 
	 * @return <code>true</code> if the verification profile is defined; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasProfile() {
		return (this.profile != null ? true : false);
	}
	
	
	/**
	 * Gets the client specified profile to base the verification information on.
	 * 
	 * @return Verification profile if defined; <code>null</code> otherwise.
	 */
	public String getProfile() {
		return this.profile;
	}

	/**
	 * Sets the client specified profile to base the verification information on.
	 * 
	 * @param aProfile
	 *            Profile to base the verification information on.
	 */
	public void setProfile(String aProfile) {
		this.profile = aProfile;
	}


	/**
	 * Clone <code>EPPVerificationCodeInfo</code>.
	 * 
	 * @return clone of <code>EPPVerificationCodeInfo</code>
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {

		EPPVerificationCodeInfo clone = (EPPVerificationCodeInfo) super.clone();
		return clone;
	}

	/**
	 * Encode the <code>EPPVerificationCodeInfo</code> to a DOM Element
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
					+ " on in EPPVerificationCodeInfo.encode(Document)");
		}

		Element root = aDocument.createElementNS(EPPVerificationCodeExtFactory.NS,
				ELM_NAME);
		
		// Profile
		if (this.hasProfile()) {
			root.setAttribute(ATTR_PROFILE, this.profile);
		}

		return root;
	}

	/**
	 * Decode the DOM element to the <code>EPPVerificationCodeInfo</code>.
	 * 
	 * @param aElement
	 *            DOM Element to decode the attribute values
	 * @throws EPPDecodeException
	 *             Error decoding the DOM Element
	 */
	public void decode(Element aElement) throws EPPDecodeException {

		// Profile
		this.profile = aElement.getAttribute(ATTR_PROFILE);
		if (this.profile != null && this.profile.isEmpty()) {
			this.profile = null;
		}
	}

	/**
	 * implements a deep <code>EPPVerificationCodeInfo</code> compare.
	 * 
	 * @param aObject
	 *            <code>EPPVerificationCodeInfo</code> instance to compare with
	 * 
	 * @return true if equal false otherwise
	 */
	public boolean equals(Object aObject) {

		if (!(aObject instanceof EPPVerificationCodeInfo)) {
			cat.error("EPPVerificationCodeInfo.equals(): " + aObject.getClass().getName()
					+ " not EPPVerificationCodeInfo instance");

			return false;
		}

		EPPVerificationCodeInfo other = (EPPVerificationCodeInfo) aObject;
		
		// Profile
		if (!EqualityUtil.equals(this.profile, other.profile)) {
			cat.error("EPPVerificationCodeInfo.equals(): profile not equal");
			return false;
		}
		
		return true;
	}

}