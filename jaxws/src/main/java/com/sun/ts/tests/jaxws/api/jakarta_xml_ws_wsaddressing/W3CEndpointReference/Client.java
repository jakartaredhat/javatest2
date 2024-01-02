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

package com.sun.ts.tests.jaxws.api.jakarta_xml_ws_wsaddressing.W3CEndpointReference;

import java.io.ByteArrayOutputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamResult;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;
import com.sun.ts.tests.jaxws.wsa.common.EprUtil;

import jakarta.xml.ws.wsaddressing.W3CEndpointReference;

import com.sun.ts.tests.jaxws.common.BaseClient;

public class Client extends BaseClient {

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	private static String xmlSource = "<EndpointReference xmlns=\"http://www.w3.org/2005/08/addressing\"><Address>http://localhost:8080/WSDLHelloService_web/jaxws/Hello</Address><Metadata><wsam:InterfaceName xmlns:wsam=\"http://www.w3.org/2007/05/addressing/metadata\" xmlns:wsa=\"http://www.w3.org/2005/08/addressing\" xmlns:wsns=\"http://helloservice.org/wsdl\">wsns:Hello</wsam:InterfaceName><wsam:ServiceName xmlns:wsam=\"http://www.w3.org/2007/05/addressing/metadata\" xmlns:ns3=\"http://www.w3.org/2005/08/addressing\" xmlns=\"\" xmlns:wsns=\"http://helloservice.org/wsdl\" EndpointName=\"HelloPort\">wsns:HelloService</wsam:ServiceName><wsdl:definitions xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\" xmlns:wsa=\"http://www.w3.org/2005/08/addressing\" xmlns:wsam=\"http://www.w3.org/2007/05/addressing/metadata\"><wsdl:import xmlns:ns5=\"http://www.w3.org/2005/08/addressing\" xmlns=\"\" location=\"http://localhost:8080/WSDLHelloService_web/jaxws/Hello?wsdl\" namespace=\"http://helloservice.org/wsdl\"/></wsdl:definitions></Metadata></EndpointReference>";

	private static final String URLENDPOINT = "http://localhost:8080/WSDLHelloService_web/jaxws/Hello";

	private static final String NAMESPACEURI = "http://helloservice.org/wsdl";

	private static final String SERVICE_NAME = "HelloService";

	private static final String PORT_NAME = "HelloPort";

	private static final String PORT_TYPE_NAME = "Hello";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME = new QName(NAMESPACEURI, PORT_NAME);

	private QName PORT_TYPE_QNAME = new QName(NAMESPACEURI, PORT_TYPE_NAME);

	/* Test setup */

	/*
	 * @class.setup_props:
	 */
	@BeforeEach
	public void setup() throws Exception {
		logger.log(Level.INFO, "setup ok");
	}

	@AfterEach
	public void cleanup() throws Exception {
		logger.log(Level.INFO, "cleanup ok");
	}

	/*
	 * @testName: W3CEndpointReferenceConstructorTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:184;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void W3CEndpointReferenceConstructorTest() throws Exception {
		TestUtil.logTrace("W3CEndpointReferenceConstructorTest");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Create instance via W3CEndpointReference() ...");
			W3CEndpointReference e = new W3CEndpointReference(JAXWS_Util.makeSource(xmlSource, "StreamSource"));
			if (e != null) {
				logger.log(Level.INFO, "W3CEndpointReference object created successfully");
			} else {
				TestUtil.logErr("W3CEndpointReference object not created");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("W3CEndpointReferenceConstructorTest failed", e);
		}

		if (!pass)
			throw new Exception("W3CEndpointReferenceConstructorTest failed");
	}

	/*
	 * @testName: writeToTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:185;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void writeToTest() throws Exception {
		TestUtil.logTrace("writeToTest");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Create instance via W3CEndpointReference() ...");
			W3CEndpointReference epr = new W3CEndpointReference(JAXWS_Util.makeSource(xmlSource, "StreamSource"));
			if (epr != null) {
				logger.log(Level.INFO, "W3CEndpointReference object created successfully");
			} else {
				TestUtil.logErr("W3CEndpointReference object not created");
				pass = false;
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			epr.writeTo(new StreamResult(baos));
			logger.log(Level.INFO, "writeTo(): " + baos.toString());
			logger.log(Level.INFO, "Now perform an epr.readFrom() of the results from epr.writeTo()");
			epr = new W3CEndpointReference(JAXWS_Util.makeSource(baos.toString(), "StreamSource"));
			logger.log(Level.INFO, "Validate the EPR for correctness (Verify MetaData)");
			if (!EprUtil.validateEPR(epr, URLENDPOINT, SERVICE_QNAME, PORT_QNAME, PORT_TYPE_QNAME, Boolean.TRUE)) {
				pass = false;
				TestUtil.logErr("writeTo failed to write out xml source as expected");
			} else
				logger.log(Level.INFO, "writeTo passed to write out xml source as expected");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("writeToTest failed", e);
		}

		if (!pass)
			throw new Exception("writeToTest failed");
	}
}
