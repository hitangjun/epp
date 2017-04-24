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

import java.util.Vector;

import org.w3c.dom.Document;
//----------------------------------------------
//
// imports...
//
//----------------------------------------------
// Java Core Imports
// W3C Imports
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.verisign.epp.codec.gen.EPPAuthInfo;
import com.verisign.epp.codec.gen.EPPCodec;
import com.verisign.epp.codec.gen.EPPCodecException;
// EPP Imports
import com.verisign.epp.codec.gen.EPPCreateCmd;
import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.util.EPPEnv;
import com.verisign.epp.util.EqualityUtil;

/**
 * Represents an EPP Contact &lt;create&gt; command, which provides a transform that allows a client to create a contact
 * object. In addition to the standard EPP command elements, the &lt;create&gt; command MUST contain a
 * &lt;contact:create&gt; element that identifies the contact namespace and the location of the contact schema. The
 * &lt;contact:create&gt; element contains the following child elements: <br>
 * <br>
 * <ul>
 * <li>A &lt;contact:id&gt; element that contains the server-unique identifier of the contact object. Use
 * <code>getId</code> and <code>setId</code> to get and set the elements.</li>
 * <li>A &lt;contact:postalInfo&gt; element that contains the postal contacts. Use <code>getPostalInfo</code>,
 * <code>addPostalInfo</code> and <code>setPostalInfo</code> to get, add and set the elements.</li>
 * <li>An OPTIONAL &lt;contact:i15d&gt; ("i15d" is short for "internationalized") element that contains child elements
 * whose content SHALL be represented in unrestricted UTF-8. Use <code>getI15d</code> and <code>setI15d</code> to get
 * and set the elements.</li>
 * <li>An OPTIONAL &lt;contact:voice&gt; element that contains the contact's voice telephone number. Use
 * <code>getVoice</code> and <code>setVoice</code> to get and set the elements.</li>
 * <li>An OPTIONAL &lt;contact:fax&gt; element that contains the contact's facsimile telephone number. Use
 * <code>getFax</code> and <code>setFax</code> to get and set the elements.</li>
 * <li>A &lt;contact:email&gt; element that contains the contact's e-mail address. Use <code>getEmail</code> and
 * <code>setEmail</code> to get and set the elements.</li>
 * <li>A &lt;contact:authInfo&gt; element that contains authorization information associated with the contact object.
 * This element MUST NOT be provided if the querying client is not the current sponsoring client. Use
 * <code>getAuthInfo</code> and <code>setAuthInfo</code> to get and set the element.</li>
 * </ul>
 * <br>
 * <code>EPPContactCreateReponse</code> is the response associated with <code>EPPContactCreateCmd</code>. <br>
 * <br>
 *
 * @author $Author: jim $
 * @version $Revision: 1.3 $
 * @see com.verisign.epp.codec.gen.EPPResponse
 * @see com.verisign.epp.codec.contact.EPPContactPostalDefinition
 */
public class EPPContactCreateCmd extends EPPCreateCmd {
	/** XML Element Name of <code>EPPContactCreateCmd</code> root element. */
	final static String ELM_NAME = "contact:create";

	/** XML Element Name of <code>EPPContactCreateCmd</code> root element. */
	private final static String ELM_CONTACT_POSTAL_INFO = "contact:postalInfo";

	/** XML Element Name of <code>EPPContactCreateCmd</code> root element. */
	private final static String ELM_CONTACT_AUTHINFO = "contact:authInfo";

	/** XML Element Name of <code>EPPContactCreateCmd</code> root element. */
	private final static String ELM_CONTACT_EMAIL = "contact:email";

	/** XML Element Name of <code>EPPContactCreateCmd</code> root element. */
	private final static String ELM_CONTACT_FAX = "contact:fax";

	/** XML Element Name of <code>EPPContactCreateCmd</code> root element. */
	private final static String ELM_CONTACT_ID = "contact:id";

	/** XML Element Name of <code>EPPContactCreateCmd</code> root element. */
	private final static String ELM_CONTACT_VOICE = "contact:voice";

	/** XML tag name for the <code>disclose</code> element. */
	private final static String ELM_CONTACT_DISCLOSE = "contact:disclose";

	/**
	 * XML Attribute Name for a phone extension, which applies to fax and voice numbers.
	 */
	private final static String ATTR_EXT = "x";

	/** postal contacts */
	private java.util.Vector postalContacts = new Vector();

	/** authorization information for contact */
	private com.verisign.epp.codec.gen.EPPAuthInfo authInfo = null;

