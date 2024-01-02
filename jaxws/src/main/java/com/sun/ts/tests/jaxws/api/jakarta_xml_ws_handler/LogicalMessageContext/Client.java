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

package com.sun.ts.tests.jaxws.api.jakarta_xml_ws_handler.LogicalMessageContext;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.porting.TSURL;
import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaxws.common.HandlerTracker;
import com.sun.ts.tests.jaxws.common.Handler_Util;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;
import com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.DLHandlerService;
import com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.GetTrackerData;
import com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.GetTrackerDataAction;
import com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.Hello;
import com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.MyActionType;
import com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.MyResult2;
import com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.MyResultType;

import jakarta.activation.DataHandler;
import jakarta.xml.ws.Binding;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.Holder;
import jakarta.xml.ws.handler.Handler;
import jakarta.xml.ws.handler.HandlerResolver;
import jakarta.xml.ws.handler.PortInfo;

import com.sun.ts.tests.jaxws.common.BaseClient;

public class Client extends BaseClient {

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	// URL properties used by the test
	private static final String WSDLLOC_URL = "dlhandlerservice.wsdlloc.1";

	private static final String ENDPOINT1_URL = "dlhandlerservice.endpoint.1";

	private static final String ENDPOINT4_URL = "dlhandlerservice.endpoint.4";

	private static final String CTXROOT = "dlhandlerservice.ctxroot.1";

	private String url1 = null;

	private String url4 = null;

	private URL wsdlurl = null;

	private String ctxroot = null;

	// service and port information
	private static final String NAMESPACEURI = "http://dlhandlerservice.org/wsdl";

	private static final String SERVICE_NAME = "DLHandlerService";

	private static final String PORT_NAME1 = "HelloPort";

	private static final String PORT_NAME4 = "GetTrackerDataPort";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME1 = new QName(NAMESPACEURI, PORT_NAME1);

	private static final Class SERVICE_CLASS = com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.DLHandlerService.class;

	private static final String THEBINDINGPROTOCOL = jakarta.xml.ws.soap.SOAPBinding.SOAP11HTTP_BINDING;

	private static final String LOGICAL = "Logical";

	private static final String TEST_TYPE = LOGICAL + "Test";

	private Handler handler = null;

	Hello port1 = null;

	GetTrackerData port4 = null;

	static DLHandlerService service = null;

	BindingProvider bp1 = null;

	BindingProvider bp4 = null;

	Binding binding1 = null;

	Binding binding4 = null;

	List<Binding> listOfBindings = new ArrayList<Binding>();

	List<Handler> port1HandlerChain = null;

	List<Handler> port4HandlerChain = null;

