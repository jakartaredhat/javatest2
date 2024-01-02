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
package com.sun.ts.tests.jaxws.api.jakarta_xml_ws_soap.AddressingFeature;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.util.TestUtil;

import jakarta.xml.ws.soap.AddressingFeature;

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
		super.setup();
		logger.log(Level.INFO, "setup ok");
	}

	@AfterEach
	public void cleanup() throws Exception {
		logger.log(Level.INFO, "cleanup ok");
	}

	/*
	 * @testName: AddressingFeatureConstructorTest1
	 *
	 * @assertion_ids: JAXWS:JAVADOC:163;
	 *
	 * @test_Strategy: Create instance via AddressingFeature() constructor. Verify
	 * AddressingFeature object created successfully.
	 */
	@Test
	public void AddressingFeatureConstructorTest1() throws Exception {
		TestUtil.logTrace("AddressingFeatureConstructorTest1");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Create instance via AddressingFeature() ...");
			AddressingFeature n = new AddressingFeature();
			if (n != null) {
				if (!(n.isEnabled())) {
					TestUtil.logErr("AddressingFeature object created successfully, but Addressing is not enabled");
					pass = false;
				} else {
					logger.log(Level.INFO, "AddressingFeature object created successfully");
				}
			} else {
				TestUtil.logErr("AddressingFeature object not created");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("AddressingFeatureConstructorTest1 failed", e);
		}

		if (!pass) {
			throw new Exception("AddressingFeatureConstructorTest1 failed");
		}
	}

	/*
	 * @testName: AddressingFeatureConstructorTest2
	 *
	 * @assertion_ids: JAXWS:JAVADOC:164;
	 *
	 * @test_Strategy: Create instance via AddressingFeature(true) constructor.
	 * Verify AddressingFeature object created successfully.
	 */
	@Test
	public void AddressingFeatureConstructorTest2() throws Exception {
		TestUtil.logTrace("AddressingFeatureConstructorTest2");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Create instance via AddressingFeature(true) ...");
			AddressingFeature n = new AddressingFeature(true);
			if (n != null) {
				if (!(n.isEnabled())) {
					TestUtil.logErr("AddressingFeature object created successfully, but Addressing is not enabled");
					pass = false;
				} else {
					logger.log(Level.INFO, "AddressingFeature object created successfully");
				}
			} else {
				TestUtil.logErr("AddressingFeature object not created");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("AddressingFeatureConstructorTest2 failed", e);
		}

		if (!pass) {
			throw new Exception("AddressingFeatureConstructorTest2 failed");
		}
	}

	/*
	 * @testName: AddressingFeatureConstructorTest3
	 *
	 * @assertion_ids: JAXWS:JAVADOC:164;
	 *
	 * @test_Strategy: Create instance via AddressingFeature(false) constructor.
	 * Verify AddressingFeature object created successfully.
	 */
	@Test
	public void AddressingFeatureConstructorTest3() throws Exception {
		TestUtil.logTrace("AddressingFeatureConstructorTest2");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Create instance via AddressingFeature(false) ...");
			AddressingFeature n = new AddressingFeature(false);
			if (n != null) {
				if (!(n.isEnabled())) {
					logger.log(Level.INFO,
							"AddressingFeature object created successfully, Addressing is correctly not enabled");
				} else {
					TestUtil.logErr(
							"AddressingFeature object created successfully, but Addressing is incorrectly enabled.");
					pass = false;
				}
			} else {
				TestUtil.logErr("AddressingFeature object not created");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("AddressingFeatureConstructorTest3 failed", e);
		}

		if (!pass) {
			throw new Exception("AddressingFeatureConstructorTest3 failed");
		}
	}

	/*
	 * @testName: AddressingFeatureConstructorTest4
	 *
	 * @assertion_ids: JAXWS:JAVADOC:165;
	 *
	 * @test_Strategy: Create instance via AddressingFeature(true, true)
	 * constructor. Verify AddressingFeature object created successfully.
	 */
	@Test
	public void AddressingFeatureConstructorTest4() throws Exception {
		TestUtil.logTrace("AddressingFeatureConstructorTest4");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Create instance via AddressingFeature(true, true) ...");
			AddressingFeature n = new AddressingFeature(true, true);
			if (n != null) {
				if (!(n.isEnabled())) {
					TestUtil.logErr("AddressingFeature object created successfully, but Addressing is not enabled");
					pass = false;
				} else if (!(n.isRequired())) {
					TestUtil.logErr(
							"AddressingFeature object created successfully, but isRequired is not set correctly, expected [true], received ["
									+ n.isRequired() + "]");
					pass = false;
				} else {
					logger.log(Level.INFO, "AddressingFeature object created successfully");
				}
			} else {
				TestUtil.logErr("AddressingFeature object not created");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("AddressingFeatureConstructorTest4 failed", e);
		}

		if (!pass) {
			throw new Exception("AddressingFeatureConstructorTest4 failed");
		}
	}

	/*
	 * @testName: AddressingFeatureConstructorTest5
	 *
	 * @assertion_ids: JAXWS:JAVADOC:165;
	 *
	 * @test_Strategy: Create instance via AddressingFeature(true, false)
	 * constructor. Verify AddressingFeature object created successfully.
	 */
	@Test
	public void AddressingFeatureConstructorTest5() throws Exception {
		TestUtil.logTrace("AddressingFeatureConstructorTest5");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Create instance via AddressingFeature(true, false) ...");
			AddressingFeature n = new AddressingFeature(true, false);
			if (n != null) {
				if (!(n.isEnabled())) {
					TestUtil.logErr("AddressingFeature object created successfully, but Addressing is not enabled");
					pass = false;
				} else if (n.isRequired()) {
					TestUtil.logErr(
							"AddressingFeature object created successfully, but isRequired is not set correctly, expected [false], received ["
									+ n.isRequired() + "]");
					pass = false;
				} else {
					logger.log(Level.INFO, "AddressingFeature object created successfully");
				}
			} else {
				TestUtil.logErr("AddressingFeature object not created");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("AddressingFeatureConstructorTest5 failed", e);
		}

		if (!pass) {
			throw new Exception("AddressingFeatureConstructorTest5 failed");
		}
	}

	/*
	 * @testName: AddressingFeatureConstructorTest6
	 *
	 * @assertion_ids: JAXWS:JAVADOC:165;
	 *
	 * @test_Strategy: Create instance via AddressingFeature(false, true)
	 * constructor. Verify AddressingFeature object created successfully.
	 */
	@Test
	public void AddressingFeatureConstructorTest6() throws Exception {
		TestUtil.logTrace("AddressingFeatureConstructorTest6");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Create instance via AddressingFeature(false, true) ...");
			AddressingFeature n = new AddressingFeature(false, true);
			if (n != null) {
				if (n.isEnabled()) {
					TestUtil.logErr(
							"AddressingFeature object created successfully, but Addressing is incorrectly enabled");
					pass = false;
				} else if (!(n.isRequired())) {
					TestUtil.logErr(
							"AddressingFeature object created successfully, but isRequired is not set correctly, expected [true], received ["
									+ n.isRequired() + "]");
					pass = false;
				} else {
					logger.log(Level.INFO, "AddressingFeature object created successfully");
				}
			} else {
				TestUtil.logErr("AddressingFeature object not created");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("AddressingFeatureConstructorTest6 failed", e);
		}

		if (!pass) {
			throw new Exception("AddressingFeatureConstructorTest6 failed");
		}
	}

	/*
	 * @testName: AddressingFeatureConstructorTest7
	 *
	 * @assertion_ids: JAXWS:JAVADOC:165;
	 *
	 * @test_Strategy: Create instance via AddressingFeature(false, false)
	 * constructor. Verify AddressingFeature object created successfully.
	 */
	@Test
	public void AddressingFeatureConstructorTest7() throws Exception {
		TestUtil.logTrace("AddressingFeatureConstructorTest7");
		boolean pass = true;
		try {
			TestUtil.logMsg("Create instance via AddressingFeature(false, false) ...");
			AddressingFeature n = new AddressingFeature(false, false);
			if (n != null) {
				if (n.isEnabled()) {
					TestUtil.logErr(
							"AddressingFeature object created successfully, but Addressing is incorrectly enabled");
					pass = false;
				} else if (n.isRequired()) {
					TestUtil.logErr(
							"AddressingFeature object created successfully, but isRequired is not set correctly, expected [false], received ["
									+ n.isRequired() + "]");
					pass = false;
				} else {
					logger.log(Level.INFO, "AddressingFeature object created successfully");
				}
			} else {
				TestUtil.logErr("AddressingFeature object not created");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("AddressingFeatureConstructorTest7 failed", e);
		}

		if (!pass) {
			throw new Exception("AddressingFeatureConstructorTest7 failed");
		}
	}

	/*
	 * @testName: AddressingFeatureConstructorTest8
	 *
	 * @assertion_ids: JAXWS:JAVADOC:223;
	 *
	 * @test_Strategy: Create instance via AddressingFeature(boolean, boolean,
	 * AddressingFeature.Responses) constructor. Verify AddressingFeature object
	 * created successfully.
	 */
	@Test
	public void AddressingFeatureConstructorTest8() throws Exception {
		TestUtil.logTrace("AddressingFeatureConstructorTest8");
		boolean pass = true;
		try {
			logger.log(Level.INFO,
					"Create instance via AddressingFeature(boolean, boolean, AddressingFeature.Responses) ...");
			AddressingFeature n = new AddressingFeature(true, true, AddressingFeature.Responses.ANONYMOUS);

			if (n != null) {
				if (!n.isEnabled()) {
					TestUtil.logErr(
							"AddressingFeature object created successfully, but Addressing is incorrectly not-enabled");
					pass = false;
				} else if (!n.isRequired()) {
					TestUtil.logErr(
							"AddressingFeature object created successfully, but isRequired is not set correctly, expected [true], received ["
									+ n.isRequired() + "]");
					pass = false;
				} else {
					logger.log(Level.INFO, "AddressingFeature object created successfully");
				}
			} else {
				TestUtil.logErr("AddressingFeature object not created");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("AddressingFeatureConstructorTest8 failed", e);
		}

		if (!pass) {
			throw new Exception("AddressingFeatureConstructorTest8 failed");
		}
	}

	/*
	 * @testName: getIDTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:166; JAXWS:JAVADOC:160;
	 *
	 * @test_Strategy: Test getting the unique identifier for this AddressingFeature
	 * object. Verify value returned is set correctly.
	 */
	@Test
	public void getIDTest() throws Exception {
		TestUtil.logTrace("getIDTest");
		boolean pass = true;

		try {
			logger.log(Level.INFO, "Create instance via AddressingFeature(true) ...");
			AddressingFeature n = new AddressingFeature(true);
			if (n != null) {
				if (!(AddressingFeature.ID.equals(n.getID()))) {
					TestUtil.logErr("AddressingFeature object created with incorrect ID, expected ["
							+ AddressingFeature.ID + "], received [" + n.getID() + "]");
					pass = false;
				} else {
					logger.log(Level.INFO, "AddressingFeature object created successfully with correct ID, expected ["
							+ AddressingFeature.ID + "], received [" + n.getID() + "]");
				}
			} else {
				TestUtil.logErr("AddressingFeature object not created");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("getIDTest failed", e);
		}

		if (!pass) {
			throw new Exception("getIDTest failed");
		}
	}

	/*
	 * @testName: isRequiredTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:166; JAXWS:JAVADOC:167;
	 *
	 * @test_Strategy: Test setting isRequired of AddressingFeature object. Get
	 * value and verify value returned is set correctly.
	 */
	@Test
	public void isRequiredTest() throws Exception {
		TestUtil.logTrace("isRequiredTest");
		boolean pass = true;

		try {
			logger.log(Level.INFO, "Create instance via AddressingFeature(true, true) ...");
			AddressingFeature n = new AddressingFeature(true, true);
			if (n != null) {
				boolean isRequired = n.isRequired();
				if (isRequired == true) {
					// got returned correct value, now try setting it to new value
					n = new AddressingFeature(true, false);
					logger.log(Level.INFO, "AddressingFeature object created with correct isRequired, received ["
							+ n.isRequired() + "] now try setting it to new value [" + Boolean.valueOf(false) + "]");
					isRequired = n.isRequired();
					if (isRequired == false) {
						// get returned correct value
						logger.log(Level.INFO,
								"AddressingFeature object set and retrieved correct isRequired, expected ["
										+ Boolean.valueOf(false) + "], received [" + n.isRequired() + "]");
					} else {
						logger.log(Level.INFO, "AddressingFeature object created with incorrect isRequired, expected ["
								+ Boolean.valueOf(false) + "], received [" + n.isRequired() + "]");
						pass = false;
					}
				} else {
					logger.log(Level.INFO, "AddressingFeature object created with incorrect isRequired, expected ["
							+ Boolean.valueOf(true) + "], received [" + n.isRequired() + "]");
					pass = false;
				}
			} else {
				TestUtil.logErr("AddressingFeature object not created");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("isRequiredTest failed", e);
		}

		if (!pass) {
			throw new Exception("isRequiredTest failed");
		}
	}

	/*
	 * @testName: getResponsesTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:222; JAXWS:JAVADOC:224;
	 *
	 * @test_Strategy: Create instance via AddressingFeature(boolean, boolean,
	 * AddressingFeature.Responses) constructor. Verify AddressingFeature object
	 * created successfully. Verify responses.
	 */
	@Test
	public void getResponsesTest() throws Exception {
		TestUtil.logTrace("getResponsesTest");
		boolean pass = true;
		try {
			logger.log(Level.INFO,
					"Create instance via AddressingFeature(boolean, boolean, AddressingFeature.Responses) ...");
			AddressingFeature n = new AddressingFeature(true, true, AddressingFeature.Responses.ANONYMOUS);

			if (n != null) {
				if (n.getResponses() != AddressingFeature.Responses.ANONYMOUS) {
					TestUtil.logErr("Responses does not match what was set -> AddressingFeature.Responses.ANONYMOUS");
					pass = false;
				} else {
					logger.log(Level.INFO, "Responses matched what was set -> AddressingFeature.Responses.ANONYMOUS");
				}
			} else {
				TestUtil.logErr("AddressingFeature object not created");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("getResponsesTest failed", e);
		}

		if (!pass) {
			throw new Exception("getResponsesTest failed");
		}
	}

}
