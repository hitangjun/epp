/***********************************************************
Copyright (C) 2015 VeriSign, Inc.

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
package com.verisign.epp.codec.idntable;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPInfoCmd;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EqualityUtil;

/**
 * Represents an EPP Internationalized Domain Name (IDN) Table &lt;info&gt;
 * command, which support three different forms:<br>
 * <ul>
 * <li>Domain Info Form - Used to validate the domain name code points against
 * the IDN Tables and IDN Policies, and to return the matching IDN Table
 * meta-data.</li>
 * <li>Table Info Form - Used to retrieve information associated with an IDN
 * Table object.</li>
 * <li>List Info Form - Used to retrieve the list of IDN Tables supported by the
 * server.</li>
 * </ul>
 * 
 * <br>
 * <code>EPPIdnTableInfoResp</code> is the concrete <code>EPPReponse</code>
 * associated with <code>EPPIdnTableInfoCmd</code>.
 * 
 * @see com.verisign.epp.codec.idntable.EPPIdnTableInfoResp
 */
public class EPPIdnTableInfoCmd extends EPPInfoCmd {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPIdnTableInfoCmd.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * XML local name for <code>EPPIdnTableInfoCmd</code>.
	 */
	public static final String ELM_LOCALNAME = "info";

	/**
	 * XML root tag for <code>EPPIdnTableInfoCmd</code>.
	 */
	public static final String ELM_NAME = EPPIdnTableMapFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * Info form types, that include:<br>
	 * <ul>
	 * <li><code>UNDEFINED_FORM</code> that represents an undefined state, where
	 * none of the forms can be determined.</li>
	 * <li><code>DOMAIN_FORM</code> that represents the &quot;Domain Info
	 * Form&quot;.</li>
	 * <li><code>TABLE_FORM</code> that represents the &quot;Table Info
	 * Form&quot;.</li>
	 * <li><code>LIST_FORM</code> that represents the &quot;List Info
	 * Form&quot;.</li>
	 */
	public enum Form {
		UNDEFINED_FORM, DOMAIN_FORM, TABLE_FORM, LIST_FORM
	}

	/** XML Element Name for the <code>domain</code> element. */
	private final static String ELM_DOMAIN = "domain";

	/** XML Element Name for the <code>table</code> element. */
	private final static String ELM_TABLE = "table";

	/** XML Element Name for the <code>list</code> element. */
	private final static String ELM_LIST = "list";

	/**
	 * Domain name
	 */
	private String domain;

	/**
	 * Table identifier
	 */
	private String table;

	/**
	 * Form of the info command.
	 */
	private Form form = Form.UNDEFINED_FORM;

	/**
	 * <code>EPPIdnTableInfoCmd</code> default constructor.
	 */
	public EPPIdnTableInfoCmd() {
	}

	/**
	 * <code>EPPIdnTableInfoCmd</code> constructor that only takes the client
	 * transaction identifier
	 * 
	 * @param aTransId
	 *            Transaction Id associated with command.
	 */
	public EPPIdnTableInfoCmd(String aTransId) {
		super(aTransId);
	}

	/**
	 * <code>EPPIdnTableInfoCmd</code> constructor for setting an individual
	 * table identifier to check in Table Check Form.
	 * 
	 * @param aTransId
	 *            Transaction Id associated with command.
	 * @param aForm
	 *            What is the form of the info command?
	 * @param aValue
	 *            Value of the string passed with the form. Pass domain name for
	 *            the <code>Form.DOMAIN_FORM</code>, table identifier for the
	 *            <code>Form.TABLE_FORM</code>, and <code>null</code> for the
	 *            <code>Form.LIST_FORM</code>.
	 */
	public EPPIdnTableInfoCmd(String aTransId, Form aForm, String aValue) {
		super(aTransId);
		this.setForm(aForm);
		switch (aForm) {
			case DOMAIN_FORM:
				this.setDomain(aValue);
				break;
			case TABLE_FORM:
				this.setTable(aValue);
				break;
			case LIST_FORM:
				// Do nothing with aValue
		}
	}

	/**
	 * Gets the EPP command Namespace associated with
	 * <code>EPPIdnTableInfoCmd</code>.
	 * 
	 * @return <code>EPPIdnTableMapFactory.NS</code>
	 */
	public String getNamespace() {
		return EPPIdnTableMapFactory.NS;
	}

	/**
	 * Is the domain defined?
	 * 
	 * @return <code>true</code> if the domain is defined; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasDomain() {
		return (this.domain != null ? true : false);
	}

	/**
	 * Gets the domain name for Domain Info Form.
	 * 
	 * @return The domain name if defined; <code>null</code> otherwise.
	 */
	public String getDomain() {
		return this.domain;
	}

	/**
	 * Sets the domain name for Domain Info Form. The form will be automatically
	 * set to <code>Form.DOMAIN_FORM</code> for a non-null domain value.
	 * 
	 * @param aDomain
	 *            Domain name in Domain Info Form.
	 */
	public void setDomain(String aDomain) {
		this.domain = aDomain;
		if (this.domain != null) {
			this.setForm(Form.DOMAIN_FORM);
		}
	}

	/**
	 * Is the table defined?
	 * 
	 * @return <code>true</code> if the table is defined; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasTable() {
		return (this.table != null ? true : false);
	}

	/**
	 * Gets the table identifier for Table Info Form.
	 * 
	 * @return The table identifier if defined; <code>null</code> otherwise.
	 */
	public String getTable() {
		return this.table;
	}

