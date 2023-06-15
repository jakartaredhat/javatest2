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
package com.sun.ts.tests.jms.ee.ejb.queueCMTTests;

import java.lang.System.Logger;
import java.util.Properties;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jms.common.JmsTool;
import com.sun.ts.tests.jms.commonee.Tests;

import jakarta.ejb.EJB;
import jakarta.jms.DeliveryMode;
import jakarta.jms.Queue;

public class Client {

	private static final String testName = "com.sun.ts.tests.jms.ee.ejb.queueCMTTests.Client";

	private static final String testDir = System.getProperty("user.dir");

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	// Harness req's
	private Properties props = null;

	// properties read
	long timeout;

	String user;

	String password;

	String mode;

	@EJB(name = "ejb/QueueTests")
	private static Tests beanRef;

	/*
	 * setup() is called before each test
	 * 
	 * Creates Administrator object and deletes all previous Destinations.
	 * Individual tests create the JmsTool object with one default Queue and/or
	 * Topic Connection, as well as a default Queue and Topic. Tests that require
	 * multiple Destinations create the extras within the test
	 * 
	 * @class.setup_props: jms_timeout; user; password; platform.mode;
	 * 
	 */
	@BeforeEach
	public void setup() throws Exception {
		try {

			if (beanRef == null) {
				throw new Exception("@EJB injection failed");
			}

			// get props
			timeout = Long.parseLong(System.getProperty("jms_timeout"));
			user = System.getProperty("user");
			password = System.getProperty("password");
			mode = System.getProperty("platform.mode");

			// check props for errors
			if (timeout < 1) {
				throw new Exception("'jms_timeout' (milliseconds) in must be > 0");
			}
			if (user == null) {
				throw new Exception("'user' in must not be null ");
			}
			if (password == null) {
				throw new Exception("'password' in must not be null ");
			}
			if (mode == null) {
				throw new Exception("'platform.mode' in must not be null");
			}

			beanRef.initLogging(props);
		} catch (Exception e) {
			TestUtil.printStackTrace(e);
			throw new Exception("Setup failed!", e);
		}
	}

	/*
	 * cleanup() is called after each test
	 */
	@AfterEach
	public void cleanup() throws Exception {
	}

	private void flushTheQueue() throws Exception {
		JmsTool tool = null;
		try {
			tool = new JmsTool(JmsTool.QUEUE, user, password, mode);

		} catch (Exception e) {
			logger.log(Logger.Level.ERROR, "Error creating JmsTool and closing Connection", e);
		} finally {
			try {
				tool.flushQueue();
			} catch (Exception e) {
				logger.log(Logger.Level.ERROR, "Error flush : ", e);
			}

			try {
				logger.log(Logger.Level.TRACE, "Closing default QueueConnection");
				tool.getDefaultQueueConnection().close();
			} catch (Exception e) {
				logger.log(Logger.Level.ERROR, "Error clsoing connection : ", e);
			}
		}
	}

	/*
	 * @testName: bytesMessageFullMsgTests
	 * 
	 * @assertion_ids: JMS:JAVADOC:560; JMS:JAVADOC:562; JMS:JAVADOC:564;
	 * JMS:JAVADOC:566; JMS:JAVADOC:568; JMS:JAVADOC:570; JMS:JAVADOC:572;
	 * JMS:JAVADOC:574; JMS:JAVADOC:576; JMS:JAVADOC:578; JMS:JAVADOC:580;
	 * JMS:JAVADOC:582; JMS:JAVADOC:534; JMS:JAVADOC:536; JMS:JAVADOC:540;
	 * JMS:JAVADOC:544; JMS:JAVADOC:546; JMS:JAVADOC:548; JMS:JAVADOC:550;
	 * JMS:JAVADOC:552; JMS:JAVADOC:554; JMS:JAVADOC:556; JMS:JAVADOC:558;
	 * JMS:JAVADOC:538; JMS:JAVADOC:542; JMS:JAVADOC:532;
	 *
	 * @test_Strategy: Write to a BytesMessage using each type of method. Verify the
	 * BytesMessage received.
	 */
	@Test
	public void bytesMessageFullMsgTests() throws Exception {

		try {
			beanRef.sendFullBytesMessage_Q(testName);

			// Check to see if correct message received
			if (!beanRef.verifyFullBytesMessage())
				throw new Exception("didn't get the right message");

		} catch (Exception e) {
			logger.log(Logger.Level.ERROR, "Caught Exception in test bytesMessageFullMsgTests: ", e);
			throw new Exception("bytesMessageFullMsgTests", e);
		} finally {
			try {
				flushTheQueue();
				if (null != beanRef)
					beanRef.remove();
			} catch (Exception e) {
				logger.log(Logger.Level.ERROR, "[Client] Ignoring Exception on " + "bean remove", e);
			}
		}
	}

