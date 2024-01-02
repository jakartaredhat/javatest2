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

package com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.handlertest;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
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
import com.sun.ts.tests.jaxws.sharedclients.rlhandlerclient.GetTrackerData;
import com.sun.ts.tests.jaxws.sharedclients.rlhandlerclient.GetTrackerDataAction;
import com.sun.ts.tests.jaxws.sharedclients.rlhandlerclient.Hello;
import com.sun.ts.tests.jaxws.sharedclients.rlhandlerclient.MyActionType;
import com.sun.ts.tests.jaxws.sharedclients.rlhandlerclient.MyResultType;
import com.sun.ts.tests.jaxws.sharedclients.rlhandlerclient.RLHandlerService;

import jakarta.xml.ws.Binding;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.handler.Handler;
import jakarta.xml.ws.handler.HandlerResolver;
import jakarta.xml.ws.handler.PortInfo;

public class Client extends BaseClient {

	// URL properties used by the test
	private static final String WSDLLOC_URL = "rlhandlerservice.wsdlloc.1";

	private static final String ENDPOINT1_URL = "rlhandlerservice.endpoint.1";

	private static final String ENDPOINT2_URL = "rlhandlerservice.endpoint.2";

	private String url1 = null;

	private String url2 = null;

	private URL wsdlurl = null;

	// service and port information
	private static final String NAMESPACEURI = "http://rlhandlerservice.org/wsdl";

	private static final String SERVICE_NAME = "RLHandlerService";

	private static final String PORT_NAME1 = "HelloPort";

	private static final String PORT_NAME2 = "GetTrackerDataPort";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME1 = new QName(NAMESPACEURI, PORT_NAME1);

	private static final Class SERVICE_CLASS = com.sun.ts.tests.jaxws.sharedclients.rlhandlerclient.RLHandlerService.class;

	private static final String LOGICAL = "Logical";

	private static final String TEST_TYPE = LOGICAL + "Test";

	private HandlerResolver originalResolver = null;

	private Handler handler = null;

	Hello port1 = null;

	GetTrackerData port2 = null;

	static RLHandlerService service = null;

	BindingProvider bp1 = null;

	BindingProvider bp2 = null;

	Binding binding1 = null;

	Binding binding2 = null;

	List<Binding> listOfBindings = new ArrayList<Binding>();

	List<Handler> port1HandlerChain = null;