	protected void getTestURLs() throws Exception {
		logger.log(Level.INFO, "Get URL's used by the test");
		String file = JAXWS_Util.getURLFromProp(ENDPOINT1_URL);
		url1 = ctsurl.getURLString(PROTOCOL, hostname, portnum, file);
		file = JAXWS_Util.getURLFromProp(WSDLLOC_URL);
		wsdlurl = ctsurl.getURL(PROTOCOL, hostname, portnum, file);
		file = JAXWS_Util.getURLFromProp(ENDPOINT4_URL);
		url4 = ctsurl.getURLString(PROTOCOL, hostname, portnum, file);
		ctxroot = JAXWS_Util.getURLFromProp(CTXROOT);
		logger.log(Level.INFO, "Service Endpoint1 URL: " + url1);
		logger.log(Level.INFO, "Service Endpoint4 URL: " + url4);
		logger.log(Level.INFO, "WSDL Location URL:    " + wsdlurl);
		logger.log(Level.INFO, "Context Root:         " + ctxroot);

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

	protected void getService() throws Exception {
		service = (DLHandlerService) getSharedObject();
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
	 * @testName: ClientMessageContextTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:87; JAXWS:JAVADOC:88; JAXWS:JAVADOC:89;
	 * JAXWS:JAVADOC:91; JAXWS:JAVADOC:92; JAXWS:JAVADOC:93; JAXWS:JAVADOC:94;
	 * JAXWS:JAVADOC:95; JAXWS:JAVADOC:90; JAXWS:SPEC:9022; JAXWS:SPEC:9023;
	 * JAXWS:SPEC:9024; JAXWS:SPEC:9025; JAXWS:SPEC:9026; JAXWS:SPEC:9041;
	 * WS4EE:SPEC:6012; WS4EE:SPEC:6002; WS4EE:SPEC:6008; WS4EE:SPEC:6039;
	 * WS4EE:SPEC:6047;
	 *
	 * @test_Strategy: Invoke an RPC method and ensure that the client-side logical
	 * message context callbacks are called.
	 */
	@Test
	public void ClientMessageContextTest() throws Exception {
		TestUtil.logTrace("ClientMessageContextTest");
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
									+ "ClientSOAPHandler5, ClientLogicalHandler5");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler5 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler5();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler5 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler5();
					handlerList.add(handler);
					if (info.getBindingID().equals(THEBINDINGPROTOCOL)) {
						logger.log(Level.INFO, "----------------------------------------------");
						logger.log(Level.INFO,
								"Programmatically registering the following protocol based handlers through the binding: \n"
										+ "ClientSOAPHandler1, ClientLogicalHandler1");
						logger.log(Level.INFO, "----------------------------------------------");
						logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler1 and add to HandlerChain");
						handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler1();
						handlerList.add(handler);
						logger.log(Level.INFO,
								"Construct HandleInfo for ClientLogicalHandler1 and add to HandlerChain");
						handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler1();
						handlerList.add(handler);
					}
					if (info.getPortName().equals(PORT_QNAME1)) {
						logger.log(Level.INFO, "----------------------------------------------");
						TestUtil.logMsg("Create port based handlers for port: " + PORT_QNAME1);
						logger.log(Level.INFO, "----------------------------------------------");
						logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler2 and add to HandlerChain");
						handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler2();
						handlerList.add(handler);
						logger.log(Level.INFO,
								"Construct HandleInfo for ClientLogicalHandler2 and add to HandlerChain");
						handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler2();
						handlerList.add(handler);
						if (info.getPortName().equals(PORT_QNAME1)) {
							logger.log(Level.INFO,
									"Construct HandleInfo for ClientSOAPHandler3 and add to HandlerChain");
							handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler3();
							handlerList.add(handler);
							logger.log(Level.INFO,
									"Construct HandleInfo for ClientLogicalHandler3 and add to HandlerChain");
							handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler3();
							handlerList.add(handler);
						}
					}
					logger.log(Level.INFO, "HandlerChainList=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					return handlerList;
				}

			});

