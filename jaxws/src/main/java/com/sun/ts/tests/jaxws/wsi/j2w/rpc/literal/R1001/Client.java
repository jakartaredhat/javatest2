/*
 * Copyright (c) 2007, 2024 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.ts.tests.jaxws.wsi.j2w.rpc.literal.R1001;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Iterator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.tests.jaxws.common.BaseClient;
import com.sun.ts.tests.jaxws.sharedclients.ClientFactory;
import com.sun.ts.tests.jaxws.sharedclients.faultclient.FaultTestClient;
import com.sun.ts.tests.jaxws.sharedclients.faultclient.FaultTest;
import com.sun.ts.tests.jaxws.wsi.requests.SOAPRequests;

import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;

public class Client extends BaseClient implements SOAPRequests {

	private FaultTestClient client;

	static FaultTest service = null;

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
		client = (FaultTestClient) ClientFactory.getClient(FaultTestClient.class, service);
		logger.log(Level.INFO, "setup ok");
	}

	@AfterEach
	public void cleanup() {
		logger.log(Level.INFO, "cleanup");
	}

	/**
	 * @testName: testSoapFaultUnqualifiedChildrenDummyException
	 *
	 * @assertion_ids: WSI:SPEC:R1001
	 *
	 * @test_Strategy: Make a request and inspect response to ensure When a MESSAGE
	 *                 contains a soap:Fault element its element children MUST be
	 *                 unqualified.
	 *
	 * @throws Exception
	 */
	@Test
	public void testSoapFaultUnqualifiedChildrenDummyException() throws Exception {
		SOAPMessage response = null;
		try {
			response = client.makeSaajRequest(ALWAYS_THROWS_EXCEPTION);
		} catch (Exception e) {
			throw new Exception("Test didn't complete properly: ", e);
		}
		try {
			validateUnqualifiedFaultChildrenNames(response);
		} catch (SOAPException se) {
			throw new Exception("Error creating response object", se);
		}
		client.logMessageInHarness(response);
	}

	/**
	 * @testName: testSoapFaultUnqualifiedChildrenServerException
	 *
	 * @assertion_ids: WSI:SPEC:R1001
	 *
	 * @test_Strategy: Make a request and inspect response to ensure When a MESSAGE
	 *                 contains a soap:Fault element its element children MUST be
	 *                 unqualified.
	 *
	 * @throws Exception
	 */
	@Test
	public void testSoapFaultUnqualifiedChildrenServerException() throws Exception {
		SOAPMessage response = null;
		try {
			response = client.makeSaajRequest(ALWAYS_THROWS_SERVER_EXCEPTION);
		} catch (Exception e) {
			throw new Exception("Test didn't complete properly: ", e);
		}
		try {
			validateUnqualifiedFaultChildrenNames(response);
		} catch (SOAPException se) {
			throw new Exception("Error creating response object", se);
		}
		client.logMessageInHarness(response);
	}

	private void validateUnqualifiedFaultChildrenNames(SOAPMessage response) throws Exception, SOAPException {
		Iterator children = response.getSOAPPart().getEnvelope().getBody().getFault().getChildElements();
		SOAPElement child;
		String prefix;
		while (children.hasNext()) {
			child = (SOAPElement) children.next();
			prefix = child.getElementName().getPrefix();
			if (!(prefix == null || prefix.equals(""))) {
				client.logMessageInHarness(response);
				throw new Exception("Invalid soap:Fault child : must be unqualified (BP-R1001):  "
						+ child.getElementName().getQualifiedName());
			}
		}
	}
}
