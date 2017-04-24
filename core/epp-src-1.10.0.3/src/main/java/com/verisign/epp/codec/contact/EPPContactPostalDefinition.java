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

import org.w3c.dom.Document;
//----------------------------------------------
//
// imports...
//
//----------------------------------------------
// Java Core Imports
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
 * Represents a contact postal address definition that is used in <code>EPPContactCreateCmd</code>,
 * <code>EPPContactUpdateCmd</code>, and <code>EPPContactInfoResp</code>. The child elements associated with an
 * <code>EPPContactPostalDefinition</code> include: <br>
 * <ul>
 * <li>A &lt;contact:name&gt; element that contains the name of the individual or role represented by the contact. Use
 * <code>getName</code> and <code>setName</code> to get and set the element.</li>
 * <li>An OPTIONAL &lt;contact:org&gt; element that contains the name of the organization with which the contact is
 * affiliated. Use <code>getOrg</code> and <code>setOrg</code> to get and set the element.</li>
 * <li>A &lt;contact:addr&gt; element that contains address information associated with the contact. Use
 * <code>getAddress</code> and <code>setAdress</code> to get and set the element.</li>
 * </ul>
 * <br>
 * <br>
 *
 * @author $Author: jim $
 * @version $Revision: 1.3 $
 */
public class EPPContactPostalDefinition implements EPPCodecComponent {
	/** XML tag name for the <code>org</code> attribute. */
	public final static String ELM_NAME_POSTAL_INFO = "contact:postalInfo";

	/** XML tag name for the <code>org</code> attribute. */
	private final static String ELM_ORG = "contact:org";

	/** XML tag name for the <code>org</code> attribute. */
	private final static String ELM_CONTACT_NAME = "contact:name";

	/** XML Attribute Name for postalInfo type. */
	private final static String ATTR_TYPE = "type";

	/** Value of the LOC in contact postal info type mapping */
	public final static java.lang.String ATTR_TYPE_LOC = "loc";

	/** Value of the INT in contact postal info type mapping */
	public final static java.lang.String ATTR_TYPE_INT = "int";

	/**
	 * XML root element tag name for contact postal definition The value needs to be set before calling encode(Document)
	 * and default value is set to <code>ELM_NAME_POSTAL_INFO</code>.
	 */
	private java.lang.String rootName = ELM_NAME_POSTAL_INFO;

	/**
	 * Attribute Name of <code>EPPContactPostalDefinition</code> root element.
	 */
	private java.lang.String type = ATTR_TYPE_INT;

	/** contact name */
	private String name = null;

	/** contact organization */
	private String org = null;

	/** contact address */
	private EPPContactAddress address = null;

	/**
	 * A flag to show whether validateState() been called before calling encode(Document)
	 */
	private boolean validatedFlag = true;


	/**
	 * <code>EPPContactPostalDefinition</code> default constructor. Must call required setter methods before invoking
	 * <code>encode</code>, which include:<br>
	 * <br>
	 * <ul>
	 * <li>id - <code>setId</code></li>
	 * <li>name - <code>setName</code></li>
	 * <li>type - <code>setType</code></li>
	 * <li>address - <code>setAddress</code></li>
	 * <li>voice - <code>setVoice</code></li>
	 * <li>email - <code>setEmail</code></li>
	 * </ul>
	 * <br>
	 * <br>
	 * The following optional attributes can be set:<br>
	 * <br>
	 * <ul>
	 * <li>org - <code>setOrg</code></li>
	 * <li>fax - <code>setFax</code></li>
	 * </ul>
	 */
	public EPPContactPostalDefinition () {
		// Default values set in attribute definitions.
	}


	/**
	 * <code>EPPContactPostalDefinition</code> constructor that takes the contact address type as argument.
	 *
	 * @param aType
	 *        address type which should be one of the <code>ATTR_TYPE</code> constants.
	 */
	public EPPContactPostalDefinition ( String aType ) {
		type = aType;
	}


	/**
	 * <code>EPPContactPostalDefinition</code> constructor that sets the required attributes with the parameters. The
	 * following optional attribute can be set:<br>
	 * <br>
	 * <ul>
	 * <li>org - <code>setOrg</code></li>
	 * <li>fax - <code>setFax</code></li>
	 * <li>name - <code>setName</code></li>
	 * </ul>
	 * 
	 * @param aType
	 *        postal definition type loc or int
	 * @param aAddress
	 *        contact address
	 */
	public EPPContactPostalDefinition ( String aType, EPPContactAddress aAddress ) {
		type = aType;
		address = aAddress;
	}


	/**
	 * <code>EPPContactPostalDefinition</code> constructor that sets the required attributes with the parameters. The
	 * following optional attribute can be set:<br>
	 * <br>
	 * <ul>
	 * <li>org - <code>setOrg</code></li>
	 * <li>fax - <code>setFax</code></li>
	 * </ul>
	 * 
	 * @param aName
	 *        contact name
	 * @param aType
	 *        postal definition type loc or int
	 * @param aAddress
	 *        contact address
	 */
	public EPPContactPostalDefinition ( String aName, String aType, EPPContactAddress aAddress ) {
		name = aName;
		type = aType;
		address = aAddress;
	}


