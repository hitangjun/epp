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
package com.verisign.epp.codec.fee.v08;

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
 * The command along with the optional phase and sub-phase that the fee is
 * associated with.
 */
public class EPPFeeCommand implements EPPCodecComponent {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPFeeCommand.class.getName(),
			EPPCatFactory.getInstance().getFactory());

	/**
	 * Phase when trademark holders can submit registrations or applications
	 * with trademark information that can be validated by.
	 */
	public final static String PHASE_SUNRISE = "sunrise";

	/**
	 * Post sunrise phase when non-trademark holders are allowed to register
	 * domain names with steps taken to address a large volume of initial
	 * registrations.
	 */
	public final static String PHASE_LANDRUSH = "landrush";

	/**
	 * Trademark claims as defined by Trademark Clearinghouse model of
	 * displaying a claims notice to clients for domain names that match
	 * trademarks.
	 */
	public final static String PHASE_CLAIMS = "claims";

	/**
	 * Post launch phase that is also referred to as "steady state". Servers MAY
	 * require additional trademark protection with this phase.
	 */
	public final static String PHASE_OPEN = "open";

	/**
	 * Post launch phase that is also referred to as "steady state". Servers MAY
	 * require additional trademark protection with this phase.
	 */
	public final static String PHASE_CUSTOM = "custom";

	/**
	 * Constant for the phase local name
	 */
	public static final String ELM_LOCALNAME = "command";

	/**
	 * Constant for the phase qualified name (prefix and local name)
	 */
	public static final String ELM_NAME = EPPFeeExtFactory.NS_PREFIX + ":"
			+ ELM_LOCALNAME;

	/**
	 * OPTIONAL phase attribute name that defines the phase of the command.
	 */
	public static final String ATTR_PHASE = "phase";

	/**
	 * OPTIONAL subphase attribute name that defines the sub-phase of the
	 * command.
	 */
	public static final String ATTR_SUBPHASE = "subphase";

	/**
	 * Command value. This value is required.
	 */
	private String command;

	/**
	 * OPTIONAL "phase" attribute.
	 */
	private String phase;

	/**
	 * OPTIONAL "subphase" attribute.
	 */
	private String subphase;

	/**
	 * Default constructor. The command value MUST be set using the
	 * {@link #setCommand(String)} method.
	 */
	public EPPFeeCommand() {
	}

	/**
	 * Create <code>EPPFeeCommand</code> instance with a defined command value.
	 * 
	 * @param aCommand
	 *            Command value.
	 */
	public EPPFeeCommand(String aCommand) {
		this.command = aCommand;
	}

	/**
	 * Create <code>EPPFeeCommand</code> instance with a defined command and
	 * phase value.
	 * 
	 * @param aCommand
	 *            Command value.
	 * @param aPhase
	 *            Phase value using one of the <code>PHASE</code> constants.
	 */
	public EPPFeeCommand(String aCommand, String aPhase) {
		this.command = aCommand;
		this.phase = aPhase;
	}

	/**
	 * Create <code>EPPFeeCommand</code> instance with a defined command, phase,
	 * and sub-phase value.
	 * 
	 * @param aCommand
	 *            Command value.
	 * @param aPhase
	 *            Phase value using one of the <code>PHASE</code> constants.
	 * @param aSubPhase
	 *            Sub-phase value
	 */
	public EPPFeeCommand(String aCommand, String aPhase, String aSubPhase) {
		this.command = aCommand;
		this.phase = aPhase;
		this.subphase = aSubPhase;
	}

	/**
	 * Is the command defined?
	 * 
	 * @return <code>true</code> if the command is defined; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasCommand() {
		return (this.command != null ? true : false);
	}

	/**
	 * Gets the command value.
	 * 
	 * @return Command value if defined; <code>null</code> otherwise.
	 */
	public String getCommand() {
		return this.command;
	}

	/**
	 * Sets the command value.
	 * 
	 * @param aCommand
	 *            Command value.
	 */
	public void setCommand(String aCommand) {
		this.command = aCommand;
	}

	/**
	 * Is the phase defined?
	 * 
	 * @return <code>true</code> if the phase is defined; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasPhase() {
		return (this.phase != null ? true : false);
	}

	/**
	 * Gets the OPTIONAL phase value, which should match one of the
	 * <code>PHASE</code> constants.
	 * 
	 * @return Phase value if defined; <code>null</code> otherwise.
	 */
	public String getPhase() {
		return this.phase;
	}

	/**
	 * Sets the OPTIONAL phase value.
	 * 
	 * @param aPhase
	 *            Phase value, which should be one of the <code>PHASE</code>
	 *            constants.
	 */
	public void setPhase(String aPhase) {
		this.phase = aPhase;
	}

	/**
	 * Is the sub-phase defined?
	 * 
	 * @return <code>true</code> if the phase is defined; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasSubPhase() {
		return (this.subphase != null ? true : false);
	}

	/**
	 * Gets the OPTIONAL sub-phase value.
	 * 
	 * @return Sub-phase value if defined; <code>null</code> otherwise.
	 */
	public String getSubPhase() {
		return this.subphase;
	}

	/**
	 * Sets the OPTIONAL sub-phase value.
	 * 
	 * @param aSubPhase
	 *            Sub-phase value.
	 */
	public void setSubPhase(String aSubPhase) {
		this.subphase = aSubPhase;
	}

	/**
	 * Clone <code>EPPFeeCommand</code> instance.
	 *
	 * @return clone of <code>EPPFeeCommand</code>
	 *
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPFeeCommand clone = null;

		clone = (EPPFeeCommand) super.clone();

		return clone;
	}

	/**
	 * Decode the <code>EPPFeeCommand</code> element aElement DOM Element tree.
	 *
	 * @param aElement
	 *            - Root DOM Element to decode <code>EPPFeeCommand</code> from.
	 *
	 * @exception EPPDecodeException
	 *                Unable to decode aElement
	 */
	public void decode(Element aElement) throws EPPDecodeException {
		// Phase
		String thePhase = aElement.getAttribute(ATTR_PHASE);
		if (thePhase != null && !thePhase.isEmpty()) {
			this.phase = thePhase;
		}
		else {
			this.phase = null;
		}

		// Sub-phase
		String theSubPhase = aElement.getAttribute(ATTR_SUBPHASE);
		if (theSubPhase != null && !theSubPhase.isEmpty()) {
			this.subphase = theSubPhase;
		}
		else {
			this.subphase = null;
		}

		// Command
		Node textNode = aElement.getFirstChild();

		if (textNode != null) {
			this.command = textNode.getNodeValue();
		}
	}

	/**
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPFeeCommand</code> instance.
	 *
	 * @param aDocument
	 *            - DOM Document that is being built. Used as an Element
	 *            factory.
	 *
	 * @return Element - Root DOM Element representing the
	 *         <code>EPPFeeCommand</code> instance.
	 *
	 * @exception EPPEncodeException
	 *                - Unable to encode <code>EPPFeeCommand</code> instance.
	 */
	public Element encode(Document aDocument) throws EPPEncodeException {

		if (aDocument == null) {
			throw new EPPEncodeException("aDocument is null"
					+ " on in EPPFeeCommand.encode(Document)");
		}
		if (this.command == null) {
			throw new EPPEncodeException("command is null"
					+ " on in EPPFeeCommand.encode(Document)");
		}

		Element root = aDocument.createElementNS(EPPFeeExtFactory.NS, ELM_NAME);

		// Phase
		if (this.phase != null) {
			root.setAttribute(ATTR_PHASE, this.phase);
		}

		// Sub-phase
		if (this.subphase != null) {
			root.setAttribute(ATTR_SUBPHASE, this.subphase);
		}

		// Command
		Text phaseText = aDocument.createTextNode(this.command);
		root.appendChild(phaseText);

		return root;
	}

	/**
	 * Implements a deep <code>EPPFeeCommand</code> compare.
	 *
	 * @param aObject
	 *            <code>EPPFeeCommand</code> instance to compare with
	 *
	 * @return <code>true</code> if equal; <code>false</code> otherwise
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPFeeCommand)) {
			return false;
		}

		EPPFeeCommand other = (EPPFeeCommand) aObject;

		// Phase
		if (!EqualityUtil.equals(this.phase, other.phase)) {
			cat.error("EPPFeeCommand.equals(): phase not equal");
			return false;
		}

		// Sub-phase
		if (!EqualityUtil.equals(this.subphase, other.subphase)) {
			cat.error("EPPFeeCommand.equals(): subphase not equal");
			return false;
		}

		// Command
		if (!EqualityUtil.equals(this.command, other.command)) {
			cat.error("EPPFeeCommand.equals(): command not equal");
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
