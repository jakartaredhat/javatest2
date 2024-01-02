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

package com.sun.ts.tests.jaxws.ee.w2j.document.literal.asynctest.client;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.xml.namespace.QName;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaxws.common.BaseClient;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;

import jakarta.xml.ws.AsyncHandler;
import jakarta.xml.ws.Response;

public class Client extends BaseClient {
	// need to create jaxbContext
	private static final ObjectFactory of = new ObjectFactory();

	private static final String PKG_NAME = "com.sun.ts.tests.jaxws.ee.w2j.document.literal.asynctest.client.";

	// service and port information
	private static final String NAMESPACEURI = "http://helloservice.org/wsdl";

	private static final String SERVICE_NAME = "HelloService";

	private static final String PORT_NAME = "HelloPort";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME = new QName(NAMESPACEURI, PORT_NAME);

	// URL properties used by the test
	private static final String ENDPOINT_URL = "w2jasynctest.endpoint.1";

	private static final String WSDLLOC_URL = "w2jasynctest.wsdlloc.1";

	private String url = null;

	private URL wsdlurl = null;

	Hello port = null;

	static HelloService service = null;

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		return createWebArchive(Client.class);
	}

	protected void getService() {
		service = (HelloService) getSharedObject();
	}

	private class HelloCallbackHandler implements AsyncHandler<HelloResponse> {
		private HelloResponse output;

		public void handleResponse(Response<HelloResponse> response) {
			try {
				output = response.get();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		HelloResponse getResponse() {
			return output;
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

	protected void getPortStandalone() throws Exception {
		port = (Hello) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, HelloService.class, PORT_QNAME, Hello.class);
		JAXWS_Util.setTargetEndpointAddress(port, url);
	}

	protected void getPortJavaEE() throws Exception {
		logger.log(Level.INFO, "Obtaining service via WebServiceRef annotation");
		logger.log(Level.INFO, "service=" + service);
		port = (Hello) service.getPort(Hello.class);
		logger.log(Level.INFO, "port=" + port);
		logger.log(Level.INFO, "Obtained port");
		JAXWS_Util.dumpTargetEndpointAddress(port);
		// JAXWS_Util.setTargetEndpointAddress(port, url);
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
	 * @testName: invokeSynchronousTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2032; JAXWS:SPEC:2033; JAXWS:SPEC:2034;
	 * JAXWS:SPEC:2034; JAXWS:SPEC:2035; JAXWS:SPEC:2038; JAXWS:SPEC:2039;
	 *
	 * @test_Strategy: Create a stub instance to our service definition interface,
	 * set the target endpoint to the servlet, and invoke an RPC method using
	 * synchronous method.
	 *
	 * Description A client can invoke an RPC method via generated stub.
	 */
	@Test
	public void invokeSynchronousTest() throws Exception {
		TestUtil.logTrace("invokeSynchronousTest");
		boolean pass = true;
		String reqStr = "foo";
		String resStr = "Hello, foo!";
		try {
			HelloRequest helloReq = of.createHelloRequest();
			helloReq.setString(reqStr);
			HelloResponse helloRes = port.hello(helloReq);
			String result = helloRes.getResult();
			logger.log(Level.INFO, "result=" + result);
			if (!result.equals(resStr)) {
				TestUtil.logErr("expected: " + resStr + ", received: " + result);
				pass = false;
			}
		} catch (Exception e) {
			pass = false;
			e.printStackTrace();
		}

		if (!pass)
			throw new Exception("invokeSynchronousTest failed");
	}

	/*
	 * @testName: invokeAsyncPollTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2032; JAXWS:SPEC:2033; JAXWS:SPEC:2034;
	 * JAXWS:SPEC:2034; JAXWS:SPEC:2035; JAXWS:SPEC:2038; JAXWS:SPEC:2039;
	 * WS4EE:SPEC:4006; WS4EE:SPEC:4007; WS4EE:SPEC:4008;
	 *
	 * @test_Strategy: Create a stub instance to our service definition interface,
	 * set the target endpoint to the servlet, and invoke an RPC method using
	 * AsyncPoll method.
	 *
	 * Description A client can invoke an RPC method via generated stub.
	 */
	@Test
	public void invokeAsyncPollTest() throws Exception {
		TestUtil.logTrace("invokeAsyncPollTest");
		boolean pass = true;
		String reqStr = "foo";
		String resStr = "Hello, foo!";
		try {
			HelloRequest helloReq = of.createHelloRequest();
			helloReq.setString(reqStr);
			Response<HelloResponse> response = port.helloAsync(helloReq);
			logger.log(Level.INFO, "Polling and waiting for data ...");
			Object lock = new Object();
			while (!response.isDone()) {
				synchronized (lock) {
					try {
						lock.wait(50);
					} catch (InterruptedException e) {
						// ignore
					}
				}
			}
			HelloResponse helloRes = response.get();
			String result = helloRes.getResult();
			logger.log(Level.INFO, "result=" + result);
			if (!result.equals(resStr)) {
				TestUtil.logErr("expected: " + resStr + ", received: " + result);
				pass = false;
			}
		} catch (Exception e) {
			pass = false;
			e.printStackTrace();
		}

		if (!pass)
			throw new Exception("invokeAsyncPollTest failed");
	}

	/*
	 * @testName: invokeAsyncCallbackTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2032; JAXWS:SPEC:2033; JAXWS:SPEC:2034;
	 * JAXWS:SPEC:2034; JAXWS:SPEC:2035; JAXWS:SPEC:2038; JAXWS:SPEC:2039;
	 * WS4EE:SPEC:4006; WS4EE:SPEC:4007; WS4EE:SPEC:4008;
	 *
	 * @test_Strategy: Create a stub instance to our service definition interface,
	 * set the target endpoint to the servlet, and invoke an RPC method using
	 * AsyncCallback method.
	 *
	 * Description A client can invoke an RPC method via generated stub.
	 */
	@Test
	public void invokeAsyncCallbackTest() throws Exception {
		TestUtil.logTrace("invokeAsyncCallbackTest");
		boolean pass = true;
		String reqStr = "foo";
		String resStr = "Hello, foo!";
		try {
			HelloRequest helloReq = of.createHelloRequest();
			helloReq.setString(reqStr);
			HelloCallbackHandler callbackHandler = new HelloCallbackHandler();
			Future<?> response = port.helloAsync(helloReq, callbackHandler);
			logger.log(Level.INFO, "Waiting for Callback to complete to obtain data ...");
			Object lock = new Object();
			while (!response.isDone()) {
				synchronized (lock) {
					try {
						lock.wait(50);
					} catch (InterruptedException e) {
						// ignore
					}
				}
			}
			HelloResponse helloRes = callbackHandler.getResponse();
			String result = helloRes.getResult();
			logger.log(Level.INFO, "result=" + result);
			if (!result.equals(resStr)) {
				TestUtil.logErr("expected: " + resStr + ", received: " + result);
				pass = false;
			}
		} catch (Exception e) {
			pass = false;
			e.printStackTrace();
		}

		if (!pass)
			throw new Exception("invokeAsyncCallbackTest failed");
	}
}
