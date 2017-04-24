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
package com.verisign.epp.codec.reseller;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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
 * Represents a reseller disclose definition.
 */
public class EPPResellerDisclose implements EPPCodecComponent {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPResellerDisclose.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * XML local name for <code>EPPResellerDisclose</code>.
	 */
	public static final String ELM_LOCALNAME = "disclose";

	/**
	 * XML root tag for <code>EPPResellerContact</code>.
	 */
	public static final String ELM_NAME = EPPResellerMapFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * XML local name for the the <code>name</code> element.
	 */
	private final static String ELM_RESELLER_NAME = "name";

	/**
	 * XML local name for the the <code>org</code> element.
	 */
	private final static String ELM_RESELLER_ORG = "org";

	/**
	 * XML local name for the the <code>address</code> element.
	 */
	private final static String ELM_RESELLER_ADDRESS = "address";

	/**
	 * XML local name for the the <code>voice</code> element.
	 */
	private final static String ELM_RESELLER_VOICE = "voice";

	/**
	 * XML local name for the the <code>fax</code> element.
	 */
	private final static String ELM_RESELLER_FAX = "fax";

	/**
	 * XML local name for the the <code>email</code> element.
	 */
	private final static String ELM_RESELLER_EMAIL = "email";

	/**
	 * XML local name for the the <code>url</code> element.
	 */
	private final static String ELM_RESELLER_URL = "url";

	/**
	 * XML local name for the the <code>contact</code> element.
	 */
	private final static String ELM_RESELLER_CONTACT = "contact";

	/**
	 * XML Attribute Name for disclose flag.
	 */
	private final static String ATTR_FLAG = "flag";

	/**
	 * XML Attribute Name for type attribute.
	 */
	private final static String ATTR_TYPE = "type";

	/**
	 * Localized type with one of the following values:<br>
	 * <ul>
	 * <li><code>LOC</code> - Localized form of data that MAY be represented in
	 * unrestricted UTF-8.</li>
	 * <li><code>INT</code> - Internationalized form o data that MUST be
	 * represented in a subset of UTF-8 that can be represented in the 7-bit
	 * US-ASCII character set.</li>
	 */
	public enum Type implements java.io.Serializable, Cloneable {
		LOC("loc"), INT("int");

		private final String typeStr;

		/**
		 * Define the string value for the enumerated value.
		 * 
		 * @param aTypeStr
		 *            Enumerated value string
		 */
		Type(String aTypeStr) {
			this.typeStr = aTypeStr;
		}

		/**
		 * Get the type enumerated value given the matching string.
		 * 
		 * @param aType
		 *            <code>Type</code> enumerated string to convert to an
		 *            enumerated <code>Type</code> instance.
		 * 
		 * @return Enumerated <code>Type</code> value matching the
		 *         <code>String</code>.
		 * 
		 * @throws InvalidParameterException
		 *             If <code>aString</code> does not match an enumerated
		 *             <code>Type</code> string value.
		 */
		public static Type getType(String aType) {
			if (aType.equals(LOC.typeStr)) {
				return LOC;
			}
			else if (aType.equals(INT.typeStr)) {
				return INT;
			}
			else {
				throw new InvalidParameterException("Type enum value of "
						+ aType + " is not valid.");
			}
		}

		/**
		 * Convert the enumerated <code>Type</code> value to a
		 * <code>String</code>.
		 */
		public String toString() {
			return this.typeStr;
		}

	}

	/**
	 * Maximum number of <code>name</code> elements.
	 */
	private final static int MAX_NAMES = 2;

	/**
	 * Maximum number of <code>org</code> elements.
	 */
	private final static int MAX_ORGS = 2;

	/**
	 * Maximum number of <code>addr</code> elements.
	 */
	private final static int MAX_ADDRS = 2;

	/**
	 * Client disclosure preference
	 */
	private boolean flag = false;

	/**
	 * Reseller Names
	 */
	private List<Type> names = new ArrayList<Type>();

	/**
	 * Reseller Organizations
	 */
	private List<Type> orgs = new ArrayList<Type>();

	/**
	 * Reseller Addresses
	 */
	private List<Type> addresses = new ArrayList<Type>();

	/**
	 * Reseller Voice
	 */
	private boolean voice = false;

	/**
	 * Reseller Fax
	 */
	private boolean fax = false;

