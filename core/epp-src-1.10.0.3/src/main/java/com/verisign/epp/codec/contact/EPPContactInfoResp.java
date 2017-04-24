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

import java.util.Date;
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
import org.w3c.dom.Text;

// EPP Imports
import com.verisign.epp.codec.gen.EPPAuthInfo;
import com.verisign.epp.codec.gen.EPPCodec;
import com.verisign.epp.codec.gen.EPPCodecException;
import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPResponse;
import com.verisign.epp.codec.gen.EPPTransId;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.util.EPPEnv;
import com.verisign.epp.util.EqualityUtil;

/**
 * Represents a &lt;contact:infData&gt; response to an <code>EPPContactInfoCmd</code>. When an &lt;info&gt; command has
 * been processed successfully, the EPP &lt;resData&gt; element MUST contain a child &lt;contact:infData&gt; element
 * that identifies the contact namespace and the location of the contact schema. The &lt;contact:infData&gt; element
 * SHALL contain the following child elements: <br>
 * <br>
 * <ul>
 * <li>A &lt;contact:id&gt; element that contains the server-unique identifier of the contact object. Use
 * <code>getId</code> and <code>setId</code> to get and set the elements.</li>
 * <li>One or more &lt;contact:status&gt; elements that describe the status of the contact object. Use
 * <code>getStatuses</code> and <code>setStatuses</code> to get and set the elements.</li>
 * <li>A &lt;contact:postalInfo&gt; element that contains the postal contacts. Use <code>getPostalInfo</code>,
 * <code>addPostalInfo</code> and <code>setPostalInfo</code> to get, add and set the elements.</li>
 * <li>An OPTIONAL &lt;contact:voice&gt; element that contains the contact's voice telephone number. Use
 * <code>getVoice</code> and <code>setVoice</code> to get and set the elements.</li>
 * <li>An OPTIONAL &lt;contact:fax&gt; element that contains the contact's facsimile telephone number. Use
 * <code>getFax</code> and <code>setFax</code> to get and set the elements.</li>
 * <li>A &lt;contact:email&gt; element that contains the contact's e-mail address. Use <code>getEmail</code> and
 * <code>setEmail</code> to get and set the elements.</li>
 * <li>A &lt;contact:clID&gt; element that contains the identifier of the sponsoring client. The sponsoring client is
 * the client that has administrative privileges to manage the object. Use <code>getClientId</code> and
 * <code>setClientId</code> to get and set the element.</li>
 * <li>A &lt;contact:crID&gt; element that contains the identifier of the client that created the contact name. Use
 * <code>getCreatedBy</code> and <code>setCreatedBy</code>to get and set the element.</li>
 * <li>A &lt;contact:crDate&gt; element that contains the date and time of contact creation. Use
 * <code>getCreatedDate</code> and <code>setCreatedDate</code> to get and set the element.</li>
 * <li>A &lt;contact:upID&gt; element that contains the identifier of the client that last updated the contact name.
 * This element MUST NOT be present if the contact has never been modified. Use <code>getLastUpdatedBy</code> and
 * <code>setLastUpdatedBy</code> to get and set the element.</li>
 * <li>A &lt;contact:upDate&gt; element that contains the date and time of the most recent contact modification. This
 * element MUST NOT be present if the contact has never been modified. Use <code>getLastUpdatedDate</code> and
 * <code>setLastUpdatedDate</code> to get and set the element.</li>
 * <li>A &lt;contact:trDate&gt; element that contains the date and time of the most recent successful transfer. This
 * element MUST NOT be provided if the contact has never been transferred. Use <code>getLastTransferDate</code> and
 * <code>setLastTransferDate</code> to get and set the element.</li>
 * <li>An OPTIONAL &lt;contact:authInfo&gt; element that contains authorization information associated with the contact
 * object. This element MUST NOT be provided if the querying client is not the current sponsoring client. Use
 * <code>getAuthInfo</code> and <code>setAuthInfo</code> to get and set the element.</li>
 * <li>An OPTIONAL &lt;contact:disclose&gt; element that contains disclose information associated with the contact
 * object. Use <code>getDisclose</code> and <code>setDisclose</code> to get and set the element.</li>
 * </ul>
 * <br>
 * <br>
 *
 * @author $Author: jim $
 * @version $Revision: 1.3 $
 * @see com.verisign.epp.codec.contact.EPPContactInfoCmd
 */
public class EPPContactInfoResp extends EPPResponse {
	/**
	 *
	 */
	private static final long serialVersionUID = -1028446700493015708L;

	/** XML Element Name of <code>EPPContactInfoResp</code> root element. */
	final static String ELM_NAME = "contact:infData";

	/** XML tag name for the <code>clientId</code> element. */
	private final static String ELM_CLIENT_ID = "contact:clID";

