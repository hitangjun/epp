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

import com.verisign.epp.codec.gen.EPPCheckCmd;
import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.util.EPPCatFactory;

/**
 * Represents an EPP Internationalized Domain Name (IDN) Table &lt;check&gt;
 * command, which is used to determine if an (IDN) Table Identifier is known to
 * the server. The &lt;idnTable:check&gt; element MUST either a list of
 * &lt;idnTable:domain&gt; elements to check on the IDN table information for
 * the domain names or a list of &lt; elements to check on the IDN table
 * information for the IDN table identifiers:<br>
 * 
 * <br>
 * <code>EPPIdnTableCheckResp</code> is the concrete <code>EPPReponse</code>
 * associated with <code>EPPIdnTableCheckCmd</code>.
 * 
 * @see com.verisign.epp.codec.idntable.EPPIdnTableCheckResp
 */
public class EPPIdnTableCheckCmd extends EPPCheckCmd {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPIdnTableCheckCmd.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * XML local name for <code>EPPIdnTableCheckCmd</code>.
	 */
	public static final String ELM_LOCALNAME = "check";

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

	/** XML Element Name for the <code>table</code> element. */
	private final static String ELM_TABLE = "table";

	/**
	 * Table identifiers to check. This is a <code>List</code> of
	 * <code>String</code> instances.
	 */
	private List<String> tables = new ArrayList<String>();

	/**
	 * Domain names to check for the IDN table information.
	 */
	private List<EPPIdnTableDomainLabel> domains = new ArrayList<EPPIdnTableDomainLabel>();

	/**
	 * <code>EPPIdnTableCheckCmd</code> default constructor.
	 */
	public EPPIdnTableCheckCmd() {
	}

	/**
	 * <code>EPPIdnTableCheckCmd</code> constructor that only takes the client
	 * transaction identifier
	 * 
	 * @param aTransId
	 *            Transaction Id associated with command.
	 */
	public EPPIdnTableCheckCmd(String aTransId) {
		super(aTransId);
	}

	/**
	 * <code>EPPIdnTableCheckCmd</code> constructor for setting an individual
	 * table identifier to check in Table Check Form.
	 * 
	 * @param aTransId
	 *            Transaction Id associated with command.
	 * @param aTable
	 *            table identifier to check
	 */
	public EPPIdnTableCheckCmd(String aTransId, String aTable) {
		super(aTransId);
		this.addTable(aTable);
	}

	/**
	 * <code>EPPIdnTableCheckCmd</code> constructor for setting an individual
	 * domain name to check in Domain Check Form.
	 * 
	 * @param aTransId
	 *            Transaction Id associated with command.
	 * @param aDomain
	 *            Domain name to check
	 */
	public EPPIdnTableCheckCmd(String aTransId, EPPIdnTableDomainLabel aDomain) {
		super(aTransId);
		this.addDomain(aDomain);
	}