			if (!setupPorts()) {
				pass = false;
			} else {
				try {
					logger.log(Level.INFO, "Getting existing Handlers for Port1");
					logger.log(Level.INFO, "----------------------------------------------");
					List<Handler> handlerList = binding1.getHandlerChain();

					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Programmatically registering the following handlers through the binding: \n"
							+ "ClientSOAPHandler6, ClientLogicalHandler6");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler6 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler6();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler6 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler6();
					handlerList.add(handler);
					logger.log(Level.INFO, "HandlerChain=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					binding1.setHandlerChain(handlerList);
				} catch (Exception e) {
					TestUtil.logErr("ERROR: Adding handlers to the binding failed with the following exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}
			}
			if (pass) {

				logger.log(Level.INFO, "Getting RequestContext to set a property");
				java.util.Map<String, Object> rc = bp1.getRequestContext();
				Iterator iterator = rc.keySet().iterator();
				if (iterator.hasNext()) {
					String key = (String) iterator.next();
					logger.log(Level.INFO, "Request context key=" + key);
					rc.put("ClientToClientProp", "client");
				} else {
					TestUtil.logErr("The request context returned from BindingProvider.getRequestContext() was empty");
					pass = false;
				}

				logger.log(Level.INFO, "Invoking RPC method port1.doHandlerTest1()");
				MyActionType ma = new MyActionType();
				ma.setAction("ClientMessageContextTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);
				MyResultType mr = null;
				try {
					mr = port1.doHandlerTest1(ma);
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				if (mr != null) {
					String errors = mr.getErrors();
					if (errors != null) {
						if (!errors.equals("")) {
							TestUtil.logErr("ERROR: The following errors were reported by the endpoint:" + errors);
							pass = false;
						}
					}
				}

				boolean clientLogicalMessageScopeAppProp = false;
				boolean clientLogicalMessageScopeHandlerProp = false;
				boolean clientToClientProp = false;

				TestUtil.logTrace("Getting ResponseContext");
				rc = bp1.getResponseContext();
				if (rc == null) {
					TestUtil.logErr("The response context returned from BindingProvider.getResponseContext() was null");
					pass = false;
				} else {
					iterator = rc.keySet().iterator();
					while (iterator.hasNext()) {
						Object o = iterator.next();
						TestUtil.logTrace("Object Property=" + o);
						if (o instanceof String) {
							String key = (String) o;
							if (key.equals("INBOUNDClientLogicalMessageScopeAppPropSetByHandler3")) {
								clientLogicalMessageScopeAppProp = true;
								TestUtil.logTrace("Found INBOUNDClientLogicalMessageScopeAppPropSetByHandler3");
							}
							if (key.equals("INBOUNDClientLogicalMessageScopeHandlerPropSetByHandler3")) {
								clientLogicalMessageScopeHandlerProp = true;
								TestUtil.logTrace("Found INBOUNDClientLogicalMessageScopeHandlerPropSetByHandler3");
							}
							if (key.equals("ClientToClientProp")) {
								clientToClientProp = true;
								TestUtil.logTrace("Found ClientToClientProp");
								Object o1 = rc.get("ClientToClientProp");
								if (o1 instanceof String) {
									String value = (String) o1;
									String expected = "clientOUTBOUNDClientLogicalHandler2INBOUNDClientLogicalHandler2";
									if (!value.equals(expected)) {
										TestUtil.logErr("The value of ClientToClientProp was wrong");
										TestUtil.logErr("Expected = " + expected);
										TestUtil.logErr("Actual = " + value);
										pass = false;
									}
								} else {
									TestUtil.logErr("The value of ClientToClientProp was not a String");
									pass = false;

								}
							}
						}
					}
					if (!clientLogicalMessageScopeAppProp) {
						TestUtil.logErr(
								"The property INBOUNDClientLogicalMessageScopeAppPropSetByHandler3 was not accessible by the client");
						pass = false;
					}
					if (!clientToClientProp) {
						TestUtil.logErr("The property ClientToClientProp was not accessible by the client");
						pass = false;
					}
					if (clientLogicalMessageScopeHandlerProp) {
						TestUtil.logErr(
								"The property INBOUNDClientLogicalMessageScopeHandlerPropSetByHandler3 was accessible by the client");
						pass = false;
					}
				}

				logger.log(Level.INFO, "Get client side results back from Tracker");
				List<String> clientSideMCMsgs = HandlerTracker.getListMessages2();
				List<String> clientSideLMCMsgs = HandlerTracker.getListMessages4();

				// verify client-side callbacks
				logger.log(Level.INFO, "Verifying MessageContext callbacks on Client-Side");
				if (!Handler_Util.VerifyMessageContextCallBacks("Client", LOGICAL, clientSideMCMsgs)) {
					TestUtil.logErr("Client-Side MessageContext Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Client-Side MessageContext Callbacks are (correct)");
				}

				TestUtil.logMsg("Verifying LogicalMessageContext callbacks on Client-Side");
				if (!Handler_Util.VerifyLogicalOrSOAPMessageContextCallBacks("Client", LOGICAL, clientSideLMCMsgs)) {
					TestUtil.logErr("Client-Side LogicalMessageContext Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Client-Side LogicalMessageContext Callbacks are (correct)");
				}
				logger.log(Level.INFO, "Get client side throwables back from Tracker");
				String[] clientSideThrowables = HandlerTracker.getArrayThrowables();
				int len = clientSideThrowables.length;
				if (len > 0) {
					TestUtil.logErr("There were exceptions thrown in the Client Handlers");
					for (int i = 0; i <= len - 1; i++) {
						TestUtil.logErr(clientSideThrowables[i]);
						pass = false;
					}
				} else {
					logger.log(Level.INFO, "There were no Client Handler exceptions");
				}

				Handler_Util.clearHandlers(listOfBindings);
				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();

			}
		} catch (Exception e) {
			TestUtil.logErr("Exception occurred: " + e);
			pass = false;
		}

		if (!pass)
			throw new Exception("ClientMessageContextTest failed");
	}

	/*
	 * @testName: ServerMessageContextTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:87; JAXWS:JAVADOC:88; JAXWS:JAVADOC:89;
	 * JAXWS:JAVADOC:91; JAXWS:JAVADOC:92; JAXWS:JAVADOC:93; JAXWS:JAVADOC:94;
	 * JAXWS:JAVADOC:95; JAXWS:SPEC:9022; JAXWS:SPEC:9023; JAXWS:SPEC:9024;
	 * JAXWS:SPEC:9025; JAXWS:SPEC:9026; JAXWS:SPEC:9041; WS4EE:SPEC:6012;
	 * WS4EE:SPEC:6002; WS4EE:SPEC:6008; WS4EE:SPEC:6039; WS4EE:SPEC:6047;
	 *
	 * @test_Strategy: Invoke an RPC method and ensure that the server-side soap
	 * message handler callbacks are called.
	 */
	@Test
	public void ServerMessageContextTest() throws Exception {
		TestUtil.logTrace("ServerMessageContextTest");
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
				ma.setAction("ServerMessageContextTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);
				MyResultType mr = null;
				try {
					mr = port1.doHandlerTest1(ma);
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				if (mr != null) {
					String errors = mr.getErrors();
					if (!errors.equals("")) {
						TestUtil.logErr("ERROR: The following errors were reported by the endpoint:" + errors);
						pass = false;
					}
				}

				List<String> serverSideMCMsgs = null;
				List<String> serverSideLMCMsgs = null;

				logger.log(Level.INFO, "Get server side result back from endpoint");
				GetTrackerDataAction gtda = new GetTrackerDataAction();
				gtda.setAction("getArrayMessages2");
				gtda.setHarnessloghost(harnessHost);
				gtda.setHarnesslogport(harnessLogPort);
				gtda.setHarnesslogtraceflag(harnessLogTraceFlag);
				try {
					serverSideMCMsgs = port4.getTrackerData(gtda).getResult();
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}
				gtda.setAction("getArrayMessages4");
				gtda.setHarnessloghost(harnessHost);
				gtda.setHarnesslogport(harnessLogPort);
				gtda.setHarnesslogtraceflag(harnessLogTraceFlag);
				try {
					serverSideLMCMsgs = port4.getTrackerData(gtda).getResult();
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				logger.log(Level.INFO, "Verifying MessageContext callbacks on Server-Side");
				if (!Handler_Util.VerifyMessageContextCallBacks("Server", LOGICAL, serverSideMCMsgs)) {
					TestUtil.logErr("Server-Side MessageContext Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Server-Side MessageContext Callbacks are (correct)");
				}
				TestUtil.logMsg("Verifying LogicalMessageContext callbacks on Server-Side");
				if (!Handler_Util.VerifyLogicalOrSOAPMessageContextCallBacks("Server", LOGICAL, serverSideLMCMsgs)) {
					TestUtil.logErr("Server-Side LogicalMessageContext Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Server-Side LogicalMessageContext Callbacks are (correct)");
				}
				gtda = new GetTrackerDataAction();
				logger.log(Level.INFO, "Get server side throwables back from endpoint");
				gtda.setAction("getArrayThrowables");
				gtda.setHarnessloghost(harnessHost);
				gtda.setHarnesslogport(harnessLogPort);
				gtda.setHarnesslogtraceflag(harnessLogTraceFlag);
				List<String> serverSideThrowables = port4.getTrackerData(gtda).getResult();
				if (serverSideThrowables.size() >= 1) {
					TestUtil.logErr("There were exceptions thrown in the Client Handlers");
					Iterator iterator = serverSideThrowables.iterator();
					while (iterator.hasNext()) {
						TestUtil.logErr((String) iterator.next());
					}
					pass = false;
				}

				logger.log(Level.INFO, "Purging server-side tracker data");
				purgeServerSideTrackerData();
			} catch (Exception e) {
				TestUtil.logErr("Exception occurred: " + e);
				pass = false;
			}
		}
		if (!pass)
			throw new Exception("ServerMessageContextTest failed");
	}

	/*
	 * @testName: ContextPropertiesTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9026; JAXWS:SPEC:9027; JAXWS:SPEC:9033;
	 * JAXWS:SPEC:9034; JAXWS:SPEC:9035; JAXWS:SPEC:9036; JAXWS:SPEC:9037;
	 * JAXWS:SPEC:9038; JAXWS:SPEC:9039; JAXWS:SPEC:9040; WS4EE:SPEC:6012;
	 * WS4EE:SPEC:6002; WS4EE:SPEC:6008; WS4EE:SPEC:6047;
	 *
	 * @test_Strategy: Invoke an RPC method and ensure that the various
	 * MessageContext and LogicalMessageContext properties are accessible
	 */
	@Test
	public void ContextPropertiesTest() throws Exception {
		TestUtil.logTrace("ContextPropertiesTest");
		boolean pass = true;
		logger.log(Level.INFO, "Programatically registering the client side handlers by creating new HandlerResolver.");
		service.setHandlerResolver(new HandlerResolver() {
			public List<Handler> getHandlerChain(PortInfo info) {
				List<Handler> handlerList = new ArrayList<Handler>();
				if (info.getPortName().equals(PORT_QNAME1)) {
					logger.log(Level.INFO, "----------------------------------------------");
					TestUtil.logMsg("Create port based handlers for port: " + PORT_QNAME1);
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler2 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler2();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler2 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler2();
					handlerList.add(handler);
				}
				logger.log(Level.INFO, "HandlerChainList=" + handlerList);
				logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
				return handlerList;
			}

		});

		if (!setupPorts()) {
			pass = false;
		} else {

			try {

				logger.log(Level.INFO, "Purging server-side tracker data");
				purgeServerSideTrackerData();

				logger.log(Level.INFO, "Invoking RPC method port1.doHandlerTest1()");
				MyActionType ma = new MyActionType();

				ma.setAction("ContextPropertiesTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);

				URL url1;
				url1 = ctsurl.getURL("http", hostname, portnum, ctxroot + "/attach.text");
				logger.log(Level.INFO, "url1=" + url1);
				DataHandler dh1 = new DataHandler(url1);
				Holder<jakarta.activation.DataHandler> attach1 = new Holder<jakarta.activation.DataHandler>();
				attach1.value = dh1;
				MyResult2 mr = null;
				try {
					mr = port1.doHandlerAttachmentTest(ma, attach1);
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				if (mr != null) {
					String errors = mr.getErrors();
					if (!errors.equals("")) {
						TestUtil.logErr("ERROR: The following errors were reported by the endpoint:" + errors);
						pass = false;
					}
				}

				if (mr != null) {
					List<String> lResults = mr.getResult();
					JAXWS_Util.dumpList(mr.getResult());

					if (JAXWS_Util.looseIndexOf(lResults,
							"Endpoint:MessageContext.INBOUND_MESSAGE_ATTACHMENTS=key[0]") == -1) {
						TestUtil.logErr(
								"The property MessageContext.INBOUND_MESSAGE_ATTACHMENTS did not contain an attachment in the endpoint");
						pass = false;
					} else {
						TestUtil.logTrace("Found Endpoint:MessageContext.INBOUND_MESSAGE_ATTACHMENTS=key[0]");
					}
					if (JAXWS_Util.looseIndexOf(lResults,
							"Endpoint:MessageContext.INBOUND_MESSAGE_ATTACHMENTS=key[1]") >= 0) {
						TestUtil.logErr(
								"The property MessageContext.INBOUND_MESSAGE_ATTACHMENTS contained more than one attachment in the endpoint");
						pass = false;
					} else {
						TestUtil.logTrace("Found Endpoint:MessageContext.INBOUND_MESSAGE_ATTACHMENTS=key[1]");
					}
					if (lResults.indexOf("Endpoint:MessageContext.HTTP_REQUEST_METHOD=POST") == -1) {
						TestUtil.logErr("The property MessageContext.HTTP_REQUEST_METHOD was not POST in the endpoint");
						pass = false;
					} else {
						TestUtil.logTrace("Found Endpoint:MessageContext.HTTP_REQUEST_METHOD=POST");
					}
					if (JAXWS_Util.looseIndexOf(lResults,
							"Endpoint:MessageContext.HTTP_REQUEST_HEADERS=value[0]=") == -1) {
						TestUtil.logErr(
								"The property MessageContext.HTTP_REQUEST_HEADERS did not contain any headers in the endpoint");
						pass = false;
					} else {
						TestUtil.logTrace(
								"The property MessageContext.HTTP_REQUEST_HEADERS did contain headers in the endpoint");
					}
					if (lResults.indexOf("Endpoint:MessageContext.HTTP_RESPONSE_HEADERS=null") == -1) {
						TestUtil.logErr(
								"The property MessageContext.HTTP_RESPONSE_HEADERS was not null in the endpoint");
						pass = false;
					} else {
						TestUtil.logTrace("The property MessageContext.HTTP_RESPONSE_HEADERS was null in the endpoint");
					}
					if (lResults.indexOf("Endpoint:MessageContext.SERVLET_REQUEST=null") >= 0) {
						TestUtil.logErr("The property MessageContext.SERVLET_REQUEST was null in the endpoint");
						pass = false;
					} else {
						TestUtil.logTrace("The property MessageContext.SERVLET_REQUEST was not null in the endpoint");
					}
					if (lResults.indexOf("Endpoint:MessageContext.SERVLET_RESPONSE=null") >= 0) {
						TestUtil.logErr("The property MessageContext.SERVLET_RESPONSE was null in the endpoint");
						pass = false;
					} else {
						TestUtil.logTrace("The property MessageContext.SERVLET_RESPONSE was not null in the endpoint");
					}
					if (lResults.indexOf("Endpoint:MessageContext.SERVLET_CONTEXT=null") >= 0) {
						TestUtil.logErr("The property MessageContext.SERVLET_CONTEXT was null in the endpoint");
						pass = false;
					} else {
						TestUtil.logTrace("The property MessageContext.SERVLET_CONTEXT was not null in the endpoint");
					}
				}

				if (mr != null) {
					String endpointErrors = mr.getErrors();
					if (!endpointErrors.equals("")) {
						TestUtil.logErr("Erors:" + endpointErrors);
						pass = false;
					}
				}

				logger.log(Level.INFO, "Get client side results back from Tracker");
				List<String> clientSideMCMsgs = HandlerTracker.getListMessages2();

				List<String> serverSideMCMsgs = null;

				logger.log(Level.INFO, "Get server side result back from endpoint");
				GetTrackerDataAction gtda = new GetTrackerDataAction();
				gtda.setAction("getArrayMessages2");
				gtda.setHarnessloghost(harnessHost);
				gtda.setHarnesslogport(harnessLogPort);
				gtda.setHarnesslogtraceflag(harnessLogTraceFlag);
				try {
					serverSideMCMsgs = port4.getTrackerData(gtda).getResult();
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				// verify client-side callbacks
				logger.log(Level.INFO,
						"Verifying MessageContext and LogicalMessageContext propterty callbacks on Client-Side");
				if (!Handler_Util.VerifyStandardMessageContextPropertiesCallBacks("Client", LOGICAL,
						clientSideMCMsgs)) {
					TestUtil.logErr("Client-Side MessageContext Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Client-Side MessageContext Callbacks are (correct)");
				}
				logger.log(Level.INFO, "Get client side throwables back from Tracker");
				String[] clientSideThrowables = HandlerTracker.getArrayThrowables();
				int len = clientSideThrowables.length;
				if (len > 0) {
					TestUtil.logErr("There were exceptions thrown in the Client Handlers");
					for (int i = 0; i <= len - 1; i++) {
						TestUtil.logErr(clientSideThrowables[i]);
						pass = false;
					}
				} else {
					logger.log(Level.INFO, "There were no Client Handler exceptions");
				}

				Handler_Util.clearHandlers(listOfBindings);
				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();

				logger.log(Level.INFO,
						"Verifying MessageContext and LogicalMessageContext propterty callbacks on Server-Side");
				if (!Handler_Util.VerifyStandardMessageContextPropertiesCallBacks("Server", LOGICAL,
						serverSideMCMsgs)) {
					TestUtil.logErr("Server-Side MessageContext Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Server-Side MessageContext Callbacks are (correct)");
				}
				gtda = new GetTrackerDataAction();
				logger.log(Level.INFO, "Get server side throwables back from endpoint");
				gtda.setAction("getArrayThrowables");
				gtda.setHarnessloghost(harnessHost);
				gtda.setHarnesslogport(harnessLogPort);
				gtda.setHarnesslogtraceflag(harnessLogTraceFlag);
				List<String> serverSideThrowables = port4.getTrackerData(gtda).getResult();
				if (serverSideThrowables.size() >= 1) {
					TestUtil.logErr("There were exceptions thrown in the Client Handlers");
					Iterator iterator = serverSideThrowables.iterator();
					while (iterator.hasNext()) {
						TestUtil.logErr((String) iterator.next());
					}
					pass = false;
				}

				logger.log(Level.INFO, "Purging server-side tracker data");
				purgeServerSideTrackerData();
			} catch (Exception e) {
				TestUtil.logErr("Exception occurred: " + e);
				pass = false;
			}
		}
		if (!pass)
			throw new Exception("ContextPropertiesTest failed");
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

}
