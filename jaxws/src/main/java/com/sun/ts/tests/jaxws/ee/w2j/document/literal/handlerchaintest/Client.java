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
 *  $Id$
 */
package com.sun.ts.tests.jaxws.ee.w2j.document.literal.handlerchaintest;

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

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.ws.Dispatch;
import jakarta.xml.ws.WebServiceException;

public class Client extends BaseClient {

	private static final ObjectFactory of = new ObjectFactory();

	// ServiceName and PortName mapping configuration going java-to-wsdl
	private static final String SERVICE_NAME = "HandlerChainTestService";

	private static final String PORT_NAME = "HandlerChainTestPort";

	private static final String NAMESPACEURI = "http://handlerchaintestservice.org/wsdl";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME = new QName(NAMESPACEURI, PORT_NAME);

	// URL properties used by the test
	private static final String ENDPOINT_URL = "w2jdlhandlerchaintest.endpoint.1";

	private static final String WSDLLOC_URL = "w2jdlhandlerchaintest.wsdlloc.1";

	private String url = null;

	private URL wsdlurl = null;

	HandlerChainTest port = null;

	static HandlerChainTestService service = null;

	private Dispatch<Object> dispatchJaxb = null;

	private static final Class SERVICE_CLASS = com.sun.ts.tests.jaxws.ee.w2j.document.literal.handlerchaintest.HandlerChainTestService.class;

	private static final Class JAXB_OBJECT_FACTORY = com.sun.ts.tests.jaxws.ee.w2j.document.literal.handlerchaintest.ObjectFactory.class;

	private JAXBContext createJAXBContext() {
		try {
			return JAXBContext.newInstance(JAXB_OBJECT_FACTORY);
		} catch (jakarta.xml.bind.JAXBException e) {
			throw new WebServiceException(e.getMessage(), e);
		}
	}

	private Dispatch<Object> createDispatchJAXB() throws Exception {
		return service.createDispatch(PORT_QNAME, createJAXBContext(), jakarta.xml.ws.Service.Mode.PAYLOAD);
	}

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		return createWebArchive(Client.class);
	}

	protected void getService() {
		service = (HandlerChainTestService) getSharedObject();
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

	protected void getPortJavaEE() throws Exception {
		port = (HandlerChainTest) service.getHandlerChainTestPort();
		logger.log(Level.INFO, "port=" + port);
		logger.log(Level.INFO, "Obtained port");
		JAXWS_Util.dumpTargetEndpointAddress(port);
	}

	protected void getPortStandalone() throws Exception {
		service = (HandlerChainTestService) JAXWS_Util.getService(wsdlurl, SERVICE_QNAME, SERVICE_CLASS);
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
	 * @testName: HandlerChainOnProviderTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9009.2
	 *
	 * @test_Strategy: Use a handler that is specified on the Provider and see that
	 * it transforms the body
	 */
	@Test
	public void HandlerChainOnProviderTest() throws Exception {
		logger.log(Level.INFO, "HandlerChainOnProviderTest");
		boolean pass = true;
		String expected1 = "OutboundServerLogicalHandler";
		String expected2 = "InboundServerLogicalHandler";
		String expected3 = "InboundServerSOAPHandler";
		String expected4 = "OutboundServerSOAPHandler";

		HelloRequest helloReq = null;
		try {
			helloReq = of.createHelloRequest();
			helloReq.setArgument("HandlerChainOnProviderTest");
		} catch (Exception e) {
			e.printStackTrace();
		}
		HelloResponse helloRes = null;
		try {
			dispatchJaxb = createDispatchJAXB();
			helloRes = (HelloResponse) dispatchJaxb.invoke(helloReq);
			String result = helloRes.getArgument();
			logger.log(Level.INFO, "Return value = " + result);
			if (result.indexOf(expected1) == -1) {
				pass = false;
				TestUtil.logErr("The value:" + expected1 + " was not found ");
				TestUtil.logErr("in the result:" + result);
			}
			if (result.indexOf(expected2) == -1) {
				pass = false;
				TestUtil.logErr("The value:" + expected2 + " was not found ");
				TestUtil.logErr("in the result:" + result);
			}
			if (result.indexOf(expected3) == -1) {
				pass = false;
				TestUtil.logErr("The value:" + expected3 + " was not found ");
				TestUtil.logErr("in the result:" + result);
			}
			if (result.indexOf(expected4) == -1) {
				pass = false;
				TestUtil.logErr("The value:" + expected4 + " was not found ");
				TestUtil.logErr("in the result:" + result);
			}
		} catch (Throwable t) {
			t.printStackTrace();
			throw new Exception(t.toString());
		}

		if (!pass)
			throw new Exception("HandlerChainOnProviderTest failed");
	}

}
