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

package com.sun.ts.tests.jta.ee.usertransaction.begin;

import java.io.IOException;
import java.io.Serializable;
// General Java Package Imports
import java.util.Properties;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Common Utilities
import com.sun.ts.tests.jta.ee.common.Transact;

import jakarta.transaction.NotSupportedException;
import jakarta.transaction.Status;
import jakarta.transaction.SystemException;
// Test Specific Imports.
import jakarta.transaction.UserTransaction;

/**
 * The UserBeginClient class tests begin() method of UserTransaction interface
 * using Sun's J2EE Reference Implementation.
 * 
 */

public class UserBeginClientIT implements Serializable {

	private static final Logger logger = LoggerFactory.getLogger(UserBeginClientIT.class.getName());

	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {

		WebArchive archive = ShrinkWrap.create(WebArchive.class, ".war");
		archive.addClasses();
		archive.addClasses();

		return archive;
	}

	private static final String testName = "jta.ee.usertransaction.begin";

	private UserTransaction userTransaction = null;

	public void setup(String[] args, Properties p) throws Exception {
		try {
			// Initializes the Environment
			Transact.init();
			logger.trace("Test environment initialized");

			// Gets the User Transaction
			userTransaction = (UserTransaction) Transact.nctx.lookup("java:comp/UserTransaction");

			if (userTransaction == null) {
				logger.error("Unable to get User Transaction" + " Instance : Could not proceed with" + " tests");
				throw new Exception("couldnt proceed further");
			} else if (userTransaction.getStatus() == Status.STATUS_ACTIVE) {
				userTransaction.rollback();
			}
		} catch (Exception exception) {
			logger.error("Setup Failed!");
			logger.trace("Unable to get User Transaction Instance :" + " Could not proceed with tests");
			throw new Exception("Setup Failed", exception);
		}

	}// End of setup

	// Beginning of TestCases

	/**
	 * @testName: testUserBegin001
	 * @assertion_ids: JTA:JAVADOC:21
	 * @test_Strategy: Check the status of UserTransaction before calling begin()
	 *                 and check the status of UserTransaction after calling begin()
	 */
	@Test
	public void testUserBegin001() throws Exception {
		// TestCase id :- 4.1.1

		try {
			// Gets the Status of the UserTransaction before
			// calling begin()
			int beforeBegin = userTransaction.getStatus();

			if (beforeBegin == Status.STATUS_NO_TRANSACTION) {
				logger.debug("The Status of the UserTransaction is" + " STATUS_NO_TRANSACTION");
			} else {
				throw new Exception("Failed to return the status" + " STATUS_NO_TRANSACTION");
			}

			// Starts a Global Transaction & associates with
			// Current Thread.
			userTransaction.begin();
			logger.debug("UserTransaction Started");

			// Gets the Status of the UserTransaction after
			// calling begin()
			int afterBegin = userTransaction.getStatus();

			if (afterBegin == Status.STATUS_ACTIVE) {
				logger.debug("The Status of the UserTransaction is" + " STATUS_ACTIVE");
			} else {
				throw new Exception("Failed to return the status" + " STATUS_ACTIVE");
			}
		} catch (IllegalStateException illegalState) {
			logger.error("Exception " + illegalState.toString() + " was caught");
			throw new Exception("UnExpected Exception was caught:" + " Failed", illegalState);
		} catch (SystemException system) {
			logger.error("Exception " + system.toString() + " was caught");
			throw new Exception("UnExpected Exception was caught:" + " Failed", system);
		} catch (Exception exception) {
			logger.error("Exception " + exception.toString() + " was caught");
			throw new Exception("UnExpected Exception was caught:" + " Failed", exception);
		}

	}// End of testUserBegin001

	/**
	 * @testName: testUserBegin002
	 * @assertion_ids: JTA:JAVADOC:22
	 * @test_Strategy: Start the User Transaction and again call the begin() method
	 *                 on User Transaction
	 */
	@Test
	public void testUserBegin002() throws Exception {
		// TestCase id :- 4.1.2

		try {
			// Starts a Global Transaction & associates with
			// Current Thread.
			userTransaction.begin();
			logger.debug("UserTransaction Started");

			logger.debug("Trying to start UserTransaction again");
			// Begins the UserTransaction again
			userTransaction.begin();// should throw
			// NotSupportedException

			throw new Exception("NotSupportedException was not thrown" + " as Expected");
		} catch (NotSupportedException notSupported) {
			logger.debug("Release Doesn't Support NESTED TRANSACTIONS");
			logger.debug("NotSupportedException was caught as" + " Expected !!");
		} catch (SystemException system) {
			logger.error("Exception " + system.toString() + " was caught");
			throw new Exception("NotSupportedException was not thrown" + " as Expected", system);
		} catch (Exception exception) {
			logger.error("Exception " + exception.toString() + " was caught");
			throw new Exception("NotSupportedException was not thrown" + " as Expected", exception);
		}

	}// End of testtestUserBegin002

	public void cleanup() throws Exception {
		try {
			// Frees Current Thread, from Transaction
			Transact.free();
			try {
				userTransaction.rollback();
			} catch (Exception exception) {
				throw new Exception(exception.getCause());
			}
			int retries = 1;
			while ((userTransaction.getStatus() != Status.STATUS_NO_TRANSACTION) && (retries <= 5)) {
				logger.debug("cleanup(): retry # " + retries);
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					throw new Exception(e.getCause());
				}
				retries++;
			}
			logger.debug("Cleanup ok;");
		} catch (Exception exception) {
			logger.error("Cleanup Failed", exception);
			logger.debug("Could not clean the environment");
		}

	}// End of cleanup

}// End of UserBeginClient