	/** XML tag name for the <code>crID</code> element. */
	private final static String ELM_CREATED_BY = "contact:crID";

	/** XML tag name for the <code>crDate</code> element. */
	private final static String ELM_CREATED_DATE = "contact:crDate";

	/** XML tag name for the <code>upID</code> element. */
	private final static String ELM_LAST_UPDATED_BY = "contact:upID";

	/** XML tag name for the <code>upDate</code> element. */
	private final static String ELM_LAST_UPDATED_DATE = "contact:upDate";

	/** XML tag name for the <code>trDate</code> element. */
	private final static String ELM_LAST_TRANSFER_DATE = "contact:trDate";

	/** XML tag name for the <code>postalInfo</code> element. */
	private final static String ELM_CONTACT_POSTAL_INFO = "contact:postalInfo";

	/** XML tag name for the <code>authInfo</code> element. */
	private final static String ELM_CONTACT_AUTHINFO = "contact:authInfo";

	/** XML tag name for the <code>email</code> element. */
	private final static String ELM_CONTACT_EMAIL = "contact:email";

	/** XML tag name for the <code>fax</code> element. */
	private final static String ELM_CONTACT_FAX = "contact:fax";

	/** XML tag name for the <code>id</code> element. */
	private final static String ELM_CONTACT_ID = "contact:id";

	/** XML tag name for the <code>status</code> element. */
	private final static String ELM_CONTACT_STATUSES = "contact:status";

	/** XML tag name for the <code>voice</code> element. */
	private final static String ELM_CONTACT_VOICE = "contact:voice";

	/** XML tag name for the <code>roid</code> element. */
	private final static String ELM_ROID = "contact:roid";

	/** XML tag name for the <code>disclose</code> element. */
	private final static String ELM_CONTACT_DISCLOSE = "contact:disclose";

	/**
	 * XML Attribute Name for a phone extension, which applies to fax and voice numbers.
	 */
	private final static String ATTR_EXT = "x";

	/** identifier of sponsoring client */
	private String clientId = null;

	/** identifier of the client that created the contact */
	private String createdBy = null;

	/** date and time of contact creation */
	private Date createdDate = null;

	/** identifier of the client that last updated the contact name */
	private String lastUpdatedBy = null;

	/** date and time of the most recent contact modification */
	private Date lastUpdatedDate = null;

	/** date and time of the most recent successful transfer */
	private Date lastTransferDate = null;

	/** postal contacts */
	private java.util.Vector postalContacts = new Vector();

	/** authorization information of contact */
	private com.verisign.epp.codec.gen.EPPAuthInfo authInfo = null;

	/** disclose information of contact */
	private com.verisign.epp.codec.contact.EPPContactDisclose disclose = null;

	/** contact email */
	private java.lang.String email = null;

	/** contact fax number */
	private java.lang.String fax = null;

	/** fax extension number for contact */
	private String faxExt = null;

	/** contact id */
	private java.lang.String id = null;

	/** contact statuses */
	private java.util.Vector statuses = null;

	/** contact voice number */
	private java.lang.String voice = null;

	/** voice extension number */
	private String voiceExt = null;

	/** roid */
	private java.lang.String roid = null;


	/**
	 * <code>EPPContactInfoResp</code> default constructor. Must call required setter methods before invoking
	 * <code>encode</code>, which include:<br>
	 * <br>
	 * <ul>
	 * <li>transaction id - <code>setTransId</code></li>
	 * <li>contact - <code>setContact</code></li>
	 * <li>client id - <code>setClientId</code></li>
	 * <li>postalInfo - <code>setPostalInfo</code></li>
	 * <li>postalInfo - <code>addPostalInfo</code></li>
	 * <li>created by - <code>setCreatedBy</code></li>
	 * <li>created date - <code>setCreatedDate</code></li>
	 * </ul>
	 * <br>
	 * <br>
	 * The following optional attributes can be set:<br>
	 * <br>
	 * <ul>
	 * <li>last updated by - <code>setLastUpdatedBy</code></li>
	 * <li>last updated date - <code>setLastUpdatedDate</code></li>
	 * <li>last transfer by - <code>setLastTransferBy</code></li>
	 * <li>last transfer date - <code>setLastTransferDate</code></li>
	 * <li>authorization id - <code>setAuthId</code></li>
	 * <li>disclose - <code>setDisclose</code></li>
	 * </ul>
	 */
	public EPPContactInfoResp () {
		// Default values set in attribute definitions.
	}


