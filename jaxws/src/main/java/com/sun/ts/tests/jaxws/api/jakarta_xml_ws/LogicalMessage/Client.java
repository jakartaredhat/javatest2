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

package com.sun.ts.tests.jaxws.api.jakarta_xml_ws.LogicalMessage;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.porting.TSURL;
import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;
import com.sun.ts.tests.jaxws.sharedclients.doclithelloclient.Hello3;
import com.sun.ts.tests.jaxws.sharedclients.doclithelloclient.Hello3Request;
import com.sun.ts.tests.jaxws.sharedclients.doclithelloclient.Hello3Response;
import com.sun.ts.tests.jaxws.sharedclients.doclithelloclient.HelloService;

import jakarta.xml.ws.Binding;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.handler.Handler;

import com.sun.ts.tests.jaxws.common.BaseClient;

public class Client extends BaseClient {
	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	private static final String PKG_NAME = "com.sun.ts.tests.jaxws.api.jakarta_xml_ws.LogicalMessage.";

	private static final String SHARED_CLIENT_PKG = "com.sun.ts.tests.jaxws.sharedclients.doclithelloclient.";

	private static final String NAMESPACEURI = "http://helloservice.org/wsdl";

	private static final String SERVICE_NAME = "HelloService";

	private static final String PORT_NAME = "Hello3Port";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	// URL properties used by the test
	private static final String ENDPOINT_URL = "dlhelloservice.endpoint.3";

	private static final String WSDLLOC_URL = "dlhelloservice.wsdlloc.3";

	private String url = null;

	private URL wsdlurl = null;

	private Hello3 port = null;

	private BindingProvider bp = null;

	private Binding binding = null;

	private List<Binding> listOfBindings = new ArrayList<Binding>();

	private List<Handler> portHandlerChain = null;

	private static final Class SERVICE_CLASS = com.sun.ts.tests.jaxws.sharedclients.doclithelloclient.HelloService.class;

	static HelloService service = null;

	protected void getTestURLs() throws Exception {
		logger.log(Level.INFO, "Get URL's used by the test");
		String file = JAXWS_Util.getURLFromProp(ENDPOINT_URL);
		url = ctsurl.getURLString(PROTOCOL, hostname, portnum, file);
		file = JAXWS_Util.getURLFromProp(WSDLLOC_URL);
		wsdlurl = ctsurl.getURL(PROTOCOL, hostname, portnum, file);
		logger.log(Level.INFO, "Service Endpoint URL: " + url);
		logger.log(Level.INFO, "WSDL Location URL:    " + wsdlurl);
	}

	private void getPortsStandalone() throws Exception {
		getPorts();
		JAXWS_Util.setTargetEndpointAddress(port, url);
	}

	private void getPortsJavaEE() throws Exception {
		logger.log(Level.INFO, "Obtaining service via WebServiceRef annotation");
		logger.log(Level.INFO, "service=" + service);
		getPorts();
		getTargetEndpointAddress(port);
	}

	private void getTargetEndpointAddress(Object port) throws Exception {
		logger.log(Level.INFO, "Get Target Endpoint Address for port=" + port);
		String url = JAXWS_Util.getTargetEndpointAddress(port);
		logger.log(Level.INFO, "Target Endpoint Address=" + url);
	}

	private void getPorts() throws Exception {
		TestUtil.logTrace("entering getPorts");

		logger.log(Level.INFO, "Get port = " + PORT_NAME);
		port = (Hello3) service.getPort(Hello3.class);
		logger.log(Level.INFO, "port=" + port);

		logger.log(Level.INFO, "Get binding for port = " + PORT_NAME);
		bp = (BindingProvider) port;
		binding = bp.getBinding();
		portHandlerChain = binding.getHandlerChain();
		logger.log(Level.INFO, "Port HandlerChain =" + portHandlerChain);
		logger.log(Level.INFO, "Port HandlerChain size = " + portHandlerChain.size());

		listOfBindings.add(binding);

		TestUtil.logTrace("leaving getPorts");
	}

	/* Test setup */

