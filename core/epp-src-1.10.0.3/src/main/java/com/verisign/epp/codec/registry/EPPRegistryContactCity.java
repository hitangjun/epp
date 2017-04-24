/***********************************************************
Copyright (C) 2013 VeriSign, Inc.

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
package com.verisign.epp.codec.registry;

import com.verisign.epp.codec.gen.EPPEncodeException;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.2 $
 */
public class EPPRegistryContactCity extends EPPRegistryMinMaxLength {
	/** DOCUMENT ME! */
	private static final long  serialVersionUID = -7311257566677914226L;

	/** DOCUMENT ME! */
	public static final String ELM_NAME = "registry:city";

	/**
	 * Creates a new EPPRegistryContactCity object.
	 */
	public EPPRegistryContactCity() {
		super();
		this.rootName = ELM_NAME;
	}

	/**
	 * Creates a new EPPRegistryContactCity object.
	 *
	 * @param min DOCUMENT ME!
	 * @param max DOCUMENT ME!
	 */
	public EPPRegistryContactCity(Integer min, Integer max) {
		this();
		this.min     = min;
		this.max     = max;
	}

	/**
	 * Creates a new EPPRegistryContactCity object.
	 *
	 * @param min DOCUMENT ME!
	 * @param max DOCUMENT ME!
	 */
	public EPPRegistryContactCity(int min, int max) {
		this(new Integer(min), new Integer(max));
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param aObject DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean equals(Object aObject) {
		return super.equals(aObject);
	}
	
	protected void validateState() throws EPPEncodeException {
		if (min == null || min.intValue() <= 0) {
			throw new EPPEncodeException("Invalid state on "
					+ getClass().getName()
					+ ".encode: min is required and should be greater than 0");
		}
		if (max == null || max.intValue() < min.intValue()) {
			throw new EPPEncodeException(
					"Invalid state on "
							+ getClass().getName()
							+ ".encode: max is required and should be greater than or equal to min");
		}
		if (max.intValue() > 255) {
			throw new EPPEncodeException("Invalid state on "
					+ getClass().getName()
					+ ".encode: max should be less than or equal to 255");
		}
	}
}
