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

package com.sun.ts.tests.jaxws.wsi.w2j.rpc.literal.R1012;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.charset.Charset;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.tests.jaxws.common.BaseClient;
import com.sun.ts.tests.jaxws.sharedclients.ClientFactory;
import com.sun.ts.tests.jaxws.wsi.constants.SOAPConstants;
import com.sun.ts.tests.jaxws.wsi.constants.WSIConstants;
import com.sun.ts.tests.jaxws.wsi.requests.SOAPRequests;

public class Client extends BaseClient implements SOAPConstants, WSIConstants, SOAPRequests {

	/**
	 * The string to be echoed for request two.
	 */
	private static final String STRING_2 = "R1012-2";

	/**
	 * The one client.
	 */
	private W2JRLR1012ClientOne client1;

	/**
	 * The other client.
	 */
	private W2JRLR1012ClientTwo client2;

	static W2JRLR1012TestService service = null;

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
		client1 = (W2JRLR1012ClientOne) ClientFactory.getClient(W2JRLR1012ClientOne.class, service);
		client2 = (W2JRLR1012ClientTwo) ClientFactory.getClient(W2JRLR1012ClientTwo.class, service);
		logger.log(Level.INFO, "setup ok");
	}

	@AfterEach
	public void cleanup() {
		logger.log(Level.INFO, "cleanup");
	}

	/**
	 * @testName: testResponseEncoding
	 *
	 * @assertion_ids: WSI:SPEC:R1012
	 *
	 * @test_Strategy: A valid request is made to the endpoint and the returned
	 *                 response is investigated in order to determine the encoding.
	 *
	 * @throws Exception
	 */
	@Test
	public void testResponseEncoding() throws Exception {
		InputStream is;
		Charset cs = Charset.forName("UTF-8");
		try {
			is = client1.makeHTTPRequest(R1012_REQUEST, cs);
			String contentType = client1.getResponseHeader("Content-Type");
			if (contentType != null) {
				int index = contentType.toLowerCase().indexOf("charset=");
				if (index > 0) {
					String name = contentType.substring(index + 8).trim();
					if (name.charAt(0) == '"')
						name = name.substring(1, name.length() - 1);
					if ((name.equalsIgnoreCase("UTF-8")) || name.equalsIgnoreCase("UTF-16")) {
						char c = name.charAt(0);
						if ((c == '\"') || (c == '\'')) {
							name = name.substring(1, name.length() - 1);
						}
						cs = Charset.forName(name);
					} else {
						throw new Exception("Response encoded in '" + name + "' (BP-R1012)");
					}
				}
			}
		} catch (Exception e) {
			throw new Exception("Unable to invoke echoString operation (BP-R1012)", e);
		}
		InputStreamReader isr = new InputStreamReader(is, cs);
		try {
			char[] buffer = new char[1024];
			int length;
			do {
				length = isr.read(buffer);
			} while (length > 0);
		} catch (IOException e) {
			throw new Exception("Unable to read response from endpoint (BP-R1012)", e);
		} finally {
			try {
				isr.close();
				is.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * @testName: testRequestEncoding
	 *
	 * @assertion_ids: WSI:SPEC:R1012
	 *
	 * @test_Strategy: A request is made from the generated client. A handler
	 *                 verifies the encoding. The returned string indicates the
	 *                 success or failure.
	 *
	 * @throws Exception
	 */
	@Test
	public void testRequestEncoding() throws Exception {
		String result;
		try {
			result = client2.echoString(STRING_2);
		} catch (Exception e) {
			throw new Exception("Unable to invoke echoString operation (BP-R1012)", e);
		}
		if (!result.equals(STRING_2)) {
			if (result.equals("EXCEPTION")) {
				throw new Exception("Endpoint unable to process request (BP-R1012)");
			} else {
				throw new Exception("Request encoding neither 'UTF-8' nor 'UTF-16' (BP-R1012)");
			}
		}
	}
}
