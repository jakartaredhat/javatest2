/*
 * Copyright (c) 2007, 2023 Oracle and/or its affiliates. All rights reserved.
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
package com.sun.ts.tests.jms.core.appclient.closedTopicConnection;

import java.lang.System.Logger;
import java.util.ArrayList;
import java.util.Properties;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jms.common.JmsTool;

import jakarta.jms.ExceptionListener;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;

/**
 * JMS product tests. Testing method calls on closed TopicConnection objects.
 */
public class ClosedTopicConnectionTests {
	private static final String TestName = "com.sun.ts.tests.jms.core.appclient.closedTopicConnection.ClosedTopicConnectionTests";

	private static final String testDir = System.getProperty("user.dir");

	private static final Logger logger = (Logger) System.getLogger(ClosedTopicConnectionTests.class.getName());

	// JMS objects
	private static JmsTool tool = null;

	// Harness req's
	private Properties props = null;

	// properties read
	long timeout;

	String user;

	String password;

	String mode;

	ArrayList connections = null;

	/* Utility methods for tests */

	/**
	 * Used by tests that need a closed connection for testing. Passes any
	 * exceptions up to caller.
	 * 
	 * @param int The type of session that needs to be created and closed
	 */
	private void createAndCloseConnection(int type, String user, String password) throws Exception {
		if ((type == JmsTool.TOPIC) || (type == JmsTool.TX_TOPIC)) {
			tool = new JmsTool(type, user, password, mode);
			tool.getDefaultTopicConnection().start();

			logger.log(Logger.Level.TRACE, "Closing queue Connection");
			tool.getDefaultTopicConnection().close();
		}
		logger.log(Logger.Level.TRACE, "Connection closed");
	}

	/* Test setup: */

	/*
	 * setup() is called before each test
	 * 
	 * Creates Administrator object and deletes all previous Destinations.
	 * Individual tests create the JmsTool object with one default Topic and/or
	 * Topic Connection, as well as a default Topic and Topic. Tests that require
	 * multiple Destinations create the extras within the test
	 * 
	 * 
	 * @class.setup_props: jms_timeout; user; password; platform.mode;
	 * 
	 * @exception Fault
	 */
	@BeforeEach
	public void setup() throws Exception {
		try {

			// get props
			timeout = Long.parseLong(System.getProperty("jms_timeout"));
			user = System.getProperty("user");
			password = System.getProperty("password");
			mode = System.getProperty("platform.mode");

			// check props for errors
			if (timeout < 1) {
				throw new Exception("'timeout' (milliseconds) must be > 0");
			}
			if (user == null) {
				throw new Exception("'user' must be null");
			}
			if (password == null) {
				throw new Exception("'numProducers' must be null");
			}
			if (mode == null) {
				throw new Exception("'mode' must be null");
			}

			// get ready for new test
			logger.log(Logger.Level.TRACE, "Getting Administrator and deleting any leftover destinations.");
		} catch (Exception e) {
			TestUtil.printStackTrace(e);
			throw new Exception("Setup failed!", e);
		}
	}

	/* cleanup */

	/*
	 * cleanup() is called after each test
	 * 
	 * Closes the default connections that are created by setup(). Any separate
	 * connections made by individual tests should be closed by that test.
	 * 
	 * @exception Fault
	 */
	@AfterEach
	public void cleanup() throws Exception {
		try {
			if (tool != null) {
				logger.log(Logger.Level.TRACE, "Cleanup: Closing Topic and Topic Connections");
				tool.closeAllConnections(connections);
			}
		} catch (Exception e) {
			TestUtil.printStackTrace(e);
			logger.log(Logger.Level.ERROR, "An error occurred while cleaning");
			throw new Exception("Cleanup failed!", e);
		}
	}

	/* Tests */