	/*
	 * @testName: mapMessageFullMsgTest
	 *
	 * @assertion_ids: JMS:SPEC:74; JMS:JAVADOC:211; JMS:JAVADOC:457;
	 * JMS:JAVADOC:459; JMS:JAVADOC:475; JMS:JAVADOC:477; JMS:JAVADOC:479;
	 * JMS:JAVADOC:461; JMS:JAVADOC:463; JMS:JAVADOC:465; JMS:JAVADOC:467;
	 * JMS:JAVADOC:469; JMS:JAVADOC:471; JMS:JAVADOC:473; JMS:JAVADOC:433;
	 * JMS:JAVADOC:435; JMS:JAVADOC:437; JMS:JAVADOC:439; JMS:JAVADOC:441;
	 * JMS:JAVADOC:443; JMS:JAVADOC:445; JMS:JAVADOC:447; JMS:JAVADOC:449;
	 * JMS:JAVADOC:451; JMS:JAVADOC:453;
	 *
	 * @test_Strategy: Write to a MapMessage using each type of method. Verify the
	 * MapMessage received.
	 */
	@Test
	public void mapMessageFullMsgTest() throws Exception {

		try {
			beanRef.sendFullMapMessage_Q(testName);

			// Check to see if correct message received
			if (!beanRef.verifyFullMapMessage())
				throw new Exception("didn't get the right message");

		} catch (Exception e) {
			logger.log(Logger.Level.ERROR, "Caught Exception in test mapMessageFullMsgTest: ", e);
			throw new Exception("mapMessageFullMsgTest", e);
		} finally {
			try {
				flushTheQueue();
				if (null != beanRef)
					beanRef.remove();
			} catch (Exception e) {
				logger.log(Logger.Level.ERROR, "[Client] Ignoring Exception on " + "bean remove", e);
			}
		}
	}

	/*
	 * @testName: streamMessageFullMsgTest
	 *
	 * @assertion_ids: JMS:SPEC:82; JMS:JAVADOC:150; JMS:JAVADOC:152;
	 * JMS:JAVADOC:154; JMS:JAVADOC:156; JMS:JAVADOC:158; JMS:JAVADOC:160;
	 * JMS:JAVADOC:162; JMS:JAVADOC:164; JMS:JAVADOC:166; JMS:JAVADOC:168;
	 * JMS:JAVADOC:170; JMS:JAVADOC:172; JMS:JAVADOC:128; JMS:JAVADOC:130;
	 * JMS:JAVADOC:132; JMS:JAVADOC:134; JMS:JAVADOC:136; JMS:JAVADOC:138;
	 * JMS:JAVADOC:140; JMS:JAVADOC:142; JMS:JAVADOC:144; JMS:JAVADOC:146;
	 * JMS:JAVADOC:148;
	 *
	 * @test_Strategy: Write to a StreamMessage using each type of method. Verify
	 * the StreamMessage received.
	 */
	@Test
	public void streamMessageFullMsgTest() throws Exception {

		try {
			beanRef.sendFullStreamMessage_Q(testName);

			// Check to see if correct message received
			if (!beanRef.verifyFullStreamMessage())
				throw new Exception("didn't get the right message");

		} catch (Exception e) {
			logger.log(Logger.Level.ERROR, "Caught Exception in test streamMessageFullMsgTest: ", e);
			throw new Exception("streamMessageFullMsgTest", e);
		} finally {
			try {
				flushTheQueue();
				if (null != beanRef)
					beanRef.remove();
			} catch (Exception e) {
				logger.log(Logger.Level.ERROR, "[Client] Ignoring Exception on " + "bean remove", e);
			}
		}
	}

	/*
	 * @testName: msgHdrIDTest
	 *
	 * @assertion_ids: JMS:SPEC:4; JMS:JAVADOC:343;
	 *
	 * @test_Strategy: Send and receive single Text, map, bytes, stream, and object
	 * message call getJMSMessageID and verify that it starts with ID:
	 */
	@Test
	public void msgHdrIDTest() throws Exception {
		String id = null;
		boolean pass = true;

		try {
			beanRef.sendTextMessage_Q(testName);

			// Get JMSMessageID
			id = beanRef.getMessageID();
			if (!chkMessageID(id)) {
				logger.log(Logger.Level.ERROR, "TextMessage: JMSMessageID does not start with ID:" + id);
				pass = false;
			}

			beanRef.sendFullBytesMessage_Q(testName);

			// Get JMSMessageID
			id = beanRef.getMessageID();
			if (!chkMessageID(id)) {
				logger.log(Logger.Level.ERROR, "BytesMessage: JMSMessageID does not start with ID:" + id);
				pass = false;
			}

			beanRef.sendFullMapMessage_Q(testName);

			// Get JMSMessageID
			id = beanRef.getMessageID();
			if (!chkMessageID(id)) {
				logger.log(Logger.Level.ERROR, "MapMessage: JMSMessageID does not start with ID:" + id);
				pass = false;
			}

			beanRef.sendFullStreamMessage_Q(testName);

			// Get JMSMessageID
			id = beanRef.getMessageID();
			if (!chkMessageID(id)) {
				logger.log(Logger.Level.ERROR, "StreamMessage: JMSMessageID does not start with ID:" + id);
				pass = false;
			}

			beanRef.sendObjectMessage_Q(testName);

			// Get JMSMessageID
			id = beanRef.getMessageID();
			if (!chkMessageID(id)) {
				logger.log(Logger.Level.ERROR, "ObjectMessage: JMSMessageID does not start with ID:" + id);
				pass = false;
			}

			if (!pass)
				throw new Exception("msgHdrIDTest failed with at least one type of JMS Message");

		} catch (Exception e) {
			logger.log(Logger.Level.ERROR, "Caught Exception in test msgHdrIDTest: ", e);
			throw new Exception("msgHdrIDTest", e);
		} finally {
			try {
				flushTheQueue();
				if (null != beanRef)
					beanRef.remove();
			} catch (Exception e) {
				logger.log(Logger.Level.ERROR, "[Client] Ignoring Exception on " + "bean remove", e);
			}
		}
	}

