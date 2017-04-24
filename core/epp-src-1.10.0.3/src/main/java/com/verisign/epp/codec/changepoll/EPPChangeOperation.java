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
 ***********************************************************/
package com.verisign.epp.codec.changepoll;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import com.verisign.epp.codec.gen.EPPCodecComponent;
import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EqualityUtil;

/**
 * Transform operation executed on the object. The operation value should match
 * one of the <code>OPERATION</code> constants and the &quot;op&quot; attribute
 * can be set via the <code>{@link #setOp(String)} method
 * to handle specific cases.
 */
public class EPPChangeOperation implements EPPCodecComponent {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPChangeOperation.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * Create operation as defined in <code>RFC 5730</code>.
	 */
	public static final String OPERATION_CREATE = "create";

	/**
	 * Delete operation as defined in <code>RFC 5730</code>.
	 */
	public static final String OPERATION_DELETE = "delete";

	/**
	 * Renew operation as defined in <code>RFC 5730</code>.
	 */
	public static final String OPERATION_RENEW = "renew";

	/**
	 * Transfer operation as defined in <code>RFC 5730</code>.
	 */
	public static final String OPERATION_TRANSFER = "transfer";

	/**
	 * Update operation as defined in <code>RFC 5730</code>.
	 */
	public static final String OPERATION_UPDATE = "update";

	/**
	 * Update operation as defined in <code>RFC 3915</code>.
	 */
	public static final String OPERATION_RESTORE = "restore";

	/**
	 * Auto renew operation executed by the server.
	 */
	public static final String OPERATION_AUTO_RENEW = "autoRenew";

	/**
	 * Auto delete operation executed by the server.
	 */
	public static final String OPERATION_AUTO_DELETE = "autoDelete";

	/**
	 * Auto purge operation executed by the server.
	 */
	public static final String OPERATION_AUTO_PURGE = "autoPurge";

	/**
	 * Custom operation.
	 */
	public static final String OPERATION_CUSTOM = "custom";

	/**
	 * Constant for the phase local name
	 */
	public static final String ELM_LOCALNAME = "operation";

	/**
	 * Constant for the phase qualified name (prefix and local name)
	 */
	public static final String ELM_NAME = EPPChangePollExtFactory.NS_PREFIX
			+ ":" + ELM_LOCALNAME;

	/**
	 * Name of XML attribute for the &quot;op&quot; attribute.
	 */
	private static final String ATTR_OP = "op";

	/**
	 * Operation value. This value is required.
	 */
	private String value;

	/**
	 * OPTIONAL "op" attribute.
	 */
	private String op;

	/**
	 * Default constructor. The operation value MUST be set using the
	 * {@link #setValue(String)} method.
	 */
	public EPPChangeOperation() {
	}

	/**
	 * Create <code>EPPChangeOperation</code> instance with a defined operation
	 * value.
	 * 
	 * @param aValue
	 *            Operation value using one of the <code>OPERATION</code>
	 *            constants.
	 */
	public EPPChangeOperation(String aValue) {
		this.value = aValue;
	}

	/**
	 * Create <code>EPPChangeOperation</code> instance with a defined operation
	 * value and the optional &quot;op&quot; attribute that defines either the
	 * sub-operation or the name of the operation when the operation value is
	 * set to <code>OPERATION_CUSTOM</code>.
	 * 
	 * @param aValue
	 *            Operation value using one of the <code>OPERATION</code>
	 *            constants.
	 * @param aOp
	 *            Sub-operation or name of operation when <code>aValue</code> is
	 *            set to <code>OPERATION_CUSTOM</code>.
	 */
	public EPPChangeOperation(String aValue, String aOp) {
		this.value = aValue;
		this.op = aOp;
	}

	/**
	 * Is the operation value defined?
	 * 
	 * @return <code>true</code> if the operation value is defined;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasValue() {
		return (this.value != null ? true : false);
	}

	/**
	 * Gets the operation value, which should be one of the
	 * <code>OPERATION</code> constants.
	 * 
	 * @return Operation value if defined; <code>null</code> otherwise.
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Sets the operation value.
	 * 
	 * @param aValue
	 *            Operation value, which should be one of the
	 *            <code>OPERATION</code> constants.
	 */
	public void setValue(String aValue) {
		this.value = aValue;
	}