	/**
	 * Reseller Email
	 */
	private boolean email = false;

	/**
	 * Reseller Url
	 */
	private boolean url = false;

	/**
	 * Reseller Contact
	 */
	private boolean contact = false;

	/**
	 * <code>EPPResellerDisclose</code> default constructor. Must call required
	 * setter methods before invoking {@link #encode(Document)}.
	 */
	public EPPResellerDisclose() {
		// Default values set in attribute definitions.
	}

	/**
	 * <code>EPPResellerDisclose</code> that takes the required disclosure flag
	 * attribute.
	 * 
	 * @param aFlag
	 *            <code>true</code> if the client preference is to allow
	 *            disclosure; <code>false</code> otherwise to indicate that the
	 *            client preference is to not allow disclosure.
	 */
	public EPPResellerDisclose(boolean aFlag) {
		this.flag = aFlag;
	}

	/**
	 * Are any names defined in the list of names?
	 * 
	 * @return <code>true</code> if there is at least one name defined;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasNames() {
		if (this.names != null && !this.names.isEmpty()) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds a name element with the specific type to the list of names.
	 * 
	 * @param aType
	 *            Type of name element to add to the list of names.
	 */
	public void addName(Type aType) {
		this.names.add(aType);
	}

	/**
	 * Gets disclose names types set.
	 *
	 * @return <code>List</code> of name <code>Type</code> values.
	 */
	public List<Type> getNames() {
		return this.names;
	}

	/**
	 * Sets disclose name types.
	 *
	 * @param aNames
	 *            <code>List</code> of name <code>Type</code> values.
	 */
	public void setNames(List<Type> aNames) {
		if (aNames == null) {
			this.names = new ArrayList<Type>();
		}
		else {
			this.names = aNames;
		}
	}

	/**
	 * Are any orgs defined in the list of orgs?
	 * 
	 * @return <code>true</code> if there is at least one org defined;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasOrgs() {
		if (this.orgs != null && !this.orgs.isEmpty()) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an org element with the specific type to the list of orgs.
	 * 
	 * @param aType
	 *            Type of org element to add to the list of orgs.
	 */
	public void addOrg(Type aType) {
		this.orgs.add(aType);
	}

	/**
	 * Gets disclose org types set.
	 *
	 * @return <code>List</code> of org <code>Type</code> values.
	 */
	public List<Type> getOrgs() {
		return this.orgs;
	}

	/**
	 * Sets disclose org types.
	 *
	 * @param aOrgs
	 *            <code>List</code> of org <code>Type</code> values.
	 */
	public void setOrgs(List<Type> aOrgs) {
		if (aOrgs == null) {
			this.orgs = new ArrayList<Type>();
		}
		else {
			this.orgs = aOrgs;
		}
	}

	/**
	 * Are any addresses defined in the list of addresses?
	 * 
	 * @return <code>true</code> if there is at least one address defined;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasAddresses() {
		if (this.addresses != null && !this.addresses.isEmpty()) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an address element with the specific type to the list of addresses.
	 * 
	 * @param aType
	 *            Type of address element to add to the list of addresses.
	 */
	public void addAddress(Type aType) {
		this.addresses.add(aType);
	}

	/**
	 * Gets disclose address types set.
	 *
	 * @return <code>List</code> of address <code>Type</code> values.
	 */
	public List<Type> getAddresses() {
		return this.addresses;
	}

	/**
	 * Sets disclose address types.
	 *
	 * @param aAddresses
	 *            <code>List</code> of address <code>Type</code> values.
	 */
	public void setAddresses(List<Type> aAddresses) {
		if (aAddresses == null) {
			this.addresses = new ArrayList<Type>();
		}
		else {
			this.addresses = aAddresses;
		}
	}

	/**
	 * Is the client preference to allow disclosure?
	 * 
	 * @return <code>true</code> if the client preference is to allow
	 *         disclosure; <code>false</code> otherwise to indicate that the
	 *         client preference is to not allow disclosure.
	 */
	public boolean isFlag() {
		return this.flag;
	}

	/**
	 * Sets the client preference for disclosure.
	 * 
	 * @param aFlag
	 *            <code>true</code> if the client preference is to allow
	 *            disclosure; <code>false</code> otherwise to indicate that the
	 *            client preference is to not allow disclosure.
	 */
	public void setFlag(boolean aFlag) {
		this.flag = aFlag;
	}