	/*
	 * @testName: closedTopicConnectionGetExceptionListenerTest
	 *
	 * @assertion_ids: JMS:SPEC:107; JMS:JAVADOC:107; JMS:JAVADOC:526;
	 * JMS:JAVADOC:518;
	 *
	 * @test_Strategy: Close default Connection and call method on it. Check for
	 * IllegalStateException.
	 */
	@Test
	public void closedTopicConnectionGetExceptionListenerTest() throws Exception {
		boolean passed = false;

		try {
			createAndCloseConnection(JmsTool.TOPIC, user, password);
			logger.log(Logger.Level.TRACE, "Try to call getExceptionListener");
			try {
				ExceptionListener foo = tool.getDefaultTopicConnection().getExceptionListener();

				logger.log(Logger.Level.TRACE, "Fail: Exception was not thrown!");
			} catch (jakarta.jms.IllegalStateException ise) {
				logger.log(Logger.Level.TRACE, "Pass: threw expected error");
				passed = true;
			} catch (Exception e) {
				TestUtil.printStackTrace(e);
				logger.log(Logger.Level.TRACE, "Fail: wrong exception: " + e.getClass().getName() + " was returned");
			}
			if (!passed) {
				throw new Exception("Error: failures occurred during tests");
			}
		} catch (Exception e) {
			throw new Exception("closedTopicConnectionGetExceptionListenerTest", e);
		}
	}

	/*
	 * @testName: closedTopicConnectionSetClientIDTest
	 *
	 * @assertion_ids: JMS:SPEC:107; JMS:JAVADOC:107; JMS:JAVADOC:526;
	 * JMS:JAVADOC:514;
	 *
	 * @test_Strategy: Close default Connection and call method on it. Check for
	 * IllegalStateException.
	 */
	@Test
	public void closedTopicConnectionSetClientIDTest() throws Exception {
		boolean passed = false;

		try {
			createAndCloseConnection(JmsTool.TOPIC, user, password);
			logger.log(Logger.Level.TRACE, "Try to call setClientID");
			try {
				tool.getDefaultTopicConnection().setClientID("foo");
				logger.log(Logger.Level.TRACE, "Fail: Exception was not thrown!");
			} catch (jakarta.jms.IllegalStateException ise) {
				logger.log(Logger.Level.TRACE, "Pass: threw expected error");
				passed = true;
			} catch (Exception e) {
				TestUtil.printStackTrace(e);
				logger.log(Logger.Level.TRACE, "Fail: wrong exception: " + e.getClass().getName() + " was returned");
			}
			if (!passed) {
				throw new Exception("Error: failures occurred during tests");
			}
		} catch (Exception e) {
			throw new Exception("closedTopicConnectionSetClientIDTest", e);
		}
	}

	/*
	 * @testName: closedTopicConnectionSetExceptionListenerTest
	 *
	 * @assertion_ids: JMS:SPEC:107; JMS:JAVADOC:107; JMS:JAVADOC:526;
	 * JMS:JAVADOC:520; JMS:JAVADOC:483;
	 *
	 * @test_Strategy: Close default Connection and call method on it. Check for
	 * IllegalStateException.
	 */
	@Test
	public void closedTopicConnectionSetExceptionListenerTest() throws Exception {
		boolean passed = false;

		try {
			createAndCloseConnection(JmsTool.TOPIC, user, password);
			logger.log(Logger.Level.TRACE, "Try to call setExceptionListener");
			try {
				ExceptionListener foo = new ExceptionListener() {

					public void onException(JMSException jmsE) {
					}

				};

				tool.getDefaultTopicConnection().setExceptionListener(foo);
				logger.log(Logger.Level.TRACE, "Fail: Exception was not thrown!");
			} catch (jakarta.jms.IllegalStateException ise) {
				logger.log(Logger.Level.TRACE, "Pass: threw expected error");
				passed = true;
			} catch (Exception e) {
				TestUtil.printStackTrace(e);
				logger.log(Logger.Level.TRACE, "Fail: wrong exception: " + e.getClass().getName() + " was returned");
			}
			if (!passed) {
				throw new Exception("Error: failures occurred during tests");
			}
		} catch (Exception e) {
			throw new Exception("closedTopicConnectionSetExceptionListenerTest", e);
		}
	}

