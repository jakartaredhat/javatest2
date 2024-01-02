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
 * @(#)Client.java	1.3	03/05/09
 */

package com.sun.ts.tests.jaxws.wsi.w2j.rpc.literal.R4001;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.charset.Charset;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.tests.jaxws.common.BaseClient;
import com.sun.ts.tests.jaxws.sharedclients.ClientFactory;
import com.sun.ts.tests.jaxws.wsi.requests.SOAPRequests;

import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;

public class Client extends BaseClient implements SOAPRequests {

	/**
	 * The string to be echoed for request two.
	 */
	private static final String STRING_2 = "R4001-2";

	/**
	 * The one client.
	 */
	private W2JRLR4001ClientOne client1;

	static W2JRLR4001TestService service = null;

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
		client1 = (W2JRLR4001ClientOne) ClientFactory.getClient(W2JRLR4001ClientOne.class, service);
		logger.log(Level.INFO, "setup ok");
	}

	@AfterEach
	public void cleanup() {
		logger.log(Level.INFO, "cleanup");
	}

	/**
	 * @testName: testRequestWithBOM
	 *
	 * @assertion_ids: WSI:SPEC:R4001
	 *
	 * @test_Strategy: A valid request, encoded in UTF-16 with a BOM, is sent to the
	 *                 endpoint. A conformant server must correctly process it and
	 *                 return the expected response.
	 *
	 * @throws Exception
	 */
	@Test
	public void testRequestWithBOM() throws Exception {
		SOAPMessage response;
		try {
			Charset cs = Charset.forName("UTF-16");
			response = client1.makeSaajRequest(R4001_REQUEST, cs);
		} catch (Exception e) {
			throw new Exception("Unable to invoke echoString operation (BP-R4001)", e);
		}
		try {
			SOAPBody body = response.getSOAPPart().getEnvelope().getBody();
			if (body.hasFault()) {
				throw new Exception("Request not processed by endpoint (BP-R4001)");
			}
		} catch (SOAPException e) {
			throw new Exception("Invalid SOAP message returned (BP-R4001)", e);
		}
	}

}
