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
package com.verisign.epp.codec.nv;

import java.security.InvalidParameterException;
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
 * Represents a Real Name Verification (RNV) used in
 * {@link EPPNameVerificationCreateCmd} and the
 * {@link EPPNameVerificationInfoResp}.
 * 
 * @see com.verisign.epp.codec.nv.EPPNameVerificationCreateCmd
 * @see com.verisign.epp.codec.nv.EPPNameVerificationInfoResp
 */
public class EPPRealNameVerification implements EPPCodecComponent {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPRealNameVerification.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * XML local name for <code>EPPRealNameVerification</code>.
	 */
	public static final String ELM_LOCALNAME = "rnv";

	/**
	 * XML root tag for <code>EPPRealNameVerification</code>.
	 */
	public static final String ELM_NAME = EPPNameVerificationMapFactory.NS_PREFIX
			+ ":" + ELM_LOCALNAME;

	/**
	 * Real Name Verification (RNV) role that include:<br>
	 * <ul>
	 * <li><code>PERSON</code> - The RNV is for a person.</li>
	 * <li><code>ORG</code> - The RNV is for an organization.</li>
	 * </ul>
	 */
	public enum Role {
		PERSON("person"), ORG("org");

		private final String roleStr;

		/**
		 * Define the string value for the enumerated value.
		 * 
		 * @param aRoleStr
		 *            Enumerated value string
		 */
		Role(String aRoleStr) {
			this.roleStr = aRoleStr;
		}

		/**
		 * Get the role enumerated value given the matching string.
		 * 
		 * @param aString
		 *            <code>Role</code> enumerated string to convert to an
		 *            enumerated <code>Role</code> instance.
		 * 
		 * @return Enumerated <code>Role</code> value matching the
		 *         <code>String</code>.
		 * 
		 * @throws InvalidParameterException
		 *             If <code>aString</code> does not match an enumerated
		 *             <code>Role</code> string value.
		 */
		public static Role getRole(String aString) {
			if (aString.equals(PERSON.roleStr)) {
				return PERSON;
			}
			else if (aString.equals(ORG.roleStr)) {
				return ORG;
			}
			else {
				throw new InvalidParameterException("Role enum value of "
						+ aString + " is not valid.");
			}
		}

		/**
		 * Convert the enumerated <code>Role</code> value to a
		 * <code>String</code>.
		 */
		public String toString() {
			return this.roleStr;
		}
	}

	/**
	 * Real Name Verification (RNV) proof type include:<br>
	 * <ul>
	 * <li><code>POC</code> - Proof of Citizen, where the POC represents the
	 * citizen's identification card (ID) material.</li>
	 * <li><code>POE</code> - Proof of Enterprise, where the POE represents the
	 * Organization Code Certificate (OCC) or Business License (BL) material.</li>
	 * <li><code>POOT</code> - Proof of Other Types, in the POOT represents
	 * other certificate materials except the POC or POE.</li>
	 * </ul>
	 */
	public enum ProofType {
		POC("poc"), POE("poe"), POOT("poot");

		private final String proofTypeStr;

		/**
		 * Define the string value for the enumerated value.
		 * 
		 * @param aProofTypeStr
		 *            Enumerated value string
		 */
		ProofType(String aProofTypeStr) {
			this.proofTypeStr = aProofTypeStr;
		}

		/**
		 * Get the proof type enumerated value given the matching string.
		 * 
		 * @param aString
		 *            <code>ProofType</code> enumerated string to convert to an
		 *            enumerated <code>ProofType</code> instance.
		 * 
		 * @return Enumerated <code>ProofType</code> value matching the
		 *         <code>String</code>.
		 * 
		 * @throws InvalidParameterException
		 *             If <code>aString</code> does not match an enumerated
		 *             <code>ProofType</code> string value.
		 */
		public static ProofType getProofType(String aString) {
			if (aString.equals(POC.proofTypeStr)) {
				return POC;
			}
			else if (aString.equals(POE.proofTypeStr)) {
				return POE;
			}
			else if (aString.equals(POOT.proofTypeStr)) {
				return POOT;
			}
			else {
				throw new InvalidParameterException("Proof type enum value of "
						+ aString + " is not valid.");
			}
		}

		/**
		 * Convert the enumerated <code>ProofType</code> value to a
		 * <code>String</code>.
		 */
		public String toString() {
			return this.proofTypeStr;
		}
	}

	/**
	 * Contact for the default contact type.
	 */
	public static final String ROLE_PERSON = "registrant";

	/**
	 * XML Element Name for the <code>num</code> element.
	 */
	private final static String ELM_NUM = "num";

	/**
	 * XML Element Name for the <code>name</code> element.
	 */
	private final static String ELM_REAL_NAME = "name";

	/**
	 * XML Element Name for the <code>proofType</code> element.
	 */
	private final static String ELM_PROOF_TYPE = "proofType";

	/**
	 * The role attribute name
	 */
	private static final String ATTR_ROLE = "role";

	/**
	 * The role of the real name
	 */
	private Role role;