	/**
	 * <code>EPPContactPostalDefinition</code> constructor that sets all of the attribute with the parameter values.
	 *
	 * @param aName
	 *        contact name
	 * @param aOrg
	 *        contact organization if defined; <code>null</code> otherwise
	 * @param aType
	 *        postal definition type loc or int
	 * @param aAddress
	 *        contact address
	 */
	public EPPContactPostalDefinition ( String aName, String aOrg, String aType, EPPContactAddress aAddress ) {
		name = aName;
		org = aOrg;
		type = aType;
		address = aAddress;
	}


	/**
	 * Gets the contact organization
	 *
	 * @return Client organization if defined; <code>null</code> otherwise.
	 */
	public String getOrg () {
		return org;
	}


	/**
	 * Returns <code>true</code> if the postal info definition has organization.
	 * 
	 * @return <code>true</code> if the postal info definition has organization <code>false</code> otherwise
	 */
	public boolean hasOrg () {
		return (this.org != null);
	}


	/**
	 * Sets the contact organization
	 *
	 * @param aOrg
	 *        Client organization
	 */
	public void setOrg ( String aOrg ) {
		org = aOrg;
	}


	/**
	 * Gets the contact address
	 *
	 * @return Contact address if defined; <code>null</code> otherwise.
	 */
	public EPPContactAddress getAddress () {
		return address;
	}


	/**
	 * Returns <code>true</code> if the postal info defintion has address.
	 * 
	 * @return <code>true</code> if the postal info definition has address <code>false</code> otherwise
	 */
	public boolean hasAddress () {
		return (this.address != null);
	}


	/**
	 * Sets the contact address
	 *
	 * @param aAddress
	 *        Contact address
	 */
	public void setAddress ( EPPContactAddress aAddress ) {
		address = aAddress;
	}

	
	/**
	 * Encode a DOM Element tree from the attributes of the {@link EPPContactPostalDefinition} instance.
	 *
	 * @param aDocument
	 *        DOM Document that is being built. Used as an Element factory.
	 * @return Element
	 * @exception EPPEncodeException
	 */
	public Element encode ( Document aDocument ) throws EPPEncodeException {
		// validate states if validatedFlag been set
		if ( validatedFlag ) {
			try {
				if ( EPPEnv.isContactRelaxedValidation() ) {
					relaxedValidateState();
				}
				else {
					validateState();
				}
			}
			catch ( EPPCodecException e ) {
				throw new EPPEncodeException( "Invalid state on EPPContactPostalDefination.encode: " + e );
			}
		}

		Element root = aDocument.createElementNS( EPPContactMapFactory.NS, rootName );

		// add attribute type
		root.setAttribute( ATTR_TYPE, type );

		// Name
		EPPUtil.encodeString( aDocument, root, name, EPPContactMapFactory.NS, ELM_CONTACT_NAME );

		// Organization
		EPPUtil.encodeString( aDocument, root, org, EPPContactMapFactory.NS, ELM_ORG );

		// Address //address element is optional
		if ( hasAddress() ) {
			EPPUtil.encodeComp( aDocument, root, address );
		}

		return root;
	}


	/**
	 * Decode the {@link EPPContactPostalDefinition} attributes from the aElement DOM Element tree.
	 *
	 * @param aElement
	 *        Root DOM Element to decode <code>EPPContactPostalDefinition</code> from.
	 * @exception EPPDecodeException
	 *            Unable to decode aElement
	 */
	public void decode ( Element aElement ) throws EPPDecodeException {
		// Name
		name = EPPUtil.decodeString( aElement, EPPContactMapFactory.NS, ELM_CONTACT_NAME );

		// Organization
		org = EPPUtil.decodeString( aElement, EPPContactMapFactory.NS, ELM_ORG );

		// Type
		type = aElement.getAttribute( ATTR_TYPE );

		// Address
		address =
				(EPPContactAddress) EPPUtil.decodeComp( aElement, EPPContactMapFactory.NS, EPPContactAddress.ELM_NAME,
						EPPContactAddress.class );
	}


	/**
	 * implements a deep <code>EPPContactPostalDefinition</code> compare.
	 *
	 * @param aObject
	 *        <code>EPPContactPostalDefinition</code> instance to compare with
	 * @return <code>true</code> if objects are equal otherwise <code>false</code>
	 */
	public boolean equals ( Object aObject ) {
		if ( !(aObject instanceof EPPContactPostalDefinition) ) {
			return false;
		}

		EPPContactPostalDefinition theComp = (EPPContactPostalDefinition) aObject;

		// name
		if ( !EqualityUtil.equals( this.name, theComp.name ) ) {
			return false;
		}


		// org
		if ( !EqualityUtil.equals( this.org, theComp.org ) ) {
			return false;
		}


		// type
		if ( !EqualityUtil.equals( this.type, theComp.type ) ) {
			return false;
		}

		// address
		if ( !EqualityUtil.equals( this.address, theComp.address ) ) {
			return false;
		}

		return true;
	}