	/**
	 * <code>EPPContactInfoResp</code> constructor that sets the required attributes with the parameters. The following
	 * optional attributes can be set:<br>
	 *
	 * @param aTransId
	 *        command transaction id
	 * @param aRoid
	 *        roid
	 * @param aId
	 *        contact ID
	 * @param newStatuses
	 *        contact statuses
	 * @param aPostalInfo
	 *        postal element of contact
	 * @param aEmail
	 *        contact email
	 * @param aClientId
	 *        contact sponsoring client identifier
	 * @param aCreatedBy
	 *        identifier of the client that created the contact name
	 * @param aCreatedDate
	 *        Date and time of contact creation
	 * @param aAuthInfo
	 *        authorization information
	 */
	public EPPContactInfoResp ( EPPTransId aTransId, String aRoid, String aId, Vector newStatuses,
			EPPContactPostalDefinition aPostalInfo, String aEmail, String aClientId, String aCreatedBy, Date aCreatedDate,
			EPPAuthInfo aAuthInfo ) {
		super( aTransId );

		this.id = aId;
		this.roid = aRoid;
		this.statuses = newStatuses;
		this.postalContacts.add( aPostalInfo );
		this.email = aEmail;
		this.clientId = aClientId;
		this.createdBy = aCreatedBy;
		this.createdDate = aCreatedDate;
		this.authInfo = aAuthInfo;
		this.authInfo.setRootName( EPPContactMapFactory.NS, EPPContactMapFactory.ELM_CONTACT_AUTHINFO );
	}


	/**
	 * Gets the EPP response type associated with <code>EPPContactInfoResp</code>.
	 *
	 * @return <code>EPPContactInfoResp.ELM_NAME</code>
	 */
	public String getType () {
		return ELM_NAME;
	}


	/**
	 * Gets the EPP command namespace associated with <code>EPPContactInfoResp</code>.
	 *
	 * @return <code>EPPContactMapFactory.NS</code>
	 */
	public String getNamespace () {
		return EPPContactMapFactory.NS;
	}


	/**
	 * Gets the contact owning Client Id.
	 *
	 * @return Client Id if defined; <code>null</code> otherwise.
	 */
	public String getClientId () {
		return this.clientId;
	}


	/**
	 * Returns <code>true</code> if the contact has owning client Id.
	 * 
	 * @return <code>true</code> if the contact has owning client Id <code>false</code> otherwise
	 */
	public boolean hasClientId () {
		return (this.clientId != null);
	}

	/**
	 * Sets the contact owning Client Id.
	 *
	 * @param aClientId
	 *        Client Id
	 */
	public void setClientId ( String aClientId ) {
		this.clientId = aClientId;
	}


	/**
	 * Gets Client Id that created the contact.
	 *
	 * @return Client Id if defined; <code>null</code> otherwise.
	 */
	public String getCreatedBy () {
		return this.createdBy;
	}


	/**
	 * Returns <code>true</code> if the contact has client id that created it
	 * 
	 * @return <code>true</code> if the contact has client id that created it <code>false</code> otherwise
	 */
	public boolean hasCreatedBy () {
		return (this.createdBy != null);
	}


	/**
	 * Sets Client Id that created the contact.
	 *
	 * @param aCreatedBy
	 *        Client Id that created the contact if defined; <code>null</code> otherwise.
	 */
	public void setCreatedBy ( String aCreatedBy ) {
		this.createdBy = aCreatedBy;
	}


	/**
	 * Gets the date and time the contact was created.
	 *
	 * @return Date and time the contact was created if defined; <code>null</code> otherwise.
	 */
	public Date getCreatedDate () {
		return this.createdDate;
	}


	/**
	 * Returns <code>true</code> if the contact has created date
	 * 
	 * @return <code>true</code> if the contact has created date <code>false</code> otherwise
	 */
	public boolean hasCreatedDate () {
		return (this.createdDate != null);
	}

	/**
	 * Sets the date and time the contact was created.
	 *
	 * @param aDate
	 *        Date and time the contact was created.
	 */
	public void setCreatedDate ( Date aDate ) {
		this.createdDate = aDate;
	}


	/**
	 * Gets the Client Id that last updated the contact. This will be null if the contact has not been updated since
	 * creation.
	 *
	 * @return Client Id that last updated the contact has been updated; <code>null</code> otherwise.
	 */
	public String getLastUpdatedBy () {
		return this.lastUpdatedBy;
	}

	
	/**
	 * Returns <code>true</code> if the contact has client id that last updated it
	 * 
	 * @return <code>true</code> if the contact has client id that last updated it <code>false</code> otherwise
	 */
	public boolean hasLastUpdatedBy () {
		return (this.lastUpdatedBy != null);
	}


	/**
	 * Sets the Client Id that last updated the contact.
	 *
	 * @param aLastUpdatedBy
	 *        Client Id String that last updated the contact.
	 */
	public void setLastUpdatedBy ( String aLastUpdatedBy ) {
		this.lastUpdatedBy = aLastUpdatedBy;
	}


