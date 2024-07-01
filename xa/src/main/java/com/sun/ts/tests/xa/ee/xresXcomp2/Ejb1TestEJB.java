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
 * @(#)Ejb1TestEJB.java	1.2 03/05/16
 */

/*
 * @(#)Ejb1TestEJB.java	1.17 02/07/19
 */
package com.sun.ts.tests.xa.ee.xresXcomp2;

import java.lang.System.Logger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
  // testProps represent the test specific properties passed in
  // from the test harness.
  private Properties p = null;

  private Properties testProps = null;

  // TableSizes
  // dbSize1 is the size of dbTable1
  private Integer dbSize1;

  private String dbTable1 = null;

  private Ejb2Test ref = null;

  private TSNamingContext context = null;

  private transient PreparedStatement pStmt;

  // DataSources
  // ds1 is associated with dbTable1; con1 will be used for the dbTable1
  // connection
  private DataSource ds1, ds4 = null;

  private transient Connection con1 = null;

  private transient Connection con4 = null;

  // TSEIS
  private TSEISDataSource ds2, ds3, ds5;

  private transient TSConnection con2 = null;

  private transient TSConnection con3 = null;

  private transient TSConnection con5 = null;

  private transient Statement stmt = null;
  
  private static final Logger logger = (Logger) System.getLogger(Ejb1TestEJB.class.getName());


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

      eMsg = "Exception looking up size";
      dbSize1 = (Integer) context.lookup("java:comp/env/size");

      eMsg = "Exception looking up JDBCwhitebox";
      ds1 = (DataSource) context.lookup("java:comp/env/eis/JDBCwhitebox-xa");

      eMsg = "Exception looking up EIS whitebox-xa";
      ds2 = (TSEISDataSource) context.lookup("java:comp/env/eis/whitebox-xa");

      eMsg = "Exception looking up EIS whitebox-tx";
      ds3 = (TSEISDataSource) context.lookup("java:comp/env/eis/whitebox-tx");

      eMsg = "Exception looking up JDBCwhitebox-tx";
      ds4 = (DataSource) context.lookup("java:comp/env/eis/JDBCwhitebox-tx");

      eMsg = "Exception looking up whitebox-notx";
      ds5 = (TSEISDataSource) context.lookup("java:comp/env/eis/whitebox-notx");

      logger.log(Logger.Level.INFO,"JDBCwhitebox-xa ds1 : " + ds1);
      logger.log(Logger.Level.INFO,"whitebox-xa ds2 : " + ds2);
      logger.log(Logger.Level.INFO,"whitebox-tx ds3 : " + ds3);
      logger.log(Logger.Level.INFO,"JDBCwhitebox-tx ds4 : " + ds4);
      logger.log(Logger.Level.INFO,"whitebox-notx ds5 : " + ds5);

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
      logger.log(Logger.Level.ERROR,"Unexpected exception on connection", e);
      throw new EJBException(e.getMessage());
    }
  }

  public void txDbConnect(String tName) {
    logger.log(Logger.Level.INFO,"txDbConnect");
    try {
      if (tName.equals(dbTable1)) {
        // Make the dbTable1 connection
        conTable4();
        logger.log(Logger.Level.INFO,"Made the JDBC TX connection to " + dbTable1 + " DB");
      } else {
        conTable3();
        logger.log(Logger.Level.INFO,"Made the EIS TX connection to EIS");
      }
    } catch (Exception e) {
      logger.log(Logger.Level.ERROR,"Unexpected exception on connection", e);
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

  public void insertDup(String tName, String tSize) {
    // public void insertDup(String tName){
    String eMsg = null;
    logger.log(Logger.Level.TRACE,"insertDup");
    String key = tSize;
    try {
      if (tName.equals(dbTable1)) {
        // JDBC
      } else { // EIS
        logger.log(Logger.Level.INFO,"Getting a connection con5");
        eMsg = "Exception doing a getConnection from ds5";
        con5 = ds5.getConnection();
        logger.log(Logger.Level.TRACE,"con5: " + con5.toString());

        // Prepare the new data entry in EIS
        logger.log(Logger.Level.INFO,"Insert row in EIS");
        eMsg = "Exception doing an insert in EIS con5 ds5";
        con5.insert(key, key);
        // for duplicate
        logger.log(Logger.Level.TRACE,"Inserted a row in EIS using notx - key ");
        logger.log(Logger.Level.INFO,"nsertDup key in EIS");
      }
    } catch (Exception e) {
      logger.log(Logger.Level.INFO,eMsg);
      TestUtil.printStackTrace(e);
      logger.log(Logger.Level.INFO,"Captured Exception in insertDup");
      throw new EJBException(e.getMessage());
    } finally {
      try {
        // eMsg = "Exception in closing stmt";
        // if (stmt!=null){
        // stmt.close();
        // stmt = null;
        // }
        eMsg = "Exception in closing con5";
        if (con5 != null) {
          con5.close();
          con5 = null;
        }
        // eMsg = "Exception in closing con4";
        // if (con4 != null){
        // con4.close();
        // con4 = null;
        // }
      } catch (Exception e) {
        logger.log(Logger.Level.INFO,eMsg);
        TestUtil.printStackTrace(e);
        logger.log(Logger.Level.INFO,"Captured Exception in insertDup");
        throw new EJBException(e.getMessage());
      }
    }
  }

  public boolean insert(String tName, int key) {
    logger.log(Logger.Level.TRACE,"insert");
    // int newKey = key;
    String newName = null;

    try {
      if (tName.equals(dbTable1)) {
        // Prepare the new dbTable1 row entry
        newName = dbTable1 + "-" + key;
        String updateString = TestUtil.getProperty("Xa_Tab1_Insert");
        pStmt = con1.prepareStatement(updateString);
        // Perform the insert(s)
        pStmt.setInt(1, key);
        pStmt.setString(2, newName);
        pStmt.setString(3, newName);
        logger.log(Logger.Level.INFO,"Insert a row into the table " + tName + " valus : " + key);
        pStmt.executeUpdate();
        pStmt.close();
        logger.log(Logger.Level.TRACE,"Inserting a row into the EIS ");
        ref.dbConnect("EIS");
        ref.insert("EIS", key);
        ref.dbUnConnect("EIS");

      } else {
        // Prepare the new data entry in EIS
        con2.insert((new Integer(key)).toString(),
            (new Integer(key)).toString());
        logger.log(Logger.Level.INFO,"Inserted a row into the EIS ");
        logger.log(Logger.Level.INFO,"Inserting a row into the table " + dbTable1);
        ref.dbConnect(dbTable1);
        ref.insert(dbTable1, key);
        ref.dbUnConnect(dbTable1);
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
        logger.log(Logger.Level.INFO,"Deleted row(s) " + fromKey + " thru " + toKey
            + " from the table " + tName);
      } else {
        // Delete rows from EIS
        for (int i = fromKey; i <= toKey; i++) {
          con2.delete((new Integer(i)).toString());
        }
        logger.log(Logger.Level.INFO,"Deleted row(s) from EIS " + fromKey + " thru " + toKey);
      }
    } catch (Exception e) {
      logger.log(Logger.Level.ERROR,"Exception deleting row(s) " + fromKey + " thru " + toKey
          + " from the table " + tName, e);
      throw new EJBException(e.getMessage());
    }
  }

  public Vector getResults(String tName) {
    logger.log(Logger.Level.TRACE,"getResults");
    ResultSet rs = null;
    Vector queryResults = new Vector();
    int i;
    String query, s, name = null;
    float f;

    try {
      if (tName.equals(dbTable1)) {
        query = TestUtil.getProperty("Xa_Tab1_query");
        stmt = con4.createStatement();
        rs = stmt.executeQuery(query);
        // name = "COF_NAME";
        if (rs != null) {
          while (rs.next()) {
            i = rs.getInt(1);
            // s = rs.getString(name);
            // f = rs.getFloat("PRICE");
            queryResults.addElement(new Integer(i));
            // queryResults.addElement(s);
            // queryResults.addElement( new Float(f) );
          }
        }
        stmt.close();
        logger.log(Logger.Level.INFO,"Obtained " + tName + " table ResultSet");
      } else {
        queryResults = con3.readData();
      }

    } catch (Exception e) {
      logger.log(Logger.Level.ERROR,"Exception obtaining " + tName + " table ResultSet", e);
      throw new EJBException(e.getMessage());
    }
    return queryResults;
  }

  public void destroyData(String tName) {
    String eMsg = null;
    String removeString = TestUtil.getProperty("Xa_Tab1_Delete");
    logger.log(Logger.Level.INFO,"destroyData : " + tName);
    try {
      if (tName.equals(dbTable1)) {
        // eMsg = "Exception doing a getConnection from ds4";
        // logger.log(Logger.Level.INFO,"Getting a connection con4");
        // con4 = ds4.getConnection();
        // logger.log(Logger.Level.TRACE,"con4: " + con4.toString() );
        eMsg = "Exception doing a dropTable on con4";
        stmt = con4.createStatement();
        stmt.executeUpdate(removeString);
        // stmt.close();
        logger.log(Logger.Level.INFO,"Deleted all rows from table " + dbTable1);
      } else { // EIS
        // eMsg = "Exception doing a getConnection from ds3";
        // logger.log(Logger.Level.INFO,"Getting a connection con3");
        // con3 = ds3.getConnection();
        // logger.log(Logger.Level.TRACE,"con3: " + con3.toString() );
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
        // eMsg = "Exception in closing con3";
        // if (con3 != null){
        // con3.close();
        // con3 = null;
        // }
        // eMsg = "Exception in closing con4";
        // if (con4 != null){
        // con4.close();
        // con4 = null;
        // }
      } catch (Exception e) {
        logger.log(Logger.Level.INFO,eMsg);
        logger.log(Logger.Level.ERROR,"Exception occured trying to drop table", e);
        throw new EJBException(e.getMessage());
      }
    }
  }

  public void dbUnConnect(String tName) {
    logger.log(Logger.Level.INFO,"dbUnConnect");
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

  public void txDbUnConnect(String tName) {
    logger.log(Logger.Level.INFO,"txDbUnConnect");
    // Close the DB connections
    try {
      if (tName.equals(dbTable1)) {
        con4.close();
        con4 = null;
        logger.log(Logger.Level.INFO,"Closed " + dbTable1 + " connection");
      } else {
        con3.close();
        con3 = null;
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
      logger.log(Logger.Level.ERROR,"SQLException connecting to " + dbTable1 + " DB con1 ",
          e);
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
      logger.log(Logger.Level.INFO,"con2: " + con2.toString());
    } catch (Exception ee) {
      logger.log(Logger.Level.ERROR,"Exception connecting to EIS con2 ", ee);
      throw new EJBException(ee.getMessage());
    }
  }

  private void conTable3() {
    logger.log(Logger.Level.TRACE,"conTable3");
    try {
      // Get connection info for dbTable1 DB
      con3 = ds3.getConnection();
      logger.log(Logger.Level.TRACE,"con3: " + con3.toString());
    } catch (Exception ee) {
      logger.log(Logger.Level.ERROR,"Exception connecting to EIS con3 ", ee);
      throw new EJBException(ee.getMessage());
    }
  }

  private void conTable4() {
    logger.log(Logger.Level.TRACE,"conTable4");
    try {
      // Get connection info for dbTable1 DB
      con4 = ds4.getConnection();
      logger.log(Logger.Level.TRACE,"con4: " + con4.toString());
    } catch (SQLException e) {
      logger.log(Logger.Level.ERROR,"SQLException connecting to " + dbTable1 + " DB con4 ",
          e);
      throw new EJBException(e.getMessage());
    } catch (Exception ee) {
      logger.log(Logger.Level.ERROR,"Exception connecting to " + dbTable1 + " DB con4 ", ee);
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
      pStmt = con4.prepareStatement(updateString);

      for (int i = 1; i <= dbSize1.intValue(); i++) {
        // Perform the insert(s)
        int newKey = i;
        String newName = dbTable1 + "-" + i;
        // float newPrice = i + (float).00;

        pStmt.setInt(1, newKey);
        pStmt.setString(2, newName);
        pStmt.setString(3, newName);
        // pStmt.setFloat(3, newPrice);

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
        con3.insert((new Integer(i)).toString(), (new Integer(i)).toString());
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
      stmt = con4.createStatement();
      stmt.executeUpdate(removeString);
      stmt.close();
    } catch (SQLException e) {
      TestUtil.printStackTrace(e);
      throw new EJBException(e.getMessage());
    }
  }

  private void dropTable2() {
    logger.log(Logger.Level.TRACE,"dropTable2");
    // Delete the data from EIS table
    try {
      con3.dropTable();
    } catch (Exception e) {
      TestUtil.printStackTrace(e);
      throw new EJBException(e.getMessage());
    }
  }

}