	/*
	 * helper method for JMSMessage Header Tests. verifies that the JMSMessageID
	 * starts with ID:
	 *
	 * @param String returned from getJMSMessageID
	 * 
	 * @return boolean true if id correctly starts with ID:
	 */

	private boolean chkMessageID(String id) {
		boolean retcode = true;
		int index = 0;

		// message id must start with ID: - unless it is null
		if (id == null) {
			;
		} else if (id.startsWith("ID:")) {
			;
		} else {
			retcode = false;
		}
		return retcode;
	}

	/*
	 * @testName: msgHdrTimeStampTest
	 *
	 * @assertion_ids: JMS:SPEC:7; JMS:JAVADOC:347;
	 *
	 * @test_Strategy: Send a Text, Map, Bytes, Stream, and Object messages. Check
	 * time of send against the time send returns. JMSTimeStamp should be between
	 * these two.
	 */
	@Test
	public void msgHdrTimeStampTest() throws Exception {
		java.util.ArrayList sendValue = null;
		boolean pass = true;
		long timeBeforeSend;
		long timeAfterSend;
		long JMSTimestamp;

		try {
			sendValue = beanRef.sendTextMessage_Q(testName);

			// Get JMSMessageTimestamp
			JMSTimestamp = beanRef.getTimeStamp();
			if (!(((Long) sendValue.get(0)).longValue() <= JMSTimestamp)
					|| !(((Long) sendValue.get(1)).longValue() >= JMSTimestamp)) {
				logger.log(Logger.Level.ERROR, "TextMessage: JMSMessageTimestamp return wrong value:" + JMSTimestamp);
				logger.log(Logger.Level.ERROR, "should be between time before send " + sendValue.get(0)
						+ " and time after send " + sendValue.get(1));
				pass = false;
			}

			sendValue = beanRef.sendFullBytesMessage_Q(testName);

			// Get JMSMessageTimestamp
			JMSTimestamp = beanRef.getTimeStamp();
			if (!(((Long) sendValue.get(0)).longValue() <= JMSTimestamp)
					|| !(((Long) sendValue.get(1)).longValue() >= JMSTimestamp)) {
				logger.log(Logger.Level.ERROR, "BytesMessage: JMSMessageTimestamp return wrong value:" + JMSTimestamp);
				logger.log(Logger.Level.ERROR, "should be between time before send " + sendValue.get(0)
						+ " and time after send " + sendValue.get(1));
				pass = false;
			}

			sendValue = beanRef.sendFullMapMessage_Q(testName);

			// Get JMSMessageTimestamp
			JMSTimestamp = beanRef.getTimeStamp();
			if (!(((Long) sendValue.get(0)).longValue() <= JMSTimestamp)
					|| !(((Long) sendValue.get(1)).longValue() >= JMSTimestamp)) {
				logger.log(Logger.Level.ERROR, "MapMessage: JMSMessageTimestamp return wrong value:" + JMSTimestamp);
				logger.log(Logger.Level.ERROR, "should be between time before send " + sendValue.get(0)
						+ " and time after send " + sendValue.get(1));
				pass = false;
			}

			sendValue = beanRef.sendFullStreamMessage_Q(testName);

			// Get JMSMessageTimestamp
			JMSTimestamp = beanRef.getTimeStamp();
			if (!(((Long) sendValue.get(0)).longValue() <= JMSTimestamp)
					|| !(((Long) sendValue.get(1)).longValue() >= JMSTimestamp)) {
				logger.log(Logger.Level.ERROR,
						"StreamMessage: JMSMessageTimestamp return wrong value: " + JMSTimestamp);
				logger.log(Logger.Level.ERROR, "should be between time before send " + sendValue.get(0)
						+ " and time after send " + sendValue.get(1));
				pass = false;
			}

			sendValue = beanRef.sendObjectMessage_Q(testName);

			// Get JMSMessageTimestamp
			JMSTimestamp = beanRef.getTimeStamp();
			if (!(((Long) sendValue.get(0)).longValue() <= JMSTimestamp)
					|| !(((Long) sendValue.get(1)).longValue() >= JMSTimestamp)) {
				logger.log(Logger.Level.ERROR,
						"ObjectMessage: JMSMessageTimestamp return wrong value: " + JMSTimestamp);
				logger.log(Logger.Level.ERROR, "should be between time before send " + sendValue.get(0)
						+ " and time after send " + sendValue.get(1));
				pass = false;
			}

			if (!pass)
				throw new Exception("msgHdrTimeStampTest failed with at least one type of JMS Message");

		} catch (Exception e) {
			logger.log(Logger.Level.ERROR, "Caught Exception in test msgHdrTimeStampTest: ", e);
			throw new Exception("msgHdrTimeStampTest", e);
		} finally {
			try {
				flushTheQueue();
				if (null != beanRef)
					beanRef.remove();
			} catch (Exception e) {
				logger.log(Logger.Level.ERROR, "[Client] Ignoring Exception on " + "bean remove", e);
			}
		}
	}

