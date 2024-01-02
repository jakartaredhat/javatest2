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

package com.sun.ts.tests.jaxws.ee.w2j.document.literal.httptest;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaxws.common.BaseClient;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;

public class Client extends BaseClient {

	private static final String PKG_NAME = "com.sun.ts.tests.jaxws.ee.w2j.document.literal.httptest.";

	// service and port information
	private static final String NAMESPACEURI = "http://httptestservice.org/wsdl";

	private static final String SERVICE_NAME = "HttpTestService";

	private static final String PORT_NAME = "HelloPort";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME = new QName(NAMESPACEURI, PORT_NAME);

	// URL properties used by the test
	private static final String ENDPOINT_URL = "w2jdlhttptest.endpoint.1";

	private static final String WSDLLOC_URL = "w2jdlhttptest.wsdlloc.1";

	private String url = null;

	private URL wsdlurl = null;

	Hello port = null;

	static HttpTestService service = null;

	/************************************************************************
	 * Below are defined good and bad SOAP messages which are sent to a web *
	 * service endpoint (HttpTestService) over a HttpURLConnection in order * to
	 * verify whether we get the correct HTTP status codes as required * and
	 * specified in the WSI Basic Profile Version 1.0 Specification. *
	 ************************************************************************/
	// expect 2xx http status code
	String GoodSoapMessage = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:enc=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns=\"http://httptestservice.org/types\"><soap:Body><ns:HelloRequestElement><string>World</string></ns:HelloRequestElement></soap:Body></soap:Envelope>";

	// expect 2xx http status code
	String GoodSoapMessageNoXMLDeclaration = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:enc=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns=\"http://httptestservice.org/types\"><soap:Body><ns:HelloRequestElement><string>World</string></ns:HelloRequestElement></soap:Body></soap:Envelope>";

	// expect 2xx http status code
	String GoodOneWaySoapMessage = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:enc=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns=\"http://httptestservice.org/types\"><soap:Body><ns:HelloOneWayElement><string>World</string></ns:HelloOneWayElement></soap:Body></soap:Envelope>";

	// expect 2xx http status code
	String GoodOneWaySoapMessageNoXMLDeclaration = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:enc=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns=\"http://httptestservice.org/types\"><soap:Body><ns:HelloOneWayElement><string>World</string></ns:HelloOneWayElement></soap:Body></soap:Envelope>";

