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

package com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.httpservletmsgctxpropstest;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;
import java.util.Iterator;

import javax.naming.InitialContext;
import javax.xml.namespace.QName;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaxws.common.BaseClient;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;

public class Client extends BaseClient {

	private static final String PKG_NAME = "com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.httpservletmsgctxpropstest.";

	// service and port information
	private static final String NAMESPACEURI = "http://httptestservice.org/wsdl";

	private static final String SERVICE_NAME = "HttpTestService";

	private static final String PORT_NAME = "HttpTestPort";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME = new QName(NAMESPACEURI, PORT_NAME);

	private static final Class PORT_CLASS = HttpTest.class;

	// URL properties used by the test
	private static final String ENDPOINT_URL = "w2jrlhttpservletmsgctxpropstest.endpoint.1";

	private static final String WSDLLOC_URL = "w2jrlhttpservletmsgctxpropstest.wsdlloc.1";

	private String url = null;

	private URL wsdlurl = null;

	HttpTest port = null;

	static HttpTestService service = null;

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		return createWebArchive(Client.class);
	}

	protected void getService() {
		service = (HttpTestService) getSharedObject();
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
		port = (HttpTest) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, HttpTestService.class, PORT_QNAME, HttpTest.class);
		JAXWS_Util.setTargetEndpointAddress(port, url);
	}

	protected void getPortJavaEE() throws Exception {
		try {
			if (service == null) {
				logger.log(Level.INFO, "WebServiceRef is not set in client");
				logger.log(Level.INFO, "Obtating service via JNDI lookup -> service/wsw2jrlhttpservletmsgctxpropstest");
				InitialContext ic = new InitialContext();
				logger.log(Level.INFO, "Lookup java:comp/env/service/wsw2jrlhttpservletmsgctxpropstest");
				service = (HttpTestService) ic.lookup("java:comp/env/service/wsw2jrlhttpservletmsgctxpropstest");
			} else {
				logger.log(Level.INFO, "WebServiceRef is set in client");
			}
			logger.log(Level.INFO, "service=" + service);
			port = (HttpTest) service.getPort(HttpTest.class);
			logger.log(Level.INFO, "port=" + port);
			JAXWS_Util.dumpTargetEndpointAddress(port);
		} catch (Throwable t) {
			TestUtil.printStackTrace(t);
			throw new Exception(t.toString());
		}
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
	 * @testName: TestServletMessageContextProperties
	 *
	 * @assertion_ids: WS4EE:SPEC:5004; JAXWS:JAVADOC:69; JAXWS:JAVADOC:129;
	 *
	 * @test_Strategy: Test the servlet message context properties.
	 *
	 */
	@Test
	public void TestServletMessageContextProperties() throws Exception {
		boolean pass = true;
		Iterator iterator = null;
		try {
			logger.log(Level.INFO, "TestServletMessageContextProperties");
			port.testServletProperties();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("TestServletMessageContextProperties failed", e);
		}

		if (!pass)
			throw new Exception("TestServletMessageContextProperties failed");
	}

	/*
	 * @testName: TestHttpMessageContextProperties
	 *
	 * @assertion_ids: WS4EE:SPEC:5004; JAXWS:JAVADOC:69; JAXWS:JAVADOC:129;
	 *
	 * @test_Strategy: Test the http message context properties.
	 *
	 */
	@Test
	public void TestHttpMessageContextProperties() throws Exception {
		boolean pass = true;
		Iterator iterator = null;
		try {
			logger.log(Level.INFO, "TestHttpMessageContextProperties");
			port.testHttpProperties();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("TestHttpMessageContextProperties failed", e);
		}

		if (!pass)
			throw new Exception("TestHttpMessageContextProperties failed");
	}
}
