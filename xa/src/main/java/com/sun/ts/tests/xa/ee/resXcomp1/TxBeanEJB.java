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
 * @(#)TxBeanEJB.java	1.4 03/05/16
 */

/*
 * @(#)TxBeanEJB.java   1.0 02/07/31
 */

package com.sun.ts.tests.xa.ee.resXcomp1;

import java.lang.System.Logger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Vector;

import javax.sql.DataSource;

import com.sun.ts.lib.util.RemoteLoggingInitException;
import com.sun.ts.lib.util.TSNamingContext;
import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.common.connector.whitebox.TSConnection;
import com.sun.ts.tests.common.connector.whitebox.TSEISDataSource;

import jakarta.ejb.EJBException;

public class TxBeanEJB {
	
  private static final Logger logger = (Logger) System.getLogger(TxBeanEJB.class.getName());

  // testProps represent the test specific properties passed in
  // from the test harness.
  private Properties testProps = null;

  // con1 will be used for the dbTable1 connection
  private transient Connection con1;

  private transient Statement stmt;

  private transient PreparedStatement pStmt;

  private String dbUser1, dbPassword1, dbTable1;

  // TableSizes
  // dbSize1 is the size of dbTable1
  private Integer dbSize1;

  // DataSources
  // ds1 is associated with dbTable1
  private DataSource ds1;

  private TSNamingContext context = null;

  // TSEIS
  private TSEISDataSource ds2;

  private transient TSConnection con2;

  public void initialize() {
    String eMsg = "";
    logger.log(Logger.Level.TRACE,"initialize");
    try {

      context = new TSNamingContext();
      dbSize1 = (Integer) context.lookup("java:comp/env/size");

      eMsg = "Exception looking up JDBCwhitebox";
      ds1 = (DataSource) context.lookup("java:comp/env/eis/JDBCwhitebox-tx");
      logger.log(Logger.Level.TRACE,"ds1: " + ds1);

      eMsg = "Exception looking up EIS whitebox";
      ds2 = (TSEISDataSource) context.lookup("java:comp/env/eis/whitebox-tx");
      logger.log(Logger.Level.TRACE,"ds2: " + ds2);
    } catch (Exception e) {
      logger.log(Logger.Level.TRACE,eMsg);
      logger.log(Logger.Level.ERROR,"Unexpected exception getting the DB DataSource", e);
      throw new EJBException(e.getMessage());
    }
  }

  // ===========================================================
  // The TxBean interface implementation

  // Database methods
  public void dbConnect(String tName) {
    logger.log(Logger.Level.TRACE,"dbConnect");
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

  public void createData(String tName) {
    logger.log(Logger.Level.TRACE,"createData");
    try {
      if (tName.equals(dbTable1)) {
        // Create the dbTable1 table
        createTable1();
        logger.log(Logger.Level.INFO,"Created the table " + dbTable1 + " ");
      } else {
        createTable2();
        logger.log(Logger.Level.INFO,"Created the EIS data");
      }
    } catch (Exception e) {
      logger.log(Logger.Level.ERROR,"Exception creating table", e);
      throw new EJBException(e.getMessage());
    }
  }

  public boolean insert(String tName, int key) {
    logger.log(Logger.Level.TRACE,"insert");
    // Insert a row into the specified table
    int newKey = key;
    String newName = null;

    try {
      if (tName.equals(dbTable1)) {
        // Prepare the new dbTable1 row entry
        newName = dbTable1 + "-" + newKey;

        String updateString = TestUtil.getProperty("Xa_Tab1_Insert");
        pStmt = con1.prepareStatement(updateString);
        // Perform the insert(s)
        pStmt.setInt(1, newKey);
        pStmt.setString(2, newName);
        pStmt.setString(3, newName);
        pStmt.executeUpdate();
        pStmt.close();

        logger.log(Logger.Level.TRACE,"Inserted a row into the table " + tName);
      } else {
        // Prepare the new data entry in EIS
        con2.insert((new Integer(key)).toString(),
            (new Integer(key)).toString());
        logger.log(Logger.Level.TRACE,"Inserted a row into the EIS ");
      }
      return true;

    } catch (Exception e) {
      logger.log(Logger.Level.ERROR,"Exception inserting a row into table " + tName + ";\n"
          + e.getMessage(), e);
      return false;
    }
  }

  public void delete(String tName, int fromKey, int toKey) {
    logger.log(Logger.Level.TRACE,"delete");
    try {
      if (tName.equals(dbTable1)) {
        // Delete row(s) from the specified table
        String updateString = TestUtil.getProperty("Xa_Tab1_Delete1");
        pStmt = con1.prepareStatement(updateString);
        for (int i = fromKey; i <= toKey; i++) {
          pStmt.setInt(1, i);
          pStmt.executeUpdate();
        }
        pStmt.close();
        logger.log(Logger.Level.TRACE,"Deleted row(s) " + fromKey + " thru " + toKey
            + " from the table " + tName);
      } else {
        // Delete rows from EIS
        for (int i = fromKey; i <= toKey; i++) {
          con2.delete((new Integer(i)).toString());
        }
        logger.log(Logger.Level.TRACE,"Deleted row(s) from EIS " + fromKey + " thru " + toKey);
      }
    } catch (Exception e) {
      logger.log(Logger.Level.ERROR,"Exception deleting row(s) " + fromKey + " thru " + toKey
          + " from the table " + tName, e);
      throw new EJBException(e.getMessage());
    }
  }

  public void destroyData(String tName) {
    logger.log(Logger.Level.TRACE,"destroyData");
    try {
      if (tName.equals(dbTable1)) {
        dropTable1();
        logger.log(Logger.Level.INFO,"Deleted all rows from table " + dbTable1);
      } else {
        dropTable2();
        logger.log(Logger.Level.INFO,"Deleted all rows from EIS");
      }
    } catch (Exception e) {
      logger.log(Logger.Level.ERROR,"Exception occured trying to drop table", e);
      throw new EJBException(e.getMessage());
    }
  }

  public void dbUnConnect(String tName) {
    logger.log(Logger.Level.TRACE,"dbUnConnect");
    // Close the DB connections
    try {
      if (tName.equals(dbTable1)) {
        con1.close();
        con1 = null;
        logger.log(Logger.Level.TRACE,"Closed " + dbTable1 + " connection");
      } else {
        con2.close();
        con2 = null;
        logger.log(Logger.Level.TRACE,"Closed EIS connection");
      }
    } catch (Exception e) {
      logger.log(Logger.Level.ERROR,"Exception occured trying to close the DB connection", e);
      throw new EJBException(e.getMessage());
    }
  }

  // Test Results methods
  public Vector getResults(String tName) {
    logger.log(Logger.Level.TRACE,"getResults");
    ResultSet rs = null;
    Vector queryResults = new Vector();
    int i;
    String query, s, s1, name = null;

    try {
      if (tName.equals(dbTable1)) {
        query = TestUtil.getProperty("Xa_Tab1_Select");
        stmt = con1.createStatement();
        rs = stmt.executeQuery(query);
        name = "COF_NAME";
        if (rs != null) {
          while (rs.next()) {
            i = rs.getInt(1);
            s = rs.getString(2);
            s1 = rs.getString(3);
            queryResults.addElement(new Integer(i));
            queryResults.addElement(s);
            queryResults.addElement(s1);
          }
        }
        stmt.close();
        logger.log(Logger.Level.INFO,"Obtained " + tName + " table ResultSet");
      } else {
        queryResults = con2.readData();
      }

    } catch (Exception e) {
      logger.log(Logger.Level.ERROR,"Exception obtaining " + tName + " table ResultSet", e);
      throw new EJBException(e.getMessage());
    }
    return queryResults;
  }

  public void initLogging(Properties p) {
    logger.log(Logger.Level.TRACE,"initLogging");
    this.testProps = p;
    try {
      TestUtil.init(p);
      // Get the dbTable1 DataSource
      dbTable1 = TestUtil.getTableName(TestUtil.getProperty("Xa_Tab1_Delete"));
      logger.log(Logger.Level.INFO,dbTable1 + " initLogging OK!");

    } catch (RemoteLoggingInitException e) {
      TestUtil.printStackTrace(e);
      throw new EJBException(e.getMessage());
    }
  }

  // Exception methods
  public void throwEJBException() throws EJBException {
    logger.log(Logger.Level.TRACE,"throwEJBException");
    throw new EJBException("EJBException from TxBean");
  }

  public void listTableData(Vector dbResults) {
    logger.log(Logger.Level.TRACE,"listTableData");
    try {
      if (dbResults.isEmpty())
        logger.log(Logger.Level.TRACE,"Empty vector!!!");
      else {
        for (int j = 0; j < dbResults.size(); j++)
          logger.log(Logger.Level.TRACE,dbResults.elementAt(j).toString());
      }
    } catch (Exception e) {
      logger.log(Logger.Level.ERROR,"Exception occured trying to list table data", e);
      throw new EJBException(e.getMessage());
    }
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
      logger.log(Logger.Level.ERROR,"Exception connecting to " + dbTable1 + " DB", ee);
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
      logger.log(Logger.Level.ERROR,"Exception connecting to EIS ", ee);
      throw new EJBException(ee.getMessage());
    }
  }

  private void createTable1() {
    logger.log(Logger.Level.TRACE,"createTable1");
    // drop dbTable1 table if it exists
    try {
      dropTable1();
      logger.log(Logger.Level.TRACE,"All rows deleted from table " + dbTable1);
    } catch (Exception e) {
      TestUtil.printStackTrace(e);
      logger.log(Logger.Level.INFO,
          "SQLException encountered in createTable1: " + e.getMessage());
    }

    try {
      // Add the prescribed table rows
      logger.log(Logger.Level.INFO,"Adding the " + dbTable1 + " table rows");
      String updateString = TestUtil.getProperty("Xa_Tab1_Insert");
      pStmt = con1.prepareStatement(updateString);

      for (int i = 1; i <= dbSize1.intValue(); i++) {
        // Perform the insert(s)
        int newKey = i;
        String newName = dbTable1 + "-" + i;

        pStmt.setInt(1, newKey);
        pStmt.setString(2, newName);
        pStmt.setString(3, newName);
        pStmt.executeUpdate();
      }

      pStmt.close();
    } catch (SQLException e) {
      logger.log(Logger.Level.ERROR,"SQLException creating " + dbTable1 + " table", e);
      throw new EJBException(e.getMessage());
    }
  }

  private void createTable2() {
    try {
      dropTable2();
      logger.log(Logger.Level.TRACE,"All rows deleted from EIS ");
    } catch (Exception e) {
      TestUtil.printStackTrace(e);
      logger.log(Logger.Level.INFO,
          "SQLException encountered in createTable2: " + e.getMessage());
    }
    try {
      logger.log(Logger.Level.INFO,"Adding the EIS rows");
      for (int i = 1; i <= dbSize1.intValue(); i++) {
        con2.insert((new Integer(i)).toString(), (new Integer(i)).toString());
      }
    } catch (Exception e) {
      logger.log(Logger.Level.ERROR,"SQLException creating " + dbTable1 + " table", e);
      throw new EJBException(e.getMessage());
    }
  }

  private void dropTable1() {
    logger.log(Logger.Level.TRACE,"dropTable1");
    // Delete the data in dbTable1 table
    String removeString = TestUtil.getProperty("Xa_Tab1_Delete");
    try {
      stmt = con1.createStatement();
      stmt.executeUpdate(removeString);
      stmt.close();
    } catch (SQLException e) {
      // logger.log(Logger.Level.ERROR,"SQLException dropping "+dbTable1+" table", e);
      throw new EJBException(e.getMessage());
    }
  }

  private void dropTable2() {
    logger.log(Logger.Level.TRACE,"dropTable2");
    // Delete the data from EIS table
    try {
      con2.dropTable();
    } catch (Exception e) {
      // logger.log(Logger.Level.ERROR,"SQLException dropping "+dbTable1+" table", e);
      throw new EJBException(e.getMessage());
    }
  }

}
