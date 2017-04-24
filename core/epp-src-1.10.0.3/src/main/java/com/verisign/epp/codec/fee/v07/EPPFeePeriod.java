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
package com.verisign.epp.codec.fee.v07;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.verisign.epp.codec.gen.EPPCodecException;
import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.util.EPPCatFactory;

/**
 * Represents a fee period. Validity periods are measured in years or months
 * with the appropriate units specified using the <code>unit</code> attribute.
 * Valid values for the <code>unit</code> attribute are <code>y</code> for years
 * and <code>m</code> for months.
 */
public class EPPFeePeriod implements
		com.verisign.epp.codec.gen.EPPCodecComponent {

	/**
	 * Period in Unit Month
	 */
	public final static java.lang.String PERIOD_UNIT_MONTH = "m";

	/**
	 * Period in Unit Year
	 */
	public final static java.lang.String PERIOD_UNIT_YEAR = "y";

	/**
	 * Unspecified Period
	 */
	public final static int UNSPEC_PERIOD = -1;

	/**
	 * Maximum number of periods.
	 */
	public final static int MAX_PERIOD = 99;

	/**
	 * Minimum number of periods.
	 */
	public final static int MIN_PERIOD = 1;

	/**
	 * XML local name for <code>EPPFeePeriod</code>.
	 */
	public static final String ELM_LOCALNAME = "period";

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPFeePeriod.class.getName(),
			EPPCatFactory.getInstance().getFactory());

	/**
	 * XML Element Name of <code>EPPFeePeriod</code> root element.
	 */
	final static java.lang.String ELM_NAME = EPPFeeExtFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * XML attribute name for the <code>unit</code> attribute.
	 */
	private final static java.lang.String ATTR_PERIOD_UNIT = "unit";

	/**
	 * Domain period.
	 */
	private int period = 0;

	/**
	 * Domain period unit.
	 */
	private java.lang.String pUnit = PERIOD_UNIT_YEAR;

	/**
	 * <code>EPPFeePeriod</code> default constructor. The period is initialized
	 * to <code>UNSPEC_PERIOD</code>. The period must be set before invoking
	 * <code>encode</code>.
	 */
	public EPPFeePeriod() {
		period = UNSPEC_PERIOD;
	}

	/**
	 * <code>EPPFeePeriod</code> constructor that takes the period value with a
	 * default unit of <code>PERIOD_UNIT_YEAR</code>.
	 *
	 * @param aPeriod
	 *            Period value
	 */
	public EPPFeePeriod(int aPeriod) {
		this.period = aPeriod;
		this.pUnit = PERIOD_UNIT_YEAR;
	}

	/**
	 * <code>EPPFeePeriod</code> constructor that takes the period and period
	 * unit as an arguments.
	 *
	 * @param aPUnit
	 *            Period value
	 * @param aPeriod
	 *            The period unit that must be either
	 *            <code>PERIOD_UNIT_YEAR</code> or
	 *            <code>PERIOD_UNIT_MONTH</code>.
	 */
	public EPPFeePeriod(String aPUnit, int aPeriod) {
		this.setPUnit(aPUnit);
		this.period = aPeriod;
	}

	/**
	 * Clone <code>EPPFeePeriod</code> instance.
	 *
	 * @return clone of <code>EPPFeePeriod</code>
	 *
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPFeePeriod clone = null;

		clone = (EPPFeePeriod) super.clone();

		return clone;
	}

	/**
	 * Decode the EPPFeePeriod attributes from the aElement DOM Element tree.
	 *
	 * @param aElement
	 *            - Root DOM Element to decode EPPFeePeriod from.
	 *
	 * @exception EPPDecodeException
	 *                Unable to decode aElement
	 */
	public void decode(Element aElement) throws EPPDecodeException {
		String tempVal = null;
		tempVal = aElement.getFirstChild().getNodeValue();
		pUnit = aElement.getAttribute(ATTR_PERIOD_UNIT);

		if (tempVal == null) {
			period = UNSPEC_PERIOD;
		}
		else {
			period = Integer.parseInt(tempVal);
		}
	}

	/**
	 * Encode a DOM Element tree from the attributes of the EPPFeePeriod
	 * instance.
	 *
	 * @param aDocument
	 *            - DOM Document that is being built. Used as an Element
	 *            factory.
	 *
	 * @return Element - Root DOM Element representing the EPPFeePeriod
	 *         instance.
	 *
	 * @exception EPPEncodeException
	 *                - Unable to encode EPPFeePeriod instance.
	 */
	public Element encode(Document aDocument) throws EPPEncodeException {
		// Period with Attribute of Unit
		Element root = aDocument.createElementNS(EPPFeeExtFactory.NS, ELM_NAME);

		// Check the Period unit
		if (pUnit == null) {
			throw new EPPEncodeException(
					"EPPFeePeriod: Period Unit should not be null");
		}
		else {
			root.setAttribute(ATTR_PERIOD_UNIT, pUnit);

			// add value
			Text currVal = aDocument.createTextNode(this.period + "");

			// append child
			root.appendChild(currVal);
		}

		return root;
	}

	/**
	 * Implements a deep <code>EPPFeePeriod</code> compare.
	 *
	 * @param aObject
	 *            <code>EPPFeePeriod</code> instance to compare with
	 *
	 * @return <code>true</code> if equals; <code>false</code> otherwise.
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPFeePeriod)) {
			return false;
		}

		EPPFeePeriod theComp = (EPPFeePeriod) aObject;

		// period
		if (period != theComp.period) {
			return false;
		}

		// pUnit
		if (!((pUnit == null) ? (theComp.pUnit == null) : pUnit
				.equals(theComp.pUnit))) {
			return false;
		}

		return true;
	}

	/**
	 * Get the period.
	 *
	 * @return The period if defined; <code>UNSPEC_PERIOD</code> otherwise.
	 */
	public int getPeriod() {
		return period;
	}

	/**
	 * Get the period unit.
	 *
	 * @return Either <code>PERIOD_UNIT_YEAR</code> for year or
	 *         <code>PERIOD_UNIT_MONTH</code> for month.
	 */
	public String getPUnit() {
		return pUnit;
	}

	/**
	 * Test whether the period has been specified.
	 *
	 * @return <code>true</code> if unspecified; <code>false</code> otherwise.
	 */
	public boolean isPeriodUnspec() {
		if (period == UNSPEC_PERIOD) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Set the period.
	 *
	 * @param aPeriod
	 *            Period value. Can be <code>UNSPEC_PERIOD</code> to clear the
	 *            period; otherwise the period must be between
	 *            <code>MIN_PERIOD</code> and <code>MAX_PERIOD</code>.
	 *
	 * @throws EPPCodecException
	 *             Error with period value.
	 */
	public void setPeriod(int aPeriod) throws EPPCodecException {

		if (aPeriod != UNSPEC_PERIOD) {
			if ((aPeriod < MIN_PERIOD) && (aPeriod > MAX_PERIOD)) {

				throw new EPPCodecException("period of " + aPeriod
						+ " is out of range, must be between " + MIN_PERIOD
						+ " and " + MAX_PERIOD);
			}
		}

		period = aPeriod;

	}

	/**
	 * Sets the period unit.
	 *
	 * @param aPUnit
	 *            Must be either <code>PERIOD_UNIT_YEAR</code> or
	 *            <code>PERIOD_UNIT_MONTH</code>. If not, no change will be
	 *            made.
	 */
	public void setPUnit(String aPUnit) {

		if (aPUnit.equalsIgnoreCase(PERIOD_UNIT_MONTH)) {
			this.pUnit = PERIOD_UNIT_MONTH;
		}
		else if (pUnit.equalsIgnoreCase(PERIOD_UNIT_YEAR)) {
			this.pUnit = PERIOD_UNIT_YEAR;
		}
		else {
			cat.error("setPUnit: Invalid period unit " + aPUnit
					+ ", setting unchanged");
		}
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
