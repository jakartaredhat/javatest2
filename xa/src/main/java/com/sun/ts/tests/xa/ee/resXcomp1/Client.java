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
 * @(#)Client.java
 */

package com.sun.ts.tests.xa.ee.resXcomp1;

import java.io.Serializable;
import java.lang.System.Logger;
import java.util.Properties;
import java.util.Vector;

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

  private TxBean beanRef = null;

  private UserTransaction ut = null;

  private String tName1 = null;

  private Integer tSize = null;

  private Integer fromKey1 = null;

  private Integer fromKey2 = null;

  private Integer toKey2 = null;

  /* Test setup: */

  /*
   * @class.setup_props: org.omg.CORBA.ORBClass; java.naming.factory.initial;
   * 
   * @class.testArgs: -ap tssql.stmt
   */
  @BeforeEach
  public void setup() throws Exception {
    try {
      this.testProps = p;
      TestUtil.init(p);
      logger.log(Logger.Level.INFO,"Setup tests");

      logger.log(Logger.Level.INFO,"Obtain naming context");
      nctx = new TSNamingContext();

      logger.log(Logger.Level.INFO,"Lookup TxBean: " + txRef);
      beanRef = (TxBean) nctx.lookup(txRef, TxBean.class);

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

      this.fromKey1 = (Integer) nctx.lookup("java:comp/env/fromKey1");
      logger.log(Logger.Level.TRACE,"fromKey1: " + this.fromKey1);

      this.fromKey2 = (Integer) nctx.lookup("java:comp/env/fromKey2");
      logger.log(Logger.Level.TRACE,"fromKey2: " + this.fromKey2);

      this.toKey2 = (Integer) nctx.lookup("java:comp/env/toKey2");
      logger.log(Logger.Level.TRACE,"toKey2: " + this.toKey2);

      logger.log(Logger.Level.INFO,"Initialize " + txRef);
      beanRef.initialize();

      logger.log(Logger.Level.INFO,"Initialize logging data from server");
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

  /* Run test */

  /*
   * @testName: test1
   *
   * @assertion_ids: JavaEE:SPEC:74; JavaEE:SPEC:68
   *
   * @test_Strategy: Contact a Servlet, EJB or JSP. Obtain the UserTransaction
   * interface. Perform global transactions using the TxBean (deployed as
   * TX_REQUIRED) to a single RDBMS table.
   * 
   * Insert/Delete followed by a commit to a single table.
   *
   * Database Access is performed from TxBean EJB.
   *
   */
  @Test
  public void test1() throws Exception {
    String testname = "test1";
    Vector dbResults = new Vector();
    boolean testResult = false;
    boolean b1, b2;
    b1 = b2 = false;
    String tName1 = this.tName1;
    int tSize = this.tSize.intValue();
    int tRng = this.fromKey1.intValue();

    try {
      logger.log(Logger.Level.TRACE,testname);
      logger.log(Logger.Level.INFO,"Transaction propagation from Servlet, EJB or JSP");
      logger.log(Logger.Level.INFO,"Insert/Delete followed by a commit to a single table");
      logger.log(Logger.Level.INFO,"Database access is performed from TxBean EJB");

      logger.log(Logger.Level.INFO,"Creating the table");
      ut.begin();
      beanRef.dbConnect(tName1);
      beanRef.createData(tName1);
      beanRef.dbUnConnect(tName1);
      ut.commit();

      logger.log(Logger.Level.INFO,"Insert and delete some rows");
      ut.begin();
      beanRef.dbConnect(tName1);
      logger.log(Logger.Level.INFO,"Inserting 2 new rows");
      if (beanRef.insert(tName1, tSize + 1))
        tSize++;
      if (beanRef.insert(tName1, tSize + 1))
        tSize++;
      logger.log(Logger.Level.INFO,"Deleting a row");
      beanRef.delete(tName1, tRng, tRng);
      beanRef.dbUnConnect(tName1);
      ut.commit();

      logger.log(Logger.Level.INFO,"Get test results");
      ut.begin();
      beanRef.dbConnect(tName1);
      dbResults = beanRef.getResults(tName1);

      logger.log(Logger.Level.INFO,"Verifying the test results");
      if (!dbResults.contains(new Integer(tRng)))
        b1 = true;

      for (int i = 1; i <= tSize; i++) {
        if (i == tRng)
          continue;
        else {
          if (dbResults.contains(new Integer(i)))
            b2 = true;
          else {
            b2 = false;
            break;
          }
        }
      }
      beanRef.dbUnConnect(tName1);
      ut.commit();

      if (b1 && b2)
        testResult = true;
      //
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
   * @testName: test2
   *
   * @assertion_ids: JavaEE:SPEC:74; JavaEE:SPEC:68
   *
   * @test_Strategy: Contact a Servlet, EJB or JSP. Obtain the UserTransaction
   * interface. Perform global transactions using the TxBean (deployed as
   * TX_REQUIRED) to a single RDBMS table.
   * 
   * Insert/Delete followed by a rollback to a single table.
   *
   * Database Access is performed from TxBean EJB.
   *
   */
  @Test
  public void test2() throws Exception {
    String testname = "test2";
    Vector dbResults = new Vector();
    boolean testResult = false;
    boolean b1, b2;
    b1 = b2 = false;
    String tName1 = this.tName1;
    int tSize = this.tSize.intValue();
    int tSizeOrig = this.tSize.intValue();
    int tRngFrom = this.fromKey2.intValue();
    int tRngTo = this.toKey2.intValue();

    try {
      logger.log(Logger.Level.TRACE,testname);
      logger.log(Logger.Level.INFO,"Transaction propagation from Servlet, EJB or JSP");
      logger.log(Logger.Level.INFO,"Insert/Delete followed by a rollback to a single table");
      logger.log(Logger.Level.INFO,"Database access is performed from TxBean EJB");

      logger.log(Logger.Level.INFO,"Creating the table");
      ut.begin();
      beanRef.dbConnect(tName1);
      beanRef.createData(tName1);
      beanRef.dbUnConnect(tName1);
      ut.commit();

      logger.log(Logger.Level.INFO,"Insert and delete some rows");
      ut.begin();
      beanRef.dbConnect(tName1);
      logger.log(Logger.Level.INFO,"Inserting 2 new rows");
      if (beanRef.insert(tName1, tSize + 1))
        tSize++;
      if (beanRef.insert(tName1, tSize + 1))
        tSize++;
      logger.log(Logger.Level.INFO,"Deleting a row");
      beanRef.delete(tName1, tRngFrom, tRngTo);
      beanRef.dbUnConnect(tName1);
      ut.rollback();

      logger.log(Logger.Level.INFO,"Get test results");
      ut.begin();
      beanRef.dbConnect(tName1);
      dbResults = beanRef.getResults(tName1);

      logger.log(Logger.Level.INFO,"Verifying the test results");
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
      beanRef.dbUnConnect(tName1);
      ut.commit();

      if (b1)
        logger.log(Logger.Level.TRACE,"b1 true");
      if (b2)
        logger.log(Logger.Level.TRACE,"b2 true");

      if (b1 && b2)
        testResult = true;

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
   * @testName: test3
   *
   * @assertion_ids: JavaEE:SPEC:74
   *
   * @test_Strategy: Contact a Servlet, EJB or JSP. Obtain the UserTransaction
   * interface. Perform global transactions using the TxBean (deployed as
   * TX_REQUIRED) to a single RDBMS table.
   * 
   * Insert/Delete followed by a commit to a single table.
   *
   * Database Access is performed from TxBean EJB.
   *
   */
  @Test
  public void test3() throws Exception {
    String testname = "test3";
    Vector dbResults = new Vector();
    boolean testResult = false;
    boolean b1, b2;
    b1 = b2 = false;
    String tName1 = this.tName1;
    int tSize = this.tSize.intValue();
    int tRng = this.fromKey1.intValue();

    try {
      logger.log(Logger.Level.TRACE,testname);
      logger.log(Logger.Level.INFO,"Transaction propagation from Servlet, EJB or JSP");
      logger.log(Logger.Level.INFO,"Insert/Delete followed by a commit to a single table");
      logger.log(Logger.Level.INFO,"Database access is performed from TxBean EJB");

      logger.log(Logger.Level.INFO,"Creating the data");
      ut.begin();
      beanRef.dbConnect("EIS");
      beanRef.createData("EIS");
      beanRef.dbUnConnect("EIS");
      ut.commit();

      logger.log(Logger.Level.INFO,"Insert and delete some rows");
      ut.begin();
      beanRef.dbConnect("EIS");
      logger.log(Logger.Level.INFO,"Inserting 2 new rows");
      if (beanRef.insert("EIS", tSize + 1))
        tSize++;
      if (beanRef.insert("EIS", tSize + 1))
        tSize++;
      logger.log(Logger.Level.INFO,"Deleting a row");
      beanRef.delete("EIS", tRng, tRng);
      beanRef.dbUnConnect("EIS");
      ut.commit();

      logger.log(Logger.Level.INFO,"Get test results");
      ut.begin();
      beanRef.dbConnect("EIS");
      dbResults = beanRef.getResults("EIS");

      logger.log(Logger.Level.INFO,"Verifying the test results");
      if (!dbResults.contains((new Integer(tRng)).toString()))
        b1 = true;

      for (int i = 1; i <= tSize; i++) {
        if (i == tRng)
          continue;
        else {
          if (dbResults.contains((new Integer(i)).toString()))
            b2 = true;
          else {
            b2 = false;
            break;
          }
        }
      }
      beanRef.dbUnConnect("EIS");
      ut.commit();

      if (b1 && b2)
        testResult = true;
      //
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
   * @testName: test4
   *
   * @assertion_ids: JavaEE:SPEC:74
   *
   * @test_Strategy: Contact a Servlet, EJB or JSP. Obtain the UserTransaction
   * interface. Perform global transactions using the TxBean (deployed as
   * TX_REQUIRED) to a single RDBMS table.
   * 
   * Insert/Delete followed by a rollback to a single table.
   *
   * Database Access is performed from TxBean EJB.
   *
   */
  @Test
  public void test4() throws Exception {
    String testname = "test4";
    Vector dbResults = new Vector();
    boolean testResult = false;
    boolean b1, b2;
    b1 = b2 = false;
    String tName1 = this.tName1;
    int tSize = this.tSize.intValue();
    int tSizeOrig = this.tSize.intValue();
    int tRngFrom = this.fromKey2.intValue();
    int tRngTo = this.toKey2.intValue();

    try {
      logger.log(Logger.Level.TRACE,testname);
      logger.log(Logger.Level.INFO,"Transaction propagation from Servlet, EJB or JSP");
      logger.log(Logger.Level.INFO,"Insert/Delete followed by a rollback to a single table");
      logger.log(Logger.Level.INFO,"Database access is performed from TxBean EJB");

      logger.log(Logger.Level.INFO,"Creating the table");
      ut.begin();
      beanRef.dbConnect("EIS");
      beanRef.createData("EIS");
      beanRef.dbUnConnect("EIS");
      ut.commit();

      logger.log(Logger.Level.INFO,"Insert and delete some rows");
      ut.begin();
      beanRef.dbConnect("EIS");
      logger.log(Logger.Level.INFO,"Inserting 2 new rows");
      if (beanRef.insert("EIS", tSize + 1))
        tSize++;
      if (beanRef.insert("EIS", tSize + 1))
        tSize++;
      logger.log(Logger.Level.INFO,"Deleting a row");
      beanRef.delete("EIS", tRngFrom, tRngTo);
      beanRef.dbUnConnect("EIS");
      ut.rollback();

      logger.log(Logger.Level.INFO,"Get test results");
      ut.begin();
      beanRef.dbConnect("EIS");
      dbResults = beanRef.getResults("EIS");
      beanRef.dbUnConnect("EIS");
      ut.commit();

      logger.log(Logger.Level.INFO,"Verifying the test results");
      for (int i = 1; i <= tSizeOrig; i++) {
        if (dbResults.contains((new Integer(i)).toString())) {
          b1 = true;
        } else {
          b1 = false;
          break;
        }
      }
      for (int j = tSize; j > tSizeOrig; j--) {
        if (dbResults.contains((new Integer(j)).toString())) {
          b2 = false;
          break;
        } else {
          b2 = true;
        }
      }

      if (b1)
        logger.log(Logger.Level.TRACE,"b1 true");
      if (b2)
        logger.log(Logger.Level.TRACE,"b2 true");

      if (b1 && b2)
        testResult = true;

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
