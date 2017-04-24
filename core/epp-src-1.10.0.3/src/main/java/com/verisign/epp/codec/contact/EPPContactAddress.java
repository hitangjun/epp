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

package com.verisign.epp.codec.contact;

//----------------------------------------------
//
// imports...
//
//----------------------------------------------
// Java Core Imports
import java.util.Vector;

import org.w3c.dom.Document;
// W3C Imports
import org.w3c.dom.Element;

// EPP Imports
import com.verisign.epp.codec.gen.EPPCodecComponent;
import com.verisign.epp.codec.gen.EPPCodecException;
import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.util.EPPEnv;
import com.verisign.epp.util.EqualityUtil;

/**
 * Represents a contact address specified in <code>EPPContactCreateCmd</code>, <code>EPPContactUpdateCmd</code>, or
 * <code>EPPContactInfoResp</code>. Every contact has associated postal address information. A postal address contains
 * OPTIONAL street information, city information, OPTIONAL state/province information, an OPTIONAL postal code, and a
 * country identifier as described in [ISO11180]. Address information MAY be provided in both a subset of UTF-8
 * [RFC2279] that can be represented in 7-bit ASCII [US-ASCII] and unrestricted UTF-8. A contact address is defined as
 * the following in the EPP Contact Mapping Specification:<br>
 * <br>
 * A &lt;contact:addr&gt; element that contains address information associated with the contact. A &lt;contact:addr&gt;
 * element contains the following child elements:<br>
 * <br>
 * <ul>
 * <li>OPTIONAL &lt;contact:street&gt; elements (up to a maximum of three) that contain the contact's street address.
 * Use <code>getStreets</code> and <code>setStreets</code> to get and set the elements.</li>
 * <li>A &lt;contact:city&gt; element that contains the contact's city. Use <code>getCity</code> and
 * <code>setCity</code> to get and set the element.</li>
 * <li>A &lt;contact:sp&gt; element that contains the contact's state or province. This element is OPTIONAL for
 * addressing schemes that do not require a state or province name. Use <code>getStateProvince</code> and
 * <code>setStateProvince</code> to get and set the element.</li>
 * <li>An OPTIONAL &lt;contact:pc&gt; element that contains the contact's postal code. Use <code>getPostalCode</code>
 * and <code>setPostalCode</code> to get and set the element.</li>
 * <li>A &lt;contact:cc&gt; element that contains the two-character identifier representing with the contact's country.
 * Use <code>getCountry</code> and <code>setCountry</code> to get and set the element.</li>
 * </ul>
 * <br>
 * <br>
 *
 * @author $Author: jim $
 * @version $Revision: 1.2 $
 * @see com.verisign.epp.codec.contact.EPPContactCreateCmd
 * @see com.verisign.epp.codec.contact.EPPContactUpdateCmd
 * @see com.verisign.epp.codec.contact.EPPContactInfoResp
 */
public class EPPContactAddress implements EPPCodecComponent {
	/** XML Element Name of <code>EPPContactAddress</code> root element. */
	final static String ELM_NAME = "contact:addr";

	/** XML tag name for an streets attribute. */
	private final static String ELM_STREET = "contact:street";

	/** XML tag name for an city attribute. */
	private final static String ELM_CITY = "contact:city";

	/** XML tag name for an stateProvince attribute. */
	private final static String ELM_STATE_PROVINCE = "contact:sp";

	/** XML tag name for an postalCode attribute. */
	private final static String ELM_POSTAL_CODE = "contact:pc";

	/** XML tag name for an country attribute. */
	private final static String ELM_COUNTRY = "contact:cc";

	/** XML tag name for an streets attribute. */
	private final static int MAX_STREET = 3;

	/**
	 * Contact street, which is a <code>Vector</code> of 1 or 2 <code>String</code>'s representing street line 1 and
	 * street line 2.
	 */
	private Vector streets = null;

	/** Contact city. */
	private String city = null;

	/** Contact state/province. */
	private String stateProvince = null;

	/** Contact postal code */
	private String postalCode = null;

	/** Contact country */
	private String country = null;