	/*
	 * @testName: msgHdrCorlIdTest
	 *
	 * @assertion_ids: JMS:SPEC:246.7; JMS:JAVADOC:355; JMS:JAVADOC:357;
	 *
	 * @test_Strategy: Send a message to a Queue with CorrelationID set. Receive the
	 * msg and verify the correlationid is as set by client.
	 */
	@Test
	public void msgHdrCorlIdTest() throws Exception {
		boolean pass = true;
		String jmsCorrelationID = "JMSTCKCorrelationID";

		try {
			beanRef.sendTextMessage_Q(testName);

			String tmp = beanRef.getCorrelationID();
			if (!tmp.equals(jmsCorrelationID)) {
				logger.log(Logger.Level.ERROR, "TextMessage: incorrect JMSCorrelationID returned: " + tmp);
				pass = false;
			}

			beanRef.sendFullBytesMessage_Q(testName);

			tmp = beanRef.getCorrelationID();
			if (!tmp.equals(jmsCorrelationID)) {
				logger.log(Logger.Level.ERROR, "BytesMessage: incorrect JMSCorrelationID returned: " + tmp);
				pass = false;
			}

			beanRef.sendFullMapMessage_Q(testName);
			tmp = beanRef.getCorrelationID();

			if (!tmp.equals(jmsCorrelationID)) {
				logger.log(Logger.Level.ERROR, "MapMessage: incorrect JMSCorrelationID returned:" + tmp);
				pass = false;
			}

			beanRef.sendFullStreamMessage_Q(testName);
			tmp = beanRef.getCorrelationID();

			if (!tmp.equals(jmsCorrelationID)) {
				logger.log(Logger.Level.ERROR, "StreamMessage: incorrect JMSCorrelationID returned:" + tmp);
				pass = false;
			}

			beanRef.sendObjectMessage_Q(testName);
			tmp = beanRef.getCorrelationID();

			if (!tmp.equals(jmsCorrelationID)) {
				logger.log(Logger.Level.ERROR, "ObjectMessage: incorrect JMSCorrelationID returned:" + tmp);
				pass = false;
			}

			if (!pass)
				throw new Exception("msgHdrCorlIdTest failed with at least one type of JMS Message");

		} catch (Exception e) {
			logger.log(Logger.Level.ERROR, "Caught Exception in test msgHdrCorlIdTest: ", e);
			throw new Exception("msgHdrCorlIdTest", e);
		} finally {
			try {
				flushTheQueue();
				if (null != beanRef)
					beanRef.remove();
			} catch (Exception e) {
				logger.log(Logger.Level.ERROR, "[Client] Ignoring Exception on " + "bean remove", e);
			}
		}
	}

