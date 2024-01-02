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
 * $Id: Client.java 51837 2006-11-09 16:48:53Z adf $
 */

package com.sun.ts.tests.jaxws.wsa.w2j.document.literal.refps;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.UUID;

import javax.xml.namespace.QName;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaxws.common.BaseClient;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;
import com.sun.ts.tests.jaxws.wsa.common.WsaSOAPUtils;

import jakarta.xml.soap.SOAPHeader;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.soap.Text;
import jakarta.xml.ws.Dispatch;
import jakarta.xml.ws.soap.AddressingFeature;
import jakarta.xml.ws.soap.SOAPFaultException;

public class Client extends BaseClient {

	private static final String PKG_NAME = "com.sun.ts.tests.jaxws.wsa.w2j.document.literal.refps.";

	// URL properties used by the test
	private static final String ENDPOINT_URL = "wsaw2jdlreferenceparamstest.endpoint.1";

	private static final String WSDLLOC_URL = "wsaw2jdlreferenceparamstest.wsdlloc.1";

	private String url = null;

	// service and port information
	private static final String NAMESPACEURI = "http://example.com";

	private static final String SERVICE_NAME = "AddNumbersService";

	private static final String PORT_NAME = "AddNumbersPort";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME = new QName(NAMESPACEURI, PORT_NAME);

	private static final String CORRECT_ACTION = "http://example.com/AddNumbersPortType/addNumbersRequest";

	private Dispatch<SOAPMessage> dispatchSM;

	private URL wsdlurl = null;

	AddNumbersPortType port = null;

	static AddNumbersService service = null;

	public static final String REPLY_TO_REFPS_MESSAGE = "<?xml version=\"1.0\"?><S:Envelope "
			+ "xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" "
			+ "xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">" + "<S:Header>" + "<wsa:To>{0}</wsa:To>\n"
			+ "<wsa:MessageID>uuid:{1}</wsa:MessageID>\n" + "<wsa:ReplyTo>\n" + "  <wsa:Address>{2}</wsa:Address>\n"
			+ "  <wsa:ReferenceParameters>\n"
			+ "    <ck:CustomerKey xmlns:ck=\"http://example.org/customer\">Key#123456789</ck:CustomerKey>\n"
			+ "  </wsa:ReferenceParameters>" + "</wsa:ReplyTo>\n" + "<wsa:Action>{3}</wsa:Action>\n" + "</S:Header>\n"
			+ "<S:Body>\n" + "<addNumbers xmlns=\"http://example.com\">\n" + "  <number1>10</number1>\n"
			+ "  <number2>10</number2>\n" + "</addNumbers>\n" + "</S:Body></S:Envelope>";

	public static final String FAULT_TO_REFPS_MESSAGE = "<S:Envelope "
			+ "xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" "
			+ "xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">" + "<S:Header>\n" + "<wsa:To>{0}</wsa:To>\n"
			+ "<wsa:MessageID>uuid:{1}</wsa:MessageID>\n" + "<wsa:ReplyTo>\n" + "  <wsa:Address>{2}</wsa:Address>\n"
			+ "  <wsa:ReferenceParameters>\n"
			+ "    <ck:CustomerKey xmlns:ck=\"http://example.org/customer\">Key#123456789</ck:CustomerKey>\n"
			+ "  </wsa:ReferenceParameters>" + "</wsa:ReplyTo>\n" + "<wsa:FaultTo>\n"
			+ "  <wsa:Address>{3}</wsa:Address>\n" + "  <wsa:ReferenceParameters>\n"
			+ "    <ck:CustomerKey xmlns:ck=\"http://example.org/customer\">Fault#123456789</ck:CustomerKey>\n"
			+ "  </wsa:ReferenceParameters>" + "</wsa:FaultTo>\n" + "<wsa:Action>{4}</wsa:Action>\n" + "</S:Header>\n"
			+ "<S:Body>\n" + "<addNumbers xmlns=\"http://example.com\">\n" + "  <number1>-10</number1>\n"
			+ "  <number2>10</number2>\n" + "</addNumbers>\n" + "</S:Body></S:Envelope>";

	private static AddressingFeature ENABLED_ADDRESSING_FEATURE = new AddressingFeature(true, true);

	private static AddressingFeature DISABLED_ADDRESSING_FEATURE = new AddressingFeature(false);

	// Reference parameter constants
	private QName CUSTOMER_KEY = new QName("http://example.org/customer", "CustomerKey");

	private static final String CUSTOMER_KEY_VALUE = "Key#123456789";

	private QName IS_REF_PARAM = new QName("http://www.w3.org/2005/08/addressing", "IsReferenceParameter");

