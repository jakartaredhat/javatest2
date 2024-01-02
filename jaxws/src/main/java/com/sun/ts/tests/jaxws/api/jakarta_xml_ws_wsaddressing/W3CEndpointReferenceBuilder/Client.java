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
 * $Id: Client.java 51088 2003-12-03 17:00:09Z af70133 $
 */

package com.sun.ts.tests.jaxws.api.jakarta_xml_ws_wsaddressing.W3CEndpointReferenceBuilder;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import com.sun.ts.lib.porting.TSURL;
import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;
import com.sun.ts.tests.jaxws.common.XMLUtils;
import com.sun.ts.tests.jaxws.sharedclients.doclithelloclient.Hello;
import com.sun.ts.tests.jaxws.sharedclients.doclithelloclient.HelloService;
import com.sun.ts.tests.jaxws.wsa.common.EprUtil;

import jakarta.xml.ws.wsaddressing.W3CEndpointReference;
import jakarta.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;

import com.sun.ts.tests.jaxws.common.BaseClient;

public class Client extends BaseClient {

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	private static final String NAMESPACEURI = "http://helloservice.org/wsdl";

	private static final String SERVICE_NAME = "HelloService";

	private static final String PORT_NAME = "HelloPort";

	private static final String PORT_TYPE_NAME = "Hello";

	private static final QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private static final QName PORT_QNAME = new QName(NAMESPACEURI, PORT_NAME);

	private static final QName PORT_TYPE_QNAME = new QName(NAMESPACEURI, PORT_TYPE_NAME);

	private static final QName MyExtensionAttr = new QName("http://extensions.org/ext", "MyExtensionAttr");

	private static final String MyExtensionAttrValue = "Hello";

	private static final Class SERVICE_CLASS = com.sun.ts.tests.jaxws.sharedclients.doclithelloclient.HelloService.class;

	private static String xmlRefParam1 = "<myns1:MyParam1 wsa:IsReferenceParameter='true' xmlns:myns1=\"http://helloservice.org/myparam1\" xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">Hello</myns1:MyParam1>";

	private static String xmlRefParam2 = "<myns2:MyParam2 wsa:IsReferenceParameter='true' xmlns:myns2=\"http://helloservice.org/myparam2\" xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">There</myns2:MyParam2>";

	private static String xmlMyExtensionElement = "<myext:MyExtensionElement xmlns:myext=\"http://extension.org/ext\">MyExtensionElementValue</myext:MyExtensionElement>";

	private static String xmlInterfaceName = "<wsam:InterfaceName xmlns:wsam=\"http://www.w3.org/2007/05/addressing/metadata\" xmlns:wsns=\"http://helloservice.org/wsdl\">wsns:Hello</wsam:InterfaceName>";

	private static String xmlServiceName = "<wsam:ServiceName xmlns:wsam=\"http://www.w3.org/2007/05/addressing/metadata\" xmlns:wsa=\"http://www.w3.org/2005/08/addressing\" xmlns:wsns=\"http://helloservice.org/wsdl\" EndpointName=\"HelloPort\">wsns:HelloService</wsam:ServiceName>";

	private TSURL ctsurl = new TSURL();

	private String hostname = HOSTNAME;

	private int portnum = PORTNUM;

	private static final String ENDPOINT_URL = "dlhelloservice.endpoint.1";

	private static final String WSDLLOC_URL = "dlhelloservice.wsdlloc.1";

	private String url = null;

	private URL wsdlurl = null;

	private Hello port = null;

	W3CEndpointReferenceBuilder builder = null;

	static HelloService service = null;

	private void getPorts() throws Exception {
		logger.log(Level.INFO, "Get port  = " + PORT_NAME);
		port = (Hello) service.getPort(Hello.class);
		logger.log(Level.INFO, "port=" + port);
	}

	protected void getPortStandalone() throws Exception {
		getPorts();
		JAXWS_Util.setTargetEndpointAddress(port, url);
	}

