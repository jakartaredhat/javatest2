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

package com.sun.ts.tests.jaxws.ee.w2j.document.literal.customization.external;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;

import javax.xml.namespace.QName;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaxws.common.BaseClient;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;
import com.sun.ts.tests.jaxws.ee.w2j.document.literal.customization.external.custom.pkg.CustomizationExternalTestException;
import com.sun.ts.tests.jaxws.ee.w2j.document.literal.customization.external.custom.pkg.CustomizationExternalTestService;

import jakarta.xml.ws.Holder;

public class Client extends BaseClient {
	// need to create jaxbContext
	private static final ObjectFactory of = new ObjectFactory();

	private static final String PKG_NAME = "com.sun.ts.tests.jaxws.ee.w2j.document.literal.customization.external.";

	// service and port information
	private static final String NAMESPACEURI = "http://customizationexternaltest.org/wsdl";

	private static final String SERVICE_NAME = "myService";

	private static final String PORT_NAME = "HelloPort";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME = new QName(NAMESPACEURI, PORT_NAME);

	// URL properties used by the test
	private static final String ENDPOINT_URL = "w2jcustomizationexternaltest.endpoint.1";

	private static final String WSDLLOC_URL = "w2jcustomizationexternaltest.wsdlloc.1";

	private String url = null;

	private URL wsdlurl = null;

	Hello port = null;

	static CustomizationExternalTestService service = null;

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		return createWebArchive(Client.class);
	}

	protected void getService() {
		service = (CustomizationExternalTestService) getSharedObject();
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
		port = (Hello) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, CustomizationExternalTestService.class, PORT_QNAME,
				Hello.class);
		JAXWS_Util.setTargetEndpointAddress(port, url);
	}

	protected void getPortJavaEE() throws Exception {
		logger.log(Level.INFO, "Obtaining service via WebServiceRef annotation");
		logger.log(Level.INFO, "service=" + service);
		port = (Hello) JAXWS_Util.getPort(service, PORT_QNAME, Hello.class);
		// port = (Hello) service.getMyHelloPort();
		logger.log(Level.INFO, "port=" + port);
		logger.log(Level.INFO, "Obtained port");
		JAXWS_Util.dumpTargetEndpointAddress(port);
		// JAXWS_Util.setSOAPLogging(port);
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
	 * @testName: CustomizationExternalTest
	 *
	 * @assertion_ids: JAXWS:SPEC:8000; JAXWS:SPEC:8001; JAXWS:SPEC:8003;
	 * JAXWS:SPEC:8004; JAXWS:SPEC:8005; JAXWS:SPEC:8006; JAXWS:SPEC:8007;
	 * JAXWS:SPEC:8008; JAXWS:SPEC:8010; JAXWS:SPEC:8012; JAXWS:SPEC:8013;
	 * JAXWS:SPEC:2064; JAXWS:SPEC:7000; JAXWS:SPEC:8009;
	 *
	 * @test_Strategy: An external customization file is used to change aspects of
	 * the wsdl file. If the endpoint is reachable then the customization worked.
	 *
	 */
	@Test
	public void CustomizationExternalTest() throws Exception {
		TestUtil.logTrace("CustomizationExternalTest");
		boolean pass = true;
		String reqStr = "Hello";
		String resStr = "Hello, World!";
		try {
			HelloElement helloReq = of.createHelloElement();
			helloReq.setArgument(reqStr);
			Holder<HelloElement> holder = new Holder<HelloElement>();
			holder.value = helloReq;
			port.myHello(holder);
			String result = holder.value.getArgument();
			logger.log(Level.INFO, "result=" + result);
			if (!result.equals(resStr)) {
				TestUtil.logErr("expected: " + resStr + ", received: " + result);
				pass = false;
			}
			logger.log(Level.INFO, "Testing Exception Exception Case ...");
			helloReq.setArgument("Exception Case");
			holder.value = helloReq;
			try {
				port.myHello(holder);
				TestUtil.logErr("CustomizationExternalTestException expected but not thrown");
				pass = false;
			} catch (CustomizationExternalTestException e) {
				logger.log(Level.INFO, "Got expected CustomizationExternalTestException");
			}
		} catch (Exception e) {
			pass = false;
			e.printStackTrace();
		}

		if (!pass)
			throw new Exception("CustomizationExternalTest failed");
	}

}