	/**
	 * Clone <code>EPPContactPostalDefinition</code>.
	 *
	 * @return clone of <code>EPPContactPostalDefinition</code>
	 * @exception CloneNotSupportedException
	 *            standard Object.clone exception
	 */
	public Object clone () throws CloneNotSupportedException {
		EPPContactPostalDefinition clone = (EPPContactPostalDefinition) super.clone();

		if ( hasAddress() ) {
			clone.address = (EPPContactAddress) address.clone();
		}

		return clone;
	}


	/**
	 * Implementation of <code>Object.toString</code>, which will result in an indented XML <code>String</code>
	 * representation of the concrete <code>EPPCodecComponent</code>.
	 *
	 * @return Indented XML <code>String</code> if successful; <code>ERROR</code> otherwise.
	 */
	public String toString () {
		return EPPUtil.toString( this );
	}


	/**
	 * Validate the state of the {@link EPPContactPostalDefination} instance. A valid state means that all of the required
	 * attributes have been set. If validateState returns without an exception, the state is valid. If the state is not
	 * valid, the EPPCodecException will contain a description of the error. This will contain the name of the attribute
	 * that is not valid.
	 *
	 * @throws EPPCodecException
	 *         throws {@link EPPCodecException} if validation fails
	 */
	private void validateState () throws EPPCodecException {
		if ( !hasRootName() ) {
			throw new EPPCodecException( "root element name is not set" );
		}

		if ( !rootName.equals( ELM_NAME_POSTAL_INFO ) ) {
			throw new EPPCodecException( "root element name is not recognized" );
		}

		if ( !hasName() ) {
			throw new EPPCodecException( "name required attribute is not set" );
		}

		if ( !hasType() ) {
			throw new EPPCodecException( "required attribute type is not set" );
		}

		if ( !hasAddress() ) {
			throw new EPPCodecException( "address required attribute is not set" );
		}
	}

	/**
	 * Validate the state of the <code>EPPContactPostalDefination</code> instance with relaxed validation rules. For
	 * relaxed validations only postal info type is mandatory. A valid state means that all of the required attributes
	 * have been set. If validateState returns without an exception, the state is valid. If the state is not valid, the
	 * EPPCodecException will contain a description of the error. This will contain the name of the attribute that is not
	 * valid.
	 *
	 * @throws EPPCodecException
	 *         throws {@link EPPCodecException} if validation fails
	 */
	private void relaxedValidateState () throws EPPCodecException {
		if ( !hasRootName() ) {
			throw new EPPCodecException( "root element name is not set" );
		}

		if ( !rootName.equals( ELM_NAME_POSTAL_INFO ) ) {
			throw new EPPCodecException( "root element name is not recognized" );
		}


		if ( !hasType() ) {
			throw new EPPCodecException( "required attribute type is not set" );
		}

	}

	/**
	 * Gets the contact name
	 *
	 * @return Contact Name if defined; <code>null</code> otherwise.
	 */
	public String getName () {
		return name;
	}

	
	/**
	 * Returns <code>true</code> if the postal info definition has name.
	 * 
	 * @return <code>true</code> if the postal info definition has name <code>false</code> otherwise
	 */
	public boolean hasName () {
		return (this.name != null);
	}


	/**
	 * Get root tag name for contact postal definition.
	 *
	 * @return String root tag name
	 */
	public String getRootName () {
		return rootName;
	}


	/**
	 * Returns <code>true</code> if the postal info definition has root name.
	 * 
	 * @return <code>true</code> if the postal info definition has root name <code>false</code> otherwise
	 */
	public boolean hasRootName () {
		return (this.rootName != null);
	}


	/**
	 * Sets the contact name.
	 *
	 * @param aName
	 *        Contact Name
	 */
	public void setName ( String aName ) {
		name = aName;
	}


	/**
	 * Set root tag name for contact postal definition.
	 *
	 * @param newRootName
	 *        String
	 */
	public void setRootName ( String newRootName ) {
		rootName = newRootName;
	}


	/**
	 * Show whether needs to call validateState()
	 *
	 * @return boolean
	 */
	public boolean isValidated () {
		return validatedFlag;
	}


	/**
	 * Set validated flag.
	 *
	 * @param newValidatedFlag
	 *        boolean
	 */
	public void setValidatedFlag ( boolean newValidatedFlag ) {
		validatedFlag = newValidatedFlag;
	}


	/**
	 * Get contact address type.
	 *
	 * @return String Contact type
	 */
	public String getType () {
		return type;
	}


	/**
	 * Returns <code>true</code> if the postal info definition has type.
	 * 
	 * @return <code>true</code> if the postal info definition has type <code>false</code> otherwise
	 */
	public boolean hasType () {
		return (this.type != null);
	}


	/**
	 * Set contact type.
	 *
	 * @param newType
	 *        String
	 */
	public void setType ( String newType ) {
		type = newType;
	}

}