	protected void getPortJavaEE() throws Exception {
		logger.log(Level.INFO, "Obtaining service via WebServiceRef annotation");
		logger.log(Level.INFO, "service=" + service);
		getPorts();
		logger.log(Level.INFO, "Get Target Endpoint Address for port=" + port);
		String url = JAXWS_Util.getTargetEndpointAddress(port);
		logger.log(Level.INFO, "Target Endpoint Address=" + url);
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
		super.setup();
		try {

			if (modeProperty.equals("standalone")) {
				getTestURLs();
				logger.log(Level.INFO, "Create Service object");
				service = (HelloService) JAXWS_Util.getService(wsdlurl, SERVICE_QNAME, SERVICE_CLASS);
				getPortStandalone();
			} else {
				getTestURLs();
				logger.log(Level.INFO, "WebServiceRef is not set in Client (get it from specific vehicle)");
				service = (HelloService) getSharedObject();
				getPortJavaEE();
			}
		} catch (Exception e) {
			TestUtil.printStackTrace(e);
			throw new Exception("setup failed:", e);
		}

		builder = new W3CEndpointReferenceBuilder();
		logger.log(Level.INFO, "setup ok");
	}

	@AfterEach
	public void cleanup() throws Exception {
		logger.log(Level.INFO, "cleanup ok");
	}

	/*
	 * @testName: W3CEndpointReferenceBuilderConstructorTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:204;
	 *
	 * @test_Strategy: Create instance via W3CEndpointReferenceBuilder()
	 * constructor. Verify W3CEndpointReferenceBuilder object created successfully.
	 */
	@Test
	public void W3CEndpointReferenceBuilderConstructorTest() throws Exception {
		TestUtil.logTrace("W3CEndpointReferenceBuilderConstructorTest");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Create instance via W3CEndpointReferenceBuilder() ...");
			W3CEndpointReferenceBuilder b = new W3CEndpointReferenceBuilder();
			if (b != null) {
				TestUtil.logMsg("W3CEndpointReferenceBuilder object created successfully");
			} else {
				TestUtil.logErr("W3CEndpointReferenceBuilder object not created");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("W3CEndpointReferenceBuilderConstructorTest failed", e);
		}

		if (!pass)
			throw new Exception("W3CEndpointReferenceBuilderConstructorTest failed");
	}

	/*
	 * @testName: addressNULLTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:198;
	 *
	 * @test_Strategy: Call address() api.
	 * 
	 */
	@Test
	public void addressNULLTest() throws Exception {
		TestUtil.logTrace("addressNULLTest");
		boolean pass = true;
		try {
			builder = builder.address(null);
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("addressNULLTest failed", e);
		}

		if (!pass)
			throw new Exception("addressNULLTest failed");
	}

	/*
	 * @testName: addressNonNULLTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:198;
	 *
	 * @test_Strategy: Call address() api.
	 * 
	 */
	@Test
	public void addressNonNULLTest() throws Exception {
		TestUtil.logTrace("addressNonNULLTest");
		boolean pass = true;
		try {
			builder = builder.address(url);
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("addressNonNULLTest failed", e);
		}

		if (!pass)
			throw new Exception("addressNonNULLTest failed");
	}

	/*
	 * @testName: serviceNameNULLTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:203;
	 *
	 * @test_Strategy: Call serviceName() api.
	 * 
	 */
	@Test
	public void serviceNameNULLTest() throws Exception {
		TestUtil.logTrace("serviceNameNULLTest");
		boolean pass = true;
		try {
			builder = builder.serviceName(null);
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("serviceNameNULLTest failed", e);
		}

		if (!pass)
			throw new Exception("serviceNameNULLTest failed");
	}

	/*
	 * @testName: serviceNameNonNULLTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:203;
	 *
	 * @test_Strategy: Call serviceName() api.
	 * 
	 */
	@Test
	public void serviceNameNonNULLTest() throws Exception {
		TestUtil.logTrace("serviceNameNonNULLTest");
		boolean pass = true;
		try {
			builder = builder.serviceName(SERVICE_QNAME);
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("serviceNameNonNULLTest failed", e);
		}

		if (!pass)
			throw new Exception("serviceNameNonNULLTest failed");
	}

