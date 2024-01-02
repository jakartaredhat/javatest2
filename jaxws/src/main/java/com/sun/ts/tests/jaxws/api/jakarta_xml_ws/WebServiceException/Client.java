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

package com.sun.ts.tests.jaxws.api.jakarta_xml_ws.WebServiceException;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.util.TestUtil;

import jakarta.xml.ws.WebServiceException;

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
	 * @testName: WebServiceExceptionConstructorTest1
	 *
	 * @assertion_ids: JAXWS:JAVADOC:72;
	 *
	 * @test_Strategy: Create instance via WebServiceException() constructor. Verify
	 * WebServiceException object created successfully.
	 */
	@Test
	public void WebServiceExceptionConstructorTest1() throws Exception {
		TestUtil.logTrace("WebServiceExceptionConstructorTest1");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Create instance via WebServiceException() ...");
			WebServiceException e = new WebServiceException();
			if (e != null) {
				logger.log(Level.INFO, "WebServiceException object created successfully");
			} else {
				TestUtil.logErr("WebServiceException object not created");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("WebServiceExceptionConstructorTest1 failed", e);
		}

		if (!pass)
			throw new Exception("WebServiceExceptionConstructorTest1 failed");
	}

	/*
	 * @testName: WebServiceExceptionConstructorTest2
	 *
	 * @assertion_ids: JAXWS:JAVADOC:74;
	 *
	 * @test_Strategy: Create instance via WebServiceException(String, Throwable).
	 * Verify WebServiceException object created successfully.
	 */
	@Test
	public void WebServiceExceptionConstructorTest2() throws Exception {
		TestUtil.logTrace("WebServiceExceptionConstructorTest2");
		boolean pass = true;
		String detailMsg = "a detail message";
		Exception foo = new Exception("foo");
		try {
			logger.log(Level.INFO, "Create instance via " + " WebServiceException(String, Throwable) ...");
			WebServiceException e = new WebServiceException(detailMsg, foo);
			if (e != null) {
				logger.log(Level.INFO, "WebServiceException object created successfully");
				String msg = e.getMessage();
				if (msg.equals(detailMsg))
					logger.log(Level.INFO, "detail message match: " + detailMsg);
				else {
					TestUtil.logErr("detail message mismatch - expected: " + detailMsg + ", received: " + msg);
					pass = false;
				}
			} else {
				TestUtil.logErr("WebServiceException object not created");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("WebServiceExceptionConstructorTest2 failed", e);
		}

		if (!pass)
			throw new Exception("WebServiceExceptionConstructorTest2 failed");
	}

	/*
	 * @testName: WebServiceExceptionConstructorTest3
	 *
	 * @assertion_ids: JAXWS:JAVADOC:73;
	 *
	 * @test_Strategy: Create instance via WebServiceException(String). Verify
	 * WebServiceException object created successfully.
	 */
	@Test
	public void WebServiceExceptionConstructorTest3() throws Exception {
		TestUtil.logTrace("WebServiceExceptionConstructorTest3");
		boolean pass = true;
		String detailMsg = "a detail message";
		try {
			TestUtil.logMsg("Create instance via " + " WebServiceException(String) ...");
			WebServiceException e = new WebServiceException(detailMsg);
			if (e != null) {
				logger.log(Level.INFO, "WebServiceException object created successfully");
				String msg = e.getMessage();
				if (msg.equals(detailMsg))
					logger.log(Level.INFO, "detail message match: " + detailMsg);
				else {
					TestUtil.logErr("detail message mismatch - expected: " + detailMsg + ", received: " + msg);
					pass = false;
				}
			} else {
				TestUtil.logErr("WebServiceException object not created");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("WebServiceExceptionConstructorTest3 failed", e);
		}

		if (!pass)
			throw new Exception("WebServiceExceptionConstructorTest3 failed");
	}

	/*
	 * @testName: WebServiceExceptionConstructorTest4
	 *
	 * @assertion_ids: JAXWS:JAVADOC:75;
	 *
	 * @test_Strategy: Create instance via WebServiceException(Throwable). Verify
	 * WebServiceException object created successfully.
	 */
	@Test
	public void WebServiceExceptionConstructorTest4() throws Exception {
		TestUtil.logTrace("WebServiceExceptionConstructorTest4");
		boolean pass = true;
		Exception foo = new Exception("foo");
		try {
			logger.log(Level.INFO, "Create instance via " + " WebServiceException(Throwable) ...");
			WebServiceException e = new WebServiceException(foo);
			if (e != null) {
				logger.log(Level.INFO, "WebServiceException object created successfully");
			} else {
				TestUtil.logErr("WebServiceException object not created");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("WebServiceExceptionConstructorTest4 failed", e);
		}

		if (!pass)
			throw new Exception("WebServiceExceptionConstructorTest4 failed");
	}

}
