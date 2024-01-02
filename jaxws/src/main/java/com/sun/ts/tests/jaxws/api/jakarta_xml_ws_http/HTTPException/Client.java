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

package com.sun.ts.tests.jaxws.api.jakarta_xml_ws_http.HTTPException;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaxws.common.BaseClient;

import jakarta.xml.ws.http.HTTPException;

import com.sun.ts.tests.jaxws.common.BaseClient;

public class Client extends BaseClient {

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	private final static int MY_HTTP_STATUS_CODE = 100;

	/* Test setup */

	/*
	 * @class.setup_props:
	 */
	@BeforeEach
	public void setup() throws Exception {
		super.setup();
		logger.log(Level.INFO, "setup ok");
	}

	@AfterEach
	public void cleanup() throws Exception {
		logger.log(Level.INFO, "cleanup ok");
	}

	/*
	 * @testName: HTTPExceptionConstructorTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:105;
	 *
	 * @test_Strategy: Create instance via HTTPException(int statusCode)
	 * constructor. Verify HTTPException object created successfully.
	 */
	@Test
	public void HTTPExceptionConstructorTest() throws Exception {
		TestUtil.logTrace("HTTPExceptionConstructorTest");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Create instance via HTTPException() ...");
			HTTPException e = new HTTPException(MY_HTTP_STATUS_CODE);
			if (e != null) {
				logger.log(Level.INFO, "HTTPException object created successfully");
			} else {
				TestUtil.logErr("HTTPException object not created");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("HTTPExceptionConstructorTest failed", e);
		}

		if (!pass)
			throw new Exception("HTTPExceptionConstructorTest failed");
	}

	/*
	 * @testName: getStatusCodeTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:104;
	 *
	 * @test_Strategy: Create instance via HTTPException(int statusCode)
	 * constructor. Verify HTTPException.getStatusCode() returns expected code.
	 */
	@Test
	public void getStatusCodeTest() throws Exception {
		TestUtil.logTrace("getStatusCodeTest");
		boolean pass = true;
		int code;
		try {
			logger.log(Level.INFO, "Create instance via HTTPException() ...");
			HTTPException e = new HTTPException(MY_HTTP_STATUS_CODE);
			if (e != null) {
				logger.log(Level.INFO, "HTTPException object created successfully");
			} else {
				TestUtil.logErr("HTTPException object not created");
				pass = false;
			}
			code = e.getStatusCode();
			if (code == MY_HTTP_STATUS_CODE)
				logger.log(Level.INFO, "getStatusCode returned expected code " + MY_HTTP_STATUS_CODE);
			else {
				TestUtil.logErr("getStatusCode returned unexpected code, expected " + MY_HTTP_STATUS_CODE
						+ ", received " + code);
			}

		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("getStatusCodeTest failed", e);
		}

		if (!pass)
			throw new Exception("getStatusCodeTest failed");
	}
}