	/*
	 * @testName: msgHdrReplyToTest
	 *
	 * @assertion_ids: JMS:SPEC:12; JMS:JAVADOC:359; JMS:JAVADOC:361;
	 * JMS:JAVADOC:286; JMS:JAVADOC:289; JMS:JAVADOC:562; JMS:JAVADOC:166;
	 * JMS:SPEC:246.8;
	 *
	 * @test_Strategy: Send a message to a Queue with ReplyTo set to null. Send a
	 * message to a Queue with ReplyTo set to a destination. Test with Text, Map,
	 * Object, Bytes, and Stream messages. Verify on receive that in both cases
	 * ReplyTo is as set by Client.
	 */
	@Test
	public void msgHdrReplyToTest() throws Exception {
		boolean pass = true;
		String QueueName = null;
		String QueueName_used = null;

		try {
			beanRef.sendTextMessage_Q(testName);
			QueueName = beanRef.getReplyTo();

			if (QueueName != null) {
				logger.log(Logger.Level.ERROR, "TextMessage: null JMSReplyTo should be returned");
				pass = false;
			}

			beanRef.sendTextMessage_Q(testName, true);
			QueueName = beanRef.getReplyTo();
			QueueName_used = beanRef.getDestination_1();

			if (!QueueName.equals(QueueName_used)) {
				logger.log(Logger.Level.ERROR, "TextMessage: incorrect JMSReplyTo returned: " + QueueName);
				logger.log(Logger.Level.ERROR, "TextMessage: expecting: " + QueueName_used);
				pass = false;
			} else {
				logger.log(Logger.Level.TRACE, "TextMessage: passed");
			}

			beanRef.sendFullBytesMessage_Q(testName);
			QueueName = beanRef.getReplyTo();

			if (QueueName != null) {
				logger.log(Logger.Level.ERROR, "BytesMessage: null JMSReplyTo should be returned");
				pass = false;
			}

			beanRef.sendBytesMessage_Q(testName, true);
			QueueName = beanRef.getReplyTo();

			if (!QueueName.equals(QueueName_used)) {
				logger.log(Logger.Level.ERROR, "BytesMessage: incorrect JMSReplyTo returned:" + QueueName);
				logger.log(Logger.Level.ERROR, "BytesMessage: expecting: " + QueueName_used);
				pass = false;
			} else {
				logger.log(Logger.Level.TRACE, "BytesMessage: passed");
			}

			beanRef.sendFullMapMessage_Q(testName);
			QueueName = beanRef.getReplyTo();

			if (QueueName != null) {
				logger.log(Logger.Level.ERROR, "MapMessage: null JMSReplyTo should be returned:" + QueueName);
				pass = false;
			}

			beanRef.sendMapMessage_Q(testName, true);
			QueueName = beanRef.getReplyTo();

			if (!QueueName.equals(QueueName_used)) {
				logger.log(Logger.Level.ERROR, "MapMessage: incorrect JMSReplyTo returned: " + QueueName);
				logger.log(Logger.Level.ERROR, "MapMessage: expecting: " + QueueName_used);
				pass = false;
			} else {
				logger.log(Logger.Level.TRACE, "MapMessage: passed");
			}

			beanRef.sendFullStreamMessage_Q(testName);
			QueueName = beanRef.getReplyTo();

			if (QueueName != null) {
				logger.log(Logger.Level.ERROR, "StreamMessage: null JMSReplyTo should be returned: " + QueueName);
				pass = false;
			}

			beanRef.sendStreamMessage_Q(testName, true);
			QueueName = beanRef.getReplyTo();

			if (!QueueName.equals(QueueName_used)) {
				logger.log(Logger.Level.ERROR, "StreamMessage: incorrect JMSReplyTo returned: " + QueueName);
				logger.log(Logger.Level.ERROR, "StreamMessage: expecting: " + QueueName_used);
				pass = false;
			} else {
				logger.log(Logger.Level.TRACE, "StreamMessage: passed");
			}

			beanRef.sendObjectMessage_Q(testName);
			QueueName = beanRef.getReplyTo();

			if (QueueName != null) {
				logger.log(Logger.Level.ERROR, "ObjectMessage: null JMSReplyTo should be returned" + QueueName);
				pass = false;
			}

			beanRef.sendObjectMessage_Q(testName, true);
			QueueName = beanRef.getReplyTo();

			if (!QueueName.equals(QueueName_used)) {
				logger.log(Logger.Level.ERROR, "ObjectMessage: incorrect JMSReplyTo returned: " + QueueName);
				logger.log(Logger.Level.ERROR, "ObjectMessage: expecting: " + QueueName_used);
				pass = false;
			} else {
				logger.log(Logger.Level.TRACE, "ObjectMessage: passed");
			}

			if (!pass)
				throw new Exception("msgHdrReplyToTest failed with at least one type of JMS Message");

		} catch (Exception e) {
			logger.log(Logger.Level.ERROR, "Caught Exception in test msgHdrReplyToTest: ", e);
			throw new Exception("msgHdrReplyToTest", e);
		} finally {
			try {
				flushTheQueue();
				if (null != beanRef)
					beanRef.remove();
			} catch (Exception e) {
				logger.log(Logger.Level.ERROR, "[Client] Ignoring Exception on " + "bean remove", e);
			}
		}
	}

	/*
	 * @testName: msgHdrJMSTypeTest
	 *
	 * @assertion_ids: JMS:SPEC:246.9; JMS:JAVADOC:375; JMS:JAVADOC:377;
	 *
	 * @test_Strategy: Send a message to a Queue with JMSType set to TESTMSG. Test
	 * with Text, Map, Object, Bytes, and Stream messages. Verify on receive.
	 */
	@Test
	public void msgHdrJMSTypeTest() throws Exception {
		boolean pass = true;
		String type = "JMSTCKTESTMSG";

		try {
			beanRef.sendTextMessage_Q(testName);

			if (!beanRef.getType().equals(type)) {
				logger.log(Logger.Level.ERROR, "TextMessage: wrong JMSType returned: " + beanRef.getType());
				pass = false;
			}

			beanRef.sendFullBytesMessage_Q(testName);

			if (!beanRef.getType().equals(type)) {
				logger.log(Logger.Level.ERROR, "BytesMessage: wrong JMSType returned");
				pass = false;
			}

			beanRef.sendFullMapMessage_Q(testName);

			if (!beanRef.getType().equals(type)) {
				logger.log(Logger.Level.ERROR, "MapMessage: wrong JMSType returned");
				pass = false;
			}

			beanRef.sendFullStreamMessage_Q(testName);

			if (!beanRef.getType().equals(type)) {
				logger.log(Logger.Level.ERROR, "StreamMessage: wrong JMSType returned" + beanRef.getType());
				pass = false;
			}

			beanRef.sendObjectMessage_Q(testName);

			if (!beanRef.getType().equals(type)) {
				logger.log(Logger.Level.ERROR, "ObjectMessage: wrong JMSType returned" + beanRef.getType());
				pass = false;
			}

			if (!pass)
				throw new Exception("msgHdrJMSTypeTest failed with at least one type of JMS Message");

		} catch (Exception e) {
			logger.log(Logger.Level.ERROR, "Caught Exception in test msgHdrJMSTypeTest: ", e);
			throw new Exception("msgHdrJMSTypeTest", e);
		} finally {
			try {
				flushTheQueue();
				if (null != beanRef)
					beanRef.remove();
			} catch (Exception e) {
				logger.log(Logger.Level.ERROR, "[Client] Ignoring Exception on " + "bean remove", e);
			}
		}
	}

