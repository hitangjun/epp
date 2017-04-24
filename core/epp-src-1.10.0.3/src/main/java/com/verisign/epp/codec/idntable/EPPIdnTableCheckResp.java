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

/**
 * Represents an Internationalized Domain Name (IDN) Table response 
 * to a &lt;check&gt; command, with two different forms including the 
 * Domain Check Form and the Table Check Form.  The response in 
 * Domain Check Form returns the validity of the domain name and the 
 * matching IDN table identifiers.  The response in Table Check Form 
 * returns whether or not the table identifier exists.  
 * 
 * @see com.verisign.epp.codec.idntable.EPPIdnTableCheckCmd
 * @see com.verisign.epp.codec.idntable.EPPIdnTableCheckDomain
 * @see com.verisign.epp.codec.idntable.EPPIdnTableCheckTable
 */
public class EPPIdnTableCheckResp extends EPPResponse {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPIdnTableCheckResp.class
			.getName(), EPPCatFactory.getInstance().getFactory());
	
	/**
	 * XML local name for <code>EPPIdnTableCheckResp</code>.
	 */
	public static final String ELM_LOCALNAME = "chkData";

	/**
	 * XML root tag for <code>EPPIdnTableCheckCmd</code>.
	 */
	public static final String ELM_NAME = EPPIdnTableMapFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;
	
	/**
	 * Check form types, that include:<br>
	 * <ul>
	 * <li><code>UNDEFINED_FORM</code> that represents an undefined state, where
	 * neither or both tables and domains are defined.</li>
	 * <li><code>DOMAIN_FORM</code> that represents the &quot;Domain Check
	 * Form&quot;, where domain names are only defined.</li>
	 * <li><code>TABLE_FORM</code> that represents the &quot;Table Check
	 * Form&quot;, where table identifiers are only defined.</li>
	 */
	public enum Form {
		UNDEFINED_FORM, DOMAIN_FORM, TABLE_FORM
	}
	
	/**
	 * Table Check Form table information 
	 */
	private List<EPPIdnTableCheckTable> tables = new ArrayList<EPPIdnTableCheckTable>();
	

	/**
	 * Domain Check Form domain information 
	 */
	private List<EPPIdnTableCheckDomain> domains = new ArrayList<EPPIdnTableCheckDomain>();
		
	/**
	 * <code>EPPIdnTableCheckResp</code> default constructor. It will set
	 * results attribute to an empty <code>List</code>.
	 */
	public EPPIdnTableCheckResp() {
	}

	/**
	 * <code>EPPIdnTableCheckResp</code> constructor that only takes the 
	 * transaction identifier.
	 * 
	 * @param aTransId
	 *            transaction Id associated with response
	 */
	public EPPIdnTableCheckResp(EPPTransId aTransId) {
		super(aTransId);
	}
	
	
	/**
	 * <code>EPPIdnTableCheckResp</code> constructor that takes an individual 
	 * table.  Add more tables using {@link #addTable(EPPIdnTableCheckTable)}.
	 * 
	 * @param aTransId
	 *            transaction Id associated with response
	 * @param aTable An individual table
	 */
	public EPPIdnTableCheckResp(EPPTransId aTransId,
			EPPIdnTableCheckTable aTable) {
		super(aTransId);
		this.addTable(aTable);
	}

	/**
	 * <code>EPPIdnTableCheckResp</code> constructor that takes an individual 
	 * domain.  Add more domains using {@link #addDomain(EPPIdnTableCheckDomain)}.
	 * 
	 * @param aTransId
	 *            transaction Id associated with response
	 * @param aDomain An individual domain
	 */
	public EPPIdnTableCheckResp(EPPTransId aTransId, EPPIdnTableCheckDomain aDomain) {
		super(aTransId);
		this.addDomain(aDomain);
	}

	
	/**
	 * What check form is being used, which are defined by the <code>Form</code>
	 * enumeration.
	 * 
	 * @return One of the <code>Form</code> values.
	 */
	public Form getForm() {
		if ((this.hasDomains() && this.hasTables())
				|| (!this.hasDomains() && !this.hasTables())) {
			return Form.UNDEFINED_FORM;
		}
		else if (this.hasDomains()) {
			return Form.DOMAIN_FORM;
		}
		else {
			return Form.TABLE_FORM;
		}
	}
	
	/**
	 * Are any table  defined in the list of tables?
	 * 
	 * @return <code>true</code> if there is at least one table 
	 *         defined; <code>false</code> otherwise.
	 */
	public boolean hasTables() {
		if (this.tables != null && !this.tables.isEmpty()) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Get the list of tables.
	 * 
	 * @return {@code List} of tables
	 */
	public List<EPPIdnTableCheckTable> getTables() {
		return this.tables;
	}

	/**
	 * Set the list of tables.
	 * 
	 * @param aTables
	 *            {@code List} of tables
	 */
	public void setTables(List<EPPIdnTableCheckTable> aTables) {
		if (aTables == null) {
			this.tables = new ArrayList<EPPIdnTableCheckTable>();
		}
		this.tables = aTables;
	}

	/**
	 * Set an individual table. This method clears the
	 * existing list of tables.
	 * 
	 * @param aTable
	 *            Table check result
	 */
	public void setTable(EPPIdnTableCheckTable aTable) {
		this.tables = new ArrayList<EPPIdnTableCheckTable>();
		this.tables.add(aTable);
	}

	/**
	 * Append a table  to the list of tables. This
	 * method does NOT clear the existing list of tables.
	 * 
	 * @param aTable
	 *            Table check result
	 */
	public void addTable(EPPIdnTableCheckTable aTable) {
		this.tables.add(aTable);
	}

	/**
	 * Are any domains defined in the list of domains?
	 * 
	 * @return <code>true</code> if there is at least one domain defined;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasDomains() {
		if (this.domains != null && !this.domains.isEmpty()) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Get the list of domains
	 * 
	 * @return {@code List} of domains
	 */
	public List<EPPIdnTableCheckDomain> getDomains() {
		return this.domains;
	}

	/**
	 * Set the list of domains
	 * 
	 * @param aDomains
	 *            {@code List} of domains
	 */
	public void setDomains(List<EPPIdnTableCheckDomain> aDomains) {
		if (aDomains == null) {
			this.domains = new ArrayList<EPPIdnTableCheckDomain>();
		}
		this.domains = aDomains;
	}

	/**
	 * Set an individual domain. This method clears the existing
	 * list of domains.
	 * 
	 * @param aDomain
	 *            Domain check result
	 */
	public void setDomain(EPPIdnTableCheckDomain aDomain) {
		this.domains = new ArrayList<EPPIdnTableCheckDomain>();
		this.domains.add(aDomain);
	}

	/**
	 * Append a domain to the list of domains. This method
	 * does NOT clear the existing list of domains.
	 * 
	 * @param aDomain
	 *            Domain check result
	 */
	public void addDomain(EPPIdnTableCheckDomain aDomain) {
		this.domains.add(aDomain);
	}
	
	
	
	/**
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPIdnTableCheckResp</code> instance.
	 * 
	 * @param aDocument
	 *            DOM Document that is being built. Used as an Element factory.
	 * 
	 * @return Element Root DOM Element representing the
	 *         <code>EPPIdnTableCheckResp</code> instance.
	 * 
	 * @exception EPPEncodeException
	 *                Unable to encode <code>EPPIdnTableCheckResp</code>
	 *                instance.
	 */
	protected Element doEncode(Document aDocument) throws EPPEncodeException {
		
		// Check required attributes
		if (this.getForm() == Form.UNDEFINED_FORM) {
			throw new EPPEncodeException(
					"Undefined form in EPPIdnTableCheckResp");
		}

		// Create root element
		Element root = aDocument.createElementNS(EPPIdnTableMapFactory.NS,
				ELM_NAME);

		// Tables
		EPPUtil.encodeCompList(aDocument, root, this.tables);

		// Domains
		EPPUtil.encodeCompList(aDocument, root, this.domains);

		return root;
	}

	/**
	 * Decode the <code>EPPIdnTableCheckResp</code> attributes from the aElement
	 * DOM Element tree.
	 * 
	 * @param aElement
	 *            Root DOM Element to decode <code>EPPIdnTableCheckResp</code>
	 *            from.
	 * 
	 * @exception EPPDecodeException
	 *                Unable to decode aElement
	 */
	protected void doDecode(Element aElement) throws EPPDecodeException {
		// Tables
		this.tables = EPPUtil.decodeCompList(aElement,
				EPPIdnTableMapFactory.NS, EPPIdnTableCheckTable.ELM_NAME,
				EPPIdnTableCheckTable.class);
		
		// Domains
		this.domains = EPPUtil.decodeCompList(aElement,
				EPPIdnTableMapFactory.NS, EPPIdnTableCheckDomain.ELM_NAME,
				EPPIdnTableCheckDomain.class);
	}

	/**
	 * Get the EPP response type associated with
	 * <code>EPPIdnTableCheckResp</code>.
	 * 
	 * @return {@code EPPIdnTableCheckResp.ELM_NAME}
	 */
	public String getType() {
		return ELM_NAME;
	}

	/**
	 * Get the EPP command Namespace associated with
	 * <code>EPPIdnTableCheckResp</code>.
	 * 
	 * @return <code>EPPRegistryMapFactory.NS</code>
	 */
	public String getNamespace() {
		return EPPIdnTableMapFactory.NS;
	}

	/**
	 * Compare an instance of <code>EPPIdnTableCheckResp</code> with this
	 * instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return {@code true} if this object is the same as the aObject argument;
	 *         {@code false} otherwise
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPIdnTableCheckResp)) {
			return false;
		}

		if (!super.equals(aObject)) {
			return false;
		}

		EPPIdnTableCheckResp theMap = (EPPIdnTableCheckResp) aObject;

		// Tables
		if (!EPPUtil.equalLists(tables, theMap.tables)) {
			cat.error("EPPIdnTableCheckResp.equals(): tables not equal");
			return false;
		}

		// Domains
		if (!EPPUtil.equalLists(domains, theMap.domains)) {
			cat.error("EPPIdnTableCheckResp.equals(): domains not equal");
			return false;
		}
		
		return true;
	}

	/**
	 * Clone <code>EPPIdnTableCheckResp</code>.
	 * 
	 * @return clone of <code>EPPIdnTableCheckResp</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPIdnTableCheckResp clone = (EPPIdnTableCheckResp) super.clone();

		// Tables
		clone.tables = (List) ((ArrayList) tables).clone();

		// Domains
		clone.domains = (List) ((ArrayList) domains).clone();

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
