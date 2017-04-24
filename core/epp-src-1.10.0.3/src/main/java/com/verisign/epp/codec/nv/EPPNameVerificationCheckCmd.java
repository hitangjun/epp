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
package com.verisign.epp.codec.nv;

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
import com.verisign.epp.util.EqualityUtil;

/**
 * The <code>EPPNameVerificationCheckCmd</code> is used for the Name
 * Verification (DNV) Check Command, that is used to determine if the domain
 * label's can be used to create a Domain Name Verification object. It provides
 * a hint that allows a client to anticipate the success or failure of create a
 * DNV object using the create command.
 * 
 * @see com.verisign.epp.codec.nv.EPPNameVerificationCheckResp
 */
public class EPPNameVerificationCheckCmd extends EPPCheckCmd {
	/** Maximum number of labels to check at once. */
	public static final int MAX_LABELS = 99;

	/**
	 * Constant for the name verification check command
	 */
	public static final String ELM_LOCALNAME = "check";

	/**
	 * XML Element Name of <code>EPPNameVerificationCheckCmd</code> root
	 * element.
	 */
	final static String ELM_NAME = EPPNameVerificationMapFactory.NS_PREFIX
			+ ":" + ELM_LOCALNAME;

	/** XML Element Name for the label <code>name</code> element. */
	private final static String ELM_LABEL_NAME = "name";

	/** Log4j category for logging */
	private static Logger cat = Logger.getLogger(
			EPPNameVerificationCheckCmd.class.getName(), EPPCatFactory
					.getInstance().getFactory());

	/**
	 * Labels to check.
	 */
	private List<String> labels = new ArrayList<String>();

	/**
	 * <code>EPPNameVerificationCheckCmd</code> default constructor.
	 */
	public EPPNameVerificationCheckCmd() {
	}

	/**
	 * <code>EPPNameVerificationCheckCmd</code> constructor with just the 
	 * client transaction identifier.
	 *
	 * @param aTransId
	 *            Transaction Id associated with command.
	 */
	public EPPNameVerificationCheckCmd(String aTransId) {
		super(aTransId);
	}
	
	/**
	 * <code>EPPNameVerificationCheckCmd</code> constructor that will check an
	 * individual label.
	 *
	 * @param aTransId
	 *            Transaction Id associated with command.
	 * @param aName
	 *            Label name to check
	 */
	public EPPNameVerificationCheckCmd(String aTransId, String aName) {
		super(aTransId);
		this.labels.add(aName);
	}

	/**
	 * <code>EPPNameVerificationCheckCmd</code> constructor that will check a
	 * list of labels.
	 *
	 * @param aTransId
	 *            Transaction Id associated with command.
	 * @param aNames
	 *            <code>List</code> of labels to check
	 */
	public EPPNameVerificationCheckCmd(String aTransId, List<String> aNames) {
		super(aTransId);
		this.labels = aNames;
	}

	/**
	 * Gets the EPP command Namespace associated with
	 * <code>EPPNameVerificationCheckCmd</code>.
	 *
	 * @return <code>EPPNameVerificationMapFactory.NS</code>
	 */
	public String getNamespace() {
		return EPPNameVerificationMapFactory.NS;
	}

	/**
	 * Compare an instance of <code>EPPNameVerificationCheckCmd</code> with this
	 * instance.
	 *
	 * @param aObject
	 *            Object to compare with.
	 *
	 * @return <code>true</code> if equal; <code>false</code> otherwise.
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPNameVerificationCheckCmd)) {
			return false;
		}

		if (!super.equals(aObject)) {
			return false;
		}

		EPPNameVerificationCheckCmd other = (EPPNameVerificationCheckCmd) aObject;

		// Domain Names
		if (!EqualityUtil.equals(this.labels, other.labels)) {
			cat.error("EPPNameVerificationCheckCmd.equals(): names not equal");
			return false;
		}

		return true;
	}

	/**
	 * Clone <code>EPPNameVerificationCheckCmd</code>.
	 *
	 * @return Deep copy clone of <code>EPPNameVerificationCheckCmd</code>
	 *
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPNameVerificationCheckCmd clone = (EPPNameVerificationCheckCmd) super
				.clone();

		clone.labels = new ArrayList<String>(this.labels.size());
		for (String item : this.labels)
			clone.labels.add(item);

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
	 * Sets label to check.
	 *
	 * @param aName
	 *            Label to check.
	 */
	public void setLabel(String aName) {
		this.labels = new ArrayList<String>();
		this.labels.add(aName);
	}

	/**
	 * Gets labels to check.
	 *
	 * @return List of labels <code>String</code>'s.
	 */
	public List<String> getLabels() {
		return this.labels;
	}

	/**
	 * Sets labels to check.
	 *
	 * @param aLabels
	 *            Labels to check.
	 */
	public void setLabels(List<String> aLabels) {
		this.labels = aLabels;
	}

	/**
	 * Add a label to the list of labels.
	 * 
	 * @param aLabel
	 *            Label to add
	 */
	public void addLabel(String aLabel) {
		this.labels.add(aLabel);
	}

	/**
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPNameVerificationCheckCmd</code> instance.
	 *
	 * @param aDocument
	 *            DOM Document that is being built. Used as an Element factory.
	 *
	 * @return Element Root DOM Element representing the
	 *         <code>EPPNameVerificationCheckCmd</code> instance.
	 *
	 * @exception EPPEncodeException
	 *                Unable to encode <code>EPPNameVerificationCheckCmd</code>
	 *                instance.
	 */
	protected Element doEncode(Document aDocument) throws EPPEncodeException {
		if (this.labels.size() == 0) {
			throw new EPPEncodeException(
					"No labels specified in EPPNameVerificationCheckCmd");
		}

		if (this.labels.size() > MAX_LABELS) {
			throw new EPPEncodeException(this.labels.size()
					+ " labels is greater than the maximum of " + MAX_LABELS);
		}

		Element root = aDocument.createElementNS(
				EPPNameVerificationMapFactory.NS, ELM_NAME);

		// Labels
		EPPUtil.encodeStringList(aDocument, root, this.labels,
				EPPNameVerificationMapFactory.NS,
				EPPNameVerificationMapFactory.NS_PREFIX + ":" + ELM_LABEL_NAME);

		return root;
	}

	/**
	 * Decode the <code>EPPNameVerificationCheckCmd</code> attributes from the
	 * aElement DOM Element tree.
	 *
	 * @param aElement
	 *            Root DOM Element to decode
	 *            <code>EPPNameVerificationCheckCmd</code> from.
	 *
	 * @exception EPPDecodeException
	 *                Unable to decode aElement
	 */
	protected void doDecode(Element aElement) throws EPPDecodeException {
		// Labels
		this.labels = EPPUtil.decodeList(aElement,
				EPPNameVerificationMapFactory.NS, ELM_LABEL_NAME);
	}

}
