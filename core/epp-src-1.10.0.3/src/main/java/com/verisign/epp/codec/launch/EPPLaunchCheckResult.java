/***********************************************************
Copyright (C) 2014 VeriSign, Inc.

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
package com.verisign.epp.codec.launch;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import com.verisign.epp.codec.gen.EPPCodecComponent;
import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EqualityUtil;

/**
 * The <code>EPPLaunchCheckResult</code> represents the claims check result for
 * an individual domain name. If there is a matching trademark for the domain
 * name, then the claims check result is indicated with <code>exists</code> set
 * to </code>true</code>; otherwise set to <code>false</code>. An OPTIONAL
 * <code>claimKey</code> attribute MAY be used to query a third party trademark
 * provider like the Trademark Clearinghouse (TMCH) for getting the information
 * needed to generate the trademark claims notice.
 * 
 * @see com.verisign.epp.codec.launch.EPPLaunchChkData
 */
public class EPPLaunchCheckResult implements EPPCodecComponent {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPLaunchCheckResult.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * Constant for the claims check result local name
	 */
	public static final String ELM_LOCALNAME = "cd";

	/**
	 * Constant for the claims check result tag
	 */
	public static final String ELM_NAME = EPPLaunchExtFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/** Domain name element local name */
	private final static String ELM_DOMAIN_NAME = "name";

	/**
	 * Attribute name for the exists attribute
	 */
	private final static String ATTR_EXISTS = "exists";

	/** Domain Name associated with result. */
	private String name;

	/** Does a matching trademark exists for the domain name? */
	private boolean exists = true;

	/**
	 * Key that MAY be passed to an info service of a third party trademark
	 * provider like the Trademark Clearinghouse (TMCH) for getting the
	 * information needed to generate the trademark claims notice.
	 */
	private List<EPPLaunchClaimKey> claimKeys = new ArrayList<EPPLaunchClaimKey>();

	/**
	 * Default constructor for <code>EPPLaunchCheckResult</code>.
	 */
	public EPPLaunchCheckResult() {
	}

	/**
	 * Create a <code>EPPLaunchCheckResult</code> with the required attributes
	 * of <code>name</code> and <code>exists</code>.
	 * 
	 * @param aName
	 *            Domain name of result
	 * @param aExists
	 *            <code>true</code> if there is a matching trademark for the
	 *            domain name; <code>false</code> otherwise.
	 */
	public EPPLaunchCheckResult(String aName, boolean aExists) {
		this.name = aName;
		this.exists = aExists;
	}

	/**
	 * Create a <code>EPPLaunchCheckResult</code> with the required attributes
	 * of <code>name</code> and <code>exists</code>, and the optional
	 * <code>claimKey</code> attribute.
	 * 
	 * @param aName
	 *            Domain name of result
	 * @param aExists
	 *            <code>true</code> if there is a matching trademark for the
	 *            domain name; <code>false</code> otherwise.
	 * @param aClaimKey
	 *            Key that MAY be passed to an info service of a third party
	 *            trademark provider like the Trademark Clearinghouse (TMCH) for
	 *            getting the information needed to generate the trademark
	 *            claims notice.
	 */
	public EPPLaunchCheckResult(String aName, boolean aExists, String aClaimKey) {
		this(aName, aExists);
		this.addClaimKey(aClaimKey);
	}

	/**
	 * Create a <code>EPPLaunchCheckResult</code> with the required attributes
	 * of <code>name</code> and <code>exists</code>, and the optional
	 * <code>claimKey</code> attribute.
	 * 
	 * @param aName
	 *            Domain name of result
	 * @param aExists
	 *            <code>true</code> if there is a matching trademark for the
	 *            domain name; <code>false</code> otherwise.
	 * @param aClaimKey
	 *            Key that MAY be passed to an info service of a third party
	 *            trademark provider like the Trademark Clearinghouse (TMCH) for
	 *            getting the information needed to generate the trademark
	 *            claims notice.
	 * @param aValidatorId
	 *            Identifier of the Trademark Validator to query using the
	 *            <code>aClaimKey</code> value.
	 */
	public EPPLaunchCheckResult(String aName, boolean aExists,
			String aClaimKey, String aValidatorId) {
		this(aName, aExists);
		this.addClaimKey(aClaimKey, aValidatorId);
	}

