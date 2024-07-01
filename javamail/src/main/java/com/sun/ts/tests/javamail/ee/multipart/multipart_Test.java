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
 * @(#)multipart_Test.java	1.16 03/05/16
 */
package com.sun.ts.tests.javamail.ee.multipart;

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

import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

public class multipart_Test implements Serializable {

	private static final Logger logger = (Logger) System.getLogger(multipart_Test.class.getName());

	static String msgText1 = "This is a message body.\nHere's line two.";

	static String msgText2 = "This is the text in the message attachment.";

	// get this from ts.jte
	private String transport_protocol = null;

	// get this from ts.jte
	private String mailTo = null;

	private String user;

	private String password;

	private transient Session session;

	private String host;

	private transient MailTestUtil mailTestUtil;


	/* Test setup: */
	/*
	 * @class.setup_props: javamail.protocol; javamail.server; javamail.username;
	 * javamail.password ; javamail.mailbox; javamail.root.path; mailuser1;
	 * mailHost; mailFrom; transport_protocol; smtp.port; imap.port;
	 */
	@BeforeEach
	public void setup() throws Exception {
		try {
			// mail recipient
			mailTo = System.getProperty("mailuser1");
			if (mailTo.length() == 0)
				throw new Exception("Invalid mailuser1 - the mail to property");

			transport_protocol = System.getProperty("transport_protocol");
			if (transport_protocol.length() == 0)
				throw new Exception("Invalid transport_protocol");

			user = System.getProperty("javamail.username");
			password = System.getProperty("javamail.password");
			host = System.getProperty("javamail.server");

			String smtpPortStr = System.getProperty("smtp.port");
			int smtpPort = Integer.parseInt(smtpPortStr);
			logger.log(Logger.Level.TRACE,"SMTP Port = " + smtpPort);

			String imapPortStr = System.getProperty("imap.port");
			int imapPort = Integer.parseInt(imapPortStr);
			logger.log(Logger.Level.TRACE,"IMAP Port = " + imapPort);

			mailTestUtil = new MailTestUtil();
			session = mailTestUtil.createSession(host, smtpPortStr, user, password);

		} catch (Exception e) {
			logger.log(Logger.Level.ERROR,"Exception : " + e.getMessage());
			logger.log(Logger.Level.ERROR,"Setup Failed!");
			TestUtil.printStackTrace(e);
		}
	}

	/*
	 * @testName: testAddBodyPart1
	 * 
	 * @assertion_ids: JavaEE:SPEC:235;
	 * 
	 * @test_Strategy: Call api with part argument, then verify by calling getCount.
	 */
	// derived from javamail suite multipart_Test class
	@Test
	public void testAddBodyPart1() throws Exception {
		String msgText = "Testing addBodyPart(BodyPart).\nPASS.";

		try {

			// create a message
			MimeMessage msg = new MimeMessage(session);

			InternetAddress[] address = { new InternetAddress(mailTo) };

			msg.setRecipients(Message.RecipientType.TO, address);
			msg.setSubject("TestAddBodyPart1()" + new Date());

			// create and fill the first message part
			MimeBodyPart mbp1 = new MimeBodyPart();
			mbp1.setText(msgText1);

			// create and fill the second message part
			MimeBodyPart mbp2 = new MimeBodyPart();

			// Use setText(text, charset), to show it off !
			mbp2.setText(msgText2, "us-ascii");

			// create the Multipart and its parts to it
			Multipart mp = new MimeMultipart();

			// BEGIN UNIT TEST:

			mp.addBodyPart(mbp1); // API TEST
			mp.addBodyPart(mbp2); // API TEST
			mp.addBodyPart(mbp1); // API TEST

			if (mp.getCount() == 3)
				logger.log(Logger.Level.TRACE,"Multipart1: passed.\n");
			else {
				throw new Exception("Multipart1: count incorrect- failed\n");
			}
			logger.log(Logger.Level.TRACE,"Count returned is : " + mp.getCount());
			// END UNIT TEST:

			// add the Multipart to the message
			msg.setContent(mp);

			// send the message
			Transport.send(msg);

		} catch (Exception e) {
			logger.log(Logger.Level.ERROR,"Unexpected Exception " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("Call to testAddBodyPart1 Failed!", e);
		}

	}

	/*
	 * @testName: testAddBodyPart2
	 * 
	 * @assertion_ids: JavaEE:SPEC:235;
	 * 
	 * @test_Strategy: Call api with part argument and position, then verify by
	 * calling getCount.
	 */
	@Test
	public void testAddBodyPart2() throws Exception {

		try {

			// create a message
			MimeMessage msg = new MimeMessage(session);

			InternetAddress[] address = { new InternetAddress(mailTo) };

			msg.setRecipients(Message.RecipientType.TO, address);
			msg.setSubject("testAddBodyPart2()" + new Date());

			// create and fill the first message part
			MimeBodyPart mbp1 = new MimeBodyPart();
			mbp1.setText(msgText1);

			// create and fill the second message part
			MimeBodyPart mbp2 = new MimeBodyPart();

			// Use setText(text, charset), to show it off !
			mbp2.setText(msgText2, "us-ascii");

			// create the Multipart and its parts to it
			Multipart mp = new MimeMultipart();

			// BEGIN UNIT TEST:
			mp.addBodyPart(mbp1, 0); // API TEST
			mp.addBodyPart(mbp2, 1); // API TEST
			mp.addBodyPart(mbp1, 2); // API TEST
			mp.addBodyPart(mbp2, 3); // API TEST

			if (mp.getCount() == 4)
				logger.log(Logger.Level.TRACE,"Multipart2: passed.\n");
			else {
				throw new Exception("Multipart2: count incorrect- failed\n");
			}
			logger.log(Logger.Level.TRACE,"Count returned is : " + mp.getCount());

			// END UNIT TEST:

			// add the Multipart to the message
			msg.setContent(mp);

			// send the message
			Transport.send(msg);

		} catch (Exception e) {
			logger.log(Logger.Level.ERROR,"Unexpected Exception " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("Call to testAddBodyPart2 Failed!", e);
		}

	}// end of testAddBodyPart2
		//

	/* cleanup */
	@AfterEach
	public void cleanup() throws Exception {
		try {
			logger.log(Logger.Level.INFO,"Cleanup ;");
		} catch (Exception e) {
			logger.log(Logger.Level.ERROR,"An error occurred in cleanup!", e);
		}
	}
}
