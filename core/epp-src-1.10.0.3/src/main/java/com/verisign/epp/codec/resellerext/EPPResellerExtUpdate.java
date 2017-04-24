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

package com.verisign.epp.codec.resellerext;

import java.security.InvalidParameterException;

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
 * <code>EPPResellerExtUpdate</code> is used in the extension to the update
 * command to manage the reseller identifier for the object.
 */
public class EPPResellerExtUpdate implements EPPCodecComponent {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPResellerExtUpdate.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * XML local name for <code>EPPResellerExtUpdate</code>.
	 */
	public static final String ELM_LOCALNAME = "update";

	/**
	 * XML root tag for <code>EPPResellerExtUpdate</code>.
	 */
	public static final String ELM_NAME = EPPResellerExtFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * Update action, which includes:<br>
	 * <ol>
	 * <li>ADD - Add the reseller identifier.</li>
	 * <li>REM - Remove the reseller identifier.</li>
	 * <li>CHG - Change the reseller identifier.</li>
	 * </ol>
	 */
	public enum Action {
		ADD("add"), REM("rem"), CHG("chg");

		private final String localElm;

		/**
		 * Define the local element name of the action.
		 * 
		 * @param aLocalElm
		 *            Local element name of action
		 */
		Action(String aLocalElm) {
			this.localElm = aLocalElm;
		}

		/**
		 * Get the action given the local element name.
		 * 
		 * @param aLocalElm
		 *            <code>Action</code> enumerated local element name to
		 *            convert to an enumerated <code>Action</code> instance.
		 * 
		 * @return Enumerated <code>Action</code> value matching the
		 *         <code>aLocalElm</code> value.
		 * 
		 * @throws InvalidParameterException
		 *             If <code>aLocalElm</code> does not match an enumerated
		 *             <code>Action</code> string value.
		 */
		public static Action getAction(String aLocalElm) {
			if (aLocalElm == null || aLocalElm.isEmpty()) {
				throw new InvalidParameterException(
						"Action null or empty enum value of is not valid.");
			}
			if (aLocalElm.equals(ADD.localElm)) {
				return ADD;
			}
			else if (aLocalElm.equals(REM.localElm)) {
				return REM;
			}
			else if (aLocalElm.equals(CHG.localElm)) {
				return CHG;
			}
			else {
				throw new InvalidParameterException("Action enum value of "
						+ aLocalElm + " is not valid.");
			}
		}

		/**
		 * Convert the enumerated <code>Action</code> value to a local element
		 * name <code>String</code>.
		 */
		public String toString() {
			return this.localElm;
		}
	}

	/**
	 * XML local name for the reseller identifier element
	 */
	private static final String ELM_RESELLER_ID = "id";

	/**
	 * Update action to take
	 */
	private Action action;

	/**
	 * The reseller identifier of the reseller of a sponsoring registrar.
	 */
	private String resellerId;

	/**
	 * Default constructor for <code>EPPResellerExtUpdate</code>.
	 */
	public EPPResellerExtUpdate() {
	}

	/**
	 * Constructor for <code>EPPResellerExtUpdate</code> that takes the action
	 * and the reseller identifier.
	 * 
	 * @param aAction
	 *            Update action to take using the <code>Action</code> enumerated
	 *            values.
	 * @param aResellerId
	 *            Reseller identifier
	 */
	public EPPResellerExtUpdate(Action aAction, String aResellerId) {
		this.setAction(aAction);
		this.setResellerId(aResellerId);
	}

	/**
	 * Gets the update action to take.
	 * 
	 * @return the update action using the <code>Action</code> enumerated
	 *         values.
	 */
	public Action getAction() {
		return this.action;
	}

	/**
	 * Sets the update action to take.
	 * 
	 * @param aAction
	 *            Update action using the <code>Action</code> enumerated values.
	 */
	public void setAction(Action aAction) {
		this.action = aAction;
	}

	/**
	 * Gets the reseller identifier.
	 * 
	 * @return The reseller identifier if defined;<code>null</code> otherwise.
	 */
	public String getResellerId() {
		return this.resellerId;
	}

	/**
	 * Sets the reseller identifier.
	 * 
	 * @param aResellerId
	 *            The reseller identifier
	 */
	public void setResellerId(String aResellerId) {
		this.resellerId = aResellerId;
	}

	/**
	 * encode instance into a DOM element tree. A DOM Document is passed as an
	 * argument and functions as a factory for DOM objects. The root element
	 * associated with the instance is created and each instance attribute is
	 * appended as a child node.
	 * 
	 * @param aDocument
	 *            DOM Document, which acts is an Element factory
	 * 
	 * @return Element Root element associated with the object
	 * 
	 * @exception EPPEncodeException
	 *                Error encoding <code>EPPResellerExtUpdate</code>
	 */
	public Element encode(Document aDocument) throws EPPEncodeException {

		// Check required attributes
		if (this.action == null) {
			throw new EPPEncodeException(
					"Undefined action in EPPResellerExtUpdate");
		}
		if (this.resellerId == null) {
			throw new EPPEncodeException(
					"Undefined resellerId in EPPResellerExtUpdate");
		}

		// Create root element
		Element root = aDocument.createElementNS(EPPResellerExtFactory.NS,
				ELM_NAME);

		// Action
		Element action = aDocument.createElementNS(EPPResellerExtFactory.NS,
				EPPResellerExtFactory.NS_PREFIX + ":" + this.action);
		root.appendChild(action);

		// Reseller Identifier
		EPPUtil.encodeString(aDocument, action, this.resellerId,
				EPPResellerExtFactory.NS, EPPResellerExtFactory.NS_PREFIX + ":"
						+ ELM_RESELLER_ID);

		return root;
	}

	/**
	 * decode a DOM element tree to initialize the instance attributes. The
	 * <code>aElement</code> argument represents the root DOM element and is
	 * used to traverse the DOM nodes for instance attribute values.
	 * 
	 * @param aElement
	 *            <code>Element</code> to decode
	 * 
	 * @exception EPPDecodeException
	 *                Error decoding <code>Element</code>
	 */
	public void decode(Element aElement) throws EPPDecodeException {

		// Action
		Element actionElm = EPPUtil.getFirstElementChild(aElement);
		if (actionElm == null) {
			throw new EPPDecodeException("Action element not found");
		}
		this.action = Action.getAction(actionElm.getLocalName());

		// Reseller Identifier
		this.resellerId = EPPUtil.decodeString(actionElm,
				EPPResellerExtFactory.NS, ELM_RESELLER_ID);
	}

	/**
	 * clone an <code>EPPCodecComponent</code>.
	 * 
	 * @return clone of concrete <code>EPPResellerExt</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPResellerExtUpdate clone = (EPPResellerExtUpdate) super.clone();

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
	 * Compare an instance of <code>EPPResellerExt</code> with this instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return <code>true</code> if equal; <code>false</code> otherwise.
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPResellerExtUpdate)) {
			return false;
		}

		EPPResellerExtUpdate other = (EPPResellerExtUpdate) aObject;

		// Action
		if (!EqualityUtil.equals(this.action, other.action)) {
			cat.error("EPPResellerExtUpdate.equals(): action not equal");
			return false;
		}

		// Reseller Identifier
		if (!EqualityUtil.equals(this.resellerId, other.resellerId)) {
			cat.error("EPPResellerExtUpdate.equals(): resellerId not equal");
			return false;
		}

		return true;
	}

}
