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

import com.verisign.epp.codec.gen.EPPCodecException;

public class EPPRegistryDefaultPeriodType extends EPPRegistryPeriodType {
	private static final long serialVersionUID = 6372101315523033369L;

	public static final String ELM_NAME = "registry:default";

	public EPPRegistryDefaultPeriodType() {
		super();
		this.rootName = ELM_NAME;
	}

	public EPPRegistryDefaultPeriodType(Integer n, String unit) {
		this();
		this.number = n;
		this.unit = unit;
	}

	public EPPRegistryDefaultPeriodType(int number, String unit) {
		this();
		this.number = new Integer(number);
		this.unit = unit;
	}

	public boolean equals(Object aObject) {
		return super.equals(aObject);
	}

	void extraValidate() throws EPPCodecException {
		int n = number.intValue();
		if (n < 1 || n > 99) {
			throw new EPPCodecException(getRootName()
					+ ": number should be between 1 - 99.");
		}
		if (!"y".equals(unit) && !"m".equals(unit)) {
			throw new EPPCodecException(getRootName()
					+ ": invalid unit. Valid values: y/m");
		}
	}
}
