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
 *  $Id$
 */
package com.sun.ts.tests.jaxws.wsa.w2j.document.literal.providertest;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;

import javax.xml.namespace.QName;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.tests.jaxws.common.BaseClient;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.ws.Dispatch;
import jakarta.xml.ws.RespectBindingFeature;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.WebServiceFeature;
import jakarta.xml.ws.soap.AddressingFeature;
import jakarta.xml.ws.soap.MTOMFeature;

public class Client extends BaseClient {

	private static final ObjectFactory of = new ObjectFactory();

	// ServiceName and PortName mapping configuration going java-to-wsdl
	private static final String SERVICE_NAME = "ProviderTestService";

	private static final String PORT_NAME = "ProviderTestPort";

	private static final String PORT_TYPE_NAME = "ProviderTest";

	private static final String INPUT_MSG_NAME = "helloRequest";

	private static final String NAMESPACEURI = "http://providertestservice.org/wsdl";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME = new QName(NAMESPACEURI, PORT_NAME);

	// URL properties used by the test
	private static final String ENDPOINT_URL = "providertest.endpoint.1";

	private static final String WSDLLOC_URL = "providertest.wsdlloc.1";

	private String url = null;

	private URL wsdlurl = null;

	ProviderTest port = null;

	private WebServiceFeature[] wsf = { new AddressingFeature(true), new MTOMFeature(true),
			new RespectBindingFeature(true) };

	static ProviderTestService service = null;

	private Dispatch<Object> dispatchJaxb = null;

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	private static final Class SERVICE_CLASS = com.sun.ts.tests.jaxws.wsa.w2j.document.literal.providertest.ProviderTestService.class;

	private static final Class JAXB_OBJECT_FACTORY = com.sun.ts.tests.jaxws.wsa.w2j.document.literal.providertest.ObjectFactory.class;

	private JAXBContext createJAXBContext() {
		try {
			return JAXBContext.newInstance(JAXB_OBJECT_FACTORY);
		} catch (jakarta.xml.bind.JAXBException e) {
			throw new WebServiceException(e.getMessage(), e);
		}
	}

	private Dispatch<Object> createDispatchJAXB() throws Exception {
		return service.createDispatch(PORT_QNAME, createJAXBContext(), jakarta.xml.ws.Service.Mode.PAYLOAD, wsf);
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
		port = (ProviderTest) service.getPort(ProviderTest.class, wsf);
		logger.log(Level.INFO, "port=" + port);
	}

	protected void getPortStandalone() throws Exception {
		port = (ProviderTest) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, ProviderTestService.class, PORT_QNAME,
				ProviderTest.class, wsf);
		JAXWS_Util.setTargetEndpointAddress(port, url);
		service = (ProviderTestService) JAXWS_Util.getService(wsdlurl, SERVICE_QNAME, SERVICE_CLASS);
	}

	private void getTargetEndpointAddress(Object port) throws Exception {
		logger.log(Level.INFO, "Get Target Endpoint Address for port=" + port);
		String url = JAXWS_Util.getTargetEndpointAddress(port);
		logger.log(Level.INFO, "Target Endpoint Address=" + url);
	}

	protected void getService() {
		service = (ProviderTestService) getSharedObject();
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
	 * @testName: WebServiceFeaturesOnProviderTest
	 *
	 * @assertion_ids: JAXWS:SPEC:6017; JAXWS:SPEC:5025; JAXWS:JAVADOC:189
	 *
	 * @test_Strategy: enable the webservice features on the impl then ensure the
	 * endpoint can be reached
	 */
	@Test
	public void WebServiceFeaturesOnProviderTest() throws Exception {
		logger.log(Level.INFO, "WebServiceFeaturesOnProviderTest");
		boolean pass = true;
		HelloRequest helloReq = null;
		try {
			helloReq = of.createHelloRequest();
			helloReq.setArgument("WebServiceFeaturesOnProviderTest");
		} catch (Exception e) {
			e.printStackTrace();
		}
		HelloResponse helloRes = null;
		try {
			dispatchJaxb = createDispatchJAXB();
			dispatchJaxb.invoke(helloReq);
		} catch (Throwable t) {
			t.printStackTrace();
			throw new Exception(t.toString());
		}

		if (!pass)
			throw new Exception("WebServiceFeaturesOnProviderTest failed");
	}

}
