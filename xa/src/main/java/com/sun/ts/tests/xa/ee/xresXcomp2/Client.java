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
 * @(#)Client.java	1.3 03/05/16
 */

/*
 * @(#)Client.java	1.20 02/07/19
 */
package com.sun.ts.tests.xa.ee.xresXcomp2;

import java.io.Serializable;
import java.lang.System.Logger;
import java.util.Properties;
import java.util.Vector;

import com.sun.javatest.Status;
import com.sun.ts.lib.harness.ServiceEETest;
import com.sun.ts.lib.util.TSNamingContext;
import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.xa.ee.xresXcomp1.Ejb1TestEJB;

import jakarta.transaction.UserTransaction;

public class Client implements Serializable {
  private TSNamingContext nctx = null;

  private Properties testProps = null;

  private static final String txRef = "java:comp/env/ejb/MyEjbReference";
  
  private static final Logger logger = (Logger) System.getLogger(Client.class.getName());


  private Ejb1Test beanRef = null;

  private UserTransaction ut = null;

  private String tName1 = null;

  private Integer tSize = null;
  // private Integer fromKey1 = null;
  // private Integer fromKey2 = null;
  // private Integer toKey2 = null;

  // Expected resultSet from JDBC and EIS
  // private int expResultstest1ds1 [] = { 1,2,3,4,5 };
  // private int expResultstest1ds2 [] = { 1,2,3 };

  /* Test setup: */

  /*
   * @class.setup_props: org.omg.CORBA.ORBClass; java.naming.factory.initial;
   * 
   * @class.testArgs: -ap tssql.stmt
   */
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

      // Get the table sizes
      this.tSize = (Integer) nctx.lookup("java:comp/env/size");
      logger.log(Logger.Level.TRACE,"tSize: " + this.tSize);

      // this.fromKey1 = (Integer) nctx.lookup("java:comp/env/fromKey1");
      // logger.log(Logger.Level.TRACE,"fromKey1: " + this.fromKey1);

      // this.fromKey2 = (Integer) nctx.lookup("java:comp/env/fromKey2");
      // logger.log(Logger.Level.TRACE,"fromKey2: " + this.fromKey2);

      // this.toKey2 = (Integer) nctx.lookup("java:comp/env/toKey2");
      // logger.log(Logger.Level.TRACE,"toKey2: " + this.toKey2);

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

  public void cleanup() throws Exception {
    logger.log(Logger.Level.INFO,"Cleanup ok");
  }

  /* Run tests */
  /*
   * @testName: test17
   *
   * @assertion_ids: JavaEE:SPEC:90; JavaEE:SPEC:92; JavaEE:SPEC:79;
   * JavaEE:SPEC:76; JavaEE:SPEC:74; JavaEE:SPEC:70
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
  public void test17() throws Exception {
    String testname = "test17";
    Vector dbResults = new Vector();
    boolean testResult = false;
    boolean b1, b2;
    b1 = b2 = false;
    String tName1 = this.tName1;
    int tSize = this.tSize.intValue();
    // int tRng = this.fromKey1.intValue();

    try {
      logger.log(Logger.Level.TRACE,testname);
      logger.log(Logger.Level.INFO,"Transaction propagation from Servlet, EJB or JSP");
      logger.log(Logger.Level.INFO,
          "Insert/Delete followed by a commit to a single table in EIS and JDBC");
      logger.log(Logger.Level.INFO,"Database access is performed from EJB1Test");

      logger.log(Logger.Level.INFO,"Creating the data in JDBC table");
      ut.begin();
      beanRef.txDbConnect(tName1);
      beanRef.createData(tName1);
      beanRef.txDbUnConnect(tName1);
      ut.commit();

      logger.log(Logger.Level.INFO,"Creating the data in EIS table");
      ut.begin();
      beanRef.txDbConnect("EIS");
      beanRef.createData("EIS");
      beanRef.txDbUnConnect("EIS");
      ut.commit();

      logger.log(Logger.Level.INFO,"Insert and delete some rows");
      ut.begin();

      beanRef.dbConnect(tName1);
      logger.log(Logger.Level.INFO,"Inserting 1 new rows in JDBC");
      if (beanRef.insert(tName1, tSize + 1))
        tSize++;
      beanRef.dbUnConnect(tName1);

      ut.commit();

      logger.log(Logger.Level.INFO,"Get test results for JDBC");
      ut.begin();
      beanRef.txDbConnect(tName1);
      dbResults = beanRef.getResults(tName1);
      for (int i = 0; i < dbResults.size(); i++)
        logger.log(Logger.Level.INFO,
            "JDBC dbResults.elementAt" + i + " :" + dbResults.elementAt(i));

      beanRef.txDbUnConnect(tName1);
      ut.commit();

      logger.log(Logger.Level.INFO,"Verifying the test results of JDBC");
      for (int i = 1; i <= tSize; i++) {
        if (dbResults.contains(new Integer(i)))
          b1 = true;
        else {
          b1 = false;
          break;
        }
      }

      dbResults = null;
      logger.log(Logger.Level.INFO,"Get test results for EIS");
      ut.begin();
      beanRef.txDbConnect("EIS");
      dbResults = beanRef.getResults("EIS");
      beanRef.txDbUnConnect("EIS");
      ut.commit();
      for (int i = 0; i < dbResults.size(); i++)
        logger.log(Logger.Level.INFO,
            "EIS dbResults.elementAt" + i + " :" + dbResults.elementAt(i));

      logger.log(Logger.Level.INFO,"Verifying the test results of EIS");
      for (int i = 1; i <= tSize; i++) {
        if (dbResults.contains(new Integer(i).toString()))
          b2 = true;
        else {
          b2 = false;
          break;
        }
      }
      logger.log(Logger.Level.TRACE,"b1 : " + b1);
      logger.log(Logger.Level.TRACE,"b2 : " + b2);

      if (b1 && b2)
        testResult = true;

    } catch (Exception e) {
      logger.log(Logger.Level.ERROR,"Caught exception: " + e.getMessage());
      TestUtil.printStackTrace(e);
      throw new Exception(testname + " failed", e);
    } finally {
      // cleanup the bean
      try {
        logger.log(Logger.Level.INFO,"Finally cleaning the test data for JDBC");
        ut.begin();
        beanRef.txDbConnect(tName1);
        beanRef.destroyData(tName1);
        beanRef.txDbUnConnect(tName1);
        ut.commit();
        logger.log(Logger.Level.INFO,"Finally cleaning the test data for EIS");
        ut.begin();
        beanRef.txDbConnect("EIS");
        beanRef.destroyData("EIS");
        beanRef.txDbUnConnect("EIS");
        ut.commit();
      } catch (Exception e) {
      }
      ;
      if (!testResult)
        throw new Exception(testname + " failed");
    }
  }

  /*
   * @testName: test18
   *
   * @assertion_ids: JavaEE:SPEC:90; JavaEE:SPEC:92; JavaEE:SPEC:79;
   * JavaEE:SPEC:76; JavaEE:SPEC:74; JavaEE:SPEC:70
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
  public void test18() throws Exception {
    String testname = "test18";
    Vector dbResults = new Vector();
    boolean testResult = false;
    boolean b1, b2, b3, b4;
    b1 = b2 = b3 = b4 = false;
    String tName1 = this.tName1;
    int tSize = this.tSize.intValue();
    int tSizeOrig = this.tSize.intValue();

    try {
      logger.log(Logger.Level.TRACE,testname);
      logger.log(Logger.Level.INFO,"Transaction propagation from Servlet, EJB or JSP");
      logger.log(Logger.Level.INFO,
          "Insert/Delete followed by a commit to a single table in EIS and JDBC");
      logger.log(Logger.Level.INFO,"Database access is performed from EJB1Test");

      logger.log(Logger.Level.INFO,"Creating the data in JDBC table");
      ut.begin();
      beanRef.txDbConnect(tName1);
      beanRef.createData(tName1);
      beanRef.txDbUnConnect(tName1);
      ut.commit();

      logger.log(Logger.Level.INFO,"Creating the data in EIS table");
      ut.begin();
      beanRef.txDbConnect("EIS");
      beanRef.createData("EIS");
      beanRef.txDbUnConnect("EIS");
      ut.commit();

      logger.log(Logger.Level.INFO,"Insert and delete some rows");
      ut.begin();

      beanRef.dbConnect(tName1);
      logger.log(Logger.Level.INFO,"Inserting 1 new rows in JDBC");
      if (beanRef.insert(tName1, tSize + 1))
        tSize++;
      beanRef.dbUnConnect(tName1);

      ut.rollback();

      logger.log(Logger.Level.INFO,"Get test results for JDBC");
      ut.begin();
      beanRef.txDbConnect(tName1);
      dbResults = beanRef.getResults(tName1);
      for (int i = 0; i < dbResults.size(); i++)
        logger.log(Logger.Level.INFO,
            "JDBC dbResults.elementAt" + i + " :" + dbResults.elementAt(i));

      beanRef.txDbUnConnect(tName1);
      ut.commit();

      logger.log(Logger.Level.INFO,"Verifying the test results of JDBC");
      for (int i = 1; i <= tSizeOrig; i++) {
        if (dbResults.contains(new Integer(i))) {
          b1 = true;
        } else {
          b1 = false;
          break;
        }
      }
      for (int j = tSize; j > tSizeOrig; j--) {
        if (dbResults.contains(new Integer(j))) {
          b2 = false;
          break;
        } else {
          b2 = true;
        }
      }

      dbResults = null;
      logger.log(Logger.Level.INFO,"Get test results for EIS");
      ut.begin();
      beanRef.txDbConnect("EIS");
      dbResults = beanRef.getResults("EIS");
      beanRef.txDbUnConnect("EIS");
      ut.commit();
      for (int i = 0; i < dbResults.size(); i++)
        logger.log(Logger.Level.INFO,
            "EIS dbResults.elementAt" + i + " :" + dbResults.elementAt(i));

      logger.log(Logger.Level.INFO,"Verifying the test results of EIS");
      for (int i = 1; i <= tSizeOrig; i++) {
        if (dbResults.contains((new Integer(i)).toString())) {
          b3 = true;
        } else {
          b3 = false;
          break;
        }
      }
      for (int j = tSize; j > tSizeOrig; j--) {
        if (dbResults.contains((new Integer(j)).toString())) {
          b4 = false;
          break;
        } else {
          b4 = true;
        }
      }
      logger.log(Logger.Level.TRACE,"b1 : " + b1);
      logger.log(Logger.Level.TRACE,"b2 : " + b2);
      logger.log(Logger.Level.TRACE,"b3 : " + b3);
      logger.log(Logger.Level.TRACE,"b4 : " + b4);

      if (b1 && b2 && b3 && b4)
        testResult = true;

    } catch (Exception e) {
      logger.log(Logger.Level.ERROR,"Caught exception: " + e.getMessage());
      TestUtil.printStackTrace(e);
      throw new Exception(testname + " failed", e);
    } finally {
      // cleanup the bean
      try {
        logger.log(Logger.Level.INFO,"Finally cleaning the test data for JDBC");
        ut.begin();
        beanRef.txDbConnect(tName1);
        beanRef.destroyData(tName1);
        beanRef.txDbUnConnect(tName1);
        ut.commit();
        logger.log(Logger.Level.INFO,"Finally cleaning the test data for EIS");
        ut.begin();
        beanRef.txDbConnect("EIS");
        beanRef.destroyData("EIS");
        beanRef.txDbUnConnect("EIS");
        ut.commit();
      } catch (Exception e) {
      }
      ;
      if (!testResult)
        throw new Exception(testname + " failed");
    }
  }

  /*
   * @testName: test19
   *
   * @assertion_ids: JavaEE:SPEC:90; JavaEE:SPEC:92; JavaEE:SPEC:79;
   * JavaEE:SPEC:76; JavaEE:SPEC:74; JavaEE:SPEC:70
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
  public void test19() throws Exception {
    String testname = "test19";
    Vector dbResults = new Vector();
    boolean testResult = false;
    boolean b1, b2, b3, b4, b5;
    b1 = b2 = b3 = b4 = b5 = false;
    String tName1 = this.tName1;
    int tSize = this.tSize.intValue();
    int tSizeOrig = this.tSize.intValue();
    int tSizeDuplicate = this.tSize.intValue() + 1;
    // int tRng = this.fromKey1.intValue();

    try {
      logger.log(Logger.Level.TRACE,testname);
      logger.log(Logger.Level.INFO,"Transaction propagation from Servlet, EJB or JSP");
      logger.log(Logger.Level.INFO,
          "Insert/Delete followed by a commit to a single table in EIS and JDBC");
      logger.log(Logger.Level.INFO,"Database access is performed from EJB1Test");

      logger.log(Logger.Level.INFO,"Creating the data in JDBC table");
      ut.begin();
      beanRef.txDbConnect(tName1);
      beanRef.createData(tName1);
      beanRef.txDbUnConnect(tName1);
      ut.commit();

      logger.log(Logger.Level.INFO,"Creating the data in EIS table");
      ut.begin();
      beanRef.txDbConnect("EIS");
      beanRef.createData("EIS");
      beanRef.txDbUnConnect("EIS");
      ut.commit();

      try {
        logger.log(Logger.Level.INFO,"Insert and delete some rows");
        ut.begin();

        beanRef.dbConnect(tName1);
        logger.log(Logger.Level.INFO,"Inserting 1 new rows in JDBC");
        if (beanRef.insert(tName1, tSize + 1))
          tSize++;
        beanRef.dbUnConnect(tName1);

        logger.log(Logger.Level.INFO,"Insert rows using notx whitebox");
        logger.log(Logger.Level.INFO,"Calling insertDup in Ejb1 with value = " + tSizeDuplicate);
        beanRef.insertDup("EIS", Integer.toString(tSizeDuplicate)); // 6

        ut.commit();
      } catch (jakarta.transaction.RollbackException rex) {
        TestUtil.printStackTrace(rex);
        b5 = true;
        logger.log(Logger.Level.INFO,"Captured Rollback Exception : b5 : " + b5);
      } catch (Exception ex) {
        TestUtil.printStackTrace(ex);
        b5 = false;
        logger.log(Logger.Level.INFO,"Captured Exception : b5 : " + b5);
      }

      logger.log(Logger.Level.INFO,"Get test results for JDBC");
      ut.begin();
      beanRef.txDbConnect(tName1);
      dbResults = beanRef.getResults(tName1);
      for (int i = 0; i < dbResults.size(); i++)
        logger.log(Logger.Level.INFO,
            "JDBC dbResults.elementAt" + i + " :" + dbResults.elementAt(i));

      beanRef.txDbUnConnect(tName1);
      ut.commit();

      logger.log(Logger.Level.INFO,"Verifying the test results of JDBC");
      for (int i = 1; i <= tSizeOrig; i++) {
        if (dbResults.contains(new Integer(i))) {
          b1 = true;
        } else {
          b1 = false;
          break;
        }
      }
      for (int j = tSize; j > tSizeOrig; j--) {
        if (dbResults.contains(new Integer(j))) {
          b2 = false;
          break;
        } else {
          b2 = true;
        }
      }

      dbResults = null;
      logger.log(Logger.Level.INFO,"Get test results for EIS");
      ut.begin();
      beanRef.txDbConnect("EIS");
      dbResults = beanRef.getResults("EIS");
      beanRef.txDbUnConnect("EIS");
      ut.commit();
      for (int i = 0; i < dbResults.size(); i++)
        logger.log(Logger.Level.TRACE,
            "EIS dbResults.elementAt" + i + " :" + dbResults.elementAt(i));

      logger.log(Logger.Level.INFO,"Verifying the test results of EIS");
      for (int i = 1; i <= (tSizeOrig + 1); i++) { // to include tSizeDuplicate
                                                   // also
        if (dbResults.contains((new Integer(i)).toString())) {
          b3 = true;
        } else {
          b3 = false;
          break;
        }
      }
      for (int j = tSize; j > tSizeOrig; j--) {
        if (j == tSizeDuplicate) {
          b4 = true;
          continue;
        }
        // else {
        // if( dbResults.contains((new Integer(j)).toString()) ) {
        // b4 = false;
        // break;
        // }else
        // {
        // b4 = true;
        // }
        // }
      }
      logger.log(Logger.Level.TRACE,"b1 : " + b1);
      logger.log(Logger.Level.TRACE,"b2 : " + b2);
      logger.log(Logger.Level.TRACE,"b3 : " + b3);
      logger.log(Logger.Level.TRACE,"b4 : " + b4);
      logger.log(Logger.Level.TRACE,"b5 : " + b5);

      if (b1 && b2 && b3 && b4 && b5)
        testResult = true;

    } catch (Exception e) {
      logger.log(Logger.Level.ERROR,"Caught exception: " + e.getMessage());
      TestUtil.printStackTrace(e);
      throw new Exception(testname + " failed", e);
    } finally {
      // cleanup the bean
      try {
        logger.log(Logger.Level.INFO,"Finally cleaning the test data for JDBC");
        ut.begin();
        beanRef.txDbConnect(tName1);
        beanRef.destroyData(tName1);
        beanRef.txDbUnConnect(tName1);
        ut.commit();
        logger.log(Logger.Level.INFO,"Finally cleaning the test data for EIS");
        ut.begin();
        beanRef.txDbConnect("EIS");
        beanRef.destroyData("EIS");
        beanRef.txDbUnConnect("EIS");
        ut.commit();
      } catch (Exception e) {
      }
      ;
      if (!testResult)
        throw new Exception(testname + " failed");
    }
  }

  /*
   * @testName: test20
   *
   * @assertion_ids: JavaEE:SPEC:90; JavaEE:SPEC:92; JavaEE:SPEC:79;
   * JavaEE:SPEC:76; JavaEE:SPEC:74; JavaEE:SPEC:70
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
  public void test20() throws Exception {
    String testname = "test20";
    Vector dbResults = new Vector();
    boolean testResult = false;
    boolean b1, b2;
    b1 = b2 = false;
    String tName1 = this.tName1;
    int tSize = this.tSize.intValue();
    // int tRng = this.fromKey1.intValue();

    try {
      logger.log(Logger.Level.TRACE,testname);
      logger.log(Logger.Level.INFO,"Transaction propagation from Servlet, EJB or JSP");
      logger.log(Logger.Level.INFO,
          "Insert/Delete followed by a commit to a single table in EIS and JDBC");
      logger.log(Logger.Level.INFO,"Database access is performed from EJB1Test");

      logger.log(Logger.Level.INFO,"Creating the data in JDBC table");
      ut.begin();
      beanRef.txDbConnect(tName1);
      beanRef.createData(tName1);
      beanRef.txDbUnConnect(tName1);
      ut.commit();

      logger.log(Logger.Level.INFO,"Creating the data in EIS table");
      ut.begin();
      beanRef.txDbConnect("EIS");
      beanRef.createData("EIS");
      beanRef.txDbUnConnect("EIS");
      ut.commit();

      logger.log(Logger.Level.INFO,"Insert and delete some rows");
      ut.begin();

      beanRef.dbConnect("EIS");
      logger.log(Logger.Level.INFO,"Inserting 1 new rows in EIS");
      if (beanRef.insert("EIS", tSize + 1))
        tSize++;
      beanRef.dbUnConnect("EIS");

      ut.commit();

      logger.log(Logger.Level.INFO,"Get test results for JDBC");
      ut.begin();
      beanRef.txDbConnect(tName1);
      dbResults = beanRef.getResults(tName1);
      for (int i = 0; i < dbResults.size(); i++)
        logger.log(Logger.Level.INFO,
            "JDBC dbResults.elementAt" + i + " :" + dbResults.elementAt(i));

      beanRef.txDbUnConnect(tName1);
      ut.commit();

      logger.log(Logger.Level.INFO,"Verifying the test results of JDBC");
      for (int i = 1; i <= tSize; i++) {
        if (dbResults.contains(new Integer(i)))
          b1 = true;
        else {
          b1 = false;
          break;
        }
      }

      dbResults = null;
      logger.log(Logger.Level.INFO,"Get test results for EIS");
      ut.begin();
      beanRef.txDbConnect("EIS");
      dbResults = beanRef.getResults("EIS");
      beanRef.txDbUnConnect("EIS");
      ut.commit();
      for (int i = 0; i < dbResults.size(); i++)
        logger.log(Logger.Level.INFO,
            "EIS dbResults.elementAt" + i + " :" + dbResults.elementAt(i));

      logger.log(Logger.Level.INFO,"Verifying the test results of EIS");
      for (int i = 1; i <= tSize; i++) {
        if (dbResults.contains(new Integer(i).toString()))
          b2 = true;
        else {
          b2 = false;
          break;
        }
      }
      logger.log(Logger.Level.TRACE,"b1 : " + b1);
      logger.log(Logger.Level.TRACE,"b2 : " + b2);

      if (b1 && b2)
        testResult = true;

    } catch (Exception e) {
      logger.log(Logger.Level.ERROR,"Caught exception: " + e.getMessage());
      TestUtil.printStackTrace(e);
      throw new Exception(testname + " failed", e);
    } finally {
      // cleanup the bean
      try {
        logger.log(Logger.Level.INFO,"Finally cleaning the test data for JDBC");
        ut.begin();
        beanRef.txDbConnect(tName1);
        beanRef.destroyData(tName1);
        beanRef.txDbUnConnect(tName1);
        ut.commit();
        logger.log(Logger.Level.INFO,"Finally cleaning the test data for EIS");
        ut.begin();
        beanRef.txDbConnect("EIS");
        beanRef.destroyData("EIS");
        beanRef.txDbUnConnect("EIS");
        ut.commit();
      } catch (Exception e) {
      }
      ;
      if (!testResult)
        throw new Exception(testname + " failed");
    }
  }

  /*
   * @testName: test21
   *
   * @assertion_ids: JavaEE:SPEC:90; JavaEE:SPEC:92; JavaEE:SPEC:79;
   * JavaEE:SPEC:76; JavaEE:SPEC:74; JavaEE:SPEC:70
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
  public void test21() throws Exception {
    String testname = "test21";
    Vector dbResults = new Vector();
    boolean testResult = false;
    boolean b1, b2, b3, b4;
    b1 = b2 = b3 = b4 = false;
    String tName1 = this.tName1;
    int tSize = this.tSize.intValue();
    int tSizeOrig = this.tSize.intValue();

    try {
      logger.log(Logger.Level.TRACE,testname);
      logger.log(Logger.Level.INFO,"Transaction propagation from Servlet, EJB or JSP");
      logger.log(Logger.Level.INFO,
          "Insert/Delete followed by a commit to a single table in EIS and JDBC");
      logger.log(Logger.Level.INFO,"Database access is performed from EJB1Test");

      logger.log(Logger.Level.INFO,"Creating the data in JDBC table");
      ut.begin();
      beanRef.txDbConnect(tName1);
      beanRef.createData(tName1);
      beanRef.txDbUnConnect(tName1);
      ut.commit();

      logger.log(Logger.Level.INFO,"Creating the data in EIS table");
      ut.begin();
      beanRef.txDbConnect("EIS");
      beanRef.createData("EIS");
      beanRef.txDbUnConnect("EIS");
      ut.commit();

      logger.log(Logger.Level.INFO,"Insert and delete some rows");
      ut.begin();

      beanRef.dbConnect("EIS");
      logger.log(Logger.Level.INFO,"Inserting 1 new rows in EIS");
      if (beanRef.insert("EIS", tSize + 1))
        tSize++;
      beanRef.dbUnConnect("EIS");

      ut.rollback();

      logger.log(Logger.Level.INFO,"Get test results for JDBC");
      ut.begin();
      beanRef.txDbConnect(tName1);
      dbResults = beanRef.getResults(tName1);
      for (int i = 0; i < dbResults.size(); i++)
        logger.log(Logger.Level.INFO,
            "JDBC dbResults.elementAt" + i + " :" + dbResults.elementAt(i));

      beanRef.txDbUnConnect(tName1);
      ut.commit();

      logger.log(Logger.Level.INFO,"Verifying the test results of JDBC");
      for (int i = 1; i <= tSizeOrig; i++) {
        if (dbResults.contains(new Integer(i))) {
          b1 = true;
        } else {
          b1 = false;
          break;
        }
      }
      for (int j = tSize; j > tSizeOrig; j--) {
        if (dbResults.contains(new Integer(j))) {
          b2 = false;
          break;
        } else {
          b2 = true;
        }
      }

      dbResults = null;
      logger.log(Logger.Level.INFO,"Get test results for EIS");
      ut.begin();
      beanRef.txDbConnect("EIS");
      dbResults = beanRef.getResults("EIS");
      beanRef.txDbUnConnect("EIS");
      ut.commit();
      for (int i = 0; i < dbResults.size(); i++)
        logger.log(Logger.Level.INFO,
            "EIS dbResults.elementAt" + i + " :" + dbResults.elementAt(i));

      logger.log(Logger.Level.INFO,"Verifying the test results of EIS");
      for (int i = 1; i <= tSizeOrig; i++) {
        if (dbResults.contains((new Integer(i)).toString())) {
          b3 = true;
        } else {
          b3 = false;
          break;
        }
      }
      for (int j = tSize; j > tSizeOrig; j--) {
        if (dbResults.contains((new Integer(j)).toString())) {
          b4 = false;
          break;
        } else {
          b4 = true;
        }
      }
      logger.log(Logger.Level.TRACE,"b1 : " + b1);
      logger.log(Logger.Level.TRACE,"b2 : " + b2);
      logger.log(Logger.Level.TRACE,"b3 : " + b3);
      logger.log(Logger.Level.TRACE,"b4 : " + b4);

      if (b1 && b2 && b3 && b4)
        testResult = true;

    } catch (Exception e) {
      logger.log(Logger.Level.ERROR,"Caught exception: " + e.getMessage());
      TestUtil.printStackTrace(e);
      throw new Exception(testname + " failed", e);
    } finally {
      // cleanup the bean
      try {
        logger.log(Logger.Level.INFO,"Finally cleaning the test data for JDBC");
        ut.begin();
        beanRef.txDbConnect(tName1);
        beanRef.destroyData(tName1);
        beanRef.txDbUnConnect(tName1);
        ut.commit();
        logger.log(Logger.Level.INFO,"Finally cleaning the test data for EIS");
        ut.begin();
        beanRef.txDbConnect("EIS");
        beanRef.destroyData("EIS");
        beanRef.txDbUnConnect("EIS");
        ut.commit();
      } catch (Exception e) {
      }
      ;
      if (!testResult)
        throw new Exception(testname + " failed");
    }
  }

  /*
   * @testName: test22
   *
   * @assertion_ids: JavaEE:SPEC:90; JavaEE:SPEC:92; JavaEE:SPEC:79;
   * JavaEE:SPEC:76; JavaEE:SPEC:74; JavaEE:SPEC:70
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
  public void test22() throws Exception {
    String testname = "test22";
    Vector dbResults = new Vector();
    boolean testResult = false;
    boolean b1, b2, b3, b4, b5;
    b1 = b2 = b3 = b4 = b5 = false;
    String tName1 = this.tName1;
    int tSize = this.tSize.intValue();
    int tSizeOrig = this.tSize.intValue();
    int tSizeDuplicate = this.tSize.intValue() + 1;
    // int tRng = this.fromKey1.intValue();

    try {
      logger.log(Logger.Level.TRACE,testname);
      logger.log(Logger.Level.INFO,"Transaction propagation from Servlet, EJB or JSP");
      logger.log(Logger.Level.INFO,
          "Insert/Delete followed by a commit to a single table in EIS and JDBC");
      logger.log(Logger.Level.INFO,"Database access is performed from EJB1Test");

      logger.log(Logger.Level.INFO,"Creating the data in JDBC table");
      ut.begin();
      beanRef.txDbConnect(tName1);
      beanRef.createData(tName1);
      beanRef.txDbUnConnect(tName1);
      ut.commit();

      logger.log(Logger.Level.INFO,"Creating the data in EIS table");
      ut.begin();
      beanRef.txDbConnect("EIS");
      beanRef.createData("EIS");
      beanRef.txDbUnConnect("EIS");
      ut.commit();

      try {
        logger.log(Logger.Level.INFO,"Insert and delete some rows");
        ut.begin();

        beanRef.dbConnect("EIS");
        logger.log(Logger.Level.INFO,"Inserting 1 new rows in EIS");
        if (beanRef.insert("EIS", tSize + 1))
          tSize++;
        beanRef.dbUnConnect("EIS");

        logger.log(Logger.Level.INFO,"Insert rows using notx whitebox");
        logger.log(Logger.Level.INFO,"Calling insertDup in Ejb1 with value = " + tSizeDuplicate);
        beanRef.insertDup("EIS", Integer.toString(tSizeDuplicate)); // 6

        ut.commit();
      } catch (jakarta.transaction.RollbackException rex) {
        TestUtil.printStackTrace(rex);
        b5 = true;
        logger.log(Logger.Level.INFO,"Captured Rollback Exception : b5 : " + b5);
      } catch (Exception ex) {
        TestUtil.printStackTrace(ex);
        b5 = false;
        logger.log(Logger.Level.INFO,"Captured Exception : b5 : " + b5);
      }

      logger.log(Logger.Level.INFO,"Get test results for JDBC");
      ut.begin();
      beanRef.txDbConnect(tName1);
      dbResults = beanRef.getResults(tName1);
      for (int i = 0; i < dbResults.size(); i++)
        logger.log(Logger.Level.INFO,
            "JDBC dbResults.elementAt" + i + " :" + dbResults.elementAt(i));

      beanRef.txDbUnConnect(tName1);
      ut.commit();

      logger.log(Logger.Level.INFO,"Verifying the test results of JDBC");
      for (int i = 1; i <= tSizeOrig; i++) {
        if (dbResults.contains(new Integer(i))) {
          b1 = true;
        } else {
          b1 = false;
          break;
        }
      }
      for (int j = tSize; j > tSizeOrig; j--) {
        if (dbResults.contains(new Integer(j))) {
          b2 = false;
          break;
        } else {
          b2 = true;
        }
      }

      dbResults = null;
      logger.log(Logger.Level.INFO,"Get test results for EIS");
      ut.begin();
      beanRef.txDbConnect("EIS");
      dbResults = beanRef.getResults("EIS");
      beanRef.txDbUnConnect("EIS");
      ut.commit();
      for (int i = 0; i < dbResults.size(); i++)
        logger.log(Logger.Level.TRACE,
            "EIS dbResults.elementAt" + i + " :" + dbResults.elementAt(i));

      logger.log(Logger.Level.INFO,"Verifying the test results of EIS");
      for (int i = 1; i <= (tSizeOrig + 1); i++) { // to include tSizeDuplicate
                                                   // also
        if (dbResults.contains((new Integer(i)).toString())) {
          b3 = true;
        } else {
          b3 = false;
          break;
        }
      }
      for (int j = tSize; j > tSizeOrig; j--) {
        if (j == tSizeDuplicate) {
          b4 = true;
          continue;
        }
        // else {
        // if( dbResults.contains((new Integer(j)).toString()) ) {
        // b4 = false;
        // break;
        // }else
        // {
        // b4 = true;
        // }
        // }
      }
      logger.log(Logger.Level.TRACE,"b1 : " + b1);
      logger.log(Logger.Level.TRACE,"b2 : " + b2);
      logger.log(Logger.Level.TRACE,"b3 : " + b3);
      logger.log(Logger.Level.TRACE,"b4 : " + b4);
      logger.log(Logger.Level.TRACE,"b5 : " + b5);

      if (b1 && b2 && b3 && b4 && b5)
        testResult = true;

    } catch (Exception e) {
      logger.log(Logger.Level.ERROR,"Caught exception: " + e.getMessage());
      TestUtil.printStackTrace(e);
      throw new Exception(testname + " failed", e);
    } finally {
      // cleanup the bean
      try {
        logger.log(Logger.Level.INFO,"Finally cleaning the test data for JDBC");
        ut.begin();
        beanRef.txDbConnect(tName1);
        beanRef.destroyData(tName1);
        beanRef.txDbUnConnect(tName1);
        ut.commit();
        logger.log(Logger.Level.INFO,"Finally cleaning the test data for EIS");
        ut.begin();
        beanRef.txDbConnect("EIS");
        beanRef.destroyData("EIS");
        beanRef.txDbUnConnect("EIS");
        ut.commit();
      } catch (Exception e) {
      }
      ;
      if (!testResult)
        throw new Exception(testname + " failed");
    }
  }

}