	/** disclose information of contact */
	private com.verisign.epp.codec.contact.EPPContactDisclose disclose = null;

	/** email for contact */
	private String email = null;

	/** fax number for contact */
	private String fax = null;

	/** fax extension number for contact */
	private String faxExt = null;

	/** ID for contact */
	private String id = null;

	/** voice number for contact */
	private String voice = null;

	/** voice extension number for contact */
	private String voiceExt = null;


	/**
	 * Default constructor of EPPContactCreateCmd Allocates a new <code>EPPContactCreateCmd</code> with default attribute
	 * values.
	 */
	public EPPContactCreateCmd () {
	}


	/**
	 * Constructor of EPPContactCreateCmd Allocates a new <code>EPPContactCreateCmd</code> with the contact definition
	 * information.
	 *
	 * @param aTransId
	 *        command transaction id
	 */
	public EPPContactCreateCmd ( String aTransId ) {
		super( aTransId );
	}


	/**
	 * Constructor of EPPContactCreateCmd Allocates a new <code>EPPContactCreateCmd</code> with the contact definition
	 * information.
	 *
	 * @param aTransId
	 *        command transaction id
	 * @param aId
	 *        String ID
	 * @param aPostalContact
	 *        postalInfo element of contact
	 * @param aEmail
	 *        String email
	 * @param aAuthInfo
	 *        authorization information
	 */
	public EPPContactCreateCmd ( String aTransId, String aId, EPPContactPostalDefinition aPostalContact, String aEmail,
			EPPAuthInfo aAuthInfo ) {
		super( aTransId );

		id = aId;
		postalContacts.add( aPostalContact );
		voice = null;
		fax = null;
		email = aEmail;
		authInfo = aAuthInfo;
		authInfo.setRootName( EPPContactMapFactory.NS, EPPContactMapFactory.ELM_CONTACT_AUTHINFO );
	}

	/**
	 * Get the EPP command Namespace associated with EPPContactCreateCmd.
	 *
	 * @return <code>EPPContactMapFactory.NS</code>
	 */
	public String getNamespace () {
		return EPPContactMapFactory.NS;
	}


	/**
	 * Validate the state of the <code>EPPContactCreateCmd</code> instance. A valid state means that all of the required
	 * attributes have been set. If validateState returns without an exception, the state is valid. If the state is not
	 * valid, the EPPCodecException will contain a description of the error. This will contain the name of the attribute
	 * that is not valid.
	 *
	 * @throws EPPCodecException
	 *         DOCUMENT ME!
	 */
	private void validateState () throws EPPCodecException {
		if ( !hasId() ) {
			throw new EPPCodecException( "required attribute contact id is not set" );
		}

		if ( !hasPostalContacts() ) {
			throw new EPPCodecException( "required attribute contact postalInfo is not set" );
		}

		if ( !hasEmail() ) {
			throw new EPPCodecException( "required attribute contact email is not set" );
		}

		if ( !hasAuthInfo() ) {
			throw new EPPCodecException( "required attribute contact authInfo is not set" );
		}
	}


	/**
	 * Validate the state of the <code>EPPContactCreateCmd</code> instance with relaxed validation rules. For relaxed
	 * validations contact Id, postal info type and authinfo are mandatory. A valid state means that all of the required
	 * attributes have been set. If validateState returns without an exception, the state is valid. If the state is not
	 * valid, the EPPCodecException will contain a description of the error. This will contain the name of the attribute
	 * that is not valid.
	 *
	 * @throws EPPCodecException
	 *         throws EPPCodeException if validation fails
	 */
	private void relaxedValidateState () throws EPPCodecException {
		if ( !hasId() ) {
			throw new EPPCodecException( "required attribute contact id is not set" );
		}

		if ( !hasPostalContacts() ) {
			throw new EPPCodecException( "required attribute contact postalInfo is not set" );
		}

		if ( !hasAuthInfo() ) {
			throw new EPPCodecException( "required attribute contact authInfo is not set" );
		}
	}


