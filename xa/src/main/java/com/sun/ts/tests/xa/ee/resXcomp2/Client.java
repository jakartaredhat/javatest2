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
 * $Id$
 */

/*
 * @(#)Client.java	1.20 02/07/19
 */
package com.sun.ts.tests.xa.ee.resXcomp2;

import java.io.Serializable;
import java.lang.System.Logger;
import java.util.Properties;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.javatest.Status;
import com.sun.ts.lib.harness.ServiceEETest;
import com.sun.ts.lib.util.TSNamingContext;
import com.sun.ts.lib.util.TestUtil;

import jakarta.transaction.UserTransaction;

public class Client implements Serializable {
	
  private static final Logger logger = (Logger) System.getLogger(Client.class.getName());
  
  private TSNamingContext nctx = null;

  private Properties testProps = null;

  private static final String txRef = "java:comp/env/ejb/MyEjbReference";

  private Ejb1Test beanRef = null;

  private UserTransaction ut = null;

  private String tName1 = null;

  // Expected resultSet from JDBC and EIS
  private int expResultstest1ds[] = { 1, 2, 3 };

  /* Test setup: */

  /*
   * @class.setup_props: org.omg.CORBA.ORBClass; java.naming.factory.initial;
   * 
   * @class.testArgs: -ap tssql.stmt
   */
  @BeforeEach
  public void setup(String args[], Properties p) throws Exception {
    try {
      this.testProps = p;
      TestUtil.init(p);
      logger.log(Logger.Level.INFO,"Setup tests");

      logger.log(Logger.Level.INFO,"Obtain naming context");
      nctx = new TSNamingContext();

      logger.log(Logger.Level.INFO,"Lookup Ejb1Test: " + txRef);
      beanRef = (Ejb1Test) nctx.lookup(txRef, Ejb1Test.class);

      logger.log(Logger.Level.INFO,"Lookup java:comp/UserTransaction");
      ut = (UserTransaction) nctx.lookup("java:comp/UserTransaction");

      // Get the table names
      logger.log(Logger.Level.INFO,"Lookup environment variables");
      this.tName1 = TestUtil
          .getTableName(TestUtil.getProperty("Xa_Tab1_Delete"));
      logger.log(Logger.Level.TRACE,"tName1: " + this.tName1);

      logger.log(Logger.Level.INFO,"Initialize " + txRef);
      beanRef.initialize(testProps);

      logger.log(Logger.Level.INFO,"Initialize logging data from server in Client");
      beanRef.initLogging(p);

      logger.log(Logger.Level.INFO,"Setup ok");
    } catch (Exception e) {
      logger.log(Logger.Level.ERROR,"Exception in setup: ", e);
      throw new Exception("setup failed", e);
    }
  }

  /* Test cleanup */
@AfterEach
  public void cleanup() throws Exception {
    logger.log(Logger.Level.INFO,"Cleanup ok");
  }

  /* Run tests */
  /*
   * @testName: test5
   *
   * @assertion_ids: JavaEE:SPEC:74; JavaEE:SPEC:68; JavaEE:SPEC:69;
   * JavaEE:SPEC:84
   *
   * @test_Strategy: Contact a Servlet, EJB or JSP. Obtain the UserTransaction
   * interface. Perform global transactions using the Ejb1Test (deployed as
   * TX_REQUIRED) to a single RDBMS table.
   * 
   * Insert/Delete followed by a commit to a single table.
   *
   * Database Access is performed from Ejb1Test EJB. CLIENT: tx_start, EJB1:
   * Insert, EJB2: Insert, tx_commit
   */
@Test
  public void test5() throws Exception {
    String testname = "test5";
    boolean testResult = false;
    String tName1 = this.tName1;

    try {
      logger.log(Logger.Level.TRACE,testname);
      logger.log(Logger.Level.INFO,"Transaction propagation from Servlet, EJB or JSP");
      logger.log(Logger.Level.INFO,"Insert/Delete followed by a commit to a single table");
      logger.log(Logger.Level.INFO,"Database access is performed from EJB1Test and EJB2Test");

      logger.log(Logger.Level.INFO,"Creating the table");
      ut.begin();
      beanRef.dbConnect(tName1);
      beanRef.destroyData(tName1);
      beanRef.dbUnConnect(tName1);
      ut.commit();

      logger.log(Logger.Level.INFO,"Insert rows");
      ut.begin();
      beanRef.dbConnect(tName1);
      logger.log(Logger.Level.INFO,"Calling insert in Ejb1");
      beanRef.insert(tName1);
      beanRef.dbUnConnect(tName1);
      ut.commit();

      logger.log(Logger.Level.INFO,"Get test results");
      ut.begin();
      beanRef.dbConnect(tName1);
      testResult = beanRef.verifyData(new String("commit"), tName1,
          expResultstest1ds);

      logger.log(Logger.Level.TRACE,"Test results");
      if (!testResult) {
        logger.log(Logger.Level.INFO,testResult + " - verification of data failed");
      } else {
        logger.log(Logger.Level.INFO,testResult + " - verification of data successfull");
      }
      beanRef.dbUnConnect(tName1);
      ut.commit();

    } catch (Exception e) {
      logger.log(Logger.Level.ERROR,"Caught exception: " + e.getMessage());
      TestUtil.printStackTrace(e);
      throw new Exception(testname + " failed", e);
    } finally {
      // cleanup the bean
      try {
        ut.begin();
        beanRef.dbConnect(tName1);
        beanRef.destroyData(tName1);
        beanRef.dbUnConnect(tName1);
        ut.commit();
      } catch (Exception e) {
      }
      ;
      if (!testResult)
        throw new Exception(testname + " failed");
    }
  }

