/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.ts.tests.javamail.ee.fetchprofile;

import java.io.Serializable;
import java.lang.System.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.tests.javamail.ee.common.MailTestUtil;

import jakarta.mail.FetchProfile;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Store;

public class fetchprofile_Test implements Serializable {

	private static final Logger logger = (Logger) System.getLogger(fetchprofile_Test.class.getName());

	private transient FetchProfile fp = null;

	private int errors = 0; // number of unit test errors

	private Folder folder;

	private Message[] msgs;

	private Store store;

	private String rootPath;

	private transient Session session;

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
			String user = System.getProperty("javamail.username");
			String password = System.getProperty("javamail.password");
			String mailbox = System.getProperty("javamail.mailbox");
			rootPath = System.getProperty("javamail.root.path");

			String smtpPortStr = System.getProperty("smtp.port");
			int smtpPort = Integer.parseInt(smtpPortStr);
			logger.log(Logger.Level.TRACE,"SMTP Port = " + smtpPort);

			String imapPortStr = System.getProperty("imap.port");
			int imapPort = Integer.parseInt(imapPortStr);
			logger.log(Logger.Level.TRACE,"IMAP Port = " + imapPort);

			MailTestUtil mailTestUtil = new MailTestUtil();
			store = mailTestUtil.connect2host(protocol, host, imapPort, user, password);

			// Get a Folder object
			Folder root = getRootFolder(store);
			folder = root.getFolder(mailbox);

			if (folder == null) {
				throw new Exception("Invalid folder object!");
			}
			folder.open(Folder.READ_ONLY);

			// Create an empty FetchProfile
			fp = new FetchProfile();

