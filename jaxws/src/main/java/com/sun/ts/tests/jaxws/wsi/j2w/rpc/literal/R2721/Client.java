/*
 * Copyright (c) 2007, 2018 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.ts.tests.jaxws.wsi.j2w.rpc.literal.R2721;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sun.ts.tests.jaxws.sharedclients.ClientFactory;
import com.sun.ts.tests.jaxws.sharedclients.SOAPClient;
import com.sun.ts.tests.jaxws.sharedclients.faultclient.FaultTestClient;
import com.sun.ts.tests.jaxws.sharedclients.faultclient.FaultTest;

import com.sun.ts.tests.jaxws.wsi.constants.DescriptionConstants;
import com.sun.ts.tests.jaxws.wsi.constants.SOAPConstants;
import com.sun.ts.tests.jaxws.wsi.utils.DescriptionUtils;
import com.sun.ts.tests.jaxws.common.BaseClient;

public class Client extends BaseClient implements DescriptionConstants, SOAPConstants {
	/**
	 * The client.
	 */
	private SOAPClient client;

	static FaultTest service = null;

	private boolean foundOne = false;

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	/**
	 * @class.testArgs: -ap jaxws-url-props.dat
	 * @class.setup_props: webServerHost; webServerPort; platform.mode;
	 *
	 * @param args
	 * @param properties
	 *
	 * @throws Exception
	 */
	@BeforeEach
	public void setup() throws Exception {
		super.setup();
		client = ClientFactory.getClient(FaultTestClient.class, service);
		logger.log(Level.INFO, "setup ok");
	}

	@AfterEach
	public void cleanup() {

		logger.log(Level.INFO, "cleanup");
	}

	/**
	 * @testName: testNameAttributes
	 *
	 * @assertion_ids: WSI:SPEC:R2721
	 *
	 * @test_Strategy: Retrieve the WSDL, generated by the Java-to-WSDL tool, and
	 *                 examine the wsdl:binding elements, ensuring that the
	 *                 contained soap:fault elements have the name attribute
	 *                 specified.
	 *
	 * @throws Exception
	 */
	@Test
	public void testNameAttributes() throws Exception {
		Document document = client.getDocument();
		Element[] bindings = DescriptionUtils.getBindings(document);
		for (int i = 0; i < bindings.length; i++) {
			verifyBinding(bindings[i]);
		}
		if (!foundOne) {
			throw new Exception("No soap:fault elements found during test");
		}

	}

	protected void verifyBinding(Element binding) throws Exception {
		Element[] operations = DescriptionUtils.getChildElements(binding, WSDL_NAMESPACE_URI,
				WSDL_OPERATION_LOCAL_NAME);
		for (int i = 0; i < operations.length; i++) {
			verifyOperation(operations[i]);
		}
	}

	protected void verifyOperation(Element operation) throws Exception {
		NodeList list = operation.getElementsByTagNameNS(SOAP_NAMESPACE_URI, SOAP_FAULT_LOCAL_NAME);
		for (int i = 0; i < list.getLength(); i++) {
			verifySOAPException((Element) list.item(i));
		}
	}

	protected void verifySOAPException(Element Exception) throws Exception {
		Attr attr = Exception.getAttributeNode(SOAP_NAME_ATTR);
		if (attr == null) {
			throw new Exception("Required 'name' attribute not present on soap:fault element (BP-R2721)");
		} else {
			foundOne = true;
		}
	}
}
