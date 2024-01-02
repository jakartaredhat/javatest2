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

package com.sun.ts.tests.jaxws.wsi.w2j.rpc.literal.R4003;

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
	private static final String STRING = "R4003";

	/**
	 * The client.
	 */
	private W2JRLR4003Client client;

	static W2JRLR4003TestService service = null;

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		String[] archiveFiles = { "W2JRLR4003ImportUTF8.wsdl", "W2JRLR4003ImportUTF16.wsdl" };
		return createWebArchive(Client.class, archiveFiles);
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
		client = (W2JRLR4003Client) ClientFactory.getClient(W2JRLR4003Client.class, service);
		logger.log(Level.INFO, "setup ok");
	}

	@AfterEach
	public void cleanup() {
		logger.log(Level.INFO, "cleanup");
	}

	/**
	 * @testName: testWSDLImportUTF8UTF16
	 *
	 * @assertion_ids: WSI:SPEC:R4003
	 *
	 * @test_Strategy: The supplied WSDL imports both a UTF8 and UTF16 wsdl.
	 *
	 * @throws Exception
	 */
	@Test
	public void testWSDLImportUTF8UTF16() throws Exception {
		testImportUTF8WSDL();
		testImportUTF16WSDL();
	}

	private void testImportUTF8WSDL() throws Exception {
		String result;
		try {
			result = client.echoStringUTF8(STRING);
		} catch (Exception e) {
			throw new Exception("Unable to invoke echoStringUTF8 operation (BP-R4003)", e);
		}
		if (!STRING.equals(result)) {
			throw new Exception(
					"echoStringUTF8 operation returns '" + result + "' in stead of '" + STRING + "' (BP-R4003)");
		}
	}

	private void testImportUTF16WSDL() throws Exception {
		String result;
		try {
			result = client.echoStringUTF16(STRING);
		} catch (Exception e) {
			throw new Exception("Unable to invoke echoStringUTF16 operation (BP-R4003)", e);
		}
		if (!STRING.equals(result)) {
			throw new Exception(
					"echoStringUTF16 operation returns '" + result + "' in stead of '" + STRING + "' (BP-R4003)");
		}
	}

}