	/**
	 * Gets the date and time of the last contact update. This will be <code>null</code> if the contact has not been
	 * updated since creation.
	 *
	 * @return date and time of the last contact update if defined; <code>null</code> otherwise.
	 */
	public Date getLastUpdatedDate () {
		return this.lastUpdatedDate;
	}


	/**
	 * Returns <code>true</code> if the contact has last date/time it was updated
	 * 
	 * @return <code>true</code> if the contact has last date/time it was updated <code>false</code> otherwise
	 */
	public boolean hasLastUpdatedDate () {
		return (this.lastUpdatedDate != null);
	}


	/**
	 * Sets the last date and time the contact was updated.
	 *
	 * @param aLastUpdatedDate
	 *        Date and time of the last contact update
	 */
	public void setLastUpdatedDate ( Date aLastUpdatedDate ) {
		this.lastUpdatedDate = aLastUpdatedDate;
	}


	/**
	 * Gets the last date and time the contact was successfully transferred.
	 *
	 * @return Date and time of the last successful transfer if defined; <code>null</code> otherwise.
	 */
	public Date getLastTransferDate () {
		return this.lastTransferDate;
	}


	/**
	 * Returns <code>true</code> if the contact has the last date/time it was transferred
	 * 
	 * @return <code>true</code> if the contact has the last date/time it was transferred <code>false</code> otherwise
	 */
	public boolean hasLastTransferDate () {
		return (this.lastTransferDate != null);
	}


	/**
	 * Sets the last date and time the contact was successfully transferred.
	 *
	 * @param aLastTransferDate
	 *        Date and time of the last successful transfer.
	 */
	public void setLastTransferDate ( Date aLastTransferDate ) {
		this.lastTransferDate = aLastTransferDate;
	}


	/**
	 * Validate the state of the {@link EPPContactInfoResp } instance. A valid state means that all of the required
	 * attributes have been set. If validateState returns without an exception, the state is valid. If the state is not
	 * valid, the EPPCodecException will contain a description of the error. This will contain the name of the attribute
	 * that is not valid.
	 *
	 * @throws EPPCodecException
	 *         throws EPPCodecException if validation fails
	 */
	private void validateState () throws EPPCodecException {
		if ( !hasId() ) {
			throw new EPPCodecException( "required attribute id is not set" );
		}

		if ( !hasRoid() ) {
			throw new EPPCodecException( "required attribute roid is not set" );
		}

		if ( !hasStatuses() ) {
			throw new EPPCodecException( "required attribute contact statuses is not set" );
		}

		if ( !hasPostalContacts() ) {
			throw new EPPCodecException( "required attribute postalContacts is not set" );
		}

		if ( !hasEmail() ) {
			throw new EPPCodecException( "required attribute contact email is not set" );
		}

		if ( !hasClientId() ) {
			throw new EPPCodecException( "clientId required attribute is not set" );
		}

		if ( !hasCreatedBy() ) {
			throw new EPPCodecException( "createBy required attribute is not set" );
		}

		if ( !hasCreatedDate() ) {
			throw new EPPCodecException( "createdDate required attribute is not set" );
		}
	}


	/**
	 * Validate the state of the <code>EPPContactInfoResp</code> instance with relaxed validation rules. For relaxed
	 * validation, client Id, authinfo and postal info type are mandarory. A valid state means that all of the required
	 * attributes have been set. If validateState returns without an exception, the state is valid. If the state is not
	 * valid, the EPPCodecException will contain a description of the error. This will contain the name of the attribute
	 * that is not valid.
	 *
	 * @throws EPPCodecException
	 *         DOCUMENT ME!
	 */
	private void relaxedValidateState () throws EPPCodecException {
		if ( !hasId() ) {
			throw new EPPCodecException( "required attribute id is not set" );
		}

		if ( !hasRoid() ) {
			throw new EPPCodecException( "required attribute roid is not set" );
		}

		if ( !hasStatuses() ) {
			throw new EPPCodecException( "required attribute contact statuses is not set" );
		}

		if ( !hasPostalContacts() ) {
			throw new EPPCodecException( "required attribute postalContacts is not set" );
		}


		if ( !hasClientId() ) {
			throw new EPPCodecException( "clientId required attribute is not set" );
		}

		if ( !hasCreatedBy() ) {
			throw new EPPCodecException( "createBy required attribute is not set" );
		}

		if ( !hasCreatedDate() ) {
			throw new EPPCodecException( "createdDate required attribute is not set" );
		}
	}

