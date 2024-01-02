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

package com.sun.ts.tests.jaxws.ee.j2w.document.literal.sessionmaintaintest.client;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;
import java.util.Map;

import javax.xml.namespace.QName;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaxws.common.BaseClient;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;

import jakarta.xml.ws.BindingProvider;

public class Client extends BaseClient {

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	private static final String PKG_NAME = "com.sun.ts.tests.jaxws.ee.j2w.document.literal.sessionmaintaintest.client.";

	// URL properties used by the test
	private static final String ENDPOINT_URL = "j2wdlsessionmaintaintest.endpoint.1";

	private static final String WSDLLOC_URL = "j2wdlsessionmaintaintest.wsdlloc.1";

	private String url = null;

	private URL wsdlurl = null;

	// ServiceName and PortName mapping configuration going java-to-wsdl
	private static final String SERVICE_NAME = "TestService";

	private static final String PORT_NAME = "TestPort";

	private static final String NAMESPACEURI = "http://test.org/wsdl";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME = new QName(NAMESPACEURI, PORT_NAME);

	Test port = null;

	static TestService service = null;

	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		return createWebArchive(Client.class);
	}

	protected void getService() {
		service = (TestService) getSharedObject();
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
		port = (Test) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, TestService.class, PORT_QNAME, Test.class);
		JAXWS_Util.setTargetEndpointAddress(port, url);
	}

	protected void getPortJavaEE() throws Exception {
		logger.log(Level.INFO, "Obtain service via WebServiceRef annotation");
		logger.log(Level.INFO, "service=" + service);
		port = (Test) service.getPort(Test.class);
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
	 * @testName: SessionMaintainPropertyNotSetTest
	 *
	 * @assertion_ids: WS4EE:SPEC:5004; JAXWS:JAVADOC:69; JAXWS:JAVADOC:129;
	 * JAXWS:SPEC:4005;
	 * 
	 * @test_Strategy: With maintain property set to false, session should not be
	 * maintained.
	 *
	 */

	@org.junit.jupiter.api.Test
	public void SessionMaintainPropertyNotSetTest() throws Exception {
		logger.log(Level.INFO, "SessionMaintainPropertyNotSetTest");
		boolean pass = true;

		try {
			String id = port.getSessionId();
			if (port.compareSessionId(id)) {
				TestUtil.logErr("Client session was maintain when it should not have been");
				pass = false;
			} else
				logger.log(Level.INFO, "Client session is not maintained");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("SessionMaintainPropertyNotSetTest failed", e);
		}
		if (!pass)
			throw new Exception("SessionMaintainPropertyNotSetTest failed");

	}

	/*
	 * @testName: SessionMaintainPropertySetToFalseTest
	 *
	 * @assertion_ids: WS4EE:SPEC:5004; JAXWS:JAVADOC:69; JAXWS:JAVADOC:129;
	 * JAXWS:SPEC:4005;
	 *
	 * @test_Strategy: With maintain property set to false, session should not be
	 * maintained.
	 *
	 */

	@org.junit.jupiter.api.Test
	public void SessionMaintainPropertySetToFalseTest() throws Exception {
		logger.log(Level.INFO, "SessionMaintainPropertySetToFalseTest");
		boolean pass = true;

		try {
			Map<String, Object> requestContext = ((BindingProvider) port).getRequestContext();
			requestContext.put(BindingProvider.SESSION_MAINTAIN_PROPERTY, Boolean.FALSE);
			String id = port.getSessionId();
			if (port.compareSessionId(id)) {
				TestUtil.logErr("Client session was maintain when it should not have been");
				pass = false;
			} else {
				logger.log(Level.INFO, "Client session is not maintained");
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("SessionMaintainPropertySetToFalseTest failed", e);
		}
		if (!pass)
			throw new Exception("SessionMaintainPropertySetToFalseTest failed");

	}

	/*
	 * @testName: SessionMaintainPropertySetToTrueTest
	 *
	 * @assertion_ids: WS4EE:SPEC:5004; JAXWS:JAVADOC:69; JAXWS:JAVADOC:129;
	 * JAXWS:SPEC:4005;
	 *
	 * @test_Strategy: With maintain property set to true, session should be
	 * maintained.
	 *
	 */

	@org.junit.jupiter.api.Test
	public void SessionMaintainPropertySetToTrueTest() throws Exception {
		logger.log(Level.INFO, "SessionMaintainPropertySetToTrueTest");
		boolean pass = true;

		try {
			Map<String, Object> requestContext = ((BindingProvider) port).getRequestContext();
			requestContext.put(BindingProvider.SESSION_MAINTAIN_PROPERTY, Boolean.TRUE);
			String id = port.getSessionId();
			if (!port.compareSessionId(id)) {
				TestUtil.logErr("Client session was not maintain when it should have been");
				pass = false;
			} else
				logger.log(Level.INFO, "Client session is maintained");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("SessionMaintainPropertySetToTrueTest failed", e);
		}
		if (!pass)
			throw new Exception("SessionMaintainPropertySetToTrueTest failed");

	}
}
