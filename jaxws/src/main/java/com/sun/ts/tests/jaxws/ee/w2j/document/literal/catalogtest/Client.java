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

package com.sun.ts.tests.jaxws.ee.w2j.document.literal.catalogtest;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;
import java.util.Iterator;

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
	private static final long serialVersionUID = 1L;

	private static final String PKG_NAME = "com.sun.ts.tests.jaxws.ee.w2j.document.literal.catalogtest.";

	// service and port information
	private static final String NAMESPACEURI = "http://catalogtestservice.org/wsdl";

	private static final String SERVICE_NAME = "CatalogTestService";

	private static final String PORT_NAME = "HelloPort";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME = new QName(NAMESPACEURI, PORT_NAME);

	// URL properties used by the test
	private static final String ENDPOINT_URL = "w2jdlcatalogtest.endpoint.1";

	private static final String WSDLLOC_URL = "w2jdlcatalogtest.wsdlloc.1";

	private String url = null;

	private URL wsdlurl = null;

	transient Hello port = null;

	static CatalogTestService service = null;

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		return createWebArchive(Client.class);
	}

	protected void getService() {
		service = (CatalogTestService) getSharedObject();
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
	 * @testName: TestCatalogWithValidSystemIdAndValidURIValidWSDL
	 *
	 * @assertion_ids: JAXWS:SPEC:4020; WS4EE:SPEC:5007; WS4EE:SPEC:35;
	 * WS4EE:SPEC:4014;
	 *
	 * @test_Strategy: Positive test case for oasis catalogs. Valid SystemId and
	 * valid URI pointing to a valid WSDL. Must pass.
	 */
	@Test
	public void TestCatalogWithValidSystemIdAndValidURIValidWSDL() throws Exception {
		boolean pass = true;
		Iterator iterator = null;
		try {
			logger.log(Level.INFO, "TestCatalogWithValidSystemIdAndValidURIValidWSDL");
			logger.log(Level.INFO, "Get port via wsdl catalog with Valid URI/Valid WSDL");
			if (modeProperty.equals("standalone")) {
				port = (Hello) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, CatalogTestService.class, PORT_QNAME,
						Hello.class);
				logger.log(Level.INFO, "port=" + port);
				JAXWS_Util.setTargetEndpointAddress(port, url);
			} else {
				logger.log(Level.INFO, "service=" + service);
				port = (Hello) service.getPort(Hello.class);
				logger.log(Level.INFO, "port=" + port);
				JAXWS_Util.dumpTargetEndpointAddress(port);
			}
			HelloRequest request = new HelloRequest();
			request.setString("Hello There!");
			HelloResponse response = port.hello(request);
			logger.log(Level.INFO, "response=" + response.getString());
		} catch (Exception e) {
			TestUtil.logErr("Caught unexpected exception: test failed");
			TestUtil.logErr("Invocation should have succeeded (Valid URI/Valid WSDL in catalog)");
			throw new Exception("TestCatalogWithValidSystemIdAndValidURIValidWSDL failed", e);
		}

		if (!pass)
			throw new Exception("TestCatalogWithValidSystemIdAndValidURIValidWSDL failed");
	}
}
