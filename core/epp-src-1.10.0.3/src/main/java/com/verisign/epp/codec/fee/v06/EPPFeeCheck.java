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

package com.verisign.epp.codec.fee.v06;

import java.util.ArrayList;
import java.util.List;

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
 * Fee Check Extension that enables a client to pass a list of domain names and
 * fee query information to get fee information back from the server.
 */
public class EPPFeeCheck implements EPPCodecComponent {

	/**
	 * XML local name for <code>EPPFeeCheck</code>.
	 */
	public static final String ELM_LOCALNAME = "check";

	/**
	 * XML root tag for <code>EPPFeeCheck</code>.
	 */
	public static final String ELM_NAME = EPPFeeExtFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPFeeCheck.class.getName(),
			EPPCatFactory.getInstance().getFactory());

	private List<EPPFeeDomain> domains = new ArrayList<EPPFeeDomain>();

	/**
	 * Default constructor for <code>EPPFeeCheck</code>.
	 */
	public EPPFeeCheck() {
	}

	/**
	 * Constructor for <code>EPPFeeCheck</code> that takes the list of domains
	 * to check.
	 * 
	 * @param aDomains
	 *            A list of {@link EPPFeeDomain} objects to check.
	 */
	public EPPFeeCheck(List<EPPFeeDomain> aDomains) {
		if (aDomains != null) {
			this.domains = aDomains;
		}
	}

	/**
	 * Adds a domain to the list of domains to check the fee.
	 * 
	 * @param aDomain
	 *            Domain to add.
	 */
	public void addDomain(EPPFeeDomain aDomain) {
		if (aDomain != null) {
			this.domains.add(aDomain);
		}
	}

	/**
	 * Gets the list of domains to check.
	 * 
	 * @return List of {@link EPPFeeDomain} instances. The list should never be
	 *         <code>null</code>.
	 */
	public List<EPPFeeDomain> getDomains() {
		return this.domains;
	}

	/**
	 * Sets the list of domains to check.
	 * 
	 * @param aDomains
	 *            List of {@link EPPFeeDomain} instances to check.
	 */
	public void setDomains(List<EPPFeeDomain> aDomains) {
		if (aDomains == null) {
			this.domains = new ArrayList<EPPFeeDomain>();
		}
		else {
			this.domains = aDomains;
		}
	}

	/**
	 * Encode instance into a DOM element tree. A DOM Document is passed as an
	 * argument and functions as a factory for DOM objects. The root element
	 * associated with the instance is created and each instance attribute is
	 * appended as a child node.
	 * 
	 * @param aDocument
	 *            DOM Document, which acts is an Element factory
	 * 
	 * @return Element Root element associated with the object
	 * 
	 * @exception EPPEncodeException
	 *                Error encoding <code>EPPFeeCheck</code>
	 */
	public Element encode(Document aDocument) throws EPPEncodeException {

		if (aDocument == null) {
			throw new EPPEncodeException("aDocument is null"
					+ " on in EPPFeeCheck.encode(Document)");
		}

		// Create root element
		Element root = aDocument.createElementNS(EPPFeeExtFactory.NS, ELM_NAME);

		// Domains
		EPPUtil.encodeCompList(aDocument, root, this.domains);

		return root;
	}

	/**
	 * Decode a DOM element tree to initialize the instance attributes. The
	 * <code>aElement</code> argument represents the root DOM element and is
	 * used to traverse the DOM nodes for instance attribute values.
	 * 
	 * @param aElement
	 *            <code>Element</code> to decode
	 * 
	 * @exception EPPDecodeException
	 *                Error decoding <code>Element</code>
	 */
	public void decode(Element aElement) throws EPPDecodeException {

		// Domains
		this.domains = EPPUtil.decodeCompList(aElement, EPPFeeExtFactory.NS,
				EPPFeeDomain.ELM_NAME, EPPFeeDomain.class);
		if (this.domains == null) {
			this.domains = new ArrayList<EPPFeeDomain>();
		}
	}

	/**
	 * Clone an <code>EPPCodecComponent</code> instance.
	 * 
	 * @return clone of concrete <code>EPPFeeCheck</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {

		EPPFeeCheck clone = (EPPFeeCheck) super.clone();

		// Domains
		clone.domains = new ArrayList<EPPFeeDomain>();

		for (EPPFeeDomain domain : this.domains) {
			clone.domains.add((EPPFeeDomain) domain.clone());
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

	/**
	 * Compare an instance of <code>EPPFeeCheck</code> with this instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return <code>true</code> if equal; <code>false</code> otherwise.
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPFeeCheck)) {
			cat.error("EPPFeeCheck.equals(): " + aObject.getClass().getName()
					+ " not EPPFeeCheck instance");

			return false;
		}

		EPPFeeCheck other = (EPPFeeCheck) aObject;

		// Domains
		if (!EqualityUtil.equals(this.domains, other.domains)) {
			cat.error("EPPFeeCheck.equals(): domains not equal");
			return false;
		}

		return true;
	}

}
