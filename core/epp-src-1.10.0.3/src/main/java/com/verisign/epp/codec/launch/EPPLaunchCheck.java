/***********************************************************
Copyright (C) 2012 VeriSign, Inc.

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

package com.verisign.epp.codec.launch;

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
 * Extension to the domain check command to implement the check in three
 * different forms, which include Claims Check Form, Availability Check Form, or
 * Trademark Check Form.
 */
public class EPPLaunchCheck implements EPPCodecComponent {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(
			EPPLaunchCheck.class.getName(), EPPCatFactory.getInstance()
					.getFactory());

	/**
	 * Constant used to specify the claims check form type
	 */
	public static final String TYPE_CLAIMS = "claims";

	/**
	 * Constant used to specify the availability check form type
	 */
	public static final String TYPE_AVAILABILITY = "avail";

	/**
	 * Constant used to specify the trademark check form type
	 */
	public static final String TYPE_TRADEMARK = "trademark";

	/**
	 * Constant for the launch phase check extension local name
	 */
	public static final String ELM_LOCALNAME = "check";

	/**
	 * Constant for the launch phase info extension tag
	 */
	public static final String ELM_NAME = EPPLaunchExtFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * The "type" attribute name
	 */
	private static final String ATTR_TYPE = "type";

	/**
	 * The phase that the check will run against with the default being
	 * {@link EPPLaunchPhase#PHASE_CLAIMS}.
	 */
	private EPPLaunchPhase phase;

	/**
	 * Defines check form type with one of the <code>TYPE</code> constants.
	 */
	private String type;

	/**
	 * Create an EPPLaunchInf instance
	 */
	public EPPLaunchCheck() {
	}

	/**
	 * Create a <code>EPPLaunchCheck</code> instance with the required phase
	 * attribute value.
	 * 
	 * @param aPhase
	 *            The phase with the value of to execute the check against.
	 */
	public EPPLaunchCheck(EPPLaunchPhase aPhase) {
		this.phase = aPhase;
	}

	/**
	 * Create a <code>EPPLaunchCheck</code> instance with the phase and optional
	 * type attribute value.
	 * 
	 * @param aPhase
	 *            The phase with the value of to execute the check against.
	 * @param aType
	 *            Claims form type using either {@link #TYPE_CLAIMS} or
	 *            {@link #TYPE_AVAILABILITY} or {@link #TYPE_TRADEMARK}.
	 */
	public EPPLaunchCheck(EPPLaunchPhase aPhase, String aType) {
		this.phase = aPhase;
		this.type = aType;
	}

	/**
	 * Create a <code>EPPLaunchCheck</code> instance with the type attribute
	 * value.
	 * 
	 * @param aType
	 *            Claims form type using either {@link #TYPE_CLAIMS} or
	 *            {@link #TYPE_AVAILABILITY} or {@link #TYPE_TRADEMARK}.
	 */
	public EPPLaunchCheck(String aType) {
		this.type = aType;
	}

	/**
	 * Is the check form type defined?
	 * 
	 * @return <code>true</code> if the type is defined; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasType() {
		return (this.type != null ? true : false);
	}

	/**
	 * Gets the check form type, which should be either {@link #TYPE_CLAIMS},
	 * {@link #TYPE_AVAILABILITY}, or <code>null</code> for undefined.
	 * 
	 * @return {@link #TYPE_CLAIMS}, {@link #TYPE_AVAILABILITY}, or
	 *         <code>null</code> for undefined.
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Sets the check form type. The XML schema defines the default as
	 * {@link #TYPE_CLAIMS} if undefined.
	 * 
	 * @param aType
	 *            {@link #TYPE_CLAIMS} or {@link #TYPE_AVAILABILITY}
	 */
	public void setType(String aType) {
		this.type = aType;
	}

	/**
	 * Is the phase defined?
	 * 
	 * @return <code>true</code> if the phase is defined; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasPhase() {
		return (this.phase != null ? true : false);
	}

	/**
	 * Gets phase of the check command.
	 * 
	 * @return phase of the check command if set; <code>null</code> otherwise.
	 */
	public EPPLaunchPhase getPhase() {
		return this.phase;
	}

	/**
	 * Sets the phase of the check command.
	 * 
	 * @param aPhase
	 *            The phase with the value of to execute the check against.
	 */
	public void setPhase(EPPLaunchPhase aPhase) {
		this.phase = aPhase;
	}

	/**
	 * Sets the phase with one of the <code>EPPLaunchPhase</code>
	 * <code>PHASE</code> constants.
	 * 
	 * @param aPhaseString
	 *            One of the <code>EPPLaunchPhase</code> <code>PHASE</code>
	 *            constants.
	 */
	public void setPhase(String aPhaseString) {
		this.phase = new EPPLaunchPhase(aPhaseString);
	}

