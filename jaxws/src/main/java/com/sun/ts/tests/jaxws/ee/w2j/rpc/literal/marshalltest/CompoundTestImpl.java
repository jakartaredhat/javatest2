/*
 * Copyright (c) 2007, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

/*
 * $Id$
 */

package com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.marshalltest;

import jakarta.jws.WebService;

@WebService(targetNamespace = "http://marshalltestservice.org/MarshallTestService.wsdl", portName = "MarshallTestPort3", serviceName = "MarshallTestService", wsdlLocation = "WEB-INF/wsdl/WSW2JRLMarshallTestService.wsdl", endpointInterface = "com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.marshalltest.CompoundTest")
public class CompoundTestImpl implements CompoundTest {
	public EchoEmployeeResponse echoEmployee(EchoEmployeeRequest employee) {
		EchoEmployeeResponse employeeResp = new EchoEmployeeResponse();
		employeeResp.setEmployee(employee.getEmployee());
		return employeeResp;
	}

	public EchoPersonResponse echoPerson(EchoPersonRequest person) {
		EchoPersonResponse personResp = new EchoPersonResponse();
		personResp.setPerson(person.getPerson());
		return personResp;
	}

	public Document echoDocument(Document document) {
		return document;
	}
}