	/**
	 * Default constructor for <code>EPPContactAddress</code>. All the the attributes default to <code>null</code>. Must
	 * call required setter methods before invoking <code>encode</code>, which include:<br>
	 * <br>
	 * <ul>
	 * <li>city - <code>setCity</code></li>
	 * <li>country - <code>setCountry</code></li>
	 * </ul>
	 */
	public EPPContactAddress () {
		// Default set in attribute definition.
	}


	/**
	 * Constructor for <code>EPPContactAddress</code> all of the required attributes as parameters.
	 *
	 * @param aCity
	 *        contact street
	 * @param aCountry
	 *        contract country
	 */
	public EPPContactAddress ( String aCity, String aCountry ) {
		streets = null;
		city = aCity;
		stateProvince = null;
		postalCode = null;
		country = aCountry;
	}


	/**
	 * Constructor for <code>EPPContactAddress</code> all of the required attributes as parameters.
	 *
	 * @param someStreets
	 *        <code>Vector</code> of street (up to maximum three) <code>String</code>'s
	 * @param aCity
	 *        contact street
	 * @param aStateProvince
	 *        contact state/province
	 * @param aPostalCode
	 *        contact postal code
	 * @param aCountry
	 *        contract country
	 */
	public EPPContactAddress ( Vector someStreets, String aCity, String aStateProvince, String aPostalCode,
			String aCountry ) {
		streets = someStreets;
		city = aCity;
		stateProvince = aStateProvince;
		postalCode = aPostalCode;
		country = aCountry;
	}


	/**
	 * Gets the contact street(s).
	 *
	 * @return street(s) as a <code>Vector</code> of street (up to maximum three) <code>String</code> if defined;
	 *         <code>null</code> otherwise.
	 */
	public Vector getStreets () {
		return streets;
	}


	/**
	 * Returns <code>true</code> if the address has at least one street.
	 * 
	 * @return <code>true</code> if the address at least one street <code>false</code> otherwise
	 */
	public boolean hasStreets () {
		return (this.streets != null && this.streets.elements().hasMoreElements());
	}


	/**
	 * Sets the contact street with only one <code>String</code> parameter. Only a one element <code>Vector</code> will be
	 * returned on a call to <code>getStreets</code> when originally set with this method.
	 *
	 * @param aStreet
	 *        contact street.
	 */
	public void setStreet ( String aStreet ) {
		streets = new Vector();

		streets.addElement( aStreet );
	}


	/**
	 * Sets the contact street attribute with a <code>Vector</code> of <code>String</code>'s.
	 *
	 * @param someStreets
	 *        <code>Vector</code> of one or two street <code>String</code>'s.
	 */
	public void setStreets ( Vector someStreets ) {
		streets = someStreets;
	}


	/**
	 * Sets the contact street with two sub-street <code>String</code>'s.
	 *
	 * @param aStreet1
	 *        First part/line of contact street
	 * @param aStreet2
	 *        Second part/line of contact street
	 */
	public void setStreets ( String aStreet1, String aStreet2 ) {
		streets = new Vector();

		streets.addElement( aStreet1 );
		streets.addElement( aStreet2 );
	}


	/**
	 * Sets the contact street with three sub-street <code>String</code>'s.
	 *
	 * @param aStreet1
	 *        First part/line of contact street
	 * @param aStreet2
	 *        Second part/line of contact street
	 * @param aStreet3
	 *        Third part/line of contact street
	 */
	public void setStreets ( String aStreet1, String aStreet2, String aStreet3 ) {
		streets = new Vector();

		streets.addElement( aStreet1 );
		streets.addElement( aStreet2 );
		streets.addElement( aStreet3 );
	}


	/**
	 * Gets the contact city.
	 *
	 * @return city. <code>String</code> if defined; <code>null</code> otherwise.
	 */
	public String getCity () {
		return city;
	}


	/**
	 * Returns <code>true</code> if the address has city.
	 * 
	 * @return <code>true</code> if the address has city <code>false</code> otherwise
	 */
	public boolean hasCity () {
		return (this.city != null);
	}


	/**
	 * Sets the contact city.
	 *
	 * @param aCity
	 *        contact city
	 */
	public void setCity ( String aCity ) {
		city = aCity;
	}


	/**
	 * Gets the contact state/province.
	 *
	 * @return state/province. <code>String</code> if defined; <code>null</code> otherwise.
	 */
	public String getStateProvince () {
		return stateProvince;
	}


