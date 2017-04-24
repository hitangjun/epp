/***********************************************************
Copyright (C) 2013 VeriSign, Inc.

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

package com.verisign.epp.codec.relateddomainext;

import java.util.ArrayList;
import java.util.Iterator;
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

/**
 * EPPCodecComponent that encodes and decodes a <relDom:delData> tag associated
 * with a Domain-Delete response.
 * <p>
 * Title: EPP 1.0 Related Domain - delData tag
 * </p>
 * <p>
 * Description: The EPPRelatedDomainExtDeleteResp object represents the
 * collection of domains that have been deleted. As such it is composed of a
 * collection of {@link EPPRelatedDomainExtDomainData} objects. <br/>
 * As XML, it is represented by a <relDom:delData> element containing a number
 * of <relDom:domain> elements.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * <p>
 * Company: VeriSign
 * </p>
 * 
 * @author nchigurupati
 * @version 1.0
 */
public class EPPRelatedDomainExtDeleteResp implements EPPCodecComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3042381748258235317L;

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(
			EPPRelatedDomainExtDeleteResp.class.getName(), EPPCatFactory
					.getInstance()
					.getFactory() );

	/**
	 * Element tag name for delData
	 */
	public static final String ELM_NAME = EPPRelatedDomainExtFactory.NS_PREFIX
			+ ":delData";

	/**
	 * List of domain names represented by the
	 * {@link EPPRelatedDomainExtDomainData} to be associated with the
	 * &lt;relDom:delData&gt; element
	 */
	private List<EPPRelatedDomainExtDomainData> domains = null;


	/**
	 * Default constructor
	 */
	public EPPRelatedDomainExtDeleteResp () {
	}


	/**
	 * Constructor with a list of deleted related domains.
	 * 
	 * @param aDomains List of deleted related domains
	 */
	public EPPRelatedDomainExtDeleteResp ( final List<EPPRelatedDomainExtDomainData> aDomains ) {
		this.domains = aDomains;

	}


	/**
	 * A deep clone of the EPPRelatedDomainExtDeleteResp
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone () throws CloneNotSupportedException {
		final EPPRelatedDomainExtDeleteResp theClone =
				new EPPRelatedDomainExtDeleteResp();

		if (this.domains != null) {
			theClone.domains = new ArrayList<EPPRelatedDomainExtDomainData>();

			for (EPPRelatedDomainExtDomainData domain : this.domains) {

				if (domain != null) {
					theClone.domains.add((EPPRelatedDomainExtDomainData) domain
							.clone());
				}
				else {
					theClone.domains.add(null);
				}

			}

		}
		else {
			theClone.domains = null;
		}

		return theClone;
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
	public void decode ( final Element aElement ) throws EPPDecodeException {

		this.domains =
				EPPUtil.decodeCompList( aElement, EPPRelatedDomainExtFactory.NS,
						EPPRelatedDomainExtDomainData.ELM_NAME,
						EPPRelatedDomainExtDomainData.class );
		try {
			validateState();
		}
		catch ( final EPPCodecException e ) {
			throw new EPPDecodeException(
					"Invalid state on EPPRelatedDomainExtDeleteResp.decode: " + e );
		}
	}


	/**
	 * Append all data from the list deleted related domain names represented by
	 * {@link EPPRelatedDomainExtDomainData} to given DOM Document
	 * 
	 * @param aDocument
	 *        The DOM Document to append data to
	 * @return Encoded DOM <code>Element</code>
	 * @throws EPPEncodeException
	 *         Thrown when errors occur during the encode attempt or if the
	 *         instance is invalid.
	 */
	public Element encode ( final Document aDocument ) throws EPPEncodeException {

		if ( aDocument == null ) {
			throw new EPPEncodeException( "aDocument is null"
					+ " in EPPRelatedDomainExtDeleteResp.encode(Document)" );
		}

		try {
			// Validate States
			validateState();
		}
		catch ( final EPPCodecException e ) {
			cat
					.error( "EPPRelatedDomainExtDeleteResp.encode(): Invalid state on encode: "
							+ e );
			throw new EPPEncodeException(
					"EPPRelatedDomainExtDeleteResp invalid state: " + e );
		}

		final Element root =
				aDocument.createElementNS( EPPRelatedDomainExtFactory.NS, ELM_NAME );
		EPPUtil.encodeCompList( aDocument, root, this.domains );
		return root;
	}


	/**
	 * A deep comparison of this with another EPPRelatedDomainExtDeleteResp.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals ( final Object aObj ) {

		if ( !(aObj instanceof EPPRelatedDomainExtDeleteResp) ) {
			return false;
		}

		final EPPRelatedDomainExtDeleteResp theComp =
				(EPPRelatedDomainExtDeleteResp) aObj;

		if ( !EPPUtil.equalLists( this.domains, theComp.domains ) ) {
			cat
					.error( "EPPRelatedDomainExtDeleteResp.equals(): related domains not equal" );
			return false;
		}

		return true;
	}

	/**
	 * Adds a related domain represented by
	 * {@link EPPRelatedDomainExtDomainData} to the list of related domains.
	 * 
	 * @param aDomain Related domain to add to the list.
	 */
	public void addDomain(EPPRelatedDomainExtDomainData aDomain) {
		if (this.domains == null) {
			this.domains = new ArrayList<EPPRelatedDomainExtDomainData>();
		}
		
		this.domains.add(aDomain);
	}

	
	/**
	 * Are there any domains included in the list of domains?
	 * 
	 * @return <code>true</code> if the domain list is not <code>null</code> and
	 *         there is at least one domain in the list; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasDomains() {
		if (this.domains != null && this.domains.size() > 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Returns the list of available related domain names represented by
	 * {@link EPPRelatedDomainExtDomainData}
	 * 
	 * @return the related domain names
	 */
	public List<EPPRelatedDomainExtDomainData> getDomains () {
		return this.domains;
	}


	/**
	 * Sets the list of available related domain names represented by
	 * {@link EPPRelatedDomainExtDomainData}
	 * 
	 * @param aDomains
	 *        the related domains to set
	 */
	public void setDomains ( final List<EPPRelatedDomainExtDomainData> aDomains ) {
		this.domains = aDomains;
	}


	/**
	 * Implementation of <code>Object.toString</code>, which will result in an
	 * indented XML <code>String</code> representation of the concrete
	 * <code>EPPCodecComponent</code>.
	 * 
	 * @return Indented XML <code>String</code> if successful;
	 *         <code>ERROR</code> otherwise.
	 */
	@Override
	public String toString() {
		return EPPUtil.toString(this);
	}


	/**
	 * Validate the state of the <code>EPPRelatedDomainExtDeleteResp</code>
	 * instance. A valid state means that all of the required attributes have been
	 * set. If validateState returns without an exception, the state is valid. If
	 * the state is not valid, the <code>EPPCodecException</code> will contain a
	 * description of the error. throws EPPCodecException State error. This will
	 * contain the name of the attribute that is not valid.
	 * 
	 * @throws EPPCodecException
	 *         Thrown if the instance is in an invalid state
	 */
	private void validateState () throws EPPCodecException {
		if ( (this.domains == null) || (this.domains.size() == 0) ) {
			throw new EPPCodecException(
					"EPPRelatedDomainExtCreate contains no  elements." );
		}

		for (EPPRelatedDomainExtDomainData domain : this.domains) {
			if (domain == null) {
				throw new EPPCodecException(
						"EPPRelatedDomainExtDeleteResp: contains null EPPRelatedDomainExtDomainData  element.");
			}
			domain.validateStateForDelete();
		}
		
	}

}
