/*
 * Copyright (c) 2007, 2024 Oracle and/or its affiliates. All rights reserved.
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
 * @(#)mimemessage_Test.java	1.17 03/05/16
 */
package com.sun.ts.tests.javamail.ee.mimemessage;

import java.io.Serializable;
import java.lang.System.Logger;
import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.javatest.Status;
import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.javamail.ee.common.MailTestUtil;

import jakarta.mail.Address;
import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

public class mimemessage_Test implements Serializable {

	private static final Logger logger = (Logger) System.getLogger(mimemessage_Test.class.getName());

	private String mailTo = null;

	private int errors = 0; // number of unit test errors

	private transient MailTestUtil mailTestUtil;

	private transient Session session;

	private Folder folder;

	private Message[] msgs;

	private Store store;

	private transient Status status;

	private String user;

	private String[] addrlist = { "ksnijjar@eng", "ksnijjar@eng.sun.com", "French@physicists", "cannot@waste",
			"us/@mhs-mci.ebay", "it@is", "tower@ihug.co.nz", "root@mxrelay.lanminds.com", "javaworld",
			"xx.zzz12@fea.net", "javamail-api-eng@icdev", "ksnijjar@java-test.Eng.Sun.COM" };

	/* Test setup: */
	/*
	 * @class.setup_props: javamail.protocol; javamail.server; javamail.username;
	 * javamail.password ; javamail.mailbox; javamail.root.path; smtp.port;
	 * imap.port;
	 */
	@BeforeEach
	public void setup() throws Exception {
		try {

			String protocol = System.getProperty("javamail.protocol");
			String host = System.getProperty("javamail.server");
			user = System.getProperty("javamail.username");
			String password = System.getProperty("javamail.password");
			String mailbox = System.getProperty("javamail.mailbox");

			String smtpPortStr = System.getProperty("smtp.port");
			int smtpPort = Integer.parseInt(smtpPortStr);
			logger.log(Logger.Level.TRACE,"SMTP Port = " + smtpPort);

			String imapPortStr = System.getProperty("imap.port");
			int imapPort = Integer.parseInt(imapPortStr);
			logger.log(Logger.Level.TRACE,"IMAP Port = " + imapPort);

			MailTestUtil mailTestUtil = new MailTestUtil();

			store = mailTestUtil.connect2host(protocol, host, imapPort, user, password);
			session = mailTestUtil.getSession();

			// Get a Folder object
			Folder root = mailTestUtil.getRootFolder(store);
			folder = root.getFolder(mailbox);

			if (folder == null) {
				throw new Exception("Invalid folder object!");
			}
			folder.open(Folder.READ_ONLY);

			// Get all the messages
			msgs = folder.getMessages();

		} catch (Exception e) {
			logger.log(Logger.Level.ERROR,"Exception : " + e.getMessage());
			logger.log(Logger.Level.ERROR,"Setup Failed!");
			TestUtil.printStackTrace(e);
		}
	}

