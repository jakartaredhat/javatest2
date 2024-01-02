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

package com.sun.ts.tests.jaxws.wsi.w2j.rpc.literal.R2714;

import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.tests.jaxws.common.BaseClient;
import com.sun.ts.tests.jaxws.sharedclients.ClientFactory;
import com.sun.ts.tests.jaxws.wsi.constants.WSIConstants;
import com.sun.ts.tests.jaxws.wsi.requests.SOAPRequests;

public class Client extends BaseClient implements WSIConstants, SOAPRequests {

	private W2JRLR2714Client client;

	static SimpleTest service = null;

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		return createWebArchive(Client.class);
	}

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
		client = (W2JRLR2714Client) ClientFactory.getClient(W2JRLR2714Client.class, service);
		logger.log(Level.INFO, "setup ok");
	}

	@AfterEach
	public void cleanup() {
		logger.log(Level.INFO, "cleanup");
	}

	/**
	 * @testName: testNoResponseBodyForOneWay
	 *
	 * @assertion_ids: WSI:SPEC:R2714
	 *
	 * @test_Strategy: Make a request and inspect response to ensure there is no
	 *                 HTTP response body
	 *
	 * @throws Exception
	 */
	@Test
	public void testNoResponseBodyForOneWay() throws Exception {
		InputStream response = null;
		try {
			response = client.makeHTTPRequest(ONE_WAY_OPERATION);
		} catch (Exception e) {
			throw new Exception("Test didn't complete properly: ", e);
		}
		try {
			validateNoResponseBody(response);
		} catch (IOException ioe) {
			throw new Exception("Error creating response object", ioe);
		}
		client.logMessageInHarness(response);
	}

	private void validateNoResponseBody(InputStream is) throws IOException, Exception {
		if (is.available() > 0) {
			throw new Exception(
					"Invalid HTTP response: response body must be empty for " + "one way operations (BP-R2714).");
		}
	}
}