	/**
	 * Returns <code>true</code> if the address has state.
	 * 
	 * @return <code>true</code> if the address has state <code>false</code> otherwise
	 */
	public boolean hasStateProvince () {
		return (this.stateProvince != null);
	}


	/**
	 * Sets the contact state/province.
	 *
	 * @param aStateProvince
	 *        contact state/province
	 */
	public void setStateProvince ( String aStateProvince ) {
		stateProvince = aStateProvince;
	}


	/**
	 * Gets the contact postal code
	 *
	 * @return postal code <code>String</code> if defined; <code>null</code> otherwise.
	 */
	public String getPostalCode () {
		return postalCode;
	}


	/**
	 * Returns <code>true</code> if the address has postal code.
	 * 
	 * @return <code>true</code> if the address has postal code <code>false</code> otherwise
	 */
	public boolean hasPostalCode () {
		return (this.postalCode != null);
	}

	/**
	 * Sets the contact postal code
	 *
	 * @param aPostalCode
	 *        contact postal code
	 */
	public void setPostalCode ( String aPostalCode ) {
		postalCode = aPostalCode;
	}


	/**
	 * Gets the contact country.
	 *
	 * @return contact country <code>String</code> if defined; <code>null</code> otherwise.
	 */
	public String getCountry () {
		return country;
	}

	/**
	 * Returns <code>true</code> if the address has country.
	 * 
	 * @return <code>true</code> if the address has country <code>false</code> otherwise
	 */
	public boolean hasCountry () {
		return (this.country != null);
	}


	/**
	 * Sets the contact country.
	 *
	 * @param aCountry
	 *        contact country
	 */
	public void setCountry ( String aCountry ) {
		country = aCountry;
	}

	
	/**
	 * Validate the state of the {@link EPPContactAddress} instance. A valid state means that all of the required
	 * attributes have been set. If validateState returns without an exception, the state is valid. If the state is not
	 * valid, the EPPCodecException will contain a description of the error. throws EPPCodecException State error. This
	 * will contain the name of the attribute that is not valid.
	 *
	 * @throws EPPCodecException
	 *         throws EPPCodecException if validation fails
	 */
	private void validateState () throws EPPCodecException {

		if ( hasStreets() ) {
			if ( streets.contains( null ) )
				throw new EPPCodecException( "street lines cannot be set to null" );
		}

		if ( (hasStreets() && streets.size() > MAX_STREET) ) {
			throw new EPPCodecException( "street lines exceed the maximum" );
		}

		if ( !hasCity() ) {
			throw new EPPCodecException( "city required attribute is not set" );
		}

		if ( !hasCountry() ) {
			throw new EPPCodecException( "country required attribute is not set" );
		}
	}


	/**
	 * Validate the state of the {@link EPPContactAddress} instance with relaxed validation rules.For relaxed validations,
	 * address is completely optional. A valid state means that all of the required attributes have been set. If
	 * validateState returns without an exception, the state is valid. If the state is not valid, the EPPCodecException
	 * will contain a description of the error. throws EPPCodecException State error. This will contain the name of the
	 * attribute that is not valid.
	 *
	 * @throws EPPCodecException
	 *         throws EPPCodecException if validation fails
	 */
	private void relaxedValidateState () throws EPPCodecException {

		if ( hasStreets() ) {
			if ( this.streets.contains( null ) )
				throw new EPPCodecException( "street lines cannot be set to null" );
		}

		if ( (hasStreets()) && this.streets.elements().hasMoreElements() && (this.streets.size() > MAX_STREET) ) {
			throw new EPPCodecException( "street lines exceed the maximum" );
		}
	}


