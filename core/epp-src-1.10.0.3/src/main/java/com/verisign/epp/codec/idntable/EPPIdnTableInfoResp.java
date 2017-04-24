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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPResponse;
import com.verisign.epp.codec.gen.EPPTransId;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EqualityUtil;

/**
 * Represents an EPP Internationalized Domain Name (IDN) Table &lt;info&gt;
 * response, which support three different forms:<br>
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
 * <code>EPPIdnTableInfoCmd</code> is the concrete <code>EPPCommand</code>
 * associated with <code>EPPIdnTableInfoCmd</code>.
 * 
 * @see com.verisign.epp.codec.idntable.EPPIdnTableInfoResp
 */
public class EPPIdnTableInfoResp extends EPPResponse {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPIdnTableInfoResp.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * XML local name for <code>EPPIdnTableInfoResp</code>.
	 */
	public static final String ELM_LOCALNAME = "infData";

	/**
	 * XML root tag for <code>EPPIdnTableInfoResp</code>.
	 */
	public static final String ELM_NAME = EPPIdnTableMapFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * Check form types, that include:<br>
	 * <ul>
	 * <li><code>DOMAIN_FORM</code> that represents the &quot;Domain Info
	 * Form&quot;.</li>
	 * <li><code>TABLE_FORM</code> that represents the &quot;Table Info
	 * Form&quot;.</li>
	 * <li><code>LIST_FORM</code> that represents the &quot;List Info
	 * Form&quot;.</li>
	 */
	public enum Form {
		DOMAIN_FORM, TABLE_FORM, LIST_FORM
	}

	/**
	 * XML local name for the list element
	 */
	private static final String ELM_LIST = "list";
	
	/**
	 * Form of the info response.
	 */
	private Form form = Form.LIST_FORM;

	/**
	 * Domain Info Form Information
	 */
	private EPPIdnTableInfoDomain domain;

	/**
	 * Table Info Form Information
	 */
	private EPPIdnTableInfoTable table;

	/**
	 * List Info Form Information
	 */
	private List<EPPIdnTableInfoListItem> list = new ArrayList<EPPIdnTableInfoListItem>();

	/**
	 * <code>EPPIdnTableInfoResp</code> default constructor with the default
	 * form of <code>Form.LIST_FORM</code>.
	 */
	public EPPIdnTableInfoResp() {
	}

	/**
	 * <code>EPPIdnTableInfoResp</code> constructor that only takes the
	 * transaction identifier with the default form of
	 * <code>Form.LIST_FORM</code>.
	 * 
	 * @param aTransId
	 *            Transaction Id associated with response.
	 */
	public EPPIdnTableInfoResp(EPPTransId aTransId) {
		super(aTransId);
	}

	/**
	 * <code>EPPIdnTableInfoResp</code> constructor that only takes the
	 * transaction identifier and the domain information for the Domain Info
	 * Form. The form is set to <code>Form.DOMAIN_FORM</code>.
	 * 
	 * @param aTransId
	 *            Transaction Id associated with response.
	 * @param aDomain
	 *            Domain information for Domain Info Form.
	 * 
	 */
	public EPPIdnTableInfoResp(EPPTransId aTransId,
			EPPIdnTableInfoDomain aDomain) {
		super(aTransId);
		this.setDomain(aDomain);
	}

	/**
	 * <code>EPPIdnTableInfoResp</code> constructor that only takes the
	 * transaction identifier and the table information for the Table Info Form.
	 * The form is set to <code>Form.TABLE_FORM</code>.
	 * 
	 * @param aTransId
	 *            Transaction Id associated with response.
	 * @param aTable
	 *            Table information for Table Info Form.
	 * 
	 */
	public EPPIdnTableInfoResp(EPPTransId aTransId, EPPIdnTableInfoTable aTable) {
		super(aTransId);
		this.setTable(aTable);
	}

