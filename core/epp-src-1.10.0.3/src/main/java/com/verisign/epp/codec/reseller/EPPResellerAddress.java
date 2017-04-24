/***********************************************************
Copyright (C) 2004 VeriSign, Inc.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-0107  USA

http://www.verisign.com/nds/naming/namestore/techdocs.html
 ***********************************************************/
package com.verisign.epp.codec.reseller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.verisign.epp.codec.gen.EPPCodecComponent;
import com.verisign.epp.codec.gen.EPPCodecException;
import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EqualityUtil;

/**
 * Represents a reseller address. Every reseller has associated postal address
 * information. A postal address contains OPTIONAL street information, city
 * information, OPTIONAL state/province information, an OPTIONAL postal code,
 * and a country identifier as described in [ISO11180]. Address information MAY
 * be provided in both a subset of UTF-8 [RFC2279] that can be represented in
 * 7-bit ASCII [US-ASCII] and unrestricted UTF-8. A reseller address is defined
 * as the following:<br>
 * <br>
 * A &lt;reseller:addr&gt; element that contains address information associated
 * with the reseller. A &lt;reseller:addr&gt; element contains the following
 * child elements:<br>
 * <br>
 * 
 * <ul>
 * <li>
 * OPTIONAL &lt;reseller:street&gt; elements (up to a maximum of three) that
 * contain the reseller's street address. Use {@link #getStreets()} and
 * {@link #setStreets(List)} to get and set the elements.</li>
 * <li>
 * A &lt;reseller:city&gt; element that contains the reseller's city. Use
 * {@link #getCity()} and {@link #setCity(String)} to get and set the element.</li>
 * <li>
 * A &lt;reseller:sp&gt; element that contains the reseller's state or province.
 * This element is OPTIONAL for addressing schemes that do not require a state
 * or province name. Use {@link #getStateProvince()} and
 * {@link #setStateProvince(String)} to get and set the element.</li>
 * <li>
 * An OPTIONAL &lt;reseller:pc&gt; element that contains the reseller's postal
 * code. Use {@link #getPostalCode()} and {@link #setPostalCode(String)} to get
 * and set the element.</li>
 * <li>
 * A &lt;reseller:cc&gt; element that contains the two-character identifier
 * representing with the reseller's country. Use {@link #getCountry()} and
 * {@link #setCountry(String)} to get and set the element.</li>
 * </ul>
 */
public class EPPResellerAddress implements EPPCodecComponent {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPResellerAddress.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * XML local name for <code>EPPResellerDisclose</code>.
	 */
	public static final String ELM_LOCALNAME = "addr";

	/**
	 * XML root tag for <code>EPPResellerPostalDefinition</code>.
	 */
	public static final String ELM_NAME = EPPResellerMapFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * XML tag name for an streets attribute.
	 */
	private final static String ELM_STREET = "street";

	/**
	 * XML tag name for an city attribute.
	 */
	private final static String ELM_CITY = "city";

	/**
	 * XML tag name for an stateProvince attribute.
	 */
	private final static String ELM_STATE_PROVINCE = "sp";

	/**
	 * XML tag name for an postalCode attribute.
	 */
	private final static String ELM_POSTAL_CODE = "pc";

	/**
	 * XML tag name for an country attribute.
	 */
	private final static String ELM_COUNTRY = "cc";

	/**
	 * XML tag name for an streets attribute.
	 */
	private final static int MAX_STREET = 3;

	/**
	 * Reseller street, which is a <code>List</code> of up to 3
	 * <code>String</code>'s representing street line 1, line 2, and line 3.
	 */
	private List<String> streets = new ArrayList<String>();

	/**
	 * Reseller city.
	 */
	private String city;

	/**
	 * Reseller state/province.
	 */
	private String stateProvince;

	/**
	 * Reseller postal code
	 */
	private String postalCode;

	/**
	 * Contact country
	 */
	private String country;

	/**
	 * Default constructor for <code>EPPResellerAddress</code>. All the the
	 * attributes default to <code>null</code>. Must call required setter
	 * methods before invoking {@link #encode(Document)}, which include:<br>
	 * <br>
	 * 
	 * <ul>
	 * <li>
	 * City - {@link #setCity(String)}</li>
	 * <li>
	 * Country - {@link #setCountry(String)}</li>
	 * </ul>
	 */
	public EPPResellerAddress() {
	}

	/**
	 * Constructor for <code>EPPResellerAddress</code> all of the required
	 * attributes as parameters.
	 *
	 * @param aCity
	 *            Reseller street
	 * @param aCountry
	 *            Reseller country
	 */
	public EPPResellerAddress(String aCity, String aCountry) {
		this.city = aCity;
		this.country = aCountry;
	}

