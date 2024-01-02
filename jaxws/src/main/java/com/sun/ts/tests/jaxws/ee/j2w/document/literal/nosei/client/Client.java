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

package com.sun.ts.tests.jaxws.ee.j2w.document.literal.nosei.client;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaxws.common.BaseClient;
import com.sun.ts.tests.jaxws.common.JAXWS_Data;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;

public class Client extends BaseClient {

	private static final String PKG_NAME = "com.sun.ts.tests.jaxws.ee.j2w.document.literal.nosei.client.";

	// URL properties used by the test
	private static final String ENDPOINT_URL = "j2wdlnosei.endpoint.1";

	private static final String WSDLLOC_URL = "j2wdlnosei.wsdlloc.1";

	private String url = null;

	private URL wsdlurl = null;

	// ServiceName and PortName mapping configuration going java-to-wsdl
	private static final String SERVICE_NAME = "EchoService";

	private static final String PORT_NAME = "EchoPort";

	private static final String NAMESPACEURI = "http://echo.org/wsdl";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME = new QName(NAMESPACEURI, PORT_NAME);

	Echo port = null;

	static EchoService service = null;

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		return createWebArchive(Client.class);
	}

	protected void getService() {
		logger.log(Level.INFO, "WebServiceRef is not set in Client (get it from specific vehicle)");
		service = (EchoService) getSharedObject();
	}

	protected void getTestURLs() throws Exception {
		logger.log(Level.INFO, "Get URL's used by the test");
		String file = JAXWS_Util.getURLFromProp(ENDPOINT_URL);
		url = ctsurl.getURLString(PROTOCOL, hostname, portnum, file);
		file = JAXWS_Util.getURLFromProp(WSDLLOC_URL);
		wsdlurl = ctsurl.getURL(PROTOCOL, hostname, portnum, file);
		logger.log(Level.INFO, "Service Endpoint URL: " + url);
		logger.log(Level.INFO, "WSDL Location URL:    " + wsdlurl);
	}

	protected void getPortStandalone() throws Exception {
		port = (Echo) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, EchoService.class, PORT_QNAME, Echo.class);
		JAXWS_Util.setTargetEndpointAddress(port, url);
	}

	protected void getPortJavaEE() throws Exception {
		logger.log(Level.INFO, "Obtain service via WebServiceRef annotation");
		logger.log(Level.INFO, "service=" + service);
		port = (Echo) service.getPort(Echo.class);
		logger.log(Level.INFO, "port=" + port);
		logger.log(Level.INFO, "Obtained port");
		getTargetEndpointAddress(port);
		// JAXWS_Util.setTargetEndpointAddress(port, url);
	}

	private void getTargetEndpointAddress(Object port) throws Exception {
		logger.log(Level.INFO, "Get Target Endpoint Address for port=" + port);
		String url = JAXWS_Util.getTargetEndpointAddress(port);
		logger.log(Level.INFO, "Target Endpoint Address=" + url);
	}

	/* Test setup */

	/*
	 * @class.testArgs: -ap jaxws-url-props.dat
	 * 
	 * @class.setup_props: webServerHost; webServerPort; platform.mode;
	 */
	@BeforeEach
	public void setup() throws Exception {
		super.setup();
	}

	@AfterEach
	public void cleanup() throws Exception {
		logger.log(Level.INFO, "cleanup ok");
	}

	/*
	 * @testName: test
	 *
	 * @assertion_ids: JAXWS:SPEC:3000; JAXWS:SPEC:3012; JAXWS:SPEC:3036;
	 *
	 * @test_Strategy:
	 *
	 * Description
	 */
	@Test
	public void test() throws Exception {
		logger.log(Level.INFO, "test");
		boolean pass = true;

		if (!stringTest())
			pass = false;
		if (!stringArrayTest())
			pass = false;

		if (!pass)
			throw new Exception("test failed");
	}

	public boolean stringTest() throws Exception {
		logger.log(Level.INFO, "stringTest");
		boolean pass = true;
		String request = "Mary";

		try {
			String response = port.echoString(request);
			if (!JAXWS_Data.compareValues(request, response, "String"))
				pass = false;
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("stringTest failed", e);
		}
		return pass;
	}

	public boolean stringArrayTest() throws Exception {
		logger.log(Level.INFO, "stringArrayTest");
		boolean pass = true;
		List<String> request = JAXWS_Data.list_String_nonull_data;

		try {
			List<String> response = port.echoStringArray(request);
			if (!JAXWS_Data.compareArrayValues(request, response, "String"))
				pass = false;
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("stringArrayTest failed", e);
		}
		return pass;
	}
}
