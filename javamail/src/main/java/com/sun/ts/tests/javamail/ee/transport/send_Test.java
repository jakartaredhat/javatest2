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
 * @(#)send_Test.java	1.17 03/05/16
 */
package com.sun.ts.tests.javamail.ee.transport;

import java.io.Serializable;
import java.lang.System.Logger;
import java.util.Date;
import java.util.Properties;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.javatest.Status;
import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.javamail.ee.common.MailTestUtil;

import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class send_Test implements Serializable {

	private static final Logger logger = (Logger) System.getLogger(send_Test.class.getName());

	// get this from ts.jte
	private String transport_protocol = null;

	private transient MailTestUtil mailTestUtil;

	private String user;

	private String password;

	private transient Status status;

	private int errors = 0; // number of unit test errors

	// get this from ts.jte
	private String mailTo = null;

	private transient Session session;

	public String TO = "ksnijjar@eng";

	public static final String SUBJECT = "Transport class test";

	public static final String TEXT = "Testing Transport class send() API";

	public static final String MAILER = "JavaMail";

	static String msgText = "This is a message body.\nHere's the second line.";


	/* Test setup: */
	/*
	 * 
	 * @class.setup_props: javamail.protocol; javamail.server; javamail.username;
	 * javamail.password ; javamail.mailbox; transport_protocol; mailuser1;
	 * smtp.port; imap.port;
	 */
	@BeforeEach
	public void setup() throws Exception {
		try {

			String protocol = System.getProperty("javamail.protocol");
			String host = System.getProperty("javamail.server");
			user = System.getProperty("javamail.username");
			password = System.getProperty("javamail.password");
			String mailbox = System.getProperty("javamail.mailbox");

			String smtpPortStr = System.getProperty("smtp.port");
			int smtpPort = Integer.parseInt(smtpPortStr);
			logger.log(Logger.Level.TRACE,"SMTP Port = " + smtpPort);

			String imapPortStr = System.getProperty("imap.port");
			int imapPort = Integer.parseInt(imapPortStr);
			logger.log(Logger.Level.TRACE,"IMAP Port = " + imapPort);

			mailTestUtil = new MailTestUtil();
			session = mailTestUtil.createSession(host, smtpPortStr, user, password);

			// mail recipient
			mailTo = System.getProperty("mailuser1");
			if (mailTo.length() == 0)
				throw new Exception("Invalid mailuser1 - the mail to property");

			transport_protocol = System.getProperty("transport_protocol");
			;
			if (transport_protocol.length() == 0)
				throw new Exception("Invalid transport_protocol");

		} catch (Exception e) {
			logger.log(Logger.Level.ERROR,"Exception : " + e.getMessage());
			logger.log(Logger.Level.ERROR,"Setup Failed!");
			TestUtil.printStackTrace(e);
		}
	}

	/*
	 * @testName: testSend
	 * 
	 * @assertion_ids: JavaEE:SPEC:235; JavaEE:SPEC:238;
	 * 
	 * @test_Strategy: Send this message. If success return void, else throw error.
	 */
	// derived from javamail suite send_Test class
	@Test
	public void testSend() throws Exception {
		String msgText = "Testing Transport.send(Message msg).\nPASS.";

		try {

			// Create a message object
			MimeMessage msg = new MimeMessage(session);

			// Construct an address array
			InternetAddress addr = new InternetAddress(mailTo);
			InternetAddress addrs[] = new InternetAddress[1];
			addrs[0] = addr;

			// msg.setFrom(addr);
			msg.setRecipients(Message.RecipientType.TO, addrs);
			msg.setSubject("testSend()" + new Date());
			msg.setContent(msgText, "text/plain");

			// BEGIN UNIT TEST 1:
			logger.log(Logger.Level.TRACE,"UNIT TEST 1: send(Message)");

			// send the mail message
			Transport.send(msg); // API TEST

			logger.log(Logger.Level.TRACE,"UNIT TEST 1: passed\n");
			// END UNIT TEST 1:

		} catch (Exception e) {
			logger.log(Logger.Level.ERROR,"Unexpected Exception " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("Call to testSend() Failed!", e);
		}
	}

	//
	/*
	 * @testName: testSend2
	 * 
	 * @assertion_ids: JavaEE:SPEC:235; JavaEE:SPEC:238;
	 * 
	 * @test_Strategy: Send this message. If success return void, else throw error.
	 */
	// derived from javamail suite send_Test class
	@Test
	public void testSend2() throws Exception {
		String msgText = "Testing Transport.send(Message, Address[]).\nPASS.";
		try {

			// Create a message object
			MimeMessage msg = new MimeMessage(session);

			// Construct an address array
			InternetAddress addr = new InternetAddress(mailTo);
			InternetAddress addrs[] = new InternetAddress[1];
			addrs[0] = addr;

			// msg.setFrom(addr);
			msg.setRecipients(Message.RecipientType.TO, addrs);
			msg.setSubject("testSend2()" + new Date());
			msg.setContent(msgText, "text/plain");

			// BEGIN UNIT TEST 2:

			// send the mail message off via the specified addresses
			Transport.send(msg, addrs); // API TEST

			// END UNIT TEST 2:

		} catch (Exception e) {
			logger.log(Logger.Level.ERROR,"Unexpected Exception " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("Call to testSend2() Failed!", e);
		}
	}

	/*
	 * @testName: testSend3
	 * 
	 * @assertion_ids: JavaEE:SPEC:235; JavaEE:SPEC:238;
	 * 
	 * @test_Strategy: construct the message - check the type verify that addtype
	 * returns rfc822
	 */
	// derived from javamail suite getType class
	@Test
	public void testSend3() throws Exception {
		String msgText = "Testing message/rfc822.";
		try {

			// Create a message object
			MimeMessage msg = new MimeMessage(session);

			// Construct an address array
			InternetAddress addr = new InternetAddress(mailTo);
			Address addrs[] = new Address[1];
			addrs[0] = addr;
			msg.setFrom(addr);
			msg.setRecipients(Message.RecipientType.TO, addrs);
			msg.setSubject("get rfc822 type");
			msg.setContent("Verify that type is rfc822", "text/plain");
			addrs = msg.getFrom();
			String addtype = addrs[0].getType(); // API TEST
			logger.log(Logger.Level.TRACE,"addtype is " + addtype);
			if (addtype != null) {
				if (addtype.equals("rfc822"))
					logger.log(Logger.Level.TRACE,"UNIT TEST  passed\n");
				else {
					throw new Exception("UNIT TEST  FAILED\n");
				}
			}

			// BEGIN UNIT TEST 1:

			// send the mail message
			Transport.send(msg); // API TEST

		} catch (Exception e) {
			logger.log(Logger.Level.ERROR,"Unexpected Exception " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("Call to testSend3() Failed!", e);
		}
	}

	/*
	 * @testName: testconnect1
	 * 
	 * @assertion_ids: JavaEE:SPEC:235; JavaEE:SPEC:238;
	 * 
	 * @test_Strategy: Send this message. If success return void, else throw error.
	 */
	// derived from javamail suite connect_Test class
	@Test
	public void testconnect1() throws Exception {
		String host = null;
		String user = null;
		String password = null;
		String msgText = "Testing connect().\nPASS.";
		try {

			// Get a Transport object
			Transport transport = session.getTransport(transport_protocol);
			if (transport == null) {
				throw new Exception("WARNING: Failed to create a transport object!");
			}

			// Create a MimeMessage object
			MimeMessage msg = new MimeMessage(session);

			// Construct an address array
			InternetAddress addr = new InternetAddress(mailTo);
			InternetAddress addrs[] = new InternetAddress[1];
			addrs[0] = addr;

			msg.setFrom(addr);
			msg.setRecipients(Message.RecipientType.TO, addrs);
			msg.setSubject("testconnect1" + new Date());
			msg.setContent(msgText, "text/plain");

			// BEGIN UNIT TEST 1:

			// Connect
			transport.connect(); // API TEST

			// logger.log(Logger.Level.INFO,"UNIT TEST 1: passed\n");
			// END UNIT TEST 1:

			// send the mail message via specified 'protocol'
			transport.sendMessage(msg, addrs);

		} catch (Exception e) {
			logger.log(Logger.Level.ERROR,"Unexpected Exception " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("Call to testConnect1() Failed!", e);
		}

	}// end of testconnect1()

	/*
	 * @testName: testSendMessage
	 * 
	 * @assertion_ids: JavaEE:SPEC:238; JavaEE:SPEC:235;
	 * 
	 * @test_Strategy: Call this API for given message objects with address list. If
	 * this invocation is successful then this testcase passes.
	 */
	// derived from javamail suite sendMessage_Test class
	@Test
	public void testSendMessage() throws Exception {
		String msgText = "Testing sendMessage(Message,Address[]).\nPASS.";
		try {

			// Get a Transport object
			Transport transport = session.getTransport(transport_protocol);
			if (transport == null) {
				throw new Exception("WARNING: Failed to create a transport object!");
			}

			// Create a message object
			MimeMessage msg = new MimeMessage(session);

			// Construct an address array
			InternetAddress addr = new InternetAddress(mailTo);

			InternetAddress addrs[] = new InternetAddress[1];
			addrs[0] = addr;

			msg.setFrom(addr);
			msg.setRecipients(Message.RecipientType.TO, addrs);
			msg.setSubject("testSendMessage()" + new Date());
			msg.setContent(msgText, "text/plain");

			// Connect
			transport.connect();

			// send the mail message off via the specified addresses
			transport.sendMessage(msg, addrs); // API TEST
		} catch (Exception e) {
			logger.log(Logger.Level.ERROR,"Unexpected Exception " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("Call to testSendMessage() Failed!", e);
		}
	} // end of testSendMessage()

	/*
	 * @testName: test4
	 * 
	 * @assertion_ids: JavaEE:SPEC:235;
	 * 
	 * @test_Strategy:
	 * 
	 * This test tests the <strong>send()</strong> API. It does this by passing
	 * various valid input values and then checking the type of the returned object.
	 * <p>
	 * 
	 * Send this message. <p> api2test: public void send(msg) <p> Send this message
	 * to the specified addresses. <p> api2test: public void send(Message,
	 * Address[]) <p> Send this message using the specified username and password.
	 * <p> api2test: public void send(Message, String, String) <p>
	 * 
	 * how2test: Call these APIs for given message objects with or without address
	 * list. If this invocation is successfull then this testcase passes otherwise
	 * it fails. <p>
	 * 
	 * derived from javamail suite send_Test
	 */
	@Test
	public void test4() throws Exception {

		TO = user;
		logger.log(Logger.Level.INFO,"\nTesting Transport class => send(Message, Address[])\n");

		try {

			// Create a message object
			MimeMessage msg = new MimeMessage(session);

			// Get a Transport object
			Transport transport = session.getTransport(transport_protocol);
			if (transport == null) {
				throw new Exception("WARNING: Failed to create a transport object!");
			}

			// Construct an address array
			InternetAddress addr = new InternetAddress(TO);
			InternetAddress addrs[] = new InternetAddress[1];
			addrs[0] = addr;

			msg.setFrom(addr);
			msg.setRecipients(Message.RecipientType.TO, addrs);
			msg.setSubject("JavaMail send() API Test");
			msg.setContent(msgText, "text/plain");

			// BEGIN UNIT TEST 1:
			logger.log(Logger.Level.INFO,"UNIT TEST 1: send(Message)");

			// send the mail message
			transport.send(msg); // API TEST

			logger.log(Logger.Level.INFO,"UNIT TEST 1: passed\n");
			// END UNIT TEST 1:

			// BEGIN UNIT TEST 2:
			logger.log(Logger.Level.INFO,"UNIT TEST 2: send(Message, Address[])");

			// send the mail message off via the specified addresses
			transport.send(msg, addrs); // API TEST

			logger.log(Logger.Level.INFO,"UNIT TEST 2: passed\n");
			// END UNIT TEST 2:

			// BEGIN UNIT TEST 3:
			logger.log(Logger.Level.INFO,"UNIT TEST 3: send(Message, String, String)");

			// create a Session with no authenticator
			msg = new MimeMessage(session);
			msg.setFrom(addr);
			msg.setRecipients(Message.RecipientType.TO, addrs);
			msg.setSubject("JavaMail send() API Test");
			msg.setContent(msgText, "text/plain");

			// send the mail message off with the username and password
			transport.send(msg, user, password); // API TEST

			logger.log(Logger.Level.INFO,"UNIT TEST 3: passed\n");
			// END UNIT TEST 3:

			checkStatus();

		} catch (Exception e) {
			logger.log(Logger.Level.ERROR,"Unexpected Exception " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("test4() Failed!", e);
		}

	}

	public void checkStatus() {
		if (errors == 0)
			status = Status.passed("OKAY");
		else
			status = Status.failed("");
	}

	/* cleanup */
	@AfterEach
	public void cleanup() throws Exception {
		try {
			logger.log(Logger.Level.TRACE,"Cleanup ;");
		} catch (Exception e) {
			logger.log(Logger.Level.ERROR,"An error occurred in cleanup!", e);
		}
	}
}