	/*
	 * @testName: interfaceNameNULLTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:280;
	 *
	 * @test_Strategy: Call interfaceName() api.
	 * 
	 */
	@Test
	public void interfaceNameNULLTest() throws Exception {
		TestUtil.logTrace("interfaceNameNULLTest");
		boolean pass = true;
		try {
			builder = builder.interfaceName(null);
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("interfaceNameNULLTest failed", e);
		}

		if (!pass)
			throw new Exception("interfaceNameNULLTest failed");
	}

	/*
	 * @testName: interfaceNameNonNULLTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:280;
	 *
	 * @test_Strategy: Call interfaceName() api.
	 * 
	 */
	@Test
	public void interfaceNameNonNULLTest() throws Exception {
		TestUtil.logTrace("interfaceNameNonNULLTest");
		boolean pass = true;
		try {
			builder = builder.interfaceName(PORT_TYPE_QNAME);
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("interfaceNameNonNULLTest failed", e);
		}

		if (!pass)
			throw new Exception("interfaceNameNonNULLTest failed");
	}

	/*
	 * @testName: endpointNameNULLTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:200;
	 *
	 * @test_Strategy: Call endpointName() api.
	 * 
	 */
	@Test
	public void endpointNameNULLTest() throws Exception {
		TestUtil.logTrace("endpointNameNULLTest");
		boolean pass = true;
		try {
			builder = builder.serviceName(SERVICE_QNAME);
			builder = builder.endpointName(null);
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("endpointNameNULLTest failed", e);
		}

		if (!pass)
			throw new Exception("endpointNameNULLTest failed");
	}

	/*
	 * @testName: endpointNameNonNULLTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:200;
	 *
	 * @test_Strategy: Call endpointName() api.
	 * 
	 */
	@Test
	public void endpointNameNonNULLTest() throws Exception {
		TestUtil.logTrace("endpointNameNonNULLTest");
		boolean pass = true;
		try {
			builder = builder.serviceName(SERVICE_QNAME);
			builder = builder.endpointName(PORT_QNAME);
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("endpointNameNonNULLTest failed", e);
		}

		if (!pass)
			throw new Exception("endpointNameNonNULLTest failed");
	}

	/*
	 * @testName: endpointNameIllegalStateExceptionTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:200;
	 *
	 * @test_Strategy: Call endpointName() api. Test for IllegalStateException.
	 * 
	 */
	@Test
	public void endpointNameIllegalStateExceptionTest() throws Exception {
		TestUtil.logTrace("endpointNameIllegalStateExceptionTest");
		boolean pass = true;
		try {
			builder = builder.endpointName(PORT_QNAME);
			TestUtil.logErr("Did not throw expected IllegalStateException");
			pass = false;
		} catch (IllegalStateException e) {
			logger.log(Level.INFO, "Caught expected IllegalStateException");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("endpointNameIllegalStateExceptionTest failed", e);
		}

		if (!pass)
			throw new Exception("endpointNameIllegalStateExceptionTest failed");
	}

	/*
	 * @testName: wsdlDocumentLocationNULLTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:205;
	 *
	 * @test_Strategy: Call wsdlDocumentLocation() api.
	 * 
	 */
	@Test
	public void wsdlDocumentLocationNULLTest() throws Exception {
		TestUtil.logTrace("wsdlDocumentLocationNULLTest");
		boolean pass = true;
		try {
			builder = builder.wsdlDocumentLocation(null);
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("wsdlDocumentLocationNULLTest failed", e);
		}

		if (!pass)
			throw new Exception("wsdlDocumentLocationNULLTest failed");
	}