	private static final String IS_REF_PARAM_VALUE = "1";

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	private boolean validate(SOAPMessage sm, QName refp, String refp_value, QName isRefParam, String isRefParam_value) {
		try {
			logger.log(Level.INFO, "get SOAPHeader");
			SOAPHeader header = sm.getSOAPHeader();
			logger.log(Level.INFO, "get Child Element for " + refp);
			Iterator iter = header.getChildElements(refp);
			if (!iter.hasNext()) {
				TestUtil.logErr("SOAPMessage response does not contain" + refp);
				return false;
			}
			Element element = (Element) iter.next();
			logger.log(Level.INFO, "get Attribute node for " + isRefParam);
			Attr attr = element.getAttributeNodeNS(isRefParam.getNamespaceURI(), isRefParam.getLocalPart());
			if (attr == null) {
				TestUtil.logErr("attribute not found: " + isRefParam);
				return false;
			}
			logger.log(Level.INFO, "get all Child Nodes");
			NodeList nodes = element.getChildNodes();
			boolean found = false;
			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = (Node) nodes.item(i);
				if (node instanceof Text) {
					logger.log(Level.INFO, "got TEXT node");
					String value = node.getNodeValue();
					logger.log(Level.INFO, "value=[" + value + "]");
					if (!value.equals(CUSTOMER_KEY_VALUE)) {
						TestUtil.logErr("CUSTOMER_KEY_VALUE: expected: " + CUSTOMER_KEY_VALUE + ", received: " + value);
						return false;
					} else
						found = true;
				}
			}
			if (!found) {
				TestUtil.logErr("Did not found CUSTOMER_KEY_VALUE: " + CUSTOMER_KEY_VALUE);
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private Dispatch<SOAPMessage> createDispatchSOAPMessage(QName port, boolean enabled) throws Exception {
		if (enabled)
			return service.createDispatch(port, SOAPMessage.class, jakarta.xml.ws.Service.Mode.MESSAGE,
					ENABLED_ADDRESSING_FEATURE);
		else
			return service.createDispatch(port, SOAPMessage.class, jakarta.xml.ws.Service.Mode.MESSAGE,
					DISABLED_ADDRESSING_FEATURE);
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
		port = (AddNumbersPortType) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, AddNumbersService.class, PORT_QNAME,
				AddNumbersPortType.class);
		logger.log(Level.INFO, "port=" + port);
		JAXWS_Util.setTargetEndpointAddress(port, url);
	}

	protected void getPortJavaEE() throws Exception {
		logger.log(Level.INFO, "Obtain service via WebServiceRef annotation");
		logger.log(Level.INFO, "service=" + service);
		port = (AddNumbersPortType) service.getAddNumbersPort();
		logger.log(Level.INFO, "port=" + port);
		logger.log(Level.INFO, "Obtained port");
		JAXWS_Util.dumpTargetEndpointAddress(port);
	}

	protected void getService() {
		service = (AddNumbersService) getSharedObject();
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
	 * @testName: testReplyToRefps
	 *
	 * @assertion_ids: WSACORE:SPEC:3012; WSACORE:SPEC:3012.1; WSACORE:SPEC:3012.2;
	 * WSACORE:SPEC:2004; WSACORE:SPEC:2004.3; WSACORE:SPEC:3021;
	 *
	 * @test_Strategy:
	 *
	 */

	@Test
	public void testReplyToRefps() throws Exception {
		boolean pass = true;
		try {

			String soapmsg = MessageFormat.format(REPLY_TO_REFPS_MESSAGE, url, UUID.randomUUID(),
					WsaSOAPUtils.getAddrVerAnonUri(), CORRECT_ACTION);
			dispatchSM = createDispatchSOAPMessage(PORT_QNAME, false);
			SOAPMessage request = JAXWS_Util.makeSOAPMessage(soapmsg);
			logger.log(Level.INFO, "Dumping SOAP Request ...");
			JAXWS_Util.dumpSOAPMessage(request, false);
			SOAPMessage response = dispatchSM.invoke(request);
			logger.log(Level.INFO, "Dumping SOAP Response ...");
			JAXWS_Util.dumpSOAPMessage(response, false);
			pass = validate(response, CUSTOMER_KEY, CUSTOMER_KEY_VALUE, IS_REF_PARAM, IS_REF_PARAM_VALUE);
		} catch (Exception e) {
			TestUtil.logErr("Caught Exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("testReplyToRefps failed", e);
		}

		if (!pass)
			throw new Exception("testReplyToRefps failed");
	}

	/*
	 * @testName: testFaultToRefps
	 *
	 * @assertion_ids: WSACORE:SPEC:3012; WSACORE:SPEC:3012.1; WSACORE:SPEC:3012.2;
	 * WSACORE:SPEC:2004; WSACORE:SPEC:2004.3; WSACORE:SPEC:3021;
	 *
	 * @test_Strategy:
	 *
	 */

	@Test
	public void testFaultToRefps() throws Exception {
		boolean pass = true;

		try {
			String soapmsg = MessageFormat.format(FAULT_TO_REFPS_MESSAGE, url, UUID.randomUUID(),
					WsaSOAPUtils.getAddrVerAnonUri(), WsaSOAPUtils.getAddrVerAnonUri(), CORRECT_ACTION);
			dispatchSM = createDispatchSOAPMessage(PORT_QNAME, false);
			SOAPMessage request = JAXWS_Util.makeSOAPMessage(soapmsg);
			logger.log(Level.INFO, "Dumping SOAP Request ...");
			JAXWS_Util.dumpSOAPMessage(request, false);
			SOAPMessage response = dispatchSM.invoke(request);
			JAXWS_Util.dumpSOAPMessage(response, false);
			pass = false;
			TestUtil.logErr("SOAPFaultException must be thrown");
		} catch (SOAPFaultException e) {
			logger.log(Level.INFO, "Caught expected SOAPFaultException: " + e.getMessage());
			try {
				logger.log(Level.INFO, "FaultCode=" + WsaSOAPUtils.getFaultCode(e));
				logger.log(Level.INFO, "FaultString=" + WsaSOAPUtils.getFaultString(e));
				String faultdetail[] = WsaSOAPUtils.getFaultDetail(e);
				if (faultdetail != null) {
					String output = "FaultDetail:";
					for (int i = 0; faultdetail[i] != null; i++) {
						output += " " + faultdetail[i];
					}
					logger.log(Level.INFO, "" + output);
				}
			} catch (Exception e2) {
				TestUtil.logErr("Caught unexpected exception: " + e2.getMessage());
				throw new Exception("testFaultToRefps failed", e2);
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught Exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("testFaultToRefps failed", e);
		}

		if (!pass)
			throw new Exception("testFaultToRefps failed");
	}
}