	/**
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPLaunchCheckResult</code> instance.
	 * 
	 * @param aDocument
	 *            DOM Document that is being built. Used as an Element factory.
	 * 
	 * @return Element Root DOM Element representing the
	 *         <code>EPPLaunchCheckResult</code> instance.
	 * 
	 * @exception EPPEncodeException
	 *                Unable to encode <code>EPPLaunchCheckResult</code>
	 *                instance.
	 */
	public Element encode(Document aDocument) throws EPPEncodeException {
		if (aDocument == null) {
			throw new EPPEncodeException("aDocument is null"
					+ " on in EPPLaunchCheckResult.encode(Document)");
		}

		if (this.name == null) {
			throw new EPPEncodeException(
					"EPPLaunchCheckResult name attribute must be set.");
		}

		Element root = aDocument.createElementNS(EPPLaunchExtFactory.NS,
				ELM_NAME);

		// Name
		Element nameElm = aDocument.createElementNS(EPPLaunchExtFactory.NS,
				EPPLaunchExtFactory.NS_PREFIX + ":" + ELM_DOMAIN_NAME);
		root.appendChild(nameElm);
		Text textNode = aDocument.createTextNode(this.name);
		nameElm.appendChild(textNode);

		// Exists
		EPPUtil.encodeBooleanAttr(nameElm, ATTR_EXISTS, this.exists);

		// Claim Keys
		EPPUtil.encodeCompList(aDocument, root, this.claimKeys);

		return root;
	}

	/**
	 * Decode the <code>EPPLaunchCheckResult</code> attributes from the aElement
	 * DOM Element tree.
	 * 
	 * @param aElement
	 *            Root DOM Element to decode <code>EPPLaunchCheckResult</code>
	 *            from.
	 * 
	 * @exception EPPDecodeException
	 *                Unable to decode aElement.
	 */
	public void decode(Element aElement) throws EPPDecodeException {

		// Name
		this.name = EPPUtil.decodeString(aElement, EPPLaunchExtFactory.NS,
				ELM_DOMAIN_NAME);

		// Exists
		Element currElm = EPPUtil.getElementByTagNameNS(aElement,
				EPPLaunchExtFactory.NS, ELM_DOMAIN_NAME);
		if (currElm != null) {
			this.exists = EPPUtil.decodeBooleanAttr(currElm, ATTR_EXISTS);
		}

		// Claim Keys
		this.claimKeys = EPPUtil.decodeCompList(aElement,
				EPPLaunchExtFactory.NS, EPPLaunchClaimKey.ELM_NAME,
				EPPLaunchClaimKey.class);

	}

	/**
	 * implements a deep <code>EPPLaunchCheckResult</code> compare.
	 * 
	 * @param aObject
	 *            <code>EPPLaunchCheckResult</code> instance to compare with
	 * 
	 * @return true if equal false otherwise
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPLaunchCheckResult)) {
			cat.error("EPPLaunchCheckResult.equals(): aObject is not an EPPLaunchCheckResult");
			return false;
		}

		EPPLaunchCheckResult other = (EPPLaunchCheckResult) aObject;

		// Name
		if (!EqualityUtil.equals(this.name, other.name)) {
			cat.error("EPPLaunchCheckResult.equals(): name not equal");
			return false;
		}

		// Exists
		if (!EqualityUtil.equals(this.exists, other.exists)) {
			cat.error("EPPLaunchCheckResult.equals(): exists not equal");
			return false;
		}

		// Claim Keys
		if (!EqualityUtil.equals(this.claimKeys, other.claimKeys)) {
			cat.error("EPPLaunchCheckResult.equals(): claimKeys not equal");
			return false;
		}

		return true;
	}

	/**
	 * Clone <code>EPPLaunchCheckResult</code>.
	 * 
	 * @return clone of <code>EPPLaunchCheckResult</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPLaunchCheckResult clone = null;

		clone = (EPPLaunchCheckResult) super.clone();
		
		clone.claimKeys = new ArrayList<EPPLaunchClaimKey>();

		for (EPPLaunchClaimKey claimKey : this.claimKeys) {
			clone.claimKeys.add((EPPLaunchClaimKey) claimKey.clone());
		}

		return clone;
	}

	/**
	 * Gets the domain name associated with the result.
	 * 
	 * @return Domain name associated with the result if defined;
	 *         <code>null</code> otherwise.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the domain name associated with the result.
	 * 
	 * @param aName
	 *            Domain Name associated with the result.
	 */
	public void setName(String aName) {
		this.name = aName;
	}

	/**
	 * Gets whether a trademark exist that matches the domain name?
	 * 
	 * @return <code>true</code> if there is a matching trademark for the domain
	 *         name; <code>false</code> otherwise.
	 */
	public boolean isExists() {
		return this.exists;
	}

	/**
	 * Sets whether a trademark exist that matches the domain name?
	 * 
	 * @param aExists
	 *            <code>true</code> if there is a matching trademark for the
	 *            domain name; <code>false</code> otherwise.
	 */
	public void setExists(boolean aExists) {
		this.exists = aExists;
	}

