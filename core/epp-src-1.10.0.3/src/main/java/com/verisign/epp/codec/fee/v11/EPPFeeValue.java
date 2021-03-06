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
package com.verisign.epp.codec.fee.v11;

import java.math.BigDecimal;

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
 * <code>EPPFeeValue</code> represents the fee value information returned by the
 * server.
 */
public class EPPFeeValue implements EPPCodecComponent {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPFeeValue.class.getName(),
			EPPCatFactory.getInstance().getFactory());

	/**
	 * Constant for the phase local name
	 */
	public static final String ELM_LOCALNAME = "fee";

	/**
	 * Constant for the phase qualified name (prefix and local name)
	 */
	public static final String ELM_NAME = EPPFeeExtFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * Fee is applied immediately at the time of the operation.
	 */
	public static String APPLIED_IMMEDIATE = "immediate";

	/**
	 * Fee is applied later after allocation.
	 */
	public static String APPLIED_DELAYED = "delayed";

	/**
	 * Constant to reflect that there is no grace period.
	 */
	public static String GRACE_PERIOD_NONE = "P0D";

	/**
	 * Default applied value, which is to apply the fee immediately.
	 */
	public static String DEFAULT_APPLIED = APPLIED_IMMEDIATE;

	/**
	 * OPTIONAL human-readable description of the fee.
	 */
	private static final String ATTR_DESCRIPTION = "description";

	/**
	 * OPTIONAL refundable attribute with a default of true (1)
	 */
	private static final String ATTR_REFUNDABLE = "refundable";

	/**
	 * OPTIONAL grace period attribute.
	 */
	private static final String ATTR_GRACE_PERIOD = "grace-period";

	/**
	 * Attribute to indicate when fees are applied using one of the
	 * <code>APPLIED</code> constant values with a default of
	 * <code>DEFAULT_APPLIED</code>.
	 */
	private static final String ATTR_APPLIED = "applied";

	/**
	 * OPTIONAL Human-readable description
	 */
	private String description;

	/**
	 * OPTIONAL refundable attribute
	 */
	private Boolean refundable;

	/**
	 * OPTIONAL grace-period attribute
	 */
	private String gracePeriod;

	/**
	 * OPTIONAL applied attribute the defines when fees are applied.
	 */
	private String applied = DEFAULT_APPLIED;

	/**
	 * Value of the fee
	 */
	private BigDecimal fee;

	/**
	 * Default constructor. The command value MUST be set using the
	 * {@link #setFee(BigDecimal)} method.
	 */
	public EPPFeeValue() {
	}

	/**
	 * Create <code>EPPFeeValue</code> instance with the required fee value.
	 * 
	 * @param aFee
	 *            The fee value
	 */
	public EPPFeeValue(BigDecimal aFee) {
		this.setFee(aFee);
	}

	/**
	 * Create <code>EPPFeeValue</code> instance with the required fee value and
	 * the optional description.
	 * 
	 * @param aFee
	 *            The fee value
	 * @param aDescription
	 *            Human-readable description
	 */
	public EPPFeeValue(BigDecimal aFee, String aDescription) {
		this.setFee(aFee);
		this.setDescription(aDescription);
	}

	/**
	 * Create <code>EPPFeeValue</code> instance with all attributes.
	 * 
	 * @param aFee
	 *            The required fee value.
	 * @param aDescription
	 *            Optional Human-readable description. Pass as <code>null</code>
	 *            if there is no description.
	 * @param aRefundable
	 *            Is the fee refundable? Set to <code>null</code> to not set the
	 *            optional refundable attribute.
	 * @param aGracePeriod
	 *            Optional grace period when <code>aRefundable</code> is
	 *            <code>true</code>. Set to <code>GRACE_PERIOD_NONE</code> if
	 *            <code>aRefundable</code> is <code>false</code>. Set to
	 *            <code>null</code> to not set the optional grace period
	 *            attribute.
	 * @param aApplied
	 *            Optional applied attribute that defines when fees are applied.
	 *            Use either the <code>APPLIED_IMMEDIATE</code> constant or the
	 *            <code>APPLIED_DELAYED</code> constant. Set to
	 *            <code>null</code> to not set the applied attribute.
	 */
	public EPPFeeValue(BigDecimal aFee, String aDescription,
			Boolean aRefundable, String aGracePeriod, String aApplied) {
		this.setFee(aFee);
		this.setDescription(aDescription);
		this.setRefundable(aRefundable);
		this.setGracePeriod(aGracePeriod);
		this.setApplied(aApplied);
	}

	/**
	 * Is the description defined?
	 * 
	 * @return <code>true</code> if the description is defined;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasDescription() {
		return (this.description != null ? true : false);
	}

	/**
	 * Gets the description value.
	 * 
	 * @return Description if defined; <code>null</code> otherwise.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Sets the description value.
	 * 
	 * @param aDescription
	 *            Description value.
	 */
	public void setDescription(String aDescription) {
		this.description = aDescription;
		cat.debug("setDescription: description = " + this.description);
	}

	/**
	 * Is the refundable attribute defined?
	 * 
	 * @return <code>true</code> if the refundable attribute is defined;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasRefundable() {
		return (this.refundable != null ? true : false);
	}

	/**
	 * Is the fee refundable?
	 * 
	 * @return <code>null</code> if undefined, <code>true</code> if the fee is
	 *         refundable, or <code>false</code> if not refundable.
	 */
	public Boolean isRefundable() {
		return this.refundable;
	}

	/**
	 * Sets whether the fee is refundable.
	 * 
	 * @param aRefundable
	 *            Set to <code>true</code> if the fee is refundable,
	 *            <code>false</code>, or <code>null</code> if undefined.
	 */
	public void setRefundable(Boolean aRefundable) {
		this.refundable = aRefundable;
		cat.debug("setRefundable: refundable = " + this.refundable);
	}

	/**
	 * Is the grace-period defined?
	 * 
	 * @return <code>true</code> if the grace-period is defined;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasGracePeriod() {
		return (this.gracePeriod != null ? true : false);
	}

	/**
	 * Gets the OPTIONAL grace-period value.
	 * 
	 * @return grace-period value if defined; <code>null</code> otherwise.
	 */
	public String getGracePeriod() {
		return this.gracePeriod;
	}

	/**
	 * Sets the OPTIONAL grace-period value.
	 * 
	 * @param aGracePeriod
	 *            Grace-period value if defined; <code>null</code> otherwise.
	 */
	public void setGracePeriod(String aGracePeriod) {
		this.gracePeriod = aGracePeriod;
		cat.debug("setGracePeriod: gracePeriod = " + this.gracePeriod);
	}

	/**
	 * Is the applied attribute defined?  Applied 
	 * 
	 * @return <code>true</code> if the applied attribute is defined;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasApplied() {
		return (this.applied != null ? true : false);
	}

	/**
	 * Gets the OPTIONAL applied value.
	 * 
	 * @return The applied that should be one of the <code>APPLIED</code>
	 *         constant values if set; <code>DEFAULT_APPLIED</code> otherwise.
	 */
	public String getApplied() {
		return this.applied;
	}

	/**
	 * Sets the applied value.
	 * 
	 * @param aApplied
	 *            Applied value that should be one of the <code>APPLIED</code>
	 *            constant values or <code>null</code> to set to the default
	 *            value of <code>DEFAULT_APPIED</code>.
	 */
	public void setApplied(String aApplied) {
		if (aApplied != null) {
			this.applied = aApplied;
		}
		else {
			this.applied = DEFAULT_APPLIED;
		}
		cat.debug("setApplied: applied = " + this.applied);
	}

	/**
	 * Gets the fee value.
	 * 
	 * @return Fee value
	 */
	public BigDecimal getFee() {
		return this.fee;
	}

	/**
	 * Sets the fee value.
	 * 
	 * @param aFee
	 *            Fee value
	 */
	public void setFee(BigDecimal aFee) {
		if (aFee != null) {
			this.fee = aFee;
			this.fee.setScale(2, BigDecimal.ROUND_HALF_UP);
		}
		cat.debug("setFee: fee = " + this.fee);
	}

	/**
	 * Clone <code>EPPFeeValue</code> instance.
	 *
	 * @return clone of <code>EPPFeeValue</code>
	 *
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPFeeValue clone = null;

		clone = (EPPFeeValue) super.clone();

		return clone;
	}

	/**
	 * Decode the <code>EPPFeeValue</code> element aElement DOM Element tree.
	 *
	 * @param aElement
	 *            - Root DOM Element to decode <code>EPPFeeValue</code> from.
	 *
	 * @exception EPPDecodeException
	 *                Unable to decode aElement
	 */
	public void decode(Element aElement) throws EPPDecodeException {
		// Description
		String theDescription = aElement.getAttribute(ATTR_DESCRIPTION);
		if (theDescription != null && !theDescription.isEmpty()) {
			this.description = theDescription;
		}
		else {
			this.description = null;
		}

		// Refundable
		String theRefundable = aElement.getAttribute(ATTR_REFUNDABLE);
		if (theRefundable != null && !theRefundable.isEmpty()) {
			this.refundable = EPPUtil.decodeBooleanAttr(aElement,
					ATTR_REFUNDABLE);
		}
		else {
			this.refundable = null;
		}

		// Grace Period
		String theGracePeriod = aElement.getAttribute(ATTR_GRACE_PERIOD);
		if (theGracePeriod != null && !theGracePeriod.isEmpty()) {
			this.gracePeriod = theGracePeriod;
		}
		else {
			this.gracePeriod = null;
		}

		// Applied
		String theApplied = aElement.getAttribute(ATTR_APPLIED);
		if (theApplied != null && !theApplied.isEmpty()) {
			this.applied = theApplied;
		}
		else {
			this.applied = DEFAULT_APPLIED;
		}

		// Fee
		this.fee = EPPUtil.decodeBigDecimal((Element) aElement);
	}

	/**
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPFeeValue</code> instance.
	 *
	 * @param aDocument
	 *            - DOM Document that is being built. Used as an Element
	 *            factory.
	 *
	 * @return Element - Root DOM Element representing the
	 *         <code>EPPFeeValue</code> instance.
	 *
	 * @exception EPPEncodeException
	 *                - Unable to encode <code>EPPFeeValue</code> instance.
	 */
	public Element encode(Document aDocument) throws EPPEncodeException {

		if (aDocument == null) {
			throw new EPPEncodeException("aDocument is null"
					+ " on in EPPFeeValue.encode(Document)");
		}
		if (this.fee == null) {
			throw new EPPEncodeException(
					"fee is null" + " on in EPPFeeValue.encode(Document)");
		}

		Element root = aDocument.createElementNS(EPPFeeExtFactory.NS, ELM_NAME);

		// Description
		if (this.hasDescription()) {
			root.setAttribute(ATTR_DESCRIPTION, this.description);
		}

		// Refundable
		if (this.hasRefundable()) {
			EPPUtil.encodeBooleanAttr(root, ATTR_REFUNDABLE, this.refundable);
		}

		// Grace Period
		if (this.hasGracePeriod()) {
			root.setAttribute(ATTR_GRACE_PERIOD, this.gracePeriod);
		}

		// Applied
		if (this.hasApplied()) {
			root.setAttribute(ATTR_APPLIED, this.applied);
		}

		// Fee
		EPPUtil.encodeBigDecimal(aDocument, root, this.fee, null);

		return root;
	}

	/**
	 * implements a deep <code>EPPFeeValue</code> compare.
	 *
	 * @param aObject
	 *            <code>EPPFeeValue</code> instance to compare with
	 *
	 * @return <code>true</code> if equal; <code>false</code> otherwise
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPFeeValue)) {
			return false;
		}

		EPPFeeValue other = (EPPFeeValue) aObject;

		// Description
		if (!EqualityUtil.equals(this.description, other.description)) {
			cat.error("EPPFeeValue.equals(): description not equal");
			return false;
		}

		// Refundable
		if (!EqualityUtil.equals(this.refundable, other.refundable)) {
			cat.error("EPPFeeValue.equals(): refundable not equal");
			return false;
		}

		// Grace Period
		if (!EqualityUtil.equals(this.gracePeriod, other.gracePeriod)) {
			cat.error("EPPFeeValue.equals(): gracePeriod not equal");
			return false;
		}

		// Applied
		if (!EqualityUtil.equals(this.applied, other.applied)) {
			cat.error("EPPFeeValue.equals(): applied not equal");
			return false;
		}

		// Fee
		if (!EqualityUtil.equals(this.fee, other.fee)) {
			cat.error("EPPFeeValue.equals(): fee not equal");
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