	/**
	 * Will the voice element included in the disclosure?
	 * 
	 * @return <code>true</code> if the voice element is included in the
	 *         disclosure; <code>false</code> otherwise.
	 */
	public boolean isVoice() {
		return this.voice;
	}

	/**
	 * Sets the inclusion of the voice element in the disclosure.
	 * 
	 * @param aVoice
	 *            <code>true</code> to include the voice element in the
	 *            disclosure; <code>false</code> otherwise.
	 */
	public void setVoice(boolean aVoice) {
		this.voice = aVoice;
	}

	/**
	 * Is the fax element included in the disclosure?
	 * 
	 * @return <code>true</code> if the fax element is included in the
	 *         disclosure; <code>false</code> otherwise.
	 */
	public boolean isFax() {
		return this.fax;
	}

	/**
	 * Sets the inclusion of the fax element in the disclosure.
	 * 
	 * @param aFax
	 *            <code>true</code> to include the fax element in the
	 *            disclosure; <code>false</code> otherwise.
	 */
	public void setFax(boolean aFax) {
		this.fax = aFax;
	}

	/**
	 * Is the email element included in the disclosure?
	 * 
	 * @return <code>true</code> if the email element is included in the
	 *         disclosure; <code>false</code> otherwise.
	 */
	public boolean isEmail() {
		return this.email;
	}

	/**
	 * Sets the inclusion of the email element in the disclosure.
	 * 
	 * @param aEmail
	 *            <code>true</code> to include the email element in the
	 *            disclosure; <code>false</code> otherwise.
	 */
	public void setEmail(boolean aEmail) {
		this.email = aEmail;
	}

	/**
	 * Is the url element included in the disclosure?
	 * 
	 * @return <code>true</code> to include the url element in the disclosure;
	 *         <code>false</code> otherwise.
	 */
	public boolean isUrl() {
		return this.url;
	}

	/**
	 * Sets the inclusion of the url element in the disclosure.
	 * 
	 * @param aUrl
	 *            <code>true</code> to include the url element in the
	 *            disclosure; <code>false</code> otherwise.
	 */
	public void setUrl(boolean aUrl) {
		this.url = aUrl;
	}

	/**
	 * Is the contact element included in the disclosure?
	 * 
	 * @return <code>true</code> to include the contact element in the
	 *         disclosure; <code>false</code> otherwise.
	 */
	public boolean isContact() {
		return this.contact;
	}

	/**
	 * Sets the inclusion of the contact element in the disclosure.
	 * 
	 * @param aContact
	 *            <code>true</code> to include the contact element in the
	 *            disclosure; <code>false</code> otherwise.
	 */
	public void setContact(boolean aContact) {
		this.contact = aContact;
	}

	/**
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPResellerDisclose</code> instance.
	 *
	 * @param aDocument
	 *            DOM Document that is being built. Used as an Element factory.
	 *
	 * @return Root element associated with <code>EPPResellerDisclose</code>.
	 *
	 * @exception EPPEncodeException
	 *                On error
	 */
	public Element encode(Document aDocument) throws EPPEncodeException {

		Element theElm = null;

		validateState();

		Element root = aDocument.createElementNS(EPPResellerMapFactory.NS,
				ELM_NAME);

		// Flag
		EPPUtil.encodeBooleanAttr(root, ATTR_FLAG, flag);

		// Names
		if (this.hasNames()) {
			for (Type theName : this.names) {
				theElm = aDocument.createElementNS(EPPResellerMapFactory.NS,
						EPPResellerMapFactory.NS_PREFIX + ":"
								+ ELM_RESELLER_NAME);
				theElm.setAttribute(ATTR_TYPE, theName.toString());
			}
		}

		// Orgs
		if (this.hasOrgs()) {
			for (Type theOrg : this.orgs) {
				theElm = aDocument.createElementNS(EPPResellerMapFactory.NS,
						EPPResellerMapFactory.NS_PREFIX + ":"
								+ ELM_RESELLER_ORG);
				theElm.setAttribute(ATTR_TYPE, theOrg.toString());
			}
		}

		// Addresses
		if (this.hasAddresses()) {
			for (Type theAddress : this.addresses) {
				theElm = aDocument.createElementNS(EPPResellerMapFactory.NS,
						EPPResellerMapFactory.NS_PREFIX + ":"
								+ ELM_RESELLER_ADDRESS);
				theElm.setAttribute(ATTR_TYPE, theAddress.toString());
			}
		}

		// Voice
		if (this.voice) {
			root.appendChild(aDocument.createElementNS(
					EPPResellerMapFactory.NS, EPPResellerMapFactory.NS_PREFIX
							+ ":" + ELM_RESELLER_VOICE));
		}

		// Fax
		if (this.fax) {
			root.appendChild(aDocument.createElementNS(
					EPPResellerMapFactory.NS, EPPResellerMapFactory.NS_PREFIX
							+ ":" + ELM_RESELLER_FAX));
		}

		// Email
		if (this.email) {
			root.appendChild(aDocument.createElementNS(
					EPPResellerMapFactory.NS, EPPResellerMapFactory.NS_PREFIX
							+ ":" + ELM_RESELLER_EMAIL));
		}

		// Url
		if (this.url) {
			root.appendChild(aDocument.createElementNS(
					EPPResellerMapFactory.NS, EPPResellerMapFactory.NS_PREFIX
							+ ":" + ELM_RESELLER_URL));
		}

		// Contact
		if (this.contact) {
			root.appendChild(aDocument.createElementNS(
					EPPResellerMapFactory.NS, EPPResellerMapFactory.NS_PREFIX
							+ ":" + ELM_RESELLER_CONTACT));
		}

		return root;
	}