	/**
	 * Encode a DOM Element tree from the attributes of the <code>EPPContactInfoResp</code> instance.
	 *
	 * @param aDocument
	 *        DOM Document that is being built. Used as an Element factory.
	 * @return Element Root DOM Element representing the EPPContactPingMap instance.
	 * @exception EPPEncodeException
	 *            Unable to encode EPPContactPingMap instance.
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
			throw new EPPEncodeException( "Invalid state on EPPContactInfoResp.doEncode: " + e );
		}

		Element root = aDocument.createElementNS( EPPContactMapFactory.NS, ELM_NAME );

		root.setAttribute( "xmlns:contact", EPPContactMapFactory.NS );
		root.setAttributeNS( EPPCodec.NS_XSI, "xsi:schemaLocation", EPPContactMapFactory.NS_SCHEMA );

		// id
		EPPUtil.encodeString( aDocument, root, this.id, EPPContactMapFactory.NS, ELM_CONTACT_ID );

		// roid
		EPPUtil.encodeString( aDocument, root, this.roid, EPPContactMapFactory.NS, ELM_ROID );

		// statuses
		EPPUtil.encodeCompVector( aDocument, root, this.statuses );

		// postalContacts
		EPPUtil.encodeCompVector( aDocument, root, this.postalContacts );

		// voice
		if ( this.voice != null ) {
			currElm = aDocument.createElementNS( EPPContactMapFactory.NS, ELM_CONTACT_VOICE );
			currVal = aDocument.createTextNode( this.voice );

			// voiceExt
			if ( this.voiceExt != null ) {
				currElm.setAttribute( ATTR_EXT, this.voiceExt );
			}

			currElm.appendChild( currVal );
			root.appendChild( currElm );
		}

		// fax
		if ( this.fax != null ) {
			currElm = aDocument.createElementNS( EPPContactMapFactory.NS, ELM_CONTACT_FAX );
			currVal = aDocument.createTextNode( this.fax );

			// faxExt
			if ( this.faxExt != null ) {
				currElm.setAttribute( ATTR_EXT, this.faxExt );
			}

			currElm.appendChild( currVal );
			root.appendChild( currElm );
		}

		// email
		EPPUtil.encodeString( aDocument, root, this.email, EPPContactMapFactory.NS, ELM_CONTACT_EMAIL );

		// Client Id
		EPPUtil.encodeString( aDocument, root, this.clientId, EPPContactMapFactory.NS, ELM_CLIENT_ID );

		// Created By
		EPPUtil.encodeString( aDocument, root, this.createdBy, EPPContactMapFactory.NS, ELM_CREATED_BY );

		// Created Date
		EPPUtil.encodeTimeInstant( aDocument, root, this.createdDate, EPPContactMapFactory.NS, ELM_CREATED_DATE );

		// Last Updated By
		EPPUtil.encodeString( aDocument, root, this.lastUpdatedBy, EPPContactMapFactory.NS, ELM_LAST_UPDATED_BY );

		// Last Updated Date
		EPPUtil.encodeTimeInstant( aDocument, root, this.lastUpdatedDate, EPPContactMapFactory.NS, ELM_LAST_UPDATED_DATE );

		// Last Transfer Date
		EPPUtil.encodeTimeInstant( aDocument, root, this.lastTransferDate, EPPContactMapFactory.NS, ELM_LAST_TRANSFER_DATE );

		// authInfo
		EPPUtil.encodeComp( aDocument, root, this.authInfo );

		// disclose
		EPPUtil.encodeComp( aDocument, root, this.disclose );

		return root;
	}


	/**
	 * Decode the <code>EPPContactInfoResp</code> attributes from the aElement DOM Element tree.
	 *
	 * @param aElement
	 *        Root DOM Element to decode <code>EPPContactInfoResp</code> from.
	 * @exception EPPDecodeException
	 *            Unable to decode aElement
	 */
	protected void doDecode ( Element aElement ) throws EPPDecodeException {
		Element currElm = null;

		// id
		this.id = EPPUtil.decodeString( aElement, EPPContactMapFactory.NS, ELM_CONTACT_ID );

		// roid
		this.roid = EPPUtil.decodeString( aElement, EPPContactMapFactory.NS, ELM_ROID );

		// statuses
		this.statuses =
				EPPUtil.decodeCompVector( aElement, EPPContactMapFactory.NS, ELM_CONTACT_STATUSES, EPPContactStatus.class );

		// postalInfo
		this.postalContacts =
				EPPUtil.decodeCompVector( aElement, EPPContactMapFactory.NS, ELM_CONTACT_POSTAL_INFO,
						EPPContactPostalDefinition.class );

		// voice
		this.voice = EPPUtil.decodeString( aElement, EPPContactMapFactory.NS, ELM_CONTACT_VOICE );

		// voiceExt
		if ( this.voice != null ) {
			currElm = EPPUtil.getElementByTagNameNS( aElement, EPPContactMapFactory.NS, ELM_CONTACT_VOICE );
			this.voiceExt = currElm.getAttribute( ATTR_EXT );

			if ( this.voiceExt.length() == 0 ) {
				this.voiceExt = null;
			}
		}
		else {
			this.voiceExt = null;
		}

		// fax
		this.fax = EPPUtil.decodeString( aElement, EPPContactMapFactory.NS, ELM_CONTACT_FAX );

		// faxExt
		if ( this.fax != null ) {
			currElm = EPPUtil.getElementByTagNameNS( aElement, EPPContactMapFactory.NS, ELM_CONTACT_FAX );
			this.faxExt = currElm.getAttribute( ATTR_EXT );

			if ( this.faxExt.length() == 0 ) {
				this.faxExt = null;
			}
		}
		else {
			this.faxExt = null;
		}

		// email
		this.email = EPPUtil.decodeString( aElement, EPPContactMapFactory.NS, ELM_CONTACT_EMAIL );

		// Client Id
		this.clientId = EPPUtil.decodeString( aElement, EPPContactMapFactory.NS, ELM_CLIENT_ID );

		// Created By
		this.createdBy = EPPUtil.decodeString( aElement, EPPContactMapFactory.NS, ELM_CREATED_BY );

		// Created Date
		this.createdDate = EPPUtil.decodeTimeInstant( aElement, EPPContactMapFactory.NS, ELM_CREATED_DATE );

		// Last Updated By
		this.lastUpdatedBy = EPPUtil.decodeString( aElement, EPPContactMapFactory.NS, ELM_LAST_UPDATED_BY );

		// Last Updated Date
		this.lastUpdatedDate = EPPUtil.decodeTimeInstant( aElement, EPPContactMapFactory.NS, ELM_LAST_UPDATED_DATE );

		// Last Transfer Date
		this.lastTransferDate = EPPUtil.decodeTimeInstant( aElement, EPPContactMapFactory.NS, ELM_LAST_TRANSFER_DATE );

		// authInfo
		this.authInfo =
				(EPPAuthInfo) EPPUtil.decodeComp( aElement, EPPContactMapFactory.NS, ELM_CONTACT_AUTHINFO, EPPAuthInfo.class );

		// disclose
		this.disclose =
				(EPPContactDisclose) EPPUtil.decodeComp( aElement, EPPContactMapFactory.NS, ELM_CONTACT_DISCLOSE,
						EPPContactDisclose.class );
	}