	/**
	 * Full name of the contact.
	 */
	private String name;

	/**
	 * Citizen or organization ID of the contact.
	 */
	private String num;

	/**
	 * Proof material type of the contact.
	 */
	private ProofType proofType;

	/**
	 * OPTIONAL list of verification documentation.
	 */
	private List<EPPNameVerificationDocument> documents = new ArrayList<EPPNameVerificationDocument>();

	/**
	 * <code>EPPRealNameVerification</code> default constructor.
	 */
	public EPPRealNameVerification() {
	}

	/**
	 * <code>EPPRealNameVerification</code> constructor the required attributes.
	 * 
	 * @param aRole
	 *            Contact role.
	 * @param aNum
	 *            Citizen or organization ID of the contact.
	 * @param aName
	 *            Real name to verify
	 * @param aProofType
	 */
	public EPPRealNameVerification(Role aRole, String aName, String aNum,
			ProofType aProofType) {
		this.role = aRole;
		this.name = aName;
		this.num = aNum;
		this.proofType = aProofType;
	}

	/**
	 * <code>EPPRealNameVerification</code> constructor that takes all
	 * attributes with a single document.
	 * 
	 * * @param aType Contact type with default of <code>TYPE_REGISTRANT</code>.
	 * 
	 * @param aRole
	 *            Role of the contact
	 * @param aName
	 *            Full name of the contact
	 * @param aNum 
	 *            Citizen or the organization ID of the contact.
	 * @param aProofType 
	 *            Proof material type of the contact.
	 * @param aDocument 
	 *            Single proof document
	 */
	public EPPRealNameVerification(Role aRole, String aName, String aNum,
			ProofType aProofType, EPPNameVerificationDocument aDocument) {
		this.role = aRole;
		this.name = aName;
		this.num = aNum;
		this.proofType = aProofType;
		this.addDocument(aDocument);
	}
	
	/**
	 * <code>EPPRealNameVerification</code> constructor that takes all
	 * attributes.
	 * 
	 * * @param aType Contact type with default of <code>TYPE_REGISTRANT</code>.
	 * 
	 * @param aRole
	 *            Role of the contact
	 * @param aName
	 *            Full name of the contact
	 * @param aNum 
	 *            Citizen or the organization ID of the contact.
	 * @param aProofType 
	 *            Proof material type of the contact.
	 * @param aDocuments 
	 *            List of proof documents
	 */
	public EPPRealNameVerification(Role aRole, String aName, String aNum,
			ProofType aProofType, List<EPPNameVerificationDocument> aDocuments) {
		this.role = aRole;
		this.name = aName;
		this.num = aNum;
		this.proofType = aProofType;
		this.setDocuments(aDocuments);
	}

	/**
	 * Gets the contact role.
	 * 
	 * return Contact role
	 */
	public Role getRole() {
		return this.role;
	}

	/**
	 * Sets the contact role.
	 * 
	 * @param aRole
	 *            Contact role
	 */
	public void setRole(Role aRole) {
		this.role = aRole;
	}

	/**
	 * Gets the citizen or the organization ID of the contact.
	 * 
	 * @return The citizen or the organization ID of the contact.
	 */
	public String getNum() {
		return this.num;
	}

	/**
	 * Sets the citizen or the organization ID of the contact.
	 * 
	 * @param aNum
	 *            Citizen or the organization ID of the contact.
	 */
	public void setNum(String aNum) {
		this.num = aNum;
	}

	/**
	 * Gets the full name of the contact
	 * 
	 * @return Full name of the contact
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the full name of the contact.
	 * 
	 * @param aName
	 *            Full name of the contact
	 */
	public void setName(String aName) {
		this.name = aName;
	}

	/**
	 * Gets the proof material type of the contact.
	 * 
	 * @return proof material type of the contact
	 */
	public ProofType getProofType() {
		return this.proofType;
	}

	/**
	 * Sets the proof material type of the contact.
	 * 
	 * @param aProofType
	 *            proof material type of the contact
	 */
	public void setProofType(ProofType aProofType) {
		this.proofType = aProofType;
	}

	/**
	 * Are there any verification documents?
	 * 
	 * @return <code>true</code> if there are documents; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasDocuments() {
		return !this.documents.isEmpty();
	}

	/**
	 * Gets the verification documents.
	 * 
	 * @return Verification documents
	 */
	public List<EPPNameVerificationDocument> getDocuments() {
		return this.documents;
	}

	/**
	 * Sets the verification documents.
	 * 
	 * @param aDocuments
	 *            Verification documents
	 */
	public void setDocuments(List<EPPNameVerificationDocument> aDocuments) {
		this.documents = aDocuments;
	}

	/**
	 * Adds a document to the list of verification documents.
	 * 
	 * @param aDocument
	 *            Document to add to the list of verification documents.
	 */
	public void addDocument(EPPNameVerificationDocument aDocument) {
		this.documents.add(aDocument);
	}