	/**
	 * Decode the <code>EPPResellerDisclose</code> attributes from the aElement
	 * DOM Element tree.
	 *
	 * @param aElement
	 *            Root DOM Element to decode <code>EPPResellerDisclose</code>
	 *            from.
	 *
	 * @exception EPPDecodeException
	 *                Unable to decode aElement
	 */
	public void decode(Element aElement) throws EPPDecodeException {

		Vector theChildren = null;

		// Flag
		this.flag = EPPUtil.decodeBooleanAttr(aElement, ATTR_FLAG);

		// Names
		this.names = new ArrayList<Type>();
		theChildren = EPPUtil.getElementsByTagNameNS(aElement,
				EPPResellerMapFactory.NS, ELM_RESELLER_NAME);
		for (int i = 0; i < theChildren.size(); i++) {
			this.names.add(Type.getType(((Element) theChildren.elementAt(i))
					.getAttribute(ATTR_TYPE)));
		}

		// Orgs
		this.orgs = new ArrayList<Type>();
		theChildren = EPPUtil.getElementsByTagNameNS(aElement,
				EPPResellerMapFactory.NS, ELM_RESELLER_ORG);
		for (int i = 0; i < theChildren.size(); i++) {
			this.orgs.add(Type.getType(((Element) theChildren.elementAt(i))
					.getAttribute(ATTR_TYPE)));
		}

		// Addresses
		this.addresses = new ArrayList<Type>();
		theChildren = EPPUtil.getElementsByTagNameNS(aElement,
				EPPResellerMapFactory.NS, ELM_RESELLER_ADDRESS);
		for (int i = 0; i < theChildren.size(); i++) {
			this.addresses
					.add(Type.getType(((Element) theChildren.elementAt(i))
							.getAttribute(ATTR_TYPE)));
		}

		// Voice
		if (EPPUtil.getElementByTagNameNS(aElement, EPPResellerMapFactory.NS,
				ELM_RESELLER_VOICE) != null) {
			this.voice = true;
		}
		else {
			this.voice = false;
		}

		// Fax
		if (EPPUtil.getElementByTagNameNS(aElement, EPPResellerMapFactory.NS,
				ELM_RESELLER_FAX) != null) {
			this.fax = true;
		}
		else {
			this.fax = false;
		}

		// Email
		if (EPPUtil.getElementByTagNameNS(aElement, EPPResellerMapFactory.NS,
				ELM_RESELLER_EMAIL) != null) {
			this.email = true;
		}
		else {
			this.email = false;
		}

		// Url
		if (EPPUtil.getElementByTagNameNS(aElement, EPPResellerMapFactory.NS,
				ELM_RESELLER_URL) != null) {
			this.url = true;
		}
		else {
			this.url = false;
		}

		// Contact
		if (EPPUtil.getElementByTagNameNS(aElement, EPPResellerMapFactory.NS,
				ELM_RESELLER_CONTACT) != null) {
			this.contact = true;
		}
		else {
			this.contact = false;
		}

	}

