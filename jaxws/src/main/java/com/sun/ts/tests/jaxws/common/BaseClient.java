/*
 * Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.ts.tests.jaxws.common;

import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Properties;

import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.extension.ExtendWith;

import com.sun.ts.lib.porting.TSURL;
import com.sun.ts.lib.util.TestUtil;

@ExtendWith(ArquillianExtension.class)
public abstract class BaseClient {

	private static final Logger logger = (Logger) System.getLogger(BaseClient.class.getName());

	// The webserver defaults
	protected static final String PROTOCOL = "http";

	protected static final String HOSTNAME = "localhost";

	protected static final int PORTNUM = 8000;

	// The webserver host and port property names
	protected static final String WEBSERVERHOSTPROP = "webServerHost";

	protected static final String WEBSERVERPORTPROP = "webServerPort";

	protected static final String MODEPROP = "platform.mode";

	protected static final String UserNameProp = "user";

	protected static final String PasswordProp = "password";

	protected static final String unauthUserNameProp = "authuser";

	protected static final String unauthPasswordProp = "authpassword";

	protected static final String PREFIX = "W2JRL";

	protected static final String ARCHIVE_PREFIX = "WS";

	protected String modeProperty = null; // platform.mode -> (standalone|jakartaEE)

	protected String STANDALONE = "standalone";

	protected String unauthUsername = "";

	protected String unauthPassword = "";

	protected TSURL ctsurl = new TSURL();

	protected Properties props = new Properties();

	protected String hostname = HOSTNAME;

	protected int portnum = PORTNUM;

	// The webserver username and password property names
	protected static final String USERNAME = "user";

	protected static final String PASSWORD = "password";

	protected String username = null;

	protected String password = null;

	protected static final String ENDPOINTPUBLISHPROP = "http.server.supports.endpoint.publish";

	protected static final String MINPORT = "port.range.min";

	protected int minPort = -1;

	protected static final String MAXPORT = "port.range.max";

	protected static final String HARNESSLOGPORT = null;

	protected static final String TRACEFLAG = null;

	protected static final String HARNESSHOST = null;

	protected int maxPort = -1;

	protected int javaseServerPort;

	protected String harnessHost = "";

	protected String harnessLogPort = "";

	protected String harnessLogTraceFlag = "";

	private Object theSharedObject;

	/*
	 * Set shared object
	 */
	public void setSharedObject(Object o) {
		theSharedObject = o;
	}

	/*
	 * Get shared object
	 */
	public Object getSharedObject() {
		return theSharedObject;
	}

	protected static void addFilesToArchive(String contentRoot, String[] fileNames, WebArchive archive)
			throws IOException {
		for (String fileName : fileNames) {
			InputStream inStream = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(contentRoot + fileName);
			ByteArrayAsset attach = new ByteArrayAsset(inStream);
			archive.add(attach, fileName);
		}
	}

	protected static void addWSIPackages(WebArchive archive) {
		archive.addPackages(false, "com.sun.ts.tests.jaxws.common");
		archive.addPackages(false, "com.sun.ts.tests.jaxws.wsi.constants");
		archive.addPackages(false, "com.sun.ts.tests.jaxws.wsi.jsw");
		archive.addPackages(false, "com.sun.ts.tests.jaxws.wsi.requests");
		archive.addPackages(false, "com.sun.ts.tests.jaxws.wsi.utils");
		archive.addPackages(false, "com.sun.ts.tests.jaxws.wsi.w2j");
	}

	protected static void addWSAPackages(WebArchive archive) {
		archive.addPackages(false, "com.sun.ts.tests.jaxws.wsa.common");
	}

	protected void setup() throws Exception {
		boolean pass = true;
		hostname = System.getProperty(WEBSERVERHOSTPROP);
		pass = !(hostname == null || hostname.equals(""));
		portnum = Integer.parseInt(System.getProperty(WEBSERVERPORTPROP));
		if (!pass) {
			TestUtil.logErr("Please specify host & port of web server " + "in config properties: " + WEBSERVERHOSTPROP
					+ ", " + WEBSERVERPORTPROP);
			throw new Exception("setup failed:");
		}
		username = System.getProperty(USERNAME);
		password = System.getProperty(PASSWORD);
		logger.log(Level.INFO, "Creating stub instance ...");
		modeProperty = System.getProperty(MODEPROP);

		if (STANDALONE.equals(modeProperty)) {
			getTestURLs();
			getPortStandalone();
		} else {
			logger.log(Level.INFO, "WebServiceRef is not set in Client (get it from specific vehicle)");
			getService();
			getTestURLs();
			getPortJavaEE();
		}

		try {
			harnessHost = System.getProperty(HARNESSHOST);
		} catch (Exception e) {
			harnessHost = null;
		}
		try {
			harnessLogPort = System.getProperty(HARNESSLOGPORT);
		} catch (Exception e) {
			harnessLogPort = null;
		}
		try {
			harnessLogTraceFlag = System.getProperty(TRACEFLAG);
		} catch (Exception e) {
			harnessLogTraceFlag = "false";
		}

		logger.log(Level.INFO, "setup ok");

	}

	protected static WebArchive createWebArchive(Class clientClass, String[] archiveFiles) {
		WebArchive archive = ShrinkWrap.create(WebArchive.class,
				ARCHIVE_PREFIX + PREFIX + clientClass.getPackageName() + "TestService_web.war");
		archive.addPackages(true, Filters.exclude(clientClass), clientClass.getPackageName());
		addWSIPackages(archive);
		addWSAPackages(archive);
		archive.addAsWebInfResource(clientClass.getPackage(), "standalone.web.xml", "web.xml");
		archive.addAsWebInfResource(clientClass.getPackage(),
				PREFIX + clientClass.getPackageName() + "TestService.wsdl",
				"wsdl/" + PREFIX + clientClass.getPackageName() + "TestService.wsdl");
		archive.addAsWebInfResource(clientClass.getPackage(), "standalone-sun-jaxws.xml", "sun-jaxws.xml");
		for (String fileName : archiveFiles) {
			archive.addAsWebInfResource(clientClass.getPackage(), fileName, fileName);
		}
		return archive;
	}

	protected static WebArchive createWebArchive(Class clientClass) {
		return createWebArchive(clientClass, new String[0]);
	}

	protected void getTestURLs() throws Exception {
	}

	protected void getPortStandalone() throws Exception {
	}

	protected void getPortJavaEE() throws Exception {
	}

	protected void getService() throws Exception {
	}

	protected boolean setupPorts() {
		boolean result = true;
		TestUtil.logTrace("entering setupPorts");
		try {
			if (modeProperty.equals("standalone")) {
				getPortStandalone();
			} else {
				getPortJavaEE();
			}
		} catch (Exception e) {
			TestUtil.logErr("Could not setup stubs properly");
			TestUtil.printStackTrace(e);
			result = false;
		}
		TestUtil.logTrace("leaving setupPorts");
		return result;

	}

}
