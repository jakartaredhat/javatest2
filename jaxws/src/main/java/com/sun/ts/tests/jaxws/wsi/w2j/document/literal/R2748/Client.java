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

package com.sun.ts.tests.jaxws.wsi.w2j.document.literal.R2748;

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

public class Client extends BaseClient {
	/**
	 * The one string to be echoed.
	 */
	private static final String STRING_1 = "R2748-1";

	/**
	 * The other string to be echoed.
	 */
	private static final String STRING_2 = "R2748-2";

	/**
	 * The one client.
	 */
	private W2JDLR2748ClientOne client1;

	/**
	 * The other client.
	 */
	private W2JDLR2748ClientTwo client2;

	static W2JDLR2748TestService service = null;

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
		client1 = (W2JDLR2748ClientOne) ClientFactory.getClient(W2JDLR2748ClientOne.class, service);
		client2 = (W2JDLR2748ClientTwo) ClientFactory.getClient(W2JDLR2748ClientTwo.class, service);
		logger.log(Level.INFO, "setup ok");
	}

	@AfterEach
	public void cleanup() {
		logger.log(Level.INFO, "cleanup");
	}

	/**
	 * @testName: testFalseWSDLRequireds
	 *
	 * @assertion_ids: WSI:SPEC:R2748
	 *
	 * @test_Strategy: The supplied WSDL, containing two port types, which are
	 *                 identical except for the use of the wsdl:required attribute
	 *                 on the soap binding extension elements, has been used by the
	 *                 WSDL-to-Java tool to generate an end point. If the tool works
	 *                 correctly, the end-point has been built and deployed so it
	 *                 should simply be reachable.
	 *
	 * @throws Exception
	 */
	@Test
	public void testFalseWSDLRequireds() throws Exception {
		String result;
		try {
			result = client1.echoString(STRING_1);
		} catch (Exception e) {
			throw new Exception("Unable to invoke echoString operation (BP-R2748)", e);
		}
		if (!STRING_1.equals(result)) {
			throw new Exception(
					"echoString operation returns '" + result + "' in stead of '" + STRING_1 + "' (BP-R2748)");
		}
		try {
			result = client2.echoString(STRING_2);
		} catch (Exception e) {
			throw new Exception("Unable to invoke echoString operation (BP-R2748)", e);
		}
		if (!STRING_2.equals(result)) {
			throw new Exception(
					"echoString operation returns '" + result + "' in stead of '" + STRING_2 + "' (BP-R2748)");
		}
	}
}