	/*
	 * @testName: wsdlDocumentLocationNonNULLTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:205;
	 *
	 * @test_Strategy: Call wsdlDocumentLocation() api.
	 * 
	 */
	@Test
	public void wsdlDocumentLocationNonNULLTest() throws Exception {
		TestUtil.logTrace("wsdlDocumentLocationNonNULLTest");
		boolean pass = true;
		try {
			builder = builder.wsdlDocumentLocation(wsdlurl.toString());
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("wsdlDocumentLocationNonNULLTest failed", e);
		}

		if (!pass)
			throw new Exception("wsdlDocumentLocationNonNULLTest failed");
	}

	/*
	 * @testName: metadataNULLTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:201;
	 *
	 * @test_Strategy: Call metadata() api. Test for IllegalArgumentException.
	 * 
	 */
	@Test
	public void metadataNULLTest() throws Exception {
		TestUtil.logTrace("metadataNULLTest");
		boolean pass = true;
		try {
			builder = builder.metadata(null);
			TestUtil.logErr("Passing NULL metadata should have thrown exception");
			pass = false;
		} catch (IllegalArgumentException e) {
			logger.log(Level.INFO, "Caught expected IllegalArgumentException");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("metadataNULLTest failed", e);
		}

		if (!pass)
			throw new Exception("metadataNULLTest failed");
	}

	/*
	 * @testName: metadataNonNULLTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:201;
	 *
	 * @test_Strategy: Call metadata() api.
	 * 
	 */
	@Test
	public void metadataNonNULLTest() throws Exception {
		TestUtil.logTrace("metadataNonNULLTest");
		boolean pass = true;
		try {
			DOMSource domsrc = (DOMSource) JAXWS_Util.makeSource(xmlServiceName, "DOMSource");
			Document document = (Document) domsrc.getNode();
			XMLUtils.xmlDumpDOMNodes(document, false);
			builder = builder.metadata(document.getDocumentElement());
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("metadataNonNULLTest failed", e);
		}

		if (!pass)
			throw new Exception("metadataNonNULLTest failed");
	}

	/*
	 * @testName: attributeNULLTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:278;
	 *
	 * @test_Strategy: Call attribute() api. Test for IllegalArgumentException.
	 * 
	 */
	@Test
	public void attributeNULLTest() throws Exception {
		TestUtil.logTrace("attributeNULLTest");
		boolean pass = true;
		try {
			builder = builder.attribute(null, null);
			TestUtil.logErr("Passing NULL should have thrown exception");
			pass = false;
		} catch (IllegalArgumentException e) {
			logger.log(Level.INFO, "Caught expected IllegalArgumentException");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("attributeNULLTest failed", e);
		}

		if (!pass)
			throw new Exception("attributeNULLTest failed");
	}

	/*
	 * @testName: attributeNonNULLTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:278;
	 *
	 * @test_Strategy: Call attribute() api.
	 * 
	 */
	@Test
	public void attributeNonNULLTest() throws Exception {
		TestUtil.logTrace("attributeNonNULLTest");
		boolean pass = true;
		try {
			builder = builder.attribute(MyExtensionAttr, MyExtensionAttrValue);
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("attributeNonNULLTest failed", e);
		}

		if (!pass)
			throw new Exception("attributeNonNULLTest failed");
	}

	/*
	 * @testName: elementNULLTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:279;
	 *
	 * @test_Strategy: Call element() api. Test for IllegalArgumentException.
	 * 
	 */
	@Test
	public void elementNULLTest() throws Exception {
		TestUtil.logTrace("elementNULLTest");
		boolean pass = true;
		try {
			builder = builder.element(null);
			TestUtil.logErr("Passing NULL should have thrown exception");
			pass = false;
		} catch (IllegalArgumentException e) {
			logger.log(Level.INFO, "Caught expected IllegalArgumentException");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("elementNULLTest failed", e);
		}

		if (!pass)
			throw new Exception("elementNULLTest failed");
	}