	/*
	 * @testName: closedTopicConnectionGetMessageListenerTest
	 *
	 * @assertion_ids: JMS:SPEC:107; JMS:JAVADOC:107; JMS:JAVADOC:526;
	 * JMS:JAVADOC:328;
	 *
	 * @test_Strategy: Close default subscriber and call method on it. Check for
	 * IllegalStateException.
	 */
	@Test
	public void closedTopicConnectionGetMessageListenerTest() throws Exception {
		boolean passed = false;

		try {
			createAndCloseConnection(JmsTool.TOPIC, user, password);
			logger.log(Logger.Level.TRACE, "Try to call getMessageListener");
			try {
				MessageListener foo = tool.getDefaultTopicSubscriber().getMessageListener();

				logger.log(Logger.Level.TRACE, "Fail: Exception was not thrown!");
			} catch (jakarta.jms.IllegalStateException ise) {
				logger.log(Logger.Level.TRACE, "Pass: threw expected error");
				passed = true;
			} catch (Exception e) {
				TestUtil.printStackTrace(e);
				logger.log(Logger.Level.TRACE, "Fail: wrong exception: " + e.getClass().getName() + " was returned");
			}
			if (!passed) {
				throw new Exception("Error: failures occurred during tests");
			}
		} catch (Exception e) {
			throw new Exception("closedTopicConnectionGetMessageListenerTest", e);
		}
	}

	/*
	 * @testName: closedTopicConnectionSetMessageListenerTest
	 *
	 * @assertion_ids: JMS:SPEC:107; JMS:JAVADOC:107; JMS:JAVADOC:526;
	 * JMS:JAVADOC:330; JMS:JAVADOC:325;
	 *
	 * @test_Strategy: Close default subscriber and call method on it. Check for
	 * IllegalStateException.
	 */
	@Test
	public void closedTopicConnectionSetMessageListenerTest() throws Exception {
		boolean passed = false;

		try {
			createAndCloseConnection(JmsTool.TOPIC, user, password);
			logger.log(Logger.Level.TRACE, "Try to call setMessageListener");
			try {
				MessageListener foo = new MessageListener() {

					public void onMessage(Message m) {
					}

				};

				tool.getDefaultTopicSubscriber().setMessageListener(foo);
				logger.log(Logger.Level.TRACE, "Fail: Exception was not thrown!");
			} catch (jakarta.jms.IllegalStateException ise) {
				logger.log(Logger.Level.TRACE, "Pass: threw expected error");
				passed = true;
			} catch (Exception e) {
				TestUtil.printStackTrace(e);
				logger.log(Logger.Level.TRACE, "Fail: wrong exception: " + e.getClass().getName() + " was returned");
			}
			if (!passed) {
				throw new Exception("Error: failures occurred during tests");
			}
		} catch (Exception e) {
			throw new Exception("closedTopicConnectionSetMessageListenerTest", e);
		}
	}

	/*
	 * @testName: closedTopicConnectionStopTest
	 *
	 * @assertion_ids: JMS:SPEC:107; JMS:JAVADOC:107; JMS:JAVADOC:526;
	 * JMS:JAVADOC:524;
	 * 
	 * @test_Strategy: Close default Connection and call method on it. Check for
	 * IllegalStateException.
	 */
	@Test
	public void closedTopicConnectionStopTest() throws Exception {
		boolean passed = false;

		try {
			createAndCloseConnection(JmsTool.TOPIC, user, password);
			logger.log(Logger.Level.TRACE, "Try to call stop");
			try {
				tool.getDefaultTopicConnection().stop();
				logger.log(Logger.Level.TRACE, "Fail: Exception was not thrown!");
			} catch (jakarta.jms.IllegalStateException ise) {
				logger.log(Logger.Level.TRACE, "Pass: threw expected error");
				passed = true;
			} catch (Exception e) {
				TestUtil.printStackTrace(e);
				logger.log(Logger.Level.TRACE, "Fail: wrong exception: " + e.getClass().getName() + " was returned");
			}
			if (!passed) {
				throw new Exception("Error: failures occurred during tests");
			}
		} catch (Exception e) {
			throw new Exception("closedTopicConnectionStopTest", e);
		}
	}

}