	/**
	 * Gets the first key in the claim keys that MAY be passed to an info
	 * service of a third party trademark provider like the Trademark
	 * Clearinghouse (TMCH) for getting the information needed to generate the
	 * trademark claims notice.
	 * 
	 * @return Claim key if at least one key is set; <code>null</code>
	 *         otherwise.
	 */
	public String getClaimKey() {
		if (!this.claimKeys.isEmpty()) {
			return this.claimKeys.get(0).getClaimKey();
		}
		else {
			return null;
		}
	}

	/**
	 * Clear the claim keys set. The result will be an empty claim keys list.
	 */
	public void clearClaimKeys() {
		this.claimKeys = new ArrayList<EPPLaunchClaimKey>();
	}

	/**
	 * Sets the key that MAY be passed to an info service of a third party
	 * trademark provider like the Trademark Clearinghouse (TMCH) for getting
	 * the information needed to generate the trademark claims notice.
	 * 
	 * @param aClaimKey
	 *            Claim key
	 */
	public void setClaimKey(String aClaimKey) {
		this.clearClaimKeys();
		this.addClaimKey(aClaimKey);
	}

	/**
	 * Gets the claim keys that MAY be passed to info services of a third party
	 * trademark provider like the Trademark Clearinghouse (TMCH) for getting
	 * the information needed to generate the trademark claims notices.
	 * 
	 * @return Claim key if set; <code>null</code> otherwise.
	 */
	public List<EPPLaunchClaimKey> getClaimKeys() {
		return this.claimKeys;
	}

	/**
	 * Sets the claim keys that MAY be passed to info services of a third party
	 * trademark provider like the Trademark Clearinghouse (TMCH) for getting
	 * the information needed to generate the trademark claims notices.
	 * 
	 * @param aClaimKeys
	 *            List of claim keys
	 */
	public void setClaimKeys(List<EPPLaunchClaimKey> aClaimKeys) {
		if (aClaimKeys != null) {
			this.claimKeys = aClaimKeys;
		}
		else {
			this.clearClaimKeys();
		}
	}

	/**
	 * Adds a claims key to the list of claim keys.
	 * 
	 * @param aClaimKey
	 *            Claim key to add
	 */
	public void addClaimKey(EPPLaunchClaimKey aClaimKey) {
		this.claimKeys.add(aClaimKey);
	}

	/**
	 * Adds a claims key to the list of claim keys with a specified validator.
	 * 
	 * @param aClaimKey
	 *            Claim key to add with no specified validatorId
	 */
	public void addClaimKey(String aClaimKey) {
		this.claimKeys.add(new EPPLaunchClaimKey(aClaimKey));
	}

	/**
	 * Adds a claims key to the list of claim keys with specified Validator
	 * Identifier.
	 * 
	 * @param aClaimKey
	 *            Claim key to add with no specified Validator Identifier
	 * @param aValidatorIdentifier
	 *            Validator Identifier, where <code>null</code> or a value of
	 *            &quot;tmch&quot; can be specified to indicate the use of the
	 *            ICANN TMCH.
	 */
	public void addClaimKey(String aClaimKey, String aValidatorIdentifier) {
		this.claimKeys.add(new EPPLaunchClaimKey(aClaimKey,
				aValidatorIdentifier));
	}

	/**
	 * Gets the OPTIONAL Validator Identifier for the first claims key, which is
	 * the unique identifier for the Trademark Validator to query for the
	 * Trademark Claims Notice information. If undefined, the ICANN TMCH is the
	 * Validator or if the value is &quot;tmch&quot;.
	 * 
	 * @return The Validator Identifier if defined; <code>null</code> otherwise
	 *         to indicate to use the ICANN TMCH.
	 */
	public String getValidatorId() {
		if (!this.claimKeys.isEmpty()) {
			return this.claimKeys.get(0).getValidatorId();
		}
		else {
			return null;
		}
	}

	/**
	 * Sets the OPTIONAL Validator Identifier for the first claims key, which is
	 * the unique identifier for the Trademark Validator to query for the
	 * Trademark Claims Notice information. A <code>null</code> value or a value
	 * of &quot;tmch&quot; can be specified to indicate the use of the ICANN
	 * TMCH.
	 * 
	 * @param aValidatorId
	 *            Validator Identifier, where <code>null</code> or a value of
	 *            &quot;tmch&quot; can be specified to indicate the use of the
	 *            ICANN TMCH.
	 */
	public void setValidatorId(String aValidatorId) {
		if (!this.claimKeys.isEmpty()) {
			this.claimKeys.get(0).setValidatorId(aValidatorId);
		}
	}

	/**
	 * Is the Validator Identifier defined?
	 * 
	 * @return <code>true</code> if the Validator Identifier is defined;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasValidatorId() {
		return (this.getValidatorId() != null ? true : false);
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
