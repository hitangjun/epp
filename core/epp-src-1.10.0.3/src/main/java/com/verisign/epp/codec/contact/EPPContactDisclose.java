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
 * Represents a contact disclose definition.
 */
public class EPPContactDisclose implements EPPCodecComponent {

	/**
	 * XML Attribute Name for disclose flag.
	 */
	private final static String ATTR_FLAG = "flag";

	/**
	 * False flag string value.
	 *
	 * @deprecated Flag should not be get or set via a <code>String</code>.
	 */
	@Deprecated
	public final static String ATTR_FLAG_FALSE = "0";

	/**
	 * True flag string value.
	 *
	 * @deprecated Flag should not be get or set via a <code>String</code>.
	 */
	@Deprecated
	public final static String ATTR_FLAG_TRUE = "1";

	/**
	 * XML Attribute Name for disclose type.
	 */
	private final static String ATTR_TYPE = "type";

	/**
	 * Value of the INT in contact disclose type mapping
	 */
	public final static String ATTR_TYPE_INT = "int";

	/**
	 * Value of the LOC in contact disclose type mapping
	 */
	public final static String ATTR_TYPE_LOC = "loc";

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPContactDisclose.class.getName(),
	      EPPCatFactory.getInstance().getFactory());

	/**
	 * XML tag name for the disclose <code>addr</code> attribute.
	 */
	private final static String ELM_DISCLOSE_ADDR = "addr";

	/**
	 * XML tag name for the disclose <code>email</code> attribute.
	 */
	private final static String ELM_DISCLOSE_EMAIL = "email";

	/**
	 * XML tag name for the disclose <code>fax</code> attribute.
	 */
	private final static String ELM_DISCLOSE_FAX = "fax";

	/**
	 * XML tag name for the disclose <code>name</code> attribute.
	 */
	private final static String ELM_DISCLOSE_NAME = "name";

	/**
	 * XML tag name for the disclose <code>org</code> attribute.
	 */
	private final static String ELM_DISCLOSE_ORG = "org";

	/**
	 * XML tag name for the disclose <code>voice</code> attribute.
	 */
	private final static String ELM_DISCLOSE_VOICE = "voice";

	/**
	 * Constant for disclose local name
	 */
	public static final String ELM_LOCALNAME = "disclose";

	/**
	 * Constant for the disclose full name
	 */
	public final static String ELM_NAME = EPPContactMapFactory.NS_PREFIX + ":" + ELM_LOCALNAME;

	/**
	 * Internationalized addr element set?
	 */
	private boolean addrInt = false;

	/**
	 * Localized addr element set?
	 */
	private boolean addrLoc = false;

	/**
	 * Email element set?
	 */
	private boolean email = false;

	/**
	 * Fax element set?
	 */
	private boolean fax = false;

	/**
	 * Required disclose flag value with a default value of <code>false</code>.
	 */
	private boolean flag = false;

	/**
	 * Internationalized name element set?
	 */
	private boolean nameInt = false;

	/**
	 * Localized name element set?
	 */
	private boolean nameLoc = false;

	/**
	 * Internationalized org element set?
	 */
	private boolean orgInt = false;

	/**
	 * Localized org element set?
	 */
	private boolean orgLoc = false;

	/**
	 * XML root element tag name for contact disclose definition The value needs
	 * to be set before calling encode(Document) and default value is set to
	 * <code>ELM_NAME_DISCLOSE</code>.
	 */
	private String rootName = ELM_NAME;

	/**
	 * Voice element set?
	 */
	private boolean voice = false;

	/**
	 * <code>EPPContactDisclose</code> default constructor. Must call required
	 * setter methods before invoking {@link #encode(Document)}.
	 */
	public EPPContactDisclose() {
		// Default values set in attribute definitions.
	}

	/**
	 * <code>EPPContactDisclose</code> constructor that takes all of the disclose
	 * settings. All of the parameters except for the <code>aFlag</code>
	 * parameter will include the element if the <code>boolean</code> value is
	 * <code>true</code> and won't if the <code>boolean</code> value is
	 * <code>false</code>.
	 *
	 * @param aFlag
	 *           Disclosure flag
	 * @param aNameInt
	 *           Include the internalized name element in the disclose?
	 * @param aNameLoc
	 *           Include the localized name element in the disclose?
	 * @param aOrgInt
	 *           Include the internationalized org element in the disclose?
	 * @param aOrgLoc
	 *           Include the localized org element in the disclose?
	 * @param aAddrInt
	 *           Include the internationalized address element in the disclose?
	 * @param aAddrLoc
	 *           Include the localized address element in the disclose?
	 * @param aVoice
	 *           Include the voice element in the disclose?
	 * @param aFax
	 *           Include the fax element in the disclose?
	 * @param aEmail
	 *           Include the email element in the disclose?
	 */
	public EPPContactDisclose(boolean aFlag, boolean aNameInt, boolean aNameLoc, boolean aOrgInt, boolean aOrgLoc,
	      boolean aAddrInt, boolean aAddrLoc, boolean aVoice, boolean aFax, boolean aEmail) {
		this.flag = aFlag;
		this.nameInt = aNameInt;
		this.nameLoc = aNameLoc;
		this.orgInt = aOrgInt;
		this.orgLoc = aOrgLoc;
		this.addrInt = aAddrInt;
		this.addrLoc = aAddrLoc;
		this.voice = aVoice;
		this.fax = aFax;
		this.email = aEmail;
	}

	/**
	 * Clone <code>EPPContactDisclose</code>.
	 *
	 * @return clone of <code>EPPContactDisclose</code>
	 *
	 * @exception CloneNotSupportedException
	 *               standard Object.clone exception
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		EPPContactDisclose clone = (EPPContactDisclose) super.clone();

		return clone;
	}

	/**
	 * Decode the <code>EPPContactDisclose</code> attributes from the aElement
	 * DOM Element tree.
	 *
	 * @param aElement
	 *           Root DOM Element to decode <code>EPPContactDisclose</code> from.
	 *
	 * @exception EPPDecodeException
	 *               Unable to decode aElement
	 */
	@Override
	public void decode(Element aElement) throws EPPDecodeException {
		Element theElm = null;

		// flag
		this.flag = EPPUtil.decodeBooleanAttr(aElement, ATTR_FLAG);

		// names
		this.nameInt = false;
		this.nameLoc = false;
		Vector theElms = EPPUtil.getElementsByTagNameNS(aElement, EPPContactMapFactory.NS, ELM_DISCLOSE_NAME);
		for (int i = 0; i < theElms.size(); i++) {
			theElm = (Element) theElms.elementAt(i);

			if (theElm.hasAttribute(ATTR_TYPE)) {
				if (theElm.getAttribute(ATTR_TYPE).equals(ATTR_TYPE_INT)) {
					this.nameInt = true;
				}
				else if (theElm.getAttribute(ATTR_TYPE).equals(ATTR_TYPE_LOC)) {
					this.nameLoc = true;
				}
			}

		}

		// orgs
		this.orgInt = false;
		this.orgLoc = false;
		theElms = EPPUtil.getElementsByTagNameNS(aElement, EPPContactMapFactory.NS, ELM_DISCLOSE_ORG);
		for (int i = 0; i < theElms.size(); i++) {
			theElm = (Element) theElms.elementAt(i);

			if (theElm.hasAttribute(ATTR_TYPE)) {
				if (theElm.getAttribute(ATTR_TYPE).equals(ATTR_TYPE_INT)) {
					this.orgInt = true;
				}
				else if (theElm.getAttribute(ATTR_TYPE).equals(ATTR_TYPE_LOC)) {
					this.orgLoc = true;
				}
			}
		}

		// addresses
		this.addrInt = false;
		this.addrLoc = false;
		theElms = EPPUtil.getElementsByTagNameNS(aElement, EPPContactMapFactory.NS, ELM_DISCLOSE_ADDR);
		for (int i = 0; i < theElms.size(); i++) {
			theElm = (Element) theElms.elementAt(i);

			if (theElm.hasAttribute(ATTR_TYPE)) {
				if (theElm.getAttribute(ATTR_TYPE).equals(ATTR_TYPE_INT)) {
					this.addrInt = true;
				}
				else if (theElm.getAttribute(ATTR_TYPE).equals(ATTR_TYPE_LOC)) {
					this.addrLoc = true;
				}
			}
		}

		// voice
		theElm = EPPUtil.getElementByTagNameNS(aElement, EPPContactMapFactory.NS, ELM_DISCLOSE_VOICE);
		this.voice = (theElm != null);

		// fax
		theElm = EPPUtil.getElementByTagNameNS(aElement, EPPContactMapFactory.NS, ELM_DISCLOSE_FAX);
		this.fax = (theElm != null);

		// email
		theElm = EPPUtil.getElementByTagNameNS(aElement, EPPContactMapFactory.NS, ELM_DISCLOSE_EMAIL);
		this.email = (theElm != null);
	}

	/**
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPContactDisclose</code> instance.
	 *
	 * @param aDocument
	 *           DOM Document that is being built. Used as an Element factory.
	 *
	 * @return Encoded DOM <code>Element</code>
	 *
	 * @exception EPPEncodeException
	 */
	@Override
	public Element encode(Document aDocument) throws EPPEncodeException {
		Element theElm = null;

		// Are no disclose elements set?
		if (this.isNone()) {
			throw new EPPEncodeException("No disclose elements set in EPPContactDisclose on encode");
		}

		Element root = aDocument.createElementNS(EPPContactMapFactory.NS, this.rootName);

		// add type attribute
		EPPUtil.encodeBooleanAttr(root, ATTR_FLAG, this.flag);

		// nameInt
		if (this.isNameInt()) {
			theElm = aDocument.createElementNS(EPPContactMapFactory.NS,
			      EPPContactMapFactory.NS_PREFIX + ":" + ELM_DISCLOSE_NAME);
			theElm.setAttribute(ATTR_TYPE, ATTR_TYPE_INT);
			root.appendChild(theElm);
		}

		// nameLoc
		if (this.isNameLoc()) {
			theElm = aDocument.createElementNS(EPPContactMapFactory.NS,
			      EPPContactMapFactory.NS_PREFIX + ":" + ELM_DISCLOSE_NAME);
			theElm.setAttribute(ATTR_TYPE, ATTR_TYPE_LOC);
			root.appendChild(theElm);
		}

		// orgInt
		if (this.isOrgInt()) {
			theElm = aDocument.createElementNS(EPPContactMapFactory.NS,
			      EPPContactMapFactory.NS_PREFIX + ":" + ELM_DISCLOSE_ORG);
			theElm.setAttribute(ATTR_TYPE, ATTR_TYPE_INT);
			root.appendChild(theElm);
		}

		// orgLoc
		if (this.isOrgLoc()) {
			theElm = aDocument.createElementNS(EPPContactMapFactory.NS,
			      EPPContactMapFactory.NS_PREFIX + ":" + ELM_DISCLOSE_ORG);
			theElm.setAttribute(ATTR_TYPE, ATTR_TYPE_LOC);
			root.appendChild(theElm);
		}

		// addrInt
		if (this.isAddrInt()) {
			theElm = aDocument.createElementNS(EPPContactMapFactory.NS,
			      EPPContactMapFactory.NS_PREFIX + ":" + ELM_DISCLOSE_ADDR);
			theElm.setAttribute(ATTR_TYPE, ATTR_TYPE_INT);
			root.appendChild(theElm);
		}

		// addrLoc
		if (this.isAddrLoc()) {
			theElm = aDocument.createElementNS(EPPContactMapFactory.NS,
			      EPPContactMapFactory.NS_PREFIX + ":" + ELM_DISCLOSE_ADDR);
			theElm.setAttribute(ATTR_TYPE, ATTR_TYPE_LOC);
			root.appendChild(theElm);
		}

		// Voice
		if (this.isVoice()) {
			theElm = aDocument.createElementNS(EPPContactMapFactory.NS,
			      EPPContactMapFactory.NS_PREFIX + ":" + ELM_DISCLOSE_VOICE);
			root.appendChild(theElm);
		}

		// Fax
		if (this.isFax()) {
			theElm = aDocument.createElementNS(EPPContactMapFactory.NS,
			      EPPContactMapFactory.NS_PREFIX + ":" + ELM_DISCLOSE_FAX);
			root.appendChild(theElm);
		}

		// Email
		if (this.isEmail()) {
			theElm = aDocument.createElementNS(EPPContactMapFactory.NS,
			      EPPContactMapFactory.NS_PREFIX + ":" + ELM_DISCLOSE_EMAIL);
			root.appendChild(theElm);
		}

		return root;
	}

	/**
	 * implements a deep <code>EPPContactDisclose</code> compare.
	 *
	 * @param aObject
	 *           <code>EPPContactDisclose</code> instance to compare with
	 *
	 * @return <code>true</code> if equal; <code>false</code> otherwise
	 */
	@Override
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPContactDisclose)) {
			return false;
		}

		EPPContactDisclose theComp = (EPPContactDisclose) aObject;

		// flag
		if (!EqualityUtil.equals(this.flag, theComp.flag)) {
			cat.error("EPPContactDisclose.equals(): flag not equal");
			return false;
		}

		// nameInt
		if (!EqualityUtil.equals(this.nameInt, theComp.nameInt)) {
			cat.error("EPPContactDisclose.equals(): nameInt not equal");
			return false;
		}

		// nameLoc
		if (!EqualityUtil.equals(this.nameLoc, theComp.nameLoc)) {
			cat.error("EPPContactDisclose.equals(): nameLoc not equal");
			return false;
		}

		// orgInt
		if (!EqualityUtil.equals(this.orgInt, theComp.orgInt)) {
			cat.error("EPPContactDisclose.equals(): orgInt not equal");
			return false;
		}

		// orgLoc
		if (!EqualityUtil.equals(this.orgLoc, theComp.orgLoc)) {
			cat.error("EPPContactDisclose.equals(): orgLoc not equal");
			return false;
		}

		// addrInt
		if (!EqualityUtil.equals(this.addrInt, theComp.addrInt)) {
			cat.error("EPPContactDisclose.equals(): addrInt not equal");
			return false;
		}

		// addrLoc
		if (!EqualityUtil.equals(this.addrLoc, theComp.addrLoc)) {
			cat.error("EPPContactDisclose.equals(): addrLoc not equal");
			return false;
		}

		// voice
		if (!EqualityUtil.equals(this.voice, theComp.voice)) {
			cat.error("EPPContactDisclose.equals(): voice not equal");
			return false;
		}

		// fax
		if (!EqualityUtil.equals(this.fax, theComp.fax)) {
			cat.error("EPPContactDisclose.equals(): fax not equal");
			return false;
		}

		// email
		if (!EqualityUtil.equals(this.email, theComp.email)) {
			cat.error("EPPContactDisclose.equals(): email not equal");
			return false;
		}

		return true;
	}

	/**
	 * Get contact disclose addresses.
	 *
	 * @return <code>Vector</code> of {@link EPPContactDiscloseAddress} instances
	 *         if at least one address is set; <code>null</code> otherwise.
	 *
	 * @deprecated Use {@link #isAddrInt()} and {@link #isAddrLoc()} instead.
	 */
	@Deprecated
	public Vector getAddresses() {
		if (!this.addrInt && !this.addrLoc) {
			return null;
		}

		Vector theAddresses = new Vector();

		if (this.addrInt) {
			theAddresses.addElement(new EPPContactDiscloseAddress(EPPContactDiscloseAddress.ATTR_TYPE_INT));
		}

		if (this.addrLoc) {
			theAddresses.addElement(new EPPContactDiscloseAddress(EPPContactDiscloseAddress.ATTR_TYPE_LOC));
		}

		return theAddresses;
	}

	/**
	 * Gets the contact disclose email.
	 *
	 * @return Empty <code>String</code> if set; <code>null</code> otherwise.
	 *
	 * @deprecated Use {@link #isEmail()} instead.
	 */
	@Deprecated
	public String getEmail() {
		if (this.email) {
			return new String("");
		}
		else {
			return null;
		}
	}

	/**
	 * Gets the contact disclose fax.
	 *
	 * @return Empty <code>String</code> if set; <code>null</code> otherwise.
	 *
	 * @deprecated Use {@link #isFax()} instead.
	 */
	@Deprecated
	public String getFax() {
		if (this.fax) {
			return new String("");
		}
		else {
			return null;
		}
	}

	/**
	 * Gets the disclose flag.
	 *
	 * @return {@link #ATTR_FLAG_TRUE} if <code>true</code>;
	 *         {@link #ATTR_FLAG_FALSE} otherwise.
	 */
	@Deprecated
	public String getFlag() {
		if (this.flag) {
			return ATTR_FLAG_TRUE;
		}
		else {
			return ATTR_FLAG_FALSE;
		}
	}

	/**
	 * Get contact disclose names.
	 *
	 * @return <code>Vector</code> of {@link EPPContactDiscloseName} instances if
	 *         at least one name is set; <code>null</code> otherwise.
	 *
	 * @deprecated Use {@link #isNameInt()} and {@link #isNameLoc()} instead.
	 */
	@Deprecated
	public Vector getNames() {
		if (!this.nameInt && !this.nameLoc) {
			return null;
		}

		Vector theNames = new Vector();

		if (this.nameInt) {
			theNames.addElement(new EPPContactDiscloseAddress(EPPContactDiscloseAddress.ATTR_TYPE_INT));
		}

		if (this.nameLoc) {
			theNames.addElement(new EPPContactDiscloseAddress(EPPContactDiscloseAddress.ATTR_TYPE_LOC));
		}

		return theNames;
	}

	/**
	 * Get contact disclose orgs.
	 *
	 * @return <code>Vector</code> of {@link EPPContactDiscloseOrg} instances if
	 *         at least one org is set; <code>null</code> otherwise.
	 *
	 * @deprecated Use {@link #isOrgInt()} and {@link #isOrgLoc()} instead.
	 */
	@Deprecated
	public Vector getOrgs() {
		if (!this.orgInt && !this.orgLoc) {
			return null;
		}

		Vector theOrgs = new Vector();

		if (this.orgInt) {
			theOrgs.addElement(new EPPContactDiscloseAddress(EPPContactDiscloseAddress.ATTR_TYPE_INT));
		}

		if (this.orgLoc) {
			theOrgs.addElement(new EPPContactDiscloseAddress(EPPContactDiscloseAddress.ATTR_TYPE_LOC));
		}

		return theOrgs;
	}

	/**
	 * Gets the root tag name for the disclose.
	 *
	 * @return Root tag name
	 */
	public String getRootName() {
		return this.rootName;
	}

	/**
	 * Gets the contact disclose voice.
	 *
	 * @return Empty <code>String</code> if set; <code>null</code> otherwise.
	 *
	 * @deprecated Use {@link #isVoice()} instead.
	 */
	@Deprecated
	public String getVoice() {
		if (this.voice) {
			return new String("");
		}
		else {
			return null;
		}
	}

	/**
	 * Is the internationalized addr element set?
	 *
	 * @return <code>true</code> if the internationalized addr element is set;
	 *         <code>false</code> otherwise.
	 */
	public boolean isAddrInt() {
		return this.addrInt;
	}

	/**
	 * Is the localized addr element set?
	 *
	 * @return <code>true</code> if the localized addr element is set;
	 *         <code>false</code> otherwise.
	 */
	public boolean isAddrLoc() {
		return this.addrLoc;
	}

	/**
	 * Are all of the disclose elements set?
	 *
	 * @return <code>true</code> if all of the disclose elements are set;
	 *         <code>false</code> otherwise.
	 */
	public boolean isAll() {

		if (this.isAllInt() && this.isAllLoc() && this.isAllOther()) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Are all of the internationalized disclose elements set?
	 *
	 * @return <code>true</code> if all of the internationalized disclose
	 *         elements are set; <code>false</code> otherwise.
	 */
	public boolean isAllInt() {

		if (this.nameInt && this.orgInt && this.addrInt) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Are all of the localized disclose elements set?
	 *
	 * @return <code>true</code> if all of the localized disclose elements are
	 *         set; <code>false</code> otherwise.
	 */
	public boolean isAllLoc() {

		if (this.nameLoc && this.orgLoc && this.addrLoc) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Are all of the other (voice, fax, email) disclose elements set?
	 *
	 * @return <code>true</code> if all of the other (voice, fax, email) disclose
	 *         elements are set; <code>false</code> otherwise.
	 */
	public boolean isAllOther() {

		if (this.voice && this.fax && this.email) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Is the email element set?
	 *
	 * @return <code>true</code> if the email element is set; <code>false</code>
	 *         otherwise.
	 */
	public boolean isEmail() {
		return this.email;
	}

	/**
	 * Is the fax element set?
	 *
	 * @return <code>true</code> if the fax element is set; <code>false</code>
	 *         otherwise.
	 */
	public boolean isFax() {
		return this.fax;
	}

	/**
	 * Is the disclose flag set to <code>true</code>?
	 *
	 * @return <code>true</code> if the disclose flag is set to <code>true</code>
	 *         ; <code>false</code> otherwise.
	 */
	public boolean isFlag() {
		return this.flag;
	}

	/**
	 * Is the internationalized name element set?
	 *
	 * @return <code>true</code> if the internationalized name element is set;
	 *         <code>false</code> otherwise.
	 */
	public boolean isNameInt() {
		return this.nameInt;
	}

	/**
	 * Is the localized name element set?
	 *
	 * @return <code>true</code> if the localized name element is set;
	 *         <code>false</code> otherwise.
	 */
	public boolean isNameLoc() {
		return this.nameLoc;
	}

	/**
	 * Are none of the disclose elements set?
	 *
	 * @return <code>true</code> if none of the disclose elements are set;
	 *         <code>false</code> otherwise.
	 */
	private boolean isNone() {

		if (!this.nameInt && !this.nameLoc && !this.orgInt && !this.orgLoc && !this.addrInt && !this.addrLoc
		      && !this.voice && !this.fax && !this.email) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Is the internationalized org element set?
	 *
	 * @return <code>true</code> if the internationalized org element is set;
	 *         <code>false</code> otherwise.
	 */
	public boolean isOrgInt() {
		return this.orgInt;
	}

	/**
	 * Is the localized org element set?
	 *
	 * @return <code>true</code> if the localized org element is set;
	 *         <code>false</code> otherwise.
	 */
	public boolean isOrgLoc() {
		return this.orgLoc;
	}

	/**
	 * Is the voice element set?
	 *
	 * @return <code>true</code> if the voice element is set; <code>false</code>
	 *         otherwise.
	 */
	public boolean isVoice() {
		return this.voice;
	}

	/**
	 * Sets the contact disclose addresses.
	 *
	 * @param aAddresses
	 *           <code>Vector</code> of {@link EPPContactDiscloseAddress}
	 *           instances to set.
	 *
	 * @deprecated Use {@link #setAddrInt(boolean)} and
	 *             {@link #setAddrLoc(boolean)} instead.
	 */
	@Deprecated
	public void setAddresses(Vector aAddresses) {
		this.addrInt = false;
		this.addrLoc = false;

		if (aAddresses == null) {
			return;
		}

		for (int i = 0; i < aAddresses.size(); i++) {
			EPPContactDiscloseAddress theAddress = (EPPContactDiscloseAddress) aAddresses.elementAt(i);

			if (theAddress.getType().equals(EPPContactDiscloseAddress.ATTR_TYPE_INT)) {
				this.addrInt = true;
			}

			if (theAddress.getType().equals(EPPContactDiscloseAddress.ATTR_TYPE_LOC)) {
				this.addrLoc = true;
			}
		}
	}

	/**
	 * Set to include the internationalized addr element. If <code>true</code>
	 * the element will be included in the disclose.
	 *
	 * @param aAddrInt
	 *           <code>true</code> to include the internationalized addr;
	 *           <code>false</code> otherwise.
	 */
	public void setAddrInt(boolean aAddrInt) {
		this.addrInt = aAddrInt;
	}

	/**
	 * Set to include the localized addr element. If <code>true</code> the
	 * element will be included in the disclose.
	 *
	 * @param aAddrLoc
	 *           <code>true</code> to include the localized addr;
	 *           <code>false</code> otherwise.
	 */
	public void setAddrLoc(boolean aAddrLoc) {
		this.addrLoc = aAddrLoc;
	}

	/**
	 * Set all of the disclose elements to <code>true</code>.
	 */
	public void setAll() {
		this.setAllInt();
		this.setAllLoc();
		this.setAllOther();
	}

	/**
	 * Set all of the internationalized disclose elements to <code>true</code>.
	 */
	public void setAllInt() {
		this.nameInt = true;
		this.orgInt = true;
		this.addrInt = true;
	}

	/**
	 * Set all of the localized disclose elements to <code>true</code>.
	 */
	public void setAllLoc() {
		this.nameLoc = true;
		this.orgLoc = true;
		this.addrLoc = true;
	}

	/**
	 * Set all of the other (voice, fax, email) disclose elements to
	 * <code>true</code>.
	 */
	public void setAllOther() {
		this.voice = true;
		this.fax = true;
		this.email = true;
	}

	/**
	 * Set to include the email element. If <code>true</code> the element will be
	 * included in the disclose.
	 *
	 * @param aEmail
	 *           <code>true</code> to include the email; <code>false</code>
	 *           otherwise.
	 */
	public void setEmail(boolean aEmail) {
		this.email = aEmail;
	}

	/**
	 * Sets the contact disclose email.
	 *
	 * @param aEmail
	 *           <code>null</code> to set email element to <code>false</code>;
	 *           otherwise set email element to <code>true</code>.
	 *
	 * @deprecated Use {@link #setEmail(boolean)} instead.
	 */
	@Deprecated
	public void setEmail(String aEmail) {
		if (aEmail == null) {
			this.email = false;
		}
		else {
			this.email = true;
		}
	}

	/**
	 * Set to include the fax element. If <code>true</code> the element will be
	 * included in the disclose.
	 *
	 * @param aFax
	 *           <code>true</code> to include the fax; <code>false</code>
	 *           otherwise.
	 */
	public void setFax(boolean aFax) {
		this.fax = aFax;
	}

	/**
	 * Sets the contact disclose fax.
	 *
	 * @param aEmail
	 *           <code>null</code> to set fax element to <code>false</code>;
	 *           otherwise set fax element to <code>true</code>.
	 *
	 * @deprecated Use {@link #setFax(boolean)} instead.
	 */
	@Deprecated
	public void setFax(String aFax) {
		if (aFax == null) {
			this.fax = false;
		}
		else {
			this.fax = true;
		}
	}

	/**
	 * Sets the contact disclose flag.
	 *
	 * @param aFlag
	 *           <code>true</code> to allow disclosure as an exception to the
	 *           stated data-collection policy; <code>false</code> to not allow
	 *           disclosure of the specified elements as an exception to the
	 *           stated data-collection policy.
	 */
	public void setFlag(boolean aFlag) {
		this.flag = aFlag;
	}

	/**
	 * Sets the contact disclose flag.
	 *
	 * @param aFlag
	 *           {@link #ATTR_FLAG_TRUE} if <code>true</code> and
	 *           {@link #ATTR_FLAG_FALSE} if <code>false</code>.
	 *
	 * @deprecated Use {@link #setFlag(boolean)} instead.
	 */
	@Deprecated
	public void setFlag(String aFlag) {
		if (aFlag.equals(ATTR_FLAG_TRUE)) {
			this.flag = true;
		}
		else if (aFlag.equals(ATTR_FLAG_FALSE)) {
			this.flag = false;
		}
	}

	/**
	 * Set to include the internationalized name element. If <code>true</code>
	 * the element will be included in the disclose.
	 *
	 * @param aNameInt
	 *           <code>true</code> to include the internationalized name;
	 *           <code>false</code> otherwise.
	 */
	public void setNameInt(boolean aNameInt) {
		this.nameInt = aNameInt;
	}

	/**
	 * Set to include the localized name element. If <code>true</code> the
	 * element will be included in the disclose.
	 *
	 * @param aNameLoc
	 *           <code>true</code> to include the localized name;
	 *           <code>false</code> otherwise.
	 */
	public void setNameLoc(boolean aNameLoc) {
		this.nameLoc = aNameLoc;
	}

	/**
	 * Sets the contact disclose names.
	 *
	 * @param aNames
	 *           <code>Vector</code> of {@link EPPContactDiscloseName} instances
	 *           to set.
	 *
	 * @deprecated Use {@link #setNameInt(boolean)} and
	 *             {@link #setNameLoc(boolean)} instead.
	 */
	@Deprecated
	public void setNames(Vector aNames) {
		this.nameInt = false;
		this.nameLoc = false;

		if (aNames == null) {
			return;
		}

		for (int i = 0; i < aNames.size(); i++) {
			EPPContactDiscloseName theName = (EPPContactDiscloseName) aNames.elementAt(i);

			if (theName.getType().equals(EPPContactDiscloseName.ATTR_TYPE_INT)) {
				this.nameInt = true;
			}

			if (theName.getType().equals(EPPContactDiscloseName.ATTR_TYPE_LOC)) {
				this.nameLoc = true;
			}
		}
	}

	/**
	 * Set to include the internationalized org element. If <code>true</code> the
	 * element will be included in the disclose.
	 *
	 * @param aOrgInt
	 *           <code>true</code> to include the internationalized org;
	 *           <code>false</code> otherwise.
	 */
	public void setOrgInt(boolean aOrgInt) {
		this.orgInt = aOrgInt;
	}

	/**
	 * Set to include the localized org element. If <code>true</code> the element
	 * will be included in the disclose.
	 *
	 * @param aOrgInt
	 *           <code>true</code> to include the localized org;
	 *           <code>false</code> otherwise.
	 */
	public void setOrgLoc(boolean orgLoc) {
		this.orgLoc = orgLoc;
	}

	/**
	 * Sets the contact disclose orgs.
	 *
	 * @param aOrgs
	 *           <code>Vector</code> of {@link EPPContactDiscloseOrg} instances
	 *           to set.
	 *
	 * @deprecated Use {@link #setOrgInt(boolean)} and
	 *             {@link #setOrgLoc(boolean)} instead.
	 */
	@Deprecated
	public void setOrgs(Vector aOrgs) {
		this.orgInt = false;
		this.orgLoc = false;

		if (aOrgs == null) {
			return;
		}

		for (int i = 0; i < aOrgs.size(); i++) {
			EPPContactDiscloseOrg theOrg = (EPPContactDiscloseOrg) aOrgs.elementAt(i);

			if (theOrg.getType().equals(EPPContactDiscloseOrg.ATTR_TYPE_INT)) {
				this.orgInt = true;
			}

			if (theOrg.getType().equals(EPPContactDiscloseOrg.ATTR_TYPE_LOC)) {
				this.orgLoc = true;
			}
		}
	}

	/**
	 * Set root tag name for contact postal definition.
	 *
	 * @param aRootName
	 *           String
	 */
	public void setRootName(String aRootName) {
		this.rootName = aRootName;
	}

	/**
	 * Set to include the voice element. If <code>true</code> the element will be
	 * included in the disclose.
	 *
	 * @param aVoice
	 *           <code>true</code> to include the voice; <code>false</code>
	 *           otherwise.
	 */
	public void setVoice(boolean aVoice) {
		this.voice = aVoice;
	}

	/**
	 * Sets the contact disclose voice.
	 *
	 * @param aEmail
	 *           <code>null</code> to set voice element to <code>false</code>;
	 *           otherwise set voice element to <code>true</code>.
	 *
	 * @deprecated Use {@link #setVoice(boolean)} instead.
	 */
	@Deprecated
	public void setVoice(String aVoice) {
		if (aVoice == null) {
			this.voice = false;
		}
		else {
			this.voice = true;
		}
	}

	/**
	 * Implementation of <code>Object.toString</code>, which will result in an
	 * indented XML <code>String</code> representation of the concrete
	 * <code>EPPCodecComponent</code>.
	 *
	 * @return Indented XML <code>String</code> if successful; <code>ERROR</code>
	 *         otherwise.
	 */
	@Override
	public String toString() {
		return EPPUtil.toString(this);
	}

}
