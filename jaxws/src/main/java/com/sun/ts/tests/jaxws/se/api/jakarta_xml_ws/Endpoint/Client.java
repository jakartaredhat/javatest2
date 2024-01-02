/*
 * Copyright (c) 2009, 2020 Oracle and/or its affiliates. All rights reserved.
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
 * $Id: Client.java.se 51058 2006-02-11 20:00:31Z adf $
 */

package com.sun.ts.tests.jaxws.se.api.jakarta_xml_ws.Endpoint;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.porting.TSURL;
import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;
import com.sun.ts.tests.jaxws.common.BaseClient;
import com.sun.ts.tests.jaxws.sharedclients.HttpClient;

import jakarta.xml.ws.Binding;
import jakarta.xml.ws.Endpoint;
import jakarta.xml.ws.EndpointReference;
import jakarta.xml.ws.soap.SOAPBinding;
import jakarta.xml.ws.wsaddressing.W3CEndpointReference;

public class Client extends BaseClient {

	private static final String ENDPOINTPUBLISHPROP = "http.server.supports.endpoint.publish";

	private boolean endpointPublishSupport;

	// service and port info
	private static final String NAMESPACEURI = "http://helloservice.org/wsdl";

	private static final String SERVICE_NAME = "HelloService";

	private static final String PORT_NAME = "HelloPort";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME = new QName(NAMESPACEURI, PORT_NAME);

	// Endpoint info
	private Endpoint endpoint = null;

	private static final Object IMPLEMENTOR = new com.sun.ts.tests.jaxws.se.api.jakarta_xml_ws.Endpoint.HelloImpl();

	private static final String CONTEXTROOT = "/WSEndpoint";

	private static final String URL_ALIAS = "/jaxws/Hello";

	private String ts_home;

	private String sepChar = System.getProperty("file.separator");

	private String testDir = "src" + sepChar + "com" + sepChar + "sun" + sepChar + "ts" + sepChar + "tests" + sepChar
			+ "jaxws" + sepChar + "se" + sepChar + "api" + sepChar + "jakarta_xml_ws" + sepChar + "Endpoint";

	private String url = null;

	// SE URL properties used by the test
	private static final String SE_ENDPOINT_URL = "/WSEndpoint/jaxws/Hello";

	private static final String SE_WSDLLOC_URL = "/WSEndpoint/jaxws/Hello?wsdl";

	private URL wsdlurl = null;

	private int javaseServerPort;

	private String helloReq = "<?xml version='1.0' encoding='UTF-8'?><S:Envelope xmlns:S='http://schemas.xmlsoap.org/soap/envelope/'><S:Body><ns2:hello xmlns:ns2='http://helloservice.org/wsdl'><arg0>you</arg0></ns2:hello><S:Body></S:Envelope>";

	private EndpointReference epr = null;

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	private boolean makeHTTPRequest(String request, String url) throws Exception {
		boolean pass = true;
		logger.log(Level.INFO, "HTTP REQUEST: " + request);
		HttpClient httpClient = new HttpClient();
		httpClient.setUrl(url);
		InputStream response = httpClient.makeRequest(getInputStreamForString(request));
		if (response != null) {
			ByteArrayOutputStream baos = getInputStreamAsOutputStream(response);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			logger.log(Level.INFO, "HTTP RESPONSE: " + baos.toString());
			if (baos.toString().indexOf("Hello, you") < 0) {
				TestUtil.logErr("HTTP RESPONSE does not contain expected string -> Hello, you");
				pass = false;
			}
		} else {
			TestUtil.logErr("HTTP RESPONSE no response returned");
			pass = false;
		}
		try {
			int status = httpClient.getStatusCode();
			logger.log(Level.INFO, "HTTP STATUS CODE: " + status);
			if (status != 200) {
				TestUtil.logErr("HTTP STATUS CODE is not okay");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("unable to get HTTP STATUS CODE, exception occurred: " + e);
			pass = false;
		}
		return pass;
	}

	private ByteArrayInputStream getInputStreamForString(String request) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		OutputStreamWriter osw;
		osw = new OutputStreamWriter(bos, Charset.forName("UTF-8"));
		osw.write(request);
		osw.flush();
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		return bis;
	}