	// expect 2xx http status code
	String SoapMessageUsingUTF16Encoding = "<?xml version=\"1.0\" encoding=\"utf-16\"?><soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:enc=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns=\"http://httptestservice.org/types\"><soap:Body><ns:HelloRequestElement><string>World</string></ns:HelloRequestElement></soap:Body></soap:Envelope>";

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		return createWebArchive(Client.class);
	}

	protected void getService() {
		service = (HttpTestService) getSharedObject();
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
		port = (Hello) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, HttpTestService.class, PORT_QNAME, Hello.class);
		JAXWS_Util.setTargetEndpointAddress(port, url);
	}

	protected void getPortJavaEE() throws Exception {
		logger.log(Level.INFO, "Obtaining service via WebServiceRef annotation");
		logger.log(Level.INFO, "service=" + service);
		port = (Hello) service.getPort(Hello.class);
		logger.log(Level.INFO, "port=" + port);
		logger.log(Level.INFO, "Obtained port");
		JAXWS_Util.dumpTargetEndpointAddress(port);
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

		// Initialize QNames used by the test
		SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);
		PORT_QNAME = new QName(NAMESPACEURI, PORT_NAME);

		super.setup();

	}

	@AfterEach
	public void cleanup() throws Exception {
		logger.log(Level.INFO, "cleanup ok");
	}

	/*
	 * @testName: TestGoodSoapMessage
	 *
	 * @assertion_ids: WSI:SPEC:R1125; WSI:SPEC:R1111; WSI:SPEC:R4004;
	 *
	 * @test_Strategy: Send a good SOAP RPC request over an HttpURLConnection.
	 * Verify that we get a correct HTTP status code of 2xx.
	 */
	@Test
	public void TestGoodSoapMessage() throws Exception {
		boolean pass = true;
		Iterator iterator = null;
		try {
			logger.log(Level.INFO, "TestGoodSoapMessage");
			logger.log(Level.INFO, "Send good SOAP RPC request (expect 2xx status code)");
			HttpURLConnection conn = openHttpConnection(url);
			int httpStatusCode = sendRequest(conn, GoodSoapMessage, "utf-8");
			closeHttpConnection(conn);
			if (httpStatusCode < 200 || httpStatusCode > 299) {
				TestUtil.logErr("Expected 2xx status code, instead got " + httpStatusCode);
				pass = false;
			} else
				TestUtil.logMsg("Received expected 2xx status code of " + httpStatusCode);
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("TestGoodSoapMessage failed", e);
		}

		if (!pass)
			throw new Exception("TestGoodSoapMessage failed");
	}

	/*
	 * @testName: TestGoodSoapMessageNoXMLDeclaration
	 *
	 * @assertion_ids: WSI:SPEC:R1125; WSI:SPEC:R1111;
	 *
	 * @test_Strategy: Send a good SOAP RPC request over an HttpURLConnection. Soap
	 * message does not contain the XML declaration. Verify that we get a correct
	 * HTTP status code of 2xx.
	 */
	@Test
	public void TestGoodSoapMessageNoXMLDeclaration() throws Exception {
		boolean pass = true;
		Iterator iterator = null;
		try {
			logger.log(Level.INFO, "TestGoodSoapMessageNoXMLDeclaration");
			logger.log(Level.INFO, "Send good SOAP RPC request (expect 2xx status code)");
			HttpURLConnection conn = openHttpConnection(url);
			int httpStatusCode = sendRequest(conn, GoodSoapMessageNoXMLDeclaration, "utf-8");
			closeHttpConnection(conn);
			if (httpStatusCode < 200 || httpStatusCode > 299) {
				TestUtil.logErr("Expected 2xx status code, instead got " + httpStatusCode);
				pass = false;
			} else
				TestUtil.logMsg("Received expected 2xx status code of " + httpStatusCode);
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("TestGoodSoapMessageNoXMLDeclaration failed", e);
		}

		if (!pass)
			throw new Exception("TestGoodSoapMessageNoXMLDeclaration failed");
	}

	/*
	 * @testName: TestGoodOneWaySoapMessage
	 *
	 * @assertion_ids: WSI:SPEC:R1125; WSI:SPEC:R1111; WSI:SPEC:R1112;
	 * WSI:SPEC:R4004; JAXWS:SPEC:11005; JAXWS:SPEC:10016;
	 *
	 * @test_Strategy: Send a good SOAP RPC request over an HttpURLConnection.
	 * Verify that we get a correct HTTP status code of 2xx.
	 */
	@Test
	public void TestGoodOneWaySoapMessage() throws Exception {
		boolean pass = true;
		Iterator iterator = null;
		try {
			logger.log(Level.INFO, "TestGoodOneWaySoapMessage");
			logger.log(Level.INFO, "Send good SOAP RPC request (expect 2xx status code)");
			HttpURLConnection conn = openHttpConnection(url);
			int httpStatusCode = sendRequest(conn, GoodOneWaySoapMessage, "utf-8");
			closeHttpConnection(conn);
			if (httpStatusCode < 200 || httpStatusCode > 299) {
				TestUtil.logErr("Expected 2xx status code, instead got " + httpStatusCode);
				pass = false;
			} else
				TestUtil.logMsg("Received expected 2xx status code of " + httpStatusCode);
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("TestGoodOneWaySoapMessage failed", e);
		}

		if (!pass)
			throw new Exception("TestGoodOneWaySoapMessage failed");
	}

	/*
	 * @testName: TestGoodOneWaySoapMessageNoXMLDeclaration
	 *
	 * @assertion_ids: WSI:SPEC:R1125; WSI:SPEC:R1111; WSI:SPEC:R1112;
	 * JAXWS:SPEC:11005; JAXWS:SPEC:10016;
	 *
	 * @test_Strategy: Send a good SOAP RPC request over an HttpURLConnection. Soap
	 * message does not contain the XML declaration. Verify that we get a correct
	 * HTTP status code of 2xx.
	 */
	@Test
	public void TestGoodOneWaySoapMessageNoXMLDeclaration() throws Exception {
		boolean pass = true;
		Iterator iterator = null;
		try {
			logger.log(Level.INFO, "TestGoodOneWaySoapMessageNoXMLDeclaration");
			logger.log(Level.INFO, "Send good SOAP RPC request (expect 2xx status code)");
			HttpURLConnection conn = openHttpConnection(url);
			int httpStatusCode = sendRequest(conn, GoodOneWaySoapMessageNoXMLDeclaration, "utf-8");
			closeHttpConnection(conn);
			if (httpStatusCode < 200 || httpStatusCode > 299) {
				TestUtil.logErr("Expected 2xx status code, instead got " + httpStatusCode);
				pass = false;
			} else
				TestUtil.logMsg("Received expected 2xx status code of " + httpStatusCode);
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("TestGoodOneWaySoapMessageNoXMLDeclaration failed", e);
		}

		if (!pass)
			throw new Exception("TestGoodOneWaySoapMessageNoXMLDeclaration failed");
	}

	/*
	 * @testName: TestSoapMessageUsingUTF16Encoding
	 *
	 * @assertion_ids: WSI:SPEC:R1125; WSI:SPEC:R1111; WSI:SPEC:R4003;
	 * WSI:SPEC:R4004;
	 *
	 * @test_Strategy: Send a good SOAP RPC request over an HttpURLConnection. Send
	 * SOAP RPC request using utf-16 encoding. Verify that we get a correct HTTP
	 * status code of 2xx.
	 */
	@Test
	public void TestSoapMessageUsingUTF16Encoding() throws Exception {
		boolean pass = true;
		Iterator iterator = null;
		try {
			logger.log(Level.INFO, "TestSoapMessageUsingUTF16Encoding");
			logger.log(Level.INFO, "Send SOAP RPC request using utf-16 encoding " + "(expect 2xx status code)");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			OutputStreamWriter out = new OutputStreamWriter(baos, "utf-16");
			out.write(SoapMessageUsingUTF16Encoding);
			out.flush();
			out.close();
			boolean debug = false;
			if (debug) {
				FileOutputStream faos = new FileOutputStream("/tmp/foo");
				out = new OutputStreamWriter(faos, "utf-16");
				out.write(SoapMessageUsingUTF16Encoding);
				out.flush();
				out.close();
				faos.close();
			}
			logger.log(Level.INFO, "Original SOAP message length=" + SoapMessageUsingUTF16Encoding.length());
			TestUtil.logMsg("Encoded SOAP message length=" + baos.toByteArray().length);
			HttpURLConnection conn = openHttpConnection(url);
			conn.setRequestProperty("Content-Type", "text/xml; charset=utf-16");
			int httpStatusCode = sendRequest(conn, baos.toByteArray(), "utf-16");
			closeHttpConnection(conn);
			if (httpStatusCode < 200 || httpStatusCode > 299) {
				TestUtil.logErr("Expected 2xx status code, instead got " + httpStatusCode);
				pass = false;
			} else
				TestUtil.logMsg("Received expected 2xx status code of " + httpStatusCode);
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("TestSoapMessageUsingUTF16Encoding failed", e);
		}

		if (!pass)
			throw new Exception("TestSoapMessageUsingUTF16Encoding failed");
	}

	private HttpURLConnection openHttpConnection(String s) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(s).openConnection();
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("HTTP-Version", "HTTP/1.1");
		conn.setRequestProperty("Content-Type", "text/xml");
		conn.setRequestProperty("SOAPAction", "\"\"");
		return conn;
	}

	private void closeHttpConnection(HttpURLConnection conn) throws IOException {
		conn.disconnect();
	}

	private int sendRequest(HttpURLConnection conn, String request, String charsetName) throws IOException {
		logger.log(Level.INFO, "Request=" + request);
		return _sendRequest(conn, request.getBytes(charsetName));
	}

	private int sendRequest(HttpURLConnection conn, byte[] request, String encoding) throws IOException {

		logger.log(Level.INFO, "Request=" + new String(request, encoding));
		return _sendRequest(conn, request);
	}

	private int _sendRequest(HttpURLConnection conn, byte[] data) throws IOException {

		conn.setRequestProperty("Content-Length", Integer.valueOf(data.length).toString());
		OutputStream outputStream = null;
		try {
			outputStream = conn.getOutputStream();
			outputStream.write(data);
		} finally {
			try {
				outputStream.close();
			} catch (Throwable t) {
			}
		}

		boolean isFailure = true;
		int responseCode = conn.getResponseCode();

		String responseMessage = conn.getResponseMessage();

		logger.log(Level.INFO, "ResponseCode=" + responseCode);
		logger.log(Level.INFO, "ResponseMessage=" + responseMessage);
		if (responseCode == HttpURLConnection.HTTP_OK) {
			isFailure = false;
		}
		InputStream istream = null;
		BufferedReader reader = null;
		try {
			istream = !isFailure ? conn.getInputStream() : conn.getErrorStream();
			if (istream != null) {
				StringBuffer response = new StringBuffer();
				String buf = null;
				reader = new BufferedReader(new InputStreamReader(istream));
				while ((buf = reader.readLine()) != null) {
					response.append(buf);
				}
			}
		} finally {
			try {
				reader.close();
				istream.close();
			} catch (Throwable t) {
			}
		}
		return responseCode;
	}
}