	/**
	 * Constructor for <code>EPPResellerAddress</code> all of the attributes as
	 * parameters.
	 *
	 * @param aStreets
	 *            <code>List&lt;String&gt;</code> collection of streets (up to
	 *            maximum three)
	 * @param aCity
	 *            Reseller street
	 * @param aStateProvince
	 *            Reseller state/province
	 * @param aPostalCode
	 *            Reseller postal code
	 * @param aCountry
	 *            Reseller country
	 */
	public EPPResellerAddress(List<String> aStreets, String aCity,
			String aStateProvince, String aPostalCode, String aCountry) {
		this.streets = aStreets;
		this.city = aCity;
		this.stateProvince = aStateProvince;
		this.postalCode = aPostalCode;
		this.country = aCountry;
	}

	/**
	 * Is there any street lines set?
	 * 
	 * @return <code>true</code> if there is at least one street line set.
	 */
	public boolean hasStreets() {
		if (this.streets != null && !this.streets.isEmpty()) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Add a street line to the street.  This will add <code>aStreetLine</code> 
	 * to the list of street lines.
	 * 
	 * @param aStreetLine Street line to add to the street
	 */
	public void addStreet(String aStreetLine) {
		this.streets.add(aStreetLine);
	}
	
	/**
	 * Gets the reseller street(s).
	 *
	 * @return street(s) as a <code>List&lt;String&gt;</code> of streets (up to
	 *         maximum three).
	 */
	public List<String> getStreets() {
		return streets;
	}

	/**
	 * Sets the reseller streets attribute with a
	 * <code>List&gt;String&gt;</code>, where each element represents a line of
	 * the street address.
	 *
	 * @param aStreets
	 *            Up to 3 street elements
	 */
	public void setStreets(List<String> aStreets) {
		this.streets = aStreets;
	}

	/**
	 * Sets the reseller streets with only one line. Only a one element
	 * <code>List&lt;String&gt;</code> will be returned on a call to
	 * {@link #getStreets()} after calling this method.
	 *
	 * @param aStreet
	 *            Reseller street.
	 */
	public void setStreet(String aStreet) {
		this.streets = new ArrayList<String>();
		this.streets.add(aStreet);
	}

	/**
	 * Sets the reseller street with two street lines.
	 *
	 * @param aStreet1
	 *            First street line
	 * @param aStreet2
	 *            Second street line
	 */
	public void setStreets(String aStreet1, String aStreet2) {
		this.streets = new ArrayList<String>();

		this.streets.add(aStreet1);
		this.streets.add(aStreet2);
	}

	/**
	 * Sets the reseller street with three street lines.
	 *
	 * @param aStreet1
	 *            First street line
	 * @param aStreet2
	 *            Second street line
	 * @param aStreet3
	 *            Third street line
	 */
	public void setStreets(String aStreet1, String aStreet2, String aStreet3) {
		this.streets = new ArrayList<String>();

		this.streets.add(aStreet1);
		this.streets.add(aStreet2);
		this.streets.add(aStreet3);
	}

	/**
	 * Gets the reseller city.
	 *
	 * @return city. <code>String</code> if defined; <code>null</code>
	 *         otherwise.
	 */
	public String getCity() {
		return this.city;
	}

	/**
	 * Sets the reseller city.
	 *
	 * @param aCity
	 *            reseller city
	 */
	public void setCity(String aCity) {
		this.city = aCity;
	}

	/**
	 * Gets the reseller state/province.
	 *
	 * @return state/province. <code>String</code> if defined; <code>null</code>
	 *         otherwise.
	 */
	public String getStateProvince() {
		return this.stateProvince;
	}

	/**
	 * Sets the reseller state/province.
	 *
	 * @param aStateProvince
	 *            reseller state/province
	 */
	public void setStateProvince(String aStateProvince) {
		this.stateProvince = aStateProvince;
	}

	/**
	 * Gets the reseller postal code
	 *
	 * @return postal code <code>String</code> if defined; <code>null</code>
	 *         otherwise.
	 */
	public String getPostalCode() {
		return this.postalCode;
	}

	/**
	 * Sets the reseller postal code
	 *
	 * @param aPostalCode
	 *            reseller postal code
	 */
	public void setPostalCode(String aPostalCode) {
		this.postalCode = aPostalCode;
	}

	/**
	 * Gets the reseller country.
	 *
	 * @return reseller country <code>String</code> if defined;
	 *         <code>null</code> otherwise.
	 */
	public String getCountry() {
		return this.country;
	}

	/**
	 * Sets the reseller country.
	 *
	 * @param aCountry
	 *            reseller country
	 */
	public void setCountry(String aCountry) {
		this.country = aCountry;
	}

	/**
	 * Validate the state of the <code>EPPResellerAddress</code> instance. A
	 * valid state means that all of the required attributes have been set. If
	 * validateState returns without an exception, the state is valid. If the
	 * state is not valid, the EPPCodecException will contain a description of
	 * the error. throws EPPCodecException State error. This will contain the
	 * name of the attribute that is not valid.
	 *
	 * @throws EPPCodecException
	 *             On validation error
	 */
	void validateState() throws EPPCodecException {

		if (this.streets != null) {
			if (this.streets.contains(null))
				throw new EPPCodecException(
						"street lines cannot be set to null");
		}

		if ((this.streets != null) && !this.streets.isEmpty()
				&& (this.streets.size() > MAX_STREET)) {
			throw new EPPCodecException("street lines exceed the maximum");
		}

		if (this.city == null) {
			throw new EPPCodecException("city required attribute is not set");
		}

		if (this.country == null) {
			throw new EPPCodecException("country required attribute is not set");
		}
	}

	/**
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPResellerAddress</code> instance.
	 *
	 * @param aDocument
	 *            DOM Document that is being built. Used as an Element factory.
	 *
	 * @return Root DOM Element representing the <code>EPPResellerAddress</code>
	 *         instance.
	 *
	 * @exception EPPEncodeException
	 *                Unable to encode <code>EPPResellerAddress</code> instance.
	 */
	public Element encode(Document aDocument) throws EPPEncodeException {
		try {
			validateState();
		}
		catch (EPPCodecException e) {
			throw new EPPEncodeException(
					"Invalid state on EPPResellerAddress.encode: " + e);
		}

		// Create root element
		Element root = aDocument.createElementNS(EPPResellerMapFactory.NS,
				ELM_NAME);

		// Street
		EPPUtil.encodeStringList(aDocument, root, this.streets,
				EPPResellerMapFactory.NS, EPPResellerMapFactory.NS_PREFIX + ":"
						+ ELM_STREET);

		// City
		EPPUtil.encodeString(aDocument, root, this.city,
				EPPResellerMapFactory.NS, EPPResellerMapFactory.NS_PREFIX + ":"
						+ ELM_CITY);

		// State/Province
		EPPUtil.encodeString(aDocument, root, this.stateProvince,
				EPPResellerMapFactory.NS, EPPResellerMapFactory.NS_PREFIX + ":"
						+ ELM_STATE_PROVINCE);

		// Postal Code
		EPPUtil.encodeString(aDocument, root, this.postalCode,
				EPPResellerMapFactory.NS, EPPResellerMapFactory.NS_PREFIX + ":"
						+ ELM_POSTAL_CODE);

		// Country
		EPPUtil.encodeString(aDocument, root, this.country,
				EPPResellerMapFactory.NS, EPPResellerMapFactory.NS_PREFIX + ":"
						+ ELM_COUNTRY);

		return root;
	}

	/**
	 * Decode the <code>EPPResellerAddress</code> attributes from the aElement
	 * DOM Element tree.
	 *
	 * @param aElement
	 *            Root DOM Element to decode <code>EPPResellerAddress</code>
	 *            from.
	 *
	 * @exception EPPDecodeException
	 *                Unable to decode aElement.
	 */
	public void decode(Element aElement) throws EPPDecodeException {

		// Street
		this.streets = (List<String>) EPPUtil.decodeList(aElement,
				EPPResellerMapFactory.NS, ELM_STREET);

		// City
		this.city = EPPUtil.decodeString(aElement, EPPResellerMapFactory.NS,
				ELM_CITY);

		// State/Province
		this.stateProvince = EPPUtil.decodeString(aElement,
				EPPResellerMapFactory.NS, ELM_STATE_PROVINCE);

		// Postal Code
		this.postalCode = EPPUtil.decodeString(aElement,
				EPPResellerMapFactory.NS, ELM_POSTAL_CODE);

		// Country
		this.country = EPPUtil.decodeString(aElement, EPPResellerMapFactory.NS,
				ELM_COUNTRY);
	}

	/**
	 * implements a deep <code>EPPResellerAddress</code> compare.
	 *
	 * @param aObject
	 *            <code>EPPResellerAddress</code> instance to compare with
	 *
	 * @return <code>true</code> of <code>aObject</code> is equal to instance;
	 *         <code>false</code> otherwise.
	 */
	public boolean equals(Object aObject) {

		if (!(aObject instanceof EPPResellerAddress)) {
			return false;
		}

		EPPResellerAddress other = (EPPResellerAddress) aObject;

		// Street
		if (!EPPUtil.equalLists(this.streets, other.streets)) {
			cat.error("EPPResellerAddress.equals(): streets not equal");
			return false;
		}

		// City
		if (!EqualityUtil.equals(this.city, other.city)) {
			cat.error("EPPResellerAddress.equals(): city not equal");
			return false;
		}

		// State/Province
		if (!EqualityUtil.equals(this.stateProvince, other.stateProvince)) {
			cat.error("EPPResellerAddress.equals(): stateProvince not equal");
			return false;
		}

		// Postal Code
		if (!EqualityUtil.equals(this.postalCode, other.postalCode)) {
			cat.error("EPPResellerAddress.equals(): postalCode not equal");
			return false;
		}

		// Country
		if (!EqualityUtil.equals(this.country, other.country)) {
			cat.error("EPPResellerAddress.equals(): country not equal");
			return false;
		}

		return true;
	}

	/**
	 * Clone <code>EPPResellerAddress</code>.
	 *
	 * @return clone of <code>EPPResellerAddress</code>
	 *
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPResellerAddress clone = null;

		clone = (EPPResellerAddress) super.clone();

		if (this.hasStreets()) {
			clone.streets = (List) ((ArrayList) this.streets).clone();
		}

		return clone;
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