	/**
	 * Sets the table identifier for Table Info Form. The form will be
	 * automatically set to <code>Form.TABLE_FORM</code> for a non-null table
	 * identifier value.
	 * 
	 * @param aTable
	 *            Table identifier
	 */
	public void setTable(String aTable) {
		this.table = aTable;
		if (this.table != null) {
			this.setForm(Form.TABLE_FORM);
		}
	}

	/**
	 * What inform is being used?
	 * 
	 * @return One of the <code>Form</code> enumerated values, where
	 *         <code>UNDEFINED_FORM</code> is used when the form has not been
	 *         set.
	 */
	public Form getForm() {
		return this.form;
	}

	/**
	 * Set the form of the info command using one of the <code>Form</code>
	 * enumerated values.
	 * 
	 * @param aForm
	 *            One of the <code>Form</code> enumerated values.
	 */
	public void setForm(Form aForm) {
		this.form = aForm;
	}

	/**
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPIdnTableInfoCmd</code> instance.
	 * 
	 * @param aDocument
	 *            DOM Document that is being built. Used as an Element factory.
	 * 
	 * @return Element Root DOM Element representing the
	 *         <code>EPPIdnTableInfoCmd</code> instance.
	 * 
	 * @exception EPPEncodeException
	 *                Unable to encode <code>EPPIdnTableInfoCmd</code> instance.
	 */
	protected Element doEncode(Document aDocument) throws EPPEncodeException {

		// Check required attributes
		if (this.getForm() == Form.UNDEFINED_FORM) {
			throw new EPPEncodeException("Undefined form in EPPIdnTableInfoCmd");
		}

		// Create root element
		Element root = aDocument.createElementNS(EPPIdnTableMapFactory.NS,
				ELM_NAME);

		// Create element based on form (Domain, Table, List)
		switch (this.form) {
			case DOMAIN_FORM:
				if (this.domain == null) {
					throw new EPPEncodeException(
							"domain is null in EPPIdnTableInfoCmd with Domain Info Form");
				}
				EPPUtil.encodeString(aDocument, root, this.domain,
						EPPIdnTableMapFactory.NS,
						EPPIdnTableMapFactory.NS_PREFIX + ":" + ELM_DOMAIN);
				break;
			case TABLE_FORM:
				if (this.table == null) {
					throw new EPPEncodeException(
							"table is null in EPPIdnTableInfoCmd with Table Info Form");
				}
				EPPUtil.encodeString(aDocument, root, this.table,
						EPPIdnTableMapFactory.NS,
						EPPIdnTableMapFactory.NS_PREFIX + ":" + ELM_TABLE);
				break;
			case LIST_FORM:
				Element listElm = aDocument.createElementNS(
						EPPIdnTableMapFactory.NS,
						EPPIdnTableMapFactory.NS_PREFIX + ":" + ELM_LIST);
				root.appendChild(listElm);
				break;
		}

		return root;
	}

	/**
	 * Decode the <code>EPPIdnTableInfoCmd</code> attributes from the aElement
	 * DOM Element tree.
	 * 
	 * @param aElement
	 *            Root DOM Element to decode <code>EPPIdnTableInfoCmd</code>
	 *            from.
	 * 
	 * @exception EPPDecodeException
	 *                Unable to decode aElement
	 */
	protected void doDecode(Element aElement) throws EPPDecodeException {

		// Clear attributes
		this.domain = null;
		this.table = null;
		this.setForm(Form.UNDEFINED_FORM);

		// Domain
		this.domain = EPPUtil.decodeString(aElement, EPPIdnTableMapFactory.NS,
				ELM_DOMAIN);
		if (this.domain != null) {
			this.setForm(Form.DOMAIN_FORM);
			return;
		}

		// Table
		this.table = EPPUtil.decodeString(aElement, EPPIdnTableMapFactory.NS,
				ELM_TABLE);
		if (this.table != null) {
			this.setForm(Form.TABLE_FORM);
			return;
		}

		// List
		if (EPPUtil.getElementByTagNameNS(aElement, EPPIdnTableMapFactory.NS,
				ELM_LIST) != null) {
			this.setForm(Form.LIST_FORM);
		}
	}

	/**
	 * Compare an instance of <code>EPPIdnTableInfoCmd</code> with this
	 * instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return {@code true} if this object is the same as the aObject argument;
	 *         {@code false} otherwise.
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPIdnTableInfoCmd)) {
			return false;
		}

		if (!super.equals(aObject)) {
			return false;
		}

		EPPIdnTableInfoCmd other = (EPPIdnTableInfoCmd) aObject;

		// Domain
		if (!EqualityUtil.equals(this.domain, other.domain)) {
			cat.error("EPPIdnTableInfoCmd.equals(): domain not equal");
			return false;
		}

		// Table
		if (!EqualityUtil.equals(this.table, other.table)) {
			cat.error("EPPIdnTableInfoCmd.equals(): table not equal");
			return false;
		}

		return true;
	}

	/**
	 * Clone <code>EPPIdnTableInfoCmd</code>.
	 * 
	 * @return Deep copy clone of <code>EPPIdnTableInfoCmd</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPIdnTableInfoCmd clone = (EPPIdnTableInfoCmd) super.clone();

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
