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
 *  @(#)Client.java	1.16 06/02/11
 */

package com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.onewayhandlertest.client;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaxws.common.BaseClient;
import com.sun.ts.tests.jaxws.common.Constants;
import com.sun.ts.tests.jaxws.common.HandlerTracker;
import com.sun.ts.tests.jaxws.common.Handler_Util;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;

import jakarta.xml.ws.Binding;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.handler.Handler;
import jakarta.xml.ws.handler.HandlerResolver;
import jakarta.xml.ws.handler.PortInfo;

public class Client extends BaseClient {

	private static final String CLIENTDELAY = "client.delay";

	private int clientDelay = 1;

	// URL properties used by the test
	private static final String WSDLLOC_URL = "rlowhandlertest.wsdlloc.1";

	private static final String ENDPOINT1_URL = "rlowhandlertest.endpoint.1";

	private static final String ENDPOINT4_URL = "rlowhandlertest.endpoint.2";

	private String url1 = null;

	private String url4 = null;

	private URL wsdlurl = null;

	// service and port information
	private static final String NAMESPACEURI = "http://rlowhandlertestservice.org/wsdl";

	private static final String SERVICE_NAME = "RLOWHandlerTestService";

	private static final String PORT_NAME1 = "HelloPort";

	private static final String PORT_NAME4 = "GetTrackerDataPort";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME1 = new QName(NAMESPACEURI, PORT_NAME1);

	private static final Class SERVICE_CLASS = com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.onewayhandlertest.client.RLOWHandlerTestService.class;

	private static final String THEBINDINGPROTOCOL = jakarta.xml.ws.soap.SOAPBinding.SOAP11HTTP_BINDING;

	private static final String LOGICAL = "Logical";

	private static final String SOAP = "SOAP";

	private static final String TEST_TYPE = LOGICAL + "Test";

	private Handler handler = null;

	Hello port1 = null;

	GetTrackerData port4 = null;

	static RLOWHandlerTestService service = null;

	BindingProvider bp1 = null;

	BindingProvider bp4 = null;

	Binding binding1 = null;

	Binding binding4 = null;

	List<Binding> listOfBindings = new ArrayList<Binding>();

	List<Handler> port1HandlerChain = null;

	List<Handler> port4HandlerChain = null;

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		return createWebArchive(Client.class);
	}

	protected void getService() {
		service = (RLOWHandlerTestService) getSharedObject();
	}

	protected void getTestURLs() throws Exception {
		logger.log(Level.INFO, "Get URL's used by the test");
		String file = JAXWS_Util.getURLFromProp(ENDPOINT1_URL);
		url1 = ctsurl.getURLString(PROTOCOL, hostname, portnum, file);
		file = JAXWS_Util.getURLFromProp(WSDLLOC_URL);
		wsdlurl = ctsurl.getURL(PROTOCOL, hostname, portnum, file);
		file = JAXWS_Util.getURLFromProp(ENDPOINT4_URL);
		url4 = ctsurl.getURLString(PROTOCOL, hostname, portnum, file);
		logger.log(Level.INFO, "Service Endpoint1 URL: " + url1);
		logger.log(Level.INFO, "Service Endpoint4 URL: " + url4);
		logger.log(Level.INFO, "WSDL Location URL:    " + wsdlurl);
	}

	protected void getPortStandalone() throws Exception {
		getPorts();
		JAXWS_Util.setTargetEndpointAddress(port1, url1);
		JAXWS_Util.setTargetEndpointAddress(port4, url4);
	}

	protected void getPortJavaEE() throws Exception {
		logger.log(Level.INFO, "Obtaining service via WebServiceRef annotation");
		logger.log(Level.INFO, "service=" + service);
		getPorts();
		getTargetEndpointAddress(port1, port4);
	}

	private void getTargetEndpointAddress(Object port1, Object port4) throws Exception {
		logger.log(Level.INFO, "Get Target Endpoint Address for port1=" + port1);
		String url1 = JAXWS_Util.getTargetEndpointAddress(port1);
		logger.log(Level.INFO, "Target Endpoint Address=" + url1);
		logger.log(Level.INFO, "Get Target Endpoint Address for port4=" + port4);
		String url4 = JAXWS_Util.getTargetEndpointAddress(port4);
		logger.log(Level.INFO, "Target Endpoint Address=" + url4);
	}

	private void getPorts() throws Exception {
		TestUtil.logTrace("entering getPorts");

		logger.log(Level.INFO, "Get port 1 = " + PORT_NAME1);
		port1 = (Hello) service.getPort(Hello.class);
		logger.log(Level.INFO, "port1=" + port1);

		logger.log(Level.INFO, "Get port 4 = " + PORT_NAME4);
		port4 = (GetTrackerData) service.getPort(GetTrackerData.class);
		logger.log(Level.INFO, "port4=" + port4);

		logger.log(Level.INFO, "Get binding for port 1 = " + PORT_NAME1);
		bp1 = (BindingProvider) port1;
		binding1 = bp1.getBinding();
		port1HandlerChain = binding1.getHandlerChain();
		logger.log(Level.INFO, "Port1 HandlerChain =" + port1HandlerChain);
		logger.log(Level.INFO, "Port1 HandlerChain size = " + port1HandlerChain.size());

		logger.log(Level.INFO, "------------------------------------------------------");

		logger.log(Level.INFO, "Get binding for port 4 = " + PORT_NAME4);
		bp4 = (BindingProvider) port4;
		binding4 = bp4.getBinding();
		port4HandlerChain = binding4.getHandlerChain();
		logger.log(Level.INFO, "Port4 HandlerChain=" + port4HandlerChain);
		logger.log(Level.INFO, "Port4 HandlerChain size = " + port4HandlerChain.size());

		listOfBindings.add(binding1);
		listOfBindings.add(binding4);

		TestUtil.logTrace("leaving getPorts");
	}

	/* Test setup */

	/*
	 * @class.testArgs: -ap jaxws-url-props.dat
	 * 
	 * @class.setup_props: webServerHost; webServerPort; platform.mode;
	 * client.delay;
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

			try {
				harnessHost = System.getProperty(HARNESSHOST);
			} catch (Exception e) {
				harnessHost = null;
			}
			try {
				harnessLogPort = System.getProperty(HARNESSLOGPORT);
			} catch (Exception e) {
				harnessLogPort = null;
			}
			try {
				harnessLogTraceFlag = System.getProperty(TRACEFLAG);
			} catch (Exception e) {
				harnessLogTraceFlag = "false";
			}

			try {
				clientDelay = Integer.parseInt(System.getProperty(CLIENTDELAY));
			} catch (Exception e) {
				logger.log(Level.INFO, "An ERROR occurred for the property " + CLIENTDELAY + ", using default value of "
						+ clientDelay + " second");
				TestUtil.printStackTrace(e);
			}

			modeProperty = System.getProperty(MODEPROP);
			if (modeProperty.equals("standalone")) {
				logger.log(Level.INFO, "Create Service object");
				getTestURLs();
				service = (RLOWHandlerTestService) JAXWS_Util.getService(wsdlurl, SERVICE_QNAME, SERVICE_CLASS);
			} else {
				getTestURLs();
				logger.log(Level.INFO, "WebServiceRef is not set in Client (get it from specific vehicle)");
				service = (RLOWHandlerTestService) getSharedObject();
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
	 * @testName: ClientOneWayHandlerTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9002; JAXWS:SPEC:9007; JAXWS:SPEC:9012;
	 * JAXWS:SPEC:9014; JAXWS:SPEC:9015.1; JAXWS:SPEC:9017; JAXWS:SPEC:9018;
	 *
	 * @test_Strategy: Invoke an RPC method and ensure that the client-side logical
	 * message handler callbacks are called.
	 */
	@Test
	public void ClientOneWayHandlerTest() throws Exception {
		TestUtil.logTrace("ClientOneWayHandlerTest");
		boolean pass = true;
		try {
			logger.log(Level.INFO,
					"Programatically registering the client side handlers by creating new HandlerResolver.");
			service.setHandlerResolver(new HandlerResolver() {
				public List<Handler> getHandlerChain(PortInfo info) {
					List<Handler> handlerList = new ArrayList<Handler>();
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO,
							"Programmatically registering the following service based handlers through the binding: \n"
									+ "ClientSOAPHandler1, ClientLogicalHandler1");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler1 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.onewayhandlertest.client.ClientSOAPHandler1();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler1 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.onewayhandlertest.client.ClientLogicalHandler1();
					handlerList.add(handler);
					if (info.getBindingID().equals(THEBINDINGPROTOCOL)) {
						logger.log(Level.INFO, "----------------------------------------------");
						logger.log(Level.INFO,
								"Programmatically registering the following protocol based handlers through the binding: \n"
										+ "ClientSOAPHandler2, ClientLogicalHandler2");
						logger.log(Level.INFO, "----------------------------------------------");
						logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler2 and add to HandlerChain");
						handler = new com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.onewayhandlertest.client.ClientSOAPHandler2();
						handlerList.add(handler);
						logger.log(Level.INFO,
								"Construct HandleInfo for ClientLogicalHandler2 and add to HandlerChain");
						handler = new com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.onewayhandlertest.client.ClientLogicalHandler2();
						handlerList.add(handler);
					}
					if (info.getPortName().equals(PORT_QNAME1)) {
						logger.log(Level.INFO, "----------------------------------------------");
						TestUtil.logMsg("Create port based handlers for port: " + PORT_QNAME1);
						logger.log(Level.INFO, "----------------------------------------------");
						logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler3 and add to HandlerChain");
						handler = new com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.onewayhandlertest.client.ClientSOAPHandler3();
						handlerList.add(handler);
						logger.log(Level.INFO,
								"Construct HandleInfo for ClientLogicalHandler3 and add to HandlerChain");
						handler = new com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.onewayhandlertest.client.ClientLogicalHandler3();
						handlerList.add(handler);
					}
					logger.log(Level.INFO, "HandlerChainList=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					return handlerList;
				}

			});

			if (!setupPorts()) {
				pass = false;
			}
			if (pass) {

				logger.log(Level.INFO, "Invoking RPC method port1.doHandlerTest1()");
				MyActionType ma = new MyActionType();
				ma.setAction("ClientLogicalTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);
				try {
					port1.doHandlerTest1(ma);
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				Handler_Util.clearHandlers(listOfBindings);

				logger.log(Level.INFO, "Get client side results back from Tracker");
				List<String> clientSideMsgs = HandlerTracker.getListMessages1();

				// verify client-side callbacks

				if (!Handler_Util.VerifyOneWayCallbacks("Client", Constants.OUTBOUND, clientSideMsgs)) {
					TestUtil.logErr("Client-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Client-Side Callbacks are (correct)");
				}

				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();

			}
		} catch (Exception e) {
			TestUtil.logErr("Exception occurred: " + e);
			pass = false;
		}

		if (!pass)
			throw new Exception("ClientOneWayHandlerTest failed");
	}

	/*
	 * @testName: ClientLogicalOutboundHandleMessageThrowsSOAPFaultTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9002; JAXWS:SPEC:9007; JAXWS:SPEC:9014;
	 * JAXWS:SPEC:9015.3.2; JAXWS:SPEC:9017; JAXWS:SPEC:9018;
	 *
	 * @test_Strategy: Invoke an RPC method and ensure that the server-side soap
	 * message handler callbacks are called.
	 */
	@Test
	public void ClientLogicalOutboundHandleMessageThrowsSOAPFaultTest() throws Exception {
		TestUtil.logTrace("ClientLogicalOutboundHandleMessageThrowsSOAPFaultTest");
		boolean pass = true;
		try {
			logger.log(Level.INFO,
					"Programatically registering the client side handlers by creating new HandlerResolver.");
			service.setHandlerResolver(new HandlerResolver() {
				public List<Handler> getHandlerChain(PortInfo info) {
					List<Handler> handlerList = new ArrayList<Handler>();
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO,
							"Programmatically registering the following service based handlers through the binding: \n"
									+ "ClientSOAPHandler1, ClientLogicalHandler1");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler1 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.onewayhandlertest.client.ClientSOAPHandler1();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler1 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.onewayhandlertest.client.ClientLogicalHandler1();
					handlerList.add(handler);
					if (info.getBindingID().equals(THEBINDINGPROTOCOL)) {
						logger.log(Level.INFO, "----------------------------------------------");
						logger.log(Level.INFO,
								"Programmatically registering the following protocol based handlers through the binding: \n"
										+ "ClientSOAPHandler2, ClientLogicalHandler2");
						logger.log(Level.INFO, "----------------------------------------------");
						logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler2 and add to HandlerChain");
						handler = new com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.onewayhandlertest.client.ClientSOAPHandler2();
						handlerList.add(handler);
						logger.log(Level.INFO,
								"Construct HandleInfo for ClientLogicalHandler2 and add to HandlerChain");
						handler = new com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.onewayhandlertest.client.ClientLogicalHandler2();
						handlerList.add(handler);
					}
					if (info.getPortName().equals(PORT_QNAME1)) {
						logger.log(Level.INFO, "----------------------------------------------");
						TestUtil.logMsg("Create port based handlers for port: " + PORT_QNAME1);
						logger.log(Level.INFO, "----------------------------------------------");
						logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler3 and add to HandlerChain");
						handler = new com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.onewayhandlertest.client.ClientSOAPHandler3();
						handlerList.add(handler);
						logger.log(Level.INFO,
								"Construct HandleInfo for ClientLogicalHandler3 and add to HandlerChain");
						handler = new com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.onewayhandlertest.client.ClientLogicalHandler3();
						handlerList.add(handler);
					}
					logger.log(Level.INFO, "HandlerChainList=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					return handlerList;
				}

			});

			if (!setupPorts()) {
				pass = false;
			}
			if (pass) {

				logger.log(Level.INFO, "Invoking RPC method port1.doHandlerTest1()");
				MyActionType ma = new MyActionType();
				ma.setAction("ClientLogicalOutboundHandleMessageThrowsSOAPFaultTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);
				try {
					port1.doHandlerTest1(ma);
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				Handler_Util.clearHandlers(listOfBindings);

				logger.log(Level.INFO, "Get client side results back from Tracker");
				List<String> clientSideMsgs = HandlerTracker.getListMessages1();

				// verify client-side callbacks

				if (!Handler_Util.VerifyOneWaySOAPFaultCallbacks("Client", Constants.OUTBOUND, LOGICAL,
						clientSideMsgs)) {
					TestUtil.logErr("Client-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Client-Side Callbacks are (correct)");
				}

				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();

			}
		} catch (Exception e) {
			TestUtil.logErr("Exception occurred: " + e);
			pass = false;
		}

		if (!pass)
			throw new Exception("ClientLogicalOutboundHandleMessageThrowsSOAPFaultTest failed");
	}

	/*
	 * @testName: ClientSOAPOutboundHandleMessageThrowsSOAPFaultTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9002; JAXWS:SPEC:9007; JAXWS:SPEC:9014;
	 * JAXWS:SPEC:9015.3.2; JAXWS:SPEC:9017; JAXWS:SPEC:9018;
	 *
	 * @test_Strategy: Invoke an RPC method and ensure that the server-side soap
	 * message handler callbacks are called.
	 */
	@Test
	public void ClientSOAPOutboundHandleMessageThrowsSOAPFaultTest() throws Exception {
		TestUtil.logTrace("ClientSOAPOutboundHandleMessageThrowsSOAPFaultTest");
		boolean pass = true;
		try {
			logger.log(Level.INFO,
					"Programatically registering the client side handlers by creating new HandlerResolver.");
			service.setHandlerResolver(new HandlerResolver() {
				public List<Handler> getHandlerChain(PortInfo info) {
					List<Handler> handlerList = new ArrayList<Handler>();
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO,
							"Programmatically registering the following service based handlers through the binding: \n"
									+ "ClientLogicalHandler1, ClientSOAPHandler1");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler1 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.onewayhandlertest.client.ClientLogicalHandler1();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler1 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.onewayhandlertest.client.ClientSOAPHandler1();
					handlerList.add(handler);
					if (info.getBindingID().equals(THEBINDINGPROTOCOL)) {
						logger.log(Level.INFO, "----------------------------------------------");
						logger.log(Level.INFO,
								"Programmatically registering the following protocol based handlers through the binding: \n"
										+ "ClientLogicalHandler2, ClientSOAPHandler2");
						logger.log(Level.INFO, "----------------------------------------------");
						logger.log(Level.INFO,
								"Construct HandleInfo for ClientLogicalHandler2 and add to HandlerChain");
						handler = new com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.onewayhandlertest.client.ClientLogicalHandler2();
						handlerList.add(handler);
						logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler2 and add to HandlerChain");
						handler = new com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.onewayhandlertest.client.ClientSOAPHandler2();
						handlerList.add(handler);
					}
					if (info.getPortName().equals(PORT_QNAME1)) {
						logger.log(Level.INFO, "----------------------------------------------");
						TestUtil.logMsg("Create port based handlers for port: " + PORT_QNAME1);
						logger.log(Level.INFO, "----------------------------------------------");
						logger.log(Level.INFO,
								"Construct HandleInfo for ClientLogicalHandler3 and add to HandlerChain");
						handler = new com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.onewayhandlertest.client.ClientLogicalHandler3();
						handlerList.add(handler);
						logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler3 and add to HandlerChain");
						handler = new com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.onewayhandlertest.client.ClientSOAPHandler3();
						handlerList.add(handler);
					}
					logger.log(Level.INFO, "HandlerChainList=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					return handlerList;
				}

			});

			if (!setupPorts()) {
				pass = false;
			}
			if (pass) {

				logger.log(Level.INFO, "Invoking RPC method port1.doHandlerTest1()");
				MyActionType ma = new MyActionType();
				ma.setAction("ClientSOAPOutboundHandleMessageThrowsSOAPFaultTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);
				try {
					port1.doHandlerTest1(ma);
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				Handler_Util.clearHandlers(listOfBindings);

				logger.log(Level.INFO, "Get client side results back from Tracker");
				List<String> clientSideMsgs = HandlerTracker.getListMessages1();

				// verify client-side callbacks

				if (!Handler_Util.VerifyOneWaySOAPFaultCallbacks("Client", Constants.OUTBOUND, SOAP, clientSideMsgs)) {
					TestUtil.logErr("Client-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Client-Side Callbacks are (correct)");
				}

				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();

			}
		} catch (Exception e) {
			TestUtil.logErr("Exception occurred: " + e);
			pass = false;
		}

		if (!pass)
			throw new Exception("ClientSOAPOutboundHandleMessageThrowsSOAPFaultTest failed");
	}

	/*
	 * @testName: ClientLogicalOutboundHandleMessageReturnsFalseTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9002; JAXWS:SPEC:9007; JAXWS:SPEC:9014;
	 * JAXWS:SPEC:9015.2.2; JAXWS:SPEC:9017; JAXWS:SPEC:9018;
	 *
	 * @test_Strategy: Invoke an RPC method and ensure that the server-side soap
	 * message handler callbacks are called.
	 */
	@Test
	public void ClientLogicalOutboundHandleMessageReturnsFalseTest() throws Exception {
		TestUtil.logTrace("ClientLogicalOutboundHandleMessageReturnsFalseTest");
		boolean pass = true;
		try {
			logger.log(Level.INFO,
					"Programatically registering the client side handlers by creating new HandlerResolver.");
			service.setHandlerResolver(new HandlerResolver() {
				public List<Handler> getHandlerChain(PortInfo info) {
					List<Handler> handlerList = new ArrayList<Handler>();
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO,
							"Programmatically registering the following service based handlers through the binding: \n"
									+ "ClientSOAPHandler1, ClientLogicalHandler1");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler1 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.onewayhandlertest.client.ClientSOAPHandler1();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler1 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.onewayhandlertest.client.ClientLogicalHandler1();
					handlerList.add(handler);
					if (info.getBindingID().equals(THEBINDINGPROTOCOL)) {
						logger.log(Level.INFO, "----------------------------------------------");
						logger.log(Level.INFO,
								"Programmatically registering the following protocol based handlers through the binding: \n"
										+ "ClientSOAPHandler2, ClientLogicalHandler2");
						logger.log(Level.INFO, "----------------------------------------------");
						logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler2 and add to HandlerChain");
						handler = new com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.onewayhandlertest.client.ClientSOAPHandler2();
						handlerList.add(handler);
						logger.log(Level.INFO,
								"Construct HandleInfo for ClientLogicalHandler2 and add to HandlerChain");
						handler = new com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.onewayhandlertest.client.ClientLogicalHandler2();
						handlerList.add(handler);
					}
					if (info.getPortName().equals(PORT_QNAME1)) {
						logger.log(Level.INFO, "----------------------------------------------");
						TestUtil.logMsg("Create port based handlers for port: " + PORT_QNAME1);
						logger.log(Level.INFO, "----------------------------------------------");
						logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler3 and add to HandlerChain");
						handler = new com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.onewayhandlertest.client.ClientSOAPHandler3();
						handlerList.add(handler);
						logger.log(Level.INFO,
								"Construct HandleInfo for ClientLogicalHandler3 and add to HandlerChain");
						handler = new com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.onewayhandlertest.client.ClientLogicalHandler3();
						handlerList.add(handler);
					}
					logger.log(Level.INFO, "HandlerChainList=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					return handlerList;
				}

			});

			if (!setupPorts()) {
				pass = false;
			}
			if (pass) {

				logger.log(Level.INFO, "Invoking RPC method port1.doHandlerTest1()");
				MyActionType ma = new MyActionType();
				ma.setAction("ClientLogicalOutboundHandleMessageReturnsFalseTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);
				try {
					port1.doHandlerTest1(ma);
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				Handler_Util.clearHandlers(listOfBindings);

				logger.log(Level.INFO, "Get client side results back from Tracker");
				List<String> clientSideMsgs = HandlerTracker.getListMessages1();

				// verify client-side callbacks

				if (!Handler_Util.VerifyOneWayHandleMessageFalseCallbacks("Client", Constants.OUTBOUND, LOGICAL,
						clientSideMsgs)) {
					TestUtil.logErr("Client-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Client-Side Callbacks are (correct)");
				}

				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();

			}
		} catch (Exception e) {
			TestUtil.logErr("Exception occurred: " + e);
			pass = false;
		}

		if (!pass)
			throw new Exception("ClientLogicalOutboundHandleMessageReturnsFalseTest failed");
	}

	/*
	 * @testName: ClientSOAPOutboundHandleMessageReturnsFalseTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9002; JAXWS:SPEC:9007; JAXWS:SPEC:9014;
	 * JAXWS:SPEC:9015.2.2; JAXWS:SPEC:9017; JAXWS:SPEC:9018;
	 *
	 * @test_Strategy: Invoke an RPC method and ensure that the server-side soap
	 * message handler callbacks are called.
	 */
	@Test
	public void ClientSOAPOutboundHandleMessageReturnsFalseTest() throws Exception {
		TestUtil.logTrace("ClientSOAPOutboundHandleMessageReturnsFalseTest");
		boolean pass = true;
		try {
			logger.log(Level.INFO,
					"Programatically registering the client side handlers by creating new HandlerResolver.");
			service.setHandlerResolver(new HandlerResolver() {
				public List<Handler> getHandlerChain(PortInfo info) {
					List<Handler> handlerList = new ArrayList<Handler>();
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO,
							"Programmatically registering the following service based handlers through the binding: \n"
									+ "ClientLogicalHandler1, ClientSOAPHandler1");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler1 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.onewayhandlertest.client.ClientLogicalHandler1();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler1 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.onewayhandlertest.client.ClientSOAPHandler1();
					handlerList.add(handler);
					if (info.getBindingID().equals(THEBINDINGPROTOCOL)) {
						logger.log(Level.INFO, "----------------------------------------------");
						logger.log(Level.INFO,
								"Programmatically registering the following protocol based handlers through the binding: \n"
										+ "ClientLogicalHandler2, ClientSOAPHandler2");
						logger.log(Level.INFO, "----------------------------------------------");
						logger.log(Level.INFO,
								"Construct HandleInfo for ClientLogicalHandler2 and add to HandlerChain");
						handler = new com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.onewayhandlertest.client.ClientLogicalHandler2();
						handlerList.add(handler);
						logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler2 and add to HandlerChain");
						handler = new com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.onewayhandlertest.client.ClientSOAPHandler2();
						handlerList.add(handler);
					}
					if (info.getPortName().equals(PORT_QNAME1)) {
						logger.log(Level.INFO, "----------------------------------------------");
						TestUtil.logMsg("Create port based handlers for port: " + PORT_QNAME1);
						logger.log(Level.INFO, "----------------------------------------------");
						logger.log(Level.INFO,
								"Construct HandleInfo for ClientLogicalHandler3 and add to HandlerChain");
						handler = new com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.onewayhandlertest.client.ClientLogicalHandler3();
						handlerList.add(handler);
						logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler3 and add to HandlerChain");
						handler = new com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.onewayhandlertest.client.ClientSOAPHandler3();
						handlerList.add(handler);
					}
					logger.log(Level.INFO, "HandlerChainList=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					return handlerList;
				}

			});

			if (!setupPorts()) {
				pass = false;
			}
			if (pass) {

				logger.log(Level.INFO, "Invoking RPC method port1.doHandlerTest1()");
				MyActionType ma = new MyActionType();
				ma.setAction("ClientSOAPOutboundHandleMessageReturnsFalseTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);
				try {
					port1.doHandlerTest1(ma);
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				Handler_Util.clearHandlers(listOfBindings);

				logger.log(Level.INFO, "Get client side results back from Tracker");
				List<String> clientSideMsgs = HandlerTracker.getListMessages1();

				// verify client-side callbacks

				if (!Handler_Util.VerifyOneWayHandleMessageFalseCallbacks("Client", Constants.OUTBOUND, SOAP,
						clientSideMsgs)) {
					TestUtil.logErr("Client-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Client-Side Callbacks are (correct)");
				}

				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();

			}
		} catch (Exception e) {
			TestUtil.logErr("Exception occurred: " + e);
			pass = false;
		}

		if (!pass)
			throw new Exception("ClientSOAPOutboundHandleMessageReturnsFalseTest failed");
	}

	/*
	 * @testName: ServerLogicalHandlerTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9002; JAXWS:SPEC:9007; JAXWS:SPEC:9014;
	 * JAXWS:SPEC:9015.1; JAXWS:SPEC:9017; JAXWS:SPEC:9018;
	 *
	 * @test_Strategy: Invoke an RPC method and ensure that the server-side soap
	 * message handler callbacks are called.
	 */
	@Test
	public void ServerLogicalHandlerTest() throws Exception {
		TestUtil.logTrace("ServerLogicalHandlerTest");
		boolean pass = true;
		if (!setupPorts()) {
			pass = false;
		}
		if (pass) {
			Handler_Util.clearHandlers(listOfBindings);
			try {
				logger.log(Level.INFO, "Purging server-side tracker data");
				purgeServerSideTrackerData();

				logger.log(Level.INFO, "Invoking RPC method port1.doHandlerTest1()");
				MyActionType ma = new MyActionType();
				ma.setAction("ServerLogicalTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);
				// MyResultType mr = null;
				try {
					// mr = port1.doHandlerTest1(ma);
					port1.doHandlerTest1(ma);
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				StringBuffer sb = new StringBuffer();
				sb.append("\n-------------------------------------------------------------------\n");
				sb.append("Sleeping: " + clientDelay + " second(s) before getting results from server\n");
				sb.append("-------------------------------------------------------------------\n");
				logger.log(Level.INFO, sb.toString());
				TestUtil.sleepSec(clientDelay);

				List<String> serverSideMsgs = null;

				logger.log(Level.INFO, "Get server side result back from endpoint");
				GetTrackerDataAction gtda = new GetTrackerDataAction();
				gtda.setAction("getArrayMessages1");
				gtda.setHarnessloghost(harnessHost);
				gtda.setHarnesslogport(harnessLogPort);
				gtda.setHarnesslogtraceflag(harnessLogTraceFlag);
				try {
					serverSideMsgs = port4.getTrackerData(gtda).getResult();
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				logger.log(Level.INFO, "Verifying Server-Side Handler callbacks");
				serverSideMsgs = JAXWS_Util.getMessagesStartingFrom(serverSideMsgs, Constants.INBOUND);

				if (!Handler_Util.VerifyOneWayCallbacks("Server", Constants.INBOUND, serverSideMsgs)) {
					TestUtil.logErr("Server-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Server-Side Callbacks are (correct)");
				}

				logger.log(Level.INFO, "Purging server-side tracker data");
				purgeServerSideTrackerData();
			} catch (Exception e) {
				TestUtil.logErr("Exception occurred: " + e);
				pass = false;
			}
		}
		if (!pass)
			throw new Exception("ServerLogicalHandlerTest failed");
	}

	/*
	 * @testName: ServerLogicalInboundHandleMessageThrowsSOAPFaultTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9002; JAXWS:SPEC:9007; JAXWS:SPEC:9014;
	 * JAXWS:SPEC:9015.3.2; JAXWS:SPEC:9017; JAXWS:SPEC:9018;
	 *
	 * @test_Strategy: Invoke an RPC method and ensure that the server-side soap
	 * message handler callbacks are called.
	 */
	@Test
	public void ServerLogicalInboundHandleMessageThrowsSOAPFaultTest() throws Exception {
		TestUtil.logTrace("ServerLogicalInboundHandleMessageThrowsSOAPFaultTest");
		boolean pass = true;
		if (!setupPorts()) {
			pass = false;
		}
		if (pass) {
			Handler_Util.clearHandlers(listOfBindings);
			try {
				logger.log(Level.INFO, "Purging server-side tracker data");
				purgeServerSideTrackerData();

				logger.log(Level.INFO, "Invoking RPC method port1.doHandlerTest1()");
				MyActionType ma = new MyActionType();
				ma.setAction("ServerLogicalInboundHandleMessageThrowsSOAPFaultTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);
				// MyResultType mr = null;
				try {
					// mr = port1.doHandlerTest1(ma);
					port1.doHandlerTest1(ma);
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				StringBuffer sb = new StringBuffer();
				sb.append("\n-------------------------------------------------------------------\n");
				sb.append("Sleeping: " + clientDelay + " second(s) before getting results from server\n");
				sb.append("-------------------------------------------------------------------\n");
				logger.log(Level.INFO, sb.toString());
				TestUtil.sleepSec(clientDelay);

				List<String> serverSideMsgs = null;

				logger.log(Level.INFO, "Get server side result back from endpoint");
				GetTrackerDataAction gtda = new GetTrackerDataAction();
				gtda.setAction("getArrayMessages1");
				gtda.setHarnessloghost(harnessHost);
				gtda.setHarnesslogport(harnessLogPort);
				gtda.setHarnesslogtraceflag(harnessLogTraceFlag);
				try {
					serverSideMsgs = port4.getTrackerData(gtda).getResult();
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				logger.log(Level.INFO, "Verifying Server-Side Handler callbacks");
				serverSideMsgs = JAXWS_Util.getMessagesStartingFrom(serverSideMsgs, Constants.INBOUND);

				if (!Handler_Util.VerifyOneWaySOAPFaultCallbacks("Server", Constants.INBOUND, LOGICAL,
						serverSideMsgs)) {
					TestUtil.logErr("Server-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Server-Side Callbacks are (correct)");
				}

				logger.log(Level.INFO, "Purging server-side tracker data");
				purgeServerSideTrackerData();
			} catch (Exception e) {
				TestUtil.logErr("Exception occurred: " + e);
				pass = false;
			}
		}
		if (!pass)
			throw new Exception("ServerLogicalInboundHandleMessageThrowsSOAPFaultTest failed");
	}

	/*
	 * @testName: ServerSOAPInboundHandleMessageThrowsSOAPFaultTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9002; JAXWS:SPEC:9007; JAXWS:SPEC:9014;
	 * JAXWS:SPEC:9015.3.2; JAXWS:SPEC:9017; JAXWS:SPEC:9018;
	 *
	 * @test_Strategy: Invoke an RPC method and ensure that the server-side soap
	 * message handler callbacks are called.
	 */
	@Test
	public void ServerSOAPInboundHandleMessageThrowsSOAPFaultTest() throws Exception {
		TestUtil.logTrace("ServerSOAPInboundHandleMessageThrowsSOAPFaultTest");
		boolean pass = true;
		if (!setupPorts()) {
			pass = false;
		}
		if (pass) {
			Handler_Util.clearHandlers(listOfBindings);
			try {
				logger.log(Level.INFO, "Purging server-side tracker data");
				purgeServerSideTrackerData();

				logger.log(Level.INFO, "Invoking RPC method port1.doHandlerTest1()");
				MyActionType ma = new MyActionType();
				ma.setAction("ServerSOAPInboundHandleMessageThrowsSOAPFaultTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);
				// MyResultType mr = null;
				try {
					// mr = port1.doHandlerTest1(ma);
					port1.doHandlerTest1(ma);
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				StringBuffer sb = new StringBuffer();
				sb.append("\n-------------------------------------------------------------------\n");
				sb.append("Sleeping: " + clientDelay + " second(s) before getting results from server\n");
				sb.append("-------------------------------------------------------------------\n");
				logger.log(Level.INFO, sb.toString());
				TestUtil.sleepSec(clientDelay);

				List<String> serverSideMsgs = null;

				logger.log(Level.INFO, "Get server side result back from endpoint");
				GetTrackerDataAction gtda = new GetTrackerDataAction();
				gtda.setAction("getArrayMessages1");
				gtda.setHarnessloghost(harnessHost);
				gtda.setHarnesslogport(harnessLogPort);
				gtda.setHarnesslogtraceflag(harnessLogTraceFlag);
				try {
					serverSideMsgs = port4.getTrackerData(gtda).getResult();
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				logger.log(Level.INFO, "Verifying Server-Side Handler callbacks");
				serverSideMsgs = JAXWS_Util.getMessagesStartingFrom(serverSideMsgs, Constants.INBOUND);

				if (!Handler_Util.VerifyOneWaySOAPFaultCallbacks("Server", Constants.INBOUND, SOAP, serverSideMsgs)) {
					TestUtil.logErr("Server-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Server-Side Callbacks are (correct)");
				}

				logger.log(Level.INFO, "Purging server-side tracker data");
				purgeServerSideTrackerData();
			} catch (Exception e) {
				TestUtil.logErr("Exception occurred: " + e);
				pass = false;
			}
		}
		if (!pass)
			throw new Exception("ServerSOAPInboundHandleMessageThrowsSOAPFaultTest failed");
	}

	/*
	 * @testName: ServerLogicalInboundHandleMessageReturnsFalseTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9002; JAXWS:SPEC:9007; JAXWS:SPEC:9014;
	 * JAXWS:SPEC:9015.2.2; JAXWS:SPEC:9017; JAXWS:SPEC:9018;
	 *
	 * @test_Strategy: Invoke an RPC method and ensure that the server-side soap
	 * message handler callbacks are called.
	 */
	@Test
	public void ServerLogicalInboundHandleMessageReturnsFalseTest() throws Exception {
		TestUtil.logTrace("ServerLogicalInboundHandleMessageReturnsFalseTest");
		boolean pass = true;
		if (!setupPorts()) {
			pass = false;
		}
		if (pass) {
			Handler_Util.clearHandlers(listOfBindings);
			try {
				logger.log(Level.INFO, "Purging server-side tracker data");
				purgeServerSideTrackerData();

				logger.log(Level.INFO, "Invoking RPC method port1.doHandlerTest1()");
				MyActionType ma = new MyActionType();
				ma.setAction("ServerLogicalInboundHandleMessageReturnsFalseTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);
				// MyResultType mr = null;
				try {
					// mr = port1.doHandlerTest1(ma);
					port1.doHandlerTest1(ma);
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				StringBuffer sb = new StringBuffer();
				sb.append("\n-------------------------------------------------------------------\n");
				sb.append("Sleeping: " + clientDelay + " second(s) before getting results from server\n");
				sb.append("-------------------------------------------------------------------\n");
				logger.log(Level.INFO, sb.toString());
				TestUtil.sleepSec(clientDelay);

				List<String> serverSideMsgs = null;

				logger.log(Level.INFO, "Get server side result back from endpoint");
				GetTrackerDataAction gtda = new GetTrackerDataAction();
				gtda.setAction("getArrayMessages1");
				gtda.setHarnessloghost(harnessHost);
				gtda.setHarnesslogport(harnessLogPort);
				gtda.setHarnesslogtraceflag(harnessLogTraceFlag);
				try {
					serverSideMsgs = port4.getTrackerData(gtda).getResult();
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				logger.log(Level.INFO, "Verifying Server-Side Handler callbacks");
				serverSideMsgs = JAXWS_Util.getMessagesStartingFrom(serverSideMsgs, Constants.INBOUND);

				if (!Handler_Util.VerifyOneWayHandleMessageFalseCallbacks("Server", Constants.INBOUND, LOGICAL,
						serverSideMsgs)) {
					TestUtil.logErr("Server-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Server-Side Callbacks are (correct)");
				}

				logger.log(Level.INFO, "Purging server-side tracker data");
				purgeServerSideTrackerData();
			} catch (Exception e) {
				TestUtil.logErr("Exception occurred: " + e);
				pass = false;
			}
		}
		if (!pass)
			throw new Exception("ServerLogicalInboundHandleMessageReturnsFalseTest failed");
	}

	/*
	 * @testName: ServerSOAPInboundHandleMessageReturnsFalseTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9002; JAXWS:SPEC:9007; JAXWS:SPEC:9014;
	 * JAXWS:SPEC:9015.2.2; JAXWS:SPEC:9017; JAXWS:SPEC:9018;
	 *
	 * @test_Strategy: Invoke an RPC method and ensure that the server-side soap
	 * message handler callbacks are called.
	 */
	@Test
	public void ServerSOAPInboundHandleMessageReturnsFalseTest() throws Exception {
		TestUtil.logTrace("ServerSOAPInboundHandleMessageReturnsFalseTest");
		boolean pass = true;
		if (!setupPorts()) {
			pass = false;
		}
		if (pass) {
			Handler_Util.clearHandlers(listOfBindings);
			try {
				logger.log(Level.INFO, "Purging server-side tracker data");
				purgeServerSideTrackerData();

				logger.log(Level.INFO, "Invoking RPC method port1.doHandlerTest1()");
				MyActionType ma = new MyActionType();
				ma.setAction("ServerSOAPInboundHandleMessageReturnsFalseTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);
				// MyResultType mr = null;
				try {
					// mr = port1.doHandlerTest1(ma);
					port1.doHandlerTest1(ma);
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				StringBuffer sb = new StringBuffer();
				sb.append("\n-------------------------------------------------------------------\n");
				sb.append("Sleeping: " + clientDelay + " second(s) before getting results from server\n");
				sb.append("-------------------------------------------------------------------\n");
				logger.log(Level.INFO, sb.toString());
				TestUtil.sleepSec(clientDelay);

				List<String> serverSideMsgs = null;

				logger.log(Level.INFO, "Get server side result back from endpoint");
				GetTrackerDataAction gtda = new GetTrackerDataAction();
				gtda.setAction("getArrayMessages1");
				gtda.setHarnessloghost(harnessHost);
				gtda.setHarnesslogport(harnessLogPort);
				gtda.setHarnesslogtraceflag(harnessLogTraceFlag);
				try {
					serverSideMsgs = port4.getTrackerData(gtda).getResult();
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				logger.log(Level.INFO, "Verifying Server-Side Handler callbacks");
				serverSideMsgs = JAXWS_Util.getMessagesStartingFrom(serverSideMsgs, Constants.INBOUND);

				if (!Handler_Util.VerifyOneWayHandleMessageFalseCallbacks("Server", Constants.INBOUND, SOAP,
						serverSideMsgs)) {
					TestUtil.logErr("Server-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Server-Side Callbacks are (correct)");
				}

				logger.log(Level.INFO, "Purging server-side tracker data");
				purgeServerSideTrackerData();
			} catch (Exception e) {
				TestUtil.logErr("Exception occurred: " + e);
				pass = false;
			}
		}
		if (!pass)
			throw new Exception("ServerSOAPInboundHandleMessageReturnsFalseTest failed");
	}

	private void purgeServerSideTrackerData() {
		try {
			GetTrackerDataAction gtda = new GetTrackerDataAction();
			gtda.setAction("purge");
			gtda.setHarnessloghost(harnessHost);
			gtda.setHarnesslogport(harnessLogPort);
			gtda.setHarnesslogtraceflag(harnessLogTraceFlag);
			port4.getTrackerData(gtda);
		} catch (Exception e) {
			TestUtil.logErr("Call to purge server-side tracker data failed:" + e);
		}
	}

	static class MyStatus {
		private boolean status = true;

		public void setStatus(boolean b) {
			status = b;
		}

		public boolean getStatus() {
			return status;
		}
	}

}