	/**
	 * Encode a DOM Element tree from the attributes of the EPPContactCreateCmd instance.
	 *
	 * @param aDocument
	 *        - DOM Document that is being built. Used as an Element factory.
	 * @return Root DOM Element representing the EPPContactCreateCmd instance.
	 * @exception EPPEncodeException
	 *            Unable to encode EPPContactCreateCmd instance.
	 */
	protected Element doEncode ( Document aDocument ) throws EPPEncodeException {
		Element currElm = null;
		Text currVal = null;

		try {
			if ( EPPEnv.isContactRelaxedValidation() ) {
				relaxedValidateState();
			}
			else {
				validateState();
			}
		}
		catch ( EPPCodecException e ) {
			throw new EPPEncodeException( "Invalid state on EPPContactCreateCmd.encode: " + e );
		}

		Element root = aDocument.createElementNS( EPPContactMapFactory.NS, ELM_NAME );

		root.setAttribute( "xmlns:contact", EPPContactMapFactory.NS );
		root.setAttributeNS( EPPCodec.NS_XSI, "xsi:schemaLocation", EPPContactMapFactory.NS_SCHEMA );

		// id
		EPPUtil.encodeString( aDocument, root, id, EPPContactMapFactory.NS, ELM_CONTACT_ID );

		// postalInfo
		EPPUtil.encodeCompVector( aDocument, root, postalContacts );

		// voice
		if ( hasVoice() ) {
			currElm = aDocument.createElementNS( EPPContactMapFactory.NS, ELM_CONTACT_VOICE );
			currVal = aDocument.createTextNode( voice );

			// voiceExt
			if ( hasVoiceExt() ) {
				currElm.setAttribute( ATTR_EXT, voiceExt );
			}

			currElm.appendChild( currVal );
			root.appendChild( currElm );
		}

		// fax
		if ( hasFax() ) {
			currElm = aDocument.createElementNS( EPPContactMapFactory.NS, ELM_CONTACT_FAX );
			currVal = aDocument.createTextNode( fax );

			// faxExt
			if ( hasFaxExt() ) {
				currElm.setAttribute( ATTR_EXT, faxExt );
			}

			currElm.appendChild( currVal );
			root.appendChild( currElm );
		}

		// email
		EPPUtil.encodeString( aDocument, root, email, EPPContactMapFactory.NS, ELM_CONTACT_EMAIL );

		// authInfo
		EPPUtil.encodeComp( aDocument, root, authInfo );

		// disclose
		EPPUtil.encodeComp( aDocument, root, disclose );

		return root;
	}


	/**
	 * Decode the EPPContactCreateCmd attributes from the aElement DOM Element tree.
	 *
	 * @param aElement
	 *        - Root DOM Element to decode EPPContactCreateCmd from.
	 * @exception EPPDecodeException
	 *            Unable to decode aElement
	 */
	protected void doDecode ( Element aElement ) throws EPPDecodeException {
		Element currElm = null;

		// id
		id = EPPUtil.decodeString( aElement, EPPContactMapFactory.NS, ELM_CONTACT_ID );

		// postalContacts
		postalContacts =
				EPPUtil.decodeCompVector( aElement, EPPContactMapFactory.NS, ELM_CONTACT_POSTAL_INFO,
						EPPContactPostalDefinition.class );

		// voice
		voice = EPPUtil.decodeString( aElement, EPPContactMapFactory.NS, ELM_CONTACT_VOICE );

		// voiceExt
		if ( hasVoice() ) {
			currElm = EPPUtil.getElementByTagNameNS( aElement, EPPContactMapFactory.NS, ELM_CONTACT_VOICE );
			voiceExt = currElm.getAttribute( ATTR_EXT );

			if ( voiceExt.length() == 0 ) {
				voiceExt = null;
			}
		}
		else {
			voiceExt = null;
		}

		// fax
		fax = EPPUtil.decodeString( aElement, EPPContactMapFactory.NS, ELM_CONTACT_FAX );

		// faxExt
		if ( hasFax() ) {
			currElm = EPPUtil.getElementByTagNameNS( aElement, EPPContactMapFactory.NS, ELM_CONTACT_FAX );
			faxExt = currElm.getAttribute( ATTR_EXT );

			if ( faxExt.length() == 0 ) {
				faxExt = null;
			}
		}
		else {
			faxExt = null;
		}

		// email
		email = EPPUtil.decodeString( aElement, EPPContactMapFactory.NS, ELM_CONTACT_EMAIL );

		// authInfo
		authInfo =
				(EPPAuthInfo) EPPUtil.decodeComp( aElement, EPPContactMapFactory.NS, ELM_CONTACT_AUTHINFO, EPPAuthInfo.class );

		// disclose
		disclose =
				(EPPContactDisclose) EPPUtil.decodeComp( aElement, EPPContactMapFactory.NS, ELM_CONTACT_DISCLOSE,
						EPPContactDisclose.class );
	}