	/**
	 * Gets the EPP command Namespace associated with
	 * <code>EPPIdnTableCheckCmd</code>.
	 * 
	 * @return <code>EPPIdnTableMapFactory.NS</code>
	 */
	public String getNamespace() {
		return EPPIdnTableMapFactory.NS;
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
	 * Are any table identifiers defined in the list of table identifiers?
	 * 
	 * @return <code>true</code> if there is at least one table identifier
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
	 * Get the list of table identifiers to check.
	 * 
	 * @return {@code List} of table identifier {@code String}'s
	 */
	public List<String> getTables() {
		return this.tables;
	}

	/**
	 * Set the list of table identifiers to check.
	 * 
	 * @param aTables
	 *            {@code List} of table identifier {@code String}'s
	 */
	public void setTables(List<String> aTables) {
		if (aTables == null) {
			this.tables = new ArrayList<String>();
		}
		this.tables = aTables;
	}

	/**
	 * Set an individual table identifier to check. This method clears the
	 * existing list of table identifiers.
	 * 
	 * @param aTable
	 *            Table identifier to check
	 */
	public void setTable(String aTable) {
		this.tables = new ArrayList<String>();
		this.tables.add(aTable);
	}

	/**
	 * Append a table identifier to the list of table identifiers to check. This
	 * method does NOT clear the existing list of table identifiers.
	 * 
	 * @param aTable
	 *            Table identifier to check
	 */
	public void addTable(String aTable) {
		this.tables.add(aTable);
	}

	/**
	 * Are any domain names defined in the list of domain names?
	 * 
	 * @return <code>true</code> if there is at least one domain name defined;
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
	 * Get the list of domain names to check
	 * 
	 * @return {@code List} of domain names
	 */
	public List<EPPIdnTableDomainLabel> getDomains() {
		return this.domains;
	}

	/**
	 * Set the list of domain names to check
	 * 
	 * @param aDomains
	 *            {@code List} of domain names
	 */
	public void setDomains(List<EPPIdnTableDomainLabel> aDomains) {
		if (aDomains == null) {
			this.domains = new ArrayList<EPPIdnTableDomainLabel>();
		}
		this.domains = aDomains;
	}

	/**
	 * Set an individual domain name to check. This method clears the existing
	 * list of domain names.
	 * 
	 * @param aDomain
	 *            Domain name to check
	 */
	public void setDomain(EPPIdnTableDomainLabel aDomain) {
		this.domains = new ArrayList<EPPIdnTableDomainLabel>();
		this.domains.add(aDomain);
	}

	/**
	 * Append a domain name to the list of domain names to check. This method
	 * does NOT clear the existing list of domain names.
	 * 
	 * @param aDomain
	 *            Domain name to check
	 */
	public void addDomain(EPPIdnTableDomainLabel aDomain) {
		this.domains.add(aDomain);
	}

	/**
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPIdnTableCheckCmd</code> instance.
	 * 
	 * @param aDocument
	 *            DOM Document that is being built. Used as an Element factory.
	 * 
	 * @return Element Root DOM Element representing the
	 *         <code>EPPIdnTableCheckCmd</code> instance.
	 * 
	 * @exception EPPEncodeException
	 *                Unable to encode <code>EPPIdnTableCheckCmd</code>
	 *                instance.
	 */
	protected Element doEncode(Document aDocument) throws EPPEncodeException {

		// Check required attributes
		if (this.getForm() == Form.UNDEFINED_FORM) {
			throw new EPPEncodeException(
					"Undefined form in EPPIdnTableCheckCmd");
		}

		// Create root element
		Element root = aDocument.createElementNS(EPPIdnTableMapFactory.NS,
				ELM_NAME);

		// Tables
		EPPUtil.encodeList(aDocument, root, this.tables,
				EPPIdnTableMapFactory.NS, EPPIdnTableMapFactory.NS_PREFIX + ":"
						+ ELM_TABLE);

		// Domains
		EPPUtil.encodeCompList(aDocument, root, this.domains);

		return root;
	}

	/**
	 * Decode the <code>EPPIdnTableCheckCmd</code> attributes from the aElement
	 * DOM Element tree.
	 * 
	 * @param aElement
	 *            Root DOM Element to decode <code>EPPIdnTableCheckCmd</code>
	 *            from.
	 * 
	 * @exception EPPDecodeException
	 *                Unable to decode aElement
	 */
	protected void doDecode(Element aElement) throws EPPDecodeException {

		// Tables
		this.tables = EPPUtil.decodeList(aElement, EPPIdnTableMapFactory.NS,
				ELM_TABLE);

		// Domains
		this.domains = EPPUtil.decodeCompList(aElement,
				EPPIdnTableMapFactory.NS, EPPIdnTableDomainLabel.ELM_NAME,
				EPPIdnTableDomainLabel.class);
	}

	/**
	 * Compare an instance of <code>EPPIdnTableCheckCmd</code> with this
	 * instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return {@code true} if this object is the same as the aObject argument;
	 *         {@code false} otherwise.
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPIdnTableCheckCmd)) {
			return false;
		}

		if (!super.equals(aObject)) {
			return false;
		}

		EPPIdnTableCheckCmd theMap = (EPPIdnTableCheckCmd) aObject;

		// Tables
		if (!EPPUtil.equalLists(tables, theMap.tables)) {
			cat.error("EPPIdnTableCheckCmd.equals(): tables not equal");
			return false;
		}

		// Domains
		if (!EPPUtil.equalLists(domains, theMap.domains)) {
			cat.error("EPPIdnTableCheckCmd.equals(): domains not equal");
			return false;
		}

		return true;
	}

	/**
	 * Clone <code>EPPIdnTableCheckCmd</code>.
	 * 
	 * @return Deep copy clone of <code>EPPIdnTableCheckCmd</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPIdnTableCheckCmd clone = (EPPIdnTableCheckCmd) super.clone();

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