  /*
   * @testName: test6
   *
   * @assertion_ids: JavaEE:SPEC:74; JavaEE:SPEC:68; JavaEE:SPEC:69;
   * JavaEE:SPEC:84
   *
   * @test_Strategy: Contact a Servlet, EJB or JSP. Obtain the UserTransaction
   * interface. Perform global transactions using the Ejb1Test (deployed as
   * TX_REQUIRED) to a single RDBMS table.
   * 
   * Insert/Delete followed by a rollback to a single table.
   *
   * Database Access is performed from Ejb1Test EJB. CLIENT: tx_start, EJB1:
   * Insert, EJB2: Insert, tx_rollback
   */
@Test
  public void test6() throws Exception {
    String testname = "test6";
    boolean testResult = false;
    String tName1 = this.tName1;

    try {
      logger.log(Logger.Level.TRACE,testname);
      logger.log(Logger.Level.INFO,"Transaction propagation from Servlet, EJB or JSP");
      logger.log(Logger.Level.INFO,"Insert/Delete followed by a rollback to a single table");
      logger.log(Logger.Level.INFO,"Database access is performed from EJB1Test and EJB2Test");

      logger.log(Logger.Level.INFO,"Creating the table");
      ut.begin();
      beanRef.dbConnect(tName1);
      beanRef.destroyData(tName1);
      beanRef.dbUnConnect(tName1);
      ut.commit();

      logger.log(Logger.Level.INFO,"Insert rows");
      ut.begin();
      beanRef.dbConnect(tName1);
      logger.log(Logger.Level.INFO,"Calling insert in Ejb1");
      beanRef.insert(tName1);
      beanRef.dbUnConnect(tName1);
      ut.rollback();

      logger.log(Logger.Level.INFO,"Get test results");
      ut.begin();
      beanRef.dbConnect(tName1);
      testResult = beanRef.verifyData(new String("rollback"), tName1,
          expResultstest1ds);

      logger.log(Logger.Level.TRACE,"Test results");
      if (!testResult) {
        logger.log(Logger.Level.INFO,testResult + " - verification of data failed");
      } else {
        logger.log(Logger.Level.INFO,testResult + " - verification of data successfull");
      }
      beanRef.dbUnConnect(tName1);
      ut.commit();

    } catch (Exception e) {
      logger.log(Logger.Level.ERROR,"Caught exception: " + e.getMessage());
      TestUtil.printStackTrace(e);
      throw new Exception(testname + " failed", e);
    } finally {
      // cleanup the bean
      try {
        ut.begin();
        beanRef.dbConnect(tName1);
        beanRef.destroyData(tName1);
        beanRef.dbUnConnect(tName1);
        ut.commit();
      } catch (Exception e) {
      }
      ;
      if (!testResult)
        throw new Exception(testname + " failed");
    }
  }

