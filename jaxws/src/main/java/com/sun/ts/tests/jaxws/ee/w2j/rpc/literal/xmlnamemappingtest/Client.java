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

package com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.xmlnamemappingtest;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;

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

	private static final String PKG_NAME = "com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.xmlnamemappingtest.";

	// service and port information
	private static final String NAMESPACEURI = "http://XMLNameMappingTest.org/wsdl";

	private static final String SERVICE_NAME = "xMLNameMappingTest";

	private static final String PORT_NAME = "XMLNameMappingTestPort";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME = new QName(NAMESPACEURI, PORT_NAME);

	// EyeColor data
	private static EyeColor eyeColor_data = null;

	// XMLNameMappingTest_Type struct data
	private static XMLNameMappingTest_Type xmlNameMapping_data = null;

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		return createWebArchive(Client.class);
	}

	static {
		try {
			xmlNameMapping_data = new XMLNameMappingTest_Type();
			xmlNameMapping_data.setVarString("string1");
			xmlNameMapping_data.setVarInt(Integer.MIN_VALUE);
			xmlNameMapping_data.setVarFloat(Float.MIN_VALUE);

			eyeColor_data = new EyeColor();
			eyeColor_data.setColor("blue");
		} catch (Exception e) {
			TestUtil.logErr("exception on data initialization." + e);
			e.printStackTrace();
		}
	}

	// URL properties used by the test
	private static final String ENDPOINT_URL = "xmlnamemappingtest.endpoint.1";

	private static final String WSDLLOC_URL = "xmlnamemappingtest.wsdlloc.1";

	private String url = null;

	private URL wsdlurl = null;

	XMLNameMappingTest port = null;

	static XMLNameMappingTest_Service service = null;

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
		port = (XMLNameMappingTest) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, XMLNameMappingTest_Service.class,
				PORT_QNAME, XMLNameMappingTest.class);
		JAXWS_Util.setTargetEndpointAddress(port, url);
	}

	protected void getPortJavaEE() throws Exception {
		logger.log(Level.INFO, "Obtain service via WebServiceRef annotation");
		logger.log(Level.INFO, "service=" + service);
		port = (XMLNameMappingTest) service.getXMLNameMappingTestPort();
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
		super.setup();
	}

	@AfterEach
	public void cleanup() throws Exception {
		logger.log(Level.INFO, "cleanup ok");
	}

	private void printSeperationLine() {
		logger.log(Level.INFO, "---------------------------");
	}

	/*
	 * @testName: JavaKeywordsTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2057; JAXWS:SPEC:2066; JAXWS:SPEC:2067;
	 * JAXWS:SPEC:2068;
	 * 
	 * @test_Strategy:
	 */
	@Test
	public void JavaKeywordsTest() throws Exception {
		TestUtil.logTrace("JavaKeywordsTest");
		boolean pass = true;

		if (!nullTest())
			pass = false;
		printSeperationLine();
		if (!trueTest())
			pass = false;
		printSeperationLine();
		if (!falseTest())
			pass = false;
		printSeperationLine();
		if (!abstractTest())
			pass = false;
		printSeperationLine();
		if (!booleanTest())
			pass = false;
		printSeperationLine();
		if (!breakTest())
			pass = false;
		printSeperationLine();
		if (!byteTest())
			pass = false;
		printSeperationLine();
		if (!caseTest())
			pass = false;
		printSeperationLine();
		if (!catchTest())
			pass = false;
		printSeperationLine();
		if (!charTest())
			pass = false;
		printSeperationLine();
		if (!classTest())
			pass = false;
		printSeperationLine();
		if (!constTest())
			pass = false;
		printSeperationLine();
		if (!continueTest())
			pass = false;
		printSeperationLine();
		if (!defaultTest())
			pass = false;
		printSeperationLine();
		if (!doTest())
			pass = false;
		printSeperationLine();
		if (!doubleTest())
			pass = false;
		printSeperationLine();
		if (!elseTest())
			pass = false;
		printSeperationLine();
		if (!extendsTest())
			pass = false;
		printSeperationLine();
		if (!finalTest())
			pass = false;
		printSeperationLine();
		if (!finallyTest())
			pass = false;
		printSeperationLine();
		if (!floatTest())
			pass = false;
		printSeperationLine();
		if (!forTest())
			pass = false;
		printSeperationLine();
		if (!gotoTest())
			pass = false;
		printSeperationLine();
		if (!ifTest())
			pass = false;
		printSeperationLine();
		if (!implementsTest())
			pass = false;
		printSeperationLine();
		if (!importTest())
			pass = false;
		printSeperationLine();
		if (!instanceofTest())
			pass = false;
		printSeperationLine();
		if (!intTest())
			pass = false;
		printSeperationLine();
		if (!interfaceTest())
			pass = false;
		printSeperationLine();
		if (!longTest())
			pass = false;
		printSeperationLine();
		if (!nativeTest())
			pass = false;
		printSeperationLine();
		if (!newTest())
			pass = false;
		printSeperationLine();
		if (!packageTest())
			pass = false;
		printSeperationLine();
		if (!privateTest())
			pass = false;
		printSeperationLine();
		if (!protectedTest())
			pass = false;
		printSeperationLine();
		if (!publicTest())
			pass = false;
		printSeperationLine();
		if (!returnTest())
			pass = false;
		printSeperationLine();
		if (!shortTest())
			pass = false;
		printSeperationLine();
		if (!staticTest())
			pass = false;
		printSeperationLine();
		if (!superTest())
			pass = false;
		printSeperationLine();
		if (!switchTest())
			pass = false;
		printSeperationLine();
		if (!synchronizedTest())
			pass = false;
		printSeperationLine();
		if (!thisTest())
			pass = false;
		printSeperationLine();
		if (!throwTest())
			pass = false;
		printSeperationLine();
		if (!throwsTest())
			pass = false;
		printSeperationLine();
		if (!transientTest())
			pass = false;
		printSeperationLine();
		if (!tryTest())
			pass = false;
		printSeperationLine();
		if (!voidTest())
			pass = false;
		printSeperationLine();
		if (!volatileTest())
			pass = false;
		printSeperationLine();
		if (!whileTest())
			pass = false;
		printSeperationLine();

		if (!pass)
			throw new Exception("JavaKeywordsTest failed");
	}

	/*
	 * @testName: JavaNamingConventionsTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2057; JAXWS:SPEC:2066; JAXWS:SPEC:2067;
	 * JAXWS:SPEC:2068;
	 * 
	 * @test_Strategy:
	 */
	@Test
	public void JavaNamingConventionsTest() throws Exception {
		TestUtil.logTrace("JavaNamingConventionsTest");
		boolean pass = true;

		if (!eyeColorTest())
			pass = false;
		printSeperationLine();
		if (!structTest())
			pass = false;
		printSeperationLine();

		if (!pass)
			throw new Exception("JavaNamingConventionsTest failed");
	}

	private boolean printTestStatus(boolean pass, String test) {
		if (pass)
			logger.log(Level.INFO, "" + test + " ... PASSED");
		else
			TestUtil.logErr("" + test + " ... FAILED");

		return pass;
	}

	private boolean nullTest() {
		boolean pass = true;
		try {
			port.nullTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "nullTest");
		return pass;
	}

	private boolean trueTest() {
		boolean pass = true;
		try {
			port.trueTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "trueTest");
		return pass;
	}

	private boolean falseTest() {
		boolean pass = true;
		try {
			port.falseTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "falseTest");
		return pass;
	}

	private boolean abstractTest() {
		boolean pass = true;
		try {
			port.abstractTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "abstractTest");
		return pass;
	}

	private boolean booleanTest() {
		boolean pass = true;
		try {
			port.booleanTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "booleanTest");
		return pass;
	}

	private boolean breakTest() {
		boolean pass = true;
		try {
			port.breakTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "breakTest");
		return pass;
	}

	private boolean byteTest() {
		boolean pass = true;
		try {
			port.byteTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "byteTest");
		return pass;
	}

	private boolean caseTest() {
		boolean pass = true;
		try {
			port.caseTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "caseTest");
		return pass;
	}

	private boolean catchTest() {
		boolean pass = true;
		try {
			port.catchTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "catchTest");
		return pass;
	}

	private boolean charTest() {
		boolean pass = true;
		try {
			port.charTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "charTest");
		return pass;
	}

	private boolean classTest() {
		boolean pass = true;
		try {
			port.classTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "classTest");
		return pass;
	}

	private boolean constTest() {
		boolean pass = true;
		try {
			port.constTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "constTest");
		return pass;
	}

	private boolean continueTest() {
		boolean pass = true;
		try {
			port.continueTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "continueTest");
		return pass;
	}

	private boolean defaultTest() {
		boolean pass = true;
		try {
			port.defaultTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "defaultTest");
		return pass;
	}

	private boolean doTest() {
		boolean pass = true;
		try {
			port.doTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "doTest");
		return pass;
	}

	private boolean doubleTest() {
		boolean pass = true;
		try {
			port.doubleTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "doubleTest");
		return pass;
	}

	private boolean elseTest() {
		boolean pass = true;
		try {
			port.elseTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "elseTest");
		return pass;
	}

	private boolean extendsTest() {
		boolean pass = true;
		try {
			port.extendsTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "extendsTest");
		return pass;
	}

	private boolean finalTest() {
		boolean pass = true;
		try {
			port.finalTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "finalTest");
		return pass;
	}

	private boolean finallyTest() {
		boolean pass = true;
		try {
			port.finallyTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "finallyTest");
		return pass;
	}

	private boolean floatTest() {
		boolean pass = true;
		try {
			port.floatTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "floatTest");
		return pass;
	}

	private boolean forTest() {
		boolean pass = true;
		try {
			port.forTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "forTest");
		return pass;
	}

	private boolean gotoTest() {
		boolean pass = true;
		try {
			port.gotoTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "gotoTest");
		return pass;
	}

	private boolean ifTest() {
		boolean pass = true;
		try {
			port.ifTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "ifTest");
		return pass;
	}

	private boolean implementsTest() {
		boolean pass = true;
		try {
			port.implementsTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "implementsTest");
		return pass;
	}

	private boolean importTest() {
		boolean pass = true;
		try {
			port.importTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "importTest");
		return pass;
	}

	private boolean instanceofTest() {
		boolean pass = true;
		try {
			port.instanceofTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "instanceofTest");
		return pass;
	}

	private boolean intTest() {
		boolean pass = true;
		try {
			port.intTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "intTest");
		return pass;
	}

	private boolean interfaceTest() {
		boolean pass = true;
		try {
			port.interfaceTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "interfaceTest");
		return pass;
	}

	private boolean longTest() {
		boolean pass = true;
		try {
			port.longTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "longTest");
		return pass;
	}

	private boolean nativeTest() {
		boolean pass = true;
		try {
			port.nativeTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "nativeTest");
		return pass;
	}

	private boolean newTest() {
		boolean pass = true;
		try {
			port.newTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "newTest");
		return pass;
	}

	private boolean packageTest() {
		boolean pass = true;
		try {
			port.packageTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "packageTest");
		return pass;
	}

	private boolean privateTest() {
		boolean pass = true;
		try {
			port.privateTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "privateTest");
		return pass;
	}

	private boolean protectedTest() {
		boolean pass = true;
		try {
			port.protectedTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "protectedTest");
		return pass;
	}

	private boolean publicTest() {
		boolean pass = true;
		try {
			port.publicTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "publicTest");
		return pass;
	}

	private boolean returnTest() {
		boolean pass = true;
		try {
			port.returnTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "returnTest");
		return pass;
	}

	private boolean shortTest() {
		boolean pass = true;
		try {
			port.shortTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "shortTest");
		return pass;
	}

	private boolean staticTest() {
		boolean pass = true;
		try {
			port.staticTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "staticTest");
		return pass;
	}

	private boolean superTest() {
		boolean pass = true;
		try {
			port.superTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "superTest");
		return pass;
	}

	private boolean switchTest() {
		boolean pass = true;
		try {
			port.switchTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "switchTest");
		return pass;
	}

	private boolean synchronizedTest() {
		boolean pass = true;
		try {
			port.synchronizedTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "synchronizeTest");
		return pass;
	}

	private boolean thisTest() {
		boolean pass = true;
		try {
			port.thisTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "thisTest");
		return pass;
	}

	private boolean throwTest() {
		boolean pass = true;
		try {
			port.throwTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "throwTest");
		return pass;
	}

	private boolean throwsTest() {
		boolean pass = true;
		try {
			port.throwsTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "throwsTest");
		return pass;
	}

	private boolean transientTest() {
		boolean pass = true;
		try {
			port.transientTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "transientTest");
		return pass;
	}

	private boolean tryTest() {
		boolean pass = true;
		try {
			port.tryTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "tryTest");
		return pass;
	}

	private boolean voidTest() {
		boolean pass = true;
		try {
			port.voidTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "voidTest");
		return pass;
	}

	private boolean volatileTest() {
		boolean pass = true;
		try {
			port.volatileTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "volatileTest");
		return pass;
	}

	private boolean whileTest() {
		boolean pass = true;
		try {
			port.whileTest();
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "whileTest");
		return pass;
	}

	private boolean eyeColorTest() {
		boolean pass = true;
		try {
			EyeColor o = port.echoEyeColor(eyeColor_data);
			logger.log(Level.INFO, "EyeColor=" + o);
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "eyeColorTest");
		return pass;
	}

	private boolean structTest() {
		boolean pass = true;
		try {
			XMLNameMappingTest_Type o = port.echoXMLNameMapping(xmlNameMapping_data);
			logger.log(Level.INFO, "XMLNameMappingTest_Type=" + o);
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "structTest");
		return pass;
	}
}
