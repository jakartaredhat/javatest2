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
package com.sun.ts.tests.jaxws.ee.j2w.document.literal.restful.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Properties;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.tests.jaxws.common.BaseClient;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;
import com.sun.ts.tests.jaxws.sharedclients.HttpClient;

public class Client extends BaseClient {

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	private static final String PKG_NAME = "com.sun.ts.tests.jaxws.ee.j2w.document.literal.restful.client.";

	// URL properties used by the test
	private static final String ENDPOINT_URL = "j2wdlrestful.endpoint.1";

	private String url = null;

	/*
	 * GET http://host:port/WSJ2WDLRESTFUL/jaxws/tokens?token=1
	 * http://host:port/WSJ2WDLRESTFUL/jaxws/tokens/token/1
	 *
	 * PUT http://host:port/WSJ2WDLRESTFUL/jaxws/tokens?token=15&value=1000
	 * http://host:port/WSJ2WDLRESTFUL/jaxws/tokens/token/16/value/1001
	 *
	 * DELETE http://host:port/WSJ2WDLRESTFUL/jaxws/tokens?token=15
	 * http://host:port/WSJ2WDLRESTFUL/jaxws/tokens/token/16
	 */

	private static String queryString = "?token=1";

	private static String pathInfo = "/token/1";

	private static String putqueryString = "?token=15&value=1000";

	private static String putpathInfo = "/token/16/value/1001";

	private static String putgetqueryString = "?token=15";

	private static String putgetpathInfo = "/token/16";

	private static String deletequeryString = "?token=5";

	private static String deletegetqueryString = "?token=5";

	HttpClient httpClient;

	protected void getTestURLs() throws Exception {
		logger.log(Level.INFO, "Get URL's used by the test");
		String file = JAXWS_Util.getURLFromProp(ENDPOINT_URL);
		url = ctsurl.getURLString(PROTOCOL, hostname, portnum, file);
		logger.log(Level.INFO, "Service Endpoint URL: " + url);
	}

	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		return createWebArchive(Client.class);
	}

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
	 * @testName: testGETwithQUERYSTRING
	 *
	 * @assertion_ids: JAXWS:SPEC:3000; JAXWS:SPEC:3012; JAXWS:SPEC:3036;
	 *
	 * @test_Strategy:
	 *
	 * Description
	 */
	@Test
	public void testGETwithQUERYSTRING() throws Exception {
		boolean pass = true;
		try {
			httpClient.setUrl(url.toString() + queryString);
			httpClient.setMethod("GET");
			process();
		} catch (Exception e) {
			e.printStackTrace();
			pass = false;
		}
		if (!pass)
			throw new Exception("testGETwithQUERYSTRING failed");
	}

	/*
	 * @testName: testGETwithPATHINFO
	 *
	 * @assertion_ids: JAXWS:SPEC:3000; JAXWS:SPEC:3012; JAXWS:SPEC:3036;
	 *
	 * @test_Strategy:
	 *
	 * Description
	 */
	@Test
	public void testGETwithPATHINFO() throws Exception {

		boolean pass = true;
		try {
			httpClient.setUrl(url.toString() + pathInfo);
			httpClient.setMethod("GET");
			process();
		} catch (Exception e) {
			e.printStackTrace();
			pass = false;
		}
		if (!pass)
			throw new Exception("testGETwithPATHINFO failed");
	}

	/*
	 * @testName: testPUTwithQUERYSTRING
	 *
	 * @assertion_ids: JAXWS:SPEC:3000; JAXWS:SPEC:3012; JAXWS:SPEC:3036;
	 *
	 * @test_Strategy:
	 *
	 * Description
	 */
	@Test
	public void testPUTwithQUERYSTRING() throws Exception {
		boolean pass = true;
		try {
			httpClient.setUrl(url.toString() + putqueryString);
			httpClient.setMethod("PUT");
			process();
			httpClient.setUrl(url.toString() + putgetqueryString);
			httpClient.setMethod("GET");
			process();
		} catch (Exception e) {
			e.printStackTrace();
			pass = false;
		}
		if (!pass)
			throw new Exception("testPUTwithQUERYSTRING failed");
	}

	/*
	 * @testName: testPUTwithPATHINFO
	 *
	 * @assertion_ids: JAXWS:SPEC:3000; JAXWS:SPEC:3012; JAXWS:SPEC:3036;
	 *
	 * @test_Strategy:
	 *
	 * Description
	 */
	@Test
	public void testPUTwithPATHINFO() throws Exception {

		boolean pass = true;
		try {
			httpClient.setUrl(url.toString() + putpathInfo);
			httpClient.setMethod("PUT");
			process();
			httpClient.setUrl(url.toString() + putgetpathInfo);
			httpClient.setMethod("GET");
			process();
		} catch (Exception e) {
			e.printStackTrace();
			pass = false;
		}
		if (!pass)
			throw new Exception("testPUTwithPATHINFO failed");
	}

	/*
	 * @testName: testDELETEwithQUERYSTRING
	 *
	 * @assertion_ids: JAXWS:SPEC:3000; JAXWS:SPEC:3012; JAXWS:SPEC:3036;
	 *
	 * @test_Strategy:
	 *
	 * Description
	 */
	@Test
	public void testDELETEwithQUERYSTRING() throws Exception {
		boolean pass = true;
		try {
			httpClient.setUrl(url.toString() + deletequeryString);
			httpClient.setMethod("DELETE");
			process();
			httpClient.setUrl(url.toString() + deletegetqueryString);
			httpClient.setMethod("GET");
			process();
		} catch (Exception e) {
			e.printStackTrace();
			pass = false;
		}
		if (!pass)
			throw new Exception("testDELETEwithQUERYSTRING failed");
	}

	/*
	 * @testName: testPOST
	 *
	 * @assertion_ids: JAXWS:SPEC:3000; JAXWS:SPEC:3012; JAXWS:SPEC:3036;
	 *
	 * @test_Strategy:
	 *
	 * Description
	 */
	@Test
	public void testPOST() throws Exception {

		boolean pass = true;
		try {
			httpClient.setUrl(url.toString() + putpathInfo);
			httpClient.setMethod("POST");
			// TODO
		} catch (Exception e) {
			e.printStackTrace();
			pass = false;
		}
		if (!pass)
			throw new Exception("testPOST failed");
	}

	private Source process() throws Exception {
		return process(null);
	}

	private Source process(InputStream is) throws Exception {
		InputStream responseIs = httpClient.makeRequest(is);
		StreamSource source = new StreamSource(responseIs);
		printSource(source);
		return source;
	}

	private void printSource(Source source) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			StreamResult sr = new StreamResult(bos);
			Transformer trans = TransformerFactory.newInstance().newTransformer();
			Properties oprops = new Properties();
			oprops.put(OutputKeys.OMIT_XML_DECLARATION, "yes");
			trans.setOutputProperties(oprops);
			trans.transform(source, sr);
			System.out.println("**** Response ******" + bos.toString());
			System.out.println("");
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
