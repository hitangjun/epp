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

package com.verisign.epp.codec.idntable;

import java.util.Date;

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
 * <code>EPPIdnTableInfoListItem</code> is used to represent an IDN Table in a
 * List Info Form response. The IDN Table includes IDN Table identifier and the
 * update date.
 */
public class EPPIdnTableInfoListItem implements EPPCodecComponent {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPIdnTableInfoListItem.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * XML local name for <code>EPPIdnTableInfoListItem</code>.
	 */
	public static final String ELM_LOCALNAME = "table";

	/**
	 * XML root tag for <code>EPPIdnTableInfoListItem</code>.
	 */
	public static final String ELM_NAME = EPPIdnTableMapFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * XML local name for the name element
	 */
	private static final String ELM_TABLE_NAME = "name";

	/**
	 * XML local name for the upDate element
	 */
	private static final String ELM_UPDATE_DATE = "upDate";

	/**
	 * Server defined IDN Table Identifier
	 */
	private String name;

	/**
	 * Contains the date and time that the IDN Table was created or last
	 * updated.
	 */
	private Date upDate;

	/**
	 * Default constructor for <code>EPPIdnTableInfoListItem</code>.
	 */
	public EPPIdnTableInfoListItem() {
	}

	/**
	 * Constructor for <code>EPPIdnTableInfoListItem</code> that takes the
	 * required attributed of the table identifier (name) and the update date.
	 * 
	 * @param aName
	 *            Table identifier
	 * @param aUpdateDate
	 *            Date and time the IDN Table was created or last updated.
	 */
	public EPPIdnTableInfoListItem(String aName, Date aUpdateDate) {
		this.setName(aName);
		this.setUpdateDate(aUpdateDate);
	}

	/**
	 * Gets the IDN Table identifier.
	 * 
	 * @return The IDN Table identifier
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the IDN Table identifier.
	 * 
	 * @param aName
	 *            The IDN Table identifier.
	 */
	public void setName(String aName) {
		this.name = aName;
	}

	/**
	 * Gets the date and time that the IDN Table was created or last updated.
	 * 
	 * @return Date and time the IDN Table was created or last updated.
	 */
	public Date getUpdateDate() {
		return this.upDate;
	}

	/**
	 * Sets the date and time that the IDN Table was created or last updated.
	 * 
	 * @param aUpdateDate
	 *            Date and time the IDN Table was created or last updated.
	 */
	public void setUpdateDate(Date aUpdateDate) {
		this.upDate = aUpdateDate;
	}

	/**
	 * encode instance into a DOM element tree. A DOM Document is passed as an
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
	 *                Error encoding <code>EPPIdnTableInfoListItem</code>
	 */
	public Element encode(Document aDocument) throws EPPEncodeException {

		// Check required attributes
		if (this.name == null) {
			throw new EPPEncodeException(
					"Undefined name in EPPIdnTableInfoListItem");
		}
		if (this.upDate == null) {
			throw new EPPEncodeException(
					"Undefined upDate in EPPIdnTableInfoListItem");
		}

		// Table
		Element root = aDocument.createElementNS(EPPIdnTableMapFactory.NS,
				ELM_NAME);

		// Name
		EPPUtil.encodeString(aDocument, root, this.name,
				EPPIdnTableMapFactory.NS, EPPIdnTableMapFactory.NS_PREFIX + ":"
						+ ELM_TABLE_NAME);

		// Update Date
		EPPUtil.encodeTimeInstant(aDocument, root, this.upDate,
				EPPIdnTableMapFactory.NS, EPPIdnTableMapFactory.NS_PREFIX + ":"
						+ ELM_UPDATE_DATE);

		return root;
	}

	/**
	 * decode a DOM element tree to initialize the instance attributes. The
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

		// Name
		this.name = EPPUtil.decodeString(aElement, EPPIdnTableMapFactory.NS,
				ELM_TABLE_NAME);

		// Update Date
		this.upDate = EPPUtil.decodeTimeInstant(aElement,
				EPPIdnTableMapFactory.NS, ELM_UPDATE_DATE);
	}

	/**
	 * clone an <code>EPPCodecComponent</code>.
	 * 
	 * @return clone of concrete <code>EPPIdnTableInfoListItem</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPIdnTableInfoListItem clone = (EPPIdnTableInfoListItem) super.clone();

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
	 * Compare an instance of <code>EPPIdnTableInfoListItem</code> with this
	 * instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return <code>true</code> if equal; <code>false</code> otherwise.
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPIdnTableInfoListItem)) {
			return false;
		}

		EPPIdnTableInfoListItem other = (EPPIdnTableInfoListItem) aObject;

		// Name
		if (!EqualityUtil.equals(this.name, other.name)) {
			cat.error("EPPIdnTableInfoListItem.equals(): name not equal");
			return false;
		}

		// Update Date
		if (!EqualityUtil.equals(this.upDate, other.upDate)) {
			cat.error("EPPIdnTableInfoListItem.equals(): upDate not equal");
			return false;
		}

		return true;
	}

}