	/**
	 * <code>EPPIdnTableInfoResp</code> constructor that only takes the
	 * transaction identifier and the list information for the List Info Form.
	 * The form is set to <code>Form.LIST_FORM</code>.
	 * 
	 * @param aTransId
	 *            Transaction Id associated with response.
	 * @param aList
	 *            List information for List Info Form.
	 * 
	 */
	public EPPIdnTableInfoResp(EPPTransId aTransId,
			List<EPPIdnTableInfoListItem> aList) {
		super(aTransId);
		this.setList(aList);
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
	 * Gets the domain information in Domain Info Form.
	 * 
	 * @return Domain information if defined; <code>null</code> otherwise.
	 */
	public EPPIdnTableInfoDomain getDomain() {
		return this.domain;
	}

	/**
	 * Sets the domain information in Domain Info Form. The form will be
	 * automatically set to <code>Form.DOMAIN_FORM</code> for a non-null domain
	 * value.
	 * 
	 * @param aDomain
	 *            the domain to set
	 */
	public void setDomain(EPPIdnTableInfoDomain aDomain) {
		this.domain = aDomain;
		if (this.domain != null) {
			this.setForm(Form.DOMAIN_FORM);
		}
	}

	/**
	 * Gets the table information in Table Info Form.
	 * 
	 * @return The table information if defined: <code>null</code> otherwise.
	 */
	public EPPIdnTableInfoTable getTable() {
		return this.table;
	}

	/**
	 * Sets the table information in Table Info Form. The form will be
	 * automatically set to <code>Form.TABLE_FORM</code> for a non-null table
	 * value.
	 * 
	 * @param aTable
	 *            the table to set
	 */
	public void setTable(EPPIdnTableInfoTable aTable) {
		this.table = aTable;
		if (this.table != null) {
			this.setForm(Form.TABLE_FORM);
		}
	}

	/**
	 * Append a list item to the list in List Info Form. The form will be
	 * automatically set to <code>Form.LIST_FORM</code>.
	 * 
	 * @param aListItem
	 *            List item to add to the list
	 */
	public void addListItem(EPPIdnTableInfoListItem aListItem) {
		if (aListItem != null) {
			this.list.add(aListItem);
			this.setForm(Form.LIST_FORM);
		}
	}

	/**
	 * Gets the list information in List Info Form.
	 * 
	 * @return The list information if defined; <code>null</code> otherwise.
	 */
	public List<EPPIdnTableInfoListItem> getList() {
		return this.list;
	}

	/**
	 * Sets the list information in List Info Form. The form will be
	 * automatically set to <code>Form.LIST_FORM</code> for a non-null table
	 * value.
	 * 
	 * @param aList
	 *            the list to set
	 */
	public void setList(List<EPPIdnTableInfoListItem> aList) {
		this.list = aList;
		if (this.list != null) {
			this.setForm(Form.LIST_FORM);
		}
		else {
			this.list = new ArrayList<EPPIdnTableInfoListItem>();
		}
	}

	/**
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPIdnTableInfoResp</code> instance.
	 * 
	 * @param aDocument
	 *            DOM Document that is being built. Used as an Element factory.
	 * 
	 * @return Element Root DOM Element representing the EPPIdnTableInfoResp
	 *         instance.
	 * 
	 * @exception EPPEncodeException
	 *                Unable to encode EPPIdnTableInfoResp instance.
	 */
	protected Element doEncode(Document aDocument) throws EPPEncodeException {

		// Create root element
		Element root = aDocument.createElementNS(EPPIdnTableMapFactory.NS,
				ELM_NAME);

		// Create element based on form (Domain, Table, List)
		switch (this.form) {
			case DOMAIN_FORM:
				if (this.domain == null) {
					throw new EPPEncodeException(
							"domain is null in EPPIdnTableInfoResp with Domain Info Form");
				}
				EPPUtil.encodeComp(aDocument, root, this.domain);
				break;
			case TABLE_FORM:
				if (this.table == null) {
					throw new EPPEncodeException(
							"table is null in EPPIdnTableInfoResp with Table Info Form");
				}
				EPPUtil.encodeComp(aDocument, root, this.table);
				break;
			case LIST_FORM:
				// Create list element
				Element list = aDocument.createElementNS(EPPIdnTableMapFactory.NS,
						EPPIdnTableMapFactory.NS_PREFIX + ":"
								+ ELM_LIST);
				EPPUtil.encodeCompList(aDocument, list, this.list);
				root.appendChild(list);
				break;
		}

		return root;
	}

	/**
	 * Decode the <code>EPPIdnTableInfoResp</code> attributes from the aElement
	 * DOM Element tree.
	 * 
	 * @param aElement
	 *            Root DOM Element to decode <code>EPPIdnTableInfoResp</code>
	 *            from.
	 * 
	 * @exception EPPDecodeException
	 *                Unable to decode aElement
	 */
	protected void doDecode(Element aElement) throws EPPDecodeException {
		// Clear attributes
		this.domain = null;
		this.table = null;
		this.list = new ArrayList<EPPIdnTableInfoListItem>();

		// List
		Element theListElm = EPPUtil.getElementByTagNameNS(aElement, EPPIdnTableMapFactory.NS, ELM_LIST);

		if (theListElm != null) {
			this.list = EPPUtil
					.decodeCompList(theListElm, EPPIdnTableMapFactory.NS,
							EPPIdnTableInfoListItem.ELM_NAME,
							EPPIdnTableInfoListItem.class);
			this.setForm(Form.LIST_FORM);	
			return;
		}		
		
		// Domain
		this.domain = (EPPIdnTableInfoDomain) EPPUtil.decodeComp(aElement,
				EPPIdnTableMapFactory.NS, EPPIdnTableInfoDomain.ELM_NAME,
				EPPIdnTableInfoDomain.class);
		if (this.domain != null) {
			this.setForm(Form.DOMAIN_FORM);
			return;
		}

		// Table
		this.table = (EPPIdnTableInfoTable) EPPUtil.decodeComp(aElement,
				EPPIdnTableMapFactory.NS, EPPIdnTableInfoTable.ELM_NAME,
				EPPIdnTableInfoTable.class);
		if (this.table != null) {
			this.setForm(Form.TABLE_FORM);
			return;
		}

	}

	/**
	 * Clone <code>EPPIdnTableInfoResp</code>.
	 * 
	 * @return clone of <code>EPPIdnTableInfoResp</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPIdnTableInfoResp clone = (EPPIdnTableInfoResp) super.clone();

		// Domain
		if (this.domain != null) {
			clone.domain = (EPPIdnTableInfoDomain) this.domain.clone();
		}
		else {
			clone.domain = null;
		}

		// Table
		if (this.table != null) {
			clone.table = (EPPIdnTableInfoTable) this.table.clone();
		}
		else {
			clone.table = null;
		}

		// List
		clone.list = (List) ((ArrayList) this.list).clone();

		return clone;
	}

	/**
	 * Gets the EPP response type associated with
	 * <code>EPPIdnTableInfoResp</code>.
	 * 
	 * @return <code>EPPIdnTableInfoResp.ELM_NAME</code>
	 */
	public String getType() {
		return ELM_NAME;
	}

	/**
	 * Gets the EPP command namespace associated with
	 * <code>EPPIdnTableInfoResp</code>.
	 * 
	 * @return <code>EPPIdnTableMapFactory.NS</code>
	 */
	public String getNamespace() {
		return EPPIdnTableMapFactory.NS;
	}

	/**
	 * Compare an instance of <code>EPPIdnTableInfoResp</code> with this
	 * instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return {@code true} if this object is the same as the aObject argument;
	 *         {@code false} otherwise
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPIdnTableInfoResp)) {
			return false;
		}

		EPPIdnTableInfoResp other = (EPPIdnTableInfoResp) aObject;

		// Domain
		if (!EqualityUtil.equals(this.domain, other.domain)) {
			cat.error("EPPIdnTableInfoResp.equals(): domain not equal");
			return false;
		}

		// Table
		if (!EqualityUtil.equals(this.table, other.table)) {
			cat.error("EPPIdnTableInfoResp.equals(): table not equal");
			return false;
		}

		// List
		if (!EPPUtil.equalLists(this.list, other.list)) {
			cat.error("EPPIdnTableInfoResp.equals(): list not equal");
			return false;
		}

		return true;
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