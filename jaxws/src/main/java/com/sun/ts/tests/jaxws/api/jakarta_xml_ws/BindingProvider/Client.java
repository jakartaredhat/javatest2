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

package com.sun.ts.tests.jaxws.api.jakarta_xml_ws.BindingProvider;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.porting.TSURL;
import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;
import com.sun.ts.tests.jaxws.sharedclients.doclithelloclient.Hello;
import com.sun.ts.tests.jaxws.sharedclients.doclithelloclient.HelloRequest;
import com.sun.ts.tests.jaxws.sharedclients.doclithelloclient.HelloService;
import com.sun.ts.tests.jaxws.wsa.common.EprUtil;

import jakarta.xml.ws.Binding;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.Dispatch;
import jakarta.xml.ws.EndpointReference;
import jakarta.xml.ws.wsaddressing.W3CEndpointReference;

import com.sun.ts.tests.jaxws.common.BaseClient;

public class Client extends BaseClient {
	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	private static final String PKG_NAME = "com.sun.ts.tests.jaxws.api.jakarta_xml_ws.BindingProvider.";

	private static final String SHARED_CLIENT_PKG = "com.sun.ts.tests.jaxws.sharedclients.doclithelloclient.";

	private static final String NAMESPACEURI = "http://helloservice.org/wsdl";

	private static final String SERVICE_NAME = "HelloService";

	private static final String PORT_NAME = "HelloPort";

	private static final String PORT_TYPE = "Hello";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME = new QName(NAMESPACEURI, PORT_NAME);

	private QName PORT_TYPE_QNAME = new QName(NAMESPACEURI, PORT_TYPE);

	private String helloReq = "<HelloRequest xmlns=\"http://helloservice.org/types\"><argument>foo</argument></HelloRequest>";

	private static final String ENDPOINT_URL = "dlhelloservice.endpoint.1";

	private static final String WSDLLOC_URL = "dlhelloservice.wsdlloc.1";

	private String url = null;

	private URL wsdlurl = null;

	private EndpointReference epr = null;

	private Binding binding = null;

	private BindingProvider bpStub = null;

	private Dispatch<Source> dispatchSrc = null;

	private Hello port = null;

	private static final Class SERVICE_CLASS = com.sun.ts.tests.jaxws.sharedclients.doclithelloclient.HelloService.class;

	static HelloService service = null;

	private Dispatch<Source> createDispatchSrc(QName port, Class type, jakarta.xml.ws.Service.Mode mode) {
		logger.log(Level.INFO, "Create a Dispatch object for SOAP 1.1 over HTTP binding");
		return service.createDispatch(port, type, mode);
	}

	private void getPorts() throws Exception {
		logger.log(Level.INFO, "Get port  = " + PORT_NAME);
		port = (Hello) service.getPort(Hello.class);
		logger.log(Level.INFO, "port=" + port);
	}

	private void getPortsStandalone() throws Exception {
		getPorts();
		bpStub = (BindingProvider) port;
		dispatchSrc = createDispatchSrc(PORT_QNAME, Source.class, jakarta.xml.ws.Service.Mode.PAYLOAD);
		JAXWS_Util.setTargetEndpointAddress(port, url);
	}