	/*
	 * @testName: elementNonNULLTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:279;
	 *
	 * @test_Strategy: Call element() api.
	 * 
	 */
	@Test
	public void elementNonNULLTest() throws Exception {
		TestUtil.logTrace("elementNonNULLTest");
		boolean pass = true;
		try {
			DOMSource domsrc = (DOMSource) JAXWS_Util.makeSource(xmlMyExtensionElement, "DOMSource");
			Document document = (Document) domsrc.getNode();
			builder = builder.element(document.getDocumentElement());
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("elementNonNULLTest failed", e);
		}

		if (!pass)
			throw new Exception("elementNonNULLTest failed");
	}

	/*
	 * @testName: referenceParameterNULLTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:202;
	 *
	 * @test_Strategy: Call referenceParameter() api. Test for
	 * IllegalArgumentException.
	 * 
	 */
	@Test
	public void referenceParameterNULLTest() throws Exception {
		TestUtil.logTrace("referenceParameterNULLTest");
		boolean pass = true;
		try {
			builder = builder.referenceParameter(null);
			TestUtil.logErr("Passing NULL referenceParameter should have thrown exception");
			pass = false;
		} catch (IllegalArgumentException e) {
			logger.log(Level.INFO, "Caught expected IllegalArgumentException");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("referenceParameterNULLTest failed", e);
		}

		if (!pass)
			throw new Exception("referenceParameterNULLTest failed");
	}

	/*
	 * @testName: referenceParameterNonNULLTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:202;
	 *
	 * @test_Strategy: Call referenceParameter() api.
	 * 
	 */
	@Test
	public void referenceParameterNonNULLTest() throws Exception {
		TestUtil.logTrace("referenceParameterNonNULLTest");
		boolean pass = true;
		try {
			DOMSource domsrc = (DOMSource) JAXWS_Util.makeSource(xmlRefParam1, "DOMSource");
			Document document = (Document) domsrc.getNode();
			builder = builder.referenceParameter(document.getDocumentElement());
			domsrc = (DOMSource) JAXWS_Util.makeSource(xmlRefParam2, "DOMSource");
			document = (Document) domsrc.getNode();
			builder = builder.referenceParameter(document.getDocumentElement());
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("referenceParameterNonNULLTest failed", e);
		}

		if (!pass)
			throw new Exception("referenceParameterNonNULLTest failed");
	}

	/*
	 * @testName: buildTest1
	 *
	 * @assertion_ids: JAXWS:JAVADOC:206;
	 *
	 * @test_Strategy: Call build() api. Use calls to address() and metadata() to
	 * build the EndpointReference.
	 * 
	 */
	@Test
	public void buildTest1() throws Exception {
		TestUtil.logTrace("buildTest1");
		boolean pass = true;
		try {
			builder = builder.address(url);
			DOMSource domsrc = (DOMSource) JAXWS_Util.makeSource(xmlInterfaceName, "DOMSource");
			Document document = (Document) domsrc.getNode();
			builder = builder.metadata(document.getDocumentElement());
			domsrc = (DOMSource) JAXWS_Util.makeSource(xmlServiceName, "DOMSource");
			document = (Document) domsrc.getNode();
			builder = builder.metadata(document.getDocumentElement());
			W3CEndpointReference epr = builder.build();
			DOMResult dr = new DOMResult();
			epr.writeTo(dr);
			XMLUtils.xmlDumpDOMNodes(dr.getNode(), false);
			if (!EprUtil.validateEPR(epr, url, SERVICE_QNAME, PORT_QNAME, PORT_TYPE_QNAME, Boolean.TRUE))
				pass = false;
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("buildTest1 failed", e);
		}

		if (!pass)
			throw new Exception("buildTest1 failed");
	}