	/**
	 * Encode a DOM Element tree from the attributes of the {@link EPPContactAddress} instance.
	 *
	 * @param aDocument
	 *        DOM Document that is being built. Used as an Element factory.
	 * @return Root DOM Element representing the {@link EPPContactAddress} instance.
	 * @exception EPPEncodeException
	 *            Unable to encode {@link EPPContactAddress} instance.
	 */
	public Element encode ( Document aDocument ) throws EPPEncodeException {
		try {
			if ( EPPEnv.isContactRelaxedValidation() ) {
				relaxedValidateState();
			}
			else {
				validateState();
			}
		}
		catch ( EPPCodecException e ) {
			throw new EPPEncodeException( "Invalid state on EPPContactAddress.encode: " + e );
		}

		Element root = aDocument.createElementNS( EPPContactMapFactory.NS, ELM_NAME );

		// Street(s)
		if ( (hasStreets()) && streets.elements().hasMoreElements() ) {
			EPPUtil.encodeVector( aDocument, root, streets, EPPContactMapFactory.NS, ELM_STREET );
		}

		// City
		if ( hasCity() ) {
			EPPUtil.encodeString( aDocument, root, city, EPPContactMapFactory.NS, ELM_CITY );
		}


		// State/Province
		if ( hasStateProvince() ) {
			EPPUtil.encodeString( aDocument, root, stateProvince, EPPContactMapFactory.NS, ELM_STATE_PROVINCE );
		}

		// Postal Code
		if ( hasPostalCode() ) {
			EPPUtil.encodeString( aDocument, root, postalCode, EPPContactMapFactory.NS, ELM_POSTAL_CODE );
		}

		// Country

		if ( hasCountry() ) {
			EPPUtil.encodeString( aDocument, root, country, EPPContactMapFactory.NS, ELM_COUNTRY );
		}


		return root;
	}


	/**
	 * Decode the {@link EPPContactAddress} attributes from the aElement DOM Element tree.
	 *
	 * @param aElement
	 *        Root DOM Element to decode {@link EPPContactAddress} from.
	 * @exception EPPDecodeException
	 *            Unable to decode aElement.
	 */
	public void decode ( Element aElement ) throws EPPDecodeException {
		// Street(s)
		streets = EPPUtil.decodeVector( aElement, EPPContactMapFactory.NS, EPPContactAddress.ELM_STREET );

		if ( streets.size() == 0 ) {
			streets = null;
		}

		// City
		city = EPPUtil.decodeString( aElement, EPPContactMapFactory.NS, ELM_CITY );

		// State/Province
		stateProvince = EPPUtil.decodeString( aElement, EPPContactMapFactory.NS, ELM_STATE_PROVINCE );

		// Postal Code
		postalCode = EPPUtil.decodeString( aElement, EPPContactMapFactory.NS, ELM_POSTAL_CODE );

		// Country
		country = EPPUtil.decodeString( aElement, EPPContactMapFactory.NS, ELM_COUNTRY );
	}


	/**
	 * implements a deep {@link EPPContactAddress} compare.
	 *
	 * @param aObject
	 *        {@link EPPContactAddress} instance to compare with
	 * @return boolean
	 */
	public boolean equals ( Object aObject ) {
		if ( !(aObject instanceof EPPContactAddress) ) {
			return false;
		}

		EPPContactAddress theComp = (EPPContactAddress) aObject;

		// Street(s)
		if ( !EPPUtil.equalVectors( streets, theComp.streets ) ) {
			return false;
		}

		// City
		if ( !EqualityUtil.equals( this.city, theComp.city ) ) {
			return false;
		}

		// State/Province
		if ( !EqualityUtil.equals( this.stateProvince, theComp.stateProvince ) ) {
				return false;
		}

		// Postal Code
		if ( !EqualityUtil.equals( this.postalCode, theComp.postalCode ) ) {
			return false;
		}

		// Country
		if ( !EqualityUtil.equals( this.country, theComp.country ) ) {
			return false;
		}

		return true;
	}


	/**
	 * Clone {@link EPPContactAddress}.
	 *
	 * @return clone of {@link EPPContactAddress}
	 * @exception CloneNotSupportedException
	 *            standard Object.clone exception
	 */
	public Object clone () throws CloneNotSupportedException {
		EPPContactAddress clone = null;

		clone = (EPPContactAddress) super.clone();

		if ( hasStreets() ) {
			clone.streets = (Vector) streets.clone();
		}

		return clone;
	}


	/**
	 * Implementation of {@linkObject#toString}, which will result in an indented XML <code>String</code> representation
	 * of the concrete {@link EPPCodecComponent}.
	 *
	 * @return Indented XML <code>String</code> if successful; <code>ERROR</code> otherwise.
	 */
	public String toString () {
		return EPPUtil.toString( this );
	}

}
