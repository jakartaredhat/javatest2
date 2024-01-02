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

package com.sun.ts.tests.jaxws.wsi.j2w.rpc.literal.R1124;

import java.io.IOException;
import java.io.InputStream;
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
	 * @testName: testSuccessfulResponseStatusCode
	 *
	 * @assertion_ids: WSI:SPEC:R1124
	 *
	 * @test_Strategy: Make a request that generates a successful outcome, inpsect
	 *                 HTTP response to make sure the status code is 2xx.
	 *
	 * @throws Exception
	 */
	@Test
	public void testSuccessfulResponseStatusCode() throws Exception {
		InputStream response;
		try {
			response = client.makeHTTPRequest(HELLOWORLD);
			client.logMessageInHarness(response);
			if (!Integer.toString(client.getStatusCode()).startsWith("2")) {
				throw new Exception("Invalid response: instances must return HTTP status code 200"
						+ " for a successful request (BP-R1124).");
			}
		} catch (IOException e) {
			throw new Exception("Test didn't complete properly: ", e);
		}
	}
}