	private void getPortsJavaEE() throws Exception {
		logger.log(Level.INFO, "Obtaining service via WebServiceRef annotation");
		logger.log(Level.INFO, "service=" + service);
		getPorts();
		bpStub = (BindingProvider) port;
		logger.log(Level.INFO, "Get Target Endpoint Address for port=" + port);
		String url = JAXWS_Util.getTargetEndpointAddress(port);
		logger.log(Level.INFO, "Target Endpoint Address=" + url);
		dispatchSrc = service.createDispatch(PORT_QNAME, Source.class, jakarta.xml.ws.Service.Mode.PAYLOAD);
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
	 */@BeforeEach
	public void setup() throws Exception {

		try {
			super.setup();
			if (modeProperty.equals("standalone")) {
				logger.log(Level.INFO, "Create Service object");
				getTestURLs();
				service = (HelloService) JAXWS_Util.getService(wsdlurl, SERVICE_QNAME, SERVICE_CLASS);
				getPortsStandalone();
			} else {
				getTestURLs();
				logger.log(Level.INFO, "WebServiceRef is not set in Client (get it from specific vehicle)");
				service = (HelloService) getSharedObject();
				getPortsJavaEE();
			}
			HelloRequest req = new HelloRequest();
			req.setArgument("foo");
			logger.log(Level.INFO, "invoking hello through stub");
			port.hello(req);
			Source reqMsg = JAXWS_Util.makeSource(helloReq, "StreamSource");
			logger.log(Level.INFO, "invoking hello through dispatch");
			dispatchSrc.invoke(reqMsg);
		} catch (Exception e) {
			TestUtil.printStackTrace(e);
			throw new Exception("setup failed:", e);
		}
		logger.log(Level.INFO, "setup ok");
	}

	@AfterEach
	public void cleanup() throws Exception {
		logger.log(Level.INFO, "cleanup ok");
	}

	/*
	 * @testName: getBindingForDispatchObjTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:4;
	 *
	 * @test_Strategy: Get the Binding for this binding provider.
	 */
	@Test
	public void getBindingForDispatchObjTest() throws Exception {
		TestUtil.logTrace("getBindingForDispatchObjTest");
		boolean pass = true;
		logger.log(Level.INFO, "Calling BindingProvider.getBinding() for Dispatch object");
		binding = dispatchSrc.getBinding();
		logger.log(Level.INFO, "Binding object=" + binding);
		if (binding == null) {
			TestUtil.logErr("getBinding() returned null");
			pass = false;
		} else
			logger.log(Level.INFO, "getBinding() returned Binding object: " + binding);
		if (!pass)
			throw new Exception("getBindingForDispatchObjTest failed");
	}

	/*
	 * @testName: getRequestContextForDispatchObjTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:5;
	 *
	 * @test_Strategy: Get the context that is used to initialize the message
	 * context for request messages.
	 */
	@Test
	public void getRequestContextForDispatchObjTest() throws Exception {
		TestUtil.logTrace("getRequestContextForDispatchObjTest");
		boolean pass = true;
		logger.log(Level.INFO, "Calling BindingProvider.getRequestContext() for Dispatch object");
		java.util.Map<String, Object> requestContext = dispatchSrc.getRequestContext();
		if (requestContext == null) {
			TestUtil.logErr("getRequestContext() returned null");
			pass = false;
		} else {
			logger.log(Level.INFO, "getRequestContext() returned java.util.Map<String,Object> object");
			logger.log(Level.INFO, "map size=" + requestContext.size());
			java.util.Iterator iterator = requestContext.keySet().iterator();
			StringBuffer names = new StringBuffer();
			while (iterator.hasNext()) {
				if (names.length() > 0)
					names.append("\n" + iterator.next());
				else
					names.append("" + iterator.next());
			}
			if (names.length() > 0)
				logger.log(Level.INFO, "Request property names are\n" + names.toString());
			else
				logger.log(Level.INFO, "There are no request properties set");
		}
		if (!pass)
			throw new Exception("getRequestContextForDispatchObjTest failed");
	}

	/*
	 * @testName: getResponseContextForDispatchObjTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:6;
	 *
	 * @test_Strategy: Get the context that resulted from processing a response
	 * message.
	 */
	@Test
	public void getResponseContextForDispatchObjTest() throws Exception {
		TestUtil.logTrace("getResponseContextForDispatchObjTest");
		boolean pass = true;
		logger.log(Level.INFO, "Calling BindingProvider.getResponseContext() for Dispatch object");
		java.util.Map<String, Object> responseContext = dispatchSrc.getResponseContext();
		if (responseContext == null) {
			TestUtil.logErr("getResponseContext() returned null");
			pass = false;
		} else {
			logger.log(Level.INFO, "getResponseContext() returned java.util.Map<String,Object> object");
			logger.log(Level.INFO, "map size=" + responseContext.size());
			java.util.Iterator iterator = responseContext.keySet().iterator();
			StringBuffer names = new StringBuffer();
			while (iterator.hasNext()) {
				if (names.length() > 0)
					names.append("\n" + iterator.next());
				else
					names.append("" + iterator.next());
			}
			if (names.length() > 0)
				logger.log(Level.INFO, "Response property names are\n" + names.toString());
			else
				logger.log(Level.INFO, "There are no response properties set");
		}
		if (!pass)
			throw new Exception("getResponseContextForDispatchObjTest failed");
	}

	/*
	 * @testName: getBindingForStubObjTest
	 *
	 * @assertion_ids: JAXWS:SPEC:4009; JAXWS:SPEC:4010; JAXWS:JAVADOC:4;
	 *
	 * @test_Strategy: Get the Binding for this binding provider.
	 */
	@Test
	public void getBindingForStubObjTest() throws Exception {
		TestUtil.logTrace("getBindingForStubObjTest");
		boolean pass = true;
		logger.log(Level.INFO, "Calling BindingProvider.getBinding() for Stub object");
		binding = bpStub.getBinding();
		logger.log(Level.INFO, "Binding object=" + binding);
		if (binding == null) {
			TestUtil.logErr("getBinding() returned null");
			pass = false;
		} else
			logger.log(Level.INFO, "getBinding() returned Binding object: " + binding);
		if (!pass)
			throw new Exception("getBindingForStubObjTest failed");
	}

	/*
	 * @testName: getRequestContextForStubObjTest
	 *
	 * @assertion_ids: JAXWS:SPEC:4009; JAXWS:SPEC:4010; JAXWS:JAVADOC:5;
	 *
	 * @test_Strategy: Get the context that is used to initialize the message
	 * context for request messages.
	 */
	@Test
	public void getRequestContextForStubObjTest() throws Exception {
		TestUtil.logTrace("getRequestContextForStubObjTest");
		boolean pass = true;
		TestUtil.logMsg("Calling BindingProvider.getRequestContext() for Stub object");
		java.util.Map<String, Object> requestContext = bpStub.getRequestContext();
		if (requestContext == null) {
			TestUtil.logErr("getRequestContext() returned null");
			pass = false;
		} else {
			logger.log(Level.INFO, "getRequestContext() returned java.util.Map<String,Object> object");
			logger.log(Level.INFO, "map size=" + requestContext.size());
			java.util.Iterator iterator = requestContext.keySet().iterator();
			StringBuffer names = new StringBuffer();
			while (iterator.hasNext()) {
				if (names.length() > 0)
					names.append("\n" + iterator.next());
				else
					names.append("" + iterator.next());
			}
			if (names.length() > 0)
				logger.log(Level.INFO, "Request property names are\n" + names.toString());
			else
				logger.log(Level.INFO, "There are no request properties set");
		}
		if (!pass)
			throw new Exception("getRequestContextForStubObjTest failed");
	}

	/*
	 * @testName: getResponseContextForStubObjTest
	 *
	 * @assertion_ids: JAXWS:SPEC:4009; JAXWS:SPEC:4010; JAXWS:JAVADOC:6;
	 *
	 * @test_Strategy: Get the context that resulted from processing a response
	 * message.
	 */
	@Test
	public void getResponseContextForStubObjTest() throws Exception {
		TestUtil.logTrace("getResponseContextForStubObjTest");
		boolean pass = true;
		TestUtil.logMsg("Calling BindingProvider.getResponseContext() for Stub object");
		java.util.Map<String, Object> responseContext = bpStub.getResponseContext();
		if (responseContext == null) {
			TestUtil.logErr("getResponseContext() returned null");
			pass = false;
		} else {
			logger.log(Level.INFO, "getResponseContext() returned java.util.Map<String,Object> object");
			logger.log(Level.INFO, "map size=" + responseContext.size());
			java.util.Iterator iterator = responseContext.keySet().iterator();
			StringBuffer names = new StringBuffer();
			while (iterator.hasNext()) {
				if (names.length() > 0)
					names.append("\n" + iterator.next());
				else
					names.append("" + iterator.next());
			}
			if (names.length() > 0)
				logger.log(Level.INFO, "Response property names are\n" + names.toString());
			else
				logger.log(Level.INFO, "There are no response properties set");
		}
		if (!pass)
			throw new Exception("getResponseContextForStubObjTest failed");
	}

	/*
	 * @testName: setStandardPropertiesTest
	 *
	 * @assertion_ids: JAXWS:SPEC:4005;
	 *
	 * @test_Strategy: Get the context that is used to initialize the message
	 * context for request messages and set all the standard properties.
	 */
	@Test
	public void setStandardPropertiesTest() throws Exception {
		TestUtil.logTrace("setStandardPropertiesTest");
		boolean pass = true;
		TestUtil.logMsg("Calling BindingProvider.getRequestContext() for Stub object");
		java.util.Map<String, Object> requestContext = bpStub.getRequestContext();
		if (requestContext == null) {
			TestUtil.logErr("getRequestContext() returned null");
			pass = false;
		} else {
			logger.log(Level.INFO, "getRequestContext() returned java.util.Map<String,Object> object");
			logger.log(Level.INFO, "Verify setting of all standard properties");
			requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "endpoint-address");
			requestContext.put(BindingProvider.PASSWORD_PROPERTY, "password");
			requestContext.put(BindingProvider.USERNAME_PROPERTY, "username");
			requestContext.put(BindingProvider.SOAPACTION_URI_PROPERTY, "myuri");
			requestContext.put(BindingProvider.SOAPACTION_USE_PROPERTY, new Boolean("false"));
			requestContext.put(BindingProvider.SESSION_MAINTAIN_PROPERTY, new Boolean("false"));
			logger.log(Level.INFO, "map size=" + requestContext.size());
			java.util.Iterator iterator = requestContext.keySet().iterator();
			StringBuffer names = new StringBuffer();
			while (iterator.hasNext()) {
				if (names.length() > 0)
					names.append("\n" + iterator.next());
				else
					names.append("" + iterator.next());
			}
			if (names.length() > 0)
				logger.log(Level.INFO, "Request property names are\n" + names.toString());
			else
				logger.log(Level.INFO, "There are no request properties set");
		}
		if (!pass)
			throw new Exception("setStandardPropertiesTest failed");
	}