	/*
	 * @testName: buildTest2
	 *
	 * @assertion_ids: JAXWS:JAVADOC:206;
	 *
	 * @test_Strategy: Call build() api. Use calls to address(), metadata(),
	 * element() and attribute() and to build the EndpointReference.
	 * 
	 */
	@Test
	public void buildTest2() throws Exception {
		TestUtil.logTrace("buildTest2");
		boolean pass = true;
		try {
			builder = builder.address(url);
			builder = builder.attribute(MyExtensionAttr, MyExtensionAttrValue);
			DOMSource domsrc = (DOMSource) JAXWS_Util.makeSource(xmlMyExtensionElement, "DOMSource");
			Document document = (Document) domsrc.getNode();
			builder = builder.element(document.getDocumentElement());
			domsrc = (DOMSource) JAXWS_Util.makeSource(xmlInterfaceName, "DOMSource");
			document = (Document) domsrc.getNode();
			builder = builder.metadata(document.getDocumentElement());
			domsrc = (DOMSource) JAXWS_Util.makeSource(xmlServiceName, "DOMSource");
			document = (Document) domsrc.getNode();
			builder = builder.metadata(document.getDocumentElement());
			W3CEndpointReference epr = builder.build();
			DOMResult dr = new DOMResult();
			epr.writeTo(dr);
			XMLUtils.xmlDumpDOMNodes(dr.getNode(), false);
			if (!EprUtil.validateEPR(epr, url, SERVICE_QNAME, PORT_QNAME, PORT_TYPE_QNAME, Boolean.TRUE))
				pass = false;
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("buildTest2 failed", e);
		}

		if (!pass)
			throw new Exception("buildTest2 failed");
	}

	/*
	 * @testName: buildTest3
	 *
	 * @assertion_ids: JAXWS:JAVADOC:206;
	 *
	 * @test_Strategy: Call build() api. Call all the api's to build an
	 * EndpointReference from scratch.
	 * 
	 */
	@Test
	public void buildTest3() throws Exception {
		TestUtil.logTrace("buildTest3");
		boolean pass = true;
		try {
			builder = builder.address(url);
			DOMSource domsrc = (DOMSource) JAXWS_Util.makeSource(xmlInterfaceName, "DOMSource");
			Document document = (Document) domsrc.getNode();
			builder = builder.metadata(document.getDocumentElement());
			builder = builder.serviceName(SERVICE_QNAME);
			builder = builder.endpointName(PORT_QNAME);
			builder = builder.wsdlDocumentLocation(wsdlurl.toString());
			domsrc = (DOMSource) JAXWS_Util.makeSource(xmlRefParam1, "DOMSource");
			document = (Document) domsrc.getNode();
			builder = builder.referenceParameter(document.getDocumentElement());
			domsrc = (DOMSource) JAXWS_Util.makeSource(xmlRefParam2, "DOMSource");
			document = (Document) domsrc.getNode();
			builder = builder.referenceParameter(document.getDocumentElement());
			W3CEndpointReference epr = builder.build();
			DOMResult dr = new DOMResult();
			epr.writeTo(dr);
			XMLUtils.xmlDumpDOMNodes(dr.getNode(), false);
			if (!EprUtil.validateEPR(epr, url, SERVICE_QNAME, PORT_QNAME, PORT_TYPE_QNAME, Boolean.TRUE,
					wsdlurl.toString()))
				pass = false;
			if (!EprUtil.validateReferenceParameter(dr.getNode(), "MyParam1", "Hello"))
				pass = false;
			if (!EprUtil.validateReferenceParameter(dr.getNode(), "MyParam2", "There"))
				pass = false;
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("buildTest3 failed", e);
		}

		if (!pass)
			throw new Exception("buildTest3 failed");
	}