	private ByteArrayOutputStream getInputStreamAsOutputStream(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		do {
			length = is.read(buffer);
			if (length > 0) {
				baos.write(buffer, 0, length);
			}
		} while (length > 0);
		return baos;
	}

	protected void getTestURLs() throws Exception {
		logger.log(Level.INFO, "Get URL's used by the test");
		url = ctsurl.getURLString(PROTOCOL, hostname, javaseServerPort, SE_ENDPOINT_URL);
		wsdlurl = ctsurl.getURL(PROTOCOL, hostname, javaseServerPort, SE_WSDLLOC_URL);
		logger.log(Level.INFO, "Service Endpoint URL: " + url);
		logger.log(Level.INFO, "WSDL Location URL:    " + wsdlurl);
	}

	/* Test setup */

	/*
	 * @class.testArgs: -ap jaxws-url-props.dat
	 * 
	 * @class.setup_props: webServerHost; webServerPort; platform.mode;
	 * http.server.supports.endpoint.publish; ts.home;
	 */
	@BeforeEach
	public void setup() throws Exception {
		super.setup();
		modeProperty = System.getProperty(MODEPROP);
		endpointPublishSupport = Boolean.parseBoolean(System.getProperty(ENDPOINTPUBLISHPROP));
		ts_home = System.getProperty("ts.home");
		logger.log(Level.INFO, "ts_home=" + ts_home);
		javaseServerPort = JAXWS_Util.getFreePort();
		if (javaseServerPort <= 0) {
			TestUtil.logErr("Free port not found.");
		}
		getTestURLs();
		endpoint = Endpoint.create(SOAPBinding.SOAP11HTTP_BINDING, IMPLEMENTOR);
		if (endpoint == null)
			throw new Exception("setup failed, unable to create endpoint");
		logger.log(Level.INFO, "setup ok");

	}

	@AfterEach
	public void cleanup() throws Exception {
		logger.log(Level.INFO, "cleanup ok");
	}