	/**
	 * Is the &quot;op&quot; attribute defined?
	 * 
	 * @return <code>true</code> if the &quot;op&quot; attribute is defined;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasOp() {
		return (this.op != null ? true : false);
	}

	/**
	 * Gets the OPTIONAL &quot;op&quot; attribute value, which defines the
	 * sub-operation or name of the operation when the operation value is set to
	 * <code>OPERATION_CUSTOM</code>.
	 * 
	 * @return The &quot;op&quot; attribute value if defined; <code>null</code>
	 *         otherwise.
	 */
	public String getOp() {
		return this.op;
	}

	/**
	 * Sets the OPTIONAL &quot;op&quot; attribute value.
	 * 
	 * @param aOp
	 *            &quot;op&quot; attribute value that defines the sub-operation
	 *            or name of the operation when the operation value is set to
	 *            <code>OPERATION_CUSTOM</code>.
	 */
	public void setOp(String aOp) {
		this.op = aOp;
	}

	/**
	 * Clone <code>EPPChangeOperation</code> instance.
	 *
	 * @return clone of <code>EPPChangeOperation</code>
	 *
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPChangeOperation clone = null;

		clone = (EPPChangeOperation) super.clone();

		return clone;
	}

	/**
	 * Decode the <code>EPPChangeOperation</code> element aElement DOM Element
	 * tree.
	 *
	 * @param aElement
	 *            - Root DOM Element to decode <code>EPPChangeOperation</code>
	 *            from.
	 *
	 * @exception EPPDecodeException
	 *                Unable to decode aElement
	 */
	public void decode(Element aElement) throws EPPDecodeException {
		// Op
		String theOp = aElement.getAttribute(ATTR_OP);
		if (theOp != null && !theOp.isEmpty()) {
			this.op = theOp;
		}
		else {
			this.op = null;
		}

		// Value
		Node textNode = aElement.getFirstChild();

		if (textNode != null) {
			this.value = textNode.getNodeValue();
		}
	}

	/**
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPChangeOperation</code> instance.
	 *
	 * @param aDocument
	 *            - DOM Document that is being built. Used as an Element
	 *            factory.
	 *
	 * @return Element - Root DOM Element representing the
	 *         <code>EPPChangeOperation</code> instance.
	 *
	 * @exception EPPEncodeException
	 *                - Unable to encode <code>EPPChangeOperation</code>
	 *                instance.
	 */
	public Element encode(Document aDocument) throws EPPEncodeException {

		if (aDocument == null) {
			throw new EPPEncodeException("aDocument is null"
					+ " on in EPPChangeOperation.encode(Document)");
		}
		if (this.value == null) {
			throw new EPPEncodeException("value is null"
					+ " on in EPPChangeOperation.encode(Document)");
		}

		Element root = aDocument.createElementNS(EPPChangePollExtFactory.NS,
				ELM_NAME);

		// Op
		if (this.op != null) {
			root.setAttribute(ATTR_OP, this.op);
		}

		// Value
		Text phaseText = aDocument.createTextNode(this.value);
		root.appendChild(phaseText);

		return root;
	}

	/**
	 * Implements a deep <code>EPPChangeOperation</code> compare.
	 *
	 * @param aObject
	 *            <code>EPPChangeOperation</code> instance to compare with
	 *
	 * @return <code>true</code> if equal; <code>false</code> otherwise
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPChangeOperation)) {
			return false;
		}

		EPPChangeOperation other = (EPPChangeOperation) aObject;

		// Op
		if (!EqualityUtil.equals(this.op, other.op)) {
			cat.error("EPPChangeOperation.equals(): op not equal");
			return false;
		}

		// Value
		if (!EqualityUtil.equals(this.value, other.value)) {
			cat.error("EPPChangeOperation.equals(): value not equal");
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
