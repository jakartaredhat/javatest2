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

package com.sun.ts.tests.jaxws.api.jakarta_xml_ws_http.HTTPBinding;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaxws.common.Constants;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;
import com.sun.ts.tests.jaxws.sharedclients.xmlbinddlhelloproviderclient.HelloRequest;
import com.sun.ts.tests.jaxws.sharedclients.xmlbinddlhelloproviderclient.HelloResponse;
import com.sun.ts.tests.jaxws.sharedclients.xmlbinddlhelloproviderclient.ObjectFactory;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.ws.Binding;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.Dispatch;
import jakarta.xml.ws.EndpointReference;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.handler.Handler;
import jakarta.xml.ws.http.HTTPBinding;

import com.sun.ts.tests.jaxws.common.BaseClient;

public class Client extends BaseClient {

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	// need to create jaxbContext
	private static final ObjectFactory of = new ObjectFactory();

	private static final String PKG_NAME = "com.sun.ts.tests.jaxws.api.jakarta_xml_ws.LogicalMessage.";

	private static final String SHARED_CLIENT_PKG = "com.sun.ts.tests.jaxws.sharedclients.xmlbinddlhelloproviderclient.";

	private static final String NAMESPACEURI = "http://helloservice.org/wsdl";

	private static final String SERVICE_NAME = "HelloService";

	private static final String PORT_NAME = "HelloPort";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME = new QName(NAMESPACEURI, PORT_NAME);

	private static final Class SERVICE_CLASS = com.sun.ts.tests.jaxws.sharedclients.xmlbinddlhelloproviderclient.HelloService.class;

	// URL properties used by the test
	private static final String ENDPOINT_URL = "xmlbinddlhelloproviderservice.endpoint.1";

	private String url = null;

	private String bindingID = null;

	private Dispatch<Source> dispatchSrc = null;

	private Dispatch<Object> dispatchJaxb = null;

	static jakarta.xml.ws.Service service = null;

	private Binding binding = null;

	private BindingProvider bpDispatch = null;

	private static final Class JAXB_OBJECT_FACTORY = ObjectFactory.class;

	private String helloReq = "<HelloRequest xmlns=\"http://helloservice.org/types\"><argument>foo</argument></HelloRequest>";

	private JAXBContext createJAXBContext() {
		try {
			return JAXBContext.newInstance(JAXB_OBJECT_FACTORY);
		} catch (jakarta.xml.bind.JAXBException e) {
			throw new WebServiceException(e.getMessage(), e);
		}
	}

	private Dispatch<Object> createDispatchJAXB() throws Exception {
		jakarta.xml.ws.Service service = jakarta.xml.ws.Service.create(SERVICE_QNAME);
		service.addPort(PORT_QNAME, bindingID, url);
		return service.createDispatch(PORT_QNAME, createJAXBContext(), jakarta.xml.ws.Service.Mode.PAYLOAD);
	}

	private Dispatch<Source> createDispatchSource() throws Exception {
		jakarta.xml.ws.Service service = jakarta.xml.ws.Service.create(SERVICE_QNAME);
		service.addPort(PORT_QNAME, bindingID, url);
		return service.createDispatch(PORT_QNAME, Source.class, jakarta.xml.ws.Service.Mode.PAYLOAD);
	}

	protected void getTestURLs() throws Exception {
		logger.log(Level.INFO, "Get URL's used by the test");
		String file = JAXWS_Util.getURLFromProp(ENDPOINT_URL);
		url = ctsurl.getURLString(PROTOCOL, hostname, portnum, file);
		logger.log(Level.INFO, "Service Endpoint URL: " + url);
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
		service = jakarta.xml.ws.Service.create(SERVICE_QNAME);

		bindingID = HTTPBinding.HTTP_BINDING;
		dispatchSrc = createDispatchSource();
		bpDispatch = (BindingProvider) dispatchSrc;
	}

	@AfterEach
	public void cleanup() throws Exception {
		logger.log(Level.INFO, "cleanup ok");
	}