	/*
	 * @testName: buildTest4
	 *
	 * @assertion_ids: JAXWS:JAVADOC:206;
	 *
	 * @test_Strategy: Call build() api. Call all the api's to build an
	 * EndpointReference from scratch.
	 * 
	 */
	@Test
	public void buildTest4() throws Exception {
		TestUtil.logTrace("buildTest4");
		boolean pass = true;
		try {
			builder = builder.address(url);
			builder = builder.serviceName(SERVICE_QNAME);
			builder = builder.endpointName(PORT_QNAME);
			builder = builder.interfaceName(PORT_TYPE_QNAME);
			builder = builder.wsdlDocumentLocation(wsdlurl.toString());
			DOMSource domsrc = (DOMSource) JAXWS_Util.makeSource(xmlRefParam1, "DOMSource");
			Document document = (Document) domsrc.getNode();
			builder = builder.referenceParameter(document.getDocumentElement());
			domsrc = (DOMSource) JAXWS_Util.makeSource(xmlRefParam2, "DOMSource");
			document = (Document) domsrc.getNode();
			builder = builder.referenceParameter(document.getDocumentElement());
			W3CEndpointReference epr = builder.build();
			DOMResult dr = new DOMResult();
			epr.writeTo(dr);
			XMLUtils.xmlDumpDOMNodes(dr.getNode(), false);
			if (!EprUtil.validateEPR(epr, url, SERVICE_QNAME, PORT_QNAME, PORT_TYPE_QNAME, Boolean.TRUE,
					wsdlurl.toString()))
				pass = false;
			if (!EprUtil.validateReferenceParameter(dr.getNode(), "MyParam1", "Hello"))
				pass = false;
			if (!EprUtil.validateReferenceParameter(dr.getNode(), "MyParam2", "There"))
				pass = false;
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("buildTest4 failed", e);
		}

		if (!pass)
			throw new Exception("buildTest4 failed");
	}

	/*
	 * @testName: buildIllegalStateExceptionTest1
	 *
	 * @assertion_ids: JAXWS:JAVADOC:206;
	 *
	 * @test_Strategy: Call build() api. Test for IllegalStateException.
	 * 
	 */
	@Test
	public void buildIllegalStateExceptionTest1() throws Exception {
		TestUtil.logTrace("buildIllegalStateExceptionTest1");
		boolean pass = true;
		try {
			W3CEndpointReference epr = builder.build();
			TestUtil.logErr("Did not throw expected IllegalStateException");
			pass = false;
		} catch (IllegalStateException e) {
			logger.log(Level.INFO, "Caught expected IllegalStateException");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("buildIllegalStateExceptionTest1 failed", e);
		}

		if (!pass)
			throw new Exception("buildIllegalStateExceptionTest1 failed");
	}

	/*
	 * @testName: buildIllegalStateExceptionTest2
	 *
	 * @assertion_ids: JAXWS:JAVADOC:206;
	 *
	 * @test_Strategy: Call build() api. Test for IllegalStateException.
	 * 
	 */
	@Test
	public void buildIllegalStateExceptionTest2() throws Exception {
		TestUtil.logTrace("buildIllegalStateExceptionTest2");
		boolean pass = true;
		try {
			builder = builder.endpointName(PORT_QNAME);
			W3CEndpointReference epr = builder.build();
			TestUtil.logErr("Did not throw expected IllegalStateException");
			pass = false;
		} catch (IllegalStateException e) {
			logger.log(Level.INFO, "Caught expected IllegalStateException");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("buildIllegalStateExceptionTest2 failed", e);
		}

		if (!pass)
			throw new Exception("buildIllegalStateExceptionTest2 failed");
	}

	/*
	 * @testName: buildIllegalStateExceptionTest3
	 *
	 * @assertion_ids: JAXWS:JAVADOC:206;
	 *
	 * @test_Strategy: Call build() api. Test for IllegalStateException.
	 * 
	 */
	@Test
	public void buildIllegalStateExceptionTest3() throws Exception {
		TestUtil.logTrace("buildIllegalStateExceptionTest3");
		boolean pass = true;
		try {
			builder = builder.wsdlDocumentLocation("http://bogus.org/bogus");
			W3CEndpointReference epr = builder.build();
			TestUtil.logErr("Did not throw expected IllegalStateException");
			pass = false;
		} catch (IllegalStateException e) {
			logger.log(Level.INFO, "Caught expected IllegalStateException");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("buildIllegalStateExceptionTest3 failed", e);
		}

		if (!pass)
			throw new Exception("buildIllegalStateExceptionTest3 failed");
	}
}