	/*
	 * @class.testArgs: -ap jaxws-url-props.dat
	 * 
	 * @class.setup_props: webServerHost; webServerPort; platform.mode;
	 */
	@BeforeEach
	public void setup() throws Exception {
		boolean pass = true;

		try {
			hostname = System.getProperty(WEBSERVERHOSTPROP);
			if (hostname == null)
				pass = false;
			else if (hostname.equals(""))
				pass = false;
			try {
				portnum = Integer.parseInt(System.getProperty(WEBSERVERPORTPROP));
			} catch (Exception e) {
				TestUtil.printStackTrace(e);
				pass = false;
			}
			modeProperty = System.getProperty(MODEPROP);
			if (modeProperty.equals("standalone")) {
				logger.log(Level.INFO, "Create Service object");
				getTestURLs();
				service = (HelloService) JAXWS_Util.getService(wsdlurl, SERVICE_QNAME, SERVICE_CLASS);
			} else {
				getTestURLs();
				logger.log(Level.INFO, "WebServiceRef is not set in Client (get it from specific vehicle)");
				service = (HelloService) getSharedObject();

			}

		} catch (Exception e) {
			TestUtil.printStackTrace(e);
			throw new Exception("setup failed:", e);
		}
		if (!pass) {
			TestUtil.logErr("Please specify host & port of web server " + "in config properties: " + WEBSERVERHOSTPROP
					+ ", " + WEBSERVERPORTPROP);
			throw new Exception("setup failed:");
		}
		logger.log(Level.INFO, "setup ok");
	}

	@AfterEach
	public void cleanup() throws Exception {
		logger.log(Level.INFO, "cleanup ok");
	}

	/*
	 * @testName: SetGetPayloadSourceTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:30; JAXWS:JAVADOC:32; JAXWS:SPEC:6002;
	 * WS4EE:SPEC:5005; JAXWS:SPEC:5000; JAXWS:SPEC:5001; JAXWS:SPEC:5002;
	 * JAXWS:SPEC:5003; JAXWS:SPEC:7000; JAXWS:SPEC:7002; JAXWS:SPEC:7008;
	 * JAXWS:SPEC:7009;
	 *
	 * @test_Strategy: Test the various getPayload and setPayload methods in the
	 * handlers
	 */
	@Test
	public void SetGetPayloadSourceTest() throws Exception {
		TestUtil.logTrace("SetGetPayloadSourceTest");
		boolean pass = true;
		if (!setupPorts()) {
			pass = false;
		} else {
			try {
				String expected = "client:OutboundClientLogicalHandler_getsetPayloadSource:InboundServerLogicalHandler_getsetPayloadSource:Hello3Impl:OutboundServerLogicalHandler_getsetPayloadSource:InboundClientLogicalHandler_getsetPayloadSource";

				Hello3Request hello3Req = new Hello3Request();
				hello3Req.setTestname("setgetPayloadSourceTest");
				hello3Req.setArgument("client");

				Hello3Response hello3Res = port.hello(hello3Req);
				String actual = hello3Res.getArgument();
				logger.log(Level.INFO, "Hello3Response=" + actual);
				if (!actual.equals(expected)) {
					TestUtil.logErr("Error: Did not get expected result:");
					TestUtil.logErr("expected=" + expected);
					TestUtil.logErr("actual=" + actual);
					pass = false;
				}
			} catch (Exception e) {
				pass = false;
				e.printStackTrace();
			}
		}
		if (!pass)
			throw new Exception("SetGetPayloadSourceTest failed");
	}

	/*
	 * @testName: SetGetPayloadJAXBContextTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:31; JAXWS:JAVADOC:33; JAXWS:SPEC:6002;
	 * WS4EE:SPEC:5005; JAXWS:SPEC:5000; JAXWS:SPEC:5001; JAXWS:SPEC:5002;
	 * JAXWS:SPEC:5003; JAXWS:SPEC:7000; JAXWS:SPEC:7002; JAXWS:SPEC:7008;
	 * JAXWS:SPEC:7009;
	 *
	 * @test_Strategy: Test the various getPayload and setPayload methods in the
	 * handlers
	 */
	@Test
	public void SetGetPayloadJAXBContextTest() throws Exception {
		TestUtil.logTrace("SetGetPayloadJAXBContextTest");
		boolean pass = true;
		if (!setupPorts()) {
			pass = false;
		} else {
			try {
				String expected = "client:OutboundClientLogicalHandler_getsetPayloadJAXBContext:InboundServerLogicalHandler_getsetPayloadJAXBContext:Hello3Impl:OutboundServerLogicalHandler_getsetPayloadJAXBContext:InboundClientLogicalHandler_getsetPayloadJAXBContext";

				Hello3Request hello3Req = new Hello3Request();
				hello3Req.setTestname("setgetPayloadJAXBContextTest");
				hello3Req.setArgument("client");

				Hello3Response hello3Res = port.hello(hello3Req);
				String actual = hello3Res.getArgument();
				logger.log(Level.INFO, "Hello3Response=" + actual);
				if (!actual.equals(expected)) {
					TestUtil.logErr("Error: Did not get expected result:");
					TestUtil.logErr("expected=" + expected);
					TestUtil.logErr("actual=" + actual);
					pass = false;
				}
			} catch (Exception e) {
				pass = false;
				e.printStackTrace();
			}
		}
		if (!pass)
			throw new Exception("SetGetPayloadJAXBContextTest failed");
	}

}