	/*
	 * @testName: msgHdrJMSPriorityTest
	 *
	 * @assertion_ids: JMS:SPEC:16; JMS:SPEC:16; JMS:SPEC:18; JMS:SPEC:140;
	 * JMS:JAVADOC:305; JMS:JAVADOC:383;
	 *
	 * @test_Strategy: Send a message to a Queue with JMSPriority set to 2. Test
	 * with Text, Map, Object, Bytes, and Stream messages. Verify JMSPriority value
	 * on receive.
	 */
	@Test
	public void msgHdrJMSPriorityTest() throws Exception {
		boolean pass = true;
		int priority = 2;

		try {
			beanRef.sendTextMessage_Q(testName);

			if (beanRef.getPriority() != priority) {
				logger.log(Logger.Level.ERROR, "TextMessage: wrong JMSPriority returned: " + beanRef.getPriority());
				pass = false;
			}

			beanRef.sendFullBytesMessage_Q(testName);

			if (beanRef.getPriority() != priority) {
				logger.log(Logger.Level.ERROR, "BytesMessage: wrong JMSPriority returned: " + beanRef.getPriority());
				pass = false;
			}

			beanRef.sendFullMapMessage_Q(testName);

			if (beanRef.getPriority() != priority) {
				logger.log(Logger.Level.ERROR, "MapMessage: wrong JMSPriority returned: " + beanRef.getPriority());
				pass = false;
			}

			beanRef.sendFullStreamMessage_Q(testName);

			if (beanRef.getPriority() != priority) {
				logger.log(Logger.Level.ERROR, "StreamMessage: wrong JMSPriority returned: " + beanRef.getPriority());
				pass = false;
			}

			beanRef.sendObjectMessage_Q(testName);

			if (beanRef.getPriority() != priority) {
				logger.log(Logger.Level.ERROR, "ObjectMessage: wrong JMSPriority returned: " + beanRef.getPriority());
				pass = false;
			}

			if (!pass)
				throw new Exception("msgHdrJMSPriorityTest failed with at least one type of JMS Message");

		} catch (Exception e) {
			logger.log(Logger.Level.ERROR, "Caught Exception in test msgHdrJMSPriorityTest: ", e);
			throw new Exception("msgHdrJMSPriorityTest", e);
		} finally {
			try {
				flushTheQueue();
				if (null != beanRef)
					beanRef.remove();
			} catch (Exception e) {
				logger.log(Logger.Level.ERROR, "[Client] Ignoring Exception on " + "bean remove", e);
			}
		}
	}

	/*
	 * @testName: msgHdrJMSExpirationTest
	 *
	 * @assertion_ids: JMS:SPEC:15.2; JMS:SPEC:15.3; JMS:SPEC:140; JMS:JAVADOC:309;
	 * JMS:JAVADOC:379;
	 *
	 * @test_Strategy: Send a message to a Queue with time to live set to 0. Test
	 * with Text, Map, Object, Bytes, and Stream messages.
	 */
	@Test
	public void msgHdrJMSExpirationTest() throws Exception {
		boolean pass = true;
		long forever = 0;

		try {
			beanRef.sendTextMessage_Q(testName);

			if (beanRef.getExpiration() != forever) {
				logger.log(Logger.Level.ERROR, "TextMessage: wrong JMSExpiration returned: " + beanRef.getExpiration());
				pass = false;
			}

			beanRef.sendFullBytesMessage_Q(testName);

			if (beanRef.getExpiration() != forever) {
				logger.log(Logger.Level.ERROR,
						"BytesMessage: wrong JMSExpiration returned: " + beanRef.getExpiration());
				pass = false;
			}

			beanRef.sendFullMapMessage_Q(testName);

			if (beanRef.getExpiration() != forever) {
				logger.log(Logger.Level.ERROR, "MapMessage: wrong JMSExpiration returned: " + beanRef.getExpiration());
				pass = false;
			}

			beanRef.sendFullStreamMessage_Q(testName);

			if (beanRef.getExpiration() != forever) {
				logger.log(Logger.Level.ERROR,
						"StreamMessage: wrong JMSExpiration returned: " + beanRef.getExpiration());
				pass = false;
			}

			beanRef.sendObjectMessage_Q(testName);

			if (beanRef.getExpiration() != forever) {
				logger.log(Logger.Level.ERROR,
						"ObjectMessage: wrong JMSExpiration returned: " + beanRef.getExpiration());
				pass = false;
			}

			if (!pass)
				throw new Exception("msgHdrJMSExpirationTest failed with at least one type of JMS Message");

		} catch (Exception e) {
			logger.log(Logger.Level.ERROR, "Caught Exception in test msgHdrJMSExpirationTest: ", e);
			throw new Exception("msgHdrJMSExpirationTest");
		} finally {
			try {
				flushTheQueue();
				if (null != beanRef)
					beanRef.remove();
			} catch (Exception e) {
				logger.log(Logger.Level.ERROR, "[Client] Ignoring Exception on " + "bean remove", e);
			}
		}
	}