  /*
   * @testName: test7
   *
   * @assertion_ids: JavaEE:SPEC:74; JavaEE:SPEC:69; JavaEE:SPEC:84
   *
   * @test_Strategy: Contact a Servlet, EJB or JSP. Obtain the UserTransaction
   * interface. Perform global transactions using the Ejb1Test (deployed as
   * TX_REQUIRED) to a single RDBMS table.
   * 
   * Insert/Delete followed by a commit to a single table.
   *
   * Database Access is performed from Ejb1Test EJB. CLIENT: tx_start, EJB1:
   * Insert, EJB2: Insert, tx_commit
   */
@Test
  public void test7() throws Exception {
    String testname = "test7";
    boolean testResult = false;
    // String tName1 = this.tName1;

    try {
      logger.log(Logger.Level.TRACE,testname);
      logger.log(Logger.Level.INFO,"Transaction propagation from Servlet, EJB or JSP");
      logger.log(Logger.Level.INFO,"Insert/Delete followed by a commit to a single table");
      logger.log(Logger.Level.INFO,"Database access is performed from EJB1Test and EJB2Test");

      logger.log(Logger.Level.INFO,"Creating the table");
      ut.begin();
      beanRef.dbConnect("EIS");
      beanRef.destroyData("EIS");
      beanRef.dbUnConnect("EIS");
      ut.commit();

      logger.log(Logger.Level.INFO,"Insert rows");
      ut.begin();
      beanRef.dbConnect("EIS");
      logger.log(Logger.Level.INFO,"Calling insert in Ejb1");
      beanRef.insert("EIS");
      beanRef.dbUnConnect("EIS");
      ut.commit();

      logger.log(Logger.Level.INFO,"Get test results");
      ut.begin();
      beanRef.dbConnect("EIS");
      testResult = beanRef.verifyData("commit", "EIS", expResultstest1ds);

      logger.log(Logger.Level.TRACE,"Test results : " + testResult);
      if (!testResult) {
        logger.log(Logger.Level.INFO,testResult + " - verification of data failed");
      } else {
        logger.log(Logger.Level.INFO,testResult + " - verification of data successfull");
      }
      beanRef.dbUnConnect("EIS");
      ut.commit();

    } catch (Exception e) {
      logger.log(Logger.Level.ERROR,"Caught exception: " + e.getMessage());
      TestUtil.printStackTrace(e);
      throw new Exception(testname + " failed", e);
    } finally {
      // cleanup the bean
      try {
        ut.begin();
        beanRef.dbConnect("EIS");
        beanRef.destroyData("EIS");
        beanRef.dbUnConnect("EIS");
        ut.commit();
      } catch (Exception e) {
      }
      ;
      if (!testResult)
        throw new Exception(testname + " failed");
    }
  }

  /*
   * @testName: test8
   *
   * @assertion_ids: JavaEE:SPEC:74; JavaEE:SPEC:69; JavaEE:SPEC:84
   *
   * @test_Strategy: Contact a Servlet, EJB or JSP. Obtain the UserTransaction
   * interface. Perform global transactions using the Ejb1Test (deployed as
   * TX_REQUIRED) to a single RDBMS table.
   * 
   * Insert/Delete followed by a rollback to a single table.
   *
   * Database Access is performed from Ejb1Test EJB. CLIENT: tx_start, EJB1:
   * Insert, EJB2: Insert, tx_rollback
   */
@Test
  public void test8() throws Exception {
    String testname = "test8";
    boolean testResult = false;
    // String tName1 = this.tName1;

    try {
      logger.log(Logger.Level.TRACE,testname);
      logger.log(Logger.Level.INFO,"Transaction propagation from Servlet, EJB or JSP");
      logger.log(Logger.Level.INFO,"Insert/Delete followed by a rollback to a single table");
      logger.log(Logger.Level.INFO,"Database access is performed from EJB1Test and EJB2Test");

      logger.log(Logger.Level.INFO,"Creating the table");
      ut.begin();
      beanRef.dbConnect("EIS");
      beanRef.destroyData("EIS");
      beanRef.dbUnConnect("EIS");
      ut.commit();

      logger.log(Logger.Level.INFO,"Insert rows");
      ut.begin();
      beanRef.dbConnect("EIS");
      logger.log(Logger.Level.INFO,"Calling insert in Ejb1");
      beanRef.insert("EIS");
      beanRef.dbUnConnect("EIS");
      ut.rollback();

      logger.log(Logger.Level.INFO,"Get test results");
      ut.begin();
      beanRef.dbConnect("EIS");
      testResult = beanRef.verifyData("rollback", "EIS", expResultstest1ds);

      logger.log(Logger.Level.TRACE,"Test results : " + testResult);
      if (!testResult) {
        logger.log(Logger.Level.INFO,testResult + " - verification of data failed");
      } else {
        logger.log(Logger.Level.INFO,testResult + " - verification of data successfull");
      }
      beanRef.dbUnConnect("EIS");
      ut.commit();

    } catch (Exception e) {
      logger.log(Logger.Level.ERROR,"Caught exception: " + e.getMessage());
      TestUtil.printStackTrace(e);
      throw new Exception(testname + " failed", e);
    } finally {
      // cleanup the bean
      try {
        ut.begin();
        beanRef.dbConnect("EIS");
        beanRef.destroyData("EIS");
        beanRef.dbUnConnect("EIS");
        ut.commit();
      } catch (Exception e) {
      }
      ;
      if (!testResult)
        throw new Exception(testname + " failed");
    }
  }

}
