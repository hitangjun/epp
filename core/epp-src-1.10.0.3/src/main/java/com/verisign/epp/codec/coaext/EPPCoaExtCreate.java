/**************************************************************************
 *                                                                        *
 * The information in this document is proprietary to VeriSign, Inc.      *
 * It may not be used, reproduced or disclosed without the written        *
 * approval of VeriSign.                                                  *
 *                                                                        *
 * VERISIGN PROPRIETARY & CONFIDENTIAL INFORMATION                        *
 *                                                                        *
 *                                                                        *
 * Copyright (c) 2011 VeriSign, Inc.  All rights reserved.                *
 *                                                                        *
 *************************************************************************/

package com.verisign.epp.codec.coaext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.verisign.epp.codec.gen.EPPCodec;
import com.verisign.epp.codec.gen.EPPCodecComponent;
import com.verisign.epp.codec.gen.EPPCodecException;
import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.codec.rgpext.EPPRgpExtFactory;
import com.verisign.epp.util.EPPCatFactory;

/**
 * EPPCodecComponent that encodes and decodes a COA Create Tag.
 * <p>
 * Title: EPP 1.0 Client Object Attribute - Create tag
 * </p>
 * <p>
 * Description: The EPPCoaExtCreate object represents the collection of Client
 * Object Attributes being submitted for creation along with their associated
 * object. As such it is composed of a collection of EPPCoaExtAttr objects. <br/>
 * As XML, it is represented by a <coa:create> element containing a number of
 * <coa:attr> elements.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: VeriSign
 * </p>
 * 
 * @author jfaust
 * @version 1.0
 */
public class EPPCoaExtCreate implements EPPCodecComponent {

	/**
	 * Serial version id - increment this if the structure changes.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Log4j category for logging
	 */
	private static Logger cat =
			Logger.getLogger( EPPCoaExtCreate.class.getName(), EPPCatFactory
					.getInstance()
					.getFactory() );

	/**
	 * Constant for the key tag
	 */
	public static final String ELM_NAME = "coa:create";

	/**
	 * Attributes to be associated with the created object
	 */
	private List attrs = null;


	/**
	 * Default constructor
	 */
	public EPPCoaExtCreate () {
	}


	/**
	 * Constructor with attributes.
	 * 
	 * @param aAttrs
	 */
	public EPPCoaExtCreate ( List aAttrs ) {
		this.attrs = aAttrs;
	}


	/**
	 * Populate the data of this instance with the data stored in the given
	 * Element of the DOM tree
	 * 
	 * @param aElement
	 *        The root element of the report fragment of XML
	 * @throws EPPDecodeException
	 *         Thrown if any errors occur during decoding.
	 */
	public void decode ( Element aElement ) throws EPPDecodeException {

		this.attrs =
				EPPUtil.decodeCompList( aElement, EPPCoaExtFactory.NS,
						EPPCoaExtAttr.ELM_NAME, EPPCoaExtAttr.class );
	}


	/**
	 * Append all data from this COA create to the given DOM Document
	 * 
	 * @param aDocument
	 *        The DOM Document to append data to
	 * @return Encoded DOM <code>Element</code>
	 * @throws EPPEncodeException
	 *         Thrown when errors occur during the encode attempt or if the
	 *         instance is invalid.
	 */
	public Element encode ( Document aDocument ) throws EPPEncodeException {

		if ( aDocument == null ) {
			throw new EPPEncodeException( "aDocument is null"
					+ " in EPPCoaExtCreate.encode(Document)" );
		}

		try {
			// Validate States
			validateState();
		}
		catch ( EPPCodecException e ) {
			cat.error( "EPPCoaExtCreate.encode(): Invalid state on encode: " + e );
			throw new EPPEncodeException( "EPPCoaExtCreate invalid state: " + e );
		}

		// coa:create
		Element root = aDocument.createElementNS( EPPCoaExtFactory.NS, ELM_NAME );

		root.setAttribute( "xmlns:" + EPPCoaExtFactory.NS_PREFIX,
				EPPCoaExtFactory.NS );
        root.setAttributeNS(
                EPPCodec.NS_XSI, "xsi:schemaLocation",
                EPPCoaExtFactory.NS_SCHEMA);


		EPPUtil.encodeCompList( aDocument, root, this.attrs );

		return root;
	}


	/**
	 * Validate the state of the <code>EPPCoaCreate</code> instance. A valid state
	 * means that all of the required attributes have been set. If validateState
	 * returns without an exception, the state is valid. If the state is not
	 * valid, the <code>EPPCodecException</code> will contain a description of the
	 * error. throws EPPCodecException State error. This will contain the name of
	 * the attribute that is not valid.
	 * 
	 * @throws EPPCodecException
	 *         Thrown if the instance is in an invalid state
	 */
	private void validateState () throws EPPCodecException {
		if ( this.attrs == null || this.attrs.size() == 0 ) {
			throw new EPPCodecException( "EPPCoaExtCreate contains no attr elements." );
		}

	}


	/**
	 * A deep clone of the EPPCoaCreate.
	 * 
	 * @see java.lang.Object#clone()
	 */
	public Object clone () throws CloneNotSupportedException {
		EPPCoaExtCreate theClone = new EPPCoaExtCreate();

		if ( this.attrs != null ) {
			for ( Iterator iterator = this.attrs.iterator(); iterator.hasNext(); ) {
				Object attrObject = iterator.next();
				if ( attrObject != null ) {
					EPPCoaExtAttr attr = (EPPCoaExtAttr) attrObject;
					theClone.appendAttr( (EPPCoaExtAttr) attr.clone() );
				}
			}
		}

		return theClone;
	}


	/**
	 * A deep comparison of this with another EPPCoaExtCreate.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals ( Object aObj ) {

		if ( !(aObj instanceof EPPCoaExtCreate) ) {
			return false;
		}

		EPPCoaExtCreate theComp = (EPPCoaExtCreate) aObj;

		if ( !EPPUtil.equalLists( this.attrs, theComp.attrs ) ) {
			return false;
		}

		return true;
	}


	/**
	 * Appends to the <code>List</code> of attributes <code>EPPCoaExtAttr</code>
	 * instances to add.
	 * 
	 * @param aAttr
	 *        <code>EPPCoaExtAttr</code> instance
	 */
	public void appendAttr ( EPPCoaExtAttr aAttr ) {
		if ( aAttr == null ) {
			return;
		}
		if ( this.attrs == null ) {
			this.attrs = new ArrayList();
		}

		this.attrs.add( aAttr );
	}


	/**
	 * Gets the attr list.
	 * 
	 * @return <code>List</code> of attribute <code>EPPCoaExtAttr</code> instances
	 *         if defined; <code>null</code> otherwise.
	 */
	public List getAttrs () {
		return attrs;
	}


	/**
	 * Sets the <code>List</code> of attributes <code>EPPCoaExtAttr</code>
	 * instances to create.
	 * 
	 * @param aAttrs
	 *        <code>List</code> of <code>EPPCoaExtAttr</code> instances
	 */
	public void setAttrs ( List aAttrs ) {
		this.attrs = aAttrs;
	}

}