	/*
	 * @testName: msgHdrJMSDestinationTest
	 *
	 * @assertion_ids: JMS:SPEC:2; JMS:JAVADOC:363; JMS:JAVADOC:286;
	 *
	 * @test_Strategy: Create and send a message to a Queue. Verify on receive that
	 * JMSDestination is set to the Queue. Test with Text, Map, Object, Bytes, and
	 * Stream messages.
	 */
	@Test
	public void msgHdrJMSDestinationTest() throws Exception {
		boolean pass = true;
		long forever = 0;
		String dest = null;
		String dest_used = null;

		try {
			beanRef.sendTextMessage_Q(testName);
			dest = beanRef.getDestination();
			dest_used = beanRef.getDestination_1();

			if (!dest.equals(dest_used)) {
				logger.log(Logger.Level.ERROR, "TextMessage: wrong JMSDestination returned: " + dest);
				logger.log(Logger.Level.ERROR, "Expecting " + dest_used);
				pass = false;
			} else {
				logger.log(Logger.Level.TRACE, "TextMessage: msgHdrJMSDestinationTest passed.");
			}

			beanRef.sendFullBytesMessage_Q(testName);

			if (!(beanRef.getDestination()).equals(dest_used)) {
				logger.log(Logger.Level.ERROR, "BytesMessage: wrong JMSDestination returned: " + dest);
				logger.log(Logger.Level.ERROR, "Expecting " + dest_used);
				pass = false;
			} else {
				logger.log(Logger.Level.TRACE, "BytesMessage: msgHdrJMSDestinationTest passed.");
			}

			beanRef.sendFullMapMessage_Q(testName);

			if (!(beanRef.getDestination()).equals(dest_used)) {
				logger.log(Logger.Level.ERROR, "MapMessage: wrong JMSDestination returned: " + dest);
				logger.log(Logger.Level.ERROR, "Expecting " + dest_used);
				pass = false;
			} else {
				logger.log(Logger.Level.TRACE, "MapMessage: msgHdrJMSDestinationTest passed.");
			}

			beanRef.sendFullStreamMessage_Q(testName);

			if (!(beanRef.getDestination()).equals(dest_used)) {
				logger.log(Logger.Level.ERROR, "StreamMessage: wrong JMSDestination returned: " + dest);
				logger.log(Logger.Level.ERROR, "Expecting " + dest_used);
				pass = false;
			} else {
				logger.log(Logger.Level.TRACE, "StreamMessage: msgHdrJMSDestinationTest passed.");
			}

			beanRef.sendObjectMessage_Q(testName);

			if (!(beanRef.getDestination()).equals(dest_used)) {
				logger.log(Logger.Level.ERROR, "ObjectMessage: wrong JMSDestination returned: " + dest);
				logger.log(Logger.Level.ERROR, "Expecting " + dest_used);
				pass = false;
			} else {
				logger.log(Logger.Level.TRACE, "ObjectMessage: msgHdrJMSDestinationTest passed.");
			}

			if (!pass)
				throw new Exception("msgHdrJMSDestinationTest failed with at least one type of JMS Message");

		} catch (Exception e) {
			logger.log(Logger.Level.ERROR, "Caught Exception in test msgHdrJMSDestinationTest: ", e);
			throw new Exception("msgHdrJMSDestinationTest", e);
		} finally {
			try {
				flushTheQueue();
				if (null != beanRef)
					beanRef.remove();
			} catch (Exception e) {
				logger.log(Logger.Level.ERROR, "[Client] Ignoring Exception on " + "bean remove", e);
			}
		}
	}

