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
 * @(#)UserSetRollbackOnlyClient.java	1.19 03/05/16
 */

package com.sun.ts.tests.jta.ee.usertransaction.setrollbackonly;

import java.io.Serializable;
// General Java Package Imports
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Common Utilities
import com.sun.ts.tests.jta.ee.common.Transact;

import jakarta.transaction.Status;
import jakarta.transaction.SystemException;
// Test Specific Imports.
import jakarta.transaction.UserTransaction;

/**
 * The UserSetRollbackOnlyClient class tests setRollbackOnly() method of
 * UserTransaction interface using Sun's J2EE Reference Implementation.
 * 
 */

public class UserSetRollbackOnlyClientIT implements Serializable {

	private static final Logger logger = LoggerFactory.getLogger(UserSetRollbackOnlyClientIT.class.getName());

	private static final int SLEEPTIME = 2000;

	private static final String testName = "jta.ee.usertransaction.setrollbackonly";

	private UserTransaction userTransaction = null;

	public void setup() throws Exception {
		try {
			// Initializes the Environment
			Transact.init();
			logger.trace("Test environment initialized");

			// Gets the User Transaction
			userTransaction = (UserTransaction) Transact.nctx.lookup("java:comp/UserTransaction");
			logger.info("User Transaction object is Obtained");

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
	 * @testName: testUserSetRollbackOnly001
	 * @assertion_ids: JTA:JAVADOC:36
	 * @test_Strategy: Without starting the User Transaction call setRollbackOnly()
	 *                 on User Transaction.
	 */
	@Test
	public void testUserSetRollbackOnly001() throws Exception {
		// TestCase id :- 4.5.1

		try {
			// Checks the Status of transaction associated with
			// the current thread.
			logger.info("Getting the status of transaction");

			if (userTransaction.getStatus() == Status.STATUS_NO_TRANSACTION) {
				logger.info("UserTransaction Status is" + " STATUS_NO_TRANSACTION");
				logger.info("Trying to set the transaction for" + " Rollback operation");
				// Trying Mark the transaction for rollback
				// only.
				userTransaction.setRollbackOnly();// should
				// throw IllegalStateException
				throw new Exception("IllegalStateException not" + " thrown as Expected");
			} else {
				throw new Exception("Failed to return the status" + " STATUS_NO_TRANSACTION");
			}
		} catch (IllegalStateException illegalState) {
			logger.info("IllegalStateException was caught as" + " expected!!");
		} catch (SystemException system) {
			logger.error("Exception " + system.toString() + " was caught");
			throw new Exception("IllegalStateException not thrown as" + " Expected", system);
		} catch (Exception exception) {
			logger.error("Exception " + exception.toString() + " was caught");
			throw new Exception("IllegalStateException not thrown as" + " Expected", exception);
		}

	}// End of testUserSetRollbackOnly001

	/**
	 * @testName: testUserSetRollbackOnly002
	 * @assertion_ids: JTA:JAVADOC:35
	 * @test_Strategy: Start the UserTransaction.Call setRollbackOnly() on User
	 *                 Transaction to mark the transaction for rollback only.Then
	 *                 check the status of User Transaction.
	 */
	@Test
	public void testUserSetRollbackOnly002() throws Exception {
		// TestCase id :- 4.5.2

		try {
			// Starts a Global Transaction & associates with
			// Current Thread.
			userTransaction.begin();
			logger.info("UserTransaction Started");

			// Checks the Status of transaction associated with
			// the current thread.
			if (userTransaction.getStatus() == Status.STATUS_ACTIVE) {
				logger.info("UserTransaction Status is" + " STATUS_ACTIVE");
				// Sets the transacion for Rollback operation.
				userTransaction.setRollbackOnly();
			}

			// Checks the Status of transaction associated with
			// the current thread.
			int status = userTransaction.getStatus();

			// If unknown, try again
			if (status == Status.STATUS_UNKNOWN) {
				int count = 0;
				do {
					logger.trace("Received STATUS_UNKNOWN." + " Checking status again.");
					count++;
					try {
						Thread.sleep(SLEEPTIME);
					} catch (Exception e) {
						throw new Exception(e.getCause());
					}
					status = userTransaction.getStatus();
				} while ((status == Status.STATUS_UNKNOWN) && (count < 5));
			}
			if (status == Status.STATUS_MARKED_ROLLBACK) {
				logger.info("UserTransaction set for Rollback" + " operation");
				logger.info("UserTransaction Status is" + " STATUS_MARKED_ROLLBACK");
			}

			// other acceptable status conditions
			else if (status == Status.STATUS_ROLLING_BACK || status == Status.STATUS_ROLLEDBACK
					|| status == Status.STATUS_NO_TRANSACTION) {
				logger.info("UserTransaction marked for Rollback");
				logger.info("UserTransaction is rolling/rolled" + " back");
			}

			else {
				throw new Exception("Failed to return the status" + " STATUS_MARKED_ROLLBACK");
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

	}// End of testUserSetRollbackOnly002

	/**
	 * @testName: testUserSetRollbackOnly003
	 * @assertion_ids: JTA:JAVADOC:36
	 * @test_Strategy: Start the User Transaction.Call commit() on User
	 *                 Transaction.Check the status of the User Transaction.Call
	 *                 setRollbackOnly() on User Transaction.
	 */
	@Test
	public void testUserSetRollbackOnly003() throws Exception {
		// TestCase id :- 4.5.3

		try {
			// Starts a Global Transaction & associates with
			// Current Thread.
			userTransaction.begin();
			logger.info("UserTransaction Started");

			// Checks the Status of transaction associated with
			// the current thread.
			if (userTransaction.getStatus() == Status.STATUS_ACTIVE) {
				logger.info("UserTransaction Status is" + " STATUS_ACTIVE");
				// Commits the transaction.
				userTransaction.commit();
			}

			// Checks the status of transaction associated with
			// the current thread.
			if (userTransaction.getStatus() == Status.STATUS_NO_TRANSACTION) {
				logger.info("UserTransaction Committed");
				logger.info("UserTransaction Status is" + " STATUS_NO_TRANSACTION");
				logger.info("Trying to set the transaction for" + " Rollback operation");

				// Trying to set the transaciton for Rollback
				// only
				userTransaction.setRollbackOnly();// should
				// throw IllegalStateException
				throw new Exception("IllegalStateException not" + " thrown as Expected");
			} else {
				throw new Exception("Failed to return the status" + " STATUS_NO_TRANSACTION");
			}
		} catch (IllegalStateException illegalState) {
			logger.info("IllegalStateException was caught as" + " expected!!");
		} catch (SystemException system) {
			logger.error("Exception " + system.toString() + " was caught");
			throw new Exception("IllegalStateException not thrown as" + " Expected", system);
		} catch (Exception exception) {
			logger.error("Exception " + exception.toString() + " was caught");
			throw new Exception("IllegalStateException not thrown as" + " Expected", exception);
		}

	}// End of testUserSetRollbackOnly003

	/**
	 * @testName: testUserSetRollbackOnly004
	 * @assertion_ids: JTA:JAVADOC:36
	 * @test_Strategy: Start the User Transaction.Call rollback() on User
	 *                 Transaction.Check the status of the User Transaction.Call
	 *                 setRollbackOnly() on User Transaction.
	 */
	@Test
	public void testUserSetRollbackOnly004() throws Exception {
		// TestCase id :- 4.5.4

		try {
			// Starts a Global Transaction & associates with
			// Current Thread.
			userTransaction.begin();
			logger.info("UserTransaction Started");

			// Checks the Status of transaction associated with
			// the current thread.
			if (userTransaction.getStatus() == Status.STATUS_ACTIVE) {
				logger.info("UserTransaction Status is" + " STATUS_ACTIVE");

				// Rolls back the transaction
				userTransaction.rollback();
			}

			// Checks the status of transaction associated with
			// the current thread.
			if (userTransaction.getStatus() == Status.STATUS_NO_TRANSACTION) {
				logger.info("UserTransaction Rolled back");
				logger.info("UserTransaction Status is" + " STATUS_NO_TRANSACTION");
				logger.info("Trying to set the transaction for" + "Rollback operation");

				// Trying to set the transaciton for Rollback
				userTransaction.setRollbackOnly();// should
				// throw IllegalStateException
				throw new Exception("IllegalStateException not" + " thrown as Expected");
			} else {
				throw new Exception("Failed to return the status" + " STATUS_NO_TRANSACTION");
			}
		} catch (IllegalStateException illegalState) {
			logger.info("IllegalStateException was caught as" + " expected!!");
		} catch (SystemException system) {
			logger.error("Exception " + system.toString() + " was caught");
			throw new Exception("IllegalStateException not thrown as" + " Expected", system);
		} catch (Exception exception) {
			logger.error("Exception " + exception.toString() + " was caught");
			throw new Exception("IllegalStateException not thrown as" + " Expected", exception);
		}

	}// End of testUserSetRollbackOnly004

	public void cleanup() throws Exception {
		try {
			// Removing noisy stack trace.
			if (userTransaction.getStatus() == Status.STATUS_ACTIVE) {
				// Frees Current Thread, from Transaction
				Transact.free();
				try {
					userTransaction.rollback();
				} catch (Exception exception) {
					throw new Exception(exception.getCause());
				}
				int retries = 1;
				while ((userTransaction.getStatus() != Status.STATUS_NO_TRANSACTION) && (retries <= 5)) {
					logger.info("cleanup(): retry # " + retries);
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						throw new Exception(e.getCause());
					}
					retries++;
				}
				logger.info("Cleanup ok;");
			} else {
				logger.info("CleanUp not required as Transaction is not in Active state.");
			}
		} catch (Exception exception) {
			logger.error("Cleanup Failed", exception);
			logger.trace("Could not clean the environment");
		}

	}// End of cleanup

}// End of UserSetRollbackOnlyClient