	List<Handler> port2HandlerChain = null;

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		return createWebArchive(Client.class);
	}

	protected void getService() {
		service = (RLHandlerService) getSharedObject();
	}

	protected void getTestURLs() throws Exception {
		logger.log(Level.INFO, "Get URL's used by the test");
		String file = JAXWS_Util.getURLFromProp(ENDPOINT1_URL);
		url1 = ctsurl.getURLString(PROTOCOL, hostname, portnum, file);
		file = JAXWS_Util.getURLFromProp(WSDLLOC_URL);
		wsdlurl = ctsurl.getURL(PROTOCOL, hostname, portnum, file);
		file = JAXWS_Util.getURLFromProp(ENDPOINT2_URL);
		url2 = ctsurl.getURLString(PROTOCOL, hostname, portnum, file);
		logger.log(Level.INFO, "Service Endpoint1 URL: " + url1);
		logger.log(Level.INFO, "Service Endpoint2 URL: " + url2);
		logger.log(Level.INFO, "WSDL Location URL:    " + wsdlurl);
	}

	private void getPortsStandalone() throws Exception {
		getPorts();
		JAXWS_Util.setTargetEndpointAddress(port1, url1);
		JAXWS_Util.setTargetEndpointAddress(port2, url2);
	}

	private void getPortsJavaEE() throws Exception {
		logger.log(Level.INFO, "Obtaining service via WebServiceRef annotation");
		logger.log(Level.INFO, "service=" + service);
		getPorts();
		getTargetEndpointAddress(port1, port2);
	}

	private void getTargetEndpointAddress(Object port1, Object port2) throws Exception {
		logger.log(Level.INFO, "Get Target Endpoint Address for port1=" + port1);
		String url1 = JAXWS_Util.getTargetEndpointAddress(port1);
		logger.log(Level.INFO, "Target Endpoint Address=" + url1);
		logger.log(Level.INFO, "Get Target Endpoint Address for port2=" + port2);
		String url2 = JAXWS_Util.getTargetEndpointAddress(port2);
		logger.log(Level.INFO, "Target Endpoint Address=" + url2);
	}

	private void getPorts() throws Exception {
		TestUtil.logTrace("entering getPorts");

		logger.log(Level.INFO, "Get port 1 = " + PORT_NAME1);
		port1 = (Hello) service.getPort(Hello.class);
		logger.log(Level.INFO, "port1=" + port1);

		logger.log(Level.INFO, "Get port 2 = " + PORT_NAME2);
		port2 = (GetTrackerData) service.getPort(GetTrackerData.class);
		logger.log(Level.INFO, "port2=" + port2);

		logger.log(Level.INFO, "Get binding for port 1 = " + PORT_NAME1);
		bp1 = (BindingProvider) port1;
		binding1 = bp1.getBinding();
		port1HandlerChain = binding1.getHandlerChain();
		logger.log(Level.INFO, "Port1 HandlerChain =" + port1HandlerChain);
		logger.log(Level.INFO, "Port1 HandlerChain size = " + port1HandlerChain.size());

		logger.log(Level.INFO, "------------------------------------------------------");

		logger.log(Level.INFO, "Get binding for port 2 = " + PORT_NAME2);
		bp2 = (BindingProvider) port2;
		binding2 = bp2.getBinding();
		port2HandlerChain = binding2.getHandlerChain();
		logger.log(Level.INFO, "Port2 HandlerChain=" + port2HandlerChain);
		logger.log(Level.INFO, "Port2 HandlerChain size = " + port2HandlerChain.size());

		listOfBindings.add(binding1);
		listOfBindings.add(binding2);

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
		super.setup();
		if (modeProperty.equals("standalone")) {
			logger.log(Level.INFO, "Create Service object");
			getTestURLs();
			service = (RLHandlerService) JAXWS_Util.getService(wsdlurl, SERVICE_QNAME, SERVICE_CLASS);
		} else {
			getTestURLs();
			logger.log(Level.INFO, "WebServiceRef is not set in Client (get it from specific vehicle)");
			service = (RLHandlerService) getSharedObject();
		}
	}

	@AfterEach
	public void cleanup() throws Exception {
		logger.log(Level.INFO, "cleanup ok");
	}

	/*
	 * @testName: ClientLogicalHandlerTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9002; JAXWS:SPEC:9007; JAXWS:SPEC:9012;
	 * JAXWS:SPEC:9014; JAXWS:SPEC:9015.1; JAXWS:SPEC:9017; JAXWS:SPEC:9018;
	 * WS4EE:SPEC:6010; WS4EE:SPEC:6015.1; WS4EE:SPEC:6015.2; WS4EE:SPEC:6015.3;
	 * WS4EE:SPEC:6008; WS4EE:SPEC:6028; WS4EE:SPEC:6005; WS4EE:SPEC:6051;
	 * WS4EE:SPEC:6027;
	 *
	 * @test_Strategy: Invoke an RPC method and ensure that the client-side logical
	 * message handler callbacks are called.
	 */
	@Test
	public void ClientLogicalHandlerTest() throws Exception {
		TestUtil.logTrace("ClientLogicalHandlerTest");
		boolean pass = true;
		final MyStatus myStatus = new MyStatus();
		try {
			logger.log(Level.INFO,
					"Programmatically registering the following handlers: \n"
							+ "ClientLogicalHandler2, ClientLogicalHandler3, ClientLogicalHandler6\n"
							+ "ClientSOAPHandler2, ClientSOAPHandler3, ClientSOAPHandler6");

			logger.log(Level.INFO, "----------------------------------------------");
			logger.log(Level.INFO, "Getting existing Resolver");
			logger.log(Level.INFO, "----------------------------------------------");
			originalResolver = service.getHandlerResolver();

			service.setHandlerResolver(new HandlerResolver() {
				public List<Handler> getHandlerChain(PortInfo info) {
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Getting existing Handlers");
					logger.log(Level.INFO, "----------------------------------------------");
					List<Handler> handlerList = originalResolver.getHandlerChain(info);
					logger.log(Level.INFO, "HandlerChainList=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					if (info.getPortName().equals(PORT_QNAME1)) {
						logger.log(Level.INFO, "----------------------------------------------");
						TestUtil.logMsg("Create port based handlers for port: " + PORT_QNAME1);
						logger.log(Level.INFO, "----------------------------------------------");
						logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler2 and add to HandlerChain");
						handler = new com.sun.ts.tests.jaxws.sharedclients.rlhandlerclient.ClientSOAPHandler2();
						handlerList.add(handler);
						logger.log(Level.INFO,
								"Construct HandleInfo for ClientLogicalHandler2 and add to HandlerChain");
						handler = new com.sun.ts.tests.jaxws.sharedclients.rlhandlerclient.ClientLogicalHandler2();
						handlerList.add(handler);
						if (info.getPortName().equals(PORT_QNAME1)) {
							logger.log(Level.INFO,
									"Construct HandleInfo for ClientSOAPHandler3 and add to HandlerChain");
							handler = new com.sun.ts.tests.jaxws.sharedclients.rlhandlerclient.ClientSOAPHandler3();
							handlerList.add(handler);
							logger.log(Level.INFO,
									"Construct HandleInfo for ClientLogicalHandler3 and add to HandlerChain");
							handler = new com.sun.ts.tests.jaxws.sharedclients.rlhandlerclient.ClientLogicalHandler3();
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
			} else if (!myStatus.getStatus()) {
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
					handler = new com.sun.ts.tests.jaxws.sharedclients.rlhandlerclient.ClientSOAPHandler6();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler6 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.rlhandlerclient.ClientLogicalHandler6();
					handlerList.add(handler);
					logger.log(Level.INFO, "HandlerChain=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					binding1.setHandlerChain(handlerList);
				} catch (Exception e) {
					TestUtil.logErr("Adding handlers to the binding failed with the following exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}
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

				logger.log(Level.INFO, "Verify handleMessage()/init callbacks");
				logger.log(Level.INFO, "Verifying Client-Side JAXWS-RUNTIME Callbacks");
				if (!Handler_Util.VerifyHandlerCallBacks3("Client", LOGICAL, clientSideMsgs)) {
					TestUtil.logErr("Client-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Client-Side Callbacks are (correct)");
				}
				logger.log(Level.INFO,
						"Verifying callbacks where LogicalHandlers were called before SOAPHandlers on Client-Side");
				if (!Handler_Util.VerifyLogicalVerseSOAPHandlerOrder(clientSideMsgs)) {
					TestUtil.logErr("Client-Side Logical verses SOAP Handler Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Client-Side Logical verses SOAP Handler Callbacks are (correct)");
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
				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();

			}
		} catch (Exception e) {
			TestUtil.logErr("Exception occurred: " + e);
			pass = false;
		}

		if (!pass)
			throw new Exception("ClientLogicalHandlerTest failed");
	}

	/*
	 * @testName: ClientHandlerDoesNotGetCalledTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9002; JAXWS:SPEC:9007; JAXWS:SPEC:9011;
	 *
	 * @test_Strategy: Invoke an RPC method and ensure that a client-side handler
	 * added to the binding does not get called. The service-name-pattern and
	 * port-name-pattern tags are used in the handler xml file to denote what what
	 * handlers will and won't be instanciated
	 */
	@Test
	public void ClientHandlerDoesNotGetCalledTest() throws Exception {
		TestUtil.logTrace("ClientHandlerDoesNotGetCalledTest");
		boolean pass = true;
		try {
			if (!setupPorts()) {
				pass = false;
			} else {
				logger.log(Level.INFO,
						"Programmatically registering the following handlers which should never be called: \n"
								+ "ClientSNBCLogicalHandler, ClientSNBCSOAPHandler");
				List<Handler> handlerList = new ArrayList<Handler>();
				logger.log(Level.INFO, "Construct HandleInfo for ClientSNBCLogicalHandler and add to HandlerChain");
				handler = new com.sun.ts.tests.jaxws.sharedclients.rlhandlerclient.ClientSNBCLogicalHandler();
				handlerList.add(handler);
				logger.log(Level.INFO, "Construct HandleInfo for ClientSNBCSOAPHandler and add to HandlerChain");
				handler = new com.sun.ts.tests.jaxws.sharedclients.rlhandlerclient.ClientSNBCSOAPHandler();
				handlerList.add(handler);
				binding1.setHandlerChain(handlerList);
				port1HandlerChain = binding1.getHandlerChain();
				logger.log(Level.INFO, "Port1 HandlerChain =" + port1HandlerChain);
				TestUtil.logMsg("Port1 HandlerChain size = " + port1HandlerChain.size());
				TestUtil.logMsg("------------------------------------------------------");
				logger.log(Level.INFO, "Get port 1 again = " + PORT_NAME1);
				port1 = (Hello) service.getPort(Hello.class);
				logger.log(Level.INFO, "Get binding for port 1 again = " + PORT_NAME1);
				bp1 = (BindingProvider) port1;
				binding1 = bp1.getBinding();
				port1HandlerChain = binding1.getHandlerChain();
				logger.log(Level.INFO, "Port1 HandlerChain =" + port1HandlerChain);
				TestUtil.logMsg("Port1 HandlerChain size = " + port1HandlerChain.size());
				TestUtil.logMsg("------------------------------------------------------");

				try {
					logger.log(Level.INFO, "Getting existing Handlers for Port1");
					logger.log(Level.INFO, "----------------------------------------------");
					handlerList = binding1.getHandlerChain();

					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Programmatically registering the following handlers through the binding: \n"
							+ "ClientSOAPHandler6, ClientLogicalHandler6");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler6 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.rlhandlerclient.ClientSOAPHandler6();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler6 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.rlhandlerclient.ClientLogicalHandler6();
					handlerList.add(handler);
					logger.log(Level.INFO, "HandlerChain=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					binding1.setHandlerChain(handlerList);
				} catch (Exception e) {
					TestUtil.logErr("Adding handlers to the binding failed with the following exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				logger.log(Level.INFO, "Invoking RPC method port1.doHandlerTest1()");
				MyActionType ma = new MyActionType();
				ma.setAction("ClientHandlerDoesNotGetCalledTest");
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

				logger.log(Level.INFO, "Verifying Client-Side JAXWS-RUNTIME Callbacks");
				if (!Handler_Util.VerifyHandlerDoesNotGetCalled("Client", clientSideMsgs)) {
					TestUtil.logErr("Client-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Client-Side Callbacks are (correct)");
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
				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();

			}
		} catch (Exception e) {
			TestUtil.logErr("Exception occurred: " + e);
			pass = false;
		}

		if (!pass)
			throw new Exception("ClientHandlerDoesNotGetCalledTest failed");
	}

	/*
	 * @testName: ServerLogicalHandlerTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9002; JAXWS:SPEC:9007; JAXWS:SPEC:9014;
	 * JAXWS:SPEC:9015.1; JAXWS:SPEC:9017; JAXWS:SPEC:9018; WS4EE:SPEC:6010;
	 * WS4EE:SPEC:6008; WS4EE:SPEC:6028; WS4EE:SPEC:6005; WS4EE:SPEC:6051;
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
				MyResultType mr = null;
				try {
					mr = port1.doHandlerTest1(ma);
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}
				String result = null;
				if (mr != null)
					result = mr.getErrors();
				else
					result = "null return";
				if (!result.equals("")) {
					pass = false;
					TestUtil.logErr("The serverside tests for MessageContext.Scope failed:" + result);
				}

				List<String> serverSideMsgs = null;

				logger.log(Level.INFO, "Get server side result back from endpoint");
				GetTrackerDataAction gtda = new GetTrackerDataAction();
				gtda.setAction("getArrayMessages1");
				gtda.setHarnessloghost(harnessHost);
				gtda.setHarnesslogport(harnessLogPort);
				gtda.setHarnesslogtraceflag(harnessLogTraceFlag);
				try {
					serverSideMsgs = port2.getTrackerData(gtda).getResult();
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				logger.log(Level.INFO, "Verifying Server-Side Handler callbacks");
				serverSideMsgs = JAXWS_Util.getMessagesStartingFrom(serverSideMsgs, Constants.INBOUND);

				if (!Handler_Util.VerifyHandlerCallBacks("Server", LOGICAL, serverSideMsgs)) {
					TestUtil.logErr("Server-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Server-Side Callbacks are (correct)");
				}

				logger.log(Level.INFO,
						"Verifying callbacks where LogicalHandlers are called before SOAPHandlers on Server-Side");
				if (!Handler_Util.VerifyLogicalVerseSOAPHandlerOrder(serverSideMsgs)) {
					TestUtil.logErr("Server-Side Logical verses SOAP Handler Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Server-Side Logical verses SOAP Handler Callbacks are (correct)");
				}
				gtda = new GetTrackerDataAction();
				logger.log(Level.INFO, "Get server side throwables back from endpoint");
				gtda.setAction("getArrayThrowables");
				gtda.setHarnessloghost(harnessHost);
				gtda.setHarnesslogport(harnessLogPort);
				gtda.setHarnesslogtraceflag(harnessLogTraceFlag);
				List<String> serverSideThrowables = port2.getTrackerData(gtda).getResult();
				if (serverSideThrowables.size() >= 1) {
					TestUtil.logErr("There were exceptions thrown in the Server Handlers");
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
			throw new Exception("ServerLogicalHandlerTest failed");
	}

	/*
	 * @testName: ServerHandlerDoesNotGetCalledTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9002; JAXWS:SPEC:9007; JAXWS:SPEC:9014;
	 * JAXWS:SPEC:9015.1; JAXWS:SPEC:9017; JAXWS:SPEC:9018;
	 *
	 * @test_Strategy: Invoke an RPC method and ensure that the server-side soap
	 * message handler callbacks are called. The service-name-pattern and
	 * port-name-pattern tags are used in the handler xml file to denote what what
	 * handlers will and won't be instanciated
	 */
	@Test
	public void ServerHandlerDoesNotGetCalledTest() throws Exception {
		TestUtil.logTrace("ServerHandlerDoesNotGetCalledTest");
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
				ma.setAction("ServerHandlerDoesNotGetCalledTest");
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
				String result = null;
				if (mr != null)
					result = mr.getErrors();
				else
					result = "null return";
				if (!result.equals("")) {
					pass = false;
					TestUtil.logErr("The serverside tests for MessageContext.Scope failed:" + result);
				}

				List<String> serverSideMsgs = null;

				logger.log(Level.INFO, "Get server side result back from endpoint");
				GetTrackerDataAction gtda = new GetTrackerDataAction();
				gtda.setAction("getArrayMessages1");
				gtda.setHarnessloghost(harnessHost);
				gtda.setHarnesslogport(harnessLogPort);
				gtda.setHarnesslogtraceflag(harnessLogTraceFlag);
				try {
					serverSideMsgs = port2.getTrackerData(gtda).getResult();
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				logger.log(Level.INFO, "Verifying Server-Side Handler callbacks");
				serverSideMsgs = JAXWS_Util.getMessagesStartingFrom(serverSideMsgs, Constants.INBOUND);

				if (!Handler_Util.VerifyHandlerDoesNotGetCalled("Server", serverSideMsgs)) {
					TestUtil.logErr("Server-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Server-Side Callbacks are (correct)");
				}

				gtda = new GetTrackerDataAction();
				logger.log(Level.INFO, "Get server side throwables back from endpoint");
				gtda.setAction("getArrayThrowables");
				gtda.setHarnessloghost(harnessHost);
				gtda.setHarnesslogport(harnessLogPort);
				gtda.setHarnesslogtraceflag(harnessLogTraceFlag);
				List<String> serverSideThrowables = port2.getTrackerData(gtda).getResult();
				if (serverSideThrowables.size() >= 1) {
					TestUtil.logErr("There were exceptions thrown in the Server Handlers");
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
			throw new Exception("ServerHandlerDoesNotGetCalledTest failed");
	}

	private void purgeServerSideTrackerData() {
		try {
			GetTrackerDataAction gtda = new GetTrackerDataAction();
			gtda.setAction("purge");
			gtda.setHarnessloghost(harnessHost);
			gtda.setHarnesslogport(harnessLogPort);
			gtda.setHarnesslogtraceflag(harnessLogTraceFlag);
			port2.getTrackerData(gtda);
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