	/**
	 * Compare an instance of <code>EPPContactCreateCmd</code> with this instance.
	 *
	 * @param aObject
	 *        Object to compare with.
	 * @return DOCUMENT ME!
	 */
	public boolean equals ( Object aObject ) {
		if ( !(aObject instanceof EPPContactCreateCmd) ) {
			return false;
		}

		if ( !super.equals( aObject ) ) {
			return false;
		}

		EPPContactCreateCmd theComp = (EPPContactCreateCmd) aObject;

		// id
		if ( !EqualityUtil.equals( this.id, theComp.id ) ) {
			return false;
		}


		// postalContacts
		if ( !EPPUtil.equalVectors( postalContacts, theComp.postalContacts ) ) {
			return false;
		}

		// voice
		if ( !EqualityUtil.equals( this.voice, theComp.voice ) ) {
			return false;
		}


		// voiceExt
		if ( !EqualityUtil.equals( this.voiceExt, theComp.voiceExt ) ) {
			return false;
		}


		// fax
		if ( !EqualityUtil.equals( this.fax, theComp.fax ) ) {
			return false;
		}

		// faxExt
		if ( !EqualityUtil.equals( this.faxExt, theComp.faxExt ) ) {
			return false;
		}

		// email
		if ( !EqualityUtil.equals( this.email, theComp.email ) ) {
			return false;
		}


		// authInfo
		if ( !EqualityUtil.equals( this.authInfo, theComp.authInfo ) ) {
			return false;
		}
		

		// disclose
		if ( !EqualityUtil.equals( this.disclose, theComp.disclose ) ) {
			return false;

		}
		return true;
	}