	/**
	 * Is the Claims Check Form being used?
	 * 
	 * @return <code>true</code> if Claims Check Form is defined;
	 *         <code>false</code> otherwise.
	 */
	public boolean isClaimsCheckForm() {
		return (this.type == null || this.type.equals(TYPE_CLAIMS) ? true
				: false);
	}

	/**
	 * Is the Availability Check Form being used?
	 * 
	 * @return <code>true</code> if Availability Check Form is defined;
	 *         <code>false</code> otherwise.
	 */
	public boolean isAvailabilityCheckForm() {
		return (this.type != null && this.type.equals(TYPE_AVAILABILITY) ? true
				: false);
	}

	/**
	 * Is the Trademark Check Form being used?
	 * 
	 * @return <code>true</code> if Trademark Check Form is defined;
	 *         <code>false</code> otherwise.
	 */
	public boolean isTrademarkCheckForm() {
		return (this.type != null && this.type.equals(TYPE_TRADEMARK) ? true
				: false);
	}

	/**
	 * Clone <code>EPPLaunchCheck</code>.
	 * 
	 * @return clone of <code>EPPLaunchCheck</code>
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {

		EPPLaunchCheck clone = (EPPLaunchCheck) super.clone();
		return clone;
	}

	/**
	 * Encode the <code>EPPLaunchCheck</code> to a DOM Element
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
					+ " on in EPPLaunchCheck.encode(Document)");
		}

		// Validate the setting of the phase
		if (this.isTrademarkCheckForm()) {
			if (this.phase != null) {
				throw new EPPEncodeException(
						"EPPLaunchCheck phase attribute must not be set using Trademark Check Form.");
			}
		}
		else if (this.phase == null) {
			throw new EPPEncodeException(
					"EPPLaunchCheck phase attribute must be set when not the Trademark Check Form.");
		}

		Element root = aDocument.createElementNS(EPPLaunchExtFactory.NS,
				ELM_NAME);

		// Type
		if (this.type != null) {
			if (!this.type.equals(TYPE_CLAIMS)
					&& !this.type.equals(TYPE_AVAILABILITY)
					&& !this.type.equals(TYPE_TRADEMARK)) {
				throw new EPPEncodeException(
						"EPPLaunchCheck type attribute with invalid value of: "
								+ this.type);
			}

			root.setAttribute(ATTR_TYPE, this.type);
		}

		// Phase
		EPPUtil.encodeComp(aDocument, root, this.phase);

		return root;
	}

	/**
	 * Decode the DOM element to the <code>EPPLaunchCheck</code>.
	 * 
	 * @param aElement
	 *            DOM Element to decode the attribute values
	 * @throws EPPDecodeException
	 *             Error decoding the DOM Element
	 */
	public void decode(Element aElement) throws EPPDecodeException {

		// Type
		this.type = aElement.getAttribute(ATTR_TYPE);
		if (this.type.isEmpty()) {
			this.type = null;
		}

		if (this.isClaimsCheckForm() || this.isAvailabilityCheckForm()) {
			// Phase
			this.phase = (EPPLaunchPhase) EPPUtil.decodeComp(aElement,
					EPPLaunchExtFactory.NS, EPPLaunchPhase.ELM_NAME,
					EPPLaunchPhase.class);

			if (this.phase == null) {
				throw new EPPDecodeException(
						"EPPLaunchCheck phase element not found.");
			}
		}
		else if (this.isTrademarkCheckForm()) {
			this.phase = null;
		}
		else {
			throw new EPPDecodeException(
					"EPPLaunchCheck invalid form on decode");
		}

	}

	/**
	 * implements a deep <code>EPPLaunchCheck</code> compare.
	 * 
	 * @param aObject
	 *            <code>EPPLaunchCheck</code> instance to compare with
	 * 
	 * @return true if equal false otherwise
	 */
	public boolean equals(Object aObject) {

		if (!(aObject instanceof EPPLaunchCheck)) {
			cat.error("EPPLaunchCheck.equals(): "
					+ aObject.getClass().getName()
					+ " not EPPLaunchCheck instance");

			return false;
		}

		EPPLaunchCheck other = (EPPLaunchCheck) aObject;

		// Type
		if (!EqualityUtil.equals(this.type, other.type)) {
			cat.error("EPPLaunchCheck.equals(): type not equal");
			return false;
		}

		// Phase
		if (!EqualityUtil.equals(this.phase, other.phase)) {
			cat.error("EPPLaunchCheck.equals(): phase not equal");
			return false;
		}

		return true;
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