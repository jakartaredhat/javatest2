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

package com.sun.ts.tests.jaxws.api.jakarta_xml_ws.Service_Mode;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.util.TestUtil;

import com.sun.ts.tests.jaxws.common.BaseClient;

public class Client extends BaseClient {

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	// Expected Enum Constant Summary
	private final static jakarta.xml.ws.Service.Mode expectedEnums[] = { jakarta.xml.ws.Service.Mode.MESSAGE,
			jakarta.xml.ws.Service.Mode.PAYLOAD, };

	private boolean findEnums(jakarta.xml.ws.Service.Mode[] args) {
		boolean pass = true;
		boolean found;
		for (jakarta.xml.ws.Service.Mode a : args) {
			found = false;
			logger.log(Level.INFO, "Searching expected list of enums for " + a);
			for (jakarta.xml.ws.Service.Mode b : expectedEnums) {
				if (a == b) {
					found = true;
					break;
				}
			}
			if (!found) {
				pass = false;
				TestUtil.logErr("No enum found for " + a);
			} else {
				logger.log(Level.INFO, "Enum found for " + a);
			}
		}
		return pass;
	}

	private void printEnums(jakarta.xml.ws.Service.Mode[] args) {
		logger.log(Level.INFO, "Print Enums");
		logger.log(Level.INFO, "-----------");
		for (jakarta.xml.ws.Service.Mode c : args)
			logger.log(Level.INFO, "" + c);
	}

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
	 * @testName: valuesTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:60; JAXWS:JAVADOC:61;
	 *
	 * @test_Strategy: Verify jakarta.xml.ws.Service.Mode.values() returns array
	 * containing the constants of this enum type.
	 */
	@Test
	public void valuesTest() throws Exception {
		TestUtil.logTrace("valuesTest");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Call jakarta.xml.ws.Service.Mode.values() ...");
			jakarta.xml.ws.Service.Mode[] methods = jakarta.xml.ws.Service.Mode.values();
			printEnums(methods);
			pass = findEnums(methods);
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("valuesTest failed", e);
		}

		if (!pass)
			throw new Exception("valuesTest failed");
	}

	/*
	 * @testName: valueOfTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:59;
	 *
	 * @test_Strategy: Verify jakarta.xml.ws.Service.Mode.valueOf(String name)
	 * returns the enum constant of this type with specified name.
	 */
	@Test
	public void valueOfTest() throws Exception {
		TestUtil.logTrace("valuesTest");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Call jakarta.xml.ws.Service.Mode.valueOf(MESSAGE) ...");
			jakarta.xml.ws.Service.Mode method = jakarta.xml.ws.Service.Mode.valueOf("MESSAGE");
			if (method != jakarta.xml.ws.Service.Mode.MESSAGE) {
				TestUtil.logErr("jakarta.xml.ws.Service.Mode.valueOf(MESSAGE) failed:" + " expected: "
						+ jakarta.xml.ws.Service.Mode.MESSAGE + ", received: " + method);
				pass = false;
			} else {
				logger.log(Level.INFO, "jakarta.xml.ws.Service.Mode.valueOf(MESSAGE) passed");
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("valuesTest failed", e);
		}

		if (!pass)
			throw new Exception("valuesTest failed");
	}
}
