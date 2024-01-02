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

package com.sun.ts.tests.jaxws.api.jakarta_xml_ws.Dispatch;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;
import java.util.concurrent.Future;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.porting.TSURL;
import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;
import com.sun.ts.tests.jaxws.sharedclients.doclithelloclient.HelloRequest;
import com.sun.ts.tests.jaxws.sharedclients.doclithelloclient.HelloResponse;
import com.sun.ts.tests.jaxws.sharedclients.doclithelloclient.HelloService;
import com.sun.ts.tests.jaxws.sharedclients.doclithelloclient.ObjectFactory;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.ws.AsyncHandler;
import jakarta.xml.ws.Binding;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.Dispatch;
import jakarta.xml.ws.Response;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.soap.SOAPBinding;
import jakarta.xml.ws.soap.SOAPFaultException;

import com.sun.ts.tests.jaxws.common.BaseClient;

public class Client extends BaseClient {

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	// need to create jaxbContext
	private static final ObjectFactory of = new ObjectFactory();

	// Messages sent as straight XML as Source objects
	private String helloReq = "<HelloRequest xmlns=\"http://helloservice.org/types\"><argument>foo</argument></HelloRequest>";

	private String helloOneWayReq = "<HelloOneWayRequest xmlns=\"http://helloservice.org/types\"><argument>foo</argument></HelloOneWayRequest>";

	// Messages sent as SOAPMessage objects
	private String helloReqSM = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body><HelloRequest xmlns=\"http://helloservice.org/types\"><argument>foo</argument></HelloRequest></soapenv:Body></soapenv:Envelope>";

	private String helloOneWayReqSM = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body><HelloOneWayRequest xmlns=\"http://helloservice.org/types\"><argument>foo</argument></HelloOneWayRequest></soapenv:Body></soapenv:Envelope>";

	// Negative test case invalid messages
	private String helloBadReq1 = "<HelloRequest xmlns=\"http://helloservice.org/types\"><argument>foo</argument><HelloRequest>";

	private static final String PKG_NAME = "com.sun.ts.tests.jaxws.api.jakarta_xml_ws.Dispatch.";

	private static final String SHARED_CLIENT_PKG = "com.sun.ts.tests.jaxws.sharedclients.doclithelloclient";

	private static final Class SERVICE_CLASS = com.sun.ts.tests.jaxws.sharedclients.doclithelloclient.HelloService.class;

	private static final String NAMESPACEURI = "http://helloservice.org/wsdl";

	private static final String SERVICE_NAME = "HelloService";

	private static final String PORT_NAME = "HelloPort";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME = new QName(NAMESPACEURI, PORT_NAME);

	// URL properties used by the test
	private static final String ENDPOINT_URL = "dlhelloservice.endpoint.1";

	private static final String WSDLLOC_URL = "dlhelloservice.wsdlloc.1";

	private String url = null;

	private URL wsdlurl = null;

	private Dispatch<Object> dispatchJaxb = null;

	private Dispatch<Source> dispatchSrc = null;

	private Dispatch<SOAPMessage> dispatchSM = null;

	static HelloService service = null;

	private static final Class JAXB_OBJECT_FACTORY = com.sun.ts.tests.jaxws.sharedclients.doclithelloclient.ObjectFactory.class;

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

	private Dispatch<Source> createDispatchSource() throws Exception {
		return service.createDispatch(PORT_QNAME, Source.class, jakarta.xml.ws.Service.Mode.PAYLOAD);
	}

	private Dispatch<SOAPMessage> createDispatchSOAPMessage() throws Exception {
		return service.createDispatch(PORT_QNAME, SOAPMessage.class, jakarta.xml.ws.Service.Mode.MESSAGE);
	}

	class MyHandlerXML implements AsyncHandler<Source> {
		private String theData;

		public String getData() {
			return theData;
		}

		public void handleResponse(Response<Source> res) {
			logger.log(Level.INFO, "AsyncHandler: MyHandlerXML()");
			String resStr = null;
			try {
				Source resMsg = res.get();
				resStr = JAXWS_Util.getDOMResultAsString(JAXWS_Util.getSourceAsDOMResult(resMsg));
				logger.log(Level.INFO, "resStr=" + resStr);
			} catch (Exception e) {
				e.printStackTrace();
			}
			theData = resStr;
		}
	}

