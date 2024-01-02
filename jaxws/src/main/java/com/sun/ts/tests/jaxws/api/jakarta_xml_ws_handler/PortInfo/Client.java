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

package com.sun.ts.tests.jaxws.api.jakarta_xml_ws_handler.PortInfo;

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
import com.sun.ts.tests.jaxws.common.BaseClient;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;
import com.sun.ts.tests.jaxws.sharedclients.doclithelloclient.Hello;
import com.sun.ts.tests.jaxws.sharedclients.doclithelloclient.HelloService;

import jakarta.xml.ws.Binding;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.handler.Handler;
import jakarta.xml.ws.handler.HandlerResolver;
import jakarta.xml.ws.handler.PortInfo;
import jakarta.xml.ws.soap.SOAPBinding;

public class Client extends BaseClient {

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	private static final String PKG_NAME = "com.sun.ts.tests.jaxws.api.jakarta_xml_ws_handler.PortInfo.";

	private static final String NAMESPACEURI = "http://helloservice.org/wsdl";

	private static final String SERVICE_NAME = "HelloService";

	private static final String PORT_NAME = "HelloPort";

	private static final QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private static final QName PORT_QNAME = new QName(NAMESPACEURI, PORT_NAME);

	private static final Class SERVICE_CLASS = com.sun.ts.tests.jaxws.sharedclients.doclithelloclient.HelloService.class;

	private static final Class SEI_CLASS = com.sun.ts.tests.jaxws.sharedclients.doclithelloclient.Hello.class;

	private static final String BINDING_ID = SOAPBinding.SOAP11HTTP_BINDING;

	private static PortInfo pinfo = null;

	static HelloService service = null;

	// URL properties used by the test
	private static final String ENDPOINT_URL = "dlhelloservice.endpoint.1";

	private static final String WSDLLOC_URL = "dlhelloservice.wsdlloc.1";

	private String url = null;

	private URL wsdlurl = null;

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
	 */@BeforeEach
	public void setup() throws Exception {
		super.setup();
	}

	@AfterEach
	public void cleanup() throws Exception {
		logger.log(Level.INFO, "cleanup ok");
	}

	/*
	 * @testName: PortInfoTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:96; JAXWS:JAVADOC:97; JAXWS:JAVADOC:98;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void PortInfoTest() throws Exception {
		TestUtil.logTrace("PortInfoTest");
		boolean pass = true;
		try {
			if (modeProperty.equals("standalone")) {
				getTestURLs();
				service = (HelloService) JAXWS_Util.getService(wsdlurl, SERVICE_QNAME, SERVICE_CLASS);
			} else {
				getTestURLs();
				logger.log(Level.INFO, "WebServiceRef is not set in Client (get it from specific vehicle)");
				service = (HelloService) getSharedObject();
			}
			service.setHandlerResolver(new HandlerResolver() {
				public List<Handler> getHandlerChain(PortInfo info) {
					logger.log(Level.INFO, "BindingID=" + info.getBindingID());
					logger.log(Level.INFO, "ServiceName=" + info.getServiceName());
					logger.log(Level.INFO, "PortName=" + info.getPortName());
					pinfo = info;
					return new ArrayList<Handler>();
				}
			});
			Hello port = (Hello) service.getPort(PORT_QNAME, SEI_CLASS);
			BindingProvider bp = (BindingProvider) port;
			Binding b = bp.getBinding();
			List<Handler> hl = b.getHandlerChain();
			logger.log(Level.INFO, "HandlerChainList=" + hl);
			logger.log(Level.INFO, "HandlerChainSize = " + hl.size());
			logger.log(Level.INFO, "ServiceName check -> " + pinfo.getServiceName());
			if (!pinfo.getServiceName().equals(SERVICE_QNAME)) {
				TestUtil.logErr(
						"ServiceName mismatch, expected: " + SERVICE_QNAME + ", received: " + pinfo.getServiceName());
				pass = false;
			}
			logger.log(Level.INFO, "PortName check -> " + pinfo.getPortName());
			if (!pinfo.getPortName().equals(PORT_QNAME)) {
				TestUtil.logErr("PortName mismatch, expected: " + PORT_QNAME + ", received: " + pinfo.getPortName());
				pass = false;
			}
			logger.log(Level.INFO, "BindingID check -> " + pinfo.getBindingID());
			if (!pinfo.getBindingID().equals(BINDING_ID)) {
				TestUtil.logErr("BindingID mismatch, expected: " + BINDING_ID + ", received: " + pinfo.getBindingID());
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("PortInfoTest failed", e);
		}

		if (!pass)
			throw new Exception("PortInfoTest failed");
	}
}