	/*
	 * @testName: setNonStandardPropertiesTest
	 *
	 * @assertion_ids: JAXWS:SPEC:4007;
	 *
	 * @test_Strategy: Get the context that is used to initialize the message
	 * context for request messages and set all the standard properties.
	 */
	@Test
	public void setNonStandardPropertiesTest() throws Exception {
		TestUtil.logTrace("setNonStandardPropertiesTest");
		boolean pass = true;
		TestUtil.logMsg("Calling BindingProvider.getRequestContext() for Stub object");
		java.util.Map<String, Object> requestContext = bpStub.getRequestContext();
		if (requestContext == null) {
			TestUtil.logErr("getRequestContext() returned null");
			pass = false;
		} else {
			logger.log(Level.INFO, "getRequestContext() returned java.util.Map<String,Object> object");
			logger.log(Level.INFO, "Verify setting of a non standard properties");
			requestContext.put("foobar.property", "foobar");
			logger.log(Level.INFO, "map size=" + requestContext.size());
			java.util.Iterator iterator = requestContext.keySet().iterator();
			StringBuffer names = new StringBuffer();
			while (iterator.hasNext()) {
				if (names.length() > 0)
					names.append("\n" + iterator.next());
				else
					names.append("" + iterator.next());
			}
			if (names.length() > 0)
				logger.log(Level.INFO, "Request property names are\n" + names.toString());
			else
				logger.log(Level.INFO, "There are no request properties set");
		}
		if (!pass)
			throw new Exception("setNonStandardPropertiesTest failed");
	}