			if (fp == null) {
				throw new Exception("Failed to create an empty FetchProfile object!");
			}
			// Get all the messages
			msgs = folder.getMessages();

		} catch (Exception e) {
			logger.log(Logger.Level.ERROR,"Exception : " + e.getMessage());
			logger.log(Logger.Level.ERROR,"Setup Failed!");
		}
	}

	/*
	 * @testName: test1
	 * 
	 * @assertion_ids: JavaEE:SPEC:235;
	 * 
	 * @test_Strategy: verify FetchProfile()
	 */
	// derived from javamail suite fetchProfile_Test class
	@Test
	public void test1() throws Exception {

		try {
			// BEGIN UNIT TEST 1:
			logger.log(Logger.Level.TRACE,"UNIT TEST 1:  FetchProfile()");

			FetchProfile fp = new FetchProfile(); // API TEST

			if (fp != null) {
				logger.log(Logger.Level.TRACE,"UNIT TEST 1: passed\n");
			}
			// END UNIT TEST 1:

		} catch (Exception e) {
			logger.log(Logger.Level.ERROR,"Unexpected Exception " + e.getMessage());
			throw new Exception("test1 Failed");

		}
	} // end of test1()

	/*
	 * @testName: test2
	 * 
	 * @assertion_ids: JavaEE:SPEC:235;
	 * 
	 * @test_Strategy: verify FetchProfile()
	 */
	// derived from javamail suite fetchProfile_Test class
	@Test
	public void test2() throws Exception {

		logger.log(Logger.Level.TRACE,"\nTesting class FetchProfile: add(FetchProfile.Item | String)\n");

		try {

			// BEGIN UNIT TEST 1:

			logger.log(Logger.Level.TRACE,"UNIT TEST 1: add(FetchProfile.Item.ENVELOPE)");

			fp.add(FetchProfile.Item.ENVELOPE); // API TEST

			if (fp.contains(FetchProfile.Item.ENVELOPE))
				logger.log(Logger.Level.TRACE,"UNIT TEST 1: passed.\n");
			else {
				logger.log(Logger.Level.TRACE,"UNIT TEST 1: FAILED.\n");
				errors++;
			}
			// END UNIT TEST 1:
			// BEGIN UNIT TEST 2:

			logger.log(Logger.Level.TRACE,"UNIT TEST 2: add(FetchProfile.Item.FLAGS)");

			fp.add(FetchProfile.Item.FLAGS); // API TEST

			if (fp.contains(FetchProfile.Item.FLAGS))
				logger.log(Logger.Level.TRACE,"UNIT TEST 2: passed.\n");
			else {
				logger.log(Logger.Level.TRACE,"UNIT TEST 2: FAILED.\n");
				errors++;
			}
			// END UNIT TEST 2:
			// BEGIN UNIT TEST 3:

			logger.log(Logger.Level.TRACE,"UNIT TEST 3: add(FetchProfile.Item.CONTENT_INFO)");

			fp.add(FetchProfile.Item.CONTENT_INFO); // API TEST

			if (fp.contains(FetchProfile.Item.CONTENT_INFO))
				logger.log(Logger.Level.TRACE,"UNIT TEST 3: passed.\n");
			else {
				logger.log(Logger.Level.TRACE,"UNIT TEST 3: FAILED.\n");
				errors++;
			}
			// END UNIT TEST 3:
			// BEGIN UNIT TEST 4:

			logger.log(Logger.Level.TRACE,"UNIT TEST 4: add(Subject)");

			fp.add("Subject"); // API TEST

			if (fp.contains("Subject"))
				logger.log(Logger.Level.TRACE,"UNIT TEST 4: passed.\n");
			else {
				logger.log(Logger.Level.TRACE,"UNIT TEST 4: FAILED.\n");
				errors++;
			}
			// END UNIT TEST 4:
			// BEGIN UNIT TEST 5:

			logger.log(Logger.Level.TRACE,"UNIT TEST 5: add(From)");

			fp.add("From"); // API TEST

			if (fp.contains("From"))
				logger.log(Logger.Level.TRACE,"UNIT TEST 5: passed.\n");
			else {
				logger.log(Logger.Level.TRACE,"UNIT TEST 5: FAILED.\n");
				errors++;
			}
			// END UNIT TEST 5:
			// BEGIN UNIT TEST 6:

			logger.log(Logger.Level.TRACE,"UNIT TEST 6: add(X-mailer)");

			fp.add("X-mailer"); // API TEST

			if (fp.contains("X-mailer"))
				logger.log(Logger.Level.TRACE,"UNIT TEST 6: passed.\n");
			else {
				logger.log(Logger.Level.TRACE,"UNIT TEST 6: FAILED.\n");
				errors++;
			}
			// END UNIT TEST 6:

			folder.fetch(msgs, fp);

			if (errors > 0) {
				throw new Exception("test2 Failed: No of unit test failed = " + errors);
			}

		} catch (Exception e) {
			logger.log(Logger.Level.ERROR,"Unexpected Exception " + e.getMessage());
			throw new Exception("test2 Failed");
		}

	}// end of test2()

	/*
	 * @testName: getItems_test
	 * 
	 * @assertion_ids: JavaEE:SPEC:235;
	 * 
	 * @test_Strategy: getItems_test
	 */
	// derived from javamail suite fetchProfile getItems_test
	@Test
	public void getItems_test() throws Exception {

		try {
			// Create an empty FetchProfile
			FetchProfile fp = new FetchProfile();

			// Add header names to Profile object

			fp.add(FetchProfile.Item.ENVELOPE);
			fp.add(FetchProfile.Item.FLAGS);
			fp.add(FetchProfile.Item.CONTENT_INFO);
			fp.add(FetchProfile.Item.SIZE);

			// BEGIN UNIT TEST 1:

			logger.log(Logger.Level.INFO,"UNIT TEST 1: getItems()");

			FetchProfile.Item[] items = fp.getItems(); // API TEST

			boolean foundEnv = false, foundFlags = false, foundCont = false, foundSize = false;
			for (int j = 0; j < items.length; j++) {
				if (items[j] == FetchProfile.Item.ENVELOPE)
					foundEnv = true;
				else if (items[j] == FetchProfile.Item.FLAGS)
					foundFlags = true;
				else if (items[j] == FetchProfile.Item.CONTENT_INFO)
					foundCont = true;
				else if (items[j] == FetchProfile.Item.SIZE)
					foundSize = true;
			}

			if (foundEnv && foundFlags && foundCont && foundSize)
				logger.log(Logger.Level.INFO,"UNIT TEST 1: passed.\n");
			else {
				logger.log(Logger.Level.INFO,"UNIT TEST 1: FAILED.\n");
				errors++;
			}
			// END UNIT TEST 1:
			
			if (errors > 0) {
				throw new Exception("getItems_test Failed: No of unit test failed = " + errors);
			}

		} catch (Exception e) {
			logger.log(Logger.Level.ERROR,"Unexpected Exception " + e.getMessage());
			throw new Exception("getItems_test Failed");
		}

	}

	/* cleanup */
	@AfterEach
	public void cleanup() throws Exception {
		try {
			logger.log(Logger.Level.INFO,"Cleanup ;");
			folder.close(false);
			store.close();
		} catch (Exception e) {
			logger.log(Logger.Level.ERROR,"An error occurred in cleanup!", e);
		}
	}

	/**
	 * Get the root folder.
	 */
	public Folder getRootFolder(Store store) {
		Folder folder = null;
		try {
			if (rootPath.equals(""))
				folder = store.getDefaultFolder();
			else
				folder = store.getFolder(rootPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return folder;
	}

}