	/*
	 * @testName: msgHdrJMSDeliveryModeTest
	 *
	 * @assertion_ids: JMS:SPEC:3; JMS:SPEC:140; JMS:JAVADOC:367; JMS:SPEC:246.2;
	 * JMS:JAVADOC:301;
	 *
	 * @test_Strategy: Create and send a message to a Queue. Verify on receive that
	 * JMSDeliveryMode is set as the default delivery mode of persistent. Create
	 * another message with a nonpersistent delivery mode. Test with Text, Map,
	 * Object, Bytes, and Stream messages.
	 */
	@Test
	public void msgHdrJMSDeliveryModeTest() throws Exception {
		boolean pass = true;
		long forever = 0;

		try {
			beanRef.sendTextMessage_Q(testName);

			if (beanRef.getDeliveryMode() != DeliveryMode.PERSISTENT) {
				logger.log(Logger.Level.ERROR,
						"TextMessage: wrong DeliveryMode returned: " + beanRef.getDeliveryMode());
				pass = false;
			}

			beanRef.sendTextMessage_Q(testName, false, DeliveryMode.NON_PERSISTENT);

			if (beanRef.getDeliveryMode() != DeliveryMode.NON_PERSISTENT) {
				logger.log(Logger.Level.ERROR,
						"TextMessage: wrong DeliveryMode returned: " + beanRef.getDeliveryMode());
				pass = false;
			}

			beanRef.sendFullBytesMessage_Q(testName);

			if (beanRef.getDeliveryMode() != DeliveryMode.PERSISTENT) {
				logger.log(Logger.Level.ERROR, "BytesMessage: wrong DeliveryMode returned: ");
				pass = false;
			}

			beanRef.sendBytesMessage_Q(testName, false, DeliveryMode.NON_PERSISTENT);

			if (beanRef.getDeliveryMode() != DeliveryMode.NON_PERSISTENT) {
				logger.log(Logger.Level.ERROR, "BytesMessage: wrong DeliveryMode returned: ");
				pass = false;
			}

			beanRef.sendFullMapMessage_Q(testName);

			if (beanRef.getDeliveryMode() != DeliveryMode.PERSISTENT) {
				logger.log(Logger.Level.ERROR, "MapMessage: wrong DeliveryMode returned: ");
				pass = false;
			}

			beanRef.sendMapMessage_Q(testName, false, DeliveryMode.NON_PERSISTENT);

			if (beanRef.getDeliveryMode() != DeliveryMode.NON_PERSISTENT) {
				logger.log(Logger.Level.ERROR, "MapMessage: wrong DeliveryMode returned: ");
				pass = false;
			}

			beanRef.sendFullStreamMessage_Q(testName);

			if (beanRef.getDeliveryMode() != DeliveryMode.PERSISTENT) {
				logger.log(Logger.Level.ERROR, "StreamMessage: wrong DeliveryMode returned: ");
				pass = false;
			}

			beanRef.sendStreamMessage_Q(testName, false, DeliveryMode.NON_PERSISTENT);

			if (beanRef.getDeliveryMode() != DeliveryMode.NON_PERSISTENT) {
				logger.log(Logger.Level.ERROR, "StreamMessage: wrong DeliveryMode returned: ");
				pass = false;
			}

			beanRef.sendObjectMessage_Q(testName);

			if (beanRef.getDeliveryMode() != DeliveryMode.PERSISTENT) {
				logger.log(Logger.Level.ERROR, "ObjectMessage: wrong DeliveryMode returned: ");
				pass = false;
			}

			beanRef.sendObjectMessage_Q(testName, false, DeliveryMode.NON_PERSISTENT);

			if (beanRef.getDeliveryMode() != DeliveryMode.NON_PERSISTENT) {
				logger.log(Logger.Level.ERROR, "ObjectMessage: wrong DeliveryMode returned: ");
				pass = false;
			}

			if (!pass)
				throw new Exception("msgHdrJMSDeliveryModeTest failed with at least one type of JMS Message");

		} catch (Exception e) {
			logger.log(Logger.Level.ERROR, "Caught Exception in test msgHdrJMSDeliveryModeTest: ", e);
			throw new Exception("msgHdrJMSDeliveryModeTest", e);
		} finally {
			try {
				flushTheQueue();
				if (null != beanRef)
					beanRef.remove();
			} catch (Exception e) {
				logger.log(Logger.Level.ERROR, "[Client] Ignoring Exception on " + "bean remove", e);
			}
		}
	}

	/*
	 * @testName: messageOrderTest
	 *
	 * @assertion_ids: JMS:SPEC:146; JMS:JAVADOC:122;
	 *
	 * @test_Strategy: Send messages to a Queue and receive them. Verify that the
	 * order of received messages matches the order of messages sent through the
	 * text of the sent messages.
	 */
	@Test
	public void messageOrderTest() throws Exception {
		int numMessages = 3;
		String text[] = new String[numMessages];

		try {
			for (int i = 0; i < numMessages; i++) {
				text[i] = "message order test " + i;
				beanRef.sendTextMessage_Q(testName + i, text[i]);
			}

			for (int i = 0; i < numMessages; i++) {

				if (!beanRef.getText().equals(text[i])) {
					logger.log(Logger.Level.ERROR, "Received message: ");
					throw new Exception("received wrong message");
				}
			}

		} catch (Exception e) {
			logger.log(Logger.Level.ERROR, "Caught Exception in test messageOrderTest: ", e);
			throw new Exception("messageOrderTest", e);
		} finally {
			try {
				flushTheQueue();
				if (null != beanRef)
					beanRef.remove();
			} catch (Exception e) {
				logger.log(Logger.Level.ERROR, "[Client] Ignoring Exception on " + "bean remove", e);
			}
		}
	}

	/*
	 * @testName: nullDestinationTest
	 *
	 * @assertion_ids: JMS:SPEC:139; JMS:JAVADOC:188; JMS:JAVADOC:202;
	 *
	 * @test_Strategy: Create a QueueSender with null destination. Send a message
	 * using the QueueSender specifying destination in send and verify receiving the
	 * message
	 */
	@Test
	public void nullDestinationTest() throws Exception {
		String text = "JMS TCK test for null Destination";
		Queue testQueue = null;

		try {
			beanRef.sendTextMessage_Q(testName, text, testQueue);

			if (!beanRef.getText().equals(text)) {
				throw new Exception("received wrong message");
			}

		} catch (Exception e) {
			logger.log(Logger.Level.ERROR, "Caught Exception in test nullDestinationTest: ", e);
			throw new Exception("nullDestinationTest", e);
		} finally {
			try {
				flushTheQueue();
				if (null != beanRef)
					beanRef.remove();
			} catch (Exception e) {
				logger.log(Logger.Level.ERROR, "[Client] Ignoring Exception on " + "bean remove", e);
			}
		}
	}
}
