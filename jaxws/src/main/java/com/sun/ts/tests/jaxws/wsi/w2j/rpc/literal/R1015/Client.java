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

package com.sun.ts.tests.jaxws.wsi.w2j.rpc.literal.R1015;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.tests.jaxws.common.BaseClient;
import com.sun.ts.tests.jaxws.sharedclients.ClientFactory;
import com.sun.ts.tests.jaxws.sharedclients.simpleclient.SimpleTest;
import com.sun.ts.tests.jaxws.sharedclients.simpleclient.SimpleTestClient;
import com.sun.ts.tests.jaxws.wsi.requests.SOAPRequests;

import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;

public class Client extends BaseClient implements SOAPRequests {

	private SimpleTestClient client;

	static SimpleTest service = null;

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
		client = (SimpleTestClient) ClientFactory.getClient(SimpleTestClient.class, service);
		logger.log(Level.INFO, "setup ok");
	}

	@AfterEach
	public void cleanup() {
		logger.log(Level.INFO, "cleanup");
	}

	/**
	 * @testName: testEnvelopeWrongNamespace
	 *
	 * @assertion_ids: WSI:SPEC:R1015
	 *
	 * @test_Strategy: Make a request with envelope with wrong namespace, inpsect
	 *                 response to make sure it is a soap:Fault.
	 *
	 * @throws Exception
	 */
	@Test
	public void testEnvelopeWrongNamespace() throws Exception {
		SOAPMessage response = null;
		try {
			response = client.makeSaajRequest(BAD_SOAP_ENVELOPE);
		} catch (Exception e) {
			throw new Exception("Test didn't complete properly: ", e);
		}
		try {
			validateIsException(response);
		} catch (SOAPException se) {
			throw new Exception("Error creating response object", se);
		}
		client.logMessageInHarness(response);
	}

	private void validateIsException(SOAPMessage response) throws Exception, SOAPException {
		if (!response.getSOAPPart().getEnvelope().getBody().hasFault()) {
			client.logMessageInHarness(response);
			throw new Exception("Invalid response: instances must generate a soap:Fault when a request "
					+ "soap:Envelope uses a namespace other than http://schemas.xmlsoap.org/soap/envelope/"
					+ "(BP-R1015).");
		}
	}
}