	/**
	 * Compare an instance of <code>EPPContactInfoResp</code> with this instance.
	 *
	 * @param aObject
	 *        Object to compare with.
	 * @return boolean <code>true</code> if the objects are equal otherwise <code>false</code>
	 */
	public boolean equals ( Object aObject ) {
		if ( !(aObject instanceof EPPContactInfoResp) ) {
			return false;
		}

		if ( !super.equals( aObject ) ) {
			return false;
		}

		EPPContactInfoResp theComp = (EPPContactInfoResp) aObject;

		// id
		if ( !EqualityUtil.equals( this.id, theComp.id ) ) {
			return false;
		}


		// roid
		if ( !EqualityUtil.equals( this.roid, theComp.roid ) ) {
			return false;
		}


		// statuses
		if ( !EPPUtil.equalVectors( this.statuses, theComp.statuses ) ) {
			return false;
		}

		// postalContacts
		if ( !EPPUtil.equalVectors( this.postalContacts, theComp.postalContacts ) ) {
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


		// Client Id
		if ( !EqualityUtil.equals( this.clientId, theComp.clientId ) ) {
			return false;
		}

		// Created By
		if ( !EqualityUtil.equals( this.createdBy, theComp.createdBy ) ) {
			return false;
		}


		// Created Date
		if ( !EqualityUtil.equals( this.createdDate, theComp.createdDate ) ) {
			return false;
		}


		// Last Updated By
		if ( !EqualityUtil.equals( this.lastUpdatedBy, theComp.lastUpdatedBy ) ) {
			return false;
		}


		// Last Updated Date
		if ( !EqualityUtil.equals( this.lastUpdatedDate, theComp.lastUpdatedDate ) ) {
			return false;
		}


		// Last Transfer Date
		if ( !EqualityUtil.equals( this.lastTransferDate, theComp.lastTransferDate ) ) {
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
	 * Clone <code>EPPContactInfoResp</code>.
	 *
	 * @return clone of <code>EPPContactInfoResp</code>
	 * @exception CloneNotSupportedException
	 *            standard Object.clone exception
	 */
	public Object clone () throws CloneNotSupportedException {
		EPPContactInfoResp clone = (EPPContactInfoResp) super.clone();

		if ( hasStatuses() ) {
			clone.statuses = (Vector) this.statuses.clone();

			for ( int i = 0; i < this.statuses.size(); i++ ) {
				clone.statuses.setElementAt( ((EPPContactStatus) this.statuses.elementAt( i )).clone(), i );
			}
		}

		if ( hasPostalContacts() ) {
			clone.postalContacts = (Vector) this.postalContacts.clone();

			for ( int i = 0; i < this.postalContacts.size(); i++ ) {
				clone.postalContacts
						.setElementAt( ((EPPContactPostalDefinition) this.postalContacts.elementAt( i )).clone(), i );
			}
		}

		if ( hasAuthInfo() ) {
			clone.authInfo = (EPPAuthInfo) this.authInfo.clone();
		}

		if ( hasDisclose() ) {
			clone.disclose = (EPPContactDisclose) this.disclose.clone();
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
	 * Gets the contact postal info.
	 *
	 * @return Postal info if set; <code>null</code> otherwise
	 */
	public Vector getPostalInfo () {
		return this.postalContacts;
	}


	/**
	 * Returns <code>true</code> if the contact has postal info
	 * 
	 * @return <code>true</code> if the contact has postal info <code>false</code> otherwise
	 */
	public boolean hasPostalContacts () {
		return (this.postalContacts != null && this.postalContacts.elements().hasMoreElements());
	}

	/**
	 * Gets the first postal info object.
	 *
	 * @return First postal info object (<code>EPPContactPostalDefinition</code> ) if it exists; <code>null</code>
	 *         otherwise.
	 */
	public EPPContactPostalDefinition getFirstPostalInfo () {
		if ( hasPostalContacts() ) {
			return (EPPContactPostalDefinition) this.postalContacts.get( 0 );
		}
		return null;
	}


	/**
	 * Set contact postal info.
	 *
	 * @param aPostalInfo
	 *        <code>Vector</code> of {@link EPPContactPostalDefinition} objects.
	 */
	public void setPostalInfo ( Vector aPostalInfo ) {
		this.postalContacts = aPostalInfo;
	}


	/**
	 * Adds contact postal info.
	 *
	 * @param aPostalInfo
	 *        {@link EPPContactPostalDefinition} object to add to <code>Vector</code> of postal info.
	 */
	public void addPostalInfo ( EPPContactPostalDefinition aPostalInfo ) {
		this.postalContacts.add( aPostalInfo );
	}


	/**
	 * Gets the authorization information.
	 *
	 * @return Authorization information if defined; <code>null</code> otherwise;
	 */
	public EPPAuthInfo getAuthInfo () {
		return this.authInfo;
	}

	
	/**
	 * Returns <code>true</code> if the contact has authinfo
	 * 
	 * @return <code>true</code> if the contact has authinfo <code>false</code> otherwise
	 */
	public boolean hasAuthInfo () {
		return (this.authInfo != null);
	}


	/**
	 * Sets the authorization information.
	 *
	 * @param aAuthInfo
	 *        Authorization information
	 */
	public void setAuthInfo ( EPPAuthInfo aAuthInfo ) {
		if ( aAuthInfo != null ) {
			this.authInfo = aAuthInfo;
			this.authInfo.setRootName( EPPContactMapFactory.NS, EPPContactMapFactory.ELM_CONTACT_AUTHINFO );
		}
	}


	/**
	 * Gets the disclose information.
	 *
	 * @return Disclose information if defined; <code>null</code> otherwise;
	 */
	public EPPContactDisclose getDisclose () {
		return this.disclose;
	}

	
	/**
	 * Returns <code>true</code> if the contact has disclose flag
	 * 
	 * @return <code>true</code> if the contact has disclose flag <code>false</code> otherwise
	 */
	public boolean hasDisclose () {
		return (this.disclose != null);
	}


	/**
	 * Sets the disclose information.
	 *
	 * @param aDisclose
	 *        Contact disclose information
	 */
	public void setDisclose ( EPPContactDisclose aDisclose ) {
		if ( aDisclose != null ) {
			this.disclose = aDisclose;
			this.disclose.setRootName( ELM_CONTACT_DISCLOSE );
		}
	}


	/**
	 * Gets the contact email.
	 *
	 * @return Contact email if defined: <code>null</code> otherwise.
	 */
	public String getEmail () {
		return this.email;
	}


	/**
	 * Returns <code>true</code> if the contact has email
	 * 
	 * @return <code>true</code> if the contact has email <code>false</code> otherwise
	 */
	public boolean hasEmail () {
		return (this.email != null);
	}


	/**
	 * Sets the contact email.
	 *
	 * @param aEmail
	 *        Contact email
	 */
	public void setEmail ( String aEmail ) {
		this.email = aEmail;
	}


	/**
	 * Gets the contact fax number.
	 *
	 * @return Contact fax number if defined; <code>null</code> otherwise.
	 */
	public String getFax () {
		return this.fax;
	}


	/**
	 * Returns <code>true</code> if the contact has fax
	 * 
	 * @return <code>true</code> if the contact has fax <code>false</code> otherwise
	 */
	public boolean hasFax () {
		return (this.fax != null);
	}

	/**
	 * Sets the contact fax number.
	 *
	 * @param aFax
	 *        Contact fax number
	 */
	public void setFax ( String aFax ) {
		this.fax = aFax;
	}


	/**
	 * Gets the fax number extension.
	 *
	 * @return fax number extension if defined; <code>null</code> otherwise.
	 */
	public String getFaxExt () {
		return this.faxExt;
	}


	/**
	 * Returns <code>true</code> if the contact has fax extension
	 * 
	 * @return <code>true</code> if the contact has fax extension <code>false</code> otherwise
	 */
	public boolean hasFaxExt () {
		return (this.faxExt != null);
	}

	/**
	 * Sets the fax number extension.
	 *
	 * @param aFaxExt
	 *        Fax number extension
	 */
	public void setFaxExt ( String aFaxExt ) {
		this.faxExt = aFaxExt;
	}


	/**
	 * Gets the contact identifier.
	 *
	 * @return Contact identifier if defined; <code>null</code> otherwise.
	 */
	public String getId () {
		return this.id;
	}


	/**
	 * Returns <code>true</code> if the contact has ID
	 * 
	 * @return <code>true</code> if the contact has ID <code>false</code> otherwise
	 */
	public boolean hasId () {
		return (this.id != null);
	}


	/**
	 * Sets the contact identifier.
	 *
	 * @param aId
	 *        Contact identifier
	 */
	public void setId ( String aId ) {
		this.id = aId;
	}


	/**
	 * Gets the contact statuses.
	 *
	 * @return <code>Vector</code> of {@link EPPContactStatus} objects.
	 */
	public Vector getStatuses () {
		return this.statuses;
	}


	/**
	 * Returns <code>true</code> if the contact has statuses
	 * 
	 * @return <code>true</code> if the contact has statues <code>false</code> otherwise
	 */
	public boolean hasStatuses () {
		return (this.statuses != null);
	}


	/**
	 * Sets the contact statuses.
	 *
	 * @param aStatuses
	 *        <code>Vector</code> of {@link EPPContactStatus} objects.
	 */
	public void setStatuses ( Vector aStatuses ) {
		this.statuses = aStatuses;
	}


	/**
	 * Gets the contact voice number.
	 *
	 * @return Contact voice number if defined; <code>null</code> otherwise.
	 */
	public String getVoice () {
		return this.voice;
	}


	/**
	 * Returns <code>true</code> if the contact has voice
	 * 
	 * @return <code>true</code> if the contact has voice <code>false</code> otherwise
	 */
	public boolean hasVoice () {
		return (this.voice != null);
	}


	/**
	 * Sets the contact voice number.
	 *
	 * @param aVoice
	 *        contact voice number
	 */
	public void setVoice ( String aVoice ) {
		this.voice = aVoice;
	}


	/**
	 * Get voice number extension.
	 *
	 * @return Voice number extension if defined; <code>null</code> otherwise.
	 */
	public String getVoiceExt () {
		return this.voiceExt;
	}

	
	/**
	 * Returns <code>true</code> if the contact has voice extension
	 * 
	 * @return <code>true</code> if the contact has voice extension <code>false</code> otherwise
	 */
	public boolean hasVoiceExt () {
		return (this.voiceExt != null);
	}


	/**
	 * Sets the contact voice extension.
	 *
	 * @param aVoiceExt
	 *        voice extension
	 */
	public void setVoiceExt ( String aVoiceExt ) {
		this.voiceExt = aVoiceExt;
	}


	/**
	 * Gets the Registry Object Identifier (ROID).
	 *
	 * @return Registry Object Identifier (ROID) if defined; <code>null</code> otherwise.
	 */
	public java.lang.String getRoid () {
		return this.roid;
	}


	/**
	 * Returns <code>true</code> if the contact has Registry Object Identifier (ROID).
	 * 
	 * @return <code>true</code> if the contact has Registry Object Identifier (ROID) <code>false</code> otherwise
	 */
	public boolean hasRoid () {
		return (this.roid != null);
	}


	/**
	 * Sets the Registry Object Identifier (ROID).
	 *
	 * @param aRoid
	 *        Registry Object Identifier (ROID)
	 */
	public void setRoid ( String aRoid ) {
		this.roid = aRoid;
	}

}
