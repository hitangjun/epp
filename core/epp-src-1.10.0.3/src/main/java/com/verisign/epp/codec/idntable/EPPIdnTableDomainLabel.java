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

package com.verisign.epp.codec.idntable;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.verisign.epp.codec.gen.EPPCodecComponent;
import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EqualityUtil;

/**
 * <code>EPPIdnTableDomainLabel</code> is used to represent a domain name label
 * used in the Domain Check Form and the Domain Info Form. The domain label
 * consists of the domain name plus an optional &quot;form&quot; attribute that
 * defines whether the domain name is an A-label or U-label, with the default
 * being A-label.
 */
public class EPPIdnTableDomainLabel implements EPPCodecComponent {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPIdnTableDomainLabel.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * XML local name for <code>EPPIdnTableDomainLabel</code>.
	 */
	public static final String ELM_LOCALNAME = "domain";

	/**
	 * XML root tag for <code>EPPIdnTableDomainLabel</code>.
	 */
	public static final String ELM_NAME = EPPIdnTableMapFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * Label form types, that include:<br>
	 * <ul>
	 * <li><code>A_LABEL</code> A-label domain name label.</li>
	 * <li><code>U_LABEL</code> U-label domain name label.</li>
	 */
	public enum Form {
		A_LABEL, U_LABEL
	}
	
	public static final String FORM_A_LABEL = "aLabel";

	public static final String FORM_U_LABEL = "uLabel";

	/**
	 * XML local name for the uname element
	 */
	private static final String ATTR_FORM = "form";

	/**
	 * Value for the domain name
	 */
	private String domain;

	/**
	 * OPTIONAL form with the default value of <code>Form.A_LABEL</code>
	 */
	private Form form = Form.A_LABEL;

	/**
	 * Default constructor for <code>EPPIdnTableDomainLabel</code>.
	 */
	public EPPIdnTableDomainLabel() {
	}

	/**
	 * Constructor for <code>EPPIdnTableDomainLabel</code> that takes the domain
	 * name.
	 * 
	 * @param aDomain
	 *            Domain name
	 */
	public EPPIdnTableDomainLabel(String aDomain) {
		this.setDomain(aDomain);
	}

	/**
	 * Constructor for <code>EPPIdnTableDomainLabel</code> that takes the domain
	 * name and the form.
	 * 
	 * @param aDomain
	 *            Domain name
	 * @param aForm
	 *            Either <code>Form.A_LABEL</code> or </code>Form.U_LABEL</code>
	 */
	public EPPIdnTableDomainLabel(String aDomain, Form aForm) {
		this.setDomain(aDomain);
		this.setForm(aForm);
	}

	/**
	 * Returns the domain name.
	 * 
	 * @return Domain name if defined: <code>null</code> otherwise.
	 */
	public String getDomain() {
		return this.domain;
	}

	/**
	 * Sets the domain name.
	 * 
	 * @param aDomain
	 *            Domain name
	 */
	public void setDomain(String aDomain) {
		this.domain = aDomain;
	}

	/**
	 * Gets the form of the domain name that should be either
	 * <code>Form.A_LABEL</code> or </code>Form.U_LABEL</code>.
	 * 
	 * @return Either <code>Form.A_LABEL</code> or </code>Form.U_LABEL</code>
	 */
	public Form getForm() {
		return this.form;
	}

	/**
	 * Sets the form of the domain name that should be either
	 * <code>Form.A_LABEL</code> or </code>Form.U_LABEL</code>.
	 * 
	 * @param aForm
	 *            Either <code>Form.A_LABEL</code> or </code>Form.U_LABEL</code>
	 */
	public void setForm(Form aForm) {
		this.form = aForm;
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
	 *                Error encoding <code>EPPIdnTableDomainLabel</code>
	 */
	public Element encode(Document aDocument) throws EPPEncodeException {

		// Check required attributes
		if (this.domain == null) {
			throw new EPPEncodeException(
					"Undefined domain in EPPIdnTableDomainLabel");
		}

		// Domain
		Element root = aDocument.createElementNS(EPPIdnTableMapFactory.NS,
				ELM_NAME);

		root.appendChild(aDocument.createTextNode(this.domain));

		// Form
		switch (this.form) {
			case A_LABEL:
				root.setAttribute(ATTR_FORM, FORM_A_LABEL);
				break;
			case U_LABEL:
				root.setAttribute(ATTR_FORM, FORM_U_LABEL);
				break;
		}

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

		if (aElement != null) {
			// Domain
			Node textNode = aElement.getFirstChild();

			if (textNode != null) {
				this.domain = textNode.getNodeValue();
			}
			else {
				this.domain = null;
			}

			// Form
			String formAttr = aElement.getAttribute(ATTR_FORM);
			if ((formAttr == null) || formAttr.equals(FORM_A_LABEL)) {
				this.form = Form.A_LABEL;
			}
			else {
				this.form = Form.U_LABEL;
			}
		}
	}

	/**
	 * clone an <code>EPPCodecComponent</code>.
	 * 
	 * @return clone of concrete <code>EPPIdnTableDomainLabel</code>
	 * 
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPIdnTableDomainLabel clone = (EPPIdnTableDomainLabel) super.clone();

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
	 * Compare an instance of <code>EPPIdnTableDomainLabel</code> with this
	 * instance.
	 * 
	 * @param aObject
	 *            Object to compare with.
	 * 
	 * @return <code>true</code> if equal; <code>false</code> otherwise.
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPIdnTableDomainLabel)) {
			return false;
		}

		EPPIdnTableDomainLabel other = (EPPIdnTableDomainLabel) aObject;

		// Domain
		if (!EqualityUtil.equals(this.domain, other.domain)) {
			cat.error("EPPIdnTableDomainLabel.equals(): domain not equal");
			return false;
		}

		// Form
		if (!EqualityUtil.equals(this.form, other.form)) {
			cat.error("EPPIdnTableDomainLabel.equals(): form not equal");
			return false;
		}

		return true;
	}

}