	class MyHandlerSOAPMessage implements AsyncHandler<SOAPMessage> {
		private String theData;

		public String getData() {
			return theData;
		}

		public void handleResponse(Response<SOAPMessage> res) {
			logger.log(Level.INFO, "AsyncHandler: MyHandlerSOAPMessage()");
			String resStr = null;
			try {
				SOAPMessage resMsg = res.get();
			} catch (Exception e) {
				e.printStackTrace();
			}
			theData = resStr;
		}
	}

	class MyHandlerJAXB implements AsyncHandler<Object> {
		private HelloResponse theData;

		public HelloResponse getData() {
			return theData;
		}

		public void handleResponse(Response<Object> res) {
			logger.log(Level.INFO, "AsyncHandler: MyHandlerJAXB()");
			try {
				HelloResponse resMsg = (HelloResponse) res.get();
				theData = resMsg;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
	 * @testName: invokeTestXML
	 *
	 * @assertion_ids: JAXWS:SPEC:4014; JAXWS:JAVADOC:8; WS4EE:SPEC:4005;
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
			dispatchSrc = createDispatchSource();
			Source resMsg = dispatchSrc.invoke(reqMsg);
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
	 * @testName: invokeAsyncTestXML
	 *
	 * @assertion_ids: JAXWS:SPEC:4014; JAXWS:JAVADOC:9; WS4EE:SPEC:4005;
	 * WS4EE:SPEC:4006; WS4EE:SPEC:4007;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void invokeAsyncTestXML() throws Exception {
		TestUtil.logTrace("invokeAsyncTestXML");
		boolean pass = true;
		Source reqMsg = JAXWS_Util.makeSource(helloReq, "StreamSource");
		String resStr;
		try {
			dispatchSrc = createDispatchSource();
			Response<Source> res = dispatchSrc.invokeAsync(reqMsg);
			logger.log(Level.INFO, "Polling and waiting for data ...");
			Object lock = new Object();
			while (!res.isDone()) {
				synchronized (lock) {
					try {
						lock.wait(50);
					} catch (InterruptedException e) {
						// ignore
					}
				}
			}

			try {
				Source resMsg = res.get();
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
			throw new Exception("invokeAsyncTestXML failed");
	}

	/*
	 * @testName: invokeAsyncHandlerTestXML
	 *
	 * @assertion_ids: JAXWS:SPEC:4014; JAXWS:JAVADOC:9; WS4EE:SPEC:4005;
	 * WS4EE:SPEC:4006; WS4EE:SPEC:4008;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void invokeAsyncHandlerTestXML() throws Exception {
		TestUtil.logTrace("invokeAsyncHandlerTestXML");
		boolean pass = true;
		Source reqMsg = JAXWS_Util.makeSource(helloReq, "StreamSource");
		MyHandlerXML handler = new MyHandlerXML();
		Future<?> future;
		try {
			dispatchSrc = createDispatchSource();
			future = dispatchSrc.invokeAsync(reqMsg, handler);
			logger.log(Level.INFO, "Polling and waiting for data ...");
			Object lock = new Object();
			while (!future.isDone()) {
				synchronized (lock) {
					try {
						lock.wait(50);
					} catch (InterruptedException e) {
						// ignore
					}
				}
			}
			TestUtil.logMsg("Data from AsyncHandler MyHandler(): " + handler.getData());
		} catch (Exception e) {
			pass = false;
			e.printStackTrace();
		}

		if (!pass)
			throw new Exception("invokeAsyncHandlerTestXML failed");
	}

	/*
	 * @testName: invokeOneWayTestXML
	 *
	 * @assertion_ids: JAXWS:SPEC:4014; JAXWS:JAVADOC:11; JAXWS:SPEC:10016;
	 * JAXWS:SPEC:6006; WS4EE:SPEC:4005;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void invokeOneWayTestXML() throws Exception {
		TestUtil.logTrace("invokeOneWayTestXML");
		boolean pass = true;
		Source reqMsg = JAXWS_Util.makeSource(helloOneWayReq, "StreamSource");
		try {
			dispatchSrc = createDispatchSource();
			dispatchSrc.invokeOneWay(reqMsg);
		} catch (Exception e) {
			pass = false;
			e.printStackTrace();
		}
		if (!pass)
			throw new Exception("invokeOneWayTestXML failed");
	}

	/*
	 * @testName: invokeTestJAXB
	 *
	 * @assertion_ids: JAXWS:SPEC:4014; JAXWS:JAVADOC:8; WS4EE:SPEC:4005;
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
			java.util.Map<String, Object> reqContext = dispatchJaxb.getRequestContext();
			helloRes = (HelloResponse) dispatchJaxb.invoke(helloReq);
			logger.log(Level.INFO, "HelloRequest: " + helloReq.getArgument());
			logger.log(Level.INFO, "HelloResponse: " + helloRes.getArgument());
			if (!helloReq.getArgument().equals(helloRes.getArgument()))
				pass = false;
		} catch (Exception e) {
			pass = false;
			e.printStackTrace();
		}
		if (!pass)
			throw new Exception("invokeTestJAXB failed");
	}

	/*
	 * @testName: invokeAsyncTestJAXB
	 *
	 * @assertion_ids: JAXWS:SPEC:4014; JAXWS:JAVADOC:9; WS4EE:SPEC:4005;
	 * WS4EE:SPEC:4006; WS4EE:SPEC:4007;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void invokeAsyncTestJAXB() throws Exception {
		TestUtil.logTrace("invokeAsyncTestJAXB");
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
			java.util.Map<String, Object> reqContext = dispatchJaxb.getRequestContext();
			Response<Object> res = dispatchJaxb.invokeAsync(helloReq);
			logger.log(Level.INFO, "Polling and waiting for data ...");
			Object lock = new Object();
			while (!res.isDone()) {
				synchronized (lock) {
					try {
						lock.wait(50);
					} catch (InterruptedException e) {
						// ignore
					}
				}
			}
			helloRes = (HelloResponse) res.get();
			logger.log(Level.INFO, "HelloRequest: " + helloReq.getArgument());
			logger.log(Level.INFO, "HelloResponse: " + helloRes.getArgument());
			if (!helloReq.getArgument().equals(helloRes.getArgument()))
				pass = false;
		} catch (Exception e) {
			pass = false;
			e.printStackTrace();
		}
		if (!pass)
			throw new Exception("invokeAsyncTestJAXB failed");
	}

	/*
	 * @testName: invokeAsyncHandlerTestJAXB
	 *
	 * @assertion_ids: JAXWS:SPEC:4014; JAXWS:JAVADOC:10; WS4EE:SPEC:4005;
	 * WS4EE:SPEC:4006; WS4EE:SPEC:4008;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void invokeAsyncHandlerTestJAXB() throws Exception {
		TestUtil.logTrace("invokeAsyncHandlerTestJAXB");
		boolean pass = true;
		HelloRequest helloReq = null;
		Future<?> future;
		try {
			helloReq = of.createHelloRequest();
			helloReq.setArgument("foo");
		} catch (Exception e) {
			e.printStackTrace();
		}
		MyHandlerJAXB handler = new MyHandlerJAXB();
		HelloResponse helloRes = null;
		try {
			dispatchJaxb = createDispatchJAXB();
			java.util.Map<String, Object> reqContext = dispatchJaxb.getRequestContext();
			future = dispatchJaxb.invokeAsync(helloReq, handler);
			logger.log(Level.INFO, "Polling and waiting for data ...");
			Object lock = new Object();
			while (!future.isDone()) {
				synchronized (lock) {
					try {
						lock.wait(50);
					} catch (InterruptedException e) {
						// ignore
					}
				}
			}
			helloRes = handler.getData();
			if (helloRes != null) {
				logger.log(Level.INFO, "HelloRequest: " + helloReq.getArgument());
				logger.log(Level.INFO, "HelloResponse: " + helloRes.getArgument());
				if (!helloReq.getArgument().equals(helloRes.getArgument()))
					pass = false;
			}
		} catch (Exception e) {
			pass = false;
			e.printStackTrace();
		}
		if (!pass)
			throw new Exception("invokeAsyncHandlerTestJAXB failed");
	}

	/*
	 * @testName: invokeOneWayTestJAXB
	 *
	 * @assertion_ids: JAXWS:SPEC:4014; JAXWS:JAVADOC:11; JAXWS:SPEC:6006;
	 * WS4EE:SPEC:4005;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void invokeOneWayTestJAXB() throws Exception {
		TestUtil.logTrace("invokeOneWayTestJAXB");
		boolean pass = true;
		HelloRequest helloReq = null;
		try {
			helloReq = of.createHelloRequest();
			helloReq.setArgument("foo");
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			dispatchJaxb = createDispatchJAXB();
			java.util.Map<String, Object> reqContext = dispatchJaxb.getRequestContext();
			dispatchJaxb.invokeOneWay(helloReq);
		} catch (Exception e) {
			pass = false;
			e.printStackTrace();
		}
		if (!pass)
			throw new Exception("invokeOneWayTestJAXB failed");
	}

	/*
	 * @testName: invokeNegativeTestXML
	 *
	 * @assertion_ids: JAXWS:SPEC:4014; JAXWS:SPEC:4015;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void invokeNegativeTestXML() throws Exception {
		TestUtil.logTrace("invokeNegativeTestXML");
		boolean pass = true;
		Source reqMsg = JAXWS_Util.makeSource(helloBadReq1, "StreamSource");
		String resStr;
		try {
			dispatchSrc = createDispatchSource();
			Source resMsg = dispatchSrc.invoke(reqMsg);
			pass = false;
			TestUtil.logErr("Did not get WebServiceException ...");
		} catch (WebServiceException e) {
			logger.log(Level.INFO, "Got expected WebServiceException");
			e.printStackTrace();
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Received unexpected exception", e);
		}
		if (!pass)
			throw new Exception("invokeNegativeTestXML failed");
	}

	/*
	 * @testName: invokeOneWayNegTestXML
	 *
	 * @assertion_ids: JAXWS:SPEC:4014; JAXWS:SPEC:4017;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void invokeOneWayNegTestXML() throws Exception {
		TestUtil.logTrace("invokeOneWayNegTestXML");
		boolean pass = true;
		Source reqMsg = JAXWS_Util.makeSource(helloBadReq1, "StreamSource");
		try {
			dispatchSrc = createDispatchSource();
			dispatchSrc.invokeOneWay(reqMsg);
			pass = false;
			TestUtil.logErr("Did not get WebServiceException ...");
		} catch (WebServiceException e) {
			logger.log(Level.INFO, "Got expected WebServiceException");
			e.printStackTrace();
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Received unexpected exception", e);
		}
		if (!pass)
			throw new Exception("invokeOneWayNegTestXML failed");
	}

	/*
	 * @testName: invokeTestSOAPMessage
	 *
	 * @assertion_ids: JAXWS:SPEC:4014; JAXWS:JAVADOC:8;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void invokeTestSOAPMessage() throws Exception {
		TestUtil.logTrace("invokeTestSOAPMessage");
		boolean pass = true;
		SOAPMessage reqMsg = JAXWS_Util.makeSOAPMessage(helloReqSM);
		String resStr;
		try {
			dispatchSM = createDispatchSOAPMessage();
			SOAPMessage resMsg = dispatchSM.invoke(reqMsg);
			try {
				resStr = JAXWS_Util.getSOAPMessageAsString(resMsg);
				logger.log(Level.INFO, "resStr=" + resStr);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			pass = false;
			e.printStackTrace();
		}
		if (!pass)
			throw new Exception("invokeTestSOAPMessage failed");
	}

	/*
	 * @testName: invokeAsyncTestSOAPMessage
	 *
	 * @assertion_ids: JAXWS:SPEC:4014; JAXWS:JAVADOC:10;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void invokeAsyncTestSOAPMessage() throws Exception {
		TestUtil.logTrace("invokeAsyncTestSOAPMessage");
		boolean pass = true;
		SOAPMessage reqMsg = JAXWS_Util.makeSOAPMessage(helloReqSM);
		String resStr;
		try {
			dispatchSM = createDispatchSOAPMessage();
			Response<SOAPMessage> res = dispatchSM.invokeAsync(reqMsg);
			logger.log(Level.INFO, "Polling and waiting for data ...");
			Object lock = new Object();
			while (!res.isDone()) {
				synchronized (lock) {
					try {
						lock.wait(50);
					} catch (InterruptedException e) {
						// ignore
					}
				}
			}
			SOAPMessage resMsg = res.get();
			try {
				resStr = JAXWS_Util.getSOAPMessageAsString(resMsg);
				logger.log(Level.INFO, "resStr=" + resStr);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			pass = false;
			e.printStackTrace();
		}
		if (!pass)
			throw new Exception("invokeAsyncTestSOAPMessage failed");
	}

	/*
	 * @testName: invokeAsyncHandlerTestSOAPMessage
	 *
	 * @assertion_ids: JAXWS:SPEC:4014; JAXWS:JAVADOC:10;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void invokeAsyncHandlerTestSOAPMessage() throws Exception {
		TestUtil.logTrace("invokeAsyncHandlerTestSOAPMessage");
		boolean pass = true;
		SOAPMessage reqMsg = JAXWS_Util.makeSOAPMessage(helloReqSM);
		MyHandlerSOAPMessage handler = new MyHandlerSOAPMessage();
		Future<?> future;
		try {
			dispatchSM = createDispatchSOAPMessage();
			future = dispatchSM.invokeAsync(reqMsg, handler);
			logger.log(Level.INFO, "Polling and waiting for data ...");
			Object lock = new Object();
			while (!future.isDone()) {
				synchronized (lock) {
					try {
						lock.wait(50);
					} catch (InterruptedException e) {
						// ignore
					}
				}
			}
			TestUtil.logMsg("Data from AsyncHandler MyHandler(): " + handler.getData());
		} catch (Exception e) {
			pass = false;
			e.printStackTrace();
		}

		if (!pass)
			throw new Exception("invokeAsyncHandlerTestSOAPMessage failed");
	}

	/*
	 * @testName: invokeOneWayTestSOAPMessage
	 *
	 * @assertion_ids: JAXWS:SPEC:4014; JAXWS:JAVADOC:11;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void invokeOneWayTestSOAPMessage() throws Exception {
		TestUtil.logTrace("invokeOneWayTestSOAPMessage");
		boolean pass = true;
		SOAPMessage reqMsg = JAXWS_Util.makeSOAPMessage(helloOneWayReqSM);
		try {
			dispatchSM = createDispatchSOAPMessage();
			dispatchSM.invokeOneWay(reqMsg);
		} catch (Exception e) {
			pass = false;
			e.printStackTrace();
		}
		if (!pass)
			throw new Exception("invokeOneWayTestSOAPMessage failed");
	}

	/*
	 * @testName: invokeTestJAXBNull
	 *
	 * @assertion_ids: JAXWS:SPEC:2036; JAXWS:SPEC:4013; JAXWS:SPEC:4015;
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
			Binding binding = ((BindingProvider) dispatchJaxb).getBinding();
			if (binding instanceof SOAPBinding) {
				logger.log(Level.INFO, "binding is a SOAPBinding instance");
			} else {
				TestUtil.logErr("binding is not a SOAPBinding instance");
				pass = false;
			}
			java.util.Map<String, Object> reqContext = dispatchJaxb.getRequestContext();
			logger.log(Level.INFO, "Calling invoke ....");
			helloRes = (HelloResponse) dispatchJaxb.invoke(helloReq);
			TestUtil.logErr("No SOAPFaultException or WebServiceException from bad invoke");
			pass = false;
		} catch (SOAPFaultException e) {
			logger.log(Level.INFO, "Got expected SOAPFaultException: " + e);
		} catch (WebServiceException e) {
			logger.log(Level.INFO, "Got expected WebServiceException: " + e);
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Received unexpected exception", e);
		}
		if (!pass)
			throw new Exception("invokeTestJAXBNull failed");
	}
}