	/*
	 * @testName: getEndpointReferenceForDispatchObjTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:186; JAXWS:SPEC:5023; JAXWS:SPEC:4022;
	 * WSAMD:SPEC:2000.2; WSAMD:SPEC:2001; WSAMD:SPEC:2001.1; WSAMD:SPEC:2001.2;
	 * WSAMD:SPEC:2001.3; WSAMD:SPEC:2002; WSAMD:SPEC:2002.1; WSAMD:SPEC:2002.2;
	 * WSAMD:SPEC:2002.3; WSAMD:SPEC:2002.4; JAXWS:SPEC:4033;
	 *
	 * @test_Strategy: Get the EndpointReference for this binding provider. Validate
	 * the EndpointReference (EPR) WSDL MetaData.
	 */
	@Test
	public void getEndpointReferenceForDispatchObjTest() throws Exception {
		TestUtil.logTrace("getEndpointReferenceForDispatchObjTest");
		boolean pass = true;
		logger.log(Level.INFO, "Calling BindingProvider.getEndpointReference() for Dispatch object");
		epr = dispatchSrc.getEndpointReference();
		logger.log(Level.INFO, "EndpointReference object=" + epr);
		if (epr == null) {
			TestUtil.logErr("getEndpointReference() returned null");
			pass = false;
		} else {
			logger.log(Level.INFO, "getEndpointReference() returned EndpointReference object: " + epr);
			pass = EprUtil.validateEPR(epr, url, SERVICE_QNAME, PORT_QNAME, PORT_TYPE_QNAME, Boolean.FALSE);
		}
		if (!pass)
			throw new Exception("getEndpointReferenceForDispatchObjTest failed");
	}

