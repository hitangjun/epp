/***********************************************************
Copyright (C) 2016 VeriSign, Inc.

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

/**
 * Operational state of the reseller, with one of the following values:<br>
 * <ul>
 * <li><code>OK</code> - The normal status value for the reseller object.</li>
 * <li><code>READONLY</code> - Transform commands submitted with the
 * reseller identifier would not be allowed.</li>
 * <li><code>TERMINATED</code> - Query and transform commands submitted with
 * the reseller identifier would not be allowed.</li>
 */
public enum State implements java.io.Serializable, Cloneable {
	OK("ok"), READONLY("readonly"), TERMINATED("terminated");

	private final String stateStr;

	/**
	 * Define the string value for the state value.
	 * 
	 * @param aStateStr
	 *            Enumerated value string
	 */
	State(String aStateStr) {
		this.stateStr = aStateStr;
	}

	/**
	 * Get the state enumerated value given the matching string.
	 * 
	 * @param aState
	 *            <code>State</code> enumerated string to convert to an
	 *            enumerated <code>State</code> instance.
	 * 
	 * @return Enumerated <code>State</code> value matching the
	 *         <code>String</code>.
	 * 
	 * @throws InvalidParameterException
	 *             If <code>aString</code> does not match an enumerated
	 *             <code>State</code> string value.
	 */
	public static State getState(String aState) {
		if (aState.equals(OK.stateStr)) {
			return OK;
		}
		else if (aState.equals(READONLY.stateStr)) {
			return READONLY;
		}
		else if (aState.equals(TERMINATED.stateStr)) {
			return TERMINATED;
		}
		else {
			throw new InvalidParameterException("Type enum value of "
					+ aState + " is not valid.");
		}
	}

	/**
	 * Convert the enumerated <code>State</code> value to a
	 * <code>String</code>.
	 */
	public String toString() {
		return this.stateStr;
	}

}