	/**
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPRealNameVerification</code> instance.
	 * 
	 * @param aDocument
	 *            DOM Document that is being built. Used as an Element factory.
	 * 
	 * @return Element Root DOM Element representing the
	 *         <code>EPPRealNameVerification</code> instance.
	 * 
	 * @exception EPPEncodeException
	 *                Unable to encode <code>EPPRealNameVerification</code>
	 *                instance.
	 */
	@Override
	public Element encode(Document aDocument) throws EPPEncodeException {

		// Check required attributes
		if (this.role == null) {
			throw new EPPEncodeException(
					"Undefined role in EPPRealNameVerification");
		}
		if (this.name == null) {
			throw new EPPEncodeException(
					"Undefined name in EPPRealNameVerification");
		}
		if (this.num == null) {
			throw new EPPEncodeException(
					"Undefined num in EPPRealNameVerification");
		}
		if (this.proofType == null) {
			throw new EPPEncodeException(
					"Undefined proofType in EPPRealNameVerification");
		}

		// Create root element
		Element root = aDocument.createElementNS(
				EPPNameVerificationMapFactory.NS, ELM_NAME);

		// Role
		root.setAttribute(ATTR_ROLE, this.role.toString());

		// Name
		EPPUtil.encodeString(aDocument, root, this.name,
				EPPNameVerificationMapFactory.NS,
				EPPNameVerificationMapFactory.NS_PREFIX + ":" + ELM_REAL_NAME);

		// Num
		EPPUtil.encodeString(aDocument, root, this.num,
				EPPNameVerificationMapFactory.NS,
				EPPNameVerificationMapFactory.NS_PREFIX + ":" + ELM_NUM);

		// Proof Type
		EPPUtil.encodeString(aDocument, root, this.proofType.toString(),
				EPPNameVerificationMapFactory.NS,
				EPPNameVerificationMapFactory.NS_PREFIX + ":" + ELM_PROOF_TYPE);

		// Documents
		if (this.hasDocuments()) {
			EPPUtil.encodeCompList(aDocument, root, this.documents);
		}

		return root;
	}

	/**
	 * Decode the <code>EPPRealNameVerification</code> attributes from the
	 * aElement DOM Element tree.
	 * 
	 * @param aElement
	 *            Root DOM Element to decode
	 *            <code>EPPRealNameVerification</code> from.
	 * 
	 * @exception EPPDecodeException
	 *                Unable to decode aElement
	 */
	@Override
	public void decode(Element aElement) throws EPPDecodeException {

		// Role
		this.role = Role.getRole(aElement.getAttribute(ATTR_ROLE));

		// Name
		this.name = EPPUtil.decodeString(aElement,
				EPPNameVerificationMapFactory.NS, ELM_REAL_NAME);

		// Num
		this.num = EPPUtil.decodeString(aElement,
				EPPNameVerificationMapFactory.NS, ELM_NUM);

		// Proof Type
		String theProofTypeStr = EPPUtil.decodeString(aElement,
				EPPNameVerificationMapFactory.NS, ELM_PROOF_TYPE);
		this.proofType = ProofType.getProofType(theProofTypeStr);

		// Documents
		this.documents = EPPUtil.decodeCompList(aElement,
				EPPNameVerificationMapFactory.NS,
				EPPNameVerificationDocument.ELM_NAME,
				EPPNameVerificationDocument.class);
	}

	/**
	 * Compare an instance of <code>EPPRealNameVerification</code> with this
	 * instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return {@code true} if this object is the same as the aObject argument;
	 *         {@code false} otherwise.
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPRealNameVerification)) {
			cat.error("EPPRealNameVerification.equals(): object "
					+ aObject.getClass().getName()
					+ "!= EPPRealNameVerification");
			return false;
		}

		EPPRealNameVerification other = (EPPRealNameVerification) aObject;

		// Role
		if (!EqualityUtil.equals(this.role, other.role)) {
			cat.error("EPPRealNameVerification.equals(): role not equal");
			return false;
		}

		// Name
		if (!EqualityUtil.equals(this.name, other.name)) {
			cat.error("EPPRealNameVerification.equals(): name not equal");
			return false;
		}

		// Num
		if (!EqualityUtil.equals(this.num, other.num)) {
			cat.error("EPPRealNameVerification.equals(): num not equal");
			return false;
		}

		// Proof Type
		if (!EqualityUtil.equals(this.proofType, other.proofType)) {
			cat.error("EPPRealNameVerification.equals(): proofType not equal");
			return false;
		}

		// Documents
		if (!EqualityUtil.equals(this.documents, other.documents)) {
			cat.error("EPPRealNameVerification.equals(): documents not equal");
			return false;
		}

		return true;
	}

	/**
	 * Clone <code>EPPRealNameVerification</code>.
	 * 
	 * @return Deep copy clone of <code>EPPRealNameVerification</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPRealNameVerification clone = (EPPRealNameVerification) super.clone();

		// Profiles
		clone.documents = new ArrayList<EPPNameVerificationDocument>(
				this.documents.size());
		for (EPPNameVerificationDocument document : this.documents)
			clone.documents.add((EPPNameVerificationDocument) document.clone());

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