	/*
	 * @testName: createTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:12; JAXWS:JAVADOC:13; JAXWS:JAVADOC:115;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void createTest() throws Exception {
		TestUtil.logTrace("createTest");
		boolean pass = true;
		try {
			Endpoint theEndpoint = Endpoint.create(IMPLEMENTOR);
			logger.log(Level.INFO, "endpoint=" + theEndpoint);
			if (theEndpoint == null) {
				TestUtil.logErr("Endpoint.create returned null");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.printStackTrace(e);
			throw new Exception("createTest failed", e);
		}

		if (!pass)
			throw new Exception("createTest failed");
	}

	/*
	 * @testName: createTest2
	 *
	 * @assertion_ids: JAXWS:JAVADOC:13; JAXWS:JAVADOC:14; JAXWS:JAVADOC:115;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void createTest2() throws Exception {
		TestUtil.logTrace("createTest2");
		boolean pass = true;
		try {
			Endpoint theEndpoint = Endpoint.create(SOAPBinding.SOAP11HTTP_BINDING, IMPLEMENTOR);
			logger.log(Level.INFO, "endpoint=" + theEndpoint);
			if (theEndpoint == null) {
				TestUtil.logErr("Endpoint.create returned null");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.printStackTrace(e);
			throw new Exception("createTest2 failed", e);
		}

		if (!pass)
			throw new Exception("createTest2 failed");
	}

	/*
	 * @testName: getBindingTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:15;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void getBindingTest() throws Exception {
		TestUtil.logTrace("getBindingTest");
		boolean pass = true;
		try {
			Binding binding = endpoint.getBinding();
			logger.log(Level.INFO, "binding=" + binding);
			if (binding == null) {
				TestUtil.logErr("Endpoint.getBinding returned null");
				pass = false;
			}
			if (!(binding instanceof SOAPBinding)) {
				TestUtil.logErr("binding is not an instance of SOAPBinding");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.printStackTrace(e);
			throw new Exception("getBindingTest failed", e);
		}

		if (!pass)
			throw new Exception("getBindingTest failed");
	}

	/*
	 * @testName: getImplementorTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:17;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void getImplementorTest() throws Exception {
		TestUtil.logTrace("getImplementorTest");
		boolean pass = true;
		try {
			Object implementor = endpoint.getImplementor();
			logger.log(Level.INFO, "implementor=" + implementor);
			if (implementor == null) {
				TestUtil.logErr("Endpoint.getImplementor returned null");
				pass = false;
			}
			if (!(implementor instanceof com.sun.ts.tests.jaxws.se.api.jakarta_xml_ws.Endpoint.HelloImpl)) {
				TestUtil.logErr("binding is not an instance of HelloImpl");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.printStackTrace(e);
			throw new Exception("getImplementorTest failed", e);
		}

		if (!pass)
			throw new Exception("getImplementorTest failed");
	}

	public void collectMetadata(File wsdlDirFile, List<File> metadataFile) {
		File[] files = wsdlDirFile.listFiles();
		if (files == null) {
			logger.log(Level.INFO, "no metadata files to collect");
			return;
		}
		for (File file : files) {
			if (file.isDirectory()) {
				// collectMetadata(file, metadataFile);
				continue;
			}
			if (file.getName().endsWith(".xsd") || file.getName().endsWith(".wsdl")) {
				logger.log(Level.INFO, "collectMetadata: adding file " + file.getName());
				metadataFile.add(file);
			}
		}
	}

	/*
	 * @testName: publishTest2
	 *
	 * @assertion_ids: JAXWS:JAVADOC:21; JAXWS:JAVADOC:27; JAXWS:SPEC:5005;
	 * JAXWS:SPEC:5007; JAXWS:SPEC:5008; JAXWS:SPEC:5017; JAXWS:SPEC:5018;
	 * JAXWS:SPEC:5019; JAXWS:SPEC:5020; JAXWS:SPEC:5021;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void publishTest2() throws Exception {
		TestUtil.logTrace("publishTest2");
		boolean pass = true;
		try {
			endpoint.publish(url);
			if (modeProperty.equals("jakartaEE") || !endpointPublishSupport) {
				TestUtil.logErr("expected exception when endpoint publish not supported");
				pass = false;
			} else if (modeProperty.equals("standalone") && endpointPublishSupport) {
				logger.log(Level.INFO, "invoke hello operation of published endpoint");
				if (makeHTTPRequest(helloReq, url))
					logger.log(Level.INFO, "Successful invocation of published endpoint");
				else {
					TestUtil.logErr("Unsuccessful invocation of published endpoint");
					pass = false;
				}
				endpoint.stop();
			}
		} catch (Exception e) {
			if (modeProperty.equals("standalone") && endpointPublishSupport) {
				TestUtil.printStackTrace(e);
				pass = false;
			}
		}

		if (!pass)
			throw new Exception("publishTest2 failed");
	}

	/*
	 * @testName: publishTest3
	 *
	 * @assertion_ids: JAXWS:JAVADOC:22; JAXWS:JAVADOC:27; JAXWS:JAVADOC:114;
	 * JAXWS:SPEC:5004; JAXWS:SPEC:6002; JAXWS:SPEC:5005; JAXWS:SPEC:5007;
	 * JAXWS:SPEC:5008; JAXWS:SPEC:5017; JAXWS:SPEC:5018; JAXWS:SPEC:5019;
	 * JAXWS:SPEC:5020; JAXWS:SPEC:5021;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void publishTest3() throws Exception {
		TestUtil.logTrace("publishTest3");
		boolean pass = true;
		try {
			endpoint = Endpoint.publish(url, IMPLEMENTOR);
			if (modeProperty.equals("jakartaEE") || !endpointPublishSupport) {
				TestUtil.logErr("expected exception when endpoint publish not supported");
				pass = false;
			} else if (modeProperty.equals("standalone") && endpointPublishSupport) {
				logger.log(Level.INFO, "invoke hello operation of published endpoint");
				if (makeHTTPRequest(helloReq, url))
					logger.log(Level.INFO, "Successful invocation of published endpoint");
				else {
					TestUtil.logErr("Unsuccessful invocation of published endpoint");
					pass = false;
				}
				endpoint.stop();
			}
		} catch (Exception e) {
			if (modeProperty.equals("standalone") && endpointPublishSupport) {
				TestUtil.printStackTrace(e);
				pass = false;
			}
		}

		if (!pass)
			throw new Exception("publishTest3 failed");
	}

	/*
	 * @testName: stopTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:21; JAXWS:JAVADOC:27; JAXWS:SPEC:5005;
	 * JAXWS:SPEC:5007; JAXWS:SPEC:5008; JAXWS:SPEC:5017; JAXWS:SPEC:5018;
	 * JAXWS:SPEC:5019; JAXWS:SPEC:5020; JAXWS:SPEC:5021;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void stopTest() throws Exception {
		TestUtil.logTrace("stopTest");
		boolean pass = true;
		try {
			endpoint.publish(url);
			if (modeProperty.equals("jakartaEE") || !endpointPublishSupport) {
				TestUtil.logErr("expected exception when endpoint publish not supported");
				pass = false;
			} else if (modeProperty.equals("standalone") && endpointPublishSupport) {
				logger.log(Level.INFO, "invoke hello operation of published endpoint");
				if (makeHTTPRequest(helloReq, url))
					logger.log(Level.INFO, "Successful invocation of published endpoint");
				else {
					TestUtil.logErr("Unsuccessful invocation of published endpoint");
					pass = false;
				}
				endpoint.stop();
				logger.log(Level.INFO, "invoke hello operation of published endpoint that is stopped");
				if (makeHTTPRequest(helloReq, url)) {
					TestUtil.logErr("Successful invocation of a stopped endpoint - unexpected ");
					pass = false;
				} else
					logger.log(Level.INFO, "Unsuccessful invocation of a stopped endpoint - expected ");
			}
		} catch (Exception e) {
			if (modeProperty.equals("standalone") && endpointPublishSupport) {
				TestUtil.printStackTrace(e);
				pass = false;
			}
		}

		if (!pass)
			throw new Exception("stopTest failed");
	}

	/*
	 * @testName: isPublishedTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:20;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void isPublishedTest() throws Exception {
		TestUtil.logTrace("isPublishedTest");
		boolean pass = true;
		try {
			boolean isPub = endpoint.isPublished();
			if (isPub) {
				TestUtil.logErr("Endpoint is published - unexpected");
				pass = false;
			} else
				logger.log(Level.INFO, "Endpoint is not published - expected");
			endpoint.publish(url);
			if (modeProperty.equals("jakartaEE") || !endpointPublishSupport) {
				TestUtil.logErr("expected exception when endpoint publish not supported");
				pass = false;
			} else if (modeProperty.equals("standalone") && endpointPublishSupport) {
				isPub = endpoint.isPublished();
				if (!isPub) {
					TestUtil.logErr("Endpoint is not published - unexpected");
					pass = false;
				} else
					logger.log(Level.INFO, "Endpoint is published - expected");
			}
		} catch (Exception e) {
			if (modeProperty.equals("standalone") && endpointPublishSupport) {
				TestUtil.printStackTrace(e);
				pass = false;
			}
		}

		if (!pass)
			throw new Exception("isPublishedTest failed");
	}

	/*
	 * @testName: GetSetPropertiesTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:19; JAXWS:JAVADOC:26;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void GetSetPropertiesTest() throws Exception {
		TestUtil.logTrace("GetSetPropertiesTest");
		boolean pass = true;
		try {
			Map<String, Object> map = endpoint.getProperties();
			if (map == null) {
				map = new HashMap<String, Object>();
				endpoint.setProperties(map);
			}
			map.put(Endpoint.WSDL_SERVICE, SERVICE_QNAME);
			map.put(Endpoint.WSDL_PORT, PORT_QNAME);
		} catch (Exception e) {
			TestUtil.printStackTrace(e);
			throw new Exception("GetSetPropertiesTest failed", e);
		}

		if (!pass)
			throw new Exception("GetSetPropertiesTest failed");
	}

	/*
	 * @testName: GetSetExecutorTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:16; JAXWS:JAVADOC:24; JAXWS:SPEC:5011;
	 * JAXWS:SPEC:5012;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void GetSetExecutorTest() throws Exception {
		TestUtil.logTrace("GetSetExecutorTest");
		boolean pass = true;
		try {
			Executor executor = endpoint.getExecutor();
			if (executor != null) {
				logger.log(Level.INFO, "set same Executor");
				endpoint.setExecutor(executor);
			} else {
				ExecutorService appExecutorService = Executors.newFixedThreadPool(5);
				endpoint.setExecutor(appExecutorService);
				logger.log(Level.INFO, "set new Executor");
			}
		} catch (Exception e) {
			TestUtil.printStackTrace(e);
			throw new Exception("GetSetExecutorTest failed", e);
		}

		if (!pass)
			throw new Exception("GetSetExecutorTest failed");
	}

	/*
	 * @testName: GetSetMetaDataTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:18; JAXWS:JAVADOC:25; JAXWS:SPEC:5010;
	 * JAXWS:SPEC:5011;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void GetSetMetaDataTest() throws Exception {
		TestUtil.logTrace("GetSetMetaDataTest");
		boolean pass = true;
		File metaGood = new File(ts_home + sepChar + testDir);
		File metaBad = new File(ts_home + sepChar + testDir + sepChar + "contentRoot" + sepChar + "mydocs");
		logger.log(Level.INFO, "metaGood=" + metaGood);
		logger.log(Level.INFO, "metaBad=" + metaBad);
		try {
			logger.log(Level.INFO, "Testing GetSetMetaData using good MetaData");
			List<Source> metadata = endpoint.getMetadata();
			List<File> metadataFile = new ArrayList();
			collectMetadata(metaGood, metadataFile);
			if (metadataFile.size() > 0) {
				metadata = new ArrayList<Source>();
				for (File file : metadataFile) {
					Source source = new StreamSource(new FileInputStream(file));
					source.setSystemId(file.toURL().toExternalForm());
					metadata.add(source);
				}
				endpoint.setMetadata(metadata);
				if (endpointPublishSupport) {
					logger.log(Level.INFO, "Publishing endpoint to url: " + url);
					endpoint.publish(url);
					logger.log(Level.INFO, "Invoking published endpoint");
					if (makeHTTPRequest(helloReq, url))
						logger.log(Level.INFO, "Successful invocation of published endpoint");
					else {
						logger.log(Level.INFO, "Unsuccessful invocation of published endpoint");
						pass = false;
					}
					logger.log(Level.INFO, "Stopping endpoint");
					endpoint.stop();
				}
			}
		} catch (Exception e) {
			TestUtil.printStackTrace(e);
			throw new Exception("GetSetMetaDataTest failed", e);
		}
		try {
			logger.log(Level.INFO, "Testing GetSetMetaData using bad MetaData");
			List<Source> metadata = endpoint.getMetadata();
			List<File> metadataFile = new ArrayList();
			collectMetadata(metaBad, metadataFile);
			if (metadataFile.size() > 0) {
				metadata = new ArrayList<Source>();
				for (File file : metadataFile) {
					Source source = new StreamSource(new FileInputStream(file));
					source.setSystemId(file.toURL().toExternalForm());
					metadata.add(source);
				}
				endpoint.setMetadata(metadata);
				if (endpointPublishSupport) {
					try {
						logger.log(Level.INFO, "Publishing endpoint to url: " + url);
						endpoint.publish(url);
						logger.log(Level.INFO, "Stopping endpoint");
						endpoint.stop();
						pass = false;
						TestUtil.logErr("publishing should have failed with bad metadata");
					} catch (Exception e) {
						logger.log(Level.INFO, "Got expected exception on bad metadata");
					}
				}
			}
		} catch (Exception e) {
			TestUtil.printStackTrace(e);
			throw new Exception("GetSetMetaDataTest failed", e);
		}

		if (!pass)
			throw new Exception("GetSetMetaDataTest failed");
	}

	/*
	 * @testName: getEndpointReferenceParamsTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:137;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void getEndpointReferenceParamsTest() throws Exception {
		TestUtil.logTrace("getEndpointReferenceParamsTest");
		boolean pass = true;
		if (modeProperty.equals("jakartaEE")) {
			logger.log(Level.INFO, "Not tested in jakartaEE platform");
			pass = false;
		}
		try {
			endpoint.publish(url);
			if (!endpointPublishSupport) {
				TestUtil.logErr("expected exception when endpoint publish not supported");
				pass = false;
			} else {
				epr = endpoint.getEndpointReference();
				logger.log(Level.INFO, "EndpointReference object=" + epr);
				if (epr == null) {
					TestUtil.logErr("getEndpointReference() returned null");
					pass = false;
				} else
					logger.log(Level.INFO, "getEndpointReference() returned EndpointReference object: " + epr);
				if (epr instanceof W3CEndpointReference)
					logger.log(Level.INFO, "epr instanceof W3CEndpointReference");
				else {
					TestUtil.logErr("epr not instanceof W3CEndpointReference");
					pass = false;
				}
			}
		} catch (Exception e) {
			if (modeProperty.equals("standalone") && endpointPublishSupport) {
				TestUtil.printStackTrace(e);
				pass = false;
			}
		}

		if (!pass)
			throw new Exception("getEndpointReferenceParamsTest failed");
	}

	/*
	 * @testName: getEndpointReferenceClassTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:138;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void getEndpointReferenceClassTest() throws Exception {
		TestUtil.logTrace("getEndpointReferenceClassTest");
		boolean pass = true;
		if (modeProperty.equals("jakartaEE")) {
			logger.log(Level.INFO, "Not tested in jakartaEE platform");
			pass = false;
		}
		try {
			endpoint.publish(url);
			if (!endpointPublishSupport) {
				TestUtil.logErr("expected exception when endpoint publish not supported");
				pass = false;
			} else {
				epr = endpoint.getEndpointReference(W3CEndpointReference.class);
				logger.log(Level.INFO, "EndpointReference object=" + epr);
				if (epr == null) {
					TestUtil.logErr("getEndpointReference() returned null");
					pass = false;
				} else
					logger.log(Level.INFO, "getEndpointReference() returned EndpointReference object: " + epr);
				if (epr instanceof W3CEndpointReference)
					logger.log(Level.INFO, "epr instanceof W3CEndpointReference");
				else {
					TestUtil.logErr("epr not instanceof W3CEndpointReference");
					pass = false;
				}
			}
		} catch (Exception e) {
			if (modeProperty.equals("standalone") && endpointPublishSupport) {
				TestUtil.printStackTrace(e);
				pass = false;
			}
		}

		if (!pass)
			throw new Exception("getEndpointReferenceClassTest failed");
	}

}