	/**
	 * implements a deep <code>EPPResellerDisclose</code> compare.
	 *
	 * @param aObject
	 *            <code>EPPResellerDisclose</code> instance to compare with
	 *
	 * @return {@code true} if this object is the same as the aObject argument;
	 *         {@code false} otherwise
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPResellerDisclose)) {
			return false;
		}

		EPPResellerDisclose theComp = (EPPResellerDisclose) aObject;

		// Flag
		if (!EqualityUtil.equals(this.flag, theComp.flag)) {
			cat.error("EPPResellerDisclose.equals(): flag not equal");
			return false;
		}

		// Names
		if (!EPPUtil.equalLists(this.names, theComp.names)) {
			cat.error("EPPResellerDisclose.equals(): names not equal");
			return false;
		}

		// Orgs
		if (!EPPUtil.equalLists(this.orgs, theComp.orgs)) {
			cat.error("EPPResellerDisclose.equals(): orgs not equal");
			return false;
		}

		// Addresses
		if (!EPPUtil.equalLists(this.addresses, theComp.addresses)) {
			cat.error("EPPResellerDisclose.equals(): addresses not equal");
			return false;
		}

		// Voice
		if (!EqualityUtil.equals(this.voice, theComp.voice)) {
			cat.error("EPPResellerDisclose.equals(): voice not equal");
			return false;
		}

		// Fax
		if (!EqualityUtil.equals(this.fax, theComp.fax)) {
			cat.error("EPPResellerDisclose.equals(): fax not equal");
			return false;
		}

		// Email
		if (!EqualityUtil.equals(this.email, theComp.email)) {
			cat.error("EPPResellerDisclose.equals(): email not equal");
			return false;
		}

		// Url
		if (!EqualityUtil.equals(this.url, theComp.url)) {
			cat.error("EPPResellerDisclose.equals(): url not equal");
			return false;
		}

		// Contact
		if (!EqualityUtil.equals(this.contact, theComp.contact)) {
			cat.error("EPPResellerDisclose.equals(): contact not equal");
			return false;
		}

		return true;
	}

	/**
	 * Clone <code>EPPResellerDisclose</code>.
	 *
	 * @return clone of <code>EPPResellerDisclose</code>
	 *
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPResellerDisclose clone = (EPPResellerDisclose) super.clone();

		// Names
		if (this.names != null) {
			clone.names = (List) ((ArrayList) names).clone();
		}

		// Orgs
		if (this.orgs != null) {
			clone.orgs = (List) ((ArrayList) orgs).clone();
		}

		// Addresses
		if (this.addresses != null) {
			clone.addresses = (List) ((ArrayList) addresses).clone();
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
	 * Validate the state of the <code>EPPResllerDisclose</code> instance. A
	 * valid state means that all of the required attributes have been set. If
	 * validateState returns without an exception, the state is valid. If the
	 * state is not valid, the EPPCodecException will contain a description of
	 * the error. throws EPPCodecException State error. This will contain the
	 * name of the attribute that is not valid.
	 *
	 * @throws EPPEncodeException
	 *             Error with the state
	 */
	protected void validateState() throws EPPEncodeException {

		// Names
		if ((this.names != null) && !this.names.isEmpty()) {

			if (this.names.size() > MAX_NAMES) {
				cat.error("EPPResellerDisclose.validateState(): name lines exceed the maximum of "
						+ MAX_NAMES);
				throw new EPPEncodeException(
						"name lines exceed the maximum of " + MAX_NAMES);
			}
		}

		// Orgs
		if ((this.orgs != null) && !this.orgs.isEmpty()) {

			if (this.orgs.size() > MAX_ORGS) {
				cat.error("EPPResellerDisclose.validateState(): org lines exceed the maximum of "
						+ MAX_ORGS);
				throw new EPPEncodeException("org lines exceed the maximum of "
						+ MAX_ORGS);
			}
		}

		// Addresses
		if ((this.addresses != null) && !this.addresses.isEmpty()) {

			if (addresses.size() > MAX_ADDRS) {
				cat.error("EPPResellerDisclose.validateState(): address lines exceed the maximum of "
						+ MAX_ADDRS);
				throw new EPPEncodeException(
						"address lines exceed the maximum of " + MAX_ADDRS);
			}
		}

	}

}
