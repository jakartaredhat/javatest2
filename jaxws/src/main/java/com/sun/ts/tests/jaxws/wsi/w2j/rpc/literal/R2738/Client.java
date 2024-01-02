/*
 * Copyright (c) 2007, 2020 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.ts.tests.jaxws.wsi.w2j.rpc.literal.R2738;

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

import jakarta.xml.ws.Holder;

public class Client extends BaseClient implements SOAPRequests {

	private W2JRLR2738Client client;

	static W2JRLR2738TestService service = null;

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
		client = (W2JRLR2738Client) ClientFactory.getClient(W2JRLR2738Client.class, service);
		logger.log(Level.INFO, "setup ok");
	}

	@AfterEach
	public void cleanup() {
		logger.log(Level.INFO, "cleanup");
	}

	/**
	 * @testName: IncludeAllSoapHeadersTest
	 *
	 * @assertion_ids: WSI:SPEC:R2738
	 *
	 * @test_Strategy: Send a SOAP request/response that has the headers in the
	 *                 wsdl:input and wsdl:output sections The endpoint and the
	 *                 client checks for their correctness.
	 *
	 * @throws Exception
	 */
	@Test
	public void IncludeAllSoapHeadersTest() throws Exception {
		String result = null;
		ConfigHeader ch1 = new ConfigHeader();
		ch1.setMessage("ConfigHeader1");
		ConfigHeader ch2 = new ConfigHeader();
		ch2.setMessage("ConfigHeader2");
		Holder ghh2 = new Holder<ConfigHeader>();
		ghh2.value = ch2;
		ConfigHeader ch3 = new ConfigHeader();
		ch3.setMessage("ConfigHeader3");
		Holder ghh3 = new Holder<ConfigHeader>();
		ghh3.value = ch3;

		try {
			result = client.echoIt("IncludeAllSoapHeadersTest", ch1, ghh2, ghh3);
		} catch (Exception e) {
			throw new Exception("Unable to invoke echoString operation (BP-R2738)", e);
		}
		String expected = "PASSED";
		if (result.equals(expected)) {
			expected = "ConfigHeader1";
			if (ch1.getMessage().equals(expected)) {
				expected = "ConfigHeader22";
				ch2 = (ConfigHeader) ghh2.value;
				if (ch2.getMessage().equals(expected)) {
					expected = "ConfigHeader33";
					ch3 = (ConfigHeader) ghh3.value;
					if (!ch3.getMessage().equals(expected)) {
						throw new Exception("echoIt operation did not return correct value for ConfigHeader: expected="
								+ expected + ", got=" + ch3.getMessage());
					}
				} else {
					throw new Exception("echoIt operation did not return correct value for ConfigHeader: expected="
							+ expected + ", got=" + ch2.getMessage());
				}
			} else {
				throw new Exception("echoIt operation did not return correct value for ConfigHeader: expected="
						+ expected + ", got=" + ch1.getMessage());
			}
		} else {
			throw new Exception(
					"echoIt operation did not return correct result: expected=" + expected + ", got=" + result);
		}
	}
}