	/*
	 * @testName: getEndpointReferenceForStubObjTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:186; JAXWS:SPEC:5023; JAXWS:SPEC:4022;
	 * JAXWS:SPEC:4023; WSAMD:SPEC:2000.2; WSAMD:SPEC:2001; WSAMD:SPEC:2001.1;
	 * WSAMD:SPEC:2001.2; WSAMD:SPEC:2001.3; WSAMD:SPEC:2002; WSAMD:SPEC:2002.1;
	 * WSAMD:SPEC:2002.2; WSAMD:SPEC:2002.3; WSAMD:SPEC:2002.4; JAXWS:SPEC:4033;
	 *
	 * @test_Strategy: Get the EndpointReference for this binding provider. Validate
	 * the EndpointReference (EPR) WSDL MetaData.
	 */
	@Test
	public void getEndpointReferenceForStubObjTest() throws Exception {
		TestUtil.logTrace("getEndpointReferenceForStubObjTest");
		boolean pass = true;
		logger.log(Level.INFO, "Calling BindingProvider.getEndpointReference() for Stub object");
		epr = bpStub.getEndpointReference();
		logger.log(Level.INFO, "EndpointReference object=" + epr);
		if (epr == null) {
			TestUtil.logErr("getEndpointReference() returned null");
			pass = false;
		} else {
			logger.log(Level.INFO, "getEndpointReference() returned EndpointReference object: " + epr);
			pass = EprUtil.validateEPR(epr, url, SERVICE_QNAME, PORT_QNAME, PORT_TYPE_QNAME, Boolean.FALSE);
		}
		if (epr instanceof W3CEndpointReference)
			logger.log(Level.INFO, "epr instanceof W3CEndpointReference");
		else {
			TestUtil.logErr("epr not instanceof W3CEndpointReference");
			pass = false;
		}
		if (!pass)
			throw new Exception("getEndpointReferenceForStubObjTest failed");
	}