	/**
	 * Clone <code>EPPContactCreateCmd</code>.
	 *
	 * @return clone of <code>EPPContactCreateCmd</code>
	 * @exception CloneNotSupportedException
	 *            standard Object.clone exception
	 */
	public Object clone () throws CloneNotSupportedException {
		EPPContactCreateCmd clone = (EPPContactCreateCmd) super.clone();

		if ( hasPostalContacts() ) {
			clone.postalContacts = (Vector) postalContacts.clone();

			for ( int i = 0; i < postalContacts.size(); i++ )
				clone.postalContacts.setElementAt( ((EPPContactPostalDefinition) postalContacts.elementAt( i )).clone(), i );
		}

		if ( hasAuthInfo() ) {
			clone.authInfo = (EPPAuthInfo) authInfo.clone();
		}

		if ( hasDisclose() ) {
			clone.disclose = (EPPContactDisclose) disclose.clone();
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
	 * Get postalInfo elements of contact.
	 *
	 * @return java.util.Vector
	 */
	public java.util.Vector getPostalInfo () {
		return postalContacts;
	}


	/**
	 * Set contact postalInfo.
	 *
	 * @param newPostalContacts
	 *        java.util.Vector
	 */
	public void setPostalInfo ( java.util.Vector newPostalContacts ) {
		postalContacts = newPostalContacts;
	}

	/**
	 * Returns <code>true</code> if the contact has postal info.
	 * 
	 * @return <code>true</code> if the contact has postal info <code>false</code> otherwise
	 */
	public boolean hasPostalContacts () {
		return (this.postalContacts != null && this.postalContacts.elements().hasMoreElements());
	}


	/**
	 * Adds contact postalInfo.
	 *
	 * @param newPostalInfo
	 *        com.verisign.epp.codec.contact.EPPContactPostalDefinition
	 */
	public void addPostalInfo ( EPPContactPostalDefinition newPostalInfo ) {
		// clone necessary here
		EPPContactPostalDefinition aPostalContact = null;

		if ( newPostalInfo != null ) {
			try {
				aPostalContact = (EPPContactPostalDefinition) newPostalInfo.clone();
			}
			catch ( CloneNotSupportedException e ) {
				// Nothing needs to be done here
			}

			postalContacts.add( newPostalInfo );
		}
	}


	/**
	 * Get authorization information.
	 *
	 * @return com.verisign.epp.codec.gen.EPPAuthInfo
	 */
	public com.verisign.epp.codec.gen.EPPAuthInfo getAuthInfo () {
		return authInfo;
	}


	/**
	 * Get disclose information.
	 *
	 * @return Disclose information if defined; <code>null</code> otherwise;
	 */
	public com.verisign.epp.codec.contact.EPPContactDisclose getDisclose () {
		return disclose;
	}


	/**
	 * Set disclose information.
	 *
	 * @param newDisclose
	 *        com.verisign.epp.codec.gen.EPPContactDisclose
	 */
	public void setDisclose ( com.verisign.epp.codec.contact.EPPContactDisclose newDisclose ) {
		if ( newDisclose != null ) {
			disclose = newDisclose;
			disclose.setRootName( ELM_CONTACT_DISCLOSE );
		}
	}


	/**
	 * Returns <code>true</code> if the contact has disclose flag.
	 * 
	 * @return <code>true</code> if the contact has disclose flag <code>false</code> otherwise
	 */
	public boolean hasDisclose () {
		return (this.disclose != null);
	}

	/**
	 * Get email.
	 *
	 * @return email if defined; <code>null</code> otherwise.
	 */
	public String getEmail () {
		return email;
	}


	/**
	 * Get fax number.
	 *
	 * @return Fax number if defined; <code>null</code> otherwise.
	 */
	public String getFax () {
		return fax;
	}


	/**
	 * Get fax number extension.
	 *
	 * @return fax number extension if defined; <code>null</code> otherwise.
	 */
	public String getFaxExt () {
		return faxExt;
	}


	/**
	 * Get contact ID.
	 *
	 * @return String
	 */
	public String getId () {
		return id;
	}


	/**
	 * Get voice number.
	 *
	 * @return Voice number if defined; <code>null</code> otherwise.
	 */
	public String getVoice () {
		return voice;
	}


	/**
	 * Get voice number extension.
	 *
	 * @return Voice number extension if defined; <code>null</code> otherwise.
	 */
	public String getVoiceExt () {
		return voiceExt;
	}


	/**
	 * Set authorization information.
	 *
	 * @param newAuthInfo
	 *        com.verisign.epp.codec.gen.EPPAuthInfo
	 */
	public void setAuthInfo ( com.verisign.epp.codec.gen.EPPAuthInfo newAuthInfo ) {
		if ( newAuthInfo != null ) {
			authInfo = newAuthInfo;
			authInfo.setRootName( EPPContactMapFactory.NS, EPPContactMapFactory.ELM_CONTACT_AUTHINFO );
		}
	}


	/**
	 * Returns <code>true</code> if the contact has auth info.
	 * 
	 * @return <code>true</code> if the contact has auth info info <code>false</code> otherwise
	 */
	public boolean hasAuthInfo () {
		return (this.authInfo != null);
	}


	/**
	 * Set email.
	 *
	 * @param newEmail
	 *        String
	 */
	public void setEmail ( String newEmail ) {
		email = newEmail;
	}


	/**
	 * Returns <code>true</code> if the contact has email.
	 * 
	 * @return <code>true</code> if the contact has email <code>false</code> otherwise
	 */
	public boolean hasEmail () {
		return (this.email != null);
	}


	/**
	 * Set fax number.
	 *
	 * @param newFax
	 *        Fax number
	 */
	public void setFax ( String newFax ) {
		fax = newFax;
	}


	/**
	 * Returns <code>true</code> if the contact has fax.
	 * 
	 * @return <code>true</code> if the contact has fax <code>false</code> otherwise
	 */
	public boolean hasFax () {
		return (this.fax != null);
	}


	/**
	 * Set fax number extension.
	 *
	 * @param newFaxExt
	 *        Fax number extension
	 */
	public void setFaxExt ( String newFaxExt ) {
		faxExt = newFaxExt;
	}


	/**
	 * Returns <code>true</code> if the contact has fax extension.
	 * 
	 * @return <code>true</code> if the contact has fax extension <code>false</code> otherwise
	 */
	public boolean hasFaxExt () {
		return (this.faxExt != null);
	}


	/**
	 * Set contact ID.
	 *
	 * @param newId
	 *        String
	 */
	public void setId ( String newId ) {
		id = newId;
	}


	/**
	 * Returns <code>true</code> if the contact has contact ID.
	 * 
	 * @return <code>true</code> if the contact has contact ID <code>false</code> otherwise
	 */
	public boolean hasId () {
		return (this.id != null);
	}


	/**
	 * Set contact voice number.
	 *
	 * @param newVoice
	 *        voice number
	 */
	public void setVoice ( String newVoice ) {
		voice = newVoice;
	}


	/**
	 * Returns <code>true</code> if the contact has voice.
	 * 
	 * @return <code>true</code> if the contact has voice <code>false</code> otherwise
	 */
	public boolean hasVoice () {
		return (this.voice != null);
	}

	/**
	 * Set contact voice extension.
	 *
	 * @param newVoiceExt
	 *        voice extension
	 */
	public void setVoiceExt ( String newVoiceExt ) {
		voiceExt = newVoiceExt;
	}


	/**
	 * Returns <code>true</code> if the contact has voice extension.
	 * 
	 * @return <code>true</code> if the contact has voice extension <code>false</code> otherwise
	 */
	public boolean hasVoiceExt () {
		return (this.voiceExt != null);
	}

}
