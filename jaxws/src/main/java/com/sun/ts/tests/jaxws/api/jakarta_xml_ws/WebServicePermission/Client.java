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

package com.sun.ts.tests.jaxws.api.jakarta_xml_ws.WebServicePermission;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.util.TestUtil;

import jakarta.xml.ws.WebServicePermission;
import com.sun.ts.tests.jaxws.common.BaseClient;

public class Client extends BaseClient {

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

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
	 * @testName: WebServicePermissionConstructorTest1
	 *
	 * @assertion_ids: JAXWS:JAVADOC:76;
	 *
	 * @test_Strategy: Create instance via WebServicePermission(String) constructor.
	 * Verify WebServicePermission object created successfully.
	 */
	@Test
	public void WebServicePermissionConstructorTest1() throws Exception {
		TestUtil.logTrace("WebServicePermissionConstructorTest1");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Create instance via WebServicePermission(String) ...");
			WebServicePermission e = new WebServicePermission("thename");
			if (e != null) {
				logger.log(Level.INFO, "WebServicePermission object created successfully");
			} else {
				TestUtil.logErr("WebServicePermission object not created");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("WebServicePermissionConstructorTest1 failed", e);
		}

		if (!pass)
			throw new Exception("WebServicePermissionConstructorTest1 failed");
	}

	/*
	 * @testName: WebServicePermissionConstructorTest2
	 *
	 * @assertion_ids: JAXWS:JAVADOC:77;
	 *
	 * @test_Strategy: Create instance via WebServicePermission(String, String)
	 * constructor. Verify WebServicePermission object created successfully.
	 */
	@Test
	public void WebServicePermissionConstructorTest2() throws Exception {
		TestUtil.logTrace("WebServicePermissionConstructorTest2");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Create instance via WebServicePermission(String, String) ...");
			WebServicePermission e = new WebServicePermission("thename", null);
			if (e != null) {
				logger.log(Level.INFO, "WebServicePermission object created successfully");
			} else {
				TestUtil.logErr("WebServicePermission object not created");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("WebServicePermissionConstructorTest2 failed", e);
		}

		if (!pass)
			throw new Exception("WebServicePermissionConstructorTest2 failed");
	}

	/*
	 * @testName: WebServicePermissionConstructorTest2a
	 *
	 * @assertion_ids: JAXWS:JAVADOC:77;
	 *
	 * @test_Strategy: Create instance via WebServicePermission(String, String)
	 * constructor. Verify WebServicePermission object created successfully.
	 */
	@Test
	public void WebServicePermissionConstructorTest2a() throws Exception {
		TestUtil.logTrace("WebServicePermissionConstructorTest2a");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Create instance via WebServicePermission(String, String) ...");
			WebServicePermission e = new WebServicePermission("thename", "someaction");
			if (e != null) {
				logger.log(Level.INFO, "WebServicePermission object created successfully");
			} else {
				TestUtil.logErr("WebServicePermission object not created");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("WebServicePermissionConstructorTest2a failed", e);
		}

		if (!pass)
			throw new Exception("WebServicePermissionConstructorTest2a failed");
	}

}