	/*
	 * @testName: getEndpointReference2ForDispatchObjTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:187; JAXWS:SPEC:4022; JAXWS:SPEC:4023;
	 * WSAMD:SPEC:2000.2; WSAMD:SPEC:2001; WSAMD:SPEC:2001.1; WSAMD:SPEC:2001.2;
	 * WSAMD:SPEC:2001.3; WSAMD:SPEC:2002; WSAMD:SPEC:2002.1; WSAMD:SPEC:2002.2;
	 * WSAMD:SPEC:2002.3; WSAMD:SPEC:2002.4; JAXWS:SPEC:4033;
	 *
	 * @test_Strategy: Get the EndpointReference for this binding provider. Validate
	 * the EndpointReference (EPR) WSDL MetaData.
	 */
	@Test
	public void getEndpointReference2ForDispatchObjTest() throws Exception {
		TestUtil.logTrace("getEndpointReference2ForDispatchObjTest");
		boolean pass = true;
		logger.log(Level.INFO, "Calling BindingProvider.getEndpointReference(Class) for Dispatch object");
		epr = dispatchSrc.getEndpointReference(W3CEndpointReference.class);
		logger.log(Level.INFO, "EndpointReference object=" + epr);
		if (epr == null) {
			TestUtil.logErr("getEndpointReference() returned null");
			pass = false;
		} else {
			logger.log(Level.INFO, "getEndpointReference() returned EndpointReference object: " + epr);
			pass = EprUtil.validateEPR(epr, url, SERVICE_QNAME, PORT_QNAME, PORT_TYPE_QNAME, Boolean.FALSE);
		}
		if (epr instanceof W3CEndpointReference)
			logger.log(Level.INFO, "epr instanceof W3CEndpointReference");
		else {
			TestUtil.logErr("epr not instanceof W3CEndpointReference");
			pass = false;
		}
		if (!pass)
			throw new Exception("getEndpointReference2ForDispatchObjTest failed");
	}

	/*
	 * @testName: getEndpointReference2ForStubObjTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:187; JAXWS:SPEC:4022; JAXWS:SPEC:4023;
	 * WSAMD:SPEC:2000.2; WSAMD:SPEC:2001; WSAMD:SPEC:2001.1; WSAMD:SPEC:2001.2;
	 * WSAMD:SPEC:2001.3; WSAMD:SPEC:2002; WSAMD:SPEC:2002.1; WSAMD:SPEC:2002.2;
	 * WSAMD:SPEC:2002.3; WSAMD:SPEC:2002.4; JAXWS:SPEC:4033;
	 *
	 * @test_Strategy: Get the EndpointReference for this binding provider. Validate
	 * the EndpointReference (EPR) WSDL MetaData.
	 */
	@Test
	public void getEndpointReference2ForStubObjTest() throws Exception {
		TestUtil.logTrace("getEndpointReference2ForStubObjTest");
		boolean pass = true;
		logger.log(Level.INFO, "Calling BindingProvider.getEndpointReference(Class) for Stub object");
		epr = bpStub.getEndpointReference(W3CEndpointReference.class);
		logger.log(Level.INFO, "EndpointReference object=" + epr);
		if (epr == null) {
			TestUtil.logErr("getEndpointReference() returned null");
			pass = false;
		} else {
			logger.log(Level.INFO, "getEndpointReference() returned EndpointReference object: " + epr);
			pass = EprUtil.validateEPR(epr, url, SERVICE_QNAME, PORT_QNAME, PORT_TYPE_QNAME, Boolean.FALSE);
		}
		if (epr instanceof W3CEndpointReference)
			logger.log(Level.INFO, "epr instanceof W3CEndpointReference");
		else {
			TestUtil.logErr("epr not instanceof W3CEndpointReference");
			pass = false;
		}
		if (!pass)
			throw new Exception("getEndpointReference2ForStubObjTest failed");
	}
}
