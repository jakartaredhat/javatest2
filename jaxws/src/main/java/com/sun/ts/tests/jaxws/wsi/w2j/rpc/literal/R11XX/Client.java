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

package com.sun.ts.tests.jaxws.wsi.w2j.rpc.literal.R11XX;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.tests.jaxws.common.BaseClient;
import com.sun.ts.tests.jaxws.sharedclients.ClientFactory;
import com.sun.ts.tests.jaxws.wsi.requests.SOAPRequests;

public class Client extends BaseClient implements SOAPRequests {

	private W2JRLR11XXClientOne client1;

	private W2JRLR11XXClientTwo client2;

	static W2JRLR11XXTestService service = null;

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
		client1 = (W2JRLR11XXClientOne) ClientFactory.getClient(W2JRLR11XXClientOne.class, service);
		client2 = (W2JRLR11XXClientTwo) ClientFactory.getClient(W2JRLR11XXClientTwo.class, service);
		logger.log(Level.INFO, "setup ok");
	}

	@AfterEach
	public void cleanup() {
		logger.log(Level.INFO, "cleanup");
	}

	/**
	 * @testName: testSoapActionHeaderIsQuotedInRequest
	 *
	 * @assertion_ids: WSI:SPEC:R1109; WSI:SPEC:R1127
	 *
	 * @test_Strategy: Make a request and inspect request soapAction HTTP header to
	 *                 ensure value is quoted
	 *
	 * @throws Exception
	 */
	@Test
	public void testSoapActionHeaderIsQuotedInRequest() throws Exception {
		String response = "";
		try {
			response = client2.echoString("hello string");
		} catch (Exception e) {
			throw new Exception("Test didn't complete properly: ", e);
		}
	}

}
