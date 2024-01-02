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

package com.sun.ts.tests.jaxws.wsi.w2j.rpc.literal.R2728;

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
	 * The string to be echoed.
	 */
	private static final String STRING = "R2728";

	/**
	 * The client.
	 */
	private W2JRLR2728Client client;

	static W2JRLR2728TestService service = null;

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
		client = (W2JRLR2728Client) ClientFactory.getClient(W2JRLR2728Client.class, service);
		logger.log(Level.INFO, "setup ok");
	}

	@AfterEach
	public void cleanup() {
		logger.log(Level.INFO, "cleanup");
	}

	/**
	 * @testName: testUseAttributeDefaulting
	 *
	 * @assertion_ids: WSI:SPEC:R2728
	 *
	 * @test_Strategy: The supplied WSDL, containg a soap:fault element without the
	 *                 use="literal" attribute has been used by the WSDL-to-Java
	 *                 tool to generate an end point. If the tool works correctly,
	 *                 the end-point has been built and deployed so it should simply
	 *                 be reachable.
	 *
	 * @throws Exception
	 */
	@Test
	public void testUseAttributeDefaulting() throws Exception {
		String result;
		try {
			result = client.echoString(STRING);
		} catch (Exception e) {
			throw new Exception("Unable to invoke echoString operation (BP-R2728)", e);
		}
		if (!STRING.equals(result)) {
			throw new Exception(
					"echoString operation returns '" + result + "' in stead of '" + STRING + "' (BP-R2728)");
		}
	}

}
