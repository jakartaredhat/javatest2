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

package com.sun.ts.tests.jaxws.api.jakarta_xml_ws.Holder;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.util.TestUtil;

import jakarta.xml.ws.Holder;

import com.sun.ts.tests.jaxws.common.BaseClient;

public class Client extends BaseClient {

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	private final static Byte myByte = Byte.valueOf(Byte.MAX_VALUE);

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
	 * @testName: HolderConstructorTest1
	 *
	 * @assertion_ids: JAXWS:JAVADOC:28;
	 *
	 * @test_Strategy: Create instance via Holder() constructor. Verify Holder
	 * object created successfully.
	 */
	@Test
	public void HolderConstructorTest1() throws Exception {
		TestUtil.logTrace("HolderConstructorTest1");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Create instance via Holder() ...");
			Holder n = new Holder();
			if (n != null) {
				logger.log(Level.INFO, "Holder object created successfully");
			} else {
				TestUtil.logErr("Holder object not created");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("HolderConstructorTest1 failed", e);
		}

		if (!pass)
			throw new Exception("HolderConstructorTest1 failed");
	}

	/*
	 * @testName: HolderConstructorTest2
	 *
	 * @assertion_ids: JAXWS:JAVADOC:29;
	 *
	 * @test_Strategy: Create instance via Holder(byte) constructor. Verify Holder
	 * object created successfully.
	 */
	@Test
	public void HolderConstructorTest2() throws Exception {
		TestUtil.logTrace("HolderConstructorTest2");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Create instance via Holder(byte) ...");
			Holder n = new Holder(myByte);
			if (n != null) {
				logger.log(Level.INFO, "Holder object created successfully");
			} else {
				TestUtil.logErr("Holder object not created");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("HolderConstructorTest2 failed", e);
		}

		if (!pass)
			throw new Exception("HolderConstructorTest2 failed");
	}

	/*
	 * @testName: getValueTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:28; JAXWS:JAVADOC:29;
	 *
	 * @test_Strategy: Test using both constructors. Verify value is set correct in
	 * each case.
	 */
	@Test
	public void getValueTest() throws Exception {
		TestUtil.logTrace("getValueTest");
		boolean pass = true;

		if (!getValueTest1())
			pass = false;
		if (!getValueTest2())
			pass = false;

		if (!pass)
			throw new Exception("getValueTest failed");
	}

	/*
	 * Create instance via Holder(). Verify value is set to default.
	 */
	private boolean getValueTest1() throws Exception {
		TestUtil.logTrace("getValueTest1");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Create instance via Holder() ...");
			Holder n = new Holder();
			if (n != null) {
				if (n.value == null)
					logger.log(Level.INFO, "value set as expected to null");
				else {
					logger.log(Level.INFO, "value set unexpected to non-null");
					pass = false;
				}
			} else {
				TestUtil.logErr("Holder object not created");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		return pass;
	}

	/*
	 * Create instance via Holder(byte). Verify value is set to default.
	 */
	private boolean getValueTest2() throws Exception {
		TestUtil.logTrace("getValueTest2");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Create instance via Holder(byte) ...");
			Holder n = new Holder(myByte);
			if (n != null) {
				Byte v = (Byte) n.value;
				if (myByte.equals(v))
					logger.log(Level.INFO, "value set as expected: " + myByte);
				else {
					TestUtil.logErr("value: expected - " + myByte + ", received - " + v);
					pass = false;
				}
			} else {
				TestUtil.logErr("Holder object not created");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		return pass;
	}
}
