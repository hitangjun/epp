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
 * The <code>EPPLaunchClaimKey</code> a claim key that MAY be passed to a
 * third-party trademark validator such as the Trademark Clearinghouse (TMCH)
 * for querying the information needed to generate a Trademark Claims Notice.
 * The claim key is used as the key for the query in place of the domain name to
 * securely query the service without using a well-known value like a domain
 * name. The OPTIONAL &quot;validatorID&quot; attribute is the Validator
 * Identifier whose value indicates which Trademark Validator to query for the
 * Claims Notice information, with the default being the ICANN TMCH. The
 * &quot;validatorID&quot; attribute MAY reference a non-trademark claims
 * clearinghouse identifier to support other forms of claims notices. 
 */
public class EPPLaunchClaimKey implements EPPCodecComponent {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPLaunchClaimKey.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * Constant for the claim key local name
	 */
	public static final String ELM_LOCALNAME = "claimKey";

	/**
	 * Constant for the claims check result tag
	 */
	public static final String ELM_NAME = EPPLaunchExtFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * OPTIONAL &quot;validatorID&quot; attribute name that is used to define
	 * the Validator Identifier of the Trademark Validator.
	 */
	private final static String ATTR_VALIDATOR_ID = "validatorID";

	/**
	 * Key that MAY be passed to an info service of a third party trademark
	 * provider like the Trademark Clearinghouse (TMCH) for getting the
	 * information needed to generate the trademark claims notice.
	 */
	private String claimKey;

	/**
	 * OPTIONAL &quot;validatorID&quot; attribute that is used to define the
	 * Validator Identifier of the Trademark Validator.
	 */
	private String validatorId = null;

	/**
	 * Default constructor for <code>EPPLaunchClaimKey</code>.
	 */
	public EPPLaunchClaimKey() {
	}

	/**
	 * Create a <code>EPPLaunchClaimKey</code> with the required attribute of of
	 * <code>claimKey</code>.
	 * 
	 * @param aClaimKey
	 *            Key that MAY be passed to an info service of a third party
	 *            trademark provider like the Trademark Clearinghouse (TMCH) for
	 *            getting the information needed to generate the trademark
	 *            claims notice.
	 */
	public EPPLaunchClaimKey(String aClaimKey) {
		this.claimKey = aClaimKey;
	}

	/**
	 * Create a <code>EPPLaunchClaimKey</code> with the required attribute of
	 * <code>claimKey</code>, and the optional <code>validatorId</code>
	 * attribute.
	 * 
	 * @param aClaimKey
	 *            Key that MAY be passed to an info service of a third party
	 *            trademark provider like the Trademark Clearinghouse (TMCH) for
	 *            getting the information needed to generate the trademark
	 *            claims notice.
	 * @param aValidatorId
	 *            Identifier of the Trademark Validator to query using the
	 *            <code>aClaimKey</code> value.
	 */
	public EPPLaunchClaimKey(String aClaimKey, String aValidatorId) {
		this(aClaimKey);
		this.validatorId = aValidatorId;
	}

	/**
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPLaunchClaimKey</code> instance.
	 * 
	 * @param aDocument
	 *            DOM Document that is being built. Used as an Element factory.
	 * 
	 * @return Element Root DOM Element representing the
	 *         <code>EPPLaunchClaimKey</code> instance.
	 * 
	 * @exception EPPEncodeException
	 *                Unable to encode <code>EPPLaunchClaimKey</code> instance.
	 */
	public Element encode(Document aDocument) throws EPPEncodeException {
		if (aDocument == null) {
			throw new EPPEncodeException("aDocument is null"
					+ " on in EPPLaunchClaimKey.encode(Document)");
		}

		if (this.claimKey == null) {
			throw new EPPEncodeException(
					"EPPLaunchClaimKey claimKey attribute must be set.");
		}

		Element root = aDocument.createElementNS(EPPLaunchExtFactory.NS,
				ELM_NAME);

		Text textNode = aDocument.createTextNode(this.claimKey);
		root.appendChild(textNode);

		// Validator Id
		if (this.validatorId != null) {
			root.setAttribute(ATTR_VALIDATOR_ID, this.validatorId);
		}

		return root;
	}

