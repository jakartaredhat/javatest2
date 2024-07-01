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
 * @(#)Ejb1TestEJB.java	1.3 03/05/16
 */

/*
 * @(#)Ejb1TestEJB.java	1.17 02/07/19
 */
package com.sun.ts.tests.xa.ee.resXcomp3;

import java.lang.System.Logger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Vector;

import javax.sql.DataSource;

import com.sun.ts.lib.util.TSNamingContext;
import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.common.connector.whitebox.TSConnection;
import com.sun.ts.tests.common.connector.whitebox.TSEISDataSource;

import jakarta.ejb.EJBException;

public class Ejb1TestEJB {
	
	  private static final Logger logger = (Logger) System.getLogger(Ejb1TestEJB.class.getName());

  // testProps represent the test specific properties passed in
  // from the test harness.
  private Properties p = null;

  private Properties testProps = null;

  private Ejb2Test ref = null;

  private TSNamingContext context = null;

  // DataSources
  // ds1 is associated with dbTable1; con1 will be used for the dbTable1
  // connection
  private DataSource ds1, ds4 = null;

  private String dbTable1 = null;

  private transient Connection con1 = null;

  private transient Connection con4 = null;

  // TSEIS
  private TSEISDataSource ds2, ds3;

  private transient TSConnection con2 = null;

  private transient TSConnection con3 = null;

  private transient Statement stmt = null;

  public Ejb1TestEJB() {
  }

  public void initialize(Properties props) {
    this.testProps = props;
    String eMsg = "";
    try {
      TestUtil.init(props);
      context = new TSNamingContext();

      // get reference to ejb
      eMsg = "Exception doing a lookup for Ejb2Test ";
      ref = (Ejb2Test) context.lookup("java:comp/env/ejb/Ejb2Test", Ejb2Test.class);
      ref.initialize(props);

      logger.log(Logger.Level.INFO,"Initialize logging data from server in Ejb1");
      eMsg = "Exception doing a initLogging for Ejb2Test ";
      ref.initLogging(p);

      eMsg = "Exception looking up JDBCwhitebox";
      ds1 = (DataSource) context.lookup("java:comp/env/eis/JDBCwhitebox-xa");

      eMsg = "Exception looking up EIS whitebox-xa";
      ds2 = (TSEISDataSource) context.lookup("java:comp/env/eis/whitebox-xa");

      eMsg = "Exception looking up EIS whitebox-notx";
      ds3 = (TSEISDataSource) context.lookup("java:comp/env/eis/whitebox-notx");

      eMsg = "Exception looking up EIS JDBCwhitebox-notx";
      ds4 = (DataSource) context.lookup("java:comp/env/eis/JDBCwhitebox-notx");

      logger.log(Logger.Level.INFO,"JDBCwhitebox-xa ds1 : " + ds1);
      logger.log(Logger.Level.INFO,"whitebox-xa ds2 : " + ds2);
      logger.log(Logger.Level.INFO,"whitebox-notx ds3 : " + ds3);
      logger.log(Logger.Level.INFO,"JDBCwhitebox-notx ds4 : " + ds4);

    } catch (Exception e) {
      logger.log(Logger.Level.INFO,eMsg);
      logger.log(Logger.Level.ERROR,"Ejb1: initialize failed", e);
      throw new EJBException(e.getMessage());
    }
  }

  // ===========================================================
  public void dbConnect(String tName) {
    logger.log(Logger.Level.INFO,"dbConnect");
    try {
      if (tName.equals(dbTable1)) {
        // Make the dbTable1 connection
        conTable1();
        logger.log(Logger.Level.INFO,"Made the JDBC connection to " + dbTable1 + " DB");
      } else {
        conTable2();
        logger.log(Logger.Level.INFO,"Made the connection to EIS");
      }
    } catch (Exception e) {
      logger.log(Logger.Level.ERROR,"Unexpected exception on JDBC connection", e);
      throw new EJBException(e.getMessage());
    }
  }

  public void insertDup(String tName) {
    String key, eMsg = null;
    logger.log(Logger.Level.TRACE,"insertDup");
    try {
      if (tName.equals(dbTable1)) {
        logger.log(Logger.Level.INFO,"Getting a connection con4");
        eMsg = "Exception doing a getConnection from ds4";
        con4 = ds4.getConnection();
        logger.log(Logger.Level.TRACE,"con4: " + con4.toString());

        // Prepare the new data entry in dbTable
        logger.log(Logger.Level.INFO,"Insert row in " + dbTable1);
        key = new String(testProps.getProperty("Xa_Tab1_insert_init"));
        stmt = con4.createStatement();
        stmt.executeUpdate(key);
        logger.log(Logger.Level.TRACE,
            "Inserted a row into the table using notx - key " + tName + key);
        logger.log(Logger.Level.INFO,"nsertDup key in " + dbTable1);
      } else { // EIS
        logger.log(Logger.Level.INFO,"Getting a connection con3");
        eMsg = "Exception doing a getConnection from ds3";
        con3 = ds3.getConnection();
        logger.log(Logger.Level.TRACE,"con3: " + con3.toString());

        // Prepare the new data entry in EIS
        logger.log(Logger.Level.INFO,"Insert row in EIS");
        eMsg = "Exception doing an insert in EIS con3 ds3";
        key = new String(testProps.getProperty("TSEIS_insert_init"));
        con3.insert(key, key);
        logger.log(Logger.Level.TRACE,"Inserted a row in EIS using notx - key " + key);
        logger.log(Logger.Level.INFO,"nsertDup key in EIS");
      }
    } catch (Exception e) {
      logger.log(Logger.Level.INFO,eMsg);
      TestUtil.printStackTrace(e);
      logger.log(Logger.Level.INFO,"Captured Exception in insertDup");
      throw new EJBException(e.getMessage());
    } finally {
      try {
        eMsg = "Exception in closing stmt";
        if (stmt != null) {
          stmt.close();
          stmt = null;
        }
        eMsg = "Exception in closing con3";
        if (con3 != null) {
          con3.close();
          con3 = null;
        }
        eMsg = "Exception in closing con4";
        if (con4 != null) {
          con4.close();
          con4 = null;
        }
      } catch (Exception e) {
        logger.log(Logger.Level.INFO,eMsg);
        TestUtil.printStackTrace(e);
        logger.log(Logger.Level.INFO,"Captured Exception in insertDup");
        throw new EJBException(e.getMessage());
      }
    }
  }

  public void insert(String tName) {
    String key = null;
    logger.log(Logger.Level.TRACE,"insert");
    // Insert a row into the specified table
    try {
      if (tName.equals(dbTable1)) {
        // Prepare the new dbTable1 row entry
        logger.log(Logger.Level.INFO,"Insert row in " + dbTable1);
        key = new String(testProps.getProperty("Xa_Tab1_insert_init"));
        stmt = con1.createStatement();
        stmt.executeUpdate(key);
        logger.log(Logger.Level.TRACE,"Inserted a row into the table " + tName);
        logger.log(Logger.Level.INFO,"Calling insert in Ejb2 ");
        ref.dbConnect(tName);
        ref.insert(tName);
        ref.dbUnConnect(tName);
      } else {
        // Prepare the new data entry in EIS
        logger.log(Logger.Level.INFO,"Insert row in EIS");
        key = new String(testProps.getProperty("TSEIS_insert_init"));
        con2.insert(key, key);
        logger.log(Logger.Level.TRACE,"Inserted a row into the EIS ");
        logger.log(Logger.Level.INFO,"Calling insert in Ejb2 ");
        ref.dbConnect("EIS");
        ref.insert("EIS");
        ref.dbUnConnect("EIS");
      }
    } catch (Exception e) {
      logger.log(Logger.Level.ERROR,"Exception inserting a row into table " + tName + ";\n"
          + e.getMessage(), e);
      throw new EJBException(e.getMessage());
    } finally {
      try {
        if (stmt != null) {
          stmt.close();
          stmt = null;
        }
      } catch (Exception e) {
        logger.log(Logger.Level.ERROR,
            "Exception in insert@Ejb1 closing stmt" + e.getMessage(), e);
        throw new EJBException(e.getMessage());
      }
    }
  }

  public void destroyData(String tName) {
    String eMsg = null;
    String removeString = TestUtil.getProperty("Xa_Tab1_Delete");
    logger.log(Logger.Level.INFO,"destroyData : " + tName);
    try {
      if (tName.equals(dbTable1)) {
        eMsg = "Exception doing a getConnection from ds4";
        logger.log(Logger.Level.INFO,"Getting a connection con4");
        con4 = ds4.getConnection();
        logger.log(Logger.Level.TRACE,"con4: " + con4.toString());
        eMsg = "Exception doing a dropTable on con4";
        stmt = con4.createStatement();
        stmt.executeUpdate(removeString);
        // stmt.close();
        logger.log(Logger.Level.INFO,"Deleted all rows from table " + dbTable1);
      } else { // EIS
        eMsg = "Exception doing a getConnection from ds3";
        logger.log(Logger.Level.INFO,"Getting a connection con3");
        con3 = ds3.getConnection();
        logger.log(Logger.Level.TRACE,"con3: " + con3.toString());
        eMsg = "Exception doing a dropTable on con3";
        con3.dropTable();
        logger.log(Logger.Level.INFO,"Deleted all rows from EIS");
      }
    } catch (Exception e) {
      logger.log(Logger.Level.INFO,eMsg);
      logger.log(Logger.Level.ERROR,"Exception occured trying to drop table", e);
      throw new EJBException(e.getMessage());
    } finally {
      try {
        eMsg = "Exception in closing stmt";
        if (stmt != null) {
          stmt.close();
          stmt = null;
        }
        eMsg = "Exception in closing con3";
        if (con3 != null) {
          con3.close();
          con3 = null;
        }
        eMsg = "Exception in closing con4";
        if (con4 != null) {
          con4.close();
          con4 = null;
        }
      } catch (Exception e) {
        logger.log(Logger.Level.INFO,eMsg);
        logger.log(Logger.Level.ERROR,"Exception occured trying to drop table", e);
        throw new EJBException(e.getMessage());
      }
    }
  }

  public void dbUnConnect(String tName) {
    logger.log(Logger.Level.INFO,"dbUnConnect");
    Vector queryResults = new Vector();
    // Close the DB connections
    try {
      if (tName.equals(dbTable1)) {
        con1.close();
        con1 = null;
        logger.log(Logger.Level.INFO,"Closed " + dbTable1 + " connection");
      } else {
        con2.close();
        con2 = null;
        logger.log(Logger.Level.INFO,"Closed EIS connection");
      }
    } catch (Exception e) {
      logger.log(Logger.Level.ERROR,"Exception occured trying to close the DB connection", e);
      throw new EJBException(e.getMessage());
    }
  }

  public void initLogging(Properties p) {
    logger.log(Logger.Level.TRACE,"initLogging Ejb1");
    this.testProps = p;
    try {
      TestUtil.init(p);
      // Get the dbTable1 DataSource
      dbTable1 = TestUtil.getTableName(TestUtil.getProperty("Xa_Tab1_Delete"));
      logger.log(Logger.Level.INFO,dbTable1 + " Ejb1 initLogging OK!");

    } // catch(RemoteLoggingInitException e) {
    catch (Exception e) {
      TestUtil.printStackTrace(e);
      throw new EJBException("Inside Ejb1 initLogging" + e.getMessage());
    }
  }

  public boolean verifyData(String operation, String tName, int[] expResults) {
    boolean status = false;
    int linenum = 0;
    PreparedStatement queryStatement = null;
    ResultSet theResults = null;
    Vector queryResults = new Vector();
    try {
      if (operation.equals("commit")) { // operation is commit
        logger.log(Logger.Level.INFO,"Expected number of rows is: " + expResults.length);
        if (tName.equals(dbTable1)) {
          String query = testProps.getProperty("Xa_Tab1_query");
          logger.log(Logger.Level.INFO,"query is " + query);
          queryStatement = con1.prepareStatement(query);
          theResults = queryStatement.executeQuery();
          ResultSetMetaData rsmeta = theResults.getMetaData();
          int numColumns = rsmeta.getColumnCount();
          logger.log(Logger.Level.TRACE,"Number of columns from rsmeta " + numColumns);
          while (theResults.next()) {
            linenum++;
            logger.log(Logger.Level.INFO,
                "Line No: " + linenum + " results: " + theResults.getInt(1)
                    + " expResults: " + expResults[linenum - 1]);

            if (theResults.getInt(1) == expResults[linenum - 1]) {
              status = true;
              logger.log(Logger.Level.INFO,"verifyData OK, Status is : " + status);
            } else {
              status = false;
              logger.log(Logger.Level.INFO,"verifyData ERROR, Status is : " + status);
              break;
            }
          } // while
          if (expResults.length != linenum) {
            logger.log(Logger.Level.TRACE,
                "Error - expected row count does not match table in verifyData!!");
            status = false;
          }
        } else { // EIS
          logger.log(Logger.Level.INFO,"Getting a connection con3");
          con3 = ds3.getConnection();
          logger.log(Logger.Level.INFO,"con3: " + con3.toString());
          queryResults = con3.readData();
          for (int i = 0; i < expResults.length; i++) {
            logger.log(Logger.Level.INFO,"Expected results: " + expResults.length);
            if (queryResults
                .contains((new Integer(expResults[i])).toString())) {
              status = true;
              logger.log(Logger.Level.INFO,"VerifyData OK, Status is : " + status);
            } else {
              status = false;
              logger.log(Logger.Level.INFO,"VerifyData Error, Status is : " + status);
              break;
            }
          }
          for (int i = 0; i < queryResults.size(); i++) {
            logger.log(Logger.Level.INFO,
                "Query Results contains : " + queryResults.elementAt(i));
          }
        }
      } else { // operation is rollback
        if (tName.equals(dbTable1)) {
          String query = testProps.getProperty("Xa_Tab1_query");
          logger.log(Logger.Level.INFO,"query is " + query);
          queryStatement = con1.prepareStatement(query);
          theResults = queryStatement.executeQuery();
          logger.log(Logger.Level.TRACE,"status is " + status);
          status = !theResults.next();
          logger.log(Logger.Level.INFO,"Resultset has no data? " + status);
        } else { // EIS
          logger.log(Logger.Level.INFO,"Getting a connection con3");
          con3 = ds3.getConnection();
          logger.log(Logger.Level.INFO,"con3: " + con3.toString());
          queryResults = con3.readData();
          for (int i = 0; i < expResults.length; i++) {
            logger.log(Logger.Level.INFO,"Not Expected results: " + expResults[i]);
            if (queryResults
                .contains((new Integer(expResults[i])).toString())) {
              status = false;
              logger.log(Logger.Level.INFO,"VerifyData Error, Status is : " + status);
              break;
            } else {
              status = true;
              logger.log(Logger.Level.INFO,"VerifyData OK, Status is : " + status);
            }
          }
          for (int i = 0; i < queryResults.size(); i++) {
            logger.log(Logger.Level.INFO,
                "Query REsults contains : " + queryResults.elementAt(i));
          }
        }
      }
    } catch (Exception e) {
      logger.log(Logger.Level.TRACE,"Error verifyData database inserts Ejb1 ");
      TestUtil.printStackTrace(e);
      status = false;
      throw new EJBException(e.getMessage());
    } finally {
      try {
        if (theResults != null) {
          theResults.close();
        }
        if (queryStatement != null) {
          queryStatement.close();
        }
        if (con3 != null) {
          con3.close();
          con3 = null;
        }
      } catch (Exception ee) {
      }
    }
    logger.log(Logger.Level.INFO,"Verify Data Status is : " + status);
    return status;
  }

  // Exception methods
  public void throwEJBException() throws EJBException {
    logger.log(Logger.Level.TRACE,"throwEJBException");
    throw new EJBException("EJBException from Ejb1TestEJB");
  }

  // private methods
  private void conTable1() {
    logger.log(Logger.Level.TRACE,"conTable1");
    try {
      // Get connection info for dbTable1 DB
      con1 = ds1.getConnection();
      logger.log(Logger.Level.TRACE,"con1: " + con1.toString());
    } catch (SQLException e) {
      logger.log(Logger.Level.ERROR,"SQLException connecting to " + dbTable1 + " DB", e);
      throw new EJBException(e.getMessage());
    } catch (Exception ee) {
      logger.log(Logger.Level.ERROR,"Exception connecting to " + dbTable1 + " DB con1 ", ee);
      throw new EJBException(ee.getMessage());
    }
  }

  private void conTable2() {
    logger.log(Logger.Level.TRACE,"conTable2");
    try {
      // Get connection info for dbTable1 DB
      con2 = ds2.getConnection();
      logger.log(Logger.Level.TRACE,"con2: " + con2.toString());
    } catch (Exception ee) {
      logger.log(Logger.Level.ERROR,"Exception connecting to EIS con2 ", ee);
      throw new EJBException(ee.getMessage());
    }
  }

  /*
   * private void dropTable1() { logger.log(Logger.Level.TRACE,"dropTable1"); // Delete the
   * data in dbTable1 table String removeString =
   * TestUtil.getProperty("Xa_Tab1_Delete"); try{ stmt = con1.createStatement();
   * stmt.executeUpdate(removeString); stmt.close(); } catch (SQLException e) {
   * throw new EJBException( e.getMessage() ); } }
   * 
   * private void dropTable2() { logger.log(Logger.Level.TRACE,"dropTable2"); // Delete the
   * data from EIS table try{ con2.dropTable(); } catch (Exception e) { throw
   * new EJBException( e.getMessage() ); } }
   */

}
