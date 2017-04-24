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

package com.verisign.epp.serverstub;

import com.verisign.epp.codec.gen.EPPResponse;
import com.verisign.epp.codec.registry.EPPRegistryMapFactory;
import com.verisign.epp.framework.EPPPollDataRecord;
import com.verisign.epp.framework.EPPPollHandler;
import com.verisign.epp.framework.EPPPollQueueException;

public class RegistryPollHandler implements EPPPollHandler {

	public String getKind() {
		return EPPRegistryMapFactory.NS;
	}

	public EPPResponse toResponse(EPPPollDataRecord aRecord)
			throws EPPPollQueueException {
		if (!aRecord.getKind().equals(getKind())) {
			throw new EPPPollQueueException("Handler for kind "
					+ aRecord.getKind() + " does not match");
		}

		// Get the concrete response from the record
		EPPResponse theResponse = (EPPResponse) aRecord.getData();

		throw new EPPPollQueueException("Unable to handle message class <"
				+ theResponse.getClass().getName());
	}
}