	/**
	 * Decode the <code>EPPLaunchClaimKey</code> attributes from the aElement
	 * DOM Element tree.
	 * 
	 * @param aElement
	 *            Root DOM Element to decode <code>EPPLaunchClaimKey</code>
	 *            from.
	 * 
	 * @exception EPPDecodeException
	 *                Unable to decode aElement.
	 */
	public void decode(Element aElement) throws EPPDecodeException {

		// Claim Key
		this.claimKey = EPPUtil.getTextContent(aElement, true);

		// Validator Id
		if (aElement.hasAttribute(ATTR_VALIDATOR_ID)) {
			this.validatorId = aElement.getAttribute(ATTR_VALIDATOR_ID);
			if (this.validatorId != null && this.validatorId.length() == 0) {
				this.validatorId = null;
			}
		}
		else {
			this.validatorId = null;
		}

	}

	/**
	 * implements a deep <code>EPPLaunchClaimKey</code> compare.
	 * 
	 * @param aObject
	 *            <code>EPPLaunchClaimKey</code> instance to compare with
	 * 
	 * @return true if equal false otherwise
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPLaunchClaimKey)) {
			cat.error("EPPLaunchClaimKey.equals(): aObject is not an EPPLaunchClaimKey");
			return false;
		}

		EPPLaunchClaimKey other = (EPPLaunchClaimKey) aObject;

		// Claim Key
		if (!EqualityUtil.equals(this.claimKey, other.claimKey)) {
			cat.error("EPPLaunchClaimKey.equals(): claimKey not equal");
			return false;
		}

		// Validator Id
		if (!EqualityUtil.equals(this.validatorId, other.validatorId)) {
			cat.error("EPPLaunchClaimKey.equals(): validatorId not equal");
			return false;
		}

		return true;
	}

	/**
	 * Clone <code>EPPLaunchClaimKey</code>.
	 * 
	 * @return clone of <code>EPPLaunchClaimKey</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPLaunchClaimKey clone = null;

		clone = (EPPLaunchClaimKey) super.clone();

		return clone;
	}

	/**
	 * Gets the key that MAY be passed to an info service of a third party
	 * trademark provider like the Trademark Clearinghouse (TMCH) for getting
	 * the information needed to generate the trademark claims notice.
	 * 
	 * @return Claim key if set; <code>null</code> otherwise.
	 */
	public String getClaimKey() {
		return this.claimKey;
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
		this.claimKey = aClaimKey;
	}

	/**
	 * Gets the OPTIONAL Validator Identifier, which is the unique identifier
	 * for the Trademark Validator to query for the Trademark Claims Notice
	 * information. If undefined, the ICANN TMCH is the Validator or if the
	 * value is &quot;tmch&quot;.
	 * 
	 * @return The Validator Identifier if defined; <code>null</code> otherwise
	 *         to indicate to use the ICANN TMCH.
	 */
	public String getValidatorId() {
		return this.validatorId;
	}

	/**
	 * Sets the OPTIONAL Validator Identifier, which is the unique identifier
	 * for the Trademark Validator to query for the Trademark Claims Notice
	 * information. A <code>null</code> value or a value of &quot;tmch&quot; can
	 * be specified to indicate the use of the ICANN TMCH.
	 * 
	 * @param aValidatorId
	 *            Validator Identifier, where <code>null</code> or a value of
	 *            &quot;tmch&quot; can be specified to indicate the use of the
	 *            ICANN TMCH.
	 */
	public void setValidatorId(String aValidatorId) {
		this.validatorId = aValidatorId;
	}

	/**
	 * Is the Validator Identifier defined?
	 * 
	 * @return <code>true</code> if the Validator Identifier is defined;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasValidatorId() {
		return (this.validatorId != null ? true : false);
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
