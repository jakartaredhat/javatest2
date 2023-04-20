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

package com.sun.ts.tests.jta.ee.usertransaction.commit;

import java.io.Serializable;
// General Java Package Imports
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.ts.lib.harness.ServiceEETest;
// Common Utilities
import com.sun.ts.tests.jta.ee.common.Transact;
import com.sun.ts.tests.jta.ee.txpropagationtest.TxBeanEJB;

import jakarta.transaction.HeuristicMixedException;
import jakarta.transaction.HeuristicRollbackException;
import jakarta.transaction.RollbackException;
// Test Specific Imports.
import jakarta.transaction.Status;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;

/**
 * The UserCommitClient class tests commit() method of UserTransaction interface
 * using Sun's J2EE Reference Implementation.
 * 
 */

public class UserCommitClientIT extends ServiceEETest implements Serializable {

	private static final Logger logger = LoggerFactory.getLogger(UserCommitClientIT.class.getName());

	private static final int SLEEPTIME = 2000;

	private static final String testName = "jta.ee.usertransaction.commit";

	private UserTransaction userTransaction = null;

	public void setup(String[] args, Properties p) throws Exception {
		try {
			// Initializes the Environment
			Transact.init();
			logger.trace("Test environment initialized");

			// Gets the User Transaction
			userTransaction = (UserTransaction) Transact.nctx.lookup("java:comp/UserTransaction");
			logger.info("User Transaction is Obtained");

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

	// Beginning of testCases

	/**
	 * @testName: testUserCommit001
	 * @assertion_ids: JTA:JAVADOC:29
	 * @test_Strategy: Without starting the User Transaction Call commit() on User
	 *                 Transaction.
	 */
	@Test
	public void testUserCommit001() throws Exception {
		// TestCase id :- 4.2.1

		try {
			// Checks the Status of the transaction associated with
			// the current thread
			if (userTransaction.getStatus() == Status.STATUS_NO_TRANSACTION) {
				logger.info("UserTransaction Status is" + " STATUS_NO_TRANSACTION");
				logger.info("Trying to Commit on inactive" + " UserTransaction");

				// Commits the UserTransaction.
				userTransaction.commit(); // should
				// throw IllegalStateException
				throw new Exception("IllegalStateException was not" + " thrown as Expected");
			} else {
				throw new Exception("Failed to return the status" + " STATUS_NO_TRANSACTION");
			}
		} catch (IllegalStateException illegalState) {
			logger.info("IllegalStateException was caught as" + " Expected !!");
		} catch (SystemException system) {
			logger.error("Exception " + system.toString() + " was caught");
			throw new Exception("IllegalStateException was not" + " thrown as Expected", system);
		} catch (Exception exception) {
			logger.error("Exception " + exception.toString() + " was caught");
			throw new Exception("IllegalStateException was not" + " thrown as Expected", exception);
		}

	}// End of testUserCommit001

	/**
	 * @testName: testUserCommit002
	 * @assertion_ids: JTA:JAVADOC:24
	 * @test_Strategy: Start the User Transaction.call commit() on User
	 *                 Transaction.Check the status. .
	 */
	@Test
	public void testUserCommit002() throws Exception {
		// TestCase id :- 4.2.2

		try {
			// Starts a Global Transaction & associates with
			// Current Thread.
			userTransaction.begin();
			logger.info("UserTransaction Started");

			// Checks the Status of transaction associated with
			// the current thread
			if (userTransaction.getStatus() == Status.STATUS_ACTIVE) {
				logger.info("UserTransaction Status is" + " STATUS_ACTIVE");
				// Commits the UserTransaction.
				userTransaction.commit();
			}

			// Checks the status of the transaction associated with
			// the current thread after commit
			if (userTransaction.getStatus() == Status.STATUS_NO_TRANSACTION) {
				logger.info("UserTransaction Committed");
				logger.info("UserTransaction Status is" + " STATUS_NO_TRANSACTION");
			} else {
				throw new Exception("Failed to return the status" + " STATUS_NO_TRANSACTION");
			}
		} catch (RollbackException rollback) {
			logger.error("Exception " + rollback.toString() + " was caught");
			throw new Exception("UnExpected Exception was caught:" + " Failed", rollback);
		} catch (HeuristicMixedException heuristicMixed) {
			logger.error("Exception " + heuristicMixed.toString() + " was caught");
			throw new Exception("UnExpected Exception was caught:" + " Failed", heuristicMixed);
		} catch (HeuristicRollbackException heuristicRollback) {
			logger.error("Exception " + heuristicRollback.toString() + " was caught");
			throw new Exception("UnExpected Exception was caught:" + " Failed", heuristicRollback);
		} catch (SecurityException security) {
			logger.error("Exception " + security.toString() + " was caught");
			throw new Exception("UnExpected Exception was caught:" + " Failed", security);
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

	}// End of testUserCommit002

	/**
	 * @testName: testUserCommit003
	 * @assertion_ids: JTA:JAVADOC:29
	 * @test_Strategy: Start the User Transaction.Call commit() on User
	 *                 Transaction.Check status of the User Transaction. Call
	 *                 commit() again on User Transaction.
	 */
	@Test
	public void testUserCommit003() throws Exception {
		// TestCase id :- 4.2.3

		try {
			// Starts a Global Transaction & associates with
			// Current Thread.
			userTransaction.begin();
			logger.info("UserTransaction Started");

			// Checks the Status of transaction associated with
			// the current thread
			if (userTransaction.getStatus() == Status.STATUS_ACTIVE) {
				logger.info("UserTransaction Status is" + " STATUS_ACTIVE");

				// Commits the UserTransaction.
				userTransaction.commit();
			}

			// Check for the status of the transaction
			if (userTransaction.getStatus() == Status.STATUS_NO_TRANSACTION) {
				logger.info("UserTransaction Committed");
				logger.info("UserTransaction Status is" + " STATUS_NO_TRANSACTION");
				logger.info("Trying to Commit on UserTransaction" + " again");

				// Trying to Commit on UserTransaction.
				userTransaction.commit();// should
				// throw IllegalStateException
				throw new Exception("IllegalStateException was not" + " thrown as Expected");
			} else {
				throw new Exception("Failed to return the status" + " STATUS_NO_TRANSACTION");
			}
		} catch (IllegalStateException illegalState) {
			logger.info("IllegalStateException was caught as" + " Expected!!");
		} catch (SystemException system) {
			logger.error("Exception " + system.toString() + " was caught");
			throw new Exception("IllegalStateException was not" + " thrown as Expected", system);
		} catch (Exception exception) {
			logger.error("Exception " + exception.toString() + " was caught");
			throw new Exception("IllegalStateException was not" + " thrown as Expected", exception);
		}

	}// End of testUserCommit003

	/**
	 * @testName: testUserCommit004
	 * @assertion_ids: JTA:JAVADOC:29
	 * @test_Strategy: Start the User Transaction.Call rollback() on User
	 *                 Transaction.Check status of the User Transaction.Call
	 *                 commit() again on User Transaction.
	 */
	@Test
	public void testUserCommit004() throws Exception {
		// TestCase id :- 4.2.4

		try {
			// Starts a Global Transaction & associates with
			// Current Thread.
			userTransaction.begin();
			logger.info("UserTransaction Started");

			// Checks the Status of transaction associated with
			// the current thread
			if (userTransaction.getStatus() == Status.STATUS_ACTIVE) {
				logger.info("UserTransaction Status is" + " STATUS_ACTIVE");

				// Rollbacks the UserTransaction.
				userTransaction.rollback();
			}

			// Checks the Status of transaction associated with
			// the current thread
			if (userTransaction.getStatus() == Status.STATUS_NO_TRANSACTION) {
				logger.info("UserTransaction rolled back");
				logger.info("Trying to Commit on UserTransaction");
				// Trying to Commit on UserTransaction.
				userTransaction.commit();// should throw
				// IllegalStateException
				throw new Exception("IllegalStateException was not" + " thrown as Expected");
			} else {
				throw new Exception("Failed to return the status" + " STATUS_NO_TRANSACTION");
			}
		} catch (IllegalStateException illegalState) {
			logger.info("IllegalStateException was caught as" + " Expected!!");
		} catch (SystemException system) {
			logger.error("Exception " + system.toString() + " was caught");
			throw new Exception("IllegalStateException was not" + " thrown as Expected", system);
		} catch (Exception exception) {
			logger.error("Exception " + exception.toString() + " was caught");
			throw new Exception("IllegalStateException was not" + " thrown as Expected", exception);
		}

	}// End of testUserCommit004

	/**
	 * @testName: testUserCommit005
	 * @assertion_ids: JTA:JAVADOC:25
	 * @test_Strategy: Start the User Transaction.Mark the User Transaction for
	 *                 rollback only by calling setRollbackOnly().Check the
	 *                 status.Call commit() on User Transaction.
	 */
	@Test
	public void testUserCommit005() throws Exception {
		// TestCase id :- 4.2.5

		try {
			// Starts a Global Transaction & associates with
			// Current Thread.
			userTransaction.begin();
			logger.info("UserTransaction Started");

			// Checks the status of transaction associated with
			// the current thread
			if (userTransaction.getStatus() == Status.STATUS_ACTIVE) {
				logger.info("UserTransaction Status is" + " STATUS_ACTIVE");
				userTransaction.setRollbackOnly();
			}

			// Checks the Status of transaction associated with
			// the current thread
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
			if (status == Status.STATUS_MARKED_ROLLBACK || status == Status.STATUS_ROLLING_BACK
					|| status == Status.STATUS_ROLLEDBACK || status == Status.STATUS_NO_TRANSACTION) {
				logger.info("UserTransaction set for Rollback");
				logger.info("Trying to Commit on UserTransaction");
				// Tries to Commit on UserTransaction.
				userTransaction.commit(); // should throw
				// RollbackException
				// or IllegalStateException
				throw new Exception("RollbackException was not" + " thrown as Expected");
			} else {
				throw new Exception("Failed to return a valid" + " status");
			}
		} catch (RollbackException rollback) {
			logger.info("RollbackException was caught as Expected!!");
		} catch (IllegalStateException state) // if rolled back
		{
			logger.info("IllegalStateException was caught. Exception" + " is acceptable.");
		} catch (SystemException system) {
			logger.error("Exception " + system.toString() + " was caught");
			throw new Exception("RollbackException was not" + " thrown as Expected", system);
		} catch (Exception exception) {
			logger.error("Exception " + exception.toString() + " was caught");
			throw new Exception("RollbackException was not" + " thrown as Expected", exception);
		}

	}// End of testUserCommit005

	public void cleanup() throws Exception {
		try {
			// Referring to issue raised
			// (https://github.com/eclipse-ee4j/jakartaee-tck/issues/70)
			// Performing additional check if userTransaction is Active or not.
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

}// End of UserCommitClient