	/*
	 * @testName: getHTTPBindingTest
	 *
	 * @assertion_ids: JAXWS:SPEC:11000; WS4EE:SPEC:5005; JAXWS:SPEC:7012;
	 * JAXWS:SPEC:3039;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void getHTTPBindingTest() throws Exception {
		TestUtil.logTrace("getHTTPBindingTest");
		boolean pass = true;
		logger.log(Level.INFO, "Get Binding interface for Dispatch object");
		binding = bpDispatch.getBinding();
		if (binding == null) {
			TestUtil.logErr("getBinding() returned null");
			pass = false;
		} else {
			if (binding instanceof HTTPBinding) {
				logger.log(Level.INFO, "binding is a HTTPBinding instance");
			} else {
				TestUtil.logErr("binding is not a HTTPBinding instance");
				pass = false;
			}
		}
		if (!pass)
			throw new Exception("getHTTPBindingTest failed");
	}

	/*
	 * @testName: HTTPBindingConstantsTest
	 *
	 * @assertion_ids: JAXWS:SPEC:11000; WS4EE:SPEC:5005;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void HTTPBindingConstantsTest() throws Exception {
		TestUtil.logTrace("HTTPBindingConstantsTest");
		boolean pass = true;

		logger.log(Level.INFO, "Verify that HTTP_BINDING constant value is correct");
		if (!HTTPBinding.HTTP_BINDING.equals(Constants.EXPECTED_HTTP_BINDING)) {
			TestUtil.logErr("HTTP_BINDING is incorrect");
			TestUtil.logErr("Got: [" + HTTPBinding.HTTP_BINDING + "]");
			TestUtil.logErr("Expected: [" + Constants.EXPECTED_HTTP_BINDING + "]");
			pass = false;
		}
		if (!pass)
			throw new Exception("HTTPBindingConstantsTest failed");
	}

	/*
	 * @testName: invokeTestJAXB
	 *
	 * @assertion_ids: JAXWS:SPEC:11000; WS4EE:SPEC:5005; JAXWS:SPEC:7012;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void invokeTestJAXB() throws Exception {
		TestUtil.logTrace("invokeTestJAXB");
		boolean pass = true;
		HelloRequest helloReq = null;
		try {
			helloReq = of.createHelloRequest();
			helloReq.setArgument("foo");
		} catch (Exception e) {
			e.printStackTrace();
		}
		HelloResponse helloRes = null;
		try {
			dispatchJaxb = createDispatchJAXB();
			binding = ((BindingProvider) dispatchJaxb).getBinding();
			if (binding instanceof HTTPBinding) {
				logger.log(Level.INFO, "binding is a HTTPBinding instance");
			} else {
				TestUtil.logErr("binding is not a HTTPBinding instance");
				pass = false;
			}
			java.util.Map<String, Object> reqContext = dispatchJaxb.getRequestContext();
			logger.log(Level.INFO, "Calling invoke ....");
			helloRes = (HelloResponse) dispatchJaxb.invoke(helloReq);
			logger.log(Level.INFO, "After invoke ....");
			logger.log(Level.INFO, "HelloRequest=" + helloReq.getArgument());
			logger.log(Level.INFO, "HelloResponse=" + helloRes.getArgument());
			if (!helloRes.getArgument().equals(helloReq.getArgument()))
				pass = false;
		} catch (Exception e) {
			pass = false;
			e.printStackTrace();
		}
		if (!pass)
			throw new Exception("invokeTestJAXB failed");
	}

	/*
	 * @testName: invokeTestXML
	 *
	 * @assertion_ids: JAXWS:SPEC:4014; JAXWS:JAVADOC:8; WS4EE:SPEC:5005;
	 * JAXWS:SPEC:7012;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void invokeTestXML() throws Exception {
		TestUtil.logTrace("invokeTestXML");
		boolean pass = true;
		Source reqMsg = JAXWS_Util.makeSource(helloReq, "StreamSource");
		String resStr;
		try {
			logger.log(Level.INFO, "Calling invoke ....");
			Source resMsg = dispatchSrc.invoke(reqMsg);
			logger.log(Level.INFO, "After invoke ....");
			try {
				resStr = JAXWS_Util.getDOMResultAsString(JAXWS_Util.getSourceAsDOMResult(resMsg));
				logger.log(Level.INFO, "resStr=" + resStr);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			pass = false;
			e.printStackTrace();
		}
		if (!pass)
			throw new Exception("invokeTestXML failed");
	}

	/*
	 * @testName: invokeTestJAXBBad
	 *
	 * @assertion_ids: JAXWS:SPEC:11000; WS4EE:SPEC:5005; JAXWS:SPEC:6004;
	 * JAXWS:SPEC:4012; JAXWS:SPEC:4019;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void invokeTestJAXBBad() throws Exception {
		TestUtil.logTrace("invokeTestJAXBBad");
		boolean pass = true;
		String helloReq = "Hello Request";
		HelloResponse helloRes = null;
		try {
			dispatchJaxb = createDispatchJAXB();
			binding = ((BindingProvider) dispatchJaxb).getBinding();
			if (binding instanceof HTTPBinding) {
				logger.log(Level.INFO, "binding is a HTTPBinding instance");
			} else {
				TestUtil.logErr("binding is not a HTTPBinding instance");
				pass = false;
			}
			java.util.Map<String, Object> reqContext = dispatchJaxb.getRequestContext();
			logger.log(Level.INFO, "Calling invoke ....");
			dispatchJaxb.invoke(helloReq);
			TestUtil.logErr("No WebServiceException from bad invoke");
			pass = false;
		} catch (WebServiceException e) {
			logger.log(Level.INFO, "Got expected runtime exception WebServiceException" + e);
		} catch (Exception e) {
			TestUtil.logErr("Unexpected exception occurred: " + e);
			pass = false;
		}
		if (!pass)
			throw new Exception("invokeTestJAXBBad failed");
	}

	/*
	 * @testName: invokeTestJAXBNull
	 *
	 * @assertion_ids: JAXWS:SPEC:11000; WS4EE:SPEC:5005; JAXWS:SPEC:2036;
	 * JAXWS:SPEC:4013; JAXWS:SPEC:4015;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void invokeTestJAXBNull() throws Exception {
		TestUtil.logTrace("invokeTestJAXBNull");
		boolean pass = true;
		HelloRequest helloReq = null;
		HelloResponse helloRes = null;
		try {
			dispatchJaxb = createDispatchJAXB();
			binding = ((BindingProvider) dispatchJaxb).getBinding();
			if (binding instanceof HTTPBinding) {
				logger.log(Level.INFO, "binding is a HTTPBinding instance");
			} else {
				TestUtil.logErr("binding is not a HTTPBinding instance");
				pass = false;
			}
			java.util.Map<String, Object> reqContext = dispatchJaxb.getRequestContext();
			logger.log(Level.INFO, "Calling invoke ....");
			dispatchJaxb.invoke(helloReq);
			TestUtil.logErr("No WebServiceException from bad invoke");
			pass = false;
		} catch (WebServiceException e) {
			logger.log(Level.INFO, "Got expected WebServiceException" + e);
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Received unexpected exception", e);
		}
		if (!pass)
			throw new Exception("invokeTestJAXBNull failed");
	}

	/*
	 * @testName: incompatibleHandlerTest
	 *
	 * @assertion_ids: JAXWS:SPEC:10006;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void incompatibleHandlerTest() throws Exception {
		TestUtil.logTrace("incompatibleHandlerTest");
		boolean pass = true;
		logger.log(Level.INFO, "Getting the Binding");
		binding = bpDispatch.getBinding();
		if (binding == null) {
			TestUtil.logErr("getBinding() returned null");
			pass = false;
		} else {
			if (binding instanceof HTTPBinding) {
				logger.log(Level.INFO, "binding is a HTTPBinding instance");
				try {
					List<Handler> handlerList = new ArrayList<Handler>();
					Handler handler = new com.sun.ts.tests.jaxws.sharedclients.xmlbinddlhelloproviderclient.SOAPHandler();
					handlerList.add(handler);
					logger.log(Level.INFO, "HandlerChain=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					binding.setHandlerChain(handlerList);
					TestUtil.logErr("Adding an incompatible handler did not throw a WebServiceException");
					pass = false;
				} catch (WebServiceException wse) {
					// test passed
					TestUtil.logTrace("WebServiceException was thrown");
				}
			} else {
				TestUtil.logErr("binding is not a HTTPBinding instance");
				pass = false;
			}
		}
		if (!pass)
			throw new Exception("incompatibleHandlerTest failed");
	}

	/*
	 * @testName: getEndpointReferenceTest
	 *
	 * @assertion_ids: JAXWS:SPEC:5023.4; JAXWS:SPEC:4024; JAXWS:SPEC:5024.4;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void getEndpointReferenceTest() throws Exception {
		TestUtil.logTrace("getEndpointReferenceTest");
		boolean pass = false;
		try {
			TestUtil.logMsg("Attempt to get EndpointReference for HTTP Binding object");
			EndpointReference epr = dispatchSrc.getEndpointReference();
			TestUtil.logErr("Did not catch expected UnsupportedOperationException");
		} catch (UnsupportedOperationException e) {
			logger.log(Level.INFO, "Caught expected UnsupportedOperationException");
			pass = true;
		} catch (Exception e) {
			TestUtil.logErr("Received unexpected exception", e);
		}
		if (!pass)
			throw new Exception("getEndpointReferenceTest failed");
	}
}