	/*
	 * @testName: testSetContent1
	 * 
	 * @assertion_ids: JavaEE:SPEC:235;
	 * 
	 * @test_Strategy: Call setContent with required arguments for multipart then
	 * call getContent() to verify.
	 */
	// derived from javamail suite setContent_Test class
	@Test
	public void testSetContent1() throws Exception {

		try {

			// BEGIN UNIT TEST:

			// Create a MimeMessage object
			MimeMessage mob = new MimeMessage(session);

			if (mob == null) {
				throw new Exception("Warning: Failed to create a MimeMessage object!");
			}
			// Create a Multipart object
			Multipart mmp = new MimeMultipart();

			if (mmp == null) {
				throw new Exception("Warning: Failed to create a Multipart object!");
			}
			// BEGIN UNIT TEST 1:
			logger.log(Logger.Level.TRACE,"UNIT TEST 1:  setContent(Multipart)");

			mob.setContent(mmp); // API TEST
			Object content = mob.getContent();

			if ((content != null) && (content instanceof Multipart)) {
				logger.log(Logger.Level.TRACE,"This is a Multipart");
				Multipart mp1 = (Multipart) content;
				int count = mp1.getCount();
				logger.log(Logger.Level.TRACE,"UNIT TEST 1:  passed\n");
			} else {
				logger.log(Logger.Level.TRACE,"UNIT TEST 1:  FAILED\n");
				throw new Exception("testSetContent1 failed\n");
			}
			// END UNIT TEST 1:

			// END UNIT TEST:
		} catch (Exception e) {
			logger.log(Logger.Level.ERROR,"Unexpected Exception " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("Call to testSetContent1() Failed!", e);
		}

	} // end of testSetContent1()

	/*
	 * @testName: testSetContent2
	 * 
	 * @assertion_ids: JavaEE:SPEC:235; JavaEE:SPEC:238;
	 * 
	 * @test_Strategy: Call setContent with required arguments for text/plain then
	 * call getContentType() to verify.
	 */
	// derived from javamail suite sendMessage_Test class
	@Test
	public void testSetContent2() throws Exception {
		String msgText = "testing 1,2,3 ...";
		try {

			// Create a message object
			MimeMessage msg = new MimeMessage(session);

			if (msg == null) {
				throw new Exception("WARNING: Failed to create a message object!");
			}
			// Construct an address array
			InternetAddress addr = new InternetAddress(user);

			if (addr == null) {
				throw new Exception("WARNING: Failed to create a InternetAddress object!");
			}
			InternetAddress addrs[] = new InternetAddress[1];
			addrs[0] = addr;

			msg.setFrom(addr);
			msg.setRecipients(Message.RecipientType.TO, addrs);
			msg.setSubject("testSetContent2()" + new Date());
			msg.setContent(msgText, "text/plain");
			Object content = msg.getContentType();
			logger.log(Logger.Level.TRACE,"content is " + content.toString());
			if ((content != null) && (content instanceof String)) {
				if (((String) content).equals("text/plain"))
					logger.log(Logger.Level.TRACE,"UNIT TEST 1:  passed\n");
				else {
					logger.log(Logger.Level.TRACE,"UNIT TEST 1:  FAILED\n");
					throw new Exception("testSetContent2() failed\n");
				}
			}
			// END UNIT TEST 1:
		} catch (Exception e) {
			logger.log(Logger.Level.ERROR,"Unexpected Exception " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("Call to testSetContent2() Failed!", e);
		}

	} // end of testSetContent2()

	/*
	 * @testName: getSession_Test
	 * 
	 * @assertion_ids: JavaEE:SPEC:235; JavaEE:SPEC:238;
	 * 
	 * @test_Strategy:
	 * 
	 * This class tests the <strong>getSession()</strong> API. It does this by
	 * invoking the test api and then checking that the returned object is the same
	 * object used to create the message.<p>
	 * 
	 * Get the session of this message. <p> api2test: public String getSession() <p>
	 * 
	 * how2test: Call this API on given message object, verify that it returns the
	 * Session object used to create this message. If this operation is successfull
	 * then this testcase passes, otherwise it fails. <p>
	 * 
	 * Returns the Session object used when the message was created. Returns null if
	 * no Session is available. <p>
	 */
	// derived from javamail suite getSession_Test class
	@Test
	public void getSession_Test() throws Exception {

		logger.log(Logger.Level.INFO,"\nTesting class Message: getSession()\n");

		try {

			// BEGIN UNIT TEST 1:
			Message msg = new MimeMessage(session);
			logger.log(Logger.Level.INFO,"UNIT TEST 1:  getSession()");

			Session sess = msg.getSession(); // API TEST

			if (sess == session) {
				logger.log(Logger.Level.INFO,"UNIT TEST 1:  passed\n");
			} else {
				logger.log(Logger.Level.INFO,"got Session: " + sess);
				logger.log(Logger.Level.INFO,"UNIT TEST 1:  Failed\n");
				errors++;
			}
			// END UNIT TEST 1:

			// BEGIN UNIT TEST 2:
			msg = new MimeMessage((Session) null);
			logger.log(Logger.Level.INFO,"UNIT TEST 2:  getSession() null");

			sess = msg.getSession(); // API TEST

			if (sess == null) {
				logger.log(Logger.Level.INFO,"UNIT TEST 2:  passed\n");
			} else {
				logger.log(Logger.Level.INFO,"got Session: " + sess);
				logger.log(Logger.Level.INFO,"UNIT TEST 2:  Failed\n");
				errors++;
			}
			// END UNIT TEST 2:
			checkStatus();

		} catch (Exception e) {
			logger.log(Logger.Level.ERROR,"Unexpected Exception " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("getSession_Test Failed!", e);
		}

	}

	/**
	
	 */
	/*
	 * @testName: createMimeMessage_Test
	 * 
	 * @assertion_ids: JavaEE:SPEC:235; JavaEE:SPEC:238;
	 * 
	 * @test_Strategy: This test tests the <strong>createMessage()</strong> API. It
	 * does by passing various valid input values and then checking the type of the
	 * returned object. <p>
	 * 
	 * Create a subclassed MimeMessage object and in reply(boolean) return instance
	 * of this new subclassed object. <p> api2test: protected void
	 * createMimeMessage() <p>
	 * 
	 * how2test: Call API with various arguments, then call getRecipients() api,
	 * verify that user specified recipient address types have been added. If so
	 * then this testcase passes, otherwise it fails. <p>
	 */
	// derived from javamail suite createMimeMessage_Test
	@Test
	public void createMimeMessage_Test() throws Exception {

		try {
			// Create a custom MimeMessage objectcreateMimeMessage_Test
			// with custom message ID algorithm
			MimeMessage msg = new MyMimeMessage(session);

			// BEGIN UNIT TEST:
			logger.log(Logger.Level.INFO,"UNIT TEST 1: createMimeMessage");

			// create Message ID
			Message replyMsg = msg.reply(false);// API TEST for
			// createMimeMessage called in reply(boolean).
			// See the reply() in the inner class below

			if (replyMsg instanceof MyReplyMimeMessage) {
				logger.log(Logger.Level.INFO,"UNIT TEST 1: passed\n");
			} else {
				logger.log(Logger.Level.INFO,"UNIT TEST 1: FAILED\n");
				errors++;
			}

			// END UNIT TEST:
			checkStatus();

		} catch (Exception e) {
			logger.log(Logger.Level.ERROR,"Unexpected Exception " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("createMimeMessage_Test Failed!", e);
		}

	}

	/*
	 * @testName: reply_Test
	 * 
	 * @assertion_ids: JavaEE:SPEC:235;
	 * 
	 * @test_Strategy:
	 * 
	 * This test tests the <strong>reply()</strong> APIs. <p>
	 * 
	 * Create a reply MimeMessage object and check that it has the appropriate
	 * headers. <p> api2test: public Message reply() <p>
	 * 
	 * how2test: Call API with various arguments, then verify that the reply message
	 * has the required recipients and subject. If so then this testcase passes,
	 * otherwise it fails. <p>
	 * 
	 * derived from javamail suite reply_Test
	 */
	@Test
	public void reply_Test() throws Exception {

		try {

			InternetAddress from = new InternetAddress("joe@example.com");
			InternetAddress to = new InternetAddress("bob@example.com");
			String subj = "test";

			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(from);
			msg.setRecipient(Message.RecipientType.TO, to);
			msg.setSubject(subj);
			msg.setText("test");

			// BEGIN UNIT TEST 1:
			logger.log(Logger.Level.INFO,"UNIT TEST 1: reply(false)");

			Message replyMsg = msg.reply(false); // API TEST

			Address[] addrs = replyMsg.getRecipients(Message.RecipientType.TO);
			if (addrs != null && addrs.length == 1 && addrs[0].equals(from)
					&& replyMsg.getSubject().equals("Re: " + subj) && msg.isSet(Flags.Flag.ANSWERED)) {
				logger.log(Logger.Level.INFO,"UNIT TEST 1: passed\n");
			} else {
				logger.log(Logger.Level.INFO,"UNIT TEST 1: FAILED\n");
				errors++;
			}

			// END UNIT TEST 1:

			// BEGIN UNIT TEST 2:
			logger.log(Logger.Level.INFO,"UNIT TEST 2: reply(true)");

			replyMsg = msg.reply(true); // API TEST

			addrs = replyMsg.getRecipients(Message.RecipientType.TO);
			if (addrs != null && addrs.length == 2 && ((addrs[0].equals(from) && addrs[1].equals(to))
					|| (addrs[0].equals(to) && addrs[1].equals(from)))) {
				logger.log(Logger.Level.INFO,"UNIT TEST 2: passed\n");
			} else {
				logger.log(Logger.Level.INFO,"UNIT TEST 2: FAILED\n");
				errors++;
			}

			// END UNIT TEST 2:

			// BEGIN UNIT TEST 3:
			logger.log(Logger.Level.INFO,"UNIT TEST 3: reply(false, false)");

			msg = new MimeMessage(session);
			msg.setFrom(from);
			msg.setRecipient(Message.RecipientType.TO, to);
			msg.setSubject(subj);
			msg.setText("test");
			replyMsg = msg.reply(false, false); // API TEST

			addrs = replyMsg.getRecipients(Message.RecipientType.TO);
			if (addrs != null && addrs.length == 1 && addrs[0].equals(from)
					&& replyMsg.getSubject().equals("Re: " + subj) && !msg.isSet(Flags.Flag.ANSWERED)) {
				logger.log(Logger.Level.INFO,"UNIT TEST 3: passed\n");
			} else {
				logger.log(Logger.Level.INFO,"UNIT TEST 3: FAILED\n");
				errors++;
			}

			// END UNIT TEST 3:
			checkStatus();

		} catch (Exception e) {
			logger.log(Logger.Level.ERROR,"Unexpected Exception " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("reply_Test Failed!", e);
		}

	}

	/*
	 * @testName: setFrom_Test
	 * 
	 * @assertion_ids: JavaEE:SPEC:235;
	 * 
	 * @test_Strategy:
	 * 
	 * This test tests the <strong>setFrom(String)</strong> API. It does this by
	 * passing various valid input values and then checking the type of the returned
	 * object. <p>
	 * 
	 * Set the "From" attribute in this Message. <p> api2test: public void
	 * setFrom(String) <p>
	 * 
	 * how2test: Call this API with various addresses, then call call getFrom() api,
	 * if the setFrom values and getFrom values are the same, then this testcase
	 * passes, otherwise it fails. <p>
	 * 
	 * derived from javamail suite setContent_Test
	 */
	@Test
	public void setFrom_Test() throws Exception {

		logger.log(Logger.Level.INFO,"\nTesting class MimeMessage: setFrom(String)\n");

		try {

			// Create Message object
			MimeMessage msg = new MimeMessage(session);

			int i;
			// BEGIN UNIT TEST:
			for (i = 0; i < addrlist.length; i++) {
				// Create the Address object
				InternetAddress addr = new InternetAddress(addrlist[i]);

				// set whom the message is from
				logger.log(Logger.Level.INFO,"UNIT TEST " + (i + 1) + ":  setFrom(String)");

				msg.setFrom(addrlist[i]); // API TEST

				Address[] nowfrom = msg.getFrom();
				String newFrom = nowfrom[0].toString();

				if (newFrom != null) {
					if (addrlist[i].equals(newFrom)) {
						logger.log(Logger.Level.INFO,"setFrom(" + addrlist[i] + ")");
						logger.log(Logger.Level.INFO,"UNIT TEST " + (i + 1) + ":  passed\n");
					} else {
						logger.log(Logger.Level.INFO,"getFrom() :=> " + newFrom);
						logger.log(Logger.Level.INFO,"setFrom(" + addrlist[i] + ")");
						logger.log(Logger.Level.INFO,"UNIT TEST " + (i + 1) + ":  FAILED\n");
						errors++;
					}
				} else {
					logger.log(Logger.Level.INFO,"WARNING: Message " + (i + 1) + " has null 'From' header");
					logger.log(Logger.Level.INFO,"UNIT TEST " + (i + 1) + ":  FAILED\n");
					errors++;
				}
			}
			// END UNIT TEST:

			// BEGIN UNIT TEST:
			// now try with more than one From address
			logger.log(Logger.Level.INFO,"UNIT TEST " + (i + 1) + ":  setFrom(String)");

			String addr1 = "joe@example.com";
			String addr2 = "bob@example.com";
			msg.setFrom(addr1 + "," + addr2); // API TEST

			Address[] afrom = msg.getFrom();
			if (afrom != null && afrom.length == 2
					&& ((afrom[0].toString().equals(addr1) && afrom[1].toString().equals(addr2))
							|| (afrom[0].toString().equals(addr2) && afrom[1].toString().equals(addr1)))) {
				logger.log(Logger.Level.INFO,"setFrom(" + addr1 + "," + addr2 + ")");
				logger.log(Logger.Level.INFO,"UNIT TEST " + (i + 1) + ":  passed\n");
			} else {
				// logger.log(Logger.Level.INFO,"getFrom() :=> " + afrom);
				logger.log(Logger.Level.INFO,"setFrom(" + addr1 + "," + addr2 + ")");
				logger.log(Logger.Level.INFO,"UNIT TEST " + (i + 1) + ":  FAILED\n");
				errors++;
			}
			// END UNIT TEST:
			checkStatus();

		} catch (Exception e) {
			logger.log(Logger.Level.ERROR,"Unexpected Exception " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("reply_Test Failed!", e);
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
			logger.log(Logger.Level.INFO,"Cleanup ;");
			if (store != null) {
				store = null;
			}
			if (session != null) {
				session = null;
			}
		} catch (Exception e) {
			logger.log(Logger.Level.ERROR,"An error occurred in cleanup!", e);
		}
	}
}

class MyMimeMessage extends MimeMessage {

	public MyMimeMessage(Session session) {
		super(session);
	}

	public MimeMessage createMimeMessage(Session session) throws MessagingException {
		return new MyReplyMimeMessage(session);
	}
}

class MyReplyMimeMessage extends MimeMessage {

	public MyReplyMimeMessage(Session session) {
		super(session);
	}
}